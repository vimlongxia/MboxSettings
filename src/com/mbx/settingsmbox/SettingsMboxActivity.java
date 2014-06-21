package com.mbx.settingsmbox;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.SystemWriteManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManagerPolicy;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.UserHandle ;


public class SettingsMboxActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private String TAG = "SettingsMboxActivity";

    private final int MAX_Height = 100;
    private final int MIN_Height = 80;

	private static final String PREFERENCE_BOX_SETTING = "preference_box_settings";
	private static final String CEC_CONFIG = "ubootenv.var.cecconfig";
	private static final String writeCecConfig = "/sys/class/amhdmitx/amhdmitx0/cec_config";
    private static final String eth_device_sysfs = "/sys/class/ethernet/linkspeed";
	private final Context mContext = this;
	private SystemWriteManager sw = null;
    private final static int UPDATE_AP_LIST = 100;
    private final static int UPDATE_OUTPUT_MODE_UI = 101;

	public final static int VIEW_NETWORK = 0;
	public final static int VIEW_DISPLAY = 1;
	public final static int VIEW_MORE = 2;
	public final static int VIEW_OTHER = 3;
	public final static int VIEW_SCREEN_ADJUST = 4;

	public static int mCurrentContentNum = 0;
	private static final int ETH_STATE_UNKNOWN = 0;
	private static final int ETH_STATE_DISABLED = 1;
	private static final int ETH_STATE_ENABLED = 2;


	private int screen_rate = MIN_Height;

	private LinearLayout wifi_connected;
	private LinearLayout wifi_input_password;

	private LinearLayout wifi_not_connect;

	private TextView wifi_slect_tip;

	private EditText password_editview;

	private TextView wifi_listview_tip;
	private ListView mAcessPointListView = null;

	private AccessPointListAdapter mAccessPointListAdapter = null;
	private MyHandle mHander = null;

	private TextView wifi_ssid_value;
	private TextView ip_address_value;
	
	private TextView select_wifi;

	private TextView select_ethernet;

	private TextView wifi_connected_tip;

	private LinearLayout root_eth_view;
    private LinearLayout net_root_view;
	private LinearLayout root_wifi_view;

	private EthernetManager mEthernetManager;

	private WifiManager mWifiManager;
    private TextView eth_IP_value = null;

	private TextView eth_connected_tip = null;
	private LinearLayout eth_ip_layout = null;
    
	private BroadcastReceiver myReceiver = null;

	private LinearLayout setting_network_layout;
	

	SettingsTopView settingsTopView_01;
	SettingsTopView settingsTopView_02;
	SettingsTopView settingsTopView_03;
	SettingsTopView settingsTopView_04;

	private LinearLayout settingsContentLayout_01;
	private LinearLayout settingsContentLayout_02;
	private LinearLayout settingsContentLayout_03;
	private LinearLayout settingsContentLayout_04;

	private LinearLayout screen_self_set = null;
    private LinearLayout cvbs_screen_self_set = null;
    private LinearLayout secreen_auto = null;

	private LinearLayout settings_content_postion = null;
	private LinearLayout button_scrren_adjust;

	private LinearLayout wifi_direct = null;

	private ImageButton btn_position_zoom_out = null;
	private ImageButton btn_position_zoom_in = null;
	private ImageView img_num_hundred = null;
	private ImageView img_num_ten = null;
	private ImageView img_num_unit = null;

	private ImageView img_progress_bg;
	private ScreenPositionManager mScreenPositionManager = null;

	private TextView screen_time_01;
	private TextView screen_time_02;
	private TextView screen_time_03;
	private TextView screen_time_04;
	private TextView[] mScreenKeepTimes;

	private LinearLayout screen_keep = null;
	private static int mCurrentScreenKeepIndex = 0;

	public static int Num[] = { R.drawable.ic_num0, R.drawable.ic_num1,
			R.drawable.ic_num2, R.drawable.ic_num3, R.drawable.ic_num4,
			R.drawable.ic_num5, R.drawable.ic_num6, R.drawable.ic_num7,
			R.drawable.ic_num8, R.drawable.ic_num9 };
	public static int progressNum[] = { R.drawable.ic_per_81,
			R.drawable.ic_per_82, R.drawable.ic_per_83, R.drawable.ic_per_84,
			R.drawable.ic_per_85, R.drawable.ic_per_86, R.drawable.ic_per_87,
			R.drawable.ic_per_88, R.drawable.ic_per_89, R.drawable.ic_per_90,
			R.drawable.ic_per_91, R.drawable.ic_per_92, R.drawable.ic_per_93,
			R.drawable.ic_per_94, R.drawable.ic_per_95, R.drawable.ic_per_96,
			R.drawable.ic_per_97, R.drawable.ic_per_98, R.drawable.ic_per_99,
			R.drawable.ic_per_100 };

	private PopupWindow popupWindow = null;
	private TextView current_mode_value = null;
    private TextView cvbs_current_mode_value = null;
	private TextView self_select_mode = null;
    private TextView cvbs_self_select_mode = null;
	private TextView auto_set_screen = null;
	private SharedPreferences sharepreference = null;

	private TextView hide_status_bar = null;

	private TextView requestScreen = null;
	private TextView screen_land = null;

	private TextView miracast;
	private TextView remoteControl;

	private RelativeLayout cec_main;
	private RelativeLayout cec_play;
	private RelativeLayout cec_power;
	private RelativeLayout cec_language;

	private ImageView imageview_cec_main;
	private ImageView imageview_cec_play;
	private ImageView imageview_cec_power;
	private ImageView imageview_cec_language;

	private LinearLayout city_select = null;
	private TextView province_view;
	private TextView city_view;
	private int mCrrentLocationfocus = 0;

    private String string_pcm =  null;
    private String string_spdif =  null;
	private String string_hdmi =  null;

	private WifiP2pDevice mThisDevice = null;
	OutPutModeManager mOutPutModeManager = null;
    SettingsTopView preView = null;
    private TextView miracast_name = null;
    private TextView remoteControlIp = null;
    public static OnSharedPreferenceChangeListener listener = null ;

    private static boolean isG35Device = false ;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_main);

		ImageView cursorview = (ImageView) findViewById(R.id.cursorview);
		SettingsTopView.setCursorView(cursorview);
        
		sw = (SystemWriteManager) mContext.getSystemService("system_write");
        mEthernetManager = (EthernetManager) mContext.getSystemService("ethernet");
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mHander = new MyHandle();

		settingsContentLayout_01 = (LinearLayout) findViewById(R.id.settingsContent01);
		settingsContentLayout_02 = (LinearLayout) findViewById(R.id.settingsContent02);
		settingsContentLayout_03 = (LinearLayout) findViewById(R.id.settingsContent03);
		settingsContentLayout_04 = (LinearLayout) findViewById(R.id.settingsContent04);

		screen_self_set = (LinearLayout) findViewById(R.id.screen_self_set);
		screen_self_set.setOnClickListener(this);
		self_select_mode = (TextView) findViewById(R.id.self_select_mode);
		auto_set_screen = (TextView) findViewById(R.id.auto_set_screen);

		current_mode_value = (TextView) findViewById(R.id.current_mode_value);

		cvbs_current_mode_value = (TextView) findViewById(R.id.cvbs_current_mode_value);
        miracast_name = (TextView) findViewById(R.id.miracast_name);
		sharepreference = getSharedPreferences(PREFERENCE_BOX_SETTING,
				Context.MODE_PRIVATE);
        remoteControlIp = (TextView) findViewById(R.id.remoteControl_ip);
        listener = new OnSharedPreferenceChangeListener() {
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
			    Log.d(TAG,"===== onSharedPreferenceChanged() , key :" + key);
				if(key.equals("open_remote_control")){
                     String value = pref.getString("open_remote_control","false");
                     
                     if(value.equals("true")){
                            remoteControlIp.setVisibility(View.VISIBLE);
                            String ip = getDeviceIpAddress();
                            if(ip!=null){
                                remoteControlIp.setText(ip);
                            }
                     }else{
                            remoteControlIp.setVisibility(View.INVISIBLE);
                     }                   
                }else if(key.equals("open_mirrcast")){
                    
                   
                    String value = pref.getString("open_mirrcast","false");
                    if ("true".equals(value)) {
                        miracast_name.setVisibility(View.VISIBLE);
                        if (mThisDevice != null) {
                            String name = mContext.getResources().getString(R.string.miracast_server_name)+ " " + mThisDevice.deviceName;
                            miracast_name.setText(name);
                            Log.d(TAG, "===== mThisDevice name is  "+ mThisDevice.deviceName);
                        } 
                    }else{
                        miracast_name.setVisibility(View.INVISIBLE);
                    }   
                }
			}
		};
        sharepreference.registerOnSharedPreferenceChangeListener(listener);

        
		secreen_auto = (LinearLayout) findViewById(R.id.secreen_auto);

		secreen_auto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setAutoOutModeSwitch();
			}
		});

        cvbs_screen_self_set = (LinearLayout) findViewById(R.id.cvbs_screen_self_set);
		cvbs_screen_self_set.setOnClickListener(this);
		cvbs_self_select_mode = (TextView) findViewById(R.id.cvbs_self_select_mode); 
        
		upDateOutModeUi();

		hide_status_bar = (TextView) findViewById(R.id.hide_status_bar);
		upDateStatusBarUi();

		LinearLayout screen_hide_bar = (LinearLayout) findViewById(R.id.screen_hide_bar);
		screen_hide_bar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setStatusBarSwitch();
			}
		});

		settingsTopView_01 = (SettingsTopView) findViewById(R.id.settingsTopView_01);
        settingsTopView_01.setOnClickListener(this);
		settingsTopView_02 = (SettingsTopView) findViewById(R.id.settingsTopView_02);
        settingsTopView_02.setOnClickListener(this);
		settingsTopView_03 = (SettingsTopView) findViewById(R.id.settingsTopView_03);
        settingsTopView_03.setOnClickListener(this);
		settingsTopView_04 = (SettingsTopView) findViewById(R.id.settingsTopView_04);
        settingsTopView_04.setOnClickListener(this);

		settings_content_postion = (LinearLayout) findViewById(R.id.settings_content_postion);
		button_scrren_adjust = (LinearLayout) findViewById(R.id.button_scrren_adjust);
		button_scrren_adjust.setOnClickListener(this);

		wifi_direct = (LinearLayout) findViewById(R.id.wifi_direct);
		wifi_direct.setNextFocusUpId(R.id.settingsTopView_03);

		SettingsTopView.settingsContentLayout_01 = settingsContentLayout_01;
		SettingsTopView.settingsContentLayout_02 = settingsContentLayout_02;
		SettingsTopView.settingsContentLayout_03 = settingsContentLayout_03;
		SettingsTopView.settingsContentLayout_04 = settingsContentLayout_04;

		img_num_hundred = (ImageView) findViewById(R.id.img_num_hundred);
		img_num_ten = (ImageView) findViewById(R.id.img_num_ten);
		img_num_unit = (ImageView) findViewById(R.id.img_num_unit);
		img_progress_bg = (ImageView) findViewById(R.id.img_progress_bg);

		screen_keep = (LinearLayout) findViewById(R.id.screen_keep);

		screen_time_01 = (TextView) findViewById(R.id.screen_time_01);
        screen_time_01.setOnClickListener(this);
		screen_time_02 = (TextView) findViewById(R.id.screen_time_02);
        screen_time_02.setOnClickListener(this);
		screen_time_03 = (TextView) findViewById(R.id.screen_time_03);
        screen_time_03.setOnClickListener(this);
		screen_time_04 = (TextView) findViewById(R.id.screen_time_04);
        screen_time_04.setOnClickListener(this);
		mScreenKeepTimes = new TextView[4];
		mScreenKeepTimes[0] = screen_time_01;
		mScreenKeepTimes[1] = screen_time_02;
		mScreenKeepTimes[2] = screen_time_03;
		mScreenKeepTimes[3] = screen_time_04;

		

		Button system_update = (Button) findViewById(R.id.system_update);
		system_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent();
				ComponentName componetName = new ComponentName(
						"com.amlapp.update.otaupgrade",
						"com.amlapp.update.otaupgrade.MainActivity");
				i.setComponent(componetName);
				mContext.startActivity(i);

			}
		});

		Button more_settings = (Button) findViewById(R.id.more_settings);
		more_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				ComponentName componetName = new ComponentName("com.android.settings", "com.android.settings.Settings");
				i.setComponent(componetName);
				mContext.startActivity(i);
			}
		});

		LinearLayout request_screen = (LinearLayout) findViewById(R.id.request_screen);
		requestScreen = (TextView) findViewById(R.id.requestScreen);
		upDateRequestScreen();
		request_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setRequestScreenSwitch();
			}
		});

		LinearLayout keep_screen_land = (LinearLayout) findViewById(R.id.keep_screen_land);
		screen_land = (TextView) findViewById(R.id.screen_land);
		upDateKeepScreenLandUi();
		keep_screen_land.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setKeepScreenLandSwitch();
			}
		});

		LinearLayout voice_setting = (LinearLayout) findViewById(R.id.voice_setting);
		voice_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openVoicePopupWindow();

			}
		});

		LinearLayout wifi_direct = (LinearLayout) findViewById(R.id.wifi_direct);
		
		miracast = (TextView) findViewById(R.id.miracast);
		upDateMirrcastUi();
		wifi_direct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                Intent i = new Intent();
                ComponentName componetName = new ComponentName("com.amlogic.miracast", "com.amlogic.miracast.WiFiDirectMainActivity");
                i.setComponent(componetName);
				mContext.startActivity(i);
				//setMiracastSwitch();
			}
		});

		LinearLayout remote_control = (LinearLayout) findViewById(R.id.remote_control);
		remoteControl = (TextView) findViewById(R.id.remoteControl);
        
		upDateRemoteControlUi();
		remote_control.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setRemoteControlSwitch();

			}
		});

		LinearLayout cec_control = (LinearLayout) findViewById(R.id.cec_control);
		cec_control.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openCECPopupWindow();
			}
		});

		city_select = (LinearLayout) findViewById(R.id.city_select);
		province_view = (TextView) findViewById(R.id.province);
        province_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openLocationPopupWindow(0);
			}
		});
		city_view = (TextView) findViewById(R.id.city);
        city_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openLocationPopupWindow(1);
			}
		});
		city_select.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					province_view
							.setBackgroundResource(R.drawable.select_focused);

				} else {
					province_view.setBackgroundResource(Color.TRANSPARENT);
					city_view.setBackgroundResource(Color.TRANSPARENT);
				}

			}
		});

		city_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openLocationPopupWindow(mCrrentLocationfocus);
			}
		});

		sharepreference = getSharedPreferences(PREFERENCE_BOX_SETTING,
				Context.MODE_PRIVATE);
		String p = sharepreference.getString("settings_province",
				getResources().getString(R.string.province));
		province_view.setText(p);
		String c = sharepreference.getString("settings_city", getResources()
				.getString(R.string.city));
		city_view.setText(c);

		eth_IP_value = (TextView) findViewById(R.id.eth_IP_value);

		eth_connected_tip = (TextView) findViewById(R.id.eth_connected_tip);
		eth_ip_layout = (LinearLayout) findViewById(R.id.eth_ip_layout);

        String temp = null;
        TextView model_number_value = (TextView) findViewById(R.id.model_number_value);
        String productModel = SystemInfoManager.getModelNumber();
        model_number_value.setText(productModel);
        if(productModel.contains("g35")){
            isG35Device = true;
            Log.d(TAG,"===== it's g35 devices!");
        }
        
        TextView firmware_version_value = (TextView) findViewById(R.id.firmware_version_value);
        temp =  SystemInfoManager.getAndroidVersion();
        firmware_version_value.setText(temp);
        
        
        TextView build_number_value = (TextView) findViewById(R.id.build_number_value);
        
        build_number_value.setText( SystemInfoManager.getBuildNumber());
        
        TextView kernel_version_value = (TextView) findViewById(R.id.kernel_version_value);
        temp = SystemInfoManager.getKernelVersion();
        kernel_version_value.setText(temp);

        initNetView();

        IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        filter.addAction(WindowManagerPolicy.ACTION_HDMI_HW_PLUGGED);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
         
		
	    myReceiver = new MyReceiver();
		registerReceiver(myReceiver, filter);

        string_pcm = mContext.getResources().getString(R.string.voices_settings_pcm);
        string_spdif = mContext.getResources().getString(R.string.voices_settings_spdif);
        string_hdmi = mContext.getResources().getString(R.string.voices_settings_hdmi);
        
	}

	private void upDateRemoteControlUi() {

		String isOpenRemoteContrl = sharepreference.getString("open_remote_control", "false");
		if (isOpenRemoteContrl.equals("true")) {
			remoteControl.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.on, 0);
            remoteControlIp.setVisibility(View.VISIBLE);
            String ip = getDeviceIpAddress();
            if(ip!=null){
                remoteControlIp.setText(ip);
            }
		} else {
			remoteControl.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.off, 0);
            remoteControlIp.setVisibility(View.INVISIBLE);
		}

	}

	private void setRemoteControlSwitch() {
		String isOpenRemoteContrl = sharepreference.getString(
				"open_remote_control", "false");
		Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,
				Context.MODE_PRIVATE).edit();
		if (isOpenRemoteContrl.equals("true")) {
			editor.putString("open_remote_control", "false");
			editor.commit();
			Intent i = new Intent();
			i.setAction("com.amlogic.remoteControl.RC_STOP");
			//SettingsMboxActivity.this.sendBroadcast(i);
            mContext.sendBroadcastAsUser(i, UserHandle.ALL ); 
			Log.d(TAG,"===== send broadcast stop remote service");
		} else {
			editor.putString("open_remote_control", "true");
			editor.commit();
			Intent i = new Intent();
			i.setAction("com.amlogic.remoteControl.RC_START");
			//SettingsMboxActivity.this.sendBroadcast(i);
			mContext.sendBroadcastAsUser(i, UserHandle.ALL); 
			Log.d(TAG,"===== send broadcast start remote service");
		}
		upDateRemoteControlUi();
	}

	private void setMiracastSwitch() {
      
		String isOpenMiracast = sharepreference.getString("open_mirrcast","false");
		Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,Context.MODE_PRIVATE).edit();
		if (isOpenMiracast.equals("true")) {
			editor.putString("open_mirrcast", "false");
			editor.commit();
			Intent i = new Intent();
			i.setAction("com.amlogic.miracast.MIRACAST_BKSTOP");
			//mContext.sendBroadcast(i);
			mContext.sendBroadcastAsUser(i, UserHandle.ALL ); 
            //com.amlogic.miracast   WiFiDirectMainActivity
			Log.d(TAG,"===== send broadcast stop miracast");
		} else {
			editor.putString("open_mirrcast", "true");
			editor.commit();
			Intent i = new Intent();
			i.setAction("com.amlogic.miracast.MIRACAST_BKSTART");
			//mContext.sendBroadcast(i);
            mContext.sendBroadcastAsUser(i, UserHandle.ALL ); 
			Log.d(TAG,"===== send broadcast start miracast");
		}
		upDateMirrcastUi();
	}

	private void upDateMirrcastUi() {
		String isOpenMirrcast = sharepreference.getString("open_mirrcast",
				"false");
		if (isOpenMirrcast.equals("true")) {
			//miracast.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.on, 0);
            miracast_name.setVisibility(View.VISIBLE);
            String name = sharepreference.getString("miracast_device_name","NULL");
            if("NULL".equals(name)){
                miracast_name.setVisibility(View.INVISIBLE);
            }else{
                miracast_name.setText(name);
            }
            
		} else {
		    miracast_name.setVisibility(View.INVISIBLE);
			//miracast.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.off, 0);
		}

	}

	private void setKeepScreenLandSwitch() {
		String istKeepScreenLand = sharepreference.getString(
				"keep_screen_land", "true");
		Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,
				Context.MODE_PRIVATE).edit();
		if ("true".equals(istKeepScreenLand)) {
			editor.putString("keep_screen_land", "false");
			editor.commit();
			sw.setProperty("sys.keeplauncher.landcape", "false");

		} else {
			editor.putString("keep_screen_land", "true");
			editor.commit();
			sw.setProperty("sys.keeplauncher.landcape", "true");
		}

		upDateKeepScreenLandUi();

	}

	private void upDateKeepScreenLandUi() {

		String vlaue = sharepreference.getString("keep_screen_land", "true");
		if ("true".equals(vlaue)) {
			screen_land.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_checked, 0);
		} else {
			screen_land.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uncheck, 0);
		}

	}

	private void setRequestScreenSwitch() {

		String vlaue = sw.getPropertyString("ubootenv.var.has.accelerometer" , "***");
		if ("true".equals(vlaue)) {
			sw.setProperty("ubootenv.var.has.accelerometer", "false");
		} else if("false".equals(vlaue)){
			sw.setProperty("ubootenv.var.has.accelerometer", "true");
		}else{
            sw.setProperty("ubootenv.var.has.accelerometer", "false");
        }

		upDateRequestScreen();
	}

	private void upDateRequestScreen() {
		String isRequest_screen = sw.getPropertyString("ubootenv.var.has.accelerometer","***");
		Log.d(TAG, "====== isRequest_screen: " + isRequest_screen);
		if ("true".equals(isRequest_screen)) {
			requestScreen.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_checked, 0);
		} else if("false".equals(isRequest_screen)){
			requestScreen.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uncheck, 0);
		}
	}

	private void setStatusBarProperty(String value) {
		sw.setProperty("persist.sys.hideStatusBar", value);
	}

	private String getStatusBarProperty() {
		return sw.getPropertyString("persist.sys.hideStatusBar", "true");
	}

	void setStatusBarSwitch() {
		String isHideStatusBar = sharepreference.getString("hide_status_bar","true");
		Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,Context.MODE_PRIVATE).edit();

		if (isHideStatusBar.equals("true")) {
			setStatusBarProperty("false");
			editor.putString("hide_status_bar", "false");
			editor.commit();
		} else {
			setStatusBarProperty("true");
			editor.putString("hide_status_bar", "true");
			editor.commit();
		}
		upDateStatusBarUi();

	}

	void upDateStatusBarUi() {
		String isHideStatusBar = sharepreference.getString("hide_status_bar",
				"true");
		if ("true".equals(isHideStatusBar)) {
			hide_status_bar.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.on, 0);
			setStatusBarProperty("true");
		} else {
			hide_status_bar.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.off, 0);
			setStatusBarProperty("false");
		}

	}

	void upDateOutModeUi() {

		String isAutoSelectOutMode = sharepreference.getString("auto_output_mode", "true");

		if (isAutoSelectOutMode.equals("true")) {
			screen_self_set.setFocusable(false);
			screen_self_set.setClickable(false);
			self_select_mode.setTextColor(Color.GRAY);
			current_mode_value.setTextColor(Color.GRAY);
			auto_set_screen.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.on, 0);
            Message msg = mHander.obtainMessage();
            msg.what = UPDATE_OUTPUT_MODE_UI ;
            mHander.sendMessageDelayed(msg, 5000);
            
		} else {
			auto_set_screen.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.off, 0);
			screen_self_set.setFocusable(true);
			screen_self_set.setClickable(true);
			self_select_mode.setTextColor(Color.WHITE);
			current_mode_value.setTextColor(Color.WHITE);
		}
       
        boolean isDualOutPutMode = sw.getPropertyBoolean("ro.platform.has.cvbsmode", false);
        if(!isDualOutPutMode){
            if(HdmiManager.isHDMIPlugged(sw)){
                cvbs_screen_self_set.setVisibility(View.GONE);
                Log.d(TAG,"===== hdmi mode : " +  OutPutModeManager.getCurrentOutPutModeTitle(sw,"hdmi"));
                current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,"hdmi"));
            }else{
                cvbs_screen_self_set.setVisibility(View.VISIBLE);
                secreen_auto.setFocusable(false);
                secreen_auto.setClickable(false);
                screen_self_set.setFocusable(false);
			    screen_self_set.setClickable(false);
			    self_select_mode.setTextColor(Color.GRAY);
			    current_mode_value.setTextColor(Color.GRAY);
                auto_set_screen.setTextColor(Color.GRAY);
                cvbs_current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,"cvbs"));
			    auto_set_screen.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.off, 0);
            }
            
        }else{
             cvbs_screen_self_set.setVisibility(View.VISIBLE);
             cvbs_current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,"cvbs"));
             current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,"hdmi"));
        }       
	}

	void setAutoOutModeSwitch() {
		String isAuto = sharepreference.getString("auto_output_mode", "true");
		Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,
				Context.MODE_PRIVATE).edit();
		if (isAuto.equals("true")) {

			editor.putString("auto_output_mode", "false");
			editor.commit();

		} else {
			editor.putString("auto_output_mode", "true");
			editor.commit();

            HdmiManager mHdmiManager = new HdmiManager(mContext);
            mHdmiManager.hdmiPlugged();

			//Intent i = new Intent("AUTO.CHANGE.OUTPUT.MODE");
			//mContext.sendBroadcast(i);
		}

		upDateOutModeUi();

	}

	private void initNetView() {
		mCurrentContentNum = VIEW_NETWORK;
		wifi_ssid_value = (TextView) findViewById(R.id.wifi_ssid_value);
		ip_address_value = (TextView) findViewById(R.id.ip_address_value);
		
		select_wifi = (TextView) findViewById(R.id.select_wifi);        
		select_wifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setWifiCheckBoxSwitch();
			}
		});
		select_ethernet = (TextView) findViewById(R.id.select_ethernet);
        if(isG35Device){
            TextView no_network = (TextView)findViewById(R.id.no_network);
            no_network.setText(mContext.getResources().getString(R.string.no_network_wifi_only));
            select_ethernet.setVisibility(View.INVISIBLE);
        }
		select_ethernet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setEthCheckBoxSwitch();
			}
		});
		select_ethernet.setNextFocusUpId(R.id.settingsTopView_01);
        select_wifi.setNextFocusLeftId(R.id.select_ethernet);
		select_wifi.setNextFocusRightId(R.id.select_ethernet);
		wifi_connected_tip = (TextView) findViewById(R.id.wifi_connected_tip);

		root_eth_view = (LinearLayout) findViewById(R.id.root_eth_view);
        net_root_view = (LinearLayout) findViewById(R.id.net_root_view);
		root_wifi_view = (LinearLayout) findViewById(R.id.root_wifi_view);
		root_wifi_view.setVisibility(View.GONE);
		root_eth_view.setVisibility(View.VISIBLE);

		

		wifi_connected = (LinearLayout) findViewById(R.id.wifi_connected);
		wifi_input_password = (LinearLayout) findViewById(R.id.wifi_input_password);

		wifi_not_connect = (LinearLayout) findViewById(R.id.wifi_not_connect);

		wifi_slect_tip = (TextView) findViewById(R.id.wifi_slect_tip);

		password_editview = (EditText) findViewById(R.id.password_input);

		password_editview.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		wifi_listview_tip = (TextView) findViewById(R.id.wifi_listview_tip);
		// password_editview.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

		password_editview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				imm.showSoftInput(v, InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});

		Button password_connect = (Button) findViewById(R.id.password_connect);
		password_connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = password_editview.getText().toString();

				if (password != null) {
					showConnectingView();
					mAccessPointListAdapter.connect2AccessPoint(password);
				} else {
					Toast.makeText(mContext, mContext.getResources().getString(R.string.passwork_input_notice),3000).show();
				}

			}
		});

		mAcessPointListView = (ListView) findViewById(R.id.wifiListView);
        mAcessPointListView.setNextFocusRightId(R.id.password_input);
	
		mAcessPointListView.setOnItemClickListener(this);

		mAccessPointListAdapter = new AccessPointListAdapter(this);
		mAcessPointListView.setAdapter(mAccessPointListAdapter);

	}

    private void wifiResume(){
        if(getEthCheckBoxState()){ 
            if(isEthDeviceAdded()) {
                mWifiManager.setWifiEnabled(false);
                mEthernetManager.setEthEnabled(true);
                updateNetWorkUI(2);
            }else{
                mEthernetManager.setEthEnabled(false); 
                if(getWifiCheckBoxState()){
                    mWifiManager.setWifiEnabled(true);
                    wifiScan(); 
                    updateNetWorkUI(1);
                }
            }
        }else{
            if(getWifiCheckBoxState()){
                mWifiManager.setWifiEnabled(true);
                wifiScan(); 
                updateNetWorkUI(1);
            }else{
                updateNetWorkUI(0);
            }                
        }               
        updateEthCheckBoxUI();
        upDateWifiCheckBoxUI();

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
        Log.d(TAG,"===== isEthDeviceAdded() , get sysfs value : " + value);

        Log.d(TAG,"===== isEthDeviceAdded() , get API value : " + mEthernetManager.isEthDeviceAdded());

        if("100".equals(value) ||  mEthernetManager.isEthDeviceAdded()){
            return true ;
        }else{
            return false ;
        }
    }

    private void setEthCheckBoxSwitch(){ 
        if(isEthDeviceAdded()){           
            if(getEthCheckBoxState()){            
                enableEthernetView(false);
            }else{
                enableEthernetView(true);
            }               
        }else{
            mEthernetManager.setEthEnabled(false); 
            Toast.makeText(mContext, mContext.getResources().getString(R.string.ethernet_inplug_notice), 4000).show(); 
            if(!getWifiCheckBoxState())
                updateNetWorkUI(0);
        }
         updateEthCheckBoxUI();   
         upDateWifiCheckBoxUI();
    }
    
    private void enableEthernetView(boolean able){                     
        if(able){ 
            updateNetWorkUI(2);
                mWifiManager.setWifiEnabled(false);
            if(isEthDeviceAdded()){
                eth_connected_tip.setText(R.string.ethernet_connectting);
                mEthernetManager.setEthEnabled(true);   
            }else{
                Toast.makeText(mContext, mContext.getResources().getString(R.string.ethernet_inplug_notice), 4000).show();
            } 
        }else{
            updateNetWorkUI(0);
            mEthernetManager.setEthEnabled(false);
        }       
    }
    
    private void enableWifiView(boolean able){
        if(able){ 
            if(isEthDeviceAdded()){
                mEthernetManager.setEthEnabled(false);
            }  

            mWifiManager.setWifiEnabled(true);
            wifiScan(); 
            updateNetWorkUI(1);
        }else{          
            mWifiManager.setWifiEnabled(false);
            updateNetWorkUI(0);
        }     
        
    }
    
    private void updateEthCheckBoxUI(){         
         if(getEthCheckBoxState()){
            select_ethernet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
         }else{
            select_ethernet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_uncheck, 0, 0, 0);
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
            select_wifi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
         }else{
            select_wifi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_uncheck, 0, 0, 0);
         }
    }
    private void updateNetWorkUI(int type){
        Log.d(TAG,"===== updateNetWorkUI() 001");
        if(type == 0){
            Log.d(TAG,"===== updateNetWorkUI() 002");
            net_root_view.setVisibility(View.VISIBLE);
            root_eth_view.setVisibility(View.GONE);
            root_wifi_view.setVisibility(View.GONE);
        }else if(type == 1){
            Log.d(TAG,"===== updateNetWorkUI() 003");
            net_root_view.setVisibility(View.GONE);
            root_eth_view.setVisibility(View.GONE);
            root_wifi_view.setVisibility(View.VISIBLE);
            
            boolean isWifiConnected = WifiUtils.isWifiConnected(mContext);
            if(isWifiConnected){
                showWifiConnectedView();
            }else{
                showWifiDisconnectedView();
            }            
        }else if(type == 2){
            Log.d(TAG,"===== updateNetWorkUI() 004");
            net_root_view.setVisibility(View.GONE);
            root_eth_view.setVisibility(View.VISIBLE);
            root_wifi_view.setVisibility(View.GONE);
            upDateEthernetInfo();
        }else{
            Log.d(TAG,"===== updateNetWorkUI() 005");
            net_root_view.setVisibility(View.VISIBLE);
            root_eth_view.setVisibility(View.GONE);
            root_wifi_view.setVisibility(View.GONE);
        }

    }
    private void wifiScan(){
        mAccessPointListAdapter.startScanApcessPoint();

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
				while (mAccessPointListAdapter.getCount() <= 0
						&& mCurrentContentNum == VIEW_NETWORK
						) {
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
				if (mAccessPointListAdapter.getCount() > 0) {
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

		wifi_listview_tip.setVisibility(View.GONE);
		mAcessPointListView.setVisibility(View.VISIBLE);

		wifi_input_password.setVisibility(View.GONE);
		wifi_not_connect.setVisibility(View.GONE);
		wifi_connected.setVisibility(View.VISIBLE);
		mAccessPointListAdapter.updateAccesspointList();

		DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
		WifiInfo mWifiinfo = mWifiManager.getConnectionInfo();

		if (mWifiinfo != null) {
			wifi_ssid_value.setVisibility(View.VISIBLE);
			String wifi_name = mWifiinfo.getSSID().substring(1,mWifiinfo.getSSID().length() - 1);
			wifi_ssid_value.setText(wifi_name);
			ip_address_value.setText(int2ip(mWifiinfo.getIpAddress()));
			//mAccessPointListAdapter.setCurrentConnectedItemBySsid(mWifiinfo.getSSID());
            mAccessPointListAdapter.setCurrentConnectItemSSID(mWifiinfo.getSSID());
		}

		//if (mDhcpInfo != null) {
		//	gateway_value.setText(Formatter.formatIpAddress(mDhcpInfo.gateway));
		//	subnet_mask_value.setText(Formatter.formatIpAddress(mDhcpInfo.netmask));
		//}

		//int wifiLevelDrawableId = mAccessPointListAdapter.getWifiLevelDrawableId(mWifiinfo.getRssi());

		//wifi_level.setBackgroundResource(wifiLevelDrawableId);

	}

    private String getDeviceIpAddress(){
        boolean isEthConnected = WifiUtils.isEthConnected(mContext);
        boolean isWifiConnected = WifiUtils.isWifiConnected(mContext);
        if(isWifiConnected){
            WifiInfo mWifiinfo = mWifiManager.getConnectionInfo();
            String ipAddress = null;
            if(mWifiinfo != null){
                ipAddress = int2ip(mWifiinfo.getIpAddress());
                Log.d(TAG,"==== wifi ipAddress : " + ipAddress);
            }
            
            return ipAddress;   
        }else if(isEthConnected){
            DhcpInfo mDhcpInfo = mEthernetManager.getDhcpInfo();
            if (mDhcpInfo != null) {
				int ip = mDhcpInfo.ipAddress;
                String ipAddress = int2ip(ip);
				Log.d(TAG,"==== wifi ipAddress : " + ipAddress);
				return ipAddress;
			}
        }
            return null;
    }

	@Override
	protected void onResume() {
		super.onResume();
        wifiResume();
	}

	public String int2ip(long ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	private void upDateEthernetInfo() {
		Log.d(TAG, "===== update ethernet info ");
        boolean isEthConnected = WifiUtils.isEthConnected(mContext);
		if (isEthConnected) {
            if(eth_ip_layout != null && wifi_connected != null){
                eth_ip_layout.setVisibility(View.VISIBLE);
                wifi_connected.setVisibility(View.VISIBLE);
            }
			   
			DhcpInfo mDhcpInfo = mEthernetManager.getDhcpInfo();
			if (mDhcpInfo != null) {
				int ip = mDhcpInfo.ipAddress;
                if(eth_connected_tip != null)
				    eth_connected_tip.setText(R.string.eth_connectd);
				Log.d(TAG, "====== ip  : " + ip + "   int2ip(ip) : "+ int2ip(ip));
				if(eth_IP_value != null)
				    eth_IP_value.setText(int2ip(ip));
				else {
					Log.d(TAG,"=====  eth_IP_value is null !!!");
				}	
			}

		} else {
		    if(eth_connected_tip != null && eth_ip_layout != null){
			    eth_connected_tip.setText(R.string.ethernet_error);
			    eth_ip_layout.setVisibility(View.GONE);
            }
		}

	}

	private void showPasswordView() {
		wifi_connected.setVisibility(View.GONE);
		wifi_not_connect.setVisibility(View.GONE);
		wifi_input_password.setVisibility(View.VISIBLE);
		password_editview.requestFocus();

	}

	private void showConnectingView() {
		wifi_connected.setVisibility(View.GONE);
		wifi_input_password.setVisibility(View.GONE);
		wifi_not_connect.setVisibility(View.VISIBLE);
		wifi_slect_tip.setText(R.string.connectting_wifi_tips);
	}

	private void showWifiDisconnectedView() {
		wifi_connected.setVisibility(View.GONE);
		wifi_input_password.setVisibility(View.GONE);
		wifi_not_connect.setVisibility(View.VISIBLE);
		mAccessPointListAdapter.updateAccesspointList();
		wifi_slect_tip.setText(R.string.wifi_ap_select);
	}

    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (myReceiver != null) {
			unregisterReceiver(myReceiver);
			myReceiver = null;
		}
		super.onStop();
	}

	@Override
	public void onClick(View v) {
        Log.d(TAG,"===== onClick()");
		int id = v.getId();

		if (v instanceof TextView) {
            if(id == R.id.screen_time_01){
                slectKeepScreenIndex(0);
            }else if(id == R.id.screen_time_02){
                slectKeepScreenIndex(1);
            }else if(id == R.id.screen_time_03){
                slectKeepScreenIndex(2);
            }else if(id == R.id.screen_time_04){
                slectKeepScreenIndex(3);
            }
		}

		if (v instanceof LinearLayout) {
			if (id == R.id.button_scrren_adjust) {
				openScreenAdjustLayout();
            }else if (id == R.id.screen_self_set) {
                openOutPutModePopupWindow("hdmi");
            }else if (id == R.id.cvbs_screen_self_set){
                openOutPutModePopupWindow("cvbs");
            }
		}

        if (v instanceof SettingsTopView) {
            if( mCurrentContentNum != VIEW_SCREEN_ADJUST)
                setViewVisable((SettingsTopView)v);			
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

	private void openScreenAdjustLayout() {

		mCurrentContentNum = VIEW_SCREEN_ADJUST;
		SettingsTopView.preFocusView = settingsTopView_02;
		settingsTopView_02.requestFocus();
		settingsContentLayout_02.setVisibility(View.GONE);

		settings_content_postion.setVisibility(View.VISIBLE);
		btn_position_zoom_out = (ImageButton) findViewById(R.id.btn_position_zoom_out);
        btn_position_zoom_out.setOnClickListener(this);
		btn_position_zoom_in = (ImageButton) findViewById(R.id.btn_position_zoom_in);
        btn_position_zoom_in.setOnClickListener(this);
        TextView screen_tip_01 = (TextView)findViewById(R.id.screen_tip_01);
		screen_tip_01.requestFocus();
		// btn_position_zoom_out.setBackgroundResource(R.drawable.plus_focus);
		mScreenPositionManager = new ScreenPositionManager(this);
		mScreenPositionManager.initPostion();
		screen_rate = mScreenPositionManager.getCurrentRate();
		showProgressUI(0);
		ImageView around_line = (ImageView) findViewById(R.id.screen_adjust_line);
		around_line.setVisibility(View.VISIBLE);
	}

	private void closeScreenAdjustLayout() {
		mScreenPositionManager.savePostion();
		mCurrentContentNum = VIEW_DISPLAY;
		SettingsTopView.preFocusView = settingsTopView_02;
		settingsTopView_02.requestFocus();
		settings_content_postion.setVisibility(View.GONE);
		settingsContentLayout_02.setVisibility(View.VISIBLE);
		button_scrren_adjust.requestFocus();
		ImageView around_line = (ImageView) findViewById(R.id.screen_adjust_line);
		around_line.setVisibility(View.GONE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.d(TAG, "onKeyDown(),keyCode : " + keyCode);
		if (mCurrentContentNum == VIEW_SCREEN_ADJUST) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				btn_position_zoom_in.setBackgroundResource(R.drawable.minus_unfocus);
				btn_position_zoom_out.setBackgroundResource(R.drawable.plus_focus);
                if(screen_rate < MAX_Height){
				    showProgressUI(1);
			        mScreenPositionManager.zoomIn();
                }

			} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				if (screen_rate > MIN_Height) {
                    showProgressUI(-1);
					mScreenPositionManager.zoomOut();
				}
				btn_position_zoom_in.setBackgroundResource(R.drawable.minus_focus);
				btn_position_zoom_out.setBackgroundResource(R.drawable.plus_unfocus);
			} else if (keyCode == KeyEvent.KEYCODE_BACK
					|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				closeScreenAdjustLayout();
			}
			return true;
		}

		if (screen_keep.isFocused()) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				slectKeepScreenIndex(true);
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				slectKeepScreenIndex(false);
				return true;
			}
		}

		if (city_select.isFocused()) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if (mCrrentLocationfocus == 0) {
					mCrrentLocationfocus = 1;
					province_view.setBackgroundResource(Color.TRANSPARENT);
					city_view.setBackgroundResource(R.drawable.select_focused);
				} else {
					mCrrentLocationfocus = 0;
					city_view.setBackgroundResource(Color.TRANSPARENT);
					province_view.setBackgroundResource(R.drawable.select_focused);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setScreenTimeOut(int index) {
		int value = 0;
		if (index == 0) {
			value = Integer.MAX_VALUE;
		} else if (index == 1) {
			value = 4 * 60 * 1000;
		} else if (index == 2) {
			value = 8 * 60 * 1000;
		} else if (index == 3) {
			value = 12 * 60 * 1000;
		}
		Log.d(TAG, "===== set time out is : " + value);
		Settings.System.putInt(getContentResolver(), "screen_off_timeout",value);
        Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE).edit();
        editor.putInt("screen_timeout", value);
	    editor.commit();   
    }

    private void slectKeepScreenIndex(int index){
        mCurrentScreenKeepIndex = index;
        for(int i=0 ; i<=3 ;i++){
            if(i == index){
                mScreenKeepTimes[i].setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_checked, 0);
            }else{
                    mScreenKeepTimes[i].setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uncheck, 0);
            }  
        }
        setScreenTimeOut(mCurrentScreenKeepIndex);
    }

	private void slectKeepScreenIndex(boolean isLeft) {
		if (isLeft) {
			if (mCurrentScreenKeepIndex == 0) {

				mScreenKeepTimes[0].setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_uncheck, 0);
				mScreenKeepTimes[mScreenKeepTimes.length - 1]
                    .setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_checked, 0);
				mCurrentScreenKeepIndex = mScreenKeepTimes.length - 1;

			} else {

				mScreenKeepTimes[mCurrentScreenKeepIndex - 1]
						.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_checked, 0);
				mScreenKeepTimes[mCurrentScreenKeepIndex]
						.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uncheck, 0);
				--mCurrentScreenKeepIndex;
			}

		} else {

			if (mCurrentScreenKeepIndex == mScreenKeepTimes.length - 1) {
				mScreenKeepTimes[0].setCompoundDrawablesWithIntrinsicBounds(0,
						0, R.drawable.ic_checked, 0);
				mScreenKeepTimes[mScreenKeepTimes.length - 1]
						.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uncheck, 0);
				mCurrentScreenKeepIndex = 0;
			} else {

				mScreenKeepTimes[mCurrentScreenKeepIndex + 1]
						.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_checked, 0);
				mScreenKeepTimes[mCurrentScreenKeepIndex]
						.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_uncheck, 0);
				++mCurrentScreenKeepIndex;
			}

		}
		setScreenTimeOut(mCurrentScreenKeepIndex);
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

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG,"===== onKeyUp(), keyCode : " + keyCode);
		if (mCurrentContentNum == VIEW_SCREEN_ADJUST) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				btn_position_zoom_in.setBackgroundResource(R.drawable.minus_unfocus);
				btn_position_zoom_out.setBackgroundResource(R.drawable.plus_unfocus);
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				btn_position_zoom_in.setBackgroundResource(R.drawable.minus_unfocus);
				btn_position_zoom_out.setBackgroundResource(R.drawable.plus_unfocus);
			}
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent instanceof ListView) {
			onClickAccessPoint(position);
		}

	}

	private void onClickAccessPoint(int index) {

		mAccessPointListAdapter.setCurrentSelectItem(index);
		int securityType = mAccessPointListAdapter.getCurrentAccessPointSecurityType(index);
		Log.d(TAG, "===== securityType :  " + securityType);

		if (securityType == 0 || securityType == 4) {
			showConnectingView();
			mAccessPointListAdapter.connect2OpenAccessPoint();
		} else {
			showPasswordView();
		}

	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,Context.MODE_PRIVATE).edit();
			Log.e(TAG, "action : " + action);
            
			if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				mThisDevice = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
				Log.d(TAG, "miracast device name: " + mThisDevice.deviceName);
                editor.putString("miracast_device_name", mThisDevice.deviceName);
			    editor.commit();   
			}
            
            Log.e(TAG, "===== onReceive() 001");
			if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {  

                ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                Log.d(TAG,"===== wifi.getState()  : " +  wifi.getState());
                
                if(mCurrentContentNum == VIEW_NETWORK){
                    boolean isWifiConnected = WifiUtils.isWifiConnected(mContext);
                    boolean isEthConnected = WifiUtils.isEthConnected(mContext);
                    Log.e(TAG, "===== onReceive() 002");
                    if(isEthConnected){
                        Log.e(TAG, "===== onReceive() 003");
                        updateNetWorkUI(2);
                        updateEthCheckBoxUI();   
                        upDateWifiCheckBoxUI();
                    }else if(isWifiConnected){
                         updateNetWorkUI(1);
                         Log.e(TAG, "===== onReceive() 004");
                    }
                }
                 Log.e(TAG, "===== onReceive() 005");
                
                //if(mCurrentContentNum == VIEW_DISPLAY){
                    String isOpenRemoteContrl = sharepreference.getString("open_remote_control", "false");
                    if("true".equals(isOpenRemoteContrl)){
                        String ip = getDeviceIpAddress();
                        if(ip!=null){
                            remoteControlIp.setText(ip);
                        }
                    }
               // }             
                 
		    }


            if(WindowManagerPolicy.ACTION_HDMI_HW_PLUGGED.equals(action)){
                boolean plugged = intent.getBooleanExtra(WindowManagerPolicy.EXTRA_HDMI_HW_PLUGGED_STATE, false); 
                if(plugged){
                    Log.d(TAG,"===== himi plugged");
                    secreen_auto.setFocusable(true);
                    secreen_auto.setClickable(true);
                    auto_set_screen.setTextColor(Color.WHITE);
                    SharedPreferences sharepreference = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,Context.MODE_PRIVATE);
                    String isAutoSelectOutMode = sharepreference.getString("auto_output_mode", "true");
                    if(isAutoSelectOutMode.equals("false")){
                        screen_self_set.setFocusable(true);
                        screen_self_set.setClickable(true);
                        self_select_mode.setTextColor(Color.WHITE);
                        current_mode_value.setTextColor(Color.WHITE);
                    }else{
                        screen_self_set.setFocusable(false);
                        screen_self_set.setClickable(false);
                        self_select_mode.setTextColor(Color.GRAY);
                        current_mode_value.setTextColor(Color.GRAY);
                    }
                    cvbs_screen_self_set.setVisibility(View.GONE);
                    current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,"hdmi")); 
                }else{
                    Log.d(TAG,"===== himi unplugged");

                    cvbs_screen_self_set.setVisibility(View.VISIBLE);
                    secreen_auto.setFocusable(false);
                    secreen_auto.setClickable(false);
                    screen_self_set.setFocusable(false);
                    screen_self_set.setClickable(false);
                    self_select_mode.setTextColor(Color.GRAY);
                    current_mode_value.setTextColor(Color.GRAY);
                    auto_set_screen.setTextColor(Color.GRAY);
                    cvbs_current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,"cvbs"));
                    auto_set_screen.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.off, 0);       
                }
            }

		}
	}
	


	private void openOutPutModePopupWindow(final String mode) {
        
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View outPutView = (View) mLayoutInflater.inflate(R.layout.out_mode_popup_window, null, true);

		ListView listview = (ListView) outPutView.findViewById(R.id.output_list);
		final OutPutModeManager output = new OutPutModeManager(mContext,listview,mode);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int index, long id) {
				output.selectItem(index);
                if(mode.equals("hdmi")){
                    current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,mode));
                }
                else{
                    cvbs_current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,mode));
                }
			}
		});

		popupWindow = new PopupWindow(outPutView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAtLocation(outPutView, Gravity.CENTER, 0, 0);
		popupWindow.update();

	}

	private void openVoicePopupWindow() {
        
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final View voicePopupView = (View) mLayoutInflater.inflate(R.layout.voice_popup_window, null, true);
		upDateDigitaVoiceUi(voicePopupView);

		RelativeLayout pcm = (RelativeLayout) voicePopupView.findViewById(R.id.voice_pcm);
		pcm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDigitalVoiceValue(string_pcm);
				upDateDigitaVoiceUi(voicePopupView);

			}
		});
        
		RelativeLayout voice_sddif = (RelativeLayout) voicePopupView.findViewById(R.id.voice_sddif);
		voice_sddif.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				setDigitalVoiceValue(string_spdif);
				upDateDigitaVoiceUi(voicePopupView);
			}
		});
        
		RelativeLayout voice_hdmi = (RelativeLayout) voicePopupView.findViewById(R.id.voice_hdmi);
		voice_hdmi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDigitalVoiceValue(string_hdmi);
				upDateDigitaVoiceUi(voicePopupView);
			}
		});

		PopupWindow voicePopupWindow = new PopupWindow(voicePopupView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		voicePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		voicePopupWindow.showAtLocation(voicePopupView, Gravity.CENTER, 0, 0);
		voicePopupWindow.update();

	}

	void upDateDigitaVoiceUi(View voicePopupView) {
		ImageView imageview_pcm = (ImageView) voicePopupView.findViewById(R.id.imageview_pcm);
		ImageView imageview_sddif = (ImageView) voicePopupView.findViewById(R.id.imageview_sddif);
		ImageView imageview_hdmi = (ImageView) voicePopupView.findViewById(R.id.imageview_hdmi);
		String value = sw.getProperty("ubootenv.var.digitaudiooutput");
		if ("PCM".equals(value)) {
			imageview_pcm.setBackgroundResource(R.drawable.current_select);
			imageview_sddif.setBackgroundResource(R.drawable.current_unselect);
			imageview_hdmi.setBackgroundResource(R.drawable.current_unselect);
		} else if ("SPDIF passthrough".equals(value)) {
			imageview_pcm.setBackgroundResource(R.drawable.current_unselect);
			imageview_sddif.setBackgroundResource(R.drawable.current_select);
			imageview_hdmi.setBackgroundResource(R.drawable.current_unselect);
		} else if ("HDMI passthrough".equals(value)) {
			imageview_pcm.setBackgroundResource(R.drawable.current_unselect);
			imageview_sddif.setBackgroundResource(R.drawable.current_unselect);
			imageview_hdmi.setBackgroundResource(R.drawable.current_select);
		} else {
			imageview_pcm.setBackgroundResource(R.drawable.current_select);
			imageview_sddif.setBackgroundResource(R.drawable.current_unselect);
			imageview_hdmi.setBackgroundResource(R.drawable.current_unselect);
			setDigitalVoiceValue("PCM");
		}

	}

	void setDigitalVoiceValue(String value) {
		// value : "PCM" ,"RAW","SPDIF passthrough","HDMI passthrough"
		sw.setProperty("ubootenv.var.digitaudiooutput", value);
		if ("PCM".equals(value)) {
			sw.writeSysfs("/sys/class/audiodsp/digital_raw", "0");
		} else if ("RAW".equals(value)) {
			sw.writeSysfs("/sys/class/audiodsp/digital_raw", "1");
		} else if ("SPDIF passthrough".equals(value)) {
			sw.writeSysfs("/sys/class/audiodsp/digital_raw", "1");
		} else if ("HDMI passthrough".equals(value)) {
			sw.writeSysfs("/sys/class/audiodsp/digital_raw", "2");
		}

	}

	private void openCECPopupWindow() {
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final View cecView = (View) mLayoutInflater.inflate(
				R.layout.cec_popup_window, null, true);

		popupWindow = new PopupWindow(cecView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// popupWindow.setAnimationStyle();
		popupWindow.showAtLocation(cecView, Gravity.CENTER, 0, 0);
		popupWindow.update();

		upDateCecControlUi(cecView);

	}

	void upDateCecControlUi(final View view) {

		imageview_cec_main = (ImageView) view
				.findViewById(R.id.imageview_cec_main);
		imageview_cec_play = (ImageView) view
				.findViewById(R.id.imageview_cec_play);
		imageview_cec_power = (ImageView) view
				.findViewById(R.id.imageview_cec_power);
		imageview_cec_language = (ImageView) view
				.findViewById(R.id.imageview_cec_language);

		cec_main = (RelativeLayout) view.findViewById(R.id.cec_main);
		cec_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCecMainSwitch(0);
			}
		});
		cec_play = (RelativeLayout) view.findViewById(R.id.cec_play);

		cec_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCecMainSwitch(1);
			}
		});

		cec_power = (RelativeLayout) view.findViewById(R.id.cec_power);
		cec_power.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCecMainSwitch(2);
			}
		});
		cec_language = (RelativeLayout) view.findViewById(R.id.cec_language);
		cec_language.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCecMainSwitch(3);
			}
		});

		upDateCecMainUi(0);
		upDateCecMainUi(1);
		upDateCecMainUi(2);
		upDateCecMainUi(3);

	}

	void setCecMainSwitch(int index) {

		if (index == 0) {
			String isCecOpen = sharepreference.getString("cec_open", "false");
			Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE).edit();
			if (isCecOpen.equals("true")) {

				editor.putString("cec_open", "false");
				editor.putString("cec_play_open", "false");
				editor.putString("cec_power_open", "false");
				editor.putString("cec_language_open", "false");
				editor.commit();

			} else {
				editor.putString("cec_open", "true");
				editor.putString("cec_play_open", "true");
				editor.putString("cec_power_open", "true");
				editor.putString("cec_language_open", "true");
				editor.commit();
				if (!isMyServiceRunning()) {
					Intent serviceIntent = new Intent(mContext,CecCheckingService.class);
					serviceIntent.setAction("CEC_LANGUAGE_AUTO_SWITCH");
					mContext.startService(serviceIntent);
				}

			}
			upDateCecMainUi(0);

		} else if (index == 1) {
			String isCecPlayOpen = sharepreference.getString("cec_play_open","false");
			Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE).edit();

			if (isCecPlayOpen.equals("true")) {
				editor.putString("cec_play_open", "false");
				editor.commit();
			} else {
				editor.putString("cec_play_open", "true");
				editor.commit();
			}
			upDateCecMainUi(1);

		} else if (index == 2) {
			String isCecPowerOpen = sharepreference.getString("cec_power_open","false");
			Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE).edit();
			if (isCecPowerOpen.equals("true")) {
				editor.putString("cec_power_open", "false");
				editor.commit();
			} else {
				editor.putString("cec_power_open", "true");
				editor.commit();
			}
			upDateCecMainUi(2);

		} else if (index == 3) {
			String isCecLanguageOpen = sharepreference.getString("cec_language_open", "false");
			Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE).edit();
			if (isCecLanguageOpen.equals("true")) {
				editor.putString("cec_language_open", "false");
				editor.commit();
			} else {
				editor.putString("cec_language_open", "true");
				editor.commit();

				if (!isMyServiceRunning()) {
					Intent serviceIntent = new Intent(mContext,CecCheckingService.class);
					serviceIntent.setAction("CEC_LANGUAGE_AUTO_SWITCH");
					mContext.startService(serviceIntent);
				}
			}
			upDateCecMainUi(3);
		}

	}

	public boolean isMyServiceRunning() {

		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.aml.settingsmbox.CecCheckingService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	void writeSysFile(final String file, final String value) {
		synchronized (file) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Log.d(TAG, "===== write file : " + file + "  value : "+ value);
					sw.writeSysfs(file, value);
				}
			}).start();

		}

	}

	void upDateCecMainUi(int index) {

		if (index == 0) {
			String isCecOpen = sharepreference.getString("cec_open", "false");
			if (isCecOpen.equals("true")) {
				imageview_cec_main.setBackgroundResource(R.drawable.on);
				imageview_cec_play.setBackgroundResource(R.drawable.on);
				imageview_cec_power.setBackgroundResource(R.drawable.on);
				imageview_cec_language.setBackgroundResource(R.drawable.on);
				
				cec_play.setClickable(true);
				
				cec_power.setClickable(true);

				cec_language.setClickable(true);
				
				setCecSysfsValue(0, true);

			} else {
				imageview_cec_main.setBackgroundResource(R.drawable.off);
				imageview_cec_play.setBackgroundResource(R.drawable.off);
				imageview_cec_power.setBackgroundResource(R.drawable.off);
				imageview_cec_language.setBackgroundResource(R.drawable.off);				
				cec_play.setClickable(false);				
				cec_power.setClickable(false);
				cec_language.setClickable(false);				
				setCecSysfsValue(0, false);
			}

		} else if (index == 1) {
			String isCecPlayOpen = sharepreference.getString("cec_play_open","false");
			if (isCecPlayOpen.equals("true")) {
				imageview_cec_play.setBackgroundResource(R.drawable.on);
				setCecSysfsValue(1, true);
			} else {
				imageview_cec_play.setBackgroundResource(R.drawable.off);
				setCecSysfsValue(1, false);
			}

		} else if (index == 2) {
			String isCecPowerOpen = sharepreference.getString("cec_power_open","false");
			if (isCecPowerOpen.equals("true")) {
				imageview_cec_power.setBackgroundResource(R.drawable.on);
				setCecSysfsValue(2, true);

			} else {
				setCecSysfsValue(2, false);
				imageview_cec_power.setBackgroundResource(R.drawable.off);
			}

		} else if (index == 3) {
			String isCecLanguageOpen = sharepreference.getString(
					"cec_language_open", "false");
			if (isCecLanguageOpen.equals("true")) {
				imageview_cec_language.setBackgroundResource(R.drawable.on);

			} else {
				imageview_cec_language.setBackgroundResource(R.drawable.off);
			}

		}

	}

	void setCecSysfsValue(int index, boolean value) {
		String cec_config = sw.getPropertyString(CEC_CONFIG, "cec0");
		String configString = Utils.getBinaryString(cec_config);
		int tmpArray[] = Utils.getBinaryArray(configString);

		if (index != 0) {
			if ("cec0".equals(sw.getProperty(CEC_CONFIG))) {
				return;
			}
		}

		if (index == 0) {
			if (value) {
				sw.setProperty(CEC_CONFIG, "cecf");
				writeSysFile(writeCecConfig, "f");
			} else {
				sw.setProperty(CEC_CONFIG, "cec0");
				writeSysFile(writeCecConfig, "0");
			}

		} else if (index == 1) {
			if (value) {
				tmpArray[2] = 1;
				tmpArray[0] = 1;
			} else {
				tmpArray[2] = 0;
				tmpArray[0] = 1;
			}
			String writeConfig = Utils.arrayToString(tmpArray);
			sw.setProperty(CEC_CONFIG, writeConfig);
			Log.d(TAG, "==== cec set config : " + writeConfig);
			String s = writeConfig.substring(writeConfig.length() - 1,
					writeConfig.length());

			writeSysFile(writeCecConfig, s);

		} else if (index == 2) {
			if (value) {
				tmpArray[1] = 1;
				tmpArray[0] = 1;
			} else {
				tmpArray[1] = 0;
				tmpArray[0] = 1;
			}
			String writeConfig = Utils.arrayToString(tmpArray);
			sw.setProperty(CEC_CONFIG, writeConfig);
			Log.d(TAG, "==== cec set config : " + writeConfig);
			String s = writeConfig.substring(writeConfig.length() - 1,writeConfig.length());
			writeSysFile(writeCecConfig, s);
		}

	}

	void openLocationPopupWindow(final int flag) {
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final View locationView = (View) mLayoutInflater.inflate(R.layout.location_popup_window, null, true);
		popupWindow = new PopupWindow(locationView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, true);
		final ListView listview = (ListView) locationView.findViewById(R.id.provinces_list);
		final LocationManager mLocationManager = new LocationManager(mContext);
		TextView location_title = (TextView) locationView.findViewById(R.id.location_title);
		if (flag == 0) {
			location_title.setText(R.string.provinces);
			mLocationManager.setProvinceView(listview);
		} else if (flag == 1) {
			location_title.setText(R.string.citys);
			mLocationManager.setCityView(listview);
		}

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int index, long id) {
				if (flag == 0) {
					mLocationManager.setCurrentProvinceIndex(index);
					province_view.setText(mLocationManager.getCurrentProvinceName());
					city_view.setText(mLocationManager.getCurrentCityNameNoIndex());
				} else {
					mLocationManager.setCurrentCitysIndex(index);
					city_view.setText(mLocationManager.getCurrentCityNameByIndex());
				}
			}
		});

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// popupWindow.setAnimationStyle();
		popupWindow.showAtLocation(locationView, Gravity.CENTER, 0, 0);
		popupWindow.update();

	}

    public void setViewVisable(SettingsTopView v) {
        
        int id = v.getId();
	    v.getChildAt(0).setScaleX(1.3f);
	    v.getChildAt(0).setScaleY(1.1f);
		((TextView) v.getChildAt(1)).setTextSize(28);
        if(preView!=null){
            preView.getChildAt(0).setScaleX(1f);
            preView.getChildAt(0).setScaleY(1f);
            ((TextView) v.getChildAt(1)).setTextSize(25);
        }
		if (id == R.id.settingsTopView_01) {
			mCurrentContentNum = VIEW_NETWORK;
			settingsContentLayout_02.setVisibility(View.GONE);
			settingsContentLayout_03.setVisibility(View.GONE);
			settingsContentLayout_04.setVisibility(View.GONE);
			settingsContentLayout_01.setVisibility(View.VISIBLE);
            wifiResume();

		} else if (id == R.id.settingsTopView_02) {
			mCurrentContentNum = VIEW_DISPLAY;
			settingsContentLayout_01.setVisibility(View.GONE);
			settingsContentLayout_03.setVisibility(View.GONE);
			settingsContentLayout_04.setVisibility(View.GONE);
			settingsContentLayout_02.setVisibility(View.VISIBLE);

		} else if (id == R.id.settingsTopView_03) {
			mCurrentContentNum = VIEW_MORE;
			settingsContentLayout_01.setVisibility(View.GONE);
			settingsContentLayout_02.setVisibility(View.GONE);
			settingsContentLayout_04.setVisibility(View.GONE);
			settingsContentLayout_03.setVisibility(View.VISIBLE);
            
		} else if (id == R.id.settingsTopView_04) {
			mCurrentContentNum = VIEW_OTHER;
			settingsContentLayout_01.setVisibility(View.GONE);
			settingsContentLayout_03.setVisibility(View.GONE);
			settingsContentLayout_02.setVisibility(View.GONE);
			settingsContentLayout_04.setVisibility(View.VISIBLE);
		}
        preView = v ;
	}

    class MyHandle extends Handler {
		@Override
		public void handleMessage(Message msg) {
            switch(msg.what){
                case UPDATE_AP_LIST :
                        mAccessPointListAdapter.updateAccesspointList();
                        if (mAccessPointListAdapter.getCount() <= 0) {
                            mAcessPointListView.setVisibility(View.GONE);
                            wifi_listview_tip.setVisibility(View.VISIBLE);
                        } else {
                            wifi_listview_tip.setVisibility(View.GONE);
                            mAcessPointListView.setVisibility(View.VISIBLE);
                        }
                    break;
            }
            
			if (msg.what == UPDATE_OUTPUT_MODE_UI) {
				current_mode_value.setText(OutPutModeManager.getCurrentOutPutModeTitle(sw,"hdmi"));
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
        int state = mEthernetManager.getEthState();
        Log.d(TAG,"===== getEthCheckBoxState() , state : " + state);
        if(state == EthernetManager.ETH_STATE_ENABLED){
            return true;
        }
        else{
            return false;
        }
    }

}
