package kr.go.KNPA.Romeo.Base;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Appendix implements Parcelable{
	
	public static final int APPENDIX_TYPE_CHAT = 0;
	public static final int APPENDIX_TYPE_DOCUMENT = 1;
	public static final int APPENDIX_TYPE_SURVEY = 2;
	
	public static final int TYPE_FILTER_MASK	= 0x00FFFFFF; 
	public static final int TYPE_MASK 	= 0x11;
	public static final int TYPE_DIVIDER = 0x0100;
	
	public static final int TYPE_1_MASK = 0x00110000;
	public static final int TYPE_2_MASK = 0x00001100;
	public static final int TYPE_3_MASK = 0x00000011;
	
	public static final int TYPE_1_EMPTY	= 0x00000000;
	public static final int TYPE_1_LONG		= 0x00010000;
	public static final int TYPE_1_STRING 	= 0x00020000;
	public static final int TYPE_1_JSON 	= 0x00030000;
	public static final int TYPE_1_FILE		= 0x00040000; 
	public static final int TYPE_1_UPLOAD	= 0x00050000;
	public static final int TYPE_1_ETC 		= 0x00FF0000;
	
	public static final int TYPE_2_EMPTY	= 0x00000000;
	public static final int TYPE_2_ROOMCODE = 0x00000100;
	public static final int TYPE_2_SURVEY 	= 0x00000200;
	public static final int TYPE_2_IMAGE 	= 0x00000300;
	public static final int TYPE_2_PDF 		= 0x00000400;
	public static final int TYPE_2_TS		= 0x00000500;
	public static final int TYPE_2_TIMESTAMP= 0x00000500;
	public static final int TYPE_2_ETC 		= 0x0000FF00;

	public static final int TYPE_3_OPENTS	= 0x00000001;
	public static final int TYPE_3_CLOSETS	= 0x00000002;
	
	public ArrayList<Attachment> appendixes = null;
	
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
		if(){
		
		} else {
		return Message.NOT_SPECIFIED;
	}
	
	public long getCloseTS() {
		return Message.NOT_SPECIFIED;
	}
	
	
	// Manage Type
	public static int makeType(int type1, int type2, int type3) {
		return type1|type2|type3;
	}
	public static int makeType(int type1, int type2) {
		return type1|type2;
	}
	public static int makeType(int type1) {
		return type1;
	}
	
	public static int getType1(int type) {
		return type&TYPE_1_MASK;
	}
	public static int getType2(int type) {
		return type&TYPE_2_MASK;
	}
	public static int getType3(int type) {
		return type&TYPE_3_MASK;
	}
	
	public int getType1() {
		return Appendix.getType1(type);
	}
	public int getType2() {
		return Appendix.getType2(type);
	}
	public int getType3() {
		return Appendix.getType3(type);
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
	
	public static class Attachment {
		private int type;
		private byte[] value;
		private String string;
		public Attachment(int type, Object attachment) {
			
		}
		
		public int getType() {
			return type;
		}
		
		public String valueType() {
			int type1 = getType1(getType());
			if( type1 == TYPE_1_STRING || type1 == TYPE_1_JSON) {
				return "STRING";
			} else {
				return "BYTES";
			}
		}
		
		public Attachment(int type, String string) {
			this.type = type;
			if(getType1(type) == TYPE_1_STRING || getType1(type) == TYPE_1_JSON) {
				this.string = string;
			} else {
				//this.value 
			}
		}

	}
	
}
