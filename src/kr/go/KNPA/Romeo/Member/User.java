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
	
	public String idx;
	public String name;
	public int rank;
	public String role;
	public String pic;
	
	public Department department;
	
	/*
	public User(int idx) {
	}
	*/
	public User() {
		
	}
	
	public User(Parcel in) {
		readFromPalcel(in);
	}

	public static class Builder {
		public	String		_idx 		= 	null;
		public	String		_name 		= 	null;
		public	int			_rank 		= 	NOT_SPECIFIED;
		public	String		_role 		= 	null;
		public	String		_pic 		= 	null;
		public	Department 	_department	= 	null;
		
		public Builder idx (String idx) {
			this._idx = idx;
			return this;
		}

		public Builder name (String name) {
			this._name = name;
			return this;
		}

		public Builder rank(int rank) {
			this._rank = rank;
			return this;
		}

		public Builder role (String role) {
			this._role = role;
			return this;
		}

		public Builder pic (String pic) {
			this._pic = pic;
			return this;
		}
		
		public Builder department (Department department) {
			this._department = department;
			return this;
		}
		
		public User build() {
			User user = new User();
			user.idx = this._idx;
			user.name = this._name;
			user.rank = this._rank;
			user.role = this._role;
			user.department = this._department;
			user.pic = this._pic;
			return user;
		}
	}
	
	public User clone() {
		return new User.Builder()
		.idx(this.idx)
		.name(this.name)
		.rank(this.rank)
		.role(this.role)
		.department(department)
		.pic(this.pic)
		.build();
	}
	
	public static User userWithIdx(String idx) {

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
	
	public static ArrayList<User> getUsersWithIndexes(String[] idxs) {
		ArrayList<User> result = new ArrayList<User>(idxs.length);
		for(int i=0; i<idxs.length; i++) {
			result.add(getUserWithIdx(idxs[i]));
		}
		
		return result;
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

	public static int[] indexesInStringToIntArray(String _receivers) {
		String[] parsed = _receivers.split(":");
		int[] result = new int[parsed.length];
		for(int i=0; i<result.length; i++) {
			result[i] = Integer.parseInt(parsed[i]);
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

	public String toJSON() {
		return idx;
	}
	
	
	
	
	
	
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(idx);
		dest.writeString(name);
		dest.writeInt(rank);
		dest.writeString(role);
		dest.writeString(pic);
		dest.writeParcelable(department, 0);
	}
	
	private void readFromPalcel(Parcel in) {
		idx = in.readString();
		name = in.readString();
		rank = in.readInt();
		role = in.readString();
		pic = in.readString();
		department = in.readParcelable(Department.class.getClassLoader());
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
