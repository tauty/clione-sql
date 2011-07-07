package tetz42.clione.parsar;

import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.parsar.ParsarUtil.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.exception.WrapException;
import tetz42.clione.io.IOUtil;
import tetz42.clione.io.LineReader;
import tetz42.clione.node.LineNode;
import tetz42.clione.node.PlaceHolder;
import tetz42.clione.node.SQLNode;
import tetz42.clione.parsar.ParsarUtil.NodeHolder;
import tetz42.clione.setting.Config;
import tetz42.clione.util.SBHolder;
import tetz42.util.ObjDumper4j;

public class SQLParserSample {

	public static void main(String args[]) {
		System.out.println("---------------------------");
		System.out.println(ObjDumper4j.dumper(new SQLParserSample("1111")
				.parse("tako'\r\nika\r\n'namako")));
	}

	private static final String COMMENT = "COMMNET";
	private static final String LINEEND = "LINEEND";

	private static final Pattern divPtn = Pattern
			.compile("/\\*|\\*/|--|'|(\r|\n|\r\n)");
	private static final Pattern commentPtn = Pattern.compile("/\\*|\\*/");
	private static final Pattern lineEndPtn = Pattern.compile("(.*)$",
			Pattern.MULTILINE);
	private static final Pattern indentPtn = Pattern.compile("\\A(\\s+)");
	private static final Pattern closePtn = Pattern.compile("\\A\\s*\\)");

	private String resourceInfo = null;

	private List<LineNode> parseFunction2(String src) {
		List<LineNode> flatList = new ArrayList<LineNode>();
		MatcherHolder mh = new MatcherHolder(src, divPtn)
				.bind(COMMENT, commentPtn).bind(LINEEND, lineEndPtn).remember();
		LineInfo info = new LineInfo(1);
		while (mh.find()) {
			String str = mh.get().group();
			if (str.equals("*/")) {
				throw new ClioneFormatException(joinByCrlf(
						"SQL Format Error: too much '*/'", getResourceInfo()));
			} else if (str.equals("--")) {
				info.lineNo++;
				if (!doLineComment(mh, info))
					continue;
			} else if (str.equals("/*")) {
				// find end comment and try to parse as function.
				info.sb.append(mh.getRememberdToStart());
				// :
				mh.remember();
			} else if (str.equals("'")) {
				// find end string literal.
			} else {
				// fix line node and add to flatList
			}
			info.lineNode.sql = info.sb.toString();
			flatList.add(info.lineNode);
			info.clear();
		}

		return flatList;
	}

	private static class LineInfo {
		private LineNode lineNode;
		private StringBuilder sb;
		private int lineNo;

		LineInfo(int lineNo) {
			this.lineNode = new LineNode(lineNo);
			this.sb = new StringBuilder();
			this.lineNo = lineNo;
		}

		void clear() {
			this.lineNode = new LineNode(lineNo);
			this.sb.setLength(0);
		}
	}

	private boolean doLineComment(MatcherHolder mh, LineInfo info) {
		info.sb.append(mh.getRememberdToStart());
		mh.find(LINEEND);
		String s = mh.get().group(1);
		if (isEmpty(s) || isAllSpace(s))
			return false;
		if (s.startsWith(" ") && "$@&?#%'\":|".contains(s.substring(1, 2))) {
			info.lineNode.holders
					.add(new PlaceHolder(s, null, info.sb.length()));
		}
		return true;
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

	private List<LineNode> parseFunction(String src) throws IOException {
		List<LineNode> flatList = new ArrayList<LineNode>();
		MatcherHolder mh = new MatcherHolder(src, divPtn);
		LineReader br = null;
		SBHolder sbh = new SBHolder();
		LineNode lineNode = null;
		String line;
		while (null != (line = br.readLine())) {
			if (lineNode == null)
				lineNode = new LineNode(br.getStartLineNo(), br.getEndLineNo());
			else
				lineNode.curLineNo(br.getCurLineNo());
			sbh.append(line);
			Matcher m = divPtn.matcher(line);
			while (m.find()) {
				if (m.group().equals("*/"))
					throw new ClioneFormatException(
							"SQL Format Error: too much '*/'" + CRLF + line
									+ CRLF + getResourceInfo());
				if (m.group().equals("--")) {
					if (CRLF.equals(nextStr(line, m.end(), CRLF.length())))
						continue; // '--' join is responsibility of LineReader.
					String s = nextStr(line, m.end(), 2);
					if (s != null && s.startsWith(" ")
							&& "$@&?#%'\":|".contains(s.substring(1))) {
						// create place holder
						lineNode.holders.add(new PlaceHolder(line.substring(m
								.end()), null, m.start()));
						sbh.delete(m.start(), sbh.sb.length());
					}
					break;
				}
				int start = sbh.getPreLength() + m.start();
				m = findCommentEnd(br, sbh, m, lineNode);
				int end = sbh.getPreLength() + m.end();
				String commentFlg = sbh.sb.substring(start + 2, start + 3);
				if ("*".equals(commentFlg))
					sbh.delete(start, end);
				else if (!"+!".contains(commentFlg)) {
					// create place holder
					String valueInBack = getValueInBack(sbh.sb.substring(end),
							br, sbh);
					lineNode.holders.add(new PlaceHolder(sbh.sb.substring(
							start + 2, end - 2), valueInBack, start));
					if (valueInBack == null) {
						sbh.delete(start, end);
					} else {
						int length = valueInBack.length();
						sbh.delete(start, end + length);
						// skip searching on valueInBack;
						int lastLineLength = sbh.sb.length()
								- sbh.getPreLength();
						m.region(m.end() + length, lastLineLength);
					}
				}
			}
			lineNode.sql = sbh.sb.toString();
			flatList.add(lineNode);
			lineNode = null;
			sbh.clear();
		}

		return flatList;
	}

	private Matcher findCommentEnd(LineReader br, SBHolder sbh, Matcher m,
			LineNode lineNode) throws IOException {
		while (m.find()) {
			if (m.group().equals("*/"))
				return m;
			if (m.group().equals("--"))
				continue;
			// in case nested '/*' has detected
			m = findCommentEnd(br, sbh, m, lineNode);
		}
		String line = br.readLine();
		if (line == null)
			throw new ClioneFormatException("SQL Format Error: too match '/*'"
					+ CRLF + getResourceInfo());
		lineNode.curLineNo(br.getCurLineNo());
		sbh.append(CRLF).append(line);
		return findCommentEnd(br, sbh, divPtn.matcher(line), lineNode);
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
