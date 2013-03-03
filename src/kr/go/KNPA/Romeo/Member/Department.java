package kr.go.KNPA.Romeo.Member;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Department {
	public static final int NOT_SPECIFIED = -777;
	public static final int TYPE_MEMBERLIST = 0;
	public static final int TYPE_FAVORITE = 1;
	
	public long sequence;
	public String title;

	public Department parent;
	public ArrayList<Department> departments =null;
	public ArrayList<User> users = null;
	
	private static ArrayList<User> _allUsers = new ArrayList<User>();
	private static Department _root;
	public static JSONObject rootData;

	public int selected = 0;
	public Department() {
		super();
	}

	public static class Builder {
		private long sequence = -1;
		private String title = null;
		private JSONObject rootData = null;
		private Department parent = null;
		private ArrayList<Department> departments = null;
		private ArrayList<User> users = null;
		public Builder sequence(long seq) {
			this.sequence = seq;
			return this;
		}
		public Builder title(String title) {
			this.title = title;
			return this;
		}
		
		public Builder rootData(JSONObject rootData) {
			this.rootData = rootData;
			return this;
		}
		
		public Builder parent(Department parent) {
			this.parent = parent;
			return this;
		}
		
		public Builder departments(ArrayList<Department> departments) {
			this.departments = departments;
			return this;
		}
		
		public Builder users(ArrayList<User> users) {
			this.users = users;
			return this;
		}
		
		public Department build() {
			Department department = new Department();
			department.sequence = this.sequence;
			department.title = this.title;
			department.rootData = this.rootData;
			department.departments = this.departments;
			department.users = this.users;
			department.parent = this.parent;
			return department;
		}
	}

	public static Department root() {
		if(Department._root == null) {
			Department._root = new Department();
		}
		return Department._root;
	}
	private static void setRoot(Department dep) {
		if(Department._root != null) {
			_root = dep;
		}
	}

	/*
	public Department(JSONObject data) {
		this(data, null);
	}
	
	public Department(JSONObject currentData, Department parentDep) {
		this.data = currentData;
		this.parent = parentDep;
	}
	*/
	

	
	public void fetch() {
		if(this != _root) return;
		setRoot(parseJSONObjectToDepartment(Department.rootData));
		User.setUsers(_allUsers);
	}
	
	public void fetch(JSONObject _rootData) {
		if(this != _root) return;
		rootData = _rootData;
		fetch();
	}
	
	public Department parseJSONObjectToDepartment(JSONObject json) {
		
		
		ArrayList<Department> _departments = new ArrayList<Department>();
		
		if(json.has("departments")) {
			JSONArray jDepartments=null;
			try {
				
				JSONObject jobj = json.getJSONObject("departments");
				jDepartments = jobj.toJSONArray(jobj.names());
				ArrayList<JSONObject> jsonValues = new ArrayList<JSONObject>();
				for(int i=0; i< jDepartments.length(); i++) {
					jsonValues.add(jDepartments.getJSONObject(i));
				}
				Collections.sort(jsonValues, new Comparator<JSONObject>() {
					@Override
					public int compare(JSONObject lhs, JSONObject rhs) {
						int lSeq=0, rSeq=0;
						try {
							lSeq = lhs.getInt("sequence");
							rSeq = rhs.getInt("sequence"); 
						} catch (JSONException e) {
							e.printStackTrace();
						}
						 if( lSeq > rSeq ) return 1;
						 if( lSeq < rSeq ) return -1;	 
						return 0;
					}
				});
				jDepartments = new JSONArray(jsonValues);
				
			} catch (JSONException e) {
				jDepartments = new JSONArray();
			}
			
			for(int i=0; i<jDepartments.length() ; i++) {
				JSONObject jDepartment=null;
				try {
					jDepartment = jDepartments.getJSONObject(i);
				} catch (JSONException e) {
				}
				_departments.add(parseJSONObjectToDepartment(jDepartment));
			}
			
		}
		
		long sequence = -1;
		if(json.has("sequence")) {
			try {
				sequence = json.getLong("sequence");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		String title = null;
		if(json.has("title") ) {
			try {
				title = json.getString("title");
			} catch (JSONException e) {
			}
		}

		ArrayList<User> _users = new ArrayList<User>();
		
		if(json.has("users")) {
			JSONArray jUsers = null;
			try {
				jUsers = json.getJSONArray("users");
			} catch (JSONException e) {
			}
			
			for(int i=0; i < jUsers.length(); i++) {
				JSONObject jUser = null;
				try {
					jUser = jUsers.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				User user = parseJSONObjectToUser(jUser);
				_users.add( user );
				_allUsers.add(user);

			}
		}

		
		
		Department result = new Department.Builder().departments(_departments)
													.title(title)
													.sequence(sequence)
													.users(_users)
													.parent(this)
													.build();
		return result;
	}
	
	public User parseJSONObjectToUser(JSONObject jUser) {
		User user = null;
		
			long TS = 0;
			boolean enabled = false;
			int department = User.NOT_SPECIFIED;
			int idx = User.NOT_SPECIFIED;
			int pic = User.NOT_SPECIFIED;
			String name = null;
			int rank = User.NOT_SPECIFIED;
			String level1 = null;
			String level2 = null;
			String level3 = null;
			String level4 = null;
			String level5 = null;
			String level6 = null;
			
 			try {
				String stringTS = jUser.getString("TS");
				java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
				//http://www.yunsobi.com/blog/408
				//http://www.roseindia.net/java/java-conversion/StringToDate.shtml
				java.util.Date date;
				try {
					date = format.parse(stringTS);
				} catch (ParseException e) {
					throw e;
				}
				TS = date.getTime();
			} catch (Exception e) {
				TS = User.NOT_SPECIFIED;
			}
 			
			try {
				enabled = (jUser.getInt("enabled")==1 ? true : false);
			} catch (JSONException e) {
			}
			
			try {
				department = jUser.getInt("department");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				idx = jUser.getInt("idx");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				pic = jUser.getInt("pic");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				name = jUser.getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				rank = jUser.getInt("rank");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				level1 = jUser.getString("level1");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				level2 = jUser.getString("level2");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				level3 = jUser.getString("level3");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				level4 = jUser.getString("level4");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				level5 = jUser.getString("level5");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				level6 = jUser.getString("level6");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			user = new User.Builder().department(department)
										  .idx(idx)
										  .pic(pic)
										  .name(name)
										  .rank(rank)
										  .TS(TS)
										  .levels(level1, 
												  level2,
												  level3,
												  level4,
												  level5,
												  level6)
										  .build();
		return user;
	}
	
	/*
	public ArrayList<User> fetchUsers () {
		ArrayList<User> users = new ArrayList<User>();
		
		
		JSONArray jUsers;
		
		jUsers = getJSONArray(data, "users");
		
		users = new ArrayList<User>();
		
		for(int i=0; i< jUsers.length() ; i++) {
			JSONObject jUser = getJSONObject(jUsers, i);
			int idx = getInt(jUser, "idx");
			User user = new User(idx);
			users.add(user);
		}
	
		return users;
	}
	*/
	
	
	
	
	
/*
 * Dynamic Data Parsing From Json
 * 
	
	public ArrayList<Department> departments() {
		if(departments == null) departments = new ArrayList<Department>();
		if(fetchDepartments == true || departments.size()<1) {
			departments.clear();
			JSONArray jDepartments;
			jDepartments = getJSONObjectToJSONArray(data, "departments");
			
			departments = new ArrayList<Department>();
			
			for(int i=0; i< jDepartments.length() ; i++) {
				JSONObject jDepartment = getJSONObject(jDepartments, i);
				
				int sequence = getInt(jDepartment, "sequence");
				String title = getString(jDepartment, "title");
				
				
				Department department = new Department.Builder().title(title).sequence(sequence).data(jDepartment).parent(this).build();
				departments.add(department);
			}
		}
		
		return departments;
	}

	
	public ArrayList<User> users () {
		if(users==null) users = new ArrayList<User>();
		if(fetchUsers == true || users.size()<1) {
			users.clear();
			
			JSONArray jUsers;
			
			jUsers = getJSONArray(data, "users");
			
			users = new ArrayList<User>();
			
			for(int i=0; i< jUsers.length() ; i++) {
				JSONObject jUser = getJSONObject(jUsers, i);
				int idx = getInt(jUser, "idx");
				User user = new User(idx);
				users.add(user);
			}
		}
		return users;
	}
	
	
	*/
	
	
	
	/*
	private JSONArray getJSONArray(JSONObject from, String key) {
		JSONArray result = null;
		try {
			result = from.getJSONArray(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private JSONArray getJSONObjectToJSONArray(JSONObject from, String key) {
		JSONArray result = null;
		JSONObject _obj = null;
		try {
			_obj = from.getJSONObject(key);
			result = _obj.toJSONArray(_obj.names());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private JSONObject getJSONObject(JSONArray from, int i) {
		JSONObject result = null;
		try {
			result = from.getJSONObject(i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	private int getInt(JSONObject from, String key) {
		int result = -1;
		try {
			result = from.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	private String getString(JSONObject from, String key) {
		String result = null;
		try {
			result = from.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	*/
}
