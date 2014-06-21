package com.mbx.settingsmbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LocationManager {
	private final String TAG = "LocationManager";
	private static final String PREFERENCE_BOX_SETTING = "preference_box_settings";
	private final int PROVINCE = 0;
	private final int CITY = 1;

	private Context mContext = null;

	ArrayList<String> mProvinceList = null;
	ArrayList<String> mCityList = null;

	private int mCurrentProvicesIndex = 0;
	private static String mCurrentProviceName;

	private int mCurrentCityIndex = 0;
	private String mCurrentCityName = null;
	private SoapObject mWeatherInfo = null;
	private final int UPDATE_PRO = 100;
	private final int UPDATE_CITY = 101;
	private final int UPDATE_WEATHER = 102;

	private ListView mListview = null;
	private LocationCityAdapter mCityAdapter = null;
	private LocationProviceAdapter mProvinceAdapter = null;

	public ArrayList<String> province_list = new ArrayList<String>();
	public ArrayList<String> citys = new ArrayList<String>();
	String[][] allcitys;

	public LocationManager(Context context) {
		mContext = context;
		initCitys();
		province_list = getProvincesList();
	}

	void initCitys() {

		String[] city_0 = mContext.getResources().getStringArray(
				R.array.citys_01);

		String[] city_1 = mContext.getResources().getStringArray(
				R.array.citys_02);
		String[] city_2 = mContext.getResources().getStringArray(
				R.array.citys_03);
		String[] city_3 = mContext.getResources().getStringArray(
				R.array.citys_04);
		String[] city_4 = mContext.getResources().getStringArray(
				R.array.citys_05);
		String[] city_5 = mContext.getResources().getStringArray(
				R.array.citys_06);
		String[] city_6 = mContext.getResources().getStringArray(
				R.array.citys_07);
		String[] city_7 = mContext.getResources().getStringArray(
				R.array.citys_08);
		String[] city_8 = mContext.getResources().getStringArray(
				R.array.citys_09);
		String[] city_9 = mContext.getResources().getStringArray(
				R.array.citys_10);
		String[] city_10 = mContext.getResources().getStringArray(
				R.array.citys_11);
		String[] city_11 = mContext.getResources().getStringArray(
				R.array.citys_12);
		String[] city_12 = mContext.getResources().getStringArray(
				R.array.citys_13);
		String[] city_13 = mContext.getResources().getStringArray(
				R.array.citys_14);
		String[] city_14 = mContext.getResources().getStringArray(
				R.array.citys_15);
		String[] city_15 = mContext.getResources().getStringArray(
				R.array.citys_16);
		String[] city_16 = mContext.getResources().getStringArray(
				R.array.citys_17);
		String[] city_17 = mContext.getResources().getStringArray(
				R.array.citys_18);
		String[] city_18 = mContext.getResources().getStringArray(
				R.array.citys_20);
		String[] city_19 = mContext.getResources().getStringArray(
				R.array.citys_21);
		String[] city_20 = mContext.getResources().getStringArray(
				R.array.citys_22);
		String[] city_21 = mContext.getResources().getStringArray(
				R.array.citys_23);
		String[] city_22 = mContext.getResources().getStringArray(
				R.array.citys_24);
		String[] city_23 = mContext.getResources().getStringArray(
				R.array.citys_25);
		String[] city_24 = mContext.getResources().getStringArray(
				R.array.citys_26);
		String[] city_25 = mContext.getResources().getStringArray(
				R.array.citys_27);
		String[] city_26 = mContext.getResources().getStringArray(
				R.array.citys_28);
		String[] city_27 = mContext.getResources().getStringArray(
				R.array.citys_28);
		String[] city_28 = mContext.getResources().getStringArray(
				R.array.citys_29);
		String[] city_29 = mContext.getResources().getStringArray(
				R.array.citys_30);
		String[] city_30 = mContext.getResources().getStringArray(
				R.array.citys_31);
		String[] city_31 = mContext.getResources().getStringArray(
				R.array.citys_32);
		String[] city_32 = mContext.getResources().getStringArray(
				R.array.citys_33);
		String[] city_33 = mContext.getResources().getStringArray(
				R.array.citys_34);
		String[] city_34 = mContext.getResources().getStringArray(
				R.array.citys_35);

		allcitys = new String[35][];
		allcitys[0] = city_0;
		allcitys[1] = city_1;
		allcitys[2] = city_2;
		allcitys[3] = city_3;
		allcitys[4] = city_4;
		allcitys[5] = city_5;
		allcitys[6] = city_6;
		allcitys[7] = city_7;
		allcitys[8] = city_8;
		allcitys[9] = city_9;
		allcitys[10] = city_10;
		allcitys[11] = city_11;
		allcitys[12] = city_12;
		allcitys[13] = city_13;
		allcitys[14] = city_14;
		allcitys[15] = city_15;
		allcitys[16] = city_16;
		allcitys[17] = city_17;
		allcitys[18] = city_18;
		allcitys[19] = city_19;
		allcitys[20] = city_20;
		allcitys[21] = city_21;
		allcitys[22] = city_22;
		allcitys[23] = city_23;
		allcitys[24] = city_24;
		allcitys[25] = city_25;
		allcitys[26] = city_26;
		allcitys[27] = city_27;
		allcitys[28] = city_28;
		allcitys[29] = city_29;
		allcitys[30] = city_30;
		allcitys[31] = city_31;
		allcitys[32] = city_32;
		allcitys[33] = city_33;
		allcitys[34] = city_34;

	}

	public ArrayList<String> getProvincesList() {

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < allcitys.length; i++) {
			list.add(allcitys[i][0]);
		}
		return list;
	}

	public ArrayList<String> getCitysByProvinces(String province) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < province_list.size(); i++) {
			if (province.equals(province_list.get(i))) {

				for (int j = 1; j < allcitys[i].length; j++) {
					list.add(allcitys[i][j]);
				}
				return list;
			}
		}
		return null;

	}

	public String getCurrentProvinceName() {
		SharedPreferences sharedPrefrences = mContext.getSharedPreferences(
				PREFERENCE_BOX_SETTING, Context.MODE_PRIVATE);
		mCurrentProviceName = sharedPrefrences.getString("settings_province",
				province_list.get(0));
		return mCurrentProviceName;
	}

	public String getCurrentCityNameByIndex() {
		mCityList = getCitysByProvinces(getCurrentProvinceName());
		return mCityList.get(mCurrentCityIndex);
	}

	public String getCurrentCityNameNoIndex() {
		mCityList = getCitysByProvinces(getCurrentProvinceName());
		return mCityList.get(0);
	}

	void setSharedPrefrences() {
		Editor editor = mContext.getSharedPreferences(PREFERENCE_BOX_SETTING,
				Context.MODE_PRIVATE).edit();

		editor.putString("settings_province", mCurrentProviceName);
		editor.putString("settings_city", mCurrentCityName);
		editor.commit();

	}

	public void setCurrentProvinceIndex(int index) {
		Log.d(TAG, "====== setCurrentProvinceIndex : " + index);
		mCurrentProvicesIndex = index;
		mCurrentProviceName = province_list.get(mCurrentProvicesIndex);

		mProvinceAdapter.refreshUI();
		mCurrentCityIndex = 0;
		mCityList = getCitysByProvinces(mCurrentProviceName);
		mCurrentCityName = mCityList.get(mCurrentCityIndex);
		setSharedPrefrences();
        new WeatherBroadcastThread(mContext).start();

	}

	public void setCurrentCitysIndex(int index) {
		mCurrentCityIndex = index;
		mCityList = getCitysByProvinces(getCurrentProvinceName());
		mCurrentCityName = mCityList.get(mCurrentCityIndex);
		mCityAdapter.refreshUI();
		setSharedPrefrences();
		new WeatherBroadcastThread(mContext).start();

	}

	public void setProvinceView(ListView listview) {
		mListview = listview;
		mProvinceAdapter = new LocationProviceAdapter();
		mListview.setAdapter(mProvinceAdapter);
		mProvinceAdapter.refreshUI();
		// new getLocationThread().start();

	}

	public void setCityView(ListView listview) {
		mListview = listview;
		mCityAdapter = new LocationCityAdapter();
		mListview.setAdapter(mCityAdapter);
		mCurrentProviceName = getCurrentProvinceName();

		Log.d(TAG, "=====mCurrentProviceName = " + mCurrentProviceName);
		mCityList = getCitysByProvinces(mCurrentProviceName);
		if (mCityList != null)
			Log.d(TAG, "===== mCityList.szie() is :" + mCityList.size());
		else
			Log.d(TAG, "===== mCityList is null !!!");
		mCityAdapter.refreshUI();

	}

	public ArrayList<String> getCityListByProvince() {
		mCityList = (ArrayList<String>) WebServiceUtil
				.getCityListByProvince(mCurrentProviceName);
		return mCityList;
	}

	class LocationCityAdapter extends BaseAdapter {

		void refreshUI() {
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (mCityList != null) {
				return mCityList.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int index) {
			if (mCityList != null) {
				return mCityList.get(index);
			}

			return null;
		}

		@Override
		public long getItemId(int id) {

			return id;
		}

		class CityHolder {
			TextView name;
			ImageView icon;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {

			CityHolder holder = new CityHolder();

			LayoutInflater listContainer;

			listContainer = LayoutInflater.from(mContext);

			convertView = listContainer.inflate(R.layout.location_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.title);
			holder.name.setText(getItem(index).toString());

			holder.icon = (ImageView) convertView.findViewById(R.id.check);
			if (mCurrentCityIndex == index) {
				holder.icon.setBackgroundResource(R.drawable.current_select);
			} else {
				holder.icon.setBackgroundResource(R.drawable.current_unselect);
			}

			return convertView;
		}

	}

	class LocationProviceAdapter extends BaseAdapter {

		void refreshUI() {
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return allcitys.length;
		}

		@Override
		public Object getItem(int index) {

			return allcitys[index][0];
		}

		@Override
		public long getItemId(int id) {

			return id;
		}

		class LocationHolder {
			TextView name;
			ImageView icon;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {

			LocationHolder holder = new LocationHolder();

			LayoutInflater listContainer;

			listContainer = LayoutInflater.from(mContext);

			convertView = listContainer.inflate(R.layout.location_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.title);
			holder.name.setText(getItem(index).toString());

			holder.icon = (ImageView) convertView.findViewById(R.id.check);
			if (mCurrentProvicesIndex == index) {
				holder.icon.setBackgroundResource(R.drawable.current_select);
			} else {
				holder.icon.setBackgroundResource(R.drawable.current_unselect);
			}

			return convertView;
		}

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_PRO:

				break;
			case UPDATE_CITY:

				break;
			case UPDATE_WEATHER:

				break;
			default:
				break;
			}
		}
	};

}
