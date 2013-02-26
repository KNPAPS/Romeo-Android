package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.ChatListView;
import kr.go.KNPA.Romeo.Chat.RoomListView;
import kr.go.KNPA.Romeo.Util.DBManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DocumentFragment extends Fragment {
	// Fragment Static Variables
	private static DocumentFragment _departedFragment = null;
	private static DocumentFragment _receivedFragment = null;
	private static DocumentFragment _favoriteFragment = null;

	// Database
	private DBManager dbManager;
	private SQLiteDatabase db;

	// Variables
	public int type = Document.NOT_SPECIFIED;
	
	// Constructor
	public DocumentFragment () {
		this(Document.TYPE_RECEIVED);
	}
	
	public DocumentFragment (int type) {
		this.type = type;
	}
	
	public static DocumentFragment documentFragment(int type) {
		DocumentFragment f = null;
		if(type == Document.TYPE_DEPARTED) {
			if( _departedFragment == null)
				_departedFragment = new DocumentFragment(type);
			f = _departedFragment;
		} else if(type == Document.TYPE_FAVORITE) {
			if( _favoriteFragment == null)
				_favoriteFragment = new DocumentFragment(type);
			f = _favoriteFragment;			
		} else if(type == Document.TYPE_RECEIVED) {
			if(_receivedFragment == null)
				_receivedFragment = new DocumentFragment(type);
			f = _receivedFragment;
		} 
		return f;
	}
	
	// Fragment Life-cycle
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		dbManager = new DBManager(getActivity());
		db = dbManager.getWritableDatabase();

		DocumentListView lv = getListView();
		lv.setDatabase(db);
		lv.refresh();
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		DocumentListView lv = getListView();
		lv.unsetDatabase();
		db.close();
		db = null;
		dbManager.close();
		dbManager = null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(type == Document.TYPE_DEPARTED) {
			_departedFragment = null;
		} else if(type ==Document.TYPE_FAVORITE) {
			_favoriteFragment = null;
		} else if (type == Document.TYPE_RECEIVED) {
			_receivedFragment = null;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return setup(inflater, container, savedInstanceState);
	}

	private View setup(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		String titleText = null;
		String lbbText = null, rbbText = null;
		boolean lbbIsVisible = false;
		boolean rbbIsVisible = false;
		
		switch(this.type) {
		case Document.TYPE_RECEIVED :
			view = inflater.inflate(R.layout.document_fragment, container, false);
			titleText = getString(R.string.documentReceivedTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.dummy);
			lbbIsVisible = true;
			rbbIsVisible = false;
			break;
		case Document.TYPE_DEPARTED :
			view = inflater.inflate(R.layout.document_fragment, container, false);
			titleText = getString(R.string.documentDepartedTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.dummy);
			lbbIsVisible = true;
			rbbIsVisible = false;
			break;
		case Document.TYPE_FAVORITE : 
			view = inflater.inflate(R.layout.document_fragment, container, false);
			titleText = getString(R.string.documentFavoriteTitle);
			lbbText = getString(R.string.menu);
			rbbText = getString(R.string.dummy);
			lbbIsVisible = true;
			rbbIsVisible = false;
		}

		if(view!=null) {
			DocumentListView dlv = (DocumentListView)view.findViewById(R.id.documentListView);
			dlv.setType(this.type);
		}
		
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbIsVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbIsVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbText);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbText);	}
		
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText(titleText);
		
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
					
				}
			});
		}
		
		return view;
	}
	
	// Manage List View
	private DocumentListView getListView() {
		View view = ((ViewGroup)getView());
		DocumentListView lv = null;
		
		if(view!=null) {
			lv = (DocumentListView)view.findViewById(R.id.documentListView);
		}
		
		return lv;
	}
	
	// Message Receiving
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
						lv.refresh();	
					}
				});
			}
			
		}
	}
}
