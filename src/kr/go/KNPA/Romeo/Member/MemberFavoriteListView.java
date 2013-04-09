package kr.go.KNPA.Romeo.Member;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBManager;
import kr.go.KNPA.Romeo.DB.DBProcManager;

public class MemberFavoriteListView extends RomeoListView {

	MemberFavoriteListAdapter listAdatper;
	public MemberFavoriteListView(Context context) 										{	this(context, null);				}
	public MemberFavoriteListView(Context context, AttributeSet attrs) 					{	this(context, attrs, 0);			}
	public MemberFavoriteListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle);	}

	@Override
	public RomeoListView initWithType(int type) {
		this.type = type;
		listAdapter = new MemberFavoriteListAdapter(getContext(), this.type, null, false);
		this.setAdapter(listAdapter);
		this.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) listAdapter);
		return this;
	}
	@Override
	protected Cursor query() 	{	return DBProcManager.sharedManager(getContext()).member().getFavoriteList();	}
}
