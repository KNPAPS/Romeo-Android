package kr.go.KNPA.Romeo.Payload;

/**
 * status code enumeration 객체
 */
public enum StatusCodeEnum {
	SUCCESS(1), /*!< 정상적으로 성공 */
	INSUFFICIENT_INFORMATION(2), /*!< 전달한 정보가 불충분함 */
	NO_DATA(3), /*!< db에 원하는 데이터가 없음. 유저가 등록되지 않았거나 삭제하려는 데이터가 없거나.. */
	FAILED_TO_UPLOAD(4) /*!< 파일 업로드 실패 */
	;
	
	private Integer code;
	private StatusCodeEnum(Integer code) {
		this.code = code;
	}
	
	/**
	 * Status 객체의 code 반환
	 * @return
	 */
	public Integer getCode() {
		return code;
	}
	
	/**
	 * Integer code를 입력하여 해당 코드에 맞는 status 객체 반환
	 * @param code
	 * @return
	 */
	public static StatusCodeEnum findStatus(Integer code) {

		StatusCodeEnum[] status = StatusCodeEnum.values();
		
		for ( StatusCodeEnum s : status ) {
			if ( s.getCode() == code ) {
				return s;
			}
		}

		return null;
	}
}