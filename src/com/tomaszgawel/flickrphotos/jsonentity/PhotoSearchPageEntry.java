package com.tomaszgawel.flickrphotos.jsonentity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoSearchPageEntry implements Parcelable {

	public enum PhotoSize {

		NORMAL('c'),
		THUMB('s');

		final char code;

		private PhotoSize(char code) {
			this.code = code;
		}
	}

	public String id;
	public String owner;
	public String secret;
	public String server;
	public int farm;
	public String title;
	@JsonProperty("ispublic")
	public boolean isPublic;
	@JsonProperty("isfriend")
	public boolean isFriend;
	@JsonProperty("isfamily")
	public boolean isFamily;
	public double latitude;
	public double longitude;
	public int accuracy;

	@Override
	public String toString() {
		return "id: " + id +
				", title: " + title +
				", owner: " + owner +
				", secret: " + secret +
				", server: " + server +
				", farm: " + farm +
				", isPublic: " + isPublic +
				", isFriend: " + isFriend +
				", isFamily: " + isFamily +
				", latitude: " + latitude +
				", longitude: " + longitude +
				", accuracy: " + accuracy;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(owner);
		dest.writeString(secret);
		dest.writeString(server);
		dest.writeInt(farm);
		dest.writeString(title);
		dest.writeByte((byte) (isPublic ? 1 : 0));
		dest.writeByte((byte) (isFriend ? 1 : 0));
		dest.writeByte((byte) (isFamily ? 1 : 0));
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeInt(accuracy);
	}

	public static final Creator<PhotoSearchPageEntry> CREATOR = new Creator<PhotoSearchPageEntry>() {

		@Override
		public PhotoSearchPageEntry[] newArray(int size) {
			return new PhotoSearchPageEntry[size];
		}

		@Override
		public PhotoSearchPageEntry createFromParcel(Parcel source) {
			PhotoSearchPageEntry o = new PhotoSearchPageEntry();
			o.id = source.readString();
			o.owner = source.readString();
			o.secret = source.readString();
			o.server = source.readString();
			o.farm = source.readInt();
			o.title = source.readString();
			o.isPublic = source.readByte() == 1;
			o.isFriend = source.readByte() == 1;
			o.isFamily = source.readByte() == 1;
			o.latitude = source.readDouble();
			o.longitude = source.readDouble();
			o.accuracy = source.readInt();
			return o;
		}
	};

	public String getPhotoUrl(PhotoSize size) {
		// https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
		return new StringBuilder("https://farm").append(farm)
				.append(".staticflickr.com/")
				.append(server).append("/").append(id)
				.append("_").append(secret).append("_").append(size.code)
				.append(".jpg").toString();
	}
}