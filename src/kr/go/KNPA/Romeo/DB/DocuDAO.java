package kr.go.KNPA.Romeo.DB;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBManager.DBSchema;
import kr.go.KNPA.Romeo.Document.Document;
import android.content.Context;
import android.database.Cursor;

public class DocuDAO extends DAO {
	
	public DocuDAO(Context context)
	{
		super(context);
	}
	
	private void saveAttachmentInfo(long docId, ArrayList<HashMap<String, Object>> files)
	{

		db.beginTransaction();
		try
		{
			// 첨부파일 정보 insert

			for (int i = 0; i < files.size(); i++)
			{
				HashMap<String, Object> hm = files.get(i);

				String[] binds = { hm.get(KEY.DOCUMENT.FILE_IDX).toString(), hm.get(KEY.DOCUMENT.FILE_NAME).toString() };
				long fileSize = (Long) hm.get(KEY.DOCUMENT.FILE_SIZE);
				int fileType = (Integer) hm.get(KEY.DOCUMENT.FILE_TYPE);

				String sql = "insert into " + DBSchema.DOCUMENT_ATTACHMENT.TABLE_NAME + "(" + DBSchema.DOCUMENT_ATTACHMENT.COLUMN_DOC_ID + "," + DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_IDX + ","
						+ DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_NAME + "," + DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_TYPE + "," + DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_SIZE_IN_BYTE + ") "
						+ "values(" + String.valueOf(docId) + ", ?, ?, " + String.valueOf(fileType) + ", " + String.valueOf(fileSize) + ")";
				db.execSQL(sql, binds);
			}

			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	/**
	 * 문서를 자신이 만들어서 보낼 때 문서 내용 저장
	 * 
	 * @param docHash
	 *            서버가 부여한 문서 해쉬
	 * @param senderHash
	 *            보내는 사람 해쉬 (자기자신)
	 * @param title
	 *            문서 제목
	 * @param content
	 *            문서 내용
	 * @param createdTS
	 *            문서를 보낸 TS
	 * @param files
	 *            첨부파일정보. \n @see {Document.ATTACH_FILE_URL}, @see
	 *            {Document.ATTACH_FILE_NAME}, @see
	 *            {Document.ATTACH_FILE_TYPE}, @see
	 *            {Document.ATTACH_FILE_SIZE} 가 key로 설정되어야함
	 */
	public void saveDocumentOnSend(String docHash, String senderHash, String title, String content, long createdTS, ArrayList<HashMap<String, Object>> files)
	{
		// document 테이블에 insert
		String sql = "insert into " + DBSchema.DOCUMENT.TABLE_NAME + " (" + DBSchema.DOCUMENT.COLUMN_IDX + "," + DBSchema.DOCUMENT.COLUMN_CREATOR_IDX + "," + DBSchema.DOCUMENT.COLUMN_TITLE + ","
				+ DBSchema.DOCUMENT.COLUMN_CONTENT + "," + DBSchema.DOCUMENT.COLUMN_CREATED_TS + "," + DBSchema.DOCUMENT.COLUMN_IS_CHECKED + "," + DBSchema.DOCUMENT.COLUMN_CHECKED_TS + ","
				+ DBSchema.DOCUMENT.COLUMN_CATEGORY + ") " + "values(?, ?, ?, ?, " + String.valueOf(createdTS) + ", 1, " + String.valueOf(createdTS) + ", " + Document.TYPE_DEPARTED + ")";
		String[] val = { docHash, senderHash, title, content };
		db.execSQL(sql, val);

		// doc rowid
		long docId = lastInsertId();
		saveAttachmentInfo(docId, files);
	}

	public void saveDocumentOnForward(String docHash, String senderHash, String title, String content, long createdTS, ArrayList<HashMap<String, Object>> files,
			ArrayList<HashMap<String, Object>> forwards)
	{
		saveDocumentOnSend(docHash, senderHash, title, content, createdTS, files);
		addForwardInfo(docHash, forwards);
	}

	private void addForwardInfo(String docHash, ArrayList<HashMap<String, Object>> forwards)
	{

		long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);
		if (docId == Constants.NOT_SPECIFIED)
		{
			return;
		}

		db.beginTransaction();
		try
		{

			for (int i = 0; i < forwards.size(); i++)
			{
				String sql = "insert into " + DBSchema.DOCUMENT_FORWARD.TABLE_NAME + " (" + DBSchema.DOCUMENT_FORWARD.COLUMN_DOC_ID + "," + DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARDER_IDX + ","
						+ DBSchema.DOCUMENT_FORWARD.COLUMN_COMMENT + "," + DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS + ")" + " values (" + String.valueOf(docId) + ", ?, ?, "
						+ String.valueOf(forwards.get(i).get(KEY.DOCUMENT.FORWARDER_IDX)) + ")";
				String[] val = { forwards.get(i).get(KEY.DOCUMENT.FORWARDER_IDX).toString(), forwards.get(i).get(KEY.DOCUMENT.FORWARD_CONTENT).toString() };
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
	 * 문서를 받았을 때 저장
	 * 
	 * @param docHash
	 *            서버가 부여한 문서 해쉬
	 * @param senderHash
	 *            문서 작성자의 해쉬
	 * @param title
	 *            문서 제목
	 * @param content
	 *            문서 내용
	 * @param createdTS
	 *            문서를 만든 시점의 타임스탬프
	 * @param forwards
	 *            포워딩 정보 \n @see {Document.FWD_CONTENT}, @see
	 *            {Document.FWD_ARRIVAL_DT}, @see
	 *            {Document.FWD_FORWARDER_IDX} 가 key로 설정되어야함
	 * @param files
	 *            첨부파일정보. \n @see {Document.ATTACH_FILE_URL}, @see
	 *            {Document.ATTACH_FILE_NAME}, @see
	 *            {Document.ATTACH_FILE_TYPE}, @see
	 *            {Document.ATTACH_FILE_SIZE} 가 key로 설정되어야함
	 */
	public void saveDocumentOnReceived(String docHash, String senderHash, String title, String content, long createdTS, ArrayList<HashMap<String, Object>> forwards,
			ArrayList<HashMap<String, Object>> files)
	{

		// document 테이블에 insert
		String sql = "insert or ignore into " + DBSchema.DOCUMENT.TABLE_NAME + " (" + DBSchema.DOCUMENT.COLUMN_IDX + "," + DBSchema.DOCUMENT.COLUMN_CREATOR_IDX + ","
				+ DBSchema.DOCUMENT.COLUMN_TITLE + "," + DBSchema.DOCUMENT.COLUMN_CONTENT + "," + DBSchema.DOCUMENT.COLUMN_CREATED_TS + "," + DBSchema.DOCUMENT.COLUMN_IS_CHECKED + ","
				+ DBSchema.DOCUMENT.COLUMN_CATEGORY + ") " + "values(?, ?, ?, ?, " + String.valueOf(createdTS) + ", 0 ," + Document.TYPE_RECEIVED + ")";
		String[] val = { docHash, senderHash, title, content };
		db.execSQL(sql, val);

		// doc rowid
		long docId = lastInsertId();
		// 포워딩정보저장
		if (forwards != null)
			addForwardInfo(docHash, forwards);
		if (files != null)
			saveAttachmentInfo(docId, files);
	}

	/**
	 * 문서의 즐겨찾기 상태 toggle
	 * 
	 * @param hash
	 *            문서 해쉬
	 */
	public void setFavorite(String hash, boolean isFavorite)
	{
		long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, hash);
		if (docId == Constants.NOT_SPECIFIED)
		{
			return;
		}

		int isFavorite_int;
		if (isFavorite == true)
		{
			isFavorite_int = 1;
		}
		else
		{
			isFavorite_int = 0;
		}
		String sql = "update " + DBSchema.DOCUMENT.TABLE_NAME + " SET " + DBSchema.DOCUMENT.COLUMN_IS_FAVORITE + " = " + String.valueOf(isFavorite_int) + " where _id = " + String.valueOf(docId);
		db.execSQL(sql);
	}

	/**
	 * 문서를 확인했을 때
	 * 
	 * @param docHash
	 *            채팅 해쉬
	 * @param checkedTS
	 *            체크한 시간
	 */
	public void updateCheckedTS(String docHash, long checkedTS)
	{
		long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);
		if (docId == Constants.NOT_SPECIFIED)
		{
			return;
		}
		String sql = "update " + DBSchema.DOCUMENT.TABLE_NAME + " SET " + DBSchema.DOCUMENT.COLUMN_IS_CHECKED + " = 1," + DBSchema.DOCUMENT.COLUMN_CHECKED_TS + " = " + String.valueOf(checkedTS)
				+ " where _id = " + String.valueOf(docId);
		db.execSQL(sql);
	}

	/**
	 * 문서 목록 가져오기
	 * 
	 * @b 커서구조
	 * @b COLUMN_DOC_IDX str 문서해쉬\n
	 * @b COLUMN_DOC_TITLE str 문서제목\n
	 * @b COLUMN_IS_CHECKED int 자기가확인했는지\n
	 * @b COLUMN_SENDER_IDX str 문서보낸사람\n
	 * @b COLUMN_CREATED_TS long 문서생성일(보낸시간)\n
	 * @param docCategory
	 *            문서타입 @see {Document.TYPE_RECEIVED} @see
	 *            {Document.TYPE_FAVORITE} @see {Document.TYPE_DEPARTED}
	 * @return
	 */
	public Cursor getDocumentList(int docCategory)
	{

		if (docCategory == Document.TYPE_FAVORITE)
		{
			String sql = "select _id," + DBSchema.DOCUMENT.COLUMN_IDX + COLUMN_DOC_IDX + ", " + DBSchema.DOCUMENT.COLUMN_TITLE + COLUMN_DOC_TITLE + ", " + DBSchema.DOCUMENT.COLUMN_IS_CHECKED
					+ COLUMN_IS_CHECKED + ", " + DBSchema.DOCUMENT.COLUMN_CREATOR_IDX + COLUMN_SENDER_IDX + ", " + DBSchema.DOCUMENT.COLUMN_CREATED_TS + COLUMN_CREATED_TS + " from"
					+ DBSchema.DOCUMENT.TABLE_NAME + "where " + DBSchema.DOCUMENT.COLUMN_IS_FAVORITE + " = 1 " + " order by " + DBSchema.DOCUMENT.COLUMN_CREATED_TS + " desc ";
			return db.rawQuery(sql, null);
		}
		else
		{
			String sql = "select _id, " + DBSchema.DOCUMENT.COLUMN_IDX + COLUMN_DOC_IDX + ", " + DBSchema.DOCUMENT.COLUMN_TITLE + COLUMN_DOC_TITLE + ", " + DBSchema.DOCUMENT.COLUMN_IS_CHECKED
					+ COLUMN_IS_CHECKED + ", " + DBSchema.DOCUMENT.COLUMN_CREATOR_IDX + COLUMN_SENDER_IDX + ", " + DBSchema.DOCUMENT.COLUMN_CREATED_TS + COLUMN_CREATED_TS + " from"
					+ DBSchema.DOCUMENT.TABLE_NAME + "where " + DBSchema.DOCUMENT.COLUMN_CATEGORY + " = " + String.valueOf(docCategory) + " order by " + DBSchema.DOCUMENT.COLUMN_CREATED_TS
					+ " desc ";
			return db.rawQuery(sql, null);
		}

	}

	/**
	 * 한 문서의 기본 정보 조회(포워딩,파일빼고)
	 * 
	 * @b 커서구조
	 * @b COLUMN_DOC_TITLE str 제목\n
	 * @b COLUMN_DOC_CONTENT str 내용\n
	 * @b COLUMN_SENDER_IDX str 발신자\n
	 * @b COLUMN_DOC_TS long 발신일시\n
	 * @b COLUMN_DOC_TYPE int 문서카테고리 Document.TYPE_DEPARTED,
	 *    Document.TYPE_RECEIVED, Document.TYPE_FAVORITE\n
	 * @b COLUMN_IS_FAVORITE int 즐겨찾기여부
	 * @b COLUMN_IS_CHECKED int 자기가확인했는지\n
	 * @b COLUMN_CHECKED_TS long 확인한시간
	 * @param docHash
	 *            문서 해시
	 * @return
	 */
	public Cursor getDocumentContent(String docHash)
	{

		long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);

		String sql = "select _id," + DBSchema.DOCUMENT.COLUMN_TITLE + COLUMN_DOC_TITLE + ", " + DBSchema.DOCUMENT.COLUMN_CONTENT + COLUMN_DOC_CONTENT + ", " + DBSchema.DOCUMENT.COLUMN_CREATOR_IDX
				+ COLUMN_SENDER_IDX + ", " + DBSchema.DOCUMENT.COLUMN_CREATED_TS + COLUMN_DOC_TS + ", " + DBSchema.DOCUMENT.COLUMN_CHECKED_TS + COLUMN_CHECKED_TS + ", "
				+ DBSchema.DOCUMENT.COLUMN_CATEGORY + COLUMN_DOC_TYPE + ", " + DBSchema.DOCUMENT.COLUMN_IS_CHECKED + COLUMN_IS_CHECKED + ", " + DBSchema.DOCUMENT.COLUMN_IS_FAVORITE
				+ COLUMN_IS_FAVORITE + " from" + DBSchema.DOCUMENT.TABLE_NAME + "where _id = " + String.valueOf(docId);
		return db.rawQuery(sql, null);
	}

	/**
	 * 문서의 포워딩 정보
	 * 
	 * @b 커서구조
	 * @b COLUMN_FORWARDER_IDX str 포워더\n
	 * @b COLUMN_FORWARD_COMMENT str 코멘트\n
	 * @b COLUMN_FORWARD_TS long 포워딩한 시간\n
	 * @param docHash
	 * @return
	 */
	public Cursor getDocumentForwardInfo(String docHash)
	{
		long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);

		String sql = "select _id, " + DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARDER_IDX + COLUMN_FORWARDER_IDX + ", " + DBSchema.DOCUMENT_FORWARD.COLUMN_COMMENT + COLUMN_FORWARD_COMMENT + ", "
				+ DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS + COLUMN_FORWARD_TS + " from" + DBSchema.DOCUMENT_FORWARD.TABLE_NAME + " where " + DBSchema.DOCUMENT_FORWARD.COLUMN_DOC_ID + " = "
				+ String.valueOf(docId) + " order by " + DBSchema.DOCUMENT_FORWARD.COLUMN_FORWARD_TS + " desc";
		return db.rawQuery(sql, null);
	}

	/**
	 * 문서의 첨부파일 정보
	 * 
	 * @b 커서구조
	 * @b COLUMN_FILE_NAME str 파일이름\n
	 * @b COLUMN_FILE_TYPE int 파일종류\n
	 * @b COLUMN_FILE_SIZE long 파일사이즈 in byte\n
	 * @b COLUMN_FILE_IDX str 파일URL\n
	 * @param docHash
	 * @return
	 */
	public Cursor getDocumentAttachment(String docHash)
	{
		long docId = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, docHash);

		String sql = "select _id, " + DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_NAME + COLUMN_FILE_NAME + ", " + DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_TYPE + COLUMN_FILE_TYPE + ", "
				+ DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_SIZE_IN_BYTE + COLUMN_FILE_SIZE + ", " + DBSchema.DOCUMENT_ATTACHMENT.COLUMN_FILE_IDX + COLUMN_FILE_IDX + " from"
				+ DBSchema.DOCUMENT_ATTACHMENT.TABLE_NAME + "where _id = " + String.valueOf(docId);
		return db.rawQuery(sql, null);
	}

	/**
	 * 문서 수신자 정보 가져오기
	 * 
	 * @b 커서구조
	 * @b COLUMN_USER_IDX 수신자해쉬\n
	 * @param hash
	 *            문서 해쉬
	 * @return
	 */
	public Cursor getReceivers(String hash)
	{
		long id = hashToId(DBSchema.DOCUMENT.TABLE_NAME, DBSchema.DOCUMENT.COLUMN_IDX, hash);

		String sql = "select _id, " + DBSchema.DOCUMENT_RECEIVER.COLUMN_RECEIVER_IDX + " from" + DBSchema.DOCUMENT_RECEIVER.TABLE_NAME + " where " + DBSchema.DOCUMENT_RECEIVER.COLUMN_DOC_ID
				+ " = " + String.valueOf(id);
		return db.rawQuery(sql, null);
	}

	/**
	 * 문서검색
	 * 
	 * @b 커서구조
	 * @b COLUMN_DOC_TITLE str 제목\n
	 * @b COLUMN_DOC_CONTENT str 내용\n
	 * @b COLUMN_SENDER_IDX str 발신자\n
	 * @b COLUMN_DOC_TS long 발신일시\n
	 * @b COLUMN_DOC_TYPE int 문서카테고리 Document.TYPE_DEPARTED,
	 *    Document.TYPE_RECEIVED, Document.TYPE_FAVORITE\n
	 * @b COLUMN_IS_FAVORITE int 즐겨찾기여부
	 * @b COLUMN_IS_CHECKED int 자기가확인했는지\n
	 * @b COLUMN_CHECKED_TS long 확인한시간
	 * @param query
	 * @return
	 */
	public Cursor search(String query)
	{
		String sql = "select " + DBSchema.DOCUMENT.COLUMN_CATEGORY + COLUMN_DOC_TYPE + ", " + DBSchema.DOCUMENT.COLUMN_CHECKED_TS + COLUMN_CHECKED_TS + ", " + DBSchema.DOCUMENT.COLUMN_IS_CHECKED
				+ COLUMN_IS_CHECKED + ", " + DBSchema.DOCUMENT.COLUMN_IS_FAVORITE + COLUMN_IS_FAVORITE + ", " + DBSchema.DOCUMENT.COLUMN_CONTENT + COLUMN_DOC_CONTENT + ", "
				+ DBSchema.DOCUMENT.COLUMN_CREATED_TS + COLUMN_CREATED_TS + ", " + DBSchema.DOCUMENT.COLUMN_CREATOR_IDX + COLUMN_SENDER_IDX + ", " + DBSchema.DOCUMENT.COLUMN_IDX + COLUMN_DOC_IDX
				+ ", " + DBSchema.DOCUMENT.COLUMN_TITLE + COLUMN_DOC_TITLE + " FROM " + DBSchema.DOCUMENT.TABLE_NAME + " where " + DBSchema.DOCUMENT.COLUMN_TITLE + " LIKE ? or "
				+ DBSchema.DOCUMENT.COLUMN_CONTENT + " like ? ";
		String bind = "%" + query + "%";
		String[] val = { bind, bind };
		return db.rawQuery(sql, val);
	}
	
	public Integer getNumUnchecked()
	{
		String sql = "SELECT count(_id) n FROM "+DBSchema.DOCUMENT.TABLE_NAME+
					" WHERE "+DBSchema.DOCUMENT.COLUMN_IS_CHECKED+" = 0" +
					" AND "+DBSchema.DOCUMENT.COLUMN_CATEGORY+" = "+Document.TYPE_RECEIVED;
		Cursor c = db.rawQuery(sql, null);
		Integer n = 0;
		if (c.moveToNext())
		{
			n = c.getInt(0);
		}
		
		c.close();
		return n;
	}

	public static final String	COLUMN_DOC_IDX			= "doc_idx";
	public static final String	COLUMN_DOC_TITLE		= "doc_title";
	public static final String	COLUMN_DOC_CONTENT		= "doc_content";
	public static final String	COLUMN_DOC_TS			= "doc_ts";
	public static final String	COLUMN_CHECKED_TS		= "checked_TS";
	public static final String	COLUMN_DOC_TYPE			= "doc_type";
	public static final String	COLUMN_IS_FAVORITE		= "is_favorite";
	public static final String	COLUMN_IS_CHECKED		= "is_checked";
	public static final String	COLUMN_SENDER_IDX		= "sender_idx";
	public static final String	COLUMN_CREATED_TS		= "created_ts";
	public static final String	COLUMN_FORWARDER_IDX	= "fwder_idx";
	public static final String	COLUMN_FORWARD_COMMENT	= "fwd_comment";
	public static final String	COLUMN_FORWARD_TS		= "fwd_ts";
	public static final String	COLUMN_FILE_NAME		= "file_name";
	public static final String	COLUMN_FILE_TYPE		= "file_type";
	public static final String	COLUMN_FILE_SIZE		= "file_size";
	public static final String	COLUMN_FILE_IDX			= "file_idx";
	public static final String	COLUMN_USER_IDX			= "user_idx";

}