package kr.go.KNPA.Romeo.Connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import kr.go.KNPA.Romeo.Config.ConnectionConfig;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

/**
 * 통신을 담당하는 ajax-like 객체.\n
 * 
 * @b 사용법
 * @code {.java} //콜백함수 설정 ConnectionCallback callBack = new
 *       ConnectionCallback(){
 * @Override public void onPreExecute(Payload request) { logBeforeSend(request);
 *           }
 * 
 * @Override public void onError(Payload request, String errorMsg, Exception e){
 *           logOnError(request, errorMsg, e); }
 * 
 * @Override public void onPostExecute(Payload response) {
 *           logOnSuccess(response); } };
 * 
 *           new Connection(this) .requestPayload(request) // 필수/request json
 *           .async(true) // 선택/비동기로 처리할 지 여부/ default true .callBack(callBack)
 *           // 선택/ 콜백함수/ default 아무것도안함 .contentType(contentType) // 선택/ 요청
 *           데이터타입/ default json .attachFile(filePath) // 선택 / 첨부할 파일 / default
 *           없음 .request();
 * @endcode
 */

public class Connection {
	private static final String							TAG					= Connection.class.getName();
	/**
	 * @name settings 설정 변수들. 기본값은 ConnectionConfig에서 받아옴
	 * @{
	 */
	private String										sUrl				= ConnectionConfig.sUrl;
	private String										accepts				= ConnectionConfig.accepts;
	private boolean										async				= ConnectionConfig.async;
	private CallbackEvent<Payload, Integer, Payload>	callBack			= ConnectionConfig.callBack;
	private String										contentType			= ConnectionConfig.contentType;
	private String										requestPayloadJSON	= ConnectionConfig.requestPayloadJSON;
	private int											HTTPStatusCode		= ConnectionConfig.HTTPStatusCode;
	private int											timeout				= ConnectionConfig.timeout;
	private String										type				= ConnectionConfig.type;
	public boolean										successful			= false;
	/** @} */

	/**
	 * @name 내부 변수
	 * @{
	 */
	private URL											url					= null;
	private String										responsePayloadJSON	= null;
	private Payload										requestPayload		= null;
	private Payload										responsePayload		= null;
	private Handler										mHandler;
	private Thread										mThread;
	private ArrayList<String>							attachedFiles		= null;

	/** @} */

	/**
	 * @name setters
	 * @{
	 */
	public Connection url(String v)
	{
		this.sUrl = v;
		return this;
	}

	public Connection accepts(String v)
	{
		this.accepts = v;
		return this;
	}

	public Connection async(boolean v)
	{
		this.async = v;
		return this;
	}

	public Connection callBack(CallbackEvent<Payload, Integer, Payload> v)
	{
		this.callBack = v;
		return this;
	}

	public Connection contentType(String v)
	{
		this.contentType = v;
		return this;
	}

	public Connection requestPayload(Payload payload)
	{
		this.requestPayload = payload;
		this.requestPayloadJSON = payload.toJSON();
		return this;
	}

	public Connection timeout(int v)
	{
		this.timeout = v;
		return this;
	}

	public Connection type(String v)
	{
		this.type = v;
		return this;
	}

	/** @} */

	/**
	 * @name 파일 전송 관련
	 * @{
	 */
	public Connection attachFile(String fileName)
	{
		if (this.attachedFiles == null)
		{
			this.attachedFiles = new ArrayList<String>();
		}
		this.attachedFiles.add(fileName);
		return this;
	}

	/** @} */

	/**
	 * @name getters
	 * @{
	 */
	public String getResponsePayloadJSON()
	{
		return this.responsePayloadJSON;
	}

	public Payload getResponsePayload()
	{
		if (this.responsePayload == null)
		{
			return new Payload().setEvent(requestPayload.getEvent()).setStatusCode(Constants.NOT_SPECIFIED);
		}
		return this.responsePayload;
	}

	/** @} */

	/**
	 * 일반적인 payload만 보내는 요청
	 * 
	 * @return
	 */
	public Connection request()
	{

		callBack.onPreExecute(requestPayload);
		if (async == false)
		{
			try
			{
				Pair<Integer, Payload> result = doRequest(accepts, contentType, requestPayloadJSON, timeout, type);
				HTTPStatusCode = result.first;
				responsePayload = result.second;
			}
			catch (RuntimeException e)
			{
				callBack.onError("서버와 통신 중 오류가 발생했습니다", e);
			}

			if (HTTPStatusCode == HttpURLConnection.HTTP_OK)
			{ // 성공
				callBack.onPostExecute(responsePayload);
				successful = true;
			}
			else
			{ // HTTP 에러
				responsePayload = new Payload();
				responsePayload.setStatusCode(StatusCode.NETWORK_ERROR);
				callBack.onError("서버와 통신 중 오류가 발생했습니다", new Exception("HTTP response Code : " + HTTPStatusCode));
			}

		}
		else
		{
			// 비동기 == true일 때에는 다른 쓰레드를 만들어 통신을 진행한다.
			mHandler = new NetHandler(this);
			mThread = new Thread(new Runnable() {
				@Override
				public void run()
				{

					/**
					 * doRequest를 해서 HTTP statusCode와 responsePayload pair를 obj로
					 * 설정해 전달하고 만약 에러가 나면 그 에러 객체를 obj에 담아 핸들러로 전달 msg.arg1에 1이
					 * 들어가면 성공. 0이면 에러로 구분
					 */

					Message msg = mHandler.obtainMessage();
					try
					{
						Pair<Integer, Payload> result = doRequest(accepts, contentType, requestPayloadJSON, timeout, type);
						msg.obj = result;
						msg.what = 1;
					}
					catch (RuntimeException e)
					{
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
	 * 서버에 연결하여 payload를 json형태로 주고받는다.\n HTTP status code와 responsePayloadJSON을
	 * 반환하며, HTTP에러가 아닌 에러가 났을 경우 error throw
	 * 
	 * @return statusCode
	 */
	private Pair<Integer, Payload> doRequest(String accepts, String contentType, String requestPayloadJSON, int timeout, String type) throws RuntimeException
	{
		HttpURLConnection conn = null;
		Integer statusCode = Constants.NOT_SPECIFIED;
		String responsePayloadJSON = null;
		Payload responsePayload = null;

		// url connection
		try
		{
			url = new URL(sUrl);
		}
		catch (MalformedURLException e)
		{
			Log.e(TAG, e.getMessage());
			throw new RuntimeException(e);
		}

		// open url connection pointer
		try
		{
			conn = (HttpURLConnection) url.openConnection();
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage());
			throw new RuntimeException(e);
		}

		try
		{
			conn.setRequestMethod(type);
		}
		catch (ProtocolException e)
		{
			Log.e(TAG, e.getMessage());
			throw new RuntimeException(e);
		}

		final String RN = "\r\n";
		final String TH = "--";
		final String BOUNDARY = "*****";

		int nAttachedFiles = 0;
		if (attachedFiles != null)
		{
			nAttachedFiles = attachedFiles.size();
		}

		// 캐시 사용여부
		conn.setUseCaches(false);
		// URLConnection 객체에게 인풋과 아웃풋 스트림을 모두 허용한다
		conn.setDoInput(true);
		conn.setDoOutput(true);

		// 타임아웃 설정
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);

		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Cache-Control", "no-cache, no-store");
		conn.setRequestProperty("Accept", accepts);
		System.setProperty("http.keepAlive", "false");
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

		try
		{
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(TH + BOUNDARY);
			dos.writeBytes(RN + "Content-Disposition: form-data; name=" + "\"payload\"" + ";" + RN);
			dos.writeBytes("Content-Type: application/x-www-form-urlencoded;" + RN);
			String encoded = null;
			encoded = Formatter.encodeURIComponent(requestPayloadJSON);
			dos.writeBytes(RN + encoded + RN);

			for (int fi = 0; fi < nAttachedFiles; fi++)
			{
				// 개별 파일인풋스트림 열기
				File f = new File(attachedFiles.get(fi));

				// 파일이 없을 시 에러 던지기
				if (f.exists() == false)
				{
					FileNotFoundException e = new FileNotFoundException(f.getPath());
					throw new RuntimeException(e);
				}

				// write data
				dos.writeBytes(TH + BOUNDARY);
				dos.writeBytes(RN + "Content-Disposition: form-data; name=" + "\"file_" + fi + "\"" + "; filename=\"" + f.getName() + "\"" + RN);
				dos.writeBytes("Content-Type: " + contentType + ";" + RN);

				dos.writeBytes(RN);
				FileInputStream fis = new FileInputStream(f);

				int bytesAvailable = fis.available();
				int maxBufferSize = Constants.MAX_BUFFER_SIZE;
				int bufferSize = Math.min(bytesAvailable, maxBufferSize);

				byte[] buffer = new byte[bufferSize];
				// int bytesRead = fis.read(buffer,0,bufferSize);
				int bytesRead = -1;
				while ((bytesRead = fis.read(buffer)) != -1)
				{
					dos.write(buffer, 0, bytesRead);
					bytesAvailable = fis.available();
					maxBufferSize = Constants.MAX_BUFFER_SIZE;
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
				}

				dos.writeBytes(RN);

				fis.close();
			}

			dos.writeBytes(TH + BOUNDARY + TH + RN);

			dos.flush();
			dos.close();

			/**
			 * HTTP_OK가 떨어지면 응답을 inputstream으로 읽기 시작.
			 */
			statusCode = conn.getResponseCode();

			// HTTP 통신이 올바르게 되었으면 response 읽어들인다.
			if (statusCode == HttpURLConnection.HTTP_OK)
			{
				InputStream is;
				is = conn.getInputStream();

				// 가져오는 정보가 파일이 아닐 때
				StringBuffer resp = new StringBuffer();

				String line;
				InputStreamReader isr;
				isr = new InputStreamReader(is, ConnectionConfig.CHARSET_RESPONSE_ENCODING);
				BufferedReader br = new BufferedReader(isr);
				while ((line = br.readLine()) != null)
				{
					resp.append(line);
				}
				br.close();
				is.close();
				responsePayloadJSON = resp.toString();
				responsePayload = new Payload(responsePayloadJSON);
			}
			else
			{
				responsePayload = new Payload();
				responsePayload.setStatusCode(StatusCode.NETWORK_ERROR);
				Log.e(TAG, "HTTP response code : " + statusCode + " " + requestPayloadJSON);
			}
		}
		catch (IOException e)
		{
			responsePayload = new Payload();
			responsePayload.setStatusCode(StatusCode.NETWORK_ERROR);
			Log.e(TAG, e.getMessage());
			throw new RuntimeException(e);
		}
		finally
		{
			conn.disconnect();
		}

		Log.d(TAG, "request : " + requestPayloadJSON + ", response : " + responsePayloadJSON);
		return new Pair<Integer, Payload>(statusCode, responsePayload);
	}

	private static class NetHandler extends Handler {
		private final WeakReference<Connection>	mConnection;

		public NetHandler(Connection conn)
		{
			mConnection = new WeakReference<Connection>(conn);

		}

		@Override
		public void handleMessage(Message msg)
		{
			Connection connection = mConnection.get();
			if (mConnection != null)
			{
				if (msg.what == 0)
				{
					// doRequest() 도중 예외가 발생한거임. 그러니 콜백으로 error를 호출
					// msg.obj = RuntimeException 객체임

					connection.callBack.onError("서버와 통신 중 오류가 발생했습니다", null);

				}
				else
				{
					@SuppressWarnings("unchecked")
					Pair<Integer, Payload> pair = (Pair<Integer, Payload>) msg.obj;
					connection.HTTPStatusCode = pair.first;
					connection.responsePayload = pair.second;
					connection.successful = true;
					connection.callBack.onPostExecute(connection.responsePayload);
				}
			}
			else
			{
				Log.e(TAG, "weak reference to Connection object failed");
			}
			super.handleMessage(msg);
		}
	}
}