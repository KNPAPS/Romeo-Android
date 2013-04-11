package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;

public class ChatListView extends RomeoListView {
	// Constants
	private final int NUMBER_OF_INITIAL_RECENT_ITEM = 10;
	
	// Variables
	private Room room;
	private int currentNumberOfRecentItem = NUMBER_OF_INITIAL_RECENT_ITEM;

	// Constructor
	public ChatListView(Context context) 									{	this (context,null);					}
	public ChatListView(Context context, AttributeSet attrs) 				{	this (context, attrs,0);				}
	public ChatListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle);	}
	
	// Initializer
	public void setRoom(Room room) {
		this.room = room;		
		listAdapter = new ChatListAdapter(getContext(), null, false, room.type);
		this.setAdapter(listAdapter);
		scrollToBottom();
	}
	
	// Manage NumberOfItems
	public void increaseNumberOfItemsBy(int nItem) {
		this.currentNumberOfRecentItem += nItem;
	}
	
	public void decreaseNumberOfItemsBy(int nItem) { 
		this.currentNumberOfRecentItem = Math.max(this.currentNumberOfRecentItem - nItem, 0); 
	}

	// Database
	@Override
	protected Cursor query() {
		return query(currentNumberOfRecentItem);
	}

	public Cursor query(int nItems) {
		if(room == null || room.roomCode == null || room.roomCode.trim().length() == 0 )
			return null;
		return DBProcManager.sharedManager(getContext()).chat().getChatList(room.roomCode, 0, nItems);
	}

	// refresh()
	@Override
	public void refresh() {
		super.refresh();
		scrollToBottom();
	}
	
	public void scrollToBottom() {
		this.setSelectionFromTop(this.getCount(), 0);
	}

	@Override
	public ChatListView initWithType(int type) {
		// Do Nothing
		return null;
	}


}
