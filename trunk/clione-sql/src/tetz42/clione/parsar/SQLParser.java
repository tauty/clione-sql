/*
 * Copyright 2011 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tetz42.clione.parsar;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.util.Util.*;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
import tetz42.util.RegexpTokenizer;
import tetz42.util.exception.WrapException;

public class SQLParser {

	public static final Pattern indentPtn = Pattern.compile("\\A([ \\t]+)");

	private static final String COMMENT = "COMMNET";
	private static final String LINEEND = "LINEEND";
	private static final String OPERATOR = "OPERATOR";
	private static final String NORMAL = "NORMAL";
	private static final String EMPTYLN = "EMPTYLN";

	private static final Pattern delimPtn = Pattern.compile(
			"/\\*|\\*/|--|'|\"|\\(|\\)|(\r\n|\r|\n)"
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
					"(=\\s*|in\\s+|is\\s+|like\\s+)|(!=\\s*|<>\\s*|not\\s+in\\s+|is\\s+not\\s+|not\\s+like\\s+)",
					Pattern.CASE_INSENSITIVE);
	private static final Pattern normalValuePtn = Pattern
			.compile("[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*");

	private String resourceInfo = null;

	public SQLParser(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public SQLNode parse(InputStream in) {
		try {
			byte[] bs = IOUtil.toByteArray(in);
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
		RegexpTokenizer rt = new RegexpTokenizer(src, delimPtn).bind(COMMENT,
				commentPtn).bind(LINEEND, lineEndPtn).bind("'", singleStrPtn)
				.bind("\"", doubleStrPtn).bind(OPERATOR, operatorPtn).bind(
						NORMAL, normalValuePtn).bind(EMPTYLN, emptyLinePtn);
		LineInfo info = new LineInfo(1);
		if (!parseFunc(flatList, rt, info))
			throw new ClioneFormatException(mkStringByCRLF(
					"SQL Format Error: too much ')'", getResourceInfo()));
		return flatList;
	}

	/**
	 * @return the end of source string -> true, the end of parenthesis -> false
	 */
	private boolean parseFunc(final List<LineNode> flatList, RegexpTokenizer rt,
			LineInfo info) {
		doEmptyLine(flatList, rt, info);
		while (rt.hasNext()) {
			info.nodeSb.append(rt.nextToken());
			String div = rt.getDelim();
			if (div.equals("*/")) {
				throw new ClioneFormatException(mkStringByCRLF(
						"SQL Format Error: too much '*/'", getResourceInfo()));
			} else if (div.equals("--")) {
				doLineComment(rt, info);
			} else if (div.equals("/*")) {
				doMultiComment(rt, info);
			} else if (div.equals("(")) {
				doParenthesis(rt, info);
			} else if (div.equals("'") || div.equals("\"")) {
				doString(rt, info, div);
			} else if (div.equals(",") || startsWith(div, "and", "or", "union")) {
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
					return false;
				doEmptyLine(flatList, rt, info);
			}
		}
		return true;
	}

	private void doEmptyLine(List<LineNode> flatList, RegexpTokenizer rt,
			LineInfo info) {
		while (rt.startsWith(EMPTYLN)) {
			flatList.add(new EmptyLineNode(info.lineNo));
			info.fixLineNode();
			rt.updateTokenPosition();
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
	private void doMultiComment(RegexpTokenizer rt, LineInfo info) {
		findCommentEnd(rt, info);
		String comment = rt.getToken();
		if (isEmpty(comment) || "*".contains(comment.substring(0, 1))) {
			// Just a comment. Ignore.
			rt.updateTokenPosition();
			return;
		}
		if ("!+".contains(comment.substring(0, 1))) {
			// hint clause.
			info.nodeSb.append(rt.nextDelimTokenDelim());
			return;
		}
		rt.updateTokenPosition();

		String operator = null;
		boolean isPositive = false;
		if (rt.startsWith(OPERATOR)) {
			String positiveOpe = rt.matcher().group(1);
			String negativeOpe = rt.matcher().group(2);
			isPositive = positiveOpe != null;
			operator = isPositive ? positiveOpe : negativeOpe;
		}

		INode valueInBack = genValueInBack(rt, info);
		if (operator == null) {
			info.mergeNode();
			info.addPlaceHolder(new PlaceHolder(comment, valueInBack));
		} else {
			Node node = info.fixNode();
			info.addPlaceHolder(new ConditionPlaceHolder(node, comment,
					isPositive, operator, valueInBack));
		}
	}

	private void findCommentEnd(RegexpTokenizer rt, LineInfo info) {
		while (rt.find(COMMENT)) {
			if (rt.matcher().group().equals("*/"))
				return; // normal end
			else if (rt.matcher().group().equals("/*"))
				// in case nested '/*' is detected
				findCommentEnd(rt, info);
			else
				// in case CRLF is detected
				info.addLineNo();
		}
		throw new ClioneFormatException(mkStringByCRLF(
				"SQL Format Error: too much '/*'", getResourceInfo()));
	}

	private INode genValueInBack(RegexpTokenizer rt, LineInfo info) {
		INode valueInBack = null;
		char c = rt.getNextChar();
		switch (c) {
		case '\'':
		case '"':
			rt.forward().updateTokenPosition();
			info.push();
			doString(rt, info, "" + c);
			valueInBack = new StrNode(info.nodeSb.toString());
			info.pop();
			break;
		case '(':
			rt.forward().updateTokenPosition();
			info.push();
			doParenthesis(rt, info);
			ParenthesisPlaceHolder holder = (ParenthesisPlaceHolder) info.node.holders
					.get(0);
			valueInBack = holder.sqlNode();
			info.pop();
			break;
		default:
			if (rt.startsWith(NORMAL)) {
				valueInBack = new StrNode(rt.matcher().group());
				rt.updateTokenPosition();
			}
		}
		return valueInBack;
	}

	// find end parenthesis and try to parse as SQLNode.
	private void doParenthesis(RegexpTokenizer rt, LineInfo info) {
		List<LineNode> flatList = new ArrayList<LineNode>();
		info.push();
		if (parseFunc(flatList, rt, info))
			throw new ClioneFormatException(mkStringByCRLF(
					"SQL Format Error: too much '('", getResourceInfo()));
		info.pop();
		info.addPlaceHolder(new ParenthesisPlaceHolder(parseIndent(flatList)));
	}

	// find end string literal.
	private void doString(RegexpTokenizer rt, LineInfo info, final String type) {
		info.nodeSb.append(type);
		if (!rt.find(type))
			throw new ClioneFormatException(mkStringByCRLF(
					"SQL Format Error: [" + type + "] unmatched!",
					getResourceInfo()));
		String literal = rt.nextTokenDelim();
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
	private void doLineComment(RegexpTokenizer rt, LineInfo info) {
		rt.find(LINEEND);
		String comment = rt.matcher().group(1);
		if (isEmpty(comment) || isAllSpace(comment)) {
			info.addLineNo(); // because find the line end.
			return;
		} else if (comment.startsWith(" ")
				&& "$@&?#%'\":|".contains(comment.substring(1, 2))) {
			info.addPlaceHolder(new PlaceHolder(comment, (String) null));
		}
		rt.backward(rt.matcher().group(2).length()); // ready for next
		rt.updateTokenPosition();
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
