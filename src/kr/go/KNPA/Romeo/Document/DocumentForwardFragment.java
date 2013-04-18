package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DocumentForwardFragment extends Fragment {

	public Document document;
	private Fragment fragment;
	
	private EditText receiversET;
	private Button receiversSearchBT;
	private EditText contentET;
	private ArrayList<String> receiversIdx;
	public DocumentForwardFragment() {
		fragment = this;
	}

	
	public DocumentForwardFragment(Document document) {
		fragment = this;
		this.document = document;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// initialize
		receiversIdx = new ArrayList<String>();
		View view = inflater.inflate(R.layout.document_forward_compose, null, false);

		// Navigation Bar
		String navBarTitle = document.title;
		if(navBarTitle == null) navBarTitle = getString(R.string.documentForwardTitle);
		
		ViewGroup navBar = (ViewGroup)view.findViewById(R.id.navigationBar);
		TextView navBarTitleView = (TextView)navBar.findViewById(R.id.title);
		navBarTitleView.setText(navBarTitle);
		
		Button lbb = (Button)navBar.findViewById(R.id.left_bar_button);
		lbb.setText(R.string.menu);
		
		if(lbb.getVisibility() == View.VISIBLE) {
			lbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MainActivity.sharedActivity().toggle();
				}
			});
		}
		
		Button rbb = (Button)navBar.findViewById(R.id.right_bar_button);
		rbb.setText(R.string.done);
		
		if(rbb.getVisibility() == View.VISIBLE) {
			rbb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Forward!!
					forwardDocument(v);
					MainActivity.sharedActivity().popContent();
				}
			});
		}
		
		// Receivers
		OnClickListener callSearchActivity = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(getActivity(), "리시버", Toast.LENGTH_SHORT).show();
				callMemberSearchActivity();
				
			}
		};
		
		// 사용자 입력을 누르면
		receiversET = (EditText)view.findViewById(R.id.receivers);
		receiversET.setOnClickListener(callSearchActivity);
		
		// 사용자 찾기 버튼을 누르면
		receiversSearchBT = (Button)view.findViewById(R.id.receivers_search);
		receiversSearchBT.setOnClickListener(callSearchActivity);
		
		contentET = (EditText)view.findViewById(R.id.content);
		
		return view;
	}
	
	private void forwardDocument(View v) {
		
		// Appdix에 att 추가
		Document fwdDocument = document.clone();
		
		HashMap<String,Object> forward = new HashMap<String,Object>();
		forward.put(KEY.DOCUMENT.FORWARDER_IDX, UserInfo.getUserIdx( getActivity() ));
		forward.put(KEY.DOCUMENT.FORWARD_TS, 	(Long)System.currentTimeMillis()/1000);
		forward.put(KEY.DOCUMENT.FORWARDER_IDX, contentET.getText().toString());
		
		if(fwdDocument.forwards == null)
			fwdDocument.forwards = new ArrayList<HashMap<String, Object>>();
		fwdDocument.forwards.add(forward);
		
		fwdDocument.receiversIdx = receiversIdx;
		
		// Send
		fwdDocument.send(getActivity());
		
	}
	
	private void callMemberSearchActivity() {
		Intent intent = new Intent(getActivity(), MemberSearch.class);
		startActivityForResult(intent, MemberSearch.REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MemberSearch.REQUEST_CODE) {
			if(resultCode != MemberSearch.RESULT_OK) {
				// onError
			} else {
				ArrayList<String> receiversIdxs = data.getExtras().getStringArrayList(MemberSearch.KEY_RESULT_USERS_IDX);
				// 선택한 사람들로 <대체>된다.
				
				ArrayList<String> newUsers = new ArrayList<String>();
				for(int i=0; i< receiversIdxs.size(); i++ ){
					String userIdx = receiversIdxs.get(i);
					//if(receivers.contains(user)) continue;
					newUsers.add(userIdx);
				}
				//receivers.addAll(newUsers);
				receiversIdx = newUsers;
				
				if(receiversIdx.size() > 0) {
					User fReceiver = User.getUserWithIdx(receiversIdx.get(0));
					receiversET.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name);
				} else if (receiversIdx.size() > 1) {
					User fReceiver = User.getUserWithIdx(receiversIdx.get(0));
					receiversET.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name+" 등 "+receiversIdx.size()+"명");
				} else {
					receiversET.setText("선택된 사용자가 없습니다.");
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	
}