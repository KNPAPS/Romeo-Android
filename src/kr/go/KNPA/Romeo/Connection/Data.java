package kr.go.KNPA.Romeo.Connection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 데이터 Entry들의 배열
 */
public class Data extends ArrayList<HashMap<String,Object>> {

	/**
	 * index번째 HashMap에 key,value 페어 삽입.\n
	 * 만약 index번째가 없다면 새로 생성하여 삽입한다.
	 * @param index arraylist의 인덱스
	 * @param key hashmap key
	 * @param value hashmap value object
	 */
	public Data add( int index, String key, Object value ) {
		
		if ( this.get(index) != null ) {
			this.get(index).put(key, value);
		} else {
			HashMap<String,Object> hm = new HashMap<String,Object>();
			hm.put(key,value);
			this.add(index,hm);
		}
		
		return this;
	}
	
	/**
	 * index번째 HashMap의 해당 key를 가진 Object 리턴.\n
	 * 만약 없으면 null 리턴 
	 * @param index
	 * @param key
	 * @return obj object or null
	 */
	public Object get( int index, String key ) {
		return this.get(index) != null ? this.get(index).get(key) : null;
	}
	
	//! 객체 구분용 ID
	/**
	 *
	 * Data라는 이름을 가진 객체가 여럿 있어서 그런지 구별할 UID를 지정하라고 떠서\n
	 * 걍 지정 해 놓은 상수 
	 */
	private static final long serialVersionUID = 1L;
	
	//! String 유저해쉬
	public static final String KEY_USER_HASH = "user_hash"; 
	//! String 유저네임
	public static final String KEY_USER_NAME = "user_name"; 
	//! int 유저 계급 
	public static final String KEY_USER_RANK = "user_rank";
	//!  
	public static final String KEY_USER_ROLE = "user_role";
	//!  
	public static final String KEY_IS_ENABLED = "is_enabled";
	//!  
	public static final String KEY_PROFILE_PIC_FILENAME = "pic_filename";
	//!  
	public static final String KEY_DEPT_HASH = "dept_hash";
	//!  
	public static final String KEY_DEPT_NAME = "dept_name";
	//!  
	public static final String KEY_DEPT_FULL_NAME = "dept_full_name";
	//!  
	public static final String KEY_DEPT_PARENT_HASH = "parent_hash";
	//!  
	public static final String KEY_SENDER_HASH	 = "sender_hash";
	//!  
	public static final String KEY_RECEIVER_HASH = "receiver_hash";
	//!  
	public static final String KEY_ROOM_HASH = "room_hash";
	//!  
	public static final String KEY_MESSAGE = "message";
	//!  
	public static final String KEY_MESSAGE_TYPE = "type";
	//!  
	public static final String KEY_MESSAGE_TITLE = "title";
	//!  
	public static final String KEY_MESSAGE_CONTENT = "content";
	//!  
	public static final String KEY_MESSAGE_APPENDIX = "appendix";
	//! HashMap GCM SendResult 
	public static final String KEY_GCM_SEND_RESULT = "sendResultFromGCM";
	//!  
	public static final String KEY_GCM_MULTICAST_ID = "multicast_id";
	//! int 전송성공 수  
	public static final String KEY_GCM_SUCCESS = "success";
	//! int 전송실패 수 
	public static final String KEY_GCM_FAILURE = "failure";
	//! 
	public static final String KEY_GCM_CANONICAL_IDS = "canonical_ids";
	//! String GCM 결과  
	public static final String KEY_GCM_RESULTS = "results";
	//! String gcm message id
	public static final String KEY_GCM_MESSAGE_ID = "message_id";
	//!  String 새 GCM id 
	public static final String KEY_GCM_NEW_REGID = "registration_id";
	//! String GCM 에러 메세지 
	public static final String KEY_GCM_ERROR = "error";
	//! Array of Strings 메세지 수신자 해쉬 배열  
	public static final String KEY_MESSAGE_RECEIVERS_IDS = "receiversIds";
	//! Array of Strings gcm registration ids 
	public static final String KEY_REG_IDS = "regIds";
	//! 메세지(챗,DOC,SVY) 해쉬 
	public static final String KEY_MSG_HASH = "msg_hash";
	//! String 에러 메세지 또는 상태 메세지
	public static final String KEY_RESULT_MSG = "msg";
	//! String unique id 
	public static final String KEY_DEVICE_UUID = "uuid";
	//! String gcm registration id 
	public static final String KEY_DEVICE_REG_ID = "regid";
	//! char device type  
	public static final String KEY_DEVICE_TYPE = "dev_type";
	//! boolean 등록여부 
	public static final String KEY_DEVICE_IS_REGISTERED = "isRegistered";
	//! boolean 활성화여부
	public static final String KEY_DEVICE_IS_ENABLED = "isEnabled";
	//! 하위 부서를 전부 가져오는지
	public static final String KEY_GET_MEMBER_FETCH_ALL = "fetch_all";
	
	public static final String KEY_FILE = "fetched_file";
}
