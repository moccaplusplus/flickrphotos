package com.tomaszgawel.flickrphotos.provider;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.android.volley.Cache.Entry;
import com.tomaszgawel.flickrphotos.volley.VolleyHelper;

public class PhotoShareProvider extends ContentProvider {

	public static final String AUTHORITY = "com.tomaszgawel.flickrphotos.share";
	private static final String[] COLUMNS = {
			OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE };

	public static Intent createShareIntent(String url, String displayName,
			int size) {
		Intent shareIntent = null;
		Uri contentUri = PhotoShareProvider.flickrUrlToShareUri(url,
				displayName, size);
		if (contentUri != null) {
			shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
			shareIntent.setDataAndType(contentUri, "image/*");
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		return shareIntent;
	}

	public static Uri flickrUrlToShareUri(String url, String displayName,
			int size) {
		Builder b = new Uri.Builder().scheme("content")
				.authority(AUTHORITY)
				.appendQueryParameter("flickrUrl", url);
		if (!TextUtils.isEmpty(displayName)) {
			b.appendQueryParameter(OpenableColumns.DISPLAY_NAME, displayName);
		}
		if (size > 0) {
			b.appendQueryParameter(OpenableColumns.SIZE, String.valueOf(size));
		}
		return b.build();
	}

	public static String shareUriToFlickrUrl(Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())
				&& AUTHORITY.equalsIgnoreCase(uri.getAuthority())) {
			return uri.getQueryParameter("flickrUrl");
		}
		return null;
	}

	@Override
	public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
		String[] a = mimeTypeFilter.trim().split("/");
		int length = a.length;
		if (length == 0) {
			return new String[] { "image/jpeg" };
		}
		String s = a[0].trim();
		if (s.equals("*") || s.equalsIgnoreCase("image")
				|| TextUtils.isEmpty(s)) {
			if (length == 1) {
				return new String[] { "image/jpeg" };
			}
			s = a[1].trim();
			if (s.equals("*") || s.equalsIgnoreCase("jpeg")
					|| TextUtils.isEmpty(s)) {
				return new String[] { "image/jpeg" };
			}
		}
		return null;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		if (mode.indexOf("r") == -1) {
			throw new FileNotFoundException();
		}
		final String url = shareUriToFlickrUrl(uri);
		if (TextUtils.isEmpty(url)) {
			throw new FileNotFoundException();
		}
		final Entry cache = VolleyHelper.getInstance(
				getContext()).requestQueue.getCache().get(url);
		if (cache == null || cache.data == null) {
			throw new FileNotFoundException();
		}
		final ParcelFileDescriptor[] d;
		try {
			d = ParcelFileDescriptor.createPipe();
		} catch (IOException e) {
			throw new FileNotFoundException();
		}
		new Thread() {

			@Override
			public void run() {
				AutoCloseOutputStream out = null;
				try {
					out = new AutoCloseOutputStream(d[1]);
					out.write(cache.data);
					out.flush();
				} catch (IOException e) {
				} finally {
					try {
						out.close();
					} catch (Exception e) {
					}
				}
			}
		}.start();
		return d[0];
	}
	
	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (projection == null) {
			projection = COLUMNS;
		}
		final MatrixCursor c = new MatrixCursor(projection);
		final Object[] row = new Object[projection.length];
		for (int i = 0; i < projection.length; i++) {
			row[i] = uri.getQueryParameter(projection[i]);
		}
		c.addRow(row);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		final String url = shareUriToFlickrUrl(uri);
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		final Entry cache = VolleyHelper.getInstance(getContext()).requestQueue
				.getCache().get(url);
		if (cache == null) {
			return null;
		}
		return "image/jpeg";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
}
