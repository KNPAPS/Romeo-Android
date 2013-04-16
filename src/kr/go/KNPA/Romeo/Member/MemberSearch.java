package kr.go.KNPA.Romeo.Member;


import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBManager;

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
	
	public MemberListView memberListView;
	public MemberFavoriteListView favoriteListView;
	ConditionalSearch conditionalSearch; 
	
	public static final String TYPE_DEPARTMENT = "TYPE_DEPARTMENT";
	public static final String TYPE_USER = "TYPE_USER";
	public static final int REQUEST_CODE = 100;
	public static final String KEY_RESULT_USERS_IDX = "receivers";
	// return ArrayList<String> idxs
	
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
		memberListView.initWithType(User.TYPE_MEMBERLIST_SEARCH);
		//conditionalSearch = (ConditionalSearch)container.findViewById(R.id.conditionalSearchView); // TODO 조건부 검색
		
		
		favoriteListView = (MemberFavoriteListView)container.findViewById(R.id.favoriteListView);
		LayoutParams flp = favoriteListView.getLayoutParams();
		flp.height = LayoutParams.MATCH_PARENT;
		favoriteListView.setLayoutParams(flp);
		favoriteListView.initWithType(User.TYPE_FAVORITE_SEARCH);
		setContentView(view);
		
	}
	
	public RomeoListView getListView() {
		return favoriteListView;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		RomeoListView lv = getListView();
		lv.refresh();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
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
		ArrayList<String> fromMemberList = CellNode.collect(memberListView.listAdapter.rootNode());
		ArrayList<String> fromFavoriteList = ((MemberFavoriteListAdapter)favoriteListView.listAdapter).collect();
		
		ArrayList<String> result = new ArrayList<String>(fromMemberList.size()+fromFavoriteList.size());
		result.addAll(fromMemberList);
		for(int i=0; i<fromFavoriteList.size(); i++) {
			if(result.contains(fromFavoriteList.get(i)));
				result.add(fromFavoriteList.get(i));
		}
		//result.addAll(fromFavoriteList);
		
		if(result.size() < 1) {
			Toast.makeText(this, "수신자가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
			return ; 
		}
		
		Bundle b = new Bundle();
		b.putStringArrayList(KEY_RESULT_USERS_IDX, result);
		
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

}
