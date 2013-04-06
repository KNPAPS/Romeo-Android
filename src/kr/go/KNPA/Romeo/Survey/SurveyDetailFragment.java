package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SurveyDetailFragment extends Fragment  {
	private Context context;
	private Survey survey;
	public int type;
	private QuestionManager qm;
	
	public SurveyDetailFragment() {
	}
/*	
	public SurveyDetailFragment(int type) {
		super();
		this.survey = survey;
		this.type = type;
	}
	*/
	
	public void init() {
		Bundle b = getArguments();
		this.survey = (Survey)b.getParcelable("survey");
		this.type = b.getInt("type");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	@Override
	public void onResume() {
		super.onResume();

		survey.setChecked(getActivity());
		SurveyFragment.surveyFragment(type).getListView().refresh();
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
		User user = this.survey.sender;
		String sender = user.department.nameFull + " " + User.RANK[user.rank] +" "  + user.name;
		senderTV.setText(sender);

		TextView openDTTV = (TextView)view.findViewById(R.id.openDT);
		String openDT = Formatter.timeStampToStringWithFormat(this.survey.openTS(), getString(R.string.formatString_openDT));
		openDTTV.setText(openDT);

		TextView closeDTTV = (TextView)view.findViewById(R.id.closeDT);
		String closeDT = Formatter.timeStampToStringWithFormat(this.survey.closeTS(), getString(R.string.formatString_closeDT));
		closeDTTV.setText(closeDT);

		TextView contentTV = (TextView)view.findViewById(R.id.content);
		String content = this.survey.content;
		contentTV.setText(content);

		qm = new QuestionManager();

		// parsing Start TODO
		/*
		Appendix adx = survey.appendix;
		String json = adx.getAttachmentWithKey(Message.MESSAGE_KEY_SURVEY).getJSON();

		JSONObject s = null;
		try {
			s = new JSONObject(json);
		} catch (JSONException e) {
		}
		if(s != null) {
			// 위에서 이미 Survey Message Object를 통해 처리된 것들 
			//s.getString("title");
			//s.getString("content");
			//s.getLong("openTS");
			//s.getLong("closeTS");
			//s.getString("receivers");
			JSONObject q = null;
			JSONArray os = null;
			JSONObject o = null;
			JSONArray qs = null;

			String qTitle = null;
			boolean qIsMultiple = false;
			String oTitle = null;

			LinearLayout questionsLL = (LinearLayout)view.findViewById(R.id.questions);
			View questionView = null;
			LinearLayout optionsLL = null;
			View optionView = null;

			try {
				qs = s.getJSONArray("questions");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(qs != null) {


				for(int qi=0; qi<qs.length(); qi++) {
					try {
						q = qs.getJSONObject(qi);
						qTitle = q.getString("title");
						qIsMultiple = (q.getInt("isMultiple") == 1 ? true : false);
						os = q.getJSONArray("options");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					qm.questions.add(new QuestionUnit(qIsMultiple, qi));
					// Question level의 form 처리.
					// qTitle
					// qIsMultiple
					questionView = inflater.inflate(R.layout.survey_question_detail, questionsLL, false);

					if( qIsMultiple) {
						TextView isMultipleTV = (TextView)questionView.findViewById(R.id.isMultiple);
						isMultipleTV.setVisibility(View.VISIBLE);
					} else {
						TextView isMultipleTV = (TextView)questionView.findViewById(R.id.isMultiple);
						isMultipleTV.setVisibility(View.INVISIBLE);
					}

					TextView qTV = (TextView)questionView.findViewById(R.id.title);
					qTV.setText(qTitle);

					optionsLL = (LinearLayout)questionView.findViewById(R.id.options);

					// Options Level로 진입
					for(int oi=0; oi<os.length(); oi++) {
						try {
							//o = os.getJSONObject(oi);
							oTitle = os.getString(oi);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						// Option Level의 Manipulating
						optionView = inflater.inflate(R.layout.survey_option_result, optionsLL, false);
						Button optionControl = (Button)optionView.findViewById(R.id.control);
						OptionUnit ou = new OptionUnit(qi, oi, optionControl);
						qm.questions.get(qi).options.add(ou);

						optionControl.setTag(qi+":"+oi);
						optionControl.setOnClickListener(ou.optionClick);

						TextView oTV = (TextView) optionView.findViewById(R.id.title);
						oTV.setText(oTitle);
						optionsLL.addView(optionView);
					} // for oi END

					questionsLL.addView(questionView);
				} // for qi END


			}// qs != null END

		}// s != null END
		*/
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


	private class QuestionManager {
		ArrayList<QuestionUnit> questions;
		public QuestionManager() {
			questions = new ArrayList<QuestionUnit>();
		}

		public String toJSON() {
			StringBuilder sb = new StringBuilder();

			final String q = "\"";
			final String c = ":";
			final String lab = "[";
			final String rab = "]";
			final String lob = "{";
			final String rob = "}";

			boolean wasTrue;
			sb.append(lab);
			for(int qi=0; qi<questions.size(); qi++) {
				wasTrue = false;
				sb.append(lob);
				QuestionUnit qu = questions.get(qi);
				sb.append(q).append("isMultiple").append(q).append(c).append((qu.isMultiple == true?1:0)).append(",");
				sb.append(q).append("options").append(q).append(c);
				if(qu.isMultiple) sb.append(lab);
				for(int oi=0; oi<qu.options.size(); oi++) {
					OptionUnit ou = qu.options.get(oi);

					if(ou.selected==true && oi!=0 && wasTrue==true) sb.append(",");
					if(ou.selected==true) {
						sb.append(oi);
						wasTrue = true;
					}
				}
				if(qu.isMultiple) sb.append(rab);
				sb.append(rob);
				if(qi != (questions.size()-1)) sb.append(",");
			}
			sb.append(rab);
			return sb.toString();
		}
	}

	private class QuestionUnit {
		ArrayList<OptionUnit> options;
		public boolean isMultiple;
		int index = Message.NOT_SPECIFIED;
		public QuestionUnit(boolean isMultiple, int index) {
			options = new ArrayList<OptionUnit>();
			this.isMultiple = isMultiple;
		}
	}

	private class OptionUnit {
		public boolean selected = false;
		int index = Message.NOT_SPECIFIED;
		int questinoIndex = Message.NOT_SPECIFIED;
		public View control;
		public OptionUnit(int qi, int oi, View v) {
			questinoIndex = qi;
			index = oi;
			control = v;
		}
		final OnClickListener optionClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				QuestionUnit qu = qm.questions.get(questinoIndex);
				boolean m = qu.isMultiple;
				if( m == true) {
					if(selected == true) {
						selected = false;
						v.setBackgroundResource(R.drawable.circle_check_gray);
					}else {
						selected = true;
						v.setBackgroundResource(R.drawable.circle_check_active);
					}
				} else {
					if(selected == true) {

					}else {
						v.setBackgroundResource(R.drawable.circle_check_active);
						selected = true;
						for(int i=0; i<qu.options.size(); i++) {
							if(i == index) continue;
							OptionUnit ou = qu.options.get(i);
							ou.selected = false;
							ou.control.setBackgroundResource(R.drawable.circle_check_gray);
						}

					}
				}
			}
		}; 
	}
}