package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.DBManager;
import kr.go.KNPA.Romeo.Util.IndexPath;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MemberListView extends ListView {

	// Database
	private SQLiteDatabase db;
	public String tableName = null;
	
	// Variables
	public static Department rootDepartment = null;
	public int type = User.NOT_SPECIFIED;
	private Context context = null;
	
	// Adapter
	public ListAdapter listAdapter;
	
	// Constructor
	public MemberListView(Context context) {
		this(context, null);
	}

	public MemberListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MemberListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}
	
	
	// Database management
	public void setDatabase(SQLiteDatabase db) {
		this.db = db;
	}
	
	public void unsetDatabase() {
		this.db = null;
	}
	
	protected Cursor selectAll() {
		String sql = "SELECT * FROM "+this.tableName+";"; // sectionizer 를 위해 정렬을 한다.
		
		Cursor c = db.rawQuery(sql, null);
		return c;
	}
	
	// View management
	public void setType (int type) {
		this.type = type;

		if(!(type == User.TYPE_FAVORITE || type==User.TYPE_MEMBERLIST)) {
			return;
		}
		
		final Context ctx = this.context;
		
		switch(this.type) {
		case User.TYPE_FAVORITE :
			tableName = DBManager.TABLE_MEMBER;
			Cursor c = selectAll();
			if(c.getCount() == 0) {
				this.setBackgroundResource(R.drawable.empty_set_background);
			} else {
				this.setBackgroundResource(R.color.light);
			}
			FavoriteMemberAdapter fmAdapter = new FavoriteMemberAdapter(ctx, c, false);
			
			this.setOnItemClickListener(fmAdapter);
			this.setAdapter(fmAdapter);
			break;
		default :
		case User.TYPE_MEMBERLIST :
			if(rootDepartment == null) {
				try {
					MemberManager.sharedManager().getMembers();
				} catch(RuntimeException e) {
					throw e;
				}
				rootDepartment = Department.root();
			}	
			MemberListAdapter adapter = new MemberListAdapter();
			this.setOnItemClickListener(adapter);
			this.setAdapter(adapter);
			
			break;		
		}
		
		
	}
	
	public void refresh() {
		if(listAdapter == null) return;
		if(listAdapter instanceof SimpleSectionAdapter) {
			((SimpleSectionAdapter)listAdapter).notifyDataSetChanged();
		} else if(listAdapter instanceof MemberListAdapter) {
			((MemberListAdapter)listAdapter).notifyDataSetChanged();
		}
		
	}
	
	// Inner Class Favorite Member Adapter //
	private class FavoriteMemberAdapter extends CursorAdapter implements OnItemClickListener {
		public FavoriteMemberAdapter(Context ctx, Cursor c, boolean autoRequery) {
			super(ctx, c, autoRequery);
		}
		@Override
		public View newView(Context ctx, Cursor c, ViewGroup parent) {
			String idxs = c.getString(c.getColumnIndex("idxs"));
			long TS = c.getLong(c.getColumnIndex("TS"));
			String title = c.getString(c.getColumnIndex("title"));
			boolean isGroup = c.getInt(c.getColumnIndex("isGroup")) == 1 ? true : false;
			
			LayoutInflater inflater = LayoutInflater.from(ctx);
			View v = null;
			if(isGroup) { 
				v = inflater.inflate(R.layout.member_favorite_group_cell, parent,false);
			} else {
				v = inflater.inflate(R.layout.member_favorite_user_cell, parent,false);
			}
			return v; 
		}
		
		@Override
		public void bindView(View v, Context ctx, Cursor c) {
			String idxs = c.getString(c.getColumnIndex("idxs"));
			long TS = c.getLong(c.getColumnIndex("TS"));
			String title = c.getString(c.getColumnIndex("title"));
			boolean isGroup = c.getInt(c.getColumnIndex("isGroup")) == 1 ? true : false;
			
			ImageView userPicIV= (ImageView)v.findViewById(R.id.userPic);
			TextView departmentTV= (TextView)v.findViewById(R.id.department);
			TextView rankTV= (TextView)v.findViewById(R.id.rank);
			TextView nameTV= (TextView)v.findViewById(R.id.name);
			TextView roleTV= (TextView)v.findViewById(R.id.role);
			Button goDetail = null;
			if(isGroup) {
				goDetail = (Button)v.findViewById(R.id.goDetail);
			}
			
			String department = "";
			String rank = "";
			String name = "";
			String role = "";
			
			departmentTV.setText(department);
			rankTV.setText(rank);
			nameTV.setText(name);
			roleTV.setText(role);
			
			if(isGroup) {
				goDetail.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO
					}
				});
			}
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long l_position) {
			Intent intent = new Intent(getContext(), MemberDetailActivity.class);
			//intent.putExtra("KEY", VALUE); TODO
			int userIdx = MemberDetailActivity.NOT_SPECIFIED;
			intent.putExtra("idx", 1);
			getContext().startActivity(intent);
			
		}
	}
	
	// Inner Class Member List Adapter //
	private class MemberListAdapter extends BaseAdapter implements OnItemClickListener {
		
		private CellNode models = new CellNode();
		
		public MemberListAdapter() {
			ArrayList<Department> deps = rootDepartment.departments;

			models.isRoot = true;
			models.setUnfolded(true);
			for(int i=0; i<deps.size(); i++) {
				CellNode node = new CellNode.Builder().unfolded(true)
													  .type(CellNode.CELLNODE_DEPARTMENT)
													  .parentIndexPath(null)
													  .index(i)
													  .unfolded(false)
													  .build();
				models.add(node);
			}
		}
		
		@Override
		public int getCount() {
			return models.count();
		}

		@Override
		public Object getItem(int position) {
			IndexPath path = getIndexPathFromPosition(position);
			return objectForRowAtIndexPath(path);
		}

		@Override
		public long getItemId(int position) {
			return getIndexPathFromPosition(position).indexPathToLong();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			IndexPath path = getIndexPathFromPosition(position);

			Object model = objectForRowAtIndexPath(path);
			
			Department department = null;
			User user = null;
			
			CellNode node = CellNode.nodeAtIndexPath(models, path);
			
			// TODO : cell Reusing source
			if(node.type == CellNode.CELLNODE_DEPARTMENT) {
				department = (Department)model;
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.member_department_cell, parent, false);
				TextView titleTV = (TextView)convertView.findViewById(R.id.title);
				titleTV.setText(department.title);
				
			} else if (node.type == CellNode.CELLNODE_USER) {
				user = (User)model;
				int uDepartment = user.department;
				long uIdx = user.idx;
				String[] uLevels = user.levels;
				String uName = user.name;
				int uPicIdx = user.pic;
				int uRank = user.rank;
				long uTS = user.TS;
				
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.member_user_cell, parent, false);
				TextView rankTV = (TextView)convertView.findViewById(R.id.rank);
				rankTV.setText(User.RANK[uRank]);
				TextView nameTV = (TextView)convertView.findViewById(R.id.name);
				nameTV.setText(uName);
				TextView roleTV = (TextView)convertView.findViewById(R.id.role);
				roleTV.setText("ROLE");
				TextView departmentTV = (TextView)convertView.findViewById(R.id.department);
				departmentTV.setText(""+uDepartment);
			}
			
			if(node.type == CellNode.CELLNODE_DEPARTMENT || node.type == CellNode.CELLNODE_USER) {
				final int IdtMargin = 16;
				ImageView siIV = (ImageView)convertView.findViewById(R.id.sub_indicator);
				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) siIV.getLayoutParams();
				int lMargin = (int)(IdtMargin * getContext().getResources().getDisplayMetrics().density + 0.5f);
			    
				lp.setMargins(lp.leftMargin + lMargin*(path.length() -1), lp.topMargin, lp.rightMargin, lp.bottomMargin);
						//new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				
			    if(path.length() == 1) {
			    	siIV.setVisibility(INVISIBLE);
			    }
				siIV.setLayoutParams(lp);
			}	
			return convertView;
		}

		public int numberOfRowsInSection(IndexPath path) {
			return (numberOfFoldingRowsInSection(path)+numberOfPlainRowsInSection(path));
		}
		
		public int numberOfFoldingRowsInSection(IndexPath path) {
			return numberOfRowsOfTypeInSection(CellNode.CELLNODE_DEPARTMENT, path);
		}
		
		public int numberOfPlainRowsInSection(IndexPath path) {
			return numberOfRowsOfTypeInSection(CellNode.CELLNODE_USER, path);
		}
		
		public CellNode nodeForRowAtIndexPath(IndexPath path) {
			return CellNode.nodeAtIndexPath(models, path);
		}
		
		public int numberOfRowsOfTypeInSection(int type, IndexPath path) {
			int result = -1;
			
			CellNode node = nodeForRowAtIndexPath(path);
			int firstUserCellIndex = -1;
			
			for(int i=0; i< node.size(); i++) {
				if(node.get(i).type == CellNode.CELLNODE_USER) {
					firstUserCellIndex = i;
					break;
				}
			}
			
			if(type == CellNode.CELLNODE_DEPARTMENT) {
				result = firstUserCellIndex;
			} else if(type == CellNode.CELLNODE_USER) {
				result = node.size() - firstUserCellIndex;
			}
			return result;
		}
		
		public int nodeOrderInTypeWithIndexPath(int type, IndexPath path) {
			int result = -1;
			if(type == CellNode.CELLNODE_DEPARTMENT) {
				// Department 후에 Users가 나오므로, Department는 그대로 return해도 무방하다.
				IndexPath.Iterator itr = new IndexPath.Iterator(path);
				result = itr.lastIndex();
			} else if(type == CellNode.CELLNODE_USER) {
				IndexPath.Iterator itr = new IndexPath.Iterator(path);
				int lastIndex = itr.lastIndex();
				
				IndexPath parentPath = path.indexPathByRemovingLastIndex();
				CellNode parentNode = nodeForRowAtIndexPath(parentPath);
				int firstUserCellIndex = -1;
				for(int i=0; i< parentNode.size(); i++) {
					if(parentNode.get(i).type == CellNode.CELLNODE_USER) {
						firstUserCellIndex = i;
						break;
					}
				}
				
				result =  lastIndex - firstUserCellIndex;
			}
			return result;
		}
		private int getNodeTypeAtIndexPath(IndexPath path) {
			CellNode node = CellNode.nodeAtIndexPath(models, path);
			return node.type;
		}
		public Object objectForRowAtIndexPath(IndexPath path) {
			Object obj = null;
			
			int objectType = getNodeTypeAtIndexPath(path);
			
			int[] paths = path.getIndexes(null);
			int lastIndex = paths[(paths.length-1)];
			
			Department dep = rootDepartment;
			ArrayList<Department> deps = null;
			for(int i =0; i<(paths.length-1); i++) {
				int index = paths[i];
				deps = dep.departments;
				dep = deps.get(index);
			}

			int order = -1;
			if(objectType == CellNode.CELLNODE_DEPARTMENT) {
				order = nodeOrderInTypeWithIndexPath(CellNode.CELLNODE_DEPARTMENT, path);
				obj = (Object)dep.departments.get(order);
			} else if (objectType == CellNode.CELLNODE_USER) {
				order = nodeOrderInTypeWithIndexPath(CellNode.CELLNODE_USER, path);
				obj = (Object)dep.users.get(order);
			}
			
			return obj;
		}
		
//		public View cellForRowAtIndexPath(IndexPath path) {
//			return null;
//		}
		
//		public String titleForHeaderInSection() {
//			return null;
//		}
		
		@Override
		public int getViewTypeCount() 		{		return 	3;		}
		
		@Override
		public boolean areAllItemsEnabled() {		return 	true;	}
		
		public IndexPath getIndexPathFromPosition(int pos) {
			IndexPath path = null;
			
			
			int[] l = new int[IndexPath.MAX_LENGTH];
			for(int i=0; i<l.length; i++) {
				l[i] = -1;
			}
			
			CellNode cn = models;
			
			int cnt = 0;
			int li = 0;
			while( true ){
				CellNode _cn = cn.get(li).copy(); //
				
				
				int _cnt = cnt + _cn.count(); 
				
				if(_cnt >= (pos+1)) { // cnt = x, _cnt = x+y (x+1 ~ x+y) => group1 : 0~x-1, group2 : x ~ x+y-1
					// target is in this element tree
					if((cnt + 1) == (pos+1) ) { // cn.size() == 0;
						// 기존 cnt에 하나만 더한 것이 pos 값과 같다면, 현재 element를 선택한 것이다.
						// child가 존재했다면 _cnt > pos 였을 것이고, child가 존재하지 않았다면 _cnt == pos 였을 것이다.
						path = _cn.getIndexPath();
						break;
					} else {
						// 그게 아니라면, 하위 오브젝트를 선택한 것이므로,
						// go to children
						cn = _cn;
						_cn = null;
						li = 0;
						cnt = cnt+1;
					
					}
				} else if(_cnt < (pos+1)) {
					// go to next sibling
					li++;
					cnt = _cnt;
				}
				
			}
			
			return path;
		}
		
		public int getPositionFromIndexPath(IndexPath path) {
			int[] paths = path.getIndexes(null);
			int pos = -1;
			
			int cnt = 0;
			
			for(int li=0; li<paths.length; li++) {
				CellNode cn = models;
				int l = paths[li];
				
				
				for(int i=0; i< li; i++) {
					int _l = paths[li];
					cn = cn.get(_l);
				}
				
				for(int i=0; i < paths[l]; i++) {
					cnt += cn.get(i).count();
				}
			}
			
			pos = cnt;
			return pos;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long l_position) {
			// parent : AdapterView의 속성을 모두 사용할 수 있다.
			// view : 클릭한 row의 view
			// position : 클릭한 row의 position
			// l_position : 클릭한 row의 long Type의 position을 반환
			//String tv = (String)parent.getAdapter().getItem(position);
			//android.widget.Toast.makeText(getContext(), tv, android.widget.Toast.LENGTH_SHORT).show();
			//android.widget.Toast.makeText(getContext(), "" + position, android.widget.Toast.LENGTH_SHORT).show();
			
			// 클릭된 셀의 position을 이용하여 indexpath를 알아낸다.
			IndexPath path = getIndexPathFromPosition(position);
			IndexPath.Iterator itr = new IndexPath.Iterator(path);

			CellNode cn = models;
			// indexPath의 index들을 하나하나 따라간다.
			while(itr.hasNextIndex()) {
				Department dp = rootDepartment;
				int idx = itr.nextIndex();
				cn = cn.get(idx);
				
				if(cn.type == CellNode.CELLNODE_USER) {
				// node의 type이 USER이면 상세안내창 띄우기
					Intent intent = new Intent(getContext(), MemberDetailActivity.class);
					//intent.putExtra("KEY", VALUE);	//TODO
					int userIdx = MemberDetailActivity.NOT_SPECIFIED;
					intent.putExtra("idx", 1);
					getContext().startActivity(intent);

				} else if(cn.type == CellNode.CELLNODE_DEPARTMENT) {
				// node의 type이 DEPARTMENT이면 
				
					if(cn.isUnfolded() == false) {
					// unfolded == false 이면
					// 안에 내용물이 있는지 확인하고,
						if(cn.size() > 0) {
						// 있다면 그냥 unfold
							cn.setUnfolded(true);
						} else {
						// 없다면 추가
						// 누른 것은 department이므로, indexpath를 이용하여 해당 아이템을 데이터로부터 가져온다.
							int[] untilNow = itr.getIndexesUntilNow();
							for(int i=0; i< untilNow.length; i++) {
								dp = dp.departments.get(untilNow[i]);
							}
						// 정보를 쭉 읽으며 node에 저장시키고, departments와 users..흐규흐규
							ArrayList<Department> deps = dp.departments;
							ArrayList<User> uss = dp.users;
							
							int indexForChildren = 0;
							for(int i=0; i<deps.size(); i++) {
								CellNode child = new CellNode.Builder().type(CellNode.CELLNODE_DEPARTMENT)
																	   .parentIndexPath(IndexPath.indexPathWithIndexesAndLength(untilNow, untilNow.length))
																	   .index(indexForChildren++)
																	   .unfolded(false)
																	   .build();
								cn.add(child);
							}
							
							for(int i=0; i<uss.size();i++) {
								CellNode child = new CellNode.Builder().type(CellNode.CELLNODE_USER)
																	   .parentIndexPath(IndexPath.indexPathWithIndexesAndLength(untilNow, untilNow.length))
																	   .index(indexForChildren++)
																	   .unfolded(false)
																	   .build();
								cn.add(child);
							}
							
							cn.setUnfolded(true);
						}
					} else if(cn.isUnfolded() == true && (!itr.hasNextIndex()) ){
					// unfolded == true이면,
					// fold 시킨다.
						cn.setUnfolded(false);
					// 일단 하위 트리는 굳이 제거하지 말자.
					}
				}
				// 한단계 안의 index에 대해서도반복
				
			}
			
			// view referesh
			this.notifyDataSetChanged();
			// TODO : register dataset??
		}
		
	}
	
	// Inner Class Cell node MODEL
	static class CellNode extends ArrayList<CellNode> {

		private static final long serialVersionUID = 498188955518204141L;
		public static final int CELLNODE_NULL = -1;
		public static final int CELLNODE_USER = 1;
		public static final int CELLNODE_DEPARTMENT = 2;
		public boolean isRoot = false;
		
		public int type = CELLNODE_NULL;
		private boolean _unfolded;
		private int _index = -1;
		private IndexPath _parentIndexPath = null;
		
		
		public static class Builder {
			private int _type = CELLNODE_NULL;
			private boolean _unfolded;
			private int _index = -1;
			private IndexPath _parentIndexPath = null;
			
			public Builder type(int type) {
				this._type = type;
				return this;
			}
			public Builder unfolded(boolean unfolded) {
				this._unfolded = unfolded;
				return this;
			}
			public Builder index(int index) {
				this._index = index;
				return this;
			}
			public Builder parentIndexPath(IndexPath parentIndexPath) {
				this._parentIndexPath = parentIndexPath;
				return this;
			}
			public CellNode build() {
				CellNode node = new CellNode();
				node.type = this._type;
				node._unfolded = this._unfolded;
				node._index = this._index;
				node._parentIndexPath = this._parentIndexPath;
				return node;
				
			}
		}
		
		public CellNode copy() {
			CellNode newCellNode =  new CellNode.Builder().type(this.type)
										 .parentIndexPath(this._parentIndexPath)
										 .unfolded(this._unfolded)
										 .index(this._index)
										 .build();
			newCellNode.addAll(this);
			
			return newCellNode;
		}
		
		public static CellNode nodeAtIndexPath(CellNode rootNode, IndexPath path) {
			CellNode cn = rootNode;
			IndexPath.Iterator itr = new IndexPath.Iterator(path);
			
			while(itr.hasNextIndex()) {
				cn = cn.get(itr.nextIndex());
			}
			return cn;
		}

		
		public int countIncludeFolded() {
			int result = 0;
			for(int i=0; i< this.size(); i++) {
				result += this.get(i).countIncludeFolded();
			}
			return (result+1);
		}
		
		public int count() {
			int result = 0;
			
			if(isUnfolded() == true) {		  	// UnFolded
				for(int i=0; i< this.size(); i++) {
					result += this.get(i).count();
				}
			} else if(isUnfolded() == false) { 	// Folded
			//	result += 1;
			}
			
			
			if(isRoot != true) return result+1;
			return result;
		}
		
		public boolean isUnfolded() {
			return _unfolded;
		}
		
		public void setUnfolded(boolean unfolded) {
			_unfolded = unfolded;
		}
		
		public IndexPath getIndexPath() {
			int idx = _index;
			
			if(_parentIndexPath == null) {
				return IndexPath.indexPathWithIndex(_index);
			} else {
				return _parentIndexPath.indexPathByAddingIndex(idx);
			}
		}
		
		public void setParentIndexPath(IndexPath path) {
			_parentIndexPath = path;
		}
		public IndexPath getParentIndexPath() {
			return _parentIndexPath;
		}
		public void setIndex(int index) {
			_index = index;
		}
		
	}
	
}
