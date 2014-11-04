package com.tomaszgawel.flickrphotos.jsonentity;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoSearchEntry implements Parcelable {

	public static final Creator<PhotoSearchEntry> CREATOR = new Creator<PhotoSearchEntry>() {

		@Override
		public PhotoSearchEntry[] newArray(int size) {
			return new PhotoSearchEntry[size];
		}

		@Override
		public PhotoSearchEntry createFromParcel(Parcel source) {
			PhotoSearchEntry entry = new PhotoSearchEntry();
			entry.id = source.readString();
			entry.secret = source.readString();
			entry.server = source.readString();
			entry.farm = source.readInt();
			entry.title = source.readString();
			return entry;
		}
	};

	public String id;
	public String secret;
	public String server;
	public int farm;
	public String title;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(secret);
		dest.writeString(server);
		dest.writeInt(farm);
		dest.writeString(title);
	}

	public String getPhotoUrl(PhotoSize size) {
		// https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
		return new StringBuilder("https://farm").append(farm)
				.append(".staticflickr.com/")
				.append(server).append("/").append(id)
				.append("_").append(secret).append("_").append(size.code)
				.append(".jpg").toString();
	}
}