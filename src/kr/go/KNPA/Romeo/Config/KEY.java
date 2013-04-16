package kr.go.KNPA.Romeo.Config;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Config.Event.User;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.Survey.Form.Question;


/**
 * 서버와의 인터페이스에 쓰이는 KEY 값들 모음
 * payload의 data에 들어가는 객체들의 멤버변수명이거나\n
 * 해쉬맵의 Key 값임.
 */
public class KEY {
	
	public static final String RESPONSE_TEXT = "responseText";
	public static final String _MESSAGE = "message";
	
	public static class MESSAGE {
		public static final String IDX = Message.class.getSimpleName()+"_idx";
		public static final String TYPE = Message.class.getSimpleName()+"_type";
		public static final String TITLE = Message.class.getSimpleName()+"_title";
		public static final String CONTENT = Message.class.getSimpleName()+"_content";
		public static final String SENDER_IDX = Message.class.getSimpleName()+"_senderIdx";
		public static final String CREATED_TS = Message.class.getSimpleName()+"_TS";
		public static final String IS_CHECKED = Message.class.getSimpleName()+"_checked";
		public static final String CHECK_TS = Message.class.getSimpleName()+"_checkTS";
		public static final String RECEIVERS_IDX = Message.class.getSimpleName()+"_receiversIdx";
		public static final String RECEIVERS_REGISTRATION_IDS = "receiversRegistrationIds";
		
	}
	
	public static class GCM {
		public static final String SEND_RESULT = "sendResultFromGCM";
		public static final String MULTICAST_ID = "multicast_id";
		public static final String SUCCESS = "success";
		public static final String FAILURE = "failure";
		public static final String CANONICAL_IDS = "canonical_ids";
		public static final String RESULTS = "results";
		public static final String RESULTS_MESSAGE_ID = "message_id";
		public static final String RESULTS_NEW_REGISTRATION_ID = "registration_id";
		public static final String RESULTS_ERROR = "error";
		
	}
	
	public static class CHAT {
		public static final String IDX = Chat.class.getSimpleName()+"_idx";
		public static final String TYPE = Chat.class.getSimpleName()+"_type";
		public static final String CONTENT = Chat.class.getSimpleName()+"_content";
		public static final String SENDER_IDX = Chat.class.getSimpleName()+"_senderIdx";
		public static final String RECEIVERS_IDX = Chat.class.getSimpleName()+"_receiversIdx";
		public static final String CREATED_TS = Chat.class.getSimpleName()+"_TS";
		public static final String IS_CHECKED = Chat.class.getSimpleName()+"_checked";
		public static final String CHECK_TS = Chat.class.getSimpleName()+"_checkTS";
		public static final String ROOM_CODE = Chat.class.getSimpleName()+"_roomCode";
		public static final String CONTENT_TYPE = Chat.class.getSimpleName()+"_contentType";
		public static final String IMAGE_SIZE = "imageSize";
	}

	public static class DOCUMENT {
		public static final String IDX = Document.class.getSimpleName()+"_idx";
		public static final String TYPE = Document.class.getSimpleName()+"_type";
		public static final String TITLE = Document.class.getSimpleName()+"_title";
		public static final String CONTENT = Document.class.getSimpleName()+"_content";
		public static final String SENDER_IDX = Document.class.getSimpleName()+"_senderIdx";
		public static final String RECEIVERS_IDX = Document.class.getSimpleName()+"_receiversIdx";
		public static final String CREATED_TS = Document.class.getSimpleName()+"_TS";
		public static final String IS_CHECKED = Document.class.getSimpleName()+"_checked";
		public static final String CHECK_TS = Document.class.getSimpleName()+"_checkTS";
		
		public static final String FORWARDS = Document.class.getSimpleName()+"_forwards";
		public static final String FORWARDER_IDX = "forwarderIdx";
		public static final String FORWARD_TS = "forwardTS";
		public static final String FORWARD_CONTENT = "forwardContent";
		
		public static final String FILES = Document.class.getSimpleName()+"_files";
		public static final String FILE_IDX = "fileIdx";
		public static final String FILE_NAME = "fileName";
		public static final String FILE_TYPE = "fileType";
		public static final String FILE_SIZE = "fileSize";
		
		public static final String IS_FAVORITE = Document.class.getSimpleName()+"_isFavorite";
	}
	
	public static class SURVEY {
		public static final String IDX = Survey.class.getSimpleName()+"_idx";
		public static final String TYPE = Survey.class.getSimpleName()+"_type";
		public static final String CONTENT = Survey.class.getSimpleName()+"_content";
		public static final String TITLE = Survey.class.getSimpleName()+"_title";
		public static final String SENDER_IDX = Survey.class.getSimpleName()+"_senderIdx";
		public static final String RECEIVERS_IDX = Survey.class.getSimpleName()+"_receiversIdx";
		public static final String CREATED_TS = Survey.class.getSimpleName()+"_TS";
		public static final String IS_CHECKED = Survey.class.getSimpleName()+"_checked";
		public static final String CHECK_TS = Survey.class.getSimpleName()+"_checkTS";
		
		public static final String OPEN_TS = Survey.class.getSimpleName()+"_openTS";
		public static final String CLOSE_TS = Survey.class.getSimpleName()+"_closeTS";
		
		public static final String FORM = Survey.class.getSimpleName()+"_form";
		public static final String QUESTIONS = "questions";
		public static final String IS_MULTIPLE = Survey.class.getSimpleName()+"_isMultiple";
		public static final String QUESTION_TITLE = Question.class.getSimpleName()+"_title";
		public static final String QUESTION_CONTENT = Question.class.getSimpleName()+"_content";
		public static final String OPTIONS = Question.class.getSimpleName()+"_options";
		
		public static final String ANSWER_SHEET = "answerSheet";
		
		public static final String NUM_RECEIVERS = "numReceivers";
		public static final String NUM_UNCHECKERS = "numUnCheckers";
		public static final String NUM_CHECKERS = "numCheckers";
		public static final String NUM_RESPONDERS = "numResponders";
		public static final String NUM_GIVE_UP = "numGiveUp";
		public static final String RESULT = "result";
		
	}
	
	public static class USER {
		public static final String IDX = User.class.getSimpleName()+"_idx";
		public static final String NAME = User.class.getSimpleName()+"_name";
		public static final String ROLE = User.class.getSimpleName()+"_role";
		public static final String RANK = User.class.getSimpleName()+"_rank";
		
		public static final String IS_ENABLED = "isUserEnabled";
		public static final String PROFILE_IMAGE_SIZE = "profileImgSize";
	}
	
	public static class DEPT {
		public static final String IDX = Department.class.getSimpleName()+"_idx";
		public static final String SEQUENCE = Department.class.getSimpleName()+"_deptCode";
		public static final String NAME = Department.class.getSimpleName()+"_name";
		public static final String FULL_NAME = Department.class.getSimpleName()+"_nameFull";
		public static final String PARENT_IDX = Department.class.getSimpleName()+"_parentIdx";
		public static final String FETCH_RECURSIVE = "fetchRecursive";
	}
	
	public static class DEVICE {
		public static final String IDX = "deviceIdx";
		public static final String UUID = "uuid";
		public static final String GCM_REGISTRATION_ID = "GCMRegistrationId";
		public static final String TYPE = "deviceType";
		public static final String IS_REGISTERED = "isDeviceRegistered";
		public static final String IS_ENABLED = "isDeviceEnabled";
	}
	
	public static class SEARCH {
		public static final String QUERY = "query";
	}
	
	public static class UPLOAD {
		public static final String FILE_IDX = "fileHash";
		public static final String FILE_TYPE = "fileType";
	}
}
