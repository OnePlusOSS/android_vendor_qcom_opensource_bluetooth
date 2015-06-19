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

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.ProcessBuilder;
import java.lang.Exception;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

public class Utils {
    private static Process process,p;
    private static String pid;
    private static String timestamp;
    private static String filesDir;

    public static void startLogging(Context context){
    //Called when the start logging button is clicked
        try{
            filesDir = context.getFilesDir().getAbsolutePath();
            String cmd_clear[] = {"/system/bin/sh", "-c", "logcat -c"};
            p = Runtime.getRuntime().exec(cmd_clear);
            p.waitFor();
            p.destroy();
            if(Main.bt_log_filter){
                int bt_id = android.os.Process.myPid();
                //String cmd_start[] = {"/system/bin/sh", "-c", "logcat -v threadtime | grep "+String.valueOf(bt_id)+" | dd of="+filesDir+"/log.txt"};
                //p = Runtime.getRuntime().exec(cmd_start);
                //ProcessBuilder builder = new ProcessBuilder("/system/bin/sh", "-c", "logcat -v threadtime | grep "+String.valueOf(bt_id)+" | dd of="+filesDir+"/log.txt");
                ProcessBuilder builder = new ProcessBuilder("/system/bin/sh", "-c", "logcat -v threadtime | grep "+String.valueOf(bt_id)+" > "+filesDir+"/log.txt");
                p = builder.start();
                Toast.makeText(context,String.valueOf(getPid(p)),Toast.LENGTH_LONG).show();
            }else{
                String cmd_start[] = {"/system/bin/sh", "-c", "logcat -v threadtime -f "+filesDir+"/log.txt"};
                p = Runtime.getRuntime().exec(cmd_start);
            }
        }catch(IOException e){
            Log.e(Main.TAG,e.toString());
        }catch(InterruptedException e){
            Log.e(Main.TAG,e.toString());
        }
    }

    public static int getPid(Process process) {
        try {
            Class<?> ProcessImpl = process.getClass();
            Field field = ProcessImpl.getDeclaredField("pid");
            field.setAccessible(true);
            return field.getInt(process);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            return -1;
        }
    }

    public static void stopLogging(Context context){
    //Called when the stop logging button is clicked
        try{
            filesDir = context.getFilesDir().getAbsolutePath();
            if(p!=null){
                p.destroy();
                Toast toast = Toast.makeText(context,"Log stored in "+filesDir,Toast.LENGTH_LONG);
                toast.show();
            }
            Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("MM-dd-HH-mm-ss-SSS");
            cal.setTimeInMillis(System.currentTimeMillis());
            timestamp = df.format(cal.getTime());
            process = Runtime.getRuntime().exec("mv "+filesDir+"/log.txt "+filesDir+"/"+timestamp+"_log.txt");
            process.waitFor();
            process.destroy();
            /*if(Main.bt_log_filter){
                int bt_id = android.os.Process.myPid();
                Toast toast = Toast.makeText(context,String.valueOf(bt_id),Toast.LENGTH_SHORT);
                toast.show();
                Scanner scanner = new Scanner(filesDir+"/"+timestamp+"_log.txt");
                File filtered_file = new File(filesDir,timestamp+"_log_filtered.txt");
                FileOutputStream f = new FileOutputStream(filtered_file);
                PrintWriter pw = new PrintWriter(f);
                while(scanner.hasNextLine()){
                    String newLine = scanner.nextLine();
                    Log.e(Main.TAG,newLine);
                    if(newLine.contains(String.valueOf(bt_id))){
                        pw.println(newLine);
                    }
                }
                pw.flush();
                pw.close();
                f.close();
            }*/
        }catch (FileNotFoundException e) {
            Log.e(Main.TAG,e.toString());
        }catch(IOException e){
            Log.e(Main.TAG,e.toString());
        }catch(InterruptedException e){
            Log.e(Main.TAG,e.toString());
        }
    }

    public static Map<String, String> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        Map<String, String> map = new HashMap<String, String>();
        int eventType = parser.getEventType();
        String title = "nothingyet";
        String presState = "0", prevState = "0", prevState2 = "0";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    Log.v(Main.TAG, "Start of the document");
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (!name.equalsIgnoreCase("Data")) {
                        if (name.equalsIgnoreCase("presentState")) {
                            presState = parser.nextText();
                        } else if (name.equalsIgnoreCase("prevState")) {
                            prevState = parser.nextText();
                        } else if (name.equalsIgnoreCase("prevState2")) {
                            prevState2 = parser.nextText();
                            Log.v(Main.TAG, title + presState + prevState);
                            map.put(title, prevState + prevState2 + presState);
                        } else {
                            title = name;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    Log.d(Main.TAG, "Reading document over");
            }
            eventType = parser.next();
        }
        return map;
    }

    public static Map<String, String> getPreviousSettings(Context context, int option) {
        Map<String, String> map = new HashMap<>();
        String xmlFile = ".xml";
        if (option == Main.PROFILE_MODULE_ID) {
            xmlFile = "ProfileSettings.xml";
        } else if (option == Main.STACK_MODULE_ID) {
            xmlFile = "StackSettings.xml";
        } else if (option == Main.SOC_MODULE_ID) {
            xmlFile = "SocSettings.xml";
        }
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = context.openFileInput(xmlFile);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            map = Utils.parseXML(parser);
            in_s.close();
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    public static void saveCurrentState(Context context) {
        LinearLayout lv = getSecondoryOptions.lv;
        String xmlFile = "nothing yet";
        FileOutputStream fileos = null;
        if (getSecondoryOptions.selected == Main.PROFILE_MODULE_ID) {
            xmlFile = "ProfileSettings.xml";
        } else if (getSecondoryOptions.selected == Main.STACK_MODULE_ID) {
            xmlFile = "StackSettings.xml";
        } else if (getSecondoryOptions.selected == Main.SOC_MODULE_ID) {
            xmlFile = "SocSettings.xml";
        }
        try {
            fileos = new FileOutputStream(new File(context.getFilesDir(), xmlFile));
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);

            xmlSerializer.startTag(null, "Data");

            StackOptionsView stackOptionsView;
            ProfileOptionsView profileOptionsView;
            String[] list_names = null, list_tags = null;
            if (getSecondoryOptions.selected == Main.PROFILE_MODULE_ID) {
                list_names = context.getResources().getStringArray(R.array.profile_list_names);
                list_tags = context.getResources().getStringArray(R.array.profile_list_tags);
            } else if (getSecondoryOptions.selected == Main.STACK_MODULE_ID) {
                list_names = context.getResources().getStringArray(R.array.stack_list_names);
                list_tags = context.getResources().getStringArray(R.array.stack_list_tags);
            } else if (getSecondoryOptions.selected == Main.SOC_MODULE_ID) {
                getSecondoryOptions.SOC_levels="";
                list_names = context.getResources().getStringArray(R.array.soc_list_names);
                list_tags = context.getResources().getStringArray(R.array.soc_list_tags);
            }
            if (list_names != null && list_tags != null) {
                for (int counter = 0; counter < list_names.length; counter++) {
                    if (getSecondoryOptions.selected == Main.PROFILE_MODULE_ID) {
                        xmlSerializer.startTag(null, list_tags[counter]);
                        profileOptionsView = (ProfileOptionsView) lv.findViewWithTag(list_tags[counter]);
                        xmlSerializer.startTag(null, "presentState");
                        xmlSerializer.text(String.valueOf(profileOptionsView.state));
                        xmlSerializer.endTag(null, "presentState");
                        xmlSerializer.startTag(null, "prevState");
                        xmlSerializer.text(String.valueOf(profileOptionsView.prevState));
                        xmlSerializer.endTag(null, "prevState");
                        xmlSerializer.startTag(null, "prevState2");
                        xmlSerializer.text(String.valueOf(profileOptionsView.prevState));
                        xmlSerializer.endTag(null, "prevState2");
                        xmlSerializer.endTag(null, list_tags[counter]);
                    } else {
                        xmlSerializer.startTag(null, list_names[counter]);
                        stackOptionsView = (StackOptionsView) lv.findViewWithTag(list_tags[counter]);
                        Log.v(Main.TAG, "XML----" + list_names[counter] + String.valueOf(stackOptionsView.prevState) + String.valueOf(stackOptionsView.prevState2) + String.valueOf(stackOptionsView.state));
                        xmlSerializer.startTag(null, "presentState");
                        xmlSerializer.text(String.valueOf(stackOptionsView.state));
                        xmlSerializer.endTag(null, "presentState");
                        xmlSerializer.startTag(null, "prevState");
                        xmlSerializer.text(String.valueOf(stackOptionsView.prevState));
                        xmlSerializer.endTag(null, "prevState");
                        xmlSerializer.startTag(null, "prevState2");
                        xmlSerializer.text(String.valueOf(stackOptionsView.prevState2));
                        xmlSerializer.endTag(null, "prevState2");
                        xmlSerializer.endTag(null, list_names[counter]);
                        if(getSecondoryOptions.selected==Main.SOC_MODULE_ID){
                            getSecondoryOptions.SOC_levels+=String.valueOf(stackOptionsView.state);
                        }
                    }
                }
            }
            xmlSerializer.endTag(null, "Data");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            saveGlobalState(context);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, e.toString());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "Illegal argument");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "Illegal state");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "IO Exception");
        }
        finally {
            try {
                if (fileos != null)
                    fileos.close();
            } catch (IOException e) {
                Log.e(Main.TAG, "IO Exception");
            }
        }
    }

    public static void saveGlobalState(Context context){
        String xmlFile="GlobalSettings.xml";
        FileOutputStream fileos = null;
        try{
            fileos = new FileOutputStream(new File(context.getFilesDir(), xmlFile));
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "Data");
            xmlSerializer.startTag(null,"isLogging");
            xmlSerializer.text(String.valueOf(Main.isLogging));
            xmlSerializer.endTag(null,"isLogging");
            xmlSerializer.startTag(null,"SOC_Enabled");
            xmlSerializer.text(String.valueOf(Main.soc_log_enabled));
            xmlSerializer.endTag(null,"SOC_Enabled");
            xmlSerializer.startTag(null,"Stack_all");
            xmlSerializer.text(String.valueOf(Main.stack_set_all));
            xmlSerializer.endTag(null,"Stack_all");
            xmlSerializer.startTag(null,"bt_log_filter");
            xmlSerializer.text(String.valueOf(Main.bt_log_filter));
            xmlSerializer.endTag(null,"bt_log_filter");
            xmlSerializer.endTag(null, "Data");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, e.toString());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "Illegal argument");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "Illegal state");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "IO Exception");
        }
        finally {
            try {
                if (fileos != null)
                    fileos.close();
            } catch (IOException e) {
                Log.e(Main.TAG, "IO Exception");
            }
        }
    }

    public static boolean isAppRunning(Context context) {
        String activity = Main.class.getName();
        ActivityManager activityManager = (ActivityManager) context.
                getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.
                getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activity.equals(task.baseActivity.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void setGlobalSettings(Context context){
        String xmlFile = "GlobalSettings.xml";
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = context.openFileInput(xmlFile);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name;

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.v(Main.TAG, "Start of the document");
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (!name.equalsIgnoreCase("Data")) {
                            if (name.equalsIgnoreCase("isLogging")) {
                                Main.isLogging = Boolean.valueOf(parser.nextText());
                            } else if (name.equalsIgnoreCase("SOC_Enabled")) {
                                Main.soc_log_enabled = Boolean.valueOf(parser.nextText());
                            } else if (name.equalsIgnoreCase("bt_log_filter")) {
                                Main.bt_log_filter = Boolean.valueOf(parser.nextText());
                            } else if (name.equalsIgnoreCase("Stack_all")) {
                                Main.stack_set_all = Boolean.valueOf(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        Log.d(Main.TAG, "Reading document over");
                }
                eventType = parser.next();
            }
            in_s.close();
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setDefaultLogLevels(Activity activity, int option) {
        String xmlFile = "nothing yet";
        FileOutputStream fileos = null;
        if (option == Main.PROFILE_MODULE_ID) {
            xmlFile = "ProfileSettings.xml";
        } else if (option == Main.STACK_MODULE_ID) {
            xmlFile = "StackSettings.xml";
        } else if (option == Main.SOC_MODULE_ID) {
            xmlFile = "SocSettings.xml";
        }
        try {
            fileos = new FileOutputStream(new File(activity.getApplicationContext().getFilesDir(), xmlFile));
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);

            xmlSerializer.startTag(null, "Data");

            StackOptionsView stackOptionsView;
            ProfileOptionsView profileOptionsView;

            String[] list_names = null, list_tags = null,default_state=null;
            if (option == Main.PROFILE_MODULE_ID) {
                list_names = activity.getApplicationContext().getResources().getStringArray(R.array.profile_list_names);
                list_tags = activity.getApplicationContext().getResources().getStringArray(R.array.profile_list_tags);
                default_state = activity.getApplicationContext().getResources().getStringArray(R.array.profile_defaults);
            } else if (option == Main.STACK_MODULE_ID) {
                list_names = activity.getApplicationContext().getResources().getStringArray(R.array.stack_list_names);
                list_tags = activity.getApplicationContext().getResources().getStringArray(R.array.stack_list_tags);
                default_state = activity.getApplicationContext().getResources().getStringArray(R.array.stack_defaults);
            } else if (option == Main.SOC_MODULE_ID) {
                list_names = activity.getApplicationContext().getResources().getStringArray(R.array.soc_list_names);
                list_tags = list_names;
                default_state = activity.getApplicationContext().getResources().getStringArray(R.array.soc_defaults);
            }
            if (list_names != null && list_tags != null && default_state != null) {
                for (int counter = 0; counter < list_names.length; counter++) {
                    LinearLayout lv = (LinearLayout) activity.findViewById(R.id.outer_box);
                    if (option == Main.PROFILE_MODULE_ID) {
                        xmlSerializer.startTag(null, list_tags[counter]);
                        xmlSerializer.startTag(null, "presentState");
                        xmlSerializer.text(default_state[counter]);
                        xmlSerializer.endTag(null, "presentState");
                        xmlSerializer.startTag(null, "prevState");
                        xmlSerializer.text(String.valueOf(0));
                        xmlSerializer.endTag(null, "prevState");
                        xmlSerializer.startTag(null, "prevState2");
                        xmlSerializer.text(String.valueOf(0));
                        xmlSerializer.endTag(null, "prevState2");
                        xmlSerializer.endTag(null, list_tags[counter]);
                    } else {
                        xmlSerializer.startTag(null, list_names[counter]);
                        xmlSerializer.startTag(null, "presentState");
                        xmlSerializer.text(default_state[counter]);
                        xmlSerializer.endTag(null, "presentState");
                        xmlSerializer.startTag(null, "prevState");
                        xmlSerializer.text(String.valueOf(0));
                        xmlSerializer.endTag(null, "prevState");
                        xmlSerializer.startTag(null, "prevState2");
                        xmlSerializer.text(String.valueOf(0));
                        xmlSerializer.endTag(null, "prevState2");
                        xmlSerializer.endTag(null, list_names[counter]);
                    }
                }
            }
            xmlSerializer.endTag(null, "Data");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, e.toString());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "Illegal argument");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "Illegal state");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(Main.TAG, "IO Exception");
        }
        finally {
            try {
                if (fileos != null)
                    fileos.close();
            } catch (IOException e) {
                Log.e(Main.TAG, "IO Exception");
            }
        }
    }
}
