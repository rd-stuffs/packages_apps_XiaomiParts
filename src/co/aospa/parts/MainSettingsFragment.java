// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import co.aospa.parts.dirac.DiracActivity;
import co.aospa.parts.speaker.ClearSpeakerActivity;
import co.aospa.parts.display.KcalSettingsActivity;
import co.aospa.parts.display.LcdFeaturesPreferenceActivity;
import co.aospa.parts.refreshrate.RefreshActivity;

import co.aospa.parts.utils.FileUtils;
import co.aospa.parts.utils.HapticUtils;

public class MainSettingsFragment extends PreferenceFragmentCompat implements OnPreferenceChangeListener {

    private static final String PREF_DIRAC_SETTINGS = "dirac_settings";
    private static final String PREF_CLEAR_SPEAKER_SETTINGS = "clear_speaker_settings";
    private static final String PREF_KCAL_SETTINGS = "kcal_settings";
    private static final String PREF_LCD_FEATURES_SETTINGS = "lcd_features_settings";
    private static final String PREF_REFRESH_RATE_SETTINGS = "refresh_rate_settings";

    private Preference mDiracSettingsPref;
    private Preference mClearSpeakerSettingsPref;
    private Preference mKcalSettingsPref;
    private Preference mLcdFeaturesSettingsPref;
    private Preference mRefreshRateSettingsPref;

    private Vibrator mVibrator;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.xiaomiparts, rootKey);

        mDiracSettingsPref = (Preference) findPreference(PREF_DIRAC_SETTINGS);
        mDiracSettingsPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), DiracActivity.class);
            startActivity(intent);
            return true;
        });

        mClearSpeakerSettingsPref = (Preference) findPreference(PREF_CLEAR_SPEAKER_SETTINGS);
        mClearSpeakerSettingsPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), ClearSpeakerActivity.class);
            startActivity(intent);
            return true;
        });

        mKcalSettingsPref = (Preference) findPreference(PREF_KCAL_SETTINGS);
        mKcalSettingsPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), KcalSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        mLcdFeaturesSettingsPref = (Preference) findPreference(PREF_LCD_FEATURES_SETTINGS);
        mLcdFeaturesSettingsPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), LcdFeaturesPreferenceActivity.class);
            startActivity(intent);
            return true;
        });

        mRefreshRateSettingsPref = (Preference) findPreference(PREF_REFRESH_RATE_SETTINGS);
        mRefreshRateSettingsPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), RefreshActivity.class);
            startActivity(intent);
            return true;
        });

        final SeekBarPreference mHapticLevel = (SeekBarPreference) findPreference(HapticUtils.PREF_LEVEL);
        if (FileUtils.fileExists(HapticUtils.PATH_LEVEL)) {
            mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (mVibrator == null || !mVibrator.hasVibrator()) {
                mVibrator = null;
            }
            mHapticLevel.setEnabled(true);
            mHapticLevel.setOnPreferenceChangeListener(this);
        } else {
            mHapticLevel.setSummary(R.string.haptic_level_summary_incompatible);
            mHapticLevel.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (HapticUtils.PREF_LEVEL.equals(preference.getKey())) {
            HapticUtils.applyLevel(getContext(), (int) newValue, true);
            doHapticFeedback();
        }
        return true;
    }

    private void doHapticFeedback() {
        if (mVibrator == null) {
            return;
        }
        mVibrator.vibrate(VibrationEffect.createOneShot(500,
                VibrationEffect.DEFAULT_AMPLITUDE));
    }
}
