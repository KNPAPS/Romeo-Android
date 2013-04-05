package kr.go.KNPA.Romeo.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Survey.Survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Payload의 Data 객체에 대한 parsing을 수행. 대부분 같은 자료구조를 가지고 있으므로\n
 * basicParse만을 거치면 되지만 간혹 특별한 자료구조를 가지고 있는 응답에 대해서는\n
 * DataParser.parse() 함수의 내부에서 switch 문으로 구별하여 개별적인 parsing을 수행한다.
 * @author 최영우
 * @since 2013.4.1
 */
public class DataParser {

	/**
	 * Payload에서 일단 response json을 JSONObject로 바꾼 후,\n
	 * key가 Payload.KEY_DATA로 설정된 json array 객체를 이 메소드로 넘기면\n
	 * 이 메소드에서 event와 status code에 따라 JSONArray를 네이티브 자바 콜렉션으로 파싱하여\n
	 * 리턴한다.\n
	 * DataParser.parse( EventEnum, StatusCodeEnum, JSONArray 타입의 Data )
	 * @param event
	 * @param status
	 * @param dataJSONArray
	 * @return data
	 * @throws JSONException
	 */
	public static Data parse( String event, int status, JSONArray dataJSONArray ) throws JSONException {
		Data dataNative = null;
		
		if ( event == Event.MESSAGE_SEND ) {
			dataNative = parse_on_msg_send(dataJSONArray);					
		} else if ( event == Event.MESSAGE_RECEIVED ) {
			dataNative = parse_on_msg_receive(dataJSONArray);	
			
		} else {
			dataNative = basicParse(dataJSONArray);
		}
		
		return dataNative;
	}
	
	/**
	 * 대부분의 event들이 공통적으로 가지는 구조를 parsing 한다.\n
	 * "data" : [ {"key1":obj1, "key2":obj2, ... }, { ... } ] 와 같은 JSONArray를 ArrayList<HashMap<String,Object>> 형태로 변환한다.\n
	 * @param dataJSONArray
	 * @return parsed data
	 * @throws JSONException
	 */
	private static Data basicParse( JSONArray dataJSONArray ) throws JSONException {
		
		Data dataNative = new Data();
		
		int i,n;
		n = dataJSONArray.length();
		for ( i=0; i<n; i++ ) {
	        dataNative.add( JSONObjectToHashMap( dataJSONArray.getJSONObject(i) ) );
		}
		
		return dataNative;
	}
	
	/**
	 * MESSAGE:SEND 이벤트에 대한 파싱
	 * @param dataJSONArray
	 * @return
	 */
	private static Data parse_on_msg_send(JSONArray dataJSONArray) {
		Data dataNative = new Data();

		return dataNative;
	}
	
	/**
	 * MESSAGE:RECEIVE 이벤트에 대한 파싱
	 * @param dataJSONArray
	 * @return
	 * @throws JSONException 
	 */
	private static Data parse_on_msg_receive(JSONArray dataJSONArray) throws JSONException {
		Data dataNative = new Data();
		
		JSONObject jo = dataJSONArray.getJSONObject(0);
		
		switch( jo.getInt(Data.KEY_MESSAGE_TYPE)/Message.MESSAGE_TYPE_DIVIDER ) {
		case Message.MESSAGE_TYPE_CHAT:
			//jsonobject를 다시 json으로 바꿔서 MessageParser를 통해 생성
			Chat chat = (Chat) Message.parseMessage(jo.get(Data.KEY_MESSAGE).toString());
			dataNative.add(0,Data.KEY_MESSAGE,chat);
			break;
		case Message.MESSAGE_TYPE_DOCUMENT:
			Document document = (Document) Message.parseMessage(jo.get(Data.KEY_MESSAGE).toString());
			dataNative.add(0,Data.KEY_MESSAGE,document);
			
			HashMap<String,String> af = new HashMap<String, String>();
			
			af.put(Data.KEY_FILE_HASH, jo.getString(Data.KEY_FILE_HASH) );
			af.put(Data.KEY_FILE_TYPE, jo.getString(Data.KEY_FILE_TYPE) );
			af.put(Data.KEY_FILE_SIZE, jo.getString(Data.KEY_FILE_SIZE) );
			
			dataNative.add(0,Data.KEY_ATTACHED_FILES,af);
			break;
		case Message.MESSAGE_TYPE_SURVEY:
			Survey svy = (Survey) Message.parseMessage(jo.get(Data.KEY_MESSAGE).toString());
			dataNative.add(0,Data.KEY_MESSAGE,svy);
			break;
		default:
			break;
		}
		
		return dataNative;
	}
	
	private static <T> ArrayList<T> JSONArrayToArrayList(JSONArray jsonArray) throws JSONException {
		int i,n;
		ArrayList<T> arrayList;
		
		n = jsonArray.length();
		arrayList = new ArrayList<T>(n);
		for ( i=0; i<n; i++ ) {
			arrayList.add( (T)jsonArray.get(i) );
		}
		
		return arrayList;
	}
	
	private static HashMap<String,Object> JSONObjectToHashMap(JSONObject jsonObject) throws JSONException {
		
		HashMap<String,Object> hm = new HashMap<String,Object>();
		
        Iterator<?> keys = jsonObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            hm.put( key, jsonObject.get(key) );
        }
        
		return hm;
	}
}
