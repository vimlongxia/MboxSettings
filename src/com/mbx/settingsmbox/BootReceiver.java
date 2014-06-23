package com.mbx.settingsmbox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.List;

import android.app.ActivityManager;
import android.app.SystemWriteManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.ScanResult;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerPolicy;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "MboxSetting.BootReceiver";
    private static final String PREFERENCE_BOX_SETTING = "preference_box_settings";
    private SharedPreferences sharedPrefrences;
    Context mContext = null;

    private static final String FREQ_DEFAULT = "";
    private static final String FREQ_SETTING = "50hz";

    public static final boolean mAutoStartConfig = true;
    private static final String PROP_CEC_LANGUAGE_AUTO_SWITCH = "auto.swtich.language.by.cec";
    private static final String mAutoLanguagePreferencesFile = "cec_language_preferences";

    public static final int HDMICHECK_START = 18001;
    public static final int HDMICHECK_STOP = 18002;
    public static final int HDMICHECK_UNPLUGGED = 18003;
    public static final int DISABLE_OUTOUTMODE_SETTING = 18004;
    public static final int ENABLE_OUTOUTMODE_SETTING = 18005;

    public static final int EXECUTE_ONCE = 1;
    public static final int EXECUTE_MANY = 2;
    public static final int EXECUTE_UNPLUGGED = 3;
    private final boolean isForTopResolution = false;
    private final String[] mUsualResolutions = { "1080p", "1080p50hz", "1080i",
            "1080i50hz", "720p", "720p50hz", "576p", "576i", "480p", "480i" };

    private final String ACTION_OUTPUTMODE_CHANGE = "android.intent.action.OUTPUTMODE_CHANGE";
    private final String ACTION_OUTPUTMODE_SAVE = "android.intent.action.OUTPUTMODE_SAVE";
    private final String ACTION_OUTPUTMODE_CANCEL = "android.intent.action.OUTPUTMODE_CANCEL";
    private final String OUTPUT_MODE = "output_mode";
    private final String CVBS_MODE = "cvbs_mode";
    private final String mOutputStatusConfig = "/sys/class/amhdmitx/amhdmitx0/disp_cap";
    private final static String mHDMIStatusConfig = "/sys/class/amhdmitx/amhdmitx0/hpd_state";
    private final String mCurrentResolution = "/sys/class/display/mode";
    private final String mHdmiUnplugged = "/sys/class/aml_mod/mod_on";
    private final String mHdmiPlugged = "/sys/class/aml_mod/mod_off";
    private final String FreescaleFb0File = "/sys/class/graphics/fb0/free_scale";
    private final String blankFb0File = "/sys/class/graphics/fb0/blank";
    private static final String STR_1080SCALE = "ro.platform.has.1080scale";
    private final String VideoAxisFile = "/sys/class/video/axis";
    private final String DispFile = "/sys/class/ppmgr/disp";

    private final String ACTION_DISP_CHANGE = "android.intent.action.DISP_CHANGE";
    private final String ACTION_REALVIDEO_ON = "android.intent.action.REALVIDEO_ON";
    private final String ACTION_REALVIDEO_OFF = "android.intent.action.REALVIDEO_OFF";
    private final String ACTION_VIDEOPOSITION_CHANGE = "android.intent.action.VIDEOPOSITION_CHANGE";
    private final String ACTION_CVBSMODE_CHANGE = "android.intent.action.CVBSMODE_CHANGE";

    SystemWriteManager sw;

    TimerTask weatherBroadcastServicesTask = null;
    WifiUtils mWifiUtils = null;
    WifiManager mWifiManager = null;
    String isAutoSelectOutMode = "true";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "==== BootReceiver , action : " + intent.getAction());
        mContext = context;
        sw = (SystemWriteManager) mContext.getSystemService("system_write");
        sharedPrefrences = context.getSharedPreferences(PREFERENCE_BOX_SETTING,
                Context.MODE_PRIVATE);
        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        String action = intent.getAction();

        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            // ======================================start system boot process

            // ========= for CEC
            String isCecLanguageOpen = sharedPrefrences.getString(
                    "cec_language_open", "false");
            if (isCecLanguageOpen.equals("true")) {
                Log.d(TAG, "===== start cec language checking service");
                Intent serviceIntent = new Intent(mContext,
                        CecCheckingService.class);
                serviceIntent.setAction("CEC_LANGUAGE_AUTO_SWITCH");
                mContext.startService(serviceIntent);
            }

            // ======== for request screen
            String isRequest_screen = sw.getPropertyString(
                    "ubootenv.var.has.accelerometer", "***");
            if ("***".equals(isRequest_screen)) {
                sw.setProperty("ubootenv.var.has.accelerometer", "false");
            }

            // ======== for hdmi and cvbs mode check
            String currentMode = sw.readSysfs(mCurrentResolution);

            Log.d(TAG, "===== currentMode : " + currentMode);
            if (!HdmiManager.isHDMIPlugged(sw)) {
                Log.d(TAG, "===== hdmi unplug ");
                if (!currentMode.contains("cvbs")) {
                    Log.d(TAG, "===== hdmi unplug process");
                    // HdmiManager mHdmiManager = new HdmiManager(mContext);
                    // mHdmiManager.hdmiUnPlugged();
                }
            } else {
                Log.d(TAG, "===== hdmi plug ");
                if (currentMode.contains("cvbs")) {
                    Log.d(TAG, "===== hdmi plug process");
                    // HdmiManager mHdmiManager = new HdmiManager(mContext);
                    // mHdmiManager.hdmiPlugged();
                }

            }

            // ======== for reconecnt wifi
            reconnectWifi();

            // ======== for auto output mode
            if (HdmiManager.isHDMIPlugged(sw)) {
                if (sw.getPropertyBoolean("ro.platform.hdmionly", true)) {
                    isAutoSelectOutMode = sharedPrefrences.getString(
                            "auto_output_mode", "true");
                    if ("true".equals(isAutoSelectOutMode)) {
                        HdmiManager mHdmiManager = new HdmiManager(mContext);
                        mHdmiManager.hdmiPlugged();
                    }
                }

            }

            // ======== for scrreen timeout
            int timeout = sharedPrefrences.getInt("screen_timeout", -1);

            if (timeout < 0) {
                Log.d(TAG, "===== set timeout : " + Integer.MAX_VALUE);
                Settings.System.putInt(context.getContentResolver(),
                        "screen_off_timeout", Integer.MAX_VALUE);
            }

            // ======================================end system boot process
        } else if ("AUTO.CHANGE.OUTPUT.MODE".equals(action)) {
            isAutoSelectOutMode = sharedPrefrences.getString(
                    "auto_output_mode", "true");
            if ("true".equals(isAutoSelectOutMode)
                    && HdmiManager.isHDMIPlugged(sw)) {
                HdmiManager mHdmiManager = new HdmiManager(mContext);
                mHdmiManager.hdmiPlugged();
            }
        } else if ("android.amlogic.launcher.REQUEST_WEATHER".equals(action)) {
            // =================for weather
            new WeatherBroadcastThread(mContext).start();
        } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            String value = sw.getPropertyString("sys.weather.send", "false");
            if ("false".equals(value)) {
                upDateWeather();
            }
        } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            // =================for save wifi info
            Editor editor = mContext.getSharedPreferences(
                    PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE).edit();
            boolean isWifiConnected = WifiUtils.isWifiConnected(mContext);
            if (isWifiConnected) {
                String password = WifiUtils.getPassWord();
                WifiInfo mWifiinfo = mWifiManager.getConnectionInfo();
                if (password == null)
                    password = "***";
                editor.putString("wifi_connected_password", password);
                String mApName = WifiUtils.getApName();
                if (mApName == null)
                    mApName = "***";
                editor.putString("wifi_connected_ssid", mApName);
                editor.commit();
                Log.d(TAG, "===== save ap name : " + mApName);
            }
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            // WifiManager mWifiManager = (WifiManager)
            // mContext.getSystemService(Context.WIFI_SERVICE);
            // mWifiManager.reconnect();
            // =========for wifi connect
        } else if (WindowManagerPolicy.ACTION_HDMI_HW_PLUGGED.equals(action)) {
            boolean plugged = intent.getBooleanExtra(
                    WindowManagerPolicy.EXTRA_HDMI_HW_PLUGGED_STATE, false);
            if (plugged) {
                Log.d(TAG, "===== himi plugged");
                HdmiManager mHdmiManager = new HdmiManager(mContext);
                mHdmiManager.hdmiPlugged();
            } else {
                Log.d(TAG, "===== himi unplugged");
                HdmiManager mHdmiManager = new HdmiManager(mContext);
                mHdmiManager.hdmiUnPlugged();
            }
        }
    }

    void reconnectWifi() {
        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        if (getWifiCheckBoxState()) {
            mWifiManager.setWifiEnabled(true);
            mWifiUtils = new WifiUtils(mContext);

            String name = sharedPrefrences.getString("wifi_connected_ssid",
                    "***");
            Log.d(TAG, "===== get ap name : " + name);
            String password = sharedPrefrences.getString(
                    "wifi_connected_password", "***");
            if (!"***".equals(name)) {
                List<ScanResult> mScanResultList = new ArrayList<ScanResult>();
                mScanResultList = mWifiUtils.getWifiAccessPointList();
                ScanResult result = null;
                for (ScanResult s : mScanResultList) {
                    if (s.SSID.equals(name)) {
                        result = s;
                        break;
                    }
                }
                if (result != null) {
                    if (!"***".equals(password)) {
                        Log.d(TAG, "===== connect to : " + name);
                        mWifiUtils.connect2AccessPoint(result, password);
                    } else {
                        Log.d(TAG, "===== connect without password");
                        mWifiUtils.connect2AccessPoint(result, null);
                    }
                }
            }
        } else {
            boolean isWifiEnabled = mWifiManager.isWifiEnabled();
            if (isWifiEnabled) {
                mWifiManager.setWifiEnabled(false);
            }
        }
    }

    void upDateWeather() {
        State wifiState = null;
        State mobileState = null;
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        if (wifiState != null && State.CONNECTED == wifiState) {
            Log.d(TAG, "wifi connect , send weather info right now !!!");
            new WeatherBroadcastThread(mContext).start();
        }

    }

    void setSharedPrefrences() {
        Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,
                Context.MODE_PRIVATE).edit();

        editor.putInt("first_start_up", 0);
        editor.commit();

    }

    private boolean isAmlogicVideoPlayerRunning() {
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
        String className = componentName.getClassName();
        String packageName = componentName.getPackageName();

        String videoPlayerClassName = "com.farcore.videoplayer.playermenu";

        if (className.equalsIgnoreCase(videoPlayerClassName)) {
            return true;
        }
        return false;

    }

    private String getBestMatchResolution() {
        ArrayList<String> resolutionList = new ArrayList<String>();

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String readLine = null;

        try {
            fileReader = new FileReader(mOutputStatusConfig);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bufferedReader = new BufferedReader(fileReader);

        try {
            while ((readLine = bufferedReader.readLine()) != null) {
                resolutionList.add(readLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resolutionList.isEmpty()) {
            return "720p";
        }

        if (isForTopResolution) {
            for (int index = 0; index < mUsualResolutions.length; index++) {
                if (resolutionList.contains(mUsualResolutions[index])) {
                    return mUsualResolutions[index];
                }
            }
        } else {
            for (int index = 0; index < resolutionList.size(); index++) {
                if (resolutionList.get(index).contains("*")) {
                    return resolutionList.get(index);
                }
            }
        }

        return "720p";
    }

    public String getBestResolution(String resolution) {
        // Force to set to 720p
        if (resolution == null) {
            resolution = "720p" + FREQ_DEFAULT;
        } else {
            if (resolution.contains("480i")) {
                resolution = "480i";
            } else if (resolution.contains("480cvbs")) {
                resolution = "480cvbs";
            } else if (resolution.contains("480p")) {
                resolution = "480p";
            } else if (resolution.contains("576i")) {
                resolution = "576i";
            } else if (resolution.contains("576cvbs")) {
                resolution = "576cvbs";
            } else if (resolution.contains("576p")) {
                resolution = "576p";
            } else if (resolution.contains("720p")) {
                if (resolution.contains(FREQ_SETTING)) {
                    resolution = "720p" + FREQ_SETTING;
                } else {
                    resolution = "720p" + FREQ_DEFAULT;
                }
            } else if (resolution.contains("1080i")) {
                if (resolution.contains(FREQ_SETTING)) {
                    resolution = "1080i" + FREQ_SETTING;
                } else {
                    resolution = "1080i" + FREQ_DEFAULT;
                }
            } else if (resolution.contains("1080p")) {
                if (resolution.contains(FREQ_SETTING)) {
                    resolution = "1080p" + FREQ_SETTING;
                } else {
                    resolution = "1080p" + FREQ_DEFAULT;
                }
            }
        }
        return resolution;
    }

    private boolean getWifiCheckBoxState() {
        int state = Settings.Global.getInt(mContext.getContentResolver(),
                Settings.Global.WIFI_ON, 0);
        // int state = mWifiManager.getWifiState();
        Log.d(TAG, "===== getWifiCheckBoxState() , state : " + state);
        if (state == 1) {
            Log.d(TAG, "===== getWifiCheckBoxState() , true ");
            return true;
        } else {
            Log.d(TAG, "===== getWifiCheckBoxState() , false ");
            return false;
        }
    }

}
