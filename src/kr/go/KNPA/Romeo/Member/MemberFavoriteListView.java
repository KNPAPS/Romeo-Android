package kr.go.KNPA.Romeo.Member;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.DBManager;

public class MemberFavoriteListView extends RomeoListView {

	MemberFavoriteListAdapter listAdatper;
	public MemberFavoriteListView(Context context) {
		this(context, null);
	}
	public MemberFavoriteListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public MemberFavoriteListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public RomeoListView initWithType(int type) {
		this.type = type;
			listAdapter = new MemberFavoriteListAdapter(getContext(), this.type, null, false);
			this.setAdapter(listAdapter);
			this.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) listAdapter);
		return this;
	}

	@Override
	public String getTableName() {
		return DBManager.TABLE_MEMBER_FAVORITE;
	}

	@Override
	protected Cursor query() {
		String sql = "SELECT * FROM "+getTableName()+" ORDER BY TS ASC;";
		Cursor c = db.rawQuery(sql, null);
		return c;
	}


}
