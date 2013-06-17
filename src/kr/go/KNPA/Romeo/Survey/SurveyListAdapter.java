package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Survey.Survey.Form;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

class SurveyListAdapter extends BaseAdapter implements OnItemClickListener {
	
	private static final String	TAG	= "SurveyListAdapter";
	private Context mContext;
	private ArrayList<Survey> mData;
	private Integer	mSubType;
	private static final Handler mHandler = new Handler();
	public SurveyListAdapter(Context context, Integer subType, ArrayList<Survey> data)
	{
		mContext = context;
		mSubType = subType;
		
		if (data == null)
		{
			mData = new ArrayList<Survey>();
		}
		else
		{
			mData = data;
		}
	}

	public void setData(ArrayList<Survey> data)
	{
		mData = data;
	}

	@Override
	public int getCount()
	{
		return mData.size();
	}

	@Override
	public Survey getItem(int position)
	{
		return mData.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		if (mData == null)
		{
			return Constants.NOT_SPECIFIED;
		}
		else
		{
			return mData.get(position).hashCode();
		}
	}
	
	public View newView(Integer position)
	{
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = null;
		
		switch (mSubType)
		{
		case Survey.TYPE_DEPARTED:
			v = inflater.inflate(R.layout.survey_list_cell_departed, null);
			break;
		case Survey.TYPE_RECEIVED:
			v = inflater.inflate(R.layout.survey_list_cell_received, null);
			break;
		default:
			Log.wtf(TAG, "mSubType이 정해지지 않음");
			return null;
		}

		// 서베이 객체를 해당 view item의 tag로 지정.
		v.setTag(mData.get(position));

		return v;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		String surveyIdx = mData.get(position).idx;
		
		View v = null;
		if (convertView != null && convertView.getTag() != null && convertView.getTag().equals(surveyIdx))
		{
			v = convertView;
		}
		else
		{
			v = newView(position);
		}
		
		bindView(v, position);
		
		return v;
	}
	
	public void bindView(final View v, Integer position)
	{
		final Survey survey = mData.get(position);
		final User sender = MemberManager.sharedManager().getUser(survey.senderIdx);
		Form form = survey.form;

		// Title
		TextView titleTV = (TextView) v.findViewById(R.id.title);
		titleTV.setText(survey.title);
		
		TextView senderTV = (TextView) v.findViewById(R.id.sender);
		String senderInfo = sender.department.nameFull + " " + User.RANK[sender.rank] + " " + sender.name;
		senderTV.setText(senderInfo);

		TextView closeDTTV = (TextView) v.findViewById(R.id.closeDT);
		String closeDT = "";
		try
		{
			closeDT = Formatter.timeStampToStringWithFormat((Long) form.get(KEY.SURVEY.CLOSE_TS), mContext.getString(R.string.formatString_closeDT));
		}
		catch (Exception e)
		{
			closeDT = "-";
		}
		closeDTTV.setText(closeDT+"까지");

		// Departed : set Uncheckers Button
		if (mSubType == Survey.TYPE_DEPARTED)
		{
			Button goUnchecked = (Button) v.findViewById(R.id.goUnchecked);
			goUnchecked.setText(survey.numUncheckers.toString());
			goUnchecked.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view)
				{
					new GoToUncheckerListThread(survey.idx).start();
				}
			});

		}
		else if (mSubType == Survey.TYPE_RECEIVED)
		{
			int answeredColor = -1;
			String answeredStatus = null;

			if (survey.isAnswered)
			{
				answeredStatus = mContext.getString(R.string.statusAnswered);
				answeredColor = mContext.getResources().getColor(R.color.grayDark);
			}
			else
			{
				answeredStatus = mContext.getString(R.string.statusNotAnswered);
				answeredColor = mContext.getResources().getColor(R.color.maroon);
			}

			Button goResultBT = (Button) v.findViewById(R.id.goResult);

			goResultBT.setText(answeredStatus);
			goResultBT.setTextColor(answeredColor);

		}

	}

	private final class GoToUncheckerListThread extends Thread {
		private String mIdx;
		
		public GoToUncheckerListThread(String idx)
		{
			mIdx = idx;
		}
		
		@Override
		public void run()
		{
			super.run();
			mHandler.post(new Runnable(){
				@Override
				public void run()
				{
					WaiterView.showDialog(mContext);					
				}
			});
			
			final ArrayList<String> idxs = Survey.getUncheckersIdxsWithMessageTypeAndIndex(Message.MESSAGE_TYPE_SURVEY*Message.MESSAGE_TYPE_DIVIDER+mSubType, mIdx);
			
			if (idxs== null)
			{

				mHandler.post(new Runnable(){
					@Override
					public void run()
					{
						WaiterView.dismissDialog(mContext);
						Toast.makeText(mContext, "미확인자 목록을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
					}
				});
				return;
			}
			
			mHandler.post(new Runnable(){
				@Override
				public void run()
				{
					WaiterView.dismissDialog(mContext);
					Intent intent = new Intent(mContext, UserListActivity.class);
					intent.putExtra(UserListActivity.KEY_USERS_IDX, idxs);
					intent.putExtra(UserListActivity.KEY_TITLE, "미확인자 목록");
					mContext.startActivity(intent);
				}
			});
			
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position)
	{
		
		Survey survey = (Survey) view.getTag();
		if (mSubType == Survey.TYPE_RECEIVED)
		{

			long openTS = (Long) survey.form.get(KEY.SURVEY.OPEN_TS);
			long closeTS = (Long) survey.form.get(KEY.SURVEY.CLOSE_TS);
			long currentTS = System.currentTimeMillis() / 1000;

			boolean isAnswered = survey.isAnswered;
			boolean isChecked = survey.checked;

			boolean isResultPublic = false;
			if (survey.form.containsKey(KEY.SURVEY.IS_RESULT_PUBLIC))
			{
				isResultPublic = (Boolean) survey.form.get(KEY.SURVEY.IS_RESULT_PUBLIC);
			}
			else
			{
				isResultPublic = true;
			}

			if (isAnswered && isResultPublic)
			{
				SurveyResultFragment f = new SurveyResultFragment(survey);
				MainActivity.sharedActivity().pushContent(f);
				return;
			}
			else if (currentTS < openTS)
			{
				Toast.makeText(mContext, "아직 설문 기간이 아닙니다.", Toast.LENGTH_SHORT).show();

			}
			else if (currentTS >= openTS && currentTS < closeTS)
			{

				if (isAnswered)
				{
					// 자신의 답변을 강조한(TODO) 결과 양식을 보여준다.
					SurveyResultFragment f = new SurveyResultFragment(survey);
					MainActivity.sharedActivity().pushContent(f);
				}
				else
				{
					
					SurveyAnswerFragment f = new SurveyAnswerFragment(survey);
					MainActivity.sharedActivity().pushContent(f);
					
				}
				return;
			}
			else if (currentTS >= closeTS)
			{
				if (isAnswered || isChecked)
				{
					// 자신의 답변을 강조한(TODO) 결과 양식을 보여준다.
					SurveyResultFragment f = new SurveyResultFragment(survey);
					MainActivity.sharedActivity().pushContent(f);
				}
				else
				{
					Toast.makeText(mContext, "설문 참여 기간이 지났습니다.", Toast.LENGTH_SHORT).show();
				}

			}

		}
		else if (mSubType == Survey.TYPE_DEPARTED)
		{
			SurveyResultFragment f = new SurveyResultFragment(survey);
			MainActivity.sharedActivity().pushContent(f);
			
			return;
		}

	}
}
