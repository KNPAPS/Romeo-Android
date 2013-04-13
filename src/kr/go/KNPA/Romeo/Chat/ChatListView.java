package kr.go.KNPA.Romeo.Chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ChatListView extends ListView {
	// Constants
	private final int NUMBER_OF_INITIAL_RECENT_ITEM = 10;
	
	// Variables
	private Room room;
	private int currentNumberOfRecentItem = NUMBER_OF_INITIAL_RECENT_ITEM;
	private ChatListAdapter listAdapter;

	/**
	 * @name Constructors
	 * Android layout edit tool 때문에 여러 개의 생성자를 만들어 놓음\n
	 * 그렇지만 첫번째 생성자만 사용한다.
	 * @{
	 
	/** 주로 사용하는 생성자
	 * @param context
	 * @param room
	 */
	public ChatListView( Context context, Room room ) { 
		super(context);
		this.room = room;
		listAdapter = new ChatListAdapter(getContext(), null, room.type);
		this.setAdapter(listAdapter);
	}
	
	public ChatListView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public ChatListView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
	}
	/**@}*/
	
	
	public ChatListView setRoom(Room room) { this.room = room; return this; }
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
	
	
}
