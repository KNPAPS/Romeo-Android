package kr.go.KNPA.Romeo.Survey;

import java.util.Date;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.SurveyProcManager;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.Toast;

public class SurveyListView extends RomeoListView {
	
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
			this.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) listAdapter);
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
			this.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) listAdapter);
			break;
		}
		
		
		return this;
	}
	
	// Database management
	@Override
	protected Cursor query() {	return DBProcManager.sharedManager(getContext()).survey().getSurveyList(this.subType);	}

	
	@Override
	public void onPreExecute() {
		//WaiterView.showDialog(getContext());
		
	}
	@Override
	public void onPostExecute(boolean isValidCursor) {
		//WaiterView.dismissDialog(getContext());
		
	}

}
