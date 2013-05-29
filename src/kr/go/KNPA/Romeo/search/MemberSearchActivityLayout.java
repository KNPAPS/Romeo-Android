package kr.go.KNPA.Romeo.search;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.ActivityLayout;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MemberSearchActivityLayout extends ActivityLayout {

	public interface Listener extends BaseListener {
		void onGoToDeptTree();

		void onGoToFavorite();

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
					mListener.onGoToFavorite();
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
}
