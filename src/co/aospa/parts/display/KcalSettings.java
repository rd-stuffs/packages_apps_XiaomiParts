// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.display;

import android.os.Bundle;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragment;
import androidx.preference.Preference;
import androidx.viewpager.widget.ViewPager;

import co.aospa.parts.R;
import co.aospa.parts.preferences.SecureSettingSeekBarPreference;
import co.aospa.parts.preferences.SecureSettingSwitchPreference;
import co.aospa.parts.Controller;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class KcalSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, Controller {

    ViewPager viewPager;
    LinearLayout DotsKcal;
    private int dotscount;
    private ImageView[] dots;

    private final FileUtils mFileUtils = new FileUtils();

    private SecureSettingSwitchPreference mEnabled;
    private SecureSettingSwitchPreference mSetOnBoot;
    private SecureSettingSeekBarPreference mRed;
    private SecureSettingSeekBarPreference mGreen;
    private SecureSettingSeekBarPreference mBlue;
    private SecureSettingSeekBarPreference mSaturation;
    private SecureSettingSeekBarPreference mValue;
    private SecureSettingSeekBarPreference mContrast;
    private SecureSettingSeekBarPreference mHue;
    private SecureSettingSeekBarPreference mMin;
    private SecureSettingSwitchPreference mGrayscale;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.kcal_preferences, rootKey);

        boolean enabled = Settings.Secure.getInt(getContext().getContentResolver(), PREF_ENABLED, 0) == 1;

        mEnabled = (SecureSettingSwitchPreference) findPreference(PREF_ENABLED);
        mEnabled.setOnPreferenceChangeListener(this);
        mEnabled.setTitle(enabled ? R.string.kcal_enabled : R.string.kcal_disabled);

        mSetOnBoot = (SecureSettingSwitchPreference) findPreference(PREF_SETONBOOT);
        mSetOnBoot.setOnPreferenceChangeListener(this);

        mMin = (SecureSettingSeekBarPreference) findPreference(PREF_MINIMUM);
        mMin.setOnPreferenceChangeListener(this);

        mRed = (SecureSettingSeekBarPreference) findPreference(PREF_RED);
        mRed.setOnPreferenceChangeListener(this);

        mGreen = (SecureSettingSeekBarPreference) findPreference(PREF_GREEN);
        mGreen.setOnPreferenceChangeListener(this);

        mBlue = (SecureSettingSeekBarPreference) findPreference(PREF_BLUE);
        mBlue.setOnPreferenceChangeListener(this);

        mSaturation = (SecureSettingSeekBarPreference) findPreference(PREF_SATURATION);
        mSaturation.setEnabled((Settings.Secure.getInt(getContext().getContentResolver(), PREF_GRAYSCALE, 0) == 0));
        mSaturation.setOnPreferenceChangeListener(this);

        mValue = (SecureSettingSeekBarPreference) findPreference(PREF_VALUE);
        mValue.setOnPreferenceChangeListener(this);

        mContrast = (SecureSettingSeekBarPreference) findPreference(PREF_CONTRAST);
        mContrast.setOnPreferenceChangeListener(this);

        mHue = (SecureSettingSeekBarPreference) findPreference(PREF_HUE);
        mHue.setOnPreferenceChangeListener(this);

        mGrayscale = (SecureSettingSwitchPreference) findPreference(PREF_GRAYSCALE);
        mGrayscale.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();

        String rgbString;

        switch (key) {
            case PREF_ENABLED:
                mFileUtils.setValue(KCAL_ENABLE, (boolean) value);
                mEnabled.setTitle((boolean) value ? R.string.kcal_enabled : R.string.kcal_disabled);
                break;
            case PREF_MINIMUM:
                mFileUtils.setValue(KCAL_MIN, (int) value);
                break;
            case PREF_RED:
                rgbString = value + " " + mGreen.getValue() + " " + mBlue.getValue();
                mFileUtils.setValue(KCAL_RGB, rgbString);
                break;
            case PREF_GREEN:
                rgbString = mRed.getValue() + " " + value + " " + mBlue.getValue();
                mFileUtils.setValue(KCAL_RGB, rgbString);
                break;
            case PREF_BLUE:
                rgbString = mRed.getValue() + " " + mGreen.getValue() + " " + value;
                mFileUtils.setValue(KCAL_RGB, rgbString);
                break;
            case PREF_SATURATION:
                if (!(Settings.Secure.getInt(getContext().getContentResolver(), PREF_GRAYSCALE, 0) == 1)) {
                    mFileUtils.setValue(KCAL_SAT, (int) value + SATURATION_OFFSET);
                }
                break;
            case PREF_VALUE:
                mFileUtils.setValue(KCAL_VAL, (int) value + VALUE_OFFSET);
                break;
            case PREF_CONTRAST:
                mFileUtils.setValue(KCAL_CONT, (int) value + CONTRAST_OFFSET);
                break;
            case PREF_HUE:
                mFileUtils.setValue(KCAL_HUE, (int) value);
                break;
            case PREF_GRAYSCALE:
                setmGrayscale((boolean) value);
                break;
            default:
                break;
        }
        return true;
    }

    void applyValues(String preset) {
        String[] values = preset.split(" ");
        int red = Integer.parseInt(values[0]);
        int green = Integer.parseInt(values[1]);
        int blue = Integer.parseInt(values[2]);
        int min = Integer.parseInt(values[3]);
        int sat = Integer.parseInt(values[4]);
        int value = Integer.parseInt(values[5]);
        int contrast = Integer.parseInt(values[6]);
        int hue = Integer.parseInt(values[7]);

        mRed.refresh(red);
        mGreen.refresh(green);
        mBlue.refresh(blue);
        mMin.refresh(min);
        mSaturation.refresh(sat);
        mValue.refresh(value);
        mContrast.refresh(contrast);
        mHue.refresh(hue);
    }

    void setmSetOnBoot(boolean checked) {
        mSetOnBoot.setChecked(checked);
    }

    void setmGrayscale(boolean checked) {
        mGrayscale.setChecked(checked);
        mSaturation.setEnabled(!checked);
        mFileUtils.setValue(KCAL_SAT, checked ? 128 : Settings.Secure.getInt(getContext().getContentResolver(), PREF_SATURATION, SATURATION_DEFAULT) + SATURATION_OFFSET);
    }
}
