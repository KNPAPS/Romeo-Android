package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.DocumentProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.SurveyProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey.Form;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SurveyDetailFragment extends Fragment  {
	private String surveyIdx;
	
	private Survey survey;
	
	public int subType;
	
	public SurveyDetailFragment() {}
	public SurveyDetailFragment(String surveyIdx) {	this.surveyIdx = surveyIdx;}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Cursor cursor_surveyInfo = DBProcManager.sharedManager(getActivity()).survey().getSurveyInfo(survey.idx);
		
		this.subType = cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_TYPE));
		
		this.survey = Survey.surveyFromServer(getActivity(), surveyIdx, subType);
		/*
		this.survey = new Survey(
				surveyIdx, 
				Message.makeType(Message.MESSAGE_TYPE_SURVEY, subType),	// 서베이타입 
				cursor_surveyInfo.getString(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_NAME)),	// 설문제목 
				cursor_surveyInfo.getString(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CONTENT)), // 설문조사설명내용
				cursor_surveyInfo.getString(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_SENDER_IDX)), // 보낸사람 해쉬
				null, 
				(this.subType == Survey.TYPE_RECEIVED ? true : false ), 
				cursor_surveyInfo.getLong(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CREATED_TS)), // 설문조사 받은시간
				(cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_CHECKED)) > 0 ) ? true : false, // 확인 여부 
				cursor_surveyInfo.getLong(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_CHECKED_TS)), // 확인한시간 
				(cursor_surveyInfo.getInt(cursor_surveyInfo.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_ANSWERED)) > 0 ) ? true : false);
				*/
		
	}
	@Override
	public void onResume() {
		super.onResume();
		survey.setChecked(getActivity());
		SurveyFragment.surveyFragment(survey.subType()).getListView().refresh();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		
		
		
		View view = inflater.inflate(R.layout.survey_detail, null, false);

		initNavigationBar(
				view, 
				R.string.surveyTitle, 
				true, true, 
				R.string.menu, R.string.submit, lbbOnClickListener, rbbOnClickListener);	


		TextView titleTV = (TextView)view.findViewById(R.id.title);
		titleTV.setText(this.survey.title);

		TextView  arrivalDTTV = (TextView)view.findViewById(R.id.arrivalDT);
		String arrivalDT = Formatter.timeStampToStringInRegularFormat(this.survey.TS, getActivity());
		arrivalDTTV.setText(arrivalDT);

		TextView senderTV = (TextView)view.findViewById(R.id.sender);
		User user = User.getUserWithIdx(this.survey.senderIdx);
		String sender = user.department.nameFull + " " + User.RANK[user.rank] +" "  + user.name;
		senderTV.setText(sender);

		TextView openDTTV = (TextView)view.findViewById(R.id.openDT);
		String openDT = Formatter.timeStampToStringWithFormat((Long)this.survey.form.get(Form.CLOSE_TS), getString(R.string.formatString_openDT));
		openDTTV.setText(openDT);

		TextView closeDTTV = (TextView)view.findViewById(R.id.closeDT);
		String closeDT = Formatter.timeStampToStringWithFormat((Long)this.survey.form.get(Form.CLOSE_TS), getString(R.string.formatString_closeDT));
		closeDTTV.setText(closeDT);

		TextView contentTV = (TextView)view.findViewById(R.id.content);
		String content = this.survey.content;
		contentTV.setText(content);

		// TODO 응답한 내용
		
		return view; 
	}

	final OnClickListener lbbOnClickListener =new OnClickListener() {

		@Override
		public void onClick(View v) {
			MainActivity.sharedActivity().toggle();
		}
	};
	final OnClickListener rbbOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			submit();
		}
	};

	private void submit() {
		// TODO
		/*
		String qJson = qm.toJSON();
		String json = "{\"idx\":"+survey.idx+",\"answersheet\":"+qJson+",\"userIdx\":"+UserInfo.getUserIdx(context)+"}";
		boolean result = survey.sendAnswerSheet(json, getActivity());

		SurveyFragment.surveyFragment(Survey.TYPE_RECEIVED).listView.refresh();
		if(result == true) MainActivity.sharedActivity().popContent(this);
*/
	}

	protected void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {

		Button lbb = (Button)parentView.findViewById(R.id.left_bar_button);
		Button rbb = (Button)parentView.findViewById(R.id.right_bar_button);

		lbb.setVisibility((lbbVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbVisible?View.VISIBLE:View.INVISIBLE));

		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbTitle);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbTitle);	}

		TextView titleView = (TextView)parentView.findViewById(R.id.title);
		titleView.setText(titleText);

		if(lbb.getVisibility() == View.VISIBLE) lbb.setOnClickListener(lbbOnClickListener);
		if(rbb.getVisibility() == View.VISIBLE) rbb.setOnClickListener(rbbOnClickListener);
	}

	protected void initNavigationBar(View parentView, int titleTextId, boolean lbbVisible, boolean rbbVisible, int lbbTitleId, int rbbTitleId, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		initNavigationBar(parentView, getString(titleTextId), lbbVisible, rbbVisible, getString(lbbTitleId), getString(rbbTitleId), lbbOnClickListener, rbbOnClickListener);
	}

}