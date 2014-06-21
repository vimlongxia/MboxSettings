package com.mbx.settingsmbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AccessPointListAdapter extends BaseAdapter {
	private final static String TAG = "AccessPointListAdapter ";
	private final boolean DEBUG = true;
	Context mContext;
	private LayoutInflater mInflater;

	public final static int[] wifi_level_drawable = { R.drawable.wifi_level_01,
			R.drawable.wifi_level_02, R.drawable.wifi_level_03,
			R.drawable.wifi_level_04, R.drawable.wifi_level_05 };

	WifiManager mWifiManager = null;
	List<WifiConfiguration> mWifiConfigList = null;

	WifiInfo mWifiInfo;
	List<ScanResult> mScanResultList;
	WifiUtils mWifiUtils = null;

	private static int currentSelectIndex = -1;
	private static int currentConnectedIndex = -1;
    private static String currentConnectedName = null;
	int wifiState = -1;

	public AccessPointListAdapter(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mScanResultList = new ArrayList<ScanResult>();
		mWifiUtils = new WifiUtils(mContext);
	}

	public void connect2OpenAccessPoint() {
		ScanResult currentAccessPoint = mScanResultList.get(currentSelectIndex);
		mWifiUtils.connect2AccessPoint(currentAccessPoint, null);
	}

	public void connect2AccessPoint(String password) {
		ScanResult currentAccessPoint = mScanResultList.get(currentSelectIndex);
		mWifiUtils.connect2AccessPoint(currentAccessPoint, password);
	}

	public void startScanApcessPoint() {
		mWifiUtils.startScan();
	}

	public void updateAccesspointList() {
		mScanResultList = mWifiUtils.getWifiAccessPointList();
        notifyDataSetChanged();
		Log.d(TAG, "update Accesspoint List now ,the size is : " + mScanResultList.size());
	}

	class sortByLevel implements Comparator<ScanResult> {
		public int compare(ScanResult obj1, ScanResult obj2) {
			if (obj1.level > obj2.level)
				return 1;
			else
				return 0;
		}
	}

	private class AccessPointItemHolder {

		ImageView wifi_selected;
		ImageView wifi_arrow;
		ImageView wifi_level;
		ImageView wifi_lock;
		TextView wifi_ap_name;
	}

	@Override
	public int getCount() {
		if (mScanResultList == null) {
			return 0;
		}
		return mScanResultList.size();
	}

	@Override
	public Object getItem(int index) {
		if (mScanResultList != null) {
			return mScanResultList.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

    public void setCurrentConnectedItemBySsid(String ssid){
        int length = ssid.length();
        String name = ssid.substring(1, length -1);
        if(mScanResultList!= null && mScanResultList.size() > 0){ 
            for(int i=0 ;i< mScanResultList.size() ; i++){
				Log.d(TAG,"==== SSID is : " + mScanResultList.get(i).SSID.trim());
				if (mScanResultList.get(i).SSID.trim().equals(name)) {
                	currentConnectedIndex = i ;
                    notifyDataSetChanged();
                    return ;
                }
            }
        }
    }

    public void setCurrentConnectItemSSID(String name){
        currentConnectedName = name;
    }
    
	public void setCurrentSelectItem(int index) {
		currentSelectIndex = index;
		notifyDataSetChanged();
	}

	public WifiInfo getCurrentWifiInfo() {
		return mWifiUtils.getCurrentWifiInfo();
	}

	public int getCurrentAccessPointSecurityType(int index) {
		return mWifiUtils.getSecurityType(mScanResultList.get(index));
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {

		AccessPointItemHolder holder;
		convertView = mInflater.inflate(R.layout.accesspoint_item, null);
		holder = new AccessPointItemHolder();
		holder.wifi_selected = (ImageView) convertView.findViewById(R.id.itemImage_01);
		holder.wifi_arrow = (ImageView) convertView.findViewById(R.id.itemImage_02);
		holder.wifi_lock = (ImageView) convertView.findViewById(R.id.itemImage_04);
		holder.wifi_level = (ImageView) convertView.findViewById(R.id.itemImage_03);
        
        if(currentConnectedName != null){
            int length = currentConnectedName.length();
            String name = currentConnectedName.substring(1, length -1);
            if(mScanResultList.get(index).SSID.trim().equals(name)){
                holder.wifi_selected.setVisibility(View.VISIBLE);
                Log.d(TAG,"===== connected to : " + currentConnectedName);
            }else{
                holder.wifi_selected.setVisibility(View.INVISIBLE);
            }
        }
        
		if (currentSelectIndex == index) {
			convertView.setBackgroundColor(Color.GRAY);
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		int level = mWifiUtils.getWifiLevel(mScanResultList.get(index));
		holder.wifi_level.setBackgroundResource(wifi_level_drawable[calculate(level)]);

		if (getCurrentAccessPointSecurityType(index) == 0|| getCurrentAccessPointSecurityType(index) == 4) {
			holder.wifi_arrow.setVisibility(View.INVISIBLE);
			holder.wifi_lock.setVisibility(View.INVISIBLE);
		} else {
			holder.wifi_arrow.setVisibility(View.VISIBLE);
			holder.wifi_lock.setVisibility(View.VISIBLE);
		}

		holder.wifi_ap_name = (TextView) convertView.findViewById(R.id.apName);
		convertView.setTag(holder);
		holder.wifi_ap_name.setText(mScanResultList.get(index).SSID);
		return convertView;
	}

	public int getWifiLevelDrawableId(int level) {
		return wifi_level_drawable[calculate(level)];
	}

	private int calculate(int level) {
		if (level > -50) {
			return 4;
		} else if (level > -60) {
			return 3;
		} else if (level > -70) {
			return 2;
		} else if (level > -80) {
			return 1;
		} else {
			return 0;
		}
	}
}