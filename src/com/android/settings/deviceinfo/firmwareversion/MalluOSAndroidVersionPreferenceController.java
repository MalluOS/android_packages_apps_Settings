/*
 * Copyright 2016-2019 Paranoid Android
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
 * limitations under the License.
 */

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.SystemProperties;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class MalluOSAndroidVersionPreferenceController extends BasePreferenceController {

    private static final String MALLUOS_VERSION_FLAVOR_PROP = "ro.malluos.version.flavor";
    private static final String MALLUOS_VERSION_CODE_PROP = "ro.malluos.version.code";
    private static final String MALLUOS_BUILD_VARIANT_PROP = "ro.malluos.build.variant";

    private final Context mContext;

    public MalluOSAndroidVersionPreferenceController(Context context, String key) {
        super(context, key);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE_UNSEARCHABLE;
    }

    @Override
    public CharSequence getSummary() {
        String malluosVersionFlavor = SystemProperties.get(MALLUOS_VERSION_FLAVOR_PROP,
                mContext.getResources().getString(R.string.device_info_default));
        String malluosVersionCode = SystemProperties.get(MALLUOS_VERSION_CODE_PROP,
               mContext.getResources().getString(R.string.device_info_default));
        String malluosBuildVariant = SystemProperties.get(MALLUOS_BUILD_VARIANT_PROP,
              mContext.getResources().getString(R.string.device_info_default));

        if ((malluosBuildVariant.equals("OFFICIAL")) || (malluosBuildVariant.equals("UNOFFICIAL"))) {
           return malluosVersionFlavor + " " + malluosVersionCode + " " + malluosBuildVariant;
        } else {
           return malluosVersionFlavor + " " + malluosVersionCode;
        }
    }
}
