package kr.go.KNPA.Romeo.GCM;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

/**
 * 기기를 GCM 서버에 등록하는 절차를 담당하는 클래스이다.
 * GCMIntentService.onRegister(Context) 등 에서 호출하게 된다.
 */
public class GCMRegisterManager {
	private static final String tag = "GCMRegisterManager";
    
	/**
	 * @name Single-tone
	 * @{ 
	 */
	private static GCMRegisterManager _sharedManager = null;
	public GCMRegisterManager() {}
	public static GCMRegisterManager sharedManager() {
		if(_sharedManager == null) {
			_sharedManager = new GCMRegisterManager();
		}
		return _sharedManager;
	}
	/** @} */

	/**
	 * GCM 서버에 이 단말기를 등록한다.
	 * @param context Context 형의 변수이다. 단말기의 각종 설정을 확인하고, 등록과정에 사용된다.
	 */
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
	
	/**
	 * GCMIntentService.onError(Context, String) 에서 호출하는 스스로와 동일한 메서드이다. \n
	 * 오류 확인 메시지 다이얼로그를 출력하며, 확인 버튼 클릭시 어플리케이션이 종료된다.
	 * @param context
	 * @param errorId
	 */
	public void onError(Context context, String errorId) {			/**에러 발생시*/
        Log.d(tag, "on_error. errorId : "+errorId);
    	// 다시시작하도록 유도.
    	// 문제가 자꾸 발생할 시 재설치를 유도.
        
        AlertDialog alert = new AlertDialog.Builder(context)
        									.setIcon(kr.go.KNPA.Romeo.R.drawable.icon)
        									.setTitle("오류가 발생했습니다.")//context.getString(kr.go.KNPA.Romeo.R.string.)
        									.setMessage("다시 시도해주시기 바랍니다. 문제가 반복되어 발생하는 경우 재설치해주시기 바랍니다.")
        									.setPositiveButton(context.getString(kr.go.KNPA.Romeo.R.string.ok), new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
													android.os.Process.killProcess(android.os.Process.myPid());
												}
											}).show();
    	//alert("오류가 발생했습니다. 다시 시도해주시기 바랍니다. 문제가 반복되어 발생하는 경우 재설치해주시기 바랍니다.");
    }

	/**
	 * GCMIntentService.onRegistered(Context, String) 에서 호출하는 스스로와 동일한 메서드이다. \n
	 * 등록을 시도하고, 실패할 경우 오류메시지를 남기며 어플리케이션이 종료된다.
	 * @param context
	 * @param 단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다
	 */
	public void onRegistered(Context context, String regId) {
		Log.d(tag, "onRegistered. regId : "+regId);
        
		// 다음의 과정은 Server단(ACTION : INSERT)에서 알아서 이루어지므로, UUID값과 REGID값만 찾아서 잘 넘겨준다.
		// DB상에 UUID 값과 regid 값, id값을 찾는다		
			// 등록된 regid값과 DB상의 regid 값이 같으면 pass
			// 등록된 regid값과 DB상의 regid 값이 다르면 UPDATE
		// DB상에 해당 기기의 UUID 값이 없으면 INSERT
        
        String uuid = UserInfo.getUUID(context);
        if(uuid == null) {
        	UserInfo.setUUID(context);
        	uuid = UserInfo.getUUID(context);
        }
        String userIdx = UserInfo.getUserIdx(context);
        
        Data reqData = new Data().add(0, Data.KEY_USER_HASH, userIdx)
        						 .add(0, Data.KEY_DEVICE_UUID, uuid)
        						 .add(0, Data.KEY_DEVICE_REG_ID, regId)
        						 .add(0, Data.KEY_DEVICE_TYPE, "a");
        
        Payload request = new Payload().setEvent(Event.Device.register()).setData(reqData);
        
        final Context ctx = context;
        final String rid = regId;
        CallbackEvent<Payload, Integer, Payload> callback = new CallbackEvent<Payload, Integer, Payload>(){
        	@Override
        	public void onPostExecute(Payload response) {
        		if(response.getStatusCode() == StatusCode.SUCCESS) {
        			UserInfo.setRegid(ctx, rid);
        		} else {
        			AlertDialog alert = new AlertDialog.Builder(ctx)
									.setIcon(kr.go.KNPA.Romeo.R.drawable.icon)
									.setTitle("기기 등록에 실패했습니다.")//context.getString(kr.go.KNPA.Romeo.R.string.)
									.setMessage("다시 시도해주시기 바랍니다. 문제가 반복되어 발생하는 경우 재설치해주시기 바랍니다.")
									.setPositiveButton(ctx.getString(kr.go.KNPA.Romeo.R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					}).show();
        		}
        		
        	}
        };
        Connection conn = new Connection().requestPayloadJSON(request.toJSON()).callBack(callback);
        conn.request();
    }
	
	/**
	 * GCMIntentService.onUnregistered(Context, String) 에서 호출하는 스스로와 동일한 메서드이다.
	 * @param context
	 * @param regId 단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다
	 */
	public void onUnregistered(Context context, String regId) {
        Log.d(tag, "onUnregistered. regId : "+regId);
        
    	// 다음의 과정은 Server단(ACTION : DELETE)에서 알아서 이루어지므로, REGID값만 ARRAY Type으로 찾아서 잘 넘겨준다.
		// DB상에서 해당 regid값을 찾는다.
		// 존재한다면 DELETE
		// 없으면.... PASS
        
        String uuid = UserInfo.getUUID(context);
        String regid = UserInfo.getRegid(context);
        Data reqData = new Data().add(0, Data.KEY_DEVICE_UUID, uuid).add(0, Data.KEY_DEVICE_REG_ID, regid);
        Payload request = new Payload().setEvent(Event.Device.unRegister()).setData(reqData);
        Connection conn = new Connection().requestPayloadJSON(request.toJSON());
        conn.request();
        
        UserInfo.clear(context);
    }

}
