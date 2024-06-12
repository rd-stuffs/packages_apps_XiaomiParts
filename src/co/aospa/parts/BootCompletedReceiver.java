// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.provider.Settings;
import android.util.Log;

import co.aospa.parts.utils.FileUtils;

import co.aospa.parts.refreshrate.RefreshUtils;

public class BootCompletedReceiver extends BroadcastReceiver implements Controller {

    private static final boolean DEBUG = false;
    private static final String TAG = "XiaomiParts";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (DEBUG)
            Log.d(TAG, "Received intent: " + intent.getAction());

        if (!intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED))
            return;

        if (DEBUG)
            Log.i(TAG, "Boot completed, starting services");

        RefreshUtils.startService(context);

        if (Settings.Secure.getInt(context.getContentResolver(), PREF_ENABLED, 0) == 1) {
            FileUtils.setValue(KCAL_ENABLE, Settings.Secure.getInt(context.getContentResolver(), PREF_ENABLED, 0));

            String rgbValue = Settings.Secure.getInt(context.getContentResolver(), PREF_RED, RED_DEFAULT) + " " + Settings.Secure.getInt(context.getContentResolver(), PREF_GREEN, GREEN_DEFAULT) + " " + Settings.Secure.getInt(context.getContentResolver(), PREF_BLUE, BLUE_DEFAULT);

            FileUtils.setValue(KCAL_RGB, rgbValue);
            FileUtils.setValue(KCAL_MIN, Settings.Secure.getInt(context.getContentResolver(), PREF_MINIMUM, MINIMUM_DEFAULT));
            FileUtils.setValue(KCAL_SAT, Settings.Secure.getInt(context.getContentResolver(), PREF_GRAYSCALE, 0) == 1 ? 128 : Settings.Secure.getInt(context.getContentResolver(), PREF_SATURATION, SATURATION_DEFAULT) + SATURATION_OFFSET);
            FileUtils.setValue(KCAL_VAL, Settings.Secure.getInt(context.getContentResolver(), PREF_VALUE, VALUE_DEFAULT) + VALUE_OFFSET);
            FileUtils.setValue(KCAL_CONT, Settings.Secure.getInt(context.getContentResolver(), PREF_CONTRAST, CONTRAST_DEFAULT) + CONTRAST_OFFSET);
            FileUtils.setValue(KCAL_HUE, Settings.Secure.getInt(context.getContentResolver(), PREF_HUE, HUE_DEFAULT));
        }
    }
}
