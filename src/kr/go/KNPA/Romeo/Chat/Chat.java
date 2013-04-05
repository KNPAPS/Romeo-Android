package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Base.Payload;
import kr.go.KNPA.Romeo.GCM.GCMMessageSender;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.DBManager;

public class Chat extends Message {
	
	// Message Sub Type Constants
	public static final int TYPE_MEETING = 0;
	public static final int TYPE_COMMAND = 1;
	
	// Constructor
	public Chat() {}

	public Chat(String json) {
		
	}
	public Chat(Cursor c, int type) {
		idx = c.getInt(c.getColumnIndex("idx"));;
		type = type;
	//	title = ;
		content = c.getString(c.getColumnIndex("content"));
		appendix = Appendix.fromBlob(c.getBlob(c.getColumnIndex("appendix")));
		sender = User.getUserWithIdx(c.getInt(c.getColumnIndex("sender")));
		String _rec = c.getString(c.getColumnIndex("receivers"));
		if(_rec != null && _rec.trim().length() > 0) {
			receivers = User.indexesInStringToArrayListOfUser(_rec);
		} else {
			receivers = new ArrayList<User>();
		}
		
		TS = c.getLong(c.getColumnIndex("TS"));
		checked = (c.getInt(c.getColumnIndex("checked")) == 1 ? true : false);;
		checkTS = c.getLong(c.getColumnIndex("checkTS"));
		received = (c.getInt(c.getColumnIndex("received")) == 1 ? true : false);	
	}
	
	public Chat(Payload payload) {
		this.idx = payload.message.idx;
		this.type = payload.message.type;
		this.title = payload.message.title;
		this.content = payload.message.content;
		this.appendix = payload.message.appendix;
		this.sender = payload.sender;
		this.receivers = payload.receivers;
		this.TS = System.currentTimeMillis();
		//this.received = true;
		//this.checkTS = NOT_SPECIFIED;
		//this.checked = false;
	}
	
	public Chat(Payload payload, boolean received, long checkTS) {
		this(payload);
		this.received = received;
		this.checkTS = checkTS;
		if(this.checkTS == Message.NOT_SPECIFIED) {
			this.checked = false;
		} else {
			this.checked = true;
		}
	}
	
	
	public Chat clone() {
		Chat chat = new Chat();
		chat.idx = this.idx;
		chat.title = this.title;
		chat.type = this.type;
		chat.content = this.content;
		chat.appendix = this.appendix;
		chat.sender = this.sender;
		chat.receivers = this.receivers;
		chat.TS = this.TS;
		chat.received = this.received;
		chat.checkTS = this.checkTS;
		chat.checked = this.checked;			
		
		return chat;
	}
	//
	public String getRoomCode() {
		return this.appendix.getRoomCode();
	}
	
	public void insertIntoDatabase(String tableName) {
		
	}
	
	public static class Builder extends Message.Builder {
		
		public Chat build() {
		/*
			Message message = new Message.Builder()
									   .idx(_idx)
									   .title(_title)
									   .type(_type)
									   .content(_content)
									   .appendix(_appendix)
									   .sender(_sender)
									   .receivers(_receivers)
									   .TS(_TS)
									   .received(_received)
									   .checkTS(_checkTS)
									   .checked(_checked)
									   .buildMessage();
			Chat chat = (Chat)message;
			*/
			Chat chat = new Chat();
			chat.idx = this._idx;
			chat.title = this._title;
			chat.type = this._type;
			chat.content = this._content;
			chat.appendix = this._appendix;
			chat.sender = this._sender;
			chat.receivers = this._receivers;
			chat.TS = this._TS;
			chat.received = this._received;
			chat.checkTS = this._checkTS;
			chat.checked = this._checked;			
			
			return chat;
		}
	}
	
	public void send(Context context, Room room) {
		long idx = super.send();
		
		DBManager dbManager = new DBManager(context);
		SQLiteDatabase db = dbManager.getWritableDatabase();
		
		// DB에 등록
		String tableName = null;
		switch(room.type) {
			case Chat.TYPE_COMMAND : tableName = DBManager.TABLE_COMMAND; break;
			case Chat.TYPE_MEETING : tableName = DBManager.TABLE_MEETING; break;
			default : case Chat.NOT_SPECIFIED : tableName = null; 	break;
		}
		
		long currentTS = System.currentTimeMillis();
		ContentValues vals = new ContentValues();
		vals.put("content", this.content);
		vals.put("appendix", this.appendix.toBlob());
		vals.put("sender", this.sender.idx);
		vals.put("receivers", User.usersToString(this.receivers));
		vals.put("received", 0); // 보낸 것이다.
		vals.put("TS", currentTS);
		vals.put("checked", 1);
		vals.put("roomCode", this.getRoomCode());
		vals.put("checkTS", currentTS);	// 지금 보내고 지금 확인한 것이므로, 현재 시간을 넣어준다.
		vals.put("idx", idx);
		db.insert(tableName, null, vals);
		
		db.close();
		dbManager.close();
	}

}