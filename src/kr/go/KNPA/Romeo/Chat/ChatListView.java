package kr.go.KNPA.Romeo.Chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ChatListView extends ListView {
	
	
	public ChatListView(Context context, AttributeSet attrs) 
	{
		super(context,attrs);
	}
	
	public ChatListView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context,attrs,defStyle);
	}
	
	/**
	 * 맨 아래 채팅부터 세서 idxFromBottom번째 item으로 포커스를 이동 
	 * @param idxFromBottom 0부터 시작하는 idx
	 */
	public void scrollToItem(int idxFromBottom)
	{
		int position = this.getCount()-idxFromBottom;
		this.setSelectionFromTop(position, 0);
	}

	
	
}
