package kr.go.KNPA.Romeo.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import kr.go.KNPA.Romeo.Config.ConnectionManager;
import kr.go.KNPA.Romeo.Config.MimeTypeEnum;

/**
 * 서버와의 통신 연결을 담당하는 객체. Builder pattern을 이용하여 생성.\n
 * @b 사용법
 * @code
 * String json = "{ "event": XX , "data": [ { ... }, { ... }, ... ]  }"; // 요청할 때 보낼 json 
 * 
 * //builder를 이용해 connection 객체 생성. 이때 builder로 json만 넘겨주면 나머지 설정들은
 * //따로 지정해주지 않으면 기본 설정으로 생성됨.  (  [] 안은 optional )
 * Connection conn = new Connection.Builder(json)[.method( HTTPMethodEnum.POST )].build();
 * 
 * String responsePayload = null;
 * if ( conn.request() == HttpURLConnection.HTTP_OK ) {
 * 	responsePayload = conn.getResponsePayload();
 * }
 * 
 * ... // Payload 객체의 생성자로 responsePayload 스트링을 넘겨서 적절히 처리
 * @endcode
 * @author 최영우
 * @since 2013.4.1
 */
public class Connection extends ConnectionManager {
	
	//! request할 payload json
	private String requestPayload;
	//! connection 시간제한 default 10000
	private int timeout;
	//! HTTP method default POST
	private HTTPMethodEnum requestMethod;
	//! 요청 데이터 타입 default json
	private MimeTypeEnum requestDataType;
	//! 응답 데이터 타입 default json
	private MimeTypeEnum responseDataType;
	
	private URL url;
	private String responsePayload = null;
	private HttpURLConnection conn = null;
	//! HTTP 응답 코드. 200 이 OK
	private int responseCode;
	
	/**
	 * Builder로만 생성 가능
	 * @param builder connection builder instance
	 */
	private Connection( Builder builder ) {	
		this.requestPayload = builder.requestPayload;
		this.timeout = builder.timeout;
		this.requestDataType = builder.requestDataType;
		this.responseDataType = builder.responseDataType;
		this.requestMethod = builder.requestMethod; 
	}
	
	/**
	 * 서버로 요청 보내기. 
	 * @return int response code
	 * @throws RuntimeException
	 */
	public int request() throws RuntimeException {
		
		//TODO: 설정된 변수들이 제대로 되었는지 검증해야함
		
		//url connection
		try {
			url = new URL(REQUEST_URL);
		} catch (MalformedURLException e1) {
			RuntimeException re = new RuntimeException(e1);
			throw re;
		}
		
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			conn.setRequestMethod(requestMethod.toString());
		} catch (ProtocolException e) {
			RuntimeException re = new RuntimeException(e);
			throw re;
		}
		
		System.setProperty("http.keepAlive","false");
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setAllowUserInteraction(false);
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		conn.setRequestProperty("Cache-Control", "no-cache, no-store");
		conn.setRequestProperty("Content-type", requestDataType.toString());
		conn.setRequestProperty("Accept", responseDataType.toString());
		
		try {

			OutputStream os = conn.getOutputStream();
			os.write(requestPayload.toString().getBytes(CHARSET_REQUEST_ENCODING));
			os.flush();
			os.close();
			
			//get response start 
			responseCode = conn.getResponseCode();

			StringBuffer resp = new StringBuffer();
			
			//HTTP 통신이 올바르게 되었으면 response 읽어들인다.
			if ( responseCode == HttpURLConnection.HTTP_OK ) {
				
				String line;
				
				InputStream _is;
				_is = conn.getInputStream();
				InputStreamReader _isr;
				
				_isr = new InputStreamReader(_is, CHARSET_RESPONSE_ENCODING);
	
				BufferedReader br = new BufferedReader(_isr);
			
				while ((line = br.readLine() ) != null) {
					//System.out.println(line);
					resp.append(line);
				}

				br.close();
			}
			
			responsePayload = resp.toString();
			conn.disconnect();

			return responseCode;
				
		} catch ( IOException e ){
			RuntimeException re = new RuntimeException(e);
			throw re;
		}
	}
	
	/**
	 * 서버가 응답한 json string을 리턴
	 * @return json string response from server
	 */
	public String getResponsePayload() {
		return responsePayload;
	}
	
	/**
	 * Connection Builder
	 * @author 최영우
	 * @since 2013.04.01
	 */
	public static class Builder {
		private String requestPayload;
		private int timeout = 10000;
		private HTTPMethodEnum requestMethod = HTTPMethodEnum.POST;
		private MimeTypeEnum requestDataType = MimeTypeEnum.json;
		private MimeTypeEnum responseDataType = MimeTypeEnum.json;
		
		/**
		 * request json은 필수적인 parameter이므로 생성자에서 받는다
		 * @param request json
		 */
		public Builder( String json ) {
			this.requestPayload = json;
		}
		
		/**
		 * @name setters
		 * @{
		 */
		public Builder requestPayload(String json) { this.requestPayload = json; return this; }
		public Builder timeout(int timeout) { this.timeout = timeout; return this; }
		public Builder method(HTTPMethodEnum method) { this.requestMethod= method; return this; }
		public Builder requestDataType(MimeTypeEnum mime) { this.requestDataType= mime; return this; }
		public Builder responseDataType(MimeTypeEnum mime) { this.responseDataType= mime; return this; }
		/**@}*/
		
		/**
		 * 변수들을 설정한 뒤에 호출하여 connection 객체를 build
		 * @return Connection 
		 */
		public Connection build() {
			return new Connection(this);
		}
	}
	
}
