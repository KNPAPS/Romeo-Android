package kr.go.KNPA.Romeo.Survey;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NotScrollableViewPager extends ViewPager {

	protected boolean scrollable = true;
	
	public NotScrollableViewPager(Context context) {
		super(context);
	}

	public NotScrollableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;  
	}
	
	public boolean getScrollable() {
		return this.scrollable;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return scrollable ? super.onInterceptTouchEvent(arg0) : false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return scrollable ? super.onTouchEvent(arg0) : false;
	}
}
