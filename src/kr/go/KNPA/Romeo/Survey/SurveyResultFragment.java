package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey.Form.Question;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SurveyResultFragment extends Fragment {

	private Survey survey;
	public int subType;
	
	private View view;
	
	public SurveyResultFragment() {}
	public SurveyResultFragment(Survey survey, int subType) {	
		this.survey = survey;	
		this.subType = subType;	
	}
	public SurveyResultFragment(String surveyIdx) {	
		this.survey = new Survey(getActivity(), surveyIdx); 
		this.subType = survey.subType();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public void onResume() {
		super.onResume();
		
		survey.setChecked(getActivity());
		//SurveyFragment.surveyFragment(Survey.TYPE_RECEIVED).getListView().refresh();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//super.onCreateView(inflater, container, savedInstanceState);
		//Intent intent = getIntent();
		//Bundle b = intent.getExtras();
		//this.survey = b.getParcelable("survey");
		//this.context = SurveyDetailFragment.this;

		this.view = inflater.inflate(R.layout.survey_result, null, false);
		
		initNavigationBar(
				view, 
				R.string.surveyTitle, 
				true, false, 
				R.string.menu, R.string.dummy, lbbOnClickListener, null);	
		

		TextView titleTV = (TextView)view.findViewById(R.id.title);
		titleTV.setText(this.survey.title);
		
		TextView  arrivalDTTV = (TextView)view.findViewById(R.id.arrivalDT);
		String arrivalDT = Formatter.timeStampToStringInRegularFormat(this.survey.TS, getActivity());
		arrivalDTTV.setText(arrivalDT);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final User user = User.getUserWithIdx(survey.senderIdx);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						TextView senderTV = (TextView)view.findViewById(R.id.sender);
						String sender = user.department.nameFull + " " + User.RANK[user.rank] +" "  + user.name;
						senderTV.setText(sender);
					}
				});
			}
		}).start();
		
		
		TextView openDTTV = (TextView)view.findViewById(R.id.openDT);
		String openDT = Formatter.timeStampToStringWithFormat((Long)this.survey.form.get(KEY.SURVEY.OPEN_TS), getString(R.string.formatString_openDT));
		openDTTV.setText(openDT);
		
		TextView closeDTTV = (TextView)view.findViewById(R.id.closeDT);
		String closeDT = Formatter.timeStampToStringWithFormat((Long)this.survey.form.get(KEY.SURVEY.CLOSE_TS), getString(R.string.formatString_closeDT));
		closeDTTV.setText(closeDT);
		
		TextView contentTV = (TextView)view.findViewById(R.id.content);
		String content = this.survey.content;
		contentTV.setText(content);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() { 
				Data reqData = new Data();
				reqData.add(0, KEY.USER.IDX, UserInfo.getUserIdx(getActivity()));
				reqData.add(0, KEY.SURVEY.IDX, survey.idx);
				Payload request = new Payload().setEvent(Event.Message.Survey.getResult()).setData(reqData);
				Connection conn = new Connection().async(false).callBack(gotResult).requestPayload(request).request();
			}
		}).start();
		// parsing Start TODO
		/*
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

					questionView = inflater.inflate(R.layout.survey_question_result, questionsLL, false);
					
					if( qIsMultiple) {
						TextView isMultipleTV = (TextView)questionView.findViewById(R.id.isMultiple);
						isMultipleTV.setVisibility(View.VISIBLE);
					} else {
						TextView isMultipleTV = (TextView)questionView.findViewById(R.id.isMultiple);
						isMultipleTV.setVisibility(View.INVISIBLE);
					}
					
					TextView qTV = (TextView)questionView.findViewById(R.id.title);
					qTV.setText(qTitle);
					
					WebView graphWV = (WebView)questionView.findViewById(R.id.graphView);
					graphWV.getSettings().setJavaScriptEnabled(true);
					graphWV.loadUrl("file:///android_asset/www/result.html");
					graphWV.addJavascriptInterface(new graphPlugin(), "GraphPlugin");
							// window.GraphPlugin. functionName(ARG); in webview html javascript
					//graphWV.setWebChromeClient(new WebChromeClient(){});
					
					// JAVASCRIPT 호출 :  loadURL("javascript::callJS("arg");
					optionsLL = (LinearLayout)questionView.findViewById(R.id.options);
//					
//					// Options Level로 진입
//					for(int oi=0; oi<os.length(); oi++) {
//						try {
//							//o = os.getJSONObject(oi);
//							oTitle = os.getString(oi);
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//
//						// Option Level의 Manipulating
//						optionView = inflater.inflate(R.layout.survey_option_result, optionsLL, false);
//						Button optionControl = (Button)optionView.findViewById(R.id.control);
//						
//						optionControl.setTag(qi+":"+oi);
//						
//						TextView oTV = (TextView) optionView.findViewById(R.id.title);
//						oTV.setText(oTitle);
//						optionsLL.addView(optionView);
//					} // for oi END
//					
					questionsLL.addView(questionView);
				} // for qi END
				
				
			}// qs != null END
			
		}// s != null END
		*/
		return view;
		
	}
	
	final CallbackEvent<Payload, Integer, Payload> gotResult = new CallbackEvent<Payload, Integer, Payload>() {
		public void onError(String errorMsg, Exception e) {
			AlertDialog dialog = new AlertDialog.Builder(getActivity())
												.setIcon(R.drawable.icon_dialog)
												.setMessage("결과를 불러올 수 없습니다. 잠시 후 다시 시도해 주세요.")
												.show();
			MainActivity.sharedActivity().popContent();
			// TODO Error Handling
		};
		public void onPostExecute(Payload result) {
			Data resData = result.getData();
			makeResult(resData);
		};
	};
	
	private void makeResult(Data resData) {
		if(this.view == null || survey == null || survey.form == null) return;
		Survey.Form form = survey.form;
		
		int nReceivers 	= (Integer)resData.get(0, KEY.SURVEY.NUM_RECEIVERS);	// 총 수신자 수(확인X+확인O)
		int nUncheckers	= (Integer)resData.get(0, KEY.SURVEY.NUM_UNCHECKERS);	// 확인 안한사람 수
		int nCheckers 	= (Integer)resData.get(0, KEY.SURVEY.NUM_CHECKERS);		// 확인한 사람 수 (응답자+기권자)
		int nResponders	= (Integer)resData.get(0, KEY.SURVEY.NUM_RESPONDERS);	// 응답자수
		int nBlank 		= (Integer)resData.get(0, KEY.SURVEY.NUM_GIVE_UP);		// 기권자 수 (확인후 응답X)
		//int[][] vote = (Integer)resData.get(0, KEY.SURVEY.RESULT);			// 문항/선택지별 투표 수를 담고 있는 배열
		
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout _questionsLL = (LinearLayout)this.view.findViewById(R.id.questions);
		ArrayList<Question> _questions = form.questions(); 
		for( int qi=0; qi < _questions.size(); qi++) {
			Question question = _questions.get(qi);
			LinearLayout questionLL = (LinearLayout)inflater.inflate(R.layout.survey_question_result, _questionsLL, true);
			
			TextView qIndexTV = (TextView)questionLL.findViewById(R.id.index);
			qIndexTV.setText((qi+1)+".");
			
			TextView qTitleTV = (TextView)questionLL.findViewById(R.id.title);
			qTitleTV.setText(question.title());
			
			TextView qIsMultipleTV = (TextView)questionLL.findViewById(R.id.isMultiple);
			qIsMultipleTV.setVisibility((question.isMultiple() ? View.VISIBLE : View.INVISIBLE));
			
			LinearLayout optionsLL = (LinearLayout)questionLL.findViewById(R.id.options);
			
			ArrayList<String> options = question.options();
			for(int oi = 0; oi<options.size(); oi++) {
				String option = options.get(oi);
				LinearLayout optionLL = (LinearLayout)inflater.inflate(R.layout.survey_option_result, optionsLL, true);
				TextView optionTitleTV = (TextView)optionLL.findViewById(R.id.title);
				optionTitleTV.setText(option);
				
				TextView optionContentTV = (TextView)optionsLL.findViewById(R.id.content);
				
			}
		}
		
	}
	
	final OnClickListener lbbOnClickListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.sharedActivity().toggle();
		}
	};
	
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
	

	/** Object exposed to JavaScript */
    private class graphPlugin {
       public void callAndroid(final String json) { // must be final
 //         handler.post(new Runnable() {
 //            public void run() {
 //               Log.d(TAG, "callAndroid(" + arg + ")");
  //              textView.setText(arg);
  //           }
   //       });
       }
    }
}
