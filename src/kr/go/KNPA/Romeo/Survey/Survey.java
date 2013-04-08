package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.GCM.GCMMessageSender;
import kr.go.KNPA.Romeo.Member.User;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Survey extends Message implements Parcelable{
	
	// Message Sub Type Constants
	public static final int TYPE_RECEIVED = 0;
	public static final int TYPE_DEPARTED = 1;

	private long openTS = NOT_SPECIFIED;
	private long closeTS = NOT_SPECIFIED;
	public boolean answered = false;
	
	private static final String KEY_OPEN_TS 		= KEY.SURVEY.OPEN_TS; 
	private static final String KEY_CLOSE_TS 		= KEY.SURVEY.CLOSE_TS;
	
	private Form form;
	
	// Constructor
	public Survey() {}
	
	public Survey(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		this.openTS = jo.getLong(KEY_OPEN_TS);
		this.closeTS = jo.getLong(KEY_CLOSE_TS);
	}
	
	public Survey(Cursor c) {
		super(c);
		
		this.type = Message.MESSAGE_TYPE_SURVEY * Message.MESSAGE_TYPE_DIVIDER + (received ? Survey.TYPE_RECEIVED : Survey.TYPE_DEPARTED);

		long _openTS = c.getLong(c.getColumnIndex("openTS"));
		long _closeTS = c.getLong(c.getColumnIndex("closeTS"));
		this.openTS = _openTS;		//???
		this.closeTS = _closeTS;	//???
	}

	public Survey(Parcel source) {
		readRomParcel(source);
	}

	public Survey(
			String			idx, 
			int				type, 
			String			title, 
			String			content, 
			User 			sender, 
			ArrayList<User>	receivers, 
			boolean			received,
			long			TS,
			boolean			checked, 
			long 			checkTS,
			long			openTS,
			long			closeTS,
			boolean			answered
			) {
		this.idx = idx;
		this.type = type;
		this.title = title;
		this.content = content;
		this.sender = sender;
		this.receivers = receivers;
		this.received = received;
		this.TS = TS;
		this.checked = checked;
		this.checkTS = checkTS;
		this.openTS = openTS;
		this.closeTS = closeTS;
		this.answered = answered;
	}
	
	/*
	public Survey(Payload payload, boolean received, long checkTS) {
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
		
		
		this.received = received;
		this.checkTS = checkTS;
		if(this.checkTS == Message.NOT_SPECIFIED) {
			this.checked = false;
		} else {
			this.checked = true;
		}
		// answered TODO
	}
	*/
	
	public Survey clone() {
		Survey survey = (Survey)this.clone(new Survey());

		survey.answered = this.answered;
		survey.openTS = this.openTS;
		survey.closeTS = this.closeTS;
		survey.form = this.form;
		
		return survey;
	}
	
	public long openTS() {
		return this.openTS;
	}
	public long closeTS() {
		return this.closeTS;
	}
	
	public boolean answered() {
		return false; // TODO
	}
	
	
	public void sendAnswerSheet(String json, Context context) {
		GCMMessageSender.sendSurveyAnswerSheet(json);
		
		// TODO : make Async
		
			setAnswered(json, context);
		
	}
	
	public static void afterSendAnswerSheet() {
		
	}
	
	public void setAnswered(String json, Context context) {
		// TODO :  Insert into DB
		/*
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
		*/
	}
	
	@Override
	public void afterSend(Context context, boolean successful) {
		if(successful) {
			// Success
			DBProcManager.sharedManager(context).survey().saveSurveyOnSend(this.idx, this.title, this.content, this.sender.idx, this.TS);
		}  else {
			// Failure
		}
		// TODO : Animation 처리
	}
	
	public static class Form extends HashMap<String, Object>{
		public static final String TITLE = "title";
		public static final String CONTENT = "content";
		public static final String OPEN_TS = "openTS";
		public static final String CLOSE_TS = "closeTS";
		
		public static final String QUESTIONS = "questions";
		public static final String OPTIONS = "options";
		
		public static final String IS_MULTIPLE = "isMultiple";
		
		public Form() {}
		
		public ArrayList<Question> questions() {
			ArrayList<Question> _questions = null;
			if(this.containsKey(QUESTIONS) == false) {
				_questions = new ArrayList<Question>();
				this.put(QUESTIONS, _questions);
			}
			
			_questions = (ArrayList<Question>)this.get(QUESTIONS);
			
			if(_questions == null) {
				_questions = new ArrayList<Question>();
				this.remove(QUESTIONS);
				this.put(QUESTIONS, _questions);
			}
			
			return _questions;	
		}
		
		public Form addQuestion(Question q) {	
			questions().add(q);
			return this;
		}
		
		public static class Question {
			private String title;
			private String content;
			private ArrayList<String> options;
			private boolean isMultiple = false;
			
			public String title() {	return title;		}
			public Question title(String title) {this.title = title; return this;}
			public String content() { return content;	}
			public Question content(String content) {this.content = content; return this;}
			public ArrayList<String> options() { return options;}
			
			public boolean isMultiple() {	return isMultiple;	}
			
			public Question isMultiple(boolean isMultiple) { 
				this.isMultiple = isMultiple; 
				return this;
			}
			public Question addOption(String o) {
				if(options == null)
					options = new ArrayList<String>();
				options.add(o);
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
