package kr.go.KNPA.Romeo.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.ConnectionConfig;
import kr.go.KNPA.Romeo.Config.Constants;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

/**
 * 통신을 담당하는 ajax-like 객체.\n
 * @b 사용법
 * @code {.java}
 * 		//콜백함수 설정
		ConnectionCallback callBack = new ConnectionCallback(){
			@Override
			public void beforeSend(Payload request) {
				logBeforeSend(request);
			}
			
			@Override
			public void error(Payload request, String errorMsg, Exception e){
				logOnError(request, errorMsg, e);
			}
			
			@Override
			public void success(Payload response) {
				logOnSuccess(response);	
			}
		};
		
		new Connection(this) // context를 넘겨줌
						.requestPayloadJSON(requestJson) // (필수) request json 
						.accepts(MimeType.json) // (선택) 응답받을 content type / default:json
						.async(true) // (선택) 비동기로 처리할지 말지 / default:true
						.callBack(callBack) // (선택) 콜백함수 / default: 아무것도안함
						.contentType(MimeType.json)  // (선택) 보낼 content type / default: json
						.type(ConnectionConfig.HTTP_TYPE_POST) // (선택) httpmethod / default : post
						.timeout(10000)  // (선택) 연결 타임아웃 / default 10초
						.request();  // 연결 시작
 * @endcode
 * @author 최영우
 * @since 2013.4.1
 */
public class Connection {
	private static final String TAG = Connection.class.getName();
	private static final String BUNDLE_KEY_STATUS_CODE = "HTTPStatusCode";
	private static final String BUNDLE_KEY_RESPONSE_JSON = "responsePayloadJSON";
	/**
	 * @name settings
	 * 설정 변수들. 기본값은 ConnectionConfig에서 받아옴
	 * @{
	 */
	private String sUrl = ConnectionConfig.sUrl;
	private String accepts = ConnectionConfig.accepts;
	private boolean async = ConnectionConfig.async;
	private ConnectionCallback callBack = ConnectionConfig.callBack;
	private String contentType = ConnectionConfig.contentType;
	private Context context = ConnectionConfig.context;
	private String requestPayloadJSON = ConnectionConfig.requestPayloadJSON;
	private String dataType = ConnectionConfig.dataType;
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
	/** @} */
	
	
	/**
	 * 연결을 수행하는 context를 생성자에 줘야함
	 * @param context
	 */
	public Connection( Context context ) { context(context); }
	
	/**
	 * @name setters
	 * @{
	 */
	public Connection url( String v ) { this.sUrl = v; return this; }
	public Connection accepts( String v ) { this.accepts = v; return this; }
	public Connection async( boolean v ) { this.async = v; return this; }
	public Connection callBack(ConnectionCallback v) { this.callBack = v; return this; }
	public Connection contentType( String v ) { this.contentType = v; return this; }
	public Connection context( Context v ) { this.context = v; return this; }
	public Connection requestPayloadJSON( String v ) { 
		this.requestPayloadJSON = v;
		requestPayload = new Payload(requestPayloadJSON);
		return this; 
	}
	public Connection dataType( String v ) { this.dataType = v; return this; }
	public Connection timeout( int v ) { this.timeout = v; return this; }
	public Connection type( String v ) { this.type = v; return this; }
	/** @} */
	
	/**
	 * @name getters
	 * @{
	 */
	public String getResponsePayloadJSON() { return this.responsePayloadJSON; }
	public Payload getResponsePayload(){ return this.responsePayload; }
	/** @} */
	
	/**
	 * 일반적인 payload를 보낼 때의 요청
	 * @return
	 */
	public Connection request(){
		callBack.beforeSend(requestPayload);
		if ( async == false ) {
			try {
				Pair<Integer,String> result = doRequest(requestPayloadJSON);
				HTTPStatusCode = result.first;
				responsePayloadJSON = result.second;
			} catch ( RuntimeException e ) {
				callBack.error(requestPayload, context.getString( R.string.error_connection), e);
			}
			
			if ( HTTPStatusCode == HttpURLConnection.HTTP_OK ) { //성공
				callBack.success(responsePayload);	
			} else { //HTTP 에러
				callBack.error(requestPayload, 
						context.getString(R.string.error_connection), 
						new Exception("HTTP response Code : "+HTTPStatusCode));
			}
			
		}else {
			//비동기 == true일 때에는 다른 쓰레드를 만들어 통신을 진행한다.
			mHandler = new NetHandler(this);
			mThread = new Thread(new Runnable(){
				@Override
				public void run(){
					
					/**
					 * doRequest를 해서 HTTP statusCode와 responsePayloadJSON을 번들로 data에 저장하고
					 * 만약 에러가 나면 그 에러 객체를 obj에 담아 핸들러로 전달
					 */
					
					Message msg = new Message();
					Bundle bundle = new Bundle();
					try {
						Pair<Integer,String> result = doRequest(requestPayloadJSON);//HTTPStatusCode
						
						bundle.putInt(BUNDLE_KEY_STATUS_CODE, result.first);
						bundle.putString(BUNDLE_KEY_RESPONSE_JSON, result.second);
					} catch ( RuntimeException e ) {
						msg.obj = e;
					}
					msg.setData(bundle);
					mHandler.sendMessage(msg);
				}
			});
			
			mThread.start();
		}
		return this;
	}
	
	/**
	 * 파일을 업로드할 때 사용하는 요청
	 */
	public Connection requestUpload() {
		return this;
	}
	
	/**
	 * 파일을 다운로드할 때 
	 */
	public Connection requestDownload() {
		return this;
	}
	
	/**
	 * 서버에 연결하여 payload를 json형태로 주고받는다.\n
	 * HTTP status code와 responsePayloadJSON을 반환하며, HTTP에러가 아닌 에러가 났을 경우 error throw
	 * @return statusCode
	 */
	private Pair<Integer,String> doRequest(String requestPayloadJSON) throws RuntimeException {
		HttpURLConnection conn = null;
		Integer statusCode = Constants.NOT_SPECIFIED;
		String responsePayloadJSON = null;
				
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
		
		//keep alive 설정을 끈다. 
		System.setProperty("http.keepAlive","false");
		
		//캐시 사용여부
		conn.setUseCaches(false);
		//URLConnection 객체에게 인풋과 아웃풋 스트림을 모두 허용한다 
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		//타임아웃 설정
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		
		conn.setRequestProperty("Cache-Control", "no-cache, no-store");
		conn.setRequestProperty("Content-type", contentType);
		conn.setRequestProperty("Accept", accepts);
		
		try {

			OutputStream os = conn.getOutputStream();
			os.write(requestPayloadJSON.getBytes(ConnectionConfig.CHARSET_REQUEST_ENCODING));
			os.flush();
			os.close();
			
			//get response start 
			statusCode = conn.getResponseCode();

			//HTTP 통신이 올바르게 되었으면 response 읽어들인다.
			if ( statusCode == HttpURLConnection.HTTP_OK ) {

				StringBuffer resp = new StringBuffer();
				
				String line;
				
				InputStream _is;
				_is = conn.getInputStream();
				InputStreamReader _isr;
				
				_isr = new InputStreamReader(_is, ConnectionConfig.CHARSET_RESPONSE_ENCODING);
	
				BufferedReader br = new BufferedReader(_isr);
			
				while ((line = br.readLine() ) != null) {
					resp.append(line);
				}

				br.close();
				responsePayloadJSON = resp.toString();
			} else {
				Log.e(TAG, "HTTP response code : "+statusCode );
				return new Pair<Integer,String>(statusCode,responsePayloadJSON);
			}
			
			conn.disconnect();
		} catch ( IOException e ){
			Log.e(TAG, e.getMessage() );
			throw new RuntimeException(e);
		}

		return new Pair<Integer,String>(statusCode,responsePayloadJSON);
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
										
				connection.responsePayloadJSON = msg.getData().getString(BUNDLE_KEY_RESPONSE_JSON);
				connection.HTTPStatusCode = msg.getData().getInt(BUNDLE_KEY_STATUS_CODE);
				
				connection.responsePayload = new Payload(connection.responsePayloadJSON);
				
				if ( connection.HTTPStatusCode == Constants.NOT_SPECIFIED ) {
					//doRequest() 도중 예외가 발생한거임. 그러니 콜백으로 error를 호출
					//msg.obj = RuntimeException 객체임
					connection.callBack.error(connection.requestPayload, 
												connection.context.getString(R.string.error_connection), 
												(RuntimeException)msg.obj);
										
				} else {
					
					connection.callBack.success(connection.responsePayload);
				}
			} else {
				Log.e(TAG, "weak reference to Connection object failed" );
			}
	    	super.handleMessage(msg);
	    }
	}
}