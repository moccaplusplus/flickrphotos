package com.tomaszgawel.flickrphotos.jsonentity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoSearchResult extends FlickrApiBaseJsonResponse {

	@JsonProperty("photos")
	public PhotoSearchPage photoSearchPage;
}
