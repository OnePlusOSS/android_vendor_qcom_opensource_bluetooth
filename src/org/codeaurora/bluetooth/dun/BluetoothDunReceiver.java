/*
 * Copyright (c) 2013, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *        * Redistributions of source code must retain the above copyright
 *            notice, this list of conditions and the following disclaimer.
 *        * Redistributions in binary form must reproduce the above copyright
 *            notice, this list of conditions and the following disclaimer in the
 *            documentation and/or other materials provided with the distribution.
 *        * Neither the name of The Linux Foundation nor
 *            the names of its contributors may be used to endorse or promote
 *            products derived from this software without specific prior written
 *            permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT ARE DISCLAIMED.    IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.codeaurora.bluetooth.dun;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.SystemProperties;

public class BluetoothDunReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothDunReceiver";

    private static final boolean V = BluetoothDunService.VERBOSE;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "DunReceiver onReceive enter");

        Intent in = new Intent();
        in.putExtras(intent);
        in.setClass(context, BluetoothDunService.class);
        String action = intent.getAction();
        in.putExtra("action", action);
        Log.d(TAG,"action = " + action);
        if (action == null) return; /* Nothing to do */

        boolean startService = true;

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            in.putExtra(BluetoothAdapter.EXTRA_STATE, state);
            Log.d(TAG," Bluetooth Adapter state = " + state);
            if ((state == BluetoothAdapter.STATE_TURNING_ON)
                    || (state == BluetoothAdapter.STATE_OFF)) {
                startService = false;
            }
        } else {
            // Don't forward intent unless device has bluetooth and bluetooth is enabled.
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null || !adapter.isEnabled()) {
                startService = false;
            }
        }
        if (startService) {
            if (V) Log.v(TAG,"Calling DUN service start service with action = " + in.getAction());
            context.startService(in);
        }
        Log.d(TAG, "DunReceiver onReceive exit");
    }
}
