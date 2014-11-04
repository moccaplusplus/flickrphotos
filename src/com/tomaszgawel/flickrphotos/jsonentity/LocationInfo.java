package com.tomaszgawel.flickrphotos.jsonentity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tomaszgawel.flickrphotos.jsonentity.Deserializers.ContentDeserializer;

public class LocationInfo {

	public double latitude;

	public double longitude;

	@JsonDeserialize(using = ContentDeserializer.class)
	public String locality;

	@JsonDeserialize(using = ContentDeserializer.class)
	public String county;

	@JsonDeserialize(using = ContentDeserializer.class)
	public String region;

	@JsonDeserialize(using = ContentDeserializer.class)
	public String country;
}
