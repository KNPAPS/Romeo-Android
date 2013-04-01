/**
 * 
 */
package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.DBManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

/**
 * @author pong0923
 *
 */
public class MemberFragment extends RomeoFragment {
	
	// preDefined Constants
	public static final int NOT_SPECIFIED = -777;
	public static final int TYPE_MEMBERLIST = 0;
	public static final int TYPE_FAVORITE = 1;
	public static final int TYPE_MEMBERLIST_SEARCH = 10;
	public static final int TYPE_FAVORITE_SEARCH = 11;
	
	public static boolean showIntroView = false;
	
	// Constructor
	public MemberFragment() {
		this(TYPE_MEMBERLIST);
	}
	
	public MemberFragment(int type) {
		super(type);
	}
	
	// Manage List View
	public RomeoListView getListView() {
		View view = ((ViewGroup)getView());
		RomeoListView lv = null;
		
		if(view!=null)
			lv = (RomeoListView)view.findViewById(R.id.memberListView);
		
		return lv;
	}
	

	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		OnClickListener lbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		OnClickListener rbbOnClickListener = new OnClickListener() {		
			@Override
			public void onClick(View v) {
				callMemberSearchActivity();
			}
		};
		
		View view = null;
		switch(this.type) {
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
			
			listView = (MemberFavoriteListView)initListViewWithType(this.type, R.id.memberListView, view);
			
			break;
			
		case TYPE_MEMBERLIST :
		case TYPE_MEMBERLIST_SEARCH :	
			view = inflater.inflate(R.layout.member_fragment, container, false);
			
			// IntroView
			if(showIntroView == true ) {
				ImageView introView = new ImageView(getActivity());
				introView.setTag("intro");
				LinearLayout.LayoutParams lp = (LayoutParams) introView.getLayoutParams();
				if(lp == null) lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				introView.setLayoutParams(lp);
				introView.setImageResource(R.drawable.intro);
				((LinearLayout)view).addView(introView, 0);
			}
			
			
			initNavigationBar(
					view, 
					R.string.memberListTitle, 
					true, 
					false, 
					R.string.menu, 
					R.string.dummy, 
					lbbOnClickListener, rbbOnClickListener);
			
			Handler h = new Handler();
			final int _t = this.type;
			final View _v = view;
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					try {
						listView = (MemberListView)initListViewWithType(_t, R.id.memberListView, _v);
					} catch (RuntimeException e) {
						Toast.makeText(getActivity(), "통신 오류가 발생했습니다.", Toast.LENGTH_SHORT);
						// TODO : getMembers 통신 오류 등이다. 다시 로드 할 수 있도록 조치를 취해야 하는데..
					}
				}
			};
			
			h.postDelayed(r,100);
			break;
		}
		

		
		return view;
	}


	private void callMemberSearchActivity() {
		
		Intent intent = new Intent(getActivity(), MemberSearch.class);
		
		startActivityForResult(intent, MainActivity.MEMBER_SEARCH_ACTIVITY);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MainActivity.MEMBER_SEARCH_ACTIVITY) {
			if(resultCode != MemberSearch.RESULT_OK) {
				// onError
				Toast.makeText(getActivity(), "Activity Result Error", Toast.LENGTH_SHORT).show();
			} else {
				//data.getExtras().get;
				Bundle b = data.getExtras();
				long[] _users = b.getLongArray("users");
				StringBuilder sb = new StringBuilder();
				
				for(int i=0; i<_users.length; i++) {
					sb.append(_users[i]);
					if(i!=(_users.length-1)) {
						sb.append(":");
					}
				}
				String users = sb.toString();
				
				DBManager dbManager = new DBManager(getActivity());
				SQLiteDatabase db = dbManager.getWritableDatabase();	
				Cursor c = null;
				// 정렬을 했다면, 무결성. TODO 
				String sql = "SELECT * FROM "+DBManager.TABLE_MEMBER_FAVORITE+" WHERE idxs=\""+users+"\";";
				c = db.rawQuery(sql, null);
				
				int isGroup = (_users.length > 1 ? 1 : 0);
				if(!(c.getCount() > 0)) {
					ContentValues cv = new ContentValues();
					cv.put("idxs", users);
					cv.put("isGroup", isGroup);
					cv.put("TS", System.currentTimeMillis());
					db.insert(DBManager.TABLE_MEMBER_FAVORITE, null, cv);
				}
				
				if(c != null) c.close();
				db.close();
				dbManager.close();
				
				Toast.makeText(getActivity(), "Activity Result Success", Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
}
