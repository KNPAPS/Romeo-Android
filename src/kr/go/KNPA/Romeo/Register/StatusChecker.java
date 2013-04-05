package kr.go.KNPA.Romeo.Register;

import java.util.HashMap;

import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 애플리케이션을 구동하기 위해 요구되는 상태를 체크하는 객체
 * @since 2013.4.2
 */
public class StatusChecker {
	public static final int USER_NOT_REGISTERED = 0;
	public static final int USER_REGISTERED_NOT_ENABLED = 1;
	public static final int USER_REGISTERED_ENABLED = 2;
	
	public static final int DEVICE_NOT_REGISTERED = 0;
	public static final int DEVICE_REGISTERED_NOT_ENABLED = 1;
	public static final int DEVICE_REGISTERED_ENABLED = 2;	
	private Context context;
	
	public StatusChecker( Context context ) {
		this.context = context;
	}
	
	/**
	 * 사용자의 상태를 int 값으로 구별하여 리턴함\n
	 * @b 사용자상태\n
	 * 등록안됨 		: USER_NOT_REGISTERED = 0\n
	 * 등록됨,인증안됨	: USER_REGISTERED_NOT_ENABLED = 1\n
	 * 등록됨,인증됨	: USER_REGISTERED_ENABLED = 2\n
	 * @return int 사용자 상태 
	 */
	public int getUserStatus() {
		//기기에 유저의 기존 정보가 등록되어 있지 않으면 바로 unregistered 창으로 돌린다.
		String userIdx = UserInfo.getUserIdx(context);
		if ( userIdx == null ) {
			return USER_NOT_REGISTERED;
		}
		
		//기존 정보가 등록되어 있다면 서버에 유저 해쉬를 보내 유저 정보를 가져온 후 활성화여부를 판단한다.
		
		/** 데이터 가져오기 시작 */
		HashMap<String,Object> hm = new HashMap<String,Object>();
		hm.put(Data.KEY_USER_HASH, userIdx);
		
		Data data = new Data();
		data.add(hm);
		
		Payload request = new Payload(Event.User.getUserInfo());
		request.setData(data);
		
		Connection conn = new Connection().requestPayloadJSON(request.toJSON()).request();
		Payload responsePayload = conn.getResponsePayload();
		/** 데이터 가져오기 끝 */
		
		
		/**
		 * 서버에 등록되어 있다면 상태코드를 SUCCESS로 리턴할 것이고
		 * 그 때 is_enabled = 1 이면 정상적으로 등록되고 활성화된 것.
		 * is_enabled=0 이면 등록은 되었지만 활성화는 안된것
		 * 
		 * 만약 서버에서 리턴한 상태코드가 success가 아니라면
		 * 서버에 등록조차 안되어 있는 상태임
		 */
		if ( responsePayload.getStatusCode() == StatusCode.SUCCESS ) {
			int status = (Integer) responsePayload.getData().get(0).get(Data.KEY_IS_ENABLED);
			return status == 1 ? USER_REGISTERED_ENABLED : USER_REGISTERED_NOT_ENABLED;
		}else {
			return USER_NOT_REGISTERED;
		}
	}
	
	/**
	 * 기기 상태를 int값으로 구별하여 리턴\n
	 * @b 기기\n
	 * 등록안됨 		: DEVICE_NOT_REGISTERED = 0\n
	 * 등록됨,인증안됨	: DEVICE_REGISTERED_NOT_ENABLED = 1\n
	 * 등록됨,인증됨	: DEVICE_REGISTERED_ENABLED = 2\n
	 * @return int devices status
	 */
	public int getDeviceStatus() { 
		//기기에 유저의 기존 정보가 등록되어 있지 않으면 바로 unregistered 창으로 돌린다.
		String userHash = UserInfo.getUserIdx(context);
		String uuid = UserInfo.getUUID(context);
		String regid = UserInfo.getRegid(context);
		if ( userHash == null || uuid == null || regid == null ) {
			return DEVICE_NOT_REGISTERED;
		}
		
		//기존 정보가 등록되어 있다면 서버에 유저 해쉬를 보내 유저 정보를 가져온 후 활성화여부를 판단한다.
		
		/** 데이터 가져오기 시작 */
		HashMap<String,Object> hm = new HashMap<String,Object>();
		hm.put(Data.KEY_USER_HASH,userHash);
		hm.put(Data.KEY_DEVICE_UUID, uuid);
		hm.put(Data.KEY_DEVICE_REG_ID, regid);
		
		Data data = new Data();
		data.add(hm);
		Payload requestPayload = new Payload(Event.DEVICE_IS_REGISTERED);
		requestPayload.setData(data);
		Connection conn = new Connection().requestPayloadJSON(requestPayload.toJSON());
		conn.request();
		Payload responsePayload = conn.getResponsePayload();
		/** 데이터 가져오기 끝 */
		
		
		/**
		 * 서버에 등록되어 있다면 상태코드를 SUCCESS로 리턴할 것이고
		 * 그 때 is_enabled = 1 이면 정상적으로 등록되고 활성화된 것.
		 * is_enabled=0 이면 등록은 되었지만 활성화는 안된것
		 * 
		 * 만약 서버에서 리턴한 상태코드가 success가 아니라면
		 * 서버에 등록조차 안되어 있는 상태임
		 */
		int isRegistered = (Integer) responsePayload.getData().get(0).get(Data.KEY_DEVICE_IS_REGISTERED);
		int isEnabled = (Integer) responsePayload.getData().get(0).get(Data.KEY_DEVICE_IS_ENABLED);

		if ( isRegistered == 0 ) {
			return DEVICE_NOT_REGISTERED;
		} else if ( isEnabled == 0 ) {
			return DEVICE_REGISTERED_NOT_ENABLED;
		} else {
			return DEVICE_REGISTERED_ENABLED;
		}
	}
	
	/**
	 * 네트워크에 연결되어 있는지 체크
	 * @return
	 */
	public boolean isConnectedToNetwork() {
		ConnectivityManager cManager; 
		NetworkInfo mobile; 
		NetworkInfo wifi; 
		 
		cManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		 
		return mobile.isConnected() || wifi.isConnected();
	}
	
}
