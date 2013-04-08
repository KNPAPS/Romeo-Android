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
		
		DBProcManager.sharedManager(context);
		
		// 센더해쉬
		String 	senderIdx	= c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_SENDER_HASH));
		// 채팅TS
		long 	arrivalTS	= c.getLong(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_TS));
		// 내용
		String 	content 	= c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_CONTENT));
		// 내용의 종류 Chat.CONTENT_TYPE_TEXT, Chat.CONTENT_TYPE_PICTURE
		int 	contentType	= c.getInt(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_CONTENT_TYPE));
		// 챗 해쉬값
		int		messageIdx	= c.getInt(c.getColumnIndex(ChatProcManager.))
		
		ImageView 	userPicIV		= (ImageView) 	v.findViewById(R.id.userPic);
		TextView 	departmentTV	= (TextView) 	v.findViewById(R.id.department);
		TextView 	rankNameTV		= (TextView) 	v.findViewById(R.id.rankName);
		TextView 	contentTV	= (TextView) 	v.findViewById(R.id.content);
		ImageView	contentIV	= (ImageView)	v.findViewById(R.id.contentImage);
		TextView 	arrivalDTTV		= (TextView) 	v.findViewById(R.id.arrivalDT);
		Button 		goUncheckedBT	= (Button) 		v.findViewById(R.id.goUnchecked);
		
		
		
		User sender = User.getUserWithIdx(senderIdx);
		
		departmentTV.setText( sender.department.nameFull );
		rankNameTV.setText( User.RANK[sender.rank] +" "+ sender.name );
		// TODO userPic		
		//
		//userPicIV.setImageBitmap(bm);
		
		String arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
		arrivalDTTV.setText(arrivalDT);
		
		if(contentType == Chat.CONTENT_TYPE_TEXT) {
			contentTV.setText(content);
			contentIV.setVisibility(View.GONE);
		} else if(contentType == Chat.CONTENT_TYPE_PICTURE) {
			// TODO image
			// contentIV.setImageBitmap(bm);
			contentTV.setVisibility(View.GONE);
		}
		
		Chat.getUncheckersIdxsWithMessageTypeAndIndex(this.type, )
		
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
