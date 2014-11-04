package com.tomaszgawel.flickrphotos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.tomaszgawel.flickrphotos.jsonentity.PhotoSearchPage;

public class PhotoSearchActivity extends Activity implements OnQueryTextListener, OnItemClickListener {

	private final SearchResultsAdapter mAdapter = new SearchResultsAdapter();
	private SearchView mSearchView;
	private GridView mGridView;
	private TextView mPageInfo;
	private View mNextPageButton;
	private View mPrevPageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);

		mSearchView = (SearchView) findViewById(R.id.searchView);
		mSearchView.setOnQueryTextListener(this);

		PhotoSearchStateFragment.init(this);
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
		PhotoSearchStateFragment.get(this).query(query);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Intent intent = new Intent(this, SinglePhotoActivity.class);
		intent.putExtra(SinglePhotoActivity.EXTRA_ITEM, mAdapter.getItem(position));
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
