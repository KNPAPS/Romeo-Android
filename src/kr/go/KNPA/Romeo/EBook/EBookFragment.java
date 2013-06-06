package kr.go.KNPA.Romeo.EBook;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EBookFragment extends Fragment {

	private EPUBView epubView = null;
	private String fileName = null;
	private String fullTitle = null;
	private String shortTitle = null;
	
	public EBookFragment(String fileName, String fullTitle, String shortTitle) {
		this.fileName = fileName;
		this.fullTitle = fullTitle;
		this.shortTitle = shortTitle;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.ebook_fragment, container, false); 
		
		OnClickListener lbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		}; 
		
		OnClickListener rbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showContentsListDialog();
			}
		};
		
		initNavigationBar(view, this.fullTitle, true, true, "메뉴", "목차", lbbOnClickListener, rbbOnClickListener);
		
		epubView = (EPUBView)view.findViewById(R.id.epubView);
		epubView.setController(this);
		epubView.initEPUB(fileName);
		
		return view;
	}
	
	void showContentsListDialog() {
		RomeoDialog contents = null;
		EPUBView.ContentListAdapter contentsListAdapter = new EPUBView.ContentListAdapter(getActivity(), this.epubView);
		
		contents = new RomeoDialog.Builder(getActivity())
											  //.setCancelable(true)
											  .setTitle(this.shortTitle)
											  .setAdapter(contentsListAdapter, contentsListAdapter, contentsListAdapter)
											  //.setAdapter((ListAdapter) contentsAapter, contentsAapter)
											  .create();
		contentsListAdapter.setDialog(contents);
		contents.show();
	}

	
	protected void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		
		Button lbb = (Button)parentView.findViewById(R.id.left_bar_button);
		Button rbb = (Button)parentView.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbTitle);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbTitle);	}
		
		TextView titleView = (TextView)parentView.findViewById(R.id.title);
		titleView.setText(titleText);
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		
		if(lbb.getVisibility() == View.VISIBLE) lbb.setOnClickListener(lbbOnClickListener);
		if(rbb.getVisibility() == View.VISIBLE) rbb.setOnClickListener(rbbOnClickListener);
	}
}
