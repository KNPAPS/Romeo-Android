package kr.go.KNPA.Romeo;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Member.MemberListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public abstract class RomeoFragment extends Fragment {
	
	// Variables
	public int subType = Message.NOT_SPECIFIED;
	public RomeoListView listView;
	// Constructor
	public RomeoFragment() {}

	public RomeoFragment(int subType) {
		super();
		this.subType = subType;
	}
	
	// ListView management
	abstract public RomeoListView getListView();
	
	
	// Fragment Cycle Management
	// View Life-Cycle
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		
		
		RomeoListView lv = getListView();
		
		if ( lv != null ) {
			lv.refresh();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = init(inflater, container, savedInstanceState);
		return view; 
	}
	
	abstract public View init(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState); 
	
	protected void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
			
		Button lbb = (Button)parentView.findViewById(R.id.left_bar_button);
		Button rbb = (Button)parentView.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbTitle);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbTitle);	}
		
		TextView titleView = (TextView)parentView.findViewById(R.id.title);
		titleView.setText(titleText);
		
		if(lbb.getVisibility() == View.VISIBLE) lbb.setOnClickListener(lbbOnClickListener);
		if(rbb.getVisibility() == View.VISIBLE) rbb.setOnClickListener(rbbOnClickListener);
	}
	
	protected void initNavigationBar(View parentView, int titleTextId, boolean lbbVisible, boolean rbbVisible, int lbbTitleId, int rbbTitleId, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		initNavigationBar(parentView, getString(titleTextId), lbbVisible, rbbVisible, getString(lbbTitleId), getString(rbbTitleId), lbbOnClickListener, rbbOnClickListener);
	}
	
	protected RomeoListView initListViewWithType(int subType, int listViewResourceId, View parentView) {
		if(parentView != null) {
			return ((RomeoListView) parentView.findViewById(listViewResourceId)).initWithType(subType);
		} else {
			return null;
		}
	}
	
	//public static void receive(Message message) {};
	
	private void callMemberSearchActivity() {
		
		Intent intent = new Intent(getActivity(), MemberListActivity.class);
		
		startActivityForResult(intent, MainActivity.MEMBER_SEARCH_ACTIVITY);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MainActivity.MEMBER_SEARCH_ACTIVITY) {
			if(resultCode != MemberListActivity.RESULT_OK) {
				// onError
				Toast.makeText(getActivity(), "Activity Result Error", Toast.LENGTH_SHORT).show();
			} else {
				//data.getExtras().get;
				Toast.makeText(getActivity(), "Activity Result Success", Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
