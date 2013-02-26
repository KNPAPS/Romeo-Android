package kr.go.KNPA.Romeo.Survey;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Base.Payload;

public class Survey extends Message implements Parcelable{
	
	// Message Sub Type Constants
	public static final int TYPE_RECEIVED = 0;
	public static final int TYPE_DEPARTED = 1;

	// TODO : in to Appendix
	public long openTS = NOT_SPECIFIED;
	public long closeTS = NOT_SPECIFIED;

	// Constructor
	public Survey() {
	}
	
	public Survey(Cursor c) {
		super(c);
		
		this.type = getType();		

		Appendix _appendix = new Appendix();// TODO
		this.appendix = _appendix;

		long _openTS = c.getLong(c.getColumnIndex("openTS"));
		long _closeTS = c.getLong(c.getColumnIndex("closeTS"));
		this.openTS = _openTS;
		this.closeTS = _closeTS;
	}

	public Survey(Parcel source) {
		readRomParcel(source);
	}
	
	public Survey(Payload payload) {
		this.type = payload.message.type;
		this.idx = payload.message.idx;
		this.sender = payload.sender;
		this.receivers = payload.receivers;
		this.title = payload.message.title;
		this.content = payload.message.content;
		//this.received = true;
		this.TS = System.currentTimeMillis();
		//this.checkTS = NOT_SPECIFIED;
		//this.checked = false;
		this.appendix = payload.message.appendix;
		this.openTS = payload.message.appendix.getOpenTS();
		this.closeTS = payload.message.appendix.getCloseTS();
	}
	
	public Survey(Payload payload, boolean received, long checkTS) {
		this(payload);
		this.received = received;
		this.checkTS = checkTS;
		if(this.checkTS == Message.NOT_SPECIFIED) {
			this.checked = false;
		} else {
			this.checked = true;
		}
	}
	
	protected int getType() {
		return Message.MESSAGE_TYPE_DOCUMENT * Message.MESSAGE_TYPE_DIVIDER + (received ? Survey.TYPE_RECEIVED : Survey.TYPE_DEPARTED);
	}
	
	public static class Builder extends Message.Builder{

		protected long _openTS = NOT_SPECIFIED;
		protected long _closeTS = NOT_SPECIFIED;

		public Builder appendix(Appendix appendix) {
			_appendix = appendix;
			//TODO openTS closeTS
			return this;
		}
		public Builder openTS(long openTS) {
			_openTS = openTS;
			return this;
		}
		public Builder closeTS(long closeTS) {
			_closeTS = closeTS;
			return this;
		}
		public Builder toSurveyBuilder() {
			return this;
		}
		public Survey build() {
			
			Survey survey = (Survey)new Survey.Builder()
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
			survey.openTS = this._openTS;
			survey.closeTS = this._closeTS;
			return survey;
		}
	}


	

	// Implements Parcelable
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		
		dest.writeLong(openTS);
		dest.writeLong(closeTS);
	}
	
	private void readRomParcel(Parcel source) {
		super.readFromParcel(source);
		
		openTS = source.readLong();
		closeTS = source.readLong();
	}
	
	public static final Parcelable.Creator<Survey> CREATOR = new Parcelable.Creator<Survey>() {

		@Override
		public Survey createFromParcel(Parcel source) {
			return new Survey(source);
		}

		@Override
		public Survey[] newArray(int size) {
			return new Survey[size];
		}
		
	};
	
}
