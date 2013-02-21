package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import kr.go.KNPA.Romeo.R;

public class SampleListView extends ExpandableListView {
	
	static int _i0 = 0;
	static final String TAG = "SampleListView";
	static boolean root = true;
	public Department department = null;
	private Context context = null;
	
	public SampleListView(Context context) {
		this(context, null);
	}

	public SampleListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SampleListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//this(context, attrs, defStyle, null);
	}
	/*
	public SampleListView(Context context, AttributeSet attrs, int defStyle, JSONObject data) {
		super(context,attrs,defStyle);
		if(data == null) {
			data = UserManager.sharedManager().getMembers();
		}
		_i0++;
		
		department = new Department(data, Department.ROOT);
		
		MemberExpandableListAdapter adapter = new MemberExpandableListAdapter(context, department);
		this.setOnChildClickListener((OnChildClickListener)adapter);
		this.setOnGroupClickListener((OnGroupClickListener)adapter);
		this.setAdapter(adapter);
	}

	
	private class MemberExpandableListAdapter extends BaseExpandableListAdapter implements OnGroupClickListener, OnChildClickListener {
		private ArrayList<String> groupList = null;
		private ArrayList<ArrayList<String>> childList = null;
		private LayoutInflater inflater = null;
		private Context c = null;
		private Department d = null;
		private int _i1 = 0;
		private int _i2 = 0;
		private int _i3 = 0;
		private int _i4 = 0;
		private int _i5 = 0;
		private int _i6 = 0;
		private int _i7 = 0;
		private int _i8 = 0;
		private int _i9 = 0;
		private int _i10 = 0;
		private int _i11= 0;
		private int _i12 = 0;

		
		
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			Log.d(TAG, "onGroupClick parent:"+parent+", v:"+v+", groupPosition:"+groupPosition+", id : "+id);
			return false;
		}
		
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			Log.d(TAG, "onChildClick parent:"+parent+", v:"+v+", childPosition:"+childPosition+", id : "+id);
			return false;
		}
		
		
		public MemberExpandableListAdapter(Context context, Department department) {
			super();
			this.c = context;
			this.d = department;
			
			this.inflater = LayoutInflater.from(c);
			this.groupList = makeGroupList();
			this.childList = makeChildList();
			Log.v("MELA", "MELA!!");
		}
		
		private ArrayList<String> makeGroupList() {
			Log.d("ListView", _i0+"th list - makeGroupList's "+ (++_i1)+"th call.");
			ArrayList<String>list = new ArrayList<String>();

			ListIterator<Department> l = d.departments().listIterator();
			while(l.hasNext()) {
				Department _d = l.next();
				list.add(_d.title);
			}

			return list;
		}
		
		private ArrayList<ArrayList<String>> makeChildList(){
			Log.d("ListView", _i0+"th list - makeChildList's "+ (++_i2)+"th call.");
			
			if(true) return new ArrayList<ArrayList<String>>();
			
			ArrayList<ArrayList<String>>list = new ArrayList<ArrayList<String>>();
			ArrayList<String>subList = new ArrayList<String>();

			ListIterator<Department> l = d.departments().listIterator();
			while(l.hasNext()) {
				subList.clear();
				Department _d = l.next();
				ListIterator<Department> _l = _d.departments().listIterator();
				while(_l.hasNext()) {
					Department __d = _l.next();
					subList.add(__d.title);
				}
				list.add(subList);
			}

			return list;
		}
		
		
		@Override
		public String getGroup(int groupPosition) {
			Log.d("ListView", _i0+"th list - getGroup's "+ (++_i3)+"th call.");
			return groupList.get(groupPosition);
		}
		
		@Override
		public int getGroupCount() {
			Log.d("ListView", _i0+"th list - getGroupCount's "+ (++_i4)+"th call.");
			return groupList.size();
		}
		
		@Override
		public long getGroupId(int groupPosition) {
			Log.d("ListView", _i0+"th list - getGroupId's "+ (++_i5)+"th call.");
			return groupPosition;
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			Log.d("ListView", _i0+"th list - getGroupView's "+ (++_i6)+"th call.");
			View v = convertView;
	         
	        //if(v == null){
	            //viewHolder = new ViewHolder();
	            //v = inflater.inflate(R.layout.member_department_cell, parent, false);
	            //viewHolder.tv_groupName = (TextView) v.findViewById(R.id.tv_group);
	            //viewHolder.iv_image = (ImageView) v.findViewById(R.id.iv_image);
	            //v.setTag(viewHolder);
	        //}else{
	        //    viewHolder = (ViewHolder)v.getTag();
	        //}
	        
			
			v = inflater.inflate(R.layout.member_department_cell, parent, false);
			
			
	        // 그룹을 펼칠때와 닫을때 아이콘을 변경해 준다.
	        //if(isExpanded){
	        //    viewHolder.iv_image.setBackgroundColor(Color.GREEN);
	        //}else{
	        //    viewHolder.iv_image.setBackgroundColor(Color.WHITE);
	        //}
	         
	        //viewHolder.tv_groupName.setText(getGroup(groupPosition));
	         
	        return v;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			Log.d("ListView", _i0+"th list - getChild's "+ (++_i7)+" th call.");
			return childList.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			Log.d("ListView", _i0+"th list - getChildId's "+ (++_i8)+" th call.");
			
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			Log.d("ListView", _i0+"th list - getChildView's "+ (++_i9)+" th call.");

			View v = convertView;
	         
	        //if(v == null){
	        //    viewHolder = new ViewHolder();
	        //    v = inflater.inflate(R.layout.list_row, null);
	        //    viewHolder.tv_childName = (TextView) v.findViewById(R.id.tv_child);
	        //    v.setTag(viewHolder);
	        //}else{
	        //    viewHolder = (ViewHolder)v.getTag();
	        //}
	         
	        //viewHolder.tv_childName.setText(getChild(groupPosition, childPosition));
	        
			//v = inflater.inflate(R.layout.member_user_cell, parent, false);
			v = inflater.inflate(R.layout.member_fragment, parent, false);

	        return v;
	        
	        
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			Log.d("ListView", _i0+"th list - getChildrenCount's "+ (++_i10)+" th call.");
			return childList.get(groupPosition).size();
		}

		@Override
		public boolean hasStableIds() {
			Log.d("ListView", _i0+"th list - hasStableIds's "+ (++_i11)+" th call.");
			return true;//return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			Log.d("ListView", _i0+"th list - isChildSelectable's "+ (++_i12)+" th call.");
			return true;	// return false;
		}
		
	}
	*/
}
