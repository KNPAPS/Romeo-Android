package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.Chat.RoomModel;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.DB.SurveyDAO;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemLongClickListener;

public class SurveyFragment extends RomeoFragment {

	// Managed Fragments
	private static SurveyFragment _departedFragment = null;
	private static SurveyFragment _receivedFragment = null;
	
	
	private static Handler handler = new Handler();
//	
//	static HashMap<String, Survey> departedSurveyArrayList = null;
//	static HashMap<String, Survey> receivedSurveyArrayList = null; 
//	
	// Constructor
	public SurveyFragment() {
		this(Survey.TYPE_RECEIVED);
	}

	public SurveyFragment(int type) {
		super(type);
	}

	public static SurveyFragment surveyFragment(int type) {
		SurveyFragment f = null;
		
		if(type == Survey.TYPE_DEPARTED) {
			if(_departedFragment == null) 
				_departedFragment = new SurveyFragment(type);
			f = _departedFragment;
		} else if( type == Survey.TYPE_RECEIVED){
			if(_receivedFragment == null) 
				_receivedFragment = new SurveyFragment(type);
			f = _receivedFragment;
		}
		
		return f;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	// Manage List View
	public SurveyListView getListView() {
		View view = ((ViewGroup)getView());
		SurveyListView lv = null;
		
		if(view!=null) {
			lv = (SurveyListView)view.findViewById(R.id.surveyListView);
		}
		
		return lv;
	}
	
	
	// Fragment Life-cycle Management
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(subType == Survey.TYPE_DEPARTED) {
			_departedFragment = null;
		} else if(subType==Survey.TYPE_RECEIVED) {
			_receivedFragment = null;
		}
	}
	
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;

		OnClickListener lbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		
		OnClickListener rbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SurveyComposeFragment f = new SurveyComposeFragment();
				MainActivity.sharedActivity().pushContent(f);
			}
		};
		
		
		switch(this.subType) {
		case Survey.TYPE_RECEIVED :
			view = inflater.inflate(R.layout.survey_fragment, container, false);
			initNavigationBar(
							view, 
							R.string.surveyReceivedTitle, 
							true, 
							false, 
							R.string.menu, 
							R.string.compose, 
							lbbOnClickListener, null);

			break;
		case Survey.TYPE_DEPARTED :
			view = inflater.inflate(R.layout.survey_fragment, container, false);
			initNavigationBar(
					view, 
					R.string.surveyDepartedTitle, 
					true, 
					true, 
					R.string.menu, 
					R.string.compose, 
					lbbOnClickListener, rbbOnClickListener);
			break;
		}
		
		listView = (SurveyListView)initListViewWithType(this.subType, R.id.surveyListView, view);
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id)
			{
				final RomeoDialog.Builder chooseDlg = new RomeoDialog.Builder(getActivity());
				chooseDlg.setTitle("옵션");

				ArrayList<String> array = new ArrayList<String>();
				array.add("나가기");

				ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(getActivity(), R.layout.dialog_menu_cell2, array);

				chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface menus, int which)
					{
						switch (which)
						{
						case 0:// 나가기
							new RomeoDialog.Builder(getActivity()).setIcon(getActivity().getResources().getDrawable(kr.go.KNPA.Romeo.R.drawable.icon_dialog)).setTitle("다On")// context.getString(kr.go.KNPA.Romeo.R.string.)
									.setMessage("설문을 삭제합니다.").setPositiveButton(kr.go.KNPA.Romeo.R.string.ok, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which)
										{
											dialog.dismiss();
											menus.dismiss();
											
											new Thread() {
												public void run()
												{
													Cursor cSurvey = (Cursor)listView.getAdapter().getItem(position);
													String surveyIdx = cSurvey.getString(cSurvey.getColumnIndex(SurveyDAO.COLUMN_SURVEY_IDX));
													Survey survey = ((SurveyListAdapter)listView.getAdapter()).getSurvey(cSurvey);
													
													// TODO : Survey DB에서 삭제.
													//DAO.survey(getActivity()).
													
													SurveyFragment.handler.post(new Runnable() {
														public void run()
														{
															listView.refresh();

														}
													});
	
												}
											}.start();
											
										}
									}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int whichButton)
										{
											dialog.dismiss();
											menus.dismiss();
										}
									}).show();
							break;
						}
					}
				});

				chooseDlg.setCancelable(true);
				chooseDlg.show();
				return true;
			}
		});
		
		return view;
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
						lv.refresh();	// SurveyListView
					}
				});
			}
		}
	}
}
