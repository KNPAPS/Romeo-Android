package kr.go.KNPA.Romeo.GCM;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.Config.EventEnum;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class GCMRegisterManager {
	private static final String DEVICE_TYPE_ANDROID = "a";
	private static final String tag = "GCMRegisterManager";
    
	private static GCMRegisterManager _sharedManager = null;
	
	public static GCMRegisterManager sharedManager() {
		if(_sharedManager == null) {
			_sharedManager = new GCMRegisterManager();
		}
		return _sharedManager;
	}

	public static void registerGCM(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		GCMRegistrar.register(context, "44570658441");
	}
	
	public void onError(Context context, String errorId) {			/**에러 발생시*/
        Log.d(tag, "on_error. errorId : "+errorId);
    	// 다시시작하도록 유도.
    	// 문제가 자꾸 발생할 시 재설치를 유도.
    	//alert("오류가 발생했습니다. 다시 시도해주시기 바랍니다. 문제가 반복되어 발생하는 경우 재설치해주시기 바랍니다.");
    }
	
	/**
	 * 단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다
	 */
	public void onRegistered(Context context, String regId) {		 
		

		Log.d(tag, "onRegistered. regId : "+regId);
        
		// 다음의 과정은 Server단(ACTION : INSERT)에서 알아서 이루어지므로, UUID값과 REGID값만 찾아서 잘 넘겨준다.
		// DB상에 UUID 값과 regid 값, id값을 찾는다		
		// 등록된 regid값과 DB상의 regid 값이 같으면 pass
		// 등록된 regid값과 DB상의 regid 값이 다르면 UPDATE
		// DB상에 해당 기기의 UUID 값이 없으면 INSERT
        String userHash = UserInfo.getPref(context, UserInfo.PREF_KEY_USER_HASH);
        String uuid = UserInfo.makeUUID(context);
        UserInfo.setPref(context, UserInfo.PREF_KEY_REG_ID, regId);
        UserInfo.setPref(context, UserInfo.PREF_KEY_UUID, uuid);
        
        HashMap<String, Object> hm = new HashMap<String,Object>();
        hm.put(Data.KEY_DEVICE_REG_ID, regId);
        hm.put(Data.KEY_DEVICE_UUID, uuid);
        hm.put(Data.KEY_USER_HASH, userHash);
        hm.put(Data.KEY_DEVICE_TYPE, DEVICE_TYPE_ANDROID);
        
        Payload requestPl = new Payload(EventEnum.DEVICE_REGISTER);
        Data requestData = new Data();
        requestData.add(hm);
        
        Connection conn = new Connection.Builder(requestPl.toJson()).build();
        
        conn.request();
        
        //Payload resp = new Payload( conn.getResponsePayload() );
        //TODO response 가지고 추가 작업할 것 있으면 추가
    }
	/**
	 * 단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다
	 */
	public void onUnregistered(Context context, String regId) {		
		
        Log.d(tag, "onUnregistered. regId : "+regId);
        
        
    	// 다음의 과정은 Server단(ACTION : DELETE)에서 알아서 이루어지므로, REGID값만 ARRAY Type으로 찾아서 잘 넘겨준다.
		// DB상에서 해당 regid값을 찾는다.
		// 존재한다면 DELETE
		// 없으면.... PASS
        
        String uuid = UserInfo.getPref(context, UserInfo.PREF_KEY_UUID);
        HashMap<String, Object> hm = new HashMap<String,Object>();
        hm.put(Data.KEY_DEVICE_UUID,uuid);
        hm.put(Data.KEY_DEVICE_REG_ID,regId);
        Data reqData = new Data();
        reqData.add(hm);

        Payload reqPayload = new Payload(EventEnum.DEVICE_UNREGISTER);
        
        Connection con = new Connection.Builder(reqPayload.toJson()).build();
        
        con.request();
        
        //Payload resp = new Payload( conn.getResponsePayload() );
        //TODO response 가지고 추가 작업할 것 있으면 추가
    }

}
