package com.mbx.settingsmbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.SystemWriteManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

public class HdmiManager{
    private final String TAG = "HdmiManager";
	private final String ACTION_OUTPUTMODE_CHANGE = "android.intent.action.OUTPUTMODE_CHANGE";
	private final String ACTION_OUTPUTMODE_SAVE = "android.intent.action.OUTPUTMODE_SAVE";
	private final String ACTION_OUTPUTMODE_CANCEL = "android.intent.action.OUTPUTMODE_CANCEL";
    private final String FreescaleFb0File = "/sys/class/graphics/fb0/free_scale";
    private static final String FREQ_DEFAULT = "";
	private static final String FREQ_SETTING = "50hz";
    private final static String mHdmiUnplugged = "/sys/class/aml_mod/mod_on";
    private final static String blankFb0File = "/sys/class/graphics/fb0/blank";
    private static final String ACTION_REALVIDEO_ON = "android.intent.action.REALVIDEO_ON";
    private static final String ACTION_VIDEOPOSITION_CHANGE = "android.intent.action.VIDEOPOSITION_CHANGE";
    private final String CURRENT_MODE = "/sys/class/display/mode";
    private final String mHdmiPlugged = "/sys/class/aml_mod/mod_off";
    private final String mOutputStatusConfig = "/sys/class/amhdmitx/amhdmitx0/disp_cap";
    private final String OUT_PUT_PROP = "ubootenv.var.outputmode";
	private final String OUTPUT_MODE = "output_mode";
	private final String CVBS_MODE = "cvbs_mode";
    private static SystemWriteManager sw = null;
    private Context mContext = null;
    private static final String HDMI_SUPPORT_LIST = "/sys/class/amhdmitx/amhdmitx0/disp_cap";
    private static final String PREFERENCE_BOX_SETTING = "preference_box_settings";

    
    private SharedPreferences sharedPrefrences = null;
    public HdmiManager(Context context){
        mContext = context;
        sw = (SystemWriteManager) mContext.getSystemService("system_write");
    }
    
    public void hdmiUnPlugged(){
        Log.d(TAG,"===== hdmiUnPlugged()");
        if(sw.getPropertyBoolean("ro.platform.hdmionly",true)){         
            String cvbsmode = sw.getPropertyString("ubootenv.var.cvbsmode","480cvbs");
            if(isFreeScaleClosed()){
                setOutputWithoutFreeScale(cvbsmode);    
            }else{
                setOutputMode(cvbsmode);
            }
            sw.writeSysfs(mHdmiUnplugged,"vdac");//open vdac 
            sw.writeSysfs(blankFb0File,"0");
        }
    }

    public void hdmiPlugged(){
        Log.d(TAG,"===== hdmiPlugged()");
        sharedPrefrences = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE);
		String isAutoMode = sharedPrefrences.getString("auto_output_mode", "true");
        boolean isAutoHdmiMode = false ;
        if("true".equals(isAutoMode)){
            isAutoHdmiMode = true ;
        }else{
            isAutoHdmiMode = false ;
        }
        if(sw.getPropertyBoolean("ro.platform.hdmionly",true)){
            sw.writeSysfs(mHdmiPlugged,"vdac");
            if(isAutoHdmiMode){
                if (isFreeScaleClosed()) {
                    setOutputWithoutFreeScale(getBestResolution(getBestMatchResolution()));
                }else{
                    setOutputMode(getBestResolution(getBestMatchResolution()));
                }     

            }else{
                String mHdmiOutputMode = sw.getPropertyString("ubootenv.var.hdmimode", "720p");
                if(isFreeScaleClosed())
                    setOutputWithoutFreeScale(mHdmiOutputMode);
                else
                    setOutputMode(mHdmiOutputMode);
            }
            sw.writeSysfs(blankFb0File,"0");
        }
    }
     
    public void setOutputWithoutFreeScale(String newMode){
        Log.d(TAG,"===== setOutputWithoutFreeScale()");
        OutPutModeManager.change2NewModeWithoutFreeScale(sw,newMode);
    }


	private void setOutputMode(String mode ) {        
        OutPutModeManager.change2NewMode(sw , mode);
	}

    public String getBestResolution(String resolution){

        if (resolution.contains("480i")) {
            resolution = "480i";
        } else if(resolution.contains("480cvbs")){
            resolution = "480cvbs";
        }else if (resolution.contains("480p")) {
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
                resolution ="720p" + FREQ_DEFAULT;
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
   
        return resolution;
    }

    public boolean isFreeScaleClosed(){

        String freeScaleStatus = sw.readSysfs(FreescaleFb0File);
        if(freeScaleStatus.contains("0x0")){
            Log.d(TAG,"freescale is closed");
            return true;
        }else{
            Log.d(TAG,"freescale is open");
            return false;
        }
    }

    private String getBestMatchResolution() {
        String[] supportList = null;
        String value = readSupportList(HDMI_SUPPORT_LIST);
        if(value != null){
            supportList = (value.substring(0, value.length()-1)).split(",");
            Log.d(TAG,"===== supportList size() is " + supportList.length);
        }else{
    		return "720p";  
        }
        if(supportList ==null){
            return "720p";  
        }
        
        for (int index = 0; index < supportList.length; index++) {
            Log.d(TAG,"===== suport mode : " + supportList[index]);
            if (supportList[index].contains("*")) {
                Log.d(TAG,"===== best mode is : " + supportList[index]);
                String str = supportList[index];
            	return str.substring(0,str.length()-1);
            }
        }
        
        return "720p";
    }

    public static  boolean isHDMIPlugged(SystemWriteManager sw) {
        String status = sw.readSysfs("/sys/class/amhdmitx/amhdmitx0/hpd_state");
        if ("1".equals(status))
            return true;
        else
            return false;
    }


     private  String readSupportList(String path) {
		
        String str = null;
        StringBuilder value = new StringBuilder();    
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            try {
                while ((str = br.readLine()) != null) {
                    if(str != null){ 
                        value.append(str);
                        value.append(",");
                    }
                };
				fr.close();
				br.close();
                if(value != null){                    
                    Log.d(TAG,"===== support list is : " + value.toString());
                    return value.toString();
                }
                else 
                    return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
