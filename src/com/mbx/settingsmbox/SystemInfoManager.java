package com.mbx.settingsmbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SystemInfoManager{
    private static final String TAG = "SystemInfoManager";
    private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";
    private static final String FILENAME_PROC_VERSION = "/proc/version";
     
    public static String getModelNumber(){
        String value = Build.MODEL + getMsvSuffix();
        return value;
    }

    public static String getAndroidVersion(){
        return Build.VERSION.RELEASE ;
    }

    public static String getBuildNumber(){
        return Build.DISPLAY ;
    }



    public static String getKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

        } catch (IOException e) {
            Log.e(TAG,
                "IO Exception when getting kernel version for Device Info screen",
                e);
            return "Unavailable";
        }
    }

    

        /**
     * Returns " (ENGINEERING)" if the msv file has a zero value, else returns "".
     * @return a string to append to the model number description.
     */
    private static String getMsvSuffix() {
        // Production devices should have a non-zero value. If we can't read it, assume it's a
        // production device so that we don't accidentally show that it's an ENGINEERING device.
        try {
            String msv = readLine(FILENAME_MSV);
            // Parse as a hex number. If it evaluates to a zero, then it's an engineering build.
            if (Long.parseLong(msv, 16) == 0) {
                return " (ENGINEERING)";
            }
        } catch (IOException ioe) {
        // Fail quietly, as the file may not exist on some devices.
        } catch (NumberFormatException nfe) {
        // Fail quietly, returning empty string should be sufficient
        }
        return "";
    }

    private  static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    private static String formatKernelVersion(String rawKernelVersion) {
    // Example (see tests for more):
    // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
    //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
    //     Thu Jun 28 11:02:39 PDT 2012

    final String PROC_VERSION_REGEX =
        "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
        "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
        "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
        "(#\\d+) " +              /* group 3: "#1" */
        "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
        "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e(TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()+ " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
        m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
        m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }
}
