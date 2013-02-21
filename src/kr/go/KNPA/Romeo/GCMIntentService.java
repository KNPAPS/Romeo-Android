package kr.go.KNPA.Romeo;
//서비스는 반드시 자신의 기본 패키지에 속해야 하며 이름은 GCMIntentService로 해야 한다.

import kr.go.KNPA.Romeo.GCM.GCMMessageManager;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	//서비스는 GCMBaseIntentService를 상속받아 구현해야 한다.
	private static final String tag = "GCMIntentService";
    private static final String PROJECT_ID = "44570658441";
    //구글 api 페이지 주소 [https://code.google.com/apis/console/#project:긴 번호]
    //#project: 이후의 숫자가 위의 PROJECT_ID 값에 해당한다
    
  //public 기본 생성자를 무조건 만들어야 한다.
	public GCMIntentService() {
		 this(PROJECT_ID);
	}

	public GCMIntentService(String... senderIds) {
		super(senderIds);
	}

	@Override
	protected void onError(Context context, String errorId) {			/**에러 발생시*/
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onError(context, errorId);
    }

	@Override
	protected void onMessage(Context context, Intent intent) {			/** 푸시로 받은 메시지 */
		GCMMessageManager mm = GCMMessageManager.sharedManager();
        mm.onMessage(context, intent);
    }

	@Override
	protected void onRegistered(Context context, String regId) {		 /**단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다*/
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onRegistered(context, regId);
    }

	@Override
	protected void onUnregistered(Context context, String regId) {		/**단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다*/
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onUnregistered(context, regId);
    }

}
