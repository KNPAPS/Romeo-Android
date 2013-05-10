package kr.go.KNPA.Romeo.Base;

import kr.go.KNPA.Romeo.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * MVC 중 VIEW 역할을 하는 클래스들의 원형.\n
 * 기본적으로 navigation_bar.xml이 포함되어 있는 Fragment layout을 대상으로 한다.\n
 * 이 클래스의 자식 클래스를 생성하여 VIEW를 출력하는 Fragment는 BaseListener를 상속받은, 자식 클래스 내의 Concrete Listener를 구현해서\n
 * VIEW 단에서 일어나는 이벤트에 대한 처리를 할 수 있다.\n
 * setLeftNavBarBtnText(), setRightNavBarBtnText()를 이용하여 버튼의 텍스트를 설정할 수 있고,\n
 * 만약 아무 텍스트도 설정하지 않는다면 자동으로 해당 버튼은 invisible 상태로 변경되어 출력되지 않는다.
 */
public abstract class BaseLayout {
	
	/**
	 * navigation_bar.xml의 왼쪽, 오른쪽 버튼에 대한 클릭 리스너
	 */
	protected abstract interface BaseListener 
	{
		void onLeftNavBarBtnClick();
		void onRightNavBarBtnClick();
	}
	
	private boolean mInitiated = false;
	protected Context mContext;
	private BaseListener mBaseListener;
	
	//! layout xml file id
	protected int mLayoutResourceId;
	//! fragment root viewgroup.
	protected View mRoot;
	protected Button mLeftNavBarBtn, mRightNavBarBtn;
	protected TextView mNavBarTitleTV;
	
	//! 네비게이션 바 버튼들의 출력 여부. set...NavBarBtnText()를 호출하면 자동으로 true로 바뀐다. 
	private boolean mLeftNavBarBtnVisible=false, mRightNavBarBtnVisible=false;

	public BaseLayout(Context context, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutResourceId) 
	{
		mContext = context;
		mLayoutResourceId = layoutResourceId;
		mRoot = (View) inflater.inflate(mLayoutResourceId, container, false);
		mLeftNavBarBtn = (Button)mRoot.findViewById(R.id.left_bar_button);
		mRightNavBarBtn = (Button)mRoot.findViewById(R.id.right_bar_button);
		mNavBarTitleTV = (TextView)mRoot.findViewById(R.id.title);
	}
	
	public void setNavBarTitleTV(int resourceId)
	{
		mNavBarTitleTV.setText(resourceId);
	}
	
	public void setNavBarTitleTV(String title) 
	{
		mNavBarTitleTV.setText(title);
	}
		
	public void setLeftNavBarBtnText(int resourceId)
	{ 
		mLeftNavBarBtn.setText(resourceId);
		mLeftNavBarBtnVisible = true;
	}
	
	public void setLeftNavBarBtnText(String text) 
	{
		mLeftNavBarBtn.setText(text); 
		mLeftNavBarBtnVisible = true;
	}
	
	public void setRightNavBarBtnText(int resourceId)
	{ 
		mRightNavBarBtn.setText(resourceId); 
		mRightNavBarBtnVisible = true;
	}
	
	public void setRightNavBarBtnText(String text) 
	{
		mRightNavBarBtn.setText(text);
		mRightNavBarBtnVisible = true;
	}
	
	public Activity getActivity() 
	{ 
		return (Activity) mContext; 
	}
	
	/**
	 * 모든 설정을 완료한 후 이 메소드를 호출하여 생성된 VIEW를 리턴한다.
	 * @return inflated view
	 */
	public View getView()
	{
		if (mInitiated == false)
		{
			initNavBar();
			mInitiated = true;
		}
		return mRoot;
	}
	
	protected void setBaseListener(BaseListener l)
	{
		mBaseListener = l;
	}
	
	private void initNavBar(){
		mLeftNavBarBtn.setVisibility( mLeftNavBarBtnVisible==true ? View.VISIBLE : View.INVISIBLE );
		mRightNavBarBtn.setVisibility( mRightNavBarBtnVisible==true ? View.VISIBLE : View.INVISIBLE );
		
		if ( mBaseListener != null ) 
		{
			mLeftNavBarBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) 
				{
					mBaseListener.onLeftNavBarBtnClick();
				}
			});
			
			mRightNavBarBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) 
				{
					mBaseListener.onRightNavBarBtnClick();
				}
			});
		}
	}
}
