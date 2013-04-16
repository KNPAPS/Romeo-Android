package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Encrypter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Room {
	private static final String TAG = Room.class.getName();
	private String roomCode;
	private String title;
	private int type;
	private ArrayList<String> chatters;
	private boolean created = false;
	private Context mContext;
	
	public Room(Context context, int type, String roomCode){
		this.mContext = context;
		setType(type);
		setRoomCode(roomCode);
		created(true);
	}
	
	public Room(Context context, int type, ArrayList<String> initChatters ) {
		this.mContext = context;
		setType(type);
		chatters = new ArrayList<String>(initChatters.size());
		chatters.addAll(initChatters);
		created(false);
	}

	public ArrayList<String> getChatters() {
		
		if ( chatters == null ) {
			Cursor cursor = DBProcManager.sharedManager(mContext).chat().getRoomChatters(getRoomCode(),true);
			ArrayList<String> roomUsers = new ArrayList<String>(cursor.getCount());
			
			while(cursor.moveToNext()) {
				roomUsers.add( cursor.getString(cursor.getColumnIndex(ChatProcManager.COLUMN_USER_IDX)) );
			}
			cursor.close();
			chatters = roomUsers;
			return roomUsers;
		} else {
			return chatters;
		}
	}
	
	public void addChatter(String userIdx) {
		chatters.add(userIdx);
		DBProcManager.sharedManager(mContext).chat().addUserToRoom(userIdx, getRoomCode());
	}
	
	public void addChatters(ArrayList<String> userIdxs) {
		chatters.addAll(userIdxs);
		DBProcManager.sharedManager(mContext).chat().addUsersToRoom(userIdxs, getRoomCode());		
	}
	
	public void removeChatter(String userIdx) {
		chatters.remove(userIdx);
		DBProcManager.sharedManager(mContext).chat().removeUserFromRoom(userIdx, getRoomCode());
	}
	
	/**
	 * @name static method
	 * @{
	 */
	public static String makeRoomCode(Context context) {
		String str = UserInfo.getUserIdx(context)+":"+System.currentTimeMillis();
		return Encrypter.sharedEncrypter().md5(str);
	}
	/**@}*/

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}
	
	public int getType() { return this.type; }
	public void setType(int type) { this.type = type; }
	
	public String getTitle() {
		if ( this.title == null ) {
			
			if ( isCreated() == true ) {
			
				Cursor c = DBProcManager.sharedManager(mContext).chat().getRoomInfo(getRoomCode());
				if ( c != null && c.moveToNext() ) {
					
					String title = c.getString(c.getColumnIndex( DBProcManager.ChatProcManager.COLUMN_ROOM_TITLE ));
					if ( title != null && title != "" ) {
						return title;
					}
				}
			}
			
			if ( getChatters().size() == 2 ) {
				
				String receiverIdx = getChatters().get(0)==UserInfo.getUserIdx(mContext)?getChatters().get(1):getChatters().get(0);
				User receiver = User.getUserWithIdx(receiverIdx);
				
				String title =
						Constants.POLICE_RANK[receiver.rank]+" "+
						User.getUserWithIdx(getChatters().get(0)).name;
				
				return title;
			} else {
				return "그룹대화 ("+String.valueOf( getChatters().size() )+ "명)"; 
			}
			
		} else {
			return this.title;
		}
	}
	
	public void setTitle(String title){
		this.title = title;
		DBProcManager.sharedManager(mContext).chat().updateRoomTitle(getRoomCode(), title);
	}

	public boolean isCreated() {
		return created;
	}

	public void created(boolean created) {
		this.created = created;
	}
	
	public boolean create() {
		this.setRoomCode( Room.makeRoomCode(mContext) );
		
		Data reqData = new Data();
		reqData.add(0,KEY.CHAT.ROOM_CODE,this.getRoomCode());
		reqData.add(0,KEY.CHAT.ROOM_MEMBER, this.getChatters());
		
		Payload request = new Payload().setEvent(Event.Message.Chat.createRoom()).setData(reqData);
		Payload response = new Connection().async(false).requestPayload(request).request().getResponsePayload();

		if ( response == null ) {
			return false;
		} else {
			if ( response.getStatusCode() == StatusCode.SUCCESS ) {
				DBProcManager.sharedManager(mContext).chat().createRoom(this.getChatters(), this.getType(), this.getRoomCode());
				created(true);
				return true;
			}
			Log.e(TAG,"response status code : "+response.getStatusCode());
			return false;
		}
	}
}
