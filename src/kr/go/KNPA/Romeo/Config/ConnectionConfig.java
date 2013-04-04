package kr.go.KNPA.Romeo.Config;

import java.net.URL;

import kr.go.KNPA.Romeo.Connection.ConnectionCallback;
import android.content.Context;

/**
 * 서버와의 통신에 관련된 기본 설정 값들과 상수들\n
 * @author 최영우
 */
public class ConnectionConfig {
	//protected static final static String HOST_URL = "http://116.67.94.11:9876/index.php/handler/call";
	public static final String SERVER_HOST = "http://localhost";
	public static final String REQUEST_URL = SERVER_HOST+"/juliet/index.php/handler/call";
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
	public static ConnectionCallback callBack = new ConnectionCallback();
	public static String contentType = MimeType.json;
	public static Context context = null;
	public static String requestPayloadJSON = null;
	public static String dataType = MimeType.json;
	public static int HTTPStatusCode = Constants.NOT_SPECIFIED;
	public static int timeout = 10000;
	public static String type = ConnectionConfig.HTTP_TYPE_POST;
	/**@}*/
}
