package com.tomaszgawel.flickrphotos;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.tomaszgawel.flickrphotos.adapter.RecentQueriesAdapter;
import com.tomaszgawel.flickrphotos.provider.RecentSearchProvider;

public class RecentQueriesActivity extends Activity implements
LoaderCallbacks<Cursor>, OnItemClickListener, OnClickListener {

	private CursorAdapter mRecentSearchesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_queries);
		mRecentSearchesAdapter = new RecentQueriesAdapter(this,
				android.R.layout.simple_list_item_1, android.R.id.text1);
		final ListView listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listView.setEmptyView(findViewById(R.id.emptyView));
		listView.setAdapter(mRecentSearchesAdapter);
		getLoaderManager().initLoader(1, null, this);
		findViewById(R.id.close).setOnClickListener(this);
		findViewById(R.id.clearRecent).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.close:
				finish();
				break;

			case R.id.clearRecent:
				RecentSearchProvider.createSuggestions(this).clearHistory();
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Intent intent = new Intent(this, PhotoSearchActivity.class);
		final Cursor c = (Cursor) mRecentSearchesAdapter.getItem(position);
		final String query = c.getString(c.getColumnIndex(
				RecentSearchProvider.COLUMN_QUERY));
		intent.putExtra(PhotoSearchActivity.EXTRA_QUERY, query);
		startActivity(intent);
		finish();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, RecentSearchProvider.URI_ALL,
				RecentQueriesAdapter.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mRecentSearchesAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mRecentSearchesAdapter.swapCursor(null);
	}
}
