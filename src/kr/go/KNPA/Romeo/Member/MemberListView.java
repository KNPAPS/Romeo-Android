package kr.go.KNPA.Romeo.Member;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * 조직도를 나타내는 리스트 뷰이다. 
 * RomeoListView를 상속받는다.
 */
public class MemberListView extends RomeoListView {

	// Adapter Override
	public MemberListAdapter listAdapter;
	public View searchBar;
	
	private Context context;
	// Constructor
	public MemberListView(Context context) 										{	this(context, null);				}
	public MemberListView(Context context, AttributeSet attrs) 					{	this(context, attrs, 0);			}
	public MemberListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle);	}

	// Database management
	protected Cursor query() {	return DBProcManager.sharedManager(getContext()).member().getFavoriteList();	}
	
	// View management
	@Override
	public MemberListView initWithType (int subType) {
		this.subType = subType;

		if(!(subType == User.TYPE_FAVORITE || subType==User.TYPE_MEMBERLIST || subType==User.TYPE_FAVORITE_SEARCH || subType==User.TYPE_MEMBERLIST_SEARCH)) return null;
	
		if(this.subType == User.TYPE_MEMBERLIST) {
			searchBar = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_bar, null, false); 
			this.addHeaderView(searchBar);
			
			final EditText searchET = (EditText)searchBar.findViewById(R.id.edit);
			final Button clearBT = (Button)searchBar.findViewById(R.id.clearEdit);
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
			case User.TYPE_MEMBERLIST :

				listAdapter = new MemberListAdapter(getContext(), subType);
				this.setOnItemClickListener(listAdapter);
				this.setAdapter(listAdapter);
			
			break;		
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
				Data reqData = new Data().add(0, KEY.SEARCH.QUERY, searchET.getText().toString().trim());
				Payload request = new Payload().setData(reqData).setEvent(Event.Search.User);
				Connection conn = new Connection().callBack(searchCallback).requestPayload(request).request();
			}
			
		}
	};
	
	private CallbackEvent<Payload, Integer, Payload> searchCallback = new CallbackEvent<Payload, Integer, Payload>() {
		public void onError(String errorMsg, Exception e) {
			Toast.makeText(getContext(), "검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			WaiterView.dismissDialog(getContext());
		};
		
		public void onPostExecute(Payload result) {
			// TODO
			WaiterView.dismissDialog(getContext());
		};
	};
	
	private class SearchResultListAdatper extends ArrayAdapter {
		
	}
}

