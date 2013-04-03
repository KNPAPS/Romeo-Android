package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.Constants;

/**
 * 채팅 메세지 객체
 * @author 채호식, 최영우
 * @since 2013.4.2
 */
public class Chat extends Message {
	
	//! 채팅 타입 : 회의
	public static final int TYPE_MEETING = 1;
	//! 채팅 타입 : 지시와 보고
	public static final int TYPE_COMMAND = 2;
	
	//! 방 번호 hash.
	/**
	 * 생성 방식은 [최초 생성자 hash]:[방이 생성된 ts]
	 */
	private int roomHash = Constants.NOT_SPECIFIED;
	//! chat 타입. 지시와보고 또는 회의
	private int chatType = Constants.NOT_SPECIFIED;
	
	public int getChatType() { return chatType; }

	public Chat setChatType(int chatType) { this.chatType = chatType; return this; }

	public int getRoomHash() { return roomHash; }

	public Chat setRoomHash(int roomHash) { this.roomHash = roomHash; return this; }
}