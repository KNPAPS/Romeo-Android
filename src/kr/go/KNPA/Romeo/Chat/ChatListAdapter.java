package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
class ChatListAdapter extends CursorAdapter {

	public ChatListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public ChatListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}
	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		ImageView userPic = (ImageView)v.findViewById(R.id.user_pic);
		TextView department = (TextView)v.findViewById(R.id.department);
		TextView rankName = (TextView)v.findViewById(R.id.rankName);
		TextView content = (TextView)v.findViewById(R.id.content);
		TextView arrivalDT = (TextView)v.findViewById(R.id.arrivalDT);
		
		
		long checkTS = c.getLong(c.getColumnIndex("checkTS"));
		String checked = ((checkTS == Chat.NOT_SPECIFIED || checkTS < 1000) ?  ctx.getString(R.string.unCheckedChat) : ctx.getString(R.string.checkedChat));
		rankName.setText(checked);
		content.setText(""+checkTS);
//		department.setText(c.getString(c.getColumnIndex("department")));
//		rankName.setText(c.getString(c.getColumnIndex("rank")) + " "
//						 + c.getString(c.getColumnIndex("name")) );
//		content.setText(c.getString(c.getColumnIndex("content")));
		arrivalDT.setText("½Ã°£");
	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = inflater.inflate(R.layout.room_list_cell, parent,false);
		return v;
	}

}
