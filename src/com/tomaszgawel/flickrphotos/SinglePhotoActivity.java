package com.tomaszgawel.flickrphotos;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoSearchPageEntry;
import com.tomaszgawel.flickrphotos.jsonentity.PhotoSearchPageEntry.PhotoSize;

public class SinglePhotoActivity extends Activity {

	public static final String EXTRA_ITEM = SinglePhotoActivity.class.getName() + ".EXTRA_ITEM";

	private NetworkImageView mImageView;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_photo);
		PhotoSearchPageEntry item = getIntent().getParcelableExtra(EXTRA_ITEM);
		setTitle(item.title);
		mTextView = (TextView) findViewById(R.id.textView);
		mTextView.setText(item.toString());
		mImageView = (NetworkImageView) findViewById(R.id.imageView);
		mImageView.setImageUrl(item.getPhotoUrl(PhotoSize.NORMAL),
				VolleyHelper.getInstance(this).imageLoader);
	}
}
