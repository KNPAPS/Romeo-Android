package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.DBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class Document extends Message implements Parcelable{
	
	// Message Sub Type Constants
	public static final int TYPE_RECEIVED = 0;
	public static final int TYPE_DEPARTED = 1;
	public static final int TYPE_FAVORITE = 2;
	
	private	static final String FWD_FORWARDS 		= "forwards";
	public	static final String FWD_FORWARDER_IDX 	= "forwarder";
	public	static final String FWD_ARRIVAL_DT 		= "TS";
	public	static final String FWD_CONTENT 		= "content";
	
	
	// Specific Variables not to be sent
	public boolean favorite = false;
	
	private ArrayList<HashMap<String, String>> forwards;
	private	ArrayList<HashMap<String, String>> files; 
	
	// Constructor
	public Document() {}
	
	public Document(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		
		JSONArray ja = jo.getJSONArray(FWD_FORWARDS);
		for(int i=0; i<ja.length(); i++) {
			HashMap<String, String> hmap = new HashMap<String, String>();
			
			JSONObject fwd = ja.getJSONObject(i);
			hmap.put(FWD_FORWARDER_IDX,fwd.getString(FWD_FORWARDER_IDX));
			hmap.put(FWD_ARRIVAL_DT, fwd.getString(FWD_ARRIVAL_DT));
			hmap.put(FWD_CONTENT, fwd.getString(FWD_CONTENT));
		}
	}
	
	public Document(Cursor c) {
		super(c);
		
		this.type = getType();
		
		Appendix _appendix = Appendix.fromBlob(c.getBlob(c.getColumnIndex("appendix")));
		this.appendix = _appendix; 
		
		boolean _favorite = (c.getInt(c.getColumnIndex("favorite")) == 1? true : false);
		this.favorite = _favorite;
	}

	public Document(Parcel source) {
		readFromParcel(source);
	}
	
	public Document(Payload payload) {
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
	}
	
	public Document(Payload payload, boolean received, long checkTS, boolean favorite) {
		this(payload);
		this.received = received;
		this.checkTS = checkTS;
		this.favorite = favorite;
		if(this.checkTS == Message.NOT_SPECIFIED) {
			this.checked = false;
		}
	}
	
	public Document clone() {
		Document document = new Document();
		
		document.idx = this.idx;
		document.title = this.title;
		document.type = this.type;
		document.content = this.content;
		document.appendix = this.appendix;
		document.sender = this.sender;
		document.receivers = this.receivers;
		document.TS = this.TS;
		document.received = this.received;
		document.checkTS = this.checkTS;
		document.checked = this.checked;			
		
		document.favorite = this.favorite;			
		return document;
	}
	
	public void insertIntoDatabase(Context context) {
		
		String tableName = null;
		switch(this.type%MESSAGE_TYPE_DIVIDER) {
			case Document.TYPE_DEPARTED : tableName = DBManager.TABLE_DOCUMENT; break;
			case Document.TYPE_FAVORITE : tableName = DBManager.TABLE_DOCUMENT; break;
			case Document.TYPE_RECEIVED : tableName = DBManager.TABLE_DOCUMENT; break;
		}
		
		DBManager dbManager = new DBManager(context);
		SQLiteDatabase db = dbManager.getWritableDatabase();
		ContentValues vals = new ContentValues();
		vals.put("title", this.title);
		vals.put("content", this.content);
		vals.put("appendix", this.appendix.toBlob());
		vals.put("sender", this.sender.idx);
		vals.put("receivers", User.usersToString(this.receivers));
		vals.put("received", (this.received?1:0));
		vals.put("TS", System.currentTimeMillis());
		vals.put("checked", (this.checked?1:0));							
		vals.put("checkTS", this.checkTS);
		vals.put("favorite", (this.favorite?1:0));
		vals.put("idx", this.idx);
		db.insert(tableName, null, vals);
		db.close();
		dbManager.close();
	}
	
	public static class Builder extends Message.Builder {
		protected boolean _favorite = false;
		
		public Builder favorite(boolean favorite) {
			_favorite = favorite;
			return this;
		}
		
		public Document build() {
			/*
			Document document = (Document) new Document.Builder()
													   .idx(_idx)
													   .title(_title)
													   .type(_type)
													   .content(_content)
													   .appendix(_appendix)
													   .sender(_sender)
													   .receivers(_receivers)
													   .TS(_TS)
													   .received(_received)
													   .checkTS(_checkTS)
													   .checked(_checked)
													   .buildMessage();
													   */
			Document document = new Document();
			
			document.idx = this._idx;
			document.title = this._title;
			document.type = this._type;
			document.content = this._content;
			document.appendix = this._appendix;
			document.sender = this._sender;
			document.receivers = this._receivers;
			document.TS = this._TS;
			document.received = this._received;
			document.checkTS = this._checkTS;
			document.checked = this._checked;			
			
			document.favorite = this._favorite;			
			return document;
		}
	}

	public ArrayList<HashMap<String, String>> getForwards() {
		if(forwards == null)
			forwards = new ArrayList<HashMap<String, String>>();
		return forwards;
	}
	
	public ArrayList<HashMap<String, String>> getFilesInfo() {
		return files;
	}
	
	protected int getType() {
		int subType = Document.NOT_SPECIFIED;
		if( received == true ) {
			subType = Document.TYPE_RECEIVED ;
		} else {
			subType = Document.TYPE_DEPARTED;
		}
		if(favorite == true) {// && received == ture)
			subType = Document.TYPE_FAVORITE;
		}
		
		return Message.MESSAGE_TYPE_DOCUMENT * Message.MESSAGE_TYPE_DIVIDER + subType;
	}

	
	// Manage if Favorite
	public void setFavorite(boolean fav, Context context) {
		DBManager dbManager = new DBManager(context);
		SQLiteDatabase db = dbManager.getWritableDatabase();
		
		int f = (fav? 1 : 0);
		String sql = "UPDATE "+DBManager.TABLE_DOCUMENT+
					 " SET favorite="+f+
					 " WHERE idx="+this.idx+";";
		
		db.execSQL(sql);
		
		this.favorite = fav;
	}
	
	public void toggleFavorite(Context context) {
		setFavorite(!this.favorite, context);
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
	
	public void send(Context context) {
		long idx = super.send();
		
		String tableName = null;
		switch(this.type%MESSAGE_TYPE_DIVIDER) {
			case Document.TYPE_DEPARTED : tableName = DBManager.TABLE_DOCUMENT; break;
			case Document.TYPE_FAVORITE : tableName = DBManager.TABLE_DOCUMENT; break;
			case Document.TYPE_RECEIVED : tableName = DBManager.TABLE_DOCUMENT; break;
		}
		
		DBManager dbManager = new DBManager(context);
		SQLiteDatabase db = dbManager.getWritableDatabase();
		long currentTS = System.currentTimeMillis();
		
		ContentValues vals = new ContentValues();
		vals.put("title", this.title);
		vals.put("content", this.content);
		vals.put("appendix", this.appendix.toBlob());
		vals.put("sender", this.sender.idx);
		vals.put("receivers", User.usersToString(this.receivers));
		vals.put("received", false);
		vals.put("TS", currentTS);
		vals.put("checked", true);							
		vals.put("checkTS", this.checkTS);
		vals.put("favorite", 0); //(this.favorite?1:0)
		vals.put("idx", idx);
		db.insert(tableName, null, vals);

		db.close();
		dbManager.close();

	}
}
