package com.raos.autocode.core.algorithm;

import java.util.LinkedList;
import java.util.List;

public interface StringUtil {

	public static long hash(String str, int h) {
		return hash(str.toCharArray(), h);
	}

	public static long hash(char[] str, int h) {
		long i = 0;

		for (int j = 0; j < str.length; j++) {
			i += str[j] * Math.pow(h, j);
		}

		return i;
	}

	public static long hash(byte[] str, int h) {
		long i = 0;

		for (int j = 0; j < str.length; j++) {
			i += str[j] * Math.pow(h, j);
		}

		return i;
	}

	// Searches for a string using the Rabin-Karp Searching algorithm
	public static List<Integer> rabinKarpSearch(String pattern, String text, int hash) {
		// If the length of the pattern is bigger than the text then there are no
		// occurences
		if (pattern.length() > text.length())
			return List.of();

		// Creates a list
		List<Integer> list = new LinkedList<>();

		// Gets the hashcode of the string
		long pHash = hash(pattern, hash);

		// Substring and get hashcode
		long tHash = hash(text.substring(0, pattern.length()), hash);

		// Check if hashes are the same
		if (pHash == tHash)
			list.add(0);

		// For every character after the substring
		// Check for hashcode equality
		for (int i = pattern.length(); i < text.length(); i++) {
			// Ditch the first character
			tHash -= text.charAt(i - pattern.length());
			// Divide everything by 31
			tHash /= hash;
			// Add the last character
			tHash += text.charAt(i) * Math.pow(hash, pattern.length() - 1);

			// CHeck if hashes are the same
			if (tHash == pHash)
				list.add(i - pattern.length() - 1);
		}

		// Returns the list of occurrences
		return list;
	}

	
}
