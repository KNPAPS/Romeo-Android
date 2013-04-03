package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.Constants;

public class Survey extends Message{

	protected static final int msgType = MESSAGE_TYPE_SURVEY;
	//! 설문조사 시작 시간
	private long openTS = Constants.NOT_SPECIFIED;
	//! 설문조사 종료 시간
	protected long closeTS = Constants.NOT_SPECIFIED;
	//! 응답여부
	protected boolean isAnswered = false;
	//! 응답한 시간
	protected long answeredTS = Constants.NOT_SPECIFIED;
	//! 설문조사 복수 선택 가능 여부
	protected boolean isMultipleChoicePossible = false;
	//! 설문조사 선택지
	protected ArrayList< SurveyOption > options = null;
	
	/**
	 * @name getters
	 * @{
	 */
	public long getOpenTS() { return openTS; }
	public long getCloseTS() { return closeTS; }
	public boolean isAnswered() { return isAnswered; }
	public boolean isMultipleChoicePossible() { return isMultipleChoicePossible; }
	public long getAnsweredTS() { return answeredTS; }
	/** @} */
	
	/**
	 * @name setters
	 * @{
	 */
	public Survey setOpenTS(long ts) { this.openTS = ts ; return this; }
	public Survey setCloseTS(long ts) { this.closeTS = ts; return this; }
	public Survey setAnswered(boolean v) { this.isAnswered = v; return this; }
	public Survey setMultipleChoicePossible(boolean v) { this.isMultipleChoicePossible = v; return this; }
	public Survey setAnsweredTS(long ts) { this.answeredTS = ts; return this; }
	/** @} */
	
	/**
	 * 설문조사 선택지 객체
	 * @author 최영우
	 * @since 2014.4.2
	 */
	public class SurveyOption {
		//! 선택지 내용
		private String description;
		//! 득표수
		private int poll;
		public SurveyOption(String description) {
			this.setDescription(description) ;
			this.setPoll(0);
		}
		public String getDescription() {
			return description;
		}
		public SurveyOption setDescription(String description) {
			this.description = description; return this;
		}
		public int getPoll() {
			return poll;
		}
		public SurveyOption setPoll(int poll) {
			this.poll = poll; return this;
		}
		
	}
	
}
