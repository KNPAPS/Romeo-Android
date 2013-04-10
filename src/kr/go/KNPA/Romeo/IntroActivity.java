package kr.go.KNPA.Romeo;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import kr.go.KNPA.Romeo.Register.NotRegisteredActivity;
import kr.go.KNPA.Romeo.Register.StatusChecker;
import kr.go.KNPA.Romeo.Register.UserRegisterActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


public class IntroActivity extends BaseActivity{

	private static IntroActivity _sharedActivity; 
	private final int REQUEST_REGISTER_USER = 0;
	private static StatusChecker checker; 
	private int devStatus ;
	private int userStatus;
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

		checker = new StatusChecker(this);
		
		if ( !checker.isConnectedToNetwork() ) {
			//TODO 인터넷이 안될 때 띄울 화면 처리.
			//만약 처음에 킬 땐 안 됐다가 나중에 연결하는 걸 대비해서
			//다음 사이트에 있는 내용을 참고하여 구현해야함
			//http://shstarkr.tistory.com/158
			finish();
			return ;
		}

		
		devStatus = checker.getDeviceStatus();
		userStatus = checker.getUserStatus();
		if ( devStatus  == StatusChecker.DEVICE_NOT_REGISTERED ) {
			GCMRegisterManager.registerGCM(IntroActivity.this);
			
		}
		
		// DEVICE_REGISTERED_NOT_ENABLED or // DEVICE_REGISTERED_ENABLED
		
		if ( userStatus == StatusChecker.USER_NOT_REGISTERED ) {
			startUserRegisterActivity();
		} else {
			
			if ( isEnabled() ) {
				runApplication();
			} else {
				startNotRegisteredActivity();
			}
			
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

	private boolean isEnabled() {
		return userStatus == StatusChecker.USER_REGISTERED_ENABLED && devStatus == StatusChecker.DEVICE_REGISTERED_ENABLED;
	}
	
	private void runApplication() {
		
		/**
		 * 모든 검증을 정상적으로 통과.
		 * 메인 액티비티 시작 
		 */
		Intent moduleInfoIntent = getIntent();
		Bundle moduleInfoBundle = null;
		if(moduleInfoIntent != null)
			moduleInfoBundle = moduleInfoIntent.getExtras();
		
		targetModuleInfo = new Bundle();
		if(moduleInfoIntent != null) targetModuleInfo.putAll(moduleInfoBundle);
		
		
		Intent intent = new Intent(IntroActivity.this, MainActivity.class);
		intent.putExtras(targetModuleInfo);
		
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_REGISTER_USER) {
				// UserRegisterActivity를 통해 User는 성공적으로 동록되었다고 본다. (UserRegisterActivity가 유저의 등록을 책임진다.)
				// TODO : 유저 등록, 디바이스 등록을 할만큼 처음 사용하는 것이라면,
				// 메시지가 온적도 없을 것이고, 메시지가 와서 그것을 알리는 푸시 노티피케이션을 누르고
				// 앱 && 액티비티가 실행되는 일도 없을 것이다.
				// 따라서 이 곳에 해당 모듈이 실행되도록 하는 로직을 구성할 필요는 없다.
				// TODO : 분실 신고, 분실 중 메시지 도착, 단말기 복구 ?? =>> 쌓여있던 메시지들은??,,,,
				if ( isEnabled() ) {
					runApplication();
				} else {
					startNotRegisteredActivity();
				}
			}
		} else {
			
		}
	}
	
	/*
	public void removeIntroView(ViewGroup v) {
		View view = (View)v.findViewById(R.id.intro);
		if(view == null)
			view = (View)v.findViewWithTag("intro");
		if(view != null)
			v.removeView(view);
		
	}
	*/
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		//h.removeCallbacks(logo);
		return;
	}
	
}
