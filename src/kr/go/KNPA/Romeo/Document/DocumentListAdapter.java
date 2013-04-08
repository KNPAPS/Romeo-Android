package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.DocumentProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
class DocumentListAdapter extends CursorAdapter {
	public int type = Document.NOT_SPECIFIED;
	public DocumentListAdapter(Context context, Cursor c, boolean autoRequery) 				{	super(context, c, autoRequery);						}
	public DocumentListAdapter(Context context, Cursor c, boolean autoRequery, int type) 	{	super(context, c, autoRequery);	this.type = type;	}
	public DocumentListAdapter(Context context, Cursor c, int flags) 						{	super(context, c, flags);							}

	@Override
	public void bindView(View v, final Context ctx, Cursor c) {
		
		DBProcManager.sharedManager(ctx).document();
		// 문서해쉬 (String)
		final String docIdx = c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_IDX));
		// 확인여부 (int)
		
		//boolean docChecked = ( c.getInt(c.getColumnIndex(DocumentProcManager.COLUMN_IS_CHECKED)) > 0) ? true : false;
		// 문서제목 (String)
		String title = c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_DOC_TITLE));
		
		// 발신자 (String)
		User sender = User.getUserWithIdx( c.getString(c.getColumnIndex(DocumentProcManager.COLUMN_SENDER_IDX)) );
		String senderInfo = sender.department.nameFull + " "+User.RANK[sender.rank] +" "+ sender.name;
		// 발신일시 (long)
		long TS =  c.getLong(c.getColumnIndex(DocumentProcManager.COLUMN_CREATED_TS));
		String DT = Formatter.timeStampToRecentString(TS);
		
		
		TextView titleTV = (TextView)v.findViewById(R.id.title);
		TextView senderTV = (TextView)v.findViewById(R.id.sender);
		TextView arrivalDTTV = (TextView)v.findViewById(R.id.arrivalDT);
	
		titleTV.setText(title);
		senderTV.setText(senderInfo);
		arrivalDTTV.setText(DT);
		
		if(this.type == Document.TYPE_DEPARTED) {
			Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
			goUnchecked.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(ctx, UserListActivity.class);
					Bundle b = new Bundle();
					b.putStringArrayList("idxs", Document.getUncheckersIdxsWithMessageTypeAndIndex(type, docIdx));
					intent.putExtras(b);
					ctx.startActivity(intent);
				}
			});
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
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
}
