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
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.DocumentProcManager;
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
	
	private List<Map<String, String>> gData;
	private List<List<Map<String, String>>> cData;
	
	private LinearLayout userLL;
	private View searchBar;
	private EditText searchET;
	private Button searchBT;
	private Button cancelSearchBT;
	private ExpandableListView menuList;
	private ListView searchList;
	
	private static MenuListFragment sharedFragment;
	
	private SimpleSectionAdapter<MenuListItem> sectionAdapter; 
	private BaseAdapter emptyAdapter = new BaseAdapter() {	
		@Override	public int getCount() {	return 0;	}
		@Override	public Object getItem(int arg0) {	return null;	}
		@Override	public long getItemId(int arg0) {	return 0;	}
		@Override	public View getView(int arg0, View arg1, ViewGroup arg2) {	return null; }	
	};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		sharedFragment = this;
		
		View v = inflater.inflate(R.layout.menu_list_fragment, null);
		
		////////////////////////
		//// 간단 프로필 출력!!	//
		//////////////////////
		userLL = (LinearLayout) v.findViewById(R.id.user_profile);
		userLL.setBackgroundResource(R.drawable.menu_user_info_box);
		
		ImageView userPicIV = (ImageView) v.findViewById(R.id.userPic);
		new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, UserInfo.getUserIdx(getActivity()), userPicIV);
		
		TextView rankTV = (TextView)v.findViewById(R.id.rank);
		rankTV.setTextColor(Color.WHITE);
		rankTV.setText(User.RANK[UserInfo.getRank(getActivity())]);
		
		TextView departmentTV = (TextView)v.findViewById(R.id.department);
		departmentTV.setText(UserInfo.getDepartment(getActivity()));
		departmentTV.setTextColor(Color.WHITE);
		
		TextView nameTV = (TextView)v.findViewById(R.id.name);
		nameTV.setText(UserInfo.getName(getActivity()));
		nameTV.setTextColor(Color.WHITE);
		
		TextView roleTV = (TextView)v.findViewById(R.id.role);
		roleTV.setText("");
		roleTV.setTextColor(Color.WHITE);
		
		userLL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserProfileFragment profile = new UserProfileFragment();
				MainActivity.sharedActivity().switchContent(profile);
			}
		});
		
		makeMenuList();
		
		ExpandableMenuListAdapter adapter = 
				new ExpandableMenuListAdapter(
						getActivity(),
						
						gData, 
						R.layout.menu_list_cell_section_unfolded, 
						R.layout.menu_list_cell_section_folded, 
						new String[] {"section"},//, "iconImage"}, 
						new int[] { R.id.title },//, R.id.cell_icon}, 
						
						cData, 
						R.layout.menu_list_cell_item, 
						new String[] {"title", "code"},// "iconImage" } , 
						new int[] {R.id.title, R.id.code}// R.id.cell_icon,}
					);
		

		menuList = (ExpandableListView)v.findViewById(android.R.id.list);
		menuList.setAdapter(adapter);
		menuList.setOnGroupClickListener(adapter);
		menuList.setOnChildClickListener(adapter);
		
		
		/////////////////
		// Search List //
		/////////////////
		searchList = (ListView)v.findViewById(R.id.searchList);
		searchList.setAdapter(emptyAdapter);
		
		////////////////
		// Search Bar //
		////////////////
		
		searchBar = v.findViewById(R.id.search_bar);
		searchET = (EditText)searchBar.findViewById(R.id.edit);
		searchBT = (Button)searchBar.findViewById(R.id.editButton);
		cancelSearchBT = (Button)searchBar.findViewById(R.id.cancel);

		searchBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setSearchMode(true);
			}
		});
	
		searchET.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.d("focus", "getFocus");
			}
		});
		
		searchET.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("im", "key pressed");
				if(event.getAction() == KeyEvent.ACTION_UP && 
						keyCode == KeyEvent.KEYCODE_ENTER) {
					if(searchET.getText().toString().trim().length() == 0) {
						searchList.setAdapter(emptyAdapter);
					} else {
						searchResult();
					}
					//searchET.clearFocus(); // 결과가 감춰져버리네
					InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					im.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
		
		cancelSearchBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchET.setText("");
				searchET.clearFocus();
				searchList.setAdapter(emptyAdapter);
				setSearchMode(false);
			}
		});
		
		
		return v;
	}

	public void makeMenuList() {
		///////////////
		// MENU LIST //
		///////////////
			
			
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		  
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	getString(R.string.memberListTitle), 		"iconImage", ""+R.drawable.icon_people,			"code", "member") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	getString(R.string.commandTitle), 			"iconImage", ""+R.drawable.icon_arrow_side,		"code",	"chat:Command") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	getString(R.string.meetingTitle), 			"iconImage", ""+R.drawable.icon_circle,			"code", "chat:Meeting" ) );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	getString(R.string.documentTitle), 			"iconImage", ""+R.drawable.icon_document,		"code",	"document") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	getString(R.string.surveyTitle),		 	"iconImage", ""+R.drawable.icon_pie_graph,		"code",	"survey") ); 
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	getString(R.string.libraryTitle), 			"iconImage", ""+R.drawable.icon_folder,			"code", "library") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	getString(R.string.settingsTitle), 			"iconImage", ""+R.drawable.icon_gear,			"code",	"settings") );
		  
		  
		List<Map<String, String>> l = null;
		  
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.memberListTitle),		 	"iconImage", ""+R.drawable.icon_people, 						"code", "member:MemberList"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.memberFavoriteTitle),	 	"iconImage", ""+R.drawable.icon_star, 							"code", "member:Favorite"));
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.commandTitle), 				"iconImage", ""+R.drawable.icon_chat, 							"code", "chat:Command")); 
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.meetingTitle),	 			"iconImage", ""+R.drawable.icon_circle, 						"code", "chat:Meeting")); 
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.documentFavoriteTitle), 		"iconImage", ""+R.drawable.icon_document_star, 					"code", "document:Favorite"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.documentReceivedTitle), 		"iconImage", ""+R.drawable.icon_document_received, 				"code", "document:Received"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.documentDepartedTitle), 		"iconImage", ""+R.drawable.icon_document_departed, 				"code", "document:Departed"));
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.surveyReceivedTitle),	 	"iconImage", ""+R.drawable.icon_pie_graph_received, 			"code", "survey:Received"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		getString(R.string.surveyDepartedTitle),	 	"iconImage", ""+R.drawable.icon_pie_graph_departed, 			"code", "survey:Departed"));
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"집회시위 현장매뉴얼", 		"iconImage", ""+R.drawable.icon_folder, 			"code", "library:HandBook"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"4대 사회악 근절 전담부대 매뉴얼", 		"iconImage", ""+R.drawable.icon_folder, 			"code", "library:SocialEvil"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"샘플 EBook 뷰어", 		"iconImage", ""+R.drawable.icon_folder, 			"code", "library:ebook"));
		childData.add(l);
		
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"설정", 			"iconImage", ""+R.drawable.indentation, 	"code", "settings"));
		childData.add(l);	l= null;
	  
		
		this.gData = groupData;
		this.cData = childData;
	}
	
	public static void setMode(boolean willSearchMode) {
		if ( sharedFragment != null)
			sharedFragment.setSearchMode(willSearchMode);
	}
	
	private void setSearchMode(boolean willSearchMode) {
		InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if(willSearchMode == true) {
			cancelSearchBT.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.VISIBLE);
			menuList.setVisibility(View.INVISIBLE);
			
			if(searchET != null)
				searchET.requestFocus();
			im.showSoftInput(searchET, InputMethod.SHOW_EXPLICIT);
		} else {
			cancelSearchBT.setVisibility(View.GONE);
			searchList.setVisibility(View.INVISIBLE);
			menuList.setVisibility(View.VISIBLE);
			
			if(searchET != null)
				searchET.clearFocus();
			
			im.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
		}
	}
	
	private void searchResult() {
		final String keyword = searchET.getText().toString().trim();
		WaiterView.showDialog(getActivity());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				searchResult(keyword);
				
			}
		}).start();
		
	}
	
	private void searchResult(String keyword) {
		// 각 섹션별로 정보 얻어오기
		// 얻어온 정보들을 ArrayList로 만들기
		
		ArrayList<User> resUsers = searchInMembers(keyword);
		ArrayList<Document> resDocs = searchInDocuments(keyword);
		
		
		// SimpleSectionList
		
		BaseAdapter listAdapter = new IntergratedSearchListAdatper(getActivity(), keyword, resUsers, resDocs, null);
		
		Sectionizer<Object> sectionizer = new Sectionizer<Object>() {

			@Override
			public String getSectionTitleForItem(Object obj) {
				String sectionTitle = "";
				if(obj instanceof User) {
					sectionTitle = "사용자";
				} else if(obj instanceof Document) {
					sectionTitle = getString(R.string.document);
				} else if(obj instanceof Survey) {
					sectionTitle = getString(R.string.survey);
				} else {
					sectionTitle = "기타";
				}
				return sectionTitle;
			}
			
		};
		
		final SimpleSectionAdapter<Object> resultAdapter = 
					new SimpleSectionAdapter<Object>(
														getActivity(), 
														listAdapter, 
														R.layout.section_header, R.id.title,
														sectionizer
													);
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				searchList.setAdapter(resultAdapter);
				WaiterView.dismissDialog(getActivity());
			}
		});
	}
	
	private ArrayList<User>  searchInMembers(String keyword) {
		//// 유저
		Payload request = new Payload().setEvent(Event.Search.user()).setData(new Data().add(0, KEY.SEARCH.QUERY, keyword));
		Data resData = new Connection().async(false).requestPayload(request).request().getResponsePayload().getData();
		ArrayList<User> resUsers = new ArrayList<User>();
		for( int i=0; i<resData.size(); i++) {
			HashMap<String, Object> _user = resData.get(i);
			
			Department department = new Department(
													(String)_user.get(KEY.DEPT.IDX), 
													(String)_user.get(KEY.DEPT.NAME), 
													(String)_user.get(KEY.DEPT.FULL_NAME), 
													(String)_user.get(KEY.DEPT.PARENT_IDX)
												);
			User user = new User(
									(String)_user.get(KEY.USER.IDX), 
									(String)_user.get(KEY.USER.NAME), 
									(Integer)_user.get(KEY.USER.RANK), 
									(String)_user.get(KEY.USER.ROLE), 
									department
								);
			resUsers.add(user);
		}
		return resUsers;
	}
	
	private ArrayList<Document> searchInDocuments(String keyword) {
	//// 문서
			Cursor cDoc = DBProcManager.sharedManager(getActivity()).document().search(keyword);
			ArrayList<Document> resDocs = new ArrayList<Document>();
			cDoc.moveToFirst();
			while( cDoc.getCount() > 0 && !cDoc.isAfterLast() ) {
				Document doc = new Document(
												cDoc.getString(cDoc.getColumnIndex(DocumentProcManager.COLUMN_DOC_IDX)), 
												cDoc.getInt(cDoc.getColumnIndex(DocumentProcManager.COLUMN_DOC_TYPE)), 
												cDoc.getString(cDoc.getColumnIndex(DocumentProcManager.COLUMN_DOC_TITLE)), 
												cDoc.getString(cDoc.getColumnIndex(DocumentProcManager.COLUMN_DOC_CONTENT)), 
												cDoc.getString(cDoc.getColumnIndex(DocumentProcManager.COLUMN_SENDER_IDX)), 
												null, 
												( cDoc.getInt(cDoc.getColumnIndex(DocumentProcManager.COLUMN_DOC_TYPE)) != Document.TYPE_DEPARTED )? true : false, 
												cDoc.getLong(cDoc.getColumnIndex(DocumentProcManager.COLUMN_CREATED_TS)), 
												( cDoc.getInt(cDoc.getColumnIndex(DocumentProcManager.COLUMN_IS_CHECKED)) == 1 )? true : false, 
												cDoc.getLong(cDoc.getColumnIndex(DocumentProcManager.COLUMN_CHECKED_TS)), 
												null, 
												null, 
												( cDoc.getInt(cDoc.getColumnIndex(DocumentProcManager.COLUMN_IS_FAVORITE)) == 1)? true : false
											);
				resDocs.add(doc);
				cDoc.moveToNext();
			}
			return resDocs;
	}
	
	
	
	
	
	
	
}