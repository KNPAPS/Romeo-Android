package kr.go.KNPA.Romeo.DB;


import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.DB.DBManager.DBSchema;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DB Access Object
 */
public class DAO {
	private static final String		TAG				= DAO.class.getName();

	protected DBManager			dbm	= null;
	protected SQLiteDatabase	db;
	protected Context			context;

	protected DAO(Context context)
	{
		this.dbm = DBManager.getInstance(context);
		this.db = dbm.getWritableDatabase();
		this.context = context;
	}
	
	public static ChatDAO chat(Context context)
	{
		return new ChatDAO(context);
	}
	
	public static DocuDAO document(Context context)
	{
		return new DocuDAO(context);
	}
	
	public static SurveyDAO survey(Context context)
	{
		return new SurveyDAO(context);
	}
	
	public static MemberDAO member(Context context)
	{
		return new MemberDAO(context);
	}

	public void dropDatabase()
	{
		this.context.deleteDatabase(DBSchema.DATABASE_NAME);
	}

	long hashToId(String tableName, String hashColName, String hash)
	{

		if (hash == null || tableName == null || hashColName == null)
		{
			return Constants.NOT_SPECIFIED;
		}

		String[] args = { hash };
		Cursor c = db.rawQuery("select _id from " + tableName + " where " + hashColName + " = ?", args);
		if (c.moveToNext())
		{
			return c.getLong(0);
		}
		else
		{
			Log.w(TAG, "입력한 해쉬에 대한 id값을 찾을 수 없음 at hashToId(" + tableName + ", " + hashColName + ", " + hash + ")");
			return Constants.NOT_SPECIFIED;
		}

	}

	long lastInsertId()
	{
		Cursor c = db.rawQuery("select last_insert_rowid()", null);
		if (c.moveToFirst() != false)
		{
			return c.getLong(0);
		}
		return Constants.NOT_SPECIFIED;
	}
}