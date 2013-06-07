package kr.go.KNPA.Romeo.EBook;

import java.io.IOException;
import java.io.InputStream;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.CallbackEvent;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import kr.go.KNPA.Romeo.Util.WaiterView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EBookFragment extends Fragment {

	private EPUBView epubView = null;
	private String oldFileName = null;
	private String fileName = null;
	private String fullTitle = null;
	private String shortTitle = null;
	public Book book = null; 
	static private EBookFragment _eBookFragment = null;
	
	public EBookFragment() {}
	
	static public EBookFragment getEBookFragment(String fileName, String fullTitle, String shortTitle) {
		if( EBookFragment._eBookFragment == null )
			EBookFragment._eBookFragment = new EBookFragment();
		
		EBookFragment._eBookFragment.fileName = fileName;
		EBookFragment._eBookFragment.fullTitle = fullTitle;
		EBookFragment._eBookFragment.shortTitle = shortTitle;
		
		
		return EBookFragment._eBookFragment;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		oldFileName = fileName;
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
		initEPUBView();
		
		return view;
	}
	
	private void initEPUBView() {
		epubView.setController(this);
		if(oldFileName == null || !fileName.equalsIgnoreCase(oldFileName)) {
			EBookFragment.loadEPUBBook(getActivity(), fileName, loadEPUBBookCallback);
		} else {
			epubView.initEPUB(EBookFragment.this.book, fileName);
		}
		oldFileName = fileName;
	}
	
	private CallbackEvent<String, Integer, Book> loadEPUBBookCallback = new CallbackEvent<String, Integer, Book>() {
		public void onPreExecute(String params) {
			WaiterView.showDialog(getActivity());
			WaiterView.setTitle("eBook 뷰어를 로드합니다");
		};
		
		@Override
		public void onPostExecute(Book result) {
			WaiterView.dismissDialog(getActivity());
			EBookFragment.this.book = result;
			epubView.initEPUB(EBookFragment.this.book, fileName);
		}
		
		@Override
		public void onError(String errorMsg, Exception e) {
			super.onError(errorMsg, e);
		}
	};
	
	public static void loadEPUBBook(final Context context, final String fileName, final CallbackEvent<String, Integer, Book> cb) {
		final Handler epubLoadHandler = new Handler();
		cb.onPreExecute(fileName);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				AssetManager am = context.getAssets();
				
				Book book = null;
				try {
					InputStream epubIS = am.open(EPUBView.BOOKS_DIR + fileName + ".epub");
					book = (new EpubReader()).readEpub(epubIS);
				} catch(IOException e) {
					cb.onError("eBook을 로드할 수 없습니다.", e);
				}
				
				final Book _b = book;
				epubLoadHandler.post(new Runnable() {
					
					@Override
					public void run() {
						cb.onPostExecute(_b);
					}
				});
				
			}
			
		}).start();
		
		
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
