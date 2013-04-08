package kr.go.KNPA.Romeo.Util;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import kr.go.KNPA.Romeo.Config.ConnectionConfig;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

/**
 *  이미지 파일 관리\n
 *  uri에서 이미지 가져오거나, 로컬 파일 시스템에서 이미지파일을 삭제하거나 로드\n
 *  또는 파일을 byte array로 또는 그 반대로 변환한다
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ImageManager {
	private static final String TAG = ImageManager.class.getName();
	public static final int PROFILE_IMG_SIZE_SMALL = 1;
	public static final int PROFILE_IMG_SIZE_MEDIUM = 2;
	public static final int PROFILE_IMG_SIZE_ORIGINAL = 3;
	
	private CallbackEvent callBack = new CallbackEvent();
	
	public ImageManager callBack(CallbackEvent callBack) { this.callBack = callBack; return this; }
	/**
	 * 프로필 사진을 서버에 업로드\n
	 * 원본을 업로드 한다.\n
	 * @b callBackType CallBackEvent<Payload,Integer,Payload>
	 * @param userHash
	 * @param fileName
	 * @return
	 */
	public ImageManager uploadProfileImg( String userHash, String fileName){//, CallbackEvent<Payload,Integer,Payload> callBack) {
		Payload requestPayload = new Payload(Event.USER_UPLOAD_PROFILE_IMG);
		Data reqData = new Data();
		reqData.add(0,KEY.USER.IDX,userHash);
		
		new Connection().requestPayloadJSON(requestPayload.toJSON()).attachFile(fileName).callBack(callBack).request();
		return this;
	}
	
	/**
	 * 프로필 사진을 가져온다\n
	 * 먼저 캐시된 파일이 있는지 검사하고, 없으면 서버에서 가져와서 캐싱까지 함
	 * @b callBackType CallBackEvent< Pair<String,Integer> ,Integer, Bitmap>
	 * @param userHash
	 * @param sizeType
	 */
	public void loadProfileImg( String userHash, int sizeType) {//, CallbackEvent<Pair<String,Integer>,Integer,Bitmap> callBack) {
	    Pair<String,Integer> pair = new Pair<String,Integer>(userHash,sizeType);
	    callBack.onPreExecute(pair);
	    
	    String imageKey = getImageCacheKey(userHash,sizeType);
		final Bitmap bitmap = ImageCache.getBitmapFromMemCache(imageKey);
	    if (bitmap != null) {
	    	callBack.onPostExecute(bitmap);
	    } else {
	    	ImageManagerTask task = new ImageManagerTask(ImageManagerTask.TASK_LOAD_PROFILE_IMAGE);
	        task.execute(pair);
	    }
	}

	public void loadProfileImgToImageView( String userHash, int sizeType ) {
		
	}
	
	/**
	 * LruCache를 이용해 메모리에 캐싱할 때의 key값을 만듬\n
	 * 키 규칙 : 유저해시+사이즈타입
	 * @param userHash
	 * @param size
	 * @return
	 */
	protected String getImageCacheKey(String userHash, int size) { return userHash+size; }

	/**
	 * Callback을 받아 작동하는 함수들에 대한 일반적인 AsyncTask \n
	 * 콜백을 따로 받지 않고 작동하는 함수들은 이걸 활용하지 않고 각자 다른 AsyncTask 클래스를 따로 만들어서 사용함
	 */
	class ImageManagerTask extends AsyncTask {
		private int taskType = Constants.NOT_SPECIFIED; 
		static final int TASK_LOAD_PROFILE_IMAGE = 1;
		
		public ImageManagerTask( int taskType ) { this.taskType = taskType; }
		
		@Override
	    protected Object doInBackground(Object... params) {
	        switch ( this.taskType ) {
	        case TASK_LOAD_PROFILE_IMAGE:
	        	return downloadProfileImg((Pair<String,Integer>) params[0]);
	        }
	        
	        return null;
	    }
	    
	    @Override
	    protected void onProgressUpdate(Object... values){ callBack.onProgressUpdate(values[0]); }
	    
	    @Override
	    protected void onPostExecute (Object result){ callBack.onPostExecute(result); }
	}
	
	/**
	 * loadProfileImgToImageView에서 사용하는 asynctask
	 */
	class loadImageTask extends AsyncTask<Pair<String,Integer>,Void,Bitmap> {
		
		private WeakReference<ImageView> imageViewReference = null;
		
		public loadImageTask(ImageView imageView) {
			
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
	    protected Bitmap doInBackground(Pair<String,Integer>... params) {
			Bitmap bitmap = downloadProfileImg(params[0]);
	        ImageCache.addBitmapToMemoryCache(getImageCacheKey(params[0].first,params[0].second),bitmap);
	        return bitmap;
	    }
		
	    @Override
	    protected void onPostExecute (Bitmap bitmap){ 
	        if (isCancelled()) {
	            bitmap = null;
	        }

	        if (imageViewReference != null) {
	            ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	        
    	}
	}
	
	/**
	 * 서버로부터 프로필사진을 가져옴
	 * @param pair
	 * @return
	 */
	protected Bitmap downloadProfileImg( Pair<String,Integer> pair ) {
    	Bitmap bitmap = null;
    	String sUrl = null;
    	switch( pair.second ) {
    	case PROFILE_IMG_SIZE_SMALL:
    		sUrl = ConnectionConfig.PROFILE_PIC_SMALL_URL+pair.first+".jpg";
    		break;
    	case PROFILE_IMG_SIZE_MEDIUM:
    		sUrl = ConnectionConfig.PROFILE_PIC_MEDIUM_URL+pair.first+".jpg";
    		break;
    	case PROFILE_IMG_SIZE_ORIGINAL:
    		sUrl = ConnectionConfig.PROFILE_PIC_URL+pair.first+".jpg";
    		break;
		default:
			sUrl = ConnectionConfig.PROFILE_PIC_URL+pair.first+".jpg";
			break;
    	}
    	
    	URL url = null;
		try {
			url = new URL(sUrl);
		} catch (MalformedURLException e) {
			Log.d(TAG,e.getMessage());
			callBack.onError("잘못된 url로 요청을 했습니다.", e);
			return null;
		}
		
    	URLConnection conn = null;
		try {
			conn = url.openConnection();
	    	conn.connect();
	    	int nSize = conn.getContentLength();
	    	BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(),nSize);
	    	bitmap = BitmapFactory.decodeStream(bis);
	    	bis.close();
		} catch (IOException e) {
			Log.d(TAG,e.getMessage());
			callBack.onError("서버와 통신에 실패했습니다.", e);
			return null;
		}
		
		ImageCache.addBitmapToMemoryCache(getImageCacheKey(pair.first,pair.second),bitmap);
    	return bitmap;
    }
}
