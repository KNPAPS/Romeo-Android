package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class RoomListView extends ListView implements OnItemClickListener {
	
	public int type = Chat.NOT_SPECIFIED;
	private Context context = null;
	private String tableName = null; 
	
	public 	CursorAdapter 	listAdapter; 
	private	SQLiteDatabase	db;
	
	// Constructor
	public RoomListView(Context context) {
		this(context, null);
	}

	public RoomListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		this.context = context;
	}
	
	
	// Database Managemant
	public void setDatabase(SQLiteDatabase db) {
		this.db = db;
	}
	
	public void unsetDatabase() {
		this.db = null;
	}
	
	protected Cursor selectAll() {
		String subSql = "SELECT "+BaseColumns._ID+", roomCode, MAX(TS) FROM "+this.tableName+" GROUP BY roomCode";
		String sql = "SELECT "+this.tableName+".* FROM "+this.tableName+", ("+subSql+") sq WHERE "+this.tableName+"."+BaseColumns._ID+"=sq."+BaseColumns._ID+" ORDER BY checked desc, TS desc;";
		Cursor c = db.rawQuery(sql, null);
		
		return c;
	}
	
	// View Cycle Managemanet Helper
	public void setType (int type) {
		this.type = type;
		switch(this.type) {

		case Chat.TYPE_COMMAND :
			tableName = DBManager.TABLE_COMMAND;
			break;
		case Chat.TYPE_MEETING : 
			tableName = DBManager.TABLE_MEETING;
			break;
		
		default : 
		case Chat.NOT_SPECIFIED : 
			tableName = null;
			break;
		}
		
		final Context ctx = this.context;
		
		Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
			@Override
			public String getSectionTitleForItem(Cursor c) {
				boolean checked = (c.getLong(c.getColumnIndex("checked")) >0 ? true : false);
				return (checked ? ctx.getString(R.string.checkedChat)  : ctx.getString(R.string.unCheckedChat));
			}
		};
		
		RoomListAdapter roomListAdapter = new RoomListAdapter(context, null, false, this.type);
		listAdapter = roomListAdapter;
	
		 SimpleSectionAdapter<Cursor> sectionAdapter
			= new SimpleSectionAdapter<Cursor>(context, roomListAdapter, R.layout.section_header, R.id.cell_title, sectionizer);
		this.setAdapter(sectionAdapter);
		this.setOnItemClickListener(this);
		
	}
	
	public void refresh() {
		if(listAdapter == null || this.tableName == null) return;
 
		Cursor c = selectAll();
		if(c.getCount() == 0) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inPreferredConfig = Config.RGB_565;
			
			Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.empty_set_background, options);
			int height = src.getHeight();
			int width = src.getWidth();
			Bitmap resized = Bitmap.createScaledBitmap(src, width/options.inSampleSize, height/options.inSampleSize, true);
			this.setBackgroundDrawable(new BitmapDrawable(getResources(), resized));
		} else {
			this.setBackgroundResource(R.color.light);
		}

		listAdapter.changeCursor(c);
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			((SimpleSectionAdapter)this.getAdapter()).notifyDataSetChanged();
	}

	
	// Click Listener
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		// 추가 정보
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
		
		Room room = new Room(c, this.type);
		RoomFragment fragment = new RoomFragment(room);
		MainActivity.sharedActivity().pushContent(fragment);
	}
	
	

}
