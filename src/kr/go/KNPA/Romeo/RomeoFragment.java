package kr.go.KNPA.Romeo;

import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.DBManager;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Android Fragment Class Wrapper 클래스.\n
 * 애플리케이션에서 사용하는 모든 Fragment는 이 클래스를 상속한다.\n
 * 공통적인 navigation bar를 할당하고, 기타 여러 공통적인 작업을 하기 위해서..
 * @author 채호식
 */
public abstract class RomeoFragment extends Fragment {

	// Database
	protected DBManager dbManager;
	protected SQLiteDatabase db;
	
	// Variables
	public int type = Constants.NOT_SPECIFIED;
	public RomeoListView listView;
	
	// Constructor
	public RomeoFragment() {}

	public RomeoFragment(int type) {
		super();
		this.type = type;
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

		if(dbManager == null)	dbManager = new DBManager(getActivity());
		if(db == null) db = dbManager.getWritableDatabase();
		
		RomeoListView lv = getListView();
		lv.setDatabase(db);
		lv.refresh();
	}

	@Override
	public void onPause() {
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
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View view = init(inflater, container, savedInstanceState);
		return view; 
	}
	
	/**
	 * Fragment initializer
	 * @param inflater 레이아웃 인플레이터
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	public abstract View init(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState); 
	
	/**
	 * Fragment의 내비게이션 바 설정
	 * @param parentView Fragment가 속한 View 객체
	 * @param titleText 내비게이션 바의 Title 부분에 들어갈 문구
	 * @param lbbVisible 내비게이션 바 왼쪽 버튼 출력여부
	 * @param rbbVisible 내비게이션 바 오른쪽 버튼 출력 여부
	 * @param lbbTitle 내비게이션 바 왼쪽 버튼 텍스트
	 * @param rbbTitle 내비게이션 바 오른쪽 버튼 텍스트
	 * @param lbbOnClickListener 왼쪽 버튼 리스너
	 * @param rbbOnClickListener 오른쪽 버튼 리스너
	 */
	protected void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
			
		Button lbb = (Button)parentView.findViewById(R.id.left_bar_button);
		Button rbb = (Button)parentView.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { 
			lbb.setText(lbbTitle);	
		}
		if(rbb.getVisibility() == View.VISIBLE) { 
			rbb.setText(rbbTitle);	
		}
		
		TextView titleView = (TextView)parentView.findViewById(R.id.title);
		titleView.setText(titleText);
		
		if(lbb.getVisibility() == View.VISIBLE) {
			lbb.setOnClickListener(lbbOnClickListener);
		}
		
		if(rbb.getVisibility() == View.VISIBLE) {
			rbb.setOnClickListener(rbbOnClickListener);
		}
	}
	
	/**
	 * 내비게이션 바 만들기. titleText 대신 string.xml에 정의된 문자열의 id를 넘겨받는다
	 * @see initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener)
	 */
	protected void initNavigationBar(View parentView, int titleTextId, boolean lbbVisible, boolean rbbVisible, int lbbTitleId, int rbbTitleId, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		initNavigationBar(parentView, getString(titleTextId), lbbVisible, rbbVisible, getString(lbbTitleId), getString(rbbTitleId), lbbOnClickListener, rbbOnClickListener);
	}
	
	/**
	 * Fragment 내에서 ListView를 만든다.
	 * @param type listview의 종류를 구별할 타입. 종류 구별은 View별로 initWithType(type)를 구현할 때 정한다.
	 * @param listViewResourceId
	 * @param parentView
	 * @return
	 */
	protected RomeoListView initListViewWithType(int type, int listViewResourceId, View parentView) {
		if(parentView != null) {
			return ((RomeoListView) parentView.findViewById(listViewResourceId)).initWithType(type);
		} else {
			return null;
		}
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
				Toast.makeText(getActivity(), "Activity Result Success", Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
