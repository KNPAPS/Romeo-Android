package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.DocumentProcManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;

public class Document extends Message {// implements Parcelable{
	
	// Message Sub Type Constants
	public static final int TYPE_RECEIVED = 0;
	public static final int TYPE_DEPARTED = 1;
	public static final int TYPE_FAVORITE = 2;
			
	// Specific Variables not to be sent
	public boolean favorite = false;
	
	public ArrayList<HashMap<String, Object>> forwards;
	public	ArrayList<HashMap<String, Object>> files; 
	
	// Constructor
	public Document() {}
	
	public Document(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		if(jo.has(KEY.DOCUMENT.FORWARDS)) {
			
			JSONArray jForwards = jo.getJSONArray(KEY.DOCUMENT.FORWARDS);
			
			ArrayList<HashMap<String, Object>> forwards = new ArrayList<HashMap<String, Object>>();
			for(int i=0; i<jForwards.length(); i++) {
				JSONObject jFwd = jForwards.getJSONObject(i);
				HashMap<String, Object> hmap = new HashMap<String, Object>();
				hmap.put(KEY.DOCUMENT.FORWARDER_IDX,	jFwd.getString(KEY.DOCUMENT.FORWARDER_IDX));
				hmap.put(KEY.DOCUMENT.FORWARD_TS, 		jFwd.getLong(KEY.DOCUMENT.FORWARD_TS));
				hmap.put(KEY.DOCUMENT.FORWARD_TS, 		jFwd.getLong(KEY.DOCUMENT.FORWARD_TS));
			}
			this.forwards = forwards;
		}
		
		if(jo.has(KEY.DOCUMENT.FILES)) {
			JSONArray jFiles = jo.getJSONArray(KEY.DOCUMENT.FILES);
			ArrayList<HashMap<String,Object>> files = new ArrayList<HashMap<String, Object>>();
			
			for(int i=0; i<jFiles.length(); i++) {
				JSONObject jFile = jFiles.getJSONObject(i);
				HashMap<String, Object> hmap = new HashMap<String, Object>();
				hmap.put(KEY.DOCUMENT.FILE_IDX, jFile.getString(KEY.DOCUMENT.FILE_IDX));
				hmap.put(KEY.DOCUMENT.FILE_NAME, jFile.getString(KEY.DOCUMENT.FILE_NAME));
				hmap.put(KEY.DOCUMENT.FILE_SIZE, jFile.getLong(KEY.DOCUMENT.FILE_SIZE));
				hmap.put(KEY.DOCUMENT.FILE_TYPE, jFile.getInt(KEY.DOCUMENT.FILE_TYPE));
			}
			this.files = files;
		}
	}
	
	public Document(
			String								idx, 
			int									type, 
			String								title, 
			String								content, 
			String 								senderIdx, 
			ArrayList<String>					receivers, 
			boolean								received,
			long								TS,
			boolean								checked, 
			long 								checkTS,
			ArrayList<HashMap<String, Object>> 	forwards,
			ArrayList<HashMap<String, Object>> 	files,
			boolean								favorite
			) {
		this.idx = idx;
		this.type = type;
		this.title = title;
		this.content = content;
		this.senderIdx = senderIdx;
		this.receiversIdx = receivers;
		this.received = received;
		this.TS = TS;
		this.checked = checked;
		this.checkTS = checkTS;
		this.forwards = forwards;
		this.files = files;
		this.favorite = favorite;
	}
	
	/*
	public Document(Context context, Cursor c) {
		//super(c);
		
		this.idx = c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_IDX));
		
		DocumentProcManager dpm = DBProcManager.sharedManager(context).document();
		
		//// getDocumentContent(String docHash) 한 문서의 기본 정보 조회(포워딩,파일빼고)////
		// 제목 (String)
		Cursor cursor_documentInfo = dpm.getDocumentContent(this.idx);
		cursor_documentInfo.moveToFirst();
		this.title = cursor_documentInfo.getString(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_DOC_TITLE));
		// 내용 (String)
		this.content = cursor_documentInfo.getString(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_DOC_CONTENT));
		// 발신자 (String)
		this.senderIdx = cursor_documentInfo.getString(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_SENDER_IDX));
		// 발신일시 (long)
		this.TS = cursor_documentInfo.getLong(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_DOC_TS));
		// 문서카테고리 (int) Document.TYPE_DEPARTED, Document.TYPE_RECEIVED
		int subType = cursor_documentInfo.getInt(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_DOC_TYPE));
		this.type = Message.MESSAGE_TYPE_DOCUMENT * Message.MESSAGE_TYPE_DIVIDER + subType;
		if(subType == Document.TYPE_DEPARTED) {
			this.received = false; 
		} else if (subType == Document.TYPE_RECEIVED) {
			this.received = true;
		} else {
			this.received = false;
		}
		
		// 즐겨찾기여부 (int)
		this.favorite = ( cursor_documentInfo.getInt(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_IS_FAVORITE)) > 0 ) ? true : false;
		
		this.checked = ( cursor_documentInfo.getInt(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_IS_CHECKED)) > 0) ? true : false;
		this.checkTS = cursor_documentInfo.getLong(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_CHECKED_TS));
						
		//// getDocumentForwardInfo(String docHash) 문서의 포워딩 정보	 ////
		Cursor cursor_forwardInfo = dpm.getDocumentForwardInfo(this.idx);
		if(cursor_forwardInfo.getCount() > 0) {
			ArrayList<HashMap<String, Object>> fwds = new ArrayList<HashMap<String, Object>>();
			cursor_forwardInfo.moveToFirst();
			while( !cursor_forwardInfo.isAfterLast() ) {
				HashMap<String, Object> fwd = new HashMap<String, Object>();
				// 포워더 (String)
				fwd.put(KEY.DOCUMENT.FORWARDER_IDX,  cursor_forwardInfo.getString(cursor_forwardInfo.getColumnIndex(DocumentProcManager.COLUMN_FORWARDER_IDX)) );
				// 코멘트 (String)
				fwd.put(KEY.DOCUMENT.FORWARD_TS,  cursor_forwardInfo.getString(cursor_forwardInfo.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_COMMENT)) );
				// 포워딩한 시간 (long)
				fwd.put(KEY.DOCUMENT.FORWARD_TS, cursor_forwardInfo.getLong(cursor_forwardInfo.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_TS)) );
				
				fwds.add(fwd);
			}
			this.forwards = fwds;
		}
		
		//// getDocumentAttachment(String docHash) : 문서의 첨부파일 정보	 ////
		Cursor cursor_attInfo = dpm.getDocumentAttachment(this.idx);
		if(cursor_attInfo.getCount() > 0) {
			ArrayList<HashMap<String, Object>> fs = new ArrayList<HashMap<String, Object>>();
			cursor_attInfo.moveToFirst();
			while( !cursor_attInfo.isAfterLast() ) {
				HashMap<String, Object> f = new HashMap<String, Object>();
				
				// 파일이름 (String)
				f.put( KEY.DOCUMENT.FILE_NAME , cursor_attInfo.getString(cursor_attInfo.getColumnIndex(DocumentProcManager.COLUMN_FILE_NAME)) );
				// 파일종류 (int)
				f.put( KEY.DOCUMENT.FILE_TYPE, cursor_attInfo.getInt(cursor_attInfo.getColumnIndex(DocumentProcManager.COLUMN_FILE_TYPE)) );
				// 파일사이즈 (long)
				f.put( KEY.DOCUMENT.FILE_SIZE, cursor_attInfo.getLong(cursor_attInfo.getColumnIndex(DocumentProcManager.COLUMN_FILE_SIZE)) );
				// 파일 hash (String)
				f.put( KEY.DOCUMENT.FILE_IDX, cursor_attInfo.getString(cursor_attInfo.getColumnIndex(DocumentProcManager.COLUMN_FILE_IDX)) );
				
				fs.add(f);
			}
			this.files = fs;
		}
		
		this.receiversIdx = null;	// TODO
	}
	*/
	
	public Document(Context context, String documentIdx) {
		this.idx = documentIdx;
		
		DocumentProcManager dpm = DBProcManager.sharedManager(context).document();
		
		/*getDocumentContent(String docHash) 한 문서의 기본 정보 조회(포워딩,파일빼고) */
		// 제목 (String)
		Cursor cursor_documentInfo = dpm.getDocumentContent(this.idx);
		cursor_documentInfo.moveToFirst();
		this.title = cursor_documentInfo.getString(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_DOC_TITLE));
		// 내용 (String)
		this.content = cursor_documentInfo.getString(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_DOC_CONTENT));
		// 발신자 (String)
		this.senderIdx = cursor_documentInfo.getString(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_SENDER_IDX));
		// 발신일시 (long)
		this.TS = cursor_documentInfo.getLong(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_DOC_TS));
		// 문서카테고리 (int) Document.TYPE_DEPARTED, Document.TYPE_RECEIVED
		int subType = cursor_documentInfo.getInt(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_DOC_TYPE));
		this.type = Message.MESSAGE_TYPE_DOCUMENT * Message.MESSAGE_TYPE_DIVIDER + subType;
		if(subType == Document.TYPE_DEPARTED) {
			this.received = false; 
		} else if (subType == Document.TYPE_RECEIVED) {
			this.received = true;
		} else {
			this.received = false;
		}
		
		// 즐겨찾기여부 (int)
		this.favorite = ( cursor_documentInfo.getInt(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_IS_FAVORITE)) > 0 ) ? true : false;
		
		this.checked = ( cursor_documentInfo.getInt(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_IS_CHECKED)) > 0) ? true : false;
		this.checkTS = cursor_documentInfo.getLong(cursor_documentInfo.getColumnIndex(DocumentProcManager.COLUMN_CHECKED_TS));
						
		/* getDocumentForwardInfo(String docHash) 문서의 포워딩 정보	 */
		Cursor cursor_forwardInfo = dpm.getDocumentForwardInfo(this.idx);
		if(cursor_forwardInfo.getCount() > 0) {
			ArrayList<HashMap<String, Object>> fwds = new ArrayList<HashMap<String, Object>>();
			cursor_forwardInfo.moveToFirst();
			while( !cursor_forwardInfo.isAfterLast() ) {
				HashMap<String, Object> fwd = new HashMap<String, Object>();
				// 포워더 (String)
				fwd.put(KEY.DOCUMENT.FORWARDER_IDX,  cursor_forwardInfo.getString(cursor_forwardInfo.getColumnIndex(DocumentProcManager.COLUMN_FORWARDER_IDX)) );
				// 코멘트 (String)
				fwd.put(KEY.DOCUMENT.FORWARD_TS,  cursor_forwardInfo.getString(cursor_forwardInfo.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_COMMENT)) );
				// 포워딩한 시간 (long)
				fwd.put(KEY.DOCUMENT.FORWARD_TS, cursor_forwardInfo.getLong(cursor_forwardInfo.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_TS)) );
				
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
				f.put( KEY.DOCUMENT.FILE_NAME , cursor_attInfo.getString(cursor_attInfo.getColumnIndex(DocumentProcManager.COLUMN_FILE_NAME)) );
				// 파일종류 (int)
				f.put( KEY.DOCUMENT.FILE_TYPE, cursor_attInfo.getInt(cursor_attInfo.getColumnIndex(DocumentProcManager.COLUMN_FILE_TYPE)) );
				// 파일사이즈 (long)
				f.put( KEY.DOCUMENT.FILE_SIZE, cursor_attInfo.getLong(cursor_attInfo.getColumnIndex(DocumentProcManager.COLUMN_FILE_SIZE)) );
				// 파일 hash (String)
				f.put( KEY.DOCUMENT.FILE_IDX, cursor_attInfo.getString(cursor_attInfo.getColumnIndex(DocumentProcManager.COLUMN_FILE_IDX)) );
				
				fs.add(f);
			}
			this.files = fs;
		}
		
		this.receiversIdx = null;	// TODO
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
	
	/*
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
	*/

	@Override
	public void afterSend(Context context, boolean successful) {
		if(successful) {
			// Success
			DBProcManager.sharedManager(context).document().saveDocumentOnSend(this.idx, this.senderIdx, this.title, this.content, this.TS, this.files);
		}  else {
			// Failure
		}
		// TODO : Animation 처리
	}
}
