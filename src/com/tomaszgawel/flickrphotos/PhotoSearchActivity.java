package com.tomaszgawel.flickrphotos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

	private final SearchResultsAdapter mAdapter = new SearchResultsAdapter();
	private final ProgressErrorUnit mLoadingUnit = new ProgressErrorUnit();
	private final PagerUnit mPagerUnit = new PagerUnit();
	private SearchRecentSuggestions mSearchRecentSuggestions;
	private SearchView mSearchView;
	private GridView mGridView;

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

		mPagerUnit.init(findViewById(R.id.pagerFrame));
		mLoadingUnit.init((ViewGroup) findViewById(R.id.gridProgressError));

		PhotoSearchStateFragment.init(this);
	}

	public void onLoading(String query, int pageNo) {
		mLoadingUnit.showLoading(getString(R.string.loading_query, pageNo));
		mPagerUnit.disable();
		mAdapter.clearEntries();
	}

	public void onError(Exception e) {
		mLoadingUnit.showError(getString(R.string.load_failed, e.toString()));
	}

	public void onResult(PhotoSearchPage result) {
		mLoadingUnit.hide();
		if (result.photoList != null) {
			mAdapter.swapEntries(result.photoList);
		}
		mPagerUnit.setPage(result.pageNo, result.pageCount);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		mSearchRecentSuggestions.saveRecentQuery(query, null);
		getState().query(query);
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
			case R.id.action_recent_queries:
				RecentQueriesFragment.show(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void submitQuery(String query) {
		mSearchView.setQuery(query, true);
	}

	PhotoSearchStateFragment getState() {
		return PhotoSearchStateFragment.get(this);
	}

	private class PagerUnit implements View.OnClickListener {

		private TextView mPageInfo;
		private View mNextPageButton;
		private View mPrevPageButton;
		private View mRoot;

		public void init(View root) {
			mRoot = root;
			mPageInfo = (TextView) findViewById(R.id.pageInfoText);
			mNextPageButton = root.findViewById(R.id.nextPageButton);
			mNextPageButton.setOnClickListener(this);
			mPrevPageButton = root.findViewById(R.id.prevPageButton);
			mPrevPageButton.setOnClickListener(this);
			mRoot.setVisibility(View.GONE);
		}

		public void disable() {
			mRoot.setVisibility(View.GONE);
		}

		public void setPage(int pageNo, int pageCount) {
			if (pageCount == 0) {
				mPageInfo.setText(R.string.empty_result);
				mNextPageButton.setEnabled(false);
				mPrevPageButton.setEnabled(false);
			} else {
				mPageInfo.setText(getString(R.string.page_info, pageNo, pageCount));
				mNextPageButton.setEnabled(pageCount > pageNo);
				mPrevPageButton.setEnabled(pageNo > 1);
			}
			mRoot.setVisibility(View.VISIBLE);
		}

		@Override
		public void onClick(View v) {
			if (v == mNextPageButton) {
				getState().nextPage();
			} else if (v == mPrevPageButton) {
				getState().prevPage();
			}
		}
	}

	private class ProgressErrorUnit implements View.OnClickListener {

		public View mLoadingView;
		public TextView mStatusView;
		public View mRetryButton;
		private ViewGroup mRoot;

		public void showLoading(CharSequence loadingInfo) {
			mLoadingView.setVisibility(View.VISIBLE);
			mRetryButton.setVisibility(View.GONE);
			mStatusView.setText(loadingInfo);
			mRoot.setVisibility(View.VISIBLE);
			mRetryButton.setOnClickListener(this);
		}

		public void showError(CharSequence errorInfo) {
			mLoadingView.setVisibility(View.GONE);
			mRetryButton.setVisibility(View.VISIBLE);
			mStatusView.setText(errorInfo);
			mRoot.setVisibility(View.VISIBLE);
		}

		public void hide() {
			mRoot.setVisibility(View.GONE);
		}

		public void init(ViewGroup root) {
			mRoot = root;
			mLoadingView = root.getChildAt(0);
			mStatusView = (TextView) root.getChildAt(1);
			mRetryButton = root.getChildAt(2);
		}

		@Override
		public void onClick(View v) {
			getState().requery();
		}
	}
}
