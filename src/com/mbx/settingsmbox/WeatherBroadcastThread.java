package com.mbx.settingsmbox;

import java.util.TimerTask;

import org.ksoap2.serialization.SoapObject;
import android.app.SystemWriteManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;

public class WeatherBroadcastThread extends Thread {

	private final String TAG = "WeatherBroadcastThread";
	private static final String PREFERENCE_BOX_SETTING = "preference_box_settings";
	private Context mContext = null;
    SystemWriteManager sw = null;

	@Override
	public synchronized void start() {
		super.start();
	}

	public WeatherBroadcastThread(Context context) {
		mContext = context;
        sw = (SystemWriteManager) mContext.getSystemService("system_write");		
	}

	@Override
	public void run() {
		synchronized (PREFERENCE_BOX_SETTING) {
			sendWeatherBroadcast();
		}

	}

	void sendWeatherBroadcast() {
      
		SharedPreferences sharedPrefrences = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE);
		String city = sharedPrefrences.getString("settings_city", "***");
		if ("***".equals(city)) {
			String[] defaultProvince = mContext.getResources().getStringArray(R.array.citys_30);
			city = defaultProvince[0];
		}

        boolean isEthConnected = WifiUtils.isEthConnected(mContext);
        boolean isWifiConnected = WifiUtils.isWifiConnected(mContext);
        if(isEthConnected ==false && isWifiConnected == false){
            Log.d(TAG,"==== Wifi and Ethernet both are disconnect ");
            return ;
        }

		SoapObject mWeatherInfo;
		mWeatherInfo = WebServiceUtil.getWeatherByCity(city);

		if (mWeatherInfo == null) {
			try {
				Thread.sleep(30 * 1000);
			} catch (Exception e) {

			}
			mWeatherInfo = WebServiceUtil.getWeatherByCity(city);
		}
		if (mWeatherInfo == null) {
			try {
				Thread.sleep(30 * 1000);
			} catch (Exception e) {

			}
			mWeatherInfo = WebServiceUtil.getWeatherByCity(city);
		}
		if (mWeatherInfo == null) {
			return;
		}
		Log.d(TAG, "============mWeatherInfo: " + mWeatherInfo.toString());

		String temp = null;
		String mTemperatureInfo = null;
		try {
			mTemperatureInfo = mWeatherInfo.getProperty(4).toString();
			Log.d(TAG, " temperature info : " + mWeatherInfo.getProperty(4));
		} catch (Exception e) {
			Log.d(TAG, "Weather web services info : " + mWeatherInfo.toString());
			return;
		}

		if (mTemperatureInfo != null) {
			String t = mTemperatureInfo;
			int start = t.indexOf(mContext.getString(R.string.temperature_start));
			int end = t.indexOf(mContext.getString(R.string.temperature_start));
			if (start + 3 < end)
				temp = t.substring(start + 3, end);
			Log.d(TAG, "===== 001 temp : " + temp);
		} else {
			Log.d(TAG, "===== get temperature error !!!");
		}
		if (temp == null && mTemperatureInfo != null) {
			if (mTemperatureInfo.length() >= 14)
				temp = mTemperatureInfo.substring(10, 13);
			Log.d(TAG, "===== 002 temp : " + temp);
		}
		String icon_01 = null;
		if (mWeatherInfo.getProperty(10) != null) {
			icon_01 = mWeatherInfo.getProperty(10).toString();
		} else {
			Log.d(TAG, "===== get icon error !!!");
		}
		if (temp != null && icon_01 != null && !city.equals("***")) {
			String today_info = city + "," + temp + "," + icon_01;
			Log.d(TAG, "===== today weather info : " + today_info);
			Intent i = new Intent();
			i.setAction("android.amlogic.settings.WEATHER_INFO");
			i.putExtra("weather_today", today_info);
			Log.d(TAG,"===== send broadcast with action : android.amlogic.settings.WEATHER_INFO");
			Log.d(TAG,"===== send broadcast with info , key : weather_today" + " ,value : " + today_info);
			mContext.sendBroadcast(i);
            sw.setProperty("sys.weather.send", "true");
		} else {
			Log.d(TAG, "===== something wrong happen !!!");
		}

	}

}
