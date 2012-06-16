package tetz42.clione.lang;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.util.Util.*;

import java.util.Map;
import java.util.regex.Pattern;

import tetz42.clione.exception.SecurityValidationException;
import tetz42.util.HereDoc;
import tetz42.util.RegexpTokenizer;

public class LangUtil {

	private static final Map<String, String> hereDoc = HereDoc.get(LangUtil.class);

	public static String getLongMsg(String key) {
		return hereDoc.get(key);
	}

	public static boolean isParamExists(Instruction instruction) {
		return !isAllNegative(instruction.params);
	}

	private static final String COMMENT = "COMMNET";

	private static final Pattern delimPtn = Pattern
			.compile("/\\*|\\*/|--|'|\"|\\(|\\)|;|\"|\\|\\?|[\\x00-\\x08\\x11\\x12\\x14-\\x1F\\x7F]");
	private static final Pattern commentPtn = Pattern.compile("/\\*|\\*/");
	private static final Pattern singleStrPtn = Pattern
			.compile("(([^']|'')*)'");
	private static final Pattern singleStrPtn2 = Pattern
			.compile("(([^'\\\\]|''|\\\\.)*)'");

	public static void check(String src) {
		RegexpTokenizer rt = new RegexpTokenizer(src, delimPtn).bind(COMMENT,
				commentPtn).bind(
				"'",
				getDialect().backslashWorkAsEscape() ? singleStrPtn2
						: singleStrPtn);
		if (!parseFunc(rt))
			throw new RuntimeException(mkStringByCRLF(
					"Too much ')'. It may be unsafe.", getResourceInfo()));
	}

	/**
	 * @return the end of source string -> true, the end of parenthesis -> false
	 */
	private static boolean parseFunc(RegexpTokenizer rt) {
		while (rt.hasNext()) {
			rt.updateTokenPosition();
			String div = rt.getDelim();
			if (div.equals("*/")) {
				throw new RuntimeException(mkStringByCRLF(
						"Too much '*/'. It may be unsafe.", getResourceInfo()));
			} else if (div.equals("/*")) {
				findCommentEnd(rt);
			} else if (div.equals("'")) {
				doString(rt, div);
			} else if (div.equals("(")) {
				doParenthesis(rt);
			} else if (div.equals(")")) {
				return false; // in case of parenthesis end
			} else if (div.equals("") && rt.isEnd()) {
				return true;
			} else {
				throw new SecurityValidationException("Unsafe symbol, '" + div
						+ "', is detected.");
			}
		}
		return true;
	}

	private static void findCommentEnd(RegexpTokenizer rt) {
		while (rt.find(COMMENT)) {
			if (rt.matcher().group().equals("*/"))
				return; // normal end
			else
				throw new SecurityValidationException(
						"Recursive comment is not allowd because it might not be safe.");
		}
		throw new SecurityValidationException(mkStringByCRLF(
				"Too much '/*'. It may be unsafe.", getResourceInfo()));
	}

	// find end parenthesis and try to parse as SQLNode.
	private static void doParenthesis(RegexpTokenizer rt) {
		if (parseFunc(rt))
			throw new SecurityValidationException(mkStringByCRLF(
					"Too much '('. It may be unsafe.", getResourceInfo()));
	}

	// find end string literal.
	private static void doString(RegexpTokenizer rt, final String type) {
		if (!rt.startsWith(type))
			throw new SecurityValidationException(
					mkStringByCRLF("Unmatch String literal: [" + type + "]",
							getResourceInfo()));
		rt.updateTokenPosition();
	}
}
