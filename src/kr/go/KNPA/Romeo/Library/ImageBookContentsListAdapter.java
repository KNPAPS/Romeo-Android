package kr.go.KNPA.Romeo.Library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

public class ImageBookContentsListAdapter implements ExpandableListAdapter, OnGroupClickListener, OnChildClickListener {
	List<Map<String, String>> groupData;
	List<List<Map<String, String>>> childData;
	
	protected Context context;
	protected ViewPager pager;
	protected RomeoDialog contents;
	
	protected static final String NO_CHILD = "NOCHILD"; 
	protected static final String KEY_CHILD_TITLE = "title";
	protected static final String KEY_GROUP_TITLE = "section";
	
	final public void init(ViewPager pager, RomeoDialog contents) {
		this.pager = pager;
		this.contents = contents;
	}
	
	public ImageBookContentsListAdapter(Context context) {
		this.context = context;
	}

	public void setGroupData(List<Map<String, String>> groupData) {	this.groupData = groupData;	}
	public void setChildData(List<List<Map<String, String>>> childData) { this.childData = childData; }

	@Override	public boolean areAllItemsEnabled() {	return true;	}
	@Override	public Object getChild(int groupPosition, int childPosition) {	return childData.get(groupPosition).get(childPosition);	}
	@Override	public long getChildId(int groupPosition, int childPosition) {	return getChild(groupPosition, childPosition).hashCode();	}
	@Override	public int getChildrenCount(int groupPosition) {	return childData.get(groupPosition).size();	}
	@Override	public long getCombinedChildId(long groupId, long childId) {	return ((groupId + childId)+"").hashCode();		}
	@Override	public long getCombinedGroupId(long groupId) {	return (groupId + "").hashCode();	}
	@Override	public Object getGroup(int groupPosition) {	return groupData.get(groupPosition);	}
	@Override	public int getGroupCount() {	return groupData.size();	}
	@Override	public long getGroupId(int groupPosition) {	return getGroup(groupPosition).hashCode();	}
	@Override	public boolean hasStableIds() {	return false;	}
	@Override	public boolean isChildSelectable(int groupPosition, int childPosition) {	return true;	}
	@Override	public boolean isEmpty() {	return false;	}

	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		HashMap<String, String> child = (HashMap<String, String>)getChild(groupPosition, childPosition);
		convertView  = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
									.inflate(R.layout.dialog_menu_cell, parent, false);
		
		TextView titleTV = (TextView)convertView.findViewById(R.id.title);
		titleTV.setText(child.get(KEY_CHILD_TITLE));
		return convertView;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,	View convertView, ViewGroup parent) {
		HashMap<String, String> group = (HashMap<String, String>)getGroup(groupPosition);
		convertView  = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.dialog_menu_cell, parent, false);
		TextView titleTV = (TextView)convertView.findViewById(R.id.title);
		titleTV.setText(group.get(KEY_GROUP_TITLE));
		
		return convertView;
	}
	
	@Override	public void onGroupCollapsed(int groupPosition) {}
	@Override	public void onGroupExpanded(int groupPosition) {}
	@Override	public void registerDataSetObserver(DataSetObserver observer) {}
	@Override	public void unregisterDataSetObserver(DataSetObserver observer) {}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		int count = getCountUntilGroup(groupPosition);
//		switch(groupPosition) {
//			case 1 :	// 제2장 유형별 법규 적용		(14)
//			case 2 :	// 제3장 관련법령 요약 해설	(6)
//			case 5 :	// 참고 (3)
//				pager.setCurrentItem(count+childPosition);
//				contents.dismiss();
//				return true;
//		}
		
		if( hasChildren(groupPosition) ) {
			pager.setCurrentItem(count+childPosition);
			contents.dismiss();
			return true;
		}
		
		return false;
	}


	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		int count = getCountUntilGroup(groupPosition);
		
//		switch(groupPosition) {
//		
//		case 1 :	// 제2장 유형별 법규 적용		(14)
//		case 2 :	// 제3장 관련법령 요약 해설	(6)
//		case 5 :	// 참고 (3)
//			return false;
//			
//		case 0 :	// 제1장 집회시위 관리 지침	(0)
//		case 3 :	// 제4장 집회시위 관리 지침	(0)
//		case 4 :	// 제5장 집회시위 관리 지침	(0)
//		default:
//			pager.setCurrentItem(count, true);
//			contents.dismiss();
//			return true;	
//		}
		
		if( hasChildren(groupPosition) ) {
			return false;
		} else {
			pager.setCurrentItem(count, true);
			contents.dismiss();
			return true;
		}
		
	}
	
	private int getCountUntilGroup(int groupPosition) {
		int count = 0;
		for(int i=0; i<groupPosition; i++) {
			count += childData.get(i).size();
		}
		return count;
	}
	
	protected boolean hasChildren(int groupPosition) {
		if( groupPosition < childData.size() && 
				childData.get(groupPosition).size() >= 1 && 
				childData.get(groupPosition).get(0).get(KEY_CHILD_TITLE) != null &&
				!childData.get(groupPosition).get(0).get(KEY_CHILD_TITLE).equalsIgnoreCase(NO_CHILD) )
			return true;
		else
			return false;
	}
	
}