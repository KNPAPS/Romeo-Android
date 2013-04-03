package kr.go.KNPA.Romeo.Base;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Config.Constants;
/**
 * 대화나 문서 등 사용자끼리 주고받는 메세지들의 base class\n
 * 이 객체를 chat, document, survey 객체가 상속받아 각자 추가적인 내용을 붙이고\n
 * 실제 payload에는 이 클래스의 하위 객체를 담아서 보낸다
 * 
 * @author 채호식, 최영우
 * @since 2013.4.2
 */
public abstract class Message {
	/**
	 * @name 상수
	 * @{
	 */
	public static final int MESSAGE_TYPE_CHAT = 0;
	public static final int MESSAGE_TYPE_DOCUMENT = 1;
	public static final int MESSAGE_TYPE_SURVEY = 2;

	public static final String MESSAGE_KEY_CHAT = "CHAT";
	public static final String MESSAGE_KEY_DOCUMENT = "DOCUMENT";
	public static final String MESSAGE_KEY_SURVEY = "SURVEY";
	/**
	 * @}
	 * @name 고정값
	 * 모든 메세지 타입이 공통적으로 포함하고 있는 필드들 중 생성된 후 변하지 않는 값\n
	 * @{
	 */
	protected final String msgHash = null;
	protected final int msgType = Constants.NOT_SPECIFIED;
	protected final String title = null;
	protected final String content = null;
	protected final String senderHash = null;
	protected final ArrayList<String> receiversHash = null;
	protected final long createdTS = Constants.NOT_SPECIFIED;
	/** @} */
	
	/**
	 * @name 변수
	 * 생성된 후 특정 시점에 변할 수 있는 값
	 * @{
	 */
	protected boolean isChecked = false;
	protected long checkedTS = Constants.NOT_SPECIFIED;
	/**
	 * @}
	 * @name getters
	 * @{
	 */
	public int getMsgType() { return this.msgType; }
	public String getMsgHash() { return this.msgHash; }
	public String getTitle() { return this.title; }
	public String getContent() { return this.content; }
	public String getSenderHash() { return this.senderHash; }
	public ArrayList<String> getReceiversHash() { return this.receiversHash; }
	public long getCreatedTS() { return this.createdTS; }
	public boolean isChecked() { return this.isChecked; }
	public long getCheckedTS() { return this.checkedTS; }
	/** @} */
	
	/**
	 * @name setters
	 * @{
	 */
	public Message setChecked(boolean v) { this.isChecked = v; return this; }
	public Message setCheckedTS(long TS) { this.checkedTS = TS; return this; }
	/** @} */
	
	public abstract static class Builder {
		protected String msgHash = null;
		protected int type = Constants.NOT_SPECIFIED;
		protected String title = null;
		protected String content = null;
		protected String senderHash = null;
		protected ArrayList<String> receiversHash = null;
		protected long createdTS	= Constants.NOT_SPECIFIED;
		
		public Builder msgHash(String v) { this.msgHash = v; return this; }
		public Builder title(String v) { this.title = v; return this; }
		public Builder content(String v) { this.content = v; return this; }
		public Builder senderHash(String v) { this.senderHash = v; return this; }
		public Builder receiversHash(ArrayList<String> v) { this.receiversHash = v; return this; }
		public Builder createdTS(long v) { this.createdTS = v; return this; }
		
		public abstract Message build();
		
	}

}
//	// Implements Parcelable
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//	
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeLong(idx);
//		dest.writeString(title);
//		dest.writeInt(type);
//		dest.writeString(content);
//		dest.writeParcelable(appendix, flags); 
//		dest.writeParcelable(sender, flags);
//		dest.writeTypedList(receivers);
//		dest.writeLong(TS);
//		dest.writeLong(checkTS);
//		boolean[] ba = {received, checked};
//		dest.writeBooleanArray(ba);
//	}
//	
//	public void readFromParcel(Parcel source) {
//		idx = source.readLong();
//		title = source.readString();
//		type = source.readInt();
//		content = source.readString();
//		appendix = source.readParcelable(Appendix.class.getClassLoader());
//		sender = source.readParcelable(User.class.getClassLoader());
//		receivers = source.createTypedArrayList(User.CREATOR);
//		TS = source.readLong();
//		checkTS = source.readLong();
//		boolean[] ba = source.createBooleanArray();
//		received = ba[0];
//		checked = ba[1];		
//	}
//	
//	//	getting Message Uncheckers
//	public static long[] getUncheckersInIntArrayWithMessageTypeAndIndex(int type, long index) {
//		String __uncheckers =""; //TODO GCMMessageSender.requestUncheckers(type, index);
//		String[] _uncheckers = __uncheckers.split("/[^0-9]+");
//		long[] uncheckers = new long[(_uncheckers.length-2)];
//		for(int i=0; i< uncheckers.length; i++) {
//			uncheckers[i] = Long.getLong(_uncheckers[i+1]);
//		}
//		
//		return uncheckers;
//	}
//	
//	public static long[] getUncheckersInIntArrayWithMessage(Message message) {
//		return getUncheckersInIntArrayWithMessageTypeAndIndex(message.type, message.idx);
//	}
//	
//	public static ArrayList<User> getUncheckersInUsersWithMessageTypeAndIndex(int type, long index) {
//		long[] uncheckers = getUncheckersInIntArrayWithMessageTypeAndIndex(type, index);
//		return User.getUsersWithIndexes(uncheckers);
//	}
//	
//	public static ArrayList<User> uncheckersInUsersWithMessageTypeAndIndex(int type, long index) {
//		long[] uncheckers = getUncheckersInIntArrayWithMessageTypeAndIndex(type, index);
//		return User.usersWithIndexes(uncheckers);
//	}
//	
//	public static ArrayList<User> getUncheckersInUsersWithMessage(Message message) {
//		long[] uncheckers = getUncheckersInIntArrayWithMessage(message);
//		return User.getUsersWithIndexes(uncheckers);
//	}
//	
//	public static ArrayList<User> uncheckersInUsersWithMessage(Message message) {
//		long[] uncheckers = getUncheckersInIntArrayWithMessage(message);
//		return User.usersWithIndexes(uncheckers);
//	}
//	
//	public void setChecked(Context context) {
//		if(this.checked == false) {
//			
//			// TODO : make Async
//			boolean result = false;//GCMMessageSender.setMessageChecked(context, this.type, this.idx);
//			
//			if(result == true) {
//				
//				DBManager dbManager = new DBManager(context);
//				SQLiteDatabase db = dbManager.getWritableDatabase();
//				
//				String tableName =  Message.getTableNameWithMessageType(this.type);
//				
//				ContentValues vals = new ContentValues();
//				vals.put("checked", 1);
//				db.update(tableName, vals, "idx=?", new String[] {this.idx+""});
//				
//				this.checked = true;
//				
//			}
//			
//		} else {
//			return;
//		}
//	}
//	
//	
//	public static String getTableNameWithMessageType(int type) {
//		int messageType = type/MESSAGE_TYPE_DIVIDER;
//		int messageSubType = type%MESSAGE_TYPE_DIVIDER;
//		
//		String tableName = null;
//		if(messageType == Message.MESSAGE_TYPE_CHAT) {
//			switch(messageSubType) {
//				case Chat.TYPE_COMMAND : tableName = DBManager.TABLE_COMMAND; break;
//				case Chat.TYPE_MEETING : tableName = DBManager.TABLE_MEETING; break;
//			}
//		} else if(messageType == Message.MESSAGE_TYPE_DOCUMENT) {
//			switch(messageSubType) {
//				case Document.TYPE_DEPARTED : tableName = DBManager.TABLE_DOCUMENT; break;
//				case Document.TYPE_RECEIVED : tableName = DBManager.TABLE_DOCUMENT; break;
//				case Document.TYPE_FAVORITE : tableName = DBManager.TABLE_DOCUMENT; break;
//			}
//			
//		} else if(messageType == Message.MESSAGE_TYPE_SURVEY) {
//			switch(messageSubType) {
//				case Survey.TYPE_DEPARTED : tableName = DBManager.TABLE_SURVEY; break;
//				case Document.TYPE_RECEIVED : tableName = DBManager.TABLE_SURVEY; break;
//			}
//		}
//		
//		return tableName;
//		
//	}
//	
//	public static String getTableNameWithMassage(Message message) {
//		return getTableNameWithMessageType(message.type);
//	}
