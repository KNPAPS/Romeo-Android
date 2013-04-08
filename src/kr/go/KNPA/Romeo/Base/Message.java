package kr.go.KNPA.Romeo.Base;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBManager;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.GCM.GCMMessageSender;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable{

	// Common Constants
	public static final int NOT_SPECIFIED = -777;
	public static final int MESSAGE_TYPE_DIVIDER = 100; 
	
	// Message Type Constants
	public static final int MESSAGE_TYPE_CHAT = 0;
	public static final int MESSAGE_TYPE_DOCUMENT = 1;
	public static final int MESSAGE_TYPE_SURVEY = 2;

	
	public static final String MESSAGE_KEY_CHAT = "CHAT";
	public static final String MESSAGE_KEY_MEETING = "MEETING";
	public static final String MESSAGE_KEY_COMMAND = "COMMAND";
	public static final String MESSAGE_KEY_DOCUMENT = "DOCUMENT";
	public static final String MESSAGE_KEY_SURVEY = "SURVEY";
		
	public String 				idx 			= null;
	protected int 				type			= NOT_SPECIFIED;
	
	public String 				title			= null;
	public String 				content			= null;
	
	public String 				senderIdx		= null;
	public ArrayList<String> 	receiversIdx 	= null;
	
	public long 				TS				= NOT_SPECIFIED;
	
	public boolean 				checked 		= false;
	public long 				checkTS			= NOT_SPECIFIED;
	
	
	public boolean received;
	
	
	/**
	 * 
	 */
	public Message() {}
	
	public Object clone(Message message){
		message.idx = this.idx;
		message.type = this.type;
		message.title = this.title;
		message.content = this.content;
		message.senderIdx = this.senderIdx;
		message.receiversIdx = this.receiversIdx;
		message.TS = this.TS;
		message.checked = this.checked;
		message.checkTS = this.checkTS;
		message.received = this.received;
		return message;
	}
	
	/**
	 * 
	 * @param json
	 * @return
	 */
	public static Message parseMessage(String json) {
		Message message = null;
		
		JSONObject jo = null;
		int type = NOT_SPECIFIED;
		
		try {
			jo = new JSONObject(json);
			type = jo.getInt(KEY.MESSAGE.TYPE);
		} catch (JSONException e) {
		}
		
		try {
			switch(type/MESSAGE_TYPE_DIVIDER) {
				case Message.MESSAGE_TYPE_CHAT		:		message = new Chat(json);		break;
				case Message.MESSAGE_TYPE_DOCUMENT	:		message = new Document(json);	break;
				case Message.MESSAGE_TYPE_SURVEY	:		message = new Survey(json);		break;
			}
			
			message.type = jo.getInt(KEY.MESSAGE.TYPE);
			message.title = jo.getString(KEY.MESSAGE.TITLE);
			message.content = jo.getString(KEY.MESSAGE.CONTENT);
			message.senderIdx = jo.getString(KEY.MESSAGE.SENDER_IDX);
			
			JSONArray __receivers = jo.getJSONArray(KEY.MESSAGE.RECEIVERS_IDX);
			ArrayList<String> _receivers = new ArrayList<String>(__receivers.length()); 
			for(int i=0; i<__receivers.length(); i++) {
				_receivers.add(__receivers.getString(i));
			}
			message.receiversIdx = _receivers;
		} catch (JSONException e) {
			message = null;
		}
		
		return message;
	}

	/**
	 * @name type getters
	 * 타입과 관련된 연산을 하는 메서드들
	 * @{
	 */
	public int type() 				{		return type;									}
	public int mainType()			{		return type / Message.MESSAGE_TYPE_DIVIDER;		}
	public int subType()			{		return type % Message.MESSAGE_TYPE_DIVIDER;		}
	public static int makeType(int type, int subType)	{	return type * Message.MESSAGE_TYPE_DIVIDER + subType;	}
	public static int mainType(int type) 				{		return type / Message.MESSAGE_TYPE_DIVIDER;		}
	public static int subType(int type)					{		return type % Message.MESSAGE_TYPE_DIVIDER;		}
	/** @} */
	
	public Message(Cursor c) {
		this.idx 		= c.getString(c.getColumnIndex("idx"));
		// int type		:	Chat, Document, Survey 에서.
		this.title 		= c.getString(c.getColumnIndex("title"));
		this.content 	= c.getString(c.getColumnIndex("content"));
		
		this.senderIdx		= c.getString(c.getColumnIndex("sender"));
		//TODO this.receiversIdx 	= c.getString(c.getColumnIndex("receivers"));
		
		this.TS			= c.getLong(c.getColumnIndex("TS"));
		
		this.checked 	= (c.getInt(c.getColumnIndex("checked")) == 1 ? true : false);
		this.checkTS	= c.getLong(c.getColumnIndex("checkTS"));

		this.received 	= (c.getInt(c.getColumnIndex("received")) == 1 ? true : false);
	}
	
	/*
	public String toJSON() {
		final String q = "\"";
		final String c = ":";
		final String lb = "[";
		final String rb = "]";
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(q).append("type").append(q).append(c).append(type).append(",");
		sb.append(q).append("idx").append(q).append(c).append(idx).append(",");
		sb.append(q).append("title").append(q).append(c).append(q).append(title).append(q).append(",");
		sb.append(q).append("content").append(q).append(c).append(q).append(content).append(q);
		sb.append("}");
		
		return sb.toString();
		
	}
	*/

	public void send(Context context) {
		GCMMessageSender.sendMessage(context, this);
	}
	
	public void afterSend(Context context, boolean successful) {}
	
	
	// Implements Parcelable
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(idx);
		dest.writeString(title);
		dest.writeInt(type);
		dest.writeString(content);
		dest.writeString(senderIdx);
		dest.writeStringList(receiversIdx);
		dest.writeLong(TS);
		dest.writeLong(checkTS);
		boolean[] ba = {received, checked};
		dest.writeBooleanArray(ba);
	}
	
	public void readFromParcel(Parcel source) {
		idx = source.readString();
		title = source.readString();
		type = source.readInt();
		content = source.readString();
		senderIdx = source.readString();
		receiversIdx = source.createStringArrayList();
		TS = source.readLong();
		checkTS = source.readLong();
		boolean[] ba = source.createBooleanArray();
		received = ba[0];
		checked = ba[1];		
	}
	
	//	getting Message Uncheckers
	public static ArrayList<String> getUncheckersIdxsWithMessageTypeAndIndex(int type, String index) {
		return GCMMessageSender.getUncheckers(type, index);
	}
	
	public static ArrayList<String> getUncheckersIdxsWithMessage(Message message) {
		return getUncheckersIdxsWithMessageTypeAndIndex(message.type, message.idx);
	}
	
	public static ArrayList<User> getUncheckersWithMessageTypeAndIndex(int type, String index) {
		ArrayList<String> uncheckers = getUncheckersIdxsWithMessageTypeAndIndex(type, index);
		return User.getUsersWithIdxs(uncheckers);
	}
	
	public static ArrayList<User> getUncheckersInUsersWithMessage(Message message) {
		ArrayList<String> uncheckers = getUncheckersIdxsWithMessage(message);
		return User.getUsersWithIdxs(uncheckers);
	}

	/**
	 * @name Setting Message Checked
	 * @{
	 */
	public static void setChecked(Context context, ArrayList<Message> messages) {
		
		if(messages == null || messages.size() < 1) return;
		
		int mainType = messages.get(0).mainType();
		if( mainType == MESSAGE_TYPE_CHAT) {
			for(int i=0; i<messages.size(); i++) {
				if(messages.get(i).checked == false)
					Message.setCheckedOnServer(context, messages.get(i));
			}
		} else {
			for(int i=0; i<messages.size(); i++) {
				if(messages.get(i).checked == false)
					messages.get(i).setChecked(context);
			}
		}
	}

	public void setChecked(Context context) {
		if(this.mainType() == MESSAGE_TYPE_DOCUMENT)  {
			Message.setCheckedOnServer(context, this);
		} else if(this.mainType() == MESSAGE_TYPE_SURVEY) {
			Message.setCheckedOnServer(context, this);
		} else if(this.mainType() == MESSAGE_TYPE_CHAT){
			ArrayList<Message> messages = new ArrayList<Message>(1);
			messages.add(this);
			Message.setChecked(context, messages);
		}
	}
	
	private static void setCheckedOnLocal(Context context, Message message) {
		if(message.mainType() == MESSAGE_TYPE_DOCUMENT)  {
			DBProcManager.sharedManager(context).document().updateCheckedTS(message.idx, System.currentTimeMillis());
			message.checked = true;
		} else if(message.mainType() == MESSAGE_TYPE_SURVEY) {
			DBProcManager.sharedManager(context).survey().updateCheckedTS(message.idx, System.currentTimeMillis());
			message.checked = true;
		} else if(message.mainType() == MESSAGE_TYPE_CHAT){
			ArrayList<String> chatIdxs = new ArrayList<String>(1);
			chatIdxs.add(message.idx);
			message.checked = true;
			DBProcManager.sharedManager(context).chat().updateCheckedTS(chatIdxs, System.currentTimeMillis());
		}
	}
	
	private static void setCheckedOnServer(final Context context, final Message message) {
		Data reqData = new Data().add(0, KEY.MESSAGE.TYPE, message.type)
				 .add(0, KEY.MESSAGE.IDX, message.idx)
				 .add(0, KEY.USER.IDX, UserInfo.getUserIdx(context));
		Payload request = new Payload().setEvent(Event.Message.setChecked()).setData(reqData);
		
		CallbackEvent<Payload, Integer, Payload> callback = new CallbackEvent<Payload, Integer, Payload>() {
			@Override
			public void onPostExecute(Payload result) {
				Message.setCheckedOnLocal(context, message);
			}
		};
		
		Connection conn = new Connection().requestPayloadJSON(request.toJSON()).callBack(callback);
		conn.request();
	}
	/** @} */
	
}
