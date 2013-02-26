package kr.go.KNPA.Romeo.Survey;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SurveyDetailFragment extends Fragment {
	private Context context;
	private Survey survey;
	
	public SurveyDetailFragment() {
		
	}
	public SurveyDetailFragment(Survey survey) {
		super();
		this.survey = survey;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//super.onCreateView(inflater, container, savedInstanceState);
		//Intent intent = getIntent();
		//Bundle b = intent.getExtras();
		//this.survey = b.getParcelable("survey");
		//this.context = SurveyDetailFragment.this;

		
		this.context = getActivity();
		String navBarTitle = getString(R.string.surveyTitle);
		 
		View view = inflater.inflate(R.layout.survey_detail, null, false);
		
		ViewGroup navBar = (ViewGroup)view.findViewById(R.id.navigationBar);
		TextView navBarTitleView = (TextView)navBar.findViewById(R.id.title);
		navBarTitleView.setText(navBarTitle);
		
		Button lbb = (Button)navBar.findViewById(R.id.left_bar_button);
		lbb.setText(R.string.menu);
		
		Button rbb = (Button)navBar.findViewById(R.id.right_bar_button);
		rbb.setText(R.string.result);
		
		
		TextView titleTV = (TextView)view.findViewById(R.id.title);
		titleTV.setText(this.survey.title);
		
		TextView  arrivalDTTV = (TextView)view.findViewById(R.id.arrivalDT);
		String arrivalDT = Formatter.timeStampToStringInRegularFormat(this.survey.TS, getActivity());
		arrivalDTTV.setText(arrivalDT);
		
		TextView senderTV = (TextView)view.findViewById(R.id.sender);
		User user = this.survey.sender;
		String sender = user.getDepartmentFull() + " " + User.RANK[user.rank] +" "  + user.name;
		senderTV.setText(sender);
		
		TextView openDTTV = (TextView)view.findViewById(R.id.openDT);
		String openDT = Formatter.timeStampToStringInRegularFormat(this.survey.openTS, context);
		openDTTV.setText(openDT);
		
		TextView closeDTTV = (TextView)view.findViewById(R.id.closeDT);
		String closeDT = Formatter.timeStampToStringInRegularFormat(this.survey.closeTS, context);
		closeDTTV.setText(closeDT);
		
		TextView contentTV = (TextView)view.findViewById(R.id.content);
		String content = this.survey.content;
		contentTV.setText(content);
		
		
		LinearLayout questionsLL = (LinearLayout)view.findViewById(R.id.questions);
		
		/*
		for(int q=0; q<   ; q++) {
			View questionView = inflater.inflate(R.layout.survey_question_detail, questionsLL, false);
			questionItem
			
			TextView indexTV = (TextView)questionView.findViewById(R.id.index);
			indexTV.setText(""+(q+1));
			TextView titleTV = (TextView)questionView.findViewById(R.id.title);
			titleTV.setText(questionItem.title);
			
			LinearLayout optionsLL = (LinearLayout)questionView.findViewById(R.id.options);
			
			for(int o=0; o< qustionItem.options.   ; o++ ) {
				optionItem
				View optionView = inflater.inflate(R.layout.survey_options_result, questionView, false);
				
				ImageView indexIV = (ImageView)optionView.findViewById(R.id.index);
				int checkBoxResourceId = (optionItem.selected ? R.drawable.circle_check_active : R.drawable.circle_check_gray);
				indexIV.setBackgroundResource(checkBoxResourceId);
				
				TextView titleTV = (TextView)optionView.findViewById(R.id.title);
				titleTV.setText(optionItem.title);
				questionView.addView(optionView);
			}
			
			questionsLL.addView(questionView);
		}
		*/
		rbb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// pie graph go
			}
		});
		
		return view; 
	}
}
