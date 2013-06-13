package kr.go.KNPA.Romeo.DB;

import java.util.Date;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.DB.DBManager.DBSchema;
import kr.go.KNPA.Romeo.Survey.Survey;
import android.content.Context;
import android.database.Cursor;

public class SurveyDAO extends DAO {
	
	public SurveyDAO(Context context)
	{
		super(context);
	}
	
	/**
	 * 설문조사보낼때 해쉬 저장
	 * 
	 * @param surveyHash
	 *            서버가 부여한 설문조사 해쉬
	 */
	public void saveSurveyOnSend(String surveyHash)
	{
		String sql = "insert into " + DBSchema.SURVEY.TABLE_NAME + " (" + DBSchema.SURVEY.COLUMN_IDX + "," +
		// DBSchema.SURVEY.COLUMN_TITLE+","+
		// DBSchema.SURVEY.COLUMN_CONTENT+","+
		// DBSchema.SURVEY.COLUMN_CREATOR_IDX+","+
		// DBSchema.SURVEY.COLUMN_CREATED_TS+","+
				DBSchema.SURVEY.COLUMN_CATEGORY + ") " + "values(?, " + Survey.TYPE_DEPARTED + ")";
		String[] val = { surveyHash };
		db.execSQL(sql, val);

		// 자기가 만든건 확인시간을 지금으로
		updateCheckedTS(surveyHash, new Date().getTime());
	}

	/**
	 * 설문조사 받았을때 해쉬저장
	 * 
	 * @param surveyHash
	 */
	public void saveSurveyOnReceived(String surveyHash)
	{
		String sql = "insert into " + DBSchema.SURVEY.TABLE_NAME + " (" + DBSchema.SURVEY.COLUMN_IDX + "," +
		// DBSchema.SURVEY.COLUMN_TITLE+","+
		// DBSchema.SURVEY.COLUMN_CONTENT+","+
		// DBSchema.SURVEY.COLUMN_CREATOR_IDX+","+
		// DBSchema.SURVEY.COLUMN_CREATED_TS+","+
				DBSchema.SURVEY.COLUMN_CATEGORY + ") " + "values(?," + Survey.TYPE_RECEIVED + ")";
		String[] val = { surveyHash };
		db.execSQL(sql, val);
	}

	/**
	 * 설문조사를 확인했을 때
	 * 
	 * @param svyHash
	 *            채팅 해쉬
	 * @param checkedTS
	 *            체크한 시간
	 */
	public void updateCheckedTS(String svyHash, long checkedTS)
	{
		long svyId = hashToId(DBSchema.SURVEY.TABLE_NAME, DBSchema.SURVEY.COLUMN_IDX, svyHash);
		if (svyId == Constants.NOT_SPECIFIED)
		{
			return;
		}
		String sql = "update " + DBSchema.SURVEY.TABLE_NAME + " SET " + DBSchema.SURVEY.COLUMN_IS_CHECKED + " = 1," + DBSchema.SURVEY.COLUMN_CHECKED_TS + " = " + String.valueOf(checkedTS)
				+ " where _id = " + String.valueOf(svyId);
		db.execSQL(sql);
	}

	/**
	 * 설문조사를 응답했을 때
	 * 
	 * @param svyHash
	 *            해쉬
	 * @param checkedTS
	 *            체크한 시간
	 */
	public void updateAnsweredTS(String svyHash, long answeredTS)
	{
		long svyId = hashToId(DBSchema.SURVEY.TABLE_NAME, DBSchema.SURVEY.COLUMN_IDX, svyHash);
		if (svyId == Constants.NOT_SPECIFIED)
		{
			return;
		}
		String sql = "update " + DBSchema.SURVEY.TABLE_NAME + " SET " + DBSchema.SURVEY.COLUMN_IS_ANSWERED + " = 1," + DBSchema.SURVEY.COLUMN_ANSWERED_TS + " = " + String.valueOf(answeredTS)
				+ " where _id = " + String.valueOf(svyId);
		db.execSQL(sql);
	}

	/**
	 * 설문조사 목록 가져오기
	 * 
	 * @b 커서구조
	 * @b COLUMN_SURVEY_IDX str 해시\n
	 * @b COLUMN_SURVEY_IS_ANSWERED int 응답여부\n
	 * @b COLUMN_SURVEY_ANSWERED_TS long 응답한시간\n
	 * @b COLUMN_SURVEY_IS_CHECKED int 확인여부\n
	 * @b COLUMN_SURVEY_CHECKED_TS long 확인한시간\n
	 * @param svyCategory
	 *            내가받은거면 Survey.TYPE_RECEIVED, 내가보낸거면 Survey.TYPE_DEPARTED
	 * @return
	 */
	public Cursor getSurveyList(int svyCategory)
	{
		String sql = "select _id, " + DBSchema.SURVEY.COLUMN_IDX + COLUMN_SURVEY_IDX + ", "
				+
				// DBSchema.SURVEY.COLUMN_TITLE+COLUMN_SURVEY_NAME+", "+
				DBSchema.SURVEY.COLUMN_IS_CHECKED + COLUMN_SURVEY_IS_CHECKED + ", "
				+
				// DBSchema.SURVEY.COLUMN_CREATOR_IDX+COLUMN_SURVEY_SENDER_IDX+", "+
				DBSchema.SURVEY.COLUMN_IS_ANSWERED + COLUMN_SURVEY_IS_ANSWERED + " from" + DBSchema.SURVEY.TABLE_NAME + "where " + DBSchema.SURVEY.COLUMN_CATEGORY + " = "
				+ String.valueOf(svyCategory);
		return db.rawQuery(sql, null);
	}

	/**
	 * 설문조사 기본 정보 가져오기
	 * 
	 * @b 커서구조
	 * @b COLUMN_SURVEY_IS_ANSWERED int 응답여부\n
	 * @b COLUMN_SURVEY_ANSWERED_TS long 응답한시간\n
	 * @b COLUMN_SURVEY_IS_CHECKED int 확인여부\n
	 * @b COLUMN_SURVEY_CHECKED_TS long 확인한시간\n
	 * @b COLUMN_SURVEY_TYPE int 서베이타입\n
	 * @param hash
	 * @return
	 */
	public Cursor getSurveyInfo(String hash)
	{
		String sql = "select _id, "
				+
				// DBSchema.SURVEY.COLUMN_TITLE+COLUMN_SURVEY_NAME+", "+
				// DBSchema.SURVEY.COLUMN_CONTENT+COLUMN_SURVEY_CONTENT+", "+
				// DBSchema.SURVEY.COLUMN_CREATED_TS+COLUMN_SURVEY_CREATED_TS+", "+
				DBSchema.SURVEY.COLUMN_ANSWERED_TS + COLUMN_SURVEY_ANSWERED_TS
				+ ", "
				+
				// DBSchema.SURVEY.COLUMN_CREATOR_IDX+COLUMN_SURVEY_SENDER_IDX+", "+
				DBSchema.SURVEY.COLUMN_CATEGORY + COLUMN_SURVEY_TYPE + ", " + DBSchema.SURVEY.COLUMN_IS_CHECKED + COLUMN_SURVEY_IS_CHECKED + ", " + DBSchema.SURVEY.COLUMN_CHECKED_TS
				+ COLUMN_SURVEY_CHECKED_TS + ", " + DBSchema.SURVEY.COLUMN_IS_ANSWERED + COLUMN_SURVEY_IS_ANSWERED + " from" + DBSchema.SURVEY.TABLE_NAME + "where " + DBSchema.SURVEY.COLUMN_IDX
				+ " = ?";
		String[] val = { hash };
		return db.rawQuery(sql, val);
	}

	public Integer getNumUnchecked()
	{
		String sql = "SELECT count(_id) n FROM "+DBSchema.SURVEY.TABLE_NAME+
				" WHERE "+DBSchema.SURVEY.COLUMN_IS_CHECKED+" = 0" +
				" AND "+DBSchema.SURVEY.COLUMN_CATEGORY+" = "+Survey.TYPE_RECEIVED;
		Cursor c = db.rawQuery(sql, null);
		Integer n = 0;
		if (c.moveToNext())
		{
			n = c.getInt(0);
		}
		
		c.close();
		return n;
	}

	public static final String	COLUMN_SURVEY_IDX			= "survey_idx";
	public static final String	COLUMN_USER_IDX				= "user_idx";
	public static final String	COLUMN_SURVEY_IS_CHECKED	= "is_checked";
	public static final String	COLUMN_SURVEY_CHECKED_TS	= "checked_ts";
	public static final String	COLUMN_SURVEY_IS_ANSWERED	= "is_answered";
	public static final String	COLUMN_SURVEY_ANSWERED_TS	= "answered_ts";
	public static final String	COLUMN_SURVEY_TYPE			= "survey_type";
}