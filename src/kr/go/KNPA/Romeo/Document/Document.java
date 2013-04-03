package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.MimeTypeEnum;

public class Document extends Message  {
	
	protected static final int msgType = MESSAGE_TYPE_DOCUMENT;
	//! 문서의 포워딩 정보.
	private ArrayList< Forwarding > forwardInfo;
	//! 첨부파일 정보
	private ArrayList< Attachment > attachments; 
	
	// Specific Variables not to be sent
	public boolean isFavorite = false;
	
	/**
	 * 문서의 포워딩 정보 가져오기
	 * [ {"forwarderHash":"전달자1해쉬","forwardComment":"전달자1코멘트" }, {"forwarderHash":"전달자2해쉬","forwardComment":"전달자2코멘트" },, ... ] 구조로 되어 있다.
	 * @return
	 */
	public ArrayList< Forwarding > getForwardInfo() {
		return this.forwardInfo;
	}
	
	/**
	 * 전달자 추가
	 * @param forwarderHash 전달한사람 hash
	 * @param forwardComment 전달한사람이 쓴 코멘트
	 * @return
	 */
	public Document addForward(String forwarderHash, String forwardComment) {
		this.forwardInfo.add( new Forwarding(forwarderHash, forwardComment) );
		return this;
	}
	
	/**
	 * 첨부파일 정보 가져오기
	 * [ {"fileName":"파일1이름","content":"파일1내용" }, ... ] 구조로 되어 있다.
	 * @return
	 */
	public ArrayList< Attachment > getAttachments(){
		return attachments;
	}
	
	/**
	 * 첨부파일 추가
	 * @param name 파일이름
	 * @param contentBytes 바이트 어레이로 된 파일 내용 
	 * @return
	 */
	public Document addAttachment(String name, byte[] contentBytes) {
		this.attachments.add(new Attachment(name, contentBytes));
		return this;
	}
	
	/**
	 * 전달 정보 객체
	 * @author 최영우
	 * @since 2013.4.2
	 */
	public class Forwarding {
		private String forwarderHash;
		private String forwardComment;
		
		public Forwarding(String hash, String comment) {
			this.setForwardComment(comment);
			this.setForwarderHash(hash);
		}

		public String getForwarderHash() {
			return forwarderHash;
		}

		public void setForwarderHash(String forwarderHash) {
			this.forwarderHash = forwarderHash;
		}

		public String getForwardComment() {
			return forwardComment;
		}

		public void setForwardComment(String forwardComment) {
			this.forwardComment = forwardComment;
		}		
	}
	
	/**
	 * 첨부파일 객체
	 * @author 최영우
	 * @since 2013.4.2
	 */
	public class Attachment {
		
		private String fileName;
		private byte[] content;
		private MimeTypeEnum mimeType;
		
		public Attachment(String name, byte[] contentBytes) {
			setFileName(name);
			setContent(contentBytes);
		}

		public String getFileName() {
			return fileName;
		}

		public Attachment setFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public byte[] getContent() {
			return content;
		}

		public Attachment setContent(byte[] content) {
			this.content = content;
			return this;
		}

		public MimeTypeEnum getMimeType() {
			return mimeType;
		}

		public Attachment setMimeType(MimeTypeEnum mimeType) {
			this.mimeType = mimeType;
			return this;
		}
		
	}
}
