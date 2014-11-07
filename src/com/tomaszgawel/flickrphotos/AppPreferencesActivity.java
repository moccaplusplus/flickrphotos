package com.tomaszgawel.flickrphotos;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class AppPreferencesActivity extends PreferenceActivity {

	// TODO : put api key to preferences
	public static final String API_KEY = "7acf6968c81051637d18ebeb85258588";
	public static int COUNT_PER_PAGE = 45;

	// clear cache
	// clear search history
	// use location

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
}
