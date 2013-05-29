package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.Date;
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
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
//	@Override
//	public void onStop() {
//		super.onStop();
//		RomeoListView received = SurveyFragment.surveyFragment(Survey.TYPE_RECEIVED).getListView();
//		if(received != null) received.refresh();
//		RomeoListView departed = SurveyFragment.surveyFragment(Survey.TYPE_DEPARTED).getListView(); 
//		if(departed != null) departed.refresh();
//	}

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
		contentET = (EditText)rootLayout.findViewById(R.id.chat_content);
		ImageView hrIV = (ImageView)rootLayout.findViewById(R.id.hr);
		hrIV.setVisibility(View.INVISIBLE);
		// TODO  갯수차면 자동으로 넘어가도록.
		
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
		//// Second Unit ////
		
		// TODO , input validation
		
		GregorianCalendar openGC = new GregorianCalendar(
				Integer.parseInt(yearET.getText().toString()), 
				(Integer.parseInt(monthET.getText().toString()) -1), 	//http://widyou.net/299 Month만 -1을 해줘야 한다.
				Integer.parseInt(dayET.getText().toString()));
		return (openGC.getTimeInMillis()/1000);
	}

	
	public void sendSurvey() {
		
		WaiterView.showDialog(getActivity());
		
		// Form
		Form form = new Form();

		String title = titleET.getText().toString();
		String content = contentET.getText().toString();
		
		if( title != null && title.trim().length() > 0) {
			form.put(KEY.MESSAGE.TITLE, 	title);
		} else {
			Toast.makeText(getActivity(), "설문 제목이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
			WaiterView.dismissDialog(getActivity());
			return;
		}
		
		if(content != null && title.trim().length() > 0) {
			form.put(KEY.MESSAGE.CONTENT, 	content);
		} else {
			Toast.makeText(getActivity(), "설문 요지가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
			WaiterView.dismissDialog(getActivity());
			return;
		}
		
		if(receiversIdx == null || receiversIdx.size() == 0 ) {
			Toast.makeText(getActivity(), "설문 수신자가 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
			WaiterView.dismissDialog(getActivity());
			return;
		}
		
		boolean openTSValid = true;
		for(int i=0; i< openETs.length; i++) {
			String open = openETs[i].getText().toString();
			if(open == null || open.trim().length() ==0 )
				openTSValid = false;
		}
		
		boolean closeTSValid = true;
		for(int i=0; i< closeETs.length; i++) {
			String close = closeETs[i].getText().toString();
			if(close == null || close.trim().length() == 0)
				closeTSValid = false;
		}
		
		if( openTSValid == true && closeTSValid == true) {
			//if( openETs[0] )
			//	closeETs[0];
			
			if( Integer.parseInt( openETs[1].getText().toString() ) < 1 
					|| Integer.parseInt( openETs[1].getText().toString() ) > 12) {
				WaiterView.dismissDialog(getActivity());
				openETs[1].setText("");
				Toast.makeText(getActivity(), "설문 시작 월이 정확하지 않습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if( Integer.parseInt( closeETs[1].getText().toString() ) < 1 
					|| Integer.parseInt( closeETs[1].getText().toString() ) > 12) {
				WaiterView.dismissDialog(getActivity());
				closeETs[1].setText("");
				Toast.makeText(getActivity(), "설문 종료 월이 정확하지 않습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			if( Integer.parseInt( openETs[2].getText().toString() ) < 1 
					|| Integer.parseInt( openETs[2].getText().toString() ) > 31) {
				WaiterView.dismissDialog(getActivity());
				openETs[1].setText("");
				Toast.makeText(getActivity(), "설문 시작 일이 정확하지 않습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if( Integer.parseInt( closeETs[2].getText().toString() ) < 1 
					|| Integer.parseInt( closeETs[2].getText().toString() ) > 31) {
				WaiterView.dismissDialog(getActivity());
				closeETs[1].setText("");
				Toast.makeText(getActivity(), "설문 종료 일이 정확하지 않습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			long openTS = getTSFrom(openETs);
			long closeTS = getTSFrom(closeETs);
			long currentTS = new Date().getTime() / 1000;
			
			if( openTS < currentTS) {
				WaiterView.dismissDialog(getActivity());
				Toast.makeText(getActivity(), "설문 시작 시간이 현재보다 이전입니다.", Toast.LENGTH_SHORT).show();
				return;
			} else if( closeTS < currentTS) {
				WaiterView.dismissDialog(getActivity());
				Toast.makeText(getActivity(), "설문 종료 시간이 현재보다 이전입니다.", Toast.LENGTH_SHORT).show();
				return;
			} else if( closeTS < openTS ) {
				WaiterView.dismissDialog(getActivity());
				Toast.makeText(getActivity(), "설문 종료 시간이 설문 시작시간보다 이전입니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			form.put(KEY.SURVEY.OPEN_TS, openTS);
			form.put(KEY.SURVEY.CLOSE_TS, closeTS);
		} else {
			Toast.makeText(getActivity(), "설문 기간이 정확히 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
			WaiterView.dismissDialog(getActivity());
			// TODO : NumberFormatException
			return;
		}
		
		
		// TODO : 나머지 Validation
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
			
			questions.add(question);
		}
		 
		form.put(KEY.SURVEY.QUESTIONS, questions);
		
		long currentTS = System.currentTimeMillis()/1000;
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
				currentTS,
				form
				);
		survey.form = form;
		survey.send(getActivity());	  
		
		InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		
		// WaiterView.dismissDialog(getActivity()); : Survey.AfterSend();
		
		// FragmentManager fm = getActivity().getSupportFragmentManager();
		// fm.getBackStackEntryAt(fm.getBackStackEntryCount()-2).;
		SurveyFragment departedFragment = SurveyFragment.surveyFragment(Survey.TYPE_DEPARTED);
		if( departedFragment != null && departedFragment.listView != null) {
			departedFragment.listView.refresh();
		}
		MainActivity.sharedActivity().popContent();
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
				Toast.makeText(getActivity(), "선택된 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
			} else {
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