package kr.go.KNPA.Romeo.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class CollectionFactory {

	public CollectionFactory() {
		// TODO Auto-generated constructor stub
	}
	
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
	
	public static ArrayList<Object> arrayListwithKeysAndValues(Object ...objects) {
		ArrayList<Object> arrayList = new ArrayList<Object>();

		for(int i=0; i<objects.length; i++) {
			Object value = (Object)objects[i+1];
			
			arrayList.add(value);
		}
		
		return arrayList;
	}

}
