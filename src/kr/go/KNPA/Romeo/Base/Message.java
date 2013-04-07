package kr.go.KNPA.Romeo.Base;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Config.Event;
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
	
	protected static final String KEY_TYPE		=	"type";
	protected static final String KEY_TITLE		=	"title";
	protected static final String KEY_CONTENT	=	"content";
	protected static final String KEY_SENDER	=	"sender";
	protected static final String KEY_RECEIVERS	=	"receivers";
	
	public String 			idx 		= null;
	protected int 			type		= NOT_SPECIFIED;
	
	public String 			title		= null;
	public String 			content		= null;
	
	public User 			sender		= null;
	public ArrayList<User> 	receivers 	= null;
	
	public long 			TS			= NOT_SPECIFIED;
	public ArrayList<User>	uncheckers 	= null;
	
	public boolean 			checked 	= false;
	public long 			checkTS		= NOT_SPECIFIED;
	
	
	public boolean received;
	
	
	/**
	 * 
	 */
	public Message() {}
	
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
			type = jo.getInt(Data.KEY_MESSAGE_TYPE);
		} catch (JSONException e) {
		}
		
		try {
			switch(type/MESSAGE_TYPE_DIVIDER) {
				case Message.MESSAGE_TYPE_CHAT		:		message = new Chat(json);		break;
				case Message.MESSAGE_TYPE_DOCUMENT	:		message = new Document(json);	break;
				case Message.MESSAGE_TYPE_SURVEY	:		message = new Survey(json);		break;
			}
			
			message.type = jo.getInt(KEY_TYPE);
			message.title = jo.getString(KEY_TITLE);
			message.content = jo.getString(KEY_CONTENT);
			message.sender = User.getUserWithIdx(jo.getString(KEY_SENDER));
			
			JSONArray __receivers = jo.getJSONArray(KEY_RECEIVERS);
			ArrayList<String> _receivers = new ArrayList<String>(__receivers.length()); 
			for(int i=0; i<__receivers.length(); i++) {
				_receivers.add(__receivers.getString(i));
			}
			message.receivers = User.getUsersWithIdxs(_receivers);
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
		String 			_idx 		= c.getString(c.getColumnIndex("idx"));
		//type
		String 			_title 		= c.getString(c.getColumnIndex("title"));
		String 			_content 	= c.getString(c.getColumnIndex("content"));
		Appendix		_appendix	= Appendix.fromBlob(c.getBlob(c.getColumnIndex("appendix")));
		User 			_sender		= User.getUserWithIdx(c.getString(c.getColumnIndex("sender")));
		ArrayList<User> _receivers 	= User.getUsersWithIdxs(c.getString(c.getColumnIndex("receivers")));
		long 			_TS			= c.getLong(c.getColumnIndex("TS"));
		boolean 		_checked 	= (c.getInt(c.getColumnIndex("checked")) == 1 ? true : false);
		long 			_checkTS	= c.getLong(c.getColumnIndex("checkTS"));
		boolean 		_received 	= (c.getInt(c.getColumnIndex("received")) == 1 ? true : false);
		ArrayList<User> _uncheckers = User.getUsersWithIdxs(c.getString(c.getColumnIndex("uncheckers")));
		
		this.idx 		= _idx;
		this.title 		= _title;
		this.content 	= _content;
		this.sender		= _sender;
		this.receivers 	= _receivers;
		this.received 	= _received;
		this.checkTS 	= _checkTS;
		this.checked 	= _checked;
		this.TS 		= _TS;
		this.uncheckers = _uncheckers;
	}
	
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
	

	public void send() {
		GCMMessageSender.sendMessage(this);
	}
	
	public void afterSend() {}
	
	public static class Builder {
		protected String 		_idx 		= null;
		protected int 			_type		= NOT_SPECIFIED;
		protected String 			_title		= null;
		protected String 			_content	= null;
		protected Appendix 		_appendix	= null;
		protected User 			_sender		= null;
		protected ArrayList<User> _receivers 	= null;
		protected long 			_TS			= NOT_SPECIFIED;
		
		protected boolean 		_received 	= true;
		
		protected boolean 		_checked 	= false;
		protected long 			_checkTS	= NOT_SPECIFIED;
		public Builder idx(String idx) {
			_idx = idx;
			return this;
		}
		
		public Builder type(int type) {
			_type = type;
			return this;
		}
		
		
		public Builder title(String title) {
			_title = title;
			return this;
		}
		
		public Builder content( String content) {
			_content = content;
			return this;
		}
		
		public Builder appendix(Appendix appendix) {
			_appendix = appendix;
			return this;
		}
		
		public Builder sender(User  sender) {
			_sender = sender;
			return this;
		}
		
		public Builder receivers(ArrayList<User> receivers) {
			_receivers = receivers;
			return this;
		}
		
		public Builder TS(long TS) {
			_TS = TS;
			return this;
		}
		
		public Builder received(boolean received) {
			_received = received;
			return this;
		}
		
		public Builder checked(boolean checked) {
			_checked = checked;
			return this;
		}
		
		public Builder checkTS(long checkTS) {
			_checkTS = checkTS;
			return this;
		}
		
		public kr.go.KNPA.Romeo.Chat.Chat.Builder toChatBuilder() {
			return (kr.go.KNPA.Romeo.Chat.Chat.Builder)this;
		}
		
		public kr.go.KNPA.Romeo.Document.Document.Builder toDocumentBuilder() {
			return (kr.go.KNPA.Romeo.Document.Document.Builder)this;
		}
		
		public kr.go.KNPA.Romeo.Survey.Survey.Builder toSurveyBuilder() {
			return (kr.go.KNPA.Romeo.Survey.Survey.Builder)this;
		}
		
		
		public Message buildMessage() {
			Message message = new Message();
			message.idx = this._idx;
			message.title = this._title;
			message.type = this._type;
			message.content = this._content;
			message.sender = this._sender;
			message.receivers = this._receivers;
			message.TS = this._TS;
			message.received = this._received;
			message.checkTS = this._checkTS;
			message.checked = this._checked;			
			message.uncheckers = new ArrayList<User>();
			for(int i=0; i< _receivers.size(); i++) {
				message.uncheckers.add(_receivers.get(i).clone());
			}
			return message;
		}
		
	}
	
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
		dest.writeParcelable(sender, flags);
		dest.writeTypedList(receivers);
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
		sender = source.readParcelable(User.class.getClassLoader());
		receivers = source.createTypedArrayList(User.CREATOR);
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
		Data reqData = new Data().add(0, Data.KEY_MESSAGE_TYPE, message.type)
				 .add(0, Data.KEY_MESSAGE_HASH, message.idx)
				 .add(0, Data.KEY_USER_HASH, UserInfo.getUserIdx(context));
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
