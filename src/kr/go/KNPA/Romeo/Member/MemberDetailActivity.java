package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.DB.MemberDAO;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.ImageViewActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberDetailActivity extends Activity {

	static final int			NOT_SPECIFIED	= -777;
	public static final String	KEY_IDX			= "idx";
	public static final String	KEY_IDX_TYPE	= "idx_type";
	public static final int		IDX_TYPE_USER	= 0;
	public static final int		IDX_TYPE_GROUP	= 1;
	private Handler				mHandler;
	private Button				background;
	private Button				close;
	private Button				favorite;
	private Button				goCommand;
	private Button				goMeeting;

	private String				idx;
	private int					idxType;

	public MemberDetailActivity()
	{
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Intent로부터 정보를 얻자
		Intent intent = getIntent();
		Bundle b = intent.getExtras();

		idx = b.getString(KEY_IDX);
		idxType = b.getInt(KEY_IDX_TYPE);

		// 전달된 idx값이 없으면 돌아간다.
		if (idx == null || idx.trim().length() < 1)
		{
			finish();
		}

		// 모양을 잡는다.
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		layoutParams.dimAmount = 0.7f;

		getWindow().setAttributes(layoutParams);
		View view = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.member_detail_activity, null);
		setContentView(view);

		int statusBarHeight = (int) Math.ceil(25 * this.getResources().getDisplayMetrics().density);
		ViewGroup vg = (ViewGroup) findViewById(R.id.memberDetailActivityLayout);
		ViewGroup.LayoutParams lp = vg.getLayoutParams();
		Point size = new Point();

		// Display.getHeight() & Display.getWidth() are deprecated in API
		// Level13,
		// instead, Display.getSize(Point) is added.
		if (Build.VERSION.SDK_INT >= 13)
		{
			getWindowManager().getDefaultDisplay().getSize(size);
		}
		else
		{
			size.y = getWindowManager().getDefaultDisplay().getHeight();
			size.x = getWindowManager().getDefaultDisplay().getWidth();
		}

		lp.width = size.x;
		lp.height = size.y - statusBarHeight;
		vg.setLayoutParams(lp);

		// 주어진 idx가 즐겨찾기 되어있는지 판단한다.
		MemberDAO mpm = DAO.member(MemberDetailActivity.this);
		boolean isFavorite = mpm.isUserFavorite(idx);

		favorite = (Button) findViewById(R.id.favorite);
		if (isFavorite)
		{
			favorite.setBackgroundResource(R.drawable.star_active);
		}
		else
		{
			favorite.setBackgroundResource(R.drawable.star_gray);
		}

		// 주어진 정보를 토대로 채워넣는다.
		TextView departmentTV = (TextView) findViewById(R.id.department);
		TextView rankTV = (TextView) findViewById(R.id.rank);
		TextView nameTV = (TextView) findViewById(R.id.name);

		Cursor cursor_favoriteInfo = mpm.getFavoriteInfo(idx);
		String title = null;
		if (cursor_favoriteInfo.moveToNext())
		{
			title = cursor_favoriteInfo.getString(cursor_favoriteInfo.getColumnIndex(MemberDAO.COLUMN_FAVORITE_NAME));
		}
		if (idxType == IDX_TYPE_USER)
		{
			// User 정보를 얻어온다.
			final User user = User.getUserWithIdx(idx);

			departmentTV.setText(user.department.nameFull);
			rankTV.setText(User.RANK[user.rank]);
			nameTV.setText(user.name);

			ImageView userPicIV = (ImageView) findViewById(R.id.user_pic);
			new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_MEDIUM, idx, userPicIV);
			userPicIV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(MemberDetailActivity.this, ImageViewActivity.class);
					intent.putExtra("imageHash", user.idx);
					intent.putExtra("imageType", ImageManager.PROFILE_SIZE_ORIGINAL);
					startActivity(intent);
				}
			});
		}
		else if (idxType == IDX_TYPE_GROUP)
		{

			if (title == null || title.trim().length() == 0)
			{
				title = "";
				Cursor cursor_favoriteUsers = mpm.getFavoriteGroupMemberList(idx);
				while (cursor_favoriteUsers.moveToNext())
				{
					User user = User.getUserWithIdx(cursor_favoriteUsers.getString(cursor_favoriteUsers.getColumnIndex(MemberDAO.COLUMN_USER_IDX)));
					title += user.rank + " " + user.name;
					if (title.length() > 20)
					{
						title = title.substring(0, 20) + "...";
						break;
					}
				}
			}
			departmentTV.setText("");
			rankTV.setText("");
			nameTV.setText(title);
			ImageView userPicIV = (ImageView) findViewById(R.id.user_pic);
			userPicIV.setImageResource(R.drawable.user_pic_default);
		}

		// Bind Click Events
		background = (Button) findViewById(R.id.backgroundButton);
		close = (Button) findViewById(R.id.close);

		goMeeting = (Button) findViewById(R.id.goMeeting);
		goCommand = (Button) findViewById(R.id.goCommand);
		goMeeting.setTag("meeting");
		goCommand.setTag("command");
		background.setOnClickListener(finish);
		close.setOnClickListener(finish);

		favorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				MemberDAO mpm = DAO.member(MemberDetailActivity.this);
				boolean isFavorite = mpm.isUserFavorite(idx);
				mpm.setFavorite(idx, !isFavorite);

				if (!isFavorite)
				{
					favorite.setBackgroundResource(R.drawable.star_active);
				}
				else
				{
					favorite.setBackgroundResource(R.drawable.star_gray);
				}
				favorite.requestLayout();
			}
		});

		goMeeting.setOnClickListener(goMessage);
		goCommand.setOnClickListener(goMessage);
	}

	private final OnClickListener	finish		= new OnClickListener() {
													@Override
													public void onClick(View v)
													{
														finish();
													}
												};

	private final OnClickListener	goMessage	= new OnClickListener() {

													@Override
													public void onClick(final View btn)
													{
														mHandler = new Handler();
														new Thread() {
															public void run()
															{
																super.run();
																final int roomType = btn.getTag().toString().equals("meeting") ? Chat.TYPE_MEETING : Chat.TYPE_COMMAND;

																String roomCode = DAO.chat(MemberDetailActivity.this).getPairRoomCode(roomType, idx);

																Room room = null;
																if (roomCode != null)
																{
																	room = new Room(roomCode);
																}
																else
																{
																	room = new Room();
																	room.setType(roomType);
																	ArrayList<String> idxs = new ArrayList<String>();
																	idxs.add(idx);
																	room.addChatters(idxs);
																}

																final Room fRoom = room;

																mHandler.post(new Runnable() {
																	public void run()
																	{
																		finish();
																		MainActivity.sharedActivity().goRoomFragment(roomType, fRoom);
																	};
																});

															};
														}.start();

													}
												};

}
