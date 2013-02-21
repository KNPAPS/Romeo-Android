package kr.go.KNPA.Romeo;
import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;

import kr.go.KNPA.Romeo.BaseActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import kr.go.KNPA.Romeo.Util.Preference;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
	static MainActivity _sharedActivity = null;
	
	private Fragment mContent;		// 현재 프레그먼트 
	
	public MainActivity() {		// 생성자 
		super(R.string.changing_fragments);
		_sharedActivity = this;
	}
	
	public static MainActivity sharedActivity() {
		return _sharedActivity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		// 부모 클래스의 온크리에잇
		Preference.initSharedPreference(getBaseContext());
		registerGCM();
		
		
		
		// set the Above View
				if (savedInstanceState != null)
					mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent"); // restore
				if (mContent == null)
					mContent = new MemberFragment();	// 첫화면										// 생성 		전혀 중요한 클래스가 아니다.
				
				// set the Above View
				setContentView(R.layout.content_frame);					// 레이아웃만 있는 빈 뷰   
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, mContent)
				.commit();												// 컨텐트 프레임과 현재(혹은 생성된) 프레그먼트를 바꾼다.
				
				// set the Behind View
				setBehindContentView(R.layout.menu_frame);				// 비하인드 프레임은, 메뉴 뷰다. 프레그먼트를 대입하기 위해 빈것으로 존재(베이스에서는)
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.menu_frame, new MenuListFragment())
				.commit();
				
				// customize the SlidingMenu
				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				// 슬라이딩 메뉴가 뭐지??????
				//?????	
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);	// 키 값으로 저
	}
	
	public void switchContent(Fragment fragment) {		// 이 소스 내에서는 쓰이지 않았다.
		mContent = fragment;							// 바꿀 프레그먼트를 fragment 변수로 받아, 이 객체의 전역 변수로 할당한다.
		getSupportFragmentManager()						// 프레그멘트 매니저를 호출하여 교체한다.
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}
	
	
	////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void registerGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if("".equals(regId))   //구글 가이드에는 regId.equals("")로 되어 있는데 Exception을 피하기 위해 수정
			// TODO : 존재하더라도, 서버상에 등록이 되어있지 않으면 등록하도록 시킴.
		      GCMRegistrar.register(this, "44570658441");
		else
		      Log.d("==============", regId);
	}

}





