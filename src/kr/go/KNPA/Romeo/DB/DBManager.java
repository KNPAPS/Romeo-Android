package kr.go.KNPA.Romeo.DB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private static final String TAG = DBManager.class.getName();
	private static final String TEXT = " TEXT ";
	private static final String INT = " TEXT ";
	private static final String REAL = " TEXT ";
	private static final String BLOB = " TEXT ";
	private static final String UNIQUE = " UNIQUE ";
	private static final String NOT_NULL = " NOT NULL ";
	private static final String AUTO_INCREMENT = " AUTOINCREMENT ";
	private static final String PRIMARY_KEY = " PRIMARY KEY ";
	private static final String COMMA = " , ";
	
	private static final String SQL_CREATE_TABLE_CHAT = 
			"CREATE TABLE "+DBSchema.CHAT.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.CHAT.COLUMN_CONTENT	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.CHAT.COLUMN_CONTENT_TYPE	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.CHAT.COLUMN_CREATED_TS	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.CHAT.COLUMN_HASH	+	TEXT	+	NOT_NULL	+	UNIQUE	+	COMMA+
			DBSchema.CHAT.COLUMN_IS_CHECKED	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.CHAT.COLUMN_ROOM_ID	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.CHAT.COLUMN_SENDER_HASH	+	TEXT	+	NOT_NULL	+	COMMA+
			")";
	private static final String SQL_CREATE_INDEX_CHAT = 
			"CREATE INDEX CHAT_IDX ON "+
					DBSchema.CHAT.TABLE_NAME+" ("+DBSchema.CHAT.COLUMN_ROOM_ID+" ASC, "+DBSchema.CHAT.COLUMN_SENDER_HASH+" ASC)";
	
	private static final String SQL_CREATE_TABLE_DOCUMENT = 
			"CREATE TABLE "+DBSchema.DOCUMENT.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT.COLUMN_HASH	+	TEXT	+	NOT_NULL	+	UNIQUE	+	COMMA+
			DBSchema.DOCUMENT.COLUMN_CREATOR_HASH  +	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT.COLUMN_CREATED_TS	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT.COLUMN_TITLE	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT.COLUMN_IS_CHECKED	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT.COLUMN_CATEGORY	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT.COLUMN_CONTENT	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT.COLUMN_CHECKED_TS	+	INT	+	COMMA+
			")";
	private static final String SQL_CREATE_INDEX_DOC = 
			"CREATE INDEX DOC_IDX ON "+
					DBSchema.DOCUMENT.TABLE_NAME+" ("+DBSchema.DOCUMENT.COLUMN_CATEGORY+" ASC, "+DBSchema.DOCUMENT.COLUMN_CREATED_TS+" DESC)";
	
	private static final String SQL_CREATE_TABLE_DOCUMENT_ATTACHMENT = 
			"CREATE TABLE "+DBSchema.DOCUMENT_ATTACHMENT.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_ATTACHMENT.COLUMN_DOC_ID  +	INT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_TYPE	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_NAME	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_URL	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_SIZE_IN_BYTE	+	INT	+	NOT_NULL	+	COMMA+
			")";
	private static final String SQL_CREATE_INDEX_DOC_ATTACH = 
			"CREATE INDEX DOC_ATTACH_IDX ON "+
					DBSchema.DOCUMENT_ATTACHMENT.TABLE_NAME+" ("+
					DBSchema.DOCUMENT_ATTACHMENT.COLUMN_DOC_ID+" ASC)";
	
	private static final String SQL_CREATE_TABLE_DOCUMENT_FORWARD = 
			"CREATE TABLE "+DBSchema.DOCUMENT_FORWARD.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_FORWARD.COLUMN_DOC_ID  +	INT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARDER_HASH	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_FORWARD.COLUMN_COMMENT	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS	+	INT	+	NOT_NULL	+	COMMA+
			")";
	private static final String SQL_CREATE_INDEX_DOC_FORWARD = 
			"CREATE INDEX DOC_FORWARD_IDX ON "+
					DBSchema.DOCUMENT_FORWARD.TABLE_NAME+" ("+
					DBSchema.DOCUMENT_FORWARD.COLUMN_DOC_ID+" ASC)";
	
	private static final String SQL_CREATE_TABLE_ROOM = 
			"CREATE TABLE "+DBSchema.ROOM.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.ROOM.COLUMN_HASH  +	TEXT	+	NOT_NULL	+	UNIQUE	+	COMMA+
			DBSchema.ROOM.COLUMN_TITLE	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.ROOM.COLUMN_TYPE	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.ROOM.COLUMN_IS_FAVORITE	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.ROOM.COLUMN_LAST_CHAT_TS	+	INT	+	COMMA+
			")";
	private static final String SQL_CREATE_INDEX_ROOM = 
			"CREATE INDEX ROOM_IDX ON "+
					DBSchema.ROOM.TABLE_NAME+" ("+
					DBSchema.ROOM.COLUMN_TYPE+" ASC, "+DBSchema.ROOM.COLUMN_LAST_CHAT_TS+" DESC)";
	
	private static final String SQL_CREATE_TABLE_ROOM_CHATTER = 
			"CREATE TABLE "+DBSchema.ROOM_CHATTER.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.ROOM_CHATTER.COLUMN_USER_HASH	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.ROOM_CHATTER.COLUMN_LAST_READ_TS	+	INT	+	COMMA+
			")";
	private static final String SQL_CREATE_INDEX_ROOM_CHATTER = 
			"CREATE INDEX ROOM_CHATTER_IDX ON "+
					DBSchema.ROOM_CHATTER.TABLE_NAME+" ("+
					DBSchema.ROOM_CHATTER.COLUMN_ROOM_ID+" ASC)";
	
	private static final String SQL_CREATE_TABLE_SURVEY = 
			"CREATE TABLE "+DBSchema.SURVEY.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_HASH	+	TEXT	+	NOT_NULL	+	UNIQUE	+	COMMA+
			DBSchema.SURVEY.COLUMN_TITLE	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_CONTENT	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_OPEN_TS	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_CLOSE_TS	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_CREATED_TS	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_SENDER_HASH	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_IS_CHECKED	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_CHECKED_TS	+	INT	+	COMMA+
			DBSchema.SURVEY.COLUMN_IS_ANSWERED	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.SURVEY.COLUMN_ANSWERED_TS	+	INT	+	COMMA+
			DBSchema.SURVEY.COLUMN_CATEGORY	+	INT	+	NOT_NULL	+	COMMA+
			")";
	private static final String SQL_CREATE_INDEX_SURVEY = 
			"CREATE INDEX SURVEY_IDX ON "+
					DBSchema.SURVEY.TABLE_NAME+" ("+
					DBSchema.SURVEY.COLUMN_CATEGORY+" ASC,"+DBSchema.SURVEY.COLUMN_CREATED_TS+" DESC)";
	
	private static final String SQL_CREATE_TABLE_USER_FAVORITE = 
			"CREATE TABLE "+DBSchema.USER_FAVORITE.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.USER_FAVORITE.COLUMN_HASH	+	TEXT	+	NOT_NULL	+	UNIQUE	+	COMMA+
			DBSchema.USER_FAVORITE.COLUMN_TITLE	+	TEXT	+	NOT_NULL	+	COMMA+
			DBSchema.USER_FAVORITE.COLUMN_IS_GROUP	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.USER_FAVORITE.COLUMN_CREATED_TS	+	INT	+	NOT_NULL	+	COMMA+
			")";
	
	private static final String SQL_CREATE_TABLE_USER_FAVORITE_GROUP = 
			"CREATE TABLE "+DBSchema.USER_FAVORITE_GROUP.TABLE_NAME+
			" ("+
			BaseColumns._ID	+	INT	+	PRIMARY_KEY	+	AUTO_INCREMENT	+	NOT_NULL	+	COMMA+
			DBSchema.USER_FAVORITE_GROUP.COLUMN_FAVORITE_ID	+	INT	+	NOT_NULL	+	COMMA+
			DBSchema.USER_FAVORITE_GROUP.COLUMN_MEMBER_HASH	+	TEXT	+	NOT_NULL	+	COMMA+
			")";
	
	private static final String SQL_CREATE_INDEX_USER_FAV = 
			"CREATE INDEX USER_FAV_IDX ON "+
					DBSchema.USER_FAVORITE_GROUP.TABLE_NAME+" ("+
					DBSchema.USER_FAVORITE_GROUP.COLUMN_FAVORITE_ID+" ASC)";
	
	
	
	public DBManager(Context context) {
		super(context, DBSchema.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(SQL_CREATE_TABLE_CHAT);
			db.execSQL(SQL_CREATE_TABLE_DOCUMENT);
			db.execSQL(SQL_CREATE_TABLE_DOCUMENT_ATTACHMENT);
			db.execSQL(SQL_CREATE_TABLE_DOCUMENT_FORWARD);
			db.execSQL(SQL_CREATE_TABLE_ROOM);
			db.execSQL(SQL_CREATE_TABLE_ROOM_CHATTER);
			db.execSQL(SQL_CREATE_TABLE_SURVEY);
			db.execSQL(SQL_CREATE_TABLE_USER_FAVORITE);
			db.execSQL(SQL_CREATE_TABLE_USER_FAVORITE_GROUP);
			db.execSQL(SQL_CREATE_INDEX_CHAT);
			db.execSQL(SQL_CREATE_INDEX_DOC);
			db.execSQL(SQL_CREATE_INDEX_DOC_ATTACH);
			db.execSQL(SQL_CREATE_INDEX_DOC_FORWARD);
			db.execSQL(SQL_CREATE_INDEX_ROOM);
			db.execSQL(SQL_CREATE_INDEX_ROOM_CHATTER);
			db.execSQL(SQL_CREATE_INDEX_SURVEY);
			db.execSQL(SQL_CREATE_INDEX_USER_FAV);
		} catch (SQLException e ) {
			Log.wtf(TAG, e.getMessage());
		}
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		
	}

	private static class DBSchema {
		private DBSchema() {}
		public static final String DATABASE_NAME = "romeo.db";
		public static abstract class DOCUMENT_ATTACHMENT implements BaseColumns {
		    public static final String TABLE_NAME = "rs_document_attachment";
		    public static final String COLUMN_DOC_ID = "doc_id";
		    public static final String COLUMN_FILE_TYPE = "file_type";
		    public static final String COLUMN_FILE_NAME = "file_name";
		    public static final String COLUMN_FILE_URL = "file_url";
		    public static final String COLUMN_FILE_SIZE_IN_BYTE = "file_size";
		}
		
		public static abstract class DOCUMENT implements BaseColumns {
		    public static final String TABLE_NAME = "rs_document";
		    public static final String COLUMN_HASH = "doc_hash";
		    public static final String COLUMN_CREATOR_HASH = "creator_hash";
		    public static final String COLUMN_CREATED_TS = "created_ts";
		    public static final String COLUMN_TITLE = "title";
		    public static final String COLUMN_CATEGORY = "doc_category";
		    public static final String COLUMN_CONTENT = "content";
		    public static final String COLUMN_IS_CHECKED = "is_checked";
		    public static final String COLUMN_CHECKED_TS = "checked_ts";
		}
		
		public static abstract class DOCUMENT_FORWARD implements BaseColumns {
		    public static final String TABLE_NAME = "rs_document_forward";
		    public static final String COLUMN_DOC_ID = "doc_id";
		    public static final String COLUMN_FORWARDER_HASH = "fwder_hash";
		    public static final String COLUMN_COMMENT = "fwd_comment";
		    public static final String COLUMN_FORWARD_TS = "fwd_ts";
		}
		
		public static abstract class CHAT implements BaseColumns {
			public static final String TABLE_NAME = "rs_chat";
			public static final String COLUMN_HASH = "chat_hash";
			public static final String COLUMN_ROOM_ID = "room_id";
			public static final String COLUMN_SENDER_HASH = "sender_hash";
			public static final String COLUMN_CONTENT = "chat_content";
			public static final String COLUMN_CREATED_TS = "created_ts";
			public static final String COLUMN_IS_CHECKED = "is_checked";
			public static final String COLUMN_CONTENT_TYPE = "content_type";
		}
		
		public static abstract class ROOM implements BaseColumns {
			public static final String TABLE_NAME = "rs_room";
			public static final String COLUMN_HASH = "room_hash";
			public static final String COLUMN_TITLE = "room_title";
			public static final String COLUMN_TYPE = "room_type";
			public static final String COLUMN_IS_FAVORITE = "is_favorite";
			public static final String COLUMN_LAST_CHAT_TS = "last_chat_ts";
		}
		
		public static abstract class ROOM_CHATTER implements BaseColumns {
			public static final String TABLE_NAME = "rs_room_chatter";
			public static final String COLUMN_ROOM_ID = "room_id";
			public static final String COLUMN_USER_HASH = "user_hash";
			public static final String COLUMN_LAST_READ_TS = "last_read_ts";
		}
		
		public static abstract class SURVEY implements BaseColumns {
			public static final String TABLE_NAME = "rs_survey";
			public static final String COLUMN_HASH = "survey_hash";
			public static final String COLUMN_CATEGORY = "survey_category";
			public static final String COLUMN_TITLE = "survey_title";
			public static final String COLUMN_CONTENT = "survey_content";
			public static final String COLUMN_OPEN_TS = "open_ts";
			public static final String COLUMN_CLOSE_TS = "close_ts";
			public static final String COLUMN_CREATED_TS = "created_ts";
			public static final String COLUMN_SENDER_HASH = "sender_hash";
			public static final String COLUMN_IS_CHECKED = "is_checked";
			public static final String COLUMN_CHECKED_TS = "checked_ts";
			public static final String COLUMN_IS_ANSWERED = "is_answered";
			public static final String COLUMN_ANSWERED_TS = "answered_ts";
		}	
		
		public static abstract class USER_FAVORITE implements BaseColumns {
			public static final String TABLE_NAME = "rs_user_favorite";
			public static final String COLUMN_HASH = "hash";
			public static final String COLUMN_TITLE = "";
			public static final String COLUMN_IS_GROUP = "";
			public static final String COLUMN_CREATED_TS = "";
		}
		
		public static abstract class USER_FAVORITE_GROUP implements BaseColumns {
			public static final String TABLE_NAME = "rs_user_favorite_group";
			public static final String COLUMN_FAVORITE_ID = "favorite_id";
			public static final String COLUMN_MEMBER_HASH = "member_hash";
		}
	}
}
