package kr.go.KNPA.Romeo.Util;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 자바의 Collection Framework 객체들을 생성한다.
 */
public class CollectionFactory {

	public CollectionFactory() {
	}
	
	/**
	 * @name HashMap Factory
	 * HashMap을 만드는 method
	 * @{ */
	
	/**
	 * 여러 개의 (string,object) 페어를 입력하여 HashMap <string,object> 객체를 생성한다.\n
	 * @code{.java}
	 *  HashMap hm = CollectionFactory.hasMapWithKeysAndValues(key1,obj1,key2,obj2,key3,obj3);
	 * @endcode
	 * @param objects string, object, string, objects 순으로 pair를 입력     
	 * @return hashmap
	 */
	public static HashMap<String, Object> hashMapWithKeysAndValues(Object ...objects) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(objects.length % 2 != 0) {
			RuntimeException e = new RuntimeException("objects length must be even");
			throw e;
		}
		
		for(int i=0; i<objects.length; i+=2) {
			String key = (String)objects[i];
			Object value = (Object)objects[i+1];
			
			hashMap.put(key, value);
		}
		
		return hashMap;
	}
	
	/**
	 * HashMap<String, String> 을 만들어준다. key와 value 모두 string
	 * @code{.java}
	 *  HashMap hm = CollectionFactory.hasMapWithKeysAndValues(key1,str1,key2,str2,key3,strj3);
	 * @endcode
	 * @param strings key string1, value string1, key string2, value string2 ...
	 * @return HashMap
	 */
	public static HashMap<String, String> hashMapWithKeysAndStrings(String ...strings) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if(strings.length % 2 != 0) {
			RuntimeException e = new RuntimeException("strings length must be even");
			throw e;
		}
		for(int i=0; i<strings.length; i+=2) {
			String key = strings[i];
			String value = strings[i+1];
			
			hashMap.put(key, value);
		}
		
		return hashMap;
	}
	
	/** @} */
	
	/**
	 * @name ArrayList
	 * ArrayList를 만드는 메소드들
	 * @{
	 */
	
	/**
	 * object들을 가변 인자로 받아서 하나의 arrayList로 리턴한다
	 * @code{.java}
	 * ArrayList l = CollectionFactory.arrayListwithKeysAndValues(obj1,obj2,...);
	 * @endcode
	 * @param objects arrayList에 들어갈 object
	 * @return arrayList
	 */
	public static ArrayList<Object> arrayListwithKeysAndValues(Object ...objects) {
		ArrayList<Object> arrayList = new ArrayList<Object>();

		for(int i=0; i<objects.length; i++) {
			Object value = (Object)objects[i+1];
			
			arrayList.add(value);
		}
		
		return arrayList;
	}

	/** @} */
}
