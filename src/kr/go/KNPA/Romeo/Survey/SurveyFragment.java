package kr.go.KNPA.Romeo.Survey;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.RoomFragment;
import kr.go.KNPA.Romeo.Chat.RoomListView;
import kr.go.KNPA.Romeo.Document.DocumentListView;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SurveyFragment extends Fragment {

	// Managed Fragments
	public static SurveyFragment _departedFragment = null;
	public static SurveyFragment _receivedFragment = null;
	
	// Database
	private DBManager dbManager;
	private SQLiteDatabase db;
	
	// Variables
	public int type = Survey.NOT_SPECIFIED;
	
	// Constructor
	public SurveyFragment() {
		this(Survey.TYPE_RECEIVED);
	}

	public SurveyFragment(int type) {
		this.type = type;
	}

	public static SurveyFragment surveyFragment(int type) {
		SurveyFragment f = null;
		
		if(type == Survey.TYPE_DEPARTED) {
			if(_departedFragment == null) 
				_departedFragment = new SurveyFragment(type);
			f = _departedFragment;
		} else {
			if(_receivedFragment == null) 
				_receivedFragment = new SurveyFragment(type);
			f = _receivedFragment;
		}
		
		return f;
	}
	
	// Fragment Life-cycle Management
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		dbManager = new DBManager(getActivity());
		db = dbManager.getWritableDatabase();
		
		SurveyListView lv = getListView();
		lv.setDatabase(db);
		lv.refresh();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		SurveyListView lv = getListView();
		lv.unsetDatabase();
		db.close();
		db = null;
		dbManager.close();
		dbManager = null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(type == Survey.TYPE_DEPARTED) {
			_departedFragment = null;
		} else if(type==Survey.TYPE_RECEIVED) {
			_receivedFragment = null;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = setup(inflater, container, savedInstanceState); 
//		SurveyListView lv = ((SurveyListView)view.findViewById(R.id.surveyListView));
//		lv.setD
//				.refresh();
		return view; 
	}

	private View setup(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		
		
		String titleText = null;
		String lbbText = null, rbbText = null;
		boolean lbbIsVisible = false;
		boolean rbbIsVisible = false;
		
		switch(this.type) {
		case Survey.TYPE_RECEIVED :
			view = inflater.inflate(R.layout.survey_fragment, container, false);
			titleText = getString(R.string.surveyReceivedTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.dummy);
			lbbIsVisible = true;
			rbbIsVisible = false;
			break;
		case Survey.TYPE_DEPARTED :
			view = inflater.inflate(R.layout.survey_fragment, container, false);
			titleText = getString(R.string.surveyDepartedTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.compose);
			lbbIsVisible = true;
			rbbIsVisible = true;
			break;
		}
		
		if(view!=null) {
			SurveyListView slv = (SurveyListView)view.findViewById(R.id.surveyListView);
			slv.setType(this.type);
		}
		
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbIsVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbIsVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbText);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbText);	}
		
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText(titleText);
		
		if(lbb.getVisibility() == View.VISIBLE) {
			lbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MainActivity.sharedActivity().toggle();
				}
			});
		}
		
		if(rbb.getVisibility() == View.VISIBLE) {
			rbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), SurveyComposeActivity.class);
					//TODO
					startActivity(intent);
				}
			});
		}
		
		return view;
	}
	
	// Manage List View
	private SurveyListView getListView() {
		View view = ((ViewGroup)getView());
		SurveyListView lv = null;
		
		if(view!=null) {
			lv = (SurveyListView)view.findViewById(R.id.surveyListView);
		}
		
		return lv;
	}
	
	public static void receive(Survey survey) {
		if(_receivedFragment != null) {
			View view = _receivedFragment.getView();
			SurveyListView slv = null;
			if(view!=null) {
				ViewGroup layout = (ViewGroup) view.findViewById(R.id.rootLayout);
				slv = (SurveyListView)layout.findViewById(R.id.surveyListView);
			}
			final SurveyListView lv = slv;
			if(slv != null) {
				_receivedFragment.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						lv.refresh();	
					}
				});
			}
		}
	}
}
