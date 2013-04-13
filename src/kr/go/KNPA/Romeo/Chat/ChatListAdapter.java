package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatListAdapter extends CursorAdapter {

	int chatType = Chat.NOT_SPECIFIED;
	
	/**
	 * @param context context
	 * @param c 커서
	 * @param chatType 채팅 리스트의 타입. @value{Chat.TYPE_MEETING}, @value{Chat.TYPE_COMMAND}
	 */
	public ChatListAdapter(Context context, Cursor c, int chatType) {
		super(context, c, 0);
		this.chatType = chatType;
	}

	@Override
	public void bindView(View v, final Context context, Cursor c) {
		
		// 센더해쉬
		String 	senderIdx	= c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_SENDER_IDX));
		// 채팅TS
		long 	arrivalTS	= c.getLong(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_TS));
		// 내용
		String 	content 	= c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_CONTENT));
		// 내용의 종류 Chat.CONTENT_TYPE_TEXT, Chat.CONTENT_TYPE_PICTURE
		int 	contentType	= c.getInt(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_CONTENT_TYPE));
		// 챗 해쉬값
		String	messageIdx	= c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_IDX));
		v.setTag(messageIdx);
		
		ImageView 	userPicIV		= (ImageView) 	v.findViewById(R.id.userPic);
		TextView 	departmentTV	= (TextView) 	v.findViewById(R.id.department);
		TextView 	rankNameTV		= (TextView) 	v.findViewById(R.id.rankName);
		TextView 	contentTV	= (TextView) 	v.findViewById(R.id.content);
		ImageView	contentIV	= (ImageView)	v.findViewById(R.id.contentImage);
		TextView 	arrivalDTTV		= (TextView) 	v.findViewById(R.id.arrivalDT);
		
		User sender = User.getUserWithIdx(senderIdx);
		
		departmentTV.setText( sender.department.nameFull );
		rankNameTV.setText( User.RANK[sender.rank] +" "+ sender.name );
		
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
		
//		final ArrayList<String> uncheckersIdxs = new ArrayList<String>();
//		//TODO 확인안한사람 목록 가져오는거 백그라운드에서 실행하기
//		//;Chat.getUncheckersIdxsWithMessageTypeAndIndex(this.chatType, messageIdx);
//		
//		goUncheckedBT.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(context, UserListActivity.class);
//				
//				Bundle b = new Bundle();
//				b.putStringArrayList(UserListActivity.KEY_USERS_IDX, uncheckersIdxs);
//				intent.putExtras(b);
//				
//				context.startActivity(intent);
//				
//			}
//		});
	}
	
	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		String userIdx = UserInfo.getUserIdx(context);

		// 유저 해시를 비교하여 자기가 전송한건지 받은건지 구별
		int rId = R.layout.chat_bubble_received;
		if ( userIdx.equals(c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_CHAT_SENDER_IDX))) ) {
			rId = R.layout.chat_bubble_departed;
		} else {
			rId = R.layout.chat_bubble_received;
		}
		
		View v = inflater.inflate(rId, parent, false);
		return v;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	
}
