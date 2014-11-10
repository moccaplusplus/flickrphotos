package com.tomaszgawel.flickrphotos;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.tomaszgawel.flickrphotos.adapter.RecentQueriesAdapter;
import com.tomaszgawel.flickrphotos.provider.RecentSearchProvider;

public class RecentQueriesFragment extends DialogFragment implements
LoaderCallbacks<Cursor>, OnItemClickListener, OnClickListener {

	private static final String TAG = RecentQueriesFragment.class.getName();

	public static void show(PhotoSearchActivity a) {
		FragmentManager fm = a.getFragmentManager();
		new RecentQueriesFragment().show(fm, TAG);
		fm.executePendingTransactions();
	}

	private CursorAdapter mRecentSearchesAdapter;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog d = super.onCreateDialog(savedInstanceState);
		d.setTitle(getString(R.string.recent_queries_title));
		return d;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_recent_queries, container);
		mRecentSearchesAdapter = new RecentQueriesAdapter(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1);
		final ListView listView = (ListView) root.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listView.setEmptyView(root.findViewById(R.id.emptyView));
		listView.setAdapter(mRecentSearchesAdapter);
		getLoaderManager().initLoader(1, null, this);
		root.findViewById(R.id.close).setOnClickListener(this);
		root.findViewById(R.id.clearRecent).setOnClickListener(this);
		return root;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.close:
				break;

			case R.id.clearRecent:
				RecentSearchProvider.createSuggestions(getActivity()).clearHistory();
				break;
		}
		dismiss();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Cursor c = (Cursor) mRecentSearchesAdapter.getItem(position);
		final String query = c.getString(c.getColumnIndex(
				RecentSearchProvider.COLUMN_QUERY));
		((PhotoSearchActivity) getActivity()).submitQuery(query);
		dismiss();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), RecentSearchProvider.URI_ALL,
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
