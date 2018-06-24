package br.ufpe.utils;

public class StringUtils {

	public static String difference(String keyName, String csvName) {
		if (keyName.contains("-")) {
			keyName = keyName.substring(0, keyName.lastIndexOf('-'));
		}
		if (csvName.contains("-")) {
			csvName = csvName.substring(0, csvName.lastIndexOf('-'));
		}
		return csvName.substring(
				csvName.lastIndexOf(keyName) + keyName.length(),
				csvName.length());
	}
}
