package kr.go.KNPA.Romeo.Base;

import kr.go.KNPA.Romeo.R;
import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

public abstract class ActivityLayout extends BaseLayout {

	public ActivityLayout(Activity activity, int layoutResourceId)
	{
		mContext = (Context) activity;
		mLayoutResourceId = layoutResourceId;
		activity.setContentView(layoutResourceId);

		mLeftNavBarBtn = (Button) activity.findViewById(R.id.left_bar_button);
		mRightNavBarBtn = (Button) activity.findViewById(R.id.right_bar_button);
		mNavBarTitleTV = (TextView) activity.findViewById(R.id.title);
	}
}
