package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
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

public class RoomListView extends RomeoListView implements OnItemClickListener {

	// Constructor
	public RoomListView(Context context) {
		this(context, null);
	}

	public RoomListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	// Database Managemant
	@Override
	protected Cursor query() {
		String subSql = "SELECT "+BaseColumns._ID+", roomCode, MAX(TS) FROM "+getTableName()+" GROUP BY roomCode";
		String sql = "SELECT "+getTableName()+".* FROM "+getTableName()+", ("+subSql+") sq WHERE "+getTableName()+"."+BaseColumns._ID+"=sq."+BaseColumns._ID+" ORDER BY checked desc, TS desc;";
		Cursor c = db.rawQuery(sql, null);

		return c;
	}
	
	@Override
	public String getTableName() {
		switch(this.type) {
			case Chat.TYPE_COMMAND : return DBManager.TABLE_COMMAND;
			case Chat.TYPE_MEETING : return DBManager.TABLE_MEETING;
			default : case Chat.NOT_SPECIFIED : return null;
		}
	}
	
	// initialize
	@Override
	public RoomListView initWithType (int type) {
		this.type = type;
		
				
		Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
			@Override
			public String getSectionTitleForItem(Cursor c) {
				boolean checked = (c.getLong(c.getColumnIndex("checked")) >0 ? true : false);
				return (checked ? getContext().getString(R.string.checkedChat)  : getContext().getString(R.string.unCheckedChat));
			}
		};
		
		listAdapter = new RoomListAdapter(getContext(), null, false, this.type);
		 SimpleSectionAdapter<Cursor> sectionAdapter
			= new SimpleSectionAdapter<Cursor>(getContext(), listAdapter, R.layout.section_header, R.id.cell_title, sectionizer);
		this.setAdapter(sectionAdapter);
		this.setOnItemClickListener(this);
		
		return this;
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
