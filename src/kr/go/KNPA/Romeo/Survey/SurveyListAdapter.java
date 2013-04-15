package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager.SurveyProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
	private final int WHAT_TITLE = 0;
	private final int WHAT_SENDER = 1;
	private final int WHAT_OPENDT = 2;
	private final int WHAT_CLOSEDT = 3;
	private final int WHAT_UNCHECKERS = 4;
	private final int WHAT_IS_ANSWERED = 5;
	
	// Constructor
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery) 				{	super(context, c, autoRequery);						}
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery, int subType) 	{	super(context, c, autoRequery);	this.subType = subType;	}
	public SurveyListAdapter(Context context, Cursor c, int flags) 							{	super(context, c, flags);							}

	@Override
	public void bindView(View v, final Context ctx, final Cursor c) {
		
		// Animation	// TODO
		
		final View _v = v;
		WaiterView.showDialog(ctx);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final Survey survey = new Survey(ctx, c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IDX)));
				
				// USER Thread
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						User user = User.getUserWithIdx(survey.senderIdx);
						
						android.os.Message message = surveyHandler.obtainMessage();
						message.what = WHAT_SENDER;
						message.obj = _v;
						Bundle b = new Bundle();
						b.putString("senderInfo", user.department.nameFull +" "+User.RANK[user.rank]+" " +user.name );
						surveyHandler.sendMessage(message);
					}
				});
				
				// Other
				android.os.Message message = surveyHandler.obtainMessage();
				message.obj = _v;
				
				Bundle b = new Bundle();
				
				message.what = WHAT_TITLE;
				message.setData(b);
				b.putString("title", survey.title);
				surveyHandler.sendMessage(message);
				b.clear();
				
				message.what = WHAT_OPENDT;
				message.setData(b);
				b.putString("openDT", Formatter.timeStampToStringWithFormat((Long)survey.form.get(KEY.SURVEY.OPEN_TS), ctx.getString(R.string.formatString_openDT)) );
				surveyHandler.sendMessage(message);
				b.clear();
				
				message.what = WHAT_CLOSEDT;
				message.setData(b);
				b.putString("closeDT", Formatter.timeStampToStringWithFormat((Long)survey.form.get(KEY.SURVEY.CLOSE_TS), ctx.getString(R.string.formatString_closeDT)) );
				surveyHandler.sendMessage(message);
				b.clear();
				
				

				if(subType == Survey.TYPE_DEPARTED) {
					//LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_departed);
					Button goUnchecked = (Button)_v.findViewById(R.id.goUnchecked);
					// TODO
					final ArrayList<String> idxs = 
							Survey.getUncheckersIdxsWithMessageTypeAndIndex(IGNORE_ITEM_VIEW_TYPE, survey.idx);
					goUnchecked.setText(""+idxs.size());
					goUnchecked.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {					
							Intent intent = new Intent(ctx, UserListActivity.class);
							Bundle b = new Bundle();
							b.putStringArrayList(UserListActivity.KEY_USERS_IDX, idxs);
							
							intent.putExtras(b);
							ctx.startActivity(intent);
						}
					});
					
				} else if(subType == Survey.TYPE_RECEIVED) {
					boolean isAnswered = survey.isAnswered(ctx);
					
					int answeredColor = ctx.getResources().getColor(R.color.black);
					String answeredStatus = null;

					if(isAnswered) {
						answeredStatus 	= ctx.getString(R.string.statusAnswered);
						answeredColor 	= ctx.getResources().getColor(R.color.grayDark);
					} else {
						answeredStatus 	= ctx.getString(R.string.statusNotAnswered);
						answeredColor 	= ctx.getResources().getColor(R.color.maroon);
					}
					
					b.clear();
					b.putBoolean("isAnswered", isAnswered);
					b.putInt("answeredColor", answeredColor);
					b.putString("answeredStatus", answeredStatus);
					b.putParcelable("survey", survey);
					surveyHandler.sendMessage(message);
					b.clear();
				}
		
		
		
			}
		});
		
		
		WaiterView.dismissDialog(ctx);
	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
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

	Handler surveyHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			View v = (View) msg.obj;
			
			switch(msg.what) {
			case WHAT_TITLE : 
				String title = msg.getData().getString("title"); 
				TextView titleTV = (TextView)v.findViewById(R.id.title);
				titleTV.setText(title);
				break;
			case WHAT_SENDER :
				String senderInfo = msg.getData().getString("senderInfo");
				TextView senderTV = (TextView)v.findViewById(R.id.sender);
				senderTV.setText(senderInfo);
				break;
			case WHAT_OPENDT :
				String openDT = msg.getData().getString("openDT");
				TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
				openDTTV.setText(openDT);
				break;
			case WHAT_CLOSEDT :
				String closeDT = msg.getData().getString("closeDT");
				TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
				closeDTTV.setText(closeDT);
				break;
				
			case WHAT_UNCHECKERS : 
				break;
			case WHAT_IS_ANSWERED : 
				//LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_received);
				boolean isAnswered = msg.getData().getBoolean("isAnswered");
				String answeredStatus = msg.getData().getString("answeredStatus");
				int answeredColor = msg.getData().getInt("answeredColor");
				final Survey survey = msg.getData().getParcelable("survey");
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
				
				break;
			}
			
			
		};
	};
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	};
}
