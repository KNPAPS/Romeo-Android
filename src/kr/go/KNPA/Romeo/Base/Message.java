package kr.go.KNPA.Romeo.Base;

public class Message {

	public static final int MESSAGE_TYPE_CHAT = 0;
	public static final int MESSAGE_TYPE_DOCUMENT = 1;
	public static final int MESSAGE_TYPE_SURVEY = 2;
	
	public static final int NOT_SPECIFIED = -777;
	
	public long idx;
	public int type;
	public String title;
	public String content;
	public Appendix appendix;
	public boolean received;
	
	public Message() {
		
	}

}
