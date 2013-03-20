package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DocumentForwardFragment extends Fragment {

	public Document document;
	private Fragment fragment;
	
	private EditText receiversET;
	private Button receiversSearchBT;
	private EditText contentET;
	private ArrayList<User> receivers;
	public DocumentForwardFragment() {
		fragment = this;
	}

	
	public DocumentForwardFragment(Document document) {
		fragment = this;
		this.document = document;

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		receivers = new ArrayList<User>();

		View view = inflater.inflate(R.layout.document_forward_compose, null, false);

		String navBarTitle = null; // TODO //b.getString("title");
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
					forwardDocument(v);
					MainActivity.sharedActivity().popContent(fragment);
				}
			});
		}
		OnClickListener callSearchActivity = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(getActivity(), "리시버", Toast.LENGTH_SHORT).show();
				callMemberSearchActivity();
				
			}
		};
		receiversET = (EditText)view.findViewById(R.id.receivers);
		receiversET.setOnClickListener(callSearchActivity);
		receiversSearchBT = (Button)view.findViewById(R.id.receivers_search);
		receiversSearchBT.setOnClickListener(callSearchActivity);
		contentET = (EditText)view.findViewById(R.id.content);
		
		return view;
	}
	
	private void forwardDocument(View v) {
		
		// Appdix에 att 추가
		Document fwdDocument = document.clone();
		
		HashMap<String,String> forward = new HashMap<String,String>();
		forward.put("forwarder", ""+UserInfo.getUserIdx(getActivity()));
		forward.put("TS", ""+System.currentTimeMillis());
		forward.put("content", contentET.getText().toString());
		fwdDocument.appendix.addForward(forward);
		
		fwdDocument.receivers = receivers;
		
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
				Toast.makeText(getActivity(), "Activity Result Error", Toast.LENGTH_SHORT).show();
			} else {
				//data.getExtras().get;
				Toast.makeText(getActivity(), "Activity Result Success", Toast.LENGTH_SHORT).show();
				
				long[] receiversIdx = data.getExtras().getLongArray("receivers");
				
				ArrayList<User> newUsers = new ArrayList<User>();
				for(int i=0; i< receiversIdx.length; i++ ){
					User user = User.getUserWithIdx(receiversIdx[i]);
					// TODO 이미 선택되어 잇는 사람은 ..
					if(receivers.contains(user)) continue;
					newUsers.add(user);
				}
				receivers.addAll(newUsers);
				
				if(receivers.size() > 0) {
					User fReceiver = receivers.get(0);
					receiversET.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name);
				} else if (receivers.size() > 1) {
					User fReceiver = receivers.get(0);
					receiversET.setText(User.RANK[fReceiver.rank]+" "+fReceiver.name+" 등 "+receivers.size()+"명");
				} else {
					receiversET.setText("선택된 사용자가 없습니다.");
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	
}
