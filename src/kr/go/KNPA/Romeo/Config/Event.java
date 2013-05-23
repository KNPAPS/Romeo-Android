package kr.go.KNPA.Romeo.Config;

/**
 * Event Enumeration Class
 */
public class Event {

	public static final String	USER_REGISTER						= "USER:REGISTER";
	public static final String	USER_GET_USER_INFO					= "USER:GET_USER_INFO";
	public static final String	USER_GET_DEPT_INFO					= "USER:GET_DEPT_INFO";
	public static final String	USER_GET_CHILD_DEPTS				= "USER:GET_CHILD_DEPTS";
	public static final String	USER_GET_MEMBERS					= "USER:GET_MEMBERS";
	public static final String	USER_GET_PROFILE_IMG				= "USER:GET_PROFILE_IMG";
	public static final String	USER_UPLOAD_PROFILE_IMG				= "USER:UPLOAD_PROFILE_IMG";

	public static final String	MESSAGE_SEND						= "MESSAGE:SEND";
	public static final String	MESSAGE_RECEIVED					= "MESSAGE:RECEIVED";
	public static final String	MESSAGE_SET_CHECKED					= "MESSAGE:SET_CHECKED";
	public static final String	MESSAGE_GET_UNCHECKERS				= "MESSAGE:GET_UNCHECKERS";
	public static final String	MESSAGE_SURVEY_ANSWER_SURVEY		= "MESSAGE:SURVEY:ANSWER_SURVEY";
	public static final String	MESSAGE_SURVEY_GET_RESULT			= "MESSAGE:SURVEY:GET_RESULT";
	public static final String	MESSAGE_SURVEY_GET_CONTENT			= "MESSAGE:SURVEY:GET_CONTENT";

	public static final String	MESSAGE_CHAT_CREATE_ROOM			= "MESSAGE:CHAT:CREATE_ROOM";
	public static final String	MESSAGE_CHAT_PULL_LAST_READ_TS		= "MESSAGE:CHAT:PULL_LAST_READ_TS";
	public static final String	MESSAGE_CHAT_NOTIFY_LAST_READ_TS	= "MESSAGE:CHAT:NOTIFY_LAST_READ_TS";
	public static final String	MESSAGE_CHAT_LEAVE_ROOM				= "MESSAGE:CHAT:LEAVE_ROOM";
	public static final String	MESSAGE_CHAT_INVITE					= "MESSAGE:CHAT:INVITE";

	public static final String	PUSH_USER_LEAVE_ROOM				= "PUSH:USER_LEAVE_ROOM";
	public static final String	PUSH_USER_JOIN_ROOM					= "PUSH:USER_JOIN_ROOM";
	public static final String	PUSH_UPDATE_LAST_READ_TS			= "PUSH:UPDATE_LAST_READ_TS";

	public static final String	DEVICE_REGISTER						= "DEVICE:REGISTER";
	public static final String	DEVICE_IS_REGISTERED				= "DEVICE:IS_REGISTERED";
	public static final String	DEVICE_UNREGISTER					= "DEVICE:UNREGISTER";

	public static final String	SEARCH_USER							= "SEARCH:USER";

	public static final String	UPLOAD_IMAGE						= "UPLOAD:IMAGE";

	public static String User()
	{
		return "USER";
	}

	public static String Message()
	{
		return "MESSAGE";
	}

	public static String Device()
	{
		return "DEVICE";
	}

	public static String Search()
	{
		return "SEARCH";
	}

	public static String Upload()
	{
		return "UPLOAD";
	}

	public static class User {

		public static String register()
		{
			return USER_REGISTER;
		}

		public static String getUserInfo()
		{
			return USER_GET_USER_INFO;
		}

		public static String getDepartmentInfo()
		{
			return USER_GET_DEPT_INFO;
		}

		public static String getChildDepartments()
		{
			return USER_GET_CHILD_DEPTS;
		}

		public static String getMembers()
		{
			return USER_GET_MEMBERS;
		}

		public static String getUserPic()
		{
			return USER_GET_PROFILE_IMG;
		}

		public static String uploadUserPic()
		{
			return USER_UPLOAD_PROFILE_IMG;
		}
	}

	public static class Message {
		public static String send()
		{
			return MESSAGE_SEND;
		}

		public static String setChecked()
		{
			return MESSAGE_SET_CHECKED;
		}

		public static String getUncheckers()
		{
			return MESSAGE_GET_UNCHECKERS;
		}

		public static String received()
		{
			return MESSAGE_RECEIVED;
		}

		public static String Survey()
		{
			return "SURVEY";
		}

		public static class Survey {
			public static String answerSurvey()
			{
				return MESSAGE_SURVEY_ANSWER_SURVEY;
			}

			public static String getResult()
			{
				return MESSAGE_SURVEY_GET_RESULT;
			}

			public static String getContent()
			{
				return MESSAGE_SURVEY_GET_CONTENT;
			}
		}

		public static class Chat {
			public static String createRoom()
			{
				return MESSAGE_CHAT_CREATE_ROOM;
			}

			public static String pullLastReadTS()
			{
				return MESSAGE_CHAT_PULL_LAST_READ_TS;
			}

			public static String notifyLastReadTS()
			{
				return MESSAGE_CHAT_NOTIFY_LAST_READ_TS;
			}

			public static String leaveRoom()
			{
				return MESSAGE_CHAT_LEAVE_ROOM;
			}

			public static String joinRoom()
			{
				return MESSAGE_CHAT_INVITE;
			}

		}
	}

	public static class Device {
		public static String register()
		{
			return DEVICE_REGISTER;
		}

		public static String isRegistered()
		{
			return DEVICE_IS_REGISTERED;
		}

		public static String unRegister()
		{
			return DEVICE_UNREGISTER;
		}
	}

	public static class Search {
		public static String user()
		{
			return SEARCH_USER;
		}
	}

	public static class Upload {
		public static String image()
		{
			return UPLOAD_IMAGE;
		}
	}
}