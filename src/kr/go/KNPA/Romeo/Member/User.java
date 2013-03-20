package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.Encrypter;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;
import android.telephony.TelephonyManager;

public class User implements Parcelable{
	// preDefined Constants
	public static final int NOT_SPECIFIED = -777;
	public static final int TYPE_MEMBERLIST = 0;
	public static final int TYPE_FAVORITE = 1;
	public static final int TYPE_MEMBERLIST_SEARCH = 10;
	public static final int TYPE_FAVORITE_SEARCH = 11;
	
	public static final int MAX_LEVEL_DEPTH = 6;
	
	public final static String[] RANK = {"치안총감", "치안정감", "치안감", "경무관", "총경", "경정", "경감", "경위", "경사", "경장", "순경", "의경"};
	
	private static ArrayList<User> _users = null;
	
	public int idx;
	public int department;
	public String[] levels;
	public String name;
	public int pic;
	public int rank;
	public long TS;
	
	public int selected = 0;
	public User(int idx) {
	}
	
	public User() {
		
	}
	
	public User(Parcel in) {
		readFromPalcel(in);
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
		}
		
		return _users;
	}
	
	public static void setUsers(ArrayList<User> users) {
		_users = users;
	}

	public static User getUserWithIdx(long idx) {
		// TODO : 캐싱 순환주기 맞추기.
		ListIterator<User> itr = null;
		if(_users.size() > 0)
			itr = _users.listIterator();
		User user = null;
		
		User _user = null;
		while(itr!= null && itr.hasNext()) {
			_user = itr.next();
			if(_user.idx == idx) {
				user = _user;
			}
		}
		
		return user;
	}
	
	public static User userWithIdx(long idx) {
		User _user = getUserWithIdx(idx);
		
		return _user.clone();
	}
	
	public static ArrayList<User> getUsersWithIndexes(long[] idxs) {
		ArrayList<User> result = new ArrayList<User>(idxs.length);
		for(int i=0; i<idxs.length; i++) {
			result.add(getUserWithIdx(idxs[i]));
		}
		
		return result;
	}
	
	public static ArrayList<User> usersWithIndexes(long[] idxs) {
		ArrayList<User> result = new ArrayList<User>(idxs.length);
		for(int i=0; i<idxs.length; i++) {
			result.add(userWithIdx(idxs[i]));
		}
		return result;
	}
	
	public User clone() {
		return new User.Builder()
		.department(department)
		.idx(this.idx)
		.levels(this.levels[0], this.levels[1], this.levels[2], this.levels[3], this.levels[4], this.levels[5])
		.name(this.name)
		.pic(this.pic)
		.rank(this.rank)
		.TS(this.TS)
		.build();
	}
	
	public static String idexesToString (int[] _receivers) {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<_receivers.length; i++) {
			sb.append(_receivers[i]);
			if(i!=(_receivers.length-1)) {
				sb.append(':');
			}
		}
		
		return sb.toString();
	}
	public static String usersToString(ArrayList<User> receivers) {
		StringBuffer sb = new StringBuffer();
		
		Iterator<User> itr = (Iterator<User>) receivers.iterator();
		while(itr.hasNext()) {
			User u = itr.next();
			sb.append(u.idx);
			if(itr.hasNext()) {
				sb.append(':');
			}
 		}
		
		return sb.toString();
	}
	
	public static int[] indexesInStringToIntArray(String _receivers) {
		String[] parsed = _receivers.split(":");
		int[] result = new int[parsed.length];
		for(int i=0; i<result.length; i++) {
			result[i] = Integer.parseInt(parsed[i]);
		}
		
		return result;
	}
	
	public static ArrayList<Integer> indexesInStringToArrayListOfInteger(String _receivers) {
		int[] _rs = User.indexesInStringToIntArray(_receivers);
		
		ArrayList<Integer> result = new ArrayList<Integer>(_rs.length);
		for(int i=0; i<_rs.length; i++) {
			result.add(Integer.valueOf(_rs[i]));
		}
		
		return result;
	}
	
	public static ArrayList<User> indexesInStringToArrayListOfUser(String _receivers) {
		int[] _rs = User.indexesInStringToIntArray(_receivers);
		
		ArrayList<User> result = new ArrayList<User>(_rs.length);
		for(int i=0; i<_rs.length; i++) {
			result.add(User.getUserWithIdx(_rs[i]));
		}
	
		return result;
	}

	public static ArrayList<User> removeUserHavingIndex(ArrayList<User>users, long idx) {
		Iterator<User> itr = users.iterator();
		int i=0;
		User u = null;
		while(itr.hasNext()) {
			u = itr.next();
			if(u.idx == idx) {
				users.remove(u);
				break;
			}
			i++;
		}
		return users;
	}
	
	public static ArrayList<User> usersRemoveUserHavingIndex(ArrayList<User>users, long idx) {
		ArrayList<User> _users = (ArrayList<User>)users.clone();
		Iterator<User> itr = _users.iterator();
		int i=0;
		User u = null;
		while(itr.hasNext()) {
			u = itr.next();
			if(u.idx == idx) {
				_users.remove(u);
				break;
			}
			i++;
		}
		return _users;
	}
	public String getDepartmentFull() {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<levels.length ; i++) {
			String p = levels[i].trim();
			boolean isEmpty = false;
			if(p.length() <1 || p == "")
				isEmpty = true;
			
			if(isEmpty != true) {
				sb.append(p);
				if(i!= levels.length-1)
					sb.append(" ");
			}
		}
		return sb.toString();
	}

	public long toJSON() {
		return idx;
	}
	
	
	
	
	
	
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idx);
		dest.writeInt(department);
		dest.writeString(name);
		dest.writeInt(pic);
		dest.writeInt(rank);
		dest.writeLong(TS);
		String s = null;
		for(int i=0; i<MAX_LEVEL_DEPTH; i++) {
			s = levels[i];
			if(s == null) s = "";
			dest.writeString(s);	
		}
		

	}
	
	private void readFromPalcel(Parcel in) {
		idx = in.readInt();
		department = in.readInt();
		
/*		String[] _levels = new String[6];
		in.readStringArray(_levels);
		in.readStringArray(_levels);
		
		int _length = 0;
		String _temp = null;
		for(int i=0; i<_levels.length; i++) {
			_temp = _levels[i].trim();
			if(_temp.length() == 0 || _temp.equals("")) continue;
			_length++;
		}
		levels = new String[_length];
		for(int i=0; i<_length; i++) {
			levels[i] = _levels[i];
		}
*/		
		name = in.readString();
		pic = in.readInt();
		rank = in.readInt();
		TS = in.readLong();
		levels = new String[MAX_LEVEL_DEPTH];
		for(int i=0; i<MAX_LEVEL_DEPTH; i++) {
			levels[i] = in.readString();
		}
		//levels = in.createStringArray();
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		
		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
		
	};
	
}
