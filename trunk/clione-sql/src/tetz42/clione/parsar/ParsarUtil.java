package tetz42.clione.parsar;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.util.ClioneUtil.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.node.LineNode;

public class ParsarUtil {

	private static final Pattern delimPtn = Pattern.compile("[()']");
	private static final Pattern opePtn = Pattern.compile(
			"\\A((!?=|<>|(not\\s+)?in|is(\\s+not)?)\\s+)",
			Pattern.CASE_INSENSITIVE);

	public static class NodeHolder {
		private List<LineNode> nodes;
		private int pos;

		public NodeHolder(List<LineNode> nodes) {
			this.nodes = nodes;
			this.pos = 0;
		}

		public LineNode next() {
			LineNode node = get();
			pos++;
			return node;
		}

		public LineNode get() {
			if (pos >= nodes.size())
				return null;
			return nodes.get(pos);
		}

		public LineNode back() {
			pos--;
			return get();
		}
	}

	public static String getValueInBack(String src) {
		if (isEmpty(src))
			return null;
		String operator = "";
		Matcher m = opePtn.matcher(src);
		if (m.find()) {
			operator = m.group();
			src = src.substring(m.end());
		}
		if (src.charAt(0) == '(' || src.charAt(0) == '\'')
			return operator + getLiteralValue(src);
		else if (isWordChar(src.charAt(0)))
			return operator + getNormalValue(src);
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

	static int wordEnd(String src) {
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
