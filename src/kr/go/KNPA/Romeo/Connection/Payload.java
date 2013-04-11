package kr.go.KNPA.Romeo.Connection;

import java.lang.reflect.Field;

import kr.go.KNPA.Romeo.Config.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 서버/클라이언트 간의 주고받는 데이터의 container\n
 * @b 사용법 \n
 * 1.Request\n
 * @code {.java}
 * Data data = new Data(); // 요청 시에 보낼 변수들을 담을 ArrayList<HashMap<String,Object>>
 * ... // data에 이벤트에 맞는 자료구조로 적절하게 변수 할당
 * Payload pl = new Payload();
 * pl.setEvent(String.EVENT);
 * pl.setData(data);
 * 
 * String requestPayload = pl.toJson(); // json string으로 변환
 * 
 * ... // connection class를 만들어서 서버와 통신
 * @endcode
 * 2.Response\n
 * @code {.java}
 * String responseJson = connection.getResponsePayload();// connection class로부터 response json을 받음
 * 
 * Payload pl = new Payload(responseJson); //native java collection으로 변환된 payload. event별로 data의 구조는 다름
 * 
 * @endcode
 */
public class Payload {
	private static final String TAG = "Payload";
	private static final String KEY_EVENT = "event";
	private static final String KEY_STATUS_CODE = "status";
	private static final String KEY_DATA = "data";
	private String event = "";
	private int statusCode = Constants.NOT_SPECIFIED;
	private Data data;
	
	/**
	 * Request Payload 생성자
	 * @param event 해당 payload가 활용되는 event
	 */
	public Payload() {
	}
	
	/**
	 * Response json을 payload 객체로 변환시킬 때 이 생성자를 씀
	 * @param json 서버로부터 받은 json string
	 */
	public Payload( String json )  {
		try {
			JSONObject jo = new JSONObject( json );
			String responseEvent = jo.get(KEY_EVENT).toString();
			int responseStatus = Constants.NOT_SPECIFIED;
			if ( jo.has(KEY_STATUS_CODE) == true ) {
				responseStatus = jo.getInt(KEY_STATUS_CODE);
			}
			setEvent(responseEvent);
			setStatusCode(responseStatus);
			setData( DataParser.parse(getEvent(), getStatusCode(), jo.getJSONArray(KEY_DATA) ) );
		} catch( JSONException e) {
			Log.d(TAG, e.getMessage()+":"+json);
		}
	}
	
	/**
	 * @name setters
	 * @{
	 */
	public Payload setEvent(String event) { this.event = event; return this; }
	public Payload setStatusCode(int status) { this.statusCode = status; return this;}
	public Payload setData(Data data) { this.data = data; return this; }
	/** @} */
	
	/**
	 * @name getters
	 * @{
	 */
	public String getEvent() {
		return this.event;
	}
	
	public int getStatusCode(){
		return this.statusCode;
	}
	
	public Data getData() {
		return this.data;
	}
	
	/** @} */
	
	/**
	 * Payload 객체를 json으로 변환
	 * @return json string
	 */
	public String toJSON() {
		FieldNamingStrategy fieldNamingStrategy = new FieldNamingStrategy() {
			
			@Override
			public String translateName(Field arg0) {
				return arg0.getDeclaringClass().getSimpleName()+"_"+arg0.getName();
				
			}
		};
		
		Gson gson = new GsonBuilder()
	     .setFieldNamingStrategy(fieldNamingStrategy)
	     .serializeNulls()
	     .create();
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"event\":\"").append(this.getEvent())
		.append("\",\"status\":").append(String.valueOf(this.getStatusCode()))
		.append(",\"data\":").append(gson.toJson(this.getData())).append("}");
		return sb.toString();
	}
	
}

