package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.SurveyDAO;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
class SurveyListAdapter extends CursorAdapter implements OnItemClickListener{
	// Variables
	public int subType = Survey.NOT_SPECIFIED;
	
	private Context context;
	// Constructor
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery) 				{	super(context, c, autoRequery);							this.context = context;	}
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery, int subType) 	{	super(context, c, autoRequery);	this.subType = subType;	this.context = context;	}
	public SurveyListAdapter(Context context, Cursor c, int flags) 							{	super(context, c, flags);								this.context = context;	}

	public Survey getSurvey(Cursor cSurvey) {
		HashMap<String, Survey> surveys = SurveyFragment.surveyFragment(this.subType).getListView().surveys;//null;
		if(surveys != null) {
			String surveyIdx = cSurvey.getString(cSurvey.getColumnIndex(SurveyDAO.COLUMN_SURVEY_IDX));
			if(surveys.containsKey(surveyIdx))
				return surveys.get(surveyIdx);
		}
		
		return null;
	}
	
	@Override
	public void bindView(final View v, final Context context, final Cursor c) {
		// Animation	// TODO
		
		final Survey survey = getSurvey(c);
		if (survey==null ) return;		
		Survey.Form form = survey.form; 

		// Title
		TextView titleTV = (TextView)v.findViewById(R.id.title);
		titleTV.setText(survey.title);

		// USER Thread
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final User sender = User.getUserWithIdx(survey.senderIdx);
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						TextView senderTV = (TextView)v.findViewById(R.id.sender);
						String senderInfo = sender.department.nameFull +" "+User.RANK[sender.rank]+" " +sender.name;
						senderTV.setText(senderInfo);
					}
				});
				
			}
		}).start();

		
		// Open ~ Close Date Time
		TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
		String openDT = "";
		try {
			openDT = Formatter.timeStampToStringWithFormat((Long)form.get(KEY.SURVEY.OPEN_TS), context.getString(R.string.formatString_openDT)); 
		} catch(Exception e) {
			openDT = "-";
		}
		openDTTV.setText(openDT);

		TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
		String closeDT = "";
		try {
			closeDT = Formatter.timeStampToStringWithFormat((Long)form.get(KEY.SURVEY.CLOSE_TS), context.getString(R.string.formatString_closeDT));
		} catch(Exception e) {
			closeDT = "-";
		}
		closeDTTV.setText(closeDT);
		
		
		// Departed : set Uncheckers Button
		if(this.subType == Survey.TYPE_DEPARTED) {
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					final ArrayList<String> idxs = Survey.getUncheckersIdxsWithMessageTypeAndIndex(survey.type(), survey.idx);
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
							goUnchecked.setText(""+idxs.size());
							goUnchecked.setOnClickListener(new OnClickListener() {
							
								@Override
								public void onClick(View view) {
									
									if(idxs != null) {
										Intent intent = new Intent(context, UserListActivity.class);
										Bundle b = new Bundle();
										b.putStringArrayList(UserListActivity.KEY_USERS_IDX, idxs);
										
										intent.putExtras(b);
										context.startActivity(intent);
									}
								}
							});

						}
					});
					
				}
			}).start();

		// Received : go Result	
		} else if(this.subType == Survey.TYPE_RECEIVED) {
			
			boolean isAnswered = survey.isAnswered(context);
			
			int answeredColor = context.getResources().getColor(R.color.black);
			String answeredStatus = null;

			if(isAnswered) {
				answeredStatus 	= context.getString(R.string.statusAnswered);
				answeredColor 	= context.getResources().getColor(R.color.grayDark);
			} else {
				answeredStatus 	= context.getString(R.string.statusNotAnswered);
				answeredColor 	= context.getResources().getColor(R.color.maroon);
			}
			
			Button goResultBT = (Button)v.findViewById(R.id.goResult);

			goResultBT.setText(answeredStatus);
			goResultBT.setTextColor(answeredColor);
					
		}
	
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = null;
		switch(this.subType) {
			case Survey.TYPE_DEPARTED :	v = inflater.inflate(R.layout.survey_list_cell_departed, parent,false);		break;
			case Survey.TYPE_RECEIVED :	v = inflater.inflate(R.layout.survey_list_cell_received, parent,false);		break;
				
			default :
			case Survey.NOT_SPECIFIED :	break;	
			// ListView 에서 tableName이 정해진 경우에만 넘어오므로, 이 지점에 도닳할 수 없다.
		}
		
		return v;
	}

	private static class SurveyHandler extends Handler {
		private int subType;
		public SurveyHandler(int subType) {
			super();
			this.subType = subType;
		}
		
		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			/*HashMap<String, Object> obj = (HashMap<String, Object>)msg.obj; 
			final View v = (View)obj.get("view") ;
			final Context context = (Context)obj.get("context");
			
			if(msg.what == WHAT_SURVEY) {
				
				final Survey survey = (Survey)obj.get("survey");
				Survey.Form form = survey.form; 
		
				// Title
				TextView titleTV = (TextView)v.findViewById(R.id.title);
				titleTV.setText(survey.title);

				// USER Thread
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						User sender = User.getUserWithIdx(survey.senderIdx);
						
						android.os.Message message = obtainMessage();
						message.what = WHAT_SENDER;
						HashMap<String, Object> obj = new HashMap<String, Object>();
						obj.put("view", v);
						obj.put("context", context);
						obj.put("sender", sender);
						message.obj = obj;
						sendMessage(message);
					}
				}).start();

				
				// Open ~ Close Date Time
				TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
				String openDT = "";
				try {
					openDT = Formatter.timeStampToStringWithFormat((Long)form.get(KEY.SURVEY.OPEN_TS), context.getString(R.string.formatString_openDT)); 
				} catch(Exception e) {
					openDT = "-";
				}
				openDTTV.setText(openDT);
	
				TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
				String closeDT = "";
				try {
					closeDT = Formatter.timeStampToStringWithFormat((Long)form.get(KEY.SURVEY.CLOSE_TS), context.getString(R.string.formatString_closeDT));
				} catch(Exception e) {
					closeDT = "-";
				}
				closeDTTV.setText(closeDT);
				
				
				// Departed : set Uncheckers Button
				if(this.subType == Survey.TYPE_DEPARTED) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ArrayList<String> idxs = Survey.getUncheckersIdxsWithMessageTypeAndIndex(survey.type(), survey.idx);
							
							android.os.Message message = obtainMessage();
							message.what = WHAT_UNCHECKERS;
							HashMap<String, Object> obj = new HashMap<String, Object>();
							obj.put("view", v);
							obj.put("uncheckers", idxs);
							obj.put("context", context);
							message.obj = obj;
							sendMessage(message);
						}
					}).start();
	
				// Received : go Result	
				} else if(this.subType == Survey.TYPE_RECEIVED) {
					
					boolean isAnswered = survey.isAnswered(context);
					
					int answeredColor = context.getResources().getColor(R.color.black);
					String answeredStatus = null;
	
					if(isAnswered) {
						answeredStatus 	= context.getString(R.string.statusAnswered);
						answeredColor 	= context.getResources().getColor(R.color.grayDark);
					} else {
						answeredStatus 	= context.getString(R.string.statusNotAnswered);
						answeredColor 	= context.getResources().getColor(R.color.maroon);
					}
					
					Button goResultBT = (Button)v.findViewById(R.id.goResult);
					
					if(isAnswered) {
						
						goResultBT.setOnClickListener( new OnClickListener() {
							@Override
							public void onClick(View v) {
								SurveyResultFragment f = new SurveyResultFragment(survey, subType);
								MainActivity.sharedActivity().pushContent(f);
							}
						});
	
					
					} else {	
					}
					
					goResultBT.setText(answeredStatus);
					goResultBT.setTextColor(answeredColor);
					
	
					
				}
		
			} else if (msg.what == WHAT_UNCHECKERS ) {
				final ArrayList<String> idxs = (ArrayList<String>)obj.get("uncheckers");
				
				Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
				goUnchecked.setText(""+idxs.size());
				goUnchecked.setOnClickListener(new OnClickListener() {
				
					@Override
					public void onClick(View view) {
						
						if(idxs != null) {
							Intent intent = new Intent(context, UserListActivity.class);
							Bundle b = new Bundle();
							b.putStringArrayList(UserListActivity.KEY_USERS_IDX, idxs);
							
							intent.putExtras(b);
							context.startActivity(intent);
						}
					}
				});
			
			
			} else if (msg.what == WHAT_SENDER ) {
				User sender = (User)obj.get("sender");
				TextView senderTV = (TextView)v.findViewById(R.id.sender);
				String senderInfo = sender.department.nameFull +" "+User.RANK[sender.rank]+" " +sender.name;
				senderTV.setText(senderInfo);
			}*/
			
		}
	}
	
	
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	};
	
	
	
	
	// Click Listener
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
			
			ListAdapter adapter = this;
			if(parent.getAdapter() instanceof SimpleSectionAdapter)
				adapter= ((SimpleSectionAdapter)parent.getAdapter());
			
			Cursor c = (Cursor)adapter.getItem(position);
			String surveyIdx = c.getString(c.getColumnIndex(SurveyDAO.COLUMN_SURVEY_IDX));
			
			Survey survey = new Survey(context, surveyIdx);
			
			if(this.subType == Survey.TYPE_RECEIVED) {

				long openTS = (Long)survey.form.get( KEY.SURVEY.OPEN_TS );
				long closeTS = (Long)survey.form.get( KEY.SURVEY.CLOSE_TS );
				long currentTS = System.currentTimeMillis() / 1000;
				
				boolean isAnswered = survey.isAnswered(context);
				boolean isChecked = survey.checked;
				
				boolean isResultPublic = false;
				if( survey.form.containsKey(KEY.SURVEY.IS_RESULT_PUBLIC)) {
					isResultPublic = (Boolean) survey.form.get(KEY.SURVEY.IS_RESULT_PUBLIC);
				} else {
					isResultPublic = true;
				}
				
				if(isAnswered && isResultPublic) {
					Fragment f = new SurveyResultFragment(survey);
					if(f != null)
						MainActivity.sharedActivity().pushContent(f);
					return;
					
					
				} else if(currentTS < openTS) {
					Toast.makeText(context, "아직 설문 기간이 아닙니다.", Toast.LENGTH_SHORT).show();
					
				} else if(currentTS >= openTS && currentTS < closeTS) {

					Fragment f = new SurveyAnswerFragment(survey);
					if(f != null)
						MainActivity.sharedActivity().pushContent(f);
					
				} else if ( currentTS >= closeTS ){
					if ( isAnswered || isChecked) {
						// 자신의 답변을 강조한(TODO) 결과 양식을 보여준다.
						SurveyResultFragment f = new SurveyResultFragment(survey, subType);
						MainActivity.sharedActivity().pushContent(f);
					} else {
						Toast.makeText(context, "설문 기간이 지났습니다.", Toast.LENGTH_SHORT).show();
					}
					
				}

			} else if(this.subType == Survey.TYPE_DEPARTED){
				Fragment f = new SurveyResultFragment(survey);
				if(f != null)
					MainActivity.sharedActivity().pushContent(f);
				return;
			}
						
		}
}
