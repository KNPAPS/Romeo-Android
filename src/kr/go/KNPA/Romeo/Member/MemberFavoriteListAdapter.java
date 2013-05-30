package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.MemberProcManager;
import kr.go.KNPA.Romeo.Util.ImageManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberFavoriteListAdapter extends CursorAdapter implements OnItemClickListener {

	private Context						context;
	public int							subType	= User.NOT_SPECIFIED;

	private ArrayList<String>			_collect;

	ArrayList<HashMap<String, String>>	search;

	public MemberFavoriteListAdapter(Context ctx, int subType, Cursor c, boolean autoRequery)
	{
		super(ctx, c, autoRequery);
		this.subType = subType;
		context = ctx;
		if (subType == User.TYPE_FAVORITE_SEARCH)
			search = new ArrayList<HashMap<String, String>>();
		_collect = new ArrayList<String>();
	}

	@Override
	public void bindView(View v, final Context ctx, Cursor c)
	{
		// 즐겨찾기 목록 가져옴
		MemberProcManager mpm = DBProcManager.sharedManager(ctx).member();
		// 즐겨찾기 해쉬(유저면 유저해쉬 그룹이면 즐찾그룹해쉬)
		final String idx = c.getString(c.getColumnIndex(MemberProcManager.COLUMN_FAVORITE_IDX));
		// 즐겨찾기 이름
		String title = c.getString(c.getColumnIndex(MemberProcManager.COLUMN_FAVORITE_NAME));
		// 그룹인지 아닌지
		final boolean isGroup = c.getInt(c.getColumnIndex(MemberProcManager.COLUMN_FAVORITE_IS_GROUP)) == 1 ? true : false;

		ImageView userPicIV = (ImageView) v.findViewById(R.id.userPic);
		TextView departmentTV = (TextView) v.findViewById(R.id.department);
		TextView rankTV = (TextView) v.findViewById(R.id.rank);
		TextView nameTV = (TextView) v.findViewById(R.id.name);
		TextView roleTV = (TextView) v.findViewById(R.id.role);
		if (isGroup == false)
		{
			ImageView subIndicator = (ImageView) v.findViewById(R.id.sub_indicator);
			subIndicator.setVisibility(View.GONE);

			User user = User.getUserWithIdx(idx);
			new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_MEDIUM, idx, userPicIV);
			if (title == null || title.length() < 1)
			{
				title = user.name;
			}
			departmentTV.setText(title);
			rankTV.setText(User.RANK[user.rank]);
			nameTV.setText(user.name);
			roleTV.setText(user.role);

		}
		else
		{
			// userPicIV.setImageResource(R.drawable.user_pic_default);
			new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, idx, userPicIV);
			if (title == null || title.length() < 1)
			{
				title = "";
				Cursor cursor_favMemberList = mpm.getFavoriteGroupMemberList(idx);
				cursor_favMemberList.moveToFirst();
				while (!cursor_favMemberList.isAfterLast())
				{
					User user = User.getUserWithIdx(cursor_favMemberList.getString(cursor_favMemberList.getColumnIndex(MemberProcManager.COLUMN_FAVORITE_IDX)));

					String _name = user.name;
					String _rank = User.RANK[user.rank];

					title += _rank + " " + _name;
					if (title.length() > 20)
					{
						title = title.substring(0, 20) + "...";
					}
				}

			}

			departmentTV.setText("");
			rankTV.setText("");
			nameTV.setText(title);
			roleTV.setText("");
		}

		if (isGroup && subType == User.TYPE_FAVORITE)
		{
			Button goDetail = (Button) v.findViewById(R.id.goDetail);
			goDetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view)
				{
					Intent intent = new Intent(ctx, UserListActivity.class);
					Bundle b = new Bundle();
					b.putString(UserListActivity.KEY_USERS_IDX, idx);
					intent.putExtras(b);
					ctx.startActivity(intent);
				}
			});
		}

		if (subType == User.TYPE_FAVORITE_SEARCH)
		{
			Button searchButton = (Button) v.findViewById(R.id.control);

			searchButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v)
				{

					if (_collect.contains(idx))
					{
						// 이미 가지고 있다. (체크된 상태)
						// 뺀다. 체크 해지.
						_collect.remove(idx);
						v.setBackgroundResource(R.drawable.circle_check_gray);
					}
					else
					{
						// 가지고 있지 않다. 체크되지 않은 상태.
						// 넣는다. 체크 등록.
						_collect.add(idx);
						v.setBackgroundResource(R.drawable.circle_check_active);
					}
				}
			});

		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position)
	{
		Intent intent = new Intent(context, MemberDetailActivity.class);
		Cursor c = (Cursor) getItem(position);

		Bundle b = new Bundle();
		String idx = c.getString(c.getColumnIndex(DBProcManager.MemberProcManager.COLUMN_FAVORITE_IDX));
		b.putString(MemberDetailActivity.KEY_IDX, idx);

		boolean isGroup = c.getInt(c.getColumnIndex(DBProcManager.MemberProcManager.COLUMN_FAVORITE_IS_GROUP)) > 0 ? true : false;
		b.putInt(MemberDetailActivity.KEY_IDX_TYPE, isGroup ? MemberDetailActivity.IDX_TYPE_GROUP : MemberDetailActivity.IDX_TYPE_USER);
		intent.putExtras(b);

		context.startActivity(intent);
	}

	public ArrayList<String> collect()
	{
		String s = null;
		String[] ss = null;

		ArrayList<String> indexes = new ArrayList<String>();

		for (int i = 0; i < _collect.size(); i++)
		{
			s = _collect.get(i);
			ss = s.split(":");
			for (int j = 0; j < ss.length; j++)
			{
				String idx = ss[j];
				if (!indexes.contains(idx))
				{
					indexes.add(idx);
				}
			}
		}

		return indexes;
	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent)
	{

		// 그룹인지 아닌지
		boolean isGroup = c.getInt(c.getColumnIndex(MemberProcManager.COLUMN_FAVORITE_IS_GROUP)) == 1 ? true : false;

		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = null;
		if (this.subType == User.TYPE_FAVORITE)
		{
			if (isGroup)
			{
				v = inflater.inflate(R.layout.member_favorite_group_cell, parent, false);
			}
			else
			{
				v = inflater.inflate(R.layout.member_favorite_user_cell, parent, false);
			}
		}
		else if (this.subType == User.TYPE_FAVORITE_SEARCH)
		{
			if (isGroup)
			{
				v = inflater.inflate(R.layout.member_favorite_group_cell_search, parent, false);
			}
			else
			{
				v = inflater.inflate(R.layout.member_favorite_user_cell_search, parent, false);
			}
		}
		return v;
	}

}
