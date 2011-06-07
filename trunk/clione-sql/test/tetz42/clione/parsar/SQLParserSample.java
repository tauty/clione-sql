package tetz42.clione.parsar;

import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.clione.util.ContextUtil.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.exception.WrapException;
import tetz42.clione.io.LineReader;
import tetz42.clione.node.LineNode;
import tetz42.clione.node.PlaceHolder;
import tetz42.clione.node.SQLNode;
import tetz42.clione.setting.Setting;
import tetz42.util.ObjDumper4j;

public class SQLParserSample {
	private static final Pattern ptn = Pattern.compile("\\A(\\s+).*",
			Pattern.DOTALL);

	private String resourceInfo = null;

	public static void main(String args[]) {
		new SQLParserSample("1111").parse("aaaa/* bbb */ccc");
		new SQLParserSample("2222").parse(new StringBuilder().append(
				"aaa/*bbbccc").append(CRLF).append("dddd*/eeee").toString());
		new SQLParserSample("3333").parse(new StringBuilder().append(
				"aaa/*bbbccc").append(CRLF).append("dddd*/eeee/* tako */ aaa")
				.toString());
		new SQLParserSample("4444").parse(new StringBuilder().append(
				"aaa/*bbbccc").append(CRLF).append("dddd*/eeee/*! tako */ aaa")
				.toString());
	}

	public SQLParserSample(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public SQLNode parse(String s) {
		return parse(new ByteArrayInputStream(s.getBytes()));
	}

	public SQLNode parse(InputStream in) {
		InputStreamReader ir;
		try {
			ir = new InputStreamReader(in, Setting.instance().get(
					"sqlfile-encoding", "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new WrapException(e.getMessage() + CRLF
					+ "The setting of 'clione.properties' might be wrong. "
					+ "The key name = 'sqlfile-encoding'", e);
		}
		try {
			pushResouceInfo(resourceInfo);
			try {
				return parseRoot(ir);
			} finally {
				ir.close();
				in.close();
			}
		} catch (IOException e) {
			throw new WrapException(e.getMessage() + CRLF + resourceInfo, e);
		} finally {
			popResourceInfo();
		}
	}

	private SQLNode parseRoot(Reader reader) throws IOException {
		List<LineNode> flatList = parseFunction(reader);
		System.out.println(ObjDumper4j.dumper(flatList));
		return convToSqlNode(flatList);
	}

	private static final Pattern commentPtn = Pattern.compile("/\\*|\\*/|--");
	private static final Pattern joinPtn = Pattern.compile("\\s*\\z");
	private static final Pattern blankPtn = Pattern.compile("\\A\\s*\\z");

	private List<LineNode> parseFunction(Reader reader) throws IOException {
		List<LineNode> flatList = new ArrayList<LineNode>();
		LineReader br = new LineReader(reader);
		StringBuilder sb = new StringBuilder();
		LineNode lineNode = null;
		String line;
		OUTER: while (null != (line = br.readLine())) {
			System.out.println(line);
			if (blankPtn.matcher(line).matches())
				continue;
			if (lineNode == null)
				lineNode = new LineNode(br.getLineNo());
			else
				lineNode.curLineNo(br.getLineNo());
			sb.append(line);
			Matcher m = commentPtn.matcher(line);
			int endLineLength = 0;
			while (m.find()) {
				if (m.group().equals("*/"))
					throw new ClioneFormatException(
							"SQL Format Error: too much '*/'" + CRLF + line
									+ CRLF + getResourceInfo());
				if (m.group().equals("--")) {
					if (joinPtn.matcher(line).find(m.end())) { // join pattern
						sb.append(CRLF);
						continue OUTER;
					}
					if ("- *+!".contains(nextChar(line, m.end())))
						break; // '---' means normal comment
					// clione function
					lineNode.holders.add(new PlaceHolder(line.substring(m.end()), null, m.start()));
					sb.delete(m.start(), sb.length());
					break;
				}
				int start = endLineLength + m.start();
				int firstLength = sb.length();
				m = findCommentEnd(br, sb, m, lineNode);
				int end = endLineLength + m.end();
				if (firstLength < sb.length()) {
					endLineLength = sb.lastIndexOf(CRLF) + CRLF.length();
					end = endLineLength + m.end();
				}
				if (!"*+!".contains(sb.substring(start + 2, start + 3))) {
					// clione function!
					getValueInBack();
//					lineNode.holders.add(new PlaceHolder(line.substring(start + 2, end -2), getValueInBack(), start));
					sb.delete(start, end);
				}
				System.out.println(sb.substring(start, end));
			}
			lineNode.sql = sb.toString();
			flatList.add(lineNode);
			lineNode = null;
			sb.setLength(0);
		}

		return flatList;
	}

	private Matcher findCommentEnd(LineReader br, StringBuilder sb, Matcher m,
			LineNode lineNode) throws IOException {
		while (m.find()) {
			if (m.group().equals("*/"))
				return m;
			if (m.group().equals("--"))
				continue;
			// in case nested '/*' has detected
			findCommentEnd(br, sb, m, lineNode);
		}
		String line = br.readLine();
		System.out.println(line);
		if (line == null)
			throw new ClioneFormatException("SQL Format Error: too match '/*'"
					+ CRLF + getResourceInfo());
		lineNode.curLineNo(br.getLineNo());
		sb.append(CRLF).append(line);
		return findCommentEnd(br, sb, commentPtn.matcher(line), lineNode);
	}

	private SQLNode convToSqlNode(List<LineNode> flatList) {
		// TODO Auto-generated method stub
		return null;
	}

	private SQLNode parse(Reader reader) {
		LineReader br = new LineReader(reader);
		List<LineNode> resultList = new ArrayList<LineNode>();
		List<LineNode> list;
		try {
			try {
				do {
					resultList.addAll(list = buildNodes(br, ""));
				} while (list.size() != 0);
			} finally {
				br.close();
			}
		} catch (IOException e) {
			throw new WrapException(e.getMessage() + CRLF + resourceInfo, e);
		}
		// if (this.commentDepth != 0)
		// throw new ClioneFormatException("SQL Format Error: too match '/*'"
		// + CRLF + resourceInfo);
		SQLNode sqlNode = new SQLNode();
		sqlNode.nodes = resultList;
		return sqlNode;
	}

	private List<LineNode> buildNodes(LineReader br, String indent)
			throws IOException {
		ArrayList<LineNode> list = new ArrayList<LineNode>();

		String line;
		LineNode block = null;
		while (null != (line = br.readLine())) {
			Matcher m = ptn.matcher(line);
			String curIndent = m.matches() ? m.group(1) : "";

			if (indent.length() < curIndent.length()) {
				// br.backLine();
				if (block == null) {
					// performed only 1st loop time.
					indent = curIndent;
					continue;
				}
				block.childBlocks.addAll(buildNodes(br, curIndent));
				continue;
			} else if (indent.length() > curIndent.length()
					&& !line.matches("\\s*\\).*")) {
				// br.backLine();
				return list;
			}

			list.add(block = genNode(line));
		}
		return list;
	}

	private LineNode genNode(String line) {
		return null;
	}

	int wordEnd(StringBuilder sb, int fromIndex) {
		int pos = fromIndex;
		while (pos < sb.length() && isWordChar(sb.charAt(pos)))
			pos++;
		return pos;
	}

	private boolean isWordChar(char c) {
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

	private String nextChar(String src, int pos) {
		if (pos >= src.length())
			return null;
		return src.substring(pos, pos + 1);
	}
}
