package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.IndexPath;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

//Inner Class Member List Adapter //
public class MemberListAdapter extends BaseAdapter implements OnItemClickListener {
	public Department rootDepartment;
	private Context context;
	private CellNode models = new CellNode();
	public int type = User.NOT_SPECIFIED;
	
	
	// related with SEARCH
	public static int[] search;
	
	public MemberListAdapter() {
		
	}
	
	public MemberListAdapter(Context context, int type, Department root) {
		this.context = context;
		rootDepartment = root;
		this.type = type;
		
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
			if(this.type == User.TYPE_MEMBERLIST) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_department_cell, parent, false);
			} else if(this.type == User.TYPE_MEMBERLIST_SEARCH) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_department_cell_search, parent, false);
				Button control = (Button)convertView.findViewById(R.id.control);
				control.setTag(path);
				control.setOnClickListener(searchCheck);
				
				if(getNodeChecked(path) == 0) {
					control.setBackgroundResource(R.drawable.circle_check_active);
				} else if(getNodeChecked(path) == 1) {
					control.setBackgroundResource(R.drawable.circle_check_gray);
				} else {
					control.setBackgroundResource(R.drawable.circle_check_gray);	// TODO half-active
				}

			}
			// TODO : EVENTLISTENER
			department = (Department)model;
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
			if(this.type == User.TYPE_MEMBERLIST) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_user_cell, parent, false);	
			} else if (this.type == User.TYPE_MEMBERLIST_SEARCH){
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_user_cell_search, parent, false);
				Button control = (Button)convertView.findViewById(R.id.control);
				control.setTag(path);
				control.setOnClickListener(searchCheck);
				
				if(getNodeChecked(path) == 0) {
					control.setBackgroundResource(R.drawable.circle_check_active);
				} else if(getNodeChecked(path) == 1) {
					control.setBackgroundResource(R.drawable.circle_check_gray);
				} else {
					control.setBackgroundResource(R.drawable.circle_check_gray);	// TODO half-active
				}
			}
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
			int lMargin = (int)(IdtMargin * this.context.getResources().getDisplayMetrics().density + 0.5f);
		    
			lp.setMargins(lp.leftMargin + lMargin*(path.length() -1), lp.topMargin, lp.rightMargin, lp.bottomMargin);
					//new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
		    if(path.length() == 1) {
		    	siIV.setVisibility(View.INVISIBLE);
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
				Intent intent = new Intent(this.context, MemberDetailActivity.class);
				Bundle b = new Bundle();
				User user = (User)getItem(position);
				b.putLong("idx", user.idx);
				b.putBoolean("fromFavorite", false);
				intent.putExtras(b);
				this.context.startActivity(intent);

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
	
	
	
	private final OnClickListener searchCheck = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			IndexPath path = (IndexPath)v.getTag();
			int nodeType = getNodeTypeAtIndexPath(path);
			
			if(nodeType == CellNode.CELLNODE_DEPARTMENT) {
				int isChecked = getNodeChecked(path);
				Department model = (Department)objectForRowAtIndexPath(path);
				if(isChecked == 0) {
				// 부서를 체크했을 경우
					model.selected = 1;
					//addIndexToSearchArray(path);
					v.setBackgroundResource(R.drawable.circle_check_active);
					
				// 하위 부서와 사람들까지 모두 선택이 되어야 한다.
					setChildrenNodeChecked(path);
				}  else if(isChecked == 2) {
				// 반-체크 되어있을 경우
				// 체크를 누르면 온전한 체크로 바뀌어야 한다.
					model.selected = 1;
					//addIndexToSearchArray(path);
					v.setBackgroundResource(R.drawable.circle_check_active);
					
				} else if(isChecked == 1) {
				// 완전한 체크인 영우
					model.selected = 0;
					//removeIndexFromSearchArray(path);
					v.setBackgroundResource(R.drawable.circle_check_gray);
					setChildrenNodeUnchecked(path);
				// 체크를 다시 한번 누르면 하위부서와 사람들까지 모두 해지되어야한다.
				}

			} else if (nodeType == CellNode.CELLNODE_USER){
				User model = (User)objectForRowAtIndexPath(path);
				int isChecked = getNodeChecked(path);
				if(isChecked == 0) {
				// 사람을 체크했을경우 체크 -> 부서는 반체크.
					model.selected = 1;
					addIndexToSearchArray(path);
					
					// 하위사람 다 체크 되어있으면 체크로, 아니면 반체크로.
					IndexPath dip = path.indexPathByRemovingLastIndex();
					Department dep = (Department)objectForRowAtIndexPath(path);
					dep.selected = 2;
					// TODO 부서를 반체크화
				} else if (isChecked == 1) {
				// 사람을 체크 해지했을 경우 체크 해지.
					model.selected = 0;
					removeIndexFromSearchArray(path);
				// 다만, 해당 부서가 선택되어있을 경우, 해당 부서는 반체크로 변한다.
					IndexPath dip = path.indexPathByRemovingLastIndex();
					Department dep = (Department)objectForRowAtIndexPath(path);
					dep.selected = 2;
				}
			}
		}
	};
	
	private int getNodeChecked(IndexPath path) {
		int nodeType = getNodeTypeAtIndexPath(path);
		if(nodeType == CellNode.CELLNODE_DEPARTMENT) {
			Department model = (Department)objectForRowAtIndexPath(path);
			return model.selected;
		} else {
			User model = (User)objectForRowAtIndexPath(path);
			return model.selected;
		}
	}
	
	private void setChildrenNodeChecked(IndexPath path) {
		
	}
	private void setChildrenNodeUnchecked(IndexPath path) {
		
	}
	
	private void addIndexToSearchArray(IndexPath path) {
		
	}
	
	private void removeIndexFromSearchArray(IndexPath path) {
		
	}
}