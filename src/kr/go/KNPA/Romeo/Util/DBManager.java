package kr.go.KNPA.Romeo.Util;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "romeo.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_COMMAND = "command";
	public static final String TABLE_MEETING = "meeting";
	public static final String TABLE_SURVEY = "survey";
	public static final String TABLE_DOCUMENT = "document";
	public static final String TABLE_MEMBER_FAVORITE = "member_favorite";
	public static final String TABLE_MEMBER = "member";
	public static final String TABLE_DEPARTMENT = "department";

	private static final String TAG = "DBManager";
	
	public DBManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 생성자에서 언급한 DATABASE_NAME의 DB가 존재하지 않을 경우에만 onCreate가 호출된다.
		String sql = null;
		
		// Command Table
		sql = "CREATE  TABLE "+TABLE_COMMAND+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" content VARCHAR, appendix BLOB,"+
				" roomCode VARCHAR,"+
				" sender INTEGER, receivers TEXT,"+
				" received BOOL, TS INTEGER,"+
				" checked BOOL DEFAULT 0, " +
				" checkTS INTEGER, idx INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create command "+e.getMessage());
		}
		// Meeting Table
		sql = "CREATE  TABLE "+TABLE_MEETING+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" content VARCHAR, appendix BLOB, sender INTEGER,"+
				" roomCode VARCHAR,"+
				" receivers TEXT, received BOOL,"+
				" TS INTEGER,"+
				" checked BOOL DEFAULT 0, " +
				" checkTS INTEGER, idx INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create meeting "+e.getMessage());
		}
		
		// Survey Table
		sql = "CREATE  TABLE "+TABLE_SURVEY+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" title VARCHAR, content TEXT, appendix BLOB,"+
				" sender INTEGER, receivers TEXT, received BOOL,"+
				" TS INTEGER,"+
				" checked BOOL DEFAULT 0, " +
				" checkTS INTEGER, openTS INTEGER,"+
				" answersheet BLOB, answered BOOL DEFAULT 0,"+
				" closeTS INTEGER, idx INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create survey "+e.getMessage());
		}
		
		// Document Table
		sql = "CREATE  TABLE "+TABLE_DOCUMENT+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" title VARCHAR, content TEXT,"+
				" appendix BLOB, sender INTEGER,"+
				" receivers TEXT, received BOOL,"+
				" TS INTEGER,"+
				" checked BOOL DEFAULT 0, " +
				" checkTS INTEGER, favorite BOOL,"+
				" idx INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create document "+e.getMessage());
		}
		
		// Favorite Members Table
		sql = "CREATE  TABLE "+TABLE_MEMBER_FAVORITE+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" idxs VARCHAR NOT NULL ," +
				" isGroup BOOL DEFAULT 0 ," +
				" title VARCHAR,"+
				" TS INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create member(favorite) "+e.getMessage());
		}
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {

	}

}
