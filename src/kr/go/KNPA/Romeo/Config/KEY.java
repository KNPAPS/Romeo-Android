package kr.go.KNPA.Romeo.Config;

/**
 * 서버와의 인터페이스에 쓰이는 KEY 값들 모음
 * payload의 data에 들어가는 객체들의 멤버변수명이거나\n
 * 해쉬맵의 Key 값임.
 */
public class KEY {
	
	public static final String RESPONSE_TEXT = "responseText";
	
	public static class MESSAGE {
		public static final String IDX = "idx";
		public static final String TYPE = "type";
		public static final String CONTENT = "content";
		public static final String SENDER = "sender";
		public static final String RECEIVERS = "receivers";
		public static final String CREATED_TS = "TS";
		public static final String IS_CHECKED = "checked";
		public static final String CHECK_TS = "checkTS";
		public static final String RECEIVERS_IDX = "receiversIdx";
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
		public static final String IDX = "idx";
		public static final String TYPE = "type";
		public static final String CONTENT = "content";
		public static final String SENDER = "sender";
		public static final String RECEIVERS = "receivers";
		public static final String CREATED_TS = "TS";
		public static final String IS_CHECKED = "checked";
		public static final String CHECK_TS = "checkTS";
		public static final String ROOM_CODE = "roomCode";
		public static final String CONTENT_TYPE = "contentType";
	}

	public static class DOCUMENT {
		public static final String IDX = "idx";
		public static final String TYPE = "type";
		public static final String TITLE = "title";
		public static final String CONTENT = "content";
		public static final String SENDER = "sender";
		public static final String RECEIVERS = "receivers";
		public static final String CREATED_TS = "TS";
		public static final String IS_CHECKED = "checked";
		public static final String CHECK_TS = "checkTS";
		
		public static final String FORWARDS = "forwards";
		public static final String FORWARDER_IDX = "forwarderIdx";
		public static final String FORWARD_TS = "forwardTS";
		public static final String FORWARD_CONTENT = "forwardContent";
		
		public static final String FILES = "FILES";
		public static final String FILE_IDX = "fileIdx";
		public static final String FILE_NAME = "fileName";
		public static final String FILE_TYPE = "fileType";
		public static final String FILE_SIZE = "fileSize";
		
		public static final String IS_FAVORITE = "isFavorite";
	}
	
	public static class SURVEY {
		public static final String IDX = "idx";
		public static final String TYPE = "type";
		public static final String CONTENT = "content";
		public static final String TITLE = "title";
		public static final String SENDER = "sender";
		public static final String RECEIVERS = "receivers";
		public static final String CREATED_TS = "TS";
		public static final String IS_CHECKED = "checked";
		public static final String CHECK_TS = "checkTS";
		
		public static final String OPEN_TS = "openTS";
		public static final String CLOSE_TS = "closeTS";
		
		public static final String FORM = "form";
		public static final String QUESTIONS = "questions";
		public static final String IS_MULTIPLE = "isMultiple";
		public static final String QUESTION_TITLE = "questionTitle";
		public static final String QUESTION_CONTENT = "questionContent";
		public static final String OPTIONS = "options";
	}
	
	public static class USER {
		public static final String IDX = "idx";
		public static final String NAME = "name";
		public static final String ROLE = "role";
		public static final String RANK = "rank";
		public static final String IS_ENABLED = "isUserEnabled";
	}
	
	public static class DEPT {
		public static final String IDX = "idx";
		public static final String SEQUENCE = "deptCode";
		public static final String NAME = "name";
		public static final String FULL_NAME = "nameFull";
		public static final String PARENT_IDX = "parentIdx";
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
}
