package kr.go.KNPA.Romeo.Base;

import kr.go.KNPA.Romeo.R;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public abstract class FragmentLayout extends BaseLayout {

	// ! fragment root viewgroup.
	protected View	mRoot;

	public FragmentLayout(Context context, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutResourceId)
	{
		mContext = context;
		mLayoutResourceId = layoutResourceId;
		mRoot = (View) inflater.inflate(mLayoutResourceId, container, false);
		mLeftNavBarBtn = (Button) mRoot.findViewById(R.id.left_bar_button);
		mRightNavBarBtn = (Button) mRoot.findViewById(R.id.right_bar_button);
		mNavBarTitleTV = (TextView) mRoot.findViewById(R.id.title);
	}

	/**
	 * 모든 설정을 완료한 후 이 메소드를 호출하여 생성된 VIEW를 리턴한다.
	 * 
	 * @return inflated view
	 */
	public View getView()
	{
		return mRoot;
	}
}
