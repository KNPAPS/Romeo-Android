package kr.go.KNPA.Romeo;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.Chat.RoomFragment;
import kr.go.KNPA.Romeo.Chat.RoomListFragment;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends BaseActivity {
	static MainActivity		_sharedActivity			= null;
	public static final int	MEMBER_SEARCH_ACTIVITY	= 1;

	public boolean			isRegistered			= false;

	public MainActivity()
	{
		super(R.string.app_name);

		if (_sharedActivity != null)
		{
			_sharedActivity.finish();
		}

		_sharedActivity = this;
	}

	public static MainActivity sharedActivity()
	{
		return _sharedActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		// set the Above View
		setContentView(R.layout.content_frame); // 레이아웃만 있는 빈 뷰

		// set the Behind View
		setBehindContentView(R.layout.menu_frame); // 비하인드 프레임은, 메뉴 뷰다. 프레그먼트를
													// 대입하기 위해 빈것으로 존재(베이스에서는)
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new MenuListFragment()).commit();

		Fragment fragment = null;

		Intent intent = getIntent();

		if (intent != null && intent.getExtras().containsKey(KEY.MESSAGE.TYPE))
		{
			try
			{
				Bundle b = intent.getExtras();
				int type = b.getInt(KEY.MESSAGE.TYPE);

				int mainType = type / Message.MESSAGE_TYPE_DIVIDER;
				int subType = type % Message.MESSAGE_TYPE_DIVIDER;

				switch (mainType)
				{
				case Message.MESSAGE_TYPE_CHAT:
					Room room = new Room(b.getString(KEY.CHAT.ROOM_CODE));
					goRoomFragment(subType, room);
					break;
				case Message.MESSAGE_TYPE_DOCUMENT:
					goDocumentFragment();
					break;
				case Message.MESSAGE_TYPE_SURVEY:
					goSurveyFragment();
					break;
				}
			}
			catch (Exception e)
			{

				fragment = MemberFragment.memberFragment(MemberFragment.TYPE_MEMBERLIST); // 첫화면
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
			}
		}
		else
		{
			fragment = MemberFragment.memberFragment(MemberFragment.TYPE_MEMBERLIST); // 첫화면
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

		}
		setUpDeviceSpec();
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		new Handler().post(new Runnable() {
			@Override
			public void run()
			{
				toggle();
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void setUpDeviceSpec()
	{
		Point outSize = new Point();
		if (Build.VERSION.SDK_INT < 13)
		{
			outSize.x = getWindowManager().getDefaultDisplay().getWidth();
			outSize.y = getWindowManager().getDefaultDisplay().getHeight();
		}
		else
		{
			getWindowManager().getDefaultDisplay().getSize(outSize);
		}
		Constants.DEVICE_WIDTH = outSize.x;
		Constants.DEVICE_HEIGHT = outSize.y;
	}

	/**
	 * ImageLoader class initialize
	 */
	public void initImageLoader()
	{
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MainActivity.this).defaultDisplayImageOptions(defaultOptions).build();
		ImageLoader.getInstance().init(config); // Do it on Application start
	}

	public void goRoomFragment(int subType, Room room)
	{
		RoomListFragment roomListFragment = new RoomListFragment(subType);
		switchContent(roomListFragment);
		RoomFragment roomFragment = new RoomFragment(room);
		pushContent(roomFragment);
	}

	public void goDocumentFragment()
	{
		DocumentFragment docFragment = DocumentFragment.documentFragment(Document.TYPE_RECEIVED);
		switchContent(docFragment);
	}

	public void goSurveyFragment()
	{
		SurveyFragment survFragment = SurveyFragment.surveyFragment(Survey.TYPE_RECEIVED);
		switchContent(survFragment);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
	}

	public void switchContent(Fragment fragment)
	{
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName()).commit();
		getSlidingMenu().showContent();

	}

	public void pushContent(Fragment fragment)
	{
		getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
				.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
	}

	public void popContent()
	{
		getSupportFragmentManager().popBackStack();
	}

	@Override
	public void toggle()
	{
		super.toggle();

		if (getSlidingMenu().isShown())
		{
			MenuListFragment.setMode(false);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		FragmentManager fm = getSupportFragmentManager();
		int count = fm.getBackStackEntryCount();

		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			toggle();
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{

			if (getSlidingMenu().isMenuShowing() == false)
			{

				if (count == 0)
				{
					toggle();
					return true;
				}
				else
				{
					this.popContent();
					return true;
				}
			}
			else
			{

				if (count == 0)
				{
					new RomeoDialog.Builder(MainActivity.this).setIcon(this.getResources().getDrawable(kr.go.KNPA.Romeo.R.drawable.icon_dialog)).setTitle("다On")// context.getString(kr.go.KNPA.Romeo.R.string.)
							.setMessage("종료하시겠습니까?").setPositiveButton(kr.go.KNPA.Romeo.R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									dialog.dismiss();
									android.os.Process.killProcess(android.os.Process.myPid());
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton)
								{
									dialog.dismiss();
								}
							}).show();
					return true;
				}
				else
				{
					toggle();
					this.popContent();
					return true;
				}
			}

		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
}