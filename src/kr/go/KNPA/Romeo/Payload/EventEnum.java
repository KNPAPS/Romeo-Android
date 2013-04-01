package kr.go.KNPA.Romeo.Payload;


/**
 * Event Enumeration Class
 */
public enum EventEnum {
	
	USER_REGISTER("USER:REGISTER"), /*!< 유저 등록 */  
	USER_GET_USER_INFO("USER:GET_USER_INFO"), /*!< 유저 정보 가져오기. 유저가 등록되어 있는 지 여부도 이 이벤트를 통해 확인 */
	USER_GET_DEPT_INFO("USER:GET_DEPT_INFO"), /*!< 부서 정보 가져오기. */
	USER_GET_CHILD_DEPTS("USER:GET_CHILD_DEPTS"),/*!< 하위 부서 가져오기 */
	USER_GET_MEMBERS("USER:GET_MEMBERS"), /*!< 부서 소속 멤버 목록 */
	USER_GET_PROFILE_IMG("USER:GET_PROFILE_IMG"), /*!<프로필 사진 가져오기 */
	USER_UPLOAD_PROFILE_IMG("USER:UPLOAD_PROFILE_IMG"),/*!< 프로필 사진 업로드 */
	MESSAGE_SEND("MESSAGE:SEND"),/*!<메세지 보내기*/
	MESSAGE_SET_CHECKED("MESSAGE:SET_CHECKCED"),/*!<메세지를 확인했을 때 호출*/
	MESSAGE_GET_UNCHECKERS("MESSAGE:GET_UNCHECKERS"),/*!<메세지 확인 안 한 사람 목록 */
	MESSAGE_SURVEY_ANSWER_SURVEY("MESSAGE:SURVEY:ANSWER_SURVEY"),/*!<설문조사 응답*/
	MESSAGE_SURVEY_GET_RESULT("MESSAGE:SURVEY:GET_RESULT"),/*!<설문조사 결과 가져오기*/
	DEVICE_REGISTER("DEVICE:REGISTER"),/*!<기기 정보 등록*/
	DEVICE_IS_REGISTERED("DEVICE:IS_REGISTERED"),/*!<기기가 등록되어 있는지, 활성화 되어 있는지 확인*/
	DEVICE_UNREGISTER("DEVICE:UNREGISTER")/*!<기기 등록 해제*/
	;
	
	private String event;
	private EventEnum( String eventString ) {
		this.event = eventString;
		
	}
	
	@Override
	public String toString() {
		return this.event;
	}
	
	/**
	 * event의 string value(ex. USER:REGISTER)를 입력받아 그에 해당하는 event 객체를 리턴
	 * \n만약 해당되는 event가 없으면 return null
	 * @param eventString
	 * @return 
	 */
	public static EventEnum findEvent(String eventString) {
		
		EventEnum[] events = EventEnum.values();
		
		for ( EventEnum e : events ) {
			if ( e.toString().equals(eventString) ) {
				return e;
			}
		}

		return null;
	}
}