package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey.Form;
import kr.go.KNPA.Romeo.Util.IndexPath;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SurveyComposeFragment extends Fragment {

	public final static int YEAR = 0;
	public final static int MONTH = 1;
	public final static int DAY = 2;
	
	private final static int MODE_ADD = 0;
	private final static int MODE_REMOVE = 1;
	
	EditText titleET;
	EditText receiversET;
	Button receiversSearchBT;
	EditText[] openETs;
	EditText[] closeETs;
	EditText contentET;
	
	LinearLayout questionsLL;
	Button addQuestionBT;
	
	private ArrayList<String> receiversIdx;

	public SurveyComposeFragment() {
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = init(inflater, container, savedInstanceState);
		receiversIdx = new ArrayList<String>();
		return view;
	}
	
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Bar Button 리스너
		OnClickListener lbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		OnClickListener rbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendSurvey();
			}
		};
		
		View view = inflater.inflate(R.layout.survey_compose, container, false);
		initNavigationBar(
						view, 
						R.string.surveyComposeTitle, 
						true, 
						true, 
						R.string.menu, 
						R.string.send, 
						lbbOnClickListener, rbbOnClickListener);

		ViewGroup rootLayout = (ViewGroup)view.findViewById(R.id.rootLayout);
		
		titleET = (EditText)((ViewGroup)rootLayout.getChildAt(0)).findViewById(R.id.title);
		receiversET = (EditText)((ViewGroup)rootLayout.getChildAt(1)).findViewById(R.id.receivers);
		receiversSearchBT = (Button)((ViewGroup)rootLayout.getChildAt(1)).findViewById(R.id.receivers_search);
		
		openETs = new EditText[3];
		closeETs = new EditText[3];
		openETs[YEAR] = (EditText)((ViewGroup)rootLayout.getChildAt(2)).findViewById(R.id.open_year);
		openETs[MONTH] = (EditText)((ViewGroup)rootLayout.getChildAt(2)).findViewById(R.id.open_month);
		openETs[DAY] = (EditText)((ViewGroup)rootLayout.getChildAt(2)).findViewById(R.id.open_day);
		closeETs[YEAR] = (EditText)((ViewGroup)rootLayout.getChildAt(2)).findViewById(R.id.close_year);
		closeETs[MONTH] = (EditText)((ViewGroup)rootLayout.getChildAt(2)).findViewById(R.id.close_month);
		closeETs[DAY] = (EditText)((ViewGroup)rootLayout.getChildAt(2)).findViewById(R.id.close_day);
		contentET = (EditText)rootLayout.findViewById(R.id.content);
		ImageView hrIV = (ImageView)rootLayout.findViewById(R.id.hr);
		hrIV.setVisibility(View.INVISIBLE);
		// TODO  엔터치면 자동으로 넘어가도록.
		
		addQuestionBT = (Button)rootLayout.findViewById(R.id.add_question);
		addQuestionBT.setOnClickListener(addNewQuestion);
		
		receiversSearchBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callMemberSearchActivity(); 
			}
		});
		
		addQuestionBT.setOnClickListener(addNewQuestion);
		questionsLL = (LinearLayout)rootLayout.findViewById(R.id.questions);

		EditText qTitleET = (EditText)questionsLL.getChildAt(0).findViewById(R.id.title);
		CheckBox qIsMultipleCB = (CheckBox)questionsLL.getChildAt(0).findViewById(R.id.isMultiple);
		
		Button qControlBT = (Button)questionsLL.getChildAt(0).findViewById(R.id.control);
		optionControlSetMode(qControlBT, MODE_ADD);
		
		LinearLayout qOptionsLL = (LinearLayout)questionsLL.getChildAt(0).findViewById(R.id.options);
		
		
		return view;
	}
	
	
	
	private int getQuestionIndexClicked(View view){
		int count = questionsLL.getChildCount();
		View _v = null;
		for(int i=0; i< count; i++) {
			_v = questionsLL.getChildAt(i).findViewWithTag(view.getTag());
			if(_v != null) return i;
		}
		return Message.NOT_SPECIFIED;
	}
	
	private int getOptionIndexClicked(View view, int questionIndex) {
		ViewGroup options = (ViewGroup) questionsLL.getChildAt(questionIndex).findViewById(R.id.options);
		
		int count = options.getChildCount();
		
		View _v = null;
		for(int i=0; i< count; i++) {
			//_v = options.getChildAt(i).findViewById(view.getId());
			_v = options.getChildAt(i).findViewWithTag(view.getTag());
			if(_v != null) return i;
		}
		return Message.NOT_SPECIFIED;
	}
	
	private View getQuestionViewAtIndex(int qi) {
		return questionsLL.getChildAt(qi);
	}
	
	private View getOptionViewAtIndex(int qi, int oi) {
		View question = getQuestionViewAtIndex(qi);
		ViewGroup options = (ViewGroup) question.findViewById(R.id.options);
		return options.getChildAt(oi);
	}
	
	private View getQuestionViewAtIndexPath(IndexPath path) {
		return getQuestionViewAtIndex(path.getIndexes(null)[0]);
	}
	
	private View getOptionViewAtIndexPath(IndexPath path) {
		return getOptionViewAtIndex(path.getIndexes(null)[0], path.getIndexes(null)[1]);
	}
	
	private IndexPath getIndexPathClicked(View view) {
		int qi = getQuestionIndexClicked(view);
		int oi = getOptionIndexClicked(view, qi);
		int[] indexes = {qi, oi};
		return IndexPath.indexPathWithIndexesAndLength(indexes, indexes.length);
	}
	
	private View getQuestionViewClicked(View view) {
		return getQuestionViewAtIndexPath(getIndexPathClicked(view));
	}
	
	private View getOptionViewClicked(View view) {
		return getOptionViewAtIndexPath(getIndexPathClicked(view));
	}
	
	
	final OnClickListener removeClickedOption = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int[] indexes = getIndexPathClicked(v).getIndexes(null);
			View qv = getQuestionViewAtIndex(indexes[0]);
			LinearLayout options = (LinearLayout)qv.findViewById(R.id.options);
			
			v.setOnClickListener(null);
			options.removeViewAt(indexes[1]);
		}
	};

	final OnClickListener addNewOption = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int[] indexes = getIndexPathClicked(v).getIndexes(null);
			View qv = getQuestionViewAtIndex(indexes[0]);
			LinearLayout options = (LinearLayout)qv.findViewById(R.id.options);
			
			View beforeOption = getOptionViewAtIndex(indexes[0], indexes[1]);
			Button bControlBT = (Button)beforeOption.findViewById(R.id.control);
			optionControlSetMode(bControlBT, MODE_REMOVE);
			bControlBT.invalidate();
			
			
			LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View option = inflater.inflate(R.layout.survey_option_compose, options, false);
			Button controlBT = (Button)option.findViewById(R.id.control);
			optionControlSetMode(controlBT, MODE_ADD);
			
			options.addView(option);
		}
	};
	
	final OnClickListener removeClickedQuestion = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			((ViewGroup)questionsLL.getChildAt(0)).removeViewAt(0); // HR
			//ImageView hrIV = (ImageView)questionsLL.getChildAt(0).findViewById(R.id.hr);
			//hrIV.setVisibility(View.INVISIBLE);
			
		}
	};
	
	final OnClickListener addNewQuestion = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.survey_question_compose, questionsLL, false);
			EditText qTitleET = (EditText)view.findViewById(R.id.title);
			CheckBox qIsMultipleCB = (CheckBox)view.findViewById(R.id.isMultiple);
			Button qControlBT = (Button)view.findViewById(R.id.control);
			optionControlSetMode(qControlBT, MODE_ADD);
			
			LinearLayout qOptionsLL = (LinearLayout)view.findViewById(R.id.options);
			questionsLL.addView(view);
		}
	};
	
	public void optionControlSetMode(Button control, int mode) {
		if(mode == MODE_ADD) {
			control.setBackgroundResource(R.drawable.circle_plus_active);
			control.setOnClickListener(addNewOption);
		} else if (mode == MODE_REMOVE) {
			control.setBackgroundResource(R.drawable.circle_minus_gray);
			control.setOnClickListener(removeClickedOption);
		}
		String tag = System.currentTimeMillis()+""+Math.random();
		control.setTag(tag);
	}
	
	private long getTSFrom(EditText[] ets) { 
		return getTSFrom(ets[YEAR], ets[MONTH], ets[DAY]); 
	}
	
	private long getTSFrom(EditText yearET, EditText monthET, EditText dayET) {
		GregorianCalendar openGC = new GregorianCalendar(
				Integer.parseInt(yearET.getText().toString()), 
				Integer.parseInt(monthET.getText().toString()), 
				Integer.parseInt(dayET.getText().toString()));
		return openGC.getTimeInMillis();
	}
	
	public void sendSurvey() {
		// Form
		Form form = new Form();

		// TODO : title, content
		String title = titleET.getText().toString();
		String content = contentET.getText().toString();
		form.put(KEY.SURVEY.TITLE, 	title);
		form.put(KEY.SURVEY.CONTENT, 	content);
		form.put(KEY.SURVEY.OPEN_TS, getTSFrom(openETs));
		form.put(KEY.SURVEY.CLOSE_TS, getTSFrom(closeETs));
		//form.put(Form.IS_MULTIPLE, value);
		
		// 돌면서 양식을 취합.
		ArrayList<Form.Question> questions = new ArrayList<Form.Question>();
		
		for( int qi=0; qi< questionsLL.getChildCount(); qi++) {
			Form.Question question = new Form.Question();
			View qView = questionsLL.getChildAt(qi);
			ViewGroup oViews = (ViewGroup)qView.findViewById(R.id.options);
			
			// options
			for( int oi=0; oi<oViews.getChildCount(); oi++) {
				View oView = oViews.getChildAt(oi);
				String option = ((EditText)oView.findViewById(R.id.title)).getText().toString();
				question.addOption(option);
			}
			
			// title
			String questionTitle = ((EditText)qView.findViewById(R.id.title)).getText().toString();
			question.title(questionTitle);
			
			// isMultiple
			boolean isMultiple = ((CheckBox)qView.findViewById(R.id.isMultiple)).isChecked() == true;
			question.isMultiple(isMultiple);
			
		}
		 
		form.put(KEY.SURVEY.QUESTIONS, questions);
		
		long currentTS = System.currentTimeMillis();
		Survey survey = new Survey(
				null, 
				Message.makeType(Message.MESSAGE_TYPE_SURVEY, Survey.TYPE_DEPARTED) , // TODO 
				title, 
				content, 
				UserInfo.getUserIdx(getActivity()), 
				receiversIdx, 
				false, 
				currentTS, 
				true, 
				currentTS
				);
		
		survey.send(getActivity());	  
		
		MainActivity.sharedActivity().popContent(this);
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

	private void callMemberSearchActivity() {
		Intent intent = new Intent(getActivity(), MemberSearch.class);
		startActivityForResult(intent, MemberSearch.REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MemberSearch.REQUEST_CODE) {
			if(resultCode != MemberSearch.RESULT_OK) {
				// onError
				Toast.makeText(getActivity(), "Activity Result Error", Toast.LENGTH_SHORT).show();
			} else {
				//data.getExtras().get;
				Toast.makeText(getActivity(), "Activity Result Success", Toast.LENGTH_SHORT).show();
				
				// 대체
				receiversIdx = data.getExtras().getStringArrayList(MemberSearch.KEY_RESULT_USERS_IDX);
				
				if (receiversIdx.size() > 1) {
					User fReceiver = User.getUserWithIdx(receiversIdx.get(0));
					receiversET.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name+" 등 "+receiversIdx.size()+"명");
				} else if(receiversIdx.size() > 0) {
					User fReceiver = User.getUserWithIdx(receiversIdx.get(0));
					receiversET.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name);
				} else {
					receiversET.setText("선택된 사용자가 없습니다.");
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
}
