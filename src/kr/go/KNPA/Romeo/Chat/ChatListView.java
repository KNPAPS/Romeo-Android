package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ChatListView extends RomeoListView  implements OnItemLongClickListener {
	// Constants
	private final int NUMBER_OF_INITIAL_RECENT_ITEM = 10;
	// Variables
	private Room room;
	private int currentNumberOfRecentItem = NUMBER_OF_INITIAL_RECENT_ITEM;
	
	/**
	 * @name Constructors
	 * Android layout edit tool 때문에 여러 개의 생성자를 만들어 놓음\n
	 * 그렇지만 첫번째 생성자만 사용한다.
	 * @{
	 */
	
	public ChatListView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public ChatListView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
	}
	
	/**@}*/
	
	public ChatListView setRoom(Room room) { 
		this.room = room; 
		((ChatListAdapter)this.listAdapter).setRoom(room);
		return this; 
	}
	public Room getRoom(){ return this.room; } 
	
	public void increaseNumberOfItemsBy(int nItem) {
		this.currentNumberOfRecentItem += nItem;
	}
	
	public void decreaseNumberOfItemsBy(int nItem) { 
		this.currentNumberOfRecentItem = Math.max(this.currentNumberOfRecentItem - nItem, 0); 
	}

	public int getNumberOfItems() { return currentNumberOfRecentItem; }
	
	public void scrollToBottom() {
		this.setSelectionFromTop(this.getCount(), 0);
	}
	
	public ChatListView initWithType(int type){
		listAdapter = new ChatListAdapter(getContext(),null,type);
		this.setAdapter(listAdapter);
		return this;
	}
	
	public void refresh(Cursor c){
		if ( mHandler == null ) {
			mHandler = new ListHandler(this);
		}
		
		if(this.listAdapter == null) return;
		 
		if(this.listAdapter instanceof CursorAdapter) {
			this.setListBackground( c );
			if(c != null) {
				this.listAdapter.changeCursor(c);
				this.listAdapter.notifyDataSetChanged();
			}
		} else {
			this.listAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected Cursor query() {
		return query(getNumberOfItems());
	}
	
	protected Cursor query( int nItems ) {
		return DBProcManager.sharedManager(getContext()).chat().getChatList(room.getRoomCode(), 0, nItems);
	}

	@Override
	public void onPreExecute() {
		
	}

	@Override
	public void onPostExecute(boolean isValidCursor) {
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
	    AlertDialog.Builder chooseDlg = new AlertDialog.Builder(getContext());
	    chooseDlg.setTitle("작업선택");
	    
	    ArrayList<String> array = new ArrayList<String>();
	    array.add("복사");
	    array.add("삭제");
	    
	    ArrayAdapter<String> arrayAdt = new ArrayAdapter<String>(getContext(), R.layout.dialog_menu_cell, array);
	    
	    chooseDlg.setAdapter(arrayAdt, new DialogInterface.OnClickListener(){
	    	@Override
	    	public void onClick(DialogInterface dialog, int which) {
	    		switch(which){
	    		case 0://채팅방 이름 설정
	    			
	    			break;
	    		case 1://삭제
	    			new Thread(){
	    				public void run() {
	    					String chatHash = (String) view.getTag();
	    					DBProcManager.sharedManager(getContext()).chat().deleteChat(chatHash);
	    					final Cursor c = query();
	    					mHandler.post(new Runnable(){
	    						@Override
	    						public void run() {
	    							refresh(c);
	    						}
	    					});
	    				};
	    			}.start();
	    			
	    			break;
	    		}
	    	}
	    });
	    
	    chooseDlg.setCancelable(true);
	    chooseDlg.show();
		return false;
	}
	
	
}
