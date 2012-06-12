package tetz42.clione.lang;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.util.Util.*;

import java.util.regex.Pattern;

import tetz42.util.RegexpTokenizer;

public class LangUtil {

	public static void main(String[] args) {
		checkOut("takoikanamako");
		checkOut("(takoikanamako)");
		checkOut("(takoi')'kanama)ko");
		checkOut("(takoi)kanamako)"); // TODO this should be detected as error.
		checkOut("(takoi)kanama)ko");
	}

	private static void checkOut(String src) {
		System.out.println(src);
		check(src);
	}

	public static boolean isParamExists(Instruction instruction) {
		return !isAllNegative(instruction.params);
	}

	public static void check(String src) {
		RegexpTokenizer rt = new RegexpTokenizer(src, delimPtn).bind(COMMENT,
				commentPtn).bind("'", singleStrPtn).bind("\"", doubleStrPtn);
		parseFunc(rt);
		if (!rt.isEnd())
			throw new RuntimeException(mkStringByCRLF(
					"Too much ')'. It may be unsafe.", getResourceInfo()));
	}

	private static final String COMMENT = "COMMNET";

	private static final Pattern delimPtn = Pattern.compile(
			"/\\*|\\*/|--|'|\"|\\(|\\)|(\r\n|\r|\n)"
					+ "|,|(and|or|union([ \\t]+all)?)($|[ \\t]+)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private static final Pattern commentPtn = Pattern.compile("/\\*|\\*/");
	private static final Pattern singleStrPtn = Pattern
			.compile("(([^']|'')*)'");
	private static final Pattern doubleStrPtn = Pattern
			.compile("(([^\"]|\"\")*)\"");

	private static void parseFunc(RegexpTokenizer rt) {
		while (rt.hasNext()) {
			rt.updateTokenPosition();
			String div = rt.getDelim();
			if (div.equals("*/")) {
				throw new RuntimeException(mkStringByCRLF(
						"Too much '/*'. It may be unsafe.", getResourceInfo()));
			} else if (div.equals("--")) {
				throw new RuntimeException("temp");
			} else if (div.equals("/*")) {
				findCommentEnd(rt);
			} else if (div.equals("(")) {
				doParenthesis(rt);
			} else if (div.equals("'") || div.equals("\"")) {
				doString(rt, div);
			} else {
				// in case line end, end of parenthesis or end of source string
				if (div.equals(")"))
					break;
			}
		}
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
		parseFunc(rt);
		if (rt.isEnd() && !")".equals(rt.matcher().group()))
			throw new RuntimeException(mkStringByCRLF(
					"Too much '('. It may be unsafe.", getResourceInfo()));
	}

	// find end string literal.
	private static void doString(RegexpTokenizer rt, final String type) {
		if (!rt.find(type))
			throw new RuntimeException(
					mkStringByCRLF("Unmatch String literal: [" + type + "]",
							getResourceInfo()));
		rt.updateTokenPosition();
	}
}
