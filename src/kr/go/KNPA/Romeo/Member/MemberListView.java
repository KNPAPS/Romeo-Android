package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;


/**
 * 조직도를 나타내는 리스트 뷰이다. 
 * RomeoListView를 상속받는다.
 */
public class MemberListView extends RomeoListView {

	// Adapter Override
	public MemberListAdapter listAdapter;
	
	// Constructor
	public MemberListView(Context context) 										{	this(context, null);				}
	public MemberListView(Context context, AttributeSet attrs) 					{	this(context, attrs, 0);			}
	public MemberListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle);	}

	// Database management
	protected Cursor query() {	return DBProcManager.sharedManager(getContext()).member().getFavoriteList();	}
	
	// View management
	@Override
	public MemberListView initWithType (int type) {
		this.type = type;

		if(!(type == User.TYPE_FAVORITE || type==User.TYPE_MEMBERLIST || type==User.TYPE_FAVORITE_SEARCH || type==User.TYPE_MEMBERLIST_SEARCH)) return null;
	
		switch(this.type) {
			case User.TYPE_MEMBERLIST_SEARCH :
			case User.TYPE_MEMBERLIST :

				listAdapter = new MemberListAdapter(getContext(), type);
				this.setOnItemClickListener(listAdapter);
				this.setAdapter(listAdapter);
			
			break;		
		}
		return this;
	}

	
}

