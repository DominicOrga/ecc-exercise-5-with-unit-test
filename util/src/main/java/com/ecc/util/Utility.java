package com.ecc.util;

import java.util.Scanner;
import java.util.Optional;
import java.net.URL;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class Utility {
	
	public static String EMPTY_STRING = "";

	public static Optional<String> getResourcePath(String resource) {
		Optional<URL> resourceURL = 
			Optional.ofNullable(Utility.class.getClassLoader().getResource(resource));

		Optional<String> resourcePath = Optional.empty();

		if (resourceURL.isPresent()) {
			try {
				resourcePath = Optional.of(URLDecoder.decode(resourceURL.get().getPath(), "UTF-8"));
			}
			catch (UnsupportedEncodingException e) {
				return resourcePath;
			}
		}

		return resourcePath;
	}

	public static int countOccurrence(String str, String substr) {

		int count = 0;
		int s = str.length() - substr.length();

		if (s < 0) {
			return count;
		}

		for (int k = 0; k <= s; k++) {
			if (str.substring(k, k + substr.length()).equals(substr)) {
				count++;
			}
		}

		return count;
	}
}