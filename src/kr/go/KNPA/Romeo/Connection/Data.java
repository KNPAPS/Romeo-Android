package kr.go.KNPA.Romeo.Connection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 데이터 Entry들의 배열
 */
public class Data extends ArrayList<HashMap<String,Object>> {

	/**
	 * index번째 HashMap에 key,value 페어 삽입.\n
	 * 만약 index번째가 없다면 새로 생성하여 삽입한다.
	 * @param index arraylist의 인덱스
	 * @param key hashmap key
	 * @param value hashmap value object
	 */
	public Data add( int index, String key, Object value ) {
		
		HashMap<String,Object> hm = new HashMap<String,Object>();
		hm.put(key,value);
		this.add(index,hm);
		
		return this;
	}
	
	/**
	 * index번째 HashMap의 해당 key를 가진 Object 리턴.\n
	 * 만약 없으면 null 리턴 
	 * @param index
	 * @param key
	 * @return obj object or null
	 */
	public Object get( int index, String key ) {
		if(this.get(index) != null && this.get(index).containsKey(key)) {
			this.get(index).get(key);
		}
				
		return null;
	}
	
	//! 객체 구분용 ID
	/**
	 *
	 * Data라는 이름을 가진 객체가 여럿 있어서 그런지 구별할 UID를 지정하라고 떠서\n
	 * 걍 지정 해 놓은 상수 
	 */
	private static final long serialVersionUID = 1L;
	
}
