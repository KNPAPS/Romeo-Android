package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Survey.SurveyComposeFragment;
import kr.go.KNPA.Romeo.Survey.SurveyFragment;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.IndexPath;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//Inner Class Member List Adapter //
public class MemberListAdapter extends CellNodeTreeAdapter implements OnItemClickListener, OnItemLongClickListener {
	
	private ArrayList<String> exeptionList;
	
	// related with SEARCH
	public static int[] search;

	private static Handler	mHandler = new Handler();
	
	// Constructor
	public MemberListAdapter(final Context context) {
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
	
	
	// Adapter

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
		
		final CellNode node = CellNode.nodeAtIndexPath(this.rootNode(), path);
		
		if(convertView == null || getItemViewType(position) != node.type()) {			// re-usable test
			switch(node.type()) {
			
			case CellNode.CN_DEPARTMENT :			
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_department_cell, parent, false);	break;
				
			case CellNode.CN_USER :
				convertView = LayoutInflater.from(this.context).inflate(R.layout.member_user_cell, parent, false);			break;
				
			}
		}
		
		if(node.type() == CellNode.CN_DEPARTMENT) {
			department = (Department)model;
			TextView titleTV = (TextView)convertView.findViewById(R.id.title);
			titleTV.setText(department.name);
			
			if(node.isUnfolded() == true) {
				convertView.setBackgroundResource(R.drawable.list_cell_highlight_lighter);
			} else {
				convertView.setBackgroundResource(R.drawable.list_cell_highlight);
			}
			
		} else if (node.type() == CellNode.CN_USER) {
			user = (User)model;
			Department uDepartment = user.department;
			String uIdx = user.idx;
			String uName = user.name; 
			String uRole = user.role; uRole = ( uRole != null ) ? uRole : "";  
			int uRank = user.rank;
			
			ImageView userPicIV = (ImageView)convertView.findViewById(R.id.user_pic);
			userPicIV.setImageResource(R.drawable.user_pic_default);
			new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, uIdx, userPicIV);
			
			TextView rankTV = (TextView)convertView.findViewById(R.id.rank);
			rankTV.setText(User.RANK[uRank]);
			
			TextView nameTV = (TextView)convertView.findViewById(R.id.name);
			nameTV.setText(uName);
			
			TextView roleTV = (TextView)convertView.findViewById(R.id.role);
			roleTV.setText("("+ uRole +")");
			
			TextView departmentTV = (TextView)convertView.findViewById(R.id.department);
			departmentTV.setText(uDepartment.nameFull);
			
			convertView.setBackgroundResource(R.drawable.list_cell_highlight);
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
		// parent : AdapterView의 속성을 모두 사용할 수 있다.
		// view : 클릭한 row의 view
		// position : 클릭한 row의 position
		// l_position : 클릭한 row의 long Type의 position을 반환
		
		// 클릭된 셀의 position을 이용하여 indexpath를 알아낸다.
		
		//final WaiterView waiter = (WaiterView)view.findViewById(R.id.waiter);
	
		position--;
		IndexPath path = getIndexPathFromPosition(position);
		
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
			
			if(nodeClicked.isUnfolded() == true) {
				nodeClicked.isUnfolded(false);	// unfolded == true이면,
				
			} else {
				// unfolded == false 이면
				if(nodeClicked.children() != null && nodeClicked.children().size() >0)
					nodeClicked.isUnfolded(true);	// 숨겨진 children이 있으면.
				else
					getSubNodes(nodeClicked);	// 숨겨진 children이 없으면.						
				
			}
			
		}
		
		
		// view referesh
		this.notifyDataSetChanged();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l_position)
	{
		IndexPath path = getIndexPathFromPosition(position-1);
		Object model = objectForRowAtIndexPath(path);
		
		
		final CellNode node = CellNode.nodeAtIndexPath(this.rootNode(), path);
		
		
		if(node.type() == CellNode.CN_DEPARTMENT) {
			Department department = (Department)model;
		
			RomeoDialog.Builder chooseDlg = new RomeoDialog.Builder(context);
			chooseDlg.setTitle("단체 전송");
	
			ArrayList<String> array = new ArrayList<String>();
			array.add(context.getResources().getString(R.string.commandTitle));
			array.add(context.getResources().getString(R.string.meetingTitle));
			array.add(context.getResources().getString(R.string.surveyTitle));
	
			ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(context, R.layout.dialog_menu_cell2, array);
			final String deptIdx = department.idx;
			Log.d("test","onlongclick"+deptIdx);
			chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, final int which)
				{
					
					final ProgressDialog pd = ProgressDialog.show(context, "사용자 검색", "잠시만 기다려주세요..");
					new Thread(){
						public void run() 
						{
							Log.d("test","in thread"+deptIdx);
							final ArrayList<User> members = MemberManager.sharedManager().getDeptMembers(deptIdx, true);
							
							if (members == null)
							{
								mHandler.post(new Runnable(){
									public void run() 
									{
										Toast.makeText(context, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
										pd.dismiss();
									}
								});
								return;
							}
							
							if (members.size()==0) 				
							{
								mHandler.post(new Runnable(){
									public void run() 
									{
										Toast.makeText(context, "선택된 부서에 속한 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
										pd.dismiss();
									}
								});
								return;
							}

							switch(which)
							{
							case 0:
								final Room room = new Room();
								room.addChatters(members);
								room.setType(Chat.TYPE_COMMAND);
								
								mHandler.post(new Runnable(){
									public void run() 
									{
										pd.dismiss();
										MainActivity.sharedActivity().goRoomFragment(Chat.TYPE_COMMAND, room);
									}
								});
								break;
							case 1:
								final Room room2 = new Room();
								room2.addChatters(members);
								room2.setType(Chat.TYPE_MEETING);
								
								mHandler.post(new Runnable(){
									public void run() 
									{
										pd.dismiss();
										MainActivity.sharedActivity().goRoomFragment(Chat.TYPE_MEETING, room2);
									}
								});
								break;
							case 2:
								
								mHandler.post(new Runnable(){
									public void run() 
									{
										pd.dismiss();
										SurveyFragment survFragment = new SurveyFragment(Survey.TYPE_DEPARTED);
										MainActivity.sharedActivity().getSupportFragmentManager().beginTransaction()
										.replace(R.id.content_frame, survFragment, survFragment.getClass().getSimpleName()).commit();
										MainActivity.sharedActivity().getSlidingMenu().showContent();
										
										ArrayList<String> receiversIdx = new ArrayList<String>();
										for (int i=0; i<members.size(); i++)
										{
											receiversIdx.add(members.get(i).idx);
										}
										
										SurveyComposeFragment fragment = new SurveyComposeFragment(receiversIdx);
										MainActivity.sharedActivity().getSupportFragmentManager().beginTransaction()
										.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName()).commit();
									}
								});
								
								break;
							}
							

							
						}
					}.start();
					
					dialog.dismiss();
				}
	
			});
	
			chooseDlg.setCancelable(true);
			chooseDlg.show();
		}
		
		return false;
	}
	
	@Override
	protected void getSubNodes(final CellNode nodeClicked) {
		WaiterView.showDialog(context);
				
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String deptIdx = nodeClicked.idx();
				ArrayList<Department> deps = MemberManager.sharedManager().getChildDepts(deptIdx);
				
				for(int i=0; i<deps.size(); i++) {
					CellNode node = new CellNode()
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
											.type(CellNode.CN_USER)
											.idx( users.get(i).idx )
											.isRoot(false)
											.isUnfolded(false)
											.index( nodeClicked.children().size() )
											.parent(nodeClicked);
					nodeClicked.append(node);
				}
									
				nodeClicked.isUnfolded(true);
				
				
				mHandler.post(new Runnable(){
					@Override
					public void run()
					{
						WaiterView.dismissDialog(context);
						notifyDataSetChanged();
					}
				});
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
}