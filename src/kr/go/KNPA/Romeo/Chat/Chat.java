package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.KEY;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Chat extends Message {
	
	// Message Sub Type Constants
	public static final int TYPE_MEETING = 0;
	public static final int TYPE_COMMAND = 1;
	
	public static final int CONTENT_TYPE_TEXT = 1;
	public static final int CONTENT_TYPE_PICTURE = 2;

	public static final int STATE_SUCCESS = 1;
	public static final int STATE_FAIL = 2;
	public static final int STATE_SENDING = 3;
	
	public String roomCode;
	public int contentType = CONTENT_TYPE_TEXT;
	
	private static final String KEY_ROOMCODE = KEY.CHAT.ROOM_CODE;
	
	// Constructor
	public Chat() {}

	public Chat(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		this.roomCode = jo.getString(KEY_ROOMCODE);
		this.contentType = jo.getInt(KEY.CHAT.CONTENT_TYPE);
	}

	public Chat(
			String				idx, 
			int					type, 
			String				content, 
			String 				sender, 
			ArrayList<String>	receivers, 
			boolean				received,
			long				TS,
			boolean				checked, 
			long 				checkTS,
			String 				roomCode, 
			int 				contentType) {
		this.idx = idx;
		this.type = type;
		this.title = "";
		this.content = content;
		this.senderIdx = sender;
		this.receiversIdx = receivers;
		this.received = received;
		this.TS = TS;
		this.checked = checked;
		this.checkTS = checkTS;
		this.roomCode = roomCode;
		this.contentType = contentType;
	}
	
	public Chat clone() {
		Chat chat = (Chat)this.clone(new Chat());
		chat.roomCode = this.roomCode;
		chat.contentType = this.contentType;
		
		return chat;
	}

	@Override
	public void afterSend(Context context, boolean successful) {
		if(successful) {
			// Success
			//DBProcManager.sharedManager(context).chat().saveChatOnSend(this.roomCode, this.idx, this.senderIdx, this.content, this.contentType, this.TS);
		}  else {
			// Failure
		}
		// TODO : Animation 처리
	}

}