package kr.go.KNPA.Romeo.GCM;

import java.util.List;

import kr.go.KNPA.Romeo.GCMIntentService;
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
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * GCMIntentService 의 {@link GCMIntentService#onMessage(Context, Intent) onMessage(Context, Intent)} 메서드를 처리하기 좋도록 따로 불리해 놓은 클래스이다.
 *
 */
public class GCMMessageManager {
	
	private static final String TAG = "GCMMessageManager";
	
	/**
	 * @name GCMMessageManager Single-Tone
	 * @{
	 */
	private static GCMMessageManager _sharedManager = null;
	
	public static GCMMessageManager sharedManager() {
		if(_sharedManager == null)
			_sharedManager = new GCMMessageManager();
		return _sharedManager;
	}
	/** @} */
	
	//// On Process Variables
	// Class Variables associated with DB
	private DBManager 		dbManager 		= null;
	private SQLiteDatabase 	db 				= null;
	private String 			tableName 		= null;
	//
	private Context			context			= null;
	private Payload 		payload 		= null;
	private String[] 		events 			= null;
	
	/**
	 * 푸시로 받은 메시지가 도착했을 때 호출되는 메서드이다.\n
	 * 클래스내에 존재하는 private member {@link dbManager}, {@link db}, {@link payload}, {@link events}에 대해 메서드 초반에 초기화시키고,\n
	 * 중반에 각 메시지 타입에 맞는 메서드 ({@link onChat(Chat)}, {@link onDocument(Document)}, {@link onSurvey(Survey)})들을 호출하여 적절하게 처리 후, \n
	 * 위에 언급한 private member 변수들을 destroy하는 작어블 거친다.
	 *  
	 * @param context GCMIntentService.onMessage(Context, Intent)에서 넘어오는 Context 변수이다.
	 * @param intent GCMIntentService.onMessage(Context, Intent)에서 넘어오는 Intent형 변수로, extra에 GCM 메시지가 담겨있다.
	 */
	public void onMessage(Context context, Intent intent) {
		
		// Context Setting
		this.context = context;
		
		// DB Open & Setting
		dbManager = new DBManager(context);
		db = dbManager.getWritableDatabase();
        
		// Payload 
		Bundle b = intent.getExtras();        
		String _payload = b.getString("payload");
		payload = new Payload(_payload);
        
		// Specify Event
        String event = payload.getEvent();
        events = event.split(":");
        
        
        if(events[0].equalsIgnoreCase(Event.Message())) {	// MESSAGE
        	if(events[1].equalsIgnoreCase("RECEIVED")) {	// RECEIVED
        		
        		// payload 속에 담겨있는 Message 객체
        		Message message = (Message)payload.getData().get(0, Data.KEY_MESSAGE);
       
		        switch(message.mainType()) {
			    	case Message.MESSAGE_TYPE_CHAT 		:	onChat((Chat)message);			break;
			    	case Message.MESSAGE_TYPE_DOCUMENT 	:	onDocument((Document)message);	break;
			    	case Message.MESSAGE_TYPE_SURVEY 	:	onSurvey((Survey)message);		break;
		    	}

        	}	// MESSAGE : RECEIVED  - END
        }	// MESSAGE : ?? - END
        
        
        // Destroy
        db.close();				db = null;
        dbManager.close();		dbManager = null;
        payload = null;			events = null;
     
    }
	
	// on Message in cases
	private void onChat (Chat chat) {

		switch(chat.subType()) {
			case Chat.TYPE_COMMAND : tableName = DBManager.TABLE_COMMAND; break;
			case Chat.TYPE_MEETING : tableName = DBManager.TABLE_MEETING; break;
		}

		if(isRunningProcess(context))		// 실행중인지 아닌지. 판단.
			ChatFragment.receive(chat); 	// 현재 챗방에 올리기. 및 알림
		
		// TODO : DB에 삽입
		
		notifyMessage(chat);
	}
	
	private void onDocument(Document document) {
		
		switch(document.subType()) {
			case Document.TYPE_DEPARTED : 
			case Document.TYPE_RECEIVED : 
			case Document.TYPE_FAVORITE : tableName = DBManager.TABLE_DOCUMENT; break;
		}

		if(isRunningProcess(context))
			DocumentFragment.receive(document);		//리스트뷰에 notify

		// TODO : DB에 삽입
		
		notifyMessage(document);
	}
	
	private void onSurvey(Survey survey) {
		
		switch(survey.subType()) {
			case Survey.TYPE_DEPARTED :
			case Survey.TYPE_RECEIVED :	tableName = DBManager.TABLE_SURVEY; break;
		}
		
		if(isRunningProcess(context))
			SurveyFragment.receive(survey);		//리스트뷰에 notify

		// TODO : DB에 삽입
		
		notifyMessage(survey);
	}

	//// Helper Procedures	//// 
	private NotificationManager getNotificationManager() {
		return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	private Notification makeNotification(NotificationManager nm, int type, String ticker, String title, String content) {
		//알림만 띄우지
		
		title = title.substring(0, Math.min(title.length(), 15));
		content = content.substring(0, Math.min(content.length(),30));
	
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, type, new Intent(context, MainActivity.class), 
				0);
		
		Notification nt = new Notification(R.drawable.icon, ticker, System.currentTimeMillis());
		nt.setLatestEventInfo(context, title, content, contentIntent);
		nt.flags = nt.flags | Notification.FLAG_AUTO_CANCEL;
		
		return nt;
	}
	
	private void refreshNotification(NotificationManager nm, Notification nt, int type) {
		nm.cancel(type);
		nm.notify(type, nt);
	}
	
	private void notifyMessage(Message message) {
		message.type();
		String ticker = "";
		String title = "";
		String content = "";
		switch (message.mainType() ) {
		case Message.MESSAGE_TYPE_CHAT :
			if(message.subType() == Chat.TYPE_MEETING) {
				ticker = context.getString(R.string.notification_meeting_ticker);
			} else if(message.subType() == Chat.TYPE_COMMAND) {
				ticker = context.getString(R.string.notification_command_ticker);
			}
			title = message.content; 	break;
			
		case Message.MESSAGE_TYPE_DOCUMENT : 
			ticker = context.getString(R.string.notification_document_ticker);
			title = message.title;
			content = message.content;	break;
			
		case Message.MESSAGE_TYPE_SURVEY : 
			ticker = context.getString(R.string.notification_survey_ticker);
			title = message.title;
			content = message.content;	break;
		}
		
		NotificationManager nm = getNotificationManager();
		Notification nt = makeNotification(nm, message.type(), ticker, title, content);
		refreshNotification(nm, nt, message.type());
	}
	
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
