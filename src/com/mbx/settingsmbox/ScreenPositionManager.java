package com.mbx.settingsmbox;

import android.app.SystemWriteManager;
import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ScreenPositionManager {
    private String TAG = "ScreenPositionManager";
	private Context mContext = null;
	private SystemWriteManager sw = null;

    private final int MAX_Height = 100;
    private final int MIN_Height = 80;
    
	private static float outputsize_per = 0.1f;
	private static float zoomStep = 1f;
    private static float zoomStepWidth = 0f;
    private static float zoomStepHeight = 0f;
	private String curOutputmode = "";

	private String ls = "";
	private String ts = "";
	private String rs = "";
	private String bs = "";

	// sysfs path
	private final static String mCurrentResolution = "/sys/class/display/mode";
	private final static String FreeScaleOsd0File = "/sys/class/graphics/fb0/free_scale";
	private final static String FreeScaleOsd1File = "/sys/class/graphics/fb1/free_scale";
	private final static String PpscalerRectFile = "/sys/class/ppmgr/ppscaler_rect";
	private final static String CPU0ScalingMinFreqPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";

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

	private static final String OUTPUT480_FULL_WIDTH = "720";
	private static final String OUTPUT480_FULL_HEIGHT = "480";
	private static final String OUTPUT576_FULL_WIDTH = "720";
	private static final String OUTPUT576_FULL_HEIGHT = "576";
	private static final String OUTPUT720_FULL_WIDTH = "1280";
	private static final String OUTPUT720_FULL_HEIGHT = "720";
	private static final String OUTPUT1080_FULL_WIDTH = "1920";
	private static final String OUTPUT1080_FULL_HEIGHT = "1080";

	private static final String[] outputmode_array = { "480i", "480p", "576i",
			"576p", "720p", "1080i", "1080p", "720p50hz", "1080i50hz",
			"1080p50hz" };

    private String mCurrentLeftString = null;
    private String mCurrentTopString = null;
    private String mCurrentWidthString = null;
    private String mCurrentHeightString = null;


    private int mCurrentLeft = 0;
    private int mCurrentTop = 0;
    private int mCurrentWidth = 0;
    private int mCurrentHeight = 0;

    
    private int mCurrentRate = MAX_Height;

    private float mPreLeft = 0f;
    private float mPreTop = 0f;
    private float mPreRight = 0f;
    private float mPreBottom = 0f;
    private float mPreWidth = 0f;
    private float mPreHeight = 0f;

    public ScreenPositionManager(Context context) {
		mContext = context;
		sw = (SystemWriteManager) mContext.getSystemService("system_write");

	}

	public void initPostion() {
		curOutputmode = getCurrentOutputResolution();

		initStep(curOutputmode);
        initCurrentPostion();
        
		writeFile(FreeScaleOsd0File, "1");
		writeFile(FreeScaleOsd1File, "1");
        
		setScalingMinFreq(408000);
	}

     private void initCurrentPostion() {
        if (curOutputmode.equals(outputmode_array[0])  ) {
            mCurrentLeftString = 	sw.getPropertyString(sel_480ioutput_x,"0");
            mCurrentTopString = sw.getPropertyString(sel_480ioutput_y,"0");
            mCurrentWidthString = sw.getPropertyString(sel_480ioutput_width,OUTPUT480_FULL_WIDTH);
            mCurrentHeightString = sw.getPropertyString(sel_480ioutput_height,OUTPUT480_FULL_HEIGHT);
            if (mCurrentLeftString.equals(""))
                mCurrentLeftString = "0";
            if (mCurrentTopString.equals(""))
                mCurrentTopString = "0";
            if (mCurrentWidthString.equals(""))
                mCurrentWidthString = OUTPUT480_FULL_WIDTH;
            if (mCurrentHeightString.equals(""))
                mCurrentHeightString = OUTPUT480_FULL_HEIGHT;
        }  else if (curOutputmode.equals(outputmode_array[1])) {
            mCurrentLeftString = 	sw.getPropertyString(sel_480poutput_x,"0");
            mCurrentTopString = sw.getPropertyString(sel_480poutput_y,"0");
            mCurrentWidthString = sw.getPropertyString(sel_480poutput_width,OUTPUT480_FULL_WIDTH);
            mCurrentHeightString = sw.getPropertyString(sel_480poutput_height,OUTPUT480_FULL_HEIGHT);
            if (mCurrentLeftString.equals(""))
                mCurrentLeftString = "0";
            if (mCurrentTopString.equals(""))
                mCurrentTopString = "0";
            if (mCurrentWidthString.equals(""))
                mCurrentWidthString = OUTPUT480_FULL_WIDTH;
            if (mCurrentHeightString.equals(""))
                mCurrentHeightString = OUTPUT480_FULL_HEIGHT;
        } else if (curOutputmode.equals(outputmode_array[2])) {
            mCurrentLeftString = 	sw.getPropertyString(sel_576ioutput_x,"0");
            mCurrentTopString = sw.getPropertyString(sel_576ioutput_y,"0");
            mCurrentWidthString = sw.getPropertyString(sel_576ioutput_width,OUTPUT576_FULL_WIDTH);
            mCurrentHeightString = sw.getPropertyString(sel_576ioutput_height,OUTPUT576_FULL_HEIGHT);
            if (mCurrentLeftString.equals(""))
                mCurrentLeftString = "0";
            if (mCurrentTopString.equals(""))
                mCurrentTopString = "0";
            if (mCurrentWidthString.equals(""))
                mCurrentWidthString = OUTPUT576_FULL_WIDTH;
            if (mCurrentHeightString.equals(""))
                mCurrentHeightString = OUTPUT576_FULL_HEIGHT;
        }          else if (curOutputmode.equals(outputmode_array[3])) {
            mCurrentLeftString = 	sw.getPropertyString(sel_576poutput_x,"0");
            mCurrentTopString = sw.getPropertyString(sel_576poutput_y,"0");
            mCurrentWidthString = sw.getPropertyString(sel_576poutput_width,OUTPUT576_FULL_WIDTH);
            mCurrentHeightString = sw.getPropertyString(sel_576poutput_height,OUTPUT576_FULL_HEIGHT);
            if (mCurrentLeftString.equals(""))
                mCurrentLeftString = "0";
            if (mCurrentTopString.equals(""))
                mCurrentTopString = "0";
            if (mCurrentWidthString.equals(""))
                mCurrentWidthString = OUTPUT576_FULL_WIDTH;
            if (mCurrentHeightString.equals(""))
                mCurrentHeightString = OUTPUT576_FULL_HEIGHT;
        } else if (curOutputmode.equals(outputmode_array[4])||curOutputmode.equals(outputmode_array[7])){
            mCurrentLeftString = 	sw.getPropertyString(sel_720poutput_x,"0");
            mCurrentTopString = sw.getPropertyString(sel_720poutput_y,"0");
            mCurrentWidthString = sw.getPropertyString(sel_720poutput_width,OUTPUT720_FULL_WIDTH);
            mCurrentHeightString = sw.getPropertyString(sel_720poutput_height,OUTPUT720_FULL_HEIGHT);
            if (mCurrentLeftString.equals(""))
            mCurrentLeftString = "0";
            if (mCurrentTopString.equals(""))
            mCurrentTopString = "0";
            if (mCurrentWidthString.equals(""))
            mCurrentWidthString = OUTPUT720_FULL_WIDTH;
            if (mCurrentHeightString.equals(""))
            mCurrentHeightString = OUTPUT720_FULL_HEIGHT;
        } else if (curOutputmode.equals(outputmode_array[5])||curOutputmode.equals(outputmode_array[8])) {
            mCurrentLeftString = 	sw.getPropertyString(sel_1080ioutput_x,"0");
            mCurrentTopString = sw.getPropertyString(sel_1080ioutput_y,"0");
            mCurrentWidthString = sw.getPropertyString(sel_1080ioutput_width,OUTPUT1080_FULL_WIDTH);
            mCurrentHeightString = sw.getPropertyString(sel_1080ioutput_height,OUTPUT1080_FULL_HEIGHT);
            if (mCurrentLeftString.equals(""))
                mCurrentLeftString = "0";
            if (mCurrentTopString.equals(""))
                mCurrentTopString = "0";
            if (mCurrentWidthString.equals(""))
                mCurrentWidthString = OUTPUT1080_FULL_WIDTH;
            if (mCurrentHeightString.equals(""))
            mCurrentHeightString = OUTPUT1080_FULL_HEIGHT;
        } else if (curOutputmode.equals(outputmode_array[6])||curOutputmode.equals(outputmode_array[9])){
 
            mCurrentLeftString = 	sw.getPropertyString(sel_1080poutput_x,"0");
            mCurrentTopString = sw.getPropertyString(sel_1080poutput_y,"0");
            mCurrentWidthString = sw.getPropertyString(sel_1080poutput_width,OUTPUT1080_FULL_WIDTH);
            mCurrentHeightString = sw.getPropertyString(sel_1080poutput_height,OUTPUT1080_FULL_HEIGHT);
            if (mCurrentLeftString.equals(""))
            mCurrentLeftString = "0";
            if (mCurrentTopString.equals(""))
            mCurrentTopString = "0";
            if (mCurrentWidthString.equals(""))
            mCurrentWidthString = OUTPUT1080_FULL_WIDTH;
            if (mCurrentHeightString.equals(""))
            mCurrentHeightString = OUTPUT1080_FULL_HEIGHT;
        } else {
            mCurrentLeftString = 	sw.getPropertyString(sel_720poutput_x,"0");
            mCurrentTopString = sw.getPropertyString(sel_720poutput_y,"0");
            mCurrentWidthString = sw.getPropertyString(sel_720poutput_width,OUTPUT720_FULL_WIDTH);
            mCurrentHeightString = sw.getPropertyString(sel_720poutput_height,OUTPUT720_FULL_HEIGHT);
            if (mCurrentLeftString.equals(""))
            mCurrentLeftString = "0";
            if (mCurrentTopString.equals(""))
            mCurrentTopString = "0";
            if (mCurrentWidthString.equals(""))
            mCurrentWidthString = OUTPUT720_FULL_WIDTH;
            if (mCurrentHeightString.equals(""))
            mCurrentHeightString = OUTPUT720_FULL_HEIGHT;
        }  
        Log.d(TAG,"===== positon: " +mCurrentLeftString+ "  "+ mCurrentTopString +"  "+ mCurrentWidthString+ "  " + mCurrentHeightString);
         mCurrentLeft = Integer.valueOf(mCurrentLeftString);
         mCurrentTop = Integer.valueOf(mCurrentTopString);
         mCurrentWidth = Integer.valueOf(mCurrentWidthString);
         mCurrentHeight= Integer.valueOf(mCurrentHeightString);
         mPreLeft = mCurrentLeft;
        mPreTop = mCurrentTop;
        mPreWidth = mCurrentWidth;
        mPreHeight = mCurrentHeight;
        mPreRight = mPreWidth + mPreLeft ;
        mPreBottom = mPreHeight + mPreTop ;
    }

     

    private void getCurrentPostion2(){
        String str = sw.readSysfs(PpscalerRectFile);
        //ppscaler rect:x:13,y:8,w:1253,h:705
        Log.d(TAG,"===== getCurrentPostion() , str :" + str);
        String[] postions = null;
        if(str!=null){
            postions = str.split(",");
        }
        
        if(postions!=null && postions.length >= 4){
            Log.d(TAG,"===== postions[0] : " + postions[0]);
            Log.d(TAG,"===== postions[1] : " + postions[1]);
            Log.d(TAG,"===== postions[2] : " + postions[2]);
            Log.d(TAG,"===== postions[3] : " + postions[3]);
            int indexStart = -1;
            int indexEnd = -1;
            String value = null;
            indexStart = postions[0].indexOf("x:");
            indexEnd = postions[0].trim().length();
            value = postions[0].substring(indexStart+2,indexEnd);
            Log.d(TAG,"===== value : " + value);
            mCurrentLeft = Integer.valueOf(value);
            
            indexStart = postions[1].indexOf("y:");
            indexEnd = postions[1].trim().length();
            value = postions[1].substring(indexStart+2,indexEnd);
             Log.d(TAG,"===== value : " + value);
            mCurrentTop = Integer.valueOf(value);

            indexStart = postions[2].indexOf("w:");
            indexEnd = postions[2].trim().length();
             value = postions[2].substring(indexStart+2,indexEnd);
              Log.d(TAG,"===== value : " + value);
            mCurrentWidth = Integer.valueOf(value);

            indexStart = postions[3].indexOf("h:");
            indexEnd = postions[3].trim().length();
            value = postions[3].substring(indexStart+2,indexEnd);
             Log.d(TAG,"===== value : " + value);
            mCurrentHeight = Integer.valueOf(value);

            Log.d(TAG,"current postion : " + mCurrentLeft+","+mCurrentTop+"," + mCurrentWidth+","+mCurrentHeight);
            
        }
        
    }

	public int getCurrentRate() {
		float offset = mPreTop / zoomStep ;
		float curVal = MAX_Height - offset ;
		return ((int)curVal);
	}

	public void savePostion() {
		savePosition(ls, ts, rs, bs);
		setScalingMinFreq(96000);
	}

	private String getCurrentOutputResolution() {
		String mode = sw.readSysfs(mCurrentResolution);
		if ("480cvbs".equalsIgnoreCase(mode)) {
			mode = "480i";
		} else if ("576cvbs".equalsIgnoreCase(mode)) {
			mode = "576i";
		}
		return mode;
	}

	private void writeFile(String file, String value) {
		sw.writeSysfs(file, value);
	}

	private final void setScalingMinFreq(int scalingMinFreq) {

		int minFreq = scalingMinFreq;
		String minFreqString = Integer.toString(minFreq);

		sw.writeSysfs(CPU0ScalingMinFreqPath, minFreqString);

	}
    

	private void initStep(String mode) {

        if(mode.contains("480")){
            zoomStepWidth = 1.50f ;
            zoomStep = 2.0f ;
        }else  if(mode.contains("576")){
            zoomStepWidth = 1.25f ;
            zoomStep = 2.0f ;
        }else  if(mode.contains("720")){
            zoomStepWidth = 1.78f ;
            zoomStep = 3.0f ;
        }else  if(mode.contains("1080")){
            zoomStepWidth = 1.78f ;
            zoomStep = 4.0f ;
        }else{
            zoomStepWidth = 1.78f ;
            zoomStep = 4.0f ;
        }
	}

	private void savePosition(String x, String y, String w, String h) {
		int index = 4; // 720p
		for (int i = 0; i < outputmode_array.length; i++) {
			if (curOutputmode.equalsIgnoreCase(outputmode_array[i])) {
				index = i;
			}
		}

		switch (index) {
    		case 0: // 480i
    			sw.setProperty(sel_480ioutput_x, x);
    			sw.setProperty(sel_480ioutput_y, y);
    			sw.setProperty(sel_480ioutput_width, w);
    			sw.setProperty(sel_480ioutput_height, h);
    			break;
    		case 1: // 480p
    			sw.setProperty(sel_480poutput_x, x);
    			sw.setProperty(sel_480poutput_x, y);
    			sw.setProperty(sel_480poutput_width, w);
    			sw.setProperty(sel_480poutput_height, h);
    			break;
    		case 2: // 576i
    			sw.setProperty(sel_576ioutput_x, x);
    			sw.setProperty(sel_576ioutput_y, y);
    			sw.setProperty(sel_576ioutput_width, w);
    			sw.setProperty(sel_576ioutput_height, h);
    			break;
    		case 3: // 576p
    			sw.setProperty(sel_576poutput_x, x);
    			sw.setProperty(sel_576poutput_y, y);
    			sw.setProperty(sel_576poutput_width, w);
    			sw.setProperty(sel_576poutput_height, h);
    			break;
    		case 4: // 720p
    		case 7:
    			sw.setProperty(sel_720poutput_x, x);
    			sw.setProperty(sel_720poutput_y, y);
    			sw.setProperty(sel_720poutput_width, w);
    			sw.setProperty(sel_720poutput_height, h);
    			break;
    		case 5: // 1080i
    		case 8:
    			sw.setProperty(sel_1080ioutput_x, x);
    			sw.setProperty(sel_1080ioutput_y, y);
    			sw.setProperty(sel_1080ioutput_width, w);
    			sw.setProperty(sel_1080ioutput_height, h);
    			break;
    		case 6: // 1080p
    		case 9:
    			sw.setProperty(sel_1080poutput_x, x);
    			sw.setProperty(sel_1080poutput_y, y);
    			sw.setProperty(sel_1080poutput_width, w);
    			sw.setProperty(sel_1080poutput_height, h);
    			break;
		}
	}

	public void zoomIn() {
        mCurrentRate = getCurrentRate();
        Log.d(TAG,"=====001 mCurrentRate : " + mCurrentRate);
        if( mCurrentRate >MAX_Height){
           mCurrentRate = MAX_Height ;  
           return ;
        }
		mPreLeft -= (zoomStep*zoomStepWidth);
		mPreTop -= zoomStep;
		mPreRight += (zoomStep*zoomStepWidth); 
        mPreBottom += zoomStep;
        Log.d(TAG,"====== zoomIn : " + mPreLeft+"  " + mPreTop +" "+mPreRight+"   "+ mPreBottom);
	    setPosition(mPreLeft, mPreTop,mPreRight, mPreBottom, 0);   
        
	}

	public void zoomOut() {
        mCurrentRate = getCurrentRate();
        Log.d(TAG,"=====002 mCurrentRate : " + mCurrentRate);
        if( mCurrentRate <MIN_Height){
           mCurrentRate = MIN_Height ;  
        if( mCurrentRate > MAX_Height){
           mCurrentRate = MAX_Height ; 
        }
           return ;
        }
       
        mPreLeft += (zoomStep*zoomStepWidth);
		mPreTop += zoomStep;
		mPreRight -= (zoomStep*zoomStepWidth); 
        mPreBottom -= zoomStep;
        setPosition(mPreLeft, mPreTop,mPreRight, mPreBottom, 0);   
        Log.d(TAG,"====== zoomOut : " + mPreLeft+"  " + mPreTop +" "+mPreRight+"   "+ mPreBottom);
	}

	private void setPosition(float l, float t, float r, float b, int mode) {
		String str = "";
        int left =  Integer.valueOf((int)l);
        int top =  Integer.valueOf((int)t);
        int right =  Integer.valueOf((int)r);
        int bottom =  Integer.valueOf((int)b);

        if(left < 0) {
            left = 0 ;
        }

        if(top < 0){
            top = 0 ;
        }
        if(curOutputmode.contains("480")){
            if(right > (720 + (int)(5*zoomStepWidth))){
                right = (720 + (int)(5*zoomStepWidth)) ;
            }
            if(bottom > (480+5)){
                bottom = (480+5) ;
            }
        }else  if(curOutputmode.contains("576")){
            if(right > (720+(int)(5*zoomStepWidth))){
                right = (720+(int)(5*zoomStepWidth)) ;
            }
            if(bottom > (576+5)){
                bottom = (576+5) ;
            }
        }else  if(curOutputmode.contains("720")){
            if(right > (1280+(int)(5*zoomStepWidth))){
                right = (1280+(int)(5*zoomStepWidth)) ;
            }
            if(bottom > (720+5)){
                bottom = (720+5) ;
            }
        }else  if(curOutputmode.contains("1080")){
            if(right > (1920+(int)(5*zoomStepWidth))){
                right = (1920+(int)(5*zoomStepWidth)) ;
            }
            if(bottom > (1080+5)){
                bottom = (1080+5) ;
            }
        }
        
		str = left + " " + top + " " + right + " " + bottom + " " + mode;
		writeFile(PpscalerRectFile, str);
        Log.d(TAG,"====== setPosition : " + left+"  " + top +" "+right+"   "+ bottom);
		ls = String.valueOf(left);
		ts = String.valueOf(top);
		rs = String.valueOf(right-left);
		bs = String.valueOf(bottom-top);
		
	}

    

}
