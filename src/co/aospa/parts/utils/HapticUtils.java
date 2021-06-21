// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.utils;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.preference.PreferenceManager;

import java.lang.Math;

import co.aospa.parts.utils.FileUtils;

public final class HapticUtils {

    public final static String PREF_LEVEL = "haptic_level_pref";
    public final static String PATH_LEVEL = "/sys/devices/platform/soc/884000.i2c/i2c-3/3-005a/ulevel";

    final static int MIN_LEVEL = 1;
    final static int MAX_LEVEL = 128;

    public static void applyLevel(Context context, int value, boolean test) {
        if (FileUtils.fileExists(PATH_LEVEL)) {
            double level = value / 100.0 * (MAX_LEVEL - MIN_LEVEL) + MIN_LEVEL;
            int newValue = (int) Math.round(level);
            FileUtils.writeLine(PATH_LEVEL, String.valueOf(newValue));

            if (test) {
                Vibrator dev = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (dev.hasVibrator()) {
                    dev.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        }
    }

    public static void restoreLevel(Context context) {
        int level = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_LEVEL, 80);
        applyLevel(context, level, false);
    }
}
