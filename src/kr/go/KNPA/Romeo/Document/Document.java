package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.DocumentProcManager;
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
	
	public	static final String FWD_FORWARDER_IDX 	= DocumentProcManager.COLUMN_FORWARDER_HASH;
	public	static final String FWD_ARRIVAL_DT 		= DocumentProcManager.COLUMN_FORWARD_TS;
	public	static final String FWD_CONTENT 		= DocumentProcManager.COLUMN_FORWARD_COMMENT;
	
	public	static final String ATTACH_FILE_IDX 	= DocumentProcManager.COLUMN_FILE_HASH; // (string)
	public	static final String ATTACH_FILE_NAME 	= DocumentProcManager.COLUMN_FILE_NAME; // (string)
	public	static final String ATTACH_FILE_TYPE 	= DocumentProcManager.COLUMN_FILE_TYPE; // (int)
	public	static final String ATTACH_FILE_SIZE 	= DocumentProcManager.COLUMN_FILE_SIZE; // (long)
			
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
	
	public Document(Context context, Cursor c) {
		//super(c);
		
		this.idx = c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_HASH));
		
		DocumentProcManager dpm = DBProcManager.sharedManager(context).document();
		
		/*getDocumentContent(String docHash) 한 문서의 기본 정보 조회(포워딩,파일빼고) */
		// 제목 (String)
		Cursor cursor_documentInfo = dpm.getDocumentContent(this.idx);
		cursor_documentInfo.moveToFirst();
		this.title = cursor_documentInfo.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_TITLE));
		// 내용 (String)
		this.content = cursor_documentInfo.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_CONTENT));
		// 발신자 (String)
		this.sender = User.getUserWithIdx( cursor_documentInfo.getString(c.getColumnIndex(DocumentProcManager.COLUMN_SENDER_HASH)) );
		// 발신일시 (long)
		this.TS = cursor_documentInfo.getLong(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_TS));
		// 문서카테고리 (int) Document.TYPE_DEPARTED, Document.TYPE_RECEIVED
		this.type = Message.MESSAGE_TYPE_DOCUMENT * Message.MESSAGE_TYPE_DIVIDER + cursor_documentInfo.getInt(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_TYPE));
		// 즐겨찾기여부 (int)
		this.favorite = ( cursor_documentInfo.getInt(c.getColumnIndex(DocumentProcManager.COLUMN_IS_FAVORITE)) > 0 ) ? true : false;
		
		this.checked = ( c.getInt(c.getColumnIndex(DocumentProcManager.COLUMN_IS_CHECKED)) > 0) ? true : false;
		this.checkTS =;
		
				
		// this.receivers = receivers;
		// this.received = received;
				
		/* getDocumentForwardInfo(String docHash) 문서의 포워딩 정보	 */
		Cursor cursor_forwardInfo = dpm.getDocumentForwardInfo(this.idx);
		if(cursor_forwardInfo.getCount() > 0) {
			ArrayList<HashMap<String, Object>> fwds = new ArrayList<HashMap<String, Object>>();
			cursor_forwardInfo.moveToFirst();
			while( !cursor_forwardInfo.isAfterLast() ) {
				HashMap<String, Object> fwd = new HashMap<String, Object>();
				// 포워더 (String)
				cursor_forwardInfo.getString(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARDER_HASH));
				// 코멘트 (String)
				cursor_forwardInfo.getString(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_COMMENT));
				// 포워딩한 시간 (long)
				cursor_forwardInfo.getLong(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_TS));
				
				fwds.add(fwd);
			}
			this.forwards = fwds;
		}
		
		/* getDocumentAttachment(String docHash) : 문서의 첨부파일 정보	*/
		Cursor cursor_attInfo = dpm.getDocumentAttachment(this.idx);
		if(cursor_attInfo.getCount() > 0) {
			ArrayList<HashMap<String, Object>> fs = new ArrayList<HashMap<String, Object>>();
			cursor_attInfo.moveToFirst();
			while( !cursor_attInfo.isAfterLast() ) {
				HashMap<String, Object> f = new HashMap<String, Object>();
				
				// 파일이름 (String)
				cursor_attInfo.getString(c.getColumnIndex(DocumentProcManager.COLUMN_FILE_NAME));
				// 파일종류 (int)
				cursor_attInfo.getInt(c.getColumnIndex(DocumentProcManager.COLUMN_FILE_TYPE));
				// 파일사이즈 (long)
				cursor_attInfo.getLong(c.getColumnIndex(DocumentProcManager.COLUMN_FILE_SIZE));
				// 파일 hash (String)
				cursor_attInfo.getString(c.getColumnIndex(DocumentProcManager.COLUMN_FILE_HASH));
				
				fs.add(f);
			}
			this.files = fs;
		}
	
	}

	public Document(Parcel source) {
		readFromParcel(source);
	}

	
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
	public void afterSend(Context context, boolean successful) {
		if(successful) {
			// Success
			DBProcManager.sharedManager(context).document().saveDocumentOnSend(this.idx, this.sender.idx, this.title, this.content, createdTS, files)
		}  else {
			// Failure
		}
		// TODO : Animation 처리
	}
}
