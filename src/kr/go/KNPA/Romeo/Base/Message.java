package kr.go.KNPA.Romeo.Base;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.GCM.GCMMessageSender;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Util.DBManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
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
	
	
	// Variables to be sent
	public String 			idx 		= null;
	private int 				type		= NOT_SPECIFIED;
	
	public String 			title		= null;
	public String 			content		= null;
	public Appendix 		appendix	= null;
	
	public User 			sender		= null;
	public ArrayList<User> 	receivers 	= null;
	public long 			TS			= NOT_SPECIFIED;
	public ArrayList<User>	uncheckers 	= null;
	
	// Variables to be sent only in asynchronous way
	public boolean 			checked 	= false;
	public long 			checkTS		= NOT_SPECIFIED;
	
	// Variables NOT to be sent
	public boolean received;
	
	
	public Message() {
		
	}

	public Message(Cursor c) {
		String 			_idx 		= c.getString(c.getColumnIndex("idx"));
		//type
		String 			_title 		= c.getString(c.getColumnIndex("title"));
		String 			_content 	= c.getString(c.getColumnIndex("content"));
		Appendix		_appendix	= Appendix.fromBlob(c.getBlob(c.getColumnIndex("appendix")));
		User 			_sender		= User.getUserWithIndex(c.getString(c.getColumnIndex("sender")));
		ArrayList<User> _receivers 	= User.getUsersWithIndexes(c.getString(c.getColumnIndex("receivers")));
		long 			_TS			= c.getLong(c.getColumnIndex("TS"));
		boolean 		_checked 	= (c.getInt(c.getColumnIndex("checked")) == 1 ? true : false);
		long 			_checkTS	= c.getLong(c.getColumnIndex("checkTS"));
		boolean 		_received 	= (c.getInt(c.getColumnIndex("received")) == 1 ? true : false);
		ArrayList<User> _uncheckers = User.getUsersWithIndexes(c.getString(c.getColumnIndex("uncheckers")));
		
		this.idx 		= _idx;
		this.title 		= _title;
		this.appendix 	= _appendix;
		this.content 	= _content;
		this.sender		= _sender;
		this.receivers 	= _receivers;
		this.received 	= _received;
		this.checkTS 	= _checkTS;
		this.checked 	= _checked;
		this.TS 		= _TS;
		this.uncheckers = _uncheckers;
	}
	
	public static int makeType(int type, int subType) {
		return type * Message.MESSAGE_TYPE_DIVIDER + subType;
	}
	
	public int type() {
		return type;
	}
	
	public int mainType() {
		return type / Message.MESSAGE_TYPE_DIVIDER;
	}
	
	public int subType() {
		return type % Message.MESSAGE_TYPE_DIVIDER;
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
		sb.append(q).append("content").append(q).append(c).append(q).append(content).append(q).append(",");
		sb.append(q).append("appendix").append(q).append(c).append(appendix.toJSON());
		sb.append("}");
		
		return sb.toString();
		
	}
	

	public long send() {
		return GCMMessageSender.sendMessage(this);
	}
	
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
			message.appendix = this._appendix;
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
		dest.writeParcelable(appendix, flags); 
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
		appendix = source.readParcelable(Appendix.class.getClassLoader());
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
		return User.getUsersWithIndexes(uncheckers);
	}
	
	public static ArrayList<User> getUncheckersInUsersWithMessage(Message message) {
		ArrayList<String> uncheckers = getUncheckersIdxsWithMessage(message);
		return User.getUsersWithIndexes(uncheckers);
	}

	public void setChecked(Context context) {
		if(this.checked == false) {
			
			// TODO : make Async
			boolean result = GCMMessageSender.setMessageChecked(this.type, this.idx, UserInfo.getUserIdx(context));
			
			if(result == true) {
				
				DBManager dbManager = new DBManager(context);
				SQLiteDatabase db = dbManager.getWritableDatabase();
				
				String tableName =  Message.getTableNameWithMessageType(this.type);
				
				ContentValues vals = new ContentValues();
				vals.put("checked", 1);
				db.update(tableName, vals, "idx=?", new String[] {this.idx+""});
				
				this.checked = true;
				
			}
			
		} else {
			return;
		}
	}
	
	
	public static String getTableNameWithMessageType(int type) {
		int messageType = type/MESSAGE_TYPE_DIVIDER;
		int messageSubType = type%MESSAGE_TYPE_DIVIDER;
		
		String tableName = null;
		if(messageType == Message.MESSAGE_TYPE_CHAT) {
			switch(messageSubType) {
				case Chat.TYPE_COMMAND : tableName = DBManager.TABLE_COMMAND; break;
				case Chat.TYPE_MEETING : tableName = DBManager.TABLE_MEETING; break;
			}
		} else if(messageType == Message.MESSAGE_TYPE_DOCUMENT) {
			switch(messageSubType) {
				case Document.TYPE_DEPARTED : tableName = DBManager.TABLE_DOCUMENT; break;
				case Document.TYPE_RECEIVED : tableName = DBManager.TABLE_DOCUMENT; break;
				case Document.TYPE_FAVORITE : tableName = DBManager.TABLE_DOCUMENT; break;
			}
			
		} else if(messageType == Message.MESSAGE_TYPE_SURVEY) {
			switch(messageSubType) {
				case Survey.TYPE_DEPARTED : tableName = DBManager.TABLE_SURVEY; break;
				case Document.TYPE_RECEIVED : tableName = DBManager.TABLE_SURVEY; break;
			}
		}
		
		return tableName;
		
	}
	
	public static String getTableNameWithMassage(Message message) {
		return getTableNameWithMessageType(message.type);
	}
}
