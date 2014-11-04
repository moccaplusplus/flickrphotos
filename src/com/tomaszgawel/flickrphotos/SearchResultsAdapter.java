package com.tomaszgawel.flickrphotos;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoSearchPageEntry;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoSearchPageEntry.PhotoSize;

public class SearchResultsAdapter extends BaseAdapter {

	private static final class ViewTag {

		NetworkImageView mImageView;
		TextView mTextView;
	}

	private List<PhotoSearchPageEntry> mEntries;

	@Override
	public int getCount() {
		return mEntries == null ? 0 : mEntries.size();
	}

	@Override
	public PhotoSearchPageEntry getItem(int position) {
		return mEntries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = parent.getContext();
		final ViewTag tag;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_photo_search, parent, false);
			tag = new ViewTag();
			tag.mImageView = (NetworkImageView) convertView.findViewById(R.id.imageView);
			tag.mTextView = (TextView) convertView.findViewById(R.id.textView);
			convertView.setTag(tag);
		} else {
			tag = (ViewTag) convertView.getTag();
		}
		final PhotoSearchPageEntry item = getItem(position);
		tag.mImageView.setImageUrl(item.getPhotoUrl(PhotoSize.THUMB),
				VolleyHelper.getInstance(context).imageLoader);
		tag.mTextView.setText(item.title);
		return convertView;
	}

	public void swapEntries(List<PhotoSearchPageEntry> entries) {
		mEntries = entries;
		notifyDataSetChanged();
	}

	public void clearEntries() {
		mEntries = null;
		notifyDataSetChanged();
	}
}
