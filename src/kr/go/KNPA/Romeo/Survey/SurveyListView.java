package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.DB.SurveyDAO;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Toast;

public class SurveyListView extends RomeoListView {
	
	private SurveyListAdapter mAdapter;
	private static Handler mHandler;
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
		mHandler = new Handler();
		mAdapter = new SurveyListAdapter(getContext(), subType, null);
		
		switch(this.subType) {
		case Survey.TYPE_DEPARTED :
			this.setAdapter(mAdapter);
			this.setOnItemClickListener(mAdapter);
			break;
			
		case Survey.TYPE_RECEIVED :
			Sectionizer<Survey> sectionizer = new Sectionizer<Survey>() {
				@Override
				public String getSectionTitleForItem(Survey c) {
					
					Long closeTS = (Long) c.form.get(KEY.SURVEY.CLOSE_TS);
					
					if (closeTS < System.currentTimeMillis()/1000)
					{
						return getContext().getString(R.string.survey_section_closed);
					}
					else
					{
						boolean checked= false;
						checked = c.checked;
						return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));	
					}
				}
			};

			SimpleSectionAdapter<Survey> sectionAdapter
				= new SimpleSectionAdapter<Survey>(getContext(), mAdapter, R.layout.section_header, R.id.title, sectionizer);
			this.setAdapter(sectionAdapter);
			this.setOnItemClickListener(mAdapter);
			break;
		}
		return this;
	}

	@Override
	public void refresh()
	{
		new SurveyRefreshThread().start();
	}

	/**
	 * 서버와 로컬 DB로부터 서베이 목록을 가져와 mSurvey에 저장한다.
	 */
	private final class SurveyRefreshThread extends Thread {
		@Override
		public void run()
		{
			super.run();
			mHandler.post(new Runnable() {
				@Override
				public void run()
				{
					WaiterView.showDialog(getContext());
					WaiterView.setTitle(getResources().getString(R.string.survey)+" 정보를 불러옵니다");
				}
			});
			
			Cursor c = DAO.survey(getContext()).getSurveyList(SurveyListView.this.subType);
			
			final ArrayList<Survey> surveys = new ArrayList<Survey>();
			
			ArrayList<String> surveyIdxs = new ArrayList<String>();
			
			while(c.moveToNext())
			{
				String idx = c.getString(c.getColumnIndex(SurveyDAO.COLUMN_SURVEY_IDX));
				surveyIdxs.add(idx);
				Survey s = new Survey();
				s.idx = idx;
				s.checked = c.getInt(c.getColumnIndex(SurveyDAO.COLUMN_SURVEY_IS_CHECKED))==1?true:false;
				
				s.isAnswered = c.getInt(c.getColumnIndex(SurveyDAO.COLUMN_SURVEY_IS_ANSWERED))==1?true:false;
				
				surveys.add(s);
			}
			
			Data reqData = new Data();
			reqData.add(0, KEY.SURVEY.IDX, surveyIdxs);
			Payload payload = new Payload();
			payload.setEvent(Event.MESSAGE_SURVEY_GET_CONTENT).setData(reqData);
			Payload response = new Connection().requestPayload(payload).async(false).request().getResponsePayload();
			if (response.getStatusCode() == StatusCode.SUCCESS)
			{
				Data data = response.getData();
				for (int i=0; i<data.size(); i++)
				{
					Survey surveyFromServer = (Survey) data.get(i).get(KEY._MESSAGE);
					Survey s = surveys.get(i);
					
					if (surveyFromServer != null)
					{
						s.setType(surveyFromServer.type());
						s.senderIdx = surveyFromServer.senderIdx;
						s.title = surveyFromServer.title;
						s.content = surveyFromServer.content;
						s.TS = surveyFromServer.TS;
						s.form = surveyFromServer.form;
						s.numUncheckers = surveyFromServer.numUncheckers;
					}
					
					surveys.set(i, s);
				}

				mHandler.post(new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run()
					{
						WaiterView.dismissDialog(getContext());
						mAdapter.setData(surveys);
						mAdapter.notifyDataSetChanged();
						
						if (getAdapter() instanceof SimpleSectionAdapter<?>)
						{
							((SimpleSectionAdapter<Survey>)getAdapter()).notifyDataSetChanged();
						}
						
						if (surveys.size()==0)
						{
							setBackground(getContext().getResources().getDrawable(R.drawable.empty_set_background));
						}
						
						requestLayout();
					}
				});
			}
			else
			{
				mHandler.post(new Runnable() {
					@Override
					public void run()
					{
						WaiterView.dismissDialog(getContext());
						Toast.makeText(getContext(), "목록을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
	
	@Override
	protected Cursor query()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPreExecute()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostExecute(boolean isValidCursor)
	{
		// TODO Auto-generated method stub
		
	}

}
