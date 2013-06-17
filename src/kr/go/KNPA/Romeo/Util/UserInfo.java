package kr.go.KNPA.Romeo.Util;

import java.util.UUID;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.VibrationPattern;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class UserInfo {
	public static String PREFERENCE_NAME = "userInfo";
	public static void setName(Context context, String name) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("name", Encrypter.sharedEncrypter().encryptString(name));
		e.commit();
	}
	public static String getName(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("name", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	public static void setDepartment(Context context, String department) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("department", Encrypter.sharedEncrypter().encryptString(department));
		e.commit();
		
	}
	public static String getDepartment(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("department", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	public static void setDepartmentIdx(Context context, String idx) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("departmentIdx", Encrypter.sharedEncrypter().encryptString(idx));
		e.commit();
	}
	public static String getDepartmentIdx(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("departmentIdx", null);
		if(enc == null) return null; 
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	public static void setUserIdx(Context context, String idx) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("userIdx", Encrypter.sharedEncrypter().encryptString(idx));
		e.commit();
	}
	public static String getUserIdx(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("userIdx", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc); 
	}

	public static void setRank(Context context, int rank) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("rank", Encrypter.sharedEncrypter().encryptString(""+rank));
		e.commit();
	}
	public static int getRank(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("rank", null);
		if(enc == null) return Constants.NOT_SPECIFIED;
		
		try {
			return Integer.parseInt( Encrypter.sharedEncrypter().decryptString(enc) );
		} catch (NumberFormatException e) {
			return Constants.NOT_SPECIFIED;
		}
	}
	
	/*
	public static void setPicPath(Context context, String path) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("picPath", Encrypter.sharedEncrypter().encryptString(path));
		e.commit();
	}
	public static String getPicPath(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("picPath", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}*/
	
	public static void setPassword(Context context, String password) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("password", Encrypter.sharedEncrypter().encryptString(password));
		e.commit();
	}
	public static String getPassword(Context context) {
		
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		/*
		String enc = prefs.getString("password", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
		*/
		return prefs.getString("password", null);
	}
	
	public static void setUUID(Context context) {
		// http://blog.daum.net/han24_2/3041614
		// http://theeye.pe.kr/entry/how-to-get-unique-device-id-on-android
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("uuid", Encrypter.sharedEncrypter().encryptString(deviceId));
		e.commit();

	}
	
	public static void setRingtone(Context context, Uri ringtoneUri) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("ringtone", Encrypter.sharedEncrypter().encryptString(ringtoneUri.toString()));
		e.commit();
	}
	
	public static Uri getRingtone(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("ringtone", null);
		if(enc == null) return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//return null;
		return Uri.parse( Encrypter.sharedEncrypter().decryptString(enc) );
	}
	
	public static void setVibrationPattern(Context context, String vibPatternKey) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("vibrationPattern", Encrypter.sharedEncrypter().encryptString( vibPatternKey ));
		e.commit();
	}
	
	public static String getVibrationPattern(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("vibrationPattern", null);
		if(enc == null) return VibrationPattern.DEFAULT;//return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	public static void setAlarmEnabled(Context context, boolean willEnabled) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putBoolean("alarmEnabled", willEnabled);
		e.commit();
	}
	
	public static boolean getAlarmEnabled(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return prefs.getBoolean("alarmEnabled", true);
	}
	
	
	public static void setToastEnabled(Context context, boolean willEnabled) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putBoolean("toastEnabled", willEnabled);
		e.commit();
	}
	
	public static boolean getToastEnabled(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return prefs.getBoolean("toastEnabled", true);
	}
	
	
	public static String getUUID(Context context) {			
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("uuid", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	public static void setRegid(Context context, String regid) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("regid", Encrypter.sharedEncrypter().encryptString(regid));
		e.commit();
	}
	public static String getRegid(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("regid", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	public static void clear(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.clear();
		e.commit();
	}
}
