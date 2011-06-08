package tetz42.clione.parsar;

import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.clione.util.ContextUtil.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;

public class ValueInBack {

	private static final Pattern delimPtn = Pattern.compile("[()']");

	public static String getValueInBack(String src) {
		if (src.charAt(0) == '(' || src.charAt(0) == '\'')
			return getLiteralValue(src);
		else if (isWordChar(src.charAt(0)))
			return getNormalValue(src);
		return null;
	}

	private static String getNormalValue(String src) {
		return src.substring(0, wordEnd(src));
	}

	private static String getLiteralValue(String src) {
		Matcher m = delimPtn.matcher(src);
		if (m.find()) {
			if (m.group().equals("("))
				findParenthesisEnd(src, m);
			else if (m.group().equals("'"))
				endQuotation(src, m);
		}
		return src.substring(0, m.end());
	}

	private static void findParenthesisEnd(String src, Matcher m) {
		while (m.find()) {
			if (m.group().equals(")"))
				return;
			else if (m.group().equals("'"))
				endQuotation(src, m);
			else
				findParenthesisEnd(src, m);
		}
		throw new ClioneFormatException("Parenthesises Unmatched! data = "
				+ src + CRLF + "resource = " + getResourceInfo());
	}

	private static void endQuotation(String src, Matcher m) {
		final String quot = "'";
		while (m.find()) {
			if (quot.equals(m.group(0))) {
				if (quot.equals(nextChar(src, m.end()))) {
					m.region(m.end() + 1, src.length());
				} else {
					return;
				}
			}
		}
		throw new ClioneFormatException("Single quotation Unmatched! data = "
				+ src + CRLF + "resource = " + getResourceInfo());
	}

	private static String nextChar(String src, int pos) {
		if (pos >= src.length())
			return null;
		return src.substring(pos, pos + 1);
	}

	private static int wordEnd(String src) {
		int pos = 0;
		while (pos < src.length() && isWordChar(src.charAt(pos)))
			pos++;
		return pos;
	}

	private static boolean isWordChar(char c) {
		if ('0' <= c && c <= '9')
			return true;
		if ('a' <= c && c <= 'z')
			return true;
		if ('A' <= c && c <= 'Z')
			return true;
		switch (c) {
		case '_':
		case '-':
			return true;
		default:
			return false;
		}
	}
}
