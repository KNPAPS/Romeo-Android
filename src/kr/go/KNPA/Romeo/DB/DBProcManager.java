package kr.go.KNPA.Romeo.DB;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Config.Constants;
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
	
	private DBProcManager(Context context) {
		if ( this.dbm == null ) {
			this.dbm = new DBManager(context);  
		}
		if ( this.db == null ) {
			this.db = dbm.getWritableDatabase();
		}
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
	
	private long hashToId(String tableName, String hashColName, String hash) {
		String[] args = { hash };
		Cursor c = db.rawQuery("select _id from "+tableName+" where "+hashColName+" = ?",args);
		if ( c.getCount() > 0 ) {
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
		 * @return 채팅방 해쉬
		 */
		public String createRoom(ArrayList<String> userHashes, int chatType ) {

			if ( userHashes.size() == 0 ) {
				Log.w(TAG,"유저해쉬 어레이가 비어있음 at createRoom(ArrayList<String> userHashes, int chatType )");
				return null;
			}
        	
			// 새 방 레코드 생성
			String sql =
					"insert into "+DBSchema.ROOM.TABLE_NAME+
					"("+DBSchema.ROOM.COLUMN_TYPE+","+DBSchema.ROOM.COLUMN_IS_FAVORITE+
					") values ("+String.valueOf(chatType)+",0)";
			db.execSQL(sql);
			long roomId = lastInsertId();

			//roomhash 발급
			String roomHash = md5(DBSchema.ROOM.TABLE_NAME+String.valueOf(roomId));
			
			//room table에 roomhash 업데이트
			sql = 
					"update "+DBSchema.ROOM.TABLE_NAME+
					"set "+DBSchema.ROOM.COLUMN_HASH+" = ?" +
					" where "+ DBSchema.ROOM._ID + " = "+String.valueOf(roomId);
			String[] values = {roomHash};
			db.execSQL(sql, values);
			
			
			//채팅방에 참여하고 있는 유저들의 해쉬를 room_chatter 테이블에 추가			
			db.beginTransaction();
			try {
				int i, n=userHashes.size();
				for( i=0; i<n; i++ ) {
					sql = 
						"insert into "+DBSchema.ROOM_CHATTER.TABLE_NAME+
						"("+DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+", "+DBSchema.ROOM_CHATTER.COLUMN_USER_HASH+")" +
						"values ( "+String.valueOf( roomId )+", ? )";
					String[] val = { userHashes.get(i) };
					db.execSQL(sql, val);
				}
				
	            db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			return roomHash;
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
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_HASH, roomHash);
			if ( roomId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			String sql = 
					"insert into "+DBSchema.CHAT.TABLE_NAME+
					"("+DBSchema.CHAT.COLUMN_ROOM_ID+", "+
						DBSchema.CHAT.COLUMN_HASH+", "+
						DBSchema.CHAT.COLUMN_SENDER_HASH+", "+
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
			
			String sql = "update "+DBSchema.CHAT.TABLE_NAME+" " +
					"set "+
					DBSchema.CHAT.COLUMN_IS_CHECKED + " = 1,"+
					DBSchema.CHAT.COLUMN_CHECKED_TS + " = " + String.valueOf(checkedTS)+
					" where " + DBSchema.CHAT.COLUMN_HASH + " IN (";
			for( int i=0; i<chatHash.size(); i++) {
				sql += "?,";
			}
			sql = sql.replaceAll("/,$/", ")");
			db.execSQL(sql, (String[])chatHash.toArray());
		}
		
		/**
		 * 유저가 채팅방에 들어가 메세지들을 확인했을 때의 시간을 기록함 
		 * @param roomHash 채팅방 해쉬
		 * @param lastReadTS lastReadTS
		 */
		public void updateLastReadTS( String roomHash, long lastReadTS ) {
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_HASH, roomHash);
			
			if ( roomId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			String sql = "update "+DBSchema.ROOM.TABLE_NAME+
					"set "+
					DBSchema.ROOM.COLUMN_LAST_READ_TS+" = "+String.valueOf(lastReadTS)+
					" where "+DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+" = "+String.valueOf(roomId);
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
		 * @b COLUMN_ROOM_HASH 채팅방 해시\n
		 * @b COLUMN_ROOM_TITLE 채팅방 제목\n
		 * @b COLUMN_ROOM_NUM_CHATTER 채팅방에 있는 사람 수\n
		 * @b COLUMN_ROOM_NUM_UNCHECKED_CHAT 읽지 않은 채팅 수\n
		 * @b COLUMN_ROOM_LAST_CHAT_TS 마지막 채팅이 도착한 시간 TS\n
		 * @b COLUMN_ROOM_LAST_CHAT_CONTENT 마지막 채팅의 내용\n
		 * @param roomType 
		 * @return cursor
		 */
		public Cursor getRoomList(int roomType) {
			String sql = 
					"select " +
					" r."+DBSchema.ROOM.COLUMN_HASH+
						COLUMN_ROOM_HASH+"," +
					
					" r."+DBSchema.ROOM.COLUMN_TITLE+
						COLUMN_ROOM_TITLE+"," +
					
					" (select count(rc._id) " +
						"from "+DBSchema.ROOM_CHATTER.TABLE_NAME+" rc " +
						"where rc."+DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+"= r._id ) "+
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
					"ELSE \"(사진)\" END) "+
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
		 * @b COLUMN_USER_HASH 리시버 해쉬
		 * @param hash 채팅방 해쉬
		 * @return
		 */
		public Cursor getReceiverList( String hash ) {
			
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_HASH, hash);
			
			String sql = 
					"select "+DBSchema.ROOM_CHATTER.COLUMN_USER_HASH+COLUMN_USER_HASH+
					" from "+DBSchema.ROOM_CHATTER.TABLE_NAME+
					" where "+DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+"="+String.valueOf(roomId);
			return db.rawQuery(sql,null);
		}
		
		/**
		 * 채팅방 내의 채팅 목록 불러오기
		 * @b 커서구조
		 * @b COLUMN_CHAT_SENDER_HASH 센더해쉬\n
		 * @b COLUMN_CHAT_TS 채팅TS\n
		 * @b COLUMN_CHAT_CONTENT 내용 \n
		 * @b COLUMN_CHAT_CONTENT_TYPE 내용의 종류 @see{CHAT_CONTENT_TYPE_TEXT} @see{CHAT_CONTENT_TYPE_PICTURE}\n
		 * @param roomHash
		 * @param TS 역순으로 정렬시 불러올 목록 시작 index
		 * @param 불러올 채팅의 개수
		 * @return
		 */
		public Cursor getChatList(String roomHash, int start, int count) {
		
			long roomId = hashToId(DBSchema.ROOM.TABLE_NAME, DBSchema.ROOM.COLUMN_HASH, roomHash);
			String sql=
					"select "+
					DBSchema.CHAT.COLUMN_SENDER_HASH+COLUMN_CHAT_SENDER_HASH+", "+
					DBSchema.CHAT.COLUMN_CREATED_TS+COLUMN_CHAT_TS+", "+
					DBSchema.CHAT.COLUMN_CONTENT+COLUMN_CHAT_CONTENT+", "+
					DBSchema.CHAT.COLUMN_CONTENT_TYPE+COLUMN_CHAT_CONTENT_TYPE+
					" from "+DBSchema.CHAT.TABLE_NAME+
					" where "+DBSchema.CHAT.COLUMN_ROOM_ID+" = "+String.valueOf(roomId)+
					" order by "+DBSchema.CHAT.COLUMN_CREATED_TS+" desc "+
					" limit "+String.valueOf(start)+", "+String.valueOf(count);
			return db.rawQuery(sql, null);
		}
		
		public static final String COLUMN_ROOM_HASH = "room_hash";
		public static final String COLUMN_ROOM_TITLE = "room_hash";
		public static final String COLUMN_ROOM_NUM_CHATTER = "room_hash";
		public static final String COLUMN_ROOM_NUM_NEW_CHAT = "num_new_chat";
		public static final String COLUMN_ROOM_LAST_CHAT_TS = "last_chat_ts";
		public static final String COLUMN_ROOM_LAST_CHAT_CONTENT = "last_chat_content";
		public static final String COLUMN_USER_HASH = "user_hash";
		public static final String COLUMN_CHAT_SENDER_HASH = "sender_hash";
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
					
					String[] binds = { hm.get("file_url").toString(), hm.get("file_name").toString() } ;
					long fileSize = (Long)hm.get("file_size");
					int fileType = (Integer) hm.get("file_type") ;
					
					String sql = "insert into "+DBSchema.DOCUMENT_ATTACHMENT.TABLE_NAME+
							"("+
							DBSchema.DOCUMENT_ATTACHMENT.COLUMN_DOC_ID+","+
							DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_URL+","+
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
					" ("+DBSchema.DOCUMENT.COLUMN_HASH+","+
					DBSchema.DOCUMENT.COLUMN_CREATOR_HASH+","+
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
		
		/**
		 * 문서를 포워딩했을 때 정보 추가
		 * @param docHash
		 * @param forwarderHash
		 * @param forwardComment
		 * @param forwardTS
		 */
		public void addForwardToDocument(String docHash, String forwarderHash, String forwardComment, long forwardTS) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_HASH, docHash);
			if ( docId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			String sql = "insert into "+DBSchema.DOCUMENT_FORWARD.TABLE_NAME+
					" ("+
					DBSchema.DOCUMENT_FORWARD.COLUMN_DOC_ID+","+
					DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARDER_HASH+","+
					DBSchema.DOCUMENT_FORWARD.COLUMN_COMMENT+","+
					DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS+")"+
					" values ("+String.valueOf(docId)+", ?, ?, "+String.valueOf(forwardTS)+")";
			String[] val = { forwarderHash, forwardComment };
			db.execSQL(sql, val);
			
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
					"insert into "+DBSchema.DOCUMENT.TABLE_NAME+
					" ("+DBSchema.DOCUMENT.COLUMN_HASH+","+
					DBSchema.DOCUMENT.COLUMN_CREATOR_HASH+","+
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
			for (int i=0; i<forwards.size(); i++) {
				HashMap<String,Object> hm = forwards.get(i);
				addForwardToDocument(docHash, hm.get(Document.FWD_FORWARDER_IDX).toString() , hm.get(Document.FWD_CONTENT).toString(), (Long)hm.get(Document.FWD_ARRIVAL_DT));
			}
			
			saveAttachmentInfo(docId, files);
		}
		
		/**
		 * 문서의 즐겨찾기 상태 toggle
		 * @param hash 문서 해쉬
		 */
		public void setFavorite( String hash, boolean isFavorite) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_HASH, hash);
			if ( docId == Constants.NOT_SPECIFIED ) {
				return;
			}
			
			int isFavorite_int;
			int category;
			if ( isFavorite == true ) {
				isFavorite_int=1;
				category=Document.TYPE_FAVORITE;
			} else {
				isFavorite_int=0;
				category=Document.TYPE_RECEIVED;				
			}
			String sql = "update "+DBSchema.DOCUMENT.TABLE_NAME+" SET "+
						DBSchema.DOCUMENT.COLUMN_IS_FAVORITE+" = " +String.valueOf(isFavorite_int)+", "+
						DBSchema.DOCUMENT.COLUMN_CATEGORY+" = " +String.valueOf(category)+", "+
						" where _id = "+String.valueOf(docId);
			db.execSQL(sql);
		}
		
		/**
		 * 문서를 확인했을 때
		 * @param docHash 채팅 해쉬
		 * @param checkedTS 체크한 시간
		 */
		public void updateCheckedTS( String docHash, long checkedTS ) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_HASH, docHash);
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
		 * @b COLUMN_DOC_HASH str 문서해쉬\n
		 * @b COLUMN_DOC_TITLE str 문서제목\n
		 * @b COLUMN_IS_CHECKED int 자기가확인했는지\n
		 * @b COLUMN_SENDER_HASH str 문서보낸사람\n
		 * @b COLUMN_CREATED_TS int 문서생성일(보낸시간)\n
		 * @param docCategory 문서타입 @see {Document.TYPE_RECEIVED} @see {Document.TYPE_FAVORITE} @see {Document.TYPE_DEPARTED} 
		 * @return 
		 */
		public Cursor getDocumentList(int docCategory) {
			String sql ="select "+
					DBSchema.DOCUMENT.COLUMN_HASH+COLUMN_DOC_HASH+", "+
					DBSchema.DOCUMENT.COLUMN_TITLE+COLUMN_DOC_TITLE+", "+
					DBSchema.DOCUMENT.COLUMN_IS_CHECKED+COLUMN_IS_CHECKED+", "+
					DBSchema.DOCUMENT.COLUMN_CREATOR_HASH+COLUMN_SENDER_HASH+", "+
					DBSchema.DOCUMENT.COLUMN_CREATED_TS+COLUMN_CREATED_TS+
					" from"+DBSchema.DOCUMENT.TABLE_NAME+
					"where "+DBSchema.DOCUMENT.COLUMN_CATEGORY+" = "+String.valueOf(docCategory);
			return db.rawQuery(sql, null);
		}

		/**
		 * 한 문서의 기본 정보 조회(포워딩,파일빼고)
		 * @b 커서구조
		 * @b COLUMN_DOC_TITLE str 제목\n
		 * @b COLUMN_DOC_CONTENT str 내용\n
		 * @b COLUMN_DOC_SENDER_HASH str 발신자\n
		 * @b COLUMN_DOC_TS int 발신일시\n
		 * @param docHash 문서 해시
		 * @return
		 */
		public Cursor getDocumentContent(String docHash) {
			
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_HASH, docHash);
			
			String sql ="select "+
					DBSchema.DOCUMENT.COLUMN_TITLE + COLUMN_DOC_TITLE +", "+
					DBSchema.DOCUMENT.COLUMN_CONTENT + COLUMN_DOC_CONTENT +", "+
					DBSchema.DOCUMENT.COLUMN_CREATOR_HASH + COLUMN_SENDER_HASH +", "+
					DBSchema.DOCUMENT.COLUMN_CREATED_TS + COLUMN_DOC_TS +
					" from"+DBSchema.DOCUMENT.TABLE_NAME+
					"where _id = "+String.valueOf(docId);
			return db.rawQuery(sql,null );
		}
		
		/**
		 * 문서의 포워딩 정보
		 * @b 커서구조
		 * @b COLUMN_FORWARDER_HASH str 포워더\n
		 * @b COLUMN_FORWARD_COMMENT str 코멘트\n
		 * @b COLUMN_FORWARD_TS int 포워딩한 시간\n
		 * @param docHash
		 * @return
		 */
		public Cursor getDocumentForwardInfo(String docHash) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_HASH, docHash);
					
			String sql ="select "+
					DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARDER_HASH + COLUMN_FORWARDER_HASH +", "+
					DBSchema.DOCUMENT_FORWARD.COLUMN_COMMENT + COLUMN_FORWARD_COMMENT +", "+
					DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS + COLUMN_FORWARD_TS +
					" from"+DBSchema.DOCUMENT_FORWARD.TABLE_NAME +
					"where _id = "+String.valueOf(docId);
			return db.rawQuery(sql,null);
		}
		
		/**
		 * 문서의 첨부파일 정보
		 * @b 커서구조
		 * @b COLUMN_FILE_NAME str 파일이름\n
		 * @b COLUMN_FILE_TYPE int 파일종류\n
		 * @b COLUMN_FILE_SIZE int 파일사이즈 in byte\n
		 * @b COLUMN_FILE_URL str 파일URL\n
		 * @param docHash
		 * @return
		 */
		public Cursor getDocumentAttachment(String docHash) {
			long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_HASH, docHash);
			
			String sql ="select "+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_NAME + COLUMN_FILE_NAME +", "+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_TYPE + COLUMN_FILE_TYPE +", "+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_SIZE_IN_BYTE + COLUMN_FILE_SIZE +", "+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_URL + COLUMN_FILE_URL +
					" from"+DBSchema.DOCUMENT_ATTACHMENT.TABLE_NAME +
					"where _id = "+String.valueOf(docId);
			return db.rawQuery(sql,null);
		}
		
		public static final String COLUMN_DOC_HASH = "doc_hash";
		public static final String COLUMN_DOC_TITLE = "doc_title";
		public static final String COLUMN_DOC_CONTENT = "doc_content";
		public static final String COLUMN_DOC_TS = "doc_ts";
		public static final String COLUMN_IS_CHECKED = "is_checked";
		public static final String COLUMN_SENDER_HASH = "sender_hash";
		public static final String COLUMN_CREATED_TS = "created_ts";
		public static final String COLUMN_FORWARDER_HASH = "fwder_hash";
		public static final String COLUMN_FORWARD_COMMENT = "fwd_comment";
		public static final String COLUMN_FORWARD_TS = "fwd_ts";
		public static final String COLUMN_FILE_NAME = "file_name";
		public static final String COLUMN_FILE_TYPE = "file_type";
		public static final String COLUMN_FILE_SIZE = "file_size";
		public static final String COLUMN_FILE_URL = "file_url";
	}

	public class SurveyProcManager {
		/**
		 * 설문조사보낼때 정보저장
		 * @param surveyHash 서버가 부여한 설문조사 해쉬
		 * @param title 설문조사 제목
		 * @param content 설문조사 설명
		 * @param creatorHash 설문조사 만든사람 해쉬
		 * @param createdTS 설문조사 만든 시간 TS
		 */
		public void saveSurveyOnSend(String surveyHash,String title, String content, String creatorHash, long createdTS) {
			String sql =
					"insert into "+DBSchema.SURVEY.TABLE_NAME+
					" ("+DBSchema.SURVEY.COLUMN_HASH+","+
					DBSchema.SURVEY.COLUMN_TITLE+","+
					DBSchema.SURVEY.COLUMN_CONTENT+","+
					DBSchema.SURVEY.COLUMN_CREATOR_HASH+","+
					DBSchema.SURVEY.COLUMN_CREATED_TS+","+
					DBSchema.SURVEY.COLUMN_CATEGORY+") " +
					"values(?, ?, ?, ?, "+String.valueOf(createdTS)+","+Survey.TYPE_DEPARTED+")";
			String[] val = {surveyHash,title,content,creatorHash};
			db.execSQL(sql,val);
			
			//자기가 만든건 확인시간을 지금으로
			updateCheckedTS(surveyHash, createdTS);
		}
		
		/**
		 * 설문조사 받았을때 정보저장
		 * @param surveyHash
		 * @param title
		 * @param content
		 * @param creatorHash
		 */
		public void saveSurveyOnReceived(String surveyHash,String title, String content, String creatorHash, long createdTS) {
			String sql =
					"insert into "+DBSchema.SURVEY.TABLE_NAME+
					" ("+DBSchema.SURVEY.COLUMN_HASH+","+
					DBSchema.SURVEY.COLUMN_TITLE+","+
					DBSchema.SURVEY.COLUMN_CONTENT+","+
					DBSchema.SURVEY.COLUMN_CREATOR_HASH+","+
					DBSchema.SURVEY.COLUMN_CREATED_TS+","+
					DBSchema.SURVEY.COLUMN_CATEGORY+") " +
					"values(?, ?, ?, ?, "+String.valueOf(createdTS)+","+Survey.TYPE_RECEIVED+")";
			String[] val = {surveyHash,title,content,creatorHash};
			db.execSQL(sql,val);
		}
		/**
		 * 설문조사를 확인했을 때
		 * @param svyHash 채팅 해쉬
		 * @param checkedTS 체크한 시간
		 */
		public void updateCheckedTS( String svyHash, long checkedTS ) {
			long svyId = hashToId(DBSchema.SURVEY.TABLE_NAME, DBSchema.SURVEY.COLUMN_HASH, svyHash);
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
		 * 설문조사 목록 가져오기
		 * @b 커서구조
		 * @b COLUMN_SURVEY_HASH str 해시\n
		 * @b COLUMN_SURVEY_NAME str 설문제목\n
		 * @b COLUMN_SURVEY_IS_CHECKED int 확인여부\n
		 * @b COLUMN_SURVEY_IS_ANSWERED int 대답여부\n
		 * @param svyCategory 내가받은거면 Survey.TYPE_RECEIVED, 내가보낸거면 Survey.TYPE_DEPARTED
		 * @return
		 */
		public Cursor getSurveyList(int svyCategory) {
			return null;
		}
		
		/**
		 * 설문조사 기본 정보 가져오기
		 * @b 커서구조
		 * @b COLUMN_SURVEY_NAME str 설문제목\n
		 * @b COLUMN_SURVEY_CONTENT str 내용\n
		 * @b COLUMN_SURVEY_CREATED_TS int 설문조사 만든시간\n
		 * @b COLUMN_SURVEY_OPEN_TS int 오픈시간\n
		 * @b COLUMN_SURVEY_CLOSE_TS int 마감시간\n
		 * @b COLUMN_SURVEY_IS_ANSWERED int 응답여부\n
		 * @b COLUMN_SURVEY_ANSWERED_TS int 응답한시간\n
		 * @param hash
		 * @return
		 */
		public Cursor getSurveyInfo(String hash) {
			Cursor cursor = null;
			return cursor;			
		}
		
		public static final String COLUMN_SURVEY_NAME = "";
		public static final String COLUMN_SURVEY_HASH = "";
		public static final String COLUMN_SURVEY_OPEN_TS = "";
		public static final String COLUMN_SURVEY_CLOSE_TS = "";
		public static final String COLUMN_SURVEY_IS_CHECKED = "";
		public static final String COLUMN_SURVEY_IS_ANSWERED = "";
		public static final String COLUMN_SURVEY_ANSWERED_TS = "";
		public static final String COLUMN_SURVEY_CONTENT = "";
		public static final String COLUMN_SURVEY_CREATED_TS = "";
	}
	
	public class MemberProcManager {
		
		/**
		 * 멤버를 즐겨찾기에 추가
		 * @param hash
		 */
		public void addFavorite( String hash ) {
			
		}
		
		/**
		 * 멤버 여러명을 한 즐겨찾기 그룹으로 등록
		 * @param hash
		 */
		public void addFavorite( ArrayList<String> hashArray ) {
			
		}
		
		/**
		 * 즐겨찾기 멤버나 멤버그룹 이름 변경
		 * @param hash 즐겨찾기 멤버 해시나 그룹 해시
		 * @param title 변경할 이름
		 */
		public void updateFavoriteTitle( String hash, String title ) {
			
		}
		
		/**
		 * 해당 유저가 즐겨찾기에 있는지
		 * @b 커서구조
		 * @b COLUMN_IS_FAVORITE 즐겨찾기인지 아닌지  
		 * @param hash
		 * @return
		 */
		public boolean isUserFavorite(String hash) {
			
			return false;
		}
		
		/**
		 * 즐겨찾기 목록 가져옴
		 * @b 커서구조
		 * @b COLUMN_FAVORITE_HASH str 즐겨찾기 해쉬(유저면 유저해쉬 그룹이면 즐찾그룹해쉬)\n
		 * @b COLUMN_FAVORITE_NAME str 즐겨찾기 이름\n
		 * @b COLUMN_FAVORITE_IS_GROUP int 그룹인지 아닌지\n
		 * @return
		 */
		public Cursor getFavoriteList() {
			Cursor cursor = null;
			return cursor;
		}
		
		/**
		 * 즐겨찾기 그룹에 소속된 멤버들의 hash를 array로 리턴
		 * @b 커서구조
		 * @b COLUMN_USER_HASH 유저해쉬\n
		 * @param hash
		 * @return
		 */
		public Cursor getFavoriteGroupMemberList(String hash){
			Cursor cursor = null;
			return cursor;
		}
		
		public static final String COLUMN_FAVORITE_HASH = "";
		public static final String COLUMN_FAVORITE_NAME = "";
		public static final String COLUMN_FAVORITE_IS_GROUP = "";
		public static final String COLUMN_USER_HASH = "";
		public static final String COLUMN_IS_FAVORITE = "";
		
	}
}