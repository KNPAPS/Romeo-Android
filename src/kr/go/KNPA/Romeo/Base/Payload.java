package kr.go.KNPA.Romeo.Base;

import java.util.ArrayList;
import java.util.Iterator;

import kr.go.KNPA.Romeo.Member.User;

public class Payload {

	public String 			event;
	public User 			sender;
	public ArrayList<User> 	receivers;
//	public String 			roomCode;
	public Message 			message;
	
	public String toJSON() {
		final String q = "\"";
		final String c = ":";
		final String lb = "[";
		final String rb = "]";
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(q).append("event").append(q).append(c).append(q).append(event).append(q).append(",");
		sb.append(q).append("sender").append(q).append(c).append(message.sender.toJSON()).append(",");
		sb.append(q).append("receivers").append(q).append(c).append(lb);
		Iterator<User> itr = message.receivers.iterator();
		User receiver = null;
		while(itr.hasNext()) {
			receiver = itr.next();
			sb.append(receiver.toJSON());
			if(itr.hasNext())
				sb.append(",");
		}
		sb.append(rb).append(",");
		
		sb.append(q).append("message").append(q).append(c).append(message.toJSON());
		sb.append("}");
		
		return sb.toString();
	}
	public static class Builder {
		private String 				_event;
		private User 				_sender;
		private ArrayList<User> 	_receivers;
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
		

		public Builder message (Message message) {
			_message = message;
			return this;
		}
		
		public Payload build() {
			Payload payload = new Payload();
			payload.event = this._event;
			payload.sender = this._sender;
			payload.receivers = this._receivers;
			payload.message = this._message;
			return payload;
		}
	}
	
}
