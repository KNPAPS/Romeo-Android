package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.IntroActivity;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;


/**
 * 조직도를 나타내는 리스트 뷰이다. 
 * RomeoListView를 상속받는다.
 */
public class MemberListView extends RomeoListView {

	// Adapter Override
	public MemberListAdapter listAdapter;
	
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

