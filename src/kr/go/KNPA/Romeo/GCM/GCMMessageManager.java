package kr.go.KNPA.Romeo.GCM;

import java.util.List;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.ChatFragment;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class GCMMessageManager {
	
	//
	public static final int NOT_SPECIFIED = Message.NOT_SPECIFIED;
	private static final String TAG = "GCMMessageManager";
	
	// preDefined Variables

	
	// GCMMessageManager Single-Tone
	private static GCMMessageManager _sharedManager = null;
	public static GCMMessageManager sharedManager() {
		if(_sharedManager == null) {
			_sharedManager = new GCMMessageManager();
		}
		return _sharedManager;
	}


	
	//// On Process Variables
	// Class Variables associated with DB
	private DBManager 		dbManager 		= null;
	private SQLiteDatabase 	db 				= null;
	private String 			tableName 		= null;
	//
	private Context			context			= null;
	private Payload 		payload 		= null;
	private String[] 		events 			= null;
	private int 			messageType 	= NOT_SPECIFIED;
	private int 			messageSubType 	= NOT_SPECIFIED;
	
	// OnMessage
	public void onMessage(Context context, Intent intent) {			/** 푸시로 받은 메시지 */
		
		// Context Setting
		this.context = context;
		
		// DB Open & Setting
		dbManager = new DBManager(context);
		db = dbManager.getWritableDatabase();
        
		Bundle b = intent.getExtras();        
		String _payload = b.getString("payload");
		
        // Specify Payload and Event
        payload = new Payload(_payload);
        String event = payload.getEvent();
        events = event.split(":");
        
        
        
        if(events[0].equalsIgnoreCase(Event.Message())) {
        	if(events[1].equalsIgnoreCase("RECEIVED")) {
        		Message message = (Message)payload.getData().get(0, Data.KEY_MESSAGE);
        		
		    	messageType = message.mainType();
		    	messageSubType = message.subType();
		
		        switch(messageType) {
			    	case Message.MESSAGE_TYPE_CHAT 		:	onChat((Chat)message);			break;
			    	case Message.MESSAGE_TYPE_DOCUMENT 	:	onDocument((Document)message);	break;
			    	case Message.MESSAGE_TYPE_SURVEY 	:	onSurvey((Survey)message);		break;
		    	}

        	}	// MESSAGE : RECEIVED  - END
        }	// MESSAGE : ?? - END
        
        
        //// Destroy
        // DB Close
        db.close();
        dbManager.close();
        db = null;
        dbManager = null;
        
        payload = null;
        events = null;
        
        messageType = NOT_SPECIFIED;
        messageSubType = NOT_SPECIFIED;
        
        
    }
	
	// on Message in cases
	private void onChat (Chat chat) {

		switch(messageSubType) {
			case Chat.TYPE_COMMAND : tableName = DBManager.TABLE_COMMAND; break;
			case Chat.TYPE_MEETING : tableName = DBManager.TABLE_MEETING; break;
		}
		

		if(isRunningProcess(context)) {		// 실행중인지 아닌지. 판단.
		// DB에 삽입.
			ContentValues vals = new ContentValues();
			vals.put("content", chat.content);
			vals.put("appendix", chat.appendix.toBlob());
			vals.put("sender", chat.sender.idx);
			vals.put("receivers", User.usersToString(chat.receivers));
			vals.put("received", 1);
			vals.put("TS", chat.TS);
			vals.put("checked", 0);
			vals.put("roomCode", chat.getRoomCode());
			vals.put("checkTS", chat.checkTS);
			vals.put("idx", chat.idx);
			db.insert(tableName, null, vals);

		// 현재 챗방에 올리기. 및 알림
			ChatFragment.receive(chat);
		} else {
		// 알림만 띄우장
			
		}
		NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		nm.cancel(chat.type);
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 
				chat.type, 
				new Intent(context, MainActivity.class), 
				0);
		
		String ticker = "";
		if(messageSubType == Chat.TYPE_COMMAND) {
			ticker = context.getString(R.string.notification_command_ticker);
		} else if (messageSubType == Chat.TYPE_MEETING){
			ticker = context.getString(R.string.notification_meeting_ticker);
		}
		
		String contentTitle = chat.content.substring(0, Math.min(chat.content.length(), 30));
		String contentText = "";
		int iconRId = R.drawable.icon;
		
		Notification nt = new Notification(iconRId, ticker, System.currentTimeMillis());
		
		nt.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		nt.flags = nt.flags | Notification.FLAG_AUTO_CANCEL;
		
		nm.notify(chat.type, nt);
	}
	
	private void onDocument(Document document) {
		
		switch(messageSubType) {
			case Document.TYPE_DEPARTED : tableName = DBManager.TABLE_DOCUMENT; break;
			case Document.TYPE_RECEIVED : tableName = DBManager.TABLE_DOCUMENT; break;
			case Document.TYPE_FAVORITE : tableName = DBManager.TABLE_DOCUMENT; break;
		}
		

		if(isRunningProcess(context)) {
			//DB에 삽입.
			ContentValues vals = new ContentValues();
			vals.put("title", document.title);
			vals.put("content", document.content);
			vals.put("appendix", document.appendix.toBlob());
			vals.put("sender", document.sender.idx);
			vals.put("receivers", User.usersToString(document.receivers));
			vals.put("received", 1);
			vals.put("TS", document.TS);
			vals.put("checked", 0);
			vals.put("checkTS", document.checkTS);
			vals.put("favorite", document.favorite);
			vals.put("idx", document.idx);
			db.insert(tableName, null, vals);
	
			//리스트뷰에 notify
			DocumentFragment.receive(document);
		} else {

		}
		
		//알림만 띄우자
		NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		nm.cancel(document.type);
		
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 
				document.type, 
				new Intent(context, MainActivity.class), 
				0);
		
		String ticker = context.getString(R.string.notification_document_ticker);
		
		String contentTitle = document.title.substring(0, Math.min(document.title.length(), 15));
		String contentText = document.content.substring(0, Math.min(document.content.length(), 30));
		int iconRId = R.drawable.icon;
		
		Notification nt = new Notification(iconRId, ticker, System.currentTimeMillis());
		
		nt.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		nt.flags = nt.flags | Notification.FLAG_AUTO_CANCEL;
		
		nm.notify(document.type, nt);
	}
	
	private void onSurvey(Survey survey) {
		
		if(messageSubType == Survey.TYPE_DEPARTED){
			tableName = DBManager.TABLE_SURVEY;
		} else if(messageSubType == Survey.TYPE_RECEIVED) {
			tableName = DBManager.TABLE_SURVEY;
		} 
		
		if(isRunningProcess(context)) {
			//DB에 삽입.
			ContentValues vals = new ContentValues();
			vals.put("title", survey.title);
			vals.put("content", survey.content);
			vals.put("appendix", survey.appendix.toBlob());
			vals.put("sender", survey.sender.idx);
			vals.put("receivers", User.usersToString(survey.receivers));
			vals.put("received", 1);
			vals.put("TS", survey.TS);
			vals.put("checked", 0);
			vals.put("openTS", survey.openTS);
			vals.put("closeTS", survey.closeTS);
			vals.put("checkTS", survey.checkTS);
			vals.put("idx", survey.idx);
			db.insert(tableName, null, vals);
			
			//리스트뷰에 notify
			SurveyFragment.receive(survey);
		} else {

		}
		//알림만 띄우지
		NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		nm.cancel(survey.type);
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 
				survey.type, 
				new Intent(context, MainActivity.class), 
				0);
		
		String ticker = context.getString(R.string.notification_survey_ticker);
		
		String contentTitle = survey.title.substring(0, Math.min(survey.title.length(), 15));
		String contentText = survey.content.substring(0, Math.min(survey.content.length(),30));
		int iconRId = R.drawable.icon;
		
		Notification nt = new Notification(iconRId, ticker, System.currentTimeMillis());
		
		nt.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		nt.flags = nt.flags | Notification.FLAG_AUTO_CANCEL;
		
		nm.notify(survey.type, nt);
	}

	//// Helper Procedures	//// 
	
	private List<ActivityManager.RunningAppProcessInfo> processList(Context context) {
        /* 실행중인 process 목록 보기*/
		
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
 
        return appList;
    }
	
	private boolean isRunningProcess(Context context) {
		final String packageName = "kr.go.KNPA.Romeo";
		boolean isRunning = false;
		List<ActivityManager.RunningAppProcessInfo> list = processList(context);
		
		for (RunningAppProcessInfo rapi : list)
		{
			if(rapi.processName.equals(packageName)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
