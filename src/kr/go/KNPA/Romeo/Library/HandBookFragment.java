package kr.go.KNPA.Romeo.Library;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HandBookFragment extends Fragment {
	private final static String BASE_PATH = "handbook/"; 
	private final static String DELIMITER = "#";
	private ViewPager pager;
	private HashMap<Integer, ArrayList<String>> book;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		AssetManager am = getResources().getAssets();
		String[] list = null;
		
		try {
			list = am.list(BASE_PATH);
		} catch (IOException e) {
		}
		
		
		book = new HashMap<Integer, ArrayList<String>>();
		ArrayList<String> chapter = null;
		
		for( int i=0; i<list.length; i++) {
			String fileName = list[i];
			String[] segments = fileName.split(DELIMITER);
			
			int chapterNumber = Integer.parseInt(segments[i]);
			
			if( book.containsKey( (Integer)chapterNumber  ) == false) {
				chapter = new ArrayList<String>();
			} else {
				chapter = book.get((Integer) chapterNumber );
			}
			
			chapter.add(fileName);
		}
		
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.handbook_fragment, container, false); 
		
		pager = (ViewPager)view.findViewById(R.id.pager);
		pager.setAdapter(new HandBookAdapter());
		
		/*
		 pager.setCurrentItem(n);
		  
		 */
		
		
		
		
		return view;
	}
	
	private class HandBookAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			// 현재 PagerAdapter에서 관리할 갯수를 반환 한다.
			
			int count = 0;
			for(int c=0; c<book.size(); c++)
				for(int p=0; p<book.get((Integer)c).size(); p++)
					count++;
			
			return count;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// instantiateItem메소드에서 생성한 객체를 이용할 것인지 여부를 반환 한다.
			return view == object;
		}

		private Bitmap getImageFromPosition(int position) {
			int count = 0;
			String fileName = null;
			for(int c=0; c<book.size(); c++) {
				ArrayList<String> chapter = book.get((Integer)c);
				for(int p=0; p<chapter.size(); p++) {
					if(position == count) {
						fileName = chapter.get(p);
						break;
					}
					count++;
				}
			}
			
			AssetManager am = getResources().getAssets();
			InputStream is;
			try {
				is = am.open(BASE_PATH+fileName);
			} catch (IOException e) {
				return null;
			}
			return BitmapFactory.decodeStream(is);
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// ViewPager에서 사용할 뷰객체 생성 및 등록 한다.  
			ImageView iv = new ImageView(getActivity());
			
			Bitmap bm = getImageFromPosition(position);
			if(bm != null)
			iv.setImageBitmap( bm );
			
			container.addView(iv, 0);
			return iv;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// View 객체를 삭제 한다.
			container.removeView((View)object);
		}
		
		@Override
		public Parcelable saveState() {
			// 현재 UI 상태를 저장하기 위해 Adapter와 Page 관련 인스턴스 상태를 저장 합니다. 
			return super.saveState();
		}
		
		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			// saveState() 상태에서 저장했던 Adapter와 page를 복구 한다. 
			super.restoreState(state, loader);
		}
		@Override
		public void startUpdate(ViewGroup container) {
			// 페이지 변경이 시작될때 호출 됩니다.
			super.startUpdate(container);
		}
		
		@Override
		public void finishUpdate(ViewGroup container) {
			// 페이지 변경이 완료되었을때 호출 됩니다.
			super.finishUpdate(container);
		}
	}
}
