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
	public static final String MESSAGE_SET_CHECKED="MESSAGE:SET_CHECKCED";/*!<메세지를 확인했을 때 호출*/
	public static final String MESSAGE_GET_UNCHECKERS="MESSAGE:GET_UNCHECKERS";/*!<메세지 확인 안 한 사람 목록 */
	public static final String MESSAGE_SURVEY_ANSWER_SURVEY="MESSAGE:SURVEY:ANSWER_SURVEY";/*!<설문조사 응답*/
	public static final String MESSAGE_SURVEY_GET_RESULT="MESSAGE:SURVEY:GET_RESULT";/*!<설문조사 결과 가져오기*/
	public static final String DEVICE_REGISTER="DEVICE:REGISTER";/*!<기기 정보 등록*/
	public static final String DEVICE_IS_REGISTERED="DEVICE:IS_REGISTERED";/*!<기기가 등록되어 있는지, 활성화 되어 있는지 확인*/
	public static final String DEVICE_UNREGISTER="DEVICE:UNREGISTER";/*!<기기 등록 해제*/
	;
	
}