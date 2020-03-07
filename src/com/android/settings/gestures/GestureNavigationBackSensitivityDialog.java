/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.settings.gestures;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.ContentResolver;
import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.os.ServiceManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

import lineageos.providers.LineageSettings;

/**
 * Dialog to set the back gesture's sensitivity in Gesture navigation mode.
 */
public class GestureNavigationBackSensitivityDialog extends InstrumentedDialogFragment {
    private static final String TAG = "GestureNavigationBackSensitivityDialog";
    private static final String KEY_BACK_SENSITIVITY = "back_sensitivity";

    private static final String KEY_HOME_HANDLE_SIZE = "home_handle_width";

    public static void show(SystemNavigationGestureSettings parent, int sensitivity, int length) {
        if (!parent.isAdded()) {
            return;
        }

        final GestureNavigationBackSensitivityDialog dialog =
                new GestureNavigationBackSensitivityDialog();
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_BACK_SENSITIVITY, sensitivity);
        bundle.putInt(KEY_HOME_HANDLE_SIZE, length);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(parent, 0);
        dialog.show(parent.getFragmentManager(), TAG);
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.SETTINGS_GESTURE_NAV_BACK_SENSITIVITY_DLG;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(
                 R.layout.dialog_back_gesture_options, null);
        final SeekBar seekBarSensitivity = view.findViewById(R.id.back_sensitivity_seekbar);
        seekBarSensitivity.setProgress(getArguments().getInt(KEY_BACK_SENSITIVITY));
        final SeekBar seekBarHandleSize = view.findViewById(R.id.home_handle_seekbar);
        seekBarHandleSize.setProgress(getArguments().getInt(KEY_HOME_HANDLE_SIZE));

        final ContentResolver cr = getContext().getContentResolver();
        final int excludedPercentage = LineageSettings.Secure.getInt(cr,
                LineageSettings.Secure.GESTURE_BACK_EXCLUDE_TOP, 0);
        final SeekBar excludedTopSeekBar = view.findViewById(R.id.back_excluded_top_seekbar);
        excludedTopSeekBar.setProgress(excludedPercentage);

        final boolean isShowHintEnabled = LineageSettings.System.getInt(cr,
                LineageSettings.System.NAVIGATION_BAR_HINT, 1) == 1;
        final Switch hintSwitch = view.findViewById(R.id.show_navbar_hint);
        hintSwitch.setChecked(isShowHintEnabled);

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.back_options_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.okay, (dialog, which) -> {
                    int sensitivity = seekBarSensitivity.getProgress();
                    getArguments().putInt(KEY_BACK_SENSITIVITY, sensitivity);
                    int length = seekBarHandleSize.getProgress();
                    getArguments().putInt(KEY_HOME_HANDLE_SIZE, length);
                    SystemNavigationGestureSettings.setBackSensitivity(getActivity(),
                            getOverlayManager(), sensitivity);
                    SystemNavigationGestureSettings.setHomeHandleSize(getActivity(), length);

                    int excludedTopPercentage = excludedTopSeekBar.getProgress();
                    LineageSettings.Secure.putInt(cr,
                            LineageSettings.Secure.GESTURE_BACK_EXCLUDE_TOP, excludedTopPercentage);

                    int showHint = hintSwitch.isChecked() ? 1 : 0;
                    LineageSettings.System.putInt(cr,
                            LineageSettings.System.NAVIGATION_BAR_HINT, showHint);
                })
                .create();
    }

    private IOverlayManager getOverlayManager() {
        return IOverlayManager.Stub.asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
    }
}
