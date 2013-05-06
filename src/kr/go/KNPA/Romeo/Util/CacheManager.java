package kr.go.KNPA.Romeo.Util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class CacheManager {
	// Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    public static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    // Use 1/8th of the available memory for this memory cache.
    public static final int cacheSize = maxMemory / 8;

	private static LruCache<String, Bitmap> mBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        }
    };
	
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mBitmapCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mBitmapCache.get(key);
    }
    
    public static void clear() {
    	
    }
}
