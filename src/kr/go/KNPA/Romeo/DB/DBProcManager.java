package kr.go.KNPA.Romeo.DB;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;

/**
 * DB 프로시져 모음
 */
public class DBProcManager {
	//private DBManager dbm;
	
	/**
	 * @name Singleton
	 * @{
	 */
	private static DBProcManager _sharedManager;
	private ChatProcManager chat;
	private DocumentProcManager document;
	private SurveyProcManager survey;
	
	private DBProcManager(Context context) {
		//TODO 접근가능한 DB가 있는지 확인
		//this.dbm = new DBManager(context);
	}
	
	public static DBProcManager sharedManager(Context context) {
		if(_sharedManager == null)
			_sharedManager = new DBProcManager(context);
		return _sharedManager;
	}
	
	public ChatProcManager chat() {
		if(chat == null)
			chat = new ChatProcManager();
		return  chat; 
	}
	
	public DocumentProcManager document() {
		if(document == null)
			document = new DocumentProcManager();
		return document; 
	}
	
	public SurveyProcManager survey() {
		if(survey == null)
			survey = new SurveyProcManager();
		return survey; 
	}
	/** @} */
	
	public class ChatProcManager {
		/**
		 * 채팅 전송 시 메세지 내용 저장 
		 * @param roomHash 채팅방 해쉬
		 * @param chatHash 서버에서 부여한 채팅의 hash
		 * @param senderHash 보내는 사람 
		 * @param content 채팅 내용
		 * @param createdTS 채팅을 보낸 타임스탬프
		 */
		public void saveChatOnSend(String roomHash, String chatHash, String senderHash, String content, long createdTS) {
			
		}
		
		/**
		 * 채팅 수신 시 메세지 내용 저장
		 * @param roomHash 채팅방 해쉬
		 * @param chatHash 서버에서 부여한 채팅의 hash
		 * @param senderHash 보내는 사람 
		 * @param content 채팅 내용
		 * @param createdTS 채팅을 받은 타임스탬프
		 */
		public void saveChatOnReceived(String roomHash, String chatHash, String senderHash, String content, long createdTS) {
			
		}
		
		/**
		 * 채팅 메세지를 확인했을 때
		 * @param chatHash 채팅 해쉬
		 * @param checkedTS 체크한 시간
		 */
		public void updateCheckedTS( String chatHash, long checkedTS ) {
			
		}
		
		/**
		 * 유저가 채팅방에 들어가 메세지들을 확인했을 때의 시간을 기록함 
		 * @param roomHash 채팅방 해쉬
		 * @param userHash lastReadTS를 수정할 유저해쉬
		 */
		public void updateLastReadTS( String roomHash, String userHash, long lastReadTS ) {
			
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
			Cursor cursor = null;
			return cursor;
		}
		
		/**
		 * 채팅을 전송할 때 채팅방에 있는 사람들의 목록을 리턴
		 * @b 커서구조
		 * @b COLUMN_USER_HASH 리시버 해쉬
		 * @param hash 채팅방 해쉬
		 * @return
		 */
		public Cursor getReceiverList( String hash ) {
			Cursor cursor = null;
			return cursor;
		}
		
		/**
		 * 채팅방 내의 채팅 목록 불러오기
		 * @b 커서구조
		 * @b COLUMN_CHAT_SENDER_HASH \n
		 * @b COLUMN_CHAT_TS \n
		 * @b COLUMN_CHAT_NUM_UNCHECKERS \n
		 * @b COLUMN_CHAT_CONTENT 내용\n
		 * @b COLUMN_CHAT_CONTENT_TYPE 내용의 종류 @see{CHAT_CONTENT_TYPE_TEXT} @see{CHAT_CONTENT_TYPE_PICTURE}\n
		 * @param roomHash
		 * @param TS 역순으로 정렬시 불러올 목록 시작 index
		 * @param 불러올 채팅의 개수
		 * @return
		 */
		public Cursor getChatList(String roomHash, int start, int count) {
			Cursor cursor = null;
			return cursor;
		}
		
		public static final String COLUMN_ROOM_HASH = "room_hash";
		public static final String COLUMN_ROOM_TITLE = "room_hash";
		public static final String COLUMN_ROOM_NUM_CHATTER = "room_hash";
		public static final String COLUMN_ROOM_NUM_NEW_CHAT = "";
		public static final String COLUMN_ROOM_LAST_CHAT_TS = "";
		public static final String COLUMN_ROOM_LAST_CHAT_CONTENT = "";
		public static final String COLUMN_USER_HASH = "user_hash";
		public static final String COLUMN_CHAT_SENDER_HASH = "";
		public static final String COLUMN_CHAT_TS = "";
		public static final String COLUMN_CHAT_NUM_UNCHECKERS = "";
		public static final String COLUMN_CHAT_CONTENT = "";
		public static final String COLUMN_CHAT_CONTENT_TYPE = "";
		
		public static final int CHAT_CONTENT_TYPE_TEXT = 1;
		public static final int CHAT_CONTENT_TYPE_PICTURE = 2;
		
	}

	public class DocumentProcManager {
		/**
		 * 문서를 자신이 만들어서 보낼 때 문서 내용 저장
		 * @param docHash 서버가 부여한 문서 해쉬
		 * @param senderHash 보내는 사람 해쉬 (자기자신)
		 * @param title 문서 제목
		 * @param content 문서 내용
		 * @param createdTS 문서를 보낸 TS
		 * @param files 첨부파일정보. \n @see {Document.ATTACH_FILE_URL}, @see {Document.ATTACH_FILE_NAME}, @see {Document.ATTACH_FILE_TYPE}, @see {Document.ATTACH_FILE_SIZE} 가 key로 설정되어야함
		 */
		public void saveDocumentOnSend(String docHash, String senderHash, String title, String content, long createdTS, ArrayList<HashMap<String, String>> files) {
			
		}
		
		/**
		 * 문서를 포워딩했을 때 정보 추가
		 * @param docHash
		 * @param forwarderHash
		 * @param forwardComment
		 * @param forwardTS
		 */
		public void addForwardToDocument(String docHash, String forwarderHash, String forwardComment, long forwardTS) {
			
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
		public void saveDocumentOnReceived(String docHash, String senderHash, String title, String content, long createdTS, ArrayList<HashMap<String, String>> files) {
			
		}
		
		/**
		 * 문서를 즐겨찾기에 추가
		 * @param hash
		 */
		public void addFavorite( String hash ) {
			
		}
		
		/**
		 * 문서를 확인했을 때
		 * @param docHash 채팅 해쉬
		 * @param checkedTS 체크한 시간
		 */
		public void updateCheckedTS( String docHash, long checkedTS ) {
			
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
			Cursor cursor = null;
			return cursor;
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
			Cursor cursor = null;
			return cursor;			
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
			Cursor cursor = null;
			return cursor;
		}
		
		/**
		 * 문서의 첨부파일 정보
		 * @b 커서구조
		 * @b COLUMN_FILE_NAME str 파일이름\n
		 * @b COLUMN_FILE_TYPE int 파일종류\n
		 * @b COLUMN_FILE_SIZE int 파일사이즈 in byte\n
		 * @param docHash
		 * @return
		 */
		public Cursor getDocumentAttachment(String docHash) {
			Cursor cursor = null;
			return cursor;
		}
		
		public static final String COLUMN_DOC_HASH = "";
		public static final String COLUMN_DOC_TITLE = "";
		public static final String COLUMN_IS_CHECKED = "";
		public static final String COLUMN_SENDER_HASH = "";
		public static final String COLUMN_CREATED_TS = "";
		public static final String COLUMN_FORWARDER_HASH = "";
		public static final String COLUMN_FORWARD_COMMENT = "";
		public static final String COLUMN_FORWARD_TS = "";
		public static final String COLUMN_FILE_NAME = "";
		public static final String COLUMN_FILE_TYPE = "";
		public static final String COLUMN_FILE_SIZE = "";
	}

	public class SurveyProcManager {
		/**
		 * 설문조사를 보내거나 받았을 때 기본 정보 저장
		 * @param surveyHash 서버가 부여한 설문조사 해쉬
		 * @param title 설문조사 제목
		 * @param content 설문조사 설명
		 * @param creatorHash 설문조사 만든사람 해쉬
		 * @param createdTS 설문조사 만든 시간 TS
		 */
		public void saveSurvey(String surveyHash,String title, String content, String creatorHash, long createdTS) {
			
		}
		
		/**
		 * 설문조사를 확인했을 때
		 * @param svyHash 채팅 해쉬
		 * @param checkedTS 체크한 시간
		 */
		public void updateCheckedTS( String svyHash, long checkedTS ) {
			
		}
		
		/**
		 * 설문조사를 즐겨찾기에 추가
		 * @param hash
		 */
		public void addFavorite( String hash ) {
			
		}
		
		/**
		 * 설문조사 목록 가져오기
		 * @b 커서구조
		 * @b COLUMN_SURVEY_HASH str 해시\n
		 * @b COLUMN_SURVEY_NAME str 설문제목\n
		 * @b COLUMN_SURVEY_OPEN_TS int open ts\n
		 * @b COLUMN_SURVEY_CLOSE_TS int close ts\n
		 * @b COLUMN_SURVEY_IS_CHECKED int 확인여부\n
		 * @b COLUMN_SURVEY_IS_ANSWERED int 대답여부\n
		 * @param svyCategory 
		 * @return
		 */
		public Cursor getSurveyList(int svyCategory) {
			Cursor cursor = null;
			return cursor;
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
		
	}
}