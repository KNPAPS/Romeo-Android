package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;
import java.util.Iterator;

import android.database.Cursor;

import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.DBManager;

public class Room {
	//ArrayList<Chat> chats;
	String roomCode;
	int type;
	ArrayList<User> users;
	
	public Room() {
		//chats = new ArrayList<Chat>();
	}


	public Room(Cursor c) {
		this.roomCode = c.getString(c.getColumnIndex("roomCode"));
		//int subType = c.
		//this.type
		Chat chat = new Chat(c, type);
		users = (ArrayList<User>) chat.receivers.clone();
		users.add(chat.sender);
	}
	
	public Room(Cursor c, int type) {
		this.roomCode = c.getString(c.getColumnIndex("roomCode"));
		this.type = type;
		Chat chat = new Chat(c, type);
		users = (ArrayList<User>) chat.receivers.clone();
		if(isUserInRoom(chat.sender.idx) == false) { // 보낸 사람과 받는 사람이 같으면, 두번 등록된다. 따라서 검사해서 있으면 넣지 않도록 한다.
			users.add(chat.sender);
		}
		
	}

	public boolean isUserInRoom(long userIdx) {
		boolean result = false;
		Iterator<User> itr = users.iterator();
		User u = null;
		while(itr.hasNext()) {
			u = itr.next();
			if(u.idx == userIdx)
				result = true;
		}
		
		return result;
	}

	public String getTableName() {
		String tableName = null;
		
		if(this.type == Chat.TYPE_COMMAND) {
			tableName = DBManager.TABLE_COMMAND;
		} else if(this.type == Chat.TYPE_MEETING ){
			tableName = DBManager.TABLE_MEETING;
		}// else {tableName = null;}
		
		return tableName;
	}
	
	public static class Builder {
		String _roomCode;
		int _type;
		
		public Builder roomCode(String roomCode) {
			_roomCode = roomCode;
			return this;
		}
		
		public Builder type(int type) {
			_type = type;
			return this;
		}
		
		public Room build() {
			Room r = new Room();
			r.roomCode = this._roomCode;
			r.type = this._type;
			
			return r;
		}
	}
}
