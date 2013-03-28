package kr.go.KNPA.Romeo.Util;

import java.util.UUID;

import kr.go.KNPA.Romeo.Member.User;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

/**
 * Android SharedPreferences 클래스를 이용하여 유저 정보를 저장한다
 */
public class UserInfo {
	
	public static String PREFERENCE_NAME = "userInfo";
	
	/**
	 * set user's name
	 * @param context
	 * @param name
	 */
	public static void setName(Context context, String name) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("name", Encrypter.sharedEncrypter().encryptString(name));
		e.commit();
	}
	
	/**
	 * get user's name
	 * @param context
	 * @return
	 */
	public static String getName(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("name", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	/**
	 * 부서 이름 설정
	 * @param context
	 * @param department
	 */
	public static void setDepartment(Context context, String department) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("department", Encrypter.sharedEncrypter().encryptString(department));
		e.commit();
		
	}
	
	/**
	 * 부서 이름 가져오기
	 * @param context
	 * @return
	 */
	public static String getDepartment(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("department", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	/**
	 * 부서 hash 설정
	 * @param context
	 * @param idx 부서 hash
	 */
	public static void setDepartmentIdx(Context context, String idx) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("departmentIdx", Encrypter.sharedEncrypter().encryptString(""+idx));
		e.commit();
	}
	
	/**
	 * 부서 hash 가져오기
	 * @param context
	 * @return 부서 hash
	 */
	public static String getDepartmentIdx(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String _var = Encrypter.sharedEncrypter().decryptString(prefs.getString("departmentIdx", Encrypter.sharedEncrypter().encryptString(""+User.NOT_SPECIFIED)));
		return _var;
	}
	
	/**
	 * 유저 hash 설정
	 * @param context
	 * @param idx 유저 hash
	 */
	public static void setUserIdx(Context context, String idx) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("userIdx", Encrypter.sharedEncrypter().encryptString(""+idx));
		e.commit();
	}
	
	/**
	 * 유저 hash 가져오기
	 * @param context
	 * @return 유저 hash
	 */
	public static String getUserIdx(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String _var = Encrypter.sharedEncrypter().decryptString(prefs.getString("userIdx", Encrypter.sharedEncrypter().encryptString(""+User.NOT_SPECIFIED)));
		return _var; 
	}
	
	/**
	 * 유저의 계급정보를 저장. user_rank는 int로 받음
	 * @param context
	 * @param rank
	 */
	public static void setRankIdx(Context context, int rank) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("rankIdx", Encrypter.sharedEncrypter().encryptString(""+rank));
		e.commit();
	}
	
	/**
	 * 유저의 계급 정보가 담긴 int를 리턴
	 * @param context
	 * @param rank
	 * @return user_rank
	 */
	public static int getRankIdx(Context context, int rank) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String _var = Encrypter.sharedEncrypter().decryptString(prefs.getString("rankIdx", Encrypter.sharedEncrypter().encryptString(""+User.NOT_SPECIFIED)));
		return Integer.parseInt(_var);
	}
	
	/**
	 * 유저의 계급 이름 set
	 * @param context
	 * @param rank
	 */
	public static void setRank(Context context, String rank) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("rank", Encrypter.sharedEncrypter().encryptString(rank));
		e.commit();
	}
	
	/**
	 * 유저의 계급 이름 get
	 * @param context
	 * @return 계급 이름
	 */
	public static String getRank(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("rank", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	/**
	 * 유저의 프로필 사진 경로를 저장
	 * @param context
	 * @param path url
	 */
	public static void setPicPath(Context context, String path) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("picPath", Encrypter.sharedEncrypter().encryptString(path));
		e.commit();
	}
	
	/**
	 * 유저의 프로필 사진 경로를 가져옴
	 * @param context
	 * @return url
	 */
	public static String getPicPath(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("picPath", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	/**
	 * 유저의 비밀번호를 저장
	 * @param context
	 * @param password
	 */
	public static void setPassword(Context context, String password) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("password", Encrypter.sharedEncrypter().encryptString(password));
		e.commit();
	}
	
	/**
	 * 유저의 비밀번호를 가져옴
	 * @param context
	 * @return 비밀번호
	 */
	public static String getPassword(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("password", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	/**
	 * 기기 식별용 uuid를 설정함. deviceid+serial number+androidid를 조합하여 만듬
	 * @param context
	 */
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
	
	/**
	 * 기기 식별용 uuid를 가져온다
	 * @param context
	 * @return uuid
	 */
	public static String getUUID(Context context) {			
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("uuid", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
	
	/**
	 * GCM registration key를 설정한다.
	 * @param context
	 * @param regid
	 */
	public static void setRegid(Context context, String regid) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString("regid", Encrypter.sharedEncrypter().encryptString(regid));
		e.commit();
	}
	
	/**
	 * GCM registration key를 가져온다.
	 * @param context
	 * @return
	 */
	public static String getRegid(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString("regid", null);
		if(enc == null) return null;
		return Encrypter.sharedEncrypter().decryptString(enc);
	}
}
