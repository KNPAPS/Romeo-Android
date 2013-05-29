package kr.go.KNPA.Romeo.search;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import android.app.Activity;
import android.os.Bundle;

public class InviteActivity extends Activity implements InviteActivityLayout.Listener {
	public static final String		KEY_EXCLUDE_IDXS	= "excludeIdxs";
	public static final int			REQUEST_CODE		= 102;

	private InviteActivityLayout	mLayout;
	private ArrayList<String>		excludedIdxs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mLayout = new InviteActivityLayout(this, R.layout.activity_invite);
		mLayout.setListener(this);
		mLayout.setLeftNavBarBtnText(R.id.cancel);
		mLayout.setNavBarTitleTV("사용자 검색");

		excludedIdxs = new ArrayList<String>();
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
		// TODO Auto-generated method stub

	}
}
