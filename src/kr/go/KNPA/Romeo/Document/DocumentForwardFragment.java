package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.Chat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DocumentForwardFragment extends Fragment {

	public Document document;
	private Fragment fragment;
	public DocumentForwardFragment() {
		// TODO Auto-generated constructor stub
		fragment = this;
	}

	
	public DocumentForwardFragment(Document document) {
		fragment = this;
		this.document = document;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		
		EditText receiversET = (EditText)view.findViewById(R.id.receivers);
		Button receiversSearchBT = (Button)view.findViewById(R.id.receivers_search);
		EditText contentET = (EditText)view.findViewById(R.id.contact);
		
		return view;
	}
	
	private void forwardDocument(View v) {
		// TODO
	}
	
}
