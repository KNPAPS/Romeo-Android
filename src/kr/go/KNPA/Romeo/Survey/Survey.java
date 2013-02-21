package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Member.User;

public class Survey extends Message{
	public long idx	= NOT_SPECIFIED;
	
	public final int type = Message.MESSAGE_TYPE_SURVEY;
	// public String title = null;
	// public String content	= null;
	// public Appendix appendix;
	
	public User sender	= null;
	public ArrayList<User> receivers;
	public long TS	= NOT_SPECIFIED;
	public long checkTS	= NOT_SPECIFIED;
	public boolean favorite = false;
	public String roomCode = null;
	public long openTS = NOT_SPECIFIED;
	public long closeTS = NOT_SPECIFIED;
	
	public Survey() {
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
//		private long _openTS = NOT_SPECIFIED;
//		private long _closeTS = NOT_SPECIFIED;
		
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
			//TODO openTS closeTS
			return this;
		}
//		public Builder openTS(long openTS) {
//			_openTS = openTS;
//			return this;
//		}
//		public Builder closeTS(long closeTS) {
//			_closeTS = closeTS;
//			return this;
//		}
		public Survey build() {
			
			Survey survey = new Survey();
			survey.idx = this._idx;
			survey.title = this._title;
			survey.content = this._content;
			survey.received = this._received;
			survey.TS = this._TS;
			survey.checkTS = this._checkTS;
			survey.favorite = this._favorite;
			survey.roomCode = this._roomCode;
			survey.appendix = this._appendix;
			survey.sender = this._sender;
			survey.receivers = this._receivers;
//			survey.openTS = this._openTS;
//			survey.closeTS = this._closeTS;
			return survey;
		}
	}
}
