package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.ChatProcManager;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.ImageViewActivity;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ChatListAdapter extends CursorAdapter {

	public HashMap<String,WaiterView> waiterViews;
	private Room room;
	int chatType = Chat.NOT_SPECIFIED;
	
	/**
	 * @param context context
	 * @param c 커서
	 * @param chatType 채팅 리스트의 타입. @value{Chat.TYPE_MEETING}, @value{Chat.TYPE_COMMAND}
	 */
	public ChatListAdapter(Context context, Cursor c, int chatType) {
		super(context, c, 0);
		this.chatType = chatType;
		this.waiterViews = new HashMap<String, WaiterView>();
	}
	public ChatListAdapter setRoom(Room room) { 
		this.room = room; 
		return this; 
	}
	public Room getRoom(){ return this.room; } 
	/**
	 * 리스트뷰의 각 행을 만든다.
	 * @param listItem 리스트뷰의 각 행을 이루는 ViewGroup
	 * @param context context
	 * @param c 리스트뷰의 각 행에 해당하는 정보를 담고 있는 커서
	 */
	@Override
	public void bindView(final View listItem, final Context context, Cursor c) {
		
		/**
		 * 커서에서 정보 가져오기
		 * {{{
		 */
		
		final String messageIdx = c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_IDX));
		// 채팅 해쉬로 태그 설정
		listItem.setTag(messageIdx);
		
		// 센더해쉬
		String senderIdx = c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_SENDER_IDX));
		// 채팅TS
		long arrivalTS = c.getLong(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_TS));
		// 내용
		String content = c.getString(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_CONTENT));
		// 내용의 종류 Chat.CONTENT_TYPE_TEXT, Chat.CONTENT_TYPE_PICTURE
		int contentType = c.getInt(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_CONTENT_TYPE));
		
		/**
		 * }}}
		 */

		//유저 idx를 유저 객체로 변환
		User sender = MemberManager.sharedManager().getUser(senderIdx);
		
		if ( contentType == Chat.CONTENT_TYPE_USER_LEAVE ) {
			
			String text = Constants.POLICE_RANK[sender.rank]+" "+sender.name+"님이 나가셨습니다.";
			
			((TextView)listItem).setText(text);
			
		} else if ( contentType == Chat.CONTENT_TYPE_USER_JOIN ) {
			String text = Constants.POLICE_RANK[sender.rank]+" "+sender.name+"님이 ";
			
			String[] userIdxs = content.split(":");
			
			for(int i=0; i<userIdxs.length; i++) {
				User u = MemberManager.sharedManager().getUser(userIdxs[i]);
				text += Constants.POLICE_RANK[u.rank]+" "+u.name+"님,";
			}
			text = text.substring(0,text.length()-1);
			text += "을 초대하였습니다";
			((TextView)listItem).setText(text);
			
		} else {
		
			/**
			 * listItem 내 하위 View들 참조
			 * {{{
			 */
			ImageView 	userPicIV		= (ImageView) 	listItem.findViewById(R.id.userPic);
			TextView 	departmentTV	= (TextView) 	listItem.findViewById(R.id.department);
			TextView 	rankNameTV		= (TextView) 	listItem.findViewById(R.id.rankName);
			TextView 	arrivalDTTV		= (TextView) 	listItem.findViewById(R.id.arrivalDT);
			
			TextView 	contentTV		= (TextView) 	listItem.findViewById(R.id.content);
			ImageView	contentIV		= (ImageView)	listItem.findViewById(R.id.contentImage);
			
			final Button goUncheckedBT 	= (Button) 		listItem.findViewById(R.id.goUnchecked);
			/** }}} */
			
			/**
			 * 각 view에 적절한 정보를 삽입함.
			 * {{{
			 */
			
			//프로필 사진 load
			ImageManager im = new ImageManager();
			im.loadToImageView(ImageManager.PROFILE_SIZE_SMALL, senderIdx, userPicIV);
			
			//부서,계급, 채팅 도착 시간 text 삽입
			departmentTV.setText( sender.department.nameFull );
			rankNameTV.setText( User.RANK[sender.rank] +" "+ sender.name );
			String arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
			arrivalDTTV.setText(arrivalDT);
			
			//채팅의 content type에 따라 content 설정
			int chatStatus = c.getInt(c.getColumnIndex(ChatProcManager.COLUMN_CHAT_STATE));
			
			switch( contentType ) {
			case Chat.CONTENT_TYPE_TEXT:
				contentTV.setText(content);
				contentTV.setOnLongClickListener(new ChatMenu());
				contentIV.setVisibility(View.GONE);
				break;
			case Chat.CONTENT_TYPE_PICTURE:
				im.loadToImageView(ImageManager.CHAT_SIZE_SMALL, messageIdx, contentIV);
				contentIV.setOnLongClickListener(new ChatMenu());
				final String imageHash = messageIdx;
				contentIV.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Bundle b = new Bundle();
						b.putInt("imageType", ImageManager.CHAT_SIZE_ORIGINAL);
						b.putString("imageHash", imageHash);
						Intent intent = new Intent(mContext, ImageViewActivity.class);
						intent.putExtras(b);
						mContext.startActivity(intent);
						
					}
				});
				contentTV.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			/** }}} */
			
			/**
			 * 채팅의 상태에 따라 WatierView, UnChecker 버튼, 재전송 버튼을 설정한다
			 */
			switch(chatStatus){
			case Chat.STATE_SENDING:
				
				if ( goUncheckedBT.getVisibility() != View.GONE ) {
					WaiterView wv = new WaiterView(context);
					wv.substituteView(goUncheckedBT);
					DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
					LayoutParams params = new LayoutParams( (int)((26 * displayMetrics.density) + 0.5), (int)((26 * displayMetrics.density) + 0.5));
					params.gravity = Gravity.BOTTOM;
					params.bottomMargin = (int)((18 * displayMetrics.density) + 0.5);
					wv.setLayoutParams(params);
					waiterViews.put(listItem.getTag().toString(),wv);
				}
				break;
			case Chat.STATE_SUCCESS:
				
				if ( waiterViews.get(messageIdx) != null ) {
					waiterViews.remove(messageIdx).restoreView();
				}
				
				setUncheckerInfo(goUncheckedBT,arrivalTS);
				
				break;
			case Chat.STATE_FAIL:
				break;
			default:
				break;
			}

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
	    
		Cursor c = (Cursor)getItem(position);
		String chatIdx = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_CHAT_IDX));
		
		View v = null;
		
		if ( convertView != null && convertView.getTag() != null && chatIdx.equals(convertView.getTag())) {
		
			v = convertView;
			
		} else {
			
			v = newView(mContext,mCursor,parent);
			
		}

		bindView(v, mContext, mCursor);
		return v;
	}
	
	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		int rId = R.layout.chat_bubble_received;
		
		int contentType = c.getInt(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_CHAT_CONTENT_TYPE));
			
		if ( contentType == Chat.CONTENT_TYPE_USER_LEAVE || contentType == Chat.CONTENT_TYPE_USER_JOIN) {
			
			rId = R.layout.chat_info;
			
		} else {

			// 유저 해시를 비교하여 자기가 전송한건지 받은건지 구별
			String userIdx = UserInfo.getUserIdx(context);
			if ( userIdx.equals(c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_CHAT_SENDER_IDX))) ) {
				rId = R.layout.chat_bubble_departed;
			} else {
				rId = R.layout.chat_bubble_received;
			}
			
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
	
	public void setUncheckerInfo(final Button goUncheckedBT,final long arrivalTS) {
		HashMap<String,Long> lastReadTS = room.getLastReadTS();
		int numUncheckers= 0;
		ArrayList<String> uncheckers = new ArrayList<String>();
		
		Iterator<String> itr = lastReadTS.keySet().iterator();
		while (itr.hasNext()) {
		    String key = (String)itr.next();
		    if ( lastReadTS.get(key) < arrivalTS && !key.equals(UserInfo.getUserIdx(mContext))){
		    	numUncheckers++;
		    	uncheckers.add(key);
		    }
		}
		
		final ArrayList<String> unchks = uncheckers;
		goUncheckedBT.setText(String.valueOf(numUncheckers));
		goUncheckedBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.sharedActivity(),UserListActivity.class);
				intent.putExtra(UserListActivity.KEY_USERS_IDX, unchks);
				intent.putExtra(UserListActivity.KEY_TITLE, "미확인자 명단");
				mContext.startActivity(intent);
			}
		});
	}

	class ChatMenu implements OnLongClickListener{
		@Override
		public boolean onLongClick(final View view) {
		    AlertDialog.Builder chooseDlg = new AlertDialog.Builder(mContext);
		    chooseDlg.setTitle("작업선택");
		    
		    ArrayList<String> array = new ArrayList<String>();
		    array.add("복사");
		    array.add("삭제");
		    
		    ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(mContext, R.layout.dialog_menu_cell, array);
		    
		    chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener(){
		    	@Override
		    	public void onClick(DialogInterface dialog, int which) {
		    		switch(which){
		    		case 0://복사 TODO
//		    			ClipboardManager clipboardManager =  (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
//		    			ClipData clip = ClipData.newPlainText("txt",((TextView)view).getText() );
//		    			clipboardManager.setPrimaryClip(clip);
//		    			
		    			break;
		    		case 1://삭제
		    			new Thread(){
		    				public void run() {
		    					String chatHash = (String) ((View)view.getParent().getParent()).getTag();
		    					DBProcManager.sharedManager(mContext).chat().deleteChat(chatHash);
		    					final Cursor c = ChatFragment.getCurrentRoom().getListView().query();
		    					ChatFragment.getCurrentRoom().mHandler.post(new Runnable(){
		    						@Override
		    						public void run() {
		    							ChatFragment.getCurrentRoom().getListView().refresh(c);
		    						}
		    					});
		    				};
		    			}.start();
		    			
		    			break;
		    		}
		    	}
		    });
		    
		    chooseDlg.setCancelable(true);
		    chooseDlg.show();
			return false;
		}
	}
}
