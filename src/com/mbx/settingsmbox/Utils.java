package com.mbx.settingsmbox;

import android.app.SystemWriteManager;
import android.util.Log;

public class Utils {
	private static final String TAG = "Utils";

	public static String readSysFile(SystemWriteManager sw, String path) {
		if (sw == null) {
			Log.d(TAG, "readSysFile(), sw is null !!");
			return null;
		}

		if (path == null) {
			Log.d(TAG, "readSysFile(), path is null !!");
			return null;
		}

		return sw.readSysfs(path);

	}

	public static void writeSysFile(SystemWriteManager sw, String path,String value) {

		if (sw == null) {
			Log.d(TAG, "writeSysFile(), sw is null !!");
			return;
		}

		if (path == null) {
			Log.d(TAG, "writeSysFile(), path is null !!");
			return;
		}

		if (value == null) {
			Log.d(TAG, "writeSysFile(), value is null !!");
			return;
		}

		sw.writeSysfs(path, value);
	}

	public static boolean getPropertyBoolean(SystemWriteManager sw,String prop, boolean defaultValue) {
		if (sw == null) {
			Log.d(TAG, "getPropertyBoolean(), sw is null !!");
			return defaultValue;
		}

		if (prop == null) {
			Log.d(TAG, "getPropertyBoolean(), path is null !!");
			return defaultValue;
		}

		return sw.getPropertyBoolean(prop, defaultValue);

	}

	public static String getPropertyString(SystemWriteManager sw, String prop,String defaultValue) {
		if (sw == null) {
			Log.d(TAG, "getPropertyString(), sw is null !!");
			return defaultValue;
		}

		if (prop == null) {
			Log.d(TAG, "getPropertyString(), path is null !!");
			return defaultValue;
		}

		return sw.getPropertyString(prop, defaultValue);

	}

	public static int getPropertyInt(SystemWriteManager sw, String prop,int defaultValue) {
		if (sw == null) {
			Log.d(TAG, "getPropertyInt(), sw is null !!");
			return defaultValue;
		}

		if (prop == null) {
			Log.d(TAG, "getPropertyInt(), path is null !!");
			return defaultValue;
		}
		return sw.getPropertyInt(prop, defaultValue);

	}

	public static String getBinaryString(String config) {
		String indexString = "0123456789abcdef";
		String configString = config.substring(config.length() - 1,
				config.length());
		int indexOfConfigNum = indexString.indexOf(configString);
		String ConfigBinary = Integer.toBinaryString(indexOfConfigNum);
		if (ConfigBinary.length() < 4) {
			for (int i = ConfigBinary.length(); i < 4; i++) {
				ConfigBinary = "0" + ConfigBinary;
			}
		}
		return ConfigBinary;
	}
	
	public static int[] getBinaryArray(String binaryString) {
		int[] tmp = new int[4];
		for (int i = 0; i < binaryString.length(); i++) {
			String tmpString = String.valueOf(binaryString.charAt(i));
			tmp[i] = Integer.parseInt(tmpString);
		}
		return tmp;
	}
	
	public static String arrayToString(int[] array) {
		String getIndexString = "0123456789abcdef";
		int total = 0;
		System.out.println();
		for (int i = 0; i < array.length; i++) {
			total = total
					+ (int) (array[i] * Math.pow(2, array.length - i - 1));
		}
		Log.d(TAG, "in arrayToString cecConfig is:" + total);
		String cecConfig = "cec" + getIndexString.charAt(total);
		Log.d(TAG, "in arrayToString cecConfig is:" + cecConfig);
		return cecConfig;
	}

}