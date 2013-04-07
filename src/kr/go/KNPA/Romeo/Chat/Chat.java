package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.Base.Message;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Chat extends Message {
	
	// Message Sub Type Constants
	public static final int TYPE_MEETING = 0;
	public static final int TYPE_COMMAND = 1;
	
	public static final int CONTENT_TYPE_TEXT = 1;
	public static final int CONTENT_TYPE_PICTURE = 2;
	
	public String roomCode;
	public int contentType = CONTENT_TYPE_TEXT;
	
	private static final String KEY_ROOMCODE = "room_hash";
	
	// Constructor
	public Chat() {}

	public Chat(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		this.roomCode = jo.getString(KEY_ROOMCODE);
	}

	/*
	public Chat(Payload payload, boolean received, long checkTS) {
		
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
		
		this.received = received;
		this.checkTS = checkTS;
		if(this.checkTS == Message.NOT_SPECIFIED) {
			this.checked = false;
		} else {
			this.checked = true;
		}
	}
	*/
	
	public Chat clone() {
		Chat chat = (Chat)this.clone(new Chat());
		chat.roomCode = this.roomCode;
		chat.contentType = this.contentType;
		
		return chat;
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
			chat.sender = this._sender;
			chat.receivers = this._receivers;
			chat.TS = this._TS;
			chat.received = this._received;
			chat.checkTS = this._checkTS;
			chat.checked = this._checked;			
			
			return chat;
		}
	}
	
	@Override
	public void afterSend(boolean succesful) {
		// TODO :  Insert into DB
		/*
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
		vals.put("sender", this.sender.idx);
		
		StringBuilder recs = new StringBuilder();
		for(int i=0; i< this.receivers.size(); i++) {
			if(i!=0) recs.append(":");
			recs.append( this.receivers.get(i) );
		}
		vals.put("receivers", recs.toString() );
		vals.put("received", 0); // 보낸 것이다.
		vals.put("TS", currentTS);
		vals.put("checked", 1);
		vals.put("roomCode", this.roomCode);
		vals.put("checkTS", currentTS);	// 지금 보내고 지금 확인한 것이므로, 현재 시간을 넣어준다.
		vals.put("idx", idx);
		db.insert(tableName, null, vals);
		
		db.close();
		dbManager.close();
		*/
	}

}