package kr.go.KNPA.Romeo.GCM;

import java.util.ArrayList;
import java.util.List;

import kr.go.KNPA.Romeo.GCMIntentService;
import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.Chat.RoomController;
import kr.go.KNPA.Romeo.Chat.RoomListController;
import kr.go.KNPA.Romeo.Chat.RoomModel;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;

/**
 * GCMIntentService 의 {@link GCMIntentService#onMessage(Context, Intent)
 * onMessage(Context, Intent)} 메서드를 처리하기 좋도록 따로 분리해 놓은 클래스이다.
 * 
 */
public class GCMMessageManager {

	/**
	 * @name GCMMessageManager Singleton
	 * @{
	 */
	private static GCMMessageManager	_sharedManager	= null;

	public static GCMMessageManager sharedManager()
	{
		if (_sharedManager == null)
		{
			_sharedManager = new GCMMessageManager();
		}
		return _sharedManager;
	}

	/** @} */

	// // On Process Variables
	//
	private Context	mContext	= null;

	/**
	 * 푸시로 받은 메시지가 도착했을 때 호출되는 메서드이다.\n 클래스내에 존재하는 private member
	 * {@link dbManager}, {@link db}, {@link payload}, {@link events}에 대해 메서드
	 * 초반에 초기화시키고,\n 중반에 각 메시지 타입에 맞는 메서드 ({@link onChat(Chat)}, {@link
	 * onDocument(Document)}, {@link onSurvey(Survey)})들을 호출하여 적절하게 처리 후, \n 위에
	 * 언급한 private member 변수들을 destroy하는 작업을 거친다.
	 * 
	 * @param context
	 *            GCMIntentService.onMessage(Context, Intent)에서 넘어오는 Context
	 *            변수이다.
	 * @param intent
	 *            GCMIntentService.onMessage(Context, Intent)에서 넘어오는 Intent형
	 *            변수로, extra에 GCM 메시지가 담겨있다.
	 */
	public void onMessage(Context context, Intent intent)
	{

		// Context Setting
		this.mContext = context;

		// Payload
		Bundle b = intent.getExtras();
		String _payload = b.getString("payload");
		Payload payload = new Payload(_payload);

		// Specify Event
		String event = payload.getEvent().trim();
		if (event.equalsIgnoreCase(Event.PUSH_UPDATE_LAST_READ_TS))
		{
			Long lastReadTS = Long.valueOf((Integer) payload.getData().get(0, KEY.CHAT.LAST_READ_TS));

			onUpdateLastReadTS(payload.getData().get(0, KEY.USER.IDX).toString(), payload.getData().get(0, KEY.CHAT.ROOM_CODE).toString(), lastReadTS);
		}
		else if (event.equalsIgnoreCase(Event.Message.received()))
		{
			// payload 속에 담겨있는 Message 객체
			Message message = (Message) payload.getData().get(0, KEY._MESSAGE);

			switch (message.mainType())
			{
			case Message.MESSAGE_TYPE_CHAT:
				onReceiveChat((Chat) message);
				break;
			case Message.MESSAGE_TYPE_DOCUMENT:
				onDocument((Document) message);
				break;
			case Message.MESSAGE_TYPE_SURVEY:
				onSurvey((Survey) message);
				break;
			}
		}
		else if (event.equalsIgnoreCase(Event.PUSH_USER_JOIN_ROOM))
		{
			@SuppressWarnings("unchecked")
			ArrayList<String> newUsers = (ArrayList<String>) payload.getData().get(0, KEY.CHAT.ROOM_MEMBER);
			String userIdx = payload.getData().get(0, KEY.USER.IDX).toString();
			String roomCode = payload.getData().get(0, KEY.CHAT.ROOM_CODE).toString();

			onUserJoinRoom(userIdx, roomCode, newUsers);
		}
		else if (event.equalsIgnoreCase(Event.PUSH_USER_LEAVE_ROOM))
		{
			onUserLeaveRoom(payload.getData().get(0, KEY.USER.IDX).toString(), payload.getData().get(0, KEY.CHAT.ROOM_CODE).toString());
		}

		// Destroy
		payload = null;
	}

	/**
	 * 다른 사람이 채팅 목록을 읽었다는 정보를 GCM으로부터 PUSH받을 때.
	 * 
	 * @param userIdx
	 * @param roomCode
	 * @param lastReadTS
	 */
	private void onUpdateLastReadTS(String userIdx, String roomCode, long lastReadTS)
	{
		if (isRunningProcess(mContext))
		{
			RoomController currentRoomFragment = RoomListController.getCurrentRoom();

			if (currentRoomFragment != null && currentRoomFragment.getRoom() != null && currentRoomFragment.getRoom().getCode() != null
					&& currentRoomFragment.getRoom().getCode().equalsIgnoreCase(roomCode))
			{
				currentRoomFragment.onUpdateLastTS(userIdx, lastReadTS);
			}
		}
	}

	// 채팅 메세지가 도착했을 때
	private void onReceiveChat(Chat chat)
	{
		ChatProcManager proc = DBProcManager.sharedManager(mContext).chat();

		// 방이 존재하지 않으면 DB상에 새로 만든다.
		if (proc.isRoomExists(chat.roomCode) == false)
		{
			proc.createRoom(chat.type(), chat.roomCode);
			proc.addUsersToRoom(chat.receiversIdx, chat.roomCode);
		}

		// Chat 저장
		proc.saveChatOnReceived(chat.roomCode, chat.idx, chat.senderIdx, chat.content, chat.contentType, chat.TS);

		// 앱이 실행 중이면 callback 호출
		if (isRunningProcess(mContext))
		{
			FragmentManager fm = MainActivity.sharedActivity().getSupportFragmentManager();
			RoomListController roomListController = (RoomListController) fm.findFragmentByTag(RoomListController.class.getSimpleName());
			// 현재 채팅 fragment에 있다면
			if (roomListController != null)
			{
				// chatFragment의 callback 실행
				roomListController.onReceiveChat(chat);

				// 만약 room에 입장해있지 않으면 notification 만들기
				if (RoomListController.getCurrentRoom() == null)
				{
					notifyMessage(chat);
				}
				else
				{
					// 입장해 있다면 notification 대신 callback 실행
					RoomListController.getCurrentRoom().onReceiveChat(chat);
				}
			}
		}
		else
		// 앱이 꺼져 있으면 그냥 바로 notification 만들기
		{
			notifyMessage(chat);
		}
	}

	private void onDocument(Document document)
	{
		DBProcManager.sharedManager(mContext).document().saveDocumentOnReceived(document.idx, document.senderIdx, document.title, document.content, document.TS, document.forwards, document.files);

		if (isRunningProcess(mContext))
			DocumentFragment.receive(document); // 리스트뷰에 notify

		notifyMessage(document);
	}

	private void onSurvey(Survey survey)
	{
		DBProcManager.sharedManager(mContext).survey().saveSurveyOnReceived(survey.idx);

		if (isRunningProcess(mContext))
			SurveyFragment.receive(survey); // 리스트뷰에 notify

		notifyMessage(survey);
	}

	private void onUserJoinRoom(String inviterIdx, String roomCode, ArrayList<String> newbies)
	{
		if (isRunningProcess(mContext))
		{
			FragmentManager fm = MainActivity.sharedActivity().getSupportFragmentManager();
			RoomController roomController = (RoomController) fm.findFragmentByTag(RoomController.class.getSimpleName());

			if (roomController != null && roomController.getRoom().getCode().equalsIgnoreCase(roomCode))
			{
				roomController.onChatterJoin(inviterIdx, newbies);
				return;
			}
		}

		Room room = new Room(roomCode);

		RoomModel model = new RoomModel(mContext, room);

		// DB Operation
		model.addChatters(inviterIdx, newbies);

		model = null;
		room = null;
		return;
	}

	private void onUserLeaveRoom(String userIdx, String roomCode)
	{

		// 앱이 켜져 있고 roomCode에 해당하는 채팅방에 입장해 있는 상태면 onChatterLeave 호출
		if (isRunningProcess(mContext))
		{
			FragmentManager fm = MainActivity.sharedActivity().getSupportFragmentManager();
			RoomController roomController = (RoomController) fm.findFragmentByTag(RoomController.class.getSimpleName());

			if (roomController != null && roomController.getRoom().getCode().equalsIgnoreCase(roomCode))
			{
				roomController.onChatterLeave(userIdx);
				return;
			}
		}

		// 그렇지 않다면 RoomModel에서 removeChatter 호출
		Room room = new Room(roomCode);

		RoomModel model = new RoomModel(mContext, room);

		// DB Operation
		model.removeChatter(userIdx);

		model = null;
		room = null;
		return;
	}

	private void notifyMessage(Message message)
	{

		String ticker = "";
		String title = "";
		String content = "";

		switch (message.mainType())
		{
		case Message.MESSAGE_TYPE_CHAT:

			if (message.subType() == Chat.TYPE_MEETING)
			{
				ticker = mContext.getString(R.string.notification_meeting_ticker);

			}
			else if (message.subType() == Chat.TYPE_COMMAND)
			{
				ticker = mContext.getString(R.string.notification_command_ticker);
			}
			title = message.content;
			break;

		case Message.MESSAGE_TYPE_DOCUMENT:
			ticker = mContext.getString(R.string.notification_document_ticker);
			title = message.title;
			content = message.content;
			break;

		case Message.MESSAGE_TYPE_SURVEY:
			ticker = mContext.getString(R.string.notification_survey_ticker);
			title = message.title;
			content = message.content;
			break;
		}

		title = title.substring(0, Math.min(title.length(), 15));
		content = content.substring(0, Math.min(content.length(), 30));

		Intent intent = new Intent(mContext, MainActivity.class);
		Bundle b = new Bundle();
		b.putInt(KEY.MESSAGE.TYPE, message.type());

		if (message.mainType() == Message.MESSAGE_TYPE_CHAT)
		{
			b.putString(KEY.CHAT.ROOM_CODE, ((Chat) message).roomCode);
		}

		intent.putExtras(b);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setTicker(ticker).setSmallIcon(R.drawable.icon).setContentTitle(title).setContentText(content);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, message.type(), intent, 0);

		mBuilder.setContentIntent(contentIntent);
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}

	private List<ActivityManager.RunningAppProcessInfo> processList(Context context)
	{
		/* 실행중인 process 목록 보기 */

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();

		return appList;
	}

	private boolean isRunningProcess(Context context)
	{
		final String packageName = "kr.go.KNPA.Romeo";
		boolean isRunning = false;
		List<ActivityManager.RunningAppProcessInfo> list = processList(context);

		for (RunningAppProcessInfo rapi : list)
		{
			if (rapi.processName.equals(packageName))
			{
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
