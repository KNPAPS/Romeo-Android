package kr.go.KNPA.Romeo.Member;


import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.DBManager;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MemberSearch extends Activity {

	// Database
	protected DBManager dbManager;
	protected SQLiteDatabase db;
	
	public MemberListView memberListView;
	public MemberFavoriteListView favoriteListView;
	ConditionalSearch conditionalSearch; 
	
	public static final String KEY_RECEIVERS = "receivers";
	public static final String TYPE_DEPARTMENT = "TYPE_DEPARTMENT";
	public static final String TYPE_USER = "TYPE_USER";
	public static final int REQUEST_CODE = 100;
	
	public String searchResult = ""; 
	
	FrameLayout container;
	public MemberSearch() {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.member_search, null, false);
		
		String titleText = null;
		String lbbText = null, rbbText = null;
		boolean lbbIsVisible = true;
		boolean rbbIsVisible = true;

		titleText = getString(R.string.conditionalSearchTitle);
		lbbText = getString(R.string.cancel);
		rbbText = getString(R.string.done);
		
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbIsVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbIsVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbText);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbText);	}
		
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText(titleText);
		OnClickListener barButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.left_bar_button) {
					cancel();
				} else if(v.getId() == R.id.right_bar_button) {
					result();
				}
			}
		};
		
		if(lbb.getVisibility() == View.VISIBLE) {
			lbb.setOnClickListener(barButtonListener);
		}
		if(rbb.getVisibility() == View.VISIBLE) {
			rbb.setOnClickListener(barButtonListener);
		}
		
		
		OnClickListener tabListener = new OnClickListener() {	
			@Override
			public void onClick(View v) {
				tabClicked(v);
			}
		};
		
		LinearLayout tabBarLL = (LinearLayout)view.findViewById(R.id.tabBar);
		Button tabMemberListBT = (Button)tabBarLL.findViewById(R.id.tabMemberList);
		//Button tabConditionBT = (Button)tabBarLL.findViewById(R.id.tabCondition);// TODO 조건부 검색
		Button tabFavoriteBT = (Button)tabBarLL.findViewById(R.id.tabFavorite);
		
		tabMemberListBT.setOnClickListener(tabListener);
		//tabConditionBT.setOnClickListener(tabListener); // TODO 조건부 검색
		tabFavoriteBT.setOnClickListener(tabListener);
		

		FrameLayout tabContentFL = (FrameLayout)view.findViewById(R.id.tabContent);
		container = tabContentFL;
		
		memberListView = (MemberListView)container.findViewById(R.id.memberListView);
		memberListView.initWithType(MemberManager.TYPE_MEMBERLIST_SEARCH);
		//conditionalSearch = (ConditionalSearch)container.findViewById(R.id.conditionalSearchView); // TODO 조건부 검색
		
		
		favoriteListView = (MemberFavoriteListView)container.findViewById(R.id.favoriteListView);
		LayoutParams flp = favoriteListView.getLayoutParams();
		flp.height = LayoutParams.MATCH_PARENT;
		favoriteListView.setLayoutParams(flp);
		favoriteListView.initWithType(MemberManager.TYPE_FAVORITE_SEARCH);
		setContentView(view);
		
	}
	
	public RomeoListView getListView() {
		return favoriteListView;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if(dbManager == null)	dbManager = new DBManager(MemberSearch.this);
		if(db == null) db = dbManager.getWritableDatabase();
		
		RomeoListView lv = getListView();
		lv.setDatabase(db);
		lv.refresh();
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		if(db != null) {
			db.close();
			db = null;
			
			RomeoListView lv = getListView();
			lv.unsetDatabase();
		}
		
		if(dbManager != null) {
			dbManager.close();
			dbManager = null;
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private void cancel() {
		//Intent intent = new Intent();
		setResult(RESULT_CANCELED);
		finish();
	}
	
	private void result() {
		
		long[] fromMemberList = memberListView.listAdapter.nodeManager.collectInLongArray();
		long[] fromFavoriteList = ((MemberFavoriteListAdapter)favoriteListView.listAdapter).collect();
		
		ArrayList<String> _result = new ArrayList<String>();
		
		if(fromMemberList != null) {
			for(int i=0; i<fromMemberList.length; i++) {
				_result.add(new Long(fromMemberList[i]));
			}
		}
		
		if( fromFavoriteList != null ) {
			for(int i=0; i<fromFavoriteList.length; i++) {
				Long l = new Long(fromFavoriteList[i]);
				if(!_result.contains(l)) {
					_result.add(l);
				}
			}
		}
		
		String[] result = (String[]) _result.toArray();
		
		Bundle b = new Bundle();
		b.putStringArray("receivers", result);
		
		Intent intent = new Intent();
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void tabClicked(View v) {
		View targetView = null;
		switch(v.getId()) {
			case R.id.tabMemberList	: targetView = viewInContainer(R.id.memberListView); 		break;
			//case R.id.tabCondition 	: targetView = viewInContainer(R.id.conditionalSearchView);	break; // TODO 조건부 검색
			case R.id.tabFavorite 	: targetView = viewInContainer(R.id.favoriteListView);		break;
		}
		
		container.bringChildToFront(targetView);
		targetView.invalidate();
	}
	
	private View viewInContainer(int viewId) {
		container.getChildCount();
		if(container == null) return null;
		return container.findViewById(viewId);
	}
	
	
	// 호출 측에서
	/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == //SOME PRE DEFINED VALUE) {
			if(resultCode != RESULT_OK) {
				
			} else {
				data.getExtras().get;
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	*/
	

}
