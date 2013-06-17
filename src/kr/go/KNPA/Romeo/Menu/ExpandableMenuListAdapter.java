package kr.go.KNPA.Romeo.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.go.KNPA.Romeo.ContentFragment;
import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.RoomListFragment;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Document.DocumentFragment;
import kr.go.KNPA.Romeo.EBook.EBookFragment;
import kr.go.KNPA.Romeo.Library.HandBookFragment;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Settings.BugReportFragment;
import kr.go.KNPA.Romeo.Settings.SettingsFragment;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class ExpandableMenuListAdapter extends SimpleExpandableListAdapter implements OnGroupClickListener, OnChildClickListener {

	public static final String				CODE_BUG_REPORT				= "BUG:REPORT";

	public static final String				CODE_SETTINGS				= "SETTINGS";

	public static final String				CODE_LIBRARY_SOCIAL_EVIL	= "LIBRARY:SOCIALEVIL";

	public static final String				CODE_LIBRARY_FIELD_MANUAL	= "LIBRARY:FIELDMANUAL";

	public static final String				CODE_LIBRARY				= "LIBRARY";

	public static final String				CODE_MEETING				= "MEETING";

	public static final String				CODE_COMMAND				= "COMMAND";

	public static final String				CODE_SURVEY_DEPARTED		= "SURVEY:DEPARTED";

	public static final String				CODE_SURVEY_RECEIVED		= "SURVEY:RECEIVED";

	public static final String				CODE_SURVEY					= "SURVEY";

	public static final String				CODE_DOCUMENT_DEPARTED		= "DOCUMENT:DEPARTED";

	public static final String				CODE_DOCUMENT_RECEIVED		= "DOCUMENT:RECEIVED";

	public static final String				CODE_DOCUMENT_FAVORITE		= "DOCUMENT:FAVORITE";

	public static final String				CODE_DOCUMENT				= "DOCUMENT";

	public static final String				CODE_MEMBER_FAVORITE		= "MEMBER:FAVORITE";

	public static final String				CODE_MEMBER_LIST			= "MEMBER:MEMBERLIST";

	public static final String				CODE_MEMBER					= "MEMBER";

	private Context							mContext;
	public static HashMap<String, Integer>	unCheckedCounts;

	HashMap<String, Integer>				nUnChecked;

	private ExpandableMenuListAdapter(Context context, List<? extends Map<String, ?>> groupData, int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo)
	{
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
		this.mContext = context;
		unCheckedCounts = new HashMap<String, Integer>();
		fetchUncheckedCounts(context);
	}

	public static void fetchUncheckedCounts(final Context context)
	{
		new Thread() {
			public void run()
			{
				synchronized (unCheckedCounts)
				{
					unCheckedCounts.put(CODE_COMMAND, DAO.chat(context).getNumUnchecked(Chat.TYPE_COMMAND));
					unCheckedCounts.put(CODE_MEETING, DAO.chat(context).getNumUnchecked(Chat.TYPE_MEETING));

					unCheckedCounts.put(CODE_DOCUMENT, DAO.document(context).getNumUnchecked());
					unCheckedCounts.put(CODE_SURVEY, DAO.survey(context).getNumUnchecked());
				}
			}
		}.start();
	}
	
	@Override
	public View newChildView(boolean isLastChild, ViewGroup parent)
	{
		return getLayoutInflater().inflate(R.layout.menu_list_cell_item, parent, false);
	}

	@Override
	public View newGroupView(boolean isExpanded, ViewGroup parent)
	{
		return getLayoutInflater().inflate(isExpanded ? R.layout.menu_list_cell_section_unfolded : R.layout.menu_list_cell_section_folded, parent, false);
	}

	public static ExpandableMenuListAdapter getExpandableMenuListAdapter(Context context, List<MenuListItem> data)
	{

		ArrayList<MenuListItem> groupData = new ArrayList<MenuListItem>();
		ArrayList<ArrayList<MenuListItem>> childrenData = new ArrayList<ArrayList<MenuListItem>>();
		for (int gi = 0; gi < data.size(); gi++)
		{
			MenuListItem group = data.get(gi);
			groupData.add(group);

			ArrayList<MenuListItem> children = new ArrayList<MenuListItem>();
			for (int ci = 0; group.children() != null && ci < group.children().size(); ci++)
			{
				MenuListItem child = group.children().get(ci);
				children.add(child);
			}

			childrenData.add(children);
		}

		ExpandableMenuListAdapter adapter = new ExpandableMenuListAdapter(context, groupData, R.layout.menu_list_cell_section_unfolded, R.layout.menu_list_cell_section_folded,
				new String[] { "section" }, new int[] { R.id.title }, childrenData, R.layout.menu_list_cell_item, new String[] { "title" }, new int[] { R.id.title });
		return adapter;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		View view = newGroupView(isExpanded, parent);
		MenuListItem item = (MenuListItem) getGroup(groupPosition);
		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		iconView.setImageResource(item.iconImage());
		TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(getContext().getString(item.title()));

		final TextView nUncheckedTV = (TextView) view.findViewById(R.id.nUnchecked);
		final String codes = item.code();

		synchronized (unCheckedCounts)
		{
			Integer uncheckedCount = unCheckedCounts.get(codes);

			if (uncheckedCount != null && uncheckedCount > 0)
			{
				nUncheckedTV.setText(uncheckedCount.toString());
				nUncheckedTV.setVisibility(View.VISIBLE);
			}
		}

		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		View view = newChildView(isLastChild, parent);// super.getChildView(groupPosition,
														// childPosition,
														// isLastChild,
														// convertView, parent);
		MenuListItem item = (MenuListItem) getChild(groupPosition, childPosition);
		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		iconView.setImageResource(item.iconImage());
		TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(getContext().getString(item.title()));

		final TextView nUncheckedTV = (TextView) view.findViewById(R.id.nUnchecked);

		final String[] codes = item.code().split(":");

		if (item.code().equals(CODE_SURVEY_RECEIVED) || item.code().equals(CODE_DOCUMENT_RECEIVED))
		{
			synchronized (unCheckedCounts)
			{
				Integer uncheckedCount = unCheckedCounts.get(codes[0]);

				if (uncheckedCount != null && uncheckedCount > 0)
				{
					nUncheckedTV.setText(uncheckedCount.toString());
					nUncheckedTV.setVisibility(View.VISIBLE);
				}
			}
		}
		return view;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
	{
		Fragment fragment = null;

		MenuListItem item = (MenuListItem) getGroup(groupPosition);
		String code = item.code();

		if (code.equals(CODE_COMMAND))
		{
			fragment = RoomListFragment.getInstance(Chat.TYPE_COMMAND);
		}
		else if (code.equals(CODE_MEETING))
		{
			fragment = RoomListFragment.getInstance(Chat.TYPE_MEETING);
		}
		else if (code.equals(CODE_DOCUMENT))
		{
			// do nothing, expand
		}
		else if (code.equals(CODE_SURVEY))
		{
			// do nothing, expand
		}
		else if (code.equals(CODE_MEMBER))
		{
			// do nothing, expand
		}
		else if (code.equals(CODE_SETTINGS))
		{
			fragment = new SettingsFragment();
		}
		else if (code.equals(CODE_BUG_REPORT))
		{
			fragment = new BugReportFragment();
		}

		if (fragment != null)
		{
			switchFragment(fragment);
			return true;
		}
		else
		{
			return false;
		}

	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	{
		Fragment fragment = null;

		MenuListItem item = (MenuListItem) getChild(groupPosition, childPosition);
		String code = item.code();

		if (code.equals(CODE_DOCUMENT_FAVORITE))
		{
			fragment = DocumentFragment.documentFragment(Document.TYPE_FAVORITE);
		}
		else if (code.equals(CODE_DOCUMENT_RECEIVED))
		{
			fragment = DocumentFragment.documentFragment(Document.TYPE_RECEIVED);
		}
		else if (code.equals(CODE_DOCUMENT_DEPARTED))
		{
			fragment = DocumentFragment.documentFragment(Document.TYPE_DEPARTED);
		}
		else if (code.equals(CODE_SURVEY_DEPARTED))
		{
			fragment = new SurveyFragment(Survey.TYPE_DEPARTED);
		}
		else if (code.equals(CODE_SURVEY_RECEIVED))
		{
			fragment = new SurveyFragment(Survey.TYPE_RECEIVED);
		}
		else if (code.equals(CODE_MEMBER_LIST))
		{
			fragment = MemberFragment.memberFragment(User.TYPE_MEMBERLIST);
		}
		else if (code.equals(CODE_MEMBER_FAVORITE))
		{
			fragment = MemberFragment.memberFragment(User.TYPE_FAVORITE);
		}
		else if (code.equals(CODE_LIBRARY_FIELD_MANUAL))
		{
			fragment = new HandBookFragment();
		}
		else if (code.equals(CODE_LIBRARY_SOCIAL_EVIL))
		{
			fragment = EBookFragment.getEBookFragment("socialEvilManual", "4대 사회악 근절 전담부대 매뉴얼", "4대악 근절 매뉴얼");
		}

		if (fragment == null)
		{
			fragment = new ContentFragment(item.code());
		}

		if (fragment != null)
			switchFragment(fragment);

		return true;
	}

	protected Context getContext()
	{
		return this.mContext;
	}

	protected LayoutInflater getLayoutInflater()
	{
		return (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment)
	{
		MainActivity.sharedActivity().switchContent(fragment);
	}

	public static List<MenuListItem> makeMenuList()
	{
		List<MenuListItem> menus = new ArrayList<MenuListItem>();

		MenuListItem currentParentMenu = null;

		int prevDepth = 1;

		for (int i = 0; i < mMenuRes.length; i += 4)
		{
			int currentDepth = (Integer) mMenuRes[i];
			if (prevDepth > currentDepth)
			{
				if (currentParentMenu != null)
				{
					menus.add(currentParentMenu);
				}

				currentParentMenu = new MenuListItem((Integer) mMenuRes[i + 1], (Integer) mMenuRes[i + 2], (String) mMenuRes[i + 3]);
			}
			else
			{
				MenuListItem child = new MenuListItem((Integer) mMenuRes[i + 1], (Integer) mMenuRes[i + 2], (String) mMenuRes[i + 3]);
				currentParentMenu.addChild(child);
			}
		}

		return menus;
	}
	
	public static void refresh()
	{
		
	}
	
	public static Object[] mMenuRes = new Object[]{
		0, R.string.memberListTitle, R.drawable.icon_people, CODE_MEMBER,
		1, R.string.memberListTitle, R.drawable.icon_people, CODE_MEMBER_LIST,
		1, R.string.memberFavoriteTitle, R.drawable.icon_star, CODE_MEMBER_FAVORITE,
		
		0, R.string.documentTitle, R.drawable.icon_document, CODE_DOCUMENT,
		1, R.string.documentFavoriteTitle, R.drawable.icon_document_star, CODE_DOCUMENT_FAVORITE,
		1, R.string.documentReceivedTitle, R.drawable.icon_document_received, CODE_DOCUMENT_RECEIVED,
		1, R.string.documentDepartedTitle, R.drawable.icon_document_departed, CODE_DOCUMENT_DEPARTED,
		
		0, R.string.surveyTitle, R.drawable.icon_pie_graph, CODE_SURVEY,
		1, R.string.surveyReceivedTitle, R.drawable.icon_pie_graph_received, CODE_SURVEY_RECEIVED,
		1, R.string.surveyDepartedTitle, R.drawable.icon_pie_graph_departed, CODE_SURVEY_DEPARTED,
		
		0, R.string.commandTitle, R.drawable.icon_arrow_side, CODE_COMMAND,
		
		0, R.string.meetingTitle, R.drawable.icon_circle, CODE_MEETING,
		
		0, R.string.libraryTitle, R.drawable.icon_folder, CODE_LIBRARY,
		1, R.string.library_fieldManual, R.drawable.icon_folder, CODE_LIBRARY_FIELD_MANUAL,
		1, R.string.library_socialEvilManual, R.drawable.icon_folder, CODE_LIBRARY_SOCIAL_EVIL,
		
		0, R.string.settingsTitle, R.drawable.icon_gear, CODE_SETTINGS,

		0, R.string.bugReport, R.drawable.icon_mail, CODE_BUG_REPORT
	};
}
