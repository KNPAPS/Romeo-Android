package kr.go.KNPA.Romeo.Util;

import java.util.UUID;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Preference {
	// http://blog.daum.net/han24_2/3041614
	// http://theeye.pe.kr/entry/how-to-get-unique-device-id-on-android
	private static Preference _sharedPreference = null;
	private Context context;
	public Preference(Context baseContext) {
		context = baseContext;
	}

	public static Preference initSharedPreference(Context baseContext) {
		if(_sharedPreference == null) {
			_sharedPreference = new Preference(baseContext);
		}
		return _sharedPreference;
	}
	public static Preference sharedPreference() {
		if(_sharedPreference == null) {
			RuntimeException e = new RuntimeException("sharedPreferenceDoesNotExist");
			throw e;
		}
		return _sharedPreference;
	}
	
	public String getUUID() {
		
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		
		return deviceId;
	}
}
