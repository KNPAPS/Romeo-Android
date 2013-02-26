package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.widget.ListView;

public class ChatListView extends ListView {
	// Constants
	private final int NUMBER_OF_INITIAL_RECENT_ITEM = 10;
		
	// Database
	private SQLiteDatabase db;
	public String tableName;
	
	// Adapter	
	public CursorAdapter listAdapter; 
	
	public String roomCode;
	private Room _room;
	
	private Context context = null;
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
		this.context = context;
	}
	
	
	// Manage NumberOfItems
	public void increaseNumberOfItemsBy(int nItem) {
		this.currentNumberOfRecentItem += nItem;
	}
	
	public void decreaseNumberOfItemsBy(int nItem) {
		this.currentNumberOfRecentItem = Math.max(this.currentNumberOfRecentItem - nItem, 0); 
	}
	
	
	// Manage Model (Room)
	public void setRoom(Room room) {
		_room = room;
		this.roomCode = room.roomCode;
		this.tableName = room.getTableName();
		
		Cursor c = selectRecent(NUMBER_OF_INITIAL_RECENT_ITEM);
		listAdapter = new ChatListAdapter(context, c, false, room.type);

		this.setAdapter(listAdapter);
		scrollToBottom();
	}
	
	private Room getRoom() {
		return _room;
	}

	// Database
	public void setDatabase(SQLiteDatabase db) {
		this.db = db;
	}
	
	public void unsetDatabase() {
		this.db = null;
	}
	
	public Cursor selectRecent(int nItems) {
		Cursor c = null;
		if(this.tableName != null && this.roomCode != null) {	// TODO
//			String sql = "SELECT * FROM "+this.tableName+
//						 " WHERE roomCode=\""+this.roomCode+"\""+
//						 " ORDER BY TS ASC"+
//						 " LIMIT "+nItems+";";
	
			String subSql = "SELECT "+BaseColumns._ID+" FROM " + this.tableName +
					 " WHERE roomCode=\""+this.roomCode+"\""+
					 " ORDER BY TS DESC"+
					 " LIMIT "+nItems;
			String sql = "SELECT * FROM "+this.tableName+
					 " WHERE "+BaseColumns._ID+" IN ("+subSql+")"+
					 " ORDER BY TS ASC"+
					 " LIMIT "+nItems+";";

			c = db.rawQuery(sql, null);
		}
		return c;
	}
	
	public void refresh() {
		if(listAdapter == null) return;

		listAdapter.changeCursor(selectRecent(currentNumberOfRecentItem));
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			((SimpleSectionAdapter)this.getAdapter()).notifyDataSetChanged();
		
		scrollToBottom();
	}
	
	public void scrollToBottom() {
		this.setSelectionFromTop(this.getCount(), 0);
	}
}
