package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.R;
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
	public DocumentListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}
	
	public DocumentListAdapter(Context context, Cursor c, boolean autoRequery, int type) {
		super(context, c, autoRequery);
		this.type = type;
	}

	public DocumentListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		//TODO
		if(this.type == Document.TYPE_DEPARTED) {
			LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_departed);
			TextView titleTV = (TextView)v.findViewById(R.id.title);
			TextView senderTV = (TextView)v.findViewById(R.id.sender);
			TextView arrivalDTTV = (TextView)v.findViewById(R.id.arrivalDT);
			Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
			
			String title = "";
			title = c.getString(c.getColumnIndex("title"));
			long senderIdx = c.getLong(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx((int)senderIdx);
			String sender = user.getDepartmentFull() + " "+User.RANK[user.rank] +" "+ user.name;
			
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
			long senderIdx = c.getLong(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx((int)senderIdx);
			String sender = user.getDepartmentFull() + " "+User.RANK[user.rank] +" "+ user.name;
			
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
			long senderIdx = c.getLong(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx((int)senderIdx);
			String sender = user.getDepartmentFull() + " "+User.RANK[user.rank] +" "+ user.name;
			
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
	
	/*
	public Document getDocument(int position) {
		Cursor c = (Cursor) getItem(position);
		return new Document(c);
	}*/

}
