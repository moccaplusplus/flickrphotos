package com.tomaszgawel.flickrphotos;

import android.app.Fragment;
import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.tomaszgawel.flickrphotos.json.PhotoSearchPage;
import com.tomaszgawel.flickrphotos.location.LocationHelper;
import com.tomaszgawel.flickrphotos.volley.PhotoSearchRequest;
import com.tomaszgawel.flickrphotos.volley.VolleyHelper;

public final class PhotoSearchStateFragment extends Fragment
implements Listener<PhotoSearchPage>, ErrorListener, LocationHelper.Listener {

	public static final String TAG = PhotoSearchStateFragment.class.getName();

	public static void init(PhotoSearchActivity a) {
		final FragmentManager fm = a.getFragmentManager();
		PhotoSearchStateFragment f = (PhotoSearchStateFragment) fm.findFragmentByTag(PhotoSearchStateFragment.TAG);
		if (f == null) {
			fm.beginTransaction().add(
					new PhotoSearchStateFragment(), PhotoSearchStateFragment.TAG).commit();
			fm.executePendingTransactions();
		} else {
			if (f.hasRunningQuery()) {
				a.onLoading(f.mQuery, f.mPage);
			} else if (f.mResponse != null) {
				a.onResult(f.mResponse, f.mHasNextPage);
			} else if (f.mError != null) {
				a.onError(f.mError);
			}
		}
	}

	public static PhotoSearchStateFragment get(PhotoSearchActivity a) {
		return (PhotoSearchStateFragment) a.getFragmentManager().findFragmentByTag(TAG);
	}

	private final LocationHelper mLocationHelper = new LocationHelper();
	private VolleyHelper mVolleyHelper;
	private PhotoSearchRequest mLastRequest;
	private String mQuery;
	private VolleyError mError;
	private PhotoSearchPage mResponse;
	private int mPage;
	private boolean mHasNextPage;

	public PhotoSearchStateFragment() {
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mVolleyHelper = VolleyHelper.getInstance(getActivity());
		mLocationHelper.init(getActivity(), this);
	}

	@Override
	public void onDestroy() {
		cancelLastRequest();
		mLocationHelper.destroy();
		super.onDestroy();
	}

	@Override
	public void onLocationUpdate(Location location) {

	}

	@Override
	public void onLocationProviderStateChange(
			boolean gpsProviderEnabled, boolean networkProviderEnabled) {

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		mError = error;
		if (getActivity() != null) {
			((PhotoSearchActivity) getActivity()).onError(mError);
		}
	}

	@Override
	public void onResponse(PhotoSearchPage response) {
		mResponse = response;
		mHasNextPage = mResponse.pageCount > mPage;
		if (getActivity() != null) {
			((PhotoSearchActivity) getActivity()).onResult(
					mResponse, mHasNextPage);
		}
	}

	public void query(String query) {
		cancelLastRequest();
		mQuery = query;
		mPage = 1;
		queueRequest();
	}

	public boolean nextPage() {
		if (!hasNextPage()) {
			return false;
		}
		if (hasRunningQuery()) {
			return false;
		}
		if (TextUtils.isEmpty(mQuery)) {
			return false;
		}
		mPage++;
		queueRequest();
		return true;
	}

	public boolean hasNextPage() {
		return mHasNextPage;
	}

	public boolean hasRunningQuery() {
		return mLastRequest == null;
	}

	private void queueRequest() {
		mLastRequest = new PhotoSearchRequest(getApiKey(), mQuery, mPage,
				mLocationHelper.getLocation(), this, this);
		mVolleyHelper.requestQueue.add(mLastRequest);
		if (getActivity() != null) {
			((PhotoSearchActivity) getActivity()).onLoading(mQuery, mPage);
		}
	}

	private void cancelLastRequest() {
		if (mLastRequest != null) {
			mLastRequest.cancel();
			mLastRequest = null;
		}
		mQuery = null;
		mError = null;
		mResponse = null;
		mHasNextPage = false;
	}

	private String getApiKey() {
		// TODO : put api key to preferences
		return AppPreferencesActivity.API_KEY;
	}
}