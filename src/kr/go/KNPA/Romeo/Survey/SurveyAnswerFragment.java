package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Survey.Survey.AnswerSheet;
import kr.go.KNPA.Romeo.Survey.Survey.Form.Question;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SurveyAnswerFragment extends Fragment {
	private Survey			survey;
	public int				subType;
	private LinearLayout	questionsLL;

	public SurveyAnswerFragment(Survey survey)
	{
		this.survey = survey;
		this.subType = survey.subType();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// async
		survey.setChecked(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.survey_answer, null, false);

		initNavigationBar(view, R.string.surveyTitle, true, true, R.string.menu, R.string.submit, lbbOnClickListener, rbbOnClickListener);

		TextView titleTV = (TextView) view.findViewById(R.id.title);
		titleTV.setText(this.survey.title);

		TextView arrivalDTTV = (TextView) view.findViewById(R.id.tv_arrival_dt);
		String arrivalDT = Formatter.timeStampToStringInRegularFormat(this.survey.TS, getActivity());
		arrivalDTTV.setText(arrivalDT);

		final TextView senderTV = (TextView) view.findViewById(R.id.sender);
		final WaiterView spinner = new WaiterView(getActivity());

		new Thread(new Runnable() {

			@Override
			public void run()
			{
				final User user = User.getUserWithIdx(survey.senderIdx);

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						String sender = user.department.nameFull + " " + User.RANK[user.rank] + " " + user.name;
						senderTV.setText(sender);
						spinner.restoreView();
					}
				});
			}
		}).start();

		TextView openDTTV = (TextView) view.findViewById(R.id.openDT);
		String openDT = "";
		try
		{
			openDT = Formatter.timeStampToStringWithFormat((Long) this.survey.form.get(KEY.SURVEY.CLOSE_TS), getString(R.string.formatString_openDT));
		}
		catch (Exception e)
		{
			openDT = "-";
		}
		openDTTV.setText(openDT);

		TextView closeDTTV = (TextView) view.findViewById(R.id.closeDT);
		String closeDT = "";
		try
		{
			closeDT = Formatter.timeStampToStringWithFormat((Long) this.survey.form.get(KEY.SURVEY.CLOSE_TS), getString(R.string.formatString_closeDT));
		}
		catch (Exception e)
		{
			closeDT = "-";
		}
		closeDTTV.setText(closeDT);

		TextView contentTV = (TextView) view.findViewById(R.id.chat_content);
		String content = "";
		try
		{
			content = this.survey.content;
		}
		catch (Exception e)
		{
			content = "-";
		}

		contentTV.setText(content);

		questionsLL = (LinearLayout) view.findViewById(R.id.questions);
		ArrayList<Question> questions = survey.form.questions();

		for (int qi = 0; qi < questions.size(); qi++)
		{
			Question question = questions.get(qi);
			View questionView = inflater.inflate(R.layout.survey_question_answer, questionsLL, false);

			final boolean qIsMultiple = question.isMultiple();
			if (qIsMultiple)
			{
				TextView qIsMultipleTV = (TextView) questionView.findViewById(R.id.isMultiple);
				qIsMultipleTV.setVisibility(View.VISIBLE);
			}
			else
			{
				TextView qIsMultipleTV = (TextView) questionView.findViewById(R.id.isMultiple);
				qIsMultipleTV.setVisibility(View.INVISIBLE);
			}

			TextView qIndexTV = (TextView) questionView.findViewById(R.id.index);
			qIndexTV.setText((qi + 1) + ".");

			TextView qTitleTV = (TextView) questionView.findViewById(R.id.title);
			qTitleTV.setText(question.title());

			final LinearLayout optionsLL = (LinearLayout) questionView.findViewById(R.id.options);
			for (int oi = 0; oi < question.options().size(); oi++)
			{
				String option = question.options().get(oi);
				View optionView = inflater.inflate(R.layout.survey_option_answer, questionsLL, false);
				Button oControlBT = (Button) optionView.findViewById(R.id.control);

				oControlBT.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View button)
					{

						if (qIsMultiple)
						{
							// TODO : 배경 그림으로 boolean 판별을 하고 있음....
							if (button.getBackground().equals(getActivity().getResources().getDrawable(R.drawable.circle_check_active)))
								button.setBackgroundResource(R.drawable.circle_check_gray);
							else if (button.getBackground().equals(getActivity().getResources().getDrawable(R.drawable.circle_check_gray)))
								button.setBackgroundResource(R.drawable.circle_check_active);

						}
						else
						{
							for (int i = 0; i < optionsLL.getChildCount(); i++)
							{
								Button sib = (Button) optionsLL.getChildAt(i).findViewById(R.id.control);
								sib.setBackgroundResource(R.drawable.circle_check_gray);
							}
							button.setBackgroundResource(R.drawable.circle_check_active);
						}
					}
				});

				TextView oTitleTV = (TextView) optionView.findViewById(R.id.title);
				oTitleTV.setText(option);

				optionsLL.addView(optionView);
			}

			questionsLL.addView(questionView);

		}

		return view;
	}

	final OnClickListener	lbbOnClickListener	= new OnClickListener() {

													@Override
													public void onClick(View v)
													{
														MainActivity.sharedActivity().toggle();
													}
												};
	final OnClickListener	rbbOnClickListener	= new OnClickListener() {

													@Override
													public void onClick(View v)
													{
														v.setEnabled(false);
														submit(v);
													}
												};

	private void submit(View v)
	{
		AnswerSheet answerSheet = new AnswerSheet();

		for (int qi = 0; qi < questionsLL.getChildCount(); qi++)
		{
			View questionView = questionsLL.getChildAt(qi);

			ArrayList<Integer> selected = new ArrayList<Integer>();

			LinearLayout optionsLL = (LinearLayout) questionView.findViewById(R.id.options);
			for (int oi = 0; oi < optionsLL.getChildCount(); oi++)
			{
				View optionView = optionsLL.getChildAt(oi);
				Button control = (Button) optionView.findViewById(R.id.control);

				// if(control.getBackground().equals(getActivity().getResources().getDrawable(R.drawable.circle_check_active)))
				if (control.getBackground().getConstantState() == getActivity().getResources().getDrawable(R.drawable.circle_check_active).getConstantState())
					selected.add(oi);
			}

			answerSheet.add(selected);

		}

		survey.sendAnswerSheet(getActivity(), answerSheet);
		v.setEnabled(true);
	}

	protected void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener,
			OnClickListener rbbOnClickListener)
	{

		Button lbb = (Button) parentView.findViewById(R.id.left_bar_button);
		Button rbb = (Button) parentView.findViewById(R.id.right_bar_button);

		lbb.setVisibility((lbbVisible ? View.VISIBLE : View.INVISIBLE));
		rbb.setVisibility((rbbVisible ? View.VISIBLE : View.INVISIBLE));

		if (lbb.getVisibility() == View.VISIBLE)
		{
			lbb.setText(lbbTitle);
		}
		if (rbb.getVisibility() == View.VISIBLE)
		{
			rbb.setText(rbbTitle);
		}

		TextView titleView = (TextView) parentView.findViewById(R.id.title);
		titleView.setText(titleText);

		if (lbb.getVisibility() == View.VISIBLE)
			lbb.setOnClickListener(lbbOnClickListener);
		if (rbb.getVisibility() == View.VISIBLE)
			rbb.setOnClickListener(rbbOnClickListener);
	}

	protected void initNavigationBar(View parentView, int titleTextId, boolean lbbVisible, boolean rbbVisible, int lbbTitleId, int rbbTitleId, OnClickListener lbbOnClickListener,
			OnClickListener rbbOnClickListener)
	{
		initNavigationBar(parentView, getString(titleTextId), lbbVisible, rbbVisible, getString(lbbTitleId), getString(rbbTitleId), lbbOnClickListener, rbbOnClickListener);
	}

}