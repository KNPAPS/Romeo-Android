package kr.go.KNPA.Romeo.Config;

import java.util.HashMap;

public class VibrationPattern {
	private final static long[] VP_NONE				= {0,0};
	private final static long[] VP_DEFAULT 			= {500, 500, 500, 500};
	private final static long[] VP_ONE_SECOND		= {1000};
	private final static long[] VP_THREE_SECOND		= {2000};
	private final static long[] VP_FIVE_SECOND		= {5000};
	private final static long[] VP_TEN_SECOND		= {10000};
	private final static long[] VP_PIANISSIMO		= {10,90, 10,90, 10,90, 10,90, 10,90, 10,90, 10,90, 10,90, 10,90, 10,90 }; 
	private final static long[] VP_CRESCENDO 		= {10,90, 20,80, 30,70, 40,60, 50,50, 60,40, 70,30, 80,20, 90,10, 100,0 };
	private final static long[] VP_DECRESCENDO		= {100,0, 90,10, 80,20, 70,30, 60,40, 50,50, 40,60, 30,70, 20,80, 10,90 };
	
	private static HashMap<String, long[]> PATTERNS = null;
	private static HashMap<String, String> PATTERNS_TITLE = null;
	
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
	
	private static void init() {
		PATTERNS = new HashMap<String, long[]>();
		PATTERNS.put(NONE, VP_NONE);
		PATTERNS.put(DEFAULT, VP_DEFAULT);
		PATTERNS.put(ONE_SECOND, VP_ONE_SECOND);
		PATTERNS.put(THREE_SECOND, VP_THREE_SECOND);
		PATTERNS.put(FIVE_SECOND, VP_FIVE_SECOND);
		PATTERNS.put(TEN_SECOND, VP_TEN_SECOND);
		PATTERNS.put(PIANISSIMO, VP_PIANISSIMO);
		PATTERNS.put(CRESCENDO, VP_CRESCENDO);
		PATTERNS.put(DECRESCENDO, VP_DECRESCENDO);
		
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
	
	//public static ArrayList<String> getArrayList
}
