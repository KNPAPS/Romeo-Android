package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

public class User {
	public final static int NOT_SPECIFIED = -777;
	public final static String[] RANK = {"치안총감", "치안정감", "치안감", "경무관", "총경", "경정", "경감", "경위", "경사", "경장", "순경", "의경"};
	
	private static ArrayList<User> _users = null;
	
	public int idx;
	public int department;
	public String[] levels;
	public String name;
	public int pic;
	public int rank;
	public long TS;
	
	public User(int idx) {
	}
	
	public User() {
		
	}

	public static class Builder {
		private int _idx = NOT_SPECIFIED;
		private int _department = NOT_SPECIFIED;
		private String[] _levels = new String[6];
		private String _name = null;
		private int _pic = NOT_SPECIFIED;
		private int _rank = NOT_SPECIFIED;
		private long _TS = NOT_SPECIFIED;
		
		public Builder idx (int idx) {
			this._idx = idx;
			return this;
		}
		public Builder department (int department) {
			this._department = department;
			return this;
		}
		
		public Builder levels(String level1, String level2, String level3, String level4, String level5, String level6) {
			this._levels[0] = level1;
			this._levels[1] = level2;
			this._levels[2] = level3;
			this._levels[3] = level4;
			this._levels[4] = level5;
			this._levels[5] = level6;
			return this;
		}
		
		public Builder name (String name) {
			this._name = name;
			return this;
		}
		
		public Builder pic (int pic) {
			this._pic = pic;
			return this;
		}
		
		public Builder rank(int rank) {
			this._rank = rank;
			return this;
		}
		
		public Builder TS (long TS) {
			this._TS = TS;
			return this;
		}
		
		public User build() {
			User user = new User();
			user.idx = this._idx;
			user.department = this._department;
			user.levels = this._levels;
			user.name = this._name;
			user.pic = this._pic;
			user.rank = this._rank;
			user.TS = this._TS;
			return user;
		}
	}
	
	public ArrayList<User> users() {
		if(_users == null || _users.size() < 1) {
			//FETCH USERS FROM DEPARTMENTS
			Department.root().fetch();
		}
		
		return _users;
	}
	
	public static void setUsers(ArrayList<User> users) {
		_users = users;
	}

}
