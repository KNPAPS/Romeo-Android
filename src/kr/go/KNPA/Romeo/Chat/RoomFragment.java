package kr.go.KNPA.Romeo.Chat;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.MimeType;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/*
 * ChatFragment의 RoomListView 중 하나의 Cell을 누르면 RoomFragment로 진입하게 된다.
 */
public class RoomFragment extends RomeoFragment {
	private static final String TAG = RoomFragment.class.getSimpleName();
	
	public Room room;		//< 하나의 Room에 대한 Model 이다.
	private Handler mHandler;
	
	/**
	 * @name Constructor
	 * @{
	 */
	public RoomFragment(Room room) {
		this.room = room; 
		mHandler = new RoomHandler(RoomFragment.this);
	}
	/** @} */
	
	/**
	 * @name View Life-Cycle
	 * @{
	 */
	@Override
	public void onResume() {
		super.onResume();
		ChatFragment.setCurrentRoom(this);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatFragment.unsetCurrentRoom();
	}
	
	// Message Receiving
	public void receive(Chat chat) {

		Cursor c = getListView().query(getListView().getNumberOfItems()+1);
		Message msg = mHandler.obtainMessage();
		msg.what = RoomHandler.REFRESH;
		msg.obj = c;
		mHandler.sendMessage(msg);
		
	}

	@Override
	public ChatListView getListView() {
		return (ChatListView) listView;
	}
	
	/**
	 * onCreateView 역할
	 */
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		
		String title = room.getTitle();

		initNavigationBar(
			view, 
			title, 
			true, 
			true, 
			getActivity().getResources().getString(R.string.menu), 
			getActivity().getResources().getString(R.string.edit), 
			lbbOnClickListener, 
			rbbOnClickListener);
		
		// Room Setting
		final Button addApendix = (Button)view.findViewById(R.id.addAppendix);
		final EditText inputET = (EditText)view.findViewById(R.id.edit);
		final Button submitBT = (Button)view.findViewById(R.id.submit);
		
		// 사진 보내기 클릭 리스너
		addApendix.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			    AlertDialog.Builder chooseDlg = new AlertDialog.Builder(getActivity());
			    chooseDlg.setTitle("사진 보내기");
			    
			    ArrayList<String> array = new ArrayList<String>();
			    array.add("사진 촬영");
			    array.add("앨범에서 선택");
			    
			    ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(getActivity(), R.layout.dialog_menu_cell, array);
			    
			    chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener(){
			    	@Override
			    	public void onClick(DialogInterface dialog, int which) {
			    		switch(which){
			    		case 0://사진 촬영
			    			doTakePhotoAction();
			    			break;
			    		case 1://앨범에서선택
			    			doTakeAlbumAction();
			    			break;
			    		}
			    	}
			    });
			    
			    chooseDlg.setCancelable(true);
			    chooseDlg.show();
			}
		});
        

		
		// 채팅 입력 창에 글씨 숫자에 따라 전송 버튼을 활성화/비활성화 하기 위한 Listener
		inputET.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 	{ /* 눌린 키 반영하기 전 */ }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 	{ /* 눌린 키 반영 후 */		}
			
			@Override
			public void afterTextChanged(Editable s) {	/* 결과 */		
				if(s.length() > 0) {
					submitBT.setEnabled(true);
				} else {
					submitBT.setEnabled(false);
				}
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
				
				if ( room.getChatters().size() == 1 ) {
					return;
				}
				
				//만약 roomCode가 없다면 새로 만들어진 방이므로 방 생성 루틴 실행
				//다른 쓰레드에서 통신과 DB작업을 하는동안 UI 쓰레드는 다이얼로그
				//띄워놓고 대기함
				if(room.isCreated() == false) {
					startCreateRoomThread();
				}
				
				String senderIdx = UserInfo.getUserIdx(getActivity());
				ArrayList<String> receivers = new ArrayList<String>(room.getChatters().size()-1);
				
				for( int i=0; i<room.getChatters().size(); i++ ) {
					if ( room.getChatters().get(i).equals(senderIdx) == false ) {
						receivers.add(room.getChatters().get(i));
					}
				}
				
				Chat newChat = new Chat(
									null,
									room.getType(), 
									et.getText().toString(), 
									senderIdx, 
									receivers, 
									false,
									System.currentTimeMillis(),
									true,
									System.currentTimeMillis(),
									room.getRoomCode(), 
									Chat.CONTENT_TYPE_TEXT);
				
				new ChatSendThread(newChat).start();
				
				// reset input EditText
				et.setText("");
				// 채팅방목록 refresh
				ChatFragment.chatFragment(room.getType()).listView.refresh();
			}
		});

		listView = (ChatListView) initListViewWithType(room.getType(), R.id.chatListView, view);
		((ChatListView)listView).setRoom(room);

		return view;
	}
	
	public static class RoomSettingActivity extends PreferenceActivity {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.room);
		}
		
		@Override
		public SharedPreferences getSharedPreferences(String name, int mode) {
			// TODO Auto-generated method stub
			return super.getSharedPreferences(name, mode);
		}
		
		
		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			// TODO Auto-generated method stub
			super.onListItemClick(l, v, position, id);
			Toast.makeText(this, "선택", Toast.LENGTH_LONG).show();
		}
	}

	/** @} */
		
	private static class RoomHandler extends Handler {
		private final WeakReference<RoomFragment> mReference;
		/**
		 * @name 메세지 종류
		 * ChatSendThread로부터 넘겨받는 Message 객체의 msg.what에 설정되어 있는 값에 대한 구분\n
		 * msg.what의 값을 이용해 어떤 상황에서 메세지를 보낸건지 구별하여 핸들러는 해당되는 액션을 취한다.
		 * {@
		 */
		//! 채팅 전송 전에 DB에 STATE_SENDING 상태로 저장
		public static final int REFRESH = 1;
		
		//! 채팅 전송 성공
		public static final int SENDING_SUCCEED= 2;
				
		//! 채팅 전송 실패
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
					roomFragment.getListView().increaseNumberOfItemsBy(1);
					roomFragment.getListView().refresh((Cursor)msg.obj);
					roomFragment.getListView().scrollToBottom();
					break;
				case SENDING_SUCCEED:
					roomFragment.getListView().refresh((Cursor)msg.obj);
					roomFragment.getListView().scrollToBottom();
					break;
				case SENDING_FAILED:
					break;
				}
	
			}
			super.handleMessage(msg);
		}
	}

	private class ChatSendThread extends Thread {
		private Chat chat;
		private String filePath;
		public ChatSendThread(Chat chat) {
			this.chat = chat;
		}
		public ChatSendThread(Chat chat, String filePath) {
			this.chat = chat;
			this.filePath = filePath;
		}
		
		@Override
		public void run() {
			
			//로컬 DB에 저장하고 채팅해쉬를 발급받아옴
			String chatHash = DBProcManager.sharedManager(getActivity())
								.chat()
								.saveChatOnSend(chat.roomCode, chat.senderIdx, chat.content, chat.contentType, chat.TS, Chat.STATE_SENDING);
	
			//채팅해쉬를 채팅 객체에 설정함
			chat.idx = chatHash;
			if ( chat.contentType == Chat.CONTENT_TYPE_PICTURE ) {
				chat.content = chatHash;
			}
			
			//핸들러에 새 커서를 넘겨서 채팅 목록에 보내고 있는 채팅 추가
			Message msgOnNewCursor = mHandler.obtainMessage();
			msgOnNewCursor.what = RoomHandler.REFRESH; 
			msgOnNewCursor.obj = getListView().query( getListView().getNumberOfItems() );
			mHandler.sendMessage(msgOnNewCursor);
			
			Data reqData = new Data().add(0, KEY._MESSAGE, chat);
			Payload request = new Payload().setEvent(Event.Message.send()).setData(reqData);
			Connection conn = new Connection().requestPayload(request).async(false);
			
			if ( chat.contentType == Chat.CONTENT_TYPE_PICTURE ) {
				conn.attachFile(filePath).contentType(MimeType.jpeg);
			}
			
			conn.request();
			
			//응답 받아와서 성공여부를 핸들러에 알림
			Payload response = conn.getResponsePayload();
			if ( response != null && response.getStatusCode() == StatusCode.SUCCESS ) {
				DBProcManager.sharedManager(getActivity()).chat().updateChatState(chat.idx, Chat.STATE_SUCCESS);
				Message msgOnSucceed = mHandler.obtainMessage();
				msgOnSucceed.what = RoomHandler.SENDING_SUCCEED; 
				msgOnSucceed.obj = getListView().query( getListView().getNumberOfItems() );
				mHandler.sendMessage(msgOnSucceed);
			} else {
				DBProcManager.sharedManager(getActivity()).chat().updateChatState(chat.idx, Chat.STATE_FAIL);
				Message msgOnFail = mHandler.obtainMessage();
				msgOnFail.what = RoomHandler.SENDING_FAILED; 
				msgOnFail.obj = getListView().query( getListView().getNumberOfItems() );
				mHandler.sendMessage(msgOnFail);
			}			
			super.run();
		}
	}

	private void startCreateRoomThread() {
		Thread newRoomThread = new Thread() {
			@Override
			public void run() {
				super.run();
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						WaiterView.showDialog(getActivity());
					}
				});
				
				//방 생성
				if ( room.create() == false ) {
					mHandler.post(new Runnable(){
						@Override
						public void run() {
							WaiterView.dismissDialog(getActivity());
							Toast.makeText(getActivity(), "방 생성 실패", Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					mHandler.post(new Runnable(){
						@Override
						public void run() {
							WaiterView.dismissDialog(getActivity());
						}
					});
				}
			}
		};
		
		newRoomThread.start();
		try {
			newRoomThread.join();
		} catch (InterruptedException e) {
			Log.e(TAG,"쓰레드 interrupt로 인한 방 생성 실패 "+e.getMessage());
			e.printStackTrace();
		}
	}

	private Uri mImageCaptureUri;
	
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    
    /**
     * 카메라에서 이미지 가져오기
     */
	private void doTakePhotoAction() {
      /*
       * 참고 해볼곳
       * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
       * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
       * http://www.damonkohler.com/2009/02/android-recipes.html
       * http://www.firstclown.us/tag/android/
       */

      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

      // 찍은 사진을 저장할 경로를 설정함
      Date now = new Date();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
      
      String url = "sdcard/DCIM/Camera/DAON_" + format.format(now) + ".jpg";
      
      mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
      intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
      
      startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    
    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction() {
      // 앨범 호출
      Intent intent = new Intent(Intent.ACTION_PICK);
      intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
      startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(resultCode != Activity.RESULT_OK) {
        return;
      }

      switch(requestCode) {
        case PICK_FROM_ALBUM:
          mImageCaptureUri = data.getData();
		case PICK_FROM_CAMERA:
		  break;
		default:
			return;
      }
      Cursor c = getActivity().getContentResolver().query(mImageCaptureUri,null,null,null,null);
      c.moveToNext();
      
      String filePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
      c.close();
      sendImage(filePath);
    }
    
	public void sendImage(String filePath) {
		
		if ( room.getChatters().size() == 1 ) {
			return;
		}
		
		//만약 roomCode가 없다면 새로 만들어진 방이므로 방 생성 루틴 실행
		//다른 쓰레드에서 통신과 DB작업을 하는동안 UI 쓰레드는 다이얼로그
		//띄워놓고 대기함
		if(room.isCreated() == false) {
			startCreateRoomThread();
		}
		
		String senderIdx = UserInfo.getUserIdx(getActivity());
		ArrayList<String> receivers = new ArrayList<String>(room.getChatters().size()-1);
		
		for( int i=0; i<room.getChatters().size(); i++ ) {
			if ( room.getChatters().get(i).equals(senderIdx) == false ) {
				receivers.add(room.getChatters().get(i));
			}
		}
		
		Chat newChat = new Chat(
							null,
							room.getType(), 
							null, 
							senderIdx, 
							receivers, 
							false,
							System.currentTimeMillis(),
							true,
							System.currentTimeMillis(),
							room.getRoomCode(), 
							Chat.CONTENT_TYPE_PICTURE);
		
		new ChatSendThread(newChat,filePath).start();
		
		// 채팅방목록 refresh
		ChatFragment.chatFragment(room.getType()).listView.refresh();
	}
}
