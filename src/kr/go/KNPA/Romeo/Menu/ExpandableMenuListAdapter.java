package kr.go.KNPA.Romeo.Menu;

import java.util.ArrayList;
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
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.os.Handler;
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

class ExpandableMenuListAdapter extends SimpleExpandableListAdapter implements OnGroupClickListener, OnChildClickListener {

	private Context context;
	Handler unCheckedHandler;
	private ExpandableMenuListAdapter(
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
		this.context = context;
		unCheckedHandler = new Handler();
	}

	
	public static ExpandableMenuListAdapter getExpandableMenuListAdapter(Context context, List<MenuListItem> data) {
		
		ArrayList< MenuListItem > groupData = new ArrayList<MenuListItem>();
		ArrayList < ArrayList< MenuListItem > > childrenData = new ArrayList< ArrayList<MenuListItem > >();
		for( int gi = 0; gi < data.size(); gi++ ) {
			MenuListItem group = data.get(gi);
			groupData.add( group );
			
			ArrayList< MenuListItem > children = new ArrayList< MenuListItem >();
			for( int ci = 0; group.children() != null && ci < group.children().size(); ci++ ) {
				MenuListItem child = group.children().get(ci);
				children.add(child);
			}
			
			childrenData.add(children);
		}
		
		ExpandableMenuListAdapter adapter = 
				new ExpandableMenuListAdapter(
						context, groupData, 
						R.layout.menu_list_cell_section_unfolded, R.layout.menu_list_cell_section_folded, 
						new String[] { "section" }, new int[] { R.id.title }, 
						childrenData, R.layout.menu_list_cell_item, 
						new String[] { "title"}, new int[] { R.id.title});
		return adapter;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
		//View view = newGroupView(isExpanded, parent);
		MenuListItem item = (MenuListItem) getGroup(groupPosition);
		ImageView iconView = (ImageView)view.findViewById(R.id.icon);
		iconView.setImageResource( item.iconImage() );
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText( getContext().getString( item.title() ) );
		
		final TextView nUncheckedTV = (TextView)view.findViewById(R.id.nUnchecked);
		final String[] codes = item.code().split(":");
		
		if(
				codes[0].equalsIgnoreCase("CHAT") || 
				codes[0].equalsIgnoreCase("DOCUMENT") || 
				codes[0].equalsIgnoreCase("SURVEY") ) {
			
			final WaiterView wv = new WaiterView(getContext());
			nUncheckedTV.setVisibility(View.VISIBLE);
			wv.substituteView(nUncheckedTV);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					int unChecked = 0;
					if(codes[0].equalsIgnoreCase("CHAT")) {
						int nCommand = DAO.chat(getContext()).getNumUnchecked(Chat.TYPE_COMMAND);
						int nMeeting = DAO.chat(getContext()).getNumUnchecked(Chat.TYPE_MEETING);
						unChecked = nCommand + nMeeting;
					} else if (codes[0].equalsIgnoreCase("DOCUMENT")) {
						int nDocument = DAO.document(getContext()).getNumUnchecked();
						unChecked = nDocument;
					} else if (codes[0].equalsIgnoreCase("SURVEY")) {
						int nSurvey = DAO.survey(getContext()).getNumUnchecked();
						unChecked = nSurvey;
					}

					final int uc = unChecked;
					unCheckedHandler.post(new Runnable() {
						
						@Override
						public void run() {
							nUncheckedTV.setText("" + uc);
							wv.restoreView();
						}
					});
					
				}
			}).start();
		}
		return view;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
		MenuListItem item = ( MenuListItem ) getChild(groupPosition, childPosition);
		ImageView iconView = (ImageView)view.findViewById(R.id.icon);
		iconView.setImageResource( item.iconImage() );
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText( getContext().getString( item.title() ) );
		
		final TextView nUncheckedTV = (TextView)view.findViewById(R.id.nUnchecked);
		final String[] codes = item.code().split(":");
		
		if(
				codes[0].equalsIgnoreCase("CHAT") || 
				codes[0].equalsIgnoreCase("DOCUMENT") || 
				codes[0].equalsIgnoreCase("SURVEY") ) {
			
			final WaiterView wv = new WaiterView(getContext());
			nUncheckedTV.setVisibility(View.VISIBLE);
			wv.substituteView(nUncheckedTV);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					int unChecked = 0;
					if(codes[0].equalsIgnoreCase("CHAT")) {
						if(codes[1].equalsIgnoreCase("COMMAND")) {
							unChecked = DAO.chat(getContext()).getNumUnchecked(Chat.TYPE_COMMAND);
						} else if(codes[1].equalsIgnoreCase("MEETING")) {
							unChecked = DAO.chat(getContext()).getNumUnchecked(Chat.TYPE_MEETING);
						} else
							return;
						
					} else if (codes[0].equalsIgnoreCase("DOCUMENT")) {
						if(codes[1].equalsIgnoreCase("RECEIVED"))
							unChecked = DAO.document(getContext()).getNumUnchecked();
						else
							return;
						
					} else if (codes[0].equalsIgnoreCase("SURVEY")) {
						if(codes[1].equalsIgnoreCase("RECEIVED"))
							unChecked = DAO.survey(getContext()).getNumUnchecked();
						else
							return;
					}

					final int uc = unChecked;
					unCheckedHandler.post(new Runnable() {
						
						@Override
						public void run() {
							nUncheckedTV.setText("" + uc);
							wv.restoreView();
							
						}
					});
					
				}
			}).start();
		}

		return view;
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		Fragment fragment = null;
		
		MenuListItem item = (MenuListItem)getGroup(groupPosition);
		String[] codes = item.code().split(":");
		
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
		
		// TODO : Bug Report
		else if (codes[0].equalsIgnoreCase("BUG")) {
			fragment = new BugReportFragment();
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

		MenuListItem item = (MenuListItem)getChild(groupPosition, childPosition);
		String[] codes = item.code().split(":");
		
		if(codes[0].equalsIgnoreCase("CHAT")) {
			if(codes[1].equalsIgnoreCase("COMMAND")) {
				// handled in parent item
			} else if(codes[1].equalsIgnoreCase("MEETING")) {
				// handled in parent item
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
				//fragment = new SocialEvilManualBook();
				fragment = EBookFragment.getEBookFragment("socialEvilManual", "4대 사회악 근절 전담부대 매뉴얼", "4대악 근절 매뉴얼");
			} 
		} else if (codes[0].equalsIgnoreCase("SETTINGS")) {
			// handled in parent item
		}
		
		if(fragment == null) {
			fragment = new ContentFragment(item.code());
		}
		
		if (fragment != null)
			switchFragment(fragment);
		
		return true;
	}

	protected Context getContext() {
		return this.context;
	}
	
	protected LayoutInflater getLayoutInflater() {
		return (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// the meat of switching the above fragment
		private void switchFragment(Fragment fragment) {
			MainActivity.sharedActivity().switchContent(fragment);
		}
	
		
	public static List<MenuListItem> makeMenuList()	{
		List<MenuListItem> menus = new ArrayList<MenuListItem>();
		
		MenuListItem member		= new MenuListItem ( R.string.memberListTitle,	R.drawable.icon_people, 	"member" );
			member.addChild( new MenuListItem(R.string.memberListTitle, 			R.drawable.icon_people,				"member:MemberList"));
			member.addChild( new MenuListItem(R.string.memberFavoriteTitle, 		R.drawable.icon_star,				"member:Favorite"));
			menus.add(member);
			
		MenuListItem document	= new MenuListItem ( R.string.documentTitle,	R.drawable.icon_document, 	"document" );
			document.addChild( new MenuListItem(R.string.documentFavoriteTitle,	 	R.drawable.icon_document_star, "document:Favorite"));
			document.addChild( new MenuListItem(R.string.documentReceivedTitle, 		R.drawable.icon_document_received,	"document:Received"));
			document.addChild( new MenuListItem(R.string.documentDepartedTitle, 		R.drawable.icon_document_departed,	"document:Departed"));
			menus.add(document);
			
		MenuListItem survey		= new MenuListItem ( R.string.surveyTitle,			R.drawable.icon_pie_graph, "survey" );
			survey.addChild( new MenuListItem(R.string.surveyReceivedTitle, 		R.drawable.icon_pie_graph_received,	"survey:Received"));
			survey.addChild( new MenuListItem(R.string.surveyDepartedTitle, 		R.drawable.icon_pie_graph_departed,	"survey:Departed"));
			menus.add(survey);
			
		MenuListItem command	= new MenuListItem ( R.string.commandTitle,		R.drawable.icon_arrow_side,	"chat:Command" );
			menus.add(command);
		
		MenuListItem meeting	= new MenuListItem ( R.string.meetingTitle,		R.drawable.icon_circle,		"chat:Meeting" );
			menus.add(meeting);
		
		MenuListItem library	= new MenuListItem ( R.string.libraryTitle,		R.drawable.icon_folder,		"library" );
			library.addChild( new MenuListItem(R.string.library_fieldManual, 		R.drawable.icon_folder,				"member:MemberList"));
			library.addChild( new MenuListItem(R.string.library_socialEvilManual,	R.drawable.icon_folder,				"library:SocialEvil"));
			menus.add(library);
			
		MenuListItem settings	= new MenuListItem ( R.string.settingsTitle,	R.drawable.icon_gear,		"settings" );
			menus.add(settings);
		
		MenuListItem bugReport	= new MenuListItem ( R.string.bugReport,		R.drawable.icon_mail,		"bug:report" );
			menus.add(bugReport);
		
		return menus;
	}

}
