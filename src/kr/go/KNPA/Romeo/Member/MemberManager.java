package kr.go.KNPA.Romeo.Member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.UserRegisterActivity;
import kr.go.KNPA.Romeo.UserRegisterEditView;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.EventEnum;
import kr.go.KNPA.Romeo.Config.StatusCodeEnum;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Util.CollectionFactory;
import kr.go.KNPA.Romeo.Util.Connection;
import kr.go.KNPA.Romeo.Util.DBManager;
import kr.go.KNPA.Romeo.Util.ImageManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;


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
	
	
	public userWithIdx(String idx) {
		User user = cachedUsers.get(idx);
		if ( user != null ) {
			return user;
		} else {
			Payload request = new Payload(Event.User.getUserInfo())
									.setData( new Data().add(0, Data.KEY_USER_HASH, idx) );
			
			Connection conn = new Connection.Builder(request.toJson()).build();
			conn.request();
			Payload respl = new Payload(conn.getResponsePayload());
			
			//성공적으로 가져왔으면 DATA에서 정보를 꺼내 새 member 객체 생성 후 리턴
			if ( respl.getStatusCode() == StatusCodeEnum.SUCCESS ) {
				HashMap<String,Object> hm = respl.getData().get(0);
				
				Member member = new Member(hm.get(Data.KEY_USER_HASH).toString());
				member.setDeptFullName(hm.get(Data.KEY_DEPT_FULL_NAME).toString());
				member.setDeptName(hm.get(Data.KEY_DEPT_NAME).toString());
				member.setMemberName(hm.get(Data.KEY_USER_NAME).toString());
				member.setRankIdx( (Integer) hm.get(Data.KEY_USER_RANK));
				member.setMemberRole(hm.get(Data.KEY_USER_ROLE).toString());
				
				return member;
			} else {
				return null;
			}
	}
	
	public userWithIdxs(String[] idxs) {
		
	}
	
	private Connection download(String data) {
		String url = Connection.HOST_URL + "/member/getUserInfo";
		
		Connection conn = new Connection.Builder()
								.url(url)
								.async(false)
								.type(Connection.TYPE_GET)
								.data(data)
								.dataType(Connection.DATATYPE_JSON)
								.build();
		if(conn.request() == Connection.HTTP_OK) {
			return conn;
		}
		return conn;
	}
	
	private JSONObject JSONInfo(String data) {
		Connection conn = download(data);
		if(conn != null) return conn.getJSON();
		return null;
	}
	
	private String stringInfo(String data) {
		Connection conn = download(data);
		if(conn != null) return conn.getResponse();
		return null;
	}
	
	
	public User getUserWithIndex(int idx) {
		User user = null;
		try {
			user= users.get(idx);
		} catch(Exception e) {
			user = null;
		}
		 
		if( user == null || users.size() < 1) {
			// 정보가 존재하지 않으므로 Full Download
			HashMap<String, Object> dic= new HashMap<String, Object>();
			int[] idxs = {idx};
			String[] fields = {"*"};
			dic.put("idx", idxs);
			dic.put("fields", fields);

			Gson gson = new Gson();
			String data = gson.toJson(dic);
			String jsonString = stringInfo(data);
			user = gson.fromJson(jsonString, User.class);
			
			users.put(idx, user);
			//users.add(idx,  user);
			
			return user;
		} else {
			HashMap<String, Object> dic= new HashMap<String, Object>();
			int[] idxs = {idx};
			String[] fields = {"TS"};
			dic.put("idx", idxs);
			dic.put("fields", fields);
			
			Gson gson = new Gson();
			String jsonString = stringInfo(gson.toJson(dic));
			User _obj = gson.fromJson(jsonString, User[].class)[0];
			
			long serverTS = _obj.TS;
			long now = System.currentTimeMillis();
			
			if((now - serverTS) > 3 * 60 * 60 * 1000 ) {
				if(serverTS > user.TS ) {
					// 서버가 newer
					// full download (async)
					
					HashMap<String, Object> _dic= new HashMap<String, Object>();
					int[] _idxs = {idx};
					String[] _fields = {"*"};
					dic.put("idx", _idxs);
					dic.put("fields", _fields);
				
				} else {
					// Do Nothing.
					
				}
			}
		}
		
		
		return user;

	}
	
	public void getMembers(Context context) {
		JSONObject jo = null;
		if(users == null || users.size() < 1 ) {
		// 버전을 비교하는 코드를 삽입한다.
			String url = Connection.HOST_URL + "/member/getList";
			Connection conn = new Connection.Builder()
									.url(url)
									.async(false)
									.type(Connection.TYPE_GET)
									.dataType(Connection.DATATYPE_JSON)
									.build();
			int requestCode;
			try {
				requestCode = conn.request();
			} catch (RuntimeException e) {
				throw e;
			}
			
			if( requestCode == Connection.HTTP_OK) {
				//Gson gson = new Gson();
				//String json = conn.getResponse();
				//result = gson.fromJson(json, HashMap.class);
				jo = conn.getJSON();
			}
		} else {
			//result = users;
		}
		
		Department.root().fetch(context, jo);
	}
	
	public String dbToJSON(Context context) {
		StringBuilder json = new StringBuilder();
		
		DBManager dbManager = new DBManager(context);
		SQLiteDatabase db = dbManager.getReadableDatabase();
		
		String sql = "SELECT * FROM "+DBManager.TABLE_DEPARTMENT+ 
					" WHERE enabled=1 AND shown=1 ORDERBY sequence ASC";
		Cursor c = db.rawQuery(sql, null);
		
		;
		for(int i=0; i<c.getCount(); i++) {
			String level1 = (c.getString(c.getColumnIndex("level1"))).trim();
			String level2 = (c.getString(c.getColumnIndex("level2"))).trim();
			String level3 = (c.getString(c.getColumnIndex("level3"))).trim();
			String level4 = (c.getString(c.getColumnIndex("level4"))).trim();
			String level5 = (c.getString(c.getColumnIndex("level5"))).trim();
			String level6 = (c.getString(c.getColumnIndex("level6"))).trim();
			
			
			
			if(!c.isLast()) c.moveToNext();
		}
		
		
		db.close();
		dbManager.close();
		return json.toString();
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
