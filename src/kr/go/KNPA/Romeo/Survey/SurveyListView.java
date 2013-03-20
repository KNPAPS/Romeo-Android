package kr.go.KNPA.Romeo.Survey;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SurveyListView extends RomeoListView implements OnItemClickListener{
	
	// Constructor
	public SurveyListView(Context context) {
		this(context, null);
	}

	public SurveyListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SurveyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Initialize
	@Override
	public SurveyListView initWithType (int type) {
		this.type = type;
		switch(this.type) {
		case Survey.TYPE_DEPARTED :
			listAdapter = new SurveyListAdapter(getContext(), null, false, this.type);
			this.setAdapter(listAdapter);
			this.setOnItemClickListener(this);
			break;
			
		case Survey.TYPE_RECEIVED :
			
			Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
				@Override
				public String getSectionTitleForItem(Cursor c) {
					boolean checked = (c.getLong(c.getColumnIndex("checked")) > 0 ? true : false);
					return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));
				}
			};
			listAdapter = new SurveyListAdapter(getContext(), null, false, this.type);

			SimpleSectionAdapter<Cursor> sectionAdapter
				= new SimpleSectionAdapter<Cursor>(getContext(), listAdapter, R.layout.section_header, R.id.cell_title, sectionizer);
			this.setAdapter(sectionAdapter);
			this.setOnItemClickListener(this);
			break;
		}
		
		
		return this;
	}
	
	// Database management
	@Override
	protected Cursor query() {
		String sql = "SELECT * FROM "+getTableName()+" ORDER BY checked desc;"; // sectionizer 를 위해 정렬을 한다.
		Cursor c = db.rawQuery(sql, null);
		return c;
	}
	
	@Override
	public String getTableName() {
		switch(this.type) {
			case Survey.TYPE_DEPARTED :
			case Survey.TYPE_RECEIVED : return DBManager.TABLE_SURVEY;
			
			default : 
			case Survey.NOT_SPECIFIED :	return null;
		}
	}

	// Click Listener
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
		Survey survey = new Survey(c);
			
		SurveyDetailFragment f = new SurveyDetailFragment(survey, type);	// 추가 정보
		MainActivity.sharedActivity().pushContent(f);
	}




}
