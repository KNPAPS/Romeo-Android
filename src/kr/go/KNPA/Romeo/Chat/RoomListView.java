package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBManager;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.UserInfo;
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

/**
 * ChatFragment로 진입하면 보게되는 ListView이다. 방 목록을 포함하고 있다.
 */
public class RoomListView extends RomeoListView implements OnItemClickListener {

	/** 
	 * @name Constructors
	 * @{
	 */
	public RoomListView(Context context) 									{	this(context, null);				}
	public RoomListView(Context context, AttributeSet attrs) 				{	this(context, attrs, 0);			}
	public RoomListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle);	}
	/** @} */
	
	/** 
	 * @name Database Managemant
	 * *{
	 */
	@Override
	protected Cursor query() {	return DBProcManager.sharedManager(getContext()).chat().getRoomList(this.type);	}
	/** @} */
	
	/**
	 * @name initialize
	 * @{
	 */
	@Override
	public RoomListView initWithType (int type) {
		this.type = type;
		
		Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
			@Override
			public String getSectionTitleForItem(Cursor c) {	// TODO
				boolean checked = (c.getInt(c.getColumnIndex( DBProcManager.sharedManager(getContext()).chat().COLUMN_ROOM_NUM_UNCHECKED_CHAT )) > 0 ? false : true );
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
	/** @} */
	
	/**
	 * @name Click Listener
	 *  @{
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		// 추가 정보
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
		
		Room room = new Room(c, this.type);
		RoomFragment fragment = new RoomFragment(room);
		
		DBProcManager.sharedManager(getContext()).chat().updateLastReadTS(room.roomCode, UserInfo.getUserIdx(getContext()), System.currentTimeMillis());
		
		MainActivity.sharedActivity().pushContent(fragment);
	}
	/** @} */

	
	

}
