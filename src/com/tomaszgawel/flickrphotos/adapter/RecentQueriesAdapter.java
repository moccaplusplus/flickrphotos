package com.tomaszgawel.flickrphotos.adapter;

import android.content.Context;
import android.widget.SimpleCursorAdapter;

import com.tomaszgawel.flickrphotos.provider.RecentSearchProvider;

public class RecentQueriesAdapter extends SimpleCursorAdapter {

	public static final String[] PROJECTION = { RecentSearchProvider.COLUMN_QUERY };

	public RecentQueriesAdapter(Context context, int layoutResId, int textViewId) {
		super(context, layoutResId, null, PROJECTION, new int[] { textViewId }, 0);
	}
}
