package kr.go.KNPA.Romeo;

import kr.go.KNPA.Romeo.GCM.GCMMessageManager;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * GCM에서 메세지를 받기 위한 Service\n
 * 이 서비스는 반드시 자신의 기본 패키지에 속해야 하며 이름은 GCMIntentService로 해야 한다.\n
 * 이 서비스는 GCMBaseIntentService를 상속받아 구현해야 한다.
 * @author 채호식
 */
public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = "GCMIntentService";
    //! 구글에서 발급받은 project id
    /**
     * 구글 api 페이지 주소 [https://code.google.com/apis/console/#project:긴 번호]
     * #project: 이후의 숫자가 위의 PROJECT_ID 값에 해당한다
     */
    private static final String PROJECT_ID = "44570658441";

    //public 기본 생성자를 무조건 만들어야 한다.
	public GCMIntentService() {
		 this(PROJECT_ID);
	}

	public GCMIntentService(String... senderIds) {
		super(senderIds);
	}

	/**
	 * 에러 발생시
	 */
	@Override
	protected void onError(Context context, String errorId) {			
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onError(context, errorId);
    }

	/**
	 * 푸시로 메세지가 왔을 때
	 */
	@Override
	protected void onMessage(Context context, Intent intent) {
		GCMMessageManager mm = GCMMessageManager.sharedManager();
        mm.onMessage(context, intent);
    }

	/**
	 * 단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다
	 */
	@Override
	protected void onRegistered(Context context, String regId) {
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onRegistered(context, regId);
    }

	/**
	 * 단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다
	 */
	@Override
	protected void onUnregistered(Context context, String regId) {
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onUnregistered(context, regId);
    }

}
