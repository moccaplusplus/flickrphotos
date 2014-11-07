package com.tomaszgawel.flickrphotos.provider;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.net.Uri;
import android.provider.SearchRecentSuggestions;

public class RecentSearchProvider extends SearchRecentSuggestionsProvider {

	public static final String AUTHORITY = "com.tomaszgawel.flickrphotos.search";
	public static final Uri URI_ALL = Uri.parse(
			"content://" + AUTHORITY + "/suggestions");
	public static final int MODE = DATABASE_MODE_QUERIES;
	public static final String COLUMN_QUERY = "display1";

	public static SearchRecentSuggestions createSuggestions(Context context) {
		return new SearchRecentSuggestions(context, AUTHORITY, MODE);
	}

	public RecentSearchProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
