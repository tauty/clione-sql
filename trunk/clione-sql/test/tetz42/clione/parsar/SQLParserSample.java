package tetz42.clione.parsar;

import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.parsar.ParsarUtil.*;

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
import tetz42.clione.parsar.ParsarUtil.NodeHolder;
import tetz42.clione.setting.Config;
import tetz42.clione.util.SBHolder;
import tetz42.util.ObjDumper4j;

public class SQLParserSample {

	private static final Pattern commentPtn = Pattern.compile("/\\*|\\*/|--");
	private static final Pattern joinPtn = Pattern.compile("$",
			Pattern.MULTILINE);
	private static final Pattern indentPtn = Pattern.compile("\\A(\\s+)");
	private static final Pattern closePtn = Pattern.compile("\\A\\s*\\)");

	private String resourceInfo = null;

	public static void main(String args[]) {
		new SQLParserSample("1111").parse("aaaa/* bbb */ccc");
		new SQLParserSample("2222").parse(new StringBuilder()
				.append("aaa/*bbbccc").append(CRLF).append("dddd*/eeee")
				.toString());
		new SQLParserSample("3333").parse(new StringBuilder()
				.append("aaa/*bbbccc").append(CRLF)
				.append("dddd*/eeee/* tako */ aaa").toString());
		new SQLParserSample("4444").parse(new StringBuilder()
				.append("aaa/*bbbccc").append(CRLF)
				.append("dddd*/eeee/*! tako */ aaa").toString());
		new SQLParserSample("5555").parse(new StringBuilder()
				.append("aaa/*bbbccc").append(CRLF)
				.append("dddd*/'tako*/ika' eeee/* tako */ aaa").toString());
		new SQLParserSample("6666").parse(new StringBuilder()
				.append("aaa/*bbbccc").append(CRLF)
				.append("dddd*/'tako*/ika' eeee/* tako */is null").toString());
		new SQLParserSample("7777")
				.parse(new StringBuilder()
						.append("aaa/*bbbccc")
						.append(CRLF)
						.append("dddd*/'tako*/ika' eeee/* tako */not in ('aaa', 'b)b', 'ccc')")
						.toString());
		new SQLParserSample("8888").parse(new StringBuilder().append("WHERE")
				.append(CRLF).append("  ID = /* $ID */'912387'").append(CRLF)
				.append("  AND (").append(CRLF)
				.append("    PREF /* $PREF */= 'TOKYO'").append(CRLF)
				.append("    OR COUNTORY = /* $CONTORY */'JAPAN'").append(CRLF)
				.append("  )").toString());
	}

	public SQLParserSample(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public SQLNode parse(String s) {
		System.out.println("---------------------------");
		SQLNode sqlNode = parse(new ByteArrayInputStream(s.getBytes()));
		System.out.println(ObjDumper4j.dumper(sqlNode));
		return sqlNode;
	}

	public SQLNode parse(InputStream in) {
		InputStreamReader ir;
		try {
			ir = new InputStreamReader(in, nvl(Config.get().SQLFILE_ENCODING,
					"utf-8"));
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
		List<LineNode> wholeNodeList = parseFunction(reader);
		return parseIndent(wholeNodeList);
	}

	private List<LineNode> parseFunction(Reader reader) throws IOException {
		List<LineNode> flatList = new ArrayList<LineNode>();
		LineReader br = new LineReader(reader);
		SBHolder sb = new SBHolder();
		LineNode lineNode = null;
		String line;
		while (null != (line = br.readLine())) {
			if (lineNode == null)
				lineNode = new LineNode(br.getStartLineNo(), br.getEndLineNo());
			else
				lineNode.curLineNo(br.getCurLineNo());
			sb.append(line);
			Matcher m = commentPtn.matcher(line);
			while (m.find()) {
				if (m.group().equals("*/"))
					throw new ClioneFormatException(
							"SQL Format Error: too much '*/'" + CRLF + line
									+ CRLF + getResourceInfo());
				if (m.group().equals("--")) {
					if (joinPtn.matcher(line).find(m.end()))
						continue; // '--' join is responsibility of LineReader.
					if ("- *+!".contains(nextChar(line, m.end())))
						break; // '---', and so on, means normal comment
					// create place holder
					lineNode.holders.add(new PlaceHolder(
							line.substring(m.end()), null, m.start()));
					sb.delete(m.start(), sb.sb.length());
					break;
				}
				int start = sb.getPreLength() + m.start();
				m = findCommentEnd(br, sb, m, lineNode);
				int end = sb.getPreLength() + m.end();
				if (!"*+!".contains(sb.sb.substring(start + 2, start + 3))) {
					// create place holder
					String valueInBack = getValueInBack(sb.sb.substring(end));
					lineNode.holders.add(new PlaceHolder(sb.sb.substring(
							start + 2, end - 2), valueInBack, start));
					if (valueInBack == null) {
						sb.delete(start, end);
					} else {
						int length = valueInBack.length();
						sb.delete(start, end + length);
						// skip searching on valueInBack;
						int lastLineLength = sb.sb.length() - sb.getPreLength();
						m.region(m.end() + length, lastLineLength);
					}
				}
			}
			lineNode.sql = sb.sb.toString();
			flatList.add(lineNode);
			lineNode = null;
			sb.clear();
		}

		return flatList;
	}

	private Matcher findCommentEnd(LineReader br, SBHolder sb, Matcher m,
			LineNode lineNode) throws IOException {
		while (m.find()) {
			if (m.group().equals("*/"))
				return m;
			if (m.group().equals("--"))
				continue;
			// in case nested '/*' has detected
			m = findCommentEnd(br, sb, m, lineNode);
		}
		String line = br.readLine();
		if (line == null)
			throw new ClioneFormatException("SQL Format Error: too match '/*'"
					+ CRLF + getResourceInfo());
		lineNode.curLineNo(br.getCurLineNo());
		sb.append(CRLF).append(line);
		return findCommentEnd(br, sb, commentPtn.matcher(line), lineNode);
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

	private List<LineNode> buildNodes(NodeHolder br, String indent) {
		ArrayList<LineNode> list = new ArrayList<LineNode>();

		LineNode parentNode = null;
		LineNode node;
		while (null != (node = br.next())) {
			Matcher m = indentPtn.matcher(node.sql);
			String curIndent = m.find() ? m.group(1) : "";

			if (indent.length() < curIndent.length()) {
				br.back();
				if (parentNode == null) {
					// performed only 1st loop time.
					indent = curIndent;
					continue;
				}
				parentNode.childBlocks.addAll(buildNodes(br, curIndent));
				continue;
			} else if (indent.length() > curIndent.length()
					&& !closePtn.matcher(node.sql).find()) {
				br.back();
				return list;
			}

			list.add(parentNode = node);
		}
		return list;
	}

	private String nextChar(String src, int pos) {
		if (pos >= src.length())
			return null;
		return src.substring(pos, pos + 1);
	}
}
