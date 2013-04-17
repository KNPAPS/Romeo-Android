package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.ImageManager;
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
		
		// 채팅방 해시
		String roomIdx = c.getString( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_IDX ) );
		// 채팅방 제목
		String title = c.getString( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_TITLE ) );
		// 채팅방에 있는 사람 수
		int nUsers = c.getInt( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_NUM_CHATTER ) );
		// 읽지 않은 채팅 수
		int nUnchecked = c.getInt( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_NUM_NEW_CHAT ));
		// 마지막 채팅이 도착한 시간 TS
		long arrivalTS = c.getLong( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_LAST_CHAT_TS ) );
		// 마지막 채팅의 내용
		String lastContent = c.getString( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_LAST_CHAT_CONTENT ) );
		// 1:1채팅의 경우 상대방 idx
		String chatterIdx = c.getString( c.getColumnIndex( ChatProcManager.COLUMN_ROOM_CHATTER_IDX) );
		
		ImageView userPicIV = (ImageView)v.findViewById(R.id.userPic);
		TextView departmentTV = (TextView)v.findViewById(R.id.department);
		TextView rankNameTV = (TextView)v.findViewById(R.id.rankName);
		TextView contentTV = (TextView)v.findViewById(R.id.content);
		TextView arrivalDTTV = (TextView)v.findViewById(R.id.arrivalDT);
		TextView numNewChat = (TextView)v.findViewById(R.id.numNewChat);
		
		rankNameTV.setText(title);
		
		if ( nUsers > 2 ) {
			departmentTV.setText("그룹 회의");
		} else if ( nUsers == 2 ) {
			User user = MemberManager.sharedManager().getUser( chatterIdx );
			
			departmentTV.setText(user.department.nameFull);
			
			//상대방 프로필 사진 출력
			ImageManager im = new ImageManager();
			im.loadToImageView(ImageManager.PROFILE_SIZE_SMALL, user.idx, userPicIV);
		} else {
			departmentTV.setText("");
		}

		// About Message
		// Content Summary
		final int CONTENT_SUMMARY_LENGTH = 10;
		String content = lastContent.substring(0, Math.min(CONTENT_SUMMARY_LENGTH, lastContent.length()));
		contentTV.setText(content);
		
		String arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
		arrivalDTTV.setText(arrivalDT);
		
		if ( nUnchecked > 0) {
			numNewChat.setText(String.valueOf(nUnchecked));
		} else {
			numNewChat.setVisibility(View.GONE);
		}
		v.setTag(roomIdx);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (!mDataValid) {
	        throw new IllegalStateException("this should only be called when the cursor is valid");
	    }
	    if (!mCursor.moveToPosition(position)) {
	        throw new IllegalStateException("couldn't move cursor to position " + position);
	    }
	    
		Cursor c = (Cursor)getItem(position);
		String roomIdx = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_IDX));
		
		View v = null;
		if ( convertView != null && convertView.getTag() != null && roomIdx.equals(convertView.getTag())) {
			v = convertView;
		} else {
			v = newView(mContext,mCursor,parent);
		}
		bindView(v, mContext, mCursor);
		return v;
	}
	
	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = inflater.inflate(R.layout.chat_room_list_cell, parent,false);
		return v;
	}
	/** @} */
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
}
