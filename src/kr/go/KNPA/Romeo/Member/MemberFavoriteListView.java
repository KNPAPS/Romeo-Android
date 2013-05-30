package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;

public class MemberFavoriteListView extends RomeoListView {

	MemberFavoriteListAdapter	listAdatper;

	public MemberFavoriteListView(Context context)
	{
		this(context, null);
	}

	public MemberFavoriteListView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public MemberFavoriteListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public RomeoListView initWithType(int type)
	{
		this.subType = type;
		listAdapter = new MemberFavoriteListAdapter(getContext(), this.subType, null, false);
		this.setAdapter(listAdapter);
		this.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) listAdapter);
		refresh();
		return this;
	}

	@Override
	protected Cursor query()
	{
		return DBProcManager.sharedManager(getContext()).member().getFavoriteList();
	}

	@Override
	public void onPreExecute()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPostExecute(boolean isValidCursor)
	{
		// TODO Auto-generated method stub

	}
}
