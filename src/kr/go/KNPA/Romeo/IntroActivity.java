package kr.go.KNPA.Romeo;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import kr.go.KNPA.Romeo.Register.NotRegisteredActivity;
import kr.go.KNPA.Romeo.Register.StatusChecker;
import kr.go.KNPA.Romeo.Register.UserRegisterActivity;
import kr.go.KNPA.Romeo.Survey.Survey;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


public class IntroActivity extends BaseActivity{

	private static IntroActivity _sharedActivity; 
	private final int REQUEST_REGISTER_USER = 0;
	private static StatusChecker checker; 
	
	private Bundle targetModuleInfo = null;
	
	public IntroActivity() {
		super(R.string.changing_fragments);
		_sharedActivity = this;	//?
	}
	public static IntroActivity sharedActivity() {
		return _sharedActivity;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Bundle _b = intent.getExtras();
		long mil = 0;
		if(_b!= null && _b.containsKey("TEST"))
			mil = _b.getLong("TEST");
		
		targetModuleInfo = new Bundle();
		Bundle b = intent.getExtras();
		if(b != null) targetModuleInfo.putAll(b);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		_sharedActivity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);

		boolean registered = checkRegistered();
		
		/**
		 * 모든 검증을 정상적으로 통과.
		 * 메인 액티비티 시작 
		 */
		if(registered) {
			Intent intent = getIntent();
			targetModuleInfo = new Bundle();
			
			Bundle b = intent.getExtras();
			if(b != null) targetModuleInfo.putAll(b);
			
			runApplication();
		}
	}


	private boolean checkRegistered () {
		/**
		 * 애플리케이션을 구동하기 위해 요구되는 상태를 체크하는 객체
		 */
		checker = new StatusChecker(this);
		
		if ( !checker.isConnectedToNetwork() ) {
			//TODO 인터넷이 안될 때 띄울 화면 처리.
			//만약 처음에 킬 땐 안 됐다가 나중에 연결하는 걸 대비해서
			//다음 사이트에 있는 내용을 참고하여 구현해야함
			//http://shstarkr.tistory.com/158
			return false;
		}

		boolean isUserRegisterd = checkUserRegistered();
		boolean isDeviceRegistered = checkDeviceRegistered();
		
		return isUserRegisterd && isDeviceRegistered;
	}
	
	private boolean checkUserRegistered() {		
		/**
		 * 유저 상태 체크
		 * USER_REGISTERED_ENABLED 상태가 아니라면 해당 상태마다
		 * 필요한 Activity를 호출.
		 * USER_REGISTERED_ENABLED일 경우 기기 인증 진행
		 */
		switch( checker.getUserStatus() ) {
		//등록이 되어있지 않을 때
		case StatusChecker.USER_NOT_REGISTERED:
			startUserRegisterActivity();
			return false;
		//아직 유저 활성화가 안됨
		case StatusChecker.USER_REGISTERED_NOT_ENABLED:
			startNotRegisteredActivity();
			return false;
		default:
			return true;	
		}

	}
	
	private boolean checkDeviceRegistered() {
		/**
		 * 기기 상태 체크
		 * DEVICE_REGISTERED_ENABLED 상태가 아니라면 해당 상태마다
		 * 필요한 Activity를 호출.
		 * DEVICE_REGISTERED_ENABLED일 경우 기기 인증 진행
		 */
		switch( checker.getDeviceStatus() ) {
		case StatusChecker.DEVICE_NOT_REGISTERED:
			GCMRegisterManager.registerGCM(IntroActivity.this);
			startNotRegisteredActivity();
			return false;
		case StatusChecker.DEVICE_REGISTERED_NOT_ENABLED:
			startNotRegisteredActivity();
			return false;
		default: 
			return true;
		}

	}
	
	
	private void startUserRegisterActivity() {
		Intent intent = new Intent(IntroActivity.this, UserRegisterActivity.class);
		if(targetModuleInfo != null)
			intent.putExtras(targetModuleInfo);
		startActivityForResult(intent, REQUEST_REGISTER_USER);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	private void startNotRegisteredActivity() {
		Intent intent = new Intent(IntroActivity.this, NotRegisteredActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
	}

	
	private void runApplication() {
		Intent intent = null;
		
		intent = new Intent(IntroActivity.this, MainActivity.class);
		
		
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_REGISTER_USER) {
				// UserRegisterActivity를 통해 User는 성공적으로 동록되었다고 본다. (UserRegisterActivity가 유저의 등록을 책임진다.)
				
				boolean isDeviceRegistered = checkDeviceRegistered();
				if(isDeviceRegistered == true)
					runApplication();
				// TODO : 유저 등록, 디바이스 등록을 할만큼 처음 사용하는 것이라면,
				// 메시지가 온적도 없을 것이고, 메시지가 와서 그것을 알리는 푸시 노티피케이션을 누르고
				// 앱 && 액티비티가 실행되는 일도 없을 것이다.
				// 따라서 이 곳에 해당 모듈이 실행되도록 하는 로직을 구성할 필요는 없다.
				// TODO : 분실 신고, 분실 중 메시지 도착, 단말기 복구 ?? =>> 쌓여있던 메시지들은??,,,,
			}
		} else {
			
		}
	}
	
	public void removeIntroView(ViewGroup v) {
		View view = (View)v.findViewById(R.id.intro);
		if(view == null)
			view = (View)v.findViewWithTag("intro");
		if(view != null)
			v.removeView(view);
		
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		//h.removeCallbacks(logo);
		return;
	}
	
}
