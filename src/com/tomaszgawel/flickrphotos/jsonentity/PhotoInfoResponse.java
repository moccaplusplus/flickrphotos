package com.tomaszgawel.flickrphotos.jsonentity;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;

public class PhotoInfoResponse extends FlickrApiBaseJsonResponse {

	private static final ObjectReader sReader =
			jsonMapper.reader(PhotoInfoResponse.class);

	public static PhotoInfoResponse parse(String jsonString)
			throws JsonProcessingException, IOException {
		return sReader.readValue(jsonString);
	}

	@JsonProperty("photo")
	public PhotoInfo photoInfo;
}