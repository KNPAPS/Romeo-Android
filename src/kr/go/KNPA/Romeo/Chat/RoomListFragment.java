package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.search.MemberSearchActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RoomListFragment extends Fragment implements RoomListFragmentLayout.Listener {
	private static RoomListFragment	mCommandListController	= null;
	private static RoomListFragment	mMeetingListController	= null;
	private static RoomFragment		mCurrentRoom			= null;

	private static Handler			mHandler;
	private int						subType;
	private RoomListFragmentLayout	mLayout;
	private RoomListAdapter			mListAdapter;

	public RoomListFragment(int subType)
	{
		this.subType = subType;
	}

	public static RoomListFragment getInstance(int subType)
	{
		RoomListFragment f = null;

		if (subType == Chat.TYPE_COMMAND)
		{
			if (mCommandListController == null)
			{
				mCommandListController = new RoomListFragment(Chat.TYPE_COMMAND);
			}

			f = mCommandListController;
		}
		else
		{
			if (mMeetingListController == null)
			{
				mMeetingListController = new RoomListFragment(Chat.TYPE_MEETING);
			}

			f = mMeetingListController;
		}

		return f;
	}

	public static RoomFragment getCurrentRoom()
	{
		return mCurrentRoom;
	}

	public static void setCurrentRoom(RoomFragment ra)
	{
		mCurrentRoom = ra;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHandler = null;
		if (subType == Chat.TYPE_COMMAND)
		{
			mCommandListController = null;
		}
		else if (subType == Chat.TYPE_MEETING)
		{
			mMeetingListController = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mLayout = new RoomListFragmentLayout(getActivity(), inflater, container, savedInstanceState, R.layout.chat_fragment);
		mLayout.setListener(this);

		mLayout.setLeftNavBarBtnText(R.string.menu);
		mLayout.setRightNavBarBtnText(R.string.add);

		int titleResId = subType == Chat.TYPE_COMMAND ? R.string.commandTitle : R.string.meetingTitle;
		mLayout.setNavBarTitleTV(titleResId);

		if (mHandler == null)
		{
			mHandler = new Handler();
		}

		// listview에 adpater를 설정하고 초기 채팅 목록 불러오기
		new Thread() {
			@Override
			public void run()
			{
				mListAdapter = new RoomListAdapter(getActivity(), null);

				if (subType == Room.TYPE_COMMAND)
				{
					Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
						@Override
						public String getSectionTitleForItem(Cursor c)
						{
							int isHost = c.getInt(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_IS_HOST));
							String sectionTitle = null;
							if (isHost == 1)
							{
								sectionTitle = getResources().getString(R.string.header_title_command_sent);
							}
							else
							{
								sectionTitle = getResources().getString(R.string.header_title_command_received);
							}
							return sectionTitle;
						}
					};

					final SimpleSectionAdapter<Cursor> sectionAdapter = new SimpleSectionAdapter<Cursor>(getActivity(), mListAdapter, R.layout.section_header, R.id.title, sectionizer);

					final Cursor c = mListAdapter.query(subType);
					mHandler.post(new Runnable() {
						public void run()
						{
							mLayout.getListView().setAdapter(sectionAdapter);
							refresh(c);

							if (mListAdapter.getCount() == 0)
							{
								mLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.empty_set_background));
							}
						}
					});
				}
				else
				{
					final Cursor c = mListAdapter.query(subType);
					mHandler.post(new Runnable() {
						public void run()
						{
							mLayout.getListView().setAdapter(mListAdapter);
							refresh(c);

							if (mListAdapter.getCount() == 0)
							{
								mLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.empty_set_background));
							}
						}
					});
				}

				super.run();
			}
		}.start();

		return mLayout.getView();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case MemberSearchActivity.REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK)
			{
				final ArrayList<String> receiversIdxs = data.getExtras().getStringArrayList(MemberSearchActivity.KEY_RESULT_IDXS);

				Room room = null;
				String roomCode = null;
				if (receiversIdxs.size() == 0)
				{
					return;
				}
				else if (receiversIdxs.size() == 1)
				{
					roomCode = DBProcManager.sharedManager(getActivity()).chat().getPairRoomCode(subType, receiversIdxs.get(0));

				}

				if (roomCode != null)
				{
					room = new Room(roomCode);
				}
				else
				{
					room = new Room();
					room.addChatters(receiversIdxs);
					room.setType(subType);
				}

				goToRoomController(room);
			}
			break;
		case RoomAliasSettingActivity.REQUEST_CODE:

			if (resultCode == RoomAliasSettingActivity.RESULT_OK)
			{
				Bundle b = data.getExtras();
				String roomCode = b.getString(RoomAliasSettingActivity.KEY_ROOM_CODE);
				String newAlias = b.getString(RoomAliasSettingActivity.KEY_NEW_ALIAS);
				onSetRoomAlias(roomCode, newAlias);
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	/**
	 * (Message Receiving) GCMMessageManager에서 onMessage, onChat을 거쳐 이 메서드가 호출되게
	 * 된다. \n GCM 모듈을 통해서 진입했으므로 당연히 별도의 thread상에서 작업이 이루어진다.
	 * 
	 * @param chat
	 *            새로 도착한 chat의 instance
	 */

	public void onReceiveChat(Chat chat)
	{
		new Thread() {
			@Override
			public void run()
			{
				final Cursor c = mListAdapter.query(subType);
				mHandler.post(new Runnable() {
					public void run()
					{
						refresh(c);

					}
				});
				super.run();
			}
		}.start();
	}

	@Override
	public void onEnterRoom(final String roomCode)
	{
		new Thread() {
			public void run()
			{
				Room room = new Room(roomCode);
				goToRoomController(room);

			};
		}.start();
	}

	@Override
	public void onDeleteRoom(final String roomCode)
	{
		new Thread() {
			public void run()
			{
				Room room = new Room(roomCode);

				RoomModel model = new RoomModel(getActivity(), room);
				model.deleteRoom();

				final Cursor c = mListAdapter.query(subType);
				mHandler.post(new Runnable() {
					public void run()
					{
						refresh(c);

					}
				});
				model = null;
			}
		}.start();
	}

	@Override
	public void onGoToSetRoomAliasActivity(final String roomCode)
	{
		Intent intent = new Intent(getActivity(), RoomAliasSettingActivity.class);
		intent.putExtra(RoomAliasSettingActivity.KEY_ROOM_CODE, roomCode);
		startActivityForResult(intent, RoomAliasSettingActivity.REQUEST_CODE);
	}

	@Override
	public void onLeftNavBarBtnClick()
	{
		MainActivity.sharedActivity().toggle();
	}

	@Override
	public void onRightNavBarBtnClick()
	{
		Intent intent = new Intent(getActivity(), MemberSearchActivity.class);
		startActivityForResult(intent, MemberSearchActivity.REQUEST_CODE);
	}

	private void onSetRoomAlias(final String roomCode, final String newAlias)
	{
		new Thread() {
			@Override
			public void run()
			{
				Room room = new Room(roomCode);

				RoomModel model = new RoomModel(getActivity(), room);
				model.changeAlias(newAlias);

				final Cursor c = mListAdapter.query(subType);

				mHandler.post(new Runnable() {
					public void run()
					{
						refresh(c);

					}
				});
				model = null;
				super.run();
			}
		}.start();
	}

	private void goToRoomController(Room room)
	{
		RoomFragment roomController = new RoomFragment(room);
		getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
				.replace(R.id.content_frame, roomController, roomController.getClass().getSimpleName()).addToBackStack(null).commit();
	}

	@SuppressWarnings("unchecked")
	private void refresh(Cursor c)
	{
		mListAdapter.changeCursor(c);

		mListAdapter.notifyDataSetChanged();

		if (subType == Room.TYPE_COMMAND)
		{
			((SimpleSectionAdapter<Cursor>) mLayout.getListView().getAdapter()).notifyDataSetChanged();
		}
	}
}
