package kr.go.KNPA.Romeo.Survey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey.Form;
import kr.go.KNPA.Romeo.Util.IndexPath;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import kr.go.KNPA.Romeo.search.MemberSearchActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SurveyComposeFragment extends Fragment {

	public final static int YEAR = 0;
	public final static int MONTH = 1;
	public final static int DAY = 2;
	
	private final static int MODE_ADD = 0;
	private final static int MODE_REMOVE = 1;

	private TextView	closeDate;
	private TextView	closeTime;
	private EditText titleET;
	private TextView receiversTV;
	private EditText contentET;
	private CheckBox isResultPublicCB;
	
	LinearLayout questionsLL;
	Button addQuestionBT;
	
	private ArrayList<String> receiversIdx;

	public SurveyComposeFragment() {
	}
	
	/**
	 * 초기에 수신자를 외부에서 정해서 시작할 때 이 생성자로 호출
	 * @param receivers
	 */
	public SurveyComposeFragment(ArrayList<String> receivers)
	{
		receiversIdx = receivers;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = init(inflater, container, savedInstanceState);
		
		if (receiversIdx != null && receiversIdx.size() > 0)
		{
			setReceiverET();
		}
		else
		{
			receiversIdx = new ArrayList<String>();
		}
		
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
		receiversTV = (TextView)((ViewGroup)rootLayout.getChildAt(1)).findViewById(R.id.receivers);
		
		closeDate = (TextView)((ViewGroup)rootLayout.getChildAt(2)).findViewById(R.id.close_date);
		closeTime = (TextView)((ViewGroup)rootLayout.getChildAt(2)).findViewById(R.id.close_time);
		
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
		
        setCloseDate(year, month, day);
        setCloseTime(hour, minute);
        
		closeDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getActivity().getSupportFragmentManager(), "종료 일자");
			}
		});
		
		closeTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
			    DialogFragment newFragment = new TimePickerFragment();
			    newFragment.show(getActivity().getSupportFragmentManager(), "종료 시간");
			}
		});
		
		contentET = (EditText)rootLayout.findViewById(R.id.chat_content);
		isResultPublicCB = (CheckBox)rootLayout.findViewById(R.id.isResultPublic);
		
		ImageView hrIV = (ImageView)rootLayout.findViewById(R.id.hr);
		hrIV.setVisibility(View.INVISIBLE);
		// TODO  갯수차면 자동으로 넘어가도록.
		
		addQuestionBT = (Button)rootLayout.findViewById(R.id.add_question);
		addQuestionBT.setOnClickListener(addNewQuestion);
		
		receiversTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callMemberSearchActivity(); 
			}
		});
		
		addQuestionBT.setOnClickListener(addNewQuestion);
		questionsLL = (LinearLayout)rootLayout.findViewById(R.id.questions);
		
		Button qControlBT = (Button)questionsLL.getChildAt(0).findViewById(R.id.control);
		optionControlSetMode(qControlBT, MODE_ADD);
		
		
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
//TODO
		Integer year = Integer.parseInt(yearET.getText().toString());
		Integer month = Integer.parseInt(monthET.getText().toString());
		Integer day = Integer.parseInt(dayET.getText().toString());
		
		Date date = new Date(year,month,day);
		return 0;
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
		
		long openTS = System.currentTimeMillis()/1000;
		long closeTS = 0;
		
		String dateTime = closeDate.getTag().toString()+" "+closeTime.getTag().toString(); // YYYY-MM-DD HH:mm
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-dd HH:mm", Locale.KOREA);
		Date date;
		
		try
		{
			date = (Date) formatter.parse(dateTime);
		}
		catch (ParseException e)
		{
			date = new Date();
			e.printStackTrace();
		}
		
		closeTS = (date.getTime()/1000);
		
		// 양식에 실제로 시간이 들어가는 부분
		form.put(KEY.SURVEY.OPEN_TS, openTS);
		form.put(KEY.SURVEY.CLOSE_TS, closeTS);
		
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
//		SurveyFragment departedFragment = SurveyFragment.surveyFragment(Survey.TYPE_DEPARTED);
//		if( departedFragment != null && departedFragment.listView != null) {
//			departedFragment.listView.refresh();
//		}
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
		Intent intent = new Intent(getActivity(), MemberSearchActivity.class);
		intent.putExtra(MemberSearchActivity.KEY_INITIAL_IDXS, receiversIdx);
		startActivityForResult(intent, MemberSearchActivity.REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MemberSearchActivity.REQUEST_CODE) {
			if(resultCode == MemberSearchActivity.RESULT_OK) {
				// 추가
				receiversIdx = data.getExtras().getStringArrayList(MemberSearchActivity.KEY_RESULT_IDXS);
				
				setReceiverET();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void setReceiverET()
	{
		if (receiversIdx.size() > 1) {
			User fReceiver = User.getUserWithIdx(receiversIdx.get(0));
			receiversTV.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name+" 등 "+receiversIdx.size()+"명");
		} else if(receiversIdx.size() > 0) {
			User fReceiver = User.getUserWithIdx(receiversIdx.get(0));
			receiversTV.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name);
		} else {
			receiversTV.setText("선택된 사용자가 없습니다.");
		}
	}
	
	public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			setCloseTime(hourOfDay, minute);
		}

	}

	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
	        // Use the current date as the default date in the picker
	        final Calendar c = Calendar.getInstance();
	        int year = c.get(Calendar.YEAR);
	        int month = c.get(Calendar.MONTH);
	        int day = c.get(Calendar.DAY_OF_MONTH);

	        // Create a new instance of DatePickerDialog and return it
	        return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			setCloseDate(year, monthOfYear, dayOfMonth);
		}

	}
	
	private void setCloseDate(int year, int monthOfYear, int dayOfMonth)
	{
		String data = String.valueOf(year)+"-"+String.valueOf(monthOfYear+1)+"-"+String.valueOf(dayOfMonth);
		String readable = String.valueOf(year)+"년 "+String.valueOf(monthOfYear+1)+"월 "+String.valueOf(dayOfMonth)+"일";
		closeDate.setText(readable);
		closeDate.setTag(data);
	}
	
	private void setCloseTime(int hourOfDay, int minute)
	{
		String dataString = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
		String readableString = "";
		if (hourOfDay >= 12)
		{
			readableString += "오후 "+String.valueOf(hourOfDay==12?12:hourOfDay-12)+"시 "+String.valueOf(minute)+"분";
		}
		else
		{
			readableString += "오전 "+String.valueOf(hourOfDay)+"시 "+String.valueOf(minute)+"분";
		}
		
		closeTime.setText(readableString);
		closeTime.setTag(dataString);
	}
}