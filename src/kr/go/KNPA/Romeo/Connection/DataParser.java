package kr.go.KNPA.Romeo.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
@SuppressWarnings("unchecked")
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
		//TODO: 특정 method에 대해 서버와 주고받는 자료구조 변경해야 할듯
				
		dataNative = basicParse(dataJSONArray);
		
		
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
