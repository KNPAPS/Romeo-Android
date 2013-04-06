package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBManager;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ChatListView extends RomeoListView {
	// Constants
	private final int NUMBER_OF_INITIAL_RECENT_ITEM = 10;
	
	// Variables
	private Room room;
	private int currentNumberOfRecentItem = NUMBER_OF_INITIAL_RECENT_ITEM;

	// Constructor
	public ChatListView(Context context) {
		this(context,null);
	}

	public ChatListView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public ChatListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	// Initializer
	public void setRoom(Room room) {
		this.room = room;		
		listAdapter = new ChatListAdapter(getContext(), null, false, room.type);
		this.setAdapter(listAdapter);
		scrollToBottom();
	}
	
	// Manage NumberOfItems
	public void increaseNumberOfItemsBy(int nItem) {
		this.currentNumberOfRecentItem += nItem;
	}
	
	public void decreaseNumberOfItemsBy(int nItem) { 
		this.currentNumberOfRecentItem = Math.max(this.currentNumberOfRecentItem - nItem, 0); 
	}

	// Database
	@Override
	protected Cursor query() {
		return query(currentNumberOfRecentItem);
	}
	
	@Override
	public String getTableName() {
		return room.getTableName();
	}
	
	public Cursor query(int nItems) {
		Cursor c = null;
		if(getTableName() != null && this.room.roomCode != null) {	// TODO

			String subSql = "SELECT "+BaseColumns._ID+" FROM " + getTableName() +
					 " WHERE roomCode=\""+this.room.roomCode+"\""+
					 " ORDER BY TS DESC"+
					 " LIMIT "+nItems;
			String sql = "SELECT * FROM "+getTableName()+
					 " WHERE "+BaseColumns._ID+" IN ("+subSql+")"+
					 " ORDER BY TS ASC"+
					 " LIMIT "+nItems+";";

			c = db.rawQuery(sql, null);
		}
		return c;
	}

	// refresh()
	@Override
	public void refresh() {
		super.refresh();
		scrollToBottom();
	}
	
	public void scrollToBottom() {
		this.setSelectionFromTop(this.getCount(), 0);
	}

	@Override
	public ChatListView initWithType(int type) {
		// DO NOthing
		return null;
	}


}
