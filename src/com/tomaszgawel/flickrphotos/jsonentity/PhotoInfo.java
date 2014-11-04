package com.tomaszgawel.flickrphotos.jsonentity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tomaszgawel.flickrphotos.jsonentity.Deserializers.ContentDeserializer;
import com.tomaszgawel.flickrphotos.jsonentity.Deserializers.OwnerDeserializer;
import com.tomaszgawel.flickrphotos.jsonentity.Deserializers.TagsDeserializer;

public class PhotoInfo {

	@JsonProperty("owner")
	@JsonDeserialize(using = OwnerDeserializer.class)
	public String ownerInfo;

	@JsonDeserialize(using = ContentDeserializer.class)
	public String title;

	@JsonDeserialize(using = ContentDeserializer.class)
	public String description;

	@JsonProperty("tag")
	@JsonDeserialize(using = TagsDeserializer.class)
	public List<String> tags;

	public LocationInfo location;
}
