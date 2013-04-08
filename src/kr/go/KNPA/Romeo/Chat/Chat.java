package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Member.User;

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
	
	private static final String KEY_ROOMCODE = KEY.CHAT.ROOM_CODE;
	
	// Constructor
	public Chat() {}

	public Chat(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		this.roomCode = jo.getString(KEY_ROOMCODE);
	}

	public Chat(
			String				idx, 
			int					type, 
//			String				title, 
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
//		this.title = title;
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
	
	public static Chat chatOnSend(int type, String content, String sender, ArrayList<String> receivers, long TS, String roomCode, int contentType) {
		return new Chat(null, type, content, sender, receivers, false, TS, true, TS, roomCode, contentType);
		// TODO Chat checked == true?? => 서버
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
			DBProcManager.sharedManager(context).chat().saveChatOnSend(this.roomCode, this.idx, this.senderIdx, this.content, this.contentType, this.TS);
		}  else {
			// Failure
		}
		// TODO : Animation 처리
	}

}