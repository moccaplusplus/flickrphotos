package com.tomaszgawel.flickrphotos.json;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoSearchPage {

	public static final class Entry implements Parcelable {

		public static final Creator<Entry> CREATOR = new Creator<Entry>() {

			@Override
			public Entry[] newArray(int size) {
				return new Entry[size];
			}

			@Override
			public Entry createFromParcel(Parcel source) {
				Entry entry = new Entry();
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
	}

	@JsonProperty("page")
	public int pageNo;

	@JsonProperty("pages")
	public int pageCount;

	@JsonProperty("perpage")
	public int countPerPage;

	@JsonProperty("total")
	public int totalCount;

	@JsonProperty("photo")
	public List<Entry> photoList;
}
