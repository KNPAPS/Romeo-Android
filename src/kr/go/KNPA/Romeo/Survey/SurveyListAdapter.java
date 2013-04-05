package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
class SurveyListAdapter extends CursorAdapter {
	// Variables
	public int type = Survey.NOT_SPECIFIED;
	
	// Constructor
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery, int type) {
		super(context, c, autoRequery);
		this.type = type;
	}
	
	public SurveyListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		// TODO
		if(this.type == Survey.TYPE_DEPARTED) {
			LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_departed);
			TextView titleTV = (TextView)v.findViewById(R.id.title);
			TextView senderTV = (TextView)v.findViewById(R.id.sender);
			TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
			TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
			Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
			
			String title = "";
			title = c.getString(c.getColumnIndex("title"));
			
			String senderIdx = c.getString(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx(senderIdx);
			String sender = user.department.nameFull +" "+User.RANK[user.rank]+" " +user.name;
			
			long openTS = c.getLong(c.getColumnIndex("openTS"));
			long closeTS = c.getLong(c.getColumnIndex("closeTS"));
			String openDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_openDT));
			String closeDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_closeDT));
			
			titleTV.setText(title);
			senderTV.setText(sender);
			openDTTV.setText(openDT);
			closeDTTV.setText(closeDT);
			
		} else if(this.type == Survey.TYPE_RECEIVED) {
			LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_received);
			TextView titleTV = (TextView)v.findViewById(R.id.title);
			TextView senderTV = (TextView)v.findViewById(R.id.sender);
			TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
			TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
			Button goResultBT = (Button)v.findViewById(R.id.goResult);

			String title = "";
			title = c.getString(c.getColumnIndex("title"));
			
			String senderIdx = c.getString(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx(senderIdx);
			String sender = user.department.nameFull +" "+User.RANK[user.rank]+" " +user.name;
			
			long openTS = c.getLong(c.getColumnIndex("openTS"));
			long closeTS = c.getLong(c.getColumnIndex("closeTS"));
			String openDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_openDT));
			String closeDT = Formatter.timeStampToStringWithFormat(closeTS, ctx.getString(R.string.formatString_closeDT));
			
			titleTV.setText(title);
			senderTV.setText(sender);
			openDTTV.setText(openDT);
			closeDTTV.setText(closeDT);
			
			boolean answered = (c.getInt(c.getColumnIndex("answered")) == 1 ? true : false);
			String answeredStatus = null;
			int answeredColor = ctx.getResources().getColor(R.color.black);
			if(answered) {
				answeredStatus =ctx.getString(R.string.statusAnswered);
				answeredColor = ctx.getResources().getColor(R.color.grayDark);
				Survey survey = new Survey(c);
				goResultBT.setOnClickListener(goResult);
				goResultBT.setTag(survey);
			} else {
				answeredStatus =ctx.getString(R.string.statusNotAnswered);
				answeredColor = ctx.getResources().getColor(R.color.maroon);
			}
			
			goResultBT.setText(answeredStatus);
			goResultBT.setTextColor(answeredColor);
			
		}
		//department.setText(c.getString(c.getColumnIndex("department")));
		//content.setText(c.getString(c.getColumnIndex("content")));

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
	/*
	public Survey getSurvey(int position) {
		Cursor c = (Cursor)getItem(position);
		return new Survey(c);  
	}
	*/
	
	private final OnClickListener goResult = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Survey survey = (Survey)v.getTag();
			SurveyResultFragment f = new SurveyResultFragment(survey, type);// 추가 정보

			MainActivity.sharedActivity().pushContent(f);
		}
	};
}
