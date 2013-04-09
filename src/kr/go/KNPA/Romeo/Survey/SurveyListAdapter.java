package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.SurveyProcManager;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Survey.Survey.Form;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
	
	// Constructor
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery) 				{	super(context, c, autoRequery);						}
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery, int subType) 	{	super(context, c, autoRequery);	this.subType = subType;	}
	public SurveyListAdapter(Context context, Cursor c, int flags) 							{	super(context, c, flags);							}

	@Override
	public void bindView(View v, final Context ctx, final Cursor c) {
		
		// Animation	// TODO
		Survey survey = new Survey(ctx, c);
		
		User user = User.getUserWithIdx(survey.senderIdx);
		String senderInfo = user.department.nameFull +" "+User.RANK[user.rank]+" " +user.name;
		TextView senderTV = (TextView)v.findViewById(R.id.sender);
		senderTV.setText(senderInfo);
		
		TextView titleTV = (TextView)v.findViewById(R.id.title);
		titleTV.setText(survey.title);
		
		String openDT = Formatter.timeStampToStringWithFormat((Long)survey.form.get(KEY.SURVEY.OPEN_TS), ctx.getString(R.string.formatString_openDT));
		TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
		openDTTV.setText(openDT);
		
		String closeDT = Formatter.timeStampToStringWithFormat((Long)survey.form.get(KEY.SURVEY.CLOSE_TS), ctx.getString(R.string.formatString_closeDT));
		TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
		closeDTTV.setText(closeDT);
		
		
		if(this.subType == Survey.TYPE_DEPARTED) {
			//LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_departed);
			Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
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
			
		} else if(this.subType == Survey.TYPE_RECEIVED) {
			//LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_received);
			Button goResultBT = (Button)v.findViewById(R.id.goResult);
			
			String answeredStatus = null;
			int answeredColor = ctx.getResources().getColor(R.color.black);
			if(survey.isAnswered(ctx)) {	// TODO
				answeredStatus 	= ctx.getString(R.string.statusAnswered);
				answeredColor 	= ctx.getResources().getColor(R.color.grayDark);
				
				goResultBT.setOnClickListener( new OnClickListener() {
					@Override
					public void onClick(View v) {
						Survey survey = new Survey(ctx, c);
						
						SurveyResultFragment f = new SurveyResultFragment(survey, subType);
						MainActivity.sharedActivity().pushContent(f);
					}
				});
				
			} else {
				answeredStatus 	= ctx.getString(R.string.statusNotAnswered);
				answeredColor 	= ctx.getResources().getColor(R.color.maroon);
			}
			
			goResultBT.setText(answeredStatus);
			goResultBT.setTextColor(answeredColor);
			
		}
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

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	};
}
