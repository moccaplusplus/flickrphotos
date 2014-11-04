package com.tomaszgawel.flickrphotos;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.location.Location;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.tomaszgawel.flickrphotos.jsonentity.FlickrApiFailureException;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoSearchPage;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoSearchResult;

public class FlickrQueryRequest extends Request<PhotoSearchPage> {

	private static final String BASE_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&media=photos&extras=geo&format=json&nojsoncallback=1";
	private static final ObjectMapper mapper = new ObjectMapper().configure(
			DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private static final ObjectReader sReader = mapper.reader(PhotoSearchResult.class);

	private final Listener<PhotoSearchPage> mSuccessListener;

	public FlickrQueryRequest(String apiKey, String query, int page, Location location,
			Listener<PhotoSearchPage> successListener, ErrorListener errorListener) {
		super(Request.Method.GET, buildUrl(apiKey, query, page, location), errorListener);
		mSuccessListener = successListener;
	}

	private static String buildUrl(String apiKey, String query, int page,
			Location location) {
		final Uri.Builder b = Uri.parse(BASE_URL).buildUpon()
				.appendQueryParameter("api_key", apiKey)
				.appendQueryParameter("text", query);
		if(page > 1) {
			b.appendQueryParameter("page", String.valueOf(page));
		}
		if (location != null) {
			b.appendQueryParameter("lat", String.valueOf(location.getLatitude()));
			b.appendQueryParameter("lon", String.valueOf(location.getLongitude()));
		}
		return b.build().toString();
	}

	@Override
	protected Response<PhotoSearchPage> parseNetworkResponse(NetworkResponse response) {
		try {
			final String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			final PhotoSearchResult result = sReader.readValue(jsonString);
			result.throwIfFailed();
			return Response.success(result.photoSearchPage,
					HttpHeaderParser.parseCacheHeaders(response));
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
	protected void deliverResponse(PhotoSearchPage response) {
		mSuccessListener.onResponse(response);
	}
}
