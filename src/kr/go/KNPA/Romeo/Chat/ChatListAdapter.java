package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.DB.ChatDAO;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatListAdapter extends CursorAdapter {

	public interface Listener {
		void onChatDelete(String chatIdx);

		void onChatTextCopy(String text);

		void onProfileImageClick(String userIdx);

		void onChatImageClick(String imageIdx);

		void onGoToUncheckersList(ArrayList<String> uncheckers);

		void onFailedChatReSend(String chatIdx);
	}

	private Listener					mListener;
	private static final String			TAG					= ChatListAdapter.class.getSimpleName();
	public HashMap<String, WaiterView>	mWaiterViews;
	int									mChatType			= Chat.NOT_SPECIFIED;
	public static final int				NUM_CHAT_PER_PAGE	= 20;
	public int							numTotalChat		= Constants.NOT_SPECIFIED;
	private Room						mRoom;
	private String						userIdx;
	private int							TEXT_VIEW_MAX_WIDTH;

	/**
	 * @param context
	 *            context
	 * @param c
	 *            초기에 리스트뷰에 출력할 커서
	 * @param chatType
	 *            채팅 리스트의 타입. @value{Chat.TYPE_MEETING},
	 * @value{Chat.TYPE_COMMAND
	 */
	public ChatListAdapter(Context context, Cursor c, Room room)
	{
		super(context, c, 0);
		if (room == null)
		{
			Log.e(TAG, "input room is null");
			throw new RuntimeException();
		}

		this.mRoom = room;
		this.mWaiterViews = new HashMap<String, WaiterView>();
		this.userIdx = UserInfo.getUserIdx(context);
		TEXT_VIEW_MAX_WIDTH = Math.min((int) Math.floor(Constants.DEVICE_WIDTH * 0.6), 500);
	}

	public void setListener(Listener l)
	{
		this.mListener = l;
	}

	public Cursor query(int nItems)
	{
		numTotalChat = DAO.chat(mContext).getNumTotalChat(mRoom.getCode());
		return DAO.chat(mContext).getChatList(mRoom.getCode(), 0, nItems);
	}

	/**
	 * 리스트뷰의 각 행을 만든다.
	 * 
	 * @param listItem
	 *            리스트뷰의 각 행을 이루는 ViewGroup
	 * @param context
	 *            context
	 * @param c
	 *            리스트뷰의 각 행에 해당하는 정보를 담고 있는 커서
	 */
	@Override
	public void bindView(final View listItem, final Context context, Cursor c)
	{

		/**
		 * 커서에서 정보 가져오기 {{{
		 */

		final String messageIdx = c.getString(c.getColumnIndex(ChatDAO.COLUMN_CHAT_IDX));
		// 채팅 해쉬로 태그 설정
		listItem.setTag(messageIdx);

		// 센더해쉬
		final String senderIdx = c.getString(c.getColumnIndex(ChatDAO.COLUMN_CHAT_SENDER_IDX));
		// 채팅TS
		long arrivalTS = c.getLong(c.getColumnIndex(ChatDAO.COLUMN_CHAT_TS));
		// 내용
		String content = c.getString(c.getColumnIndex(ChatDAO.COLUMN_CHAT_CONTENT));
		// 내용의 종류 Chat.CONTENT_TYPE_TEXT, Chat.CONTENT_TYPE_PICTURE
		int contentType = c.getInt(c.getColumnIndex(ChatDAO.COLUMN_CHAT_CONTENT_TYPE));

		/**
		 * }}}
		 */

		switch (contentType)
		{
		case Chat.CONTENT_TYPE_TEXT:
		case Chat.CONTENT_TYPE_PICTURE:
			// received인 경우 상대방 정보 설정
			if (!senderIdx.equals(userIdx))
			{
				User sender = MemberManager.sharedManager().getUser(senderIdx);

				ImageView userPicIV = (ImageView) listItem.findViewById(R.id.userPic);
				TextView departmentTV = (TextView) listItem.findViewById(R.id.department);
				TextView rankNameTV = (TextView) listItem.findViewById(R.id.room_list_cell_room_name);

				departmentTV.setText(sender.department.nameFull);
				rankNameTV.setText(User.RANK[sender.rank] + " " + sender.name);

				ImageManager im = new ImageManager();
				// 프로필 사진 load
				im.loadToImageView(ImageManager.PROFILE_SIZE_SMALL, senderIdx, userPicIV);

				userPicIV.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v)
					{
						if (mListener != null)
						{
							mListener.onProfileImageClick(senderIdx);
						}
					}
				});

			}

			TextView arrivalDTTV = (TextView) listItem.findViewById(R.id.tv_arrival_dt);
			String arrivalDT = Formatter.timeStampToRecentString(arrivalTS);
			arrivalDTTV.setText(arrivalDT);

			View contentView = listItem.findViewById(R.id.chat_content);
			contentView.setOnLongClickListener(new ChatContentLongClickListener());

			switch (contentType)
			{
			case Chat.CONTENT_TYPE_TEXT:
				((TextView) contentView).setMaxWidth(TEXT_VIEW_MAX_WIDTH);
				((TextView) contentView).setText(content);
				break;

			case Chat.CONTENT_TYPE_PICTURE:
				ImageManager im = new ImageManager();
				im.loadToImageView(ImageManager.CHAT_SIZE_SMALL, messageIdx, (ImageView) contentView);

				final String imageIdx = messageIdx;
				contentView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v)
					{
						if (mListener != null)
						{
							mListener.onChatImageClick(imageIdx);
						}
					}
				});
				break;

			default:
				break;
			}
			/** }}} */

			/**
			 * 채팅의 상태에 따라 WatierView, UnChecker 버튼, 재전송 버튼을 설정한다
			 */
			int chatStatus = c.getInt(c.getColumnIndex(ChatDAO.COLUMN_CHAT_STATE));
			final Button goUncheckedBT = (Button) listItem.findViewById(R.id.goUnchecked);

			switch (chatStatus)
			{
			case Chat.STATE_SENDING:

				if (goUncheckedBT.getVisibility() != View.GONE)
				{
					WaiterView wv = new WaiterView(context);
					wv.substituteView(goUncheckedBT);
					mWaiterViews.put(listItem.getTag().toString(), wv);
				}

				break;
			case Chat.STATE_SUCCESS:

				if (mWaiterViews.get(messageIdx) != null)
				{
					mWaiterViews.remove(messageIdx).restoreView();
				}

				if (mRoom.getType() == Room.TYPE_COMMAND && !userIdx.equals(senderIdx))
				{
					goUncheckedBT.setVisibility(View.INVISIBLE);
				}
				else
				{
					setUncheckerInfo(goUncheckedBT, arrivalTS);
				}

				break;
			case Chat.STATE_FAIL:

				goUncheckedBT.setBackgroundResource(R.drawable.chat_fail);
				goUncheckedBT.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v)
					{
						RomeoDialog.Builder chooseDlg = new RomeoDialog.Builder(mContext);
						chooseDlg.setTitle("작업선택");

						ArrayList<String> array = new ArrayList<String>();
						array.add("재전송");
						array.add("삭제");

						ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(mContext, R.layout.dialog_menu_cell2, array);

						chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								switch (which)
								{
								case 0:// 재전송
									if (mListener != null)
									{
										if (mListener != null)
										{
											mListener.onFailedChatReSend(listItem.getTag().toString());
										}
									}
									break;
								case 1:// 삭제
									if (mListener != null)
									{
										mListener.onChatDelete(listItem.getTag().toString());
									}
									break;
								}
								dialog.dismiss();
							}
						});
						chooseDlg.show();
					}
				});

				break;
			default:
				break;
			}

			break;// end of chat.text || chat.image

		case Chat.CONTENT_TYPE_USER_JOIN:
			User sender = MemberManager.sharedManager().getUser(senderIdx);
			String text = Constants.POLICE_RANK[sender.rank] + " " + sender.name + "님이 ";

			String[] userIdxs = content.split(":");

			for (int i = 0; i < userIdxs.length; i++)
			{
				User u = MemberManager.sharedManager().getUser(userIdxs[i]);
				text += Constants.POLICE_RANK[u.rank] + " " + u.name + "님,";
			}
			text = text.substring(0, text.length() - 1);
			text += "을 초대하였습니다";
			((TextView) listItem.findViewById(R.id.tv_chat_info)).setText(text);
			break;
		case Chat.CONTENT_TYPE_USER_LEAVE:

			User leftUser = MemberManager.sharedManager().getUser(senderIdx);

			String txt = Constants.POLICE_RANK[leftUser.rank] + " " + leftUser.name + "님이 나가셨습니다.";
			((TextView) listItem.findViewById(R.id.tv_chat_info)).setText(txt);
			break;
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
		String chatIdx = c.getString(c.getColumnIndex(ChatDAO.COLUMN_CHAT_IDX));

		View v = null;

		if (convertView != null && convertView.getTag() != null && chatIdx.equals(convertView.getTag()))
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
		LayoutInflater inflater = LayoutInflater.from(context);

		int rId = R.layout.chat_cell_received_text;

		int contentType = c.getInt(c.getColumnIndex(ChatDAO.COLUMN_CHAT_CONTENT_TYPE));

		switch (contentType)
		{
		case Chat.CONTENT_TYPE_USER_LEAVE:
		case Chat.CONTENT_TYPE_USER_JOIN:
			rId = R.layout.chat_info_cell;
			break;
		case Chat.CONTENT_TYPE_TEXT:
			if (userIdx.equals(c.getString(c.getColumnIndex(ChatDAO.COLUMN_CHAT_SENDER_IDX))))
			{
				rId = R.layout.chat_cell_departed_text;
			}
			else
			{
				rId = R.layout.chat_cell_received_text;
			}
			break;
		case Chat.CONTENT_TYPE_PICTURE:
			if (userIdx.equals(c.getString(c.getColumnIndex(ChatDAO.COLUMN_CHAT_SENDER_IDX))))
			{
				rId = R.layout.chat_cell_departed_image;
			}
			else
			{
				rId = R.layout.chat_cell_received_image;
			}
			break;
		default:
			Log.e(TAG, "content type is not valid");
			throw new RuntimeException();
		}

		View v = inflater.inflate(rId, parent, false);
		return v;
	}

	@Override
	public boolean isEnabled(int position)
	{
		return true;
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	public void setUncheckerInfo(final Button goUncheckedBT, final long arrivalTS)
	{

		int numUncheckers = 0;
		ArrayList<String> uncheckers = new ArrayList<String>();

		for (int i = 0; i < mRoom.chatters.size(); i++)
		{
			Chatter c = mRoom.chatters.get(i);
			if (c.lastReadTS < arrivalTS)
			{
				numUncheckers++;
				uncheckers.add(c.idx);
			}
		}

		final ArrayList<String> unchks = uncheckers;
		goUncheckedBT.setText(String.valueOf(numUncheckers));
		goUncheckedBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onGoToUncheckersList(unchks);
				}
			}
		});
	}

	class ChatContentLongClickListener implements OnLongClickListener {
		@Override
		public boolean onLongClick(final View view)
		{
			RomeoDialog.Builder chooseDlg = new RomeoDialog.Builder(mContext);
			chooseDlg.setTitle("작업선택");

			ArrayList<String> array = new ArrayList<String>();
			array.add("복사");
			array.add("삭제");

			ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(mContext, R.layout.dialog_menu_cell2, array);

			chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (mListener != null)
					{
						switch (which)
						{
						case 0:// 복사
							String text = ((TextView) view).getText().toString();
							mListener.onChatTextCopy(text);
							break;
						case 1:// 삭제
							String chatHash = (String) ((View) view.getParent()).getTag();
							mListener.onChatDelete(chatHash);
							break;
						}
						
						dialog.dismiss();
					}
					dialog.dismiss();
				}
			});

			chooseDlg.setCancelable(true);
			chooseDlg.show();
			return false;
		}
	}
}
