package kr.go.KNPA.Romeo.GCM;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.go.KNPA.Romeo.GCMIntentService;
import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.Chat.RoomFragment;
import kr.go.KNPA.Romeo.Chat.RoomListFragment;
import kr.go.KNPA.Romeo.Chat.RoomModel;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	private static Toast 	mToast		= null;
	private static Handler toastHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle b = msg.getData();
			Toast toast = GCMMessageManager.makeToast((Context)msg.obj, (Bitmap)b.getParcelable("userPic"), b.getString("department"), b.getString("rank"), b.getString("name"), b.getString("content"));
			toast.show();
		};
	};
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
	public void onMessage(final Context context, Intent intent)
	{
		
		// Context Setting
		this.mContext = context;
		
		showToast();

		
//		Handler handler = new Handler();
//		handler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				Toast.makeText(mContext.getApplicationContext(), Calendar.getInstance().getTimeInMillis() + " Message", Toast.LENGTH_SHORT).show();
//			}
//		});
				
		// Payload
		Bundle b = intent.getExtras();
		String _payload = b.getString("payload");
		Payload payload = new Payload(_payload);

		// Specify Event
		String event = payload.getEvent().trim();
		if (event.equals(Event.PUSH_UPDATE_LAST_READ_TS))
		{
			Long lastReadTS = Long.valueOf((Integer) payload.getData().get(0, KEY.CHAT.LAST_READ_TS));

			onUpdateLastReadTS(payload.getData().get(0, KEY.USER.IDX).toString(), payload.getData().get(0, KEY.CHAT.ROOM_CODE).toString(), lastReadTS);
		}
		else if (event.equals(Event.PUSH_MESSAGE))
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
		else if (event.equals(Event.PUSH_USER_JOIN_ROOM))
		{
			@SuppressWarnings("unchecked")
			ArrayList<String> newUsers = (ArrayList<String>) payload.getData().get(0, KEY.CHAT.ROOM_MEMBER);
			String userIdx = payload.getData().get(0, KEY.USER.IDX).toString();
			String roomCode = payload.getData().get(0, KEY.CHAT.ROOM_CODE).toString();

			onUserJoinRoom(userIdx, roomCode, newUsers);
		}
		else if (event.equals(Event.PUSH_USER_LEAVE_ROOM))
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
			RoomFragment currentRoomFragment = RoomListFragment.getCurrentRoom();

			if (currentRoomFragment != null && currentRoomFragment.getRoom() != null && currentRoomFragment.getRoom().getCode() != null && currentRoomFragment.getRoom().getCode().equals(roomCode))
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
			Room room = new Room(chat.roomCode);
			room.setStatus(Room.STATUS_INVITED);
			room.setType(chat.subType());

			ArrayList<String> chattersIdx = new ArrayList<String>(chat.receiversIdx.size());

			chattersIdx.add(chat.senderIdx);
			String userIdx = UserInfo.getUserIdx(mContext);

			for (int i = 0; i < chat.receiversIdx.size(); i++)
			{
				String chatterIdx = chat.receiversIdx.get(i);
				if (!chatterIdx.equals(userIdx))
				{
					chattersIdx.add(chatterIdx);
				}
			}

			room.addChatters(chattersIdx);

			RoomModel model = new RoomModel(mContext, room);
			model.init();
			model.createRoom(false);
		}

		// Chat 저장
		//proc.saveChatOnReceived(chat.roomCode, chat.idx, chat.senderIdx, chat.content, chat.contentType, chat.TS);

		// 앱이 실행 중이면 callback 호출
		if (isRunningProcess(mContext) && MainActivity.sharedActivity() != null)
		{
			FragmentManager fm = MainActivity.sharedActivity().getSupportFragmentManager();
			RoomListFragment roomListController = (RoomListFragment) fm.findFragmentByTag(RoomListFragment.class.getSimpleName());
			// 현재 채팅 fragment에 있다면
			if (roomListController != null)
			{
				// chatFragment의 callback 실행
				roomListController.onReceiveChat(chat);

				// 만약 room에 입장해있지 않으면 notification 만들기
				if (RoomListFragment.getCurrentRoom() == null)
				{
					notifyMessage(chat);
				}
				else
				{
					// 입장해 있다면 notification 대신 callback 실행
					RoomListFragment.getCurrentRoom().onReceiveChat(chat);
				}
				return;
			}
		}

		notifyMessage(chat);
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
			RoomFragment roomController = (RoomFragment) fm.findFragmentByTag(RoomFragment.class.getSimpleName());

			if (roomController != null && roomController.getRoom().getCode().equals(roomCode))
			{
				roomController.onChatterJoin(inviterIdx, newbies);
				return;
			}
		}

		Room room = new Room(roomCode);

		RoomModel model = new RoomModel(mContext, room);
		model.init();
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
			RoomFragment roomController = (RoomFragment) fm.findFragmentByTag(RoomFragment.class.getSimpleName());

			if (roomController != null && roomController.getRoom().getCode().equals(roomCode))
			{
				roomController.onChatterLeave(userIdx);
				return;
			}
		}

		// 그렇지 않다면 RoomModel에서 removeChatter 호출
		Room room = new Room(roomCode);

		RoomModel model = new RoomModel(mContext, room);
		model.init();
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

		String toastContent = "";
		String toastDepartment = "";
		String toastRank = "";
		String toastName = "";
		
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
			
			toastContent = message.content;
			break;

		case Message.MESSAGE_TYPE_DOCUMENT:
			ticker = mContext.getString(R.string.notification_document_ticker);
			title = message.title;
			content = message.content;
			
			toastContent = message.title;
			break;

		case Message.MESSAGE_TYPE_SURVEY:
			ticker = mContext.getString(R.string.notification_survey_ticker);
			title = message.title;
			content = message.content;
			
			toastContent = message.title;
			break;
		}

		title = title.substring(0, Math.min(title.length(), 15));
		content = content.substring(0, Math.min(content.length(), 30));

		Intent intent = new Intent(mContext, MainActivity.class);
		Bundle b = new Bundle();
		b.putInt(KEY.MESSAGE.TYPE, message.type());
		boolean doAlarm = true;
		if (message.mainType() == Message.MESSAGE_TYPE_CHAT)
		{
			String roomCode = ((Chat) message).roomCode;
			b.putString(KEY.CHAT.ROOM_CODE, roomCode);

			Cursor c = DBProcManager.sharedManager(mContext).chat().getRoomInfo(roomCode);

			if (c.moveToNext())
			{
				int isAlarmOn = c.getInt(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_IS_ALARM_ON));
				doAlarm = isAlarmOn != 0 ? true : false;
				String roomTitle = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_TITLE));
				String roomAlias = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_ALIAS));

				if (roomAlias == null || roomAlias.trim().equals(""))
				{
					content = roomTitle;
				}
				else
				{
					content = roomAlias;
				}

				content = Formatter.makeEllipsis(content, Constants.CHAT_ROOM_TITLE_MAX_LEN);
			}
		}
		intent.putExtras(b);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		// Adds the back stack
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent to the top of the stack
		stackBuilder.addNextIntent(intent);

		// Gets a PendingIntent containing the entire back stack
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		ImageManager im = new ImageManager();

		Bitmap profileImg = im.load(ImageManager.PROFILE_SIZE_SMALL, message.senderIdx);
		if (profileImg == null)
		{
			profileImg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.user_pic_default);
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setLargeIcon(profileImg).setTicker(ticker).setSmallIcon(R.drawable.icon).setContentTitle(title)
				.setContentText(content).setAutoCancel(true);

		// PendingIntent contentIntent = PendingIntent.getActivity(mContext,
		// message.type(), intent, 0);

		mBuilder.setContentIntent(resultPendingIntent);

		if (doAlarm == true)
		{
			if (UserInfo.getAlarmEnabled(mContext) == true)
			{
				mBuilder.setDefaults(Notification.DEFAULT_ALL);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
		
		
		
		User user = MemberManager.sharedManager().getUser(message.senderIdx);
		toastName = user.name;
		toastRank = User.RANK[user.rank];
		toastDepartment = user.department.nameFull;
		//showToast(mContext, profileImg, toastDepartment, toastRank, toastName, toastContent);
		
	}

	private static Toast makeToast(Context context, Bitmap userPic, String department, String rank, String name, String content) {
		
		if(mToast == null) {
			mToast = new Toast(context.getApplicationContext());
			mToast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		
		View toastView = mToast.getView();
		if(toastView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			toastView = inflater.inflate(R.layout.romeo_toast, null, false);
			mToast.setView(toastView);
		}
		
		ImageView userPicIV = (ImageView) toastView.findViewById(R.id.userPic);
		if(userPic != null)
			userPicIV.setImageBitmap(userPic);
		else
			userPicIV.setImageResource(R.drawable.user_pic_default);
		
		TextView departmentTV = (TextView) toastView.findViewById(R.id.department);
		department = department != null ? department : "";
		departmentTV.setText(department);
		
		TextView rankNameTV = (TextView) toastView.findViewById(R.id.rankName);
		rank = rank != null ? rank : "";
		name = name != null ? name : "";
		String rankName = rank + " " + name;
		rankNameTV.setText(rankName);
		
		TextView contentTV = (TextView) toastView.findViewById(R.id.content);
		content = content != null ? content : "";
		content = content.substring(0, Math.min(content.length(), 24));
		contentTV.setText(content);
		
		//mToast.show();
		return mToast;
	}
	
	private void showToast() {
		
		Handler handler = new Handler();
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if(mToast == null) {
					mToast = new Toast(mContext.getApplicationContext());
					mToast.setGravity(Gravity.CENTER_VERTICAL, 0, 80);
					mToast.setDuration(Toast.LENGTH_SHORT);
				}
				
				mToast.setText(Calendar.getInstance().getTimeInMillis() + " Message");
				mToast.show();
			}
		});
	}
	private void showToast(final Context context, final Bitmap userPic, final String department, final String rank, final String name, final String content) {
		//final Toast toast = makeToast(context, userPic, department, rank, name, content);
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				android.os.Message msg = toastHandler.obtainMessage();
				
				Bundle b = new Bundle();
				b.putParcelable("userPic", userPic);
				b.putString("department", department);
				b.putString("rank", rank);
				b.putString("name", name);
				b.putString("content", content);
				msg.setData(b);
				
				msg.obj = context;
				
				//msg.obj = toast;
				toastHandler.sendMessage(msg);
			}
			
		}).start();
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
