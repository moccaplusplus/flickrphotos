package com.tomaszgawel.flickrphotos.volley;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Cache.Entry;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tomaszgawel.flickrphotos.json.FlickrApiFailureException;
import com.tomaszgawel.flickrphotos.json.PhotoInfo;
import com.tomaszgawel.flickrphotos.json.PhotoInfoResponse;

public class PhotoInfoRequest extends Request<PhotoInfo> {

	private static final String BASE_URL =
			"https://api.flickr.com/services/rest/?method=flickr.photos.getInfo&format=json&nojsoncallback=1";

	private final Listener<PhotoInfo> mListener;

	public PhotoInfoRequest(String apiKey, String photoId, String secret,
			Listener<PhotoInfo> listener, ErrorListener errorListener) {
		super(Request.Method.GET, buildUrl(apiKey, photoId, secret), errorListener);
		mListener = listener;
		setShouldCache(true);
	}

	@Override
	protected Response<PhotoInfo> parseNetworkResponse(NetworkResponse response) {
		try {
			final String json = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			final PhotoInfoResponse result = PhotoInfoResponse.parse(json);
			result.throwIfFailed();
			final Entry cache = HttpHeaderParser.parseCacheHeaders(response);
			cache.data = PhotoInfoResponse.asBytes(result);
			cache.ttl = cache.softTtl = cache.serverDate + 30 * 60 * 1000;
			return Response.success(result.photoInfo, cache);
		} catch (UnsupportedEncodingException e) {
			return Response.error(new VolleyError(e));
		} catch (JsonProcessingException e) {
			return Response.error(new VolleyError(e));
		} catch (IOException e) {
			return Response.error(new VolleyError(e));
		} catch (FlickrApiFailureException e) {
			return Response.error(new VolleyError(e));
		}
	}

	@Override
	protected void deliverResponse(PhotoInfo response) {
		mListener.onResponse(response);
	}

	private static String buildUrl(String apiKey, String photoId, String secret) {
		final Uri.Builder b = Uri.parse(BASE_URL).buildUpon()
				.appendQueryParameter("api_key", apiKey)
				.appendQueryParameter("photo_id", photoId);
		if (!TextUtils.isEmpty(secret)) {
			b.appendQueryParameter("secret", secret);
		}
		return b.build().toString();
	}
}
