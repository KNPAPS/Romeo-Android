package kr.go.KNPA.Romeo.Chat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ChatFragment의 RoomListView 중 하나의 Cell을 누르면 RoomFragment로 진입하게 된다.
 */
public class RoomFragment extends ListFragment {
	public Room room;		//< 하나의 Room에 대한 Model 이다.
	private Handler mHandler;
	private final int NUMBER_OF_INITIAL_RECENT_ITEM = 10;
	ChatListAdapter mAdapter;
	/**
	 * @name Constructor
	 * @{
	 */
	public RoomFragment(Room room) { 
		
		this.room = room; 
		mHandler = new RoomHandler(RoomFragment.this);
	}
	/** @} */

	public void toast(int i) {
		if ( i==1) {
			Toast.makeText(getActivity(), "메세지 전송 성공", Toast.LENGTH_LONG).show();
		}else {
			Toast.makeText(getActivity(), "메세지 전송 실패", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * @name View Life-Cycle
	 * @{
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ChatFragment.setCurrentRoom(this);


        Thread refreshThread = new RefreshThread(NUMBER_OF_INITIAL_RECENT_ITEM);
        refreshThread.start();
        
		getListView().setSelectionFromTop(getListView().getCount(), 0);
		
		// 방에 입장하는 순간 리스트 뷰 내의 모든 챗들 다 checked로..
		// 방에 입장하면 메시지들을 화면에 출력하게 될 것이고, 출력하는 순간 setChecked로 바꾸기로 한다. (ChatListAdatper)
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatFragment.unsetCurrentRoom();
	}
	
	/**
	 * onCreateView 역할을 함
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.chat_room_fragment, container, false);
        
		//nav bar button setting 
		OnClickListener lbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		OnClickListener rbbOnClickListener = new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), RoomSettingActivity.class);//MemberSearch.class);
				startActivity(intent);
			}
		};
		
		// Navigation Bar Initilize
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility(View.VISIBLE);
		rbb.setVisibility(View.VISIBLE);
		
		lbb.setText(getString(R.string.menu));	
		rbb.setText(getString(R.string.edit));
		
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText(getString( this.room.type==Chat.TYPE_COMMAND?R.string.commandTitle:R.string.meetingTitle ));
		
		if(lbb.getVisibility() == View.VISIBLE) lbb.setOnClickListener(lbbOnClickListener);
		if(rbb.getVisibility() == View.VISIBLE) rbb.setOnClickListener(rbbOnClickListener);
		
		// Room Setting
		final EditText inputET = (EditText)view.findViewById(R.id.edit);
		final Button submitBT = (Button)view.findViewById(R.id.submit);
		
		// 채팅 입력 창에 글씨 숫자에 따라 전송 버튼을 활성화/비활성화 하기 위한 Listener
		inputET.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 	{ /* 눌린 키 반영하기 전 */ }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 	{ /* 눌린 키 반영 후 */		}
			
			@Override
			public void afterTextChanged(Editable s) {	/* 결과 */		
				if(s.length() > 0) submitBT.setEnabled(true);
				else	submitBT.setEnabled(false);
			}
		});
		
		/**
		 * 채팅 전송 클릭리스너
		 */
		submitBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//채팅 input text
				EditText et = inputET;
				
				if ( room.usersIdx.size() == 0 ) {
					//TODO 리시버가 한 명도 없는 상태에서는 메세지 못 보냄
					return;
				}
				
				
				if(room.roomCode ==null) {
					// 만약 roomCode가 없다면 새로 만들어진 방이다.
					ArrayList<String> userIdxs = new ArrayList<String>(room.usersIdx.size());
					for(int i=0; i<room.usersIdx.size(); i++) {
						userIdxs.add(room.usersIdx.get(i));
					}
					
					// 새로 만드는 방에 대한 roomCode를 생성하고, local DB에 방을 생성한다.
					room.roomCode = Room.makeRoomCode(getActivity());
					DBProcManager.sharedManager(getActivity()).chat().createRoom(userIdxs, room.type, room.roomCode);
				}
				
				String sender = UserInfo.getUserIdx(getActivity());
				ArrayList<String> receivers = room.getUsersIdx(getActivity());	
					
				Chat newChat = Chat.chatOnSend(room.type, et.getText().toString(), sender, receivers, System.currentTimeMillis(), room.roomCode, Chat.CONTENT_TYPE_TEXT);
				
				Thread sendThread = new ChatSendThread(newChat);
				sendThread.start();
				
				// reset input EditText
				et.setText("");
			
//				// 뷰에 추가 (refresh)?
//				getListView().refresh();
//				getListView().scrollToBottom();
//				ChatFragment.chatFragment(room.type).listView.refresh();
			}
		});
		
		return view;
	}
	
	// Message Receiving
	public void receive(Chat chat) {

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// 로드할 메시지 갯수를 하나 증가시킨만큼 다시 모두 불러오는 식으로 Refresh를 진행한다.

				String s = new String();
				
			}
		});
			
	}

	/** @} */
	
	
	
	/*
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO : 대화방에 사용자 추가	
		if(requestCode == MemberSearch.REQUEST_CODE) {
			if(resultCode == Activity.RESULT_OK) {
				ArrayList<String> receiversIdxs = data.getExtras().getStringArrayList("receivers");
				ArrayList<User> newUsers = new ArrayList<User>();
				
				for(int i=0; i< receiversIdxs.size(); i++ ){
					User user = User.getUserWithIdx(receiversIdxs.get(i));
					if(room.users.contains(user)) continue;
					newUsers.add(user);
				}
				room.users.addAll(newUsers);
				
				// TODO 초대했다는 메시지를 띄운다.
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	*/
	
	private static class RoomHandler extends Handler {
		private final WeakReference<RoomFragment> mReference;
		/**
		 * @name 메세지 종류
		 * ChatSendThread로부터 넘겨받는 Message 객체의 msg.what에 설정되어 있는 값에 대한 구분\n
		 * msg.what의 값을 이용해 어떤 상황에서 메세지를 보낸건지 구별하여 핸들러는 해당되는 액션을 취한다.
		 * {@
		 */
		//! 메세지 전송 전에 DB에 STATE_SENDING 상태로 저장
		public static final int REFRESH = 1;
		
		//! 메세지 전송 성공
		public static final int SENDING_SUCCEED= 2;
				
		//! 메세지 전송 실패
		public static final int SENDING_FAILED = 3;
						
		/**@}*/
		
		public RoomHandler(RoomFragment roomFragment) {
			this.mReference = new WeakReference<RoomFragment>(roomFragment);
		}
		
		@Override
		public void handleMessage(Message msg) {
			RoomFragment roomFragment = mReference.get();
			
			if ( roomFragment != null ) {
				
				switch(msg.what) {
				case REFRESH:
					Cursor cursor = (Cursor)msg.obj;
					
					if ( roomFragment.getListAdapter() == null ) {
						roomFragment.mAdapter = new ChatListAdapter(roomFragment.getActivity(), cursor, roomFragment.room.type);
						roomFragment.setListAdapter(roomFragment.mAdapter);						
					} else {
						roomFragment.mAdapter.changeCursor(cursor);
						roomFragment.mAdapter.notifyDataSetChanged();
					}
					break;
				case SENDING_SUCCEED:
					roomFragment.toast(1);
					ViewGroup parent = (ViewGroup)roomFragment.getView();
					View chatSending = parent.findViewWithTag(msg.obj).findViewById(R.id.chatSending);
					int index = parent.indexOfChild(chatSending);
					parent.removeView(chatSending);
					
					Button goUncheckersBtn = (Button)roomFragment.getActivity().getLayoutInflater().inflate(R.id.goUnchecked, parent, false);
					
				    parent.addView(goUncheckersBtn, index);
					
					roomFragment.getListView()
								.setSelectionFromTop( roomFragment.getListView().getCount(), 0);
					break;
				case SENDING_FAILED:
					roomFragment.toast(2);
					break;
				}

			}
			super.handleMessage(msg);
		}
	}
	
	private class RefreshThread extends Thread {
		private int nItems;
		public RefreshThread(int nItems) {
			this.nItems = nItems;
		}
		@Override
		public void run() {
			//리스트뷰 커서 새로 불러오기
			Cursor newCursor = DBProcManager.sharedManager(getActivity())
								.chat()
								.getChatList(room.roomCode, 0, nItems);

			//핸들러에 새 커서를 넘겨서 채팅 목록에 보내고 있는 채팅 추가
			Message msgOnNewCursor = mHandler.obtainMessage();
			msgOnNewCursor.what = RoomHandler.REFRESH; 
			msgOnNewCursor.obj = newCursor;
			mHandler.sendMessage(msgOnNewCursor);
			super.run();
		}
	}
	
	private class ChatSendThread extends Thread {
		private Chat chat;
		public ChatSendThread(Chat chat) {
			this.chat = chat;
		}
		
		@Override
		public void run() {
			
			//로컬 DB에 저장하고 채팅해쉬를 발급받아옴
			String chatHash = DBProcManager.sharedManager(getActivity())
								.chat()
								.saveChatOnSend(chat.roomCode, chat.senderIdx, chat.content, chat.contentType, chat.TS, Chat.STATE_SENDING);

			//채팅해쉬를 채팅 객체에 설정함
			chat.idx = chatHash;
			
			int nItems = getListView().getCount();
			
			//리스트뷰 커서 새로 불러오기
			Cursor newCursor = DBProcManager.sharedManager(getActivity())
								.chat()
								.getChatList(chat.roomCode, 0, nItems+1);

			//핸들러에 새 커서를 넘겨서 채팅 목록에 보내고 있는 채팅 추가
			Message msgOnNewCursor = mHandler.obtainMessage();
			msgOnNewCursor.what = RoomHandler.REFRESH; 
			msgOnNewCursor.obj = newCursor;
			mHandler.sendMessage(msgOnNewCursor);
			
			//채팅 객체를 서버에 전송			
			Data reqData = new Data().add(0, KEY._MESSAGE, chat);
			Payload request = new Payload().setEvent(Event.Message.send()).setData(reqData);
			Connection conn = new Connection().requestPayload(request).async(false);
			conn.request();
			
			//응답 받아와서 성공여부를 핸들러에 알림
			Payload response = conn.getResponsePayload();
			if ( response != null && response.getStatusCode() == StatusCode.SUCCESS ) {
				DBProcManager.sharedManager(getActivity()).chat().updateChatState(chat.idx, Chat.STATE_SUCCESS);
				Message msgOnSucceed = mHandler.obtainMessage();
				msgOnSucceed.what = RoomHandler.SENDING_SUCCEED; 
				msgOnSucceed.obj = chatHash;
				mHandler.sendMessage(msgOnSucceed);
			} else {
				DBProcManager.sharedManager(getActivity()).chat().updateChatState(chat.idx, Chat.STATE_FAIL);
				Message msgOnFail = mHandler.obtainMessage();
				msgOnFail.what = RoomHandler.SENDING_FAILED; 
				msgOnFail.obj = chatHash;
				mHandler.sendMessage(msgOnFail);
			}
			
			super.run();
		}
	}
	
	
	
	public static class RoomSettingActivity extends PreferenceActivity {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.room);
		}
		
		
	}
}
