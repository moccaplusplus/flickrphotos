package com.tomaszgawel.flickrphotos.provider;

import android.content.SearchRecentSuggestionsProvider;

public class RecentSearchProvider extends SearchRecentSuggestionsProvider {

	public static final String AUTHORITY = "com.tomaszgawel.flickrphotos";
	public static final int MODE = DATABASE_MODE_QUERIES;

	public RecentSearchProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
