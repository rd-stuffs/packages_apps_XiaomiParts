// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SeekBarPreference;

import co.aospa.parts.dirac.DiracActivity;
import co.aospa.parts.speaker.ClearSpeakerActivity;
import co.aospa.parts.display.KcalSettingsActivity;
import co.aospa.parts.display.LcdFeaturesPreferenceActivity;
import co.aospa.parts.refreshrate.RefreshActivity;

import co.aospa.parts.utils.FileUtils;

public class MainSettingsFragment extends PreferenceFragment {

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

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.xiaomiparts);

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
    }
}
