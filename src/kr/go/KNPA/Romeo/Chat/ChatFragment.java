/**
 * 
 */
package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.Survey.SurveyComposeFragment;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;


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
	
	public static void unsetCurrentRoom() {
		_currentRoom = null;
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


		OnClickListener lbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		OnClickListener rbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SurveyComposeFragment.class);
				//TODO
				startActivity(intent);
			}
		};
		
		switch(this.type) {
		case Chat.TYPE_MEETING :
			view = inflater.inflate(R.layout.chat_fragment, container, false);
			initNavigationBar(
							view, 
							R.string.meetingTitle, 
							true, 
							false, 
							R.string.menu, 
							R.string.dummy, 
							lbbOnClickListener, rbbOnClickListener);

			break;
		case Chat.TYPE_COMMAND :
			view = inflater.inflate(R.layout.chat_fragment, container, false);
			initNavigationBar(
					view, 
					R.string.commandTitle, 
					true, 
					false, 
					R.string.menu, 
					R.string.dummy, 
					lbbOnClickListener, rbbOnClickListener);
			break;
		}
				
		listView = (RoomListView)initListViewWithType(this.type, R.id.roomListView, view);

		return view;
	}
	
	
	// Message Receiving
	public static void receive(Chat chat) {
		RoomFragment rf = getCurrentRoom();
		if(rf !=null && rf.room != null && rf.room.roomCode !=null && rf.room.roomCode.equals(chat.getRoomCode())){//ra.room!=null && ra.room.roomCode == chat.roomCode) {
			rf.receive(chat);
		}
		
		ChatFragment f = null;
		if(chat.type % 100 == Chat.TYPE_COMMAND) 
			f = _commandFragment;
		else if(chat.type % 100 == Chat.TYPE_MEETING)
			f = _meetingFragment;
	
		if(f == null) return;
		final RoomListView lv = f.getListView();
		
		if(lv == null) return;
		f.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lv.refresh();	
			}
		});
	}

}