package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentListView;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.DBManager;
import kr.go.KNPA.Romeo.Util.IndexPath;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
	
	private ArrayList<User> receivers;

	public SurveyComposeFragment() {
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = init(inflater, container, savedInstanceState);
		receivers = new ArrayList<User>();

		return view;
	}
	
	public View init(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

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
		
		addQuestionBT = (Button)rootLayout.findViewById(R.id.add_question);
		addQuestionBT.setOnClickListener(addNewQuestion);
		
		receiversSearchBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callMemberSearchActivity(); // TODO
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
	
	public void sendSurvey() {
		// 돌면서 양식을 취합.
		int count = questionsLL.getChildCount();
		View question = null;
		
		ViewGroup rootLayout = (ViewGroup)getView().findViewById(R.id.rootLayout);
		
		StringBuilder sb = new StringBuilder();
		
		final String q = "\"";
		final String c = ":";
		final String lo = "{";
		final String ro = "}";
		final String la = "[";
		final String ra = "]";

		int openYearInt = Integer.parseInt((openETs[YEAR]).getText().toString());
		int openMonthInt = Integer.parseInt((openETs[MONTH]).getText().toString());
		int openDayInt = Integer.parseInt((openETs[DAY]).getText().toString());
		GregorianCalendar openGC = new GregorianCalendar(openYearInt, openMonthInt, openDayInt);
		long openTS = openGC.getTimeInMillis();
		
		int closeYearInt = Integer.parseInt((closeETs[YEAR]).getText().toString());
		int closeMonthInt = Integer.parseInt((closeETs[MONTH]).getText().toString());
		int closeDayInt = Integer.parseInt((closeETs[DAY]).getText().toString());
		GregorianCalendar closeGC = new GregorianCalendar(closeYearInt, closeMonthInt, closeDayInt);
		long closeTS = closeGC.getTimeInMillis();
		
		
		
		
		sb.append(lo);
//TODO
		String title = titleET.getText().toString();
		String content = contentET.getText().toString();
		User sender = User.getUserWithIdx(User.UserInfo.getUserIdx(getActivity()));
		
		receivers.add(User.userWithIdx(1));
		receivers.add(User.userWithIdx(2));
		receivers.add(User.userWithIdx(3));
		//receivers.add(User.userWithIdx(4));
		
		sb.append(q).append("title").append(q).append(c).append(q).append(title).append(q).append(",");
		sb.append(q).append("content").append(q).append(c).append(q).append(content).append(q).append(",");
		sb.append(q).append("openTS").append(q).append(c).append(openTS).append(",");
		sb.append(q).append("closeTS").append(q).append(c).append(closeTS).append(",");
		sb.append(q).append("receivers").append(q).append(c).append(q).append(User.usersToString(receivers)).append(q).append(",");
		
		//		EditText receiversET;
		sb.append(q).append("questions").append(q).append(c);
		sb.append("[");
		for(int i=0; i<count; i++) {
			question = questionsLL.getChildAt(i);
			sb.append(questionToJSON(question));
			if(i != (count-1)) sb.append(",");
		}
		sb.append("]");
		
		sb.append(ro);
		
		Appendix appendix = new Appendix();
		appendix.addAttachmentWithKeyAndAttachment(Message.MESSAGE_KEY_SURVEY, new Appendix.Attachment(Appendix.makeType(Appendix.TYPE_1_JSON, Appendix.TYPE_2_SURVEY), "Survey", sb.toString()));
		
		long currentTS = System.currentTimeMillis();
		
		//받은 사람 입장에서.
		Survey survey = new Survey.Builder()
								  .type(Message.makeType(Message.MESSAGE_TYPE_SURVEY, Survey.TYPE_DEPARTED))
								  .TS(currentTS)
								  .title(title)
								  .content(content)
								  .sender(sender)
								  .receivers(receivers)
								  .received(true)							//
								  //.idx(idx)
								  .checkTS(Message.NOT_SPECIFIED)			//
								  .checked(false)							//
								  .appendix(appendix)
								  .toSurveyBuilder()
								  .build();
								  // TODO : php에서 튜닝 여기서는 그냥 보내고.
		survey.send(getActivity());	  
		
		MainActivity.sharedActivity().popContent(this);
	}

	public String optionToJSON(View option) {
		return "\""+((EditText)option.findViewById(R.id.title)).getText().toString()+"\"";
	}
	
	public String questionToJSON(View question) {
		StringBuilder sb = new StringBuilder();
		
		final String q = "\"";
		final String c = ":";
		final String lo = "{";
		final String ro = "}";
		final String la = "[";
		final String ra = "]";
		
		StringBuilder optionsJSON = new StringBuilder();
		ViewGroup options = (ViewGroup) question.findViewById(R.id.options);
		View option = null;
		optionsJSON.append(la);
		
		int count = options.getChildCount();
		for(int i=0; i<count; i++) {
			option = options.getChildAt(i);
			optionsJSON.append(optionToJSON(option));
			if(i != (count-1)) optionsJSON.append(",");
		}
		optionsJSON.append(ra);
		
		String title = ((EditText)question.findViewById(R.id.title)).getText().toString();
		int isMultiple = ((CheckBox)question.findViewById(R.id.isMultiple)).isChecked() == true ? 1 : 0;
		sb.append(lo);
		sb.append(q).append("title").append(q).append(c).append(q).append(title).append(q).append(",");
		sb.append(q).append("isMultiple").append(q).append(c).append(isMultiple).append(",");
		sb.append(q).append("options").append(q).append(c).append(optionsJSON);
		sb.append(ro);
		return sb.toString();
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
		
		startActivityForResult(intent, MainActivity.MEMBER_SEARCH_ACTIVITY);
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
				
				long[] receiversIdx = data.getExtras().getLongArray("receivers");
				
				ArrayList<User> newUsers = new ArrayList<User>();
				for(int i=0; i< receiversIdx.length; i++ ){
					User user = User.getUserWithIdx(receiversIdx[i]);
					// TODO 이미 선택되어 잇는 사람은 ..
					if(receivers.contains(user)) continue;
					newUsers.add(user);
				}
				receivers.addAll(newUsers);
				
				if(receivers.size() > 0) {
					User fReceiver = receivers.get(0);
					receiversET.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name+" 등 "+receivers.size()+"명");
				} else {
					receiversET.setText("선택된 사용자가 없습니다.");
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
}
