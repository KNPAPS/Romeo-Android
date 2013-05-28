package kr.go.KNPA.Romeo.Base;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * MVC 중 VIEW 역할을 하는 클래스들의 원형.\n 기본적으로 navigation_bar.xml이 포함되어 있는 Fragment
 * layout을 대상으로 한다.\n 이 클래스의 자식 클래스를 생성하여 VIEW를 출력하는 Fragment는 BaseListener를
 * 상속받은, 자식 클래스 내의 Concrete Listener를 구현해서\n VIEW 단에서 일어나는 이벤트에 대한 처리를 할 수 있다.\n
 * setLeftNavBarBtnText(), setRightNavBarBtnText()를 이용하여 버튼의 텍스트를 설정할 수 있고,\n 만약
 * 아무 텍스트도 설정하지 않는다면 자동으로 해당 버튼은 invisible 상태로 변경되어 출력되지 않는다.
 */
public abstract class BaseLayout {

	/**
	 * navigation_bar.xml의 왼쪽, 오른쪽 버튼에 대한 클릭 리스너
	 */
	protected abstract interface BaseListener {
		void onLeftNavBarBtnClick();

		void onRightNavBarBtnClick();
	}

	protected Context		mContext;
	protected BaseListener	mBaseListener;

	// ! layout xml file id
	protected int			mLayoutResourceId;

	protected Button		mLeftNavBarBtn, mRightNavBarBtn;
	protected TextView		mNavBarTitleTV;

	public void setNavBarTitleTV(int resourceId)
	{
		mNavBarTitleTV.setText(resourceId);
		mNavBarTitleTV.setVisibility(View.VISIBLE);
	}

	public void setNavBarTitleTV(String title)
	{
		mNavBarTitleTV.setText(title);
		mNavBarTitleTV.setVisibility(View.VISIBLE);
	}

	public void setLeftNavBarBtnText(int resourceId)
	{
		mLeftNavBarBtn.setText(resourceId);
		mLeftNavBarBtn.setVisibility(View.VISIBLE);
	}

	public void setLeftNavBarBtnText(String text)
	{
		mLeftNavBarBtn.setText(text);
		mLeftNavBarBtn.setVisibility(View.VISIBLE);
	}

	public void setRightNavBarBtnText(int resourceId)
	{
		mRightNavBarBtn.setText(resourceId);
		mRightNavBarBtn.setVisibility(View.VISIBLE);
	}

	public void setRightNavBarBtnText(String text)
	{
		mRightNavBarBtn.setText(text);
		mRightNavBarBtn.setVisibility(View.VISIBLE);
	}

	public Activity getActivity()
	{
		return (Activity) mContext;
	}

	protected void setBaseListener(BaseListener l)
	{
		mBaseListener = l;

		if (mBaseListener != null)
		{
			mLeftNavBarBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					mBaseListener.onLeftNavBarBtnClick();
				}
			});

			mRightNavBarBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					mBaseListener.onRightNavBarBtnClick();
				}
			});
		}
	}
}
