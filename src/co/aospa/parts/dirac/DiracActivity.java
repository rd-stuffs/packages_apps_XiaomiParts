// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.dirac;

import android.os.Bundle;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.widget.R;

public class DiracActivity extends CollapsingToolbarBaseActivity {

    private static final String TAG_DIRAC = "dirac";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(R.id.content_frame,
                new DiracSettingsFragment(), TAG_DIRAC).commit();
    }
}
