package kr.go.KNPA.Romeo.GCM;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gcm.GCMRegistrar;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Connection;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.util.Log;

public class GCMRegisterManager {
	private static final String tag = "GCMRegisterManager";
    
	private static GCMRegisterManager _sharedManager = null;
	//public boolean isRegistered = false;
	
	
	public GCMRegisterManager() {
	}
	
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

		
		   /*
		   	private void registerGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		if("".equals(regId) )   //구글 가이드에는 regId.equals("")로 되어 있는데 Exception을 피하기 위해 수정
		    GCMRegistrar.register(this, "44570658441");
		else {
		      Log.d("============== Already Registered ==============", regId);
		}
		
		}
		    */
	}
	
	public void onError(Context context, String errorId) {			/**에러 발생시*/
        Log.d(tag, "on_error. errorId : "+errorId);
    	// 다시시작하도록 유도.
    	// 문제가 자꾸 발생할 시 재설치를 유도.
    	//alert("오류가 발생했습니다. 다시 시도해주시기 바랍니다. 문제가 반복되어 발생하는 경우 재설치해주시기 바랍니다.");
    }

	public void onRegistered(Context context, String regId) {		 /**단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다*/

		Log.d(tag, "onRegistered. regId : "+regId);
        
		// 다음의 과정은 Server단(ACTION : INSERT)에서 알아서 이루어지므로, UUID값과 REGID값만 찾아서 잘 넘겨준다.
		// DB상에 UUID 값과 regid 값, id값을 찾는다		
			// 등록된 regid값과 DB상의 regid 값이 같으면 pass
			// 등록된 regid값과 DB상의 regid 값이 다르면 UPDATE
		// DB상에 해당 기기의 UUID 값이 없으면 INSERT
        
        UserInfo.setRegid(context, regId);
        String uuid = UserInfo.getUUID(context);
        if(uuid == null) {
        	UserInfo.setUUID(context);
        	uuid = UserInfo.getUUID(context);
        }
        long userIdx = UserInfo.getUserIdx(context);
        
        HashMap<String, String> data = new HashMap<String,String>();
        data.put("regid", regId);
        data.put("uuid", uuid);
        data.put("user", ""+userIdx);
        data.put("type", "a");
        
        Connection con = new Connection.Builder()
        							   .url(Connection.HOST_URL + "/device/register")
        							   .dataType(Connection.DATATYPE_JSON)
        							   .type(Connection.TYPE_POST)
        							   .data(data)
        							   .build();
        int responseCode = con.request();
        String response= null;
        if(responseCode == HttpURLConnection.HTTP_OK) {
        	response = con.response;
        	// SUCCESS : TODO deal with response;
        } else {
        	// ERROR : 
        }
      
    }
	
	public void onUnregistered(Context context, String regId) {		/**단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다*/
        Log.d(tag, "onUnregistered. regId : "+regId);
        
        
    	// 다음의 과정은 Server단(ACTION : DELETE)에서 알아서 이루어지므로, REGID값만 ARRAY Type으로 찾아서 잘 넘겨준다.
		// DB상에서 해당 regid값을 찾는다.
		// 존재한다면 DELETE
		// 없으면.... PASS
        
        String uuid = UserInfo.getUUID(MainActivity.sharedActivity());
        HashMap<String, Object> data = new HashMap<String,Object>();
        
        ArrayList<String> regidids = new ArrayList<String>();
        regidids.add(regId);
        
        data.put("regidids", regidids);
        data.put("uuid", uuid);
        //data.put("action", "delete"); => method unregister
        
        Connection con = new Connection.Builder()
        							   .url(Connection.HOST_URL + "/device/unregister")
        							   .dataType(Connection.DATATYPE_JSON)
        							   .type(Connection.TYPE_POST)
        							   .data(data)
        							   .build();
        int responseCode = con.request();
        String response= null;
        if(responseCode == HttpURLConnection.HTTP_OK) {
        	response = con.response;
        	// SUCCESS : TODO deal with response;
        } else {
        	// ERROR : 
        }

    }

}
