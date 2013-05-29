package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.ActivityLayout;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Util.ImageManager;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RoomInfoLayout extends ActivityLayout {

	public interface Listener extends BaseListener {
		public void onGoToRoomAliasSettingActivity();

		public void onToggleAlarmStatus();

		public void onGoToFullChatterList();

		public void onGoToInviteActivity();

		public void onLeaveRoom();
	}

	private Listener	mListener;
	private TextView	mRoomNameTV, mAlarmStatusTV;
	private LinearLayout	mRoomNameWrapper, mAlarmStatusWrapper;
	private Button			mFullChatterListBtn, mInviteChatterBtn, mLeaveRoomBtn;

	public RoomInfoLayout(Activity activity, int layoutResourceId)
	{
		super(activity, layoutResourceId);
		mRoomNameTV = (TextView) getActivity().findViewById(R.id.tv_room_name);
		mAlarmStatusTV = (TextView) getActivity().findViewById(R.id.tv_room_alarm_state);
		mFullChatterListBtn = (Button) getActivity().findViewById(R.id.btn_full_chatter_list);
		mInviteChatterBtn = (Button) getActivity().findViewById(R.id.btn_invite_chatter);
		mLeaveRoomBtn = (Button) getActivity().findViewById(R.id.btn_leave_room);

		mRoomNameWrapper = (LinearLayout) getActivity().findViewById(R.id.ll_room_name_wrapper);
		mAlarmStatusWrapper = (LinearLayout) getActivity().findViewById(R.id.ll_alarm_wrapper);

		mRoomNameWrapper.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onGoToRoomAliasSettingActivity();
				}
			}
		});

		mAlarmStatusWrapper.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onToggleAlarmStatus();
				}
			}
		});

		mFullChatterListBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onGoToFullChatterList();
				}
			}
		});
		mInviteChatterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onGoToInviteActivity();
				}
			}
		});

		mLeaveRoomBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onLeaveRoom();
				}
			}
		});
	}

	public void setListener(Listener l)
	{
		mListener = l;
		setBaseListener(l);
	}

	public void setRoomName(String title, String alias)
	{
		mRoomNameTV.setText(alias == null || alias.trim().equals("") ? title : alias);
		mRoomNameTV.requestLayout();
	}

	public void setAlarmStatusText(boolean isAlarmOn)
	{
		mAlarmStatusTV.setText(isAlarmOn == true ? "켬" : "끔");
		mAlarmStatusTV.requestLayout();
	}

	public void setChatterList(ArrayList<Chatter> chatters)
	{
		final int MAX_NUM_CHATTERS_IN_LIST = 3;
		if (chatters.size() <= MAX_NUM_CHATTERS_IN_LIST)
		{
			getActivity().findViewById(R.id.ll_full_chatter_list_btn_wrapper).setVisibility(View.GONE);
		}

		LinearLayout chatterListWrapper = (LinearLayout) getActivity().findViewById(R.id.ll_chatter_list_wrapper);

		LayoutInflater inflater = getActivity().getLayoutInflater();
		int n = Math.min(chatters.size(), MAX_NUM_CHATTERS_IN_LIST);
		for (int i = 0; i < n; i++)
		{
			LinearLayout chatterListCell = (LinearLayout) inflater.inflate(R.layout.user_list_cell, null);
			Chatter c = chatters.get(i);
			ImageView iv = (ImageView) chatterListCell.findViewById(R.id.user_pic);
			ImageManager im = new ImageManager();
			im.loadToImageView(ImageManager.PROFILE_SIZE_SMALL, c.idx, iv);

			((TextView) chatterListCell.findViewById(R.id.department)).setText(c.department.nameFull);
			((TextView) chatterListCell.findViewById(R.id.role)).setText("(" + c.role + ")");
			((TextView) chatterListCell.findViewById(R.id.rank)).setText(Constants.POLICE_RANK[c.rank]);
			((TextView) chatterListCell.findViewById(R.id.name)).setText(c.name);

			chatterListWrapper.addView(chatterListCell, 0);

		}

	}
}
