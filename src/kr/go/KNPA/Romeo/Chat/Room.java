package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Encrypter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Room {
	private static final String TAG = Room.class.getName();
	private String roomCode;
	private String title;
	private int type;
	private ArrayList<String> chatters;
	private HashMap<String,Long> lastReadTS; 
	private boolean created = false;
	private Context mContext;
	
	/**
	 * 기존에 설정된 roomCode를 가지고 해당 방에 대한 정보를 DB와 서버에서 가져옴
	 * @param context
	 * @param roomCode
	 */
	public Room(Context context, String roomCode){
		this.mContext = context;
		if ( DBProcManager.sharedManager(mContext).chat().roomExists(roomCode) == false ) {
			throw new RuntimeException("해당 룸해쉬에 대한 방이 존재하지 않음 : "+roomCode);
		}
		//룸 해쉬 설정
		setRoomCode(roomCode);
		
		//방 기본 정보 가져오기 : 타이틀, 타입
		Cursor c = DBProcManager.sharedManager(mContext).chat().getRoomInfo(roomCode);
		c.moveToNext();
		this.title = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_TITLE));
		this.type = c.getInt(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_TYPE));
		c.close();
		
		//채팅방 참가자들 정보 가져오기
		Cursor cursor = DBProcManager.sharedManager(mContext).chat().getRoomChatters(getRoomCode(),true);
		ArrayList<String> chatters = new ArrayList<String>(cursor.getCount());
		
		while(cursor.moveToNext()) {
			chatters.add( cursor.getString(cursor.getColumnIndex(ChatProcManager.COLUMN_USER_IDX)) );
		}
		cursor.close();
		this.chatters = chatters;
		
		//참가자들이 마지막으로 방에 입장한 시간을 서버에서 가져온다
		pullLastReadTS();
		
		//생성완료 플래그
		created(true);
	}
	
	/**
	 * 새 채팅방을 만들었을 때 메세지를 보내기 전 임시로 생성된 채팅방 객체.\n
	 * 첫 메세지를 보낼 때 create() 메소드를 호출하여 실제 방으로 만든다.
	 * @param context
	 * @param type
	 * @param initChatters
	 */
	public Room(Context context, int type, ArrayList<String> initChatters ) {
		this.mContext = context;
		setType(type);
		chatters = new ArrayList<String>(initChatters.size());
		chatters.addAll(initChatters);
		created(false);
	}

	/**
	 * Chatter들 중 자기 자신을 제외한 사람들의 목록
	 * @return
	 */
	public ArrayList<String> getReceivers() {
		String userIdx = UserInfo.getUserIdx(mContext);
		ArrayList<String> receiversIdx = new ArrayList<String>(this.chatters.size()-1);
		for(int i=0; i<this.chatters.size(); i++) {
			if ( !this.chatters.get(i).equals(userIdx) ) {
				receiversIdx.add(this.chatters.get(i));
			}
		}
		return receiversIdx;
	}
	
	public ArrayList<String> getChatters(){
		return chatters;
	}
	
	public void addChatter(String userIdx) {
		chatters.add(userIdx);
		DBProcManager.sharedManager(mContext).chat().addUserToRoom(userIdx, getRoomCode());
	}
	
	public void addChatters(ArrayList<String> userIdxs) {
		chatters.addAll(userIdxs);
		DBProcManager.sharedManager(mContext).chat().addUsersToRoom(userIdxs, getRoomCode());		
	}
	
	public void removeChatter(String userIdx) {
		chatters.remove(userIdx);
		DBProcManager.sharedManager(mContext).chat().removeUserFromRoom(userIdx, getRoomCode());
	}
	
	/**
	 * @name static method
	 * @{
	 */
	public static String makeRoomCode(Context context) {
		String str = UserInfo.getUserIdx(context)+":"+System.currentTimeMillis();
		return Encrypter.sharedEncrypter().md5(str);
	}
	/**@}*/

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}
	
	public int getType() { return this.type; }
	public void setType(int type) { this.type = type; }
	
	/**
	 * 방제목 가져오기
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * 방제목설정
	 * @param title
	 */
	public void setTitle(String title){
		this.title = title;
		DBProcManager.sharedManager(mContext).chat().updateRoomTitle(getRoomCode(), title);
	}
	
	/**
	 * 채팅방이 생성될 때 기본 채팅방 제목 설정
	 */
	public void setBaseTitle() {
		ArrayList<String> receiversIdx = this.getReceivers();
		
		ArrayList<User> receivers= MemberManager.sharedManager().getUsers(receiversIdx);
		
		String title = "";
		if ( receivers.size() < 1 ) {
			//상대방이없으면 빈방
			setTitle("빈 방");
		} else if ( receivers.size() == 1 ) {
			//1:1채팅이면 상대방 계급+이름
			title = Constants.POLICE_RANK[receivers.get(0).rank]+" "+receivers.get(0).name;
			setTitle(title);
		} else {
			//여러명 채팅이면 리시버들 이름
			for( int i=0; i<receivers.size(); i++) {
				title += Constants.POLICE_RANK[receivers.get(i).rank]+" "+receivers.get(i).name+", ";
			}
			title = title.substring(0,title.length()-2);
			
			//길이가 너무 길면 짜름
			final int MAX_TITLE_LENGTH = 17;
			if ( title.length() >= MAX_TITLE_LENGTH ) {
				title = title.substring(0,MAX_TITLE_LENGTH)+"...";
			}
			setTitle(title);
		}
		
	}
	
	/**
	 * 서버에서 lastReadTS 정보 가져와서 this.lastReadTS에 할당
	 */
	public void pullLastReadTS(){
		Data reqData = new Data();
		reqData.add(0,KEY.CHAT.ROOM_CODE,this.getRoomCode());
		
		Payload request = new Payload().setEvent(Event.Message.Chat.pullLastReadTS()).setData(reqData);
		Payload response = new Connection().async(false).requestPayload(request).request().getResponsePayload();
		if ( response != null && response.getData() != null ) {
			Data respData = response.getData();
			lastReadTS = new HashMap<String,Long>();
			for( int i=0; i<respData.size(); i++ ) {
				lastReadTS.put( (String)respData.get(i).get(KEY.USER.IDX), Long.parseLong(respData.get(i).get(KEY.CHAT.LAST_READ_TS).toString()) );
			}
		}
	}
	
	/**
	 * 서버에 내가 TS 시간에 마지막으로 읽었다고 업데이트를 함\n
	 */
	public void updateLastReadTS(long TS){
		String userIdx = UserInfo.getUserIdx(mContext);
		
		setLastReadTS(userIdx, TS);
		Data reqData = new Data();
		reqData.add(0,KEY.USER.IDX,userIdx);
		reqData.add(0,KEY.CHAT.LAST_READ_TS,(Long)TS);
		
		Payload request = new Payload().setEvent(Event.Message.Chat.updateLastReadTS()).setData(reqData);
		new Connection().async(false).requestPayload(request).request();
	}
	
	/**
	 * 다른 사람에게서 GCM으로 새 lastReadTS에 대한 정보가 왔을 때 업데이트
	 * @param userHash
	 * @param TS
	 */
	public void setLastReadTS(String userHash, long TS ) {
		lastReadTS.put(userHash, TS);
	}
	
	/**
	 * lastReadTS getter. 만약 싱크가 안맞으면 서버에서 가져온 후 리턴
	 * @return
	 */
	public HashMap<String,Long> getLastReadTS(){
		return lastReadTS;
	}
	
	public boolean isCreated() {
		return created;
	}

	public void created(boolean created) {
		this.created = created;
	}
	
	/**
	 * 임시로 만들어져 있는 상태 : isCreated()==false의 방을 DB와 서버에 등록하고 생성함
	 * @return
	 */
	public boolean create() {
		this.setRoomCode( Room.makeRoomCode(mContext) );
		
		Data reqData = new Data();
		reqData.add(0,KEY.CHAT.ROOM_CODE,this.getRoomCode());
		reqData.add(0,KEY.CHAT.ROOM_MEMBER, this.getChatters());
		
		Payload request = new Payload().setEvent(Event.Message.Chat.createRoom()).setData(reqData);
		Payload response = new Connection().async(false).requestPayload(request).request().getResponsePayload();

		if ( response == null ) {
			return false;
		} else {
			if ( response.getStatusCode() == StatusCode.SUCCESS ) {
				DBProcManager.sharedManager(mContext).chat().createRoom(this.getChatters(), this.getType(), this.getRoomCode());
				pullLastReadTS();
				created(true);
				
				//방 기본 타이틀 설정
				setBaseTitle();
				return true;
			}
			Log.e(TAG,"response status code : "+response.getStatusCode());
			return false;
		}
	}
}
