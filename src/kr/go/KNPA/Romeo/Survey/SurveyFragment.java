package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

public class SurveyFragment extends RomeoFragment {
	
	private static final String	TAG = "SurveyFragment";
	private static Handler mHandler = new Handler();
	public HashMap<String, Survey> surveys;

	public SurveyFragment(int type)
	{
		super(type);
	}

	public SurveyListView getListView()
	{
		return (SurveyListView) listView;
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
				array.add("설문 삭제");

				ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(getActivity(), R.layout.dialog_menu_cell2, array);

				chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface menus, int which)
					{
						switch (which)
						{
						case 0:// 나가기
							new RomeoDialog.Builder(getActivity()).setIcon(getActivity().getResources().getDrawable(kr.go.KNPA.Romeo.R.drawable.icon_dialog)).setTitle("다On")// context.getString(kr.go.KNPA.Romeo.R.string.)
									.setMessage("설문을 삭제합니다.")
									.setPositiveButton(kr.go.KNPA.Romeo.R.string.ok, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which)
										{
											dialog.dismiss();
											menus.dismiss();
											
											new Thread() {
												

												public void run()
												{
													String surveyIdx = view.getTag().toString();
													
													if (surveyIdx == null)
													{
														Log.e(TAG , "서베이 리스트의 item의 tag가 surveyIdx이어야 하는데 null로 되어있어 삭제하지 못함");
														return;
													}
													
													// TODO : Survey DB에서 삭제.
													//DAO.survey(getActivity()).
													
													SurveyFragment.mHandler.post(new Runnable() {
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
//		if(_receivedFragment != null) {
//			View view = _receivedFragment.getView();
//			SurveyListView slv = null;
//			if(view!=null) {
//				ViewGroup layout = (ViewGroup) view.findViewById(R.id.rootLayout);
//				slv = (SurveyListView)layout.findViewById(R.id.surveyListView);
//			}
//			final SurveyListView lv = slv;
//			if(slv != null) {
//				_receivedFragment.getActivity().runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						lv.refresh();	// SurveyListView
//					}
//				});
//			}
//		}
	}
}
