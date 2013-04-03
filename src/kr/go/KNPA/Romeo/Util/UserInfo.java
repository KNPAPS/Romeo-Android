package kr.go.KNPA.Romeo.Util;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

/**
 * Android SharedPreferences에 유저 정보를 저장할 때 Encrypter 클래스를 이용해 정보를 암호화한다.
 */
public class UserInfo {
	
	//! 유저 해쉬 preference key
	public static final String PREF_KEY_USER_HASH = "userHash";//"8f324f72719de0b8215867bbdfa5553e";
	//! 유저 이름 preference key
	public static final String PREF_KEY_USER_NAME = "userName";
	//! 부서 hash preference key
	public static final String PREF_KEY_DEPT_HASH = "deptHash";
	//! 부서 이름 preference key
	public static final String PREF_KEY_DEPT_NAME = "deptName";
	//! 유저 계급 이름 preference key
	public static final String PREF_KEY_RANK_NAME = "userRankName";
	//! 유저 계급 index preference key
	public static final String PREF_KEY_RANK_IDX = "userRankIdx";
	//! 유저 플필사진 경로 preference key
	public static final String PREF_KEY_PIC_PATH = "userPicPath";
	//! 유저 비번 preference key
	public static final String PREF_KEY_PASSWORD = "upw";
	//! 디바이스 uuid preference key
	public static final String PREF_KEY_UUID = "uuid";
	//! GCM Registration id preference key
	public static final String PREF_KEY_REG_ID = "regId";
	//! 유저 직책 preference key
	public static final String PREF_KEY_USER_ROLE = "userRole";
	
	//! preference name
	public static String PREFERENCE_NAME = "userInfo";
	
	/**
	 * @name setters
	 * @{
	 */
	
	/**
	 * preferences에 해당 key로 값을 저장.\n
	 * @param context preferences를 저장할 context
	 * @param key preferences 항목이름.
	 * @param value 값
	 */
	public static void setPref(Context context, String key, String value) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString(key, Encrypter.sharedEncrypter().encryptString(value));
		e.commit();
	}

	/**
	 * preferences에 해당 key로 값을 저장.\n
	 * @param context preferences를 저장할 context
	 * @param key preferences 항목이름.
	 * @param value 값
	 */
	public static void setPref(Context context, String key, Integer value) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.putString(key, Encrypter.sharedEncrypter().encryptString(value.toString()));
		e.commit();
	}
	
	/** @} */
	
	/**
	 * @name getters
	 * @{
	 */
	
	/**
	 * key에 해당하는 pref를 가져온다.\n
	 * SharedPreferences에 저장되는 정보는 기본적으로 모두 Encrypter를 통해서 암호화되므로\n
	 * 모두 자료형이 String으로 다뤄진다. 따라서 getter는 모두 String을 리턴하며\n
	 * 사용할 때 형변환을 해서 사용해야 한다 
	 * @param context preferences를 저장할 context
	 * @param key prefereces 항목 이름
	 * @return
	 */
	public static String getPref(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String enc = prefs.getString(key, null);
		if(enc == null) {
			return null;
		} else {
			return Encrypter.sharedEncrypter().decryptString(enc);
		}
	}
	
	/** @} */
	
	/**
	 * 기기 식별용 uuid를 설정함. deviceid+serial number+androidid를 조합하여 만듬
	 * @param context
	 * @see http://theeye.pe.kr/entry/how-to-get-unique-device-id-on-android
	 * @see http://blog.daum.net/han24_2/3041614 
	 * @return uuid
	 */
	public static String makeUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
	}
	
}
