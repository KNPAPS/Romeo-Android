package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class DocumentDetailFragment extends Fragment {
	private Document document;
	private Context context;
	public int type;
	public DocumentDetailFragment() {
	}
	
	public DocumentDetailFragment(Document document, int type) {
		super();
		this.document = document;
		this.type = type;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		document.setChecked(getActivity());
		DocumentFragment.documentFragment(type).getListView().refresh();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Intent intent = getIntent();
		//Bundle b = intent.getExtras();
		//this.document = b.getParcelable("document");
		this.context = getActivity();
		View view = inflater.inflate(R.layout.document_detail, null, false);
		
		// Navigation Bar
		ViewGroup navBar = (ViewGroup)view.findViewById(R.id.navigationBar);
		TextView navBarTitleView = (TextView)navBar.findViewById(R.id.title);
		String navBarTitle = getString(R.string.documentTitle);
		navBarTitleView.setText(navBarTitle);
		
		// BarButton Click Event
		Button lbb = (Button)navBar.findViewById(R.id.left_bar_button);
		lbb.setText(R.string.menu);
		
		Button rbb = (Button)navBar.findViewById(R.id.right_bar_button);
		rbb.setText(R.string.forward);
		
		if(lbb.getVisibility() == View.VISIBLE) {
			lbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MainActivity.sharedActivity().toggle();
				}
			});
		}
		
		if(rbb.getVisibility() == View.VISIBLE) {
			rbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DocumentForwardFragment fragment = new DocumentForwardFragment(document);
					MainActivity.sharedActivity().pushContent(fragment);
				}
			});
		}
		
		LinearLayout forwardsLL = (LinearLayout)view.findViewById(R.id.forwards);
		
		ArrayList<HashMap<String, Object>> forwards = this.document.forwards;
		
		if(forwards != null) {
			HashMap<String, Object> forward = null;
			
			for(int i=0 ; i< forwards.size() ; i++) {
				View forwardView = inflater.inflate(R.layout.document_forward, forwardsLL, false);
				forward = forwards.get(i);
				
				// 전달자 정보
				User u = User.getUserWithIdx( (String)forward.get(Document.FWD_FORWARDER_IDX) );
				String fForwarder = u.department.nameFull + " " + User.RANK[u.rank] + " " + u.name;
				TextView fForwarderTV = (TextView)forwardView.findViewById(R.id.forwarder);
				fForwarderTV.setText(fForwarder);
				
				// 수신 시간 정보
				String fArrivalDT = Formatter.timeStampToStringInRegularFormat( (Long)forward.get(Document.FWD_ARRIVAL_TS) , context);
				TextView fArrivalDTTV = (TextView)forwardView.findViewById(R.id.arrivalDT);
				fArrivalDTTV.setText(fArrivalDT);
				
				// 코멘트
				String fContent = (String)forward.get(Document.FWD_CONTENT);
				TextView fContentTV = (TextView)forwardView.findViewById(R.id.content);
				fContentTV.setText(fContent);
				
				forwardsLL.addView(forwardView);
			}
		}
		
		LinearLayout metaData = (LinearLayout)view.findViewById(R.id.metadata);
		TextView titleTV = (TextView)metaData.findViewById(R.id.title);
		titleTV.setText(this.document.title);
		
		TextView  receivedDTTV = (TextView)metaData.findViewById(R.id.receivedDT);
		String receivedDT = Formatter.timeStampToStringInRegularFormat(this.document.TS, getActivity());
		receivedDTTV.setText(receivedDT);
		
		TextView senderTV = (TextView)metaData.findViewById(R.id.sender);
		User user = this.document.sender;
		String sender = user.department.nameFull + " " + User.RANK[user.rank] +" "  + user.name;
		senderTV.setText(sender);
		
		ExpandableListView filesELV = (ExpandableListView)metaData.findViewById(R.id.fileList);
		final ArrayList<HashMap<String, String>> fileListCover = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> title = new HashMap<String, String>();
		title.put("key", "파일 목록");
		fileListCover.add(title);
		
		final ArrayList<HashMap<String, String>> fileListContent = new ArrayList<HashMap<String, String>>());
		
		final ArrayList<HashMap<String, Object>> fileList = document.files;
		
		filesELV.setAdapter(
				new SimpleExpandableListAdapter(
						getActivity(), 
						fileCover, 
						android.R.layout.simple_expandable_list_item_2, 
						groupFrom, groupTo, childData, childLayout, childFrom, childTo) BaseExpandableListAdapter(getActivity(), fileCover, fileCover));
		
		
		// TODO
		TextView contentTV = (TextView)view.findViewById(R.id.content);
		String content = this.document.content;
		contentTV.setText(content);
		
		
		final Button favorite = (Button)view.findViewById(R.id.favorite);
		
		int favoriteBackground = (this.document.favorite ? R.drawable.star_active : R.drawable.star_gray);
		favorite.setBackgroundResource(favoriteBackground);
		favorite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 정보를 업데이트하고 DB 에 등록한다.
				document.toggleFavorite(context);
				
				// 버튼 모양을 바꾼다.
				int _favoriteBackground = (document.favorite ? R.drawable.star_active : R.drawable.star_gray);
				favorite.setBackgroundResource(_favoriteBackground);
				
				// 리스트 리로드??
				DocumentFragment.documentFragment(type).getListView().refresh();
			}
		});

		
		
		return view;
	}

}
