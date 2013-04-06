package kr.go.KNPA.Romeo.DB;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;

/**
 * DB 프로시져 모음
 */
@SuppressWarnings("unused")
public class DBProcManager {
	
	public ChatProcManager chat;
	public DocumentProcManager document;
	public SurveyProcManager survey;
	//private DBManager dbm;
	
	public DBProcManager(Context context) {
		
		//TODO 접근가능한 DB가 있는지 확인
		//this.dbm = new DBManager(context);
		this.chat = new DBProcManager.ChatProcManager();
		this.document = new DBProcManager.DocumentProcManager();
		this.survey = new DBProcManager.SurveyProcManager();
	}
	
	private class ChatProcManager {
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
		 * @param createdTS 채팅을 보낸 타임스탬프
		 * @param checkedTS 채팅을 확인한 타임스탬프
		 */
		public void saveChatOnReceived(String roomHash, String chatHash, String senderHash, String content, long createdTS, long checkedTS) {
			
		}
		
		/**
		 * 채팅방 목록에 대한 정보를 담고 있는 커서를 반환
		 * @param roomType
		 * @return 
		 */
		public Cursor getRoomList(int roomType) {
			Cursor cursor = null;
			return cursor;
		}
		
		/**
		 * 채팅방 내의 채팅 목록 불러오기
		 * @param roomHash
		 * @return
		 */
		public Cursor getChatList(String roomHash) {
			Cursor cursor = null;
			return cursor;
		}
		
		public static final String COLUMN_ROOM_HASH = "room_hash";
	}

	private class DocumentProcManager {
		/**
		 * 문서를 자신이 만들어서 보낼 때 문서 내용 저장
		 * @param docHash 서버가 부여한 문서 해쉬
		 * @param senderHash 보내는 사람 해쉬 (자기자신)
		 * @param title 문서 제목
		 * @param content 문서 내용
		 * @param createdTS 문서를 만든 시점의 타임스탬프
		 * @param files 첨부파일정보. \n @see {Document.ATTACH_FILE_URL}, @see {Document.ATTACH_FILE_NAME}, @see {Document.ATTACH_FILE_TYPE}, @see {Document.ATTACH_FILE_SIZE} 가 key로 설정되어야함
		 */
		public void saveDocumentOnCreate(String docHash, String senderHash, String title, String content, long createdTS, ArrayList<HashMap<String, String>> files) {
			
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
		
		public Cursor getDocumentList(int docCategory) {
			Cursor cursor = null;
			return cursor;
		}
		
		public Cursor getDocumentContent(String docHash) {
			Cursor cursor = null;
			return cursor;			
		}
	}

	private class SurveyProcManager {
		/**
		 * 설문조사를 받았을 때 기본 정보 저장
		 * @param surveyHash 서버가 부여한 설문조사 해쉬
		 * @param title 설문조사 제목
		 * @param content 설문조사 설명
		 * @param creatorHash 설문조사 만든사람 해쉬
		 * @param createdTS 설문조사 만든 시간 TS
		 */
		public void saveSurveyOnReceived(String surveyHash,String title, String content, String creatorHash, long createdTS) {
			
		}
		
		
	}
	
	

}