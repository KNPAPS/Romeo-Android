package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import android.database.Cursor;

import kr.go.KNPA.Romeo.Util.DBManager;

public class Room {
	//ArrayList<Chat> chats;
	String roomCode;
	int type;
	
	public Room() {
		//chats = new ArrayList<Chat>();
	}


	public Room(Cursor c) {
		this.roomCode = c.getString(c.getColumnIndex("roomCode"));
		//int subType = c.
		//this.type
	}
	
	public Room(Cursor c, int type) {
		this.roomCode = c.getString(c.getColumnIndex("roomCode"));
		this.type = type;
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
