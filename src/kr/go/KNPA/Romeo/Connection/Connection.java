package kr.go.KNPA.Romeo.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
	private int responseCode;
	
	private Connection( Builder builder ) {	
		this.requestPayload = builder.requestPayload;
		this.timeout = builder.timeout;
		this.requestMethod = builder.requestMethod; 
	}
	
	public int request() throws RuntimeException {
		try {
			url = new URL(REQUEST_URL);
		} catch (MalformedURLException e1) {
			RuntimeException re = new RuntimeException(e1);
			throw re;
		}
		
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

			PrintWriter pw = new PrintWriter(writer);
			pw.write( URLEncoder.encode(requestPayload,CHARSET_REQUEST_ENCODING) );
			pw.flush();
			pw.close();

			responseCode = conn.getResponseCode();

			StringBuffer resp = new StringBuffer();
			
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
