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

	/**
	 * @name Constructors
	 * @{
	 */
	public RoomListAdapter(Context context, Cursor c)
	{
		super(context, c, 0);
	}

	/** @} */

	/**
	 * @name Adapter Delegates Method
	 * @{
	 */

	@Override
	public void bindView(View v, Context ctx, Cursor c)
	{

		// 룸코드
		String roomCode = c.getString(c.getColumnIndex(ChatProcManager.COLUMN_ROOM_IDX));
		v.setTag(roomCode);
		// 채팅방 제목
		String title = c.getString(c.getColumnIndex(ChatProcManager.COLUMN_ROOM_TITLE));
		// 채팅방 별칭
		String alias = c.getString(c.getColumnIndex(ChatProcManager.COLUMN_ROOM_ALIAS));
		// 채팅방에 있는 사람 수
		int nUsers = c.getInt(c.getColumnIndex(ChatProcManager.COLUMN_ROOM_NUM_CHATTER)) + 1;
		// 읽지 않은 채팅 수
		int nUnchecked = c.getInt(c.getColumnIndex(ChatProcManager.COLUMN_ROOM_NUM_NEW_CHAT));
		// 마지막 채팅이 도착한 시간 TS
		long arrivalTS = c.getLong(c.getColumnIndex(ChatProcManager.COLUMN_ROOM_LAST_CHAT_TS));
		// 마지막 채팅의 내용
		String lastContent = c.getString(c.getColumnIndex(ChatProcManager.COLUMN_ROOM_LAST_CHAT_CONTENT));
		// 1:1채팅의 경우 상대방 idx
		String chatterIdx = c.getString(c.getColumnIndex(ChatProcManager.COLUMN_USER_IDX));

		// 실제 출력될 채팅방 이름
		String roomTitle = null;

		if (alias == null || alias.trim().equals(""))
		{
			roomTitle = title;
		}
		else
		{
			roomTitle = alias;
		}

		ImageView userPicIV = (ImageView) v.findViewById(R.id.userPic);
		TextView departmentTV = (TextView) v.findViewById(R.id.department);
		TextView roomTitleTV = (TextView) v.findViewById(R.id.room_list_cell_room_name);
		TextView contentTV = (TextView) v.findViewById(R.id.content);
		TextView arrivalDTTV = (TextView) v.findViewById(R.id.arrivalDT);
		TextView numNewChat = (TextView) v.findViewById(R.id.numNewChat);
		TextView numChatters = (TextView) v.findViewById(R.id.numChatters);

		roomTitleTV.setText(roomTitle);

		if (nUsers > 1)
		{
			// 그룹 채팅일 경우
			departmentTV.setText("그룹 회의");
			numChatters.setText("(" + String.valueOf(nUsers) + " 명)");
		}
		else if (nUsers == 1)
		{
			// 1:1채팅일 경우
			numChatters.setVisibility(View.INVISIBLE);

			User user = MemberManager.sharedManager().getUser(chatterIdx);

			departmentTV.setText(user.department.nameFull);

			// 상대방 프로필 사진 출력
			ImageManager im = new ImageManager();
			im.loadToImageView(ImageManager.PROFILE_SIZE_SMALL, user.idx, userPicIV);
		}
		else
		{
			// 빈 방
			departmentTV.setText("");
		}

		// About Message
		// Content Summary
		final int CONTENT_SUMMARY_LENGTH = 10;
		String content = lastContent.substring(0, Math.min(CONTENT_SUMMARY_LENGTH, lastContent.length()));
		contentTV.setText(content);

		String arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
		arrivalDTTV.setText(arrivalDT);

		if (nUnchecked > 0)
		{
			numNewChat.setText(String.valueOf(nUnchecked));
		}
		else
		{
			numNewChat.setVisibility(View.GONE);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (!mDataValid)
		{
			throw new IllegalStateException("this should only be called when the cursor is valid");
		}

		if (!mCursor.moveToPosition(position))
		{
			throw new IllegalStateException("couldn't move cursor to position " + position);
		}

		Cursor c = (Cursor) getItem(position);
		String roomIdx = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_IDX));

		View v = null;

		if (convertView != null && convertView.getTag() != null && roomIdx.equalsIgnoreCase(convertView.getTag().toString()))
		{
			v = convertView;
		}
		else
		{
			v = newView(mContext, mCursor, parent);
		}

		bindView(v, mContext, mCursor);
		return v;
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent)
	{
		return LayoutInflater.from(context).inflate(R.layout.chat_room_list_cell, parent, false);
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}

	public Cursor query(int roomType)
	{
		return DBProcManager.sharedManager(mContext).chat().getRoomList(roomType);
	}
}
