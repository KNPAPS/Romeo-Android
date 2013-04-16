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
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.Library.HandBookFragment;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserProfileFragment;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import kr.go.KNPA.Romeo.Util.CollectionFactory;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;



public class MenuListFragment extends ListFragment {
	
	private List<Map<String, String>> gData;
	private List<List<Map<String, String>>> cData;
	
	private SimpleSectionAdapter<MenuListItem> sectionAdapter; 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu_list, null);
		
		//// 간단 프로필 출력!!
		LinearLayout userLL = (LinearLayout) v.findViewById(R.id.favorite_user_cell);
		//userLL.setBackgroundResource(android.R.color.transparent);
		userLL.setBackgroundResource(R.drawable.menu_user_info_box);
		ImageView userPicIV = (ImageView) v.findViewById(R.id.userPic);
		//userPicIV.setImageResource(R.drawable.user_pic_default);
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
		
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        
        groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	"조직도", 		"iconImage", ""+R.drawable.icon_people,					"code", "member") );
        groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	"지시와 보고", 	"iconImage", ""+R.drawable.icon_arrow_side,					"code",	"chat:Command") );
        groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	"회의", 			"iconImage", ""+R.drawable.icon_circle,					"code", "chat:Meeting" ) );
        groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	"업무연락", 		"iconImage", ""+R.drawable.icon_document,		"code",	"document") );
        groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	"설문",		 	"iconImage", ""+R.drawable.icon_pie_graph,		"code",	"survey") );
        groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	"자료실", 		"iconImage", ""+R.drawable.icon_folder,			"code", "library") );
        groupData.add( CollectionFactory.hashMapWithKeysAndStrings("section",	"설정", 			"iconImage", ""+R.drawable.icon_gear,	"code",	"settings") );
        
        
        List<Map<String, String>> l = null;
        
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"조직도",		 	"iconImage", ""+R.drawable.icon_people, 					"code", "member:MemberList"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"즐겨찾기",	 	"iconImage", ""+R.drawable.icon_star, 						"code", "member:Favorite"));
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"지시와 보고", 	"iconImage", ""+R.drawable.icon_chat, 						"code", "chat:Command")); 
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"회의",	 		"iconImage", ""+R.drawable.icon_circle, 						"code", "chat:Meeting")); 
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"중요 업무연락", 	"iconImage", ""+R.drawable.icon_document_star, 						"code", "document:Favorite"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"받은 업무연락", 	"iconImage", ""+R.drawable.icon_document_received, 			"code", "document:Received"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"보낸 업무연락", 	"iconImage", ""+R.drawable.icon_document_departed, 			"code", "document:Departed"));
		childData.add(l);
		
		l = new ArrayList<Map<String, String>>();
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"받은 설문",	 	"iconImage", ""+R.drawable.icon_pie_graph_received, 			"code", "survey:Received"));
		l.add(CollectionFactory.hashMapWithKeysAndStrings("title",		"보낸 설문",	 	"iconImage", ""+R.drawable.icon_pie_graph_departed, 			"code", "survey:Departed"));
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
		ExpandableListView elv = (ExpandableListView)v.findViewById(android.R.id.list);
		elv.setAdapter(adapter);
		elv.setOnGroupClickListener(adapter);
		elv.setOnChildClickListener(adapter);
		return v;
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