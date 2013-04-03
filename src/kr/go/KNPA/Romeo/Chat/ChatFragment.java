/**
 * 
 */
package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * 채팅리스트 화면.
 * @author user
 *
 */
public class ChatFragment extends RomeoFragment {

	private static ChatFragment _commandFragment = null;
	private static ChatFragment _meetingFragment = null;
	private static RoomFragment _currentRoom = null;

	// Constructor
	public ChatFragment() {
		this(Chat.TYPE_MEETING);
	}
	
	public ChatFragment(int type) {
		super(type);
	}
	
	public static ChatFragment chatFragment(int type) {
		ChatFragment f = null;
		if(type == Chat.TYPE_COMMAND) {
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
	
	// Manager Current Room
	public static RoomFragment getCurrentRoom() {
		return _currentRoom;
	}
	
	public static void setCurrentRoom(RoomFragment ra) {
		_currentRoom = ra;
	}
	
	// Manage List View
	public RoomListView getListView() {
		View view = ((ViewGroup)getView());
		RoomListView lv = null;
		
		if(view!=null) {
			lv = (RoomListView)view.findViewById(R.id.roomListView);
		}
		
		return lv;
	}
	
	// Manage Fragment Life-cycle
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(type == Chat.TYPE_COMMAND) {
			_commandFragment = null;
		} else if(type==Chat.TYPE_MEETING) {
			_meetingFragment = null;
		}
	}
	
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		
		/**
		 * 메인화면으로..
		 */
		OnClickListener lbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		/**
		 * 새 메세지 추가. 대화상대 찾는 검색 화면으로.
		 */
		OnClickListener rbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MemberSearch.class);
				startActivityForResult(intent, MemberSearch.REQUEST_CODE);
			}
		};
		
		switch(this.type) {
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
				
		listView = (RoomListView)initListViewWithType(this.type, R.id.roomListView, view);

		return view;
	}
	

	/**
	 * 새 메세지를 수신했을 때 동작\n
	 * 기존에 있는 채팅방에 추가된 메세지면 RoomFragment.receive()로 넘기고\n
	 * 기존 roomlist에 없는 채팅이라면 새 roomlistview를 만들고 refresh
	 * @param chat
	 */
	public static void receive(Chat chat) {
		RoomFragment rf = getCurrentRoom();
		if(rf !=null && rf.room != null && rf.room.roomCode !=null && rf.room.roomCode.equals(chat.getRoomHash())){
			rf.receive(chat);
		}
		
		ChatFragment f = null;
		if( chat.getChatType() == Chat.TYPE_COMMAND ) {
			f = _commandFragment;
		} else if ( chat.getChatType() == Chat.TYPE_MEETING ){
			f = _meetingFragment;
		} else {
			return;
		}
	
		final RoomListView lv = f.getListView();
		
		if(lv == null) return;
		f.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lv.refresh();	
			}
		});
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MemberSearch.REQUEST_CODE) {
			if(resultCode != MemberSearch.RESULT_OK) {
				// onError
			} else {
				
				String[] receivers = data.getExtras().getStringArray(MemberSearch.KEY_RECEIVERS);
				
				ArrayList<MemberManager> newUsers = new ArrayList<MemberManager>();
				for(int i=0; i< receivers.length; i++ ){
					MemberManager user = MemberManager.getUserWithHash(receivers[i]);
					// TODO 이미 선택되어 잇는 사람은 ..
					newUsers.add(user);
				}

				
				Room room = new Room();
				room.type = this.type;
				room.roomCode = UserInfo.getPref(getActivity(),UserInfo.PREF_KEY_DEPT_HASH)+":"+System.currentTimeMillis();
				room.users = newUsers;
				RoomFragment fragment = new RoomFragment(room);
				MainActivity.sharedActivity().pushContent(fragment);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}