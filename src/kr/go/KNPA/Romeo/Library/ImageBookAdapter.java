package kr.go.KNPA.Romeo.Library;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageBookAdapter extends PagerAdapter {
	
	protected Context context;
	protected String BASE_PATH;
	protected int nPages;
	protected HashMap<Integer, ArrayList<String>> book;
	
	
	public ImageBookAdapter(
			Context context, 
			String BASE_PATH, 
			int nPages, 
			HashMap<Integer, ArrayList<String>> book ) {
		this.context = context;
		this.BASE_PATH = BASE_PATH;
		this.nPages = nPages;
		this.book = book;
	}
	
	@Override
	public int getCount() {
		// 현재 PagerAdapter에서 관리할 갯수를 반환 한다.
		/*
		int count = 0;
		for(int c=0; c<book.size(); c++)
			for(int p=0; p<book.get((Integer)c).size(); p++)
				count++;
		
		return count;
		*/
		return nPages;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// instantiateItem메소드에서 생성한 객체를 이용할 것인지 여부를 반환 한다.
		return view == object;
	}

	private Bitmap getImageFromPosition(int position) {
		int count = 0;
		String fileName = null;
		
		bookLoop:
		for(int c=0; c<book.size(); c++) {
			ArrayList<String> chapter = book.get((Integer)c);
			for(int p=0; p<chapter.size(); p++) {
				if(position == count) {
					fileName = chapter.get(p);
					break bookLoop;
				}
				count++;
			}
		}
		
		AssetManager am = context.getResources().getAssets();
		InputStream is;
		try {
			is = am.open(BASE_PATH+"/"+fileName);
		} catch (IOException e) {
			return null;
		}
		
		//Display display = context.getWindowManager().getDefaultDisplay();
		DisplayMetrics display = context.getResources().getDisplayMetrics();
		return decodeSampledBitmapFromInputStraem(is, display.widthPixels, display.heightPixels);
	}
	

	public static int calculateInSampleSize(
	    BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromInputStraem(InputStream is,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;	 
	    BitmapFactory.decodeStream(is, null, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = Math.max(calculateInSampleSize(options, reqWidth, reqHeight) , 2);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeStream(is, null, options);
	}

	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// ViewPager에서 사용할 뷰객체 생성 및 등록 한다.
		final ImageView iv = new ImageView(context);
		//iv.setScrollContainer(true);
		//iv.setScrollbarFadingEnabled(true);
		//iv.setVerticalScrollBarEnabled(true);
		iv.setScaleType(ScaleType.CENTER_CROP);
		final Bitmap bm = getImageFromPosition(position);
		if(bm != null)
			iv.setImageBitmap( bm );

		//Display display = context.getWindowManager().getDefaultDisplay();
		DisplayMetrics display = context.getResources().getDisplayMetrics();
		// set maximum scroll amount (based on center of image)
		float zoomRatio = display.widthPixels / bm.getWidth();
	    //int maxX = Math.max((int)((bm.getWidth() / 2) - (display.getWidth() / 2)), 0);
	    int maxY = Math.max( (int)((bm.getHeight() * zoomRatio / 2) - (display.heightPixels / 2)), 0);
	    
	    // set scroll limits
	    final int maxLeft = 0;//(maxX * -1);
	    final int maxRight = 0;//maxX;
	    final int maxTop = 0;//(maxY * -1);
	    final int maxBottom = 2*maxY;

	    Resources r = context.getResources();
	    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
	    
		iv.scrollTo(0, -maxY-(int)px/2 );
		
		iv.setOnTouchListener(new View.OnTouchListener() {

			float downX, downY;
	        int totalX, totalY;
	        int scrollByX, scrollByY;
			@Override
			public boolean onTouch(View view, MotionEvent event)
	        {
	            float currentX, currentY;
	            switch (event.getAction())
	            {
	                case MotionEvent.ACTION_DOWN:
	                    downX = event.getX();
	                    downY = event.getY();
	                    break;

	                case MotionEvent.ACTION_MOVE:
	                    currentX = event.getX();
	                    currentY = event.getY();
	                    scrollByX = 0;//(int)(downX - currentX);
	                    scrollByY = (int)(downY - currentY);
	                    // scrolling to left side of image (pic moving to the right)
	                    /*
	                    if (currentX > downX)
	                    {
	                        if (totalX == maxLeft)
	                        {
	                            scrollByX = 0;
	                        }
	                        if (totalX > maxLeft)
	                        {
	                            totalX = totalX + scrollByX;
	                        }
	                        if (totalX < maxLeft)
	                        {
	                            scrollByX = maxLeft - (totalX - scrollByX);
	                            totalX = maxLeft;
	                        }
	                    }

	                    // scrolling to right side of image (pic moving to the left)
	                    if (currentX < downX)
	                    {
	                        if (totalX == maxRight)
	                        {
	                            scrollByX = 0;
	                        }
	                        if (totalX < maxRight)
	                        {
	                            totalX = totalX + scrollByX;
	                        }
	                        if (totalX > maxRight)
	                        {
	                            scrollByX = maxRight - (totalX - scrollByX);
	                            totalX = maxRight;
	                        }
	                    }
	                    */
	                    // scrolling to top of image (pic moving to the bottom)
	                    if (currentY > downY)
	                    {
	                        if (totalY == maxTop)
	                        {
	                            scrollByY = 0;
	                        }
	                        if (totalY > maxTop)
	                        {
	                            totalY = totalY + scrollByY;
	                        }
	                        if (totalY < maxTop)
	                        {
	                            scrollByY = maxTop - (totalY - scrollByY);
	                            totalY = maxTop;
	                        }
	                    }

	                    // scrolling to bottom of image (pic moving to the top)
	                    if (currentY < downY)
	                    {
	                        if (totalY == maxBottom)
	                        {
	                            scrollByY = 0;
	                        }
	                        if (totalY < maxBottom)
	                        {
	                            totalY = totalY + scrollByY;
	                        }
	                        if (totalY > maxBottom)
	                        {
	                            scrollByY = maxBottom - (totalY - scrollByY);
	                            totalY = maxBottom;
	                        }
	                    }

	                    iv.scrollBy(0, scrollByY); //scrollByX
	                    //downX = currentX;
	                    downY = currentY;
	                    break;

	            }

	            return true;
	        }
			
		});
		
		
		
		
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