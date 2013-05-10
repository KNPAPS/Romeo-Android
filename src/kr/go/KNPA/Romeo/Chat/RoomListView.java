package kr.go.KNPA.Romeo.Chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * RoomListController로 진입하면 보게되는 ListView이다. 방 목록을 포함하고 있다.
 */
public class RoomListView extends ListView {

	/** 
	 * @name Constructors
	 * @{
	 */
	public RoomListView(Context context, AttributeSet attrs)
	{	
		super(context, attrs);
	}
	
	public RoomListView(Context context, AttributeSet attrs, int defStyle)
	{	
		super(context, attrs, defStyle);
	}
	/** @} */
}
