package com.tomaszgawel.flickrphotos.jsonentity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoSearchPage {

	@JsonProperty("page")
	public int pageNo;
	@JsonProperty("pages")
	public int pageCount;
	@JsonProperty("perpage")
	public int countPerPage;
	@JsonProperty("total")
	public int totalCount;
	@JsonProperty("photo")
	public List<PhotoSearchPageEntry> photoList;
}
