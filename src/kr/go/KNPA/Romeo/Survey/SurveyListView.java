package kr.go.KNPA.Romeo.Survey;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.SurveyProcManager;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

public class SurveyListView extends RomeoListView implements OnItemClickListener{
	
	// Constructor
	public SurveyListView(Context context)	 									{	this(context, null);				}
	public SurveyListView(Context context, AttributeSet attrs) 					{	this(context, attrs, 0);			}
	public SurveyListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle);	}

	// Initialize
	@Override
	public SurveyListView initWithType (int type) {
		this.subType = type;
		
		switch(this.subType) {
		case Survey.TYPE_DEPARTED :
			listAdapter = new SurveyListAdapter(getContext(), query(), false, this.subType);
			this.setAdapter(listAdapter);
			this.setOnItemClickListener(this);
			break;
			
		case Survey.TYPE_RECEIVED :
			Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
				@Override
				public String getSectionTitleForItem(Cursor c) {
					boolean checked= false;
					if ( c.moveToFirst() == true ) {
						checked = c.getLong(c.getColumnIndex(DBProcManager.SurveyProcManager.COLUMN_SURVEY_IS_CHECKED)) > 0 ? true : false;
						return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));
					}
					return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));
				}
			};
			listAdapter = new SurveyListAdapter(getContext(), null, false, this.subType);

			SimpleSectionAdapter<Cursor> sectionAdapter
				= new SimpleSectionAdapter<Cursor>(getContext(), listAdapter, R.layout.section_header, R.id.title, sectionizer);
			this.setAdapter(sectionAdapter);
			this.setOnItemClickListener(this);
			break;
		}
		
		
		return this;
	}
	
	// Database management
	@Override
	protected Cursor query() {	return DBProcManager.sharedManager(getContext()).survey().getSurveyList(this.subType);	}

	// Click Listener
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
		String surveyIdx = c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IDX));
		SurveyAnswerFragment f = new SurveyAnswerFragment(surveyIdx);
		MainActivity.sharedActivity().pushContent(f);
	}
	@Override
	public void onPreExecute() {
		//WaiterView.showDialog(getContext());
		
	}
	@Override
	public void onPostExecute(boolean isValidCursor) {
		//WaiterView.dismissDialog(getContext());
		
	}

}
