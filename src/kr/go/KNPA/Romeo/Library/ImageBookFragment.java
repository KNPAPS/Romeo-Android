package kr.go.KNPA.Romeo.Library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

abstract public class ImageBookFragment extends Fragment {
	protected final static String DELIMITER = "#";
	private ViewPager pager;
	private HashMap<Integer, ArrayList<String>> book;
	private static int nPages = 0; 
	private RomeoDialog contents; 
	
	public String fullTitle;
	public String shortTitle;
	protected ImageBookContentsListAdapter contentsAdapter;
	
	public ImageBookFragment() {
		this.fullTitle = getFullTitle();
		this.shortTitle = getShortTitle();
	}
	
	abstract protected String getBasePath();
	abstract protected String getFullTitle();
	abstract protected String getShortTitle();
	abstract List<Map<String, String>> initGroupData();
	abstract List<List<Map<String, String>>> initChildData();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AssetManager am = getResources().getAssets();
		String[] list = null;
		
		try {
			list = am.list(getBasePath());
		} catch (IOException e) {
		}
		
		nPages = list.length;
		book = new HashMap<Integer, ArrayList<String>>();
		ArrayList<String> chapter = null;
		
		for( int i=0; i<list.length; i++) {
			String fileName = list[i];
			String[] segments = fileName.split(DELIMITER);
			
			int chapterNumber = Integer.parseInt(segments[0]);
			
			if( book.containsKey( (Integer)chapterNumber  ) == false) {
				chapter = new ArrayList<String>();
				book.put(chapterNumber, chapter);
			} else {
				chapter = book.get((Integer) chapterNumber );
			}
			
			chapter.add(fileName);
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.imagebook_fragment, container, false); 
		
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
		
		initNavigationBar(view, this.shortTitle, true, true, "메뉴", "목차", lbbOnClickListener, rbbOnClickListener);
		
		pager = (ViewPager)view.findViewById(R.id.pager);
		pager.setAdapter(new ImageBookAdapter(getActivity(), getBasePath(), nPages, book));
		
		/*
		 pager.setCurrentItem(n);
		  
		 */
		
		
		
		
		return view;
	}

	private void showContentsListDialog() {
		// AlertDialog. setAdapter, ArrayAdapter<String> setCancelable(true);
		
		contentsAdapter = new ImageBookContentsListAdapter(getActivity());
		
		contentsAdapter.setGroupData( initGroupData() );
		contentsAdapter.setChildData( initChildData() );
	//	ExpandableListView contentsView = new ExpandableListView(getActivity());
	//	contentsView.setAdapter(contentsAapter);
	//	contentsView.setOnGroupClickListener(contentsAapter);
	//	contentsView.setOnChildClickListener(contentsAapter);
		
		contents = new RomeoDialog.Builder(getActivity())
											  //.setCancelable(true)
											  .setTitle(this.fullTitle)
											  .setAdapter(contentsAdapter, contentsAdapter, contentsAdapter)
											  //.setAdapter((ListAdapter) contentsAapter, contentsAapter)
											  .create();
		contentsAdapter.init(pager, contents);
		contents.show();
		//contents.requestWindowFeature(Window.Fe)
	}

	@Override
	public void onResume() {
		super.onResume();
		showContentsListDialog();
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
		
		if(lbb.getVisibility() == View.VISIBLE) lbb.setOnClickListener(lbbOnClickListener);
		if(rbb.getVisibility() == View.VISIBLE) rbb.setOnClickListener(rbbOnClickListener);
	}
}
