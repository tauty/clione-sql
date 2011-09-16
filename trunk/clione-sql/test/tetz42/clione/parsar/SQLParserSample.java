package tetz42.clione.parsar;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.parsar.ParsarUtil.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.exception.WrapException;
import tetz42.clione.io.IOUtil;
import tetz42.clione.node.IPlaceHolder;
import tetz42.clione.node.LineNode;
import tetz42.clione.node.Node;
import tetz42.clione.node.ParenthesisPlaceHolder;
import tetz42.clione.node.PlaceHolder;
import tetz42.clione.node.SQLNode;
import tetz42.clione.parsar.ParsarUtil.NodeHolder;
import tetz42.clione.setting.Config;
import tetz42.util.ObjDumper4j;

public class SQLParserSample {

	private static final Map<String, String> map = HereDoc
			.get(SQLParserSample.class
					.getResourceAsStream("SQLParserSample.txt"));

	private static void test(String key) {
		System.out.println("[" + key + "]");
		System.out.println(ObjDumper4j.dumper(new SQLParserSample(key)
				.parse(map.get(key))));
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
	}

	private static final String COMMENT = "COMMNET";
	private static final String LINEEND = "LINEEND";

	private static final Pattern delimPtn = Pattern.compile(
			"/\\*|\\*/|--|'|\"|\\(|\\)|(\r\n|\r|\n)|\\z"
					+ "|,|(and|or|union([ \\t]+all)?)($|[ \\t]+)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private static final Pattern joinOnlyPtn = Pattern.compile(
			"\\A[ \\t]*(and|or|,|union(\\s+all)?)[ \\t]*\\z",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern lineEndPtn = Pattern
			.compile("(.*)(\r\n|\r|\n|\\z)");
	private static final Pattern commentPtn = Pattern
			.compile("/\\*|\\*/|(\r\n|\r|\n)");
	private static final Pattern singleStrPtn = Pattern
			.compile("(([^']|'')*)'");
	private static final Pattern doubleStrPtn = Pattern
			.compile("(([^\"]|\"\")*)\"");

	private static final Pattern crlfPth = Pattern.compile("\r\n|\r|\n");

	private static final Pattern indentPtn = Pattern.compile("\\A(\\s+)");
	private static final Pattern closePtn = Pattern.compile("\\A\\s*\\)");

	private static class LineInfo {
		LinkedList<LineInfo> stack = new LinkedList<LineInfo>();
		private Node node;
		private LineNode lineNode;
		private StringBuilder nodeSb;
		private StringBuilder lineSb;
		private int lineNo;

		void addPlaceHolder(IPlaceHolder holder) {
			holder.setPosition(this.nodeSb.length());
			this.node.holders.add(holder);
		}

		LineInfo(int lineNo) {
			this.node = new Node();
			this.nodeSb = new StringBuilder();
			this.lineNode = new LineNode(lineNo);
			this.lineSb = new StringBuilder();
			this.lineNo = lineNo;
		}

		void mergeNode() {
			for (IPlaceHolder h : this.node.holders) {
				h.movePosition(this.lineSb.length());
				lineNode.holders.add(h);
			}
			this.lineSb.append(this.nodeSb);
			this.node = new Node();
			this.nodeSb.setLength(0);
		}

		Node fixNode() {
			this.node.sql = this.nodeSb.toString();
			Node node = this.node;
			this.node = new Node();
			this.nodeSb.setLength(0);
			return node;
		}

		LineNode fixLineNode() {
			this.lineNode.sql = this.lineSb.toString();
			LineNode lineNode = this.lineNode;
			this.lineNo++;
			this.clear();
			return lineNode;
		}

		void addLineNo() {
			this.lineNo++;
			this.lineNode.curLineNo(lineNo);
		}

		void clear() {
			this.lineNode = new LineNode(lineNo);
			this.lineSb.setLength(0);
		}

		LineInfo push() {
			// backup
			LineInfo backup = new LineInfo(0);
			backup.node = this.node;
			backup.lineNode = this.lineNode;
			backup.nodeSb = this.nodeSb;
			backup.lineSb = this.lineSb;
			stack.push(backup);

			// initialize
			this.node = new Node();
			this.lineNode = new LineNode(this.lineNo);
			this.nodeSb = new StringBuilder();
			this.lineSb = new StringBuilder();
			return this;
		}

		LineInfo pop() {

			// reset to backup
			LineInfo backup = stack.pop();
			this.node = backup.node;
			this.lineNode = backup.lineNode;
			this.lineNode.curLineNo(this.lineNo);
			this.nodeSb = backup.nodeSb;
			this.lineSb = backup.lineSb;

			return this;
		}
	}

	private String resourceInfo = null;

	private List<LineNode> parseFunction2(String src) {
		List<LineNode> flatList = new ArrayList<LineNode>();
		MatcherHolder mh = new MatcherHolder(src, delimPtn).bind(COMMENT,
				commentPtn).bind(LINEEND, lineEndPtn).bind("'", singleStrPtn)
				.bind("\"", doubleStrPtn).remember();
		LineInfo info = new LineInfo(1);
		parseFunc(flatList, mh, info);
		if (!mh.isEnd())
			throw new ClioneFormatException(joinByCrlf(
					"SQL Format Error: too much ')'", getResourceInfo()));
		return flatList;
	}

	private void parseFunc(final List<LineNode> flatList, MatcherHolder mh,
			LineInfo info) {
		while (mh.find()) {
			info.nodeSb.append(mh.getRememberedToStart());
			String div = mh.get().group();
			// System.out.println("[" + div + "]");
			if (div.equals("*/")) {
				throw new ClioneFormatException(joinByCrlf(
						"SQL Format Error: too much '*/'", getResourceInfo()));
			} else if (div.equals("--")) {
				doLineComment(mh, info);
			} else if (div.equals("/*")) {
				doMultiComment(mh, info);
			} else if (div.equals("(")) {
				doParenthesis(mh, info);
			} else if (div.equals("'") || div.equals("\"")) {
				doString(mh, info, div);
			} else if (div.equalsIgnoreCase("and")
					|| div.equalsIgnoreCase("or") || div.equals(",")
					|| div.toLowerCase().startsWith("union")) {
				info.nodeSb.append(div);
				info.mergeNode();
			} else {
				// in case line end or end of source string
				info.mergeNode();
				if (joinOnlyPtn.matcher(info.lineSb).find()
						&& info.lineNode.holders.size() == 0) {
					info.lineSb.append(CRLF);
					continue;
				}
				flatList.add(info.fixLineNode());
				if (div.equals(")"))
					break;
			}
		}
	}

	// find end parenthesis and try to parse as SQLNode.
	private void doParenthesis(MatcherHolder mh, LineInfo info) {
		List<LineNode> flatList = new ArrayList<LineNode>();
		info.push();
		parseFunc(flatList, mh, info);
		System.out.println(ObjDumper4j.dumper("<flatList>\n", flatList));
		if (mh.isEnd())
			throw new ClioneFormatException(joinByCrlf(
					"SQL Format Error: too much '('", getResourceInfo()));
		info.pop();

		info.addPlaceHolder(new ParenthesisPlaceHolder(parseIndent(flatList)));
	}

	/**
	 * find line end. create place holder if it is parameter, join with next
	 * line if it is the sign of join, otherwise don't add to SQL because it's
	 * just a comment.
	 */
	private void doLineComment(MatcherHolder mh, LineInfo info) {
		mh.find(LINEEND);
		String comment = mh.get(LINEEND).group(1);
		if (isEmpty(comment) || isAllSpace(comment)) {
			info.addLineNo(); // because find the line end.
			return;
		} else if (comment.startsWith(" ")
				&& "$@&?#%'\":|".contains(comment.substring(1, 2))) {
			info.addPlaceHolder(new PlaceHolder(comment, null));
		}
		mh.back(mh.get(LINEEND).group(2).length()); // ready for next
		mh.remember();
	}

	// find end comment and try to parse as function.
	private void doMultiComment(MatcherHolder mh, LineInfo info) {
		findCommentEnd(mh, info);
		String comment = mh.getRememberedToStartWithoutRemember();
		if (isEmpty(comment) || comment.startsWith("*")) {
			mh.remember();
			return;
		}
		if ("!+".contains(comment.substring(0, 1))) {
			info.nodeSb.append(mh.getRememberedToEnd(2));
			return;
		}
		mh.remember();

		// TODO get valueInBack and create PlaceHolder
	}

	private void findCommentEnd(MatcherHolder mh, LineInfo info) {
		while (mh.find(COMMENT)) {
			if (mh.get(COMMENT).group().equals("*/"))
				return; // normal end
			else if (mh.get(COMMENT).group().equals("/*"))
				// in case nested '/*' is detected
				findCommentEnd(mh, info);
			else
				// in case CRLF is detected
				info.addLineNo();
		}
		throw new ClioneFormatException(joinByCrlf(
				"SQL Format Error: too much '/*'", getResourceInfo()));
	}

	// find end string literal.
	private void doString(MatcherHolder mh, LineInfo info, final String type) {
		info.nodeSb.append(type);
		if (!mh.find(type))
			throw new ClioneFormatException(joinByCrlf("SQL Format Error: ["
					+ type + "] unmatched!", getResourceInfo()));
		Matcher m = crlfPth.matcher(mh.getRememberedToEndWithoutRemember());
		while (m.find())
			info.addLineNo();
	}

	public SQLParserSample(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public SQLNode parse(String src) {
		try {
			pushResouceInfo(resourceInfo);
			return parseRoot(src);
		} finally {
			popResourceInfo();
		}
	}

	public SQLNode parse(InputStream in) {
		try {
			byte[] bs = IOUtil.loadFromStream(in);
			return parse(new String(bs, Config.get().SQLFILE_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new WrapException(joinByCrlf(e.getMessage(),
					"The setting of 'clione.properties' might be wrong. ",
					"The key name = 'SQLFILE_ENCODING'"), e);
		}
	}

	private SQLNode parseRoot(String src) {
		List<LineNode> flatList = parseFunction2(src);
		return parseIndent(flatList);
	}

	private SQLNode parseIndent(List<LineNode> wholeNodeList) {
		NodeHolder holder = new NodeHolder(wholeNodeList);
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
		ArrayList<LineNode> list = new ArrayList<LineNode>();

		LineNode parentNode = null;
		LineNode node;
		while (null != (node = holder.next())) {
			Matcher m = indentPtn.matcher(node.sql);
			String curIndent = m.find() ? m.group(1) : "";

			if (calcIndent(indent) < calcIndent(curIndent)) {
				holder.back();
				if (parentNode == null) {
					// performed only 1st loop time.
					indent = curIndent;
					continue;
				}
				parentNode.childBlocks.addAll(buildNodes(holder, curIndent));
				continue;
			} else if (calcIndent(indent) > calcIndent(curIndent)
					&& !closePtn.matcher(node.sql).find()) {
				holder.back();
				return list;
			}

			list.add(parentNode = node);
		}
		return list;
	}
}
