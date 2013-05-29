package kr.go.KNPA.Romeo.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.User;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class CacheManager {
	// Get max available VM memory, exceeding this amount will throw an
	// OutOfMemory exception. Stored in kilobytes as LruCache takes an
	// int in its constructor.
	public static final int						maxMemory		= (int) (Runtime.getRuntime().maxMemory() / 1024);

	// Use 1/8th of the available memory for this memory cache.
	public static final int						cacheSize		= maxMemory / 8;

	private static LruCache<String, Bitmap>		mBitmapCache	= new LruCache<String, Bitmap>(cacheSize) {
																	@Override
																	protected int sizeOf(String key, Bitmap bitmap)
																	{
																		return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
																	}
																};

	private static HashMap<String, User>		mUserCache		= new HashMap<String, User>();

	private static HashMap<String, Department>	mDeptCache		= new HashMap<String, Department>();

	public static void addBitmapToMemCache(String key, Bitmap bitmap)
	{
		if (getBitmapFromMemCache(key) == null && bitmap != null)
		{
			mBitmapCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(String key)
	{
		return mBitmapCache.get(key);
	}

	public static void addUserToMemCache(User user)
	{
		if (user.idx != null && getUserFromMemCache(user.idx) == null)
		{
			mUserCache.put(user.idx, user);
		}
	}

	public static User getUserFromMemCache(String userIdx)
	{
		return mUserCache.get(userIdx);
	}

	public static void addDeptToMemCache(Department dept)
	{
		if (dept.idx != null && getUserFromMemCache(dept.idx) == null)
		{
			mDeptCache.put(dept.idx, dept);
		}
	}

	public static Department getDeptFromMemCache(String deptIdx)
	{
		return mDeptCache.get(deptIdx);
	}

	public static void clear()
	{
		mBitmapCache.evictAll();
		mUserCache = null;
		mDeptCache = null;
	}

	public static ArrayList<User> getCachedUsers()
	{
		ArrayList<User> us = new ArrayList<User>(mUserCache.size());

		Iterator<String> itr = mUserCache.keySet().iterator();

		while (itr.hasNext())
		{
			us.add(mUserCache.get(itr.next()));
		}
		return us;

	}
}
