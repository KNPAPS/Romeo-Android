package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Parcel;
import android.os.Parcelable;

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
	
	
	
	public User() {}
	public User(String idx) { this.idx = idx;}
	
	public User(Parcel in) {
		readFromPalcel(in);
	}

	public User(String idx, String name, int rank, String role, Department department) {
		
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
	
	public static User getUserWithIdx(String idx) {
		return MemberManager.sharedManager().getUser(idx);
	}
	
	public static ArrayList<User> getUsersWithIdxs(ArrayList<String> idxs) {
		return MemberManager.sharedManager().getUsers(idxs);
	}
	
	public static ArrayList<User> getUsersWithIdxs(String[] _idxs) {
		ArrayList<String> idxs = new ArrayList<String>(_idxs.length);
		for(int i=0; i<_idxs.length; i++) {
			idxs.add(_idxs[i]);
		}
		return getUsersWithIdxs(idxs);
	}
		
	public static ArrayList<User> getUsersWithIdxs(String __idxs) {
		String[] _idxs = __idxs.split(":");
		
		return getUsersWithIdxs(_idxs);
	}
	
	public static ArrayList<User> removeUserHavingIndex(ArrayList<User>users, String idx) {
		Iterator<User> itr = ((ArrayList<User>)users.clone()).iterator();
		User u = null;
		
		while(itr.hasNext()) {
			u = itr.next();
			if(u.idx.equals(idx)) {
				users.remove(u);
			}
		}
		return users;
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
