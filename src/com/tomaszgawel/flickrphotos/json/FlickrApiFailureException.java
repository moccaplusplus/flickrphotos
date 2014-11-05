package com.tomaszgawel.flickrphotos.json;

public class FlickrApiFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public final FlickrApiBaseJsonResponse flickrApiResponse;

	public FlickrApiFailureException(FlickrApiBaseJsonResponse resp) {
		super("Flickr API failure. Error code: " + resp.code + ".\n" + resp.message);
		this.flickrApiResponse = resp;
	}

}
