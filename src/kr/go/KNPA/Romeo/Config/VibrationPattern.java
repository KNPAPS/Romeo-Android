package kr.go.KNPA.Romeo.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class VibrationPattern {
	
	
	private static long[] getVibrationPattern(String key) {
		long[] pattern;
		
		if( NONE.equalsIgnoreCase(key)) {
			return new long[] {0,0};
		} else if(ONE_SECOND.equalsIgnoreCase(key)) {
			pattern = new long[] {10,1000};
		} else if(THREE_SECOND.equalsIgnoreCase(key)) {
			pattern = new long[] {10,3000};
		} else if(FIVE_SECOND.equalsIgnoreCase(key)) {
			pattern = new long[] {10,5000};
		} else if(TEN_SECOND.equalsIgnoreCase(key)) {
			pattern = new long[] {10,10000};
		} else if(PIANISSIMO.equalsIgnoreCase(key)) {
			pattern = new long[500];
			for(int i=0; i< pattern.length; i++) {
				pattern[i] = ( (i%2 == 1) ?7:3);
			}
			return pattern;
		} else if(CRESCENDO.equalsIgnoreCase(key)) {
			pattern = new long[50];
			for(int i=0; i< pattern.length; i++) {
				pattern[i] = ( (i%2 == 1) ?20+i:80-i);
			}
		} else if(DECRESCENDO.equalsIgnoreCase(key)) {
			pattern = new long[50];
			for(int i=0; i< pattern.length; i++) {
				pattern[i] = ( (i%2 == 1) ?80-i:20+i);
			}
		} else {//if(DEFAULT.equalsIgnoreCase(key)) {
			pattern = new long[] {500, 500, 500, 500};
		}
		
		return pattern;
	}
	
	private static HashMap<String, long[]> PATTERNS = null;
	private static HashMap<String, String> PATTERNS_TITLE = null;
	private static ArrayList<HashMap<String,String>> PATTERNS_DICTIONARY = null;
	
	public final static String NONE				= "NONE";
	public final static String DEFAULT			= "DEFAULT";
	public final static String ONE_SECOND		= "ONE_SECOND";
	public final static String THREE_SECOND		= "THREE_SECOND";
	public final static String FIVE_SECOND		= "FIVE_SECOND";
	public final static String TEN_SECOND		= "TEN_SECOND";
	public final static String PIANISSIMO		= "PIANISSIMO";
	public final static String CRESCENDO		= "CRESCENDO";
	public final static String DECRESCENDO		= "DECRESCENDO";
	
	private final static String TITLE_NONE				= "진동 없음";
	private final static String TITLE_DEFAULT			= "기본 진동";
	private final static String TITLE_ONE_SECOND		= "1초";
	private final static String TITLE_THREE_SECOND		= "3초";
	private final static String TITLE_FIVE_SECOND		= "5초";
	private final static String TITLE_TEN_SECOND		= "10초";
	private final static String TITLE_PIANISSIMO		= "약하게";
	private final static String TITLE_CRESCENDO			= "점점 세게";
	private final static String TITLE_DECRESCENDO		= "점점 약하게";
	
	public final static String DICTIONARY_KEY = "KEY";
	public final static String DICTIONARY_TITLE = "TITLE";
	private static void init() {
		PATTERNS = new HashMap<String, long[]>();
		PATTERNS.put(NONE, getVibrationPattern(NONE));
		PATTERNS.put(DEFAULT, getVibrationPattern(DEFAULT));
		PATTERNS.put(ONE_SECOND, getVibrationPattern(ONE_SECOND));
		PATTERNS.put(THREE_SECOND, getVibrationPattern(THREE_SECOND));
		PATTERNS.put(FIVE_SECOND, getVibrationPattern(FIVE_SECOND));
		PATTERNS.put(TEN_SECOND, getVibrationPattern(TEN_SECOND));
		PATTERNS.put(PIANISSIMO, getVibrationPattern(PIANISSIMO));
		PATTERNS.put(CRESCENDO, getVibrationPattern(CRESCENDO));
		PATTERNS.put(DECRESCENDO, getVibrationPattern(DECRESCENDO));
		
		PATTERNS_TITLE = new HashMap<String, String>();
		PATTERNS_TITLE.put(NONE, TITLE_NONE);
		PATTERNS_TITLE.put(DEFAULT, TITLE_DEFAULT);
		PATTERNS_TITLE.put(ONE_SECOND, TITLE_ONE_SECOND);
		PATTERNS_TITLE.put(THREE_SECOND, TITLE_THREE_SECOND);
		PATTERNS_TITLE.put(FIVE_SECOND, TITLE_FIVE_SECOND);
		PATTERNS_TITLE.put(TEN_SECOND, TITLE_TEN_SECOND);
		PATTERNS_TITLE.put(PIANISSIMO, TITLE_PIANISSIMO);
		PATTERNS_TITLE.put(CRESCENDO, TITLE_CRESCENDO);
		PATTERNS_TITLE.put(DECRESCENDO, TITLE_DECRESCENDO);
	}
	
	public static long[] getPattern(String patternKey) {
		if(PATTERNS == null || PATTERNS.size() == 0)
			init();
		return PATTERNS.get(patternKey);
	}
	
	public static String getTitle(String patternKey) {
		if(PATTERNS_TITLE == null || PATTERNS_TITLE.size() == 0)
			init();
		return PATTERNS_TITLE.get(patternKey);
	}
	
	public static ArrayList<HashMap<String,String>> getDictionary() {
		if(PATTERNS_DICTIONARY != null && PATTERNS_DICTIONARY.size() > 0) return PATTERNS_DICTIONARY;
		PATTERNS_DICTIONARY = new ArrayList<HashMap<String,String>>();
		
		Iterator<String> iterator = PATTERNS.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			HashMap<String, String> dic = new HashMap<String, String>();
			dic.put("TITLE", PATTERNS_TITLE.get(key));
			dic.put("KEY", key);
			
			PATTERNS_DICTIONARY.add(dic);
		}
		return PATTERNS_DICTIONARY;
	}
}
