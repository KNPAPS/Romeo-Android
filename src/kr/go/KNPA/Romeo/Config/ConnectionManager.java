package kr.go.KNPA.Romeo.Config;

/**
 * 서버와의 통신에 관련된 설정 값들
 * @author 최영우
 */
public class ConnectionManager {
	//private final static String HOST_URL = "http://116.67.94.11:9876/index.php/handler/call";
	protected static final String SERVER_HOST = "localhost";
	protected static final String REQUEST_URL = SERVER_HOST+"/juliet/index.php/handler/call";
	protected static final String CHARSET_URL_ENCODING = "UTF-8"; 
	
	public static final int HTTP_OK = 200;
	
	public static enum HTTPMethodEnum {
		GET("GET"),
		POST("POST");
		
		private String method;
		private HTTPMethodEnum(String method){
			this.method = method;
		}
		
		@Override
		public String toString(){
			return method;
		}
	}
	
}
