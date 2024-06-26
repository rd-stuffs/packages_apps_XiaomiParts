// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts;

import android.os.Bundle;
import android.view.MenuItem;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.widget.R;

public class MainSettingsActivity extends CollapsingToolbarBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MainSettingsFragment())
                .commit();
    }
}
