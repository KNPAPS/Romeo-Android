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

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		
		// Fragment를 Programmatically하게 배치하기 위하여 Fragment Transaction 인스턴스를 매니저로부터 받아온다.
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		
		// 리스트를 담당하는 Fragment이다.
		mFrag = new MenuListFragment();
		
		// Behind View?? 를 리스트뷰로 전환한다.
		ft.replace(R.id.menu_frame, mFrag);
		ft.commit();		// 발사!!

		// customize the SlidingMenu(뭔진 모르겠지만, SlideingMenu안에 정의되어있다.)
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);	// res/drawble/shadow.xml은 그라디언트 정보를 담고있다.
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		
		//나중에 NavigationBar를 만들면서 지워버리도록하자. 리스너를 등록?
		
	}
	
	// 횡스크롤뷰를 만드려나보다.
	public class BasePagerAdapter extends FragmentPagerAdapter {
		// 프레그먼트들이 들어있는 리스트를 하나 선언하고,
		private List<Fragment> mFragments = new ArrayList<Fragment>();
		// 페이징? 횡스크롤하는 뷰인가보다.
		private ViewPager mPager;

		// 컨스트럭터.
		public BasePagerAdapter(FragmentManager fm, ViewPager vp) {
			super(fm);
			mPager = vp;				// 스크롤뷰는 이 어댑터를 생성할때 레퍼런스를 받아서,
			mPager.setAdapter(this);	// 프레그먼트들을 하나씩 푸시하나보다.
			for (int i = 0; i < 3; i++) {
				addTab(new MenuListFragment());	// 오잉????
			}
		}

		public void addTab(Fragment frag) {
			mFragments.add(frag);					// 아이템 푸쉬 인투 어레이를 쉽게!!
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);		// 아이템 게터 in 배열
		}

		@Override
		public int getCount() {
			return mFragments.size();				// 카운트.
		}
	}

}