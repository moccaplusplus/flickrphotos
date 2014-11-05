package com.tomaszgawel.flickrphotos.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FlickrApiBaseJsonResponse {

	static final ObjectMapper jsonMapper = new ObjectMapper().configure(
			DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public String stat;
	public int code;
	public String message;

	public void throwIfFailed() throws FlickrApiFailureException {
		if(!"ok".equalsIgnoreCase(stat)) {
			throw new FlickrApiFailureException(this);
		}
	}
}
