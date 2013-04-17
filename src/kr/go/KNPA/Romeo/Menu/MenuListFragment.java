package kr.go.KNPA.Romeo.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.go.KNPA.Romeo.ContentFragment;
import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.ChatFragment;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.DocumentProcManager;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Library.HandBookFragment;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserProfileFragment;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import kr.go.KNPA.Romeo.Util.CollectionFactory;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;



public class MenuListFragment extends ListFragment {
	
	private List<Map<String, String>> gData;
	private List<List<Map<String, String>>> cData;
	
	private LinearLayout userLL;
	private View searchBar;
	private EditText searchET;
	private Button cancelSearchBT;
	private ExpandableListView menuList;
	private ListView searchList;
	
	private SimpleSectionAdapter<MenuListItem> sectionAdapter; 
	private BaseAdapter emptyAdapter = new BaseAdapter() {	
		@Override	public int getCount() {	return 0;	}
		@Override	public Object getItem(int arg0) {	return null;	}
		@Override	public long getItemId(int arg0) {	return 0;	}
		@Override	public View getView(int arg0, View arg1, ViewGroup arg2) {	return null; }	
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"설정", 			"iconImage", ""+R.drawable.sub_indicator, 	"code", "settings"));
		childData.add(l);	l= null;
        
		
		gData = groupData;
		cData = childData;
		
		ExpandableMenuListAdapter adapter = 
				new ExpandableMenuListAdapter(
						getActivity(),
						
						groupData, 
						R.layout.menu_list_cell_section_unfolded, 
						R.layout.menu_list_cell_section_folded, 
						new String[] {"section"},//, "iconImage"}, 
						new int[] { R.id.cell_title },//, R.id.cell_icon}, 
						
						childData, 
						R.layout.menu_list_cell_item, 
						new String[] {"title", "code"},// "iconImage" } , 
						new int[] {R.id.cell_title, R.id.cell_code}// R.id.cell_icon,}
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
		cancelSearchBT = (Button)searchBar.findViewById(R.id.cancel);
		
		searchET.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				setSearchMode(hasFocus);
			};
		});
		
		searchET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setSearchMode(true);
			}
		});
		
		searchET.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				if(keyCode == KeyEvent.KEYCODE_ENTER) {
					// TODO : 왜 엔터는 한번 쳤는데 두번 실행되지??
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
				InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
			}
		});
		
		
		return v;
	}

	private void setSearchMode(boolean willSearchMode) {
		if(willSearchMode == true) {
			cancelSearchBT.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.VISIBLE);
			menuList.setVisibility(View.INVISIBLE);
		} else {
			cancelSearchBT.setVisibility(View.GONE);
			searchList.setVisibility(View.INVISIBLE);
			menuList.setVisibility(View.VISIBLE);
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
		
		//// 채팅  X
		//// 설문 TODO
		//// 자료실
		
		
		// SimpleSectionList
		
		BaseAdapter listAdapter = new IntergratedSearchListAdatper(keyword, resUsers, resDocs, null);
		
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
														R.layout.section_header, R.id.cell_title,
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
	
	class IntergratedSearchListAdatper extends BaseAdapter {
		private String keyword = null;
		private ArrayList<User> users;
		private ArrayList<Document> documents;
		private ArrayList<Survey> surveys;
		
		public IntergratedSearchListAdatper(String keyword, ArrayList<User> users, ArrayList<Document> documents, ArrayList<Survey> surveys) {
			this.keyword = keyword;
			if(users == null)
				users = new ArrayList<User>();
			this.users = users;
			if(documents == null)
				documents = new ArrayList<Document>();
			this.documents = documents;
			if(surveys == null) 
				surveys = new ArrayList<Survey>();
			this.surveys = surveys;
		}

		@Override
		public int getCount() {
			return users.size() + documents.size() + surveys.size();
		}

		@Override
		public Object getItem(int pos) {
			if(pos < users.size()) {
				return users.get(pos);
			} else if((pos -= users.size()) < documents.size()) {
				return documents.get(pos);
			} else if((pos -= documents.size()) < surveys.size()) {
				return surveys.get(pos);
			} else {
				return new Object();
			}
		}

		@Override
		public long getItemId(int pos) {
			return getItem(pos).hashCode();
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			Object item = getItem(pos);
			LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(item instanceof User) {
				User user = (User)item;
				View view = inflater.inflate(R.layout.member_favorite_user_cell, parent, false);
				ImageView  userPicIV = (ImageView) view.findViewById(R.id.user_pic);
				new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, user.idx, userPicIV);
				TextView deptTV = (TextView)view.findViewById(R.id.department);
				deptTV.setText(user.department.nameFull);
				TextView nameTV = (TextView)view.findViewById(R.id.name);
				nameTV.setText(user.name);
				TextView rankTV = (TextView)view.findViewById(R.id.rank);
				rankTV.setText(User.RANK[user.rank]);
				TextView roleTV = (TextView)view.findViewById(R.id.role);
				roleTV.setText(user.role);
				
				return view;
				
			} else if(item instanceof Document) {
				
				final Document doc = (Document)item;
				View view = null;
				if(doc.favorite == true) {
					view = inflater.inflate(R.layout.document_list_cell_favorite, parent, false);
				} else {
					switch(doc.subType()) {
					
						case Document.TYPE_DEPARTED: 
							view = inflater.inflate(R.layout.document_list_cell_departed, parent, false); break;
						
						case Document.TYPE_RECEIVED: 
						default : 
							view = inflater.inflate(R.layout.document_list_cell_received, parent, false); break;
					}
				}
				
				TextView titleTV = (TextView)view.findViewById(R.id.title);
				titleTV.setText(doc.title);
				final TextView senderTV = (TextView)view.findViewById(R.id.sender);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final User sender = User.getUserWithIdx(doc.senderIdx);
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								senderTV.setText(sender.department.nameFull + " " + User.RANK[sender.rank] +" "+ sender.name );
							}
						});
					}
				}).start();
				
				TextView arrivalDTTV = (TextView)view.findViewById(R.id.arrivalDT);
				arrivalDTTV.setText(Formatter.timeStampToRecentString(doc.TS));
				
				return view;
				
			} else if(item instanceof Survey) {
				
				Survey survey = (Survey)item;
				View view = inflater.inflate(R.layout.member_favorite_user_cell, parent, false);
				return view;
				
			} else {
				
				return new View(getActivity());
			}
		}
		
		
	}
	
	class ExpandableMenuListAdapter extends SimpleExpandableListAdapter implements OnGroupClickListener, OnChildClickListener {

		public ExpandableMenuListAdapter(
				Context context,
				List<? extends Map<String, ?>> groupData,
				int expandedGroupLayout, 
				int collapsedGroupLayout,
				String[] groupFrom, 
				int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout,  
				String[] childFrom,
				int[] childTo) {
			super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom,
					groupTo, childData, childLayout, childFrom, childTo);
			
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
			Map<String,String> item = (Map<String, String>) getGroup(groupPosition);
			ImageView iconView = (ImageView)view.findViewById(R.id.cell_icon);
			iconView.setImageResource(Integer.parseInt(item.get("iconImage")));
			return view;
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
			Map<String,String> item = (Map<String, String>) getChild(groupPosition, childPosition);
			ImageView iconView = (ImageView)view.findViewById(R.id.cell_icon);
			iconView.setImageResource(Integer.parseInt(item.get("iconImage")));
			return view;
		}
		
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			Fragment fragment = null;
			HashMap<String, String> g = (HashMap<String, String>) gData.get(groupPosition);
			String[] codes = g.get("code").split(":");
			
			if(codes[0].toUpperCase().equals("CHAT")) {
				if(codes[1].toUpperCase().equals("COMMAND")) {
					fragment = ChatFragment.chatFragment(Chat.TYPE_COMMAND);
				} else if(codes[1].toUpperCase().equals("MEETING")) {
					fragment = ChatFragment.chatFragment(Chat.TYPE_MEETING);
				}
			} else if (codes[0].toUpperCase().equals("DOCUMENT")) {
				// do nothing, expand
			} else if (codes[0].toUpperCase().equals("SURVEY")) {
				// do nothing, expand
			} else if (codes[0].toUpperCase().equals("MEMBER") ) {
				// do nothing, expand
			} else if (codes[0].toUpperCase().equals("SETTINGS")) {
				// do nothing, expand
			}
			
			if (fragment != null) {
				switchFragment(fragment);
				return true;
			} else {
				return false;
			}
			
		}
		
		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

			
			Fragment fragment = null;
			
			HashMap<String, String> g = (HashMap<String, String>) gData.get(groupPosition);
			HashMap<String, String> c = (HashMap<String, String>) cData.get(groupPosition).get(childPosition);
			String[] codes = c.get("code").split(":");
			
			if(codes[0].toUpperCase().equals("CHAT")) {
				if(codes[1].toUpperCase().equals("COMMAND")) {
					fragment = ChatFragment.chatFragment(Chat.TYPE_COMMAND);
				} else if(codes[1].toUpperCase().equals("MEETING")) {
					fragment = ChatFragment.chatFragment(Chat.TYPE_MEETING);
				}
			} else if (codes[0].toUpperCase().equals("DOCUMENT")) {
				if(codes[1].toUpperCase().equals("FAVORITE")) {
					fragment = DocumentFragment.documentFragment(Document.TYPE_FAVORITE);
				}else if(codes[1].toUpperCase().equals("RECEIVED")) {
					fragment = DocumentFragment.documentFragment(Document.TYPE_RECEIVED);
				} else if(codes[1].toUpperCase().equals("DEPARTED")) {
					fragment = DocumentFragment.documentFragment(Document.TYPE_DEPARTED);
				}
			} else if (codes[0].toUpperCase().equals("SURVEY")) {
				if(codes[1].toUpperCase().equals("DEPARTED")) {
					fragment = SurveyFragment.surveyFragment(Survey.TYPE_DEPARTED);
				} else if(codes[1].toUpperCase().equals("RECEIVED")) {
					fragment = SurveyFragment.surveyFragment(Survey.TYPE_RECEIVED);
				}
			} else if (codes[0].toUpperCase().equals("MEMBER") ) {
				if(codes[1].toUpperCase().equals("MEMBERLIST")) {
					fragment = MemberFragment.memberFragment(User.TYPE_MEMBERLIST);
				} else if(codes[1].toUpperCase().equals("FAVORITE")) {
					fragment = MemberFragment.memberFragment(User.TYPE_FAVORITE);
				}
			} else if (codes[0].toUpperCase().equals("LIBRARY")) {
				if(codes[1].toUpperCase().equals("HANDBOOK")) {
					fragment = new HandBookFragment();
				}
			} else if (codes[0].toUpperCase().equals("SETTINGS")) {
			}
			
			if(fragment == null) {
				fragment = new ContentFragment(c.get("code"));
			}
			
			if (fragment != null)
				switchFragment(fragment);
			
			return true;
		}
	
	}
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*
		// JSON 객체를 통해 배열을 초기화해야하는데
		// Gson menuData = new Gson();
		MenuListItem[] menuItems = new MenuListItem[] {
			
			new MenuListItem.Builder().section("지시와 보고").title("지시와 보고").iconImage(R.drawable.icon_chat).code("chat:command").build(),
			new MenuListItem.Builder().section("지시와 보고").title("회의").iconImage(R.drawable.icon_chat).code("chat:meeting").build(),
			new MenuListItem.Builder().section("업무연락").title("중요 업무연락").iconImage(R.drawable.icon_star).code("document:Favorite").build(),
			new MenuListItem.Builder().section("업무연락").title("수신 업무연락").iconImage(R.drawable.icon_document_received).code("document:Received").build(),
			new MenuListItem.Builder().section("업무연락").title("발신 업무연락").iconImage(R.drawable.icon_document_departed).code("document:Departed").build(),
			new MenuListItem.Builder().section("설문").title("수신 설문").iconImage(R.drawable.icon_survey_received).code("survey:Received").build(),
			new MenuListItem.Builder().section("설문").title("발신 설문").iconImage(R.drawable.icon_survey_departed).code("survey:Departed").build(),
			new MenuListItem.Builder().section("조직도").title("조직도").iconImage(R.drawable.icon_people).code("member").build(),
			new MenuListItem.Builder().section("설정").title("설정").iconImage(android.R.drawable.ic_menu_preferences).code("settings").build()
		};
		

		
		// 어탭터 인스턴스를 생성한다.
		MenuListAdapter menuList = new MenuListAdapter(getActivity(), R.layout.menu_list_cell);
		for(MenuListItem menuItem : menuItems) {
			menuList.add(menuItem);
		}
		
		Sectionizer<MenuListItem> sectionizer = new Sectionizer<MenuListItem>() {
			@Override
			public String getSectionTitleForItem(MenuListItem menuItem) {
				return menuItem.section;
			}
		};
		
		sectionAdapter = 
			new SimpleSectionAdapter<MenuListItem>(getActivity(), menuList, R.layout.section_header, R.id.cell_title, sectionizer);
		
		
		*/
		//setListAdapter(sectionAdapter);
		
		
	}
	
	/*
	// MenuListAdapter를 정의한다.
	// Array Adapyer는 List와 ListView를 연결시켜주는 클래스이다.
	public class MenuListAdapter extends ArrayAdapter<MenuListItem> {

		public MenuListAdapter(Context context) {
			super(context, 0);
		}
		public MenuListAdapter(Context context, int id) {
			super(context, id);
		}
		
		

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_list_cell, null);
			}
			// xml로부터 셀 하나의 템플릿을 읽어온다.
			
			// 아이템 하나로부터 정보를 취득하여 템플릿에 채워넣는다.
			ImageView icon = (ImageView) convertView.findViewById(R.id.cell_icon);
			icon.setImageResource(getItem(position).iconImage);
			TextView title = (TextView) convertView.findViewById(R.id.cell_title);
			title.setText(getItem(position).title);

			// 채워진 템플릿 발사.
			return convertView;
		}

	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		MenuListItem menuItem = (MenuListItem)sectionAdapter.getItem(position); 
		String[] codes = menuItem.code.split(":");
		
		Fragment fragment = null;
		
		if(codes[0].equals("CHAT")) {
			if(codes[1].equals("COMMAND")) {
				fragment = new ChatFragment();
			} else if(codes[1].equals("MEETING")) {
				fragment = new MeetingFragment();
			}
		} else if (codes[0].equals("DOCUMENT")) {
			if(codes[1].equals("FAVORITE")) {
			}else if(codes[1].equals("RECEIVED")) {
			} else if(codes[1].equals("DEPARTED")) {
			}
		} else if (codes[0].equals("SURVEY")) {
			if(codes[1] == "DEPARTED") {
			} else if(codes[1].equals("RECEIVED")) {
			}
		} else if (codes[0].equals("MEMBER") ) {
			fragment = new MemberFragment();
		} else if (codes[0].equals("SETTINGS")) {
		}
		
		if(fragment == null) {
			fragment = new ContentFragment(menuItem.code);
		}
		
		if (fragment != null)
			switchFragment(fragment);
	}
*/
	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		}
	}
	
}