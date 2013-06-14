package kr.go.KNPA.Romeo.Menu;

import java.util.List;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserProfileFragment;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MenuListFragment extends ListFragment {

	private LinearLayout						userLL;
	private View								searchBar;
	private EditText							searchET;
	private Button								searchBT;
	private Button								cancelSearchBT;
	private ExpandableListView					menuList;
	private ListView							searchList;

	private static MenuListFragment				sharedFragment;

	private BaseAdapter							emptyAdapter	= 
			new BaseAdapter() {
								@Override	public int getCount()	{	return 0;	}
								@Override	public Object getItem(int arg0)	{	return null;	}
								@Override	public long getItemId(int arg0)	{	return 0;		}
								@Override	public View getView(int arg0, View arg1, ViewGroup arg2)	{	return null;	}
							};
	private ExpandableMenuListAdapter	mListAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		sharedFragment = this;
		View v = inflater.inflate(R.layout.menu_list_fragment, null);

		showUserInfo(v);
		List<MenuListItem> menus = ExpandableMenuListAdapter.makeMenuList();

		mListAdapter = ExpandableMenuListAdapter.getExpandableMenuListAdapter(getActivity(), menus);
		
		menuList = (ExpandableListView) v.findViewById(android.R.id.list);
		menuList.setAdapter(mListAdapter);
		menuList.setOnGroupClickListener(mListAdapter);
		menuList.setOnChildClickListener(mListAdapter);

		// ///////////////
		// Search List //
		// ///////////////
		searchList = (ListView) v.findViewById(R.id.searchList);
		searchList.setAdapter(emptyAdapter);

		// //////////////
		// Search Bar //
		// //////////////

		searchBar = v.findViewById(R.id.search_bar);
		searchET = (EditText) searchBar.findViewById(R.id.edit);
		searchBT = (Button) searchBar.findViewById(R.id.editButton);
		cancelSearchBT = (Button) searchBar.findViewById(R.id.cancel);

		searchBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				setSearchMode(true);
			}
		});

		searchET.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				
			}
		});

		searchET.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
				{
					if (searchET.getText().toString().trim().length() == 0)
					{
						searchList.setAdapter(emptyAdapter);
					}
					else
					{
						searchResult();
					}
					// searchET.clearFocus(); // 결과가 감춰져버리네
					InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					im.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});

		cancelSearchBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				searchET.setText("");
				searchET.clearFocus();
				searchList.setAdapter(emptyAdapter);
				setSearchMode(false);
			}
		});

		return v;
	}

	public void showUserInfo(View v) {
		// //////////////////////
		// // 간단 프로필 출력!! //
		// ////////////////////
		userLL = (LinearLayout) v.findViewById(R.id.user_profile);
		userLL.setBackgroundResource(R.drawable.menu_user_info_box);

		// 프로필 사진
		ImageView userPicIV = (ImageView) v.findViewById(R.id.userPic);
		new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, UserInfo.getUserIdx(getActivity()), userPicIV);

		// 계급
		TextView rankTV = (TextView) v.findViewById(R.id.rank);
		rankTV.setTextColor(Color.WHITE);
		rankTV.setText(User.RANK[UserInfo.getRank(getActivity())]);

		// 부서
		TextView departmentTV = (TextView) v.findViewById(R.id.department);
		departmentTV.setText(UserInfo.getDepartment(getActivity()));
		departmentTV.setTextColor(Color.WHITE);

		// 이름
		TextView nameTV = (TextView) v.findViewById(R.id.name);
		nameTV.setText(UserInfo.getName(getActivity()));
		nameTV.setTextColor(Color.WHITE);

		// 직책
		TextView roleTV = (TextView) v.findViewById(R.id.role);
		roleTV.setText("");
		roleTV.setTextColor(Color.WHITE);

		userLL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				UserProfileFragment profile = new UserProfileFragment();
				MainActivity.sharedActivity().switchContent(profile);
			}
		});

	}

	/**
	 * @name Switch between Search - Menu Modes 
	 * 메뉴모드/검색모드 간의 전환에 필요한 메서드들
	 * @{
	 */
	public static void setMode(boolean willSearchMode) {
		if (sharedFragment != null)
			sharedFragment.setSearchMode(willSearchMode);
	}

	private void setSearchMode(boolean willSearchMode) {
		InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (willSearchMode == true)
		{
			cancelSearchBT.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.VISIBLE);
			menuList.setVisibility(View.INVISIBLE);

			if (searchET != null)
				searchET.requestFocus();
			im.showSoftInput(searchET, InputMethod.SHOW_EXPLICIT);
		}
		else
		{
			cancelSearchBT.setVisibility(View.GONE);
			searchList.setVisibility(View.INVISIBLE);
			menuList.setVisibility(View.VISIBLE);

			if (searchET != null)
				searchET.clearFocus();

			im.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
		}
	}
	/** @} */
	
	/**
	 *  @name Search
	 *  @{
	 */
	private void searchResult()
	{
		final String keyword = searchET.getText().toString().trim();
		WaiterView.showDialog(getActivity());
		new Thread(new Runnable() {
			@Override
			public void run()
			{
				IntergratedSearchListAdatper.searchResult(getActivity(), keyword, searchList);
			}
		}).start();
	}
	/** @} */

	public void refresh()
	{
		if (mListAdapter != null)
		{
			ExpandableMenuListAdapter.fetchUncheckedCounts(getActivity());
			mListAdapter.notifyDataSetChanged();
		}
	}
}