// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.display;

import android.content.Context;
import android.os.SystemProperties;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import co.aospa.parts.R;

public class HbmTileService extends TileService {

    private Context context;
    private Tile tile;

    private String[] HbmModes;
    private String[] HbmValues;
    private int currentHbmMode;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        HbmModes = context.getResources().getStringArray(R.array.lcd_hbm_modes);
        HbmValues = context.getResources().getStringArray(R.array.lcd_hbm_values);
    }

    private void updateCurrentHbmMode() {
        currentHbmMode = SystemProperties.getInt(LcdFeaturesPreferenceFragment.HBM_PROP, 0);
    }

    private void updateHbmTile() {
        tile.setState(currentHbmMode > 0 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setContentDescription(HbmModes[currentHbmMode]);
        tile.setSubtitle(HbmModes[currentHbmMode]);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        tile = getQsTile();
        updateCurrentHbmMode();
        updateHbmTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        updateCurrentHbmMode();
        if (currentHbmMode == HbmModes.length - 1) {
            currentHbmMode = 0;
        } else {
            currentHbmMode++;
        }
        SystemProperties.set(LcdFeaturesPreferenceFragment.HBM_PROP, Integer.toString(currentHbmMode));
        updateHbmTile();
    }
}
