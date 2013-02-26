package kr.go.KNPA.Romeo.Base;

import android.os.Parcel;
import android.os.Parcelable;

public class Appendix implements Parcelable{
	
	public static final int APPENDIX_TYPE_CHAT = 0;
	public static final int APPENDIX_TYPE_DOCUMENT = 1;
	public static final int APPENDIX_TYPE_SURVEY = 2;
	
	public int type;
	public String content;
	
	public Appendix() {
		
	}
	
	public Appendix(Parcel source) {
		readFromParcel(source);
	}
	
	public byte[] toBlob() {
		
		return null;
	}

	public String getRoomCode() {
		return null;
	}
	
	public long getOpenTS() {
		return Message.NOT_SPECIFIED;
	}
	
	public long getCloseTS() {
		return Message.NOT_SPECIFIED;
	}
	
	
	// Implements Parcelable
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type);
		dest.writeString(content);
	}
	
	
	private void readFromParcel(Parcel source) {
		type = source.readInt();
		content = source.readString();
	}
	
	public static final Parcelable.Creator<Appendix> CREATOR = new Parcelable.Creator<Appendix>() {

		@Override
		public Appendix createFromParcel(Parcel source) {
			return new Appendix(source);
		}

		@Override
		public Appendix[] newArray(int size) {
			return new Appendix[size];
		}
	};
	
}
