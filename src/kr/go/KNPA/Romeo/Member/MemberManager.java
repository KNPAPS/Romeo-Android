package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.UserRegisterEditView;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.DBManager;
import kr.go.KNPA.Romeo.Util.ImageManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;


public class MemberManager {
	public static int USER_IDX = 0;
	public static int USER_NAME = 1;
	public static int USER_DEPARTMENT = 2;
	public static int USER_RANK = 3;
	public static int USER_PIC = 4;
	public static int USER_OBJECT = 5;
	public static int USER_ALL = 6;
	
	private static MemberManager _instance = null;
	
	
	private static HashMap<String, User> cachedUsers = null;	//! 캐시된 사람들 목록
	
	// Apply Singleton Type
	public static MemberManager sharedManager() {
		if(_instance == null) {
			_instance = new MemberManager();
		}
		return _instance;
	}
	
	
	/**
	 * 다른 멤버의 정보 저장
	 * @param member Member 객체
	 */
	public static void cacheUser(User user) {
		cachedUsers.put(user.idx, user);
	}
	
	
	public User getUser(String idx) {
		User user = cachedUsers.get(idx);
		if ( user != null ) {
			return user;
		} else {
			Payload request = new Payload(Event.User.getUserInfo())
									.setData( new Data().add(0, Data.KEY_USER_HASH, idx) );
			
			//Connection conn = new Connection(this).callBack(callBackEvent).requestPayloadJSON(request.toJson()).request();
			Connection conn = new Connection().requestPayloadJSON(request.toJSON()).request();
			Payload response = conn.getResponsePayload();
			
			if ( response.getStatusCode() == StatusCode.SUCCESS ) {
				HashMap<String,Object> hm = response.getData().get(0);
				
				Department dep = new Department.Builder()
												.idx((String)hm.get(Data.KEY_DEPT_HASH))
												.name((String)hm.get(Data.KEY_DEPT_NAME))
												.nameFull((String)hm.get(Data.KEY_DEPT_FULL_NAME))
												.sequence(Long.parseLong((String)hm.get(Data.KEY_DEPT_SEQUENCE)))
												.build();
				
				user = new User.Builder()
									.idx((String)hm.get(Data.KEY_USER_HASH))
									.name((String)hm.get(Data.KEY_USER_NAME))
									.rank(Integer.parseInt((String)hm.get(Data.KEY_USER_RANK)))
									.role((String)hm.get(Data.KEY_USER_ROLE))
									.department(dep)
									.build();

				cacheUser(user);
				return user;
			} else {
				return null;
			}
		}
	}
	
	public ArrayList<User> getUsers(ArrayList<String> idxs) {

		Data data = new Data();
		for(int i=0; i< idxs.size(); i++) {
			data.add(i, Data.KEY_USER_HASH, idxs.get(i));
		}
		
		Payload request = new Payload(Event.User.getUserInfo())
								.setData( data );
		
		Connection conn = new Connection().requestPayloadJSON(request.toJSON()).request();
		Payload response = conn.getResponsePayload();
		
		if ( response.getStatusCode() == StatusCode.SUCCESS ) {
			ArrayList<User> users = new ArrayList<User>();
			Data responseData = response.getData();
			
			for(int i=0; i<responseData.size(); i++) {
				HashMap<String,Object> hm = response.getData().get(i);
				
				Department dep = new Department.Builder()
												.idx((String)hm.get(Data.KEY_DEPT_HASH))
												.name((String)hm.get(Data.KEY_DEPT_NAME))
												.nameFull((String)hm.get(Data.KEY_DEPT_FULL_NAME))
												.sequence(Long.parseLong((String)hm.get(Data.KEY_DEPT_SEQUENCE)))
												.build();
				
				User user = new User.Builder()
									.idx((String)hm.get(Data.KEY_USER_HASH))
									.name((String)hm.get(Data.KEY_USER_NAME))
									.rank(Integer.parseInt((String)hm.get(Data.KEY_USER_RANK)))
									.role((String)hm.get(Data.KEY_USER_ROLE))
									.department(dep)
									.build();

				cacheUser(user);
				users.add(user);
			}
				
			return users;
		} else {
			return null;
		}
	}
	
	
	/**
	 * 해당 hash를 가진 사람에 대한 자료 cache에서 삭제
	 * @param hash
	 * @return
	 */
	public static void removeCachedMember(String hash) {
		cachedUsers.remove(hash);
	}
	
	
	public Bundle registerUser(Context context, Bundle b) {
		String name = b.getString("name");
		String[] departments = b.getStringArray("departments");
		int rank = b.getInt("rank");
		String role = b.getString("role");
		String _picURI = b.getString("picURI");
		Uri picURI = null;
		if(_picURI != null) Uri.parse(_picURI);
		
		
		StringBuilder sb = new StringBuilder();
		
		String q = "\"";
		String c = ":";
		
		sb.append("{");
		sb.append(q).append("name").append(q).append(c).append(q).append(name).append(q).append(",");
		sb.append(q).append("levels").append(q).append(c).append("[");
		for(int i=0; i<UserRegisterEditView.DROPDOWN_MAX_LENGTH; i++) {
			if(departments[i]==null || departments[i].equals(context.getString(R.string.none))) break;
			if(i!= 0) sb.append(",");
			sb.append(q).append(departments[i]).append(q);
		}
		sb.append("]").append(",");
		if(role != null && role.length()>0) 
			sb.append(q).append("role").append(q).append(c).append(q).append(role).append(q).append(",");
		sb.append(q).append("rank").append(q).append(c).append(rank);
	
		sb.append("}");
		
		String json = null;
		
		String url = Connection.HOST_URL + "/member/register";
		Connection conn = new Connection.Builder()
								.url(url)
								.async(false)
								.data(sb.toString())
								.type(Connection.TYPE_POST)
								.dataType(Connection.DATATYPE_JSON)
								.build();
		int requestCode = conn.request();
		
		if( requestCode == Connection.HTTP_OK) {
			json = conn.getResponse();
		}
	
		
		Bundle result = new Bundle();
		if(json == null ){
			result.putBoolean("status",false);
			return result;
		}
		
		JSONObject jo = null;
		try {
			jo = new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(jo == null ){
			result.putBoolean("status",false);
			return result;
		} 
		
		try {
			result.putInt("status", jo.getInt("status"));
			result.putLong("userIdx", jo.getLong("userIdx"));
			result.putLong("departmentIdx", jo.getLong("departmentIdx"));			
		} catch (JSONException e) {
			result.putBoolean("status",false);
			return result;
		}

		
		
		// TODO 사진 업로드하는 코드를 삽입한다.
		if(picURI != null)
			ImageManager.bitmapFromURI(context, picURI);
		// 사진 업로드는 비동기로 이루어지며,
		// sharedPreference에 사진 업로드 여부를 체크하는 변수를 할당한다. ( 추후 사진 변경시 등등 활용 할 수 있기 때문이다.)
		// 앱이 켜질때 혹은 조직도 등 특정 조건 하에서 사진 전송 여부를 확인하여 되어있지 않았다면 수시로 업로드 할 수 있는 기회를 제공하도록 한다.
		
		
		return result;
	}
	
	
}
