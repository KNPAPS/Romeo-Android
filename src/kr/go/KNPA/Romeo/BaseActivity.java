package kr.go.KNPA.Romeo;

import java.util.ArrayList;
import java.util.List;

import kr.go.KNPA.Romeo.Menu.MenuListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected ListFragment mFrag;

	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(mTitleRes);

		// set the Behind fragment
		setBehindContentView(R.layout.menu_frame);
		
		// Fragment를 Programmatically하게 배치하기 위하여 Fragment Transaction 인스턴스를 매니저로부터 받아온다.
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		
		// 리스트를 담당하는 Fragment이다.
		mFrag = new MenuListFragment();
		
		// Behind fragment를 리스트뷰로 전환한다.
		ft.replace(R.id.menu_frame, mFrag);
		ft.commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);		
	}

	public class BasePagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> mFragments = new ArrayList<Fragment>();

		private ViewPager mPager;

		// 컨스트럭터.
		public BasePagerAdapter(FragmentManager fm, ViewPager vp) {
			super(fm);
			mPager = vp;				// 스크롤뷰는 이 어댑터를 생성할때 레퍼런스를 받아서,
			mPager.setAdapter(this);	// 프레그먼트들을 하나씩 푸시하나보다.
			for (int i = 0; i < 3; i++) {
				addTab(new MenuListFragment());	
			}
		}

		public void addTab(Fragment frag) {
			mFragments.add(frag);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}
	}

}