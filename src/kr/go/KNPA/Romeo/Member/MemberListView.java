package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 조직도를 나타내는 리스트 뷰이다. 
 * RomeoListView를 상속받는다.
 */
public class MemberListView extends RomeoListView {

	// Adapter Override
	public CellNodeTreeAdapter listAdapter;
	public View searchBar;
	
	private Context context;
	// Constructor
	public MemberListView(Context context) {	
		this(context, null);
		this.context = context;
	}
	public MemberListView(Context context, AttributeSet attrs) 					{	this(context, attrs, 0); this.context = context; }
	public MemberListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle); this.context = context; }

	// Database management
	protected Cursor query() {	return DAO.member(getContext()).getFavoriteList();	}
	
	// View management
	@Override
	public MemberListView initWithType (int subType) {
		
		this.subType = subType;

		if(!(subType == User.TYPE_FAVORITE || subType==User.TYPE_MEMBERLIST || subType==User.TYPE_FAVORITE_SEARCH || subType==User.TYPE_MEMBERLIST_SEARCH)) return null;
	
		if(this.subType == User.TYPE_MEMBERLIST) {
			searchBar = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_bar, null, false); 
			this.addHeaderView(searchBar);
			
			final EditText searchET = (EditText)searchBar.findViewById(R.id.edit);
			final Button submitBT = (Button)searchBar.findViewById(R.id.submit);
			
			searchET.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 	{ /* 눌린 키 반영하기 전 */ }
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) 	{ /* 눌린 키 반영 후 */		}
				
				@Override
				public void afterTextChanged(Editable s) {	/* 결과 */		
					if(s.length() > 0) submitBT.setEnabled(true);
					else	submitBT.setEnabled(false);
				}
			
			});
			
			final Button cancelBT = (Button)searchBar.findViewById(R.id.cancel);
			cancelBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if(getAdapter().equals(listAdapter) == false) {
						setAdapter(listAdapter);
						listAdapter.notifyDataSetChanged();
					}
				}
			});
			
			final Button clearBT = (Button)searchBar.findViewById(R.id.clearEdit);
			clearBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					searchET.setText("");
				}
			});
			
			submitBT.setOnClickListener(goSearch);
			
			
		}
		
		switch(this.subType) {
			case User.TYPE_MEMBERLIST_SEARCH :
				listAdapter = new MemberListSearchAdapter(context);
				this.setOnItemClickListener((MemberListSearchAdapter) listAdapter);
				this.setAdapter(listAdapter);
				break;
				
			case User.TYPE_MEMBERLIST :
				listAdapter = new MemberListAdapter(context);
				this.setOnItemClickListener((MemberListAdapter) listAdapter);
				this.setAdapter(listAdapter);
				break;		
		}
		
		
		if(this.subType == User.TYPE_MEMBERLIST) {
			this.setSelection(1);	
		}
		
	
		return this;
	}
	@Override
	public void onPreExecute() {
		if(this.subType == User.TYPE_FAVORITE || this.subType == User.TYPE_FAVORITE_SEARCH) {
			
		}
		//WaiterView.showDialog(getContext());
		
	}
	@Override
	public void onPostExecute(boolean isValidCursor) {
		if(this.subType == User.TYPE_FAVORITE || this.subType == User.TYPE_FAVORITE_SEARCH) {
			
		}
		//WaiterView.dismissDialog(getContext());
		
	}

	private OnClickListener goSearch = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			WaiterView.showDialog(getContext());
			
			if( searchBar != null) {
				EditText searchET = (EditText)searchBar.findViewById(R.id.edit);
				String searchText = searchET.getText().toString().trim();
				if(searchText != null && searchText.length() > 0) {
					Data reqData = new Data().add(0, KEY.SEARCH.QUERY, searchText);
					Payload request = new Payload().setData(reqData).setEvent(Event.Search.user());
					Connection conn = new Connection().callBack(searchCallback).requestPayload(request).request();
				} else {
					searchCallback.onPostExecute(null);
				}
			}
			
		}
	};
	
	private CallbackEvent<Payload, Integer, Payload> searchCallback = new CallbackEvent<Payload, Integer, Payload>() {
		public void onError(String errorMsg, Exception e) {
			Toast.makeText(getContext(), "검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			WaiterView.dismissDialog(getContext());
		};
		
		public void onPostExecute(Payload result) {
			
			ArrayList<User> users = new ArrayList<User>();
			if(result == null) {
				
			} else {
				if(result.getStatusCode() == StatusCode.SUCCESS) {
					Data resData = result.getData();
					for(int i = 0; i < resData.size(); i++) {
						HashMap<String, Object> _user = resData.get(i);
						
						Department dept = new Department(
															(String)_user.get(KEY.DEPT.IDX),
															(String)_user.get(KEY.DEPT.NAME),
															(String)_user.get(KEY.DEPT.FULL_NAME),
															(String)_user.get(KEY.DEPT.PARENT_IDX)
														);
						
						User user = new User(
												(String)_user.get(KEY.USER.IDX), 
												(String)_user.get(KEY.USER.NAME), 
												(_user.get(KEY.USER.RANK) instanceof String) ?
														Integer.parseInt((String)_user.get(KEY.USER.RANK)) : 
														(Integer)_user.get(KEY.USER.RANK), 
												(String)_user.get(KEY.USER.ROLE), 
												dept);
						users.add(user);
					}
				}
			}
			
			if(result == null || result.getStatusCode() == StatusCode.SUCCESS) {
				SearchResultListAdatper adapter = new SearchResultListAdatper(users);
				setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}	
			WaiterView.dismissDialog(getContext());
		};
	};
	
	private class SearchResultListAdatper extends BaseAdapter {
		private ArrayList<User> users = null;
		
		public SearchResultListAdatper(ArrayList<User> users) {	this.users = users;	}
		
		@Override
		public int getCount() {	return users.size(); }

		@Override
		public Object getItem(int pos) {	return this.users.get(pos);	}

		@Override
		public long getItemId(int pos) {	return this.users.get(pos).idx.hashCode();	}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			User user = (User)getItem(pos);
			if(convertView == null)
				convertView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
								.inflate(R.layout.member_favorite_user_cell, parent, false);
			
			ImageView userPicIV = (ImageView)convertView.findViewById(R.id.userPic);
			//userPicIV.setImageResource(R.id.userPic);
			new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, user.idx, userPicIV);
			
			TextView departmentTV = (TextView)convertView.findViewById(R.id.department);
			departmentTV.setText(user.department.nameFull);
			
			TextView rankTV = (TextView)convertView.findViewById(R.id.rank);
			rankTV.setText(User.RANK[user.rank]);
			
			TextView roleTV = (TextView)convertView.findViewById(R.id.role);
			roleTV.setText(user.role);
			
			TextView nameTV = (TextView)convertView.findViewById(R.id.name);
			nameTV.setText(user.name);
			
			return convertView;
		}
		
		
		
	}
}

