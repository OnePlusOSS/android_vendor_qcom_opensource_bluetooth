/*********************************************************************
*
* Copyright (c) 2015, The Linux Foundation. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are
* met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above
*       copyright notice, this list of conditions and the following
*       disclaimer in the documentation and/or other materials provided
*       with the distribution.
*     * Neither the name of The Linux Foundation nor the names of its
*       contributors may be used to endorse or promote products derived
*       from this software without specific prior written permission.
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
*
************************************************************************/

package com.android.qlogging;

import android.app.ActivityManager;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;
import java.util.Map;

import android.content.pm.PackageManager;

public class BTStateReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Toast toast;
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int btstate = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (btstate) {
                case BluetoothAdapter.STATE_OFF:
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    //Utils.saveCurrentState(context);
                    break;
                case BluetoothAdapter.STATE_ON:
                    Map map;
                    int counter = 0;

                    String[] profile_tags = context.getResources().getStringArray(R.array.profile_list_tags);
                    String[] stack_list_names = context.getResources().getStringArray(R.array.stack_list_names);
                    String[] stack_list_tags = context.getResources().getStringArray(R.array.stack_list_tags);
                    String[] soc_list_names = context.getResources().getStringArray(R.array.soc_list_names);
                    String[] soc_list_tags = context.getResources().getStringArray(R.array.soc_list_tags);

                    switch (getSecondoryOptions.selected) {
                        case 0:
                            Log.d(Main.TAG,"In Main activity,and BT turned on");
                            /*map = Utils.getPreviousSettings(context, Main.PROFILE_MODULE_ID);
                            if (!map.isEmpty()) {
                                for (String profile : profile_tags) {
                                    String state = map.get(profile).toString();
                                    int presState = Character.getNumericValue(state.charAt(1));
                                    sendIntent.transmitIntent(context, presState, profile, getSecondoryOptions.selected);
                                }
                            }*/ //TODO:send profiles states
                            map = Utils.getPreviousSettings(context, Main.STACK_MODULE_ID);
                            counter = 0;
                            if (!map.isEmpty()) {
                                for (String stack : stack_list_names) {
                                    Object state_obj = map.get(stack);
                                    if (state_obj != null) {
                                        String state = state_obj.toString();
                                        int presState = Character.getNumericValue(state.charAt(2));
                                        sendIntent.transmitIntent(context, presState, stack_list_tags[counter], Main.STACK_MODULE_ID);
                                        counter++;
                                    }
                                }
                            }
                            map = Utils.getPreviousSettings(context, Main.SOC_MODULE_ID);
                            counter = 0;
                            getSecondoryOptions.SOC_levels="";
                            if (!map.isEmpty()) {
                                for (String soc : soc_list_names) {
                                    Object state_obj = map.get(soc);
                                    if (state_obj != null) {
                                        String state = state_obj.toString();
                                        int presState = Character.getNumericValue(state.charAt(2));
                                        getSecondoryOptions.SOC_levels+=String.valueOf(presState);
                                        counter++;
                                    }else{
                                        getSecondoryOptions.SOC_levels+=String.valueOf(0);
                                    }
                                }
                                sendIntent.transmitIntent(context, Main.SOC_ALL_MODULE_ID, getSecondoryOptions.SOC_levels, Main.SOC_ALL_MODULE_ID);
                            }else{
                                toast = Toast.makeText(context, "Map empty for SOC", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            break;
                        case Main.PROFILE_MODULE_ID:
                            /*Log.d(Main.TAG,"In Main activity,and BT turned on");
                            for (String profile : profile_tags) {
                                ProfileOptionsView profileOptionsView = (ProfileOptionsView) getSecondoryOptions.lv.findViewWithTag(profile);
                                int presState = profileOptionsView.state;
                                sendIntent.transmitIntent(context, presState, profile, getSecondoryOptions.selected);
                            }*///TODO:Sending Profiles state
                            map = Utils.getPreviousSettings(context, Main.STACK_MODULE_ID);
                            counter = 0;
                            if (!map.isEmpty()) {
                                for (String stack : stack_list_names) {
                                    Object state_obj = map.get(stack);
                                    if (state_obj != null) {
                                        String state = state_obj.toString();
                                        int presState = Character.getNumericValue(state.charAt(2));
                                        sendIntent.transmitIntent(context, presState, stack_list_tags[counter], Main.STACK_MODULE_ID);
                                        counter++;
                                    }
                                }
                            }
                            map = Utils.getPreviousSettings(context, Main.SOC_MODULE_ID);
                            counter = 0;
                            getSecondoryOptions.SOC_levels="";
                            if (!map.isEmpty()) {
                                for (String soc : soc_list_names) {
                                    Object state_obj = map.get(soc);
                                    if (state_obj != null) {
                                        String state = state_obj.toString();
                                        int presState = Character.getNumericValue(state.charAt(2));
                                        getSecondoryOptions.SOC_levels+=String.valueOf(presState);
                                        counter++;
                                    }else{
                                        getSecondoryOptions.SOC_levels+=String.valueOf(0);
                                    }
                                }
                                sendIntent.transmitIntent(context, Main.SOC_ALL_MODULE_ID, getSecondoryOptions.SOC_levels, Main.SOC_ALL_MODULE_ID);
                            }
                            break;
                        case Main.STACK_MODULE_ID:
                            /*Log.d(Main.TAG,"In Main activity,and BT turned on");
                            map = Utils.getPreviousSettings(context, Main.PROFILE_MODULE_ID);
                            if (!map.isEmpty()) {
                                for (String profile : profile_tags) {
                                    String state = map.get(profile).toString();
                                    int presState = Character.getNumericValue(state.charAt(1));
                                    sendIntent.transmitIntent(context, presState, profile, getSecondoryOptions.selected);
                                }
                            }*///TODO:Sending Profiles state
                            counter = 0;
                            for (String stack : stack_list_names) {
                                StackOptionsView stackOptionsView = (StackOptionsView) getSecondoryOptions.lv.findViewWithTag(stack_list_tags[counter]);
                                int presState = stackOptionsView.state;
                                sendIntent.transmitIntent(context, presState, stack_list_tags[counter], Main.STACK_MODULE_ID);
                                counter++;
                            }
                            map = Utils.getPreviousSettings(context, Main.SOC_MODULE_ID);
                            counter = 0;
                            getSecondoryOptions.SOC_levels="";
                            if (!map.isEmpty()) {
                                for (String soc : soc_list_names) {
                                    Object state_obj = map.get(soc);
                                    if (state_obj != null) {
                                        String state = state_obj.toString();
                                        int presState = Character.getNumericValue(state.charAt(2));
                                        getSecondoryOptions.SOC_levels+=String.valueOf(presState);
                                        counter++;
                                    }else{
                                        getSecondoryOptions.SOC_levels+=String.valueOf(0);
                                    }
                                }
                                sendIntent.transmitIntent(context, Main.SOC_ALL_MODULE_ID, getSecondoryOptions.SOC_levels, Main.SOC_ALL_MODULE_ID);
                            }
                            break;
                        case Main.SOC_MODULE_ID:
                            /*Log.d(Main.TAG,"In Main activity,and BT turned on");
                            map = Utils.getPreviousSettings(context, Main.PROFILE_MODULE_ID);
                            if (!map.isEmpty()) {
                                for (String profile : profile_tags) {
                                    if (map.get(profile)) {
                                        String state = map.get(profile).toString();
                                        int presState = Character.getNumericValue(state.charAt(1));
                                        sendIntent.transmitIntent(context, presState, profile, getSecondoryOptions.selected);
                                    }
                                }
                            }*///TODO:Sending Profile State
                            map = Utils.getPreviousSettings(context, Main.STACK_MODULE_ID);
                            counter = 0;
                            if (!map.isEmpty()) {
                                for (String stack : stack_list_names) {
                                    Object state_obj = map.get(stack);
                                    if (state_obj != null) {
                                        String state = state_obj.toString();
                                        int presState = Character.getNumericValue(state.charAt(2));
                                        sendIntent.transmitIntent(context, presState, stack_list_tags[counter], Main.STACK_MODULE_ID);
                                        counter++;
                                    }
                                }
                            }
                            counter = 0;
                            getSecondoryOptions.SOC_levels="";
                            for (String soc : soc_list_names) {
                                StackOptionsView stackOptionsView = (StackOptionsView) getSecondoryOptions.lv.findViewWithTag(soc_list_tags[counter]);
                                int presState = stackOptionsView.state;
                                getSecondoryOptions.SOC_levels=String.valueOf(presState);
                                counter++;
                            }
                            sendIntent.transmitIntent(context, Main.SOC_ALL_MODULE_ID, getSecondoryOptions.SOC_levels, Main.SOC_ALL_MODULE_ID);
                            break;
                    }
                    toast = Toast.makeText(context, "Present Log levels set", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
            }
        }
    }
}
