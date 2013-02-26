package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
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
	
	// Constructor
	public RoomListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public RoomListAdapter(Context context, Cursor c, boolean autoRequery, int type) {
		super(context, c, autoRequery);
		this.type = type;
	}

	public RoomListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	// Adapter Delegates Method
	@Override
	public Object getItem(int position) {
		return super.getItem(position);
	}
	
	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		ImageView userPicIV = (ImageView)v.findViewById(R.id.userPic);
		TextView departmentTV = (TextView)v.findViewById(R.id.department);
		TextView rankNameTV = (TextView)v.findViewById(R.id.rankName);
		TextView contentTV = (TextView)v.findViewById(R.id.content);
		TextView arrivalDTTV = (TextView)v.findViewById(R.id.arrivalDT);

		// About User Info
		long userIdx = c.getLong(c.getColumnIndex("sender"));
		User user = User.getUserWithIdx((int) userIdx);
		
		// rank and name
		String rank = User.RANK[user.rank];
		String name = user.name;
		String rankName = rank+" "+name;
		rankNameTV.setText(rankName);
		
		// department
		String department = user.levels[0] +" "+ user.levels[1] +" "+ user.levels[2] +" "+ user.levels[3] +" "+ user.levels[4] +" "+ user.levels[5];
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

}
