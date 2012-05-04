package tetz42.clione.parsar;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.util.Util.*;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.node.ConditionPlaceHolder;
import tetz42.clione.node.EmptyLineNode;
import tetz42.clione.node.INode;
import tetz42.clione.node.LineNode;
import tetz42.clione.node.Node;
import tetz42.clione.node.ParenthesisPlaceHolder;
import tetz42.clione.node.PlaceHolder;
import tetz42.clione.node.SQLNode;
import tetz42.clione.node.StrNode;
import tetz42.clione.setting.Config;
import tetz42.util.IOUtil;
import tetz42.util.ObjDumper4j;
import tetz42.util.RegexpTokenizer;
import tetz42.util.exception.WrapException;

public class SQLParserSample {

	private static final Map<String, String> map = HereDoc
			.get(SQLParserSample.class
					.getResourceAsStream("SQLParserSample.txt"));

	private static void test(String key) {
		System.out.println("[" + key + "]");
		System.out.println(ObjDumper4j.dumper(
				new SQLParserSample(key).parse(map.get(key))).primitiveFirst()
				.classFlatten());
	}

	public static void main(String args[]) {
		System.out.println(joinOnlyPtn.matcher("   union\nall  ").find());

		System.out.println("---------------------------");
		test("normal");
		test("singleQuote");
		test("doubleQuote");
		test("lineComment");
		test("parenthesis");
		test("complecated");
		test("select");
		test("unionSelect");
		test("multiComment");
		test("emptyLine");
		test("emptyLine2");
	}

	public static final Pattern indentPtn = Pattern.compile("\\A([ \\t]+)");

	private static final String COMMENT = "COMMNET";
	private static final String LINEEND = "LINEEND";
	private static final String OPERATOR = "OPERATOR";
	private static final String NORMAL = "NORMAL";
	private static final String EMPTYLN = "EMPTYLN";

	private static final Pattern delimPtn = Pattern.compile(
			"/\\*|\\*/|--|'|\"|\\(|\\)|(\r\n|\r|\n)|\\z"
					+ "|,|(and|or|union([ \\t]+all)?)($|[ \\t]+)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private static final Pattern commentPtn = Pattern
			.compile("/\\*|\\*/|(\r\n|\r|\n)");
	private static final Pattern lineEndPtn = Pattern
			.compile("(.*)(\r\n|\r|\n|\\z)");
	private static final Pattern singleStrPtn = Pattern
			.compile("(([^']|'')*)'");
	private static final Pattern doubleStrPtn = Pattern
			.compile("(([^\"]|\"\")*)\"");

	private static final Pattern crlfPth = Pattern.compile("\r\n|\r|\n");

	private static final Pattern joinOnlyPtn = Pattern.compile(
			"\\A[ \\t]*(and|or|,|union(\\s+all)?)[ \\t]*\\z",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern emptyLinePtn = Pattern
			.compile("[ \\t]*(\r\n|\r|\n)");

	private static final Pattern operatorPtn = Pattern
			.compile(
					"(=\\s*|in\\s+|is\\s+)|(!=\\s*|<>\\s*|not\\s+in\\s+|is\\s+not\\s+)",
					Pattern.CASE_INSENSITIVE);
	private static final Pattern normalValuePtn = Pattern
			.compile("[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*");

	// private static final Pattern closePtn = Pattern.compile("\\A[ \\t]*\\)");

	private String resourceInfo = null;

	public SQLParserSample(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public SQLNode parse(InputStream in) {
		try {
			byte[] bs = IOUtil.loadFromStream(in);
			return parse(new String(bs, Config.get().SQLFILE_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new WrapException(mkStringByCRLF(e.getMessage(),
					"The setting of 'clione.properties' might be wrong. ",
					"The key name = 'SQLFILE_ENCODING'"), e);
		}
	}

	public SQLNode parse(String src) {
		try {
			pushResouceInfo(resourceInfo);
			return parseRoot(src);
		} finally {
			popResourceInfo();
		}
	}

	private SQLNode parseRoot(String src) {
		List<LineNode> flatList = parseFunction(src);
		while (flatList.size() > 0 && flatList.get(0).isEmpty())
			flatList.remove(0);
		return parseIndent(flatList);
	}

	private List<LineNode> parseFunction(String src) {
		List<LineNode> flatList = new ArrayList<LineNode>();
		RegexpTokenizer mh = new RegexpTokenizer(src, delimPtn).bind(COMMENT,
				commentPtn).bind(LINEEND, lineEndPtn).bind("'", singleStrPtn)
				.bind("\"", doubleStrPtn).bind(OPERATOR, operatorPtn).bind(
						NORMAL, normalValuePtn).bind(EMPTYLN, emptyLinePtn)
				.updateTokenPosition();
		LineInfo info = new LineInfo(1);
		parseFunc(flatList, mh, info);
		if (!mh.isEnd())
			throw new ClioneFormatException(mkStringByCRLF(
					"SQL Format Error: too much ')'", getResourceInfo()));
		return flatList;
	}

	private void parseFunc(final List<LineNode> flatList, RegexpTokenizer mh,
			LineInfo info) {
		doEmptyLine(flatList, mh, info);
		while (mh.find()) {
			info.nodeSb.append(mh.nextToken());
			String div = mh.matcher().group();
			// System.out.println("[" + div + "]");
			if (div.equals("*/")) {
				throw new ClioneFormatException(mkStringByCRLF(
						"SQL Format Error: too much '*/'", getResourceInfo()));
			} else if (div.equals("--")) {
				doLineComment(mh, info);
			} else if (div.equals("/*")) {
				doMultiComment(mh, info);
			} else if (div.equals("(")) {
				doParenthesis(mh, info);
			} else if (div.equals("'") || div.equals("\"")) {
				doString(mh, info, div);
			} else if (startsWith(div, "and", "or", "union") || div.equals(",")) {
				info.nodeSb.append(div);
				info.mergeNode();
			} else {
				// in case line end, end of parenthesis or end of source string
				info.mergeNode();
				if (joinOnlyPtn.matcher(info.lineSb).find()
						&& info.lineNode.holders.size() == 0) {
					info.lineSb.append(CRLF);
					continue;
				}
				flatList.add(info.fixLineNode());
				if (div.equals(")"))
					break;
				doEmptyLine(flatList, mh, info);
			}
		}
	}

	private void doEmptyLine(List<LineNode> flatList, RegexpTokenizer mh,
			LineInfo info) {
		while (mh.startsWith(EMPTYLN)) {
			flatList.add(new EmptyLineNode(info.lineNo));
			info.fixLineNode();
			mh.updateTokenPosition();
		}
	}

	private boolean startsWith(String src, String... dests) {
		src = src.toLowerCase();
		for (String dst : dests) {
			if (src.startsWith(dst))
				return true;
		}
		return false;
	}

	// find end comment and try to parse as function.
	private void doMultiComment(RegexpTokenizer mh, LineInfo info) {
		findCommentEnd(mh, info);
		String comment = mh.nextToken();
		if (isEmpty(comment) || "*".contains(comment.substring(0, 1))) {
			// Just a comment. Ignore.
			return;
		}
		if ("!+".contains(comment.substring(0, 1))) {
			// hint clause.
			info.nodeSb.append("/*" + comment + "*/");
			return;
		}

		String operator = null;
		boolean isPositive = false;
		if (mh.startsWith(OPERATOR)) {
			String positiveOpe = mh.matcher().group(1);
			String negativeOpe = mh.matcher().group(2);
			isPositive = positiveOpe != null;
			operator = isPositive ? positiveOpe : negativeOpe;
		}

		INode valueInBack = genValueInBack(mh, info);
		if (operator == null) {
			info.mergeNode();
			info.addPlaceHolder(new PlaceHolder(comment, valueInBack));
		} else {
			Node node = info.fixNode();
			info.addPlaceHolder(new ConditionPlaceHolder(node, comment,
					isPositive, operator, valueInBack));
		}
	}

	private void findCommentEnd(RegexpTokenizer mh, LineInfo info) {
		while (mh.find(COMMENT)) {
			if (mh.matcher().group().equals("*/"))
				return; // normal end
			else if (mh.matcher().group().equals("/*"))
				// in case nested '/*' is detected
				findCommentEnd(mh, info);
			else
				// in case CRLF is detected
				info.addLineNo();
		}
		throw new ClioneFormatException(mkStringByCRLF(
				"SQL Format Error: too much '/*'", getResourceInfo()));
	}

	private INode genValueInBack(RegexpTokenizer mh, LineInfo info) {
		INode valueInBack = null;
		char c = mh.getNextChar();
		switch (c) {
		case '\'':
		case '"':
			mh.forward().updateTokenPosition();
			info.push();
			doString(mh, info, "" + c);
			valueInBack = new StrNode(info.nodeSb.toString());
			info.pop();
			break;
		case '(':
			mh.forward().updateTokenPosition();
			info.push();
			doParenthesis(mh, info);
			ParenthesisPlaceHolder holder = (ParenthesisPlaceHolder) info.node.holders
					.get(0);
			valueInBack = holder.sqlNode();
			info.pop();
			break;
		default:
			if (mh.startsWith(NORMAL)) {
				valueInBack = new StrNode(mh.matcher().group());
				mh.updateTokenPosition();
			}
		}
		return valueInBack;
	}

	// find end parenthesis and try to parse as SQLNode.
	private void doParenthesis(RegexpTokenizer mh, LineInfo info) {
		List<LineNode> flatList = new ArrayList<LineNode>();
		info.push();
		parseFunc(flatList, mh, info);
		if (mh.isEnd())
			throw new ClioneFormatException(mkStringByCRLF(
					"SQL Format Error: too much '('", getResourceInfo()));
		info.pop();
		info.addPlaceHolder(new ParenthesisPlaceHolder(parseIndent(flatList)));
	}

	// find end string literal.
	private void doString(RegexpTokenizer mh, LineInfo info, final String type) {
		info.nodeSb.append(type);
		if (!mh.find(type))
			throw new ClioneFormatException(mkStringByCRLF(
					"SQL Format Error: [" + type + "] unmatched!",
					getResourceInfo()));
		String literal = mh.nextTokenDelim();
		info.nodeSb.append(literal);
		Matcher m = crlfPth.matcher(literal);
		while (m.find())
			info.addLineNo();
	}

	/**
	 * find line end. create place holder if it is parameter, join with next
	 * line if it is the sign of join, otherwise don't add to SQL because it's
	 * just a comment.
	 */
	private void doLineComment(RegexpTokenizer mh, LineInfo info) {
		mh.find(LINEEND);
		String comment = mh.matcher().group(1);
		if (isEmpty(comment) || isAllSpace(comment)) {
			info.addLineNo(); // because find the line end.
			return;
		} else if (comment.startsWith(" ")
				&& "$@&?#%'\":|".contains(comment.substring(1, 2))) {
			info.addPlaceHolder(new PlaceHolder(comment, (String) null));
		}
		mh.backward(mh.matcher().group(2).length()); // ready for next
		mh.updateTokenPosition();
	}

	private SQLNode parseIndent(List<LineNode> flatList) {
		NodeHolder holder = new NodeHolder(flatList);
		List<LineNode> resultList = new ArrayList<LineNode>();
		List<LineNode> list;
		do {
			resultList.addAll(list = buildNodes(holder, ""));
		} while (list.size() != 0);
		SQLNode sqlNode = new SQLNode();
		sqlNode.nodes = resultList;
		sqlNode.resourceInfo = resourceInfo;
		return sqlNode;
	}

	private List<LineNode> buildNodes(NodeHolder holder, String indent) {
		return buildNodes(holder, indent, new ArrayList<LineNode>());
	}

	private List<LineNode> buildNodes(NodeHolder holder, String indent,
			ArrayList<LineNode> empties) {
		ArrayList<LineNode> list = new ArrayList<LineNode>();

		LineNode parentNode = null;
		LineNode node;
		while (null != (node = holder.next())) {
			if (node instanceof EmptyLineNode) {
				empties.add(node);
				continue;
			}
			Matcher m = indentPtn.matcher(node.sql);
			String curIndent = m.find() ? m.group(1) : "";

			if (calcIndent(indent) < calcIndent(curIndent)) {
				holder.back();
				if (parentNode == null) {
					// performed only 1st loop time.
					indent = curIndent;
					continue;
				}
				parentNode.childBlocks.addAll(buildNodes(holder, curIndent,
						empties));
				continue;
				// } else if (calcIndent(indent) > calcIndent(curIndent)
				// && !closePtn.matcher(node.sql).find()) {
			} else if (calcIndent(indent) > calcIndent(curIndent)) {
				holder.back();
				return list;
			}

			list.addAll(empties);
			empties.clear();
			list.add(parentNode = node);
		}
		return list;
	}

	private static int calcIndent(String indent) {
		final int TAB_SIZE = Config.get().TAB_SIZE;
		byte[] bytes = indent.getBytes();
		int tabUnitSize = 0;
		int resultSize = 0;
		for (byte b : bytes) {
			if (b == ' ')
				tabUnitSize++;
			else if (b == '\t')
				tabUnitSize = TAB_SIZE;
			if (tabUnitSize == TAB_SIZE)
				resultSize += tabUnitSize;
		}
		return resultSize + tabUnitSize;
	}

	static class NodeHolder {
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
}
