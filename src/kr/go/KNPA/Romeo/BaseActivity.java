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

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

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
		setBehindContentView(R.layout.menu_frame);
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		mFrag = new MenuListFragment();
		ft.replace(R.id.menu_frame, mFrag);
		ft.commit();
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shade);	
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setSlidingEnabled(true);

		
		
	}
	
	public class BasePagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> mFragments = new ArrayList<Fragment>();
		private ViewPager mPager;

		public BasePagerAdapter(FragmentManager fm, ViewPager vp) {
			super(fm);
			mPager = vp;
			mPager.setAdapter(this);
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