package kr.go.KNPA.Romeo.Config;

/**
 * 일반적으로 사용되는 공통된 상수들의 모음
 * @author 최영우
 * @since 2013.04.02
 */
public class Constants {
	//! Type이나 기타 int형 변수의 기본 설정값
	public static final int NOT_SPECIFIED = -777;
	//! 경찰 계급 순서
	public static final String[] POLICE_RANK = 
			{"치안총감", "치안정감", "치안감", "경무관", "총경", "경정", "경감", "경위", "경사", "경장", "순경", "의경"};
	public static final String MIME_BOUNDARY = "32wif*9vk4*kdlsfv4*";
	public static final String TAG = "daonCustomLog";
	public static final int MAX_BUFFER_SIZE = 1024;
	public static boolean DEVELOPMENT = true;
	public static boolean DEBUG = true;
}
