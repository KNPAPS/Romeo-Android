package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import kr.go.KNPA.Romeo.search.MemberSearchActivity;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

public class SurveyListFragment extends RomeoFragment {

	private static final String		TAG			= "SurveyFragment";
	private static Handler			mHandler	= new Handler();

	public SurveyListFragment(int type)
	{
		super(type);
	}

	public SurveyListView getListView()
	{
		return (SurveyListView) listView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = null;

		OnClickListener lbbOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				MainActivity.sharedActivity().toggle();
			}
		};

		OnClickListener rbbOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), MemberSearchActivity.class);
				startActivityForResult(intent, MemberSearchActivity.REQUEST_CODE);
			}
		};

		switch (this.subType)
		{
		case Survey.TYPE_RECEIVED:
			view = inflater.inflate(R.layout.survey_fragment, container, false);
			initNavigationBar(view, R.string.surveyReceivedTitle, true, false, R.string.menu, R.string.compose, lbbOnClickListener, null);

			break;
		case Survey.TYPE_DEPARTED:
			view = inflater.inflate(R.layout.survey_fragment, container, false);
			initNavigationBar(view, R.string.surveyDepartedTitle, true, true, R.string.menu, R.string.compose, lbbOnClickListener, rbbOnClickListener);
			break;
		}

		listView = (SurveyListView) initListViewWithType(this.subType, R.id.surveyListView, view);

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
									.setMessage("설문을 삭제합니다.").setPositiveButton(kr.go.KNPA.Romeo.R.string.ok, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which)
										{
											dialog.dismiss();
											menus.dismiss();
											String surveyIdx = ((Survey)getListView().getItemAtPosition(position)).idx;
											new SurveyDeleteThread(surveyIdx).start();
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
	
	/**
	 * 새로 설문을 받았을 때 GCMMessageManager에 의해 일반 쓰레드에서 호출됨. 리스트뷰를 새로 고침한다.
	 * @param survey
	 */
	public void receive(Survey survey)
	{
		mHandler.post(new Runnable() {
			@Override
			public void run()
			{
				getListView().refresh();
			}
		});
	}
	
	/**
	 * 서베이를 삭제한다.
	 */
	private final class SurveyDeleteThread extends Thread {
		private final String	mIdx;
		
		/**
		 * @param surveyIdx 서베이 인덱스
		 * @param position 리스트뷰에서 해당 item의 position
		 */
		private SurveyDeleteThread(String surveyIdx)
		{
			mIdx = surveyIdx;
		
		}

		public void run()
		{
			if (mIdx == null)
			{
				Log.e(TAG, "서베이 리스트의 item의 tag가 surveyIdx이어야 하는데 null로 되어있어 삭제하지 못함");
				mHandler.post(new Runnable() {
					public void run()
					{
						Toast.makeText(getActivity(), "삭제에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
					}
				});

				return;
			}

			DAO.survey(getActivity()).deleteSurvey(mIdx);

			SurveyListFragment.mHandler.post(new Runnable() {
				public void run()
				{
					getListView().refresh();
				}
			});
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case MemberSearchActivity.REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK)
			{
				final ArrayList<String> receiversIdxs = data.getExtras().getStringArrayList(MemberSearchActivity.KEY_RESULT_IDXS);

				if (receiversIdxs.size() == 0)
				{
					return;
				}

				SurveyComposeFragment fragment = new SurveyComposeFragment(receiversIdxs);
				getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.addToBackStack(null)
					.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName())
					.commit();
			}
			break;
		}
	}
}
