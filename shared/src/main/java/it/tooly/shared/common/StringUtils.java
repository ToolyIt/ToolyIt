package it.tooly.shared.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class StringUtils.
 *
 * @author Darko Sarkanovic
 */
public class StringUtils {

	/** The Constant EMPTY_STRING_ARRAY. */
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * Matches pattern.
	 *
	 * @param string
	 *            the string
	 * @param regExPattern
	 *            the reg ex pattern
	 * @return true, if successful
	 */
	public static boolean matchesPattern(final String string, final String regExPattern) {
		final Pattern pattern = Pattern.compile(regExPattern);
		Matcher matcher = pattern.matcher(string);
		return matcher.find();
	}

	/**
	 * Converts given list of strings to a single string with values quoted and
	 * comma separated (e.g. 'value1','value2','value3')
	 *
	 * @param values
	 *            the values
	 * @return the string
	 */
	public static String toSingleQuotedCommaSeparatedString(final List<String> values) {
		return toSingleQuotedCommaSeparatedString(values.toArray(new String[values.size()]));
	}

	/**
	 * Converts given string array to a single string with values quoted and
	 * comma separated (e.g. 'value1','value2','value3')
	 *
	 * @param values
	 *            the values
	 * @return the string
	 */
	public static String toSingleQuotedCommaSeparatedString(final String[] values) {
		return toTokenSeparatedString(values, ",", "'");
	}

	/**
	 * Converts given string list to a single string with values quoted and
	 * comma separated (e.g. value1,value2,value3)
	 *
	 * @param values
	 *            the values
	 * @return the string
	 */
	public static String toCommaSeparatedString(final List<String> values) {
		return toCommaSeparatedString(values.toArray(new String[values.size()]));
	}

	/**
	 * Converts given string array to a single string with values quoted and
	 * comma separated (e.g. value1,value2,value3)
	 *
	 * @param values
	 *            the values
	 * @return the string
	 */
	public static String toCommaSeparatedString(final String[] values) {
		return toTokenSeparatedString(values, ",", null);
	}

	/**
	 * Given the array of strings, returns a single string consisting of given
	 * strings (optionally quoted and separated by given tokens).
	 *
	 * @param values
	 *            Array of strings to be converted to a single string
	 * @param separatorToken
	 *            Token that separates the source strings (e.g. ',')
	 * @param quoteToken
	 *            Token that surrounds (quotes) the source strings (e.g single
	 *            quote or double quote )
	 * @return the string
	 */
	public static String toTokenSeparatedString(final String[] values, final String separatorToken, final String quoteToken) {
		if (values != null && values.length > 0) {
			StringBuffer buff = new StringBuffer();

			for (int i = 0; i < values.length; i++) {
				// Append quote token (if any)
				if (quoteToken != null) {
					buff.append(quoteToken);
				}

				// Append the real value
				buff.append(values[i]);

				// Append quote token (if any)
				if (quoteToken != null) {
					buff.append(quoteToken);
				}

				// Append separator token (if any)
				if (i < values.length - 1) {
					buff.append(separatorToken);
				}
			}

			return buff.toString();
		}

		return null;
	}

	/**
	 * Given the List of strings, returns a single string consisting of given
	 * strings (optionally quoted and separated by given tokens).
	 *
	 * @param values
	 *            Array of strings to be converted to a single string
	 * @param separatorToken
	 *            Token that separates the source strings (e.g. ',')
	 * @param quoteToken
	 *            Token that surrounds (quotes) the source strings (e.g single
	 *            quote or double quote )
	 * @return the string
	 */
	public static String toTokenSeparatedString(final List values, final String separatorToken, final String quoteToken) {
		if (values != null && values.size() > 0) {
			StringBuffer buff = new StringBuffer();

			for (int i = 0; i < values.size(); i++) {
				// Append quote token (if any)
				if (quoteToken != null) {
					buff.append(quoteToken);
				}

				// Append the real value
				buff.append(values.get(i));

				// Append quote token (if any)
				if (quoteToken != null) {
					buff.append(quoteToken);
				}

				// Append separator token (if any)
				if (i < values.size() - 1) {
					buff.append(separatorToken);
				}
			}

			return buff.toString();
		}

		return null;
	}

	/**
	 * Quotes given string with a double quotes (" ").
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String toQuotedString(final String str) {
		if (str != null) {
			return "\"" + str + "\"";
		}
		return null;
	}

	/**
	 * Quotes given string with a single quotes (' ').
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String toSingleQuotedString(final String str) {
		if (str != null) {
			return "'" + str + "'";
		}
		return null;
	}

	/**
	 * Array to string.
	 *
	 * @param array
	 *            the array
	 * @return the string
	 */
	public static String arrayToString(final String[] array) {
		return arrayToString(array, ",", null);
	}

	/**
	 * Array to string.
	 *
	 * @param array
	 *            the array
	 * @param valueDelimiter
	 *            the value delimiter
	 * @param valueWrapper
	 *            the value wrapper
	 * @return the string
	 */
	public static String arrayToString(final String[] array, final String valueDelimiter, final String valueWrapper) {
		if (array != null) {
			StringBuffer result = new StringBuffer();

			for (int i = 0; i < array.length; i++) {

				if (valueWrapper != null) {
					result.append(valueWrapper);
				}

				result.append(array[i]);

				if (valueWrapper != null) {
					result.append(valueWrapper);
				}

				if (valueDelimiter != null && i < array.length - 1) {
					result.append(valueDelimiter);
				}

			}

			return result.toString();

		}
		return null;
	}

	/**
	 * Replaces all occurrences of a placeholder {n} in the input string with
	 * the corresponding values provided in the params.
	 *
	 * @param inString
	 *            the in string
	 * @param params
	 *            the params
	 * @return the string
	 */
	public static String formatString(final String inString, final String... params) {

		String result = inString;
		if (inString != null && params != null) {
			for (int i = 0; i < params.length; i++) {
				result = result.replaceAll("\\{" + i + "\\}", params[i]);
			}
		}

		return result;
	}

	/**
	 * Formats number to string with given number of cifers.
	 *
	 * @param number
	 *            the number
	 * @param numberofCifers
	 *            the numberof cifers
	 * @return the string
	 */
	public static String formatNumber(final long number, final int numberofCifers) {
		return String.format("%1$0" + numberofCifers + "d", number);
	}

	/**
	 * Removes all HTML tas from given String.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String removeHtmlTags(final String str) {
		return str.replaceAll("<.*?>", "");
	}

	/**
	 * Shortens the given string to the given maxLeength value (if longer that
	 * that value).
	 *
	 * @param str
	 *            the str
	 * @param maxLength
	 *            the max length of a given string
	 * @return the string
	 */
	public static String limitString(final String str, final int maxLength) {
		if (str != null && str.length() > maxLength) {
			return str.substring(0, maxLength);
		}
		return str;
	}

	/**
	 * Checks if given string either NULL or is empty (i.e. "") or is filled
	 * with whitespaces only (e.g. " ").
	 *
	 * @param string
	 *            the string
	 * @return true, if string is empty (null,"null", "" or " ")
	 */
	public static boolean isEmpty(final String string) {
		if (string == null || string.trim().length() == 0 || string.equalsIgnoreCase("null")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if given string is not NULL and is not empty (including
	 * whitespaces).
	 *
	 * @param string
	 *            the string
	 * @return true, if is non empty string
	 */
	public static boolean isNotEmpty(final String string) {
		return !isEmpty(string);
	}

	/**
	 * Checks if given list is not NULL and is not empty.
	 *
	 * @param list
	 *            the list
	 * @return true, if is non empty string
	 */
	public static boolean isNotEmpty(final List list) {
		return list != null && list.size() > 0 && list.get(0) != null;
	}

	/**
	 * Checks if given array is not NULL and is not empty.
	 *
	 * @param array
	 *            the array
	 * @return true, if is non empty string
	 */
	public static boolean isNotEmpty(final Object[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * Ensure that the fill length of a UTF-8 encoded string is not more that a
	 * given length.
	 *
	 * @param string
	 *            the string
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String ensureUTF8StringLength(final String string, final int length) {
		if (string != null && length > 0) {
			if (utf8length(string) > length) {
				String newString = substring(string, 0, length);
				if (utf8length(newString) > length) {
					return ensureUTF8StringLength(string, length - 1);
				} else {
					return newString;
				}
			}
		}
		return string;
	}

	/**
	 * Gives the length of a given String in UTF-8 character encoding. This
	 * method will return result that is larger than standard String.length()
	 * only if string contains a special characters (comprised of 2 or more
	 * standard characters)
	 *
	 * @param sequence
	 *            the sequence
	 * @return the int
	 */
	public static int utf8length(final CharSequence sequence) {
		int count = 0;
		for (int i = 0, len = sequence.length(); i < len; i++) {
			char ch = sequence.charAt(i);
			if (ch <= 0x7F) {
				count++;
			} else if (ch <= 0x7FF) {
				count += 2;
			} else if (Character.isHighSurrogate(ch)) {
				count += 4;
				++i;
			} else {
				count += 3;
			}
		}
		return count;
	}

	/**
	 * Left string.
	 *
	 * @param string
	 *            the string
	 * @param endToken
	 *            the end token
	 * @return the string
	 */
	public static String leftString(final String string, final String endToken) {
		if (string != null && endToken != null && endToken.length() > 0) {
			int idx = string.indexOf(endToken);
			if (idx > -1) {
				return string.substring(0, idx);
			}
		}
		return string;
	}

	/**
	 * Left string.
	 *
	 * @param string
	 *            the string
	 * @param startToken
	 *            the start token
	 * @return the string
	 */
	public static String rightString(final String string, final String startToken) {
		if (string != null && startToken != null && startToken.length() > 0) {
			int idx = string.lastIndexOf(startToken);
			if (idx > 0) {
				return string.substring(idx);
			}
		}
		return string;
	}

	/**
	 * Parses the int.
	 *
	 * @param value
	 *            the value
	 * @param defaultValue
	 *            the default value
	 * @param ignoredCharacters
	 *            the ignored characters
	 * @return the int
	 */
	public static int parseInt(final String value, final int defaultValue, final String... ignoredCharacters) {
		try {
			String strValue = value;
			if (strValue != null) {
				// If any 'ignored characters' are given then remove them'
				if (ignoredCharacters != null && ignoredCharacters.length > 0) {
					for (String ignoredChar : ignoredCharacters) {
						if (ignoredChar != null && ignoredChar.length() > 0) {
							strValue = strValue.replaceAll(ignoredChar, "");
						}
					}
				}

				// Try to parse it as integer
				return Integer.parseInt(strValue);
			}

		} catch (NumberFormatException e) {
			// TODO:
		}
		return defaultValue;

	}

	/**
	 * Parses the double.
	 *
	 * @param value
	 *            the value
	 * @param defaultValue
	 *            the default value
	 * @param ignoredCharacters
	 *            the ignored characters
	 * @return the double
	 */
	public static double parseDouble(final String value, final int defaultValue, final String... ignoredCharacters) {
		try {
			String strValue = value;
			if (strValue != null) {
				// If any 'ignored characters' are given then remove them'
				if (ignoredCharacters != null && ignoredCharacters.length > 0) {
					for (String ignoredChar : ignoredCharacters) {
						if (ignoredChar != null && ignoredChar.length() > 0) {
							strValue = strValue.replaceAll(ignoredChar, "");
						}
					}
				}

				// Try to parse it as integer
				return Double.parseDouble(strValue);
			}

		} catch (NumberFormatException e) {
			// TODO:
		}
		return defaultValue;
	}

	/**
	 * Extract ending number.
	 *
	 * @param str
	 *            the str
	 * @param defaultValue
	 *            the default value
	 * @return the int
	 */
	public static int extractEndingNumber(final String str, final int defaultValue) {
		if (str != null && str.trim().length() > 0) {
			final Pattern pattern = Pattern.compile("\\d+$");
			Matcher matcher = pattern.matcher(str);

			if (matcher.find()) {
				String group = matcher.group();
				return parseInt(group, defaultValue, null);
			}
		}
		return defaultValue;

	}

	/**
	 * Splits a given string into one or more strings that are up to a given
	 * length.
	 *
	 * @param string
	 *            the string
	 * @param maxLength
	 *            the max length
	 * @return the list
	 */
	public static List<String> splitStringOnLength(final String string, final int maxLength) {
		List<String> result = new ArrayList<String>();
		if (string != null && maxLength > 0) {
			String tmpString = string;
			while (tmpString.length() > 0) {
				if (tmpString.length() < maxLength) {
					result.add(tmpString);
					tmpString = "";
				} else {
					result.add(tmpString.substring(0, maxLength));
					tmpString = tmpString.substring(maxLength, tmpString.length());
				}
			}
		}
		return result;
	}

	/**
	 * Minimum.
	 *
	 * @param values
	 *            the values
	 * @return the int
	 */
	private static int minimum(final int... values) {
		int result = Integer.MAX_VALUE;

		if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; i++) {
				result = Math.min(result, values[i]);
			}
		} else {
			result = 0;
		}
		return result;
	}

	/**
	 * Minimum.
	 *
	 * @param values
	 *            the values
	 * @return the int
	 */
	private static int maximum(final int... values) {
		int result = Integer.MIN_VALUE;

		if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; i++) {
				result = Math.max(result, values[i]);
			}
		} else {
			result = 0;
		}
		return result;
	}

	/**
	 * Computes the Levenshtein distance i.e. the relative difference between
	 * two given strings.
	 *
	 * @param str1
	 *            the str1
	 * @param str2
	 *            the str2
	 * @return the int
	 */
	public static int computeLevenshteinDistance(final CharSequence str1, final CharSequence str2) {
		if (str1 == null || str1.length() == 0 || str2 == null || str2.length() == 0) {
			return -1;
		}
		if (str1.equals(str2)) {
			return 0;
		}

		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++) {
			distance[i][0] = i;
		}
		for (int j = 1; j <= str2.length(); j++) {
			distance[0][j] = j;
		}

		for (int i = 1; i <= str1.length(); i++) {
			for (int j = 1; j <= str2.length(); j++) {
				distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1));
			}
		}

		return distance[str1.length()][str2.length()];
	}

	/**
	 * Checks if two String arrays are the same (i.e. same length and contain
	 * same elements (not necessarily in same order)).
	 *
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return true, if successful
	 */
	public static boolean compareArrays(final String[] source, final String[] target) {
		if (source != null && target != null && source.length == target.length) {

			HashMap<String, String> targetMap = new HashMap<String, String>();

			for (String targetStr : target) {
				targetMap.put(targetStr, targetStr);
			}

			for (String sourceStr : source) {
				if (!targetMap.containsKey(sourceStr)) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns the given 'value' string cut to the 'maxLength' if its original
	 * length was larger.
	 *
	 * @param value
	 *            the value
	 * @param maxLength
	 *            the max length
	 * @return the with max length
	 */
	public static String getWithMaxLength(final String value, final int maxLength) {
		if (value != null && maxLength > 0) {
			return value.length() > maxLength ? value.substring(0, maxLength) : value;
		}
		return value;
	}

	/**
	 * Extracts the string located between 'startMark' and 'endMark' strings in
	 * a given 'source'.
	 * <p>
	 * EXAMPLE: source string = "attr1='value1' attr2='value2' attr3='value3'
	 * attr4='value4' ";. <br>
	 * To find a value of attr2 use this call: <b>extractStringBetween(source,
	 * "attr2='","'");</b>
	 * </p>
	 *
	 * @param source
	 *            the source
	 * @param startMark
	 *            the start mark
	 * @param endMark
	 *            the end mark
	 * @return the string
	 */
	public static String extractStringBetween(final String source, final String startMark, final String endMark) {
		Pattern pattern = Pattern.compile(startMark + "(.*?)" + endMark);
		Matcher matcher = pattern.matcher(source);
		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	/**
	 * Checks if string is blank (i.e. is null or is empty string and is string
	 * with only whitespaces).
	 *
	 * @param str
	 *            the str
	 * @return true, if is blank
	 */
	public static boolean isBlank(final String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * Checks if string is not blank (i.e. is not null and is not empty string
	 * and is not string with only whitespaces).
	 *
	 * @param str
	 *            the str
	 * @return true, if is not blank
	 */
	public static boolean isNotBlank(final String str) {
		return !isBlank(str);
	}

	/**
	 * Clean.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 * @deprecated Method clean is deprecated
	 */

	@Deprecated
	public static String clean(final String str) {
		return str != null ? str.trim() : "";
	}

	/**
	 * Trim.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String trim(final String str) {
		return str != null ? str.trim() : null;
	}

	/**
	 * Trim to null.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String trimToNull(final String str) {
		String ts = trim(str);
		return isEmpty(ts) ? null : ts;
	}

	/**
	 * Trim to empty.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String trimToEmpty(final String str) {
		return str != null ? str.trim() : "";
	}

	/**
	 * Strip.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String strip(final String str) {
		return strip(str, null);
	}

	/**
	 * Strip to null.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String stripToNull(String str) {
		if (str == null) {
			return null;
		} else {
			str = strip(str, null);
			return str.length() != 0 ? str : null;
		}
	}

	/**
	 * Strip to empty.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String stripToEmpty(final String str) {
		return str != null ? strip(str, null) : "";
	}

	/**
	 * Strip.
	 *
	 * @param str
	 *            the str
	 * @param stripChars
	 *            the strip chars
	 * @return the string
	 */
	public static String strip(String str, final String stripChars) {
		if (isEmpty(str)) {
			return str;
		} else {
			str = stripStart(str, stripChars);
			return stripEnd(str, stripChars);
		}
	}

	/**
	 * Strip start.
	 *
	 * @param str
	 *            the str
	 * @param stripChars
	 *            the strip chars
	 * @return the string
	 */
	public static String stripStart(final String str, final String stripChars) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		int start = 0;
		if (stripChars == null) {
			for (; start != strLen && Character.isWhitespace(str.charAt(start)); start++) {
				;
			}
		} else {
			if (stripChars.length() == 0) {
				return str;
			}
			for (; start != strLen && stripChars.indexOf(str.charAt(start)) != -1; start++) {
				;
			}
		}
		return str.substring(start);
	}

	/**
	 * Strip end.
	 *
	 * @param str
	 *            the str
	 * @param stripChars
	 *            the strip chars
	 * @return the string
	 */
	public static String stripEnd(final String str, final String stripChars) {
		int end;
		if (str == null || (end = str.length()) == 0) {
			return str;
		}
		if (stripChars == null) {
			for (; end != 0 && Character.isWhitespace(str.charAt(end - 1)); end--) {
				;
			}
		} else {
			if (stripChars.length() == 0) {
				return str;
			}
			for (; end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1; end--) {
				;
			}
		}
		return str.substring(0, end);
	}

	/**
	 * Strip all.
	 *
	 * @param strs
	 *            the strs
	 * @return the string[]
	 */
	public static String[] stripAll(final String strs[]) {
		return stripAll(strs, null);
	}

	/**
	 * Strip all.
	 *
	 * @param strs
	 *            the strs
	 * @param stripChars
	 *            the strip chars
	 * @return the string[]
	 */
	public static String[] stripAll(final String strs[], final String stripChars) {
		int strsLen;
		if (strs == null || (strsLen = strs.length) == 0) {
			return strs;
		}
		String newArr[] = new String[strsLen];
		for (int i = 0; i < strsLen; i++) {
			newArr[i] = strip(strs[i], stripChars);
		}

		return newArr;
	}

	/**
	 * Equals.
	 *
	 * @param str1
	 *            the str1
	 * @param str2
	 *            the str2
	 * @return true, if successful
	 */
	public static boolean equals(final String str1, final String str2) {
		return str1 != null ? str1.equals(str2) : str2 == null;
	}

	/**
	 * Equals ignore case.
	 *
	 * @param str1
	 *            the str1
	 * @param str2
	 *            the str2
	 * @return true, if successful
	 */
	public static boolean equalsIgnoreCase(final String str1, final String str2) {
		return str1 != null ? str1.equalsIgnoreCase(str2) : str2 == null;
	}

	/**
	 * Index of.
	 *
	 * @param str
	 *            the str
	 * @param searchChar
	 *            the search char
	 * @return the int
	 */
	public static int indexOf(final String str, final char searchChar) {
		if (isEmpty(str)) {
			return -1;
		} else {
			return str.indexOf(searchChar);
		}
	}

	/**
	 * Index of.
	 *
	 * @param str
	 *            the str
	 * @param searchChar
	 *            the search char
	 * @param startPos
	 *            the start pos
	 * @return the int
	 */
	public static int indexOf(final String str, final char searchChar, final int startPos) {
		if (isEmpty(str)) {
			return -1;
		} else {
			return str.indexOf(searchChar, startPos);
		}
	}

	/**
	 * Index of.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @return the int
	 */
	public static int indexOf(final String str, final String searchStr) {
		if (str == null || searchStr == null) {
			return -1;
		} else {
			return str.indexOf(searchStr);
		}
	}

	/**
	 * Ordinal index of.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @param ordinal
	 *            the ordinal
	 * @return the int
	 */
	public static int ordinalIndexOf(final String str, final String searchStr, final int ordinal) {
		return ordinalIndexOf(str, searchStr, ordinal, false);
	}

	/**
	 * Ordinal index of.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @param ordinal
	 *            the ordinal
	 * @param lastIndex
	 *            the last index
	 * @return the int
	 */
	private static int ordinalIndexOf(final String str, final String searchStr, final int ordinal, final boolean lastIndex) {
		if (str == null || searchStr == null || ordinal <= 0) {
			return -1;
		}
		if (searchStr.length() == 0) {
			return lastIndex ? str.length() : 0;
		}
		int found = 0;
		int index = lastIndex ? str.length() : -1;
		do {
			if (lastIndex) {
				index = str.lastIndexOf(searchStr, index - 1);
			} else {
				index = str.indexOf(searchStr, index + 1);
			}
			if (index < 0) {
				return index;
			}
		} while (++found < ordinal);
		return index;
	}

	/**
	 * Index of.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @param startPos
	 *            the start pos
	 * @return the int
	 */
	public static int indexOf(final String str, final String searchStr, final int startPos) {
		if (str == null || searchStr == null) {
			return -1;
		}
		if (searchStr.length() == 0 && startPos >= str.length()) {
			return str.length();
		} else {
			return str.indexOf(searchStr, startPos);
		}
	}

	/**
	 * Index of ignore case.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @return the int
	 */
	public static int indexOfIgnoreCase(final String str, final String searchStr) {
		return indexOfIgnoreCase(str, searchStr, 0);
	}

	/**
	 * Index of ignore case.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @param startPos
	 *            the start pos
	 * @return the int
	 */
	public static int indexOfIgnoreCase(final String str, final String searchStr, int startPos) {
		if (str == null || searchStr == null) {
			return -1;
		}
		if (startPos < 0) {
			startPos = 0;
		}
		int endLimit = str.length() - searchStr.length() + 1;
		if (startPos > endLimit) {
			return -1;
		}
		if (searchStr.length() == 0) {
			return startPos;
		}
		for (int i = startPos; i < endLimit; i++) {
			if (str.regionMatches(true, i, searchStr, 0, searchStr.length())) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Last index of.
	 *
	 * @param str
	 *            the str
	 * @param searchChar
	 *            the search char
	 * @return the int
	 */
	public static int lastIndexOf(final String str, final char searchChar) {
		if (isEmpty(str)) {
			return -1;
		} else {
			return str.lastIndexOf(searchChar);
		}
	}

	/**
	 * Last index of.
	 *
	 * @param str
	 *            the str
	 * @param searchChar
	 *            the search char
	 * @param startPos
	 *            the start pos
	 * @return the int
	 */
	public static int lastIndexOf(final String str, final char searchChar, final int startPos) {
		if (isEmpty(str)) {
			return -1;
		} else {
			return str.lastIndexOf(searchChar, startPos);
		}
	}

	/**
	 * Last index of.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @return the int
	 */
	public static int lastIndexOf(final String str, final String searchStr) {
		if (str == null || searchStr == null) {
			return -1;
		} else {
			return str.lastIndexOf(searchStr);
		}
	}

	/**
	 * Last ordinal index of.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @param ordinal
	 *            the ordinal
	 * @return the int
	 */
	public static int lastOrdinalIndexOf(final String str, final String searchStr, final int ordinal) {
		return ordinalIndexOf(str, searchStr, ordinal, true);
	}

	/**
	 * Last index of.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @param startPos
	 *            the start pos
	 * @return the int
	 */
	public static int lastIndexOf(final String str, final String searchStr, final int startPos) {
		if (str == null || searchStr == null) {
			return -1;
		} else {
			return str.lastIndexOf(searchStr, startPos);
		}
	}

	/**
	 * Last index of ignore case.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @return the int
	 */
	public static int lastIndexOfIgnoreCase(final String str, final String searchStr) {
		if (str == null || searchStr == null) {
			return -1;
		} else {
			return lastIndexOfIgnoreCase(str, searchStr, str.length());
		}
	}

	/**
	 * Last index of ignore case.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @param startPos
	 *            the start pos
	 * @return the int
	 */
	public static int lastIndexOfIgnoreCase(final String str, final String searchStr, int startPos) {
		if (str == null || searchStr == null) {
			return -1;
		}
		if (startPos > str.length() - searchStr.length()) {
			startPos = str.length() - searchStr.length();
		}
		if (startPos < 0) {
			return -1;
		}
		if (searchStr.length() == 0) {
			return startPos;
		}
		for (int i = startPos; i >= 0; i--) {
			if (str.regionMatches(true, i, searchStr, 0, searchStr.length())) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Contains.
	 *
	 * @param str
	 *            the str
	 * @param searchChar
	 *            the search char
	 * @return true, if successful
	 */
	public static boolean contains(final String str, final char searchChar) {
		if (isEmpty(str)) {
			return false;
		} else {
			return str.indexOf(searchChar) >= 0;
		}
	}

	/**
	 * Contains.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @return true, if successful
	 */
	public static boolean contains(final String str, final String searchStr) {
		if (str == null || searchStr == null) {
			return false;
		} else {
			return str.indexOf(searchStr) >= 0;
		}
	}

	/**
	 * Contains ignore case.
	 *
	 * @param str
	 *            the str
	 * @param searchStr
	 *            the search str
	 * @return true, if successful
	 */
	public static boolean containsIgnoreCase(final String str, final String searchStr) {
		if (str == null || searchStr == null) {
			return false;
		}
		int len = searchStr.length();
		int max = str.length() - len;
		for (int i = 0; i <= max; i++) {
			if (str.regionMatches(true, i, searchStr, 0, len)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Index of any.
	 *
	 * @param str
	 *            the str
	 * @param searchChars
	 *            the search chars
	 * @return the int
	 */
	public static int indexOfAny(final String str, final char searchChars[]) {
		if (isEmpty(str) || searchChars == null || searchChars.length == 0) {
			return -1;
		}
		int csLen = str.length();
		int csLast = csLen - 1;
		int searchLen = searchChars.length;
		int searchLast = searchLen - 1;
		for (int i = 0; i < csLen; i++) {
			char ch = str.charAt(i);
			for (int j = 0; j < searchLen; j++) {
				if (searchChars[j] != ch) {
					continue;
				}
				if (i < csLast && j < searchLast && isHighSurrogate(ch)) {
					if (searchChars[j + 1] == str.charAt(i + 1)) {
						return i;
					}
				} else {
					return i;
				}
			}

		}

		return -1;
	}

	/**
	 * Index of any.
	 *
	 * @param str
	 *            the str
	 * @param searchChars
	 *            the search chars
	 * @return the int
	 */
	public static int indexOfAny(final String str, final String searchChars) {
		if (isEmpty(str) || isEmpty(searchChars)) {
			return -1;
		} else {
			return indexOfAny(str, searchChars.toCharArray());
		}
	}

	/**
	 * Contains any.
	 *
	 * @param str
	 *            the str
	 * @param searchChars
	 *            the search chars
	 * @return true, if successful
	 */
	public static boolean containsAny(final String str, final char searchChars[]) {
		if (isEmpty(str) || searchChars == null || searchChars.length == 0) {
			return false;
		}
		int csLength = str.length();
		int searchLength = searchChars.length;
		int csLast = csLength - 1;
		int searchLast = searchLength - 1;
		for (int i = 0; i < csLength; i++) {
			char ch = str.charAt(i);
			for (int j = 0; j < searchLength; j++) {
				if (searchChars[j] != ch) {
					continue;
				}
				if (isHighSurrogate(ch)) {
					if (j == searchLast) {
						return true;
					}
					if (i < csLast && searchChars[j + 1] == str.charAt(i + 1)) {
						return true;
					}
				} else {
					return true;
				}
			}

		}

		return false;
	}

	/**
	 * Contains any.
	 *
	 * @param str
	 *            the str
	 * @param searchChars
	 *            the search chars
	 * @return true, if successful
	 */
	public static boolean containsAny(final String str, final String searchChars) {
		if (searchChars == null) {
			return false;
		} else {
			return containsAny(str, searchChars.toCharArray());
		}
	}

	/**
	 * Index of any but.
	 *
	 * @param str
	 *            the str
	 * @param searchChars
	 *            the search chars
	 * @return the int
	 */
	public static int indexOfAnyBut(final String str, final char searchChars[]) {
		if (isEmpty(str) || searchChars == null || searchChars.length == 0) {
			return -1;
		}
		int csLen = str.length();
		int csLast = csLen - 1;
		int searchLen = searchChars.length;
		int searchLast = searchLen - 1;
		int i = 0;
		label0: do {
			label1: {
				if (i >= csLen) {
					break label0;
				}
				char ch = str.charAt(i);
				for (int j = 0; j < searchLen; j++) {
					if (searchChars[j] == ch && (i >= csLast || j >= searchLast || !isHighSurrogate(ch) || searchChars[j + 1] == str.charAt(i + 1))) {
						break label1;
					}
				}

				return i;
			}
			i++;
		} while (true);
		return -1;
	}

	/**
	 * Index of any but.
	 *
	 * @param str
	 *            the str
	 * @param searchChars
	 *            the search chars
	 * @return the int
	 */
	public static int indexOfAnyBut(final String str, final String searchChars) {
		if (isEmpty(str) || isEmpty(searchChars)) {
			return -1;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			char ch = str.charAt(i);
			boolean chFound = searchChars.indexOf(ch) >= 0;
			if (i + 1 < strLen && isHighSurrogate(ch)) {
				char ch2 = str.charAt(i + 1);
				if (chFound && searchChars.indexOf(ch2) < 0) {
					return i;
				}
				continue;
			}
			if (!chFound) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Contains only.
	 *
	 * @param str
	 *            the str
	 * @param valid
	 *            the valid
	 * @return true, if successful
	 */
	public static boolean containsOnly(final String str, final char valid[]) {
		if (valid == null || str == null) {
			return false;
		}
		if (str.length() == 0) {
			return true;
		}
		if (valid.length == 0) {
			return false;
		} else {
			return indexOfAnyBut(str, valid) == -1;
		}
	}

	/**
	 * Contains only.
	 *
	 * @param str
	 *            the str
	 * @param validChars
	 *            the valid chars
	 * @return true, if successful
	 */
	public static boolean containsOnly(final String str, final String validChars) {
		if (str == null || validChars == null) {
			return false;
		} else {
			return containsOnly(str, validChars.toCharArray());
		}
	}

	/**
	 * Contains none.
	 *
	 * @param str
	 *            the str
	 * @param searchChars
	 *            the search chars
	 * @return true, if successful
	 */
	public static boolean containsNone(final String str, final char searchChars[]) {
		if (str == null || searchChars == null) {
			return true;
		}
		int csLen = str.length();
		int csLast = csLen - 1;
		int searchLen = searchChars.length;
		int searchLast = searchLen - 1;
		for (int i = 0; i < csLen; i++) {
			char ch = str.charAt(i);
			for (int j = 0; j < searchLen; j++) {
				if (searchChars[j] != ch) {
					continue;
				}
				if (isHighSurrogate(ch)) {
					if (j == searchLast) {
						return false;
					}
					if (i < csLast && searchChars[j + 1] == str.charAt(i + 1)) {
						return false;
					}
				} else {
					return false;
				}
			}

		}

		return true;
	}

	/**
	 * Checks if is high surrogate.
	 *
	 * @param ch
	 *            the ch
	 * @return true, if is high surrogate
	 */
	public static boolean isHighSurrogate(final char ch) {
		return '\uD800' <= ch && '\uDBFF' >= ch;
	}

	/**
	 * Contains none.
	 *
	 * @param str
	 *            the str
	 * @param invalidChars
	 *            the invalid chars
	 * @return true, if successful
	 */
	public static boolean containsNone(final String str, final String invalidChars) {
		if (str == null || invalidChars == null) {
			return true;
		} else {
			return containsNone(str, invalidChars.toCharArray());
		}
	}

	/**
	 * Index of any.
	 *
	 * @param str
	 *            the str
	 * @param searchStrs
	 *            the search strs
	 * @return the int
	 */
	public static int indexOfAny(final String str, final String searchStrs[]) {
		if (str == null || searchStrs == null) {
			return -1;
		}
		int sz = searchStrs.length;
		int ret = 2147483647;
		int tmp = 0;
		for (int i = 0; i < sz; i++) {
			String search = searchStrs[i];
			if (search == null) {
				continue;
			}
			tmp = str.indexOf(search);
			if (tmp != -1 && tmp < ret) {
				ret = tmp;
			}
		}

		return ret != 2147483647 ? ret : -1;
	}

	/**
	 * Last index of any.
	 *
	 * @param str
	 *            the str
	 * @param searchStrs
	 *            the search strs
	 * @return the int
	 */
	public static int lastIndexOfAny(final String str, final String searchStrs[]) {
		if (str == null || searchStrs == null) {
			return -1;
		}
		int sz = searchStrs.length;
		int ret = -1;
		int tmp = 0;
		for (int i = 0; i < sz; i++) {
			String search = searchStrs[i];
			if (search == null) {
				continue;
			}
			tmp = str.lastIndexOf(search);
			if (tmp > ret) {
				ret = tmp;
			}
		}

		return ret;
	}

	/**
	 * Substring.
	 *
	 * @param str
	 *            the str
	 * @param start
	 *            the start
	 * @return the string
	 */
	public static String substring(final String str, int start) {
		if (str == null) {
			return null;
		}
		if (start < 0) {
			start = str.length() + start;
		}
		if (start < 0) {
			start = 0;
		}
		if (start > str.length()) {
			return "";
		} else {
			return str.substring(start);
		}
	}

	/**
	 * Substring.
	 *
	 * @param str
	 *            the str
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the string
	 */
	public static String substring(final String str, int start, int end) {
		if (str == null) {
			return null;
		}
		if (end < 0) {
			end = str.length() + end;
		}
		if (start < 0) {
			start = str.length() + start;
		}
		if (end > str.length()) {
			end = str.length();
		}
		if (start > end) {
			return "";
		}
		if (start < 0) {
			start = 0;
		}
		if (end < 0) {
			end = 0;
		}
		return str.substring(start, end);
	}

	/**
	 * Left.
	 *
	 * @param str
	 *            the str
	 * @param len
	 *            the len
	 * @return the string
	 */
	public static String left(final String str, final int len) {
		if (str == null) {
			return null;
		}
		if (len < 0) {
			return "";
		}
		if (str.length() <= len) {
			return str;
		} else {
			return str.substring(0, len);
		}
	}

	/**
	 * Right.
	 *
	 * @param str
	 *            the str
	 * @param len
	 *            the len
	 * @return the string
	 */
	public static String right(final String str, final int len) {
		if (str == null) {
			return null;
		}
		if (len < 0) {
			return "";
		}
		if (str.length() <= len) {
			return str;
		} else {
			return str.substring(str.length() - len);
		}
	}

	/**
	 * Mid.
	 *
	 * @param str
	 *            the str
	 * @param pos
	 *            the pos
	 * @param len
	 *            the len
	 * @return the string
	 */
	public static String mid(final String str, int pos, final int len) {
		if (str == null) {
			return null;
		}
		if (len < 0 || pos > str.length()) {
			return "";
		}
		if (pos < 0) {
			pos = 0;
		}
		if (str.length() <= pos + len) {
			return str.substring(pos);
		} else {
			return str.substring(pos, pos + len);
		}
	}

	/**
	 * Substring before.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	public static String substringBefore(final String str, final String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
		if (separator.length() == 0) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return str;
		} else {
			return str.substring(0, pos);
		}
	}

	/**
	 * Substring after.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	public static String substringAfter(final String str, final String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return "";
		} else {
			return str.substring(pos + separator.length());
		}
	}

	/**
	 * Substring before last.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	public static String substringBeforeLast(final String str, final String separator) {
		if (isEmpty(str) || isEmpty(separator)) {
			return str;
		}
		int pos = str.lastIndexOf(separator);
		if (pos == -1) {
			return str;
		} else {
			return str.substring(0, pos);
		}
	}

	/**
	 * Substring after last.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	public static String substringAfterLast(final String str, final String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (isEmpty(separator)) {
			return "";
		}
		int pos = str.lastIndexOf(separator);
		if (pos == -1 || pos == str.length() - separator.length()) {
			return "";
		} else {
			return str.substring(pos + separator.length());
		}
	}

	/**
	 * Substring between.
	 *
	 * @param str
	 *            the str
	 * @param tag
	 *            the tag
	 * @return the string
	 */
	public static String substringBetween(final String str, final String tag) {
		return substringBetween(str, tag, tag);
	}

	/**
	 * Substring between.
	 *
	 * @param str
	 *            the str
	 * @param open
	 *            the open
	 * @param close
	 *            the close
	 * @return the string
	 */
	public static String substringBetween(final String str, final String open, final String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		int start = str.indexOf(open);
		if (start != -1) {
			int end = str.indexOf(close, start + open.length());
			if (end != -1) {
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

	/**
	 * Substrings between.
	 *
	 * @param str
	 *            the str
	 * @param open
	 *            the open
	 * @param close
	 *            the close
	 * @return the string[]
	 */
	public static String[] substringsBetween(final String str, final String open, final String close) {
		if (str == null || isEmpty(open) || isEmpty(close)) {
			return null;
		}
		int strLen = str.length();
		if (strLen == 0) {
			return EMPTY_STRING_ARRAY;
		}
		int closeLen = close.length();
		int openLen = open.length();
		List list = new ArrayList();
		int pos = 0;
		do {
			if (pos >= strLen - closeLen) {
				break;
			}
			int start = str.indexOf(open, pos);
			if (start < 0) {
				break;
			}
			start += openLen;
			int end = str.indexOf(close, start);
			if (end < 0) {
				break;
			}
			list.add(str.substring(start, end));
			pos = end + closeLen;
		} while (true);
		if (list.isEmpty()) {
			return null;
		} else {
			return (String[]) list.toArray(new String[list.size()]);
		}
	}

	/**
	 * Gets the nested string.
	 *
	 * @param str
	 *            the str
	 * @param tag
	 *            the tag
	 * @return the nested string
	 * @deprecated Method getNestedString is deprecated
	 */

	@Deprecated
	public static String getNestedString(final String str, final String tag) {
		return substringBetween(str, tag, tag);
	}

	/**
	 * Gets the nested string.
	 *
	 * @param str
	 *            the str
	 * @param open
	 *            the open
	 * @param close
	 *            the close
	 * @return the nested string
	 * @deprecated Method getNestedString is deprecated
	 */

	@Deprecated
	public static String getNestedString(final String str, final String open, final String close) {
		return substringBetween(str, open, close);
	}

	/**
	 * Trims the strings in given string array.
	 *
	 * @param array
	 *            the array
	 * @return the string[]
	 */
	public static String[] trimArrayStrings(String[] array) {
		if (array != null && array.length > 0) {
			String[] result = new String[array.length];
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null) {
					result[i] = array[i].trim();
				} else {
					result[i] = null;
				}

			}

			return result;
		}

		return array;
	}

	/**
	 * Split.
	 *
	 * @param str
	 *            the str
	 * @return the string[]
	 */
	public static String[] split(final String str) {
		return split(str, null, -1);
	}

	/**
	 * Split.
	 *
	 * @param str
	 *            the str
	 * @param separatorChar
	 *            the separator char
	 * @return the string[]
	 */
	public static String[] split(final String str, final char separatorChar) {
		return splitWorker(str, separatorChar, false);
	}

	/**
	 * Split.
	 *
	 * @param str
	 *            the str
	 * @param separatorChars
	 *            the separator chars
	 * @return the string[]
	 */
	public static String[] split(final String str, final String separatorChars) {
		return splitWorker(str, separatorChars, -1, false);
	}

	/**
	 * Split.
	 *
	 * @param str
	 *            the str
	 * @param separatorChars
	 *            the separator chars
	 * @param max
	 *            the max
	 * @return the string[]
	 */
	public static String[] split(final String str, final String separatorChars, final int max) {
		return splitWorker(str, separatorChars, max, false);
	}

	/**
	 * Split by whole separator.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @return the string[]
	 */
	public static String[] splitByWholeSeparator(final String str, final String separator) {
		return splitByWholeSeparatorWorker(str, separator, -1, false);
	}

	/**
	 * Split by whole separator.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @param max
	 *            the max
	 * @return the string[]
	 */
	public static String[] splitByWholeSeparator(final String str, final String separator, final int max) {
		return splitByWholeSeparatorWorker(str, separator, max, false);
	}

	/**
	 * Split by whole separator preserve all tokens.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @return the string[]
	 */
	public static String[] splitByWholeSeparatorPreserveAllTokens(final String str, final String separator) {
		return splitByWholeSeparatorWorker(str, separator, -1, true);
	}

	/**
	 * Split by whole separator preserve all tokens.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @param max
	 *            the max
	 * @return the string[]
	 */
	public static String[] splitByWholeSeparatorPreserveAllTokens(final String str, final String separator, final int max) {
		return splitByWholeSeparatorWorker(str, separator, max, true);
	}

	/**
	 * Split by whole separator worker.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @param max
	 *            the max
	 * @param preserveAllTokens
	 *            the preserve all tokens
	 * @return the string[]
	 */
	private static String[] splitByWholeSeparatorWorker(final String str, final String separator, final int max, final boolean preserveAllTokens) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		if (separator == null || "".equals(separator)) {
			return splitWorker(str, null, max, preserveAllTokens);
		}
		int separatorLength = separator.length();
		ArrayList substrings = new ArrayList();
		int numberOfSubstrings = 0;
		int beg = 0;
		for (int end = 0; end < len;) {
			end = str.indexOf(separator, beg);
			if (end > -1) {
				if (end > beg) {
					if (++numberOfSubstrings == max) {
						end = len;
						substrings.add(str.substring(beg));
					} else {
						substrings.add(str.substring(beg, end));
						beg = end + separatorLength;
					}
				} else {
					if (preserveAllTokens) {
						if (++numberOfSubstrings == max) {
							end = len;
							substrings.add(str.substring(beg));
						} else {
							substrings.add("");
						}
					}
					beg = end + separatorLength;
				}
			} else {
				substrings.add(str.substring(beg));
				end = len;
			}
		}

		return (String[]) substrings.toArray(new String[substrings.size()]);
	}

	/**
	 * Split preserve all tokens.
	 *
	 * @param str
	 *            the str
	 * @return the string[]
	 */
	public static String[] splitPreserveAllTokens(final String str) {
		return splitWorker(str, null, -1, true);
	}

	/**
	 * Split preserve all tokens.
	 *
	 * @param str
	 *            the str
	 * @param separatorChar
	 *            the separator char
	 * @return the string[]
	 */
	public static String[] splitPreserveAllTokens(final String str, final char separatorChar) {
		return splitWorker(str, separatorChar, true);
	}

	/**
	 * Split worker.
	 *
	 * @param str
	 *            the str
	 * @param separatorChar
	 *            the separator char
	 * @param preserveAllTokens
	 *            the preserve all tokens
	 * @return the string[]
	 */
	private static String[] splitWorker(final String str, final char separatorChar, final boolean preserveAllTokens) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		List list = new ArrayList();
		int i = 0;
		int start = 0;
		boolean match = false;
		boolean lastMatch = false;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if (match || preserveAllTokens) {
					list.add(str.substring(start, i));
					match = false;
					lastMatch = true;
				}
				start = ++i;
			} else {
				lastMatch = false;
				match = true;
				i++;
			}
		}
		if (match || preserveAllTokens && lastMatch) {
			list.add(str.substring(start, i));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Split preserve all tokens.
	 *
	 * @param str
	 *            the str
	 * @param separatorChars
	 *            the separator chars
	 * @return the string[]
	 */
	public static String[] splitPreserveAllTokens(final String str, final String separatorChars) {
		return splitWorker(str, separatorChars, -1, true);
	}

	/**
	 * Split preserve all tokens.
	 *
	 * @param str
	 *            the str
	 * @param separatorChars
	 *            the separator chars
	 * @param max
	 *            the max
	 * @return the string[]
	 */
	public static String[] splitPreserveAllTokens(final String str, final String separatorChars, final int max) {
		return splitWorker(str, separatorChars, max, true);
	}

	/**
	 * Split worker.
	 *
	 * @param str
	 *            the str
	 * @param separatorChars
	 *            the separator chars
	 * @param max
	 *            the max
	 * @param preserveAllTokens
	 *            the preserve all tokens
	 * @return the string[]
	 */
	private static String[] splitWorker(final String str, final String separatorChars, final int max, final boolean preserveAllTokens) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		List list = new ArrayList();
		int sizePlus1 = 1;
		int i = 0;
		int start = 0;
		boolean match = false;
		boolean lastMatch = false;
		if (separatorChars == null) {
			while (i < len) {
				if (Character.isWhitespace(str.charAt(i))) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
				} else {
					lastMatch = false;
					match = true;
					i++;
				}
			}
		} else if (separatorChars.length() == 1) {
			char sep = separatorChars.charAt(0);
			while (i < len) {
				if (str.charAt(i) == sep) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
				} else {
					lastMatch = false;
					match = true;
					i++;
				}
			}
		} else {
			while (i < len) {
				if (separatorChars.indexOf(str.charAt(i)) >= 0) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
				} else {
					lastMatch = false;
					match = true;
					i++;
				}
			}
		}
		if (match || preserveAllTokens && lastMatch) {
			list.add(str.substring(start, i));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Split by character type.
	 *
	 * @param str
	 *            the str
	 * @return the string[]
	 */
	public static String[] splitByCharacterType(final String str) {
		return splitByCharacterType(str, false);
	}

	/**
	 * Split by character type camel case.
	 *
	 * @param str
	 *            the str
	 * @return the string[]
	 */
	public static String[] splitByCharacterTypeCamelCase(final String str) {
		return splitByCharacterType(str, true);
	}

	/**
	 * Split by character type.
	 *
	 * @param str
	 *            the str
	 * @param camelCase
	 *            the camel case
	 * @return the string[]
	 */
	private static String[] splitByCharacterType(final String str, final boolean camelCase) {
		if (str == null) {
			return null;
		}
		if (str.length() == 0) {
			return EMPTY_STRING_ARRAY;
		}
		char c[] = str.toCharArray();
		List list = new ArrayList();
		int tokenStart = 0;
		int currentType = Character.getType(c[tokenStart]);
		for (int pos = tokenStart + 1; pos < c.length; pos++) {
			int type = Character.getType(c[pos]);
			if (type == currentType) {
				continue;
			}
			if (camelCase && type == 2 && currentType == 1) {
				int newTokenStart = pos - 1;
				if (newTokenStart != tokenStart) {
					list.add(new String(c, tokenStart, newTokenStart - tokenStart));
					tokenStart = newTokenStart;
				}
			} else {
				list.add(new String(c, tokenStart, pos - tokenStart));
				tokenStart = pos;
			}
			currentType = type;
		}

		list.add(new String(c, tokenStart, c.length - tokenStart));
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Concatenate.
	 *
	 * @param array
	 *            the array
	 * @return the string
	 * @deprecated Method concatenate is deprecated
	 */

	@Deprecated
	public static String concatenate(final Object array[]) {
		return join(array, ((String) null));
	}

	/**
	 * Join.
	 *
	 * @param array
	 *            the array
	 * @return the string
	 */
	public static String join(final Object array[]) {
		return join(array, ((String) null));
	}

	/**
	 * Join.
	 *
	 * @param array
	 *            the array
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	public static String join(final Object array[], final char separator) {
		if (array == null) {
			return null;
		} else {
			return join(array, separator, 0, array.length);
		}
	}

	/**
	 * Join.
	 *
	 * @param array
	 *            the array
	 * @param separator
	 *            the separator
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the string
	 */
	public static String join(final Object array[], final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		int bufSize = endIndex - startIndex;
		if (bufSize <= 0) {
			return "";
		}
		bufSize *= (array[startIndex] != null ? array[startIndex].toString().length() : 16) + 1;
		StringBuilder buf = new StringBuilder(bufSize);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}

		return buf.toString();
	}

	/**
	 * Join.
	 *
	 * @param array
	 *            the array
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	public static String join(final Object array[], final String separator) {
		if (array == null) {
			return null;
		} else {
			return join(array, separator, 0, array.length);
		}
	}

	/**
	 * Join.
	 *
	 * @param array
	 *            the array
	 * @param separator
	 *            the separator
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the string
	 */
	public static String join(final Object array[], String separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = "";
		}
		int bufSize = endIndex - startIndex;
		if (bufSize <= 0) {
			return "";
		}
		bufSize *= (array[startIndex] != null ? array[startIndex].toString().length() : 16) + separator.length();
		StringBuilder buf = new StringBuilder(bufSize);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}

		return buf.toString();
	}

	/**
	 * Delete whitespace.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String deleteWhitespace(final String str) {
		if (isEmpty(str)) {
			return str;
		}
		int sz = str.length();
		char chs[] = new char[sz];
		int count = 0;
		for (int i = 0; i < sz; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				chs[count++] = str.charAt(i);
			}
		}

		if (count == sz) {
			return str;
		} else {
			return new String(chs, 0, count);
		}
	}

	/**
	 * Removes the start.
	 *
	 * @param str
	 *            the str
	 * @param remove
	 *            the remove
	 * @return the string
	 */
	public static String removeStart(final String str, final String remove) {
		if (isEmpty(str) || isEmpty(remove)) {
			return str;
		}
		if (str.startsWith(remove)) {
			return str.substring(remove.length());
		} else {
			return str;
		}
	}

	/**
	 * Removes the start ignore case.
	 *
	 * @param str
	 *            the str
	 * @param remove
	 *            the remove
	 * @return the string
	 */
	public static String removeStartIgnoreCase(final String str, final String remove) {
		if (isEmpty(str) || isEmpty(remove)) {
			return str;
		}
		if (startsWithIgnoreCase(str, remove)) {
			return str.substring(remove.length());
		} else {
			return str;
		}
	}

	/**
	 * Removes the end.
	 *
	 * @param str
	 *            the str
	 * @param remove
	 *            the remove
	 * @return the string
	 */
	public static String removeEnd(final String str, final String remove) {
		if (isEmpty(str) || isEmpty(remove)) {
			return str;
		}
		if (str.endsWith(remove)) {
			return str.substring(0, str.length() - remove.length());
		} else {
			return str;
		}
	}

	/**
	 * Removes the end ignore case.
	 *
	 * @param str
	 *            the str
	 * @param remove
	 *            the remove
	 * @return the string
	 */
	public static String removeEndIgnoreCase(final String str, final String remove) {
		if (isEmpty(str) || isEmpty(remove)) {
			return str;
		}
		if (endsWithIgnoreCase(str, remove)) {
			return str.substring(0, str.length() - remove.length());
		} else {
			return str;
		}
	}

	/**
	 * Removes the.
	 *
	 * @param str
	 *            the str
	 * @param remove
	 *            the remove
	 * @return the string
	 */
	public static String remove(final String str, final String remove) {
		if (isEmpty(str) || isEmpty(remove)) {
			return str;
		} else {
			return replace(str, remove, "", -1);
		}
	}

	/**
	 * Removes the.
	 *
	 * @param str
	 *            the str
	 * @param remove
	 *            the remove
	 * @return the string
	 */
	public static String remove(final String str, final char remove) {
		if (isEmpty(str) || str.indexOf(remove) == -1) {
			return str;
		}
		char chars[] = str.toCharArray();
		int pos = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != remove) {
				chars[pos++] = chars[i];
			}
		}

		return new String(chars, 0, pos);
	}

	/**
	 * Replace once.
	 *
	 * @param text
	 *            the text
	 * @param searchString
	 *            the search string
	 * @param replacement
	 *            the replacement
	 * @return the string
	 */
	public static String replaceOnce(final String text, final String searchString, final String replacement) {
		return replace(text, searchString, replacement, 1);
	}

	/**
	 * Replace.
	 *
	 * @param text
	 *            the text
	 * @param searchString
	 *            the search string
	 * @param replacement
	 *            the replacement
	 * @return the string
	 */
	public static String replace(final String text, final String searchString, final String replacement) {
		return replace(text, searchString, replacement, -1);
	}

	/**
	 * Replace.
	 *
	 * @param text
	 *            the text
	 * @param searchString
	 *            the search string
	 * @param replacement
	 *            the replacement
	 * @param max
	 *            the max
	 * @return the string
	 */
	public static String replace(final String text, final String searchString, final String replacement, int max) {
		if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
			return text;
		}
		int start = 0;
		int end = text.indexOf(searchString, start);
		if (end == -1) {
			return text;
		}
		int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = increase >= 0 ? increase : 0;
		increase *= max >= 0 ? max <= 64 ? max : 64 : 16;
		StringBuilder buf = new StringBuilder(text.length() + increase);
		do {
			if (end == -1) {
				break;
			}
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = text.indexOf(searchString, start);
		} while (true);
		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * Replace each.
	 *
	 * @param text
	 *            the text
	 * @param searchList
	 *            the search list
	 * @param replacementList
	 *            the replacement list
	 * @return the string
	 */
	public static String replaceEach(final String text, final String searchList[], final String replacementList[]) {
		return replaceEach(text, searchList, replacementList, false, 0);
	}

	/**
	 * Replace each repeatedly.
	 *
	 * @param text
	 *            the text
	 * @param searchList
	 *            the search list
	 * @param replacementList
	 *            the replacement list
	 * @return the string
	 */
	public static String replaceEachRepeatedly(final String text, final String searchList[], final String replacementList[]) {
		int timeToLive = searchList != null ? searchList.length : 0;
		return replaceEach(text, searchList, replacementList, true, timeToLive);
	}

	/**
	 * Replace each.
	 *
	 * @param text
	 *            the text
	 * @param searchList
	 *            the search list
	 * @param replacementList
	 *            the replacement list
	 * @param repeat
	 *            the repeat
	 * @param timeToLive
	 *            the time to live
	 * @return the string
	 */
	private static String replaceEach(final String text, final String searchList[], final String replacementList[], final boolean repeat, final int timeToLive) {
		if (text == null || text.length() == 0 || searchList == null || searchList.length == 0 || replacementList == null || replacementList.length == 0) {
			return text;
		}
		if (timeToLive < 0) {
			throw new IllegalStateException("TimeToLive of " + timeToLive + " is less than 0: " + text);
		}
		int searchLength = searchList.length;
		int replacementLength = replacementList.length;
		if (searchLength != replacementLength) {
			throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
		}
		boolean noMoreMatchesForReplIndex[] = new boolean[searchLength];
		int textIndex = -1;
		int replaceIndex = -1;
		int tempIndex = -1;
		for (int i = 0; i < searchLength; i++) {
			if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].length() == 0 || replacementList[i] == null) {
				continue;
			}
			tempIndex = text.indexOf(searchList[i]);
			if (tempIndex == -1) {
				noMoreMatchesForReplIndex[i] = true;
				continue;
			}
			if (textIndex == -1 || tempIndex < textIndex) {
				textIndex = tempIndex;
				replaceIndex = i;
			}
		}

		if (textIndex == -1) {
			return text;
		}
		int start = 0;
		int increase = 0;
		for (int i = 0; i < searchList.length; i++) {
			if (searchList[i] == null || replacementList[i] == null) {
				continue;
			}
			int greater = replacementList[i].length() - searchList[i].length();
			if (greater > 0) {
				increase += 3 * greater;
			}
		}

		increase = Math.min(increase, text.length() / 5);
		StringBuilder buf = new StringBuilder(text.length() + increase);
		while (textIndex != -1) {
			int i;
			for (i = start; i < textIndex; i++) {
				buf.append(text.charAt(i));
			}

			buf.append(replacementList[replaceIndex]);
			start = textIndex + searchList[replaceIndex].length();
			textIndex = -1;
			replaceIndex = -1;
			tempIndex = -1;
			i = 0;
			while (i < searchLength) {
				if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && searchList[i].length() != 0 && replacementList[i] != null) {
					tempIndex = text.indexOf(searchList[i], start);
					if (tempIndex == -1) {
						noMoreMatchesForReplIndex[i] = true;
					} else if (textIndex == -1 || tempIndex < textIndex) {
						textIndex = tempIndex;
						replaceIndex = i;
					}
				}
				i++;
			}
		}
		int textLength = text.length();
		for (int i = start; i < textLength; i++) {
			buf.append(text.charAt(i));
		}

		String result = buf.toString();
		if (!repeat) {
			return result;
		} else {
			return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
		}
	}

	/**
	 * Replace chars.
	 *
	 * @param str
	 *            the str
	 * @param searchChar
	 *            the search char
	 * @param replaceChar
	 *            the replace char
	 * @return the string
	 */
	public static String replaceChars(final String str, final char searchChar, final char replaceChar) {
		if (str == null) {
			return null;
		} else {
			return str.replace(searchChar, replaceChar);
		}
	}

	/**
	 * Replace chars.
	 *
	 * @param str
	 *            the str
	 * @param searchChars
	 *            the search chars
	 * @param replaceChars
	 *            the replace chars
	 * @return the string
	 */
	public static String replaceChars(final String str, final String searchChars, String replaceChars) {
		if (isEmpty(str) || isEmpty(searchChars)) {
			return str;
		}
		if (replaceChars == null) {
			replaceChars = "";
		}
		boolean modified = false;
		int replaceCharsLength = replaceChars.length();
		int strLength = str.length();
		StringBuilder buf = new StringBuilder(strLength);
		for (int i = 0; i < strLength; i++) {
			char ch = str.charAt(i);
			int index = searchChars.indexOf(ch);
			if (index >= 0) {
				modified = true;
				if (index < replaceCharsLength) {
					buf.append(replaceChars.charAt(index));
				}
			} else {
				buf.append(ch);
			}
		}

		if (modified) {
			return buf.toString();
		} else {
			return str;
		}
	}

	/**
	 * Overlay string.
	 *
	 * @param text
	 *            the text
	 * @param overlay
	 *            the overlay
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the string
	 * @deprecated Method overlayString is deprecated
	 */

	@Deprecated
	public static String overlayString(final String text, final String overlay, final int start, final int end) {
		return new StringBuilder(start + overlay.length() + text.length() - end + 1).append(text.substring(0, start)).append(overlay).append(text.substring(end)).toString();
	}

	/**
	 * Overlay.
	 *
	 * @param str
	 *            the str
	 * @param overlay
	 *            the overlay
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the string
	 */
	public static String overlay(final String str, String overlay, int start, int end) {
		if (str == null) {
			return null;
		}
		if (overlay == null) {
			overlay = "";
		}
		int len = str.length();
		if (start < 0) {
			start = 0;
		}
		if (start > len) {
			start = len;
		}
		if (end < 0) {
			end = 0;
		}
		if (end > len) {
			end = len;
		}
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
		}
		return new StringBuilder(len + start - end + overlay.length() + 1).append(str.substring(0, start)).append(overlay).append(str.substring(end)).toString();
	}

	/**
	 * Chomp.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String chomp(final String str) {
		if (isEmpty(str)) {
			return str;
		}
		if (str.length() == 1) {
			char ch = str.charAt(0);
			if (ch == '\r' || ch == '\n') {
				return "";
			} else {
				return str;
			}
		}
		int lastIdx = str.length() - 1;
		char last = str.charAt(lastIdx);
		if (last == '\n') {
			if (str.charAt(lastIdx - 1) == '\r') {
				lastIdx--;
			}
		} else if (last != '\r') {
			lastIdx++;
		}
		return str.substring(0, lastIdx);
	}

	/**
	 * Chomp.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	public static String chomp(final String str, final String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
		if (str.endsWith(separator)) {
			return str.substring(0, str.length() - separator.length());
		} else {
			return str;
		}
	}

	/**
	 * Chomp last.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 * @deprecated Method chompLast is deprecated
	 */

	@Deprecated
	public static String chompLast(final String str) {
		return chompLast(str, "\n");
	}

	/**
	 * Chomp last.
	 *
	 * @param str
	 *            the str
	 * @param sep
	 *            the sep
	 * @return the string
	 * @deprecated Method chompLast is deprecated
	 */

	@Deprecated
	public static String chompLast(final String str, final String sep) {
		if (str.length() == 0) {
			return str;
		}
		String sub = str.substring(str.length() - sep.length());
		if (sep.equals(sub)) {
			return str.substring(0, str.length() - sep.length());
		} else {
			return str;
		}
	}

	/**
	 * Gets the chomp.
	 *
	 * @param str
	 *            the str
	 * @param sep
	 *            the sep
	 * @return the chomp
	 * @deprecated Method getChomp is deprecated
	 */

	@Deprecated
	public static String getChomp(final String str, final String sep) {
		int idx = str.lastIndexOf(sep);
		if (idx == str.length() - sep.length()) {
			return sep;
		}
		if (idx != -1) {
			return str.substring(idx);
		} else {
			return "";
		}
	}

	/**
	 * Prechomp.
	 *
	 * @param str
	 *            the str
	 * @param sep
	 *            the sep
	 * @return the string
	 * @deprecated Method prechomp is deprecated
	 */

	@Deprecated
	public static String prechomp(final String str, final String sep) {
		int idx = str.indexOf(sep);
		if (idx == -1) {
			return str;
		} else {
			return str.substring(idx + sep.length());
		}
	}

	/**
	 * Gets the prechomp.
	 *
	 * @param str
	 *            the str
	 * @param sep
	 *            the sep
	 * @return the prechomp
	 * @deprecated Method getPrechomp is deprecated
	 */

	@Deprecated
	public static String getPrechomp(final String str, final String sep) {
		int idx = str.indexOf(sep);
		if (idx == -1) {
			return "";
		} else {
			return str.substring(0, idx + sep.length());
		}
	}

	/**
	 * Chop.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String chop(final String str) {
		if (str == null) {
			return null;
		}
		int strLen = str.length();
		if (strLen < 2) {
			return "";
		}
		int lastIdx = strLen - 1;
		String ret = str.substring(0, lastIdx);
		char last = str.charAt(lastIdx);
		if (last == '\n' && ret.charAt(lastIdx - 1) == '\r') {
			return ret.substring(0, lastIdx - 1);
		} else {
			return ret;
		}
	}

	/**
	 * Chop newline.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 * @deprecated Method chopNewline is deprecated
	 */

	@Deprecated
	public static String chopNewline(final String str) {
		int lastIdx = str.length() - 1;
		if (lastIdx <= 0) {
			return "";
		}
		char last = str.charAt(lastIdx);
		if (last == '\n') {
			if (str.charAt(lastIdx - 1) == '\r') {
				lastIdx--;
			}
		} else {
			lastIdx++;
		}
		return str.substring(0, lastIdx);
	}

	/**
	 * Repeat.
	 *
	 * @param str
	 *            the str
	 * @param repeat
	 *            the repeat
	 * @return the string
	 */
	public static String repeat(final String str, final int repeat) {
		if (str == null) {
			return null;
		}
		if (repeat <= 0) {
			return "";
		}
		int inputLength = str.length();
		if (repeat == 1 || inputLength == 0) {
			return str;
		}
		if (inputLength == 1 && repeat <= 8192) {
			return padding(repeat, str.charAt(0));
		}
		int outputLength = inputLength * repeat;
		switch (inputLength) {
		case 1: // '\001'
			char ch = str.charAt(0);
			char output1[] = new char[outputLength];
			for (int i = repeat - 1; i >= 0; i--) {
				output1[i] = ch;
			}

			return new String(output1);

		case 2: // '\002'
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char output2[] = new char[outputLength];
			for (int i = repeat * 2 - 2; i >= 0; i--) {
				output2[i] = ch0;
				output2[i + 1] = ch1;
				i--;
			}

			return new String(output2);
		}
		StringBuilder buf = new StringBuilder(outputLength);
		for (int i = 0; i < repeat; i++) {
			buf.append(str);
		}

		return buf.toString();
	}

	/**
	 * Repeat.
	 *
	 * @param str
	 *            the str
	 * @param separator
	 *            the separator
	 * @param repeat
	 *            the repeat
	 * @return the string
	 */
	public static String repeat(final String str, final String separator, final int repeat) {
		if (str == null || separator == null) {
			return repeat(str, repeat);
		} else {
			String result = repeat(str + separator, repeat);
			return removeEnd(result, separator);
		}
	}

	/**
	 * Padding.
	 *
	 * @param repeat
	 *            the repeat
	 * @param padChar
	 *            the pad char
	 * @return the string
	 * @throws IndexOutOfBoundsException
	 *             the index out of bounds exception
	 */
	private static String padding(final int repeat, final char padChar) throws IndexOutOfBoundsException {
		if (repeat < 0) {
			throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
		}
		char buf[] = new char[repeat];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = padChar;
		}

		return new String(buf);
	}

	/**
	 * Right pad.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @return the string
	 */
	public static String rightPad(final String str, final int size) {
		return rightPad(str, size, ' ');
	}

	/**
	 * Right pad.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @param padChar
	 *            the pad char
	 * @return the string
	 */
	public static String rightPad(final String str, final int size, final char padChar) {
		if (str == null) {
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0) {
			return str;
		}
		if (pads > 8192) {
			return rightPad(str, size, String.valueOf(padChar));
		} else {
			return str.concat(padding(pads, padChar));
		}
	}

	/**
	 * Right pad.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @param padStr
	 *            the pad str
	 * @return the string
	 */
	public static String rightPad(final String str, final int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (isEmpty(padStr)) {
			padStr = " ";
		}
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str;
		}
		if (padLen == 1 && pads <= 8192) {
			return rightPad(str, size, padStr.charAt(0));
		}
		if (pads == padLen) {
			return str.concat(padStr);
		}
		if (pads < padLen) {
			return str.concat(padStr.substring(0, pads));
		}
		char padding[] = new char[pads];
		char padChars[] = padStr.toCharArray();
		for (int i = 0; i < pads; i++) {
			padding[i] = padChars[i % padLen];
		}

		return str.concat(new String(padding));
	}

	/**
	 * Left pad.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @return the string
	 */
	public static String leftPad(final String str, final int size) {
		return leftPad(str, size, ' ');
	}

	/**
	 * Left pad.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @param padChar
	 *            the pad char
	 * @return the string
	 */
	public static String leftPad(final String str, final int size, final char padChar) {
		if (str == null) {
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0) {
			return str;
		}
		if (pads > 8192) {
			return leftPad(str, size, String.valueOf(padChar));
		} else {
			return padding(pads, padChar).concat(str);
		}
	}

	/**
	 * Left pad.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @param padStr
	 *            the pad str
	 * @return the string
	 */
	public static String leftPad(final String str, final int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (isEmpty(padStr)) {
			padStr = " ";
		}
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str;
		}
		if (padLen == 1 && pads <= 8192) {
			return leftPad(str, size, padStr.charAt(0));
		}
		if (pads == padLen) {
			return padStr.concat(str);
		}
		if (pads < padLen) {
			return padStr.substring(0, pads).concat(str);
		}
		char padding[] = new char[pads];
		char padChars[] = padStr.toCharArray();
		for (int i = 0; i < pads; i++) {
			padding[i] = padChars[i % padLen];
		}

		return new String(padding).concat(str);
	}

	/**
	 * Length.
	 *
	 * @param str
	 *            the str
	 * @return the int
	 */
	public static int length(final String str) {
		return str != null ? str.length() : 0;
	}

	/**
	 * Center.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @return the string
	 */
	public static String center(final String str, final int size) {
		return center(str, size, ' ');
	}

	/**
	 * Center.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @param padChar
	 *            the pad char
	 * @return the string
	 */
	public static String center(String str, final int size, final char padChar) {
		if (str == null || size <= 0) {
			return str;
		}
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str;
		} else {
			str = leftPad(str, strLen + pads / 2, padChar);
			str = rightPad(str, size, padChar);
			return str;
		}
	}

	/**
	 * Center.
	 *
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @param padStr
	 *            the pad str
	 * @return the string
	 */
	public static String center(String str, final int size, String padStr) {
		if (str == null || size <= 0) {
			return str;
		}
		if (isEmpty(padStr)) {
			padStr = " ";
		}
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str;
		} else {
			str = leftPad(str, strLen + pads / 2, padStr);
			str = rightPad(str, size, padStr);
			return str;
		}
	}

	/**
	 * Upper case.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String upperCase(final String str) {
		if (str == null) {
			return null;
		} else {
			return str.toUpperCase();
		}
	}

	/**
	 * Upper case.
	 *
	 * @param str
	 *            the str
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	public static String upperCase(final String str, final Locale locale) {
		if (str == null) {
			return null;
		} else {
			return str.toUpperCase(locale);
		}
	}

	/**
	 * Lower case.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String lowerCase(final String str) {
		if (str == null) {
			return null;
		} else {
			return str.toLowerCase();
		}
	}

	/**
	 * Lower case.
	 *
	 * @param str
	 *            the str
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	public static String lowerCase(final String str, final Locale locale) {
		if (str == null) {
			return null;
		} else {
			return str.toLowerCase(locale);
		}
	}

	/**
	 * Capitalize.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String capitalize(final String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		} else {
			return new StringBuilder(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
		}
	}

	/**
	 * Capitalise.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 * @deprecated Method capitalise is deprecated
	 */

	@Deprecated
	public static String capitalise(final String str) {
		return capitalize(str);
	}

	/**
	 * Uncapitalize.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String uncapitalize(final String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		} else {
			return new StringBuilder(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
		}
	}

	/**
	 * Uncapitalise.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 * @deprecated Method uncapitalise is deprecated
	 */

	@Deprecated
	public static String uncapitalise(final String str) {
		return uncapitalize(str);
	}

	/**
	 * Swap case.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String swapCase(final String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		StringBuilder buffer = new StringBuilder(strLen);
		char ch = '\0';
		for (int i = 0; i < strLen; i++) {
			ch = str.charAt(i);
			if (Character.isUpperCase(ch)) {
				ch = Character.toLowerCase(ch);
			} else if (Character.isTitleCase(ch)) {
				ch = Character.toLowerCase(ch);
			} else if (Character.isLowerCase(ch)) {
				ch = Character.toUpperCase(ch);
			}
			buffer.append(ch);
		}

		return buffer.toString();
	}

	/**
	 * Capitalise all words.
	 *
	 * @param str
	 *            the str
	 * @param sub
	 *            the sub
	 * @return the string
	 * @deprecated Method capitaliseAllWords is deprecated
	 */

	/**
	 * Count matches.
	 *
	 * @param str
	 *            the str
	 * @param sub
	 *            the sub
	 * @return the int
	 */
	public static int countMatches(final String str, final String sub) {
		if (isEmpty(str) || isEmpty(sub)) {
			return 0;
		}
		int count = 0;
		for (int idx = 0; (idx = str.indexOf(sub, idx)) != -1; idx += sub.length()) {
			count++;
		}

		return count;
	}

	/**
	 * Checks if is alpha.
	 *
	 * @param str
	 *            the str
	 * @return true, if is alpha
	 */
	public static boolean isAlpha(final String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isLetter(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is alpha space.
	 *
	 * @param str
	 *            the str
	 * @return true, if is alpha space
	 */
	public static boolean isAlphaSpace(final String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ') {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is alphanumeric.
	 *
	 * @param str
	 *            the str
	 * @return true, if is alphanumeric
	 */
	public static boolean isAlphanumeric(final String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isLetterOrDigit(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is alphanumeric space.
	 *
	 * @param str
	 *            the str
	 * @return true, if is alphanumeric space
	 */
	public static boolean isAlphanumericSpace(final String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isLetterOrDigit(str.charAt(i)) && str.charAt(i) != ' ') {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is numeric.
	 *
	 * @param str
	 *            the str
	 * @return true, if is numeric
	 */
	public static boolean isNumeric(final String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is numeric space.
	 *
	 * @param str
	 *            the str
	 * @return true, if is numeric space
	 */
	public static boolean isNumericSpace(final String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != ' ') {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is whitespace.
	 *
	 * @param str
	 *            the str
	 * @return true, if is whitespace
	 */
	public static boolean isWhitespace(final String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is all lower case.
	 *
	 * @param str
	 *            the str
	 * @return true, if is all lower case
	 */
	public static boolean isAllLowerCase(final String str) {
		if (str == null || isEmpty(str)) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isLowerCase(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is all upper case.
	 *
	 * @param str
	 *            the str
	 * @return true, if is all upper case
	 */
	public static boolean isAllUpperCase(final String str) {
		if (str == null || isEmpty(str)) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isUpperCase(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Default string.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String defaultString(final String str) {
		return str != null ? str : "";
	}

	/**
	 * Default string.
	 *
	 * @param str
	 *            the str
	 * @param defaultStr
	 *            the default str
	 * @return the string
	 */
	public static String defaultString(final String str, final String defaultStr) {
		return str != null ? str : defaultStr;
	}

	/**
	 * Default if blank.
	 *
	 * @param str
	 *            the str
	 * @param defaultStr
	 *            the default str
	 * @return the string
	 */
	public static String defaultIfBlank(final String str, final String defaultStr) {
		return isBlank(str) ? defaultStr : str;
	}

	/**
	 * Default if empty.
	 *
	 * @param str
	 *            the str
	 * @param defaultStr
	 *            the default str
	 * @return the string
	 */
	public static String defaultIfEmpty(final String str, final String defaultStr) {
		return isEmpty(str) ? defaultStr : str;
	}

	/**
	 * Reverse.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String reverse(final String str) {
		if (str == null) {
			return null;
		} else {
			return new StringBuilder(str).reverse().toString();
		}
	}

	/**
	 * Abbreviate.
	 *
	 * @param str
	 *            the str
	 * @param maxWidth
	 *            the max width
	 * @return the string
	 */
	public static String abbreviate(final String str, final int maxWidth) {
		return abbreviate(str, 0, maxWidth);
	}

	/**
	 * Abbreviate.
	 *
	 * @param str
	 *            the str
	 * @param offset
	 *            the offset
	 * @param maxWidth
	 *            the max width
	 * @return the string
	 */
	public static String abbreviate(final String str, int offset, final int maxWidth) {
		if (str == null) {
			return null;
		}
		if (maxWidth < 4) {
			throw new IllegalArgumentException("Minimum abbreviation width is 4");
		}
		if (str.length() <= maxWidth) {
			return str;
		}
		if (offset > str.length()) {
			offset = str.length();
		}
		if (str.length() - offset < maxWidth - 3) {
			offset = str.length() - (maxWidth - 3);
		}
		if (offset <= 4) {
			return str.substring(0, maxWidth - 3) + "...";
		}
		if (maxWidth < 7) {
			throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
		}
		if (offset + maxWidth - 3 < str.length()) {
			return "..." + abbreviate(str.substring(offset), maxWidth - 3);
		} else {
			return "..." + str.substring(str.length() - (maxWidth - 3));
		}
	}

	/**
	 * Abbreviate middle.
	 *
	 * @param str
	 *            the str
	 * @param middle
	 *            the middle
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String abbreviateMiddle(final String str, final String middle, final int length) {
		if (isEmpty(str) || isEmpty(middle)) {
			return str;
		}
		if (length >= str.length() || length < middle.length() + 2) {
			return str;
		} else {
			int targetSting = length - middle.length();
			int startOffset = targetSting / 2 + targetSting % 2;
			int endOffset = str.length() - targetSting / 2;
			StringBuilder builder = new StringBuilder(length);
			builder.append(str.substring(0, startOffset));
			builder.append(middle);
			builder.append(str.substring(endOffset));
			return builder.toString();
		}
	}

	/**
	 * Difference.
	 *
	 * @param str1
	 *            the str1
	 * @param str2
	 *            the str2
	 * @return the string
	 */
	public static String difference(final String str1, final String str2) {
		if (str1 == null) {
			return str2;
		}
		if (str2 == null) {
			return str1;
		}
		int at = indexOfDifference(str1, str2);
		if (at == -1) {
			return "";
		} else {
			return str2.substring(at);
		}
	}

	/**
	 * Index of difference.
	 *
	 * @param str1
	 *            the str1
	 * @param str2
	 *            the str2
	 * @return the int
	 */
	public static int indexOfDifference(final String str1, final String str2) {
		if (str1 == str2) {
			return -1;
		}
		if (str1 == null || str2 == null) {
			return 0;
		}
		int i;
		for (i = 0; i < str1.length() && i < str2.length() && str1.charAt(i) == str2.charAt(i); i++) {
			;
		}
		if (i < str2.length() || i < str1.length()) {
			return i;
		} else {
			return -1;
		}
	}

	/**
	 * Index of difference.
	 *
	 * @param strs
	 *            the strs
	 * @return the int
	 */
	public static int indexOfDifference(final String strs[]) {
		if (strs == null || strs.length <= 1) {
			return -1;
		}
		boolean anyStringNull = false;
		boolean allStringsNull = true;
		int arrayLen = strs.length;
		int shortestStrLen = 2147483647;
		int longestStrLen = 0;
		for (int i = 0; i < arrayLen; i++) {
			if (strs[i] == null) {
				anyStringNull = true;
				shortestStrLen = 0;
			} else {
				allStringsNull = false;
				shortestStrLen = Math.min(strs[i].length(), shortestStrLen);
				longestStrLen = Math.max(strs[i].length(), longestStrLen);
			}
		}

		if (allStringsNull || longestStrLen == 0 && !anyStringNull) {
			return -1;
		}
		if (shortestStrLen == 0) {
			return 0;
		}
		int firstDiff = -1;
		int stringPos = 0;
		do {
			if (stringPos >= shortestStrLen) {
				break;
			}
			char comparisonChar = strs[0].charAt(stringPos);
			int arrayPos = 1;
			do {
				if (arrayPos >= arrayLen) {
					break;
				}
				if (strs[arrayPos].charAt(stringPos) != comparisonChar) {
					firstDiff = stringPos;
					break;
				}
				arrayPos++;
			} while (true);
			if (firstDiff != -1) {
				break;
			}
			stringPos++;
		} while (true);
		if (firstDiff == -1 && shortestStrLen != longestStrLen) {
			return shortestStrLen;
		} else {
			return firstDiff;
		}
	}

	/**
	 * Gets the common prefix.
	 *
	 * @param strs
	 *            the strs
	 * @return the common prefix
	 */
	public static String getCommonPrefix(final String strs[]) {
		if (strs == null || strs.length == 0) {
			return "";
		}
		int smallestIndexOfDiff = indexOfDifference(strs);
		if (smallestIndexOfDiff == -1) {
			if (strs[0] == null) {
				return "";
			} else {
				return strs[0];
			}
		}
		if (smallestIndexOfDiff == 0) {
			return "";
		} else {
			return strs[0].substring(0, smallestIndexOfDiff);
		}
	}

	/**
	 * Gets the levenshtein distance.
	 *
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return the levenshtein distance
	 */
	public static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}
		int n = s.length();
		int m = t.length();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		if (n > m) {
			String tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}
		int p[] = new int[n + 1];
		int d[] = new int[n + 1];
		for (int i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (int j = 1; j <= m; j++) {
			char t_j = t.charAt(j - 1);
			d[0] = j;
			for (int i = 1; i <= n; i++) {
				int cost = s.charAt(i - 1) != t_j ? 1 : 0;
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
			}

			int _d[] = p;
			p = d;
			d = _d;
		}

		return p[n];
	}

	/**
	 * Starts with.
	 *
	 * @param str
	 *            the str
	 * @param prefix
	 *            the prefix
	 * @return true, if successful
	 */
	public static boolean startsWith(final String str, final String prefix) {
		return startsWith(str, prefix, false);
	}

	/**
	 * Starts with ignore case.
	 *
	 * @param str
	 *            the str
	 * @param prefix
	 *            the prefix
	 * @return true, if successful
	 */
	public static boolean startsWithIgnoreCase(final String str, final String prefix) {
		return startsWith(str, prefix, true);
	}

	/**
	 * Starts with.
	 *
	 * @param str
	 *            the str
	 * @param prefix
	 *            the prefix
	 * @param ignoreCase
	 *            the ignore case
	 * @return true, if successful
	 */
	private static boolean startsWith(final String str, final String prefix, final boolean ignoreCase) {
		if (str == null || prefix == null) {
			return str == null && prefix == null;
		}
		if (prefix.length() > str.length()) {
			return false;
		} else {
			return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
		}
	}

	/**
	 * Starts with any.
	 *
	 * @param string
	 *            the string
	 * @param searchStrings
	 *            the search strings
	 * @return true, if successful
	 */
	public static boolean startsWithAny(final String string, final String searchStrings[]) {
		if (isEmpty(string) || searchStrings == null || searchStrings.length == 0) {
			return false;
		}
		for (int i = 0; i < searchStrings.length; i++) {
			String searchString = searchStrings[i];
			if (startsWith(string, searchString)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Ends with.
	 *
	 * @param str
	 *            the str
	 * @param suffix
	 *            the suffix
	 * @return true, if successful
	 */
	public static boolean endsWith(final String str, final String suffix) {
		return endsWith(str, suffix, false);
	}

	/**
	 * Ends with ignore case.
	 *
	 * @param str
	 *            the str
	 * @param suffix
	 *            the suffix
	 * @return true, if successful
	 */
	public static boolean endsWithIgnoreCase(final String str, final String suffix) {
		return endsWith(str, suffix, true);
	}

	/**
	 * Ends with.
	 *
	 * @param str
	 *            the str
	 * @param suffix
	 *            the suffix
	 * @param ignoreCase
	 *            the ignore case
	 * @return true, if successful
	 */
	private static boolean endsWith(final String str, final String suffix, final boolean ignoreCase) {
		if (str == null || suffix == null) {
			return str == null && suffix == null;
		}
		if (suffix.length() > str.length()) {
			return false;
		} else {
			int strOffset = str.length() - suffix.length();
			return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
		}
	}

	/**
	 * Normalize space.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String normalizeSpace(String str) {
		str = strip(str);
		if (str == null || str.length() <= 2) {
			return str;
		}
		StringBuilder b = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isWhitespace(c)) {
				if (i > 0 && !Character.isWhitespace(str.charAt(i - 1))) {
					b.append(' ');
				}
			} else {
				b.append(c);
			}
		}

		return b.toString();
	}

	/**
	 * Ends with any.
	 *
	 * @param string
	 *            the string
	 * @param searchStrings
	 *            the search strings
	 * @return true, if successful
	 */
	public static boolean endsWithAny(final String string, final String searchStrings[]) {
		if (isEmpty(string) || searchStrings == null || searchStrings.length == 0) {
			return false;
		}
		for (int i = 0; i < searchStrings.length; i++) {
			String searchString = searchStrings[i];
			if (endsWith(string, searchString)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Removes the quotes.
	 *
	 * @param str
	 *            the str
	 * @param quote
	 *            the quote
	 * @return the string
	 */
	public static String removeQuotes(String str, String quote) {
		if (str != null) {
			String value = str.trim();

			if (value.startsWith(quote)) {
				value = value.substring(1);
			}
			if (value.endsWith(quote)) {
				value = value.substring(0, value.length() - 1);
			}

			return value;
		}

		return str;
	}

	/**
	 * Split audit attr values line.
	 *
	 * @param line
	 *            the line
	 * @return the map
	 */
	protected static Map<String, Object> splitAuditAttrValuesLine(String line) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (line != null && line.trim().length() > 0) {
			String[] attrValuePairs = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

			for (String attrValuePair : attrValuePairs) {
				if (attrValuePair.contains("=")) {
					String attr = attrValuePair.substring(0, attrValuePair.indexOf("="));
					String rawValue = attrValuePair.substring(attrValuePair.indexOf("=") + 1);

					rawValue = removeQuotes(rawValue, "\"");
					rawValue = rawValue.trim();

					if (rawValue.startsWith("'")) {
						String[] values = rawValue.split(",(?=([^\']*\'[^\']*\')*[^\']*$)", -1);
						for (int j = 0; j < values.length; j++) {
							values[j] = removeQuotes(values[j], "\'");
						}
						result.put(attr, values);
					} else {
						result.put(attr, rawValue);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Splits given string with preserving double quote surrounded strings.
	 *
	 * @param source
	 *            the source
	 * @return the string[]
	 */
	public static String[] splitPreserveDoubleQuotes(String source) {
		return source.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
	}

	/**
	 * Splits given string with preserving single quote surrounded strings.
	 *
	 * @param source
	 *            the source
	 * @return the string[]
	 */
	public static String[] splitPreserveSingleQuotes(String source) {
		return source.split(",(?=([^\']*\'[^\']*\')*[^\']*$)", -1);
	}

	/**
	 * Splits given string with preserving bracket () surrounded strings.
	 *
	 * @param source
	 *            the source
	 * @return the string[]
	 */
	public static String[] splitPreserveBrackets(String source) {
		return source.split(" *, *(?![^()]*\\))", -1);
	}

	/**
	 * Converts given array of elements &lt;T&gt; (Generics) to a Set&lt;T&gt;
	 * (HashSet).
	 *
	 * @param <T>
	 *            the generic type
	 * @param array
	 *            the array
	 * @return the sets the
	 */
	public static <T> Set<T> toSet(T[] array) {
		Set<T> result = new HashSet<T>();

		if (array != null) {
			for (T t : array) {
				result.add(t);
			}
		}

		return result;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		String oldValue = "Darko1 Sarkanovic [ISC21065]";
		String newValue = "Darko1 Sarkanovic XX [ISC21065]";

		String oldText = "11111 Darko1 Sarkanovic [ISC21065] 2222222222222";
		String newText = replace(oldText, oldValue, newValue, -1);

		System.out.println("Old text '" + oldText + "' replaced with the new text '" + newText + "'");
	}
}
