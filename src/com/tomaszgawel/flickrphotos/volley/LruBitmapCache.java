package com.tomaszgawel.flickrphotos.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {

	public LruBitmapCache(int maxSize) {
		super(maxSize);
	}

	public LruBitmapCache(Context ctx) {
		this(getCacheSize(ctx));
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}

	public static int getCacheSize(Context ctx) {
		final DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
		final int screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4;
		return screenBytes * 3;
	}
}