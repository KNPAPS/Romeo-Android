package kr.go.KNPA.Romeo.Member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.KNPA.Romeo.Util.CollectionFactory;
import kr.go.KNPA.Romeo.Util.Connection;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
	private HashMap<Integer, User> users = new HashMap<Integer, User>();
	
	// Apply Singleton Type
	public static MemberManager sharedManager() {
		if(_instance == null) {
			_instance = new MemberManager();
		}
		return _instance;
	}
	
	private Connection download(String data) {
		String url = "http://172.16.7.52/Projects/CI/index.php/member/getUserInfo";
		
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
	
	public void getMembers() {
		JSONObject jo = null;
		if(users == null || users.size() < 1 ) {
		// 버전을 비교하는 코드를 삽입한다.
			String url = "http://172.16.7.52/Projects/CI/index.php/member/getList";
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
		
		Department.root().fetch(jo);
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
	
}
