package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.RoomFragment;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SurveyListView extends ListView implements OnItemClickListener{

	// Database Variables
	private SQLiteDatabase db;
	private String tableName = null;
	
	// Adapter
	public CursorAdapter listAdapter;
	
	// Variables
	public int type = Chat.NOT_SPECIFIED;
	private Context context = null;
	
	
	// Constructor
	public SurveyListView(Context context) {
		this(context, null);
	}

	public SurveyListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SurveyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void setType (int type) {
		this.type = type;
		switch(this.type) {

		case Survey.TYPE_DEPARTED :
		case Survey.TYPE_RECEIVED : tableName = DBManager.TABLE_SURVEY;	break;
		
		default : 
		case Survey.NOT_SPECIFIED :	tableName = null;	break;
		}
		
		if(tableName != null) {
			Cursor c = selectAll();
			if(c.getCount() == 0) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				options.inPreferredConfig = Config.RGB_565;
				
				Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.empty_set_background, options);
				int height = src.getHeight();
				int width = src.getWidth();
				Bitmap resized = Bitmap.createScaledBitmap(src, width/options.inSampleSize, height/options.inSampleSize, true);
				this.setBackgroundDrawable(new BitmapDrawable(getResources(), resized));			} else {
				this.setBackgroundResource(R.color.light);
			}
			SurveyListAdapter surveyListAdapter = new SurveyListAdapter(context, c, false, this.type);		
			this.setAdapter(surveyListAdapter);
			this.setOnItemClickListener(this);
			listAdapter = surveyListAdapter;
		}
		
	}
	
	// Database management
	public void setDatabase(SQLiteDatabase db) {
		this.db = db;
	}
	
	public void unsetDatabase() {
		this.db = null;
	}
	
	protected Cursor selectAll() { 
		String sql = "SELECT * FROM "+this.tableName+" ORDER BY checked desc;"; // sectionizer 를 위해 정렬을 한다.
		Cursor c = db.rawQuery(sql, null);
		return c;
	}

	public void refresh() {
		if(listAdapter == null) return;
		listAdapter.changeCursor(selectAll());		
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			((SimpleSectionAdapter)this.getAdapter()).notifyDataSetChanged();
	}

	// Click Listener
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
		Survey survey = new Survey(c);
			
		SurveyDetailFragment f = new SurveyDetailFragment(survey);	// 추가 정보
		MainActivity.sharedActivity().pushContent(f);
	}
}
