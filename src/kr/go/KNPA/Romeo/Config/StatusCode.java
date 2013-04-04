package kr.go.KNPA.Romeo.Config;

/**
 * 서버의 응답 json 에 답긴 status_code에 대한 enumeration 객체
 * @author 최영우
 * @since 2013.4.1
 */
public class StatusCode {
	public static final int SUCCESS=1; /*!< 정상적으로 성공 */
	public static final int INSUFFICIENT_INFORMATION=2; /*!< 전달한 정보가 불충분함 */
	public static final int NO_DATA=3;/*!< db에 원하는 데이터가 없음. 유저가 등록되지 않았거나 삭제하려는 데이터가 없거나.. */
	public static final int FAILED_TO_UPLOAD=4; /*!< 파일 업로드 실패 */
	
}