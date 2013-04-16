package kr.go.KNPA.Romeo.Chat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

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
import kr.go.KNPA.Romeo.Util.Encrypter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/*
 * ChatFragment의 RoomListView 중 하나의 Cell을 누르면 RoomFragment로 진입하게 된다.
 */
public class RoomFragment extends RomeoFragment {
	public Room room;		//< 하나의 Room에 대한 Model 이다.
	private Handler mHandler;
	
	private Uri mImageCaptureUri;
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
	public void onResume() {
		super.onResume();
		ChatFragment.setCurrentRoom(this);

        getListView().refresh();
        getListView().scrollToBottom();
		// 방에 입장하는 순간 리스트 뷰 내의 모든 챗들 다 checked로..
		// 방에 입장하면 메시지들을 화면에 출력하게 될 것이고, 출력하는 순간 setChecked로 바꾸기로 한다. (ChatListAdatper)
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatFragment.unsetCurrentRoom();
	}
	
	public void sendImage(String fileIdx) {
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
		
		Chat newChat = Chat.chatOnSend(room.type, fileIdx, sender, receivers, System.currentTimeMillis(), room.roomCode, Chat.CONTENT_TYPE_PICTURE);
		
		new ChatSendThread(newChat).start();
		
		// 방목록 refresh
		ChatFragment.chatFragment(room.type).listView.refresh();
	}
	
	// Message Receiving
	public void receive(Chat chat) {

		Cursor c = getListView().query(getListView().getNumberOfItems()+1);
		Message msg = mHandler.obtainMessage();
		msg.what = RoomHandler.REFRESH;
		msg.obj = c;
		mHandler.sendMessage(msg);			
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
					roomFragment.getListView().increaseNumberOfItemsBy(1);
					roomFragment.getListView().refresh((Cursor)msg.obj);
					roomFragment.getListView().scrollToBottom();
					break;
				case SENDING_SUCCEED:
					roomFragment.getListView().refresh((Cursor)msg.obj);
					roomFragment.getListView().scrollToBottom();
					break;
				case SENDING_FAILED:
					roomFragment.toast(2);
					break;
				}

			}
			super.handleMessage(msg);
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

			//핸들러에 새 커서를 넘겨서 채팅 목록에 보내고 있는 채팅 추가
			Message msgOnNewCursor = mHandler.obtainMessage();
			msgOnNewCursor.what = RoomHandler.REFRESH; 
			msgOnNewCursor.obj = getListView().query( getListView().getNumberOfItems() );
			mHandler.sendMessage(msgOnNewCursor);
			
			Data reqData = new Data().add(0, KEY._MESSAGE, chat);
			Payload request = new Payload().setEvent(Event.Message.send()).setData(reqData);
			Connection conn = new Connection().requestPayload(request).async(false);
			
			if ( chat.contentType == Chat.CONTENT_TYPE_PICTURE ) {
				conn.attachFile("sdcard/DCIM/"+chat.content+".jpg").contentType(MimeType.jpeg);
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

	@Override
	public ChatListView getListView() {
		return (ChatListView) listView;
	}
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
		initNavigationBar(
				view, 
				this.room.type==Chat.TYPE_COMMAND?R.string.commandTitle:R.string.meetingTitle, 
				true, 
				true, 
				R.string.menu, 
				R.string.edit, 
				lbbOnClickListener, 
				rbbOnClickListener);
		
		// Room Setting
		final Button addApendix = (Button)view.findViewById(R.id.addAppendix);
		final EditText inputET = (EditText)view.findViewById(R.id.edit);
		final Button submitBT = (Button)view.findViewById(R.id.submit);
		
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
				
				new ChatSendThread(newChat).start();
				
				// reset input EditText
				et.setText("");
				// 방목록 refresh
				ChatFragment.chatFragment(room.type).listView.refresh();
			}
		});

		listView = (ChatListView) initListViewWithType(room.type, R.id.chatListView, view);
		((ChatListView)listView).setRoom(room);

		return view;
	}
	
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    
    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction()
    {
      /*
       * 참고 해볼곳
       * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
       * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
       * http://www.damonkohler.com/2009/02/android-recipes.html
       * http://www.firstclown.us/tag/android/
       */

      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      
      // 임시로 사용할 파일의 경로를 생성
      String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
      mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
      
      intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
      // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
      //intent.putExtra("return-data", true);
      startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    
    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction()
    {
      // 앨범 호출
      Intent intent = new Intent(Intent.ACTION_PICK);
      intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
      startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
      if(resultCode != Activity.RESULT_OK)
      {
        return;
      }

      switch(requestCode)
      {
        case CROP_FROM_CAMERA:
        {
          // 크롭이 된 이후의 이미지를 넘겨 받습니다.
          // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
          // 임시 파일을 삭제합니다.
          final Bundle extras = data.getExtras();
    
          if(extras != null)
          {
            Bitmap bitmap = extras.getParcelable("data");
            String fileIdx = Encrypter.sharedEncrypter().md5(UserInfo.getUserIdx(getActivity())+System.currentTimeMillis());
            
            File file = new File("sdcard/DCIM/"+fileIdx+".jpg");
            
           try {
               file.createNewFile();
               FileOutputStream fos = new FileOutputStream(file);
               final BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);
               bitmap.compress(CompressFormat.JPEG, 100, bos);
               bos.flush();
               bos.close();
               fos.close();
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (IOException e) {

           }
            sendImage(fileIdx);
          }
    
          // 임시 파일 삭제
          File f = new File(mImageCaptureUri.getPath());
          if(f.exists())
          {
            f.delete();
          }
    
          break;
        }
    
        case PICK_FROM_ALBUM:
        {
          // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
          // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
          
          mImageCaptureUri = data.getData();
        }
        
        case PICK_FROM_CAMERA:
        {
          // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
          // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
    
          Intent intent = new Intent("com.android.camera.action.CROP");
          intent.setDataAndType(mImageCaptureUri, "image/*");
    
          intent.putExtra("outputX", 90);
          intent.putExtra("outputY", 90);
          intent.putExtra("aspectX", 1);
          intent.putExtra("aspectY", 1);
          intent.putExtra("scale", true);
          intent.putExtra("return-data", true);
          startActivityForResult(intent, CROP_FROM_CAMERA);
    
          break;
        }
      }
    }
}
