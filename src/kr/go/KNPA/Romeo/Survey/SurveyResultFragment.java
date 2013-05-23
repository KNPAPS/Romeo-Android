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

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PieChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
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
	
	protected final int[] COLORS = { Color.parseColor("#AF0452"), Color.parseColor("#FF8500"), Color.parseColor("#008196"), Color.parseColor("#002787"), Color.parseColor("#4903C7") };
	
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
			final Data resData = result.getData();
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					makeResult(resData);
				}
			});
			
		};
	};
	
	private void makeResult(Data resData) {
		if(this.view == null || survey == null || survey.form == null) return;
		Survey.Form form = survey.form;
		
//		int nReceivers 	= (Integer)resData.get(0, KEY.SURVEY.NUM_RECEIVERS);	// 총 수신자 수(확인X+확인O)
//		int nUncheckers	= (Integer)resData.get(0, KEY.SURVEY.NUM_UNCHECKERS);	// 확인 안한사람 수
//		int nCheckers 	= (Integer)resData.get(0, KEY.SURVEY.NUM_CHECKERS);		// 확인한 사람 수 (응답자+기권자)
		int nResponders	= (Integer)resData.get(0, KEY.SURVEY.NUM_RESPONDERS);	// 응답자수
//		int nBlank 		= (Integer)resData.get(0, KEY.SURVEY.NUM_GIVE_UP);		// 기권자 수 (확인후 응답X)
		ArrayList<ArrayList<Integer>> _votes = (ArrayList<ArrayList<Integer>>)resData.get(0, KEY.SURVEY.RESULT);			// 문항/선택지별 투표 수를 담고 있는 배열
		
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout _questionsLL = (LinearLayout)this.view.findViewById(R.id.questions);
		ArrayList<Question> _questions = form.questions(); 
		for( int qi=0; qi < _questions.size(); qi++) {
			Question question = _questions.get(qi);
			LinearLayout questionLL = (LinearLayout)inflater.inflate(R.layout.survey_question_result, _questionsLL, false);
			
			TextView qIndexTV = (TextView)questionLL.findViewById(R.id.index);
			qIndexTV.setText((qi+1)+".");
			
			TextView qTitleTV = (TextView)questionLL.findViewById(R.id.title);
			qTitleTV.setText(question.title());
			
			TextView qIsMultipleTV = (TextView)questionLL.findViewById(R.id.isMultiple);
			qIsMultipleTV.setVisibility((question.isMultiple() ? View.VISIBLE : View.INVISIBLE));
			
			
			// ChartView
			//ArrayList<Integer> qVote = _votes.get(qi);
			ArrayList<Integer> qVote = new ArrayList<Integer>();
			qVote.add(10);
			qVote.add(20);
			qVote.add(30);
			qVote.add(40);
			
			DefaultRenderer renderer = new DefaultRenderer();
			String dummyLabel = "";
			CategorySeries series = new CategorySeries(dummyLabel);
			for (int i=0; i< qVote.size(); i++) {
				SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
				ssr.setColor(COLORS[ i % (COLORS.length) ] );
				renderer.addSeriesRenderer(ssr);
				series.add(dummyLabel, qVote.get(i));
			}
			
			PieChart qChart = new PieChart(series, renderer);
			GraphicalView qChartView = ChartFactory.getPieChartView(getActivity(), series, renderer);
			View qChartContainer = (View)questionLL.findViewById(R.id.graphView);
					
			LinearLayout _optionsLL = (LinearLayout)questionLL.findViewById(R.id.options);
			ArrayList<String> options = question.options();
			for(int oi = 0; oi<options.size(); oi++) {
				String option = options.get(oi);
				LinearLayout optionLL = (LinearLayout)inflater.inflate(R.layout.survey_option_result, _optionsLL, false);
				TextView optionTitleTV = (TextView)optionLL.findViewById(R.id.title);
				optionTitleTV.setText(option);
				
				TextView optionContentTV = (TextView)optionLL.findViewById(R.id.content);
				int nThisOption = qVote.get(oi);
				float percent = ( (float)nThisOption / (float)nResponders * (float)100.0 );
				optionContentTV.setText( ((int)Math.round(percent)) + " %");
				optionContentTV.setTextColor( COLORS[oi%(COLORS.length)] );
				
				_optionsLL.addView(optionLL);
			}
			
			_questionsLL.addView(questionLL);
			
			// TODO : 수평선 이미지 뷰
			View ruler = new View(getActivity()); 
			ruler.setBackgroundColor(0x55EEEEEE);
			LinearLayout.LayoutParams rulerLP = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 2);
			rulerLP.leftMargin = rulerLP.rightMargin = 
					(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54, getActivity().getResources().getDisplayMetrics());
			_questionsLL.addView(ruler, rulerLP);
//			ImageView hrIV = (ImageView)inflater.inflate(R.layout.hr_view, _questionsLL, false);
//			_questionsLL.addView(hrIV);
			
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
	


    
}
