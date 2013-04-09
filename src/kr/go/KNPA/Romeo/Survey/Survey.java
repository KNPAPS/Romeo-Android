package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.SurveyProcManager;
import kr.go.KNPA.Romeo.GCM.GCMMessageSender;

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

	public boolean answered = false;
	
	private static final String KEY_OPEN_TS 		= KEY.SURVEY.OPEN_TS; 
	private static final String KEY_CLOSE_TS 		= KEY.SURVEY.CLOSE_TS;
	
	public Form form;
	
	// Constructor
	public Survey() {}
	
	public Survey(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		
		// TODO
	}
	
	public static Survey surveyFromServer(Context context, String surveyIdx, int subType) {
		Cursor cursor_surveyInfo = DBProcManager.sharedManager(context).survey().getSurveyInfo(surveyIdx);
		cursor_surveyInfo.moveToFirst();
		
		
		Data reqData = new Data();
		Payload request = new Payload().setEvent(Event.Message.Survey.getContent()).setData(reqData);
		Connection conn = new Connection().async(false).requestPayloadJSON(request.toJSON()).request();
		Payload response = conn.getResponsePayload();
		Data respData = response.getData();
		Survey s = new Survey(
				(String)respData.get(0, KEY.SURVEY.IDX), 
				Message.makeType(Message.MESSAGE_TYPE_SURVEY, subType), 
				(String)respData.get(0, KEY.SURVEY.TITLE), 
				(String)respData.get(0, KEY.SURVEY.CONTENT), 
				(String)respData.get(0, KEY.SURVEY.SENDER_IDX), 
				null, 
				(subType == Survey.TYPE_RECEIVED ? true : false), 
				(Long)respData.get(0, KEY.SURVEY.CREATED_TS), 
				cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(
						SurveyProcManager.COLUMN_SURVEY_IS_CHECKED)) > 0 ? true : false, 
				cursor_surveyInfo.getLong(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CHECKED_TS)), 
				(cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_ANSWERED))>0 ? true : false));
		s.form = Form.parseForm(respData);
		return s;
	}
	
	public Survey(Context context, Cursor c) {
		SurveyProcManager spm = DBProcManager.sharedManager(context).survey();
		
		this.idx 		= c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IDX));
		this.title 		= c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_NAME));
		this.senderIdx		= c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_SENDER_IDX));
		//this.receivers 	= 
		
		this.checked 	= c.getInt(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_CHECKED)) == 1 ? true : false;
		this.answered	= c.getInt(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_ANSWERED)) == 1 ? true : false;
		
		Cursor cursor_surveyInfo = spm.getSurveyInfo(this.idx);
		
		int subType = cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_TYPE));
		this.type = Message.MESSAGE_TYPE_SURVEY * Message.MESSAGE_TYPE_DIVIDER + subType;
		if(subType == Survey.TYPE_DEPARTED) {
			this.received = false; 
		} else if (subType == Survey.TYPE_RECEIVED) {
			this.received = true;
		}
		 
		this.content 	= cursor_surveyInfo.getString(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CONTENT));
		this.TS			= cursor_surveyInfo.getLong(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CREATED_TS));
		
		
		this.checkTS	= cursor_surveyInfo.getLong(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CHECKED_TS));

		//this.openTS = _openTS;	// TODO
		//this.closeTS = _closeTS;	// TODO
	}

	public Survey(Parcel source) {
		readRomParcel(source);
	}

	public Survey(
			String				idx, 
			int					type, 
			String				title, 
			String				content, 
			String 				senderIdx, 
			ArrayList<String>	receivers, 
			boolean				received,
			long				TS,
			boolean				checked, 
			long 				checkTS,
			boolean				answered
			) {
		this.idx = idx;
		this.type = type;
		this.title = title;
		this.content = content;
		this.senderIdx = senderIdx;
		this.receiversIdx = receivers;
		this.received = received;
		this.TS = TS;
		this.checked = checked;
		this.checkTS = checkTS;
		this.answered = answered;
	}

	public Survey clone() {
		Survey survey = (Survey)this.clone(new Survey());

		survey.answered = this.answered;
		survey.form = this.form;
		
		return survey;
	}
	
	public void sendAnswerSheet(Context context, AnswerSheet answerSheet) {
		GCMMessageSender.sendSurveyAnswerSheet(context, this, answerSheet);
		
		
	}
	
	public void afterSendAnswerSheet(Context context, AnswerSheet answerSheet, boolean status) {
		// TODO : DBProcManager.sharedManager(context).document().
		// TODO : animation
	}
	
	
	public class AnswerSheet extends ArrayList<ArrayList<Integer>> {
		
	}

	@Override
	public void afterSend(Context context, boolean successful) {
		if(successful) {
			// Success
			DBProcManager.sharedManager(context).survey().saveSurveyOnSend(this.idx, this.title, this.content, this.senderIdx, this.TS);
		}  else {
			// Failure
		}
		// TODO : Animation 처리
	}
	
	public static class Form extends HashMap<String, Object>{
		public static final String TITLE = KEY.SURVEY.QUESTION_TITLE;
		public static final String CONTENT = KEY.SURVEY.QUESTION_CONTENT;
		public static final String OPEN_TS = KEY.SURVEY.OPEN_TS;
		public static final String CLOSE_TS = KEY.SURVEY.CLOSE_TS;
		
		public static final String QUESTIONS = KEY.SURVEY.QUESTIONS;
		public static final String OPTIONS = KEY.SURVEY.OPTIONS;
		
		public static final String IS_MULTIPLE = KEY.SURVEY.IS_MULTIPLE;
		
		public Form() {}
		
		public static Form parseForm(String json) {
			// TODO
			Form form = new Form();
			return form;
		}
		
		public static Form parseForm(Data data) {
			// TODO
			Form form = new Form();
			return form;
		}
		
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

		boolean[] ba = {answered}; 
		dest.writeBooleanArray(ba);
	}
	
	private void readRomParcel(Parcel source) {
		super.readFromParcel(source);

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
