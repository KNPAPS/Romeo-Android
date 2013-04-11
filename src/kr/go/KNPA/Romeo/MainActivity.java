package kr.go.KNPA.Romeo;
import kr.go.KNPA.Romeo.Chat.ChatFragment;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.Chat.RoomFragment;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
	static MainActivity _sharedActivity = null;
	public static final int MEMBER_SEARCH_ACTIVITY = 1;
	
	private Fragment currentFragment;		// 현재 프레그먼트 
	private Fragment oldFragment;
	
	public boolean isRegistered = false;
	
	private static int lastKeyCode = Constants.NOT_SPECIFIED;
	
	public MainActivity() {		// 생성자 
		super(R.string.changing_fragments);
		_sharedActivity = this;
	}
	
	public static MainActivity sharedActivity() {
		return _sharedActivity;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Bundle b = intent.getExtras();
		long mil = 0;
		if(b!= null && b.containsKey("TEST"))
			mil = b.getLong("TEST");
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		// 부모 클래스의 온크리에잇
		
		
		// set the Above View
				if (savedInstanceState != null)
					currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment"); // restore
				if (currentFragment == null)
					currentFragment = MemberFragment.memberFragment(MemberFragment.TYPE_MEMBERLIST);	// 첫화면										// 생성 		전혀 중요한 클래스가 아니다.
					
				
				Intent intent = getIntent();
				if(intent != null) {
					// TODO
					// currentFragment = new MemberFragment(MemberFragment.TYPE_MEMBERLIST);
				}
				
				// set the Above View
				setContentView(R.layout.content_frame);					// 레이아웃만 있는 빈 뷰   
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, currentFragment)
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
	
	public void goRoomFragment(int subType, String roomCode) {
		ChatFragment chatFragment = ChatFragment.chatFragment(subType);
		switchContent(chatFragment);
		
		Room room = new Room(MainActivity.this, subType, roomCode);
		RoomFragment roomFragment = new RoomFragment(room);
		pushContent(roomFragment);
	}
	
	public void goDocumentFragment() {
		DocumentFragment docFragment = DocumentFragment.documentFragment(Document.TYPE_RECEIVED);
		switchContent(docFragment);
	}
	
	public void goSurveyFragment() {
		SurveyFragment survFragment = SurveyFragment.surveyFragment(Survey.TYPE_RECEIVED);
		switchContent(survFragment);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);	// 키 값으로 저
	}
	
	public void switchContent(Fragment fragment) {		// 이 소스 내에서는 쓰이지 않았다.
		currentFragment = fragment;							// 바꿀 프레그먼트를 fragment 변수로 받아, 이 객체의 전역 변수로 할당한다.
		getSupportFragmentManager()						// 프레그멘트 매니저를 호출하여 교체한다.
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}
	
	public void pushContent(Fragment fragment) {
		//http://developer.android.com/guide/topics/resources/animation-resource.html#View
		oldFragment = currentFragment;
		currentFragment = fragment;					
		getSupportFragmentManager()				
		.beginTransaction()
		.setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
		.addToBackStack(null)
		.add(R.id.content_frame, fragment)
		.commit();
	}
	
	public void popContent(Fragment fragment) {
		currentFragment = oldFragment;
		// TODO : 백 버튼으로 뒤돌아올때 mFragment를 복구할 방법이 없다. // savedInstace만을 위한것인듯 
		getSupportFragmentManager()				
		.beginTransaction()
		.setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
		.remove(fragment)
		.commit();
	}
		
	 
	@Override 
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		FragmentManager fm = getSupportFragmentManager();
		int count = fm.getBackStackEntryCount();
		
		if( count != 0)
			//lastKeyCode = Constants.NOT_SPECIFIED;
			;
		
		if ( keyCode == KeyEvent.KEYCODE_MENU ) { 
			toggle();
			//lastKeyCode = keyCode;
			return true; 
		} 
		
		if ( keyCode == KeyEvent.KEYCODE_BACK) {
			 
			if(count == 0) {
				
				if( getSlidingMenu().isMenuShowing() == true) {
					toggle();
					//lastKeyCode = Constants.NOT_SPECIFIED;
					return true;
				}
				
				//if( lastKeyCode == keyCode ) {
				//	 finish();
				//} else {
					//Toast.makeText(MainActivity.this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
				
				 AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
						.setIcon( this.getResources().getDrawable(kr.go.KNPA.Romeo.R.drawable.icon_dialog) )
						.setTitle("다On")//context.getString(kr.go.KNPA.Romeo.R.string.)
						.setMessage("종료하시겠습니까?")
						.setPositiveButton(kr.go.KNPA.Romeo.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								android.os.Process.killProcess(android.os.Process.myPid());
							}
						}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
							    dialog.dismiss();
							  }
							})
					.show();
				//}
				
				//lastKeyCode = keyCode;
				return true;
			} else {
				fm.popBackStack();
				//lastKeyCode = keyCode;
				return true;
			}	
		}
		
		return super.onKeyDown(keyCode, event); 
	} 
}





