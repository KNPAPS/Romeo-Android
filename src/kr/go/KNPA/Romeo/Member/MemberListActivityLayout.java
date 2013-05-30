package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.ActivityLayout;
import android.app.Activity;

public class MemberListActivityLayout extends ActivityLayout {

	public interface Listener extends BaseListener {
	}

	private MemberListView	mListView;

	public MemberListActivityLayout(Activity activity, int layoutResourceId)
	{
		super(activity, layoutResourceId);
		mListView = (MemberListView) getActivity().findViewById(R.id.memberListView);
		mListView.initWithType(User.TYPE_MEMBERLIST_SEARCH);
	}

	public void setListener(Listener l)
	{
		setBaseListener(l);
	}

	public ArrayList<String> getSelectedUsersIdx()
	{
		return CellNode.collect(((MemberListSearchAdapter) mListView.getAdapter()).rootNode());
	}
}
