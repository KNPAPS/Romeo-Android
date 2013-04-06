package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;

import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.DB.DBManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.UserInfo;

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

	public String getTableName() {
		String tableName = null;
		
		if(this.type == Chat.TYPE_COMMAND) {
			tableName = DBManager.TABLE_COMMAND;
		} else if(this.type == Chat.TYPE_MEETING ){
			tableName = DBManager.TABLE_MEETING;
		}
		
		return tableName;
	}

	public static String makeRoomCode(Context context) {
		return UserInfo.getUserIdx(context)+":"+System.currentTimeMillis();
	}
}
