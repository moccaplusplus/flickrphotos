package com.tomaszgawel.flickrphotos;

import android.content.SearchRecentSuggestionsProvider;

public class FlickrRecentSearchProvider extends SearchRecentSuggestionsProvider {

	public static final String AUTHORITY = "com.tomaszgawel.flickrphotos";
	public static final int MODE = DATABASE_MODE_QUERIES;

	public FlickrRecentSearchProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
