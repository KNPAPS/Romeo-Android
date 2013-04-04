package kr.go.KNPA.Romeo.Util;

import java.util.UUID;

import kr.go.KNPA.Romeo.Member.User;
import android.content.Context;
import android.content.SharedPreferences;
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
	
	public static void setDepartmentIdx(Context context, long idx) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("departmentIdx", Encrypter.sharedEncrypter().encryptString(""+idx));
		e.commit();
	}
	public static long getDepartmentIdx(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String _var = Encrypter.sharedEncrypter().decryptString(prefs.getString("departmentIdx", Encrypter.sharedEncrypter().encryptString(""+User.NOT_SPECIFIED)));
		return Long.parseLong(_var);
	}
	
	public static void setUserIdx(Context context, long idx) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("userIdx", Encrypter.sharedEncrypter().encryptString(""+idx));
		e.commit();
	}
	public static long getUserIdx(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String _var = Encrypter.sharedEncrypter().decryptString(prefs.getString("userIdx", Encrypter.sharedEncrypter().encryptString(""+User.NOT_SPECIFIED)));
		return Long.parseLong(_var); 
	}
	
	public static void setRankIdx(Context context, int rank) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("rankIdx", Encrypter.sharedEncrypter().encryptString(""+rank));
		e.commit();
	}
	public static int getRankIdx(Context context, int rank) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String _var = Encrypter.sharedEncrypter().decryptString(prefs.getString("rankIdx", Encrypter.sharedEncrypter().encryptString(""+User.NOT_SPECIFIED)));
		return Integer.parseInt(_var);
	}
	
	public static void setRank(Context context, String rank) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("rank", Encrypter.sharedEncrypter().encryptString(rank));
		e.commit();
	}
	public static String getRank(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("rank", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
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
	}
	
	public static void setPassword(Context context, String password) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("password", Encrypter.sharedEncrypter().encryptString(password));
		e.commit();
	}
	public static String getPassword(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("password", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
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
}