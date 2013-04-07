package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.database.Cursor;

public class Room {
	//ArrayList<Chat> chats;
	String roomCode;
	int type;
	ArrayList<User> users;
	
	public Room() {
		//chats = new ArrayList<Chat>();
	}

	public Room(int type, String roomCode, ArrayList<User> users) {
		this.type = type;
		this.roomCode = roomCode;
		this.users = users;
	}
	

	public Room(Cursor c, int type) {
		this.roomCode = c.getString(c.getColumnIndex("roomCode"));
		this.type = type;
		
		String _rec = c.getString(c.getColumnIndex("receivers"));
		ArrayList<User> receivers;
		if(_rec != null && _rec.trim().length() > 0) {
			receivers = User.getUsersWithIdxs(_rec);
		} else {
			receivers = new ArrayList<User>();
		}
		
		Chat chat = new Chat.Builder()
							.idx(c.getString(c.getColumnIndex("idx")))
							.type(this.type)
							.content(c.getString(c.getColumnIndex("content")))
							.sender(User.getUserWithIdx(c.getString(c.getColumnIndex("sender"))))
							.receivers(receivers)
							.TS(c.getLong(c.getColumnIndex("TS")))
							.checked(c.getInt(c.getColumnIndex("checked")) == 1 ? true : false)
							.checkTS(c.getLong(c.getColumnIndex("checkTS")))
							.received(c.getInt(c.getColumnIndex("received")) == 1 ? true : false)
							.toChatBuilder()
							.build();
		
		users = (ArrayList<User>) chat.receivers.clone();
		if(isUserInRoom(chat.sender.idx) == false) { // 보낸 사람과 받는 사람이 같으면, 두번 등록된다. 따라서 검사해서 있으면 넣지 않도록 한다.
			users.add(chat.sender);
		}
		// TODO new DB 와 연동
		
	}

	public boolean isUserInRoom(String userIdx) {
		for(int i=0; i<users.size(); i++) {
			if( users.get(i).idx.equals(userIdx) )
				return true;
		}
		return false;
	}

	public static String makeRoomCode(Context context) {
		return UserInfo.getUserIdx(context)+":"+System.currentTimeMillis();
	}
	
	public ArrayList<User> getUsers(Context context) {
		Cursor cursorRoomUsers = DBProcManager.sharedManager(context).chat().getReceiverList(this.roomCode);
		ArrayList<User> roomUsers = new ArrayList<User>(cursorRoomUsers.getCount());
		
		cursorRoomUsers.moveToFirst();
		while(!cursorRoomUsers.isAfterLast()) {
			roomUsers.add( new User( cursorRoomUsers.getString(cursorRoomUsers.getColumnIndex(ChatProcManager.COLUMN_USER_HASH)) ) );
			cursorRoomUsers.moveToNext();
		}
		
		// TODO : 필요한가??
		this.users = roomUsers;
		
		return roomUsers;
	}
}
