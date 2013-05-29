package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;
import java.util.Arrays;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.search.MemberSearchActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RoomInfoActivity extends Activity implements RoomInfoLayout.Listener {
	public static final int		REQUEST_CODE		= 101;

	public static final String	KEY_ROOM_CODE		= RoomInfoActivity.class.getName() + "roomCode";
	public static final String	KEY_ROOM_TITLE		= RoomInfoActivity.class.getName() + "roomTitle";
	public static final String	KEY_ROOM_ALIAS		= RoomInfoActivity.class.getName() + "roomAlias";
	public static final String	KEY_ROOM_STATUS		= RoomInfoActivity.class.getName() + "roomStatus";
	public static final String	KEY_ROOM_TYPE		= RoomInfoActivity.class.getName() + "roomType";
	public static final String	KEY_IS_ALARM_ON		= RoomInfoActivity.class.getName() + "isAlarmOn";
	public static final String	KEY_CHATTERS_IDX	= RoomInfoActivity.class.getName() + "chattersIdx";

	public static final String	KEY_ACTION			= "action";
	public static final int		ACTION_LEAVE_ROOM	= 1;

	private RoomInfoLayout		mLayout;
	private Room				mRoom;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mLayout = new RoomInfoLayout(this, R.layout.activity_room_info);
		mLayout.setNavBarTitleTV("채팅방 정보");
		mLayout.setLeftNavBarBtnText(R.string.cancel);
		mLayout.setListener(this);

		Bundle b = getIntent().getExtras();
		mRoom = new Room();
		mRoom.setStatus(b.getInt(KEY_ROOM_STATUS));
		mRoom.setType(b.getInt(KEY_ROOM_TYPE));
		mRoom.addChatters(new ArrayList<String>(Arrays.asList(b.getStringArray(KEY_CHATTERS_IDX))));

		if (mRoom.getStatus() == Room.STATUS_CREATED)
		{
			mRoom.setCode(b.getString(KEY_ROOM_CODE));
			mRoom.setAlarm(b.getBoolean(KEY_IS_ALARM_ON));
			mRoom.setTitle(b.getString(KEY_ROOM_TITLE));
			mRoom.setAlias(b.getString(KEY_ROOM_ALIAS));
			mLayout.setRoomName(mRoom.getTitle(), mRoom.getAlias());
			mLayout.setAlarmStatusText(mRoom.isAlarmOn());
		}
		else
		{
			mLayout.setRoomName(getString(mRoom.getType() == Room.TYPE_COMMAND ? R.string.commandTitle : R.string.meetingTitle), null);
		}

		mLayout.setChatterList(mRoom.chatters);
	}

	@Override
	public void onLeftNavBarBtnClick()
	{
		finish();
	}

	@Override
	public void onRightNavBarBtnClick()
	{

	}

	@Override
	public void onGoToRoomAliasSettingActivity()
	{
		if (mRoom.getStatus() != Room.STATUS_CREATED)
		{
			return;
		}

		Intent intent = new Intent(this, RoomAliasSettingActivity.class);
		intent.putExtra(RoomAliasSettingActivity.KEY_ROOM_CODE, mRoom.getCode());
		startActivityForResult(intent, RoomAliasSettingActivity.REQUEST_CODE);
	}

	@Override
	public void onToggleAlarmStatus()
	{
		if (mRoom.getStatus() != Room.STATUS_CREATED)
		{
			return;
		}

		mRoom.setAlarm(!mRoom.isAlarmOn());
		new Thread() {
			public void run()
			{
				DBProcManager.sharedManager(RoomInfoActivity.this).chat().setRoomAlarm(mRoom.getCode(), mRoom.isAlarmOn());
			}
		}.start();

		mLayout.setAlarmStatusText(mRoom.isAlarmOn());
	}

	@Override
	public void onGoToFullChatterList()
	{
		Intent intent = new Intent(this, UserListActivity.class);
		intent.putExtra(UserListActivity.KEY_USERS_IDX, mRoom.getChattersIdx());
		intent.putExtra(UserListActivity.KEY_TITLE, "대화방 참여자");
		startActivity(intent);
	}

	@Override
	public void onGoToInviteActivity()
	{
		Intent intent = new Intent(this, MemberSearchActivity.class);
		intent.putExtra(MemberSearchActivity.KEY_EXCLUDE_IDXS, mRoom.getChattersIdx());
		startActivityForResult(intent, MemberSearchActivity.REQUEST_CODE);
	}

	@Override
	public void onLeaveRoom()
	{

		Bundle b = new Bundle();
		b.putInt(KEY_ACTION, ACTION_LEAVE_ROOM);

		Intent intent = new Intent();
		intent.putExtras(b);

		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case RoomAliasSettingActivity.REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK)
			{
				Bundle b = data.getExtras();

				String newAlias = b.getString(RoomAliasSettingActivity.KEY_NEW_ALIAS);
				onChangeRoomAlias(newAlias);
			}
			break;
		case MemberSearchActivity.REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK)
			{
				Bundle b = data.getExtras();
				ArrayList<String> idxs = b.getStringArrayList(MemberSearchActivity.KEY_RESULT_IDXS);
				Intent intent = new Intent();
				intent.putExtra(KEY_CHATTERS_IDX, idxs);
				intent.putExtra(KEY_ACTION, RoomFragment.ACTION_INVITE_USERS);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			break;
		}
	}

	private void onChangeRoomAlias(final String newAlias)
	{
		mLayout.setRoomName(mRoom.getTitle(), newAlias);
		mRoom.setAlias(newAlias);

		new Thread() {
			@Override
			public void run()
			{
				super.run();
				DBProcManager.sharedManager(RoomInfoActivity.this).chat().setRoomAlias(mRoom.getCode(), newAlias);
			}
		}.start();
	}
}
