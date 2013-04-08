package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/*
 * ChatFragment의 RoomListView 중 하나의 Cell을 누르면 RoomFragment로 진입하게 된다.
 */
public class RoomFragment extends RomeoFragment {

	public Room room;		//< 하나의 Room에 대한 Model 이다.
	
	/**
	 * @name Constructor
	 * @{
	 */
	public RoomFragment() {	}
	public RoomFragment(Room room) {	this.room = room;	}
	/** @} */
	
	// Manage List View
	public ChatListView getListView() {
		ChatListView lv = null;
		View view = ((ViewGroup)getView());
		
		if(view!=null)
			lv = (ChatListView)view.findViewById(R.id.chatListView);
		
		return lv;
	}

	/**
	 * @name View Life-Cycle
	 * @{
	 */
	@Override
	public void onResume() {
		ChatFragment.setCurrentRoom(this);
		super.onResume();
		getListView().scrollToBottom();
		
		// 방에 입장하는 순간 리스트 뷰 내의 모든 챗들 다 checked로..
		// 방에 입장하면 메시지들을 화면에 출력하게 될 것이고, 출력하는 순간 setChecked로 바꾸기로 한다. (ChatListAdatper)
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatFragment.unsetCurrentRoom();
	}
	/** @} */
	
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Navigation BarButton ClickListneer
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
		View view = inflater.inflate(R.layout.chat_room_fragment, null, false);
		initNavigationBar(
				view, 
				this.room.type==Chat.TYPE_COMMAND?R.string.commandTitle:R.string.meetingTitle, 
				true, 
				true, 
				R.string.menu, 
				R.string.edit, 
				lbbOnClickListener, rbbOnClickListener);
		
		// listView 인스턴스화
		ChatListView listView = (ChatListView)view.findViewById(R.id.chatListView);
		
		// listView에 room 설정
		listView.setRoom(room);
		
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
		
		// 전송버튼에 대한 ClickListener
		submitBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et = inputET;
				
				if(room.roomCode ==null) {
					// 만약 roomCode가 없다면 새로 만들어진 방이다.
					ArrayList<String> userIdxs = new ArrayList<String>(room.users.size());
					for(int i=0; i<userIdxs.size(); i++) {
						userIdxs.add(room.users.get(i).idx);
					}
					
					// 새로 만드는 방에 대한 roomCode를 생성하고, local DB에 방을 생성한다.
					room.roomCode = Room.makeRoomCode(getActivity());
					DBProcManager.sharedManager(getActivity()).chat().createRoom(userIdxs, room.type, room.roomCode);
				}
				
				User sender = User.getUserWithIdx( UserInfo.getUserIdx(getActivity()) );
				ArrayList<User> receivers = room.getUsers(getActivity());	
					
				Chat.chatOnSend(room.type, et.getText().toString(), sender, receivers, System.currentTimeMillis(), room.roomCode, Chat.CONTENT_TYPE_TEXT).send(getActivity());
				// local DB에 대한 저장은, async로 전송 후 afterSend에서 처리한다.
				
				// 마무리
				et.setText("");
			
				// 뷰에 추가 (refresh)?
				getListView().refresh();
				getListView().scrollToBottom();
				ChatFragment.chatFragment(room.type).listView.refresh();
			}
		});
		
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
				// 로드할 메시지 갯수를 하나 증가시킨만큼 다시 모두 불러오는 식으로 Refresh를 진행한다.
				lv.increaseNumberOfItemsBy(1);
				lv.refresh();	
			}
		});
			
	}
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
	
	
	
	
	public static class RoomSettingActivity extends PreferenceActivity {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.room);
		}
		
		
	}
}
