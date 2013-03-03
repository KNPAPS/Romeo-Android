package kr.go.KNPA.Romeo.Base;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.go.KNPA.Romeo.Base.Appendix.Attachment;
import kr.go.KNPA.Romeo.Util.Encrypter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.os.Parcel;
import android.os.Parcelable;

public class Appendix implements Parcelable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5106002021704665168L;
	
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
	public static final int TYPE_1_PRIMITIVE= 0x00010000;
	public static final int TYPE_1_JSON 	= 0x00020000;
	public static final int TYPE_1_URL 		= 0x00030000;
	public static final int TYPE_1_BYTES	= 0x00FF0000;
	
	public static final int TYPE_2_EMPTY	= 0x00000000;
	public static final int TYPE_2_STRING	= 0x00001100;
	public static final int TYPE_2_LONG 	= 0x00001200;
	public static final int TYPE_2_INT 		= 0x00001300;
	public static final int TYPE_2_FILE 	= 0x00002100;
	public static final int TYPE_2_IMAGE	= 0x00002200;
	public static final int TYPE_2_COMMAND 	= 0x00003100;
	public static final int TYPE_2_MEETING	= 0x00003200;
	public static final int TYPE_2_DOCUMENT	= 0x00003300;
	public static final int TYPE_2_SURVEY 	= 0x00003400;
	public static final int TYPE_2_ETC 		= 0x0000FF00;
	
	public ArrayList<Attachment> appendixes = null;
	
	public int type;
	public String content;
	
	public Appendix() {
		appendixes = new ArrayList<Attachment>();
	}
	
	public Appendix(Parcel source) {
		appendixes = new ArrayList<Attachment>();
		readFromParcel(source);
	}
	
	public Appendix(String json) {
		
		JSONObject jo = null;
		try {
			jo = new JSONObject(json);
		} catch (JSONException e) {
			jo = null;

		}
		
		if(jo ==null) return;
		
		try {
			this.type = jo.getInt("type");
		} catch (JSONException e) {
		}
		
		JSONArray _appendixes = null;
		try {
			_appendixes = jo.getJSONArray("appendixes");
		} catch (JSONException e) {
		}
		
		if(_appendixes == null) return;
		
		appendixes = new ArrayList<Attachment>();
		Gson gson = new Gson();
		for( int i=0; i<_appendixes.length(); i++) {
			JSONObject _att = null;
			try {
				_att = _appendixes.getJSONObject(i);
			} catch (JSONException e) {
			}
			if(_att == null) continue;
			
			Attachment att = new Attachment(_att.toString());;
					//gson.fromJson(_att.toString(), new TypeToken<Attachment>() {}.getType());//
			if(att!=null) appendixes.add(att);
		}
		
	}
	
	public byte[] toBlob() {
		return Encrypter.objectToBytes(this);
	}
	
	public static Appendix fromBlob(byte[] bytes) {
		return (Appendix)Encrypter.bytesToObject(bytes);
	}
	
	// Deal with Specific type
	public Attachment getAttachmentWithKey(String key) {
		Iterator<Attachment> itr = appendixes.iterator();
		Attachment att = null;
		while(itr.hasNext()) {
			att = itr.next();
			if(att.key.equalsIgnoreCase(key)) {
				return att;
			}
			
		}
		return null;
	}
	
	public Attachment attachmentWithKey(String key) {
		Iterator<Attachment> itr = appendixes.iterator();
		Attachment att = null;
		while(itr.hasNext()) {
			att = itr.next();
			if(att.key.equalsIgnoreCase(key)) {
				return att.clone();
			}
			
		}
		return null;
	}
	
	public boolean replaceAttachmentWithKeyAndAttachment(String key, Attachment att) {
		Iterator<Attachment> itr = appendixes.iterator();
		Attachment _att = null;
		boolean isReplaced = false;
		while(itr.hasNext()) {
			_att = itr.next();
			if(_att.key.equalsIgnoreCase(key)) {
				_att = att;		// TODO 되나?? Reference 대입??
				isReplaced = true;
			}
			
		}
		return isReplaced;
	}
	
	public String getRoomCode() {
		/*
		Iterator<Attachment> itr = appendixes.iterator();
		Attachment att = null;
		String sc = null;
		while(itr.hasNext()) {
			att = itr.next();
			if(att.key.equalsIgnoreCase("roomCode")) {
				sc = att.getString();
				if(sc!=null)
					return sc;
			}
			
		}*/
		Attachment att = getAttachmentWithKey("roomCode");
		if(att != null) return att.getString();
		return "0:0";
	}
	
	public ArrayList<HashMap<String, String>> getForwards() {
		Attachment att = getAttachmentWithKey("forwards");
		if(att == null) return null;
		String json = att.getJSON();
		if(json == null) json = "";
		Gson gson = new Gson();
		return gson.fromJson(json, new TypeToken<ArrayList<HashMap<String,String>>>(){}.getType());
	}
	
	public void addForward(HashMap<String, String>forward) {
		Attachment att = attachmentWithKey("forwards");
		if(att == null) att = new Attachment(makeType(TYPE_1_JSON), null, "");
		
		String json = att.getJSON();
		if(json == null) json = "[]";
		
		Gson gson = new Gson();
		ArrayList<HashMap<String, String>> forwards = gson.fromJson(json, new TypeToken<ArrayList<HashMap<String,String>>>(){}.getType());
		if(forwards == null) forwards = new ArrayList<HashMap<String, String>>();
		forwards.add(forward);
		String newJSON = gson.toJson(forwards, forwards.getClass());
		att.setJSON(newJSON);
		att.key = "forwards";
		
		boolean isReplaced = replaceAttachmentWithKeyAndAttachment("forwards", att);
		if(isReplaced == false) addAttachmentWithKeyAndAttachment("forwards", att);
	}
	
	public void addAttachmentWithKeyAndAttachment(String key, Attachment att) {
		att.key = key;
		this.appendixes.add(att);
	}
	
	public long getOpenTS() {
		JSONObject jo = null;
		long result = Message.NOT_SPECIFIED;
		try {
			 jo = new JSONObject((String)getObjectWithKey(Message.MESSAGE_KEY_SURVEY));
			 result = jo.getLong("openTS");
		} catch (JSONException e) {
		}
		
		return result;
	}
	
	public long getCloseTS() {
		JSONObject jo = null;
		long result = Message.NOT_SPECIFIED;
		try {
			 jo = new JSONObject((String)getObjectWithKey(Message.MESSAGE_KEY_SURVEY));
			 result = jo.getLong("closeTS");
		} catch (JSONException e) {
		}
		
		return result;
	}
	
	public boolean getAnswered() {
		JSONObject jo = null;
		boolean result = false;
		try {
			 jo = new JSONObject((String)getObjectWithKey(Message.MESSAGE_KEY_SURVEY));
			 if(jo.has("answered"))
				 result = jo.getBoolean("answered");
		} catch (JSONException e) {
		}
		
		return result;
	}
	
	
	// get Objects
	public Object getObjectWithKey(String key) {
		Object result = null;
		Iterator<Attachment> itr = appendixes.iterator();
		while(itr.hasNext()) {
			Attachment att = itr.next();
			
			if(att != null && att.key.equals(key)) {
				// TODO 타입별 리턴
				result = att.getString();
				break;
			}
			
		}
		return result;
	}
	
	// manage list
	public void add(Attachment att) {
		appendixes.add(att);
	}
	
	public void add(int index, Attachment att) {
		appendixes.add(index, att);
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
	
	// Manage Data
	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this, Appendix.class);
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


	
	public static class Attachment implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2688164040218356389L;
		public String key;
		public int type;
		public String name;
		public String sValue;
		public byte[] bValue;
		
		public Attachment() {
			
		}
		
		public Attachment(int type, Object attachment) {
			
		}
		
		public Attachment clone() {
			Attachment att = new Attachment();
			att.key = this.key;
			att.type = this.type;
			att.name = this.name;
			att.sValue = this.sValue;
			att.bValue = this.bValue;
			return att;
		}
		public int getType() {
			return type;
		}
		
		public String valueType() {
			boolean sb = (sValue != null);
			boolean bb = (bValue != null);
			if(sb && bb)  { return null;  
			} else if(sb && !bb)  { return "STRING";
			} else if(!sb && bb)  { return "BYTES";
			} else { return "EMPTY";}
		}
		

		public Attachment(String key, int type, String name) {
			this(type, name);
			this.key = key;
		}
		
		public Attachment(String key, int type, String name, String value) {
			this(type, name, value);
			this.key = key;
		}
		
		public Attachment(String key, int type, String name, byte[] bytes) {
			this(type, name, bytes);
			this.key = key;
		}
		
		public Attachment(int type, String name) {
			this.type = type;
			this.name = name;
		}
		
		public Attachment(int type, String name, String value) {
			this(type, name);
			this.sValue = value;
		}
		
		public Attachment(int type, String name, byte[] value) {
			this(type, name);
			this.bValue = value;
		}

		public Attachment(String json) {
			JSONObject jo = null;
			try {
				jo = new JSONObject(json);
			} catch (JSONException e) {
			}
			
			if(jo == null) return;
			
			try {
				this.type = jo.getInt("type");
			} catch (JSONException e) {
			}
			try {
				this.key = jo.getString("key");
			} catch (JSONException e) {
			}
			try {
				this.name = jo.getString("name");
			} catch (JSONException e) {
			}
			
			if( jo.has("sValue") ) {
				try {
					this.sValue = jo.getString("sValue");
				} catch (JSONException e) {
					try {
						this.sValue = jo.getJSONObject("sValue").toString();
					} catch (JSONException e1) {
						this.sValue = null;
					}
				}
			}
			
			if(jo.has("bValue")) {
				//this.bValue = jo.getString("bValue");	//TODO
			}
			
		}

		public byte[] getBytes() {
			return bValue;
		}
		public String getString() {
			return sValue;
		}
		public String getJSON() {
			return sValue;
		}
		public String getURL() {
			return sValue;
		}
		public String getPath() {
			return sValue;
		}
		
		public void setBytes(byte[] bytes) {
			bValue = bytes;
		}
		public void setString(String string) {
			sValue = string;
		}
		public void setJSON(String json) {
			sValue = json;
		}
		public void setURL(String url) {
			sValue = url;
		}
		public void setPath(String path) {
			sValue = path;
		}

	}
	
}
