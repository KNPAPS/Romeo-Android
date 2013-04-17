package kr.go.KNPA.Romeo.Config;


/**
 * Event Enumeration Class
 */
public class Event {
	
	public static final String USER_REGISTER="USER:REGISTER"; /*!< 유저 등록 */  
	public static final String USER_GET_USER_INFO="USER:GET_USER_INFO"; /*!< 유저 정보 가져오기. 유저가 등록되어 있는 지 여부도 이 이벤트를 통해 확인 */
	public static final String USER_GET_DEPT_INFO="USER:GET_DEPT_INFO"; /*!< 부서 정보 가져오기. */
	public static final String USER_GET_CHILD_DEPTS="USER:GET_CHILD_DEPTS";/*!< 하위 부서 가져오기 */
	public static final String USER_GET_MEMBERS="USER:GET_MEMBERS"; /*!< 부서 소속 멤버 목록 */
	public static final String USER_GET_PROFILE_IMG="USER:GET_PROFILE_IMG"; /*!<프로필 사진 가져오기 */
	public static final String USER_UPLOAD_PROFILE_IMG="USER:UPLOAD_PROFILE_IMG";/*!< 프로필 사진 업로드 */
	public static final String MESSAGE_SEND="MESSAGE:SEND";/*!<메세지 보내기*/
 
	public static final String MESSAGE_RECEIVED = "MESSAGE:RECEIVED";/*!<GCM으로부터 메세지를 받음*/
	public static final String MESSAGE_SET_CHECKED="MESSAGE:SET_CHECKCED";/*!<메세지를 확인했을 때 호출*/
	public static final String MESSAGE_GET_UNCHECKERS="MESSAGE:GET_UNCHECKERS";/*!<메세지 확인 안 한 사람 목록 */
	public static final String MESSAGE_CHAT_CREATE_ROOM = "MESSAGE:CHAT:CREATE_ROOM";
	public static final String MESSAGE_SURVEY_ANSWER_SURVEY="MESSAGE:SURVEY:ANSWER_SURVEY";/*!<설문조사 응답*/
	public static final String MESSAGE_SURVEY_GET_RESULT="MESSAGE:SURVEY:GET_RESULT";/*!<설문조사 결과 가져오기*/
	public static final String MESSAGE_SURVEY_GET_CONTENT="MESSAGE:SURVEY:GET_CONTENT";/*!<설문조사 상세 내용 가져오기*/
	
	public static final String MESSAGE_CHAT_PULL_LAST_READ_TS ="MESSAGE:CHAT:PULL_LAST_READ_TS";
	public static final String MESSAGE_CHAT_UPDATE_LAST_READ_TS ="MESSAGE:CHAT:UPDATE_LAST_READ_TS";

	
	public static final String DEVICE_REGISTER="DEVICE:REGISTER";/*!<기기 정보 등록*/
	public static final String DEVICE_IS_REGISTERED="DEVICE:IS_REGISTERED";/*!<기기가 등록되어 있는지, 활성화 되어 있는지 확인*/
	public static final String DEVICE_UNREGISTER="DEVICE:UNREGISTER";/*!<기기 등록 해제*/
	
	//! 유저 검색
	public static final String SEARCH_USER="SEARCH:USER";
	
	//! 이미지 파일 업로드
	public static final String UPLOAD_IMAGE="UPLOAD:IMAGE";
	
	
	public static String User()						{	return "USER";							}
	public static String Message()					{	return "MESSAGE";						}
	public static String Device()					{	return "DEVICE";						}
	public static String Search()					{	return "SEARCH";						}
	public static String Upload()					{	return "UPLOAD";						}
	
	public static class User {
		
		public static String register() 			{	return USER_REGISTER;					}
		public static String getUserInfo() 			{	return USER_GET_USER_INFO;				}
		public static String getDepartmentInfo()	{	return USER_GET_DEPT_INFO;				}
		public static String getChildDepartments()	{	return USER_GET_CHILD_DEPTS;			}
		public static String getMembers()			{	return USER_GET_MEMBERS;				}
		public static String getUserPic()			{	return USER_GET_PROFILE_IMG;			}
		public static String uploadUserPic()		{	return USER_UPLOAD_PROFILE_IMG;			}
	}
	
	public static class Message {
		public static String send()					{	return MESSAGE_SEND;					}
		public static String setChecked()			{	return MESSAGE_SET_CHECKED;				}
		public static String getUncheckers()		{	return MESSAGE_GET_UNCHECKERS;			}
		public static String received()				{	return MESSAGE_RECEIVED;				}
		
		public static String Survey()				{	return "SURVEY";						}
		public static class Survey {
			public static String answerSurvey()		{	return MESSAGE_SURVEY_ANSWER_SURVEY;	}
			public static String getResult()		{	return MESSAGE_SURVEY_GET_RESULT;		}
			public static String getContent()		{	return MESSAGE_SURVEY_GET_CONTENT;		}
		}
		public static class Chat {
			public static String createRoom()		{	return MESSAGE_CHAT_CREATE_ROOM;		}
			public static String pullLastReadTS()	{	return MESSAGE_CHAT_PULL_LAST_READ_TS;	}
			public static String updateLastReadTS()	{	return MESSAGE_CHAT_UPDATE_LAST_READ_TS;	}
		}
	}
	
	public static class Device {
		public static String register()				{	return DEVICE_REGISTER;					}
		public static String isRegistered()			{	return DEVICE_IS_REGISTERED;			}
		public static String unRegister()			{	return DEVICE_UNREGISTER;				}
	}
	
	public static class Search {
		public static String user()			{	return SEARCH_USER;				}		
	}
	
	public static class Upload {
		public static String image()		{	return UPLOAD_IMAGE;				}		
	}
}