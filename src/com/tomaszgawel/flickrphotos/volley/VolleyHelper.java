package com.tomaszgawel.flickrphotos.volley;

import java.io.File;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

public class VolleyHelper {

	private static final String CACHE_DIR_NAME = "flickr_photos_cache";
	private static VolleyHelper sInstance;

	public static VolleyHelper getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new VolleyHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	public final RequestQueue requestQueue;
	public final ImageLoader imageLoader;

	private VolleyHelper(Context context) {
		File cacheDir = new File(context.getCacheDir(), CACHE_DIR_NAME);
		if (!((cacheDir.exists() || cacheDir.mkdirs()) && cacheDir.isDirectory())) {
			throw new IllegalStateException();
		}
		requestQueue = new RequestQueue(
				new DiskBasedCache(cacheDir),
				new BasicNetwork(new HurlStack()));
		requestQueue.start();

		imageLoader = new ImageLoader(requestQueue, new LruBitmapCache(
				LruBitmapCache.getCacheSize(context)));
	}
}
