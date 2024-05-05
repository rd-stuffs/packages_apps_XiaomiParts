// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.display;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

import co.aospa.parts.R;

public class LcdFeaturesPreferenceFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String HBM_PROP = "persist.lcd.hbm_mode";
    public static final String CABC_PROP = "persist.lcd.cabc_mode";

    private static final String KEY_HBM = "pref_hbm";
    private static final String KEY_CABC = "pref_cabc";

    private ListPreference mHbmPref;
    private ListPreference mCabcPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.lcd_features_settings);
        mHbmPref = (ListPreference) findPreference(KEY_HBM);
        mHbmPref.setOnPreferenceChangeListener(this);
        mCabcPref = (ListPreference) findPreference(KEY_CABC);
        mCabcPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHbmPref.setValue(SystemProperties.get(HBM_PROP, "0"));
        mHbmPref.setSummary(mHbmPref.getEntry());
        mCabcPref.setValue(SystemProperties.get(CABC_PROP, "0"));
        mCabcPref.setSummary(mCabcPref.getEntry());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();

        if (key.equals(KEY_HBM)) {
            mHbmPref.setValue((String) newValue);
            mHbmPref.setSummary(mHbmPref.getEntry());
            SystemProperties.set(HBM_PROP, (String) newValue);
        } else if (key.equals(KEY_CABC)) {
            mCabcPref.setValue((String) newValue);
            mCabcPref.setSummary(mCabcPref.getEntry());
            SystemProperties.set(CABC_PROP, (String) newValue);
        }

        return true;
    }
}
