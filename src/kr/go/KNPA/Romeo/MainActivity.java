package kr.go.KNPA.Romeo;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	static MainActivity _sharedActivity = null;
	public static final int MEMBER_SEARCH_ACTIVITY = 1;
	
	private Fragment mContent;		// 현재 프레그먼트 
	private Fragment oldFragment;
	
	public boolean isRegistered = false;
	
	public MainActivity() {		// 생성자 
		super(R.string.changing_fragments);
		_sharedActivity = this;
	}
	
	public static MainActivity sharedActivity() {
		return _sharedActivity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set the Above View
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent"); // restore
		}
			
		if (mContent == null) {
			mContent = new MemberFragment(MemberFragment.TYPE_MEMBERLIST);
		}
		
		((MemberFragment)mContent).showIntroView = true;

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
	
	public static void showToast(String string) {
		Toast.makeText(_sharedActivity, string, Toast.LENGTH_SHORT).show();
		Log.d(TAG, string);
	}
	
	 /* (non-Javadoc) * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent) */ 
	@Override 
	public boolean onKeyUp(int keyCode, KeyEvent event) { 
		if ( keyCode == KeyEvent.KEYCODE_MENU ) { 
			toggle();
			
			return true; 
		} 
		
		if ( keyCode == KeyEvent.KEYCODE_BACK) {
			FragmentManager fm = getSupportFragmentManager(); 
			int count = fm.getBackStackEntryCount();
			if(count == 0) {
				if(getSlidingMenu().isMenuShowing() == true) {
					 finish();
				} else {
					showMenu();
				}
				return true;
			} else {
				fm.popBackStack();
				return true;
			}
			
		}
		
		return super.onKeyDown(keyCode, event); 
	}
}





