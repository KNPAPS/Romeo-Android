package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.widget.ListView;

public class ChatListView extends ListView {
	private SimpleSectionAdapter<Cursor> sectionAdapter; 
	private DBManager dbManager;
	private SQLiteDatabase db;
	
	public ChatListView(Context context) {
		this(context, null);
	}

	public ChatListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ChatListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		final Context ctx = context;
		
		// DB Manager를 생성한다.
		dbManager = new DBManager(getContext());
		db = dbManager.getWritableDatabase();
		
		
		Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
			@Override
			public String getSectionTitleForItem(Cursor c) {
				long checkTS = c.getLong(c.getColumnIndex("checkTS"));
				return ((checkTS == Chat.NOT_SPECIFIED || checkTS < 1000) ?  ctx.getString(R.string.unCheckedChat) : ctx.getString(R.string.checkedChat));
			}
		};
		ChatListAdapter chatListAdapter = new ChatListAdapter(context, selectAll(), false);		
		
		sectionAdapter = new SimpleSectionAdapter<Cursor>(context, chatListAdapter, R.layout.section_header, R.id.cell_title, sectionizer);
		this.setAdapter(sectionAdapter);
	}

	protected Cursor selectAll() {
		String sql = "SELECT * FROM "+DBManager.TABLE_COMMAND+" ORDER BY checkTS desc;";
		// sectionizer 를 위해 정렬을 한다.
		Cursor c = db.rawQuery(sql, null);
		return c;
	}
	

	

}
