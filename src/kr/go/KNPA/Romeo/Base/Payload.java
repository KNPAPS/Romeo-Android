package kr.go.KNPA.Romeo.Base;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Member.User;

public class Payload {

	public String 			event;
	public User 			sender;
	public ArrayList<User> 	receivers;
	public String 			roomCode;
	public Message 			message;
	
	
	public static class Builder {
		private String 				_event;
		private User 				_sender;
		private ArrayList<User> 	_receivers;
		private String 				_roomCode;
		private Message	 			_message;
		
		public Builder event (String event) {
			_event = event;
			return this;
		}
		
		public Builder sender (User sender) {
			_sender = sender;
			return this;
		}
		
		public Builder receivers (ArrayList<User> receivers) {
			_receivers = receivers;
			return this;
		}
		
		public Builder roomCode (String roomCode) {
			_roomCode = roomCode;
			return this;
		}
		
		public Builder message (Message message) {
			_message = message;
			return this;
		}
		
		public Payload build() {
			Payload payload = new Payload();
			payload.event = this._event;
			payload.sender = this._sender;
			payload.receivers = this._receivers;
			payload.roomCode = this._roomCode;
			payload.message = this._message;
			return payload;
		}
	}
	
}
