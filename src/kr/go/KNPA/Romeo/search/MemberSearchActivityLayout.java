package kr.go.KNPA.Romeo.search;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.ActivityLayout;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MemberSearchActivityLayout extends ActivityLayout {

	public interface Listener extends BaseListener {
		void onGoToDeptTree();

		void onGoToFavoriteList();

		void onSubmit();
	}

	private Listener				mListener;
	private MemberSearchTextView	mSearchTV;
	private Button					mGoToDeptTreeBtn, mGoToFavoriteBtn, mSubmitBtn;

	public MemberSearchActivityLayout(Activity activity, int layoutResourceId)
	{
		super(activity, layoutResourceId);

		mGoToDeptTreeBtn = (Button) getActivity().findViewById(R.id.btn_invite_from_dept_tree);
		mGoToFavoriteBtn = (Button) getActivity().findViewById(R.id.btn_invite_from_favorite);
		mSubmitBtn = (Button) getActivity().findViewById(R.id.btn_invite_submit);
		mSearchTV = (MemberSearchTextView) getActivity().findViewById(R.id.atv_invitee_search);

		mGoToDeptTreeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onGoToDeptTree();
				}
			}
		});

		mGoToFavoriteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onGoToFavoriteList();
				}
			}
		});

		mSubmitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onSubmit();
				}
			}
		});

	}

	public void setListener(Listener l)
	{
		mListener = l;
		setBaseListener(l);
	}

	public MemberSearchTextView getSearchInput()
	{
		return mSearchTV;
	}

	public void appendMemberClips(ArrayList<String> newbies)
	{
		for (int i = 0; i < newbies.size(); i++)
		{
			if (((MemberSearchTextViewAdapter) getSearchInput().getAdapter()).isExcluded(newbies.get(i)) == false)
			{

				User u = MemberManager.sharedManager().getUser(newbies.get(i));
				getSearchInput().appendMemberClip(u);
			}
		}
	}
}
