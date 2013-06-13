package kr.go.KNPA.Romeo.DB;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.DB.DBManager.DBSchema;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.database.Cursor;

public class ChatDAO extends DAO {

	public ChatDAO(Context context)
	{
		super(context);
	}
	
	/**
	 * 새 채팅방 생성
	 * 
	 * @param chatters
	 *            방에 참여하고 있는 사람들의 유저해쉬
	 * @param chatType
	 *            채팅방 타입. @see {Chat.TYPE_MEETING}, @see {Chat.TYPE_COMMAND}
	 * @param roomHash
	 *            룸해쉬
	 * @param isHost
	 *            내가 방을 만든 사람인지 아닌지(지시와보고때문에필요)
	 * @return 채팅방 해쉬
	 */
	public void createRoom(int chatType, String roomHash, boolean isHost)
	{

		int amIHost = isHost == true ? 1 : 0;
		// 새 방에 대한 레코드 생성
		String sql = "insert into " + DBSchema.ROOM.TABLE_NAME + "(" + DBSchema.ROOM.COLUMN_TYPE + "," + DBSchema.ROOM.COLUMN_IS_FAVORITE + "," + DBSchema.ROOM.COLUMN_IDX + ","
				+ DBSchema.ROOM.COLUMN_IS_ALARM_ON + "," + DBSchema.ROOM.COLUMN_AM_I_HOST + ") values (" + String.valueOf(chatType) + ",0,?,1," + String.valueOf(amIHost) + ")";
		String[] value = { roomHash };
		db.execSQL(sql, value);

	}

	/**
	 * 채팅방 정보를 담고 있는 커서를 반환
	 * 
	 * @b 커서구조\n
	 * @b COLUMN_ROOM_TYPE 채팅방 타입\n
	 * @b COLUMN_ROOM_ALIAS 채팅방 별칭\n
	 * @b COLUMN_ROOM_TITLE 채팅방 제목\n
	 * @b COLUMN_ROOM_NUM_CHATTER 채팅방에 있는 사람 수\n
	 * @b COLUMN_LAST_ENTERED_TS 자신이 마지막으로 들어간 시간
	 * @b COLUMN_IS_ALARM_ON 알람 여부
	 * @b COLUMN_IS_HOST 내가 호스트인지여부
	 * @param roomCode
	 *            룸 코드
	 * @return cursor
	 */
	public Cursor getRoomInfo(String roomCode)
	{
		String sql = "select r._id ," + " r." + DBSchema.ROOM.COLUMN_TYPE + COLUMN_ROOM_TYPE + "," +

		" r." + DBSchema.ROOM.COLUMN_ALIAS + COLUMN_ROOM_ALIAS + "," + " r." + DBSchema.ROOM.COLUMN_AM_I_HOST + COLUMN_IS_HOST + "," +

		" r." + DBSchema.ROOM.COLUMN_TITLE + COLUMN_ROOM_TITLE + "," + " r." + DBSchema.ROOM.COLUMN_IS_ALARM_ON + COLUMN_ROOM_IS_ALARM_ON + "," + " r." + DBSchema.ROOM.COLUMN_LAST_ENTERED_TS
				+ COLUMN_LAST_ENTERED_TS + "," + " (select count(rc._id) " + "from " + DBSchema.ROOM_CHATTER.TABLE_NAME + " rc " + "where rc." + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID
				+ "= r._id ) " + COLUMN_ROOM_NUM_CHATTER +

				" from " + DBSchema.ROOM.TABLE_NAME + " r " + " where " + DBSchema.ROOM.COLUMN_IDX + " = ?";

		String[] val = { roomCode };

		return db.rawQuery(sql, val);
	}

	/**
	 * 채팅방에 있는 사람들의 목록을 리턴
	 * 
	 * @b 커서구조
	 * @b COLUMN_USER_IDX user idx
	 * @b COLUMN_ENTERED_TS 입장시간
	 * @param roomCode
	 *            채팅방 idx
	 * @return
	 */
	public Cursor getRoomChatters(String roomCode)
	{

		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);

		String sql = "select _id, " + DBSchema.ROOM_CHATTER.COLUMN_USER_IDX + COLUMN_USER_IDX + ", " + DBSchema.ROOM_CHATTER.COLUMN_ENTERED_TS + ChatDAO.COLUMN_ENTERED_TS
				+ " from " + DBSchema.ROOM_CHATTER.TABLE_NAME + " where " + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID + "=" + String.valueOf(roomId);

		sql += " and " + DBSchema.ROOM_CHATTER.COLUMN_USER_IDX + " != ?";
		String[] val = { UserInfo.getUserIdx(context) };
		return db.rawQuery(sql, val);
	}

	public int getNumTotalChat(String roomCode)
	{
		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);

		if (roomId == Constants.NOT_SPECIFIED)
		{
			return Constants.NOT_SPECIFIED;
		}

		String sql = "SELECT COUNT(_id) n FROM " + DBSchema.CHAT.TABLE_NAME + " WHERE " + DBSchema.CHAT.COLUMN_ROOM_ID + " = " + String.valueOf(roomId);
		Cursor c = db.rawQuery(sql, null);

		if (c.moveToNext())
		{
			return c.getInt(0);
		}
		else
		{
			return Constants.NOT_SPECIFIED;
		}
	}

	/**
	 * receiverIdx와 1대 1로 채팅하고 있는 방의 룸코드를 리턴. 없으면 null
	 * 
	 * @param chatterIdx
	 *            상대방 idx
	 * @return 룸코드 or null
	 */
	public String getPairRoomCode(int roomType, String chatterIdx)
	{

		// 채팅방에 자기자신과 receiverHash에 해당하는 유저만 있는 경우를 선택해서 roomId를 가져온다.
		String sql = "select " + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID + " roomId, " + DBSchema.ROOM_CHATTER.COLUMN_USER_IDX + " receiver " + " from " + DBSchema.ROOM_CHATTER.TABLE_NAME
				+ " group by " + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID + " having count( _id ) = 1 and " + " receiver = ? ";
		String[] val = { chatterIdx };
		Cursor c = db.rawQuery(sql, val);

		// 만약 있으면 그 roomId로 ROOM 테이블에 쿼리를 날려서 인자로 받은 roomType의 방이 있는지 검사한다.
		if (c.moveToNext())
		{

			long roomId = c.getInt(0);
			c.close();
			sql = "select " + DBSchema.ROOM.COLUMN_IDX + " roomCode from " + DBSchema.ROOM.TABLE_NAME + " where _id = " + String.valueOf(roomId) + " and " + DBSchema.ROOM.COLUMN_TYPE + " = "
					+ String.valueOf(roomType);
			Cursor cursor = db.rawQuery(sql, null);

			// 만약 있다면 roomCode를 리턴하고 없으면 return null
			if (cursor.moveToNext())
			{
				String roomCode = cursor.getString(0);
				cursor.close();
				return roomCode;
			}
			else
			{
				return null;
			}

			// 그런 방이 없으면 걍 리턴 null
		}
		else
		{
			c.close();
			return null;
		}
	}

	/**
	 * 채팅방의 Title을 바꾼다.\n Alias의 우선순위가 더 높음. Alias == null or Alias == "" 이면
	 * Title을 출력하고 아니면 Alias 출력\n 채팅방의 타이틀은 [계급] [사람이름] 형식이며 여러 명일 경우 ,로
	 * 분리한다.
	 * 
	 * @param roomHash
	 *            룸코드
	 * @param title
	 *            타이틀
	 */
	public void setRoomTitle(String roomCode, String title)
	{
		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);

		if (roomId == Constants.NOT_SPECIFIED)
		{
			return;
		}

		String sql = "update " + DBSchema.ROOM.TABLE_NAME + " set " + DBSchema.ROOM.COLUMN_TITLE + " = ? where _id = " + String.valueOf(roomId);
		String[] val = { title };
		db.execSQL(sql, val);
	}

	/**
	 * 채팅방의 Alias를 바꾼다.\n Alias는 사용자가 직접 지정한 채팅방의 이름이며 이 값이 설정되어 있을 경우 사용자에게
	 * 출력하는 채팅방의 이름은\n Title이 아니라 Alias가 된다
	 * 
	 * @param roomCode
	 *            룸코드
	 * @param alias
	 *            별칭
	 */
	public void setRoomAlias(String roomCode, String alias)
	{
		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);

		if (roomId == Constants.NOT_SPECIFIED)
		{
			return;
		}

		String sql = "update " + DBSchema.ROOM.TABLE_NAME + " set " + DBSchema.ROOM.COLUMN_ALIAS + " = ? where _id = " + String.valueOf(roomId);
		String[] val = { alias };
		db.execSQL(sql, val);
	}

	public void setRoomAlarm(String roomCode, boolean isAlarmOn)
	{

		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);

		if (roomId == Constants.NOT_SPECIFIED)
		{
			return;
		}

		int is = isAlarmOn == true ? 1 : 0;

		String sql = "update " + DBSchema.ROOM.TABLE_NAME + " set " + DBSchema.ROOM.COLUMN_IS_ALARM_ON + " = " + String.valueOf(is) + " where _id = " + String.valueOf(roomId);

		db.execSQL(sql);

	}

	/**
	 * 채팅방 나가기
	 * 
	 * @param chatHash
	 */
	public void deleteRoom(String roomHash)
	{

		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomHash);

		String sql = "delete from " + DBSchema.CHAT.TABLE_NAME + " where " + DBSchema.CHAT.COLUMN_ROOM_ID + " = " + String.valueOf(roomId);
		db.execSQL(sql);

		sql = "delete from " + DBSchema.ROOM_CHATTER.TABLE_NAME + " where " + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID + " = " + String.valueOf(roomId);
		db.execSQL(sql);

		sql = "delete from " + DBSchema.ROOM.TABLE_NAME + " where _id = " + String.valueOf(roomId);
		db.execSQL(sql);

	}

	/**
	 * 방이 존재하는지
	 * 
	 * @param roomHash
	 * @return
	 */
	public boolean isRoomExists(String roomHash)
	{

		String sql = "select count(_id)>0 is_exists from " + DBSchema.ROOM.TABLE_NAME + " where " + DBSchema.ROOM.COLUMN_IDX + " = ?";
		String[] val = { roomHash };
		Cursor cursor = db.rawQuery(sql, val);
		cursor.moveToNext();

		return cursor.getInt(0) == 1;
	}

	/**
	 * users에 담겨 있는 유저들의 idx를 roomCode에 해당하는 방에 추가
	 * 
	 * @param usersIdx
	 *            추가할 유저들의 idx
	 * @param roomCode
	 *            방코드
	 */
	public void addUsersToRoom(ArrayList<String> usersIdx, String roomCode)
	{

		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);

		if (roomId == Constants.NOT_SPECIFIED)
		{
			return;
		}

		String sql = "insert into " + DBSchema.ROOM_CHATTER.TABLE_NAME + " (" + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID + ", " + DBSchema.ROOM_CHATTER.COLUMN_USER_IDX + ","
				+ DBSchema.ROOM_CHATTER.COLUMN_ENTERED_TS + ") " + "values( " + String.valueOf(roomId) + ", ?, " + String.valueOf(System.currentTimeMillis() / 1000) + " )";

		db.beginTransaction();

		try
		{

			for (int i = 0; i < usersIdx.size(); i++)
			{
				String[] val = { usersIdx.get(i) };
				db.execSQL(sql, val);
			}

			db.setTransactionSuccessful();

		}
		finally
		{
			db.endTransaction();
		}
	}

	/**
	 * userIdx를 roomCode의 참여자 목록에서 삭제
	 * 
	 * @param userIdx
	 *            삭제할 user idx
	 * @param roomCode
	 *            방코드
	 */
	public void removeUserFromRoom(String userIdx, String roomCode)
	{
		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);
		if (roomId == Constants.NOT_SPECIFIED)
		{
			return;
		}

		String sql = "delete from" + DBSchema.ROOM_CHATTER.TABLE_NAME + " where " + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID + " = " + String.valueOf(roomId) + " and "
				+ DBSchema.ROOM_CHATTER.COLUMN_USER_IDX + " = ?";

		String[] val = { userIdx };
		db.execSQL(sql, val);
	}

	/**
	 * 채팅 보낼때 채팅 내용 저장 후 chatIdx 생성해서 리턴
	 * 
	 * @param chatIdx
	 *            채팅 메세지의 idx
	 * @param roomCode
	 *            룸코드
	 * @param senderIdx
	 *            보내는 사람 idx
	 * @param content
	 *            본문
	 * @param contentType
	 *            콘텐츠타입
	 * @param createdTS
	 *            채팅을 보낸 타임스탬프
	 * @param chatState
	 *            채팅메세지의 전송상태
	 */
	public void saveChatOnSend(String chatIdx, String roomCode, String senderIdx, String content, int contentType, long createdTS, int chatState)
	{

		// 저장
		saveChat(roomCode, chatIdx, senderIdx, content, contentType, createdTS, chatState);

	}

	/**
	 * 채팅 받을때 메세지 내용 저장
	 * 
	 * @param roomCode
	 *            채팅방 해쉬
	 * @param chatHash
	 *            서버에서 부여한 채팅의 hash
	 * @param senderIdx
	 *            보내는 사람
	 * @param content
	 *            채팅 내용
	 * @param contentType
	 *            채팅메세지의 콘텐츠타입 (채팅이면 1 사진이면 2) @see
	 *            {DBDAO.CHAT_CONTENT_TYPE_TEXT},
	 * @see{DBDAO.CHAT_CONTENT_TYPE_PICTURE
	 * @param createdTS
	 *            채팅을 보낸 타임스탬프
	 */
	public void saveChatOnReceived(String roomCode, String chatHash, String senderIdx, String content, int contentType, long createdTS)
	{
		saveChat(roomCode, chatHash, senderIdx, content, contentType, createdTS, Chat.STATE_SUCCESS);
	}

	/**
	 * insert chat into rs_chat
	 * 
	 * @param roomCode
	 * @param chatIdx
	 * @param senderIdx
	 * @param content
	 * @param contentType
	 * @param createdTS
	 * @param chatState
	 */
	private void saveChat(String roomCode, String chatIdx, String senderIdx, String content, int contentType, long createdTS, int chatState)
	{
		// room hash가 유효한 방인지 검사
		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);
		if (roomId == Constants.NOT_SPECIFIED)
		{
			return;
		}

		String sql = "insert into " + DBSchema.CHAT.TABLE_NAME + "(" + DBSchema.CHAT.COLUMN_ROOM_ID + ", " + DBSchema.CHAT.COLUMN_IDX + ", " + DBSchema.CHAT.COLUMN_SENDER_IDX + ", "
				+ DBSchema.CHAT.COLUMN_CONTENT + ", " + DBSchema.CHAT.COLUMN_CONTENT_TYPE + ", " + DBSchema.CHAT.COLUMN_STATE + ", " + DBSchema.CHAT.COLUMN_CREATED_TS + ", "
				+ DBSchema.CHAT.COLUMN_IS_CHECKED + ") " + "values(" + String.valueOf(roomId) + "," + "?," + "?," + "?," + String.valueOf(contentType) + "," + String.valueOf(chatState) + ","
				+ String.valueOf(createdTS) + ", 0)";
		String[] val = { chatIdx, senderIdx, content };
		db.execSQL(sql, val);

		long chatId = lastInsertId();

		sql = "update " + DBSchema.ROOM.TABLE_NAME + " set last_chat_id = " + String.valueOf(chatId) + " where _id = " + String.valueOf(roomId);
		db.execSQL(sql);
	}

	public void updateLastEnteredTS(String roomCode, Long TS)
	{
		String sql = "UPDATE " + DBSchema.ROOM.TABLE_NAME + " SET " + DBSchema.ROOM.COLUMN_LAST_ENTERED_TS + " = " + String.valueOf(TS) + " WHERE " + DBSchema.ROOM.COLUMN_IDX + " = ?";
		String[] val = { roomCode };
		db.execSQL(sql, val);
	}

	/**
	 * 채팅의 상태를 변경
	 * 
	 * @param chatHash
	 *            채팅 해쉬
	 * @param chatState
	 *            채팅메세지의 전송상태. @see{Chat.STATE_SUCCESS},
	 * @see{Chat.STATE_FAIL , @see{Chat.STATE_SENDING}
	 */
	public void updateChatState(String chatHash, int chatState)
	{

		String sql = "update " + DBSchema.CHAT.TABLE_NAME + " " + "set " + DBSchema.CHAT.COLUMN_STATE + " = " + String.valueOf(chatState) + " where " + DBSchema.CHAT.COLUMN_IDX + " = ?";
		String[] val = { chatHash };
		db.execSQL(sql, val);
	}

	/**
	 * 채팅방 즐겨찾기 설정/해제
	 * 
	 * @param roomCode
	 *            룸코드
	 * @param isFavorite
	 *            즐겨찾기 여부
	 */
	public void setRoomFavorite(String roomCode, boolean isFavorite)
	{
		String sql = "UPDATE " + DBSchema.ROOM.TABLE_NAME + " SET " + DBSchema.ROOM.COLUMN_IS_FAVORITE + " = " + String.valueOf(isFavorite ? 1 : 0) + " WHERE " + DBSchema.ROOM.COLUMN_IDX + " = ?";
		String[] val = { roomCode };
		db.execSQL(sql, val);
	}

	/**
	 * 채팅방 목록에 대한 정보를 담고 있는 커서를 반환
	 * 
	 * @b 커서구조\n
	 * @b COLUMN_ROOM_IDX 채팅방 해시\n
	 * @b COLUMN_ROOM_TITLE 채팅방 제목\n
	 * @b COLUMN_ROOM_ALIAS 채팅방 별칭\n
	 * @b COLUMN_ROOM_NUM_CHATTER 채팅방에 있는 사람 수\n
	 * @b COLUMN_ROOM_NUM_NEW_CHAT 읽지 않은 채팅 수\n
	 * @b COLUMN_ROOM_LAST_CHAT_TS 마지막 채팅이 도착한 시간 TS\n
	 * @b COLUMN_ROOM_LAST_CHAT_CONTENT 마지막 채팅의 내용\n
	 * @b COLUMN_USER_IDX 1:1채팅의 경우 같이 채팅하고 있는 사람의 idx, 그룹 채팅의 경우 무시
	 * @b COLUMN_IS_ALARM_ON 알람여부
	 * @b COLUMN_IS_HOST 호스트여부
	 * @param roomType
	 *            채팅방의 종류(chat의 subtype)
	 * @return cursor
	 */
	public Cursor getRoomList(int roomType)
	{
		String sql = "select r._id ," + " r." + DBSchema.ROOM.COLUMN_IDX + COLUMN_ROOM_IDX + "," +

		" r." + DBSchema.ROOM.COLUMN_TITLE + COLUMN_ROOM_TITLE + "," +

		" r." + DBSchema.ROOM.COLUMN_ALIAS + COLUMN_ROOM_ALIAS + "," +

		" r." + DBSchema.ROOM.COLUMN_IS_ALARM_ON + COLUMN_ROOM_IS_ALARM_ON + "," + " r." + DBSchema.ROOM.COLUMN_AM_I_HOST + COLUMN_IS_HOST + "," +

		" (select count(rc._id) " + "from " + DBSchema.ROOM_CHATTER.TABLE_NAME + " rc " + "where rc." + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID + "= r._id ) " + COLUMN_ROOM_NUM_CHATTER + ", " +

		" (select count(c._id) from " + DBSchema.CHAT.TABLE_NAME + " c " + 
			"where c." + DBSchema.CHAT.COLUMN_ROOM_ID + " = r._id " +
			" and r." + DBSchema.ROOM.COLUMN_LAST_ENTERED_TS + " < c." + DBSchema.CHAT.COLUMN_CREATED_TS + 
			" and c." + DBSchema.CHAT.COLUMN_SENDER_IDX + " != ? " +
			" and c." + DBSchema.CHAT.COLUMN_CONTENT_TYPE + " IN (" + Chat.CONTENT_TYPE_TEXT+ "," + Chat.CONTENT_TYPE_PICTURE + ") ) " + COLUMN_ROOM_NUM_NEW_CHAT + ", " +

				" lc." + DBSchema.CHAT.COLUMN_CREATED_TS + COLUMN_ROOM_LAST_CHAT_TS + ", " +

				" ( select chatter." + DBSchema.ROOM_CHATTER.COLUMN_USER_IDX + " from " + DBSchema.ROOM_CHATTER.TABLE_NAME + " chatter where chatter." + DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID
				+ " = r._id limit 1) " + COLUMN_USER_IDX + ", " +

				" (CASE lc." + DBSchema.CHAT.COLUMN_CONTENT_TYPE + " " + "WHEN " + Chat.CONTENT_TYPE_TEXT + " " + "THEN lc." + DBSchema.CHAT.COLUMN_CONTENT + " " + "WHEN "
				+ Chat.CONTENT_TYPE_PICTURE + " " + "THEN \"(사진)\" " + "ELSE \"\" END) " + COLUMN_ROOM_LAST_CHAT_CONTENT +

				" from " + DBSchema.ROOM.TABLE_NAME + " r " + " left join " + DBSchema.CHAT.TABLE_NAME + " lc " + " on lc._id = r." + DBSchema.ROOM.COLUMN_LAST_CHAT_ID +

				" where " + DBSchema.ROOM.COLUMN_TYPE + " = " + String.valueOf(roomType) +
				" order by r."+DBSchema.ROOM.COLUMN_AM_I_HOST+" desc, lc." + DBSchema.CHAT.COLUMN_CREATED_TS + " desc ";
		String[] val = { UserInfo.getUserIdx(context) };
		Cursor c = db.rawQuery(sql, val);
		return c;
	}

	/**
	 * 채팅방 내의 채팅 목록 불러오기
	 * 
	 * @b 커서구조
	 * @b COLUMN_CHAT_IDX 채팅해쉬\n
	 * @b COLUMN_CHAT_SENDER_IDX 센더해쉬\n
	 * @b COLUMN_CHAT_TS 채팅TS\n
	 * @b COLUMN_CHAT_CONTENT 내용 \n
	 * @b COLUMN_CHAT_CONTENT_TYPE 내용의 종류\n
	 * @b COLUMN_CHAT_STATE 채팅 상태
	 * @param roomCode
	 * @param TS
	 *            역순으로 정렬시 불러올 목록 시작 index
	 * @param 불러올
	 *            채팅의 개수
	 * @return
	 */
	public Cursor getChatList(String roomCode, int start, int count)
	{

		long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomCode);
		String sql = "select * from (select _id, " + DBSchema.CHAT.COLUMN_IDX + COLUMN_CHAT_IDX + ", " + DBSchema.CHAT.COLUMN_SENDER_IDX + COLUMN_CHAT_SENDER_IDX + ", "
				+ DBSchema.CHAT.COLUMN_CREATED_TS + COLUMN_CHAT_TS + ", " + DBSchema.CHAT.COLUMN_CONTENT + COLUMN_CHAT_CONTENT + ", " + DBSchema.CHAT.COLUMN_STATE + COLUMN_CHAT_STATE + ", "
				+ DBSchema.CHAT.COLUMN_CONTENT_TYPE + COLUMN_CHAT_CONTENT_TYPE + " from " + DBSchema.CHAT.TABLE_NAME + " where " + DBSchema.CHAT.COLUMN_ROOM_ID + " = " + String.valueOf(roomId)
				+ " order by " + DBSchema.CHAT.COLUMN_CREATED_TS + " desc " + " limit " + String.valueOf(start) + ", " + String.valueOf(count) + ") tmp order by case "+COLUMN_CHAT_STATE+" when "+Chat.STATE_FAIL+" then 1 else 0 end asc," + COLUMN_CHAT_TS + " asc ";
		return db.rawQuery(sql, null);
	}

	public Cursor getChatInfo(String chatIdx){
		String sql = "select "+
				DBSchema.CHAT.COLUMN_SENDER_IDX+COLUMN_CHAT_SENDER_IDX+", "+
				DBSchema.CHAT.COLUMN_CONTENT+COLUMN_CHAT_CONTENT+", "+
				DBSchema.CHAT.COLUMN_CREATED_TS+COLUMN_CHAT_TS+", "+
				DBSchema.CHAT.COLUMN_CONTENT_TYPE+COLUMN_CHAT_CONTENT_TYPE+", "+
				DBSchema.CHAT.COLUMN_STATE+COLUMN_CHAT_STATE+
				" FROM "+DBSchema.CHAT.TABLE_NAME+" WHERE "+DBSchema.CHAT.COLUMN_IDX+" = ?";
		String[] val = {chatIdx};
		return db.rawQuery(sql, val);
	}
	
	/**
	 * 개별채팅삭제
	 * 
	 * @param chatHash
	 */
	public void deleteChat(String chatHash)
	{
		if (chatHash == null) return;
		
		String sql = "select "+DBSchema.CHAT.COLUMN_ROOM_ID+" from "+DBSchema.CHAT.TABLE_NAME+" where "+DBSchema.CHAT.COLUMN_IDX+" = ?";
		String[] val = { chatHash };
		Cursor c = db.rawQuery(sql, val);
		if (!c.moveToNext())
		{
			return;
		}
		
		int roomId = c.getInt(0);
		c.close();
		sql = "delete from " + DBSchema.CHAT.TABLE_NAME + " where " + DBSchema.CHAT.COLUMN_IDX + " = ?";
		db.execSQL(sql, val);
		
		sql = "select _id from "+DBSchema.CHAT.TABLE_NAME+" where "+DBSchema.CHAT.COLUMN_ROOM_ID+" = "+String.valueOf(roomId)+" order by "+DBSchema.CHAT.COLUMN_CREATED_TS+" desc limit 1";
		Cursor cc = db.rawQuery(sql, null);
		if (cc.moveToNext())
		{
			int lastChatId = cc.getInt(0);
			sql = "update "+DBSchema.ROOM.TABLE_NAME+" set "+DBSchema.ROOM.COLUMN_LAST_CHAT_ID+" = "+String.valueOf(lastChatId)+" where _id = "+String.valueOf(roomId);
			db.execSQL(sql);
		}
		
	}

	public Integer getNumUnchecked(Integer type)
	{
		String sql = "SELECT count(c._id) n FROM "+DBSchema.CHAT.TABLE_NAME+" c "+
					" LEFT JOIN "+DBSchema.ROOM.TABLE_NAME+" r ON c."+DBSchema.CHAT.COLUMN_ROOM_ID+" = r._id "+
					" WHERE c."+DBSchema.ROOM.COLUMN_TYPE+" = "+type.toString()+
					" AND c."+DBSchema.CHAT.COLUMN_SENDER_IDX+" != ?"+
					" AND c."+DBSchema.CHAT.COLUMN_CONTENT_TYPE+" IN ("+Chat.CONTENT_TYPE_TEXT+","+Chat.CONTENT_TYPE_PICTURE+") "+
					" AND c."+DBSchema.CHAT.COLUMN_CREATED_TS+" > r."+DBSchema.ROOM.COLUMN_LAST_ENTERED_TS;
		Cursor c = db.rawQuery(sql, new String[]{UserInfo.getUserIdx(context)});
		if (c.moveToNext())
		{
			Integer n = c.getInt(0);
			c.close();
			return n;
		}
		else
		{
			c.close();
			return 0;
		}
	}
	
	public static final String	COLUMN_ROOM_IDX					= "room_idx";
	public static final String	COLUMN_ROOM_TYPE				= "room_type";
	public static final String	COLUMN_ROOM_TITLE				= "room_title";
	public static final String	COLUMN_ROOM_ALIAS				= "room_alias";
	public static final String	COLUMN_ROOM_NUM_CHATTER			= "num_chatter";
	public static final String	COLUMN_ROOM_NUM_NEW_CHAT		= "num_new_chat";
	public static final String	COLUMN_ROOM_LAST_CHAT_TS		= "last_chat_ts";
	public static final String	COLUMN_ROOM_LAST_CHAT_CONTENT	= "last_chat_content";
	public static final String	COLUMN_ROOM_IS_ALARM_ON			= "is_alarm_on";
	public static final String	COLUMN_IS_HOST					= "is_host";

	public static final String	COLUMN_ENTERED_TS				= "entered_ts";
	public static final String	COLUMN_USER_IDX					= "user_idx";
	public static final String	COLUMN_CHAT_SENDER_IDX			= "sender_idx";
	public static final String	COLUMN_CHAT_IDX					= "chat_idx";
	public static final String	COLUMN_CHAT_TS					= "created_ts";
	public static final String	COLUMN_CHAT_CONTENT				= "chat_content";
	public static final String	COLUMN_CHAT_CONTENT_TYPE		= "chat_content_type";
	public static final String	COLUMN_CHAT_STATE				= "chat_state";
	public static final String	COLUMN_LAST_ENTERED_TS			= "last_entered_ts";

}