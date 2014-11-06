package com.tomaszgawel.flickrphotos.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class PhotoSearchResponse extends FlickrApiBaseJsonResponse {

	private static final ObjectReader sReader =
			jsonMapper.reader(PhotoSearchResponse.class);

	private static final ObjectWriter sWriter =
			jsonMapper.writerWithType(PhotoSearchResponse.class);

	public static PhotoSearchResponse parse(String jsonString)
			throws JsonProcessingException, IOException {
		return sReader.readValue(jsonString);
	}

	public static String asString(PhotoSearchResponse value)
			throws JsonProcessingException {
		return sWriter.writeValueAsString(value);
	}

	public static byte[] asBytes(PhotoSearchResponse value)
			throws JsonProcessingException {
		return sWriter.writeValueAsBytes(value);
	}

	@JsonProperty("photos")
	public PhotoSearchPage photoSearchPage;
}
