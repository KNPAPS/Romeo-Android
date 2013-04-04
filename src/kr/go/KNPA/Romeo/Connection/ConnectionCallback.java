package kr.go.KNPA.Romeo.Connection;
/**
 * Connection 객체에서 일반 쓰레드로 작업한 후 호출할 UI 쓰레드의 콜백 메소드
 * @author 최영우
 * @since 2013.4.3
 */
public class ConnectionCallback {
	public void beforeSend(Payload requestPayload){ }
	public void error(Payload responsePayload, String textStatus, Exception e){ }
	public void success(Payload responsePayload){ }
}
