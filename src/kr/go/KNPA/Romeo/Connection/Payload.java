package kr.go.KNPA.Romeo.Connection;

import kr.go.KNPA.Romeo.Config.EventEnum;
import kr.go.KNPA.Romeo.Config.StatusCodeEnum;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * 서버/클라이언트 간의 주고받는 데이터의 container\n
 * @b 사용법 \n
 * 1.Request\n
 * @code {.java}
 * Data data = new Data(); // 요청 시에 보낼 변수들을 담을 ArrayList<HashMap<String,Object>>
 * ... // data에 이벤트에 맞는 자료구조로 적절하게 변수 할당
 * Payload pl = new Payload();
 * pl.setEvent(EventEnum.EVENT);
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
 * @author 최영우
 * @since 2013.4.1
 */
public class Payload {
	private static final String KEY_EVENT = "event";
	private static final String KEY_STATUS_CODE = "status_code";
	private static final String KEY_DATA = "data";
	private EventEnum event;
	private StatusCodeEnum statusCode;
	private Data data;
	
	/**
	 * Request Payload 생성자
	 * @param event 해당 payload가 활용되는 event
	 */
	public Payload( EventEnum event ) {
		setEvent(event);
	}
	
	/**
	 * Response json을 payload 객체로 변환시킬 때 이 생성자를 씀
	 * @param json 서버로부터 받은 json string
	 */
	public Payload( String json ) throws JSONException {
		JSONObject jo;
		EventEnum responseEvent;
		StatusCodeEnum responseStatus;
		jo = new JSONObject( json );
		
		responseEvent = EventEnum.findEvent( jo.get(KEY_EVENT).toString() );
		
		responseStatus = StatusCodeEnum.findStatus( jo.getInt(KEY_STATUS_CODE) ); 
		
		if ( responseEvent == null ) {
			throw new JSONException("invalid response status code");
		} else if ( responseStatus == null  ) {
			throw new JSONException("invalid response status code");
		}
		
		setEvent(responseEvent);
		setStatusCode(responseStatus);
		setData( DataParser.parse(event, statusCode, jo.getJSONArray(KEY_DATA)) );
	}
	
	/**
	 * @name setters
	 * @{
	 */
	public Payload setEvent(EventEnum event) { this.event = event; return this; }
	public Payload setStatusCode(StatusCodeEnum status) { this.statusCode = status; return this;}
	public Payload setData(Data data) { this.data = data; return this; }
	/** @} */
	
	/**
	 * @name getters
	 * @{
	 */
	public EventEnum getEvent() {
		return this.event;
	}
	
	public StatusCodeEnum getStatusCode(){
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
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
}

