package kr.go.KNPA.Romeo.Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.go.KNPA.Romeo.ContentFragment;
import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.RoomListFragment;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.EBook.EBookFragment;
import kr.go.KNPA.Romeo.Library.HandBookFragment;
import kr.go.KNPA.Romeo.Library.SocialEvilManualBook;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Settings.SettingsFragment;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;

class ExpandableMenuListAdapter extends SimpleExpandableListAdapter implements OnGroupClickListener, OnChildClickListener {
//	private List<Map<String, String>> gData;
//	private List<List<Map<String, String>>> cData;
	
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
		ImageView iconView = (ImageView)view.findViewById(R.id.icon);
		iconView.setImageResource(Integer.parseInt(item.get("iconImage")));
		return view;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
		Map<String,String> item = (Map<String, String>) getChild(groupPosition, childPosition);
		ImageView iconView = (ImageView)view.findViewById(R.id.icon);
		iconView.setImageResource(Integer.parseInt(item.get("iconImage")));
		return view;
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		Fragment fragment = null;
		HashMap<String, String> g = (HashMap<String, String>) getGroup(groupPosition);//(HashMap<String, String>) gData.get(groupPosition);
		String[] codes = g.get("code").split(":");
		
		if(codes[0].equalsIgnoreCase("CHAT")) {
			if(codes[1].equalsIgnoreCase("COMMAND")) {
				fragment = RoomListFragment.getInstance(Chat.TYPE_COMMAND);
			} else if(codes[1].equalsIgnoreCase("MEETING")) {
				fragment = RoomListFragment.getInstance(Chat.TYPE_MEETING);
			}
		} else if (codes[0].equalsIgnoreCase("DOCUMENT")) {
			// do nothing, expand
		} else if (codes[0].equalsIgnoreCase("SURVEY")) {
			// do nothing, expand
		} else if (codes[0].equalsIgnoreCase("MEMBER") ) {
			// do nothing, expand
		} else if (codes[0].equalsIgnoreCase("SETTINGS")) {
			fragment = new SettingsFragment();
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
		
		HashMap<String, String> g = (HashMap<String, String>) getGroup(groupPosition);//(HashMap<String, String>) gData.get(groupPosition);
		HashMap<String, String> c = (HashMap<String, String>) getChild(groupPosition, childPosition);//(HashMap<String, String>) cData.get(groupPosition).get(childPosition);
		String[] codes = c.get("code").split(":");
		
		if(codes[0].equalsIgnoreCase("CHAT")) {
			if(codes[1].equalsIgnoreCase("COMMAND")) {
				fragment = RoomListFragment.getInstance(Chat.TYPE_COMMAND);
			} else if(codes[1].equalsIgnoreCase("MEETING")) {
				fragment = RoomListFragment.getInstance(Chat.TYPE_MEETING);
			} 
		} else if (codes[0].equalsIgnoreCase("DOCUMENT")) {
			if(codes[1].equalsIgnoreCase("FAVORITE")) {
				fragment = DocumentFragment.documentFragment(Document.TYPE_FAVORITE);
			}else if(codes[1].equalsIgnoreCase("RECEIVED")) {
				fragment = DocumentFragment.documentFragment(Document.TYPE_RECEIVED);
			} else if(codes[1].equalsIgnoreCase("DEPARTED")) {
				fragment = DocumentFragment.documentFragment(Document.TYPE_DEPARTED);
			}
		} else if (codes[0].equalsIgnoreCase("SURVEY")) {
			if(codes[1].equalsIgnoreCase("DEPARTED")) {
				fragment = SurveyFragment.surveyFragment(Survey.TYPE_DEPARTED);
			} else if(codes[1].equalsIgnoreCase("RECEIVED")) {
				fragment = SurveyFragment.surveyFragment(Survey.TYPE_RECEIVED);
			}
		} else if (codes[0].equalsIgnoreCase("MEMBER") ) {
			if(codes[1].equalsIgnoreCase("MEMBERLIST")) {
				fragment = MemberFragment.memberFragment(User.TYPE_MEMBERLIST);
			} else if(codes[1].equalsIgnoreCase("FAVORITE")) {
				fragment = MemberFragment.memberFragment(User.TYPE_FAVORITE);
			}
		} else if (codes[0].equalsIgnoreCase("LIBRARY")) {
			if(codes[1].equalsIgnoreCase("HANDBOOK")) {
				fragment = new HandBookFragment();
			} else if(codes[1].equalsIgnoreCase("SOCIALEVIL")) {
				fragment = new SocialEvilManualBook();
			} else if(codes[1].equalsIgnoreCase("EBOOK")) {
				fragment = new EBookFragment("styleTemplateGuide", "샘플 이북 뷰어", "이북");
			}
		} else if (codes[0].equalsIgnoreCase("SETTINGS")) {
			// NOTHING
		}
		
		if(fragment == null) {
			fragment = new ContentFragment(c.get("code"));
		}
		
		if (fragment != null)
			switchFragment(fragment);
		
		return true;
	}

	

	// the meat of switching the above fragment
		private void switchFragment(Fragment fragment) {
			MainActivity.sharedActivity().switchContent(fragment);
//			if (getActivity() == null)
//				return;
//			
//			if (getActivity() instanceof MainActivity) {
//				MainActivity fca = (MainActivity) getActivity();
//				fca.switchContent(fragment);
//			}
		}
	
}
