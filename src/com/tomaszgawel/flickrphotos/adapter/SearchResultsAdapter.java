package com.tomaszgawel.flickrphotos.adapter;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.tomaszgawel.flickrphotos.R;
import com.tomaszgawel.flickrphotos.json.PhotoSearchPage;
import com.tomaszgawel.flickrphotos.json.PhotoUrl;
import com.tomaszgawel.flickrphotos.volley.VolleyHelper;

public class SearchResultsAdapter extends BaseAdapter {

	public static final class SquareImageView extends NetworkImageView {

		public SquareImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			setMeasuredDimension(width, width);
		}
	}

	private static final class ViewTag {

		NetworkImageView mImageView;
		TextView mTextView;
	}

	private List<PhotoSearchPage.Entry> mEntries;

	@Override
	public int getCount() {
		return mEntries == null ? 0 : mEntries.size();
	}

	@Override
	public PhotoSearchPage.Entry getItem(int position) {
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
		final PhotoSearchPage.Entry item = getItem(position);
		tag.mImageView.setImageUrl(PhotoUrl.THUMB.get(item),
				VolleyHelper.getInstance(context).imageLoader);
		tag.mTextView.setText(item.title);
		return convertView;
	}

	public void swapEntries(List<PhotoSearchPage.Entry> entries) {
		mEntries = entries;
		notifyDataSetChanged();
	}

	public void clearEntries() {
		mEntries = null;
		notifyDataSetChanged();
	}
}
