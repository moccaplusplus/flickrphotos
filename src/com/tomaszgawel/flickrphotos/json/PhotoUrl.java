package com.tomaszgawel.flickrphotos.json;

public enum PhotoUrl {

	NORMAL('c'),
	THUMB('s');

	final char sizeCode;

	private PhotoUrl(char sizeCode) {
		this.sizeCode = sizeCode;
	}

	public String get(PhotoSearchPage.Entry e) {
		// https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
		return new StringBuilder("https://farm").append(e.farm)
				.append(".staticflickr.com/")
				.append(e.server).append("/").append(e.id)
				.append("_").append(e.secret).append("_").append(sizeCode)
				.append(".jpg").toString();
	}
}