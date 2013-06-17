package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class DocumentListFragment extends RomeoFragment {
	// Fragment Static Variables
	private static DocumentListFragment _departedFragment = null;
	private static DocumentListFragment _receivedFragment = null;
	private static DocumentListFragment _favoriteFragment = null;

	
	// Constructor
	public DocumentListFragment () {
		this(Document.TYPE_RECEIVED);
	}
	
	public DocumentListFragment (int subType) {
		super(subType);
	}
	
	public static DocumentListFragment documentFragment(int subType) {
		DocumentListFragment f = null;
		if(subType == Document.TYPE_DEPARTED) {
			if( _departedFragment == null)
				_departedFragment = new DocumentListFragment(subType);
			f = _departedFragment;
		} else if(subType == Document.TYPE_FAVORITE) {
			if( _favoriteFragment == null)
				_favoriteFragment = new DocumentListFragment(subType);
			f = _favoriteFragment;			
		} else if(subType == Document.TYPE_RECEIVED) {
			if(_receivedFragment == null)
				_receivedFragment = new DocumentListFragment(subType);
			f = _receivedFragment;
		} 
		return f;
	}
	
	// Manage List View
	public DocumentListView getListView() {
		View view = ((ViewGroup)getView());
		DocumentListView lv = null;
		
		if(view!=null) {
			lv = (DocumentListView)view.findViewById(R.id.documentListView);
		}
		
		return lv;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	// Fragment Life-cycle
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(subType == Document.TYPE_DEPARTED) {
			_departedFragment = null;
		} else if(subType ==Document.TYPE_FAVORITE) {
			_favoriteFragment = null;
		} else if (subType == Document.TYPE_RECEIVED) {
			_receivedFragment = null;
		}
	}
	
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		OnClickListener lbbOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
	
		View view = null;
		switch(this.subType) {
		case Document.TYPE_RECEIVED :
			view = inflater.inflate(R.layout.document_fragment, container, false);
			initNavigationBar(
							view, 
							R.string.documentReceivedTitle, 
							true, 
							false, 
							R.string.menu, 
							R.string.dummy, 
							lbbOnClickListener, null);

			break;
		case Document.TYPE_DEPARTED :
			view = inflater.inflate(R.layout.document_fragment, container, false);
			initNavigationBar(
					view, 
					R.string.documentDepartedTitle, 
					true, 
					false, 
					R.string.menu, 
					R.string.dummy, 
					lbbOnClickListener, null);
			break;
		case Document.TYPE_FAVORITE :
			view = inflater.inflate(R.layout.document_fragment, container, false);
			initNavigationBar(
					view, 
					R.string.documentFavoriteTitle, 
					true, 
					false, 
					R.string.menu, 
					R.string.dummy, 
					lbbOnClickListener, null);
			break;
		}
		
		listView = (DocumentListView)initListViewWithType(this.subType, R.id.documentListView, view);
		
		return view;
	}
	
	
	
	// Message Receiving	: in OTHER THREAD
	public static void receive(Document document) {
		if(_receivedFragment != null) {
			View view = _receivedFragment.getView();
			DocumentListView dlv = null;
			if(view!=null) {
				ViewGroup layout = (ViewGroup) view.findViewById(R.id.rootLayout);
				dlv = (DocumentListView)layout.findViewById(R.id.documentListView);
			}
			
			final DocumentListView lv = dlv;
			if(dlv != null) {
				_receivedFragment.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// DB
						lv.refresh();	
					}
				});
			}
			
		}
	}
}
