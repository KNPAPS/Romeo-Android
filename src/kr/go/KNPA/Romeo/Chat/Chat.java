package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Base.Payload;

public class Chat extends Message {
	
	// Message Sub Type Constants
	public static final int TYPE_MEETING = 0;
	public static final int TYPE_COMMAND = 1;
	
	// Constructor
	public Chat() {
	}

	public Chat(Payload payload) {
		this.idx = payload.message.idx;
		this.type = payload.message.type;
		this.title = payload.message.title;
		this.content = payload.message.content;
		this.appendix = payload.message.appendix;
		this.sender = payload.sender;
		this.receivers = payload.receivers;
		this.TS = System.currentTimeMillis();
		//this.received = true;
		//this.checkTS = NOT_SPECIFIED;
		//this.checked = false;
	}
	
	public Chat(Payload payload, boolean received, long checkTS) {
		this(payload);
		this.received = received;
		this.checkTS = checkTS;
		if(this.checkTS == Message.NOT_SPECIFIED) {
			this.checked = false;
		} else {
			this.checked = true;
		}
	}
	
	//
	public String getRoomCode() {
		return this.appendix.getRoomCode();
	}
	
	public static class Builder extends Message.Builder {
		
		public Chat build() {
			
			Chat chat = (Chat) new Chat.Builder()
									   .idx(_idx)
									   .title(_title)
									   .type(_type)
									   .content(_content)
									   .appendix(_appendix)
									   .sender(_sender)
									   .receivers(_receivers)
									   .TS(_TS)
									   .received(_received)
									   .checkTS(_checkTS)
									   .checked(_checked)
									   .buildMessage();

			return chat;
		}
	}
}