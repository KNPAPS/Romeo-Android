/**
 * 
 */
package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * 메뉴에서 Chat 종류의 기능을 선택했을 때 만날 수 있는 Fragment. \n
 * Meeting과 Command가 존재한다.
 */
public class ChatFragment extends RomeoFragment {

	private static final String TAG = ChatFragment.class.getSimpleName();
	private Handler mHandler;
	/**
	 * @name Constructors
	 * @{
	 */
	public ChatFragment(int subType) {
		super(subType);
		
	}
	/** @} */
	
	/**
	 * @name Singleton (Fragment)
	 * @{
	 */
	private static ChatFragment _commandFragment = null;
	private static ChatFragment _meetingFragment = null;
	public static ChatFragment chatFragment(int subType) {
		ChatFragment f = null;
		if(subType == Chat.TYPE_COMMAND) {
			if(_commandFragment == null)
				_commandFragment = new ChatFragment(Chat.TYPE_COMMAND);
			f = _commandFragment;
		} else {
			if(_meetingFragment == null)
				_meetingFragment = new ChatFragment(Chat.TYPE_MEETING);
			f = _meetingFragment;
		}
		
		return f;
	}
	/** @} */
	
	/** 
	 * @name Singleton (Room)
	 * 현재 특정 Room Fragment안에 입장해 있다면, 해당 RoomFragment를 반환한다.
	 * @{
	 */
	private static RoomFragment _currentRoom = null;
	/*
	 * Manager Current Room
	 */
	public static RoomFragment 	getCurrentRoom() 					{	return _currentRoom;	}
	public static void		 	setCurrentRoom(RoomFragment ra) 	{	_currentRoom = ra;		}
	public static void 			unsetCurrentRoom() 					{	_currentRoom = null;	}
	/** @} */
	
	// Manage List View
	public RoomListView getListView() {
		return (RoomListView)this.listView;
	}
	
	// Manage Fragment Life-cycle
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(subType == Chat.TYPE_COMMAND) {
			_commandFragment = null;
		} else if(subType==Chat.TYPE_MEETING) {
			_meetingFragment = null;
		}
	}
	
	//onCreateView
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		
		OnClickListener lbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		OnClickListener rbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MemberSearch.class);
				startActivityForResult(intent, MemberSearch.REQUEST_CODE);
			}
		};
		
		switch(this.subType) {
		case Chat.TYPE_MEETING :
			view = inflater.inflate(R.layout.chat_fragment, container, false);
			initNavigationBar(
							view, 
							R.string.meetingTitle, 
							true, 
							true, 
							R.string.menu, 
							R.string.add, 
							lbbOnClickListener, rbbOnClickListener);

			break;
		case Chat.TYPE_COMMAND :
			view = inflater.inflate(R.layout.chat_fragment, container, false);
			initNavigationBar(
					view, 
					R.string.commandTitle, 
					true, 
					true, 
					R.string.menu, 
					R.string.add, 
					lbbOnClickListener, rbbOnClickListener);
			break;
		}
				
		listView = (RoomListView)initListViewWithType(this.subType, R.id.roomListView, view);

		return view;
	}
	
	
	/**
	 * (Message Receiving) GCMMessageManager에서 onMessage, onChat을 거쳐 이 메서드가 호출되게 된다. \n
	 * GCM 모듈을 통해서 진입했으므로 당연히 별도의 thread상에서 작업이 이루어진다.
	 * @param chat 새로 도착한 chat의 instance
	 */

	public static void receive(Chat chat) {
		// 현재 방에 대해서 작업을 한다.
		// 현재 특정 방안에 입장해 있다면, 그 방의 인스턴스에도 메시지를 전달한다.
		RoomFragment currentRoomFragment = getCurrentRoom();
		
		if(	currentRoomFragment !=null && 
			currentRoomFragment.room != null && 
			currentRoomFragment.room.getRoomCode() !=null &&
			currentRoomFragment.room.getRoomCode().equals(chat.roomCode)){
		
			currentRoomFragment.receive(chat);
		}
		
		// 현재 Fragment의 ListView에도 메시지를 전달하여 refresh할 수 있도록 한다.
		ChatFragment f = null;
		if(chat.subType() == Chat.TYPE_COMMAND) 
			f = _commandFragment;
		else if(chat.subType() == Chat.TYPE_MEETING)
			f = _meetingFragment;
	
		if(f == null) return;
		final RoomListView lv = f.getListView();
		
		if(lv == null) return;
		f.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lv.refresh();	// RoomListView
			}
		});
	}

	
	/**
	 * ChatFragment의 RoomListView 상단의 NavigationBar의 버튼 중 새 채팅을 시작하는 버튼이 있다. \n
	 * 이 버튼을 누르면 조건부 검색 창이 떠서 사람들을 선택할 수 있고, 그 결과를 이 onActivityResult 에서 처리한다.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MemberSearch.REQUEST_CODE) {
			if(resultCode == MemberSearch.RESULT_OK) {

				WaiterView.showDialog(getActivity());
				//채팅을 할 사람들 목록을 받음 
				final ArrayList<String> receiversIdxs = data.getExtras().getStringArrayList(MemberSearch.KEY_RESULT_USERS_IDX);
				
				if ( receiversIdxs.size() == 0 ) {
					return;
				}
				
				mHandler = new ChatFragmentHandler();
				
				new Thread(){
					@Override
					public void run() {
						super.run();
						
						
						//일단 roomCode가 없는 빈 room 객체를 만듬
						Room room = null;
						String roomCode = null;
						//만약 리시버가 1명이라면 기존에 있는 방이 있는지 검사
						if ( receiversIdxs.size() == 1 ) {
							 
							roomCode = DBProcManager.sharedManager(getActivity())
									 				.chat().getRoomCode(subType, receiversIdxs.get(0));
							
						} 
						//기존에 있는 방이 있다면 roomCode를 지정해 생성함
						if ( roomCode != null ) {
							room = new Room(getActivity(), roomCode);
						} else {
							ArrayList<String> chatters = new ArrayList<String>();
							chatters.addAll(receiversIdxs);
							chatters.add( UserInfo.getUserIdx(getActivity()) );
							room = new Room(getActivity(),subType,chatters);
						}
						Message msg = mHandler.obtainMessage();
						msg.what = ChatFragmentHandler.ENTER_ROOM;
						msg.obj = room;
						mHandler.sendMessage(msg);

					}
				}.start();
				

			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	static class ChatFragmentHandler extends Handler {
		public static final int ENTER_ROOM = 1;
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ENTER_ROOM ) {
				RoomFragment fragment = new RoomFragment((Room)msg.obj);
				MainActivity.sharedActivity().pushContent(fragment);
				WaiterView.dismissDialog(MainActivity.sharedActivity());
			}
			super.handleMessage(msg);
		}
	}
}