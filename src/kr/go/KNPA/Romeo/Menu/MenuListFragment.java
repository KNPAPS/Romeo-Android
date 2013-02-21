package kr.go.KNPA.Romeo.Menu;

import kr.go.KNPA.Romeo.ContentFragment;
import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.R.drawable;
import kr.go.KNPA.Romeo.R.id;
import kr.go.KNPA.Romeo.R.layout;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import com.google.gson.*;

import kr.go.KNPA.Romeo.Chat.CommandFragment;
import kr.go.KNPA.Romeo.Chat.MeetingFragment;
import kr.go.KNPA.Romeo.Menu.*;


import kr.go.KNPA.Romeo.Member.*;

public class MenuListFragment extends ListFragment {
	private SimpleSectionAdapter<MenuListItem> sectionAdapter; 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
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
		
		

		setListAdapter(sectionAdapter);
	}
	
	

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
				fragment = new CommandFragment();
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