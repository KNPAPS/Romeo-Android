package kr.go.KNPA.Romeo;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.ChatFragment;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.Chat.RoomFragment;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
	static MainActivity _sharedActivity = null;
	public static final int MEMBER_SEARCH_ACTIVITY = 1;
	
	public boolean isRegistered = false;
	
	private static int lastKeyCode = Constants.NOT_SPECIFIED;
	
	public MainActivity() {		// 생성자 
		super(R.string.app_name);
		_sharedActivity = this;
	}
	
	public static MainActivity sharedActivity() {
		return _sharedActivity;
	}
	
	/*
	@Override
	protected void onNewIntent(Intent intent) {
		Bundle b = intent.getExtras();
		long mil = 0;
		if(b!= null && b.containsKey("TEST"))
			mil = b.getLong("TEST");
		
		if(b != null) {
			Set keySet = b.keySet();
			Iterator itr = keySet.iterator();
			while(itr.hasNext()) {
				Log.i("intent:onNewIntent",itr.next().toString());
			}
		}
		
	}
	*/

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		// 부모 클래스의 온크리에잇
		
		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		// set the Above View
		setContentView(R.layout.content_frame);					// 레이아웃만 있는 빈 뷰
				
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);				// 비하인드 프레임은, 메뉴 뷰다. 프레그먼트를 대입하기 위해 빈것으로 존재(베이스에서는)

		Fragment fragment=null;
		// set the Above View
		if (savedInstanceState != null)
			fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment"); // restore
		if (fragment == null)
			fragment = MemberFragment.memberFragment(MemberFragment.TYPE_MEMBERLIST);	// 첫화면										// 생성 		전혀 중요한 클래스가 아니다.
		

		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new MenuListFragment())
		.commit();

		Intent intent = getIntent();
		if(intent != null && intent.getExtras().containsKey(KEY.MESSAGE.TYPE)) {
			try {
				Bundle b = intent.getExtras();
				int type = b.getInt(KEY.MESSAGE.TYPE);
				
				int mainType = type / Message.MESSAGE_TYPE_DIVIDER;
				int subType = type % Message.MESSAGE_TYPE_DIVIDER;
				switch(mainType) {
				case Message.MESSAGE_TYPE_CHAT :
					Room room = new Room(getApplicationContext(), b.getString(KEY.CHAT.ROOM_CODE));
					goRoomFragment(subType, room);
					break;
				case Message.MESSAGE_TYPE_DOCUMENT :
					goDocumentFragment();
					break;
				case Message.MESSAGE_TYPE_SURVEY :
					goSurveyFragment();
					break;
				}
				// currentFragment = new MemberFragment(MemberFragment.TYPE_MEMBERLIST);
			} catch (Exception e) {
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commit();												// 컨텐트 프레임과 현재(혹은 생성된) 프레그먼트를 바꾼다.
			}
		} else {
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, fragment)
			.commit();												// 컨텐트 프레임과 현재(혹은 생성된) 프레그먼트를 바꾼다.
		}
				
	}
	
	public void goRoomFragment(int subType, Room room) {
		ChatFragment chatFragment = ChatFragment.chatFragment(subType);
		switchContent(chatFragment);
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
	
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);	// 키 값으로 저
//	}
//	
	public void switchContent(Fragment fragment) {		// 이 소스 내에서는 쓰이지 않았다.
														// 바꿀 프레그먼트를 fragment 변수로 받아, 이 객체의 전역 변수로 할당한다.
		getSupportFragmentManager()						// 프레그멘트 매니저를 호출하여 교체한다.
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}
	
	public void pushContent(Fragment fragment) {
		//http://developer.android.com/guide/topics/resources/animation-resource.html#View
		
		getSupportFragmentManager()				
		.beginTransaction()
		.setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
		.addToBackStack(null)
		.add(R.id.content_frame, fragment)
		.commit();
	}
	
	public void popContent() {
//		currentFragment = oldFragment;
//		// TODO : 백 버튼으로 뒤돌아올때 mFragment를 복구할 방법이 없다. // savedInstace만을 위한것인듯 
//		getSupportFragmentManager()				
//		.beginTransaction()
//		.setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
//		.remove(fragment)
//		.commit();
		getSupportFragmentManager().popBackStack();
	}
		
//	@Override
//	public void toggle() {
//		super.toggle();
//		
//		View focusedView = getCurrentFocus();
//		if(focusedView != null) {
//			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(focusedView.getApplicationWindowToken(), 0);
//		}
//	  
//	}
	 
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
				this.popContent();
				//lastKeyCode = keyCode;
				return true;
			}	
		}
		
		return super.onKeyDown(keyCode, event); 
	} 
}





