package kr.go.KNPA.Romeo.DB;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.DB.DBManager.DBSchema;
import kr.go.KNPA.Romeo.Util.Encrypter;
import android.content.Context;
import android.database.Cursor;

public class MemberDAO extends DAO {

	public MemberDAO(Context context)
	{
		super(context);
	}
	
	/**
	 * 멤버를 즐겨찾기에 추가/삭제
	 * 
	 * @param hash
	 */
	public void setFavorite(String hash, boolean isFavorite)
	{
		String sql = null;
		if (isFavorite)
		{

			sql = "insert or ignore into " + DBSchema.USER_FAVORITE.TABLE_NAME + "(" + DBSchema.USER_FAVORITE.COLUMN_IDX + ", " + DBSchema.USER_FAVORITE.COLUMN_IS_GROUP + ") " + " values (?, 0)";
			String[] val = { hash };
			db.execSQL(sql, val);
		}
		else
		{

			sql = "delete from " + DBSchema.USER_FAVORITE.TABLE_NAME + "" + " where " + DBSchema.USER_FAVORITE.COLUMN_IDX + " = ? ";
			String[] val = { hash };
			db.execSQL(sql, val);
		}
	}

	/**
	 * 멤버 여러명을 한 즐겨찾기 그룹으로 등록\n
	 * 
	 * @param hash
	 */
	public void addFavoriteGroup(ArrayList<String> hashArray)
	{
		if (hashArray.size() == 0)
		{
			return;
		}

		String sql = "insert into " + DBSchema.USER_FAVORITE.TABLE_NAME + " (" + DBSchema.USER_FAVORITE.COLUMN_IS_GROUP + ") values(1)";
		db.execSQL(sql);

		long gpId = lastInsertId();
		String gpHash = Encrypter.sharedEncrypter().md5(DBSchema.USER_FAVORITE.TABLE_NAME + String.valueOf(gpId));

		sql = "update " + DBSchema.USER_FAVORITE.TABLE_NAME + " set hash = \"" + gpHash + "\" where _id = " + String.valueOf(gpId);
		db.execSQL(sql);

		db.beginTransaction();
		try
		{
			for (int i = 0; i < hashArray.size(); i++)
			{
				sql = "insert into " + DBSchema.USER_FAVORITE_GROUP.TABLE_NAME + " (" + DBSchema.USER_FAVORITE_GROUP.COLUMN_FAVORITE_ID + ", " + DBSchema.USER_FAVORITE_GROUP.COLUMN_MEMBER_IDX
						+ ") " + "values (" + String.valueOf(gpId) + ", ?)";
				String[] val = { hashArray.get(i) };
				db.execSQL(sql, val);
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}

	}

	/**
	 * 즐겨찾기그룹삭제
	 * 
	 * @param hash
	 *            즐겨찾기그룹hash
	 */
	public void removeFavoriteGroup(String groupHash)
	{
		long gpId = hashToId(DBSchema.USER_FAVORITE.TABLE_NAME, DBSchema.USER_FAVORITE.COLUMN_IDX, groupHash);
		if (gpId == Constants.NOT_SPECIFIED)
		{
			return;
		}
		// 소속멤버들을 group테이블에서 삭제
		String sql = "delete from " + DBSchema.USER_FAVORITE_GROUP.TABLE_NAME + " where favorite_id = " + String.valueOf(gpId);
		db.execSQL(sql);

		// 그룹삭제
		sql = "delete from " + DBSchema.USER_FAVORITE.TABLE_NAME + " where _id = " + String.valueOf(gpId);
		db.execSQL(sql);
	}

	/**
	 * 즐겨찾기 멤버나 멤버그룹 이름 변경
	 * 
	 * @param hash
	 *            즐겨찾기 멤버 해시나 그룹 해시
	 * @param title
	 *            변경할 이름
	 */
	public void updateFavoriteTitle(String hash, String title)
	{
		String sql = "update " + DBSchema.USER_FAVORITE.TABLE_NAME + " set " + DBSchema.USER_FAVORITE.COLUMN_TITLE + " = ? " + "where " + DBSchema.USER_FAVORITE.COLUMN_IDX + " = ?";
		String[] val = { title, hash };
		db.execSQL(sql, val);
	}

	/**
	 * 해당 유저가 즐겨찾기에 있는지
	 * 
	 * @b 커서구조
	 * @b COLUMN_IS_FAVORITE 즐겨찾기인지 아닌지
	 * @param hash
	 * @return
	 */
	public boolean isUserFavorite(String hash)
	{
		String sql = "select count(_id) isFav from " + DBSchema.USER_FAVORITE.TABLE_NAME + " where " + DBSchema.USER_FAVORITE.COLUMN_IDX + "=?";
		String[] val = { hash };
		Cursor c = db.rawQuery(sql, val);
		c.moveToNext();
		return c.getInt(0) > 0 ? true : false;
	}

	/**
	 * 즐겨찾기 목록 가져옴
	 * 
	 * @b 커서구조
	 * @b COLUMN_FAVORITE_IDX str 즐겨찾기 해쉬(유저면 유저해쉬 그룹이면 즐찾그룹해쉬)\n
	 * @b COLUMN_FAVORITE_NAME str 즐겨찾기 이름\n
	 * @b COLUMN_FAVORITE_IS_GROUP int 그룹인지 아닌지\n
	 * @return
	 */
	public Cursor getFavoriteList()
	{
		String sql = "select _id, " + DBSchema.USER_FAVORITE.COLUMN_IDX + COLUMN_FAVORITE_IDX + ", " + DBSchema.USER_FAVORITE.COLUMN_TITLE + COLUMN_FAVORITE_NAME + ", "
				+ DBSchema.USER_FAVORITE.COLUMN_IS_GROUP + COLUMN_FAVORITE_IS_GROUP + " from " + DBSchema.USER_FAVORITE.TABLE_NAME + " where 1=1 order by " + DBSchema.USER_FAVORITE.COLUMN_TITLE
				+ " asc";
		return db.rawQuery(sql, null);
	}

	/**
	 * 즐겨찾기 그룹에 소속된 멤버들의 hash를 array로 리턴
	 * 
	 * @b 커서구조
	 * @b COLUMN_USER_IDX 유저해쉬\n
	 * @param hash
	 * @return
	 */
	public Cursor getFavoriteGroupMemberList(String hash)
	{

		long gpId = hashToId(DBSchema.USER_FAVORITE.TABLE_NAME, DBSchema.USER_FAVORITE.COLUMN_IDX, hash);

		String sql = "select _id, " + DBSchema.USER_FAVORITE_GROUP.COLUMN_MEMBER_IDX + COLUMN_USER_IDX + " from " + DBSchema.USER_FAVORITE_GROUP.TABLE_NAME + " where "
				+ DBSchema.USER_FAVORITE_GROUP.COLUMN_FAVORITE_ID + " = " + String.valueOf(gpId);
		return db.rawQuery(sql, null);
	}

	/**
	 * 즐겨찾기에 등록된 유저나 그룹의 정보 가져옴
	 * 
	 * @b 커서구조
	 * @b COLUMN_FAVORITE_NAME str 제목. 따로 설정안되었을 시에는 null
	 * @param hash
	 *            favorite hash
	 * @return
	 */
	public Cursor getFavoriteInfo(String hash)
	{
		long gpId = hashToId(DBSchema.USER_FAVORITE.TABLE_NAME, DBSchema.USER_FAVORITE.COLUMN_IDX, hash);

		String sql = "select _id, " + DBSchema.USER_FAVORITE.COLUMN_TITLE + COLUMN_FAVORITE_NAME + " from " + DBSchema.USER_FAVORITE.TABLE_NAME + " where _id = " + String.valueOf(gpId);
		return db.rawQuery(sql, null);
	}

	public static final String	COLUMN_FAVORITE_IDX			= "fav_idx";
	public static final String	COLUMN_FAVORITE_NAME		= "fav_name";
	public static final String	COLUMN_FAVORITE_IS_GROUP	= "fav_is_group";
	public static final String	COLUMN_USER_IDX				= "user_idx";
	public static final String	COLUMN_IS_FAVORITE			= "is_fav";

}