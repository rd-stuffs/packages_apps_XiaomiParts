// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.dirac;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.android.settingslib.widget.MainSwitchPreference;

import co.aospa.parts.R;

public class DiracSettingsFragment extends PreferenceFragmentCompat implements
        OnPreferenceChangeListener {

    private static final String PREF_ENABLE = "dirac_enable";
    private static final String PREF_HEADSET = "dirac_headset_pref";
    private static final String PREF_PRESET = "dirac_preset_pref";

    public static final String PREF_SCENE = "scenario_selection";

    private MainSwitchPreference mSwitchBar;

    private ListPreference mHeadsetType;
    private ListPreference mPreset;
    private ListPreference mScenes;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.dirac_settings, rootKey);

        DiracUtils.initialize(getActivity());
        boolean enhancerEnabled = DiracUtils.isDiracEnabled();

        mSwitchBar = (MainSwitchPreference) findPreference(PREF_ENABLE);
        mSwitchBar.setOnPreferenceChangeListener(this);
        mSwitchBar.setChecked(enhancerEnabled);

        mHeadsetType = (ListPreference) findPreference(PREF_HEADSET);
        mHeadsetType.setOnPreferenceChangeListener(this);
        mHeadsetType.setEnabled(enhancerEnabled);

        mPreset = (ListPreference) findPreference(PREF_PRESET);
        mPreset.setOnPreferenceChangeListener(this);
        mPreset.setEnabled(enhancerEnabled);

        mScenes = (ListPreference) findPreference(PREF_SCENE);
        mScenes.setOnPreferenceChangeListener(this);
        mScenes.setEnabled(enhancerEnabled);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case PREF_ENABLE:
                boolean isChecked = (Boolean) newValue;
                DiracUtils.setMusic(isChecked);
                mHeadsetType.setEnabled(isChecked);
                mPreset.setEnabled(isChecked);
                mScenes.setEnabled(isChecked);
                return true;
            case PREF_HEADSET:
                DiracUtils.setHeadsetType(Integer.parseInt(newValue.toString()));
                return true;
            case PREF_PRESET:
                DiracUtils.setLevel((String) newValue);
                return true;
            case PREF_SCENE:
                DiracUtils.setScenario(Integer.parseInt(newValue.toString()));
                return true;
            default:
                return false;
        }
    }
}
