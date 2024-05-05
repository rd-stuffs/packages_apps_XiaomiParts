// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.display;

import android.content.Context;
import android.os.SystemProperties;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import java.util.Arrays;

import co.aospa.parts.R;

public class CabcTileService extends TileService {

    private Context context;
    private Tile tile;

    private String[] CabcModes;
    private String[] CabcValues;
    private int currentCabcMode;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CabcModes = context.getResources().getStringArray(R.array.lcd_cabc_modes);
        CabcValues = context.getResources().getStringArray(R.array.lcd_cabc_values);
    }

    private void updateCurrentCabcMode() {
        currentCabcMode = Arrays.asList(CabcValues).indexOf(SystemProperties.get(LcdFeaturesPreferenceFragment.CABC_PROP, "0"));
    }

    private void updateCabcTile() {
        tile.setState(currentCabcMode > 0 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setContentDescription(CabcModes[currentCabcMode]);
        tile.setSubtitle(CabcModes[currentCabcMode]);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        tile = getQsTile();
        updateCurrentCabcMode();
        updateCabcTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        updateCurrentCabcMode();
        if (currentCabcMode == CabcModes.length - 1) {
            currentCabcMode = 0;
        } else {
            currentCabcMode++;
        }
        SystemProperties.set(LcdFeaturesPreferenceFragment.CABC_PROP, CabcValues[currentCabcMode]);
        updateCabcTile();
    }
}
