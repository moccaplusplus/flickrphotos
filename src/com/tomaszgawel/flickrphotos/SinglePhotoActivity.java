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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

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
		mInfoRequestLogic.init((ViewGroup) findViewById(R.id.infoFrame));
		mImageRequestLogic.init((ViewGroup) findViewById(R.id.imageFrame));
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
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
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

	private class ImageRequestLogic extends VolleyRequestListeners<Bitmap> {

		private ImageView mImageView;
		private Point mScreenSize;
		private String mUrl;

		@Override
		public void init(ViewGroup root) {
			super.init((ViewGroup) root.getChildAt(0));
			mScreenSize = new Point();
			getWindowManager().getDefaultDisplay().getSize(mScreenSize);
			mUrl = PhotoUrl.NORMAL.get(mEntry);
			mImageView = (ImageView) root.getChildAt(1);
		}

		public void sendRequest() {
			sendRequest(getString(R.string.loading_image));
		}

		@Override
		public void sendRequest(CharSequence loadingText) {
			super.sendRequest(loadingText);
			mImageView.setImageBitmap(null);
			mImageView.setVisibility(View.GONE);
			setShareIntent(null);
			mRequest = new ImageRequest(mUrl, this, mScreenSize.x,
					mScreenSize.y, Config.RGB_565, this);
			getVolley().requestQueue.add(mRequest);

		}

		@Override
		public void onResponse(Bitmap response) {
			super.onResponse(response);
			mImageView.setVisibility(View.VISIBLE);
			mImageView.setImageBitmap(response);
			setShareIntent(PhotoShareProvider.createShareIntent(mUrl,
					displayName(mEntry.title), response.getByteCount()));
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			super.onErrorResponse(error);
		}

		private String displayName(String title) {
			if (TextUtils.isEmpty(title)) {
				return getString(R.string.photo_display_name_missing);
			}
			if (title.length() > 25) {
				return getString(R.string.photo_display_name_truncated,
						title.substring(0, 22));
			}
			return title;
		}
	}

	private class InfoRequestLogic extends VolleyRequestListeners<PhotoInfo> {

		private TextView mTextView;

		@Override
		public void init(ViewGroup root) {
			super.init((ViewGroup) root.getChildAt(0));
			mTextView = (TextView) root.getChildAt(1);
			mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		}

		public void sendRequest() {
			sendRequest(getString(R.string.loading_description));
		}

		@Override
		public void sendRequest(CharSequence loadingText) {
			super.sendRequest(loadingText);
			mTextView.setVisibility(View.GONE);
			mRequest = new PhotoInfoRequest(PhotoSearchStateFragment.API_KEY,
			        mEntry.id, mEntry.secret, this, this);
			getVolley().requestQueue.add(mRequest);
		}

		@Override
		public void onResponse(PhotoInfo response) {
			super.onResponse(response);
			final LocationInfo location = response.location;
			mTextView.setText(Html.fromHtml(
					getString(R.string.photo_info_summary, response.title,
							response.description, response.owner,
							location.latitude, location.longitude,
							location.locality, location.county,
							location.region, location.country,
							TextUtils.join(", ", response.tags))));
			mTextView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			super.onErrorResponse(error);
			mTextView.setVisibility(View.GONE);
		}
	}

	private abstract class VolleyRequestListeners<T> implements
	Response.Listener<T>, Response.ErrorListener, OnClickListener {

		ViewGroup mRoot;
		Request<T> mRequest;
		View mLoadingView;
		TextView mTextView;
		View mRetryButton;
		CharSequence mLastRequestLoadingText;

		public void init(ViewGroup root) {
			mRoot = root;
			mLoadingView = root.getChildAt(0);
			mTextView = (TextView) root.getChildAt(1);
			mRetryButton = root.getChildAt(2);
			mRetryButton.setOnClickListener(this);
		}

		public void sendRequest(CharSequence loadingText) {
			cancel();
			mTextView.setText(mLastRequestLoadingText = loadingText);
			mRetryButton.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.VISIBLE);
			mRoot.setVisibility(View.VISIBLE);
		}

		public void cancel() {
			if (mRequest != null) {
				mRequest.cancel();
				mRequest = null;
				onErrorResponse(new VolleyError("Request cancelled"));
			}
		}

		@Override
		public void onResponse(T response) {
			mRequest = null;
			mRoot.setVisibility(View.GONE);
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			mRequest = null;
			mLoadingView.setVisibility(View.GONE);
			mRetryButton.setVisibility(View.VISIBLE);
			mTextView.setText(getString(R.string.load_failed, error.toString()));
			mRoot.setVisibility(View.VISIBLE);
		}

		@Override
		public void onClick(View v) {
			sendRequest(mLastRequestLoadingText);
		}
	}
}
