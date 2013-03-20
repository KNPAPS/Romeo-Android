package kr.go.KNPA.Romeo;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import kr.go.KNPA.Romeo.Member.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;


public class IntroActivity extends Activity{
	private static IntroActivity _sharedActivity; 
	
	private final int REQUEST_REGISTER_USER = 0;
	
	private boolean isUserRegistered;
	private boolean isUserEnabled;
	private boolean isDeviceRegistered;
	private boolean isDeviceEnabled;
	
	Handler h;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		_sharedActivity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		//h = new Handler();
		//h.post(irun);
		
		checkRegistered();
		//runOnUiThread(irun);
		
	}
	
	public static IntroActivity sharedActivity() {
		return _sharedActivity;
	}
	
	public void removeIntroView(ViewGroup v) {
		View view = (View)v.findViewById(R.id.intro);
		if(view == null)
			view = (View)v.findViewWithTag("intro");
		if(view != null)
			v.removeView(view);
		
	}
	
	Runnable logo = new Runnable() {
		@Override
		public void run() {
			
			
			
			
		
		}
	};
	
	Runnable check = new Runnable() {

		@Override
		public void run() {
			
			
		}
		
	};
	
	private void checkRegistered () {
		
		// 사용 조건 : 
		// 유저 등록이 되어 있어야 하고, 유저가 사용 가능해야 하며,
		// 기기 등록이 되어 있어야 하고, 기기가 사용 가능해야 한다. 
		
		boolean isUserAlreadyRegistered = checkUserRegistered();
		if(isUserAlreadyRegistered == true) {
			// false 인 경우, ActivityResult 쪽 흐름을 통해 DeviceRegister 과정을 거치게 된다.
			checkDeviceRegistered();
			runApplication();
		}
		// 따라서, false인 경우의 흐름은 버리도록 한다.
	}
	
	private boolean checkUserRegistered() {
		Intent intent = null;
		Bundle _bUserReg = MainActivity.isUserRegistered(IntroActivity.this);
		isUserRegistered = _bUserReg.getBoolean("isRegistered");
		isUserEnabled = _bUserReg.getBoolean("isEnabled");
		
		if(isUserRegistered == false) {
			// 유저 등록 절차.
			intent = new Intent(IntroActivity.this, UserRegisterActivity.class);
			startActivityForResult(intent, REQUEST_REGISTER_USER);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			return false;
		} else {
			// 유저 등록이 되어 있을 경우.
			return true;
		}
	}
	
	private void checkDeviceRegistered() {
		Bundle _bDeviceReg = MainActivity.isDeviceRegistered(IntroActivity.this);
		isDeviceRegistered = _bDeviceReg.getBoolean("isRegistered");
		isDeviceEnabled = _bDeviceReg.getBoolean("isEnabled");
		
		if(isDeviceRegistered == false) {
			// 기기 등록 절차.
			GCMRegisterManager.registerGCM(IntroActivity.this);
			
			// 다른 thread에서 등록 절차가 실행되며, 
			// GCMRegisterManager 의 onRegister 메소드로 흐름이 넘어가는데,
			// 어차피 허가가 나야 사용할 수 있으므로, 등록 절차 직후에 앱을 사용 할 수 없다.
			// 따라서 등록 전의 Enabled 정보 (disabled) 정보를 사용하여 앱 실행 판단을 내려도 무방한다.
			
			// 자동으로 허가나도록 할 것이 아니라면!!
		}
	}
	
	private void runApplication() {
		Intent intent = null;
		if(isUserEnabled && isDeviceEnabled ) {
			// 사용 가능.
			// MainActivity로 넘긴다.
			intent = new Intent(IntroActivity.this, MainActivity.class);
			
		} else {
			// 사용 불가 상황
			// 일단 등록 대기 창으로 전환.
			intent = new Intent(IntroActivity.this, NotRegisteredActivity.class);
		}
		
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_REGISTER_USER) {
				checkDeviceRegistered();
				runApplication();
			}
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		//h.removeCallbacks(logo);
		return;
	}
	
}
