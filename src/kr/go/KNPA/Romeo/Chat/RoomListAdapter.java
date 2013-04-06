package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
class RoomListAdapter extends CursorAdapter {
	public int type = Chat.NOT_SPECIFIED;
	
	/** 
	 * @name Constructors
	 * @{
	 */
	public RoomListAdapter(Context context, Cursor c, boolean autoRequery) 				{	super(context, c, autoRequery);	}
	public RoomListAdapter(Context context, Cursor c, boolean autoRequery, int type) 	{	super(context, c, autoRequery);	this.type = type;	}
	public RoomListAdapter(Context context, Cursor c, int flags) 						{	super(context, c, flags);	}
	/** @} */
	
	/**
	 * @name Adapter Delegates Method
	 * @{
	 */
	@Override
	public Object getItem(int position) {
		return super.getItem(position);
	}
	
	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		// TODO
		
		DBProcManager.sharedManager(ctx);
		
		// 채팅방 해시
		String roomIdx = c.getString( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_HASH ) );
		// 채팅방 제목
		String title = c.getString( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_TITLE ) );
		// 채팅방에 있는 사람 수
		int nUsers = c.getInt( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_NUM_CHATTER ) );
		// 읽지 않은 채팅 수
		int nUnchecked = c.getInt( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_NUM_UNCHECKED_CHAT ));
		// 마지막 채팅이 도착한 시간 TS
		long lastTS = c.getLong( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_LAST_CHAT_TS ) );
		// 마지막 채팅의 내용
		String lastContent = c.getString( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_LAST_CHAT_CONTENT ) );

		
		
		
		
		ImageView userPicIV = (ImageView)v.findViewById(R.id.userPic);
		TextView departmentTV = (TextView)v.findViewById(R.id.department);
		TextView rankNameTV = (TextView)v.findViewById(R.id.rankName);
		TextView contentTV = (TextView)v.findViewById(R.id.content);
		TextView arrivalDTTV = (TextView)v.findViewById(R.id.arrivalDT);

		// About User Info
		String userIdx = c.getString(c.getColumnIndex("sender"));
		User user = User.getUserWithIdx(userIdx);
		
		// rank and name
		String rank = User.RANK[user.rank];
		String name = user.name;
		String rankName = rank+" "+name;
		rankNameTV.setText(rankName);
		
		// department
		String department = user.department.nameFull;
		departmentTV.setText(department);
		
		
		// About Message
		// Content Summary
		final int CONTENT_SUMMARY_LENGTH = 10;
		String _content = c.getString(c.getColumnIndex("content"));
		String content = _content.substring(0, Math.min(CONTENT_SUMMARY_LENGTH, _content.length()));
		contentTV.setText(content);

		long arrivalTS = c.getLong(c.getColumnIndex("TS"));		
		String arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
		arrivalDTTV.setText(arrivalDT);
		
		// user Pic : TODO
		userPicIV.setImageResource(android.R.drawable.ic_popup_disk_full);
	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = inflater.inflate(R.layout.chat_room_list_cell, parent,false);
		return v;
	}
	/** @} */
}
