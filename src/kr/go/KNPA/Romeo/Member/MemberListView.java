package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.IntroActivity;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.DBManager;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;


public class MemberListView extends RomeoListView {

	// Adapter Override
	public MemberListAdapter listAdapter;
	
	// Variables
	public static Department rootDepartment = null;
	
	// Constructor
	public MemberListView(Context context) {
		this(context, null);
	}

	public MemberListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MemberListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	// Database management

	protected Cursor query() {
		String sql = "SELECT * FROM "+getTableName()+";"; // sectionizer 를 위해 정렬을 한다.
		
		Cursor c = db.rawQuery(sql, null);
		return c;
	}
	
	@Override
	public String getTableName() {
		return null;
	}
	
	// View management
	@Override
	public MemberListView initWithType (int type) {
		this.type = type;

		if(!(type == MemberManager.TYPE_FAVORITE || type==MemberManager.TYPE_MEMBERLIST || type==MemberManager.TYPE_FAVORITE_SEARCH || type==MemberManager.TYPE_MEMBERLIST_SEARCH)) return null;
	
		switch(this.type) {
			case MemberManager.TYPE_MEMBERLIST_SEARCH :
			case MemberManager.TYPE_MEMBERLIST :
				// TODO
				if(rootDepartment == null) {
					try {
						MemberManager_old.sharedManager().getMembers(getContext());
					} catch(RuntimeException e) {
						throw e;
					}
					rootDepartment = Department.root();
			}
			
			listAdapter = new MemberListAdapter(getContext(), type, rootDepartment);
			this.setOnItemClickListener(listAdapter);
			this.setAdapter(listAdapter);
			
			break;		
		}
		
		// introView
		IntroActivity.sharedActivity().removeIntroView((ViewGroup) getParent());
		
		return this;
	}

	
}

