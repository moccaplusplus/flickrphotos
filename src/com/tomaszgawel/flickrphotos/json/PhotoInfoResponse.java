package com.tomaszgawel.flickrphotos.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class PhotoInfoResponse extends FlickrApiBaseJsonResponse {

	private static final ObjectReader sReader =
			jsonMapper.reader(PhotoInfoResponse.class);

	private static final ObjectWriter sWriter =
			jsonMapper.writerWithType(PhotoInfoResponse.class);

	public static PhotoInfoResponse parse(String jsonString)
			throws JsonProcessingException, IOException {
		return sReader.readValue(jsonString);
	}

	public static String asString(PhotoInfoResponse value)
			throws JsonProcessingException {
		return sWriter.writeValueAsString(value);
	}

	public static byte[] asBytes(PhotoInfoResponse value)
			throws JsonProcessingException {
		return sWriter.writeValueAsBytes(value);
	}

	@JsonProperty("photo")
	public PhotoInfo photoInfo;
}
