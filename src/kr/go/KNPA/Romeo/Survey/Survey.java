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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Survey extends Message {// implements Parcelable{
	
	// Message Sub Type Constants
	public static final int TYPE_RECEIVED = 0;
	public static final int TYPE_DEPARTED = 1;
	
	public Form form;
	
	// Constructor
	public Survey() {}
	
	public Survey(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		if(jo.has(KEY.SURVEY.FORM))
			this.form = Form.parseForm(jo.getJSONObject(KEY.SURVEY.FORM).toString());
	}
	
	private static Survey surveyFromServer;
	public static Survey surveyFromServer(Context context, final String surveyIdx, int subType) {
		//Cursor cursor_surveyInfo = DBProcManager.sharedManager(context).survey().getSurveyInfo(surveyIdx);
		//cursor_surveyInfo.moveToFirst();
		
		Thread t = new Thread() {
			public void run() {
				Data reqData = new Data().add(0, KEY.MESSAGE.IDX, surveyIdx);
				Payload request = new Payload().setEvent(Event.Message.Survey.getContent()).setData(reqData);
				Connection conn = new Connection().async(false).requestPayload(request).request();
				Payload response = conn.getResponsePayload();
				Data respData = response.getData();
				surveyFromServer = (Survey)respData.get(0, KEY._MESSAGE);
			}
		};
		
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return surveyFromServer;
	}
	/*
	public Survey(Context context, Cursor c) {
		SurveyProcManager spm = DBProcManager.sharedManager(context).survey();
		Cursor cursor_surveyInfo = spm.getSurveyInfo(this.idx);
		
		String idx = c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IDX));
		int subType = cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_TYPE));
		
		Survey fromServer = surveyFromServer(
				context, 
				idx, 
				subType);
		
		this.idx 			= idx;
		this.type = Message.MESSAGE_TYPE_SURVEY * Message.MESSAGE_TYPE_DIVIDER + subType;
		
		this.title 			= fromServer.title;
		this.content 		= fromServer.content;
		this.senderIdx		= fromServer.senderIdx;		
		// X : receiversIdx 
		
		if(subType == Survey.TYPE_DEPARTED) {
			this.received = false; 
		} else if (subType == Survey.TYPE_RECEIVED) {
			this.received = true;
		}

		this.TS			= fromServer.TS;
		this.checked 	= c.getInt(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_CHECKED)) == 1 ? true : false;
		this.checkTS	= cursor_surveyInfo.getLong(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CHECKED_TS));
	}
	*/
	public Survey(Context context, String surveyIdx) {
		SurveyProcManager spm = DBProcManager.sharedManager(context).survey();
		Cursor cursor_surveyInfo = spm.getSurveyInfo(surveyIdx);
		
		String idx = surveyIdx;
		cursor_surveyInfo.moveToFirst();
		int subType = cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_TYPE));
		
		Survey fromServer = surveyFromServer(
				context, 
				idx, 
				subType);
		
		this.idx 			= idx;
		this.type = Message.MESSAGE_TYPE_SURVEY * Message.MESSAGE_TYPE_DIVIDER + subType;
		
		this.title 			= fromServer.title;
		this.content 		= fromServer.content;
		this.senderIdx		= fromServer.senderIdx;		
		// X : receiversIdx 
		
		if(subType == Survey.TYPE_DEPARTED) {
			this.received = false; 
		} else if (subType == Survey.TYPE_RECEIVED) {
			this.received = true;
		}

		this.TS			= fromServer.TS;
		this.checked 	= cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_CHECKED)) == 1 ? true : false;
		this.checkTS	= cursor_surveyInfo.getLong(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CHECKED_TS));

		this.form = fromServer.form;
	}

	/*
	public Survey(Parcel source) {
		readRomParcel(source);
	}
	 */
	
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
			long 				checkTS
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
	}
	
	public Survey (
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
			Form				form
			) {
		this(idx, type, title, content, senderIdx, receivers, received, TS, checked, checkTS);
		this.form = form;
	}

	public static boolean isAnswered(Context context, String surveyIdx) {
		Cursor cursor_surveyInfo = DBProcManager.sharedManager(context).survey().getSurveyInfo(surveyIdx);
		cursor_surveyInfo.moveToFirst();
		
		return (cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_ANSWERED))>0 ? true : false);
	}
	
	public boolean isAnswered(Context context) {
		return Survey.isAnswered(context, this.idx);
	}
	
	public Survey clone() {
		Survey survey = (Survey)this.clone(new Survey());
		survey.form = this.form;
		return survey;
	}
	
	public void sendAnswerSheet(Context context, AnswerSheet answerSheet) {
		GCMMessageSender.sendSurveyAnswerSheet(context, this, answerSheet);
	}
	
	public void afterSendAnswerSheet(Context context, AnswerSheet answerSheet, boolean status) {
		DBProcManager.sharedManager(context).survey().updateAnsweredTS(this.idx, System.currentTimeMillis()/1000);
		// TODO : animation
	}
	
	
	public static class AnswerSheet extends ArrayList<ArrayList<Integer>> {
		
	}

	@Override
	public void afterSend(Context context, boolean successful) {
		if(successful) {
			// Success
			DBProcManager.sharedManager(context).survey().saveSurveyOnSend(this.idx);
		}  else {
			// Failure
		}
		// TODO : Animation 처리
		super.afterSend(context, successful);
	}
	
	public static class Form extends HashMap<String, Object>{
		private static final String TAG = Form.class.getName();

		/*
		X public static final String TITLE = KEY.SURVEY.QUESTION_TITLE;
		X public static final String CONTENT = KEY.SURVEY.QUESTION_CONTENT;
		public static final String OPEN_TS = KEY.SURVEY.OPEN_TS;
		public static final String CLOSE_TS = KEY.SURVEY.CLOSE_TS;
		
		public static final String QUESTIONS = KEY.SURVEY.QUESTIONS;
		public static final String OPTIONS = KEY.SURVEY.OPTIONS;
		
		public static final String IS_MULTIPLE = KEY.SURVEY.IS_MULTIPLE;
		*/
		public Form() {}
		
		public static Form parseForm(String json) {
			try {
			JSONObject jo = new JSONObject(json);
			
			Form form = new Form();
			
			form.put( KEY.SURVEY.OPEN_TS ,  jo.getLong(KEY.SURVEY.OPEN_TS) );
			form.put( KEY.SURVEY.CLOSE_TS,  jo.getLong(KEY.SURVEY.CLOSE_TS) );
			
			
			JSONArray jQuestions = jo.getJSONArray(KEY.SURVEY.QUESTIONS);
			
			for(int qi=0; qi<jQuestions.length(); qi++) {
				JSONObject jQuestion = jQuestions.getJSONObject(qi);
				Question question = new Question();
				if(jQuestion.has(KEY.SURVEY.QUESTION_TITLE))
					question.title(jQuestion.getString(KEY.SURVEY.QUESTION_TITLE));
				if(jQuestion.has(KEY.SURVEY.QUESTION_CONTENT))
					question.content(jQuestion.getString(KEY.SURVEY.QUESTION_CONTENT));
				if(jQuestion.has(KEY.SURVEY.IS_MULTIPLE))
					question.isMultiple( ( jQuestion.getInt(KEY.SURVEY.IS_MULTIPLE) > 0 ? true : false ) );
				JSONArray jOptions = jQuestion.getJSONArray(KEY.SURVEY.OPTIONS);
				for(int oi=0; oi<jOptions.length(); oi++){
					question.addOption(jOptions.getString(oi));
				}
				
				form.addQuestion(question);
			}
			
			return form;
			} catch (JSONException e) {
				Log.e(TAG,e.getMessage());
				return null;
			}
		}

		public ArrayList<Question> questions() {
			ArrayList<Question> _questions = null;
			if(this.containsKey(KEY.SURVEY.QUESTIONS) == false) {
				_questions = new ArrayList<Question>();
				this.put(KEY.SURVEY.QUESTIONS, _questions);
			}
			
			_questions = (ArrayList<Question>)this.get(KEY.SURVEY.QUESTIONS);
			
			if(_questions == null) {
				_questions = new ArrayList<Question>();
				this.remove(KEY.SURVEY.QUESTIONS);
				this.put(KEY.SURVEY.QUESTIONS, _questions);
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

/*
	// Implements Parcelable
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);

		// Form
	}
	
	private void readRomParcel(Parcel source) {
		super.readFromParcel(source);

		// Form
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
	*/
}
