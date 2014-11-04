package com.tomaszgawel.flickrphotos.jsonentity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class Deserializers {

	static final class OwnerDeserializer extends
			JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			String name = getString(node.get("username"));
			String realName = getString(node.get("realname"));
			if (TextUtils.isEmpty(name)) {
				if (!TextUtils.isEmpty(realName)) {
					return realName;
				}
			} else {
				return TextUtils.isEmpty(realName) ?
						name : realName + " (" + name + ")";
			}
			if (!TextUtils.isEmpty(name = getString(node.get("path_alias")))) {
				return name;
			}
			return getString(node.get("nsid"));
		}
	}

	static final class ContentDeserializer extends
			JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().<JsonNode> readTree(jp);
			return getString(node.get("_content"));
		}
	}

	static final class TagsDeserializer extends
			JsonDeserializer<List<String>> {

		@Override
		public List<String> deserialize(JsonParser jp,
				DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			final JsonNode node = jp.getCodec().<JsonNode> readTree(jp);
			final JsonNode tagNode = node.get("tag");
			final int size = tagNode == null ? 0 : node.size();
			final List<String> list = new ArrayList<String>(size);
			if (size > 0) {
				final Iterator<JsonNode> it = tagNode.elements();
				while (it.hasNext()) {
					String tag = parseSingleTag(it.next());
					if (!TextUtils.isEmpty(tag)) {
						list.add(tag);
					}
				}
			}
			return list;
		}

		public String parseSingleTag(JsonNode node) {
			String tag = getString(node.get("_content"));
			return TextUtils.isEmpty(tag) ? getString(node.get("raw")) : tag;
		}
	}

	static String getString(JsonNode node) {
		return node == null ? "" : node.asText().trim();
	}
}
