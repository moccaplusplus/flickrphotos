package com.tomaszgawel.flickrphotos.jsonentity;

public enum PhotoSize {

	NORMAL('c'),
	THUMB('s');

	final char code;

	private PhotoSize(char code) {
		this.code = code;
	}
}