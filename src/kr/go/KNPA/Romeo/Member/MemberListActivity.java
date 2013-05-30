package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MemberListActivity extends Activity implements MemberListActivityLayout.Listener {

	public static final String			TYPE_DEPARTMENT			= "TYPE_DEPARTMENT";
	public static final String			TYPE_USER				= "TYPE_USER";
	public static final int				REQUEST_CODE			= 100;
	public static final String			KEY_RESULT_USERS_IDX	= "receivers";

	public String						searchResult			= "";
	private MemberListActivityLayout	mLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mLayout = new MemberListActivityLayout(this, R.layout.activity_member_list);

		mLayout.setListener(this);
		mLayout.setNavBarTitleTV(R.string.memberListTitle);
		mLayout.setLeftNavBarBtnText(R.string.cancel);
		mLayout.setRightNavBarBtnText(R.string.done);
	}

	@Override
	public void onLeftNavBarBtnClick()
	{
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public void onRightNavBarBtnClick()
	{
		submit();
	}

	private void submit()
	{
		ArrayList<String> result = mLayout.getSelectedUsersIdx();

		if (result.size() < 1)
		{
			Toast.makeText(this, "수신자가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent();
		intent.putExtra(KEY_RESULT_USERS_IDX, result);

		setResult(RESULT_OK, intent);
		finish();
	}
}
