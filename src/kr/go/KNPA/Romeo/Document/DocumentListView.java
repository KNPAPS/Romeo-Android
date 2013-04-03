package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;;

public class DocumentListView extends RomeoListView implements android.widget.AdapterView.OnItemClickListener{

	// Constructor
	public DocumentListView(Context context) {
		this(context, null);
	}

	public DocumentListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DocumentListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	// initializer
	@Override
	public DocumentListView initWithType(int type) {
		this.type = type;
		
		listAdapter = new DocumentListAdapter(getContext(), null, false, this.type);
		switch(this.type) {
		case Document.TYPE_RECEIVED :
			Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
				@Override
				public String getSectionTitleForItem(Cursor c) {
					boolean checked = (c.getLong(c.getColumnIndex("checked")) >0 ? true : false);
					return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));
				}
			};
			
			SimpleSectionAdapter<Cursor> sectionAdapter
				= new SimpleSectionAdapter<Cursor>(getContext(), listAdapter, R.layout.section_header, R.id.cell_title, sectionizer);
			this.setAdapter(sectionAdapter);
			this.setOnItemClickListener(this);
			break;
		case Document.TYPE_DEPARTED :
		case Document.TYPE_FAVORITE :
			this.setAdapter(listAdapter);
			this.setOnItemClickListener(this);
			break;
		default : break;
		
		}
		
		return this;
	}

	
	@Override
	protected Cursor query() {
		String sql = null;
		switch(type) {
		case Document.TYPE_DEPARTED :
			sql = "SELECT * FROM "+getTableName()+" WHERE received="+0+" ORDER BY TS DESC;"; break;
		case Document.TYPE_FAVORITE :
			sql = "SELECT * FROM "+getTableName()+" WHERE favorite="+1+" ORDER BY TS DESC"; break;
		case Document.TYPE_RECEIVED :
			sql = "SELECT * FROM "+getTableName()+" WHERE received="+1+" ORDER BY checked DESC;"; break;
		}

		Cursor c = db.rawQuery(sql, null);
		
		return c;
	}	

	// Click Listener
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
		Document document = new Document(c);
		
		DocumentDetailFragment fragment = new DocumentDetailFragment(document, type);// 추가 정보
		MainActivity a = MainActivity.sharedActivity();
		a.pushContent(fragment);
	}



	@Override
	public String getTableName() {
		if(this.type == Document.NOT_SPECIFIED)
			return null;
		return DBManager.TABLE_DOCUMENT;
	}
}
