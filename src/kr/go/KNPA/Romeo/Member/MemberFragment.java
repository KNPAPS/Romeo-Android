/**
 * 
 */
package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Member.MemberListView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class MemberFragment extends RomeoFragment {
	
	// preDefined Constants
	public static final int NOT_SPECIFIED = -777;
	public static final int TYPE_MEMBERLIST = 0;
	public static final int TYPE_FAVORITE = 1;
	public static final int TYPE_MEMBERLIST_SEARCH = 10;
	public static final int TYPE_FAVORITE_SEARCH = 11;
	
	private static HashMap<Integer, MemberFragment> _sharedMemberFragments;
	
	// Constructor
	public MemberFragment() 			{	memberFragment(TYPE_MEMBERLIST);	}
	public MemberFragment(int type) 	{	super(type);			}
	public static MemberFragment memberFragment(int type) {
		if(_sharedMemberFragments == null)
			_sharedMemberFragments = new HashMap<Integer, MemberFragment>();
		if(_sharedMemberFragments.containsKey(type) == false)
			_sharedMemberFragments.put(type, new MemberFragment(type));
		return _sharedMemberFragments.get(type);
	}
	
	// Manage List View
	public RomeoListView getListView() {
		View view = ((ViewGroup)getView());
		RomeoListView lv = null;
		
		if(view!=null)
			lv = (RomeoListView)view.findViewById(R.id.memberListView);
		
		return lv;
	}
	
	public void setExeptionList(ArrayList<String> exeptionList) {
		((MemberListView)(this.listView)).listAdapter.setExeptionList(exeptionList);
	}
	
	public void unsetExeptionList() {
		((MemberListView)(this.listView)).listAdapter.unsetExeptionList();
	}

	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		// BarButton Click Listners
		OnClickListener lbbOnClickListener = new OnClickListener() {		@Override	public void onClick(View v) {	MainActivity.sharedActivity().toggle();	}	};
		OnClickListener rbbOnClickListener = new OnClickListener() {		@Override	public void onClick(View v) {	startMemberSearchActivity();			}	};
		
		
		View view = null;
		switch(this.subType) {
		case TYPE_FAVORITE :
		case TYPE_FAVORITE_SEARCH :
			view = inflater.inflate(R.layout.member_favorite_fragment, container, false);
			initNavigationBar(
							view, 
							R.string.memberFavoriteTitle, 
							true, 
							true, 
							R.string.menu, 
							R.string.add, 
							lbbOnClickListener, rbbOnClickListener);
			
			listView = (MemberFavoriteListView)initListViewWithType(this.subType, R.id.memberListView, view);
			
			break;
			
		case TYPE_MEMBERLIST :
		case TYPE_MEMBERLIST_SEARCH :	
			view = inflater.inflate(R.layout.member_fragment, container, false);
			
			/*
			// IntroView
			if(showIntroView == true ) {
				ImageView introView = new ImageView(getActivity());
				introView.setTag("intro");
				LinearLayout.LayoutParams lp = (LayoutParams) introView.getLayoutParams();
				if(lp == null) lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				introView.setLayoutParams(lp);
				introView.setImageResource(R.drawable.intro);
				((LinearLayout)view).addView(introView, 0);
			}*/
			
			
			initNavigationBar(
					view, 
					R.string.memberListTitle, 
					true, 
					false, 
					R.string.menu, 
					R.string.dummy, 
					lbbOnClickListener, rbbOnClickListener);
				
			listView = (MemberListView)initListViewWithType(this.subType, R.id.memberListView, view);
			//int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getActivity().getResources().getDisplayMetrics());
			//listView.scrollTo(0, px);
			
			break;
		}
		

		
		return view;
	}


	private void startMemberSearchActivity() {
		Intent intent = new Intent(getActivity(), MemberSearch.class);
		startActivityForResult(intent, MainActivity.MEMBER_SEARCH_ACTIVITY);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MainActivity.MEMBER_SEARCH_ACTIVITY) {
			if(resultCode != MemberSearch.RESULT_OK) {
				// onError
				Toast.makeText(getActivity(), "선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
			} else {
				//data.getExtras().get;
				Bundle b = data.getExtras();
				ArrayList<String> usersIdx = b.getStringArrayList(MemberSearch.KEY_RESULT_USERS_IDX);
				
				if(usersIdx == null || usersIdx.size() < 1) {
					Toast.makeText(getActivity(), "선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
					
				if(usersIdx.size() == 1 && DBProcManager.sharedManager(getActivity()).member().isUserFavorite(usersIdx.get(0))) {
					DBProcManager.sharedManager(getActivity()).member().setFavorite(usersIdx.get(0), true);
				} else {
					DBProcManager.sharedManager(getActivity()).member().addFavoriteGroup(usersIdx);
				}
							
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
}
