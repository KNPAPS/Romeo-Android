package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager.SurveyProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
class SurveyListAdapter extends CursorAdapter {
	// Variables
	public int subType = Survey.NOT_SPECIFIED;
	
	private static final int WHAT_SURVEY = 0;
	private static final int WHAT_SENDER = 1;
	private static final int WHAT_UNCHECKERS = 2;
	
	// Constructor
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery) 				{	super(context, c, autoRequery);						}
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery, int subType) 	{	super(context, c, autoRequery);	this.subType = subType;	}
	public SurveyListAdapter(Context context, Cursor c, int flags) 							{	super(context, c, flags);							}

	@Override
	public void bindView(final View v, final Context context, final Cursor c) {
		// Animation	// TODO
		
		final Handler surveyHandler = new SurveyHandler(subType);
		final Survey survey = new Survey(context, c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IDX)));
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				android.os.Message message = surveyHandler.obtainMessage();
				message.what = WHAT_SURVEY;
				HashMap<String, Object> obj = new HashMap<String, Object>();
				obj.put("survey", survey);
				obj.put("view", v);
				obj.put("context", context);
				message.obj = obj;
				surveyHandler.sendMessage(message);
						
			}
		}).start();
		
		
		
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
			
			HashMap<String, Object> obj = (HashMap<String, Object>)msg.obj; 
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
							ArrayList<String> idxs = Survey.getUncheckersIdxsWithMessageTypeAndIndex(IGNORE_ITEM_VIEW_TYPE, survey.idx);
							
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
			}
			
		}
	}
	
	
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	};
}
