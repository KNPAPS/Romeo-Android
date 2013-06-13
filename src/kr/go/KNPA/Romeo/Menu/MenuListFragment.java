package kr.go.KNPA.Romeo.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.DB.DocuDAO;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserProfileFragment;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Util.CollectionFactory;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MenuListFragment extends ListFragment {

//	private List<Map<String, String>>			gData;
//	private List<List<Map<String, String>>>		cData;
	
	private LinearLayout						userLL;
	private View								searchBar;
	private EditText							searchET;
	private Button								searchBT;
	private Button								cancelSearchBT;
	private ExpandableListView					menuList;
	private ListView							searchList;

	private static MenuListFragment				sharedFragment;

	private SimpleSectionAdapter<MenuListItem>	sectionAdapter;
	private BaseAdapter							emptyAdapter	= 
			new BaseAdapter() {
								@Override	public int getCount()	{	return 0;	}
								@Override	public Object getItem(int arg0)	{	return null;	}
								@Override	public long getItemId(int arg0)	{	return 0;		}
								@Override	public View getView(int arg0, View arg1, ViewGroup arg2)	{	return null;	}
							};

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		sharedFragment = this;

		View v = inflater.inflate(R.layout.menu_list_fragment, null);

		showUserInfo(v);
		List<MenuListItem> menus = ExpandableMenuListAdapter.makeMenuList();

		ExpandableMenuListAdapter adapter = ExpandableMenuListAdapter.getExpandableMenuListAdapter(getActivity(), menus);
		
		menuList = (ExpandableListView) v.findViewById(android.R.id.list);
		menuList.setAdapter(adapter);
		menuList.setOnGroupClickListener(adapter);
		menuList.setOnChildClickListener(adapter);

		// ///////////////
		// Search List //
		// ///////////////
		searchList = (ListView) v.findViewById(R.id.searchList);
		searchList.setAdapter(emptyAdapter);

		// //////////////
		// Search Bar //
		// //////////////

		searchBar = v.findViewById(R.id.search_bar);
		searchET = (EditText) searchBar.findViewById(R.id.edit);
		searchBT = (Button) searchBar.findViewById(R.id.editButton);
		cancelSearchBT = (Button) searchBar.findViewById(R.id.cancel);

		searchBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				setSearchMode(true);
			}
		});

		searchET.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				Log.d("focus", "getFocus");
			}
		});

		searchET.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				Log.d("im", "key pressed");
				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
				{
					if (searchET.getText().toString().trim().length() == 0)
					{
						searchList.setAdapter(emptyAdapter);
					}
					else
					{
						searchResult();
					}
					// searchET.clearFocus(); // 결과가 감춰져버리네
					InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					im.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});

		cancelSearchBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				searchET.setText("");
				searchET.clearFocus();
				searchList.setAdapter(emptyAdapter);
				setSearchMode(false);
			}
		});

		return v;
	}

	public void showUserInfo(View v) {
		// //////////////////////
		// // 간단 프로필 출력!! //
		// ////////////////////
		userLL = (LinearLayout) v.findViewById(R.id.user_profile);
		userLL.setBackgroundResource(R.drawable.menu_user_info_box);

		// 프로필 사진
		ImageView userPicIV = (ImageView) v.findViewById(R.id.userPic);
		new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, UserInfo.getUserIdx(getActivity()), userPicIV);

		// 계급
		TextView rankTV = (TextView) v.findViewById(R.id.rank);
		rankTV.setTextColor(Color.WHITE);
		rankTV.setText(User.RANK[UserInfo.getRank(getActivity())]);

		// 부서
		TextView departmentTV = (TextView) v.findViewById(R.id.department);
		departmentTV.setText(UserInfo.getDepartment(getActivity()));
		departmentTV.setTextColor(Color.WHITE);

		// 이름
		TextView nameTV = (TextView) v.findViewById(R.id.name);
		nameTV.setText(UserInfo.getName(getActivity()));
		nameTV.setTextColor(Color.WHITE);

		// 직책
		TextView roleTV = (TextView) v.findViewById(R.id.role);
		roleTV.setText("");
		roleTV.setTextColor(Color.WHITE);

		userLL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				UserProfileFragment profile = new UserProfileFragment();
				MainActivity.sharedActivity().switchContent(profile);
			}
		});

	}
	
//	public void makeMenuList()
//	{
//		// /////////////
//		// MENU LIST //
//		// /////////////
//
//		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
//		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
//
//		groupData.add(CollectionFactory.hashMapWithKeysAndStrings("section", getString(R.string.memberListTitle), "iconImage", "" + R.drawable.icon_people, "code", "member"));
//		groupData.add(CollectionFactory.hashMapWithKeysAndStrings("section", getString(R.string.documentTitle), "iconImage", "" + R.drawable.icon_document, "code", "document"));
//		groupData.add(CollectionFactory.hashMapWithKeysAndStrings("section", getString(R.string.surveyTitle), "iconImage", "" + R.drawable.icon_pie_graph, "code", "survey"));
//		groupData.add(CollectionFactory.hashMapWithKeysAndStrings("section", getString(R.string.commandTitle), "iconImage", "" + R.drawable.icon_arrow_side, "code", "chat:Command"));
//		groupData.add(CollectionFactory.hashMapWithKeysAndStrings("section", getString(R.string.meetingTitle), "iconImage", "" + R.drawable.icon_circle, "code", "chat:Meeting"));
//		groupData.add(CollectionFactory.hashMapWithKeysAndStrings("section", getString(R.string.libraryTitle), "iconImage", "" + R.drawable.icon_folder, "code", "library"));
//		groupData.add(CollectionFactory.hashMapWithKeysAndStrings("section", getString(R.string.settingsTitle), "iconImage", "" + R.drawable.icon_gear, "code", "settings"));
//		
//		List<Map<String, String>> l = null;
//		l = new ArrayList<Map<String, String>>();
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.memberListTitle), "iconImage", "" + R.drawable.icon_people, "code", "member:MemberList"));
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.memberFavoriteTitle), "iconImage", "" + R.drawable.icon_star, "code", "member:Favorite"));
//		childData.add(l);
//
//		l = new ArrayList<Map<String, String>>();
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.documentFavoriteTitle), "iconImage", "" + R.drawable.icon_document_star, "code", "document:Favorite"));
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.documentReceivedTitle), "iconImage", "" + R.drawable.icon_document_received, "code", "document:Received"));
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.documentDepartedTitle), "iconImage", "" + R.drawable.icon_document_departed, "code", "document:Departed"));
//		childData.add(l);
//
//		l = new ArrayList<Map<String, String>>();
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.surveyReceivedTitle), "iconImage", "" + R.drawable.icon_pie_graph_received, "code", "survey:Received"));
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.surveyDepartedTitle), "iconImage", "" + R.drawable.icon_pie_graph_departed, "code", "survey:Departed"));
//		childData.add(l);
//
//		l = new ArrayList<Map<String, String>>();
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.commandTitle), "iconImage", "" + R.drawable.icon_chat, "code", "chat:Command"));
//		childData.add(l);
//
//		l = new ArrayList<Map<String, String>>();
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", getString(R.string.meetingTitle), "iconImage", "" + R.drawable.icon_circle, "code", "chat:Meeting"));
//		childData.add(l);
//
//		l = new ArrayList<Map<String, String>>();
//
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"집회시위 현장매뉴얼", 		"iconImage", ""+R.drawable.icon_folder, 			"code", "library:HandBook"));
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"4대 사회악 근절 전담부대 매뉴얼", 		"iconImage", ""+R.drawable.icon_folder, 			"code", "library:SocialEvil"));
//
//		childData.add(l);
//
//		l = new ArrayList<Map<String, String>>();
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", "설정", "iconImage", "" + R.drawable.indentation, "code", "settings"));
//		childData.add(l);
//		
//		// TODO : BugReport
//		groupData.add(CollectionFactory.hashMapWithKeysAndStrings("section", "버그리포트", "iconImage", "" + R.drawable.icon_mail, "code", "bug:report"));
//		l = new ArrayList<Map<String, String>>();
//		l.add(CollectionFactory.hashMapWithKeysAndStrings("title", "버그 리포트", "iconImage", "" + R.drawable.indentation, "code", "bug:report"));
//		childData.add(l);
//		
//		l = null;
//
//		this.gData = groupData;
//		this.cData = childData;
//	}

		/**
	 * @name Switch between Search - Menu Modes 
	 * 메뉴모드/검색모드 간의 전환에 필요한 메서드들
	 * @{
	 */
	public static void setMode(boolean willSearchMode) {
		if (sharedFragment != null)
			sharedFragment.setSearchMode(willSearchMode);
	}

	private void setSearchMode(boolean willSearchMode) {
		InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (willSearchMode == true)
		{
			cancelSearchBT.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.VISIBLE);
			menuList.setVisibility(View.INVISIBLE);

			if (searchET != null)
				searchET.requestFocus();
			im.showSoftInput(searchET, InputMethod.SHOW_EXPLICIT);
		}
		else
		{
			cancelSearchBT.setVisibility(View.GONE);
			searchList.setVisibility(View.INVISIBLE);
			menuList.setVisibility(View.VISIBLE);

			if (searchET != null)
				searchET.clearFocus();

			im.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
		}
	}
	/** @} */
	
	/**
	 *  @name Search
	 *  @{
	 */
	private void searchResult()
	{
		final String keyword = searchET.getText().toString().trim();
		WaiterView.showDialog(getActivity());
		new Thread(new Runnable() {

			@Override
			public void run()
			{
				IntergratedSearchListAdatper.searchResult(getActivity(), keyword, searchList);

			}
		}).start();

	}

	/** @} */
}