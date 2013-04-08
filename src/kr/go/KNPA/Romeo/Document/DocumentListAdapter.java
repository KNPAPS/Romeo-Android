package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.DocumentProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
class DocumentListAdapter extends CursorAdapter {
	public int type = Document.NOT_SPECIFIED;
	public DocumentListAdapter(Context context, Cursor c, boolean autoRequery) 				{	super(context, c, autoRequery);						}
	public DocumentListAdapter(Context context, Cursor c, boolean autoRequery, int type) 	{	super(context, c, autoRequery);	this.type = type;	}
	public DocumentListAdapter(Context context, Cursor c, int flags) 						{	super(context, c, flags);							}

	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		
		DBProcManager.sharedManager(ctx);
		// 문서해쉬 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_HASH));
		// 문서제목 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_TITLE));
		// 자기가확인했는지 (int)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_IS_CHECKED));
		// 문서보낸사람 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_SENDER_HASH));
		// 문서생성일(보낸시간) (long)
		c.getLong(c.getColumnIndex(DocumentProcManager.COLUMN_CREATED_TS));
		
		
		/*getDocumentContent(String docHash) 한 문서의 기본 정보 조회(포워딩,파일빼고) */
		// 제목 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_TITLE));
		// 내용 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_CONTENT));
		// 발신자 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_SENDER_HASH));
		// 발신일시 (long)
		c.getLong(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_TS));
		
		/* getDocumentForwardInfo(String docHash) 문서의 포워딩 정보	 */
		// 포워더 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARDER_HASH));
		// 코멘트 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_COMMENT));
		// 포워딩한 시간 (long)
		c.getLong(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_TS));
		
		/* getDocumentAttachment(String docHash) : 문서의 첨부파일 정보	*/
		// 파일이름 (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARDER_HASH));
		// 파일종류 (int)
		c.getInt(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_TS));
		// 파일사이즈 (long)
		c.getLong(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARD_TS));
		// 파일URL (String)
		c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_FORWARDER_HASH));
		
		//TODO
		if(this.type == Document.TYPE_DEPARTED) {
			
			LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_departed);
			TextView titleTV = (TextView)v.findViewById(R.id.title);
			TextView senderTV = (TextView)v.findViewById(R.id.sender);
			TextView arrivalDTTV = (TextView)v.findViewById(R.id.arrivalDT);
			Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
			
			String title = "";
			title = c.getString(c.getColumnIndex("title"));
			String senderIdx = c.getString(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx(senderIdx);
			String sender = user.department.nameFull + " "+User.RANK[user.rank] +" "+ user.name;
			
			String arrivalDT = "";
			long arrivalTS = c.getLong(c.getColumnIndex("TS"));
			arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
			
			
			titleTV.setText(title);
			senderTV.setText(sender);
			arrivalDTTV.setText(arrivalDT);
			
		} else if(this.type == Document.TYPE_RECEIVED) {
			LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_received);
			TextView titleTV = (TextView)v.findViewById(R.id.title);
			TextView senderTV = (TextView)v.findViewById(R.id.sender);
			TextView arrivalDTTV = (TextView)v.findViewById(R.id.arrivalDT);
			
			String title = "";
			title = c.getString(c.getColumnIndex("title"));
			String senderIdx = c.getString(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx(senderIdx);
			String sender = user.department.nameFull + " "+User.RANK[user.rank] +" "+ user.name;
			
			String arrivalDT = "";
			long arrivalTS = c.getLong(c.getColumnIndex("TS"));
			arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
			
			
			titleTV.setText(title);
			senderTV.setText(sender);
			arrivalDTTV.setText(arrivalDT);
			
		} else if(this.type == Document.TYPE_FAVORITE) {
			LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_received);
			TextView titleTV = (TextView)v.findViewById(R.id.title);
			TextView senderTV = (TextView)v.findViewById(R.id.sender);
			TextView arrivalDTTV = (TextView)v.findViewById(R.id.arrivalDT);
			
			String title = "";
			title = c.getString(c.getColumnIndex("title"));
			String senderIdx = c.getString(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx(senderIdx);
			String sender = user.department.nameFull + " "+User.RANK[user.rank] +" "+ user.name;
			
			String arrivalDT = "";
			long arrivalTS = c.getLong(c.getColumnIndex("TS"));
			arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
			
			
			titleTV.setText(title);
			senderTV.setText(sender);
			arrivalDTTV.setText(arrivalDT);
			
		}

	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = null;
		switch(this.type) {
			case Document.TYPE_DEPARTED : v = inflater.inflate(R.layout.document_list_cell_departed, parent,false);		break;
			case Document.TYPE_RECEIVED : v = inflater.inflate(R.layout.document_list_cell_received, parent,false);		break;
			case Document.TYPE_FAVORITE : v = inflater.inflate(R.layout.document_list_cell_favorite, parent,false);		break;
			default :
			case Document.NOT_SPECIFIED : break;
			// ListView에서 tableName이 정해저야만 Adapter를 호출할 수 있는데,
			//이가 정해지기 위해서는 type이 정해진 상태이어야 하므로, 
			//이 지점에 도달 할 수가 없다. 불가능!!
		}
		
		return v;
	}
}
