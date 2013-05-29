package kr.go.KNPA.Romeo.search;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.MemberManager;
import kr.go.KNPA.Romeo.Member.User;
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

		mExcludeIdxs = b.getStringArrayList(KEY_EXCLUDE_IDXS);
		if (mExcludeIdxs == null)
		{
			mExcludeIdxs = new ArrayList<String>();
		}

		ArrayList<User> users = new ArrayList<User>();

		users.add(MemberManager.sharedManager().getUser("3dd934211069ed71a25e76020d221b4a"));
		users.add(MemberManager.sharedManager().getUser("3f32d765839a5e5ca54ea8c28d4e9a00"));
		users.add(MemberManager.sharedManager().getUser("415beeb72c93ce5c817cc8e8fcdf8eb1"));
		users.add(MemberManager.sharedManager().getUser("54b9bf834f1ee08c9fc0080395c1eb9a"));
		users.add(MemberManager.sharedManager().getUser("86815084101098be92cfd8ec9f9bbde8"));
		users.add(MemberManager.sharedManager().getUser("d758f3d8f7cdb2c672ed9b84d487b230"));
		users.add(MemberManager.sharedManager().getUser("ffe33dc526aec3f2786bb0fe4d48dada"));

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
		// TODO Auto-generated method stub

	}

	@Override
	public void onGoToFavorite()
	{
		// TODO Auto-generated method stub

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
		intent.putExtra(KEY_RESULT_IDXS, getMembersIdx().toArray(new String[getMembersIdx().size()]));
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public ArrayList<String> getMembersIdx()
	{
		return mLayout.getSearchInput().getMembersIdx();
	}
}
