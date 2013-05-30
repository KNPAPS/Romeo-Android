package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.ActivityLayout;
import android.app.Activity;

public class MemberFavoriteListActivityLayout extends ActivityLayout {

	public interface Listener extends BaseListener {
	}

	private MemberFavoriteListView	mListView;

	public MemberFavoriteListActivityLayout(Activity activity, int layoutResourceId)
	{
		super(activity, layoutResourceId);
		mListView = (MemberFavoriteListView) getActivity().findViewById(R.id.favoriteListView);
		mListView.initWithType(User.TYPE_FAVORITE_SEARCH);
	}

	public void setListener(Listener l)
	{
		setBaseListener(l);
	}

	public ArrayList<String> getSelectedUsersIdx()
	{
		return ((MemberFavoriteListAdapter) mListView.getAdapter()).collect();
	}
}
