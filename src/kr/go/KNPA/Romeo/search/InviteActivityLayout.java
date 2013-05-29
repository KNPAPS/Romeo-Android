package kr.go.KNPA.Romeo.search;

import kr.go.KNPA.Romeo.Base.ActivityLayout;
import android.app.Activity;

public class InviteActivityLayout extends ActivityLayout {

	public interface Listener extends BaseListener {
		void onGoToDeptTree();

		void onGoToFavorite();

		void onSubmit();
	}

	private Listener	mListener;

	public InviteActivityLayout(Activity activity, int layoutResourceId)
	{
		super(activity, layoutResourceId);
	}

	public void setListener(Listener l)
	{
		mListener = l;
		setBaseListener(l);
	}

}
