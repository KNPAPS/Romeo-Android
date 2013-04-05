package kr.go.KNPA.Romeo.Connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import kr.go.KNPA.Romeo.Config.ConnectionConfig;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.MimeType;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

/**
 * 통신을 담당하는 ajax-like 객체.\n
 * @b 사용법
 * @code {.java}
 *	//콜백함수 설정
 *	ConnectionCallback callBack = new ConnectionCallback(){
 *		@Override
 *		public void beforeSend(Payload request) {
 *				logBeforeSend(request);
 *			}
 *			
 *		@Override
 *		public void error(Payload request, String errorMsg, Exception e){
 *			logOnError(request, errorMsg, e);
 *		}
 *			
 *		@Override
 *		public void success(Payload response) {
 *			logOnSuccess(response);	
 *		}
 *	};
 *	
 *	new Connection(this)
 *					.requestPayloadJSON(requestJson) // 필수/request json
 *					.accepts(acceptContentType) // 선택/응답받을 콘텐츠타입/default json
 *					.async(true) // 선택/비동기로 처리할 지 여부/ default true
 *					.callBack(callBack) // 선택/ 콜백함수/ default 아무것도안함
 *					.contentType(contentType) // 선택/ 요청 데이터타입/ default json
 *					.timeout(timeout) // 선택/ 응답 타임아웃/ default 10초 
 *					.type(requestMethod) // 선택/ 요청 http method / default post
 *					.attachFile(filePath) // 선택 / 첨부할 파일 / default 없음
 *					.request();
 * @endcode
 * @author 최영우
 * @since 2013.4.1
 */

public class Connection {
	private static final String TAG = Connection.class.getName();
	/**
	 * @name settings
	 * 설정 변수들. 기본값은 ConnectionConfig에서 받아옴
	 * @{
	 */
	private String sUrl = ConnectionConfig.sUrl;
	private String accepts = ConnectionConfig.accepts;
	private boolean async = ConnectionConfig.async;
	private CallbackEvent<Payload, Integer, Payload> callBack = ConnectionConfig.callBack;
	private String contentType = ConnectionConfig.contentType;
	private Context context = ConnectionConfig.context;
	private String requestPayloadJSON = ConnectionConfig.requestPayloadJSON;
	private int HTTPStatusCode = ConnectionConfig.HTTPStatusCode;
	private int timeout = ConnectionConfig.timeout;
	private String type = ConnectionConfig.type;
	/** @} */
	
	/**
	 * @name 내부 변수
	 * @{
	 */
	private URL url = null;
	private String responsePayloadJSON = null;
	private Payload requestPayload = null;
	private Payload responsePayload = null;
	private Handler mHandler;
	private Thread mThread;
	private ArrayList<String> attachedFiles = null;
	/** @} */
	
	
	/**
	 * @name setters
	 * @{
	 */
	public Connection url( String v ) { this.sUrl = v; return this; }
	public Connection accepts( String v ) { this.accepts = v; return this; }
	public Connection async( boolean v ) { this.async = v; return this; }
	public Connection callBack(CallbackEvent<Payload, Integer, Payload> v) { this.callBack = v; return this; }
	public Connection contentType( String v ) { this.contentType = v; return this; }
	public Connection context( Context v ) { this.context = v; return this; }
	public Connection requestPayloadJSON( String v ) { 
		this.requestPayloadJSON = v;
		requestPayload = new Payload(requestPayloadJSON);
		return this; 
	}
	public Connection timeout( int v ) { this.timeout = v; return this; }
	public Connection type( String v ) { this.type = v; return this; }
	/** @} */
	
	/**
	 * @name 파일 전송 관련
	 * @{
	 */
	public Connection attachFile(String fileName) { this.attachedFiles.add(fileName); return this; }
	/** @} */
	
	/**
	 * @name getters
	 * @{
	 */
	public String getResponsePayloadJSON() { return this.responsePayloadJSON; }
	public Payload getResponsePayload(){ return this.responsePayload; }
	/** @} */
	
	/**
	 * 일반적인 payload만 보내는 요청
	 * @return
	 */
	public Connection request(){
		
		callBack.beforeSend(requestPayload);
		if ( async == false ) {
			try {
				Pair<Integer,Payload> result = doRequest(accepts, contentType, requestPayloadJSON, timeout, type);
				HTTPStatusCode = result.first;
				responsePayload = result.second;
			} catch ( RuntimeException e ) {
				callBack.error("서버와 통신 중 오류가 발생했습니다", e);
			}
			
			if ( HTTPStatusCode == HttpURLConnection.HTTP_OK ) { //성공
				callBack.success(responsePayload);	
			} else { //HTTP 에러
				callBack.error("서버와 통신 중 오류가 발생했습니다", 
						new Exception("HTTP response Code : "+HTTPStatusCode));
			}
			
		}else {
			//비동기 == true일 때에는 다른 쓰레드를 만들어 통신을 진행한다.
			mHandler = new NetHandler(this);
			mThread = new Thread(new Runnable(){
				@Override
				public void run(){
					
					/**
					 * doRequest를 해서 HTTP statusCode와 responsePayload pair를 obj로 설정해 전달하고
					 * 만약 에러가 나면 그 에러 객체를 obj에 담아 핸들러로 전달
					 * msg.arg1에 1이 들어가면 성공. 0이면 에러로 구분
					 */
					
					Message msg = new Message();
					try {
						Pair<Integer,Payload> result = doRequest(accepts, contentType, requestPayloadJSON, timeout, type);
						msg.obj = result;
						msg.what = 1;
					} catch ( RuntimeException e ) {
						msg.obj = e;
						msg.arg1 = 0;
					}
					mHandler.sendMessage(msg);
				}
			});
			
			mThread.start();
		}
		return this;
	}
	
	/**
	 * 서버에 연결하여 payload를 json형태로 주고받는다.\n
	 * HTTP status code와 responsePayloadJSON을 반환하며, HTTP에러가 아닌 에러가 났을 경우 error throw
	 * @return statusCode
	 */
	private Pair<Integer,Payload> doRequest(String accepts, String contentType, String requestPayloadJSON, int timeout, String type) 
			throws RuntimeException {
		HttpURLConnection conn = null;
		Integer statusCode = Constants.NOT_SPECIFIED;
		String responsePayloadJSON = null;
		Payload responsePayload = null;
				
		//url connection
		try {
			url = new URL(sUrl);
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage() );
			throw new RuntimeException(e);
		}
		
		//open url connection pointer
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage() );
			throw new RuntimeException(e);
		}
		
		try {
			conn.setRequestMethod(type);
		} catch (ProtocolException e) {
			Log.e(TAG, e.getMessage() );
			throw new RuntimeException(e);
		}

		
		//캐시 사용여부
		conn.setUseCaches(false);
		//URLConnection 객체에게 인풋과 아웃풋 스트림을 모두 허용한다 
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		//타임아웃 설정
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);

		conn.setRequestProperty("Cache-Control", "no-cache, no-store");
		
		conn.setRequestProperty("Accept", accepts);

		try {

			/**
			 * 파일 첨부 여부에 따라 outputstream을 multipart로 보낼지 그냥 설정된 content type으로 보낼 지 구분하여 보내고
			 * response는 어차피 같은 식으로 string만 받으니까 같은 루틴을 사용한다 
			 */
			int nAttachedFiles = attachedFiles.size();
			if ( nAttachedFiles > 0 ) { 
				//첨부파일이 있을 때에는 무조건 multipart/form-data로 전송하고 keepalive를 사용한다
				//request byte를 쓸 때 설정된 contentType을 따른다
				System.setProperty("http.keepAlive","true");
				conn.setRequestProperty("Content-type", MimeType.multipart);
				conn.setRequestProperty("Connection", "Keep-Alive");
					DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
					//payload json 부분
					dos.writeBytes("--"+Constants.MIME_BOUNDARY+"\r\n");
					dos.writeBytes("Content-Disposition: form-data; name=\"payload\";\r\n");
					dos.writeBytes("Content-Type: "+contentType+";\r\n\r\n");
					
					dos.writeBytes(requestPayloadJSON);
	
					//파일첨부
					for ( int i=0; i<nAttachedFiles; i++ ) {
						
						//개별 파일인풋스트림 열기
						File f = new File(attachedFiles.get(i));
						
						//파일이 없을 시 에러 던지기
						if ( f.exists() == false ) {
							FileNotFoundException e = new FileNotFoundException(f.getPath());
							throw new RuntimeException(e);
						}
						
						FileInputStream fis = new FileInputStream(f);
						
						//write data
						dos.writeBytes("--"+Constants.MIME_BOUNDARY+"\r\n");
						dos.writeBytes("Content-Disposition: form-data; name=\"file_"+i+"\"; filename=\""+f.getName()+"\"\r\n");
						dos.writeBytes("Content-Type: "+contentType+";\r\n\r\n");
						
						int bytesAvailable = fis.available();
						int maxBufferSize = Constants.MAX_BUFFER_SIZE;
						int bufferSize = Math.min(bytesAvailable, maxBufferSize);
						byte[] buffer = new byte[bufferSize];
						int bytesRead = fis.read(buffer,0,bufferSize);
						
						//read image
						while(bytesRead > 0) {
							dos.write(buffer,0,bufferSize);
							bytesAvailable = fis.available();
							maxBufferSize = Constants.MAX_BUFFER_SIZE;
							bufferSize = Math.min(bytesAvailable, maxBufferSize);
							bytesRead = fis.read(buffer,0,bufferSize);
						}
						
						dos.writeBytes("\r\n");
						fis.close();
					}
					
					//close streams
					dos.flush();
					dos.close();
			} else {
				//첨부 파일이 없을 때는 설정된 contentType을 따른다
				//keep alive 설정을 끈다. 
				System.setProperty("http.keepAlive","false");
				conn.setRequestProperty("Content-type", contentType);
	
				OutputStream os = conn.getOutputStream();
				os.write(requestPayloadJSON.getBytes(ConnectionConfig.CHARSET_REQUEST_ENCODING));
				os.flush();
				os.close();
			}
			
			/**
			 * HTTP_OK가 떨어지면 응답을 inputstream으로 읽기 시작.
			 */ 
			statusCode = conn.getResponseCode();
	
			//HTTP 통신이 올바르게 되었으면 response 읽어들인다.
			if ( statusCode == HttpURLConnection.HTTP_OK ) {
				InputStream _is;
				_is = conn.getInputStream();
			
				//가져오는 정보가 파일이 아닐 때
				StringBuffer resp = new StringBuffer();
				String line;
				InputStreamReader _isr;
				_isr = new InputStreamReader(_is, ConnectionConfig.CHARSET_RESPONSE_ENCODING);
				BufferedReader br = new BufferedReader(_isr);
				while ((line = br.readLine() ) != null) {
					resp.append(line);
				}
				br.close();
				responsePayloadJSON = resp.toString();
				responsePayload = new Payload(responsePayloadJSON);
			} else {
				Log.e(TAG, "HTTP response code : "+statusCode );
			}
			
			conn.disconnect();
		} catch ( IOException e ){
			Log.e(TAG, e.getMessage() );
			throw new RuntimeException(e);
		}

		return new Pair<Integer,Payload>(statusCode,responsePayload);
	}
	
	private static class NetHandler extends Handler {
	    private final WeakReference<Connection> mConnection;
		 
	    public NetHandler(Connection conn) {
	    	mConnection = new WeakReference<Connection>(conn);
	      
	    }
	 
	    @Override
	    public void handleMessage(Message msg) {
	    	Connection connection = mConnection.get();
			if (mConnection != null) {
				if ( msg.arg1 == 0 ) {
					//doRequest() 도중 예외가 발생한거임. 그러니 콜백으로 error를 호출
					//msg.obj = RuntimeException 객체임
					
					connection.callBack.error("서버와 통신 중 오류가 발생했습니다", 
												(RuntimeException)msg.obj);
										
				} else {
					@SuppressWarnings("unchecked")
					Pair<Integer,Payload> pair = (Pair<Integer,Payload>)msg.obj;
					connection.HTTPStatusCode = pair.first;
					connection.responsePayload = pair.second;
					
					connection.callBack.success(connection.responsePayload);
				}
			} else {
				Log.e(TAG, "weak reference to Connection object failed" );
			}
	    	super.handleMessage(msg);
	    }
	}
}