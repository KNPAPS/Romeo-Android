package kr.go.KNPA.Romeo.Base;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.Chat.Builder;
import kr.go.KNPA.Romeo.Member.User;

public class Message implements Parcelable{

	// Common Constants
	public static final int NOT_SPECIFIED = -777;
	public static final int MESSAGE_TYPE_DIVIDER = 100; 
	
	// Message Type Constants
	public static final int MESSAGE_TYPE_CHAT = 0;
	public static final int MESSAGE_TYPE_DOCUMENT = 1;
	public static final int MESSAGE_TYPE_SURVEY = 2;

	
	// Variables to be sent
	public long 			idx 		= NOT_SPECIFIED;
	public int 				type		= NOT_SPECIFIED;
	
	public String 			title		= null;
	public String 			content		= null;
	public Appendix 		appendix	= null;
	
	public User 			sender		= null;
	public ArrayList<User> 	receivers 	= null;
	public long 			TS			= NOT_SPECIFIED;
	
	
	// Variables to be sent only in asynchronous way
	public boolean 			checked 	= false;
	public long 			checkTS		= NOT_SPECIFIED;
	
	// Variables NOT to be sent
	public boolean received;
	
	// TODO : abandon raw Types
	public String _appendix;
	
	
	
	public Message() {
		
	}

	public Message(Cursor c) {
		long 			_idx 		= c.getLong(c.getColumnIndex("idx"));
		//type
		String 			_title 		= c.getString(c.getColumnIndex("title"));
		String 			_content 	= c.getString(c.getColumnIndex("content"));
		//appendix
		User 			_sender		= User.getUserWithIdx(c.getLong(c.getColumnIndex("idx")));
		ArrayList<User> _receivers 	= User.indexesInStringToArrayListOfUser(c.getString(c.getColumnIndex("receivers")));
		long 			_TS			= c.getLong(c.getColumnIndex("TS"));
		boolean 		_checked 	= (c.getInt(c.getColumnIndex("checked")) == 1 ? true : false);
		long 			_checkTS	= c.getLong(c.getColumnIndex("checkTS"));
		boolean 		_received 	= (c.getInt(c.getColumnIndex("idx")) == 1 ? true : false);
		
		this.idx 		= _idx;
		this.title 		= _title;
		this.content 	= _content;
		this.sender		= _sender;
		this.receivers 	= _receivers;
		this.received 	= _received;
		this.checkTS 	= _checkTS;
		this.checked 	= _checked;
		this.TS 		= _TS;

	}
	
	protected int getType() {
		return NOT_SPECIFIED;
	}
	public static void send() {
		
	}
	
	
	public static class Builder {
		protected long 			_idx 		= NOT_SPECIFIED;
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
		
		public Builder idx(long idx) {
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
			return message;
		}
	}
	
	// Implements Parcelable
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(idx);
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
		idx = source.readLong();
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
}
