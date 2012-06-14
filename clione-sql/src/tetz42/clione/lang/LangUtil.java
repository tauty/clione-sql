package tetz42.clione.lang;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.util.Util.*;

import java.util.regex.Pattern;

import tetz42.util.RegexpTokenizer;

public class LangUtil {

	public static boolean isParamExists(Instruction instruction) {
		return !isAllNegative(instruction.params);
	}

	public static void main(String[] args) {
		checkOut("takoikanamako");
		checkOut("(takoikanamako)");
		checkOut("(takoi')'kanama)ko");
		// checkOut("(takoi)kanamako)");
		// checkOut("(takoi)kanama)ko");
		checkOut("(takoi)kan 'tako\\' amako");
		ContextUtil.setProductName("oracle");
		checkOut("(takoi)kan 'tako\\' amako"); // TODO this case must be a
												// failure case.
		ContextUtil.setProductName("mysql");
		checkOut("(takoi)kan 'tako\\' amako"); // TODO this case must be a
												// failure case.
		ContextUtil.setProductName("postgres");
		checkOut("(takoi)kan 'tako\\' amako"); // TODO this case must be a
												// failure case.
	}

	private static void checkOut(String src) {
		System.out.println(src);
		check(src);
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
			} else if (div.equals("--")) {
				throw new RuntimeException("Unsafe symbol, '" + div
						+ "', is detected.");
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
				throw new RuntimeException("Unsafe symbol, '" + div
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
				throw new RuntimeException("Recursive comment is not safe.");
		}
		throw new RuntimeException(mkStringByCRLF(
				"Too much '/*'. It may be unsafe.", getResourceInfo()));
	}

	// find end parenthesis and try to parse as SQLNode.
	private static void doParenthesis(RegexpTokenizer rt) {
		if (parseFunc(rt))
			throw new RuntimeException(mkStringByCRLF(
					"Too much '('. It may be unsafe.", getResourceInfo()));
	}

	// find end string literal.
	private static void doString(RegexpTokenizer rt, final String type) {
		if (!rt.startsWith(type))
			throw new RuntimeException(
					mkStringByCRLF("Unmatch String literal: [" + type + "]",
							getResourceInfo()));
		rt.updateTokenPosition();
	}
}
