package kr.go.KNPA.Romeo.Document;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DocumentDetailFragment extends Fragment {
	private Document document;
	private Context context;
	public int subType;
	public DocumentDetailFragment() {
	}
	
	public DocumentDetailFragment(String documentIdx) {
		super();
		
		initView(documentIdx);
	}
	
	private Handler detailHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			_initView();
		};
	};

	@Override
	public void onPause()
	{
		super.onPause();
		DocumentFragment.documentFragment(subType).getListView().refresh();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.document_detail, null, false);
	}

	public void initView(final String documentIdx) {
		WaiterView.showDialog(getActivity());
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				document = new Document(context, documentIdx);
				if(document.favorite == true) {
					subType = Document.TYPE_FAVORITE;
				} else if(document.subType() == Document.TYPE_DEPARTED) {
					subType = Document.TYPE_DEPARTED;
				} else {
					subType = Document.TYPE_RECEIVED;
				}
				
				
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						_initView();
					}
				});
				//detailHandler.sendMessage(detailHandler.obtainMessage());
								
			}//end run
			
		}).start();// end Thread
	}
	

	
	private void _initView() {
		document.setChecked(getActivity());
		View view = getView();
		initNavigationBar(view);
		initForwardView(view);		
		initContentView(view);
		initFilesView(view);
		initFavoriteView(view);
		WaiterView.dismissDialog(getActivity());
	}
	
	private void initNavigationBar(View parent) {
		// Navigation Bar
		ViewGroup navBar = (ViewGroup)parent.findViewById(R.id.navigationBar);
		TextView navBarTitleView = (TextView)navBar.findViewById(R.id.title);
		String navBarTitle = getString(R.string.documentTitle);
		navBarTitleView.setText(navBarTitle);
		
		// BarButton Click Event
		Button lbb = (Button)navBar.findViewById(R.id.left_bar_button);
		lbb.setText(R.string.menu);
		
		Button rbb = (Button)navBar.findViewById(R.id.right_bar_button);
		rbb.setText(R.string.forward);
		
		if(lbb.getVisibility() == View.VISIBLE)
			lbb.setOnClickListener(new OnClickListener() {	@Override	public void onClick(View v) {	MainActivity.sharedActivity().toggle();	}	});
		if(rbb.getVisibility() == View.VISIBLE)
			rbb.setOnClickListener(new OnClickListener() {	@Override	public void onClick(View v) {	MainActivity.sharedActivity().pushContent(new DocumentForwardFragment(document));	}	});
	}
	
	private void initForwardView(View parent) {
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout forwardsLL = (LinearLayout)parent.findViewById(R.id.forwards);
		
		ArrayList<HashMap<String, Object>> forwards = this.document.forwards;
		
		if(forwards != null) {
			
			for(int i=0 ; i< forwards.size() ; i++) {
				ViewGroup forwardView = (ViewGroup)inflater.inflate(R.layout.document_forward, forwardsLL, false);
				final HashMap<String, Object> forward = forwards.get(i);
				
				// 전달자 정보 
				final TextView fForwarderTV = (TextView)forwardView.findViewById(R.id.forwarder);
				final WaiterView spinner = new WaiterView(getActivity());
				
				spinner.substituteView(fForwarderTV);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final User u = User.getUserWithIdx( (String)forward.get(KEY.DOCUMENT.FORWARDER_IDX) );
						
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								String fForwarder = u.department.nameFull + " " + User.RANK[u.rank] + " " + u.name;
								fForwarderTV.setText(fForwarder);
								spinner.restoreView();
							}
						});
					}
				}).start();
				
				// 수신 시간 정보
				String fArrivalDT = Formatter.timeStampToStringInRegularFormat( (Long)forward.get(KEY.DOCUMENT.FORWARD_TS) , context);
				TextView fArrivalDTTV = (TextView)forwardView.findViewById(R.id.tv_arrival_dt);
				fArrivalDTTV.setText(fArrivalDT);
				
				// 코멘트
				String fContent = (String)forward.get(KEY.DOCUMENT.FORWARD_CONTENT);
				TextView fContentTV = (TextView)forwardView.findViewById(R.id.chat_content);
				fContentTV.setText(fContent);
				
				forwardsLL.addView(forwardView);
			}
		}
	}
	
	private void initContentView(View parent) {
		LinearLayout metaData = (LinearLayout)parent.findViewById(R.id.metadata);
		
		TextView titleTV = (TextView)metaData.findViewById(R.id.title);
		titleTV.setText(this.document.title);
		
		TextView  receivedDTTV = (TextView)metaData.findViewById(R.id.receivedDT);
		String receivedDT = Formatter.timeStampToStringInRegularFormat(this.document.TS, getActivity());
		receivedDTTV.setText(receivedDT);
		
		
		
		final TextView senderTV = (TextView)metaData.findViewById(R.id.sender);
		final WaiterView spinner = new WaiterView(getActivity());
        
		spinner.substituteView(senderTV);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final User user = User.getUserWithIdx( document.senderIdx );
				
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String sender = user.department.nameFull + " " + User.RANK[user.rank] +" "  + user.name;
						senderTV.setText(sender);
						spinner.restoreView();
					}
				});
			}
		}).start();
		
		
		
		TextView contentTV = (TextView)parent.findViewById(R.id.chat_content);
		String content = this.document.content;
		contentTV.setText(content);
	}
	
	private void initFilesView(View parent) {

		ExpandableListView filesELV = (ExpandableListView)parent.findViewById(R.id.fileList);
		
		ArrayList<HashMap<String, Object>> fileListCover = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> title = new HashMap<String, Object>();
		title.put("key", "파일 목록");
		fileListCover.add(title);
		
		ArrayList<ArrayList<HashMap<String, Object>>> fileList = new ArrayList<ArrayList<HashMap<String, Object>>>(1);
		fileList.add(document.files);
		
		filesELV.setAdapter(
				new SimpleExpandableListAdapter
					(
						getActivity(), 
						
						fileListCover, 
						android.R.layout.simple_expandable_list_item_1, 
						new String[] { "key" }, 
						new int[] { android.R.id.text1 }, 
						
						fileList, 
						android.R.layout.simple_list_item_1, 
						new String[] { KEY.DOCUMENT.FILE_NAME}, 
						new int[] { android.R.id.text1}
					)); 
		
		
		 // 차일드 클릭 했을 경우 이벤트
        filesELV.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            	// TODO : file Handling
                Toast.makeText(getActivity(), (String)document.files.get(childPosition).get(KEY.DOCUMENT.FILE_NAME), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
	}
	
	private void initFavoriteView(View parent) {
		final Button favorite = (Button)parent.findViewById(R.id.favorite);
		
		int favoriteBackground = (this.document.favorite ? R.drawable.star_active : R.drawable.star_gray);
		favorite.setBackgroundResource(favoriteBackground);
		favorite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				// 정보를 업데이트하고 DB 에 등록한다.
				//WaiterView.showDialog(getActivity());
				final WaiterView waiter = new WaiterView(getActivity());
				
				waiter.substituteView(v);
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)waiter.getLayoutParams();
				lp.rightMargin = lp.width/3;
				lp.topMargin = lp.height/3;
				waiter.setLayoutParams(lp);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						
						
						document.toggleFavorite(context);		
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {

								// 버튼 모양을 바꾼다.
								int _favoriteBackground = (document.favorite ? R.drawable.star_active : R.drawable.star_gray);
								favorite.setBackgroundResource(_favoriteBackground);
								waiter.restoreView();
								//WaiterView.dismissDialog(getActivity());
							}
						});
					}
				}).start();
				
				
				
			}
		});

	}
}
