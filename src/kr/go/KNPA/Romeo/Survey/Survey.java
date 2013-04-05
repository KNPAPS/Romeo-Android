package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.GCM.GCMMessageSender;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.DBManager;
import kr.go.KNPA.Romeo.Util.Encrypter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class Survey extends Message implements Parcelable{
	
	// Message Sub Type Constants
	public static final int TYPE_RECEIVED = 0;
	public static final int TYPE_DEPARTED = 1;

	private long openTS = NOT_SPECIFIED;
	private long closeTS = NOT_SPECIFIED;
	public boolean answered = false;
	
	private static final String KEY_OPEN_TS 		= "openTS"; 
	private static final String KEY_CLOSE_TS 		= "closeTS";
	private static final String KEY_QUESTION_SHEET 	= "survey_form";
	private static final String KEY_ANSWER_SHEET	= "answersheet";
	
	private Form survey_form;
	
	// Constructor
	public Survey() {}
	
	public Survey(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		this.openTS = jo.getLong(KEY_OPEN_TS);
		this.closeTS = jo.getLong(KEY_CLOSE_TS);
	}
	
	public Survey(Cursor c) {
		super(c);
		
		this.type = getType();

		long _openTS = c.getLong(c.getColumnIndex("openTS"));
		long _closeTS = c.getLong(c.getColumnIndex("closeTS"));
		this.openTS = _openTS;		//???
		this.closeTS = _closeTS;	//???
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
		this.answered = payload.message.appendix.getAnswered();
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
		// answered TODO
	}
	
	public Survey clone() {
		Survey survey = new Survey();
		
		survey.idx = this.idx;
		survey.title = this.title;
		survey.type = this.type;
		survey.content = this.content;
		survey.appendix = this.appendix;
		survey.sender = this.sender;
		survey.receivers = this.receivers;
		survey.TS = this.TS;
		survey.received = this.received;
		survey.checkTS = this.checkTS;
		survey.checked = this.checked;			
		survey.answered = this.answered;
		survey.openTS = this.openTS;
		survey.closeTS = this.closeTS;
		return survey;
	}
	protected int getType() {
		return Message.MESSAGE_TYPE_SURVEY * Message.MESSAGE_TYPE_DIVIDER + (received ? Survey.TYPE_RECEIVED : Survey.TYPE_DEPARTED);
	}
	
	public long openTS() {
		return appendix.getOpenTS();
	}
	public long closeTS() {
		return appendix.getCloseTS();
	}
	
	public boolean answered() {
		return false; // TODO
	}
	
	
	public void insertIntoDatabase(String tableName) {
		
	}
	
	public static class Builder extends Message.Builder{

		protected long _openTS = NOT_SPECIFIED;
		protected long _closeTS = NOT_SPECIFIED;
		protected boolean _answered = false;
		public Builder appendixAndTS(Appendix appendix) {
			_appendix = appendix;
			_openTS = appendix.getOpenTS();
			_closeTS = appendix.getCloseTS();
			return this;
		}
		public Builder appendix(Appendix appendix) {
			_appendix = appendix;
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
		public Builder answered(boolean answered) {
			_answered = answered;
			return this;
		}
		public Builder toSurveyBuilder() {
			return this;
		}
		public Survey build() {
			/*
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
											  */
			
			Survey survey = new Survey();
			
			survey.idx = this._idx;
			survey.title = this._title;
			survey.type = this._type;
			survey.content = this._content;
			survey.appendix = this._appendix;
			survey.sender = this._sender;
			survey.receivers = this._receivers;
			survey.TS = this._TS;
			survey.received = this._received;
			survey.checkTS = this._checkTS;
			survey.checked = this._checked;			
			survey.answered = this._answered;
			survey.openTS = this._openTS;
			survey.closeTS = this._closeTS;
			return survey;
		}
	}

	public boolean sendAnswerSheet(String json, Context context) {
		boolean result = GCMMessageSender.sendSurveyAnswerSheet(json);
		
		// TODO : make Async
		if(result == true) {
			setAnswered(json, context);
		}
		
		return result;
	}
	
	public void setAnswered(String json, Context context) {
		if(this.checked != false) {

			DBManager dbManager = new DBManager(context);
			SQLiteDatabase db = dbManager.getWritableDatabase();
			
			String tableName =  Message.getTableNameWithMessageType(this.type);
			
			ContentValues vals = new ContentValues();
			vals.put("answered", 1);
			vals.put("answersheet", Encrypter.objectToBytes(json));
			db.update(tableName, vals, "idx=?", new String[] {this.idx+""});
			
			this.checked = true;
			this.answered = true;
		}
	}
	

	public void send(Context context) {
		long idx = super.send();
		
		
		DBManager dbManager = new DBManager(context);
		SQLiteDatabase db = dbManager.getWritableDatabase();
		
		// DB에 등록
		long currentTS = System.currentTimeMillis();
		ContentValues vals = new ContentValues();
		vals.put("title", this.title);
		vals.put("content", this.content);
		vals.put("appendix", this.appendix.toBlob());
		vals.put("sender", this.sender.idx);
		vals.put("receivers", User.usersToString(this.receivers));
		vals.put("received", 0);
		vals.put("TS", currentTS);
		vals.put("checked", 1);
		vals.put("openTS", this.openTS());
		vals.put("closeTS", this.closeTS());
		vals.put("checkTS", this.checkTS);
		vals.put("answered", 0);
		vals.put("idx", idx);
		db.insert(DBManager.TABLE_SURVEY, null, vals);
		
		db.close();
		dbManager.close();
	}
	
	
	public static class Form extends HashMap<String, Object>{
		public static final String TITLE = "title";
		public static final String CONTENT = "content";
		public static final String OPEN_TS = "openTS";
		public static final String CLOSE_TS = "closeTS";
		
		public static final String QUESTIONS = "questions";
		public static final String OPTIONS = "options";
		
		public static final String IS_MULTIPLE = "isMultiple";
		
		private ArrayList<Question> _questions;
		
		public Sheet() {}
		
		public ArrayList<Question> questions() { return _questions;}
		
		public Sheet addQuestion(Question q) {
			if(_questions == null)
				_questions = new ArrayList<Question>();
			_questions.add(q);
			
			return this;
		}
		
		public static class Question extends HashMap<String, Object> {
			private ArrayList<String> _options;
			private boolean _isMultiple = false;
			
			public ArrayList<String> options() { return _options;}
			
			public boolean isMultiple() {	return _isMultiple;	}
			
			public Question isMultiple(boolean isMultiple) { 
				this._isMultiple = isMultiple; 
				return this;
			}
			public Question addOption(String o) {
				if(_options == null)
					_options = new ArrayList<String>();
				_options.add(o);
				return this;
			}
		}
		
	}
	
	

	// Implements Parcelable
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		
		dest.writeLong(openTS);
		dest.writeLong(closeTS);
		boolean[] ba = {answered}; 
		dest.writeBooleanArray(ba);
	}
	
	private void readRomParcel(Parcel source) {
		super.readFromParcel(source);
		
		openTS = source.readLong();
		closeTS = source.readLong();
		boolean[] ba = source.createBooleanArray();
		answered = ba[0];
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
