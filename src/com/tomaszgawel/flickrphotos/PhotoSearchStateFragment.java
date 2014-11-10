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

	public static final String API_KEY = "7acf6968c81051637d18ebeb85258588";
	public static int COUNT_PER_PAGE = 45;

	private static final String TAG = PhotoSearchStateFragment.class.getName();

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
				a.onResult(f.mResponse);
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
		mLastRequest = null;
		mError = error;
		if (getActivity() != null) {
			((PhotoSearchActivity) getActivity()).onError(mError);
		}
	}

	@Override
	public void onResponse(PhotoSearchPage response) {
		mLastRequest = null;
		mResponse = response;
		if (getActivity() != null) {
			((PhotoSearchActivity) getActivity()).onResult(mResponse);
		}
	}

	public void query(String query) {
		cancelLastRequest();
		mQuery = query;
		mPage = 1;
		queueRequest();
	}

	public boolean nextPage() {
		return toPage(mPage + 1);
	}

	public boolean prevPage() {
		return toPage(mPage - 1);
	}

	public boolean toPage(int page) {
		if (page > mResponse.pageCount) {
			return false;
		}
		if (page < 1) {
			return false;
		}
		if (hasRunningQuery()) {
			return false;
		}
		if (TextUtils.isEmpty(mQuery)) {
			return false;
		}
		mPage = page;
		queueRequest();
		return true;
	}

	public boolean hasRunningQuery() {
		return mLastRequest != null;
	}

	public void requery() {
		if (mLastRequest == null) {
			queueRequest();
		}
	}

	private void queueRequest() {
		mLastRequest = new PhotoSearchRequest(API_KEY, mQuery, mPage,
		        COUNT_PER_PAGE, mLocationHelper.getLocation(), this, this);
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
	}
}