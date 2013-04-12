package kr.go.KNPA.Romeo.DB;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBManager.DBSchema;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Survey.Survey;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DB 프로시져 모음
 */
public class DBProcManager {
	private static final String TAG = DBProcManager.class.getName();
	
	/**
	 * @name Singleton
	 * @{
	 */
	private static DBProcManager _sharedManager = null;
	private ChatProcManager chat = null;
	private DocumentProcManager document = null;
	private SurveyProcManager survey = null;
	private MemberProcManager member = null;
	
	private DBManager dbm = null;
	private SQLiteDatabase db;
	private Context context;
	
	private DBProcManager(Context context) {
		if ( this.dbm == null ) {
			this.dbm = new DBManager(context);  
		}
		if ( this.db == null ) {
			this.db = dbm.getWritableDatabase();
		}
		this.context = context;
	}
	
	public static DBProcManager sharedManager(Context context) {
		if(_sharedManager == null)
			_sharedManager = new DBProcManager(context);
		return _sharedManager;
	}
	
	public ChatProcManager chat() {
		if(chat == null){
			chat = new ChatProcManager();
		}
		return  chat; 
	}
	
	public DocumentProcManager document() {
		if(document == null){
			document = new DocumentProcManager();
		}
		return document; 
	}
	
	public SurveyProcManager survey() {
		if(survey == null){
			survey = new SurveyProcManager();
		}
		return survey; 
	}
	
	public MemberProcManager member() {
		if(member == null) {
			member = new MemberProcManager();
		}
		return member;
	}
	/** @} */
	
	/**
	 * 원하는 프로시져를 모두 호출한 후 DB를 닫기 위해 호출
	 */
	public void close(){
		this.dbm.close();
		this.db.close();
	}
	
	public void dropDatabase(){
		this.context.deleteDatabase(DBSchema.DATABASE_NAME);
	}
	
	private long hashToId(String tableName, String hashColName, String hash) {
		
		if ( hash == null ) {
			return Constants.NOT_SPECIFIED;
		}
		
		String[] args = { hash };
		Cursor c = db.rawQuery("select _id from "+tableName+" where "+hashColName+" = ?",args);
		if ( c.moveToNext() ) {
			return c.getLong(0);
		} else {
			Log.w(TAG,"입력한 해쉬에 대한 id값을 찾을 수 없음 at hashToId("+tableName+", "+hashColName+", "+hash+")");
			return Constants.NOT_SPECIFIED;
		}
		 
	}
	
	private long lastInsertId() {
		Cursor c = db.rawQuery("select last_insert_rowid()", null);
		if ( c.moveToFirst() != false ) {
			return c.getLong(0);
		}
		return Constants.NOT_SPECIFIED;
	}
	
	private String md5(String str){
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}
	
	public class ChatProcManager {
		
		/**
		 * 새 채팅방 생성
		 * @param userHashes 자기 자신을 포함한 방에 참여하고 있는 사람들의 유저해쉬
		 * @param chatType 채팅방 타입. @see {Chat.TYPE_MEETING}, @see {Chat.TYPE_COMMAND}
		 * @param roomHash 룸해쉬
		 * @return 채팅방 해쉬
		 */
		public void createRoom(ArrayList<String> userHashes, int chatType, String roomHash ) {

			if ( userHashes.size() == 0 ) {
				Log.w(TAG,"유저해쉬 어레이가 비어있음 at createRoom(ArrayList<String> userHashes, int chatType )");
				return;
			}
        	
			// 새 방 레코드 생성
			String sql =
					"insert into "+DBSchema.ROOM.TABLE_NAME+
					"("+DBSchema.ROOM.COLUMN_TYPE+","+
						DBSchema.ROOM.COLUMN_IS_FAVORITE+","+
						DBSchema.ROOM.COLUMN_IDX+
					") values ("+String.valueOf(chatType)+",0,?)";
			String[] value = {roomHash};
			db.execSQL(sql,value);
			long roomId = lastInsertId();

			//채팅방에 참여하고 있는 유저들의 해쉬를 room_chatter 테이블에 추가			
			db.beginTransaction();
			try {
				int i, n=userHashes.size();
				for( i=0; i<n; i++ ) {
					sql = 
						"insert into "+DBSchema.ROOM_CHATTER.TABLE_NAME+
						"("+DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+", "+DBSchema.ROOM_CHATTER.COLUMN_USER_IDX+")" +
						"values ( "+String.valueOf( roomId )+", ? )";
					String[] val = { userHashes.get(i) };
					db.execSQL(sql, val);
				}
				
	            db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
		
		/**
		 * 방이 존재하는지
		 * @param roomHash
		 * @return
		 */
		public boolean roomExists(String roomHash) {
			String sql = "select count(_id)>0 is_exists from "+DBSchema.ROOM.TABLE_NAME+
					" where "+DBSchema.ROOM.COLUMN_IDX+" = ?";
			String[] val = {roomHash};
			Cursor cursor = db.rawQuery(sql, val);
			cursor.moveToNext();
			return cursor.getInt(0)==1?true:false;
		}
		
		/**
		 * 유저 여러명이 대화에 참가
		 * @param users
		 * @param roomHash
		 */
		public void addUsersToRoom(ArrayList<String> users, String roomHash ) {
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomHash);
			if ( roomId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			String sql = 
					"insert into "+DBSchema.ROOM_CHATTER.TABLE_NAME+" ("+
							DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+", "+
							DBSchema.ROOM_CHATTER.COLUMN_USER_IDX+
					") values( "+String.valueOf(roomId)+" , ? )";
			db.beginTransaction();
			try {
				
				for ( int i=0; i<users.size(); i++ ) {
					String[] val = {users.get(i)};
					db.execSQL(sql,val);
				}
				
				db.setTransactionSuccessful();
			}finally{
				db.endTransaction();
			}
		}
		
		/**
		 * 유저 한명이 대화에 참가
		 */
		public void addUserToRoom(String user, String roomHash ) {
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomHash);
			if ( roomId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			String sql = 
					"insert into "+DBSchema.ROOM_CHATTER.TABLE_NAME+" ("+
							DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+", "+
							DBSchema.ROOM_CHATTER.COLUMN_USER_IDX+
					") values( "+String.valueOf(roomId)+" , ? )";

			String[] val = {user};
			db.execSQL(sql,val);
		}
		
		/**
		 * 유저 하나가 대화에서 나감
		 * @param user
		 * @param roomHash
		 */
		public void removeUserFromRoom(String user, String roomHash) {
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomHash);
			if ( roomId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			String sql = 
					"delete from"+DBSchema.ROOM_CHATTER.TABLE_NAME+
					" where "+
						DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+" = "+String.valueOf(roomId)+
						" and "+DBSchema.ROOM_CHATTER.COLUMN_USER_IDX+" = ?";

			String[] val = {user};
			db.execSQL(sql,val);
		}
		/**
		 * 채팅 보낼때 메세지 내용 저장 
		 * @param roomHash 채팅방 해쉬
		 * @param chatHash 서버에서 부여한 채팅의 hash
		 * @param senderHash 보내는 사람 
		 * @param content 채팅 내용
		 * @param contentType 채팅메세지의 콘텐츠타입 (채팅이면 1 사진이면 2) @see {DBProcManager.CHAT_CONTENT_TYPE_TEXT}, @see{DBProcManager.CHAT_CONTENT_TYPE_PICTURE}
		 * @param createdTS 채팅을 보낸 타임스탬프
		 */
		public void saveChatOnSend(String roomHash, String chatHash, String senderHash, String content, int contentType, long createdTS) {
			saveChat(roomHash, chatHash, senderHash, content, contentType, createdTS);
			ArrayList<String> ar = new ArrayList<String>();
			ar.add(chatHash);
			updateCheckedTS(ar, createdTS);
		}
		
		/**
		 * 채팅 받을때 메세지 내용 저장 
		 * @param roomHash 채팅방 해쉬
		 * @param chatHash 서버에서 부여한 채팅의 hash
		 * @param senderHash 보내는 사람 
		 * @param content 채팅 내용
		 * @param contentType 채팅메세지의 콘텐츠타입 (채팅이면 1 사진이면 2) @see {DBProcManager.CHAT_CONTENT_TYPE_TEXT}, @see{DBProcManager.CHAT_CONTENT_TYPE_PICTURE}
		 * @param createdTS 채팅을 보낸 타임스탬프
		 */
		public void saveChatOnReceived(String roomHash, String chatHash, String senderHash, String content, int contentType, long createdTS) {
			saveChat(roomHash, chatHash, senderHash, content, contentType, createdTS);
		}
		
		private void saveChat(String roomHash, String chatHash, String senderHash, String content, int contentType, long createdTS) {
			//room hash가 유효한 방인지 검사
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomHash);
			if ( roomId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			String sql = 
					"insert into "+DBSchema.CHAT.TABLE_NAME+
					"("+DBSchema.CHAT.COLUMN_ROOM_ID+", "+
						DBSchema.CHAT.COLUMN_IDX+", "+
						DBSchema.CHAT.COLUMN_SENDER_IDX+", "+
						DBSchema.CHAT.COLUMN_CONTENT+", "+
						DBSchema.CHAT.COLUMN_CONTENT_TYPE+", "+
						DBSchema.CHAT.COLUMN_CREATED_TS+") " +
					"values(" +
						String.valueOf(roomId)+","+
						"?,"+
						"?,"+
						"?,"+
						String.valueOf(contentType)+","+
						String.valueOf(createdTS)+")";
			String[] val = { chatHash, senderHash, content };
			db.rawQuery(sql, val);
		}
		
		/**
		 * 채팅 메세지를 확인했을 때
		 * @param chatHash 채팅 해쉬 어레이
		 * @param checkedTS 체크한 시간
		 */
		public void updateCheckedTS( ArrayList<String> chatHash, long checkedTS ) {
			
			if ( chatHash.size() == 0 ) {
				return;
			}
			
			String sql = "update "+DBSchema.CHAT.TABLE_NAME+" "+
					"set "+
					DBSchema.CHAT.COLUMN_IS_CHECKED + " = 1,"+
					DBSchema.CHAT.COLUMN_CHECKED_TS + " = " + String.valueOf(checkedTS)+
					" where " + DBSchema.CHAT.COLUMN_IDX + " IN (";
			for( int i=0; i<chatHash.size(); i++) {
				sql += "?,";
			}
			sql = sql.substring(0,sql.length()-1);
			sql += ")";
			db.execSQL(sql, chatHash.toArray());
		}

		/**
		 * 유저가 채팅방에 들어가 메세지들을 확인했을 때의 시간을 기록함 
		 * @param roomHash 채팅방 해쉬
		 * @param lastReadTS lastReadTS
		 */
		public void updateLastReadTS( String roomHash, long lastReadTS ) {
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomHash);
			
			if ( roomId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			String sql = "update "+DBSchema.ROOM.TABLE_NAME+
					"set "+
					DBSchema.ROOM.COLUMN_LAST_READ_TS+" = "+String.valueOf(lastReadTS)+
					" where _id = "+String.valueOf(roomId);
			db.execSQL(sql);	
		}
		
		/**
		 * 채팅방을 즐겨찾기에 추가
		 * @param hash
		 */
		public void addFavorite( String hash ) {
			
		}
		
		/**
		 * 채팅방 목록에 대한 정보를 담고 있는 커서를 반환
		 * @b 커서구조
		 * @b COLUMN_ROOM_IDX 채팅방 해시\n
		 * @b COLUMN_ROOM_TITLE 채팅방 제목\n
		 * @b COLUMN_ROOM_NUM_CHATTER 채팅방에 있는 사람 수\n
		 * @b COLUMN_ROOM_NUM_NEW_CHAT 읽지 않은 채팅 수\n
		 * @b COLUMN_ROOM_LAST_CHAT_TS 마지막 채팅이 도착한 시간 TS\n
		 * @b COLUMN_ROOM_LAST_CHAT_CONTENT 마지막 채팅의 내용\n
		 * @param roomType 
		 * @return cursor
		 */
		public Cursor getRoomList(int roomType) {
			String sql = 
					"select r._id ," +
					" r."+DBSchema.ROOM.COLUMN_IDX+
						COLUMN_ROOM_IDX+"," +
					
					" r."+DBSchema.ROOM.COLUMN_TITLE+
						COLUMN_ROOM_TITLE+"," +
					
					" (select count(rc._id) " +
						"from "+DBSchema.ROOM_CHATTER.TABLE_NAME+" rc " +
						"where rc."+DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+"= r._id )+1 "+
						COLUMN_ROOM_NUM_CHATTER+", " +
					
					" (select count(c._id) " +
						"from "+DBSchema.CHAT.TABLE_NAME+" c " +
						"where c."+DBSchema.CHAT.COLUMN_ROOM_ID+" = r._id and c."+DBSchema.CHAT.COLUMN_IS_CHECKED+"= 0 ) " +
						COLUMN_ROOM_NUM_NEW_CHAT+", " +
					
					" lc."+DBSchema.CHAT.COLUMN_CREATED_TS+
						COLUMN_ROOM_LAST_CHAT_TS+", " +
				
					" (CASE lc."+DBSchema.CHAT.COLUMN_CONTENT_TYPE+" " +
					"WHEN "+CHAT_CONTENT_TYPE_TEXT+" " +
					"THEN lc."+DBSchema.CHAT.COLUMN_CONTENT+" " +
					"WHEN "+CHAT_CONTENT_TYPE_PICTURE+" " +
					"THEN \"(사진)\" " +
					"ELSE \"\" END) "+
					COLUMN_ROOM_LAST_CHAT_CONTENT+
						
					" from "+DBSchema.ROOM.TABLE_NAME+" r " +
					" left join "+DBSchema.CHAT.TABLE_NAME+" lc " +
						"on lc._id = r."+DBSchema.ROOM.COLUMN_LAST_CHAT_ID+
					" where "+DBSchema.ROOM.COLUMN_TYPE+" = "+String.valueOf(roomType)+
					
					" order by lc."+DBSchema.CHAT.COLUMN_CREATED_TS+" desc ";
			Cursor c = db.rawQuery(sql, null);
			return c;
		}
		
		/**
		 * 채팅을 전송할 때 채팅방에 있는 사람들의 목록을 리턴
		 * @b 커서구조
		 * @b COLUMN_USER_IDX 리시버 해쉬
		 * @param hash 채팅방 해쉬
		 * @return
		 */
		public Cursor getReceiverList( String hash ) {
			
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, hash);
			
			String sql = 
					"select _id, "+DBSchema.ROOM_CHATTER.COLUMN_USER_IDX+COLUMN_USER_IDX+
					" from "+DBSchema.ROOM_CHATTER.TABLE_NAME+
					" where "+DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+"="+String.valueOf(roomId);
			return db.rawQuery(sql,null);
		}
		
		/**
		 * 채팅방 내의 채팅 목록 불러오기
		 * @b 커서구조
		 * @b COLUMN_CHAT_IDX 채팅해쉬\n
		 * @b COLUMN_CHAT_SENDER_IDX 센더해쉬\n
		 * @b COLUMN_CHAT_TS 채팅TS\n
		 * @b COLUMN_CHAT_CONTENT 내용 \n
		 * @b COLUMN_CHAT_CONTENT_TYPE 내용의 종류 @see{CHAT_CONTENT_TYPE_TEXT} @see{CHAT_CONTENT_TYPE_PICTURE}\n
		 * @param roomHash
		 * @param TS 역순으로 정렬시 불러올 목록 시작 index
		 * @param 불러올 채팅의 개수
		 * @return
		 */
		public Cursor getChatList(String roomHash, int start, int count) {
		
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_IDX, roomHash);
			String sql=
					"select _id, "+
					DBSchema.CHAT.COLUMN_IDX+COLUMN_CHAT_IDX+", "+
					DBSchema.CHAT.COLUMN_SENDER_IDX+COLUMN_CHAT_SENDER_IDX+", "+
					DBSchema.CHAT.COLUMN_CREATED_TS+COLUMN_CHAT_TS+", "+
					DBSchema.CHAT.COLUMN_CONTENT+COLUMN_CHAT_CONTENT+", "+
					DBSchema.CHAT.COLUMN_CONTENT_TYPE+COLUMN_CHAT_CONTENT_TYPE+
					" from "+DBSchema.CHAT.TABLE_NAME+
					" where "+DBSchema.CHAT.COLUMN_ROOM_ID+" = "+String.valueOf(roomId)+
					" order by "+DBSchema.CHAT.COLUMN_CREATED_TS+" desc "+
					" limit "+String.valueOf(start)+", "+String.valueOf(count);
			return db.rawQuery(sql, null);
		}
		
		public static final String COLUMN_ROOM_IDX = "room_idx";
		public static final String COLUMN_ROOM_TITLE = "room_title";
		public static final String COLUMN_ROOM_NUM_CHATTER = "num_chatter";
		public static final String COLUMN_ROOM_NUM_NEW_CHAT = "num_new_chat";
		public static final String COLUMN_ROOM_LAST_CHAT_TS = "last_chat_ts";
		public static final String COLUMN_ROOM_LAST_CHAT_CONTENT = "last_chat_content";
		public static final String COLUMN_USER_IDX = "user_idx";
		public static final String COLUMN_CHAT_SENDER_IDX = "sender_idx";
		public static final String COLUMN_CHAT_IDX = "chat_idx";
		public static final String COLUMN_CHAT_TS = "created_ts";
		public static final String COLUMN_CHAT_CONTENT = "chat_content";
		public static final String COLUMN_CHAT_CONTENT_TYPE = "chat_content_type";
		
		public static final int CHAT_CONTENT_TYPE_TEXT = 1;
		public static final int CHAT_CONTENT_TYPE_PICTURE = 2;
	}

	public class DocumentProcManager {
		private void saveAttachmentInfo(long docId, ArrayList<HashMap<String, Object>> files) {
						
			db.beginTransaction();
			try {
				//첨부파일 정보 insert
				
				for ( int i=0; i<files.size(); i++) {
					HashMap<String,Object> hm = files.get(i);
					
					String[] binds = { hm.get(KEY.DOCUMENT.FILE_IDX).toString(), hm.get(KEY.DOCUMENT.FILE_NAME).toString() } ;
					long fileSize = (Long)hm.get(KEY.DOCUMENT.FILE_SIZE);
					int fileType = (Integer) hm.get(KEY.DOCUMENT.FILE_TYPE) ;
					
					String sql = "insert into "+DBSchema.DOCUMENT_ATTACHMENT.TABLE_NAME+
							"("+
							DBSchema.DOCUMENT_ATTACHMENT.COLUMN_DOC_ID+","+
							DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_IDX+","+
							DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_NAME+","+
							DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_TYPE+","+
							DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_SIZE_IN_BYTE+
							") " +
							"values("+
							String.valueOf(docId)+", ?, ?, "+String.valueOf(fileType)+", "+String.valueOf(fileSize)+
							")";
					db.execSQL(sql,binds);
				}
				
				db.setTransactionSuccessful();
			} finally{
				db.endTransaction();
			}
		}
		
		/**
		 * 문서를 자신이 만들어서 보낼 때 문서 내용 저장
		 * @param docHash 서버가 부여한 문서 해쉬
		 * @param senderHash 보내는 사람 해쉬 (자기자신)
		 * @param title 문서 제목
		 * @param content 문서 내용
		 * @param createdTS 문서를 보낸 TS
		 * @param files 첨부파일정보. \n @see {Document.ATTACH_FILE_URL}, @see {Document.ATTACH_FILE_NAME}, @see {Document.ATTACH_FILE_TYPE}, @see {Document.ATTACH_FILE_SIZE} 가 key로 설정되어야함
		 */
		public void saveDocumentOnSend(String docHash, String senderHash, String title, String content, long createdTS, ArrayList<HashMap<String, Object>> files){
			//document 테이블에 insert
			String sql =
					"insert into "+DBSchema.DOCUMENT.TABLE_NAME+
					" ("+DBSchema.DOCUMENT.COLUMN_IDX+","+
					DBSchema.DOCUMENT.COLUMN_CREATOR_IDX+","+
					DBSchema.DOCUMENT.COLUMN_TITLE+","+
					DBSchema.DOCUMENT.COLUMN_CONTENT+","+
					DBSchema.DOCUMENT.COLUMN_CREATED_TS+","+
					DBSchema.DOCUMENT.COLUMN_IS_CHECKED+","+
					DBSchema.DOCUMENT.COLUMN_CHECKED_TS+","+
					DBSchema.DOCUMENT.COLUMN_CATEGORY+") " +
					"values(?, ?, ?, ?, "+String.valueOf(createdTS)+", 1, "+String.valueOf(createdTS)+
							", "+Document.TYPE_DEPARTED+")";
			String[] val = { docHash, senderHash, title, content };
			db.execSQL(sql,val);
			
			//doc rowid
			long docId = lastInsertId();
			saveAttachmentInfo(docId, files);
		}
		
		public void saveDocumentOnForward
						(String docHash, 
							String senderHash, 
							String title, 
							String content, 
							long createdTS, 
							ArrayList<HashMap<String, Object>> files,
							ArrayList<HashMap<String,Object>> forwards){
			saveDocumentOnSend(docHash, senderHash, title, content, createdTS, files);
			addForwardInfo(docHash, forwards);
		}
		
		private void addForwardInfo(String docHash, ArrayList<HashMap<String,Object>> forwards) {
			
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);
			if ( docId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			db.beginTransaction();
			try {

				
				for ( int i=0; i<forwards.size(); i++ ) {
					String sql = "insert into "+DBSchema.DOCUMENT_FORWARD.TABLE_NAME+
							" ("+
							DBSchema.DOCUMENT_FORWARD.COLUMN_DOC_ID+","+
							DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARDER_IDX+","+
							DBSchema.DOCUMENT_FORWARD.COLUMN_COMMENT+","+
							DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS+")"+
							" values ("+String.valueOf(docId)+", ?, ?, "+String.valueOf( forwards.get(i).get(KEY.DOCUMENT.FORWARDER_IDX) )+")";
					String[] val = { forwards.get(i).get(KEY.DOCUMENT.FORWARDER_IDX).toString(), forwards.get(i).get(KEY.DOCUMENT.FORWARD_CONTENT).toString()};
					db.execSQL(sql, val);
				}
				
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			
		}
		
		/**
		 * 문서를 받았을 때 저장 
		 * @param docHash 서버가 부여한 문서 해쉬
		 * @param senderHash 문서 작성자의 해쉬
		 * @param title 문서 제목
		 * @param content 문서 내용
		 * @param createdTS 문서를 만든 시점의 타임스탬프
		 * @param forwards 포워딩 정보 \n @see {Document.FWD_CONTENT}, @see {Document.FWD_ARRIVAL_DT}, @see {Document.FWD_FORWARDER_IDX} 가 key로 설정되어야함 
		 * @param files 첨부파일정보. \n @see {Document.ATTACH_FILE_URL}, @see {Document.ATTACH_FILE_NAME}, @see {Document.ATTACH_FILE_TYPE}, @see {Document.ATTACH_FILE_SIZE} 가 key로 설정되어야함
		 */
		public void saveDocumentOnReceived
				(String docHash, 
				String senderHash, 
				String title, 
				String content, 
				long createdTS, 
				ArrayList<HashMap<String, Object>> forwards,
				ArrayList<HashMap<String, Object>> files) {
			
			//document 테이블에 insert
			String sql =
					"insert or ignore into "+DBSchema.DOCUMENT.TABLE_NAME+
					" ("+DBSchema.DOCUMENT.COLUMN_IDX+","+
					DBSchema.DOCUMENT.COLUMN_CREATOR_IDX+","+
					DBSchema.DOCUMENT.COLUMN_TITLE+","+
					DBSchema.DOCUMENT.COLUMN_CONTENT+","+
					DBSchema.DOCUMENT.COLUMN_CREATED_TS+","+
					DBSchema.DOCUMENT.COLUMN_IS_CHECKED+","+
					DBSchema.DOCUMENT.COLUMN_CATEGORY+") " +
					"values(?, ?, ?, ?, "+String.valueOf(createdTS)+", 0 ,"+Document.TYPE_RECEIVED+")";
			String[] val = { docHash, senderHash, title, content };
			db.execSQL(sql,val);
			
			//doc rowid
			long docId = lastInsertId();
			//포워딩정보저장
			if(forwards != null)
				addForwardInfo(docHash, forwards);
			if(files != null)
				saveAttachmentInfo(docId, files);
		}
		
		/**
		 * 문서의 즐겨찾기 상태 toggle
		 * @param hash 문서 해쉬
		 */
		public void setFavorite( String hash, boolean isFavorite) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, hash);
			if ( docId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			int isFavorite_int;
			if ( isFavorite == true ) {
				isFavorite_int=1;
			} else {
				isFavorite_int=0;	
			}
			String sql = "update "+DBSchema.DOCUMENT.TABLE_NAME+" SET "+
						DBSchema.DOCUMENT.COLUMN_IS_FAVORITE+" = " +String.valueOf(isFavorite_int)+
						" where _id = "+String.valueOf(docId);
			db.execSQL(sql);
		}
		
		/**
		 * 문서를 확인했을 때
		 * @param docHash 채팅 해쉬
		 * @param checkedTS 체크한 시간
		 */
		public void updateCheckedTS( String docHash, long checkedTS ) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);
			if ( docId == Constants.NOT_SPECIFIED ) {
				return;
			}
			String sql = "update "+DBSchema.DOCUMENT.TABLE_NAME+
					" SET "+DBSchema.DOCUMENT.COLUMN_IS_CHECKED+" = 1," +
					DBSchema.DOCUMENT.COLUMN_CHECKED_TS+" = "+String.valueOf(checkedTS)+
					" where _id = "+String.valueOf(docId);
			db.execSQL(sql);
		}
		
		/**
		 * 문서 목록 가져오기
		 * @b 커서구조
		 * @b COLUMN_DOC_IDX str 문서해쉬\n
		 * @b COLUMN_DOC_TITLE str 문서제목\n
		 * @b COLUMN_IS_CHECKED int 자기가확인했는지\n
		 * @b COLUMN_SENDER_IDX str 문서보낸사람\n
		 * @b COLUMN_CREATED_TS long 문서생성일(보낸시간)\n
		 * @param docCategory 문서타입 @see {Document.TYPE_RECEIVED} @see {Document.TYPE_FAVORITE} @see {Document.TYPE_DEPARTED} 
		 * @return 
		 */
		public Cursor getDocumentList(int docCategory) {
			
			if ( docCategory == Document.TYPE_FAVORITE ) {
				String sql ="select _id,"+
						DBSchema.DOCUMENT.COLUMN_IDX+COLUMN_DOC_IDX+", "+
						DBSchema.DOCUMENT.COLUMN_TITLE+COLUMN_DOC_TITLE+", "+
						DBSchema.DOCUMENT.COLUMN_IS_CHECKED+COLUMN_IS_CHECKED+", "+
						DBSchema.DOCUMENT.COLUMN_CREATOR_IDX+COLUMN_SENDER_IDX+", "+
						DBSchema.DOCUMENT.COLUMN_CREATED_TS+COLUMN_CREATED_TS+
						" from"+DBSchema.DOCUMENT.TABLE_NAME+
						"where "+DBSchema.DOCUMENT.COLUMN_IS_FAVORITE+" = 1 "+
						" order by "+DBSchema.DOCUMENT.COLUMN_CREATED_TS+" desc ";
				return db.rawQuery(sql, null);
			} else {
				String sql ="select _id, "+
						DBSchema.DOCUMENT.COLUMN_IDX+COLUMN_DOC_IDX+", "+
						DBSchema.DOCUMENT.COLUMN_TITLE+COLUMN_DOC_TITLE+", "+
						DBSchema.DOCUMENT.COLUMN_IS_CHECKED+COLUMN_IS_CHECKED+", "+
						DBSchema.DOCUMENT.COLUMN_CREATOR_IDX+COLUMN_SENDER_IDX+", "+
						DBSchema.DOCUMENT.COLUMN_CREATED_TS+COLUMN_CREATED_TS+
						" from"+DBSchema.DOCUMENT.TABLE_NAME+
						"where "+DBSchema.DOCUMENT.COLUMN_CATEGORY+" = "+String.valueOf(docCategory)+
						" order by "+DBSchema.DOCUMENT.COLUMN_CREATED_TS+" desc ";
				return db.rawQuery(sql, null);
			}
			
			
		}

		/**
		 * 한 문서의 기본 정보 조회(포워딩,파일빼고)
		 * @b 커서구조
		 * @b COLUMN_DOC_TITLE str 제목\n
		 * @b COLUMN_DOC_CONTENT str 내용\n
		 * @b COLUMN_SENDER_IDX str 발신자\n
		 * @b COLUMN_DOC_TS long 발신일시\n
		 * @b COLUMN_DOC_TYPE int 문서카테고리 Document.TYPE_DEPARTED, Document.TYPE_RECEIVED, Document.TYPE_FAVORITE\n
		 * @b COLUMN_IS_FAVORITE int 즐겨찾기여부
		 * @b COLUMN_IS_CHECKED int 자기가확인했는지\n
		 * @b COLUMN_CHECKED_TS long 확인한시간
		 * @param docHash 문서 해시
		 * @return
		 */
		public Cursor getDocumentContent(String docHash) {
			
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);
			
			String sql ="select _id,"+
					DBSchema.DOCUMENT.COLUMN_TITLE + COLUMN_DOC_TITLE +", "+
					DBSchema.DOCUMENT.COLUMN_CONTENT + COLUMN_DOC_CONTENT +", "+
					DBSchema.DOCUMENT.COLUMN_CREATOR_IDX + COLUMN_SENDER_IDX +", "+
					DBSchema.DOCUMENT.COLUMN_CREATED_TS + COLUMN_DOC_TS +", "+
					DBSchema.DOCUMENT.COLUMN_CHECKED_TS + COLUMN_CHECKED_TS +", "+
					DBSchema.DOCUMENT.COLUMN_CATEGORY + COLUMN_DOC_TYPE +", "+
					DBSchema.DOCUMENT.COLUMN_IS_CHECKED + COLUMN_IS_CHECKED +", "+
					DBSchema.DOCUMENT.COLUMN_IS_FAVORITE + COLUMN_IS_FAVORITE +
					" from"+DBSchema.DOCUMENT.TABLE_NAME+
					"where _id = "+String.valueOf(docId);
			return db.rawQuery(sql,null );
		}
		
		/**
		 * 문서의 포워딩 정보
		 * @b 커서구조
		 * @b COLUMN_FORWARDER_IDX str 포워더\n
		 * @b COLUMN_FORWARD_COMMENT str 코멘트\n
		 * @b COLUMN_FORWARD_TS long 포워딩한 시간\n
		 * @param docHash
		 * @return
		 */
		public Cursor getDocumentForwardInfo(String docHash) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);
					
			String sql ="select _id, "+
					DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARDER_IDX + COLUMN_FORWARDER_IDX +", "+
					DBSchema.DOCUMENT_FORWARD.COLUMN_COMMENT + COLUMN_FORWARD_COMMENT +", "+
					DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS + COLUMN_FORWARD_TS +
					" from"+DBSchema.DOCUMENT_FORWARD.TABLE_NAME +
					" where "+DBSchema.DOCUMENT_FORWARD.COLUMN_DOC_ID+" = "+String.valueOf(docId)+
					" order by "+DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS+" desc";
			return db.rawQuery(sql,null);
		}
		
		/**
		 * 문서의 첨부파일 정보
		 * @b 커서구조
		 * @b COLUMN_FILE_NAME str 파일이름\n
		 * @b COLUMN_FILE_TYPE int 파일종류\n
		 * @b COLUMN_FILE_SIZE long 파일사이즈 in byte\n
		 * @b COLUMN_FILE_IDX str 파일URL\n
		 * @param docHash
		 * @return
		 */
		public Cursor getDocumentAttachment(String docHash) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);
			
			String sql ="select _id, "+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_NAME + COLUMN_FILE_NAME +", "+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_TYPE + COLUMN_FILE_TYPE +", "+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_SIZE_IN_BYTE + COLUMN_FILE_SIZE +", "+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_IDX + COLUMN_FILE_IDX +
					" from"+DBSchema.DOCUMENT_ATTACHMENT.TABLE_NAME +
					"where _id = "+String.valueOf(docId);
			return db.rawQuery(sql,null);
		}

		/**
		 * 문서 수신자 정보 가져오기
		 * @b 커서구조
		 * @b COLUMN_USER_IDX 수신자해쉬\n
		 * @param hash 문서 해쉬
		 * @return
		 */
		public Cursor getReceivers(String hash) {
			long id = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, hash);
			
			String sql = 
					"select _id, "+DBSchema.DOCUMENT_RECEIVER.COLUMN_RECEIVER_IDX+
					" from"+DBSchema.DOCUMENT_RECEIVER.TABLE_NAME+
					" where "+DBSchema.DOCUMENT_RECEIVER.COLUMN_DOC_ID+" = "+String.valueOf(id);
			return db.rawQuery(sql,null);
		}
		
		public static final String COLUMN_DOC_IDX = "doc_idx";
		public static final String COLUMN_DOC_TITLE = "doc_title";
		public static final String COLUMN_DOC_CONTENT = "doc_content";
		public static final String COLUMN_DOC_TS = "doc_ts";
		public static final String COLUMN_CHECKED_TS = "checked_TS";
		public static final String COLUMN_DOC_TYPE = "doc_type";
		public static final String COLUMN_IS_FAVORITE = "is_favorite";
		public static final String COLUMN_IS_CHECKED = "is_checked";
		public static final String COLUMN_SENDER_IDX = "sender_idx";
		public static final String COLUMN_CREATED_TS = "created_ts";
		public static final String COLUMN_FORWARDER_IDX = "fwder_idx";
		public static final String COLUMN_FORWARD_COMMENT = "fwd_comment";
		public static final String COLUMN_FORWARD_TS = "fwd_ts";
		public static final String COLUMN_FILE_NAME = "file_name";
		public static final String COLUMN_FILE_TYPE = "file_type";
		public static final String COLUMN_FILE_SIZE = "file_size";
		public static final String COLUMN_FILE_IDX = "file_idx";
		public static final String COLUMN_USER_IDX = "user_idx";
		
	}

	public class SurveyProcManager {
		/**
		 * 설문조사보낼때 해쉬 저장
		 * @param surveyHash 서버가 부여한 설문조사 해쉬
		 */
		public void saveSurveyOnSend(String surveyHash) {
			String sql =
					"insert into "+DBSchema.SURVEY.TABLE_NAME+
					" ("+DBSchema.SURVEY.COLUMN_IDX+","+
//					DBSchema.SURVEY.COLUMN_TITLE+","+
//					DBSchema.SURVEY.COLUMN_CONTENT+","+
//					DBSchema.SURVEY.COLUMN_CREATOR_IDX+","+
//					DBSchema.SURVEY.COLUMN_CREATED_TS+","+
					DBSchema.SURVEY.COLUMN_CATEGORY+") " +
					"values(?, "+Survey.TYPE_DEPARTED+")";
			String[] val = {surveyHash };
			db.execSQL(sql,val);
			
			//자기가 만든건 확인시간을 지금으로
			updateCheckedTS(surveyHash, new Date().getTime());
		}
		
		/**
		 * 설문조사 받았을때 해쉬저장
		 * @param surveyHash
		 */
		public void saveSurveyOnReceived(String surveyHash) {
			String sql =
					"insert into "+DBSchema.SURVEY.TABLE_NAME+
					" ("+DBSchema.SURVEY.COLUMN_IDX+","+
//					DBSchema.SURVEY.COLUMN_TITLE+","+
//					DBSchema.SURVEY.COLUMN_CONTENT+","+
//					DBSchema.SURVEY.COLUMN_CREATOR_IDX+","+
//					DBSchema.SURVEY.COLUMN_CREATED_TS+","+
					DBSchema.SURVEY.COLUMN_CATEGORY+") " +
					"values(?,"+Survey.TYPE_RECEIVED+")";
			String[] val = {surveyHash};
			db.execSQL(sql,val);
		}
		/**
		 * 설문조사를 확인했을 때
		 * @param svyHash 채팅 해쉬
		 * @param checkedTS 체크한 시간
		 */
		public void updateCheckedTS( String svyHash, long checkedTS ) {
			long svyId = hashToId(DBSchema.SURVEY.TABLE_NAME, DBSchema.SURVEY.COLUMN_IDX, svyHash);
			if ( svyId == Constants.NOT_SPECIFIED ) {
				return;
			}
			String sql = "update "+DBSchema.SURVEY.TABLE_NAME+
					" SET "+DBSchema.SURVEY.COLUMN_IS_CHECKED+" = 1," +
					DBSchema.SURVEY.COLUMN_CHECKED_TS+" = "+String.valueOf(checkedTS)+
					" where _id = "+String.valueOf(svyId);
			db.execSQL(sql);
		}
		
		/**
		 * 설문조사를 응답했을 때
		 * @param svyHash 해쉬
		 * @param checkedTS 체크한 시간
		 */
		public void updateAnsweredTS( String svyHash, long answeredTS ) {
			long svyId = hashToId(DBSchema.SURVEY.TABLE_NAME, DBSchema.SURVEY.COLUMN_IDX, svyHash);
			if ( svyId == Constants.NOT_SPECIFIED ) {
				return;
			}
			String sql = "update "+DBSchema.SURVEY.TABLE_NAME+
					" SET "+DBSchema.SURVEY.COLUMN_IS_ANSWERED+" = 1," +
					DBSchema.SURVEY.COLUMN_ANSWERED_TS+" = "+String.valueOf(answeredTS)+
					" where _id = "+String.valueOf(svyId);
			db.execSQL(sql);
		}
		
		/**
		 * 설문조사 목록 가져오기
		 * @b 커서구조
		 * @b COLUMN_SURVEY_IDX str 해시\n
		 * @b COLUMN_SURVEY_IS_ANSWERED int 응답여부\n
		 * @b COLUMN_SURVEY_ANSWERED_TS long 응답한시간\n
		 * @b COLUMN_SURVEY_IS_CHECKED int 확인여부\n
		 * @b COLUMN_SURVEY_CHECKED_TS long 확인한시간\n
		 * @param svyCategory 내가받은거면 Survey.TYPE_RECEIVED, 내가보낸거면 Survey.TYPE_DEPARTED
		 * @return
		 */
		public Cursor getSurveyList(int svyCategory) {
			String sql ="select _id, "+
					DBSchema.SURVEY.COLUMN_IDX+COLUMN_SURVEY_IDX+", "+
//					DBSchema.SURVEY.COLUMN_TITLE+COLUMN_SURVEY_NAME+", "+
					DBSchema.SURVEY.COLUMN_IS_CHECKED+COLUMN_SURVEY_IS_CHECKED+", "+
//					DBSchema.SURVEY.COLUMN_CREATOR_IDX+COLUMN_SURVEY_SENDER_IDX+", "+
					DBSchema.SURVEY.COLUMN_IS_ANSWERED+COLUMN_SURVEY_IS_ANSWERED+
					" from"+DBSchema.SURVEY.TABLE_NAME+
					"where "+DBSchema.SURVEY.COLUMN_CATEGORY+" = "+String.valueOf(svyCategory);
			return db.rawQuery(sql, null);
		}
		
		/**
		 * 설문조사 기본 정보 가져오기
		 * @b 커서구조
		 * @b COLUMN_SURVEY_IS_ANSWERED int 응답여부\n
		 * @b COLUMN_SURVEY_ANSWERED_TS long 응답한시간\n
		 * @b COLUMN_SURVEY_IS_CHECKED int 확인여부\n
		 * @b COLUMN_SURVEY_CHECKED_TS long 확인한시간\n
		 * @b COLUMN_SURVEY_TYPE int 서베이타입\n
		 * @param hash
		 * @return
		 */
		public Cursor getSurveyInfo(String hash) {
			String sql ="select _id, "+
//					DBSchema.SURVEY.COLUMN_TITLE+COLUMN_SURVEY_NAME+", "+
//					DBSchema.SURVEY.COLUMN_CONTENT+COLUMN_SURVEY_CONTENT+", "+
//					DBSchema.SURVEY.COLUMN_CREATED_TS+COLUMN_SURVEY_CREATED_TS+", "+
					DBSchema.SURVEY.COLUMN_ANSWERED_TS+COLUMN_SURVEY_ANSWERED_TS+", "+
//					DBSchema.SURVEY.COLUMN_CREATOR_IDX+COLUMN_SURVEY_SENDER_IDX+", "+
					DBSchema.SURVEY.COLUMN_CATEGORY+COLUMN_SURVEY_TYPE+", "+
					DBSchema.SURVEY.COLUMN_IS_CHECKED+COLUMN_SURVEY_IS_CHECKED+", "+
					DBSchema.SURVEY.COLUMN_CHECKED_TS+COLUMN_SURVEY_CHECKED_TS+", "+
					DBSchema.SURVEY.COLUMN_IS_ANSWERED+COLUMN_SURVEY_IS_ANSWERED+
					" from"+DBSchema.SURVEY.TABLE_NAME+
					"where "+DBSchema.SURVEY.COLUMN_IDX+" = ?";
			String[] val = {hash};
			return db.rawQuery(sql, val);		
		}
		
		/**
		 * 설문조사 수신자 정보 가져오기
		 * @b 커서구조
		 * @b COLUMN_USER_IDX 수신자해쉬\n
		 * @param hash 설문조사 해쉬
		 * @return
		 * @deprecated 필요없어짐 
		 */
		public Cursor getReceivers(String hash) {
			long id = hashToId(DBSchema.SURVEY.TABLE_NAME, DBSchema.SURVEY.COLUMN_IDX, hash);
			
			String sql = 
					"select _id, "+DBSchema.SURVEY_RECEIVER.COLUMN_RECEIVER_IDX+
					" from"+DBSchema.SURVEY_RECEIVER.TABLE_NAME+
					" where "+DBSchema.SURVEY_RECEIVER.COLUMN_SURVEY_ID+" = "+String.valueOf(id);
			return db.rawQuery(sql,null);		
		}
//		public static final String COLUMN_SURVEY_NAME = "survey_name";
		public static final String COLUMN_SURVEY_IDX = "survey_idx";
		public static final String COLUMN_USER_IDX = "user_idx";
//		public static final String COLUMN_SURVEY_SENDER_IDX = "sender_idx";
		public static final String COLUMN_SURVEY_IS_CHECKED = "is_checked";
		public static final String COLUMN_SURVEY_CHECKED_TS = "checked_ts";
		public static final String COLUMN_SURVEY_IS_ANSWERED = "is_answered";
		public static final String COLUMN_SURVEY_ANSWERED_TS = "answered_ts";
//		public static final String COLUMN_SURVEY_CONTENT = "survey_content";
//		public static final String COLUMN_SURVEY_CREATED_TS = "created_ts";
		public static final String COLUMN_SURVEY_TYPE = "survey_type";
	}
	
	public class MemberProcManager {
		
		/**
		 * 멤버를 즐겨찾기에 추가/삭제
		 * @param hash
		 */
		public void setFavorite( String hash, boolean isFavorite ) {
			String sql = null;
			if ( isFavorite ) {
				
				sql = "insert or ignore into "+
				DBSchema.USER_FAVORITE.TABLE_NAME+"("+
				DBSchema.USER_FAVORITE.COLUMN_IDX+", "+
				DBSchema.USER_FAVORITE.COLUMN_IS_GROUP+") "+
				" values (?, 0)";
				String[] val = {hash};
				db.execSQL(sql,val);
			} else {
				
				sql = "delete from "+
				DBSchema.USER_FAVORITE.TABLE_NAME+"" +
				" where "+DBSchema.USER_FAVORITE.COLUMN_IDX+" = ? ";
				String[] val = {hash};
				db.execSQL(sql,val);
			}
		}
		
		/**
		 * 멤버 여러명을 한 즐겨찾기 그룹으로 등록\n
		 * @param hash
		 */
		public void addFavoriteGroup( ArrayList<String> hashArray ) {
			if ( hashArray.size() == 0 ) {
				return;
			}
			
			String sql = "insert into "+DBSchema.USER_FAVORITE.TABLE_NAME+" ("+
			DBSchema.USER_FAVORITE.COLUMN_IS_GROUP+") values(1)";
			db.execSQL(sql);
			
			long gpId = lastInsertId();
			String gpHash = md5(DBSchema.USER_FAVORITE.TABLE_NAME+String.valueOf(gpId));
			
			sql = "update "+DBSchema.USER_FAVORITE.TABLE_NAME+" set hash = "+gpHash+" where _id = "+String.valueOf(gpId);
			db.execSQL(sql);
			
			db.beginTransaction();
			try {
				for ( int i=0; i<hashArray.size(); i++ ) {
					sql = "insert into "+DBSchema.USER_FAVORITE_GROUP.TABLE_NAME+" ("+
							DBSchema.USER_FAVORITE_GROUP.COLUMN_FAVORITE_ID+", "+
							DBSchema.USER_FAVORITE_GROUP.COLUMN_MEMBER_IDX+") " +
							"values ("+String.valueOf(gpId)+", ?)";
					String[] val = { hashArray.get(i) };
					db.execSQL(sql,val);
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			
		}
		
		/**
		 * 즐겨찾기그룹삭제
		 * @param hash 즐겨찾기그룹hash
		 */
		public void removeFavoriteGroup( String groupHash ) {
			long gpId = hashToId(DBSchema.USER_FAVORITE.TABLE_NAME, DBSchema.USER_FAVORITE.COLUMN_IDX, groupHash);
			if ( gpId == Constants.NOT_SPECIFIED ) {
				return;
			}
			//소속멤버들을 group테이블에서 삭제
			String sql = "delete from "+DBSchema.USER_FAVORITE_GROUP.TABLE_NAME+" where favorite_id = "+String.valueOf(gpId);
			db.execSQL(sql);
			
			//그룹삭제
			sql = "delete from "+DBSchema.USER_FAVORITE.TABLE_NAME+" where _id = "+String.valueOf(gpId);
			db.execSQL(sql);
		}
		
		/**
		 * 즐겨찾기 멤버나 멤버그룹 이름 변경
		 * @param hash 즐겨찾기 멤버 해시나 그룹 해시
		 * @param title 변경할 이름
		 */
		public void updateFavoriteTitle( String hash, String title ) {
			String sql = "update "+DBSchema.USER_FAVORITE.TABLE_NAME+" set "+DBSchema.USER_FAVORITE.COLUMN_TITLE+" = ? " +
					"where "+DBSchema.USER_FAVORITE.COLUMN_IDX+" = ?";
			String[] val = { title, hash };
			db.execSQL(sql,val);
		}
		
		/**
		 * 해당 유저가 즐겨찾기에 있는지
		 * @b 커서구조
		 * @b COLUMN_IS_FAVORITE 즐겨찾기인지 아닌지  
		 * @param hash
		 * @return
		 */
		public boolean isUserFavorite(String hash) {
			String sql = "select count(_id) isFav from "+DBSchema.USER_FAVORITE.TABLE_NAME+" where "+DBSchema.USER_FAVORITE.COLUMN_IDX+"=?";
			String[] val = {hash};
			Cursor c = db.rawQuery(sql, val);
			c.moveToNext();
			return c.getInt(0)>0 ? true:false;
		}
		
		/**
		 * 즐겨찾기 목록 가져옴
		 * @b 커서구조
		 * @b COLUMN_FAVORITE_IDX str 즐겨찾기 해쉬(유저면 유저해쉬 그룹이면 즐찾그룹해쉬)\n
		 * @b COLUMN_FAVORITE_NAME str 즐겨찾기 이름\n
		 * @b COLUMN_FAVORITE_IS_GROUP int 그룹인지 아닌지\n
		 * @return
		 */
		public Cursor getFavoriteList() {
			String sql = "select _id, "+
					DBSchema.USER_FAVORITE.COLUMN_IDX+COLUMN_FAVORITE_IDX+", "+
					DBSchema.USER_FAVORITE.COLUMN_TITLE+COLUMN_FAVORITE_NAME+", "+
					DBSchema.USER_FAVORITE.COLUMN_IS_GROUP+COLUMN_FAVORITE_IS_GROUP+
					" from "+DBSchema.USER_FAVORITE.TABLE_NAME+
					" where 1=1 order by "+DBSchema.USER_FAVORITE.COLUMN_TITLE+" asc";
			return db.rawQuery(sql, null);
		}
		
		/**
		 * 즐겨찾기 그룹에 소속된 멤버들의 hash를 array로 리턴
		 * @b 커서구조
		 * @b COLUMN_USER_IDX 유저해쉬\n
		 * @param hash
		 * @return
		 */
		public Cursor getFavoriteGroupMemberList(String hash){
			
			long gpId = hashToId(DBSchema.USER_FAVORITE.TABLE_NAME, DBSchema.USER_FAVORITE.COLUMN_IDX, hash);
			
			String sql = "select _id, "+
					DBSchema.USER_FAVORITE_GROUP.COLUMN_MEMBER_IDX+COLUMN_USER_IDX+
					" from "+DBSchema.USER_FAVORITE_GROUP.TABLE_NAME+
					" where "+DBSchema.USER_FAVORITE_GROUP.COLUMN_FAVORITE_ID+" = "+String.valueOf(gpId);
			return db.rawQuery(sql, null);
		}
		
		/**
		 * 즐겨찾기에 등록된 유저나 그룹의 정보 가져옴
		 * @b 커서구조
		 * @b COLUMN_FAVORITE_NAME str 제목. 따로 설정안되었을 시에는 null
		 * @param hash favorite hash
		 * @return
		 */
		public Cursor getFavoriteInfo( String hash ) {
			long gpId = hashToId(DBSchema.USER_FAVORITE.TABLE_NAME, DBSchema.USER_FAVORITE.COLUMN_IDX, hash);
			
			String sql =
					"select _id, "+
					DBSchema.USER_FAVORITE.COLUMN_TITLE+COLUMN_FAVORITE_NAME+
					" from "+DBSchema.USER_FAVORITE.TABLE_NAME+
					" where _id = "+String.valueOf(gpId);
			return db.rawQuery(sql, null);
		}
		
		public static final String COLUMN_FAVORITE_IDX = "fav_idx";
		public static final String COLUMN_FAVORITE_NAME = "fav_name";
		public static final String COLUMN_FAVORITE_IS_GROUP = "fav_is_group";
		public static final String COLUMN_USER_IDX = "user_idx";
		public static final String COLUMN_IS_FAVORITE = "is_fav";
		
	}
}