package kr.go.KNPA.Romeo.GCM;

import java.util.ArrayList;
import java.util.List;

import kr.go.KNPA.Romeo.GCMIntentService;
import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.ChatFragment;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

/**
 * GCMIntentService 의 {@link GCMIntentService#onMessage(Context, Intent) onMessage(Context, Intent)} 메서드를 처리하기 좋도록 따로 불리해 놓은 클래스이다.
 *
 */
public class GCMMessageManager {
	
	private static final String TAG = "GCMMessageManager";
	
	/**
	 * @name GCMMessageManager Singleton
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
        		Message message = (Message)payload.getData().get(0, KEY._MESSAGE);
       
		        switch(message.mainType()) {
			    	case Message.MESSAGE_TYPE_CHAT 		:	onChat((Chat)message);			break;
			    	case Message.MESSAGE_TYPE_DOCUMENT 	:	onDocument((Document)message);	break;
			    	case Message.MESSAGE_TYPE_SURVEY 	:	onSurvey((Survey)message);		break;
		    	}

        	}	// MESSAGE : RECEIVED  - END
        }	// MESSAGE : ?? - END
        
        
        // Destroy
        payload = null;			events = null;
     
    }
	
	// on Message in cases
	private void onChat (Chat chat) {
		
		// 방이 존재하지 않으면 DB상에 새로 만든다.
		if(DBProcManager.sharedManager(context).chat().roomExists(chat.roomCode) == false) {
			DBProcManager.sharedManager(context)
				.chat().createRoom(Room.getUsersIdx(context, chat.senderIdx, chat.receiversIdx), chat.type(), chat.roomCode);	// TODO chat.type
		}
		
		// DB상 방에 Chat 저장
		DBProcManager.sharedManager(context)
			.chat().saveChatOnReceived(chat.roomCode, chat.idx, chat.senderIdx, chat.content, chat.contentType, chat.TS);
		
		if(isRunningProcess(context))		// 실행중인지 아닌지. 판단.
			ChatFragment.receive(chat); 	// 현재 챗방에 올리기. 및 알림
		
		notifyMessage(chat);
	}
	
	private void onDocument(Document document) {
		DBProcManager.sharedManager(context)
		.document()
		.saveDocumentOnReceived(document.idx, document.senderIdx, document.title, document.content, 
								document.TS, document.forwards, document.files);
		
		if(isRunningProcess(context))
			DocumentFragment.receive(document);		//리스트뷰에 notify
		
		notifyMessage(document);
	}
	
	private void onSurvey(Survey survey) {
		DBProcManager.sharedManager(context)
			.survey().saveSurveyOnReceived(survey.idx);
		
		if(isRunningProcess(context))
			SurveyFragment.receive(survey);		//리스트뷰에 notify
		
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
	
		Intent intent = new Intent(context, MainActivity.class);
		Bundle b = new Bundle();
		b.putLong("TEST", System.currentTimeMillis());
		intent.putExtras(b);
		
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, type, intent, 
				0);
		
		Notification nt = new Notification(R.drawable.icon, ticker, System.currentTimeMillis());
		nt.setLatestEventInfo(context, title, content, contentIntent);
		nt.defaults = Notification.DEFAULT_SOUND;
		nt.flags = nt.flags | Notification.FLAG_AUTO_CANCEL;
		
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(1000);
		
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
