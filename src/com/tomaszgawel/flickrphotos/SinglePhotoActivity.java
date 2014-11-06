package com.tomaszgawel.flickrphotos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.tomaszgawel.flickrphotos.json.PhotoInfo;
import com.tomaszgawel.flickrphotos.json.PhotoInfo.LocationInfo;
import com.tomaszgawel.flickrphotos.json.PhotoSearchPage;
import com.tomaszgawel.flickrphotos.json.PhotoUrl;
import com.tomaszgawel.flickrphotos.provider.PhotoShareProvider;
import com.tomaszgawel.flickrphotos.volley.PhotoInfoRequest;
import com.tomaszgawel.flickrphotos.volley.VolleyHelper;

public class SinglePhotoActivity extends Activity {

	public static final String EXTRA_SEARCH_ITEM =
			"com.tomaszgawel.flickrphotos.EXTRA_SEARCH_ITEM";

	private final InfoRequestLogic mInfoRequestLogic = new InfoRequestLogic();
	private final ImageRequestLogic mImageRequestLogic = new ImageRequestLogic();
	private MenuItem mShareAction;
	private Intent mShareIntent;
	PhotoSearchPage.Entry mEntry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_photo);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mEntry = getIntent().getParcelableExtra(EXTRA_SEARCH_ITEM);
		if (!TextUtils.isEmpty(mEntry.title)) {
			setTitle(mEntry.title);
		}
		mInfoRequestLogic.init();
		mImageRequestLogic.init();
		mInfoRequestLogic.sendRequest();
		mImageRequestLogic.sendRequest();
	}

	@Override
	protected void onDestroy() {
		mInfoRequestLogic.cancel();
		mImageRequestLogic.cancel();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.single_photo, menu);
		mShareAction = menu.findItem(R.id.action_share);
		updateShareAction();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_reload) {
			mInfoRequestLogic.sendRequest();
			mImageRequestLogic.sendRequest();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	VolleyHelper getVolley() {
		return VolleyHelper.getInstance(this);
	}

	void setShareIntent(Intent shareIntent) {
		mShareIntent = shareIntent;
		updateShareAction();
	}

	void updateShareAction() {
		if (mShareAction != null) {
			if (mShareIntent == null) {
				mShareAction.setVisible(false);
			} else {
				((ShareActionProvider) mShareAction
						.getActionProvider()).setShareIntent(mShareIntent);
				mShareAction.setVisible(true);
			}
		}
	}

	String getApiKey() {
		// TODO : put api key to preferences
		return AppPreferencesActivity.API_KEY;
	}

	void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private class ImageRequestLogic extends VolleyRequestListeners<Bitmap> {

		private ImageView mImageView;
		private Point mScreenSize;
		private String mUrl;

		@Override
		void init() {
			mScreenSize = new Point();
			getWindowManager().getDefaultDisplay().getSize(mScreenSize);
			mUrl = PhotoUrl.NORMAL.get(mEntry);
			mImageView = (ImageView) findViewById(R.id.imageView);
		}

		@Override
		void sendRequest() {
			cancel();
			mImageView.setImageResource(R.drawable.loading_big);
			setShareIntent(null);
			mRequest = new ImageRequest(mUrl, this, mScreenSize.x,
					mScreenSize.y, Config.RGB_565, this);
			getVolley().requestQueue.add(mRequest);

		}

		@Override
		public void onResponse(Bitmap response) {
			mImageView.setImageBitmap(response);
			setShareIntent(PhotoShareProvider.createShareIntent(mUrl,
					"Flickr photo", 0));
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			mImageView.setImageResource(R.drawable.error_big);
			toast(getString(R.string.single_photo_activity_load_failed,
					error.toString()));
		}
	}

	private class InfoRequestLogic extends VolleyRequestListeners<PhotoInfo> {

		private View mLoadingView;
		private View mErrorView;
		private TextView mTextView;

		@Override
		public void onResponse(PhotoInfo response) {
			final LocationInfo location = response.location;
			mTextView.setText(Html.fromHtml(
					getString(R.string.photo_info_summary, response.title,
							response.description, response.owner,
							location.latitude, location.longitude,
							location.locality, location.county,
							location.region, location.country,
							TextUtils.join(", ", response.tags))));
			mLoadingView.setVisibility(View.GONE);
			mErrorView.setVisibility(View.GONE);
			mTextView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			mLoadingView.setVisibility(View.GONE);
			mTextView.setVisibility(View.GONE);
			mErrorView.setVisibility(View.VISIBLE);
			toast(getString(R.string.single_photo_activity_load_failed,
					error.toString()));
		}

		@Override
		void init() {
			mLoadingView = findViewById(R.id.loadingView);
			mErrorView = findViewById(R.id.errorView);
			mTextView = (TextView) findViewById(R.id.textView);
			mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		}

		@Override
		void sendRequest() {
			cancel();
			mTextView.setVisibility(View.GONE);
			mErrorView.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.VISIBLE);
			mRequest = new PhotoInfoRequest(getApiKey(), mEntry.id,
					mEntry.secret, this, this);
			getVolley().requestQueue.add(mRequest);
		}
	}

	private abstract class VolleyRequestListeners<T> implements
			Response.Listener<T>, Response.ErrorListener {

		Request<T> mRequest;

		abstract void init();

		abstract void sendRequest();

		void cancel() {
			if (mRequest != null) {
				mRequest.cancel();
				mRequest = null;
			}
		}
	}
}
