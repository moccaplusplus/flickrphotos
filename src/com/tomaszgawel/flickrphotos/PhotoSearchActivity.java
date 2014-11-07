package com.tomaszgawel.flickrphotos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.tomaszgawel.flickrphotos.adapter.SearchResultsAdapter;
import com.tomaszgawel.flickrphotos.json.PhotoSearchPage;
import com.tomaszgawel.flickrphotos.provider.RecentSearchProvider;

public class PhotoSearchActivity extends Activity implements
OnQueryTextListener, OnItemClickListener {

	public static final String EXTRA_QUERY =
			"com.tomaszgawel.flickrphotos.EXTRA_QUERY";

	private final SearchResultsAdapter mAdapter = new SearchResultsAdapter();
	private SearchRecentSuggestions mSearchRecentSuggestions;
	private SearchView mSearchView;
	private GridView mGridView;
	private TextView mPageInfo;
	private View mNextPageButton;
	private View mPrevPageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);

		mSearchRecentSuggestions = RecentSearchProvider.createSuggestions(this);
		mSearchView = (SearchView) findViewById(R.id.searchView);
		mSearchView.setOnQueryTextListener(this);
		PhotoSearchStateFragment.init(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		final String query = intent.getStringExtra(EXTRA_QUERY);
		if (!TextUtils.isEmpty(query)) {
			mSearchView.setQuery(query, true);
		}
	}

	public void onLoading(String query, int pageNo) {
		mAdapter.clearEntries();
		// show loading dialog
	}

	public void onError(Exception e) {
		// show error dialog
	}

	public void onResult(PhotoSearchPage result, boolean hasNextPage) {
		if (result != null) {
			if (result.photoList != null) {
				mAdapter.swapEntries(result.photoList);
			}
			// mPageInfo.setText(result.pageNo + "/" + result.pageCount);
			// mNextPageButton.setEnabled(result.pageCount > result.pageNo);
			// mPrevPageButton.setEnabled(result.pageNo > 1);
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		mSearchRecentSuggestions.saveRecentQuery(query, null);
		PhotoSearchStateFragment.get(this).query(query);
		mSearchView.clearFocus();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Intent intent = new Intent(this, SinglePhotoActivity.class);
		intent.putExtra(SinglePhotoActivity.EXTRA_SEARCH_ITEM,
				mAdapter.getItem(position));
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_settings:
				openPreferences();
				return true;

			case R.id.action_recent_queries:
				openRecentQueries();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openRecentQueries() {
		startActivity(new Intent(this, RecentQueriesActivity.class));
	}

	private void openPreferences() {
		startActivity(new Intent(this, AppPreferencesActivity.class));
	}
}
