/**
 * 
 */
package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class ChatFragment extends Fragment {

	private static ChatFragment _commandFragment = null;
	private static ChatFragment _meetingFragment = null;
	private static RoomFragment _currentRoom = null;
	
	private DBManager dbManager;
	private SQLiteDatabase db;
	
	public int type = Chat.NOT_SPECIFIED;
	
	// Constructor
	public ChatFragment() {
		this(Chat.TYPE_MEETING);
	}
	
	public ChatFragment(int type) {
		this.type = type;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		dbManager = new DBManager(getActivity());
		db = dbManager.getWritableDatabase();
		
		RoomListView lv = getListView();
		lv.setDatabase(db);
		lv.refresh();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		RoomListView lv = getListView();
		lv.unsetDatabase();
		db.close();
		db = null;
		
		dbManager.close();
		dbManager = null;
	}
	
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = setup(inflater, container, savedInstanceState);
//		((RoomListView)view.findViewById(R.id.roomListView)).refresh();
		return view;
	}

	private View setup(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		String titleText = null;
		String lbbText = null, rbbText = null;
		boolean lbbIsVisible = false;
		boolean rbbIsVisible = false;
		
		switch(this.type) {
		case Chat.TYPE_MEETING :
			view = inflater.inflate(R.layout.chat_fragment, container, false);
			titleText = getString(R.string.meetingTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.dummy);
			lbbIsVisible = true;
			rbbIsVisible = false;
			break;
		case Chat.TYPE_COMMAND :
		default :
			view = inflater.inflate(R.layout.chat_fragment, container, false);
			titleText = getString(R.string.commandTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.dummy);
			lbbIsVisible = true;
			rbbIsVisible = false;
			break;
		}

		if(view!=null) {
			RoomListView rlv = (RoomListView)view.findViewById(R.id.roomListView);
			rlv.setType(this.type);
		}
		
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbIsVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbIsVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbText);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbText);	}
		
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText(titleText);
		
		if(lbb.getVisibility() == View.VISIBLE) {
			lbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MainActivity.sharedActivity().toggle();
				}
			});
		}
		
		if(rbb.getVisibility() == View.VISIBLE) {
			rbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
		}
		
		return view;
	}
	
	
	// Message Receiving
	public static void receive(Chat chat) {
		RoomFragment rf = getCurrentRoom();
		if(rf !=null && rf.room.roomCode.equals(chat.getRoomCode())){//ra.room!=null && ra.room.roomCode == chat.roomCode) {
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