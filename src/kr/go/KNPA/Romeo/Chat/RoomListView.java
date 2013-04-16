package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

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
	protected Cursor query() {	return DBProcManager.sharedManager(getContext()).chat().getRoomList(this.subType);	}
	/** @} */
	
	/**
	 * @name initialize
	 * @{
	 */
	@Override
	public RoomListView initWithType (int subType) {
		this.subType = subType;
		
		Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
			@Override
			public String getSectionTitleForItem(Cursor c) {	// TODO
				boolean checked = (c.getInt(c.getColumnIndex( DBProcManager.sharedManager(getContext()).chat().COLUMN_ROOM_NUM_NEW_CHAT )) > 0 ? false : true );
				return (checked ? getContext().getString(R.string.checkedChat)  : getContext().getString(R.string.unCheckedChat));
			}
		};
		
		listAdapter = new RoomListAdapter(getContext(), null, false, this.subType);
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
		
		/*
		 * @b COLUMN_ROOM_HASH 채팅방 해시\n
		 * @b COLUMN_ROOM_TITLE 채팅방 제목\n
		 * @b COLUMN_ROOM_NUM_CHATTER 채팅방에 있는 사람 수\n
		 * @b COLUMN_ROOM_NUM_UNCHECKED_CHAT 읽지 않은 채팅 수\n
		 * @b COLUMN_ROOM_LAST_CHAT_TS 마지막 채팅이 도착한 시간 TS\n
		 * @b COLUMN_ROOM_LAST_CHAT_CONTENT 마지막 채팅의 내용\n
		 */
		Cursor c = (Cursor)adapter.getItem(position);
		
		String roomCode = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_IDX));
		Room room = new Room(getContext(), this.subType, roomCode);
		RoomFragment fragment = new RoomFragment(room);
		
		DBProcManager.sharedManager(getContext()).chat().updateLastReadTS(room.getRoomCode(), System.currentTimeMillis());
		
		MainActivity.sharedActivity().pushContent(fragment);
	}
	/** @} */
	@Override
	public void onPreExecute() {
	}
	@Override
	public void onPostExecute(boolean isValidCursor) {
	}

	
	

}
