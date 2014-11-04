package com.tomaszgawel.flickrphotos;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoInfo;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoSearchEntry;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoSize;
import com.tomaszgawel.flickrphotos.volley.PhotoInfoRequest;
import com.tomaszgawel.flickrphotos.volley.VolleyHelper;

public class SinglePhotoActivity extends Activity implements
		Response.Listener<PhotoInfo>, Response.ErrorListener {

	public static final String EXTRA_SEARCH_ITEM =
			"com.tomaszgawel.flickrphotos.EXTRA_SEARCH_ITEM";

	private NetworkImageView mImageView;
	private TextView mTextView;
	private Request<?> mRunningRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_photo);
		final PhotoSearchEntry item =
				getIntent().getParcelableExtra(EXTRA_SEARCH_ITEM);
		setTitle(item.title);
		mImageView = (NetworkImageView) findViewById(R.id.imageView);
		final VolleyHelper volley = VolleyHelper.getInstance(this);
		mImageView.setImageUrl(
				item.getPhotoUrl(PhotoSize.NORMAL),
				volley.imageLoader);
		mTextView = (TextView) findViewById(R.id.textView);
		mRunningRequest = new PhotoInfoRequest(getApiKey(), item.id,
				item.secret, this, this);
		volley.requestQueue.add(mRunningRequest);
	}

	@Override
	protected void onDestroy() {
		if (mRunningRequest != null) {
			mRunningRequest.cancel();
			mRunningRequest = null;
		}
		super.onDestroy();
	}

	@Override
	public void onResponse(PhotoInfo response) {

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// display failure info and show retry button.
	}

	private String getApiKey() {
		// TODO : put api key to preferences
		return AppPreferencesActivity.API_KEY;
	}
}
