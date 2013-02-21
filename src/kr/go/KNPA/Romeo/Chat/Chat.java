package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Member.User;

public class Chat extends Message {
	
	
	
	public final int type = MESSAGE_TYPE_CHAT;
	// public String title = null;
	// public String content	= null;
	// public Appendix appendix;
	
	public User sender	= null;
	public ArrayList<User> receivers;
	public long TS	= NOT_SPECIFIED;
	public long checkTS	= NOT_SPECIFIED;
	public boolean favorite = false;
	public String roomCode = null;
	
	public Chat() {
	}

	
	public static class Builder {
		private long _idx	= NOT_SPECIFIED;
		
		private String _title = null;
		private String _content	= null;
		private Appendix _appendix = null;
		private boolean _received = true;
		private User _sender	= null;
		private ArrayList<User> _receivers = null;
		private long _TS	= NOT_SPECIFIED;
		private long _checkTS	= NOT_SPECIFIED;
		private boolean _favorite = false;
		private String _roomCode = null;
		
		public Builder idx(long idx) {
			_idx = idx;
			return this;
		}
		
		public Builder title(String title) {
			_title = title;
			return this;
		}
		
		public Builder content( String content) {
			_content = content;
			return this;
		}
		
		public Builder sender(User  sender) {
			_sender = sender;
			return this;
		}
		
		public Builder receivers(ArrayList<User> receivers) {
			_receivers = receivers;
			return this;
		}
		
		public Builder received(boolean received) {
			_received = received;
			return this;
		}
		
		public Builder TS(long TS) {
			_TS = TS;
			return this;
		}
		
		public Builder checkTS(long checkTS) {
			_checkTS = checkTS;
			return this;
		}
		
		public Builder favorite(boolean favorite) {
			_favorite = favorite;
			return this;
		}
		
		public Builder roomCode(String roomCode) {
			_roomCode = roomCode;
			return this;
		}
		public Builder appendix(Appendix appendix) {
			_appendix = appendix;
			return this;
		}
		public Chat build() {
			
			Chat chat = new Chat();
			chat.idx = this._idx;
			chat.title = this._title;
			chat.content = this._content;
			chat.received = this._received;
			chat.TS = this._TS;
			chat.checkTS = this._checkTS;
			chat.favorite = this._favorite;
			chat.roomCode = this._roomCode;
			chat.appendix = this._appendix;
			chat.sender = this._sender;
			chat.receivers = this._receivers;
			
			return chat;
		}
	}
}