package kr.go.KNPA.Romeo.Config;

import java.net.URL;

import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import android.content.Context;

/**
 * 서버와의 통신에 관련된 기본 설정 값들과 상수들\n
 * @author 최영우
 */
public class ConnectionConfig {
	//! 서버호스트
	public static final String SERVER_HOST = "http://116.67.94.11:80/";
	//! event request url
	public static final String REQUEST_URL = Constants.DEVELOPMENT == true ? 
												SERVER_HOST+"juliette/index.php/handler/call" : 
												SERVER_HOST+"juliet/index.php/handler/call";
	
	public static class UploadPath {
		
		//! 업로드된 파일 url
		public static final String BASE = Constants.DEVELOPMENT == true ? 
											SERVER_HOST+"juliette/uploaded/" : 
											SERVER_HOST+"juliet/uploaded/";
		
		//! 원본 크기의 프로필 사진이 저장되어 있는 곳
		public static final String PROFILE_IMG_ORIGINAL = BASE+"pic/profile/";
		//! 작은 크기의 프로필 사진이 저장되어 있는 곳
		public static final String PROFILE_IMG_SMALL = PROFILE_IMG_ORIGINAL+"small/";
		//! 중간 크기의 프로필 사진이 저장되어 있는 곳
		public static final String PROFILE_IMG_MEDIUM = PROFILE_IMG_ORIGINAL+"medium/";
		//! 채팅방 이미지 파일 원본 사이즈
		public static final String CHAT_IMG_ORIGINAL = BASE+"pic/chat/";
		//! 채팅방 이미지 파일 작은 사이즈
		public static final String CHAT_IMG_SMALL = CHAT_IMG_ORIGINAL+"small/";
		
	}
	
	public static final String HTTP_TYPE_GET = "GET";
	public static final String HTTP_TYPE_POST = "POST";
	
	public static final String CHARSET_REQUEST_ENCODING = "UTF-8"; 
	public static final String CHARSET_RESPONSE_ENCODING = "UTF-8";
	
	/**
	 * @name default connection setup
	 * @{
	 */
	public static String sUrl = REQUEST_URL;
	public static URL url = null;
	public static String accepts = MimeType.json;
	public static boolean async = true;
	public static CallbackEvent<Payload, Integer, Payload> callBack = new CallbackEvent<Payload, Integer, Payload>();
	public static String contentType = MimeType.json;
	public static Context context = null;
	public static String requestPayloadJSON = null;
	public static boolean fetchFile = false;
	public static int HTTPStatusCode = Constants.NOT_SPECIFIED;
	public static int timeout = 5000;
	public static String type = ConnectionConfig.HTTP_TYPE_POST;
	/**@}*/
}
