package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Member.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Document extends Message implements Parcelable{
	
	// Message Sub Type Constants
	public static final int TYPE_RECEIVED = 0;
	public static final int TYPE_DEPARTED = 1;
	public static final int TYPE_FAVORITE = 2;
	
	public	static final String FWD_FORWARDER_IDX 	= "forwarder";
	public	static final String FWD_ARRIVAL_DT 		= "TS";
	public	static final String FWD_CONTENT 		= "content";
	
	public	static final String ATTACH_FILE_URL = "file_url"; // (string)
	public	static final String ATTACH_FILE_NAME = "file_name"; // (string)
	public	static final String ATTACH_FILE_TYPE = "file_type"; // (int)
	public	static final String ATTACH_FILE_SIZE = "file_size"; // (long)
			
	// Specific Variables not to be sent
	public boolean favorite = false;
	
	public ArrayList<HashMap<String, Object>> forwards;
	public	ArrayList<HashMap<String, Object>> files; 
	
	// Constructor
	public Document() {}
	
	public Document(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		
		JSONArray ja = jo.getJSONArray("forwards");
		for(int i=0; i<ja.length(); i++) {
			HashMap<String, String> hmap = new HashMap<String, String>();
			
			JSONObject fwd = ja.getJSONObject(i);
			hmap.put(FWD_FORWARDER_IDX,fwd.getString(FWD_FORWARDER_IDX));
			hmap.put(FWD_ARRIVAL_DT, fwd.getString(FWD_ARRIVAL_DT));
			hmap.put(FWD_CONTENT, fwd.getString(FWD_CONTENT));
		}
	}
	
	public Document(
			String			idx, 
			int				type, 
			String			title, 
			String			content, 
			User 			sender, 
			ArrayList<User>	receivers, 
			boolean			received,
			long			TS,
			boolean			checked, 
			long 			checkTS,
			ArrayList<HashMap<String, Object>> forwards,
			ArrayList<HashMap<String, Object>> files,
			boolean			favorite
			) {
		this.idx = idx;
		this.type = type;
		this.title = title;
		this.content = content;
		this.sender = sender;
		this.receivers = receivers;
		this.received = received;
		this.TS = TS;
		this.checked = checked;
		this.checkTS = checkTS;
		this.forwards = forwards;
		this.files = files;
		this.favorite = favorite;
	}
	
	public Document(Cursor c) {
		super(c);
		
		int subType = Document.NOT_SPECIFIED;
		
		if( received == true ) 
			subType = Document.TYPE_RECEIVED;
		else 
			subType = Document.TYPE_DEPARTED;

		if(favorite == true) 
			subType = Document.TYPE_FAVORITE;
		
		this.type = Message.MESSAGE_TYPE_DOCUMENT * Message.MESSAGE_TYPE_DIVIDER + subType;
		
		boolean _favorite = (c.getInt(c.getColumnIndex("favorite")) == 1? true : false);
		this.favorite = _favorite;
	}

	public Document(Parcel source) {
		readFromParcel(source);
	}

	/*
	public Document(Payload payload, boolean received, long checkTS, boolean favorite) {
		this.idx = payload.message.idx;
		this.title = payload.message.title;
		this.type = payload.message.type;
		this.content = payload.message.content;
		this.sender = payload.sender;
		this.receivers = payload.receivers;
		this.TS = System.currentTimeMillis();
		//this.received = true;
		//this.checkTS = NOT_SPECIFIED;
		//this.checked = false;
		this.appendix = payload.message.appendix;
		//this.favorite = false;
		
		
		this.received = received;
		this.checkTS = checkTS;
		this.favorite = favorite;
		if(this.checkTS == Message.NOT_SPECIFIED) {
			this.checked = false;
		}
	}
	*/
	public Document clone() {
		Document document = (Document)this.clone(new Document());
		
		document.forwards = this.forwards;
		document.files = this.files;
		document.favorite = this.favorite;			
		return document;
	}
	
	// Manage if Favorite
	public void toggleFavorite(Context context) {
		DBProcManager.sharedManager(context).document().setFavorite(this.idx, !this.favorite);
		this.favorite = !this.favorite;
	}
	
	
	// Implements Parcelable
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		boolean[] ba = {favorite};
		dest.writeBooleanArray(ba);

	}
	
	public void readFromParcel(Parcel source) {
		super.readFromParcel(source);
		boolean[] ba = source.createBooleanArray();
		favorite = ba[0]; 
	}
	
	public static final Parcelable.Creator<Document> CREATOR = new Parcelable.Creator<Document>() {

		@Override
		public Document createFromParcel(Parcel source) {
			return new Document(source);
		}

		@Override
		public Document[] newArray(int size) {
			return new Document[size];
		}
		
	};

	@Override
	public void afterSend(boolean successful) {
		// TODO :  Insert into DB
		/*
		String tableName = null;
		switch(this.type%MESSAGE_TYPE_DIVIDER) {
			case Document.TYPE_DEPARTED : tableName = DBManager.TABLE_DOCUMENT; break;
			case Document.TYPE_FAVORITE : tableName = DBManager.TABLE_DOCUMENT; break;
			case Document.TYPE_RECEIVED : tableName = DBManager.TABLE_DOCUMENT; break;
		}
		
		DBManager dbManager = new DBManager(context);
		SQLiteDatabase db = dbManager.getWritableDatabase();
		long currentTS = System.currentTimeMillis();
		
		StringBuilder recs = new StringBuilder();
		for(int i=0; i<this.receivers.size(); i++) {
			recs.append( this.receivers.get(i).toJSON() );
		}
		
		ContentValues vals = new ContentValues();
		vals.put("title", this.title);
		vals.put("content", this.content);
		vals.put("sender", this.sender.idx);
		vals.put("receivers", recs.toString());
		vals.put("received", false);
		vals.put("TS", currentTS);
		vals.put("checked", true);							
		vals.put("checkTS", this.checkTS);
		vals.put("favorite", 0); //(this.favorite?1:0)
		vals.put("idx", idx);
		db.insert(tableName, null, vals);

		db.close();
		dbManager.close();
		*/
	}
}
