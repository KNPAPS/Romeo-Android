package kr.go.KNPA.Romeo.Chat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.database.Cursor;

public class Room {
	//ArrayList<Chat> chats;
	String roomCode;
	int type;
	ArrayList<User> users;
	
	public Room() {
		//chats = new ArrayList<Chat>();
	}

	public Room(int type, String roomCode, ArrayList<User> users) {
		this.type = type;
		this.roomCode = roomCode;
		this.users = users;
	}
	

	public Room(Context context, int type, String roomCode) {
		DBProcManager.sharedManager(context);
		
		this.roomCode = roomCode;
		this.type = type;
		
		this.users = Room.getUsers(context, this.roomCode);
		/*
		// TODO 보낸 사람과 받는 사람이 같으면, 두번 등록된다. 따라서 검사해서 있으면 넣지 않도록 한다.
		if(isUserInRoom(chat.sender.idx) == false) { 
			users.add(chat.sender);
		}
		// TODO new DB 와 연동
		*/
	}

	public boolean isUserInRoom(String userIdx) {
		for(int i=0; i<users.size(); i++) {
			if( users.get(i).idx.equals(userIdx) )
				return true;
		}
		return false;
	}

	public static String makeRoomCode(Context context) {
		String str = UserInfo.getUserIdx(context)+":"+System.currentTimeMillis();
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}
	
	public ArrayList<User> getUsers(Context context) {
		ArrayList<User> roomUsers = Room.getUsers(context, roomCode);
		
		// TODO : 필요한가??
		this.users = roomUsers;
		return roomUsers;
	}
	
	public static ArrayList<String> getUsers(Context context, User sender, ArrayList<User> receivers) {
		ArrayList<String> roomUsers = new ArrayList<String>();
		roomUsers.add(sender.idx);
		
		for(int i=0; i<receivers.size(); i++) {
			String receiverIdx = receivers.get(i).idx; 
			if( roomUsers.contains(receiverIdx) == false )
				roomUsers.add(receiverIdx);
		}
		
		return roomUsers;
	}
	
	public static ArrayList<User> getUsers(Context context, String roomCode) {
		Cursor cursorRoomUsers = DBProcManager.sharedManager(context).chat().getReceiverList(roomCode);
		ArrayList<User> roomUsers = new ArrayList<User>(cursorRoomUsers.getCount());
		
		cursorRoomUsers.moveToFirst();
		while(!cursorRoomUsers.isAfterLast()) {
			roomUsers.add( new User( cursorRoomUsers.getString(cursorRoomUsers.getColumnIndex(ChatProcManager.COLUMN_USER_HASH)) ) );
			cursorRoomUsers.moveToNext();
		}
		
		return roomUsers;
	}
}
