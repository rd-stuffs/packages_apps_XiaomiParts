// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.fpsinfo;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import co.aospa.parts.R;

public class FPSTileService extends TileService {

  private final String KEY_FPS_INFO = "fps_info";

  private boolean isShowing = false;

  public FPSTileService() { }

  @Override
  public void onStartListening() {
      super.onStartListening();
      ActivityManager manager =
              (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
      for (ActivityManager.RunningServiceInfo service :
              manager.getRunningServices(Integer.MAX_VALUE)) {
          if (FPSInfoService.class.getName().equals(
                  service.service.getClassName())) {
              isShowing = true;
          }
      }
      updateTile();
  }

  @Override
  public void onClick() {
      Intent fpsinfo = new Intent(this, FPSInfoService.class);
      if (!isShowing)
          this.startService(fpsinfo);
      else
          this.stopService(fpsinfo);
      isShowing = !isShowing;
      updateTile();
  }

  private void updateTile() {
      final Tile tile = getQsTile();
      tile.setState(isShowing ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
      tile.updateTile();
  }

}
