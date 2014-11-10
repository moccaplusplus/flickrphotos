package com.tomaszgawel.flickrphotos.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

public class LocationHelper {

	private static final float MIN_DISTANCE = 10;
	private static final long MIN_TIME = 60 * 1000;

	public static void openLocationSettings(Context context) {
		context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	public interface Listener {

		void onLocationUpdate(Location location);

		void onLocationProviderStateChange(boolean gpsProviderEnabled,
				boolean networkProviderEnabled);
	}

	private LocationManager mLocationManager;
	private Location mLocation;
	private boolean mIsNetworkProviderEnabled;
	private boolean mIsGpsProviderEnabled;
	private Listener mListener;
	private final LocationListener mGpsListener = new InternalListener();
	private final LocationListener mNetworkListener = new InternalListener() {

		@Override
		public void onLocationChanged(Location location) {
			if (mIsGpsProviderEnabled
					&& mLocation != null
					&& mLocation.getProvider() == LocationManager.GPS_PROVIDER) {
				return;
			}
			super.onLocationChanged(location);
		}
	};

	public void init(Context context, Listener listener) {
		mListener = listener;
		mLocationManager = (LocationManager) context.getSystemService(
				Context.LOCATION_SERVICE);
		mIsGpsProviderEnabled = mLocationManager.isProviderEnabled(
				LocationManager.GPS_PROVIDER);
		if (mIsGpsProviderEnabled) {
			mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		mIsNetworkProviderEnabled = mLocationManager.isProviderEnabled(
				LocationManager.NETWORK_PROVIDER);
		if (mIsNetworkProviderEnabled && mLocation == null) {
			mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mGpsListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkListener);

		if (mListener != null) {
			mListener.onLocationProviderStateChange(mIsGpsProviderEnabled, mIsNetworkProviderEnabled);
			if (mLocation != null) {
				mListener.onLocationUpdate(mLocation);
			}
		}
	}

	public Location getLocation() {
		return mLocation;
	}

	public boolean isAnyProviderEnabled() {
		return mIsGpsProviderEnabled || mIsNetworkProviderEnabled;
	}

	public boolean isNetworkProviderEnabled() {
		return mIsNetworkProviderEnabled;
	}

	public boolean isGpsProviderEnabled() {
		return mIsGpsProviderEnabled;
	}

	public void destroy() {
		mLocationManager.removeUpdates(mNetworkListener);
		mLocationManager.removeUpdates(mGpsListener);
		mListener = null;
	}

	void setLocation(Location location) {
		mLocation = location;
		if (mListener != null) {
			mListener.onLocationUpdate(location);
		}
	}

	void setProviderEnabled(String provider, boolean enabled) {
		switch (provider) {
			case LocationManager.GPS_PROVIDER:
				mIsGpsProviderEnabled = enabled;
				break;
			case LocationManager.NETWORK_PROVIDER:
				mIsNetworkProviderEnabled = enabled;
				break;
		}
		if (mListener != null) {
			mListener.onLocationProviderStateChange(
					mIsGpsProviderEnabled, mIsNetworkProviderEnabled);
		}
	}

	private class InternalListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			setLocation(location);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
			setProviderEnabled(provider, true);
		}

		@Override
		public void onProviderDisabled(String provider) {
			setProviderEnabled(provider, false);
		}
	}
}
