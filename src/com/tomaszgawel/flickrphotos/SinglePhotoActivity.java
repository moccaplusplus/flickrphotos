package com.tomaszgawel.flickrphotos;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.tomaszgawel.flickrphotos.json.PhotoInfo;
import com.tomaszgawel.flickrphotos.json.PhotoSearchPage;
import com.tomaszgawel.flickrphotos.json.PhotoUrl;
import com.tomaszgawel.flickrphotos.volley.PhotoInfoRequest;
import com.tomaszgawel.flickrphotos.volley.VolleyHelper;

public class SinglePhotoActivity extends Activity implements
Response.Listener<PhotoInfo>, Response.ErrorListener {

	public static final String EXTRA_SEARCH_ITEM =
			"com.tomaszgawel.flickrphotos.EXTRA_SEARCH_ITEM";

	private NetworkImageView mImageView;
	private TextView mTextView;
	// private View mLoading;
	private Request<?> mRunningRequest;

	private String mSharedImageUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_photo);
		final PhotoSearchPage.Entry entry =
				getIntent().getParcelableExtra(EXTRA_SEARCH_ITEM);
		setTitle(entry.title);
		mImageView = (NetworkImageView) findViewById(R.id.imageView);
		final VolleyHelper volley = VolleyHelper.getInstance(this);
		mSharedImageUrl = PhotoUrl.NORMAL.get(entry);
		mImageView.setImageUrl(mSharedImageUrl, volley.imageLoader);
		mTextView = (TextView) findViewById(R.id.textView);
		mRunningRequest = new PhotoInfoRequest(getApiKey(), entry.id,
				entry.secret, this, this);
		// mLoading.setVisibility(View.VISIBLE);
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
	public void onResponse(PhotoInfo photoInfo) {
		// mLoading.setVisibility(View.GONE);
		mTextView.setText(Html.fromHtml(
				getString(R.string.photo_info_summary, photoInfo.title,
						photoInfo.description, photoInfo.owner,
						photoInfo.location.latitude, photoInfo.location.longitude,
						photoInfo.location.locality, photoInfo.location.county,
						photoInfo.location.region, photoInfo.location.country,
						TextUtils.join(", ", photoInfo.tags))));
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// mLoading.setVisibility(View.GONE);
		// display failure info and show retry button.
	}

	private String getApiKey() {
		// TODO : put api key to preferences
		return AppPreferencesActivity.API_KEY;
	}

	/*
	private void share() {
		Uri contentUri = null; // getContentUri(mSharedImageUrl);
		if (contentUri != null) {
			mShareIntent = new Intent(Intent.ACTION_SEND);
			mShareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
			mShareIntent.setType("image/*");
			mShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(Intent.createChooser(mShareIntent, "share..."));
		}
	}*/
}
