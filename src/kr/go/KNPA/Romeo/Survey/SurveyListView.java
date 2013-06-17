package kr.go.KNPA.Romeo.Survey;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.DB.SurveyDAO;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;

public class SurveyListView extends RomeoListView {
	
	// Constructor
	public SurveyListView(Context context)
	{
		this(context, null);
	}

	public SurveyListView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public SurveyListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	// Initialize
	@Override
	public SurveyListView initWithType (int type) {
		this.subType = type;
		
		switch(this.subType) {
		case Survey.TYPE_DEPARTED :
			listAdapter = new SurveyListAdapter(getContext(), null, false, this.subType);
			this.setAdapter(listAdapter);
			this.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) listAdapter);
			break;
			
		case Survey.TYPE_RECEIVED :
			Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
				@Override
				public String getSectionTitleForItem(Cursor c) {
					boolean checked= false;
					if ( c.moveToFirst() == true ) {
						checked = c.getLong(c.getColumnIndex(SurveyDAO.COLUMN_SURVEY_IS_CHECKED)) > 0 ? true : false;
						return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));
					}
					return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));
				}
			};
			listAdapter = new SurveyListAdapter(getContext(), null, false, this.subType);

			SimpleSectionAdapter<Cursor> sectionAdapter
				= new SimpleSectionAdapter<Cursor>(getContext(), listAdapter, R.layout.section_header, R.id.title, sectionizer);
			this.setAdapter(sectionAdapter);
			this.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) listAdapter);
			break;
		}
		
		return this;
	}
	
	// Database management
	@Override
	protected Cursor query() {	return DAO.survey(getContext()).getSurveyList(this.subType);	}

	
	@Override
	public void onPreExecute() {
		//WaiterView.showDialog(getContext());
		
	}
	@Override
	public void onPostExecute(boolean isValidCursor) {
		//WaiterView.dismissDialog(getContext());
		
	}
	
	@Override
	public void refresh(Cursor c) {
		if(listAdapter == null) return;
		 
		if(listAdapter instanceof CursorAdapter) {
			fetch(c);
			
//			if(getAdapter() instanceof SimpleSectionAdapter && getAdapter() != listAdapter)
//				((SimpleSectionAdapter)getAdapter()).notifyDataSetChanged();
		} else {
			listAdapter.notifyDataSetChanged();
			
			if(getAdapter() instanceof SimpleSectionAdapter && getAdapter() != listAdapter)
				((SimpleSectionAdapter)getAdapter()).notifyDataSetChanged();
		}
		
	}
	
	private void fetch(final Cursor c) {
		final Handler handler = new Handler();
		WaiterView.showDialog(getContext());
		String surveyName = getResources().getString(R.string.survey);
		WaiterView.setTitle(surveyName + " 정보를 불러옵니다");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Cursor cSurvey = DAO.survey(getContext()).getSurveyList(SurveyListView.this.subType);
				
//				if(cSurvey.getCount() > 0) {
//					if(surveys == null ) {
//						surveys = new HashMap<String, Survey>();
//					} else {
//						surveys.clear();
//					}
//				}
//				
//				cSurvey.moveToFirst();
//				while ( !cSurvey.isAfterLast() ) {
//					String surveyIdx = cSurvey.getString(cSurvey.getColumnIndex(SurveyDAO.COLUMN_SURVEY_IDX));
//					Survey survey = new Survey(getContext(), surveyIdx); 
//					cSurvey.moveToNext();
//					
//					surveys.put(survey.idx, survey);
//					
//				}
				
				cSurvey.close();
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						afterLoad(c);
					}
				});
			}
		}).start();
		
		
		
	}
	
	private void afterLoad(Cursor c) {
		WaiterView.dismissDialog(getContext());
		setListBackground( c );
		if(c != null) {
			listAdapter.changeCursor(c);
		}
		
		if(getAdapter() instanceof SimpleSectionAdapter && getAdapter() != listAdapter)
			((SimpleSectionAdapter)getAdapter()).notifyDataSetChanged();
	}

}
