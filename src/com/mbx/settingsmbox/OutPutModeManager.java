package com.mbx.settingsmbox;

import android.app.SystemWriteManager;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;





public class OutPutModeManager {
    private static final String TAG = "OutPutModeManager";

	private Context mContext = null;

	private final static String sel_480ioutput_x = "ubootenv.var.480ioutputx";
	private final static String sel_480ioutput_y = "ubootenv.var.480ioutputy";
	private final static String sel_480ioutput_width = "ubootenv.var.480ioutputwidth";
	private final static String sel_480ioutput_height = "ubootenv.var.480ioutputheight";
	private final static String sel_480poutput_x = "ubootenv.var.480poutputx";
	private final static String sel_480poutput_y = "ubootenv.var.480poutputy";
	private final static String sel_480poutput_width = "ubootenv.var.480poutputwidth";
	private final static String sel_480poutput_height = "ubootenv.var.480poutputheight";
	private final static String sel_576ioutput_x = "ubootenv.var.576ioutputx";
	private final static String sel_576ioutput_y = "ubootenv.var.576ioutputy";
	private final static String sel_576ioutput_width = "ubootenv.var.576ioutputwidth";
	private final static String sel_576ioutput_height = "ubootenv.var.576ioutputheight";
	private final static String sel_576poutput_x = "ubootenv.var.576poutputx";
	private final static String sel_576poutput_y = "ubootenv.var.576poutputy";
	private final static String sel_576poutput_width = "ubootenv.var.576poutputwidth";
	private final static String sel_576poutput_height = "ubootenv.var.576poutputheight";
	private final static String sel_720poutput_x = "ubootenv.var.720poutputx";
	private final static String sel_720poutput_y = "ubootenv.var.720poutputy";
	private final static String sel_720poutput_width = "ubootenv.var.720poutputwidth";
	private final static String sel_720poutput_height = "ubootenv.var.720poutputheight";
	private final static String sel_1080ioutput_x = "ubootenv.var.1080ioutputx";
	private final static String sel_1080ioutput_y = "ubootenv.var.1080ioutputy";
	private final static String sel_1080ioutput_width = "ubootenv.var.1080ioutputwidth";
	private final static String sel_1080ioutput_height = "ubootenv.var.1080ioutputheight";
	private final static String sel_1080poutput_x = "ubootenv.var.1080poutputx";
	private final static String sel_1080poutput_y = "ubootenv.var.1080poutputy";
	private final static String sel_1080poutput_width = "ubootenv.var.1080poutputwidth";
	private final static String sel_1080poutput_height = "ubootenv.var.1080poutputheight";
	private static final int OUTPUT480_FULL_WIDTH = 720;
	private static final int OUTPUT480_FULL_HEIGHT = 480;
	private static final int OUTPUT576_FULL_WIDTH = 720;
	private static final int OUTPUT576_FULL_HEIGHT = 576;
	private static final int OUTPUT720_FULL_WIDTH = 1280;
	private static final int OUTPUT720_FULL_HEIGHT = 720;
	private static final int OUTPUT1080_FULL_WIDTH = 1920;
	private static final int OUTPUT1080_FULL_HEIGHT = 1080;
    
	private static final String blankFb0File = "/sys/class/graphics/fb0/blank";
	private static final String PpscalerRectFile = "/sys/class/ppmgr/ppscaler_rect";
    private static final String UpdateFreescaleFb0File = "/sys/class/graphics/fb0/update_freescale";
	private static final String FreescaleFb0File = "/sys/class/graphics/fb0/free_scale";
	private static final String FreescaleFb1File = "/sys/class/graphics/fb1/free_scale";
	private static final String mHdmiPluggedVdac = "/sys/class/aml_mod/mod_off";
    private static final String mHdmiUnpluggedVdac = "/sys/class/aml_mod/mod_on";
    private static final String HDMI_SUPPORT_LIST_SYSFS = "/sys/class/amhdmitx/amhdmitx0/disp_cap";
    private static final String PpscalerFile = "/sys/class/ppmgr/ppscaler";
	private static final String VideoAxisFile = "/sys/class/video/axis";
    private static final String request2XScaleFile = "/sys/class/graphics/fb0/request2XScale";
	private static final String scaleAxisOsd0File = "/sys/class/graphics/fb0/scale_axis";
	private static final String scaleAxisOsd1File = "/sys/class/graphics/fb1/scale_axis";
	private static final String scaleOsd1File = "/sys/class/graphics/fb1/scale";
    private static final String OutputModeFile = "/sys/class/display/mode";
	private static final String OutputAxisFile= "/sys/class/display/axis";

    private static final String[] HDMI_MODE_VALUE_LIST = {"480i","480p","576i","576p","720p","1080i","1080p","720p50hz","1080i50hz","1080p50hz"};
    private static final String[] HDMI_MODE_TITLE_LIST = {"HDMI 480I","HDMI 480P","HDMI 576I","HDMI 576P","HDMI 720P 60HZ","HDMI 1080I 60HZ","HDMI 1080P 60HZ","HDMI 720P 50HZ","HDMI 1080I 50HZ","HDMI 1080P 50HZ"};
    private static final String[] CVBS_MODE_VALUE_LIST = {"480cvbs","576cvbs"}; 
    private static final String[] CVBS_MODE_TITLE_LIST = {"480 CVBS","576 CVBS"}; 
    private static final String[] COMMON_MODE_VALUE_LIST =  {"480i","480p","576i","576p","720p","1080i","1080p","720p50hz","1080i50hz","1080p50hz" , "480cvbs","576cvbs"};
    private static final String CVBS_MODE_PROP = "ubootenv.var.cvbsmode";
    private static final String HDMI_MODE_PROP = "ubootenv.var.hdmimode";
    private static final String COMMON_MODE_PROP = "ubootenv.var.outputmode";

   
	private final static String DISPLAY_MODE_SYSFS = "/sys/class/display/mode";
    private final static String HDMI_ONLY_MODE = "ro.platform.hdmionly";

    //=== hdmi + cvbs dual
    private final static String DISPLAY_MODE_SYSFS_DUAL = "/sys/class/display2/mode";
    private final static String HDMI_CVBS_DUAL = "ro.platform.has.cvbsmode"; 

    private ArrayList<String> mTitleList = null;
    private ArrayList<String> mValueList = null;
    private ArrayList<String> mSupportList = null;
    
	OutPutListAdapter adapter = null;
	private static  SystemWriteManager sw;

    private static String mUiMode = "hdmi";
    private Runnable setBackRunnable = null;
    private	Handler myHandler = null;


    public OutPutModeManager(Context context , String mode){
        mContext = context;
        mUiMode = mode;
		sw = (SystemWriteManager) mContext.getSystemService("system_write");
        mTitleList = new ArrayList<String>();
        mValueList = new ArrayList<String>();
        initModeValues(mUiMode);    
    }
	
	public OutPutModeManager(Context context, ListView listview , String mode) {
		mContext = context;
		sw = (SystemWriteManager) mContext.getSystemService("system_write");
        mUiMode = mode;
        initModeValues(mUiMode);
		adapter = new OutPutListAdapter(mContext,mTitleList,getCurrentSelectItemID());
		listview.setAdapter(adapter);
	}

    private int getCurrentSelectItemID(){
         String currentHdmiMode = sw.readSysfs(DISPLAY_MODE_SYSFS);
         for(int i=0 ; i < mValueList.size();i++){
             if(currentHdmiMode.equals(mValueList.get(i))){
                return i ;
             }
         }
         
         if(mUiMode.equals("hdmi")){
            return 4;
         }else{
            return 0;
         }   
    }

    private void initModeValues(String mode){
        mTitleList = new ArrayList<String>();
        mValueList = new ArrayList<String>();
        mSupportList = new ArrayList<String>();
        if(mode.equalsIgnoreCase("hdmi")){
            mSupportList = getCurrentSupportList();
            for(int i=0 ; i< HDMI_MODE_VALUE_LIST.length ; i++){
                for(int j=0 ;j < mSupportList.size(); j++){
                    if(mSupportList.get(j).equals(HDMI_MODE_VALUE_LIST[i]) ){
                        mTitleList.add(HDMI_MODE_TITLE_LIST[i]);
                        mValueList.add(HDMI_MODE_VALUE_LIST[i]);
                    }     
                }
            }
            
        }else if(mode.equalsIgnoreCase("cvbs")){          
            for(int i = 0 ; i< CVBS_MODE_VALUE_LIST.length; i++){
                mTitleList.add(CVBS_MODE_VALUE_LIST[i]);
            }          
            for(int i=0 ; i < CVBS_MODE_VALUE_LIST.length ; i++){
                mValueList.add(CVBS_MODE_VALUE_LIST[i]);
            }
        } 
   
    }

	public static String getCurrentOutPutModeTitle(SystemWriteManager swm , String ui) {
        Log.d(TAG,"==== getCurrentOutPutMode() " );
        String currentHdmiMode = swm.readSysfs(DISPLAY_MODE_SYSFS);
        if(ui.equalsIgnoreCase("hdmi")){
           
            for(int i=0 ; i< HDMI_MODE_VALUE_LIST.length ; i++){
                if(currentHdmiMode.equals(HDMI_MODE_VALUE_LIST[i])){
                    return HDMI_MODE_TITLE_LIST[i] ;
                }
            }
            return HDMI_MODE_TITLE_LIST[4];
            
        }else if(ui.equalsIgnoreCase("cvbs")){
        
            for(int i=0 ; i < CVBS_MODE_VALUE_LIST.length ; i++){
                if(currentHdmiMode.equals(CVBS_MODE_VALUE_LIST[i])){
                    return CVBS_MODE_TITLE_LIST[i] ;
                }
            }
            return CVBS_MODE_TITLE_LIST[0];
        }
        return HDMI_MODE_TITLE_LIST[4];
	}

	public void selectItem(int index) {
        final String oldMode = sw.readSysfs(DISPLAY_MODE_SYSFS);
        String mode = mValueList.get(index) ;
        if(mode.contains("cvbs")&& isHdmiCvbsDual()){
            setCvbsInDualMode(mode);
        }else{
            change2NewMode(sw,mode); 
            saveNewMode2Prop(mode);
        }
    	adapter.setSelectItem(index);
        /*
                if(myHandler == null){
                    myHandler = new Handler();
                }
                setBackRunnable = new Runnable() {
                    @Override
                    public void run() {
                    Utils.writeSysFile(sw,blankFb0File,"0");
                    }
                };
                myHandler.postDelayed(setBackRunnable,3000);
            */
	
	}

	private static int[] getPosition(SystemWriteManager swm ,String mode) {

		int[] curPosition = { 0, 0, 1280, 720 };
		int index = 4; // 720p
		for (int i = 0; i < COMMON_MODE_VALUE_LIST.length; i++) {
			if (mode.equalsIgnoreCase(COMMON_MODE_VALUE_LIST[i]))
				index = i;
		}

		switch (index) {
		case 0: // 480i
			curPosition[0] = swm.getPropertyInt(sel_480ioutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_480ioutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_480ioutput_width, 720);
			curPosition[3] = swm.getPropertyInt(sel_480ioutput_height, 480);
			break;
		case 1: // 480p
			curPosition[0] = swm.getPropertyInt(sel_480poutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_480poutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_480poutput_width, 720);
			curPosition[3] = swm.getPropertyInt(sel_480poutput_height, 480);
			break;
		case 2: // 576i
			curPosition[0] = swm.getPropertyInt(sel_576ioutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_576ioutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_576ioutput_width, 720);
			curPosition[3] = swm.getPropertyInt(sel_576ioutput_height, 576);
			break;
		case 3: // 576p
			curPosition[0] = swm.getPropertyInt(sel_576poutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_576poutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_576poutput_width, 720);
			curPosition[3] = swm.getPropertyInt(sel_576poutput_height, 576);
			break;
		case 4: // 720p
		case 7: // 720p50hz
			curPosition[0] = swm.getPropertyInt(sel_720poutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_720poutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_720poutput_width, 1280);
			curPosition[3] = swm.getPropertyInt(sel_720poutput_height, 720);
			break;

		case 5: // 1080i
		case 8: // 1080i50hz
			curPosition[0] = swm.getPropertyInt(sel_1080ioutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_1080ioutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_1080ioutput_width, 1920);
			curPosition[3] = swm.getPropertyInt(sel_1080ioutput_height, 1080);
			break;

		case 6: // 1080p
		case 9: // 1080p50hz
			curPosition[0] = swm.getPropertyInt(sel_1080poutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_1080poutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_1080poutput_width, 1920);
			curPosition[3] = swm.getPropertyInt(sel_1080poutput_height, 1080);
			break;
		case 10: // 480cvbs
			curPosition[0] = swm.getPropertyInt(sel_480ioutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_480ioutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_480ioutput_width, 720);
			curPosition[3] = swm.getPropertyInt(sel_480ioutput_height, 480);
			break;
		case 11: // 576cvbs
			curPosition[0] = swm.getPropertyInt(sel_576ioutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_576ioutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_576ioutput_width, 720);
			curPosition[3] = swm.getPropertyInt(sel_576ioutput_height, 576);
			break;
		default: // 720p
			curPosition[0] = swm.getPropertyInt(sel_720poutput_x, 0);
			curPosition[1] = swm.getPropertyInt(sel_720poutput_y, 0);
			curPosition[2] = swm.getPropertyInt(sel_720poutput_width, 1280);
			curPosition[3] = swm.getPropertyInt(sel_720poutput_height, 720);
			break;
		}

		return curPosition;
	}

    public static void change2NewMode(SystemWriteManager swm ,String newMode) {
        
        Log.d(TAG,"===== change2NewMode() : " + newMode);
        String curMode = Utils.readSysFile(swm, DISPLAY_MODE_SYSFS);	
        if(newMode.equals(curMode)){
            Log.d(TAG,"===== The same mode as current , do nothing !");
        }
        Utils.writeSysFile(swm, DISPLAY_MODE_SYSFS,newMode);	
        int[] curPosition = getPosition(swm ,newMode);

        String value = curPosition[0] + " " + curPosition[1]
                + " " + (curPosition[2] + curPosition[0] - 1)
                + " " + (curPosition[3] + curPosition[1] - 1)
                + " " + 0;
        Utils.writeSysFile(swm, PpscalerRectFile, value);
        //===== for new sysfs
        //Utils.writeSysFile(swm, UpdateFreescaleFb0File, "1");
        //Log.d(TAG,"===== do nothing about freescale !");

        //===== for old sysfs
        Utils.writeSysFile(swm, FreescaleFb0File, "0");
        Utils.writeSysFile(swm, FreescaleFb1File, "0");
        Utils.writeSysFile(swm, FreescaleFb0File, "1");
        Utils.writeSysFile(swm, FreescaleFb1File, "1");	
        
        closeVdac(swm,newMode);
        swm.setProperty(COMMON_MODE_PROP, newMode);   
	}

    public static String getCurrentOutputResolution(SystemWriteManager swm){
        String mode =  swm.readSysfs(DISPLAY_MODE_SYSFS);
        if ("480cvbs".equalsIgnoreCase(mode)) {
            mode = "480i";
        } else if ("576cvbs".equalsIgnoreCase(mode)) {
            mode = "576i";
        }
        return mode;
    }

    public static void change2NewModeWithoutFreeScale(SystemWriteManager swm, String newMode){

        swm.writeSysfs(blankFb0File, "1");                     
		swm.writeSysfs(PpscalerFile, "0");
		swm.writeSysfs(FreescaleFb0File, "0");
		swm.writeSysfs(FreescaleFb1File, "0");
        swm.writeSysfs(DISPLAY_MODE_SYSFS,newMode);
        swm.setProperty(COMMON_MODE_PROP,newMode);
        
    	int[] curPosition = {0, 0, 1280, 720};
        curPosition = getPosition(swm,newMode);    
        String value = curPosition[0] + " " + curPosition[1]+ " " + (curPosition[2] + curPosition[0] - 1) + " "+ (curPosition[3] + curPosition[1] - 1) ;
        
        if((newMode.equals(COMMON_MODE_VALUE_LIST[5])) || (newMode.equals(COMMON_MODE_VALUE_LIST[6]))
                    || (newMode.equals(COMMON_MODE_VALUE_LIST[8])) || (newMode.equals(COMMON_MODE_VALUE_LIST[9]))){
            swm.writeSysfs(OutputAxisFile, ((int)(curPosition[0]/2))*2 + " " + ((int)(curPosition[1]/2))*2 
            + " 1280 720 "+ ((int)(curPosition[0]/2))*2 + " "+ ((int)(curPosition[1]/2))*2 + " 18 18");
                swm.writeSysfs(scaleAxisOsd0File, "0 0 " + (960 - (int)(curPosition[0]/2) - 1)
            + " " + (1080 - (int)(curPosition[1]/2) - 1));
            swm.writeSysfs(request2XScaleFile, "7 " + ((int)(curPosition[2]/2)) + " " + ((int)(curPosition[3]/2))*2);
            swm.writeSysfs(scaleAxisOsd1File, "1280 720 " + ((int)(curPosition[2]/2))*2 + " " + ((int)(curPosition[3]/2))*2);
            swm.writeSysfs(scaleOsd1File, "0x10001");
        }else{
            swm.writeSysfs(OutputAxisFile, curPosition[0] + " " + curPosition[1] 
                + " 1280 720 "+ curPosition[0] + " "+ curPosition[1] + " 18 18");
            swm.writeSysfs(request2XScaleFile, "16 " + curPosition[2] + " " + curPosition[3]);
            swm.writeSysfs(scaleAxisOsd1File, "1280 720 " + curPosition[2] + " " + curPosition[3]);
            swm.writeSysfs(scaleOsd1File, "0x10001");
        }


	    swm.writeSysfs(VideoAxisFile, curPosition[0] + " " + curPosition[1]
					+ " " + (curPosition[2] + curPosition[0] - 1) + " "
					+ (curPosition[3] + curPosition[1] - 1));
        
    }

    private void saveNewMode2Prop(String newMode){
        if(newMode.contains("cvbs")){
            sw.setProperty(CVBS_MODE_PROP, newMode);
        }
        else{
            sw.setProperty(HDMI_MODE_PROP, newMode);
        }         
    }

    public static void closeVdac(SystemWriteManager sw ,String outputmode){
       if(sw.getPropertyBoolean("ro.platform.hdmionly",false)){
           if(!outputmode.contains("cvbs")){
               sw.writeSysfs(mHdmiPluggedVdac,"vdac");
               Log.d(TAG,"close vdac");
           }
       }
    }
    
    public void openVdac(){
        if(sw.getPropertyBoolean("ro.platform.hdmionly",false)){
           sw.writeSysfs(mHdmiUnpluggedVdac,"vdac");     
         }
    }
    
	public void setCvbsInDualMode(String mode) {
        String currentHdmiMode = sw.readSysfs(DISPLAY_MODE_SYSFS);
        if("480i".equals(currentHdmiMode) ||"576i".equals(currentHdmiMode)){
            Utils.writeSysFile(sw, "/sys/class/display2/mode", "null");
        }else{
            String currentMode = sw.readSysfs(DISPLAY_MODE_SYSFS_DUAL);
            if(currentMode.equals(mode)){
                Log.d(TAG,"Set the same mode with current !!!");
                return ;
            }
            if (mode.equals("480cvbs")) {
                Utils.writeSysFile(sw, "/sys/class/display2/mode", "null");
                Utils.writeSysFile(sw, "/sys/class/display2/mode", "480cvbs");
                Utils.writeSysFile(sw, "/sys/class/video2/screen_mode", "1");
                Utils.writeSysFile(sw, "ubootenv.var.cvbsmode", "480cvbs");
            } else if (mode.equals("576cvbs")) {
                Utils.writeSysFile(sw, "/sys/class/display2/mode", "null");
                Utils.writeSysFile(sw, "/sys/class/display2/mode", "576cvbs");
                Utils.writeSysFile(sw, "/sys/class/video2/screen_mode", "1");
                sw.setProperty("ubootenv.var.cvbsmode", "576cvbs");
            }else{
                Log.d(TAG,"Not supprot the cvbs mode right now !!!");
            }

            if (currentHdmiMode.contains("1080")) { 
                Utils.writeSysFile(sw, "/sys/module/amvideo2/parameters/clone_frame_scale_width","960");
            } else {
                Utils.writeSysFile(sw,"/sys/module/amvideo2/parameters/clone_frame_scale_width","0");
            }
        }
	}

	public boolean isHdmiCvbsDual() {
		return Utils.getPropertyBoolean(sw, "ro.platform.has.cvbsmode", false);
	}

    public  String getFilterModes() {
		return Utils.getPropertyString(sw, "ro.platform.filter.modes", "480p,576p,1080i");
	}

    public  ArrayList<String> getCurrentSupportList(){
        ArrayList<String> list = new ArrayList<String>();
        String[] suport_values = null;
        String[] filter_values = null;
        String value = readSupportList(HDMI_SUPPORT_LIST_SYSFS);
        String mFilterModes = getFilterModes();
        if("false".equals(mFilterModes)){
            filter_values = null;
        }else{
            if("true".equals(mFilterModes)){
                filter_values = new String[3] ;
                filter_values[0] = "480p" ;
                filter_values[1] = "576p" ;
                filter_values[2] = "1080i" ;
            }else{
                filter_values = mFilterModes.split(",");
            }
        }

        if(value != null){
            suport_values = (value.substring(0, value.length()-1)).split(",");
        }else{
            suport_values = new String[HDMI_MODE_VALUE_LIST.length];
            for(int i=0; i< HDMI_MODE_VALUE_LIST.length ; i++){
                suport_values[i] = HDMI_MODE_VALUE_LIST[i];
            }
        }

        if(filter_values!=null){
            for(int i=0; i<suport_values.length ; i++ ){ 
                int filter_index = -1;
                for(int j= 0;j<filter_values.length ; j++){
                    if((suport_values[i].equals(filter_values[j]))){
                        filter_index = i;
                        continue;
                    }
                }
                if(i != filter_index ){
                    list.add(suport_values[i]);
                }
            } 
        }else{
             for(int i=0; i<suport_values.length ; i++ ){
                list.add(suport_values[i]);
             }
        }
            
        if(list==null || list.size()==0){
            for(int i=0; i<suport_values.length ; i++ ){ 
                list.add(suport_values[i]);
            }
        }

        for(String s : list){
            Log.d(TAG,"===== support list :" + s);
        }
        
        return list ;
    }

    public static String readSupportList(String path) {
		
        String str = null;
        StringBuilder value = new StringBuilder();    
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            try {
                while ((str = br.readLine()) != null) {
                    if(str != null){ 
                        if(str.contains("*")){
                            value.append(str.substring(0,str.length()-1));
                        }else{
                            value.append(str);
                        }
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
