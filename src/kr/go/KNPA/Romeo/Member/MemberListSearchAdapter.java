package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.IndexPath;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public class MemberListSearchAdapter extends CellNodeTreeAdapter implements OnItemClickListener {
	
	private ArrayList<String> exeptionList;
	
	// Constructor
	public MemberListSearchAdapter(final Context context) {
		this.context = context;

		init();
	}

	
	@Override
	protected void init() {

		this._rootNode = new CellNode().isRoot(true).isUnfolded(true).parent((CellNode)null);

		WaiterView.showDialog(context);
		final ArrayList<Department> deps = MemberManager.sharedManager().getChildDepts(null);

		for(int i=0; i< deps.size(); i++) {
			CellNode node = new CellNode().index(i).isRoot(false).isUnfolded(false).parent(rootNode()).type(CellNode.CN_DEPARTMENT).idx(deps.get(i).idx);
			rootNode().append(node);
		}
		WaiterView.dismissDialog(context);		
	}
	
	
	public void setExeptionList(ArrayList<String> exeptionList) {
		this.exeptionList = exeptionList;
	}
	
	public void unsetExeptionList() {
		this.exeptionList = null;
	}
	
	@Override	public int 		getCount	() 					{	return	this.rootNode().count();										}
	@Override	public Object 	getItem		(int position) 		{	return	objectForRowAtIndexPath( getIndexPathFromPosition(position) );	}
	@Override	public long 	getItemId	(int position) 		{	return	getIndexPathFromPosition(position).indexPathToLong();			}
	@Override	public int 		getViewTypeCount	() 			{	return	3;																}
	@Override	public boolean 	areAllItemsEnabled	() 			{	return	true;															}	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		

		IndexPath path = getIndexPathFromPosition(position);
		Object model = objectForRowAtIndexPath(path);
		
		Department department = null;
		User user = null;
		
		CellNode node = CellNode.nodeAtIndexPath(this.rootNode(), path);
		
		if(convertView == null || getItemViewType(position) != node.type()) {			// re-usable test
			switch(node.type()) {
			case CellNode.CN_DEPARTMENT :
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_department_cell_search, parent, false);	break;
				
			case CellNode.CN_USER :
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_user_cell_search, parent, false);		break;
				
			}
		}
		
		if(node.type() == CellNode.CN_DEPARTMENT) {
			Button control = (Button)convertView.findViewById(R.id.control);
			control.setTag(path);
			control.setOnClickListener(searchCheck);
			
			switch(node.status()) {
				case CellNode.NCHECK : control.setBackgroundResource(R.drawable.circle); break;
				case CellNode.HCHECK : control.setBackgroundResource(R.drawable.circle_check_gray); break;
				case CellNode.FCHECK : control.setBackgroundResource(R.drawable.circle_check_active); break;
			}

			department = (Department)model;
			TextView titleTV = (TextView)convertView.findViewById(R.id.title);
			titleTV.setText(department.name);
			
			if(node.isUnfolded() == true) {
				convertView.setBackgroundResource(R.color.lighter);
			} else {
				convertView.setBackgroundResource(R.color.white);
			}
			
		} else if (node.type() == CellNode.CN_USER) {
			user = (User)model;
			Department uDepartment = user.department;
			String uIdx = user.idx;
			String uName = user.name; 
			String uRole = user.role; uRole = ( uRole != null ) ? uRole : "";  
			int uRank = user.rank;

			Button control = (Button)convertView.findViewById(R.id.control);
			control.setTag(path);
			control.setOnClickListener(searchCheck);
			
			switch(node.status()) {
				case CellNode.NCHECK : control.setBackgroundResource(R.drawable.circle); break;
				case CellNode.HCHECK : control.setBackgroundResource(R.drawable.circle_check_gray); break;
				case CellNode.FCHECK : control.setBackgroundResource(R.drawable.circle_check_active); break;
			}
			
			ImageView userPicIV = (ImageView)convertView.findViewById(R.id.user_pic);
			new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, uIdx, userPicIV);
			
			TextView rankTV = (TextView)convertView.findViewById(R.id.rank);
			rankTV.setText(User.RANK[uRank]);
			
			TextView nameTV = (TextView)convertView.findViewById(R.id.name);
			nameTV.setText(uName);
			
			TextView roleTV = (TextView)convertView.findViewById(R.id.role);
			roleTV.setText("("+ uRole +")");
			
			TextView departmentTV = (TextView)convertView.findViewById(R.id.department);
			departmentTV.setText(uDepartment.nameFull);
			
			convertView.setBackgroundResource(R.color.white);
		}
		
		if(node.type() == CellNode.CN_DEPARTMENT || node.type() == CellNode.CN_USER) {
			final int IdtMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
			final int lMargin = (int)(IdtMargin * this.context.getResources().getDisplayMetrics().density + 0.5f);
			
			ImageView siIV = (ImageView)convertView.findViewById(R.id.sub_indicator);
			
			LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) siIV.getLayoutParams();
			lp.setMargins(lMargin*(path.length() -1), lp.topMargin, lp.rightMargin, lp.bottomMargin);
			
		    if(path.length() == 1)
		    	siIV.setVisibility(View.INVISIBLE);
		    else
		    	siIV.setVisibility(View.VISIBLE);

			siIV.setLayoutParams(lp);
		}	
		
		return convertView;
	}

	
	// ClickListener
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long l_position) {

		IndexPath path = getIndexPathFromPosition(position);
		
		final CellNode nodeClicked = CellNode.nodeAtIndexPath(this.rootNode(), path);
		
		if(nodeClicked.type() == CellNode.CN_USER) {	
			
			
		} else if(nodeClicked.type() == CellNode.CN_DEPARTMENT) {
		// node의 type이 DEPARTMENT이면
			if(nodeClicked.isUnfolded() == false) {
			// unfolded == false 이면
				if(nodeClicked.children() != null && nodeClicked.children().size() >0) {
				// 숨겨진 children이 있으면.
					nodeClicked.isUnfolded(true);
				} else {
					getSubNodes(nodeClicked);
				}
				
			} else {
			// unfolded == true이면,
				nodeClicked.isUnfolded(false);
			}
		}
		
		
		// view referesh
		this.notifyDataSetChanged();
	}
	
	
	@Override
	protected void getSubNodes(final CellNode nodeClicked) {
	// 숨겨진 children이 없으면.
		WaiterView.showDialog(context);
		
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				WaiterView.dismissDialog(context);
				notifyDataSetChanged();
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int status;
				
				switch(nodeClicked.status()) {
					case CellNode.HCHECK : status = CellNode.NCHECK; break;	// NEVER REACH
					
					case CellNode.FCHECK : status = CellNode.FCHECK; break;
					default :
					case CellNode.NCHECK : status = CellNode.NCHECK; break;
				}
				
				String deptIdx = nodeClicked.idx();
				ArrayList<Department> deps = MemberManager.sharedManager().getChildDepts(deptIdx);
				
				for(int i=0; i<deps.size(); i++) {
					CellNode node = new CellNode()
											.status(status)
											.type(CellNode.CN_DEPARTMENT)
											.idx( deps.get(i).idx )
											.isRoot(false)
											.isUnfolded(false)
											.index( nodeClicked.children().size() )
											.parent(nodeClicked);
					nodeClicked.append(node);
				}
				
				
				ArrayList<User> users = MemberManager.sharedManager().getDeptMembers(deptIdx, false);
				
				for(int i=0; i<users.size(); i++) {
					if(users.get(i).idx.equalsIgnoreCase(UserInfo.getUserIdx(context)) == true)
						continue;
					
					if(exeptionList != null && exeptionList.contains(users.get(i).idx))
						continue;
					
					CellNode node = new CellNode()
											.status(status)
											.type(CellNode.CN_USER)
											.idx( users.get(i).idx )											
											.isRoot(false)
											.isUnfolded(false)
											.index( nodeClicked.children().size() )
											.parent(nodeClicked);
					nodeClicked.append(node);
				}
									
				nodeClicked.isUnfolded(true);
				
				handler.sendMessage(handler.obtainMessage());
			}
		}).start();
		
	}
	
	@Override	
	public int 	getItemViewType(int position) 	{
		if( getItem(position) instanceof Department ) {
			return CellNode.CN_DEPARTMENT;
		} else if (getItem(position) instanceof User) {
			return CellNode.CN_USER;
		} else {
			return Constants.NOT_SPECIFIED;
		}
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
