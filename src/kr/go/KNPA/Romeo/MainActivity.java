package kr.go.KNPA.Romeo;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import kr.go.KNPA.Romeo.BaseActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import kr.go.KNPA.Romeo.Util.UserInfo;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
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
		super.onCreate(savedInstanceState);		// 부모 클래스의 온크리에잇
		
		// set the Above View
				if (savedInstanceState != null)
					mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent"); // restore
				if (mContent == null)
					mContent = new MemberFragment(MemberFragment.TYPE_MEMBERLIST);	// 첫화면										// 생성 		전혀 중요한 클래스가 아니다.
				
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
	


	public static void showToast(String string) {
		Toast.makeText(_sharedActivity, string, Toast.LENGTH_SHORT).show();
		Log.d("GCM", string);
		//http://blog.daum.net/haha25/5388319
		//http://raid79.tistory.com/661
		//https://www.google.co.kr/#hl=ko&newwindow=1&sclient=psy-ab&q=GCM+%ED%86%A0%EC%8A%A4%ED%8A%B8&oq=GCM+%ED%86%A0%EC%8A%A4%ED%8A%B8&gs_l=hp.3...1011.7643.0.7696.17.13.3.0.0.3.132.1266.8j5.13.0.eappsweb..0.0...1.1j4.4.psy-ab.zyA55BqdMYc&pbx=1&bav=on.2,or.r_gc.r_pw.r_cp.r_qf.&bvm=bv.42768644,d.aGc&fp=45d2175d682c5ba8&biw=1024&bih=1185
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

	/*
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if(getSlidingMenu().isMenuShowing() == true) {
			super.onBackPressed();
		} else {
			showMenu();
		}
		//Log.i("MainActivity", "BackbuttonPressed");
	}
	*/
	
	public static Bundle isUserRegistered(Context context ){
		boolean hasPreference = false;
		if(UserInfo.getName(context) != null) {
			hasPreference = true;
		}
		
		Bundle b = new Bundle();
		
		if(hasPreference == false) {
			b.putBoolean("isRegistered", false);
			b.putBoolean("isEnabled", false);
			return b;
		}
		
		
		String json = "{\"idx\":"+UserInfo.getUserIdx(context)+"}";

		
		if(hasPreference) {
			Connection conn = new Connection.Builder()
											.url(Connection.HOST_URL + "/member/isRegistered")
											.type(Connection.TYPE_GET)
											.dataType(Connection.DATATYPE_JSON)
											.data(json)
											.build();
			
			String result = null;
			int requestCode = conn.request();
			if(requestCode == Connection.HTTP_OK) {
				result = conn.getResponse();
			} else {
			
			}
			
			if(result != null) {
				JSONObject jo;
				try {
					jo = new JSONObject(result);
					b.putBoolean("isRegistered", (jo.getInt("isRegistered") == 1 ? true : false));
					b.putBoolean("isEnabled", (jo.getInt("isEnabled") == 1 ? true : false));
				} catch (JSONException e) {
				}
			}
		}
		return b;
		
	}
	
	public static Bundle isDeviceRegistered(Context context) {
		Bundle b = new Bundle();
		
//		long userIdx = UserInfo.getUserIdx(context);
		String regid = UserInfo.getRegid(context);
		//if(regid == null) regid = GCMRegistrar.getRegistrationId(this);
		String uuid = UserInfo.getUUID(context);
		
		if(regid == null || regid.trim().length() < 0 || regid.equals("") ||
				uuid == null || uuid.trim().length()<0 || regid.equals("") ) {
			b.putBoolean("isRegistered", false);
			b.putBoolean("isEnabled", false);
			return b;
		}
		
		String json = "";//"{\"idx\":"+userIdx+", \"regid\":\""+regid+"\", \"uuid\":\""+uuid+"\"}";
		
		//String _permission = null;
		
		
		Connection conn = new Connection.Builder()
										.url(Connection.HOST_URL + "/device/isRegistered")
										.type(Connection.TYPE_GET)
										.dataType(Connection.DATATYPE_JSON)
										.data(json)
										.build();

		String result = null;
		int requestCode = conn.request();
		if(requestCode == Connection.HTTP_OK) {
			result = conn.getResponse();
		} else {
			
		}

		if(result != null) {
			JSONObject jo;
			try {
				jo = new JSONObject(result);
				b.putBoolean("isRegistered", (jo.getInt("isRegistered") == 1 ? true : false));
				b.putBoolean("isEnabled", (jo.getInt("isEnabled") == 1 ? true : false));
				//_permission = (jo.getString("permission"));
			} catch (JSONException e) {
			}
		}
	
		
		// 분실?
		
		//String permission = _permission; // TODO
		
		return b;
	}
}





