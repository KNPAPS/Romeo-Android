package kr.go.KNPA.Romeo;
import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.GetChars;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import kr.go.KNPA.Romeo.BaseActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import kr.go.KNPA.Romeo.Util.Preference;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
	static MainActivity _sharedActivity = null;
	
	private Fragment mContent;		// 현재 프레그먼트 
	private Fragment oldFragment;
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
					mContent = new MemberFragment(MemberFragment.TYPE_MEMBERLIST);	// 첫화면										// 생성 		전혀 중요한 클래스가 아니다.
				
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
	
	public void pushContent(Fragment fragment) {
		oldFragment = mContent;
		mContent = fragment;					
		getSupportFragmentManager()				
		.beginTransaction()
		.setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
		.addToBackStack(null)
		.add(R.id.content_frame, fragment)
		//.replace(R.id.content_frame, fragment)
		//.remove(mContent)
		.commit();
		
		//http://developer.android.com/guide/topics/resources/animation-resource.html#View
		
	}
	
	public void popContent(Fragment fragment) {
		mContent = oldFragment;
		// TODO : 백 버튼으로 뒤돌아올때 mFragment를 복구할 방법이 없다. // savedInstace만을 위한것인듯 
		getSupportFragmentManager()				
		.beginTransaction()
		.setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
		.remove(fragment)
		.commit();
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

	public static void showToast(String string) {
		Toast.makeText(_sharedActivity, string, Toast.LENGTH_SHORT).show();
		Log.d("GCM", string);
		//http://blog.daum.net/haha25/5388319
		//http://raid79.tistory.com/661
		//https://www.google.co.kr/#hl=ko&newwindow=1&sclient=psy-ab&q=GCM+%ED%86%A0%EC%8A%A4%ED%8A%B8&oq=GCM+%ED%86%A0%EC%8A%A4%ED%8A%B8&gs_l=hp.3...1011.7643.0.7696.17.13.3.0.0.3.132.1266.8j5.13.0.eappsweb..0.0...1.1j4.4.psy-ab.zyA55BqdMYc&pbx=1&bav=on.2,or.r_gc.r_pw.r_cp.r_qf.&bvm=bv.42768644,d.aGc&fp=45d2175d682c5ba8&biw=1024&bih=1185
	}
}





