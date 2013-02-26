package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RoomFragment extends Fragment {

	// Database
	private	DBManager 		dbManager;
	private	SQLiteDatabase	db;
	
	public Room room;
	
	// Constructor
	public RoomFragment() {
		// Empty Constructor
	}
	
	public RoomFragment(Room room) {
		this.room = room;
	}

	// Manage List View
	private ChatListView getListView() {
		View view = ((ViewGroup)getView());
		ChatListView lv = null;
		
		if(view!=null) {
			lv = (ChatListView)view.findViewById(R.id.chatListView);
		}
		
		return lv;
	}

	// View Life-Cycle
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	
		dbManager = new DBManager(getActivity());
		db = dbManager.getWritableDatabase();
		
		ChatListView clv = getListView();
		clv.setDatabase(db);
		// refresh?
		clv.scrollToBottom();

	}

	@Override
	public void onPause() {
		super.onPause();
		ChatListView lv = getListView();
		lv.unsetDatabase();
		db.close();
		db = null;
		
		dbManager.close();
		dbManager = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatFragment.unsetCurrentRoom();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ChatFragment.setCurrentRoom(this);
		
		String chatRoomTitle = null; // TODO //b.getString("title");
		if(chatRoomTitle == null) chatRoomTitle = this.room.type==Chat.TYPE_COMMAND?getString(R.string.commandTitle):getString(R.string.meetingTitle);
		
		View view = inflater.inflate(R.layout.chat_room_fragment, null, false);
		
		ViewGroup navBar = (ViewGroup)view.findViewById(R.id.navigationBar);
		TextView navBarTitleView = (TextView)navBar.findViewById(R.id.title);
		navBarTitleView.setText(chatRoomTitle);
		
		Button lbb = (Button)navBar.findViewById(R.id.left_bar_button);
		lbb.setText(R.string.menu);
		
		if(lbb.getVisibility() == View.VISIBLE) {
			lbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MainActivity.sharedActivity().toggle();
				}
			});
		}
		
		Button rbb = (Button)navBar.findViewById(R.id.right_bar_button);
		rbb.setText(R.string.edit);
		
		ChatListView listView = (ChatListView)view.findViewById(R.id.chatListView);
		listView.setRoom(room);
		return view; 
	}
	
	// Message Receiving
	public void receive(Chat chat) {
		//http://stackoverflow.com/questions/4486034/get-root-view-from-current-activity
		//getWindow().getDecorView().findViewById(android.R.id.content) : 
		//I've noticed that this view appears to include the status bar, so if you're looking for the visible part of your activity, use below
		//((ViewGroup)findViewById(android.R.id.content)).getChildAt(0)
		final ChatListView lv = getListView();
		if(lv == null)  return;
				
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lv.increaseNumberOfItemsBy(1);
				lv.refresh();	
			}
		});
			
	}
	
	
}
