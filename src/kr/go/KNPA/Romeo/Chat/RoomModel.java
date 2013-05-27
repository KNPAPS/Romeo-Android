package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.BaseModel;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Encrypter;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * 채팅방에 대한 Model Class
 */
public class RoomModel extends BaseModel {
	private static final String	TAG	= RoomModel.class.getSimpleName();
	private Room				mRoom;

	public RoomModel(Context context, Room room)
	{
		super(context);

		if (room == null)
		{
			Log.e(TAG, "생성자에 들어온 room 객체가 null임");
			throw new RuntimeException("생성자에 들어온 room 객체가 null임");
		}

		mRoom = room;

		if (room.getStatus() == Room.STATUS_CREATED)
		{
			fetchBaseInfo();
			fetchChatters();
		}
	}

	/**
	 * 방에서 나가기\n 로컬디비에서 해당 방의 정보를 삭제하고 서버에도 알림을 보낸다
	 */
	public void deleteRoom()
	{
		if (mRoom.getStatus() != Room.STATUS_CREATED)
		{
			return;
		}

		String senderIdx = UserInfo.getUserIdx(mContext);

		Data reqData = new Data();
		reqData.add(0, KEY.USER.IDX, senderIdx);
		reqData.add(0, KEY.CHAT.ROOM_CODE, mRoom.getCode());

		Payload request = new Payload().setEvent(Event.MESSAGE_CHAT_LEAVE_ROOM).setData(reqData);
		new Connection().async(false).requestPayload(request).request();

		DBProcManager.sharedManager(mContext).chat().deleteRoom(mRoom.getCode());
	}

	public boolean createRoom()
	{
		if (mRoom.getStatus() == Room.STATUS_CREATED)
		{
			Log.e(TAG, "이미 생성된 방에 대해 또 createRoom()을 호출");
			return false;
		}

		String roomCode = null;
		if (mRoom.getStatus() == Room.STATUS_VIRTUAL)
		{
			roomCode = makeRoomCode();

			Data reqData = new Data();
			reqData.add(0, KEY.CHAT.ROOM_MEMBER, mRoom.getChattersIdx());
			reqData.add(0, KEY.CHAT.ROOM_CODE, roomCode);
			reqData.add(0, KEY.MESSAGE.TYPE, mRoom.getType());
			reqData.add(0, KEY.USER.IDX, UserInfo.getUserIdx(mContext));

			Payload request = new Payload().setEvent(Event.MESSAGE_CHAT_CREATE_ROOM).setData(reqData);
			Payload response = new Connection().async(false).requestPayload(request).request().getResponsePayload();

			if (response.getStatusCode() == StatusCode.SUCCESS)
			{
				mRoom.setCode(roomCode);
			}
			else
			{
				Log.e(TAG, "방 생성 실패. response payload" + response.toJSON());
				return false;
			}
		}
		else
		{
			roomCode = mRoom.getCode();
		}

		ChatProcManager proc = DBProcManager.sharedManager(mContext).chat();
		proc.createRoom(mRoom.getType(), roomCode);
		proc.addUsersToRoom(mRoom.getChattersIdx(), roomCode);

		mRoom.setStatus(Room.STATUS_CREATED);
		adjustTitle();
		return true;
	}

	public Room getRoom()
	{
		return this.mRoom;
	}

	public String getRoomName()
	{
		String roomName = null;
		if (mRoom.getStatus() == Room.STATUS_VIRTUAL)
		{
			roomName = mRoom.getType() == Room.TYPE_MEETING ? mContext.getString(R.string.meetingTitle) : mContext.getString(R.string.commandTitle);
		}
		else
		{
			String alias = mRoom.getAlias();
			roomName = alias == null || alias.trim().equals("") ? mRoom.getTitle() : alias;
		}

		return roomName;
	}

	public boolean addChatters(String inviterIdx, ArrayList<String> chattersIdx)
	{
		mRoom.addChatters(chattersIdx);

		if (mRoom.getStatus() == Room.STATUS_CREATED)
		{
			adjustTitle();

			DBProcManager.sharedManager(mContext).chat().addUsersToRoom(chattersIdx, mRoom.getCode());

			String chatIdx = Chat.makeChatIdx(mContext);
			String newbiesStr = Formatter.join(chattersIdx, ":");

			DBProcManager.sharedManager(mContext).chat()
					.saveChatOnSend(chatIdx, mRoom.getCode(), inviterIdx, newbiesStr, Chat.CONTENT_TYPE_USER_JOIN, System.currentTimeMillis() / 1000, Chat.STATE_SUCCESS);

			// 자신이 초대했다면 서버를 통해 다른 사람들에게 알림
			String userIdx = UserInfo.getUserIdx(mContext);
			if (userIdx.equalsIgnoreCase(inviterIdx))
			{
				Data reqData = new Data();
				reqData.add(0, KEY.CHAT.ROOM_CODE, mRoom.getCode());
				reqData.add(0, KEY.USER.IDX, UserInfo.getUserIdx(mContext));
				reqData.add(0, KEY.CHAT.ROOM_MEMBER, chattersIdx);

				Payload request = new Payload().setEvent(Event.MESSAGE_CHAT_INVITE).setData(reqData);
				Payload response = new Connection().async(false).requestPayload(request).request().getResponsePayload();

				if (response != null && response.getStatusCode() == StatusCode.SUCCESS)
				{
					return true;
				}
				else
				{
					Log.e(TAG, "유저 초대 실패");
					return false;
				}
			}
		}

		return true;
	}

	public void removeChatter(String chatterIdx)
	{

		ChatProcManager proc = DBProcManager.sharedManager(mContext).chat();

		String chatIdx = Chat.makeChatIdx(mContext);

		proc.saveChatOnReceived(mRoom.getCode(), chatIdx, chatterIdx, "", Chat.CONTENT_TYPE_USER_LEAVE, System.currentTimeMillis() / 1000);
		proc.removeUserFromRoom(chatterIdx, mRoom.getCode());

		adjustTitle();

	}

	/**
	 * 서버에서 채팅방에 속한 사람들의 lastreadts를 한 번에 가져옴. 초기화 용도
	 */
	public void pullLastReadTS()
	{
		if (mRoom.getStatus() != Room.STATUS_CREATED)
		{
			Log.e(TAG, "VIRTUAL 상태의 room이 pullLastReadTS()를 호출함");
			return;
		}

		Data reqData = new Data();
		reqData.add(0, KEY.CHAT.ROOM_CODE, mRoom.getCode());
		reqData.add(0, KEY.USER.IDX, UserInfo.getUserIdx(mContext));

		Payload request = new Payload().setEvent(Event.Message.Chat.pullLastReadTS()).setData(reqData);
		Payload response = new Connection().async(false).requestPayload(request).request().getResponsePayload();

		if (response != null && response.getData() != null)
		{
			Data respData = response.getData();

			for (int i = 0; i < respData.size(); i++)
			{
				String receiverIdx = (String) respData.get(i).get(KEY.USER.IDX);
				Long TS = Long.parseLong(respData.get(i).get(KEY.CHAT.LAST_READ_TS).toString());

				updateLastReadTS(receiverIdx, TS);
			}
		}
	}

	/**
	 * 내가 채팅 목록을 읽었을 때 다른 사람들에게 그 정보를 push
	 */
	public void notifyLastReadTS(Long TS)
	{
		if (mRoom.getStatus() != Room.STATUS_CREATED)
		{
			Log.e(TAG, "VIRTUAL 상태의 room이 notifyLastReadTS()를 호출함");
			return;
		}

		DBProcManager.sharedManager(mContext).chat().updateLastEnteredTS(mRoom.getCode(), System.currentTimeMillis() / 1000);

		Data reqData = new Data();
		reqData.add(0, KEY.CHAT.ROOM_CODE, mRoom.getCode());
		reqData.add(0, KEY.USER.IDX, UserInfo.getUserIdx(mContext));
		reqData.add(0, KEY.CHAT.LAST_READ_TS, TS);

		Payload request = new Payload().setEvent(Event.MESSAGE_CHAT_NOTIFY_LAST_READ_TS).setData(reqData);
		Payload response = new Connection().async(false).requestPayload(request).request().getResponsePayload();

		if (response.getStatusCode() != StatusCode.SUCCESS)
		{
			Log.e(TAG, "notify 실패");
		}
	}

	/**
	 * 다른 사람이 채팅 목록을 읽었다는 정보를 push 받았을 때 업데이트 함.
	 * 
	 * @param userIdx
	 *            사용자 해쉬
	 * @param TS
	 *            읽은 시간
	 */
	public void updateLastReadTS(String userIdx, Long TS)
	{
		mRoom.setLastReadTS(userIdx, TS);
	}

	public void changeAlias(String alias)
	{
		mRoom.setAlias(alias);
		DBProcManager.sharedManager(mContext).chat().setRoomAlias(mRoom.getCode(), alias);
	}

	private void fetchBaseInfo()
	{
		Cursor c = DBProcManager.sharedManager(mContext).chat().getRoomInfo(mRoom.getCode());

		if (c.moveToNext() == true)
		{
			mRoom.setTitle(c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_TITLE)));
			mRoom.setAlias(c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_ALIAS)));
			mRoom.setType(c.getInt(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_TYPE)));
		}

		c.close();
	}

	private void fetchChatters()
	{
		Cursor c = DBProcManager.sharedManager(mContext).chat().getRoomChatters(mRoom.getCode());

		while (c.moveToNext())
		{

			String idx = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_USER_IDX));
			User user = MemberManager.sharedManager().getUser(idx);
			Chatter chatter = new Chatter(user);
			mRoom.chatters.add(chatter);

		}
	}

	private void adjustTitle()
	{
		if (mRoom.getStatus() != Room.STATUS_CREATED)
		{
			Log.e(TAG, "생성되지 않은 방은 title 수정 불가");
			return;
		}

		int n = mRoom.chatters.size();
		ArrayList<String> titles = new ArrayList<String>(n);

		for (int i = 0; i < n; i++)
		{
			Chatter chatter = mRoom.chatters.get(i);
			titles.add(Constants.POLICE_RANK[chatter.rank] + " " + chatter.name);
		}

		String title = Formatter.join(titles, ",");
		DBProcManager.sharedManager(mContext).chat().setRoomTitle(mRoom.getCode(), title);
		mRoom.setTitle(title);
	}

	private String makeRoomCode()
	{
		String str = UserInfo.getUserIdx(mContext) + ":" + System.currentTimeMillis();
		return Encrypter.sharedEncrypter().md5(str);
	}
}
