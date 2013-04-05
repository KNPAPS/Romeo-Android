package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatListAdapter extends CursorAdapter {

	int type = Chat.NOT_SPECIFIED;
	
	public ChatListAdapter(Context context, Cursor c, boolean autoRequery, int type) {
		super(context, c, autoRequery);
		this.type = type;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		ImageView 	userPicIV		= (ImageView) 	v.findViewById(R.id.userPic);
		TextView 	departmentTV	= (TextView) 	v.findViewById(R.id.department);
		TextView 	rankNameTV		= (TextView) 	v.findViewById(R.id.rankName);
		TextView 	contentTV		= (TextView) 	v.findViewById(R.id.content);
		TextView 	arrivalDTTV		= (TextView) 	v.findViewById(R.id.arrivalDT);
		Button 		goUncheckedBT	= (Button) 		v.findViewById(R.id.goUnchecked);
		
		//TODO userPic
		String senderIdx = c.getString(c.getColumnIndex("sender"));
		User sender = User.getUserWithIdx(senderIdx);
		
		String department = sender.department.nameFull;
		departmentTV.setText(department);
		
		String rank = User.RANK[sender.rank];
		String name = sender.name;
		rankNameTV.setText(rank+" "+name);
		
		String content = c.getString(c.getColumnIndex("content"));
		contentTV.setText(content);
		
		long arrivalTS = c.getLong(c.getColumnIndex("TS"));
		String arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
		arrivalDTTV.setText(arrivalDT);
		
		final Context ctx = context;
		goUncheckedBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
				Toast.makeText(ctx, "goUncheck", Toast.LENGTH_SHORT).show();
				
			}
		});
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		int rId = R.layout.chat_bubble_received;
		
		switch((c.getInt(c.getColumnIndex("received")))) {
			case 0 : rId = R.layout.chat_bubble_departed; break;
			case 1 : rId = R.layout.chat_bubble_received; break;
		}
		
		View v = inflater.inflate(rId, parent,false);
		return v;
	}

}
