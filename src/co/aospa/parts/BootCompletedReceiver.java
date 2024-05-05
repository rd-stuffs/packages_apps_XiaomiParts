// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.util.Log;

import androidx.preference.PreferenceManager;

import co.aospa.parts.dirac.DiracUtils;
import co.aospa.parts.display.KcalUtils;
import co.aospa.parts.refreshrate.RefreshUtils;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final boolean DEBUG = false;
    private static final String TAG = "XiaomiParts";

    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (DEBUG)
            Log.d(TAG, "Received intent: " + intent.getAction());

        if (!intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED))
            return;

        if (DEBUG)
            Log.i(TAG, "Boot completed, starting services");

        DiracUtils.onBootCompleted(context);
        RefreshUtils.startService(context);
        if (KcalUtils.isKcalSupported())
            KcalUtils.writeCurrentSettings(sharedPrefs);
    }
}
