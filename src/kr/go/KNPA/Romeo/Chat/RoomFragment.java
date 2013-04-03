package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.DBManager;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RoomFragment extends RomeoFragment {

	public Room room;
	
	// Constructor
	public RoomFragment() {	}
	
	public RoomFragment(Room room) {
		this.room = room;
	}

	// Manage List View
	public ChatListView getListView() {
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
		
		// 방에 입장하는 순간 리스트 뷰 내의 모든 챗들 다 checked로..
	}

	@Override
	public void onResume() {
		ChatFragment.setCurrentRoom(this);
		super.onResume();
		getListView().scrollToBottom();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatFragment.unsetCurrentRoom();
	}
	
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
		
		View view = inflater.inflate(R.layout.chat_room_fragment, null, false);
		initNavigationBar(
				view, 
				this.room.type==Chat.TYPE_COMMAND?R.string.commandTitle:R.string.meetingTitle, 
				true, 
				true, 
				R.string.menu, 
				R.string.edit, 
				lbbOnClickListener, rbbOnClickListener);
		
		ChatListView listView = (ChatListView)view.findViewById(R.id.chatListView);
		listView.setRoom(room);
		
		
		
		
		// Room Setting
		final EditText inputET = (EditText)view.findViewById(R.id.edit);
		final Button submitBT = (Button)view.findViewById(R.id.submit);
		
		inputET.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { // 눌린 키 반영하기 전
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {// 눌린 키 반영 후
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {	// 결과		
				if(s.length() > 0) submitBT.setEnabled(true);
				else	submitBT.setEnabled(false);
				Log.i("after", "e : "+submitBT.isEnabled());
			}
		});
		
		submitBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et = inputET;
				

				
				//long senderIdx = UserInfo.getUserIdx(getActivity());
//				User sender = User.getUserWithIdx(senderIdx);
//				
//				ArrayList<User> roomUsers = room.users;
//				ArrayList<User> receivers = User.usersRemoveUserHavingIndex(roomUsers, senderIdx);
//				
//				
//				Appendix adx = new Appendix();
//				String roomCode = room.roomCode;
//				if(roomCode ==null) roomCode = senderIdx+":"+System.currentTimeMillis(); // TODO 같은방 채팅, 새 방 채팅.
//				Appendix.Attachment att = new Appendix.Attachment("roomCode", Appendix.makeType(Appendix.TYPE_1_PRIMITIVE, Appendix.TYPE_2_STRING), null, roomCode);
//				adx.add(att);
//				
//				
//				Chat chat = new Chat.Builder()
//									//.idx()
//									.type(room.type)
//									.content(et.getText().toString())
//									.appendix(adx)
//									.sender(sender)
//									.receivers(receivers)
//									.TS(System.currentTimeMillis())
//									.checked(true)
//									//.checkTS()
//									.toChatBuilder()
//									.build();
				
				// 마무리
				et.setText("");
				
				// sending
//				chat.send(getActivity(), room);
				
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
				lv.increaseNumberOfItemsBy(1);
				lv.refresh();	
			}
		});
			
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == MemberSearch.REQUEST_CODE) {
			if(resultCode == Activity.RESULT_OK) {
				long[] receiversIdx = data.getExtras().getLongArray("receivers");
				ArrayList<MemberManager> newUsers = new ArrayList<MemberManager>();
				
				for(int i=0; i< receiversIdx.length; i++ ){
					MemberManager user = MemberManager.getUserWithIdx(receiversIdx[i]);
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
	
	
	
	
	
	public static class RoomSettingActivity extends PreferenceActivity {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.room);
		}
		
		
	}
}
