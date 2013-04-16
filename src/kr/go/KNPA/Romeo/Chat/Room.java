package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Encrypter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.database.Cursor;

public class Room {
	String roomCode;
	int type;
	ArrayList<User> users;
	private Context mContext;
	public Room(Context context, int type, String roomCode, ArrayList<User> users) {
		this.mContext = context;
		this.type = type;
		this.roomCode = roomCode;
		this.users = users;
	}
	
	public Room(Context context, int type, String roomCode) {
		this.mContext = context;
		this.type = type;
		this.roomCode = roomCode;
		this.users = getUsers();
	}

	public boolean isUserInRoom(String userIdx) {
		for(int i=0; i<users.size(); i++) {
			if( users.get(i).idx.equals(userIdx) )
				return true;
		}
		return false;
	}

	public ArrayList<User> getUsers() {
		return Room.getUsers(mContext, roomCode);
	}
	
	public ArrayList<String> getUsersIdx() {
		return Room.getUsersIdx(mContext, roomCode);
	}
	
	/**
	 * @name static method
	 * @{
	 */
	public static String makeRoomCode(Context context) {
		String str = UserInfo.getUserIdx(context)+":"+System.currentTimeMillis();
		return Encrypter.sharedEncrypter().md5(str);
	}
	
	public static ArrayList<User> getUsers(Context context, String roomCode) {
		Cursor cursor = DBProcManager.sharedManager(context).chat().getRoomMember(roomCode,true);
		
		ArrayList<User> roomUsers = new ArrayList<User>(cursor.getCount());
		
		while(cursor.moveToNext()) {
			roomUsers.add( 
						User.getUserWithIdx( cursor.getString( cursor.getColumnIndex( ChatProcManager.COLUMN_USER_IDX ))) 
					);
		}
		
		return roomUsers;
	}
	
	public static ArrayList<String> getUsersIdx(Context context, String roomCode ) {
		Cursor cursor = DBProcManager.sharedManager(context).chat().getRoomMember(roomCode,true);
		ArrayList<String> roomUsers = new ArrayList<String>(cursor.getCount());
		
		while(cursor.moveToNext()) {
			roomUsers.add( cursor.getString(cursor.getColumnIndex(ChatProcManager.COLUMN_USER_IDX)) );
		}
		
		return roomUsers;
	}
	/**@}*/
}
