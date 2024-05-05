// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.refreshrate;

import android.content.Context;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.Display;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RefreshTileService extends TileService {
    private static final String KEY_MIN_REFRESH_RATE = "min_refresh_rate";
    private static final String KEY_PEAK_REFRESH_RATE = "peak_refresh_rate";

    private Context context;
    private Tile tile;

    private final List<Float> availableRates = new ArrayList<>();
    private int activeRateMin;
    private int activeRateMax;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Display.Mode mode = context.getDisplay().getMode();
        Display.Mode[] modes = context.getDisplay().getSupportedModes();
        for (Display.Mode m : modes) {
            float rate = Float.valueOf(String.format(Locale.US, "%.02f", m.getRefreshRate()));
            if (m.getPhysicalWidth() == mode.getPhysicalWidth() &&
                m.getPhysicalHeight() == mode.getPhysicalHeight()) {
                availableRates.add(rate);
            }
        }
        syncFromSettings();
    }

    private int getSettingOf(String key) {
        float rate = Settings.System.getFloat(context.getContentResolver(), key, 120);
        return availableRates.indexOf(Float.valueOf(String.format(Locale.US, "%.02f", rate)));
    }

    private void syncFromSettings() {
        activeRateMin = getSettingOf(KEY_MIN_REFRESH_RATE);
        activeRateMax = getSettingOf(KEY_PEAK_REFRESH_RATE);
    }

    private void cycleRefreshRate() {
        if (activeRateMax == 0) {
            if (activeRateMin == 0) {
                activeRateMin = availableRates.size();
            }
            activeRateMax = activeRateMin;
            float rate = availableRates.get(activeRateMin - 1);
            Settings.System.putFloat(context.getContentResolver(), KEY_MIN_REFRESH_RATE, rate);
        }
        float rate = availableRates.get(activeRateMax - 1);
        Settings.System.putFloat(context.getContentResolver(), KEY_PEAK_REFRESH_RATE, rate);
    }

    private String getFormatRate(float rate) {
        return String.format("%.02f Hz", rate)
                .replaceAll("[\\.,]00", "");
    }

    private void updateTileView() {
        String displayText;
        float min = availableRates.get(activeRateMin);
        float max = availableRates.get(activeRateMax);

        displayText = String.format(Locale.US, min == max ? "%s" : "%s - %s",
        getFormatRate(min), getFormatRate(max));
        tile.setContentDescription(displayText);
        tile.setSubtitle(displayText);
        tile.setState(min != max ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        tile = getQsTile();
        syncFromSettings();
        updateTileView();
    }

    @Override
    public void onClick() {
        super.onClick();
        cycleRefreshRate();
        syncFromSettings();
        updateTileView();
    }
}
