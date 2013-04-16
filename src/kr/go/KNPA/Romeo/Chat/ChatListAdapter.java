package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ChatListAdapter extends CursorAdapter {

	public static HashMap<String,WaiterView> waiterViews;
	
	int chatType = Chat.NOT_SPECIFIED;
	
	/**
	 * @param context context
	 * @param c 커서
	 * @param chatType 채팅 리스트의 타입. @value{Chat.TYPE_MEETING}, @value{Chat.TYPE_COMMAND}
	 */
	public ChatListAdapter(Context context, Cursor c, int chatType) {
		super(context, c, 0);
		this.chatType = chatType;
		ChatListAdapter.waiterViews = new HashMap<String, WaiterView>();
	}

	@Override
	public void bindView(final View v, final Context context, Cursor c) {
		
		final String	messageIdx	= c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_IDX));
		//챗 해쉬로 태그 설정
		v.setTag(messageIdx);
		
		// 센더해쉬
		String 	senderIdx	= c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_SENDER_IDX));
		// 채팅TS
		long 	arrivalTS	= c.getLong(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_TS));
		// 내용
		String 	content 	= c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_CONTENT));
		// 내용의 종류 Chat.CONTENT_TYPE_TEXT, Chat.CONTENT_TYPE_PICTURE
		int 	contentType	= c.getInt(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_CONTENT_TYPE));
		
		ImageView 	userPicIV		= (ImageView) 	v.findViewById(R.id.userPic);
		TextView 	departmentTV	= (TextView) 	v.findViewById(R.id.department);
		TextView 	rankNameTV		= (TextView) 	v.findViewById(R.id.rankName);
		TextView 	contentTV	= (TextView) 	v.findViewById(R.id.content);
		ImageView	contentIV	= (ImageView)	v.findViewById(R.id.contentImage);
		TextView 	arrivalDTTV		= (TextView) 	v.findViewById(R.id.arrivalDT);
		
		final Button goUncheckedBT = (Button) v.findViewById(R.id.goUnchecked);
		
		User sender = User.getUserWithIdx(senderIdx);
		ImageManager im = new ImageManager();
		im.loadToImageView(ImageManager.PROFILE_SIZE_SMALL, senderIdx, userPicIV);
		
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
		
		int chatStatus = c.getInt(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_STATE));

		switch(chatStatus){
		case Chat.STATE_SENDING:
			WaiterView wv = new WaiterView(context);
			wv.substituteView(goUncheckedBT);
			DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

			LayoutParams params = new LayoutParams( (int)((26 * displayMetrics.density) + 0.5), (int)((26 * displayMetrics.density) + 0.5));
			params.gravity = Gravity.BOTTOM;
			params.bottomMargin = (int)((18 * displayMetrics.density) + 0.5);
			wv.setLayoutParams(params);
			waiterViews.put(v.getTag().toString(),wv);
			break;
		case Chat.STATE_SUCCESS:

			final Handler handler = new Handler(){
				@SuppressWarnings("unchecked")
				@Override
				public void handleMessage(Message msg) {
					final ArrayList<String> uncheckersIdxs = (ArrayList<String>) msg.obj;
					
					goUncheckedBT.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(context, UserListActivity.class);
							
							Bundle b = new Bundle();
							b.putStringArrayList(UserListActivity.KEY_USERS_IDX, uncheckersIdxs);
							intent.putExtras(b);
							
							context.startActivity(intent);
							
						}
					});
					goUncheckedBT.setText( String.valueOf(uncheckersIdxs.size()) );
					super.handleMessage(msg);
				}
			};
			
			new Thread(){
				@Override
				public void run() {
					if ( waiterViews.get(v.getTag().toString()) != null ) {
						waiterViews.get(v.getTag().toString()).restoreView();
					}
					
					Payload request = new Payload().setEvent(Event.Message.getUncheckers()).setData(new Data().add(0, KEY.MESSAGE.TYPE, chatType).add(0, KEY.MESSAGE.IDX, messageIdx));
					Connection conn = new Connection().requestPayload(request).async(false).request();
					Payload response = conn.getResponsePayload();
					
					ArrayList<String> uncheckers = new ArrayList<String>();
					if ( response.getStatusCode() == StatusCode.SUCCESS ){
						Data respData = response.getData();
						int nUncheckers = respData.size();
						for(int i=0; i<nUncheckers; i++) {
							uncheckers.add( (String)respData.get(i, KEY.USER.IDX) );
						}	
					}
					Message msg = handler.obtainMessage();
					msg.obj = uncheckers;
					handler.sendMessage(msg);
					
					super.run();
				}
			}.start();
			//TODO 확인안한사람 목록 가져오는거 백그라운드에서 실행하기
			
			break;
		case Chat.STATE_FAIL:
			break;
		default:
			break;
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (!mDataValid) {
	        throw new IllegalStateException("this should only be called when the cursor is valid");
	    }
	    if (!mCursor.moveToPosition(position)) {
	        throw new IllegalStateException("couldn't move cursor to position " + position);
	    }
//	    String userIdx = UserInfo.getUserIdx(mContext);
	    View v = newView(mContext,mCursor,parent);
//	    
//	    if ( convertView == null ) {
//	    	v = newView(mContext, mCursor, parent);
//	    } else {
//	    	if ( userIdx.equals(convertView.getTag()) && (Integer)convertView.getId() != R.layout.chat_bubble_departed ){
//	    		v = newView(mContext, mCursor, parent);
//	    	} else if ( !userIdx.equals(convertView.getTag()) && (Integer)convertView.getId() != R.layout.chat_bubble_received ) {
//	    		v = newView(mContext, mCursor, parent);
//	    	}
//	    }
	    
	    bindView(v, mContext, mCursor);
	    return v;
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
		return true;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	
}
