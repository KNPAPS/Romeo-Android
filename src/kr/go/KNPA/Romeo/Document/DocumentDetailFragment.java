package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;

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
		String navBarTitle = getString(R.string.documentTitle);
		
		View view = inflater.inflate(R.layout.document_detail, null, false);
		
		ViewGroup navBar = (ViewGroup)view.findViewById(R.id.navigationBar);
		
		TextView navBarTitleView = (TextView)navBar.findViewById(R.id.title);
		navBarTitleView.setText(navBarTitle);
		
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
		
		// TODO
		LinearLayout forwardsLL = (LinearLayout)view.findViewById(R.id.forwards);
		
		ArrayList<HashMap<String, String>> forwards = this.document.getForwards();
		
		if(forwards != null) {
			HashMap<String, String> forward = null;
			
			TextView fForwarderTV = null;
			TextView fArrivalDTTV = null;
			TextView fContentTV = null;
			String fForwarder = null;
			String fArrivalDT = null;
			String fContent = null;
			
			for(int i=forwards.size()-1 ; i>=0 ; i--) {
				View forwardView = inflater.inflate(R.layout.document_forward, forwardsLL, false);
				
				forward = forwards.get(i);
				fForwarderTV = (TextView)forwardView.findViewById(R.id.forwarder);
				User u = User.getUserWithIdx( forward.get("forwarder") );
				fForwarder = u.department.nameFull + " " + User.RANK[u.rank] + " " + u.name;
				fForwarderTV.setText(fForwarder);
				
				fArrivalDTTV = (TextView)forwardView.findViewById(R.id.arrivalDT);
				fArrivalDT = Formatter.timeStampToStringInRegularFormat(Long.parseLong(forward.get("TS")), context);
				fArrivalDTTV.setText(fArrivalDT);
				
				fContentTV = (TextView)forwardView.findViewById(R.id.content);
				fContent = forward.get("content");
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
				document.setFavorite(!document.favorite, context);
				
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
