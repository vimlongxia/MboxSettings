package com.mbx.settingsmbox;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.app.SystemWriteManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetStateTracker;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OobeActivity extends Activity implements OnItemClickListener,
		OnClickListener, OnFocusChangeListener {
	private static final String TAG = "OobeActivity";
    private static final String PREFERENCE_BOX_SETTING = "preference_box_settings";
    private final static int UPDATE_AP_LIST = 100;
    private final static int UPDATE_OUTPUT_MODE_UI = 101;
    private static final String eth_device_sysfs = "/sys/class/ethernet/linkspeed";
    
    private final int MAX_Height = 100;
    private final int MIN_Height = 80;
    
    public final static int OOBE_WELCOME = 0;
	public final static int OOBE_LANGUAGE = 1;
	public final static int OOBE_NETWORK = 2;
	public final static int OOBE_SCREEN_ADJUST = 3;

    private int mCuttentView = 0;

	public static final int ETH_STATE_UNKNOWN = 0;
	public static final int ETH_STATE_DISABLED = 1;
	public static final int ETH_STATE_ENABLED = 2;

	private boolean isDisplayView = false;
	private int screen_rate = MIN_Height;
	ImageView img_num_hundred = null;
	ImageView img_num_ten = null;
	ImageView img_num_unit = null;
	ImageButton btn_position_zoom_out = null;
	ImageView img_progress_bg;
	ScreenPositionManager mScreenPositionManager = null;

	ImageButton btn_position_zoom_in = null;

	private final static int DALAY_TIME = 10000;
	private final Context mContext = this;
	View oobe_welcome;
	View oobe_language;
	View oobe_net;
	View oobe_screenadjust;

	LinearLayout top_welcome = null;
	LinearLayout top_language = null;
	LinearLayout top_network = null;
	LinearLayout top_screenadjust = null;

	Button button_welcome_skip = null;
	Button button_welcome_next = null;
	Button button_language_previous = null;
	Button button_language_next = null;
	Button button_net_previous = null;
	Button button_net_next = null;
	Button button_screen_previous = null;
	Button button_screen_finish = null;

    private int mCurrentLanguage = -1;   //  0: chinese simple , 1: english ,2: chinese taiwan

	//GridAdapter mlanguageAdapter = null;

    //============network 
	private AccessPointListAdapter oobe_mAccessPointListAdapter = null;
	private AccessPointRefreshHander oobe_mAccessPointRefreshHander = null;
	private Timer timer = null;
	private TimerTask task = null;
    private LinearLayout oobe_wifi_connected;
	private LinearLayout oobe_wifi_input_password;

	private LinearLayout oobe_wifi_not_connect;

	private TextView oobe_wifi_slect_tip;

	private EditText oobe_password_editview;

	private TextView oobe_wifi_listview_tip;
	private ListView oobe_mAcessPointListView = null;
	

	private TextView oobe_wifi_ssid_value;
	private TextView oobe_ip_address_value;

	private TextView oobe_select_wifi;

	private TextView oobe_select_ethernet;

	private TextView oobe_wifi_connected_tip;

	private LinearLayout oobe_root_eth_view;
    private LinearLayout oobe_net_root_view;
	private LinearLayout oobe_root_wifi_view;

	private EthernetManager oobe_mEthernetManager;
	private WifiManager oobe_mWifiManager;
    private TextView oobe_eth_IP_value = null;
	private TextView oobe_eth_connected_tip = null;
	private LinearLayout oobe_eth_ip_layout = null;
	
	NetworkStateReceiver mNetworkStateReceiver = null;
    private MyHandle mHander = null;
    //============end 
	

    private SystemWriteManager sw = null;

	RelativeLayout top_welcome_layout = null;

	RelativeLayout top_language_layout;
	RelativeLayout top_network_layout;
	RelativeLayout top_screen_layout;
	ImageView around_line;
    private ImageView select_cn = null;
    private ImageView select_english = null;
    private ImageView select_tw = null;
	private int Num[] = { R.drawable.ic_num0, R.drawable.ic_num1,
			R.drawable.ic_num2, R.drawable.ic_num3, R.drawable.ic_num4,
			R.drawable.ic_num5, R.drawable.ic_num6, R.drawable.ic_num7,
			R.drawable.ic_num8, R.drawable.ic_num9 };
	private int progressNum[] = { R.drawable.ic_per_81, R.drawable.ic_per_82,
			R.drawable.ic_per_83, R.drawable.ic_per_84, R.drawable.ic_per_85,
			R.drawable.ic_per_86, R.drawable.ic_per_87, R.drawable.ic_per_88,
			R.drawable.ic_per_89, R.drawable.ic_per_90, R.drawable.ic_per_91,
			R.drawable.ic_per_92, R.drawable.ic_per_93, R.drawable.ic_per_94,
			R.drawable.ic_per_95, R.drawable.ic_per_96, R.drawable.ic_per_97,
			R.drawable.ic_per_98, R.drawable.ic_per_99, R.drawable.ic_per_100 };

    private static boolean isGotoLanguageView = false ;
    private SharedPreferences sharepreference = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oobe);
        sharepreference = getSharedPreferences(PREFERENCE_BOX_SETTING,Context.MODE_PRIVATE);
        sw = (SystemWriteManager) mContext.getSystemService("system_write");
        
        oobe_mEthernetManager = (EthernetManager) mContext.getSystemService("ethernet");
        oobe_mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mHander = new MyHandle();
		initNetView();

		oobe_welcome = (LinearLayout) findViewById(R.id.oobe_welcome);

		oobe_language = (LinearLayout) findViewById(R.id.oobe_language);
		oobe_net = (LinearLayout) findViewById(R.id.oobe_net);
		oobe_screenadjust = (LinearLayout) findViewById(R.id.oobe_screenadjust);

		top_welcome = (LinearLayout) findViewById(R.id.top_welcome);
		top_welcome.setOnClickListener(this);
		// top_welcome.setOnFocusChangeListener(this);

		top_language = (LinearLayout) findViewById(R.id.top_language);
		top_language.setOnClickListener(this);
		// top_language.setOnFocusChangeListener(this);
		top_network = (LinearLayout) findViewById(R.id.top_network);
		top_network.setOnClickListener(this);
		// top_network.setOnFocusChangeListener(this);
		top_screenadjust = (LinearLayout) findViewById(R.id.top_screen);
		top_screenadjust.setOnClickListener(this);
		// top_screenadjust.setOnFocusChangeListener(this);

		button_welcome_skip = (Button) findViewById(R.id.button_welcome_skip);
		button_welcome_skip.setOnClickListener(this);
		button_welcome_next = (Button) findViewById(R.id.button_welcome_next);
		button_welcome_next.setOnClickListener(this);
		button_language_previous = (Button) findViewById(R.id.button_language_previous);
		button_language_previous.setOnClickListener(this);
		button_language_next = (Button) findViewById(R.id.button_language_next);
		button_language_next.setOnClickListener(this);
		button_net_previous = (Button) findViewById(R.id.button_net_previous);
		button_net_previous.setOnClickListener(this);
		button_net_next = (Button) findViewById(R.id.button_net_next);
		button_net_next.setOnClickListener(this);

		button_screen_previous = (Button) findViewById(R.id.button_screen_previous);
		button_screen_previous.setOnClickListener(this);

		button_screen_finish = (Button) findViewById(R.id.button_screen_finish);
		button_screen_finish.setOnClickListener(this);

		top_welcome_layout = (RelativeLayout) findViewById(R.id.top_welcome_layout);
		top_language_layout = (RelativeLayout) findViewById(R.id.top_language_layout);
		top_network_layout = (RelativeLayout) findViewById(R.id.top_network_layout);
		top_screen_layout = (RelativeLayout) findViewById(R.id.top_screen_layout);
		
		openWelcomView();
		setCurrentViewSelected(R.id.oobe_welcome);

		around_line = (ImageView) findViewById(R.id.oobe_screen_adjust_line);
        IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

		if (mNetworkStateReceiver == null)
			mNetworkStateReceiver = new NetworkStateReceiver();
		registerReceiver(mNetworkStateReceiver, filter);
        
	}

    
    
    private boolean isEthDeviceAdded(){
        String str = Utils.readSysFile(sw,eth_device_sysfs);
        if(str == null)
            return false ;
        int start = str.indexOf("speed :");
        String value = null;
        if(str.length() > start+7){
            value = str.substring(start+7).trim();
        }
        Log.d(TAG,"===== isEthDeviceAdded() , value : " + value);
        if("100".equals(value)){
            return true ;
        }else{
            return false ;
        }
    }

	private void initNetView() {
        oobe_eth_ip_layout  = (LinearLayout) findViewById(R.id.oobe_eth_ip_layout);
        oobe_wifi_connected = (LinearLayout) findViewById(R.id.oobe_wifi_connected);
		oobe_wifi_ssid_value = (TextView) findViewById(R.id.oobe_wifi_ssid_value);
		oobe_ip_address_value = (TextView) findViewById(R.id.oobe_ip_address_value);

        oobe_eth_connected_tip = (TextView) findViewById(R.id.oobe_eth_connected_notic);
        oobe_eth_IP_value = (TextView) findViewById(R.id.oobe_eth_IP_value);
        
		oobe_select_wifi = (TextView) findViewById(R.id.oobe_select_wifi);
		oobe_select_wifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setWifiCheckBoxSwitch();
			}
		});
		oobe_select_ethernet = (TextView) findViewById(R.id.oobe_select_ethernet);
        String productModel =  SystemInfoManager.getModelNumber();
        if(productModel.contains("g35")){
            TextView oobe_no_network = (TextView)findViewById(R.id.oobe_no_network);
            oobe_no_network.setText(mContext.getResources().getString(R.string.no_network_wifi_only));
            oobe_select_ethernet.setVisibility(View.INVISIBLE);
            Log.d(TAG,"===== it's g35 devices!");
        }
		oobe_select_ethernet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setEthCheckBoxSwitch();
			}
		});
		oobe_select_ethernet.setNextFocusUpId(R.id.settingsTopView_01);
		oobe_wifi_connected_tip = (TextView) findViewById(R.id.oobe_wifi_connected_tip);
		oobe_root_eth_view = (LinearLayout) findViewById(R.id.oobe_root_eth_view);
        oobe_net_root_view = (LinearLayout) findViewById(R.id.oobe_net_root_view);
		oobe_root_wifi_view = (LinearLayout) findViewById(R.id.oobe_root_wifi_view);
		oobe_root_wifi_view.setVisibility(View.GONE);
		oobe_root_eth_view.setVisibility(View.VISIBLE);
	
		oobe_wifi_connected = (LinearLayout) findViewById(R.id.oobe_wifi_connected);
		oobe_wifi_input_password = (LinearLayout) findViewById(R.id.oobe_wifi_input_password);
		oobe_wifi_not_connect = (LinearLayout) findViewById(R.id.oobe_wifi_not_connect);
		oobe_wifi_slect_tip = (TextView) findViewById(R.id.oobe_wifi_slect_tip);
		oobe_password_editview = (EditText) findViewById(R.id.oobe_password_input);
		oobe_password_editview.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		oobe_wifi_listview_tip = (TextView) findViewById(R.id.oobe_wifi_listview_tip);
		// password_editview.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		oobe_password_editview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				imm.showSoftInput(v, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		Button oobe_password_connect = (Button) findViewById(R.id.oobe_password_connect);
		oobe_password_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = oobe_password_editview.getText().toString();
				if (password != null) {
					showConnectingView();
					oobe_mAccessPointListAdapter.connect2AccessPoint(password);
				} else {
					Toast.makeText(mContext, mContext.getResources().getString(R.string.passwork_input_notice),3000).show();
				}
			}
		});

		oobe_mAcessPointListView = (ListView) findViewById(R.id.oobe_wifiListView);
        oobe_mAcessPointListView.setNextFocusRightId(R.id.button_net_next);
		oobe_mAcessPointListView.setOnItemClickListener(this);
		oobe_mAccessPointListAdapter = new AccessPointListAdapter(this);
		oobe_mAcessPointListView.setAdapter(oobe_mAccessPointListAdapter);             
	}

    private void wifiResume(){
        if(getEthCheckBoxState()){ 
            if(isEthDeviceAdded()) {
                oobe_mWifiManager.setWifiEnabled(false);
                oobe_mEthernetManager.setEthEnabled(true);
                updateNetWorkUI(2);
            }else{
                oobe_mEthernetManager.setEthEnabled(false); 
                if(getWifiCheckBoxState()){
                    oobe_mWifiManager.setWifiEnabled(true);
                    wifiScan(); 
                    updateNetWorkUI(1);
                }
            }
        }else{
            if(getWifiCheckBoxState()){
                oobe_mWifiManager.setWifiEnabled(true);
                wifiScan(); 
                updateNetWorkUI(1);
            }else{
                updateNetWorkUI(0);
            }                
        }               
        updateEthCheckBoxUI();
        upDateWifiCheckBoxUI();

    }

    
    private void setEthCheckBoxSwitch(){
        if(isEthDeviceAdded()){           
            if(getEthCheckBoxState()){            
                enableEthernetView(false);
            }else{
                enableEthernetView(true);
            }               
        }else{
            Toast.makeText(mContext, mContext.getResources().getString(R.string.ethernet_inplug_notice), 4000).show(); 
        }
        updateEthCheckBoxUI();   
        upDateWifiCheckBoxUI();
    }
    
    private void enableEthernetView(boolean able){                     
        if(able){ 
            updateNetWorkUI(2);
            oobe_mWifiManager.setWifiEnabled(false);
            if(isEthDeviceAdded()){
            oobe_eth_connected_tip.setText(R.string.ethernet_connectting);
            oobe_mEthernetManager.setEthEnabled(true);   
            }else{
                Toast.makeText(mContext, mContext.getResources().getString(R.string.ethernet_inplug_notice), 4000).show();
            } 
        }else{
            updateNetWorkUI(0);
            oobe_mEthernetManager.setEthEnabled(false);
        }       
    }
        
        private void enableWifiView(boolean able){
            if(able){ 
                if(isEthDeviceAdded()){
                    oobe_mEthernetManager.setEthEnabled(false);
                }  

                oobe_mWifiManager.setWifiEnabled(true);
                wifiScan(); 
                updateNetWorkUI(1);
            }else{          
                oobe_mWifiManager.setWifiEnabled(false);
                updateNetWorkUI(0);
            }      
        }
        
        private void updateEthCheckBoxUI(){
            if(getEthCheckBoxState()){
                oobe_select_ethernet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
            }else{
                oobe_select_ethernet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_uncheck, 0, 0, 0);
            }
        }
        
        private void setWifiCheckBoxSwitch(){
            if(getWifiCheckBoxState()){
                enableWifiView(false);             
            }else{
                enableWifiView(true);
            }
            upDateWifiCheckBoxUI();
            updateEthCheckBoxUI();
        }
        
        private void upDateWifiCheckBoxUI(){
            if(getWifiCheckBoxState()){
                oobe_select_wifi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
            }else{
                oobe_select_wifi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_uncheck, 0, 0, 0);
            }
        }
        private void updateNetWorkUI(int type){
            Log.d(TAG,"===== updateNetWorkUI() 001");
            if(type == 0){
                Log.d(TAG,"===== updateNetWorkUI() 002");
                oobe_net_root_view.setVisibility(View.VISIBLE);
                oobe_root_eth_view.setVisibility(View.GONE);
                oobe_root_wifi_view.setVisibility(View.GONE);
            }else if(type == 1){
                Log.d(TAG,"===== updateNetWorkUI() 003");
                oobe_net_root_view.setVisibility(View.GONE);
                oobe_root_eth_view.setVisibility(View.GONE);
                oobe_root_wifi_view.setVisibility(View.VISIBLE);
                
                boolean isWifiConnected = WifiUtils.isWifiConnected(mContext);
                if(isWifiConnected){
                    showWifiConnectedView();
                }else{
                    showWifiDisconnectedView();
                }            
            }else if(type == 2){
                Log.d(TAG,"===== updateNetWorkUI() 004");
                oobe_net_root_view.setVisibility(View.GONE);
                oobe_root_eth_view.setVisibility(View.VISIBLE);
                oobe_root_wifi_view.setVisibility(View.GONE);
                upDateEthernetInfo();
            }else{
                Log.d(TAG,"===== updateNetWorkUI() 005");
                oobe_net_root_view.setVisibility(View.VISIBLE);
                oobe_root_eth_view.setVisibility(View.GONE);
                oobe_root_wifi_view.setVisibility(View.GONE);
            }
    
        }
        private void wifiScan(){
            oobe_mAccessPointListAdapter.startScanApcessPoint();
    
            Message msg0 = mHander.obtainMessage();
            msg0.what = UPDATE_AP_LIST ;
            mHander.sendMessage(msg0);
    
            Thread updateTask = new Thread(new Runnable() {
    
                @Override
                public void run() {
    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    while (oobe_mAccessPointListAdapter.getCount() <= 0
                            && mCuttentView == OOBE_NETWORK) {
                        Message msg = mHander.obtainMessage();
                        msg.what = UPDATE_AP_LIST ;
                        mHander.sendMessage(msg);
                        Log.d(TAG, "send message to refresh ap list");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (oobe_mAccessPointListAdapter.getCount() > 0) {
                        Log.d(TAG, "get new  ap list ok ");
                        Message msg = mHander.obtainMessage();
                        msg.what = UPDATE_AP_LIST ;
                        mHander.sendMessage(msg);
                    }
    
                }
            });
            updateTask.start();
        
        }
        private void showWifiConnectedView() {
    
            oobe_wifi_listview_tip.setVisibility(View.GONE);
            oobe_mAcessPointListView.setVisibility(View.VISIBLE);
    
            oobe_wifi_input_password.setVisibility(View.GONE);
            oobe_wifi_not_connect.setVisibility(View.GONE);
            oobe_wifi_connected.setVisibility(View.VISIBLE);
            oobe_mAccessPointListAdapter.updateAccesspointList();
    
            DhcpInfo mDhcpInfo = oobe_mWifiManager.getDhcpInfo();
            WifiInfo mWifiinfo = oobe_mWifiManager.getConnectionInfo();
    
            if (mWifiinfo != null) {
                oobe_wifi_ssid_value.setVisibility(View.VISIBLE);
                String wifi_name = mWifiinfo.getSSID().substring(1,mWifiinfo.getSSID().length() - 1);
                oobe_wifi_ssid_value.setText(wifi_name);
                oobe_ip_address_value.setText(int2ip(mWifiinfo.getIpAddress()));
                //oobe_mAccessPointListAdapter.setCurrentConnectedItemBySsid(mWifiinfo.getSSID());
                oobe_mAccessPointListAdapter.setCurrentConnectItemSSID(mWifiinfo.getSSID());
            }
    
        }
        

	void openWelcomView() {
        mCuttentView = OOBE_WELCOME;
		oobe_net.setVisibility(View.GONE);
		oobe_screenadjust.setVisibility(View.GONE);
		oobe_language.setVisibility(View.GONE);
		oobe_welcome.setVisibility(View.VISIBLE);
		button_welcome_next.setFocusableInTouchMode(true);
		button_welcome_next.requestFocus();
		isDisplayView = false;

	}

	void openLanguageView() {
        mCuttentView = OOBE_LANGUAGE;
		oobe_welcome.setVisibility(View.GONE);
		oobe_net.setVisibility(View.GONE);
		oobe_screenadjust.setVisibility(View.GONE);
		oobe_language.setVisibility(View.VISIBLE);

		//GridView gridview = (GridView) findViewById(R.id.language_gridview);
		//gridview.setOnItemClickListener(this);
		//gridview.setNextFocusDownId(R.id.button_language_next);
		//mlanguageAdapter = new GridAdapter(this);
		//gridview.setAdapter(mlanguageAdapter);
		//gridview.setSelector(R.drawable.select_language);

		//gridview.requestFocus();
		//gridview.setSelection(0);

        String lan = Locale.getDefault().getLanguage();
        String coun = Locale.getDefault().getCountry();
        
        Log.d(TAG,"===== lan : " + lan + ", Country : " + coun);
        select_cn = (ImageView)findViewById(R.id.select_cn);
        select_cn.setNextFocusDownId(R.id.button_language_next);
        select_cn.setOnFocusChangeListener(this);
        select_cn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    mCurrentLanguage = 0;
			    isGotoLanguageView = true ;
                selectLanguageByID(R.id.select_cn,false);
                updateLanguage(Locale.SIMPLIFIED_CHINESE);  
			}
		});
        select_english = (ImageView)findViewById(R.id.select_english);
        select_english.setOnFocusChangeListener(this);
        select_english.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    mCurrentLanguage = 1;
			    isGotoLanguageView = true ;
                selectLanguageByID(R.id.select_english,false);
                updateLanguage(Locale.US);
                
			}
		});
        select_tw = (ImageView)findViewById(R.id.select_tw);
        select_tw.setOnFocusChangeListener(this);
        select_tw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    mCurrentLanguage = 2;
			    isGotoLanguageView = true ;
                selectLanguageByID(R.id.select_tw,false);
                updateLanguage(Locale.TAIWAN); 
			}
		});

        if("zh".equals(lan)&& "CN".equals(coun)){
            mCurrentLanguage = 0;
            selectLanguageByID(R.id.select_cn,false);
        }else if("zh".equals(lan)&& "TW".equals(coun)){
            mCurrentLanguage = 2;
            selectLanguageByID(R.id.select_tw,false);
        }else if("en".equals(lan)&&"US".equals(coun)){
            mCurrentLanguage = 1;
            selectLanguageByID(R.id.select_english,false);
        }
        select_cn.requestFocus();
		isDisplayView = false;
	}

    private void selectLanguageByID(int id , boolean hasFocus){
        Log.d(TAG,"===== selectLanguageByID() , hasFocus :" + hasFocus + " , mCurrentLanguage :" +mCurrentLanguage );
        if(id == R.id.select_cn){
            Log.d(TAG,"===== select_cn,hasFocus: " + hasFocus );
            if(mCurrentLanguage == 0){
                if(hasFocus){
                    select_cn.setBackgroundResource(R.drawable.language_selected);
                }else{
                    select_cn.setBackgroundResource(R.drawable.language_current);
                }
            }else if(mCurrentLanguage == 1){
                if(hasFocus){
                    select_cn.setBackgroundResource(R.drawable.language_seclect_focused);
                }else{
                    select_cn.setBackgroundResource(Color.TRANSPARENT);
                }
            }else if(mCurrentLanguage == 2){
                if(hasFocus){
                    select_cn.setBackgroundResource(R.drawable.language_seclect_focused);
                }else{
                    select_cn.setBackgroundResource(Color.TRANSPARENT);
                }
            }
            
        }else if(id == R.id.select_english){
            Log.d(TAG,"===== select_english: " + hasFocus );
            if(mCurrentLanguage == 0){
                if(hasFocus){
                    select_english.setBackgroundResource(R.drawable.language_seclect_focused);
                }else{
                     select_english.setBackgroundResource(Color.TRANSPARENT);
                }
            }else if(mCurrentLanguage == 1){
                if(hasFocus){
                    select_english.setBackgroundResource(R.drawable.language_selected);
                }else{
                    select_english.setBackgroundResource(R.drawable.language_current);
                }
            }else if(mCurrentLanguage == 2){
                if(hasFocus){
                    select_english.setBackgroundResource(R.drawable.language_seclect_focused);
                }else{
                    select_english.setBackgroundResource(Color.TRANSPARENT);
                }
            }
                      
        }else if(id == R.id.select_tw){
            Log.d(TAG,"===== select_tw : " + hasFocus );
            if(mCurrentLanguage == 0){
                if(hasFocus){
                    select_tw.setBackgroundResource(R.drawable.language_seclect_focused);
                }else{
                    select_tw.setBackgroundResource(Color.TRANSPARENT);
                }
            }else if(mCurrentLanguage == 1){
                if(hasFocus){
                    select_tw.setBackgroundResource(R.drawable.language_seclect_focused);
                }else{
                    select_tw.setBackgroundResource(Color.TRANSPARENT);
                }
            }else if(mCurrentLanguage == 2){
                if(hasFocus){
                    select_tw.setBackgroundResource(R.drawable.language_selected);
                }else{
                    select_tw.setBackgroundResource(R.drawable.language_current);
                }
            }
           
        }
    }

    private void updateLanguage(Locale locale) {
		try {
			Object objIActMag;
			Class clzIActMag = Class.forName("android.app.IActivityManager");
			Class clzActMagNative = Class.forName("android.app.ActivityManagerNative");
			Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
			objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
			Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");
			Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);
			config.locale = locale;
			Class[] clzParams = { Configuration.class };
			Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod("updateConfiguration", clzParams);
			mtdIActMag$updateConfiguration.invoke(objIActMag, config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void openScreenAdjustView() {
        mCuttentView = OOBE_SCREEN_ADJUST ;
		oobe_net.setVisibility(View.GONE);

		oobe_language.setVisibility(View.GONE);
		oobe_welcome.setVisibility(View.GONE);
		oobe_screenadjust.setVisibility(View.VISIBLE);

		button_screen_finish.requestFocus();

		openScreenAdjustLayout();
		isDisplayView = true;
	}

	void openNetView() {
        mCuttentView = OOBE_NETWORK;
		isDisplayView = false;
		oobe_welcome.setVisibility(View.GONE);
		oobe_language.setVisibility(View.GONE);
		oobe_screenadjust.setVisibility(View.GONE);
		oobe_net.setVisibility(View.VISIBLE);
		button_net_next.requestFocus();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.oobe, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Log.d(TAG, "onItemClick(), position=" + position);
		if (parent instanceof GridView) {
            //isGotoLanguageView = true;
			//mlanguageAdapter.updateLanguageLocal(position);
		} else if (parent instanceof ListView) {
			onClickAccessPoint(position);
		}

	}

	private void openScreenAdjustLayout() {

		img_num_hundred = (ImageView) findViewById(R.id.oobe_img_num_hundred);
		img_num_ten = (ImageView) findViewById(R.id.oobe_img_num_ten);
		img_num_unit = (ImageView) findViewById(R.id.oobe_img_num_unit);
		img_progress_bg = (ImageView) findViewById(R.id.oobe_img_progress_bg);

		btn_position_zoom_out = (ImageButton) findViewById(R.id.oobe_btn_position_zoom_out);
        btn_position_zoom_out.setOnClickListener(this);
		btn_position_zoom_in = (ImageButton) findViewById(R.id.oobe_btn_position_zoom_in);
        btn_position_zoom_in.setOnClickListener(this);
		mScreenPositionManager = new ScreenPositionManager(this);
		mScreenPositionManager.initPostion();
		screen_rate = mScreenPositionManager.getCurrentRate();

		showProgressUI(0);

		around_line.setVisibility(View.VISIBLE);

	}

	private void showProgressUI(int step) {
        screen_rate = mScreenPositionManager.getCurrentRate();
        screen_rate = screen_rate + step;
        if(screen_rate >MAX_Height){
            screen_rate = MAX_Height;
        }
        if(screen_rate <MIN_Height){
            screen_rate = MIN_Height ;
        }

		if (screen_rate <= MAX_Height && screen_rate >=100) {
			int hundred = Num[(int) screen_rate / 100];
			img_num_hundred.setVisibility(View.VISIBLE);
			img_num_hundred.setBackgroundResource(hundred);
            int ten = Num[(screen_rate -100)/10] ;
			img_num_ten.setBackgroundResource(ten);
            int unit = Num[(screen_rate -100)%10];
			img_num_unit.setBackgroundResource(unit);
			if (screen_rate - MIN_Height>= 0 && screen_rate - MIN_Height <= 19)
				img_progress_bg.setBackgroundResource(progressNum[screen_rate - MIN_Height]);
		} else if (screen_rate >= 10 && screen_rate <= 99) {
			img_num_hundred.setVisibility(View.GONE);
			int ten = Num[(int) (screen_rate / 10)];
			int unit = Num[(int) (screen_rate % 10)];
			img_num_ten.setBackgroundResource(ten);
			img_num_unit.setBackgroundResource(unit);
			if (screen_rate - MIN_Height >= 0 && screen_rate - MIN_Height <= 19)
				img_progress_bg.setBackgroundResource(progressNum[screen_rate - MIN_Height]);
		} else if (screen_rate >= 0 && screen_rate <= 9) {
			int unit = Num[screen_rate];
			img_num_unit.setBackgroundResource(unit);
		}

	}

	private void closeScreenAdjustLayout() {
        /*
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"===== save position now");
                    mScreenPositionManager.savePostion();
                }
            });
            t.start();
            */
        mScreenPositionManager.savePostion();
        around_line.setVisibility(View.GONE);
	}

	private void onClickAccessPoint(int index) {

		oobe_mAccessPointListAdapter.setCurrentSelectItem(index);
		int securityType = oobe_mAccessPointListAdapter
				.getCurrentAccessPointSecurityType(index);
		Log.d(TAG, "===== securityType :  " + securityType);

		if (securityType == 0 || securityType == 4) {

			showConnectingView();

			oobe_mAccessPointListAdapter.connect2OpenAccessPoint();
		} else {
			showPasswordView();
		}

	}

	private void showPasswordView() {
		oobe_wifi_connected.setVisibility(View.GONE);
		oobe_wifi_not_connect.setVisibility(View.GONE);
		oobe_wifi_input_password.setVisibility(View.VISIBLE);
		oobe_password_editview.requestFocus();

	}

	private void showConnectingView() {
		oobe_wifi_connected.setVisibility(View.GONE);
		oobe_wifi_input_password.setVisibility(View.GONE);
		oobe_wifi_not_connect.setVisibility(View.VISIBLE);
		oobe_wifi_slect_tip.setText(R.string.wifi_connectting);
		button_net_next.requestFocus();
	}


    private void upDateEthernetInfo() {
		Log.d(TAG, "===== update ethernet info ");
        boolean isEthConnected = WifiUtils.isEthConnected(mContext);
		if (isEthConnected) {
            if(oobe_eth_ip_layout != null && oobe_wifi_connected != null){
                oobe_eth_ip_layout.setVisibility(View.VISIBLE);
                oobe_wifi_connected.setVisibility(View.VISIBLE);
            }
			   
			DhcpInfo mDhcpInfo = oobe_mEthernetManager.getDhcpInfo();
			if (mDhcpInfo != null) {
				int ip = mDhcpInfo.ipAddress;
                if(oobe_eth_connected_tip != null)
				    oobe_eth_connected_tip.setText(R.string.eth_connectd);
				Log.d(TAG, "====== ip  : " + ip + "   int2ip(ip) : "+ int2ip(ip));
				if(oobe_eth_IP_value != null)
				    oobe_eth_IP_value.setText(int2ip(ip));
				else {
					Log.d(TAG,"=====  eth_IP_value is null !!!");
				}	
			}

		} else {
		    if(oobe_eth_connected_tip != null && oobe_eth_ip_layout != null){
			    oobe_eth_connected_tip.setText(R.string.ethernet_error);
			    oobe_eth_ip_layout.setVisibility(View.GONE);
            }
		}

	}


	private void showWifiDisconnectedView() {
		oobe_wifi_connected.setVisibility(View.GONE);
		oobe_wifi_input_password.setVisibility(View.GONE);
		oobe_wifi_not_connect.setVisibility(View.VISIBLE);
		oobe_mAccessPointListAdapter.updateAccesspointList();
		oobe_wifi_slect_tip.setText(R.string.wifi_ap_select);
	}

	public String int2ip(long ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		return null;
	}

	@Override
	public void onResume() {
	    Log.d(TAG,"===== isGotoLanguageView : " + isGotoLanguageView);
        wifiResume();
	    if(isGotoLanguageView){
            openLanguageView();
            isGotoLanguageView = false;
        }
		super.onPause();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onPause();
		if (mNetworkStateReceiver != null) {
			unregisterReceiver(mNetworkStateReceiver);
			mNetworkStateReceiver = null;
		}
	}

	class AccessPointRefreshHander extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (oobe_mAccessPointListAdapter.getCount() <= 0) {
				oobe_mAcessPointListView.setVisibility(View.GONE);
				oobe_wifi_listview_tip.setVisibility(View.VISIBLE);
			} else {
				oobe_wifi_listview_tip.setVisibility(View.GONE);
				oobe_mAcessPointListView.setVisibility(View.VISIBLE);
			}

			oobe_mAccessPointListAdapter.updateAccesspointList();

		}
	}

	private void startTimer() {
		if (task == null) {
			task = new TimerTask() {

				@Override
				public void run() {
					Message msg = oobe_mAccessPointRefreshHander.obtainMessage();
					oobe_mAccessPointRefreshHander.sendMessage(msg);
				}
			};

		}
		if (timer == null) {
			timer = new Timer();
			timer.schedule(task, 0, DALAY_TIME);

		}

	}

	private void stopTimer() {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (task != null) {
			task.cancel();
			task = null;
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
        Log.d(TAG,"===== onClick(), id = " + id);

		if (v instanceof Button) {
			if (id == R.id.button_welcome_skip) {
				OobeActivity.this.finish();
			} else if (id == R.id.button_welcome_next) {
				setCurrentViewSelected(R.id.top_language);
				openLanguageView();
			} else if (id == R.id.button_language_previous) {
				setCurrentViewSelected(R.id.top_welcome);
				openWelcomView();
			} else if (id == R.id.button_language_next) {
				setCurrentViewSelected(R.id.top_network);
				openNetView();
			} else if (id == R.id.button_net_previous) {
				setCurrentViewSelected(R.id.top_language);
				openLanguageView();
			} else if (id == R.id.button_net_next) {
				setCurrentViewSelected(R.id.top_screen);
				openScreenAdjustView();
			} else if (id == R.id.button_screen_previous) {
				setCurrentViewSelected(R.id.top_network);
				around_line.setVisibility(View.GONE);
				openNetView();
			} else if (id == R.id.button_screen_finish) {
				//setSharedPrefrences("first_start_up", false);
				//setOobeStartProp("false");
                Log.d(TAG,"===== disable oobe setting !!!");
                PackageManager pm = getPackageManager();
                ComponentName name = new ComponentName(this, OobeActivity.class);
                pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                closeScreenAdjustLayout();
                finish();
			}
		}

        if (v instanceof ImageButton) {
            if (id == R.id.btn_position_zoom_in) {
                if (screen_rate > MIN_Height) {
                    showProgressUI(-1);
                    mScreenPositionManager.zoomOut();
                }               
            }else if(id == R.id.btn_position_zoom_out){
                if(screen_rate < MAX_Height){
                    showProgressUI(1);
                    mScreenPositionManager.zoomIn();
                }
            }
        }
	}

	class NetworkStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,Context.MODE_PRIVATE).edit();
			Log.e(TAG, "action : " + action);

            Log.e(TAG, "===== onReceive() 001");
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {  
                if(mCuttentView == OOBE_NETWORK){
                        boolean isWifiConnected = WifiUtils.isWifiConnected(mContext);
                        boolean isEthConnected = WifiUtils.isEthConnected(mContext);
                        Log.e(TAG, "===== onReceive() 002");
                    if(isEthConnected){
                        Log.e(TAG, "===== onReceive() 003");
                        updateNetWorkUI(2);
                        updateEthCheckBoxUI();   
                        upDateWifiCheckBoxUI();
                    }else if(oobe_mWifiManager.isWifiEnabled()){
                        updateNetWorkUI(1);
                        Log.e(TAG, "===== onReceive() 004");
                    }
                }
                Log.e(TAG, "===== onReceive() 005");
            }
		}
	}

	private void setCurrentViewSelected(int id) {
		if (id == R.id.top_welcome) {
			top_welcome_layout.setBackgroundResource(Color.TRANSPARENT);
			top_welcome.setBackgroundResource(R.drawable.image_top_welcome_focus);
			top_language_layout.setBackgroundResource(R.drawable.image_top_language);
			top_language.setBackgroundResource(Color.TRANSPARENT);
			top_network_layout.setBackgroundResource(R.drawable.image_top_network);
			top_network.setBackgroundResource(Color.TRANSPARENT);
			top_screen_layout.setBackgroundResource(R.drawable.image_top_screen);
			top_screenadjust.setBackgroundResource(Color.TRANSPARENT);
		} else if (id == R.id.top_language) {
			top_welcome_layout.setBackgroundResource(R.drawable.image_top_welcome);
			top_welcome.setBackgroundResource(Color.TRANSPARENT);
			top_language_layout.setBackgroundResource(Color.TRANSPARENT);
			top_language.setBackgroundResource(R.drawable.image_top_language_focus);
			top_network_layout.setBackgroundResource(R.drawable.image_top_network);
			top_network.setBackgroundResource(Color.TRANSPARENT);
			top_screen_layout.setBackgroundResource(R.drawable.image_top_screen);
			top_screenadjust.setBackgroundResource(Color.TRANSPARENT);
		} else if (id == R.id.top_network) {
			top_welcome_layout.setBackgroundResource(R.drawable.image_top_welcome);
			top_welcome.setBackgroundResource(Color.TRANSPARENT);
			top_language_layout.setBackgroundResource(R.drawable.image_top_language);
			top_language.setBackgroundResource(Color.TRANSPARENT);
			top_network_layout.setBackgroundResource(Color.TRANSPARENT);
			top_network.setBackgroundResource(R.drawable.image_top_network_focus);
			top_screen_layout.setBackgroundResource(R.drawable.image_top_screen);
			top_screenadjust.setBackgroundResource(Color.TRANSPARENT);
		} else if (id == R.id.top_screen) {
			top_welcome_layout.setBackgroundResource(R.drawable.image_top_welcome);
			top_welcome.setBackgroundResource(Color.TRANSPARENT);
			top_language_layout.setBackgroundResource(R.drawable.image_top_language);
			top_language.setBackgroundResource(Color.TRANSPARENT);
			top_network_layout.setBackgroundResource(R.drawable.image_top_network);
			top_network.setBackgroundResource(Color.TRANSPARENT);
			top_screen_layout.setBackgroundResource(Color.TRANSPARENT);
			top_screenadjust.setBackgroundResource(R.drawable.image_top_screen_focus);
		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		if (id == R.id.top_welcome) {
			if (hasFocus) {
				top_welcome_layout.setBackgroundResource(Color.TRANSPARENT);
				top_welcome.setBackgroundResource(R.drawable.image_top_welcome_focus);
			} else {
				top_welcome_layout.setBackgroundResource(R.drawable.image_top_welcome);
				top_welcome.setBackgroundResource(Color.TRANSPARENT);
			}

		} else if (id == R.id.top_language) {
			if (hasFocus) {
				top_language_layout.setBackgroundResource(Color.TRANSPARENT);
				top_language.setBackgroundResource(R.drawable.image_top_language_focus);
			} else {
				top_language_layout.setBackgroundResource(R.drawable.image_top_language);
				top_language.setBackgroundResource(Color.TRANSPARENT);
			}

		} else if (id == R.id.top_network) {
			if (hasFocus) {
				top_network_layout.setBackgroundResource(Color.TRANSPARENT);
				top_network.setBackgroundResource(R.drawable.image_top_network_focus);
			} else {
				top_network_layout.setBackgroundResource(R.drawable.image_top_network);
				top_network.setBackgroundResource(Color.TRANSPARENT);
			}

		} else if (id == R.id.top_screen) {
			if (hasFocus) {
				top_screen_layout.setBackgroundResource(Color.TRANSPARENT);
				top_screenadjust.setBackgroundResource(R.drawable.image_top_screen_focus);
			} else {
				top_screen_layout.setBackgroundResource(R.drawable.image_top_screen);
				top_screenadjust.setBackgroundResource(Color.TRANSPARENT);
			}

		}else if (id == R.id.select_cn){
            selectLanguageByID(id,hasFocus);
        }else  if (id == R.id.select_english){
            selectLanguageByID(id,hasFocus);
        }else if (id == R.id.select_tw){
            selectLanguageByID(id,hasFocus);
        }

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "=====  onKeyDown() , keyCode = " + keyCode);
		if (isDisplayView) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				btn_position_zoom_in.setBackgroundResource(R.drawable.minus_unfocus);
				btn_position_zoom_out.setBackgroundResource(R.drawable.plus_focus);
				if(screen_rate < MAX_Height){
				    showProgressUI(1);
			        mScreenPositionManager.zoomIn();
                }
				return true;

			} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

				if (screen_rate > MIN_Height) {
                    showProgressUI(-1);
					mScreenPositionManager.zoomOut();
				}       
				btn_position_zoom_in.setBackgroundResource(R.drawable.minus_focus);
				btn_position_zoom_out.setBackgroundResource(R.drawable.plus_unfocus);
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_BACK
					|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				//Log.d(TAG, "===== closeScreenAdjustLayout() now !!!");
				//closeScreenAdjustLayout();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	void setSharedPrefrences(String name, boolean value) {
		Editor editor = mContext.getSharedPreferences(name,Context.MODE_PRIVATE).edit();
		editor.putBoolean("first_start_up", value);
		editor.commit();

	}

	void setOobeStartProp(String value) {
		SystemWriteManager sw = (SystemWriteManager) mContext.getSystemService("system_write");
		sw.setProperty("persist.sys.oobe.start", value);
	}

   class MyHandle extends Handler {
		@Override
		public void handleMessage(Message msg) {
            switch(msg.what){
                case UPDATE_AP_LIST :
                        oobe_mAccessPointListAdapter.updateAccesspointList();
                        if (oobe_mAccessPointListAdapter.getCount() <= 0) {
                            oobe_mAcessPointListView.setVisibility(View.GONE);
                            oobe_wifi_listview_tip.setVisibility(View.VISIBLE);
                        } else {
                            oobe_wifi_listview_tip.setVisibility(View.GONE);
                            oobe_mAcessPointListView.setVisibility(View.VISIBLE);
                        }
                    break;
            }
		}
	}

       private boolean getWifiCheckBoxState(){
        int state = Settings.Global.getInt(getContentResolver(), Settings.Global.WIFI_ON, 0);
        //int state = mWifiManager.getWifiState();
        Log.d(TAG,"===== getWifiCheckBoxState() , state : " + state);
        if(state == 1){
             Log.d(TAG,"===== getWifiCheckBoxState() , true " );
            return true ;
        }else{
            Log.d(TAG,"===== getWifiCheckBoxState() , false " );
            return false;
        }
        
    }

    private boolean getEthCheckBoxState(){
        //int state = Settings.Secure.getInt(getContentResolver(), Settings.Secure.ETH_ON, 0);
        int state = oobe_mEthernetManager.getEthState();
        Log.d(TAG,"===== getEthCheckBoxState() , state : " + state);
        if(state == EthernetManager.ETH_STATE_ENABLED){
            return true;
        }
        else{
            return false;
        }
    }
}
