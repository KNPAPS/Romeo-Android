package kr.go.KNPA.Romeo.Member;

import android.os.Parcel;
import android.os.Parcelable;

public class Department implements Parcelable{
	public static final int NOT_SPECIFIED = -777;
	public static final int TYPE_MEMBERLIST = 0;
	public static final int TYPE_FAVORITE = 1;

	public String idx;
	public long sequence;
	public String title;
	public String titleFull;

	public Department() {
		super();
	}
	
	public Department(Parcel source) {
		readFromPalcel(source);
	}

	public static class Builder {
		private String idx;
		private long sequence;
		private String title;
		private String titleFull;

		public Builder idx(String idx) {
			this.idx = idx;
			return this;
		}
		
		public Builder sequence(long sequence) {
			this.sequence = sequence;
			return this;
		}
		
		public Builder title(String title) {
			this.title = title;
			return this;
		}
		
		public Builder titleFull(String titleFull) {
			this.titleFull = titleFull;
			return this;
		}
		public Department build() {
			Department department = new Department();
			department.sequence = this.sequence;
			department.title = this.title;
			department.idx = this.idx;
			department.titleFull = this.titleFull;
			
			return department;
		}
	}

	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(idx);
		dest.writeLong(sequence);
		dest.writeString(title);
		dest.writeString(titleFull);
	}
	
	private void readFromPalcel(Parcel in) {
		idx = in.readString();
		sequence = in.readLong();
		title = in.readString();
		titleFull = in.readString();
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
