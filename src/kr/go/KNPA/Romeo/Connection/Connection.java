package kr.go.KNPA.Romeo.Connection;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import kr.go.KNPA.Romeo.Config.ConnectionManager;
import kr.go.KNPA.Romeo.Config.MimeTypeEnum;

/**
 * 서버와의 통신 연결을 담당하는 객체.
 * @author user
 *
 */
public class Connection extends ConnectionManager {
	
	private String requestPayload;
	private int timeout;
	private HTTPMethodEnum requestMethod; 
	private MimeTypeEnum mimeType;
	
	private URL url;
	private String responsePayload = null;
	private HttpURLConnection conn = null;

	private Connection( Builder builder ) {	
		this.requestPayload = builder.requestPayload;
		this.timeout = builder.timeout;
		this.requestMethod = builder.requestMethod; 
	}
	
	public int request() throws RuntimeException {
		url = new URL(REQUEST_URL);
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			
			System.setProperty("http.keepAlive","false");
	
			try {
				conn.setRequestMethod(requestMethod.toString());
			} catch (ProtocolException e) {
				RuntimeException re = new RuntimeException(e);
				throw re;
			}
			
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			conn.setAllowUserInteraction(true);
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Content-type", mimeType.toString());
			conn.setRequestProperty("Accept", mimeType.toString());
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			
			writer.write(URLEncoder.encode(requestPayload,CHARSET_URL_ENCODING));
		
			writer.flush();

			
			
		} catch ( IOException e ){
			RuntimeException re = new RuntimeException(e);
			throw re;
		}
	}
	
	private int readResponse(){
		
	}
	
	public String getResponsePayload() {
		return responsePayload;
	}
	
	public static class Builder {
		private String requestPayload;
		private int timeout = 10000;
		private HTTPMethodEnum requestMethod = HTTPMethodEnum.POST;
		private MimeTypeEnum mimeType = MimeTypeEnum.json;
		
		public Builder( String json ) {
			this.requestPayload = json;
		}
		
		public Builder requestPayload(String json) { this.requestPayload = json; return this; }
		public Builder timeout(int timeout) { this.timeout = timeout; return this; }
		public Builder method(HTTPMethodEnum method) { this.requestMethod= method; return this; }
		public Builder method(MimeTypeEnum mime) { this.mimeType= mime; return this; }
		
		public Connection build() {
			return new Connection(this);
		}
	}
	
}
