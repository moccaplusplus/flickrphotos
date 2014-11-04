package com.tomaszgawel.flickrphotos.jsonentity;

public class FlickrApiBaseJsonResponse {

	public String stat;
	public int code;
	public String message;

	public void throwIfFailed() throws FlickrApiFailureException {
		if(!"ok".equalsIgnoreCase(stat)) {
			throw new FlickrApiFailureException(this);
		}
	}
}
