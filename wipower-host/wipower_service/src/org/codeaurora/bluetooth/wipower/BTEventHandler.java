/*
 * Copyright (c) 2013-2015, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of The Linux Foundation nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.codeaurora.bt_wipowersdk.wipower;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

import java.lang.Object;

public class BTEventHandler extends BroadcastReceiver {
    private static final String TAG = "BTEventHandler-Wipower";
    private static boolean V = false/*Constants.VERBOSE*/;
    private int state;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(SystemProperties.getBoolean("persist.bluetooth.a4wp", false) == false) {
            Log.e(TAG, "WipowerService is not supported");
            return;
        }

        Intent in = new Intent();
        in.putExtras(intent);
        in.setClass(context, WipowerService.class);
        String action = intent.getAction();
        in.putExtra("action",action);

        V = SystemProperties.getBoolean("persist.a4wp.logging", false);

        if (V) {
            state = intent.getIntExtra
                      (BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            Log.d(TAG, "action: " + action + "state: " + state);
        }

        if (action.equals("com.quicinc.wbc.action.ACTION_PTU_PRESENT")) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON ||
                mBluetoothAdapter.isLeEnabled()) {
                if(SystemProperties.get("bluetooth.wipower").equals("true")) {
                    Log.e(TAG, "Wipower Service is running");
                    return;
                }
                ComponentName service = context.startService(in);
                if (service != null) {
                    Log.d(TAG, "WipowerService started successfully - PTU");
                    SystemProperties.set("bluetooth.wipower", "true");
                } else {
                    Log.e(TAG, "Could Not Start Wipower Service ");
                    return;
                }
            }
        }

        if ((action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)))
        {
            state = intent.getIntExtra
                           (BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if (V) Log.d(TAG, "State: " + state);

            if ((BluetoothAdapter.STATE_ON == state))
            {
                if(SystemProperties.get("bluetooth.wipower").equals("true")) {
                    Log.e(TAG, "Wipower Service is running");
                    return;
                }
                ComponentName service = context.startService(in);
                if (service != null) {
                    Log.d(TAG, "WipowerService started successfully");
                    SystemProperties.set("bluetooth.wipower", "true");
                } else {
                    Log.e(TAG, "Could Not Start Wipower Service ");
                    return;
                }
            } else if ( BluetoothAdapter.STATE_OFF == state ) {
               mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
               if (!(mBluetoothAdapter.isLeEnabled())) {
                   Log.v(TAG, "stopping wipower service on BLE off");
                   context.startService(in);
                   SystemProperties.set("bluetooth.wipower", "false");
               } else {
                   Log.v(TAG, "le is still enbaled - do not stop service");
               }
            }
        }
    }
}
