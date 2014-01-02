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

package org.codeaurora.bluetooth.pxpservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class BluetoothLwPwrProximityMonitor {

    private static final String TAG = "BluetoothLwPwrProximityMonitor";
    private static final boolean DBG = true;
    private BluetoothDevice mDevice = null;
    private Context mContext = null;

    private BluetoothRssiMonitorCallback mMonitorCbk;
    private boolean mAutoConnect = false;
    private int mState;
    private final Object mStateLock = new Object();

    /* Monitor state constants */
    private static final int MONITOR_STATE_IDLE = 0;
    private static final int MONITOR_STATE_STARTING = 1;
    private static final int MONITOR_STATE_STOPPING = 2;
    private static final int MONITOR_STATE_STARTED = 3;
    private static final int MONITOR_STATE_CLOSED = 4;

    /* constants for rssi threshold event */
    public static final int RSSI_MONITOR_DISABLED = 0x00;
    public static final int RSSI_HIGH_ALERT = 0x01;
    public static final int RSSI_MILD_ALERT = 0x02;
    public static final int RSSI_NO_ALERT = 0x03;

    /* command status */
    public static final int COMMAND_STATUS_SUCCESS = 0x00;
    public static final int COMMAND_STATUS_FAILED = 0x01;
    private int mLowerLimit;
    private int mUpperLimit;

    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

    public static abstract class BluetoothRssiMonitorCallback {
        // Callback for start()
        public void onStarted() {
        }

        // Callback for stop()
        public void onStopped() {
        }

        public void onReadRssiThreshold(int thresh_min, int thresh_max, int alert, int status) {
        }

        public void onAlert(int evtType, int rssi) {
        }
    };

    public BluetoothLwPwrProximityMonitor(Context cxt, String device,
            BluetoothRssiMonitorCallback cbk) {
        mDevice = mAdapter.getRemoteDevice(device);
        mContext = cxt;
        mState = MONITOR_STATE_CLOSED;
        mMonitorCbk = cbk;
    }

    public boolean start(int thresh_min, int thresh_max) {
        return true;

    }

    public boolean readRssiThreshold() {
        return true;

    }

    public void stop() {

    }

    public void close() {

    }

}
