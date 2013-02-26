package kr.go.KNPA.Romeo.Document;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Base.Payload;
import kr.go.KNPA.Romeo.Util.DBManager;

public class Document extends Message implements Parcelable{
	
	// Message Sub Type Constants
	public static final int TYPE_RECEIVED = 0;
	public static final int TYPE_DEPARTED = 1;
	public static final int TYPE_FAVORITE = 2;
	
	// Specific Variables not to be sent
	public boolean favorite = false;
	

	// Constructor
	public Document() {
		
	}
	
	public Document(Cursor c) {
		super(c);
		
		this.type = getType();
		
		Appendix _appendix = new Appendix();// TODO
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
	
	public static class Builder extends Message.Builder {
		protected boolean _favorite = false;
		
		public Builder favorite(boolean favorite) {
			_favorite = favorite;
			return this;
		}
		
		public Document build() {
			
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
			document.favorite = this._favorite;			
			return document;
		}
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
		
		return Message.MESSAGE_TYPE_SURVEY * Message.MESSAGE_TYPE_DIVIDER + subType;
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
}
