package kr.go.KNPA.Romeo.Member;

import android.os.Parcel;
import android.os.Parcelable;

public class Department implements Parcelable {
	public static final int NOT_SPECIFIED = -777;
	public static final int TYPE_MEMBERLIST = 0;
	public static final int TYPE_FAVORITE = 1;

	public String idx;
	public long sequence;
	public String name;
	public String nameFull;
	public String parentIdx;

	public Department() {
		super();
	}
	
	public Department(Parcel source) {
		readFromPalcel(source);
	}

	public Department(String idx, String name, String nameFull, String parentIdx, long sequence) {
		this.idx = idx;
		this.sequence = sequence;
		this.name = name;
		this.nameFull = nameFull;
		this.parentIdx = parentIdx;
	}
	
	public Department(String idx, String name, String nameFull, String parentIdx, String sequence) {
		this(idx, name, nameFull, parentIdx, Long.parseLong(sequence));
	}
	
	public Department(String idx, String name, String nameFull, String parentIdx) {
		this(idx, name, nameFull, parentIdx, NOT_SPECIFIED);
	}
	
	// TODO UserRegisterEditView 에서 Spinner 기본값이 자꾸 Class .toString으로 뜨길래
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(idx);
		dest.writeLong(sequence);
		dest.writeString(name);
		dest.writeString(nameFull);
		dest.writeString(parentIdx);
	}
	
	private void readFromPalcel(Parcel in) {
		idx = in.readString();
		sequence = in.readLong();
		name = in.readString();
		nameFull = in.readString();
		parentIdx = in.readString();
	}
	
	public static final Parcelable.Creator<Department> CREATOR = new Parcelable.Creator<Department>() {
		
		@Override
		public Department createFromParcel(Parcel source) {
			return new Department(source);
		}

		@Override
		public Department[] newArray(int size) {
			return new Department[size];
		}
		
	};
}
