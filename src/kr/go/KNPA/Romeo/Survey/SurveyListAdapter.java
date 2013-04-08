package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.SurveyProcManager;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
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
	public int type = Survey.NOT_SPECIFIED;
	
	// Constructor
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery) 			{	super(context, c, autoRequery);						}
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery, int type) 	{	super(context, c, autoRequery);	this.type = type;	}
	public SurveyListAdapter(Context context, Cursor c, int flags) 						{	super(context, c, flags);							}

	@Override
	public void bindView(View v, final Context ctx, Cursor c) {
		// TODO
		
		 /* 설문조사 목록 가져오기 */
		DBProcManager.sharedManager(ctx).survey();
		// 설문제목 (String)
		String title = c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_NAME));
		// 설문 문서 해시 (String)
		final String surveyIdx = c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_SENDER_IDX));
		// 보낸사람 해쉬 (String)
		String senderIdx = c.getString(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IDX));
		// 확인여부 (int)
		boolean isChecked = (c.getInt(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_CHECKED)) > 0) ? true : false;
		// 대답여부 (int)
		boolean isAnswered = (c.getInt(c.getColumnIndex(SurveyProcManager.COLUMN_SURVEY_IS_ANSWERED)) > 0)? true : false;
		
		
		// Animation
		Survey
		String openDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_openDT));
		String closeDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_closeDT));
	
		User user = User.getUserWithIdx(surveyIdx);
		String senderInfo = user.department.nameFull +" "+User.RANK[user.rank]+" " +user.name;

		
		TextView titleTV = (TextView)v.findViewById(R.id.title);
		titleTV.setText(title);
		
		TextView senderTV = (TextView)v.findViewById(R.id.sender);
		senderTV.setText(senderInfo);
		
		TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
		openDTTV.setText(openDT);
		
		TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
		closeDTTV.setText(closeDT);
		
		if(this.type == Survey.TYPE_DEPARTED) {
			//LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_departed);
			Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
			final ArrayList<String> idxs = 
					Survey.getUncheckersIdxsWithMessageTypeAndIndex(IGNORE_ITEM_VIEW_TYPE, surveyIdx);
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
			
		} else if(this.type == Survey.TYPE_RECEIVED) {
			//LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_received);
			Button goResultBT = (Button)v.findViewById(R.id.goResult);
			
			String answeredStatus = null;
			int answeredColor = ctx.getResources().getColor(R.color.black);
			if(isAnswered) {
				answeredStatus 	= ctx.getString(R.string.statusAnswered);
				answeredColor 	= ctx.getResources().getColor(R.color.grayDark);
				
				goResultBT.setOnClickListener( new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO : 서버에서 정보 받기??
						SurveyResultFragment f = new SurveyResultFragment(surveyIdx, type);
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
		switch(this.type) {
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
