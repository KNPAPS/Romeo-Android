package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
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

public class DocumentListView extends ListView implements OnItemClickListener{
	
	// Database
	private SQLiteDatabase db;
	private String tableName = null; 
	
	// Adapter
	public CursorAdapter listAdapter;
	
	// Variables
	public int type = Document.NOT_SPECIFIED;
	private Context context = null;
	
	
	// Constructor
	public DocumentListView(Context context) {
		this(context, null);
	}

	public DocumentListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DocumentListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
		this.context = context;
	}

	public void setType (int type) {
		this.type = type;

		tableName = DBManager.TABLE_DOCUMENT;
		if(this.type == Document.NOT_SPECIFIED) {
			tableName = null;
		}
	
		final Context ctx = this.context;
		DocumentListAdapter documentListAdapter = null;
		if(tableName != null) {
			Cursor c = selectAll();
			if(c.getCount() == 0) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				options.inPreferredConfig = Config.RGB_565;
				
				Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.empty_set_background, options);
				int height = src.getHeight();
				int width = src.getWidth();
				Bitmap resized = Bitmap.createScaledBitmap(src, width/options.inSampleSize, height/options.inSampleSize, true);
				this.setBackgroundDrawable(new BitmapDrawable(getResources(), resized));			} else {
				this.setBackgroundResource(R.color.light);
			}
			documentListAdapter = new DocumentListAdapter(context, c, false, this.type);
		}
		switch(this.type) {
		case Document.TYPE_DEPARTED :
		case Document.TYPE_RECEIVED :
			
			Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
				@Override
				public String getSectionTitleForItem(Cursor c) {
					boolean checked = (c.getLong(c.getColumnIndex("checked")) >0 ? true : false);
					return (checked ?  ctx.getString(R.string.checkedChat) : ctx.getString(R.string.unCheckedChat));
				}
			};
			
			SimpleSectionAdapter<Cursor> sectionAdapter
				= new SimpleSectionAdapter<Cursor>(context, documentListAdapter, R.layout.section_header, R.id.cell_title, sectionizer);
			this.setAdapter(sectionAdapter);
			this.setOnItemClickListener(this);
			listAdapter = documentListAdapter;
			break;
			
		case Document.TYPE_FAVORITE :
			this.setAdapter(documentListAdapter);
			this.setOnItemClickListener(this);
			listAdapter = documentListAdapter;
			break;
		default : break;
		
		}
		
	}
	
	// Database Management
	public void setDatabase(SQLiteDatabase db) {
		this.db = db;
	}
	
	public void unsetDatabase() {
		this.db = null;
	}
	
	protected Cursor selectAll() {
		String sql = null;
		switch(type) {
		case Document.TYPE_DEPARTED :
			sql = "SELECT * FROM "+this.tableName+" WHERE received="+0+" ORDER BY checked DESC;"; break;
		case Document.TYPE_FAVORITE :
			sql = "SELECT * FROM "+this.tableName+" WHERE favorite="+1+" ORDER BY TS DESC"; break;
		case Document.TYPE_RECEIVED :
			sql = "SELECT * FROM "+this.tableName+" WHERE received="+1+" ORDER BY checked DESC;"; break;
		}

		Cursor c = db.rawQuery(sql, null);
		
		return c;
	}
	
	public void refresh() {
		if(listAdapter == null) return;

 		listAdapter.changeCursor(selectAll());
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			((SimpleSectionAdapter)this.getAdapter()).notifyDataSetChanged();
	}
	
	// Click Listener
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
		Document document = new Document(c);
		
		Fragment fragment = new DocumentDetailFragment(document);// 추가 정보
		MainActivity a = MainActivity.sharedActivity();
		a.pushContent(fragment);
	}
}
