package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.IndexPath;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//Inner Class Member List Adapter //
public class MemberListAdapter extends BaseAdapter implements OnItemClickListener {
	
	private Context context;
	private CellNode _rootNode;
	  
	public int type = User.NOT_SPECIFIED;
	
	// related with SEARCH
	public static int[] search;
	
	public MemberListAdapter() {}
	
	public MemberListAdapter(Context context, int type) {
		this.context = context;
		this.type = type;
		
		this._rootNode = new CellNode().isRoot(true).isUnfolded(true).parent((CellNode)null);
		
		WaiterView.showDialog(context);
		
		ArrayList<Department> deps = MemberManager.sharedManager().getChildDepts(null);
		
		for(int i=0; i< deps.size(); i++) {
			CellNode node = new CellNode().index(i).isRoot(false).isUnfolded(false).parent(this.rootNode()).type(CellNode.CN_DEPARTMENT).idx(deps.get(i).idx);
			this.rootNode().append(node);
		}
		
		WaiterView.dismissDialog(context);
	}
	
	public CellNode rootNode() 	{	return _rootNode;						}
	public void refresh() 		{		/* this.notifyDataSetChanged(); */	}
	
	@Override
	public int getCount() 		{	return this.rootNode().count();			}

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
		
		CellNode node = CellNode.nodeAtIndexPath(this.rootNode(), path);
		
		// TODO : cell Reusing source
		if(node.type() == CellNode.CN_DEPARTMENT) {
			if(this.type == User.TYPE_MEMBERLIST) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_department_cell, parent, false);
			} else if(this.type == User.TYPE_MEMBERLIST_SEARCH) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_department_cell_search, parent, false);
				Button control = (Button)convertView.findViewById(R.id.control);
				control.setTag(path);
				control.setOnClickListener(searchCheck);
				
				switch(node.status()) {
					case CellNode.NCHECK : control.setBackgroundResource(R.drawable.circle); break;
					case CellNode.HCHECK : control.setBackgroundResource(R.drawable.circle_check_gray); break;
					case CellNode.FCHECK : control.setBackgroundResource(R.drawable.circle_check_active); break;
				}

			}
			// TODO : EVENT LISTENER
			department = (Department)model;
			TextView titleTV = (TextView)convertView.findViewById(R.id.title);
			titleTV.setText(department.name);
			
		} else if (node.type() == CellNode.CN_USER) {
			user = (User)model;
			Department uDepartment = user.department;
			String uIdx = user.idx;
			String uName = user.name; 
			String uRole = user.role;
			int uRank = user.rank;

			if(this.type == User.TYPE_MEMBERLIST) {
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_user_cell, parent, false);	
			} else if (this.type == User.TYPE_MEMBERLIST_SEARCH){
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_user_cell_search, parent, false);
				Button control = (Button)convertView.findViewById(R.id.control);
				control.setTag(path);
				control.setOnClickListener(searchCheck);
				
				switch(node.status()) {
					case CellNode.NCHECK : control.setBackgroundResource(R.drawable.circle); break;
					case CellNode.HCHECK : control.setBackgroundResource(R.drawable.circle_check_gray); break;
					case CellNode.FCHECK : control.setBackgroundResource(R.drawable.circle_check_active); break;
				}
			}
			TextView rankTV = (TextView)convertView.findViewById(R.id.rank);
			rankTV.setText(User.RANK[uRank]);
			TextView nameTV = (TextView)convertView.findViewById(R.id.name);
			nameTV.setText(uName);
			TextView roleTV = (TextView)convertView.findViewById(R.id.role);
			roleTV.setText("ROLE");
			TextView departmentTV = (TextView)convertView.findViewById(R.id.department);
			departmentTV.setText(uDepartment.nameFull);
		}
		
		if(node.type() == CellNode.CN_DEPARTMENT || node.type() == CellNode.CN_USER) {
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
		return numberOfRowsOfTypeInSection(CellNode.CN_DEPARTMENT, path);
	}
	
	public int numberOfPlainRowsInSection(IndexPath path) {
		return numberOfRowsOfTypeInSection(CellNode.CN_USER, path);
	}
	
	public CellNode nodeForRowAtIndexPath(IndexPath path) {
		return CellNode.nodeAtIndexPath(this.rootNode(), path);
	}
	
	public int numberOfRowsOfTypeInSection(int type, IndexPath path) {
		int result = -1;
		
		CellNode node = nodeForRowAtIndexPath(path);
		int firstUserCellIndex = -1;
		
		for(int i=0; i< node.children().size(); i++) {
			if(node.children().get(i).type() == CellNode.CN_USER) {
				firstUserCellIndex = i;
				break;
			}
		}
		
		if(type == CellNode.CN_DEPARTMENT) {
			result = firstUserCellIndex;
		} else if(type == CellNode.CN_USER) {
			result = node.children().size() - firstUserCellIndex;
		}
		return result;
	}
	
	public int nodeOrderInTypeWithIndexPath(int type, IndexPath path) {
		int result = -1;
		if(type == CellNode.CN_DEPARTMENT) {
			// Department 후에 Users가 나오므로, Department는 그대로 return해도 무방하다.
			IndexPath.Iterator itr = new IndexPath.Iterator(path);
			result = itr.lastIndex();
		} else if(type == CellNode.CN_USER) {
			IndexPath.Iterator itr = new IndexPath.Iterator(path);
			int lastIndex = itr.lastIndex();
			
			IndexPath parentPath = path.indexPathByRemovingLastIndex();
			CellNode parentNode = nodeForRowAtIndexPath(parentPath);
			int firstUserCellIndex = -1;
			for(int i=0; i< parentNode.children().size(); i++) {
				if(parentNode.children().get(i).type() == CellNode.CN_USER) {
					firstUserCellIndex = i;
					break;
				}
			}
			
			result =  lastIndex - firstUserCellIndex;
		}
		return result;
	}
	private int getNodeTypeAtIndexPath(IndexPath path) {
		CellNode node = CellNode.nodeAtIndexPath(this.rootNode(), path);
		return node.type();
	}
	public Object objectForRowAtIndexPath(IndexPath path) {
		Object obj = null;
		
		CellNode node = CellNode.nodeAtIndexPath(this.rootNode(), path);
		String idx = node.idx();
		int objectType = node.type();	//getNodeTypeAtIndexPath(path);
		
		if(objectType == CellNode.CN_DEPARTMENT) {
			obj = (Object)MemberManager.sharedManager().getDeptartment(idx);
		} else if (objectType == CellNode.CN_USER){
			obj = (Object)MemberManager.sharedManager().getUser(idx);
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
		
		CellNode cn = this.rootNode();
		
		int cnt = 0;
		int li = 0;
		while( true ){
			CellNode _cn = cn.children().get(li); 
			
			int _cnt = cnt + _cn.count(); 
			
			if(_cnt >= (pos+1)) { // cnt = x, _cnt = x+y (x+1 ~ x+y) => group1 : 0~x-1, group2 : x ~ x+y-1
				// target is in this element tree
				if((cnt + 1) == (pos+1) ) { // cn.size() == 0;
					// 기존 cnt에 하나만 더한 것이 pos 값과 같다면, 현재 element를 선택한 것이다.
					// child가 존재했다면 _cnt > pos 였을 것이고, child가 존재하지 않았다면 _cnt == pos 였을 것이다.
					path = _cn.indexPath();
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
			CellNode cn = this.rootNode();
			int l = paths[li];
			
			
			for(int i=0; i< li; i++) {
				int _l = paths[li];
				cn = cn.children().get(_l);
			}
			
			for(int i=0; i < paths[l]; i++) {
				cnt += cn.children().get(i).count();
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
		
		//final WaiterView waiter = (WaiterView)view.findViewById(R.id.waiter);
		IndexPath path = getIndexPathFromPosition(position);
		//IndexPath.Iterator itr = new IndexPath.Iterator(path);

		
		
		final CellNode nodeClicked = CellNode.nodeAtIndexPath(this.rootNode(), path);
		if(nodeClicked.type() == CellNode.CN_USER) {	
		// node의 type이 USER이면 상세안내창 띄우기
			Intent intent = new Intent(this.context, MemberDetailActivity.class);

			Bundle b = new Bundle();
			b.putString(MemberDetailActivity.KEY_IDX, ((User)getItem(position)).idx );
			b.putInt(MemberDetailActivity.KEY_IDX_TYPE, MemberDetailActivity.IDX_TYPE_USER);
			intent.putExtras(b);	
			
			this.context.startActivity(intent);
			
		} else if(nodeClicked.type() == CellNode.CN_DEPARTMENT) {
		// node의 type이 DEPARTMENT이면
			if(nodeClicked.isUnfolded() == false) {
			// unfolded == false 이면
				if(nodeClicked.children() != null && nodeClicked.children().size() >0) {
				// 숨겨진 children이 있으면.
					nodeClicked.isUnfolded(true);
				} else {
					WaiterView.showDialog(context);
					//waiter.setVisibility(View.VISIBLE);
					//waiter.setVisibility(View.VISIBLE);
					//waiter.setAlpha(254);
					//((View)waiter.getParent()).invalidate();
					//waiter.invalidate();
					//waiter.setAnimation(50, 1000);
					
				// 숨겨진 children이 없으면.
					final String deptIdx = nodeClicked.idx();
					
					final Handler getDepartmentHandler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);
							
							//waiter.setVisibility(View.INVISIBLE);
							WaiterView.dismissDialog(context);
							notifyDataSetChanged();
						}
					};
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ArrayList<Department> deps = MemberManager.sharedManager().getChildDepts(deptIdx);
							
							int status;
							switch(nodeClicked.status()) {
								case CellNode.HCHECK : status = CellNode.NCHECK; break;	// NEVER REACH
								case CellNode.FCHECK : status = CellNode.FCHECK; break;
								default :
								case CellNode.NCHECK : status = CellNode.NCHECK; break;
							}
							
							for(int i=0; i<deps.size(); i++) {
								// clickedNode.child()가 없으면 오류가 난다. => CellNode에 CellNode.child() 호출시 null이면 new ArrayList<CellNode>를 할당하도록 코드를 추가했다.
								CellNode node = new CellNode().type(CellNode.CN_DEPARTMENT).idx( deps.get(i).idx ).status(status).isRoot(false).isUnfolded(false).index( nodeClicked.children().size() ).parent(nodeClicked);
								nodeClicked.append(node);
							}
							
							ArrayList<User> users = MemberManager.sharedManager().getDeptMembers(deptIdx, false);
							for(int i=0; i<users.size(); i++) {
								CellNode node = new CellNode().type(CellNode.CN_USER).idx( users.get(i).idx ).status(status).isRoot(false).isUnfolded(false).index( nodeClicked.children().size() ).parent(nodeClicked);
								nodeClicked.append(node);
							}
												
							nodeClicked.isUnfolded(true);
							
							getDepartmentHandler.sendMessage(getDepartmentHandler.obtainMessage());
						}
					}).start();
					
					
				}
				
			} else {
			// unfolded == true이면,
				nodeClicked.isUnfolded(false);
			}
		}
		
		
		// view referesh
		this.notifyDataSetChanged();
	}
	
	
	
	private final OnClickListener searchCheck = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			IndexPath path = (IndexPath)v.getTag();
			CellNode node = CellNode.nodeAtIndexPath(rootNode(), path);
			node.check();
			notifyDataSetChanged();
		}
	};
}