// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.display;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import co.aospa.parts.R;
import co.aospa.parts.utils.FileUtils;
import co.aospa.parts.display.KcalUtils;

public class KcalSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener, OnMainSwitchChangeListener {

    private static final String TAG = "KcalSettings";

    private SharedPreferences mSharedPrefs;

    private MainSwitchPreference mKcalSwitchPreference;
    private SeekBarPreference mRedColorSlider;
    private SeekBarPreference mGreenColorSlider;
    private SeekBarPreference mBlueColorSlider;
    private SeekBarPreference mSaturationSlider;
    private SeekBarPreference mContrastSlider;
    private SeekBarPreference mHueSlider;
    private SeekBarPreference mBrightnessSlider;
    private Preference mResetButton;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.kcal_settings);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mKcalSwitchPreference = (MainSwitchPreference) findPreference("kcal_enable");
        mResetButton = (Preference) findPreference("reset_default_button");

        // Check if the node exists and enable / disable the preference depending on the case
        if (KcalUtils.isKcalSupported()) {
            configurePreferences();
            KcalUtils.writeCurrentSettings(mSharedPrefs);
            configurePreferences();
        } else {
            mKcalSwitchPreference.setEnabled(false);
            mKcalSwitchPreference.setSummary(getString(R.string.kcal_not_supported));
            mResetButton.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case "red_slider":
                KcalUtils.writeConfigToNode(KcalUtils.KCAL_RGB_NODE, 1, (Integer) newValue);
                mRedColorSlider.setSummary(String.valueOf(newValue));
                break;
            case "green_slider":
                KcalUtils.writeConfigToNode(KcalUtils.KCAL_RGB_NODE, 2, (Integer) newValue);
                mGreenColorSlider.setSummary(String.valueOf(newValue));
                break;
            case "blue_slider":
                KcalUtils.writeConfigToNode(KcalUtils.KCAL_RGB_NODE, 3, (Integer) newValue);
                mBlueColorSlider.setSummary(String.valueOf(newValue));
                break;
            case "saturation_slider":
                KcalUtils.writeConfigToNode(KcalUtils.KCAL_SATURATION_NODE, 0, (Integer) newValue);
                mSaturationSlider.setSummary(String.valueOf(newValue));
                break;
            case "contrast_slider":
                KcalUtils.writeConfigToNode(KcalUtils.KCAL_CONTRAST_NODE, 0, (Integer) newValue);
                mContrastSlider.setSummary(String.valueOf(newValue));
                break;
            case "hue_slider":
                KcalUtils.writeConfigToNode(KcalUtils.KCAL_HUE_NODE, 0, (Integer) newValue);
                mHueSlider.setSummary(String.valueOf(newValue));
                break;
            case "brightness_slider":
                KcalUtils.writeConfigToNode(KcalUtils.KCAL_BRIGHTNESS_NODE, 0, (Integer) newValue);
                mBrightnessSlider.setSummary(String.valueOf(newValue));
                break;
        }
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        mKcalSwitchPreference.setChecked(isChecked);
        KcalUtils.writeConfigToNode(KcalUtils.KCAL_ENABLE_NODE, 0, isChecked ? 1 : 0);
    }

    // Configure the switches, preferences and sliders
    private void configurePreferences() {
        mKcalSwitchPreference.setEnabled(true);
        mKcalSwitchPreference.addOnSwitchChangeListener(this);

        // Set the preference so it resets all the other preference's values, and applies the configuration on click
        mResetButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.clear();
                editor.commit();
                getPreferenceScreen().removeAll();
                addPreferencesFromResource(R.xml.kcal_settings);
                configurePreferences();
                KcalUtils.writeCurrentSettings(mSharedPrefs);
                configurePreferences();
                return true;
            }
        });

        mRedColorSlider = (SeekBarPreference) findPreference("red_slider");
        configureSlider(mRedColorSlider, this);

        mGreenColorSlider = (SeekBarPreference) findPreference("green_slider");
        configureSlider(mGreenColorSlider, this);

        mBlueColorSlider = (SeekBarPreference) findPreference("blue_slider");
        configureSlider(mBlueColorSlider, this);

        mSaturationSlider = (SeekBarPreference) findPreference("saturation_slider");
        configureSlider(mSaturationSlider, this);
        mSaturationSlider.setMin(224);

        mContrastSlider = (SeekBarPreference) findPreference("contrast_slider");
        configureSlider(mContrastSlider, this);
        mContrastSlider.setMin(224);

        mHueSlider = (SeekBarPreference) findPreference("hue_slider");
        configureSlider(mHueSlider, this);
        mHueSlider.setMin(0);

        mBrightnessSlider = (SeekBarPreference) findPreference("brightness_slider");
        configureSlider(mBrightnessSlider, this);
        mBrightnessSlider.setMin(224);
    }


    // Configure the given SeekBarPreference with the given configuration
    private void configureSlider(SeekBarPreference slider, OnPreferenceChangeListener listener) {
        slider.setOnPreferenceChangeListener(listener);
        slider.setSummary(String.valueOf(slider.getValue()));
        slider.setUpdatesContinuously(true);
        slider.setMin(1);
    }
}
