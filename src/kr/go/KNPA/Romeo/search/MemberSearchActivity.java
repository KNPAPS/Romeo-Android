package kr.go.KNPA.Romeo.search;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.MemberFavoriteListActivity;
import kr.go.KNPA.Romeo.Member.MemberListActivity;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.CacheManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MemberSearchActivity extends Activity implements MemberSearchActivityLayout.Listener {
	public static final String			KEY_EXCLUDE_IDXS	= "excludeIdxs";
	public static final String			KEY_RESULT_IDXS		= "resultsIdxs";
	public static final int				REQUEST_CODE		= 102;

	private MemberSearchActivityLayout	mLayout;
	private ArrayList<String>			mExcludeIdxs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mLayout = new MemberSearchActivityLayout(this, R.layout.activity_member_search);
		mLayout.setListener(this);
		mLayout.setLeftNavBarBtnText(R.string.cancel);
		mLayout.setNavBarTitleTV("사용자 검색");

		Bundle b = getIntent().getExtras();

		if (b == null)
		{
			mExcludeIdxs = new ArrayList<String>();
		}
		else
		{
			mExcludeIdxs = b.getStringArrayList(KEY_EXCLUDE_IDXS);
			if (mExcludeIdxs == null)
			{

				mExcludeIdxs = new ArrayList<String>();
			}
		}

		ArrayList<User> users = CacheManager.getCachedUsers();

		MemberSearchTextViewAdapter adapter = new MemberSearchTextViewAdapter(MemberSearchActivity.this, users);

		adapter.setExcludeIdxs(mExcludeIdxs);
		mLayout.getSearchInput().setAdapter(adapter);
	}

	@Override
	public void onLeftNavBarBtnClick()
	{
		finish();
	}

	@Override
	public void onRightNavBarBtnClick()
	{
	}

	@Override
	public void onGoToDeptTree()
	{
		Intent intent = new Intent(this, MemberListActivity.class);
		startActivityForResult(intent, MemberListActivity.REQUEST_CODE);
	}

	@Override
	public void onGoToFavoriteList()
	{
		Intent intent = new Intent(this, MemberFavoriteListActivity.class);
		startActivityForResult(intent, MemberFavoriteListActivity.REQUEST_CODE);
	}

	@Override
	public void onSubmit()
	{
		if (getMembersIdx().size() == 0)
		{
			Toast.makeText(this, "추가할 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent();
		intent.putExtra(KEY_RESULT_IDXS, getMembersIdx());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public ArrayList<String> getMembersIdx()
	{
		return mLayout.getSearchInput().getMembersIdx();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		ArrayList<String> resIdxs = null;

		switch (requestCode)
		{
		case MemberListActivity.REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK)
			{
				resIdxs = data.getExtras().getStringArrayList(MemberListActivity.KEY_RESULT_USERS_IDX);
			}
			break;
		case MemberFavoriteListActivity.REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK)
			{
				resIdxs = data.getExtras().getStringArrayList(MemberFavoriteListActivity.KEY_RESULT_USERS_IDX);
			}
			break;
		}

		if (resIdxs == null)
		{
			return;
		}

		mLayout.appendMemberClips(resIdxs);
	}
}
