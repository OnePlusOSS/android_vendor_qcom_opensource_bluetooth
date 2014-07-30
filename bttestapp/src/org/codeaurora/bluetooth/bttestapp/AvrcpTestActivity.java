/*
 * Copyright (c) 2013-2014, The Linux Foundation. All rights reserved.
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


package org.codeaurora.bluetooth.bttestapp;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAvrcpController;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;

import org.codeaurora.bluetooth.bttestapp.R;
import org.codeaurora.bluetooth.bttestapp.util.Logger;

import java.util.ArrayList;

public class AvrcpTestActivity extends MonkeyActivity implements IBluetoothConnectionObserver {

    private final String TAG = "AvrcpTestActivity";

    private ActionBar mActionBar = null;

    BluetoothAvrcpController mAvrcpController;
    ProfileService mProfileService = null;
    BluetoothDevice mDevice;
    public static final int KEY_STATE_PRESSED = 0;
    public static final int KEY_STATE_RELEASED = 1;
    public static final int AVRC_ID_PLAY = 0x44;
    public static final int AVRC_ID_PAUSE = 0x46;
    public static final int AVRC_ID_VOL_UP = 0x41;
    public static final int AVRC_ID_VOL_DOWN = 0x42;
    public static final int AVRC_ID_STOP = 0x45;

    private final BroadcastReceiver mAvrcpControllerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = (BluetoothDevice)
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (action.equals(BluetoothAvrcpController.ACTION_CONNECTION_STATE_CHANGED)) {
                int prevState = intent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, 0);
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                onReceiveActionConnectionStateChanged(device, prevState, state, intent.getExtras());
            }
        }

        private void onReceiveActionConnectionStateChanged(BluetoothDevice device,
                int prevState, int state, Bundle features) {
            Logger.v(TAG, "onReceiveActionConnectionStateChanged: AVRCP: " +
                    device.getAddress() + " (" +
                    String.valueOf(prevState) + " -> " +
                    String.valueOf(state) + ")");
        }
    };

    private final ServiceConnection mAvrcpControllerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.v(TAG, "onServiceConnected()");
            mProfileService = ((ProfileService.LocalBinder) service).getService();
            mAvrcpController = mProfileService.getAvrcpController();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.v(TAG, "onServiceDisconnected()");
            mProfileService = null;
            mAvrcpController = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.v(TAG, "onCreate()");

        ActivityHelper.initialize(this, R.layout.activity_avrcp_test);
        BluetoothConnectionReceiver.registerObserver(this);
        ActivityHelper.setActionBarTitle(this, R.string.title_avrcp_test);
        // bind to app service
        Intent intent = new Intent(this, ProfileService.class);
        bindService(intent, mAvrcpControllerServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Logger.v(TAG, "onDestroy");
        super.onDestroy();
        unbindService(mAvrcpControllerServiceConnection);
        BluetoothConnectionReceiver.removeObserver(this);
    }

    @Override
    protected void onResume() {
        Logger.v(TAG, "onResume");
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAvrcpController.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mAvrcpControllerReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Logger.v(TAG, "onPause");
        unregisterReceiver(mAvrcpControllerReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mActionBarMenu = menu;
        return true;
    }

    @Override
    public void onDeviceChanged(BluetoothDevice device) {
        Logger.v(TAG, "onDeviceChanged()");
        mDevice = device;
    }

    @Override
    public void onDeviceDisconected() {
        Logger.v(TAG, "onDeviceDisconected");
    }

    private void prepareActionBar() {
        Logger.v(TAG, "prepareActionBar()");

        mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle(R.string.title_avrcp_test);
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

    public void onClickPassthruPlay(View v) {
        Logger.v(TAG, "onClickPassthruPlay()");
        if ((mAvrcpController != null) && mDevice != null &&
            BluetoothProfile.STATE_DISCONNECTED != (mAvrcpController.getConnectionState(mDevice))){
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_PLAY, KEY_STATE_PRESSED);
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_PLAY, KEY_STATE_RELEASED);
        } else {
            Logger.e(TAG, "passthru command not sent, connection unavailable");
        }
    }

    public void onClickPassthruPause(View v) {
        Logger.v(TAG, "onClickPassthruPause()");
        if ((mAvrcpController != null) && mDevice != null &&
            BluetoothProfile.STATE_DISCONNECTED != (mAvrcpController.getConnectionState(mDevice))){
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_PAUSE, KEY_STATE_PRESSED);
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_PAUSE, KEY_STATE_RELEASED);
        } else {
            Logger.e(TAG, "passthru command not sent, connection unavailable");
        }
    }

    public void onClickPassthruStop(View v) {
        Logger.v(TAG, "onClickPassthruStop()");
        if ((mAvrcpController != null) && mDevice != null &&
            BluetoothProfile.STATE_DISCONNECTED != (mAvrcpController.getConnectionState(mDevice))){
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_STOP, KEY_STATE_PRESSED);
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_STOP, KEY_STATE_RELEASED);
        } else {
            Logger.e(TAG, "passthru command not sent, connection unavailable");
        }

    }

    public void onClickPassthruVolUp(View v) {
        Logger.v(TAG, "onClickPassthruVolUp()");
        if ((mAvrcpController != null) && mDevice != null &&
            BluetoothProfile.STATE_DISCONNECTED != (mAvrcpController.getConnectionState(mDevice))){
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_VOL_UP, KEY_STATE_PRESSED);
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_VOL_UP, KEY_STATE_RELEASED);
        } else {
            Logger.e(TAG, "passthru command not sent, connection unavailable");
        }

    }

    public void onClickPassthruVolDown(View v) {
        Logger.v(TAG, "onClickPassthruVolDown()");
        if ((mAvrcpController != null) && mDevice != null &&
            BluetoothProfile.STATE_DISCONNECTED != (mAvrcpController.getConnectionState(mDevice))){
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_VOL_DOWN, KEY_STATE_PRESSED);
            mAvrcpController.sendPassThroughCmd(mDevice, AVRC_ID_VOL_DOWN, KEY_STATE_RELEASED);
        } else {
            Logger.e(TAG, "passthru command not sent, connection unavailable");
        }
    }
}
