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

import static tetz42.clione.parsar.ParsarUtil.*;
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
import tetz42.clione.parsar.ParsarUtil.NodeHolder;
import tetz42.clione.setting.Setting;
import tetz42.clione.util.SBHolder;

public class SQLParser {

	private static final Pattern commentPtn = Pattern.compile("/\\*|\\*/|--");
	private static final Pattern indentPtn = Pattern.compile("\\A(\\s+)");
	private static final Pattern closePtn = Pattern.compile("\\A\\s*\\)");

	private String resourceInfo = null;

	public SQLParser(String resourceInfo) {
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
		List<LineNode> wholeNodeList = parseFunction(reader);
		return parseIndent(wholeNodeList);
	}

	private List<LineNode> parseFunction(Reader reader) throws IOException {
		List<LineNode> flatList = new ArrayList<LineNode>();
		LineReader br = new LineReader(reader);
		SBHolder sbh = new SBHolder();
		LineNode lineNode = null;
		String line;
		while (null != (line = br.readLine())) {
			if (lineNode == null)
				lineNode = new LineNode(br.getStartLineNo(), br.getEndLineNo());
			else
				lineNode.curLineNo(br.getCurLineNo());
			sbh.append(line);
			Matcher m = commentPtn.matcher(line);
			while (m.find()) {
				if (m.group().equals("*/"))
					throw new ClioneFormatException(
							"SQL Format Error: too much '*/'" + CRLF + line
									+ CRLF + getResourceInfo());
				if (m.group().equals("--")) {
					if (CRLF.equals(nextStr(line, m.end(), CRLF.length())))
						continue; // '--' join is responsibility of LineReader.
					if ("- *+!".contains(nextChar(line, m.end())))
						break; // '---', and so on, means normal comment
					// create place holder
					lineNode.holders.add(new PlaceHolder(
							line.substring(m.end()), null, m.start()));
					sbh.delete(m.start(), sbh.sb.length());
					break;
				}
				int start = sbh.getPreLength() + m.start();
				m = findCommentEnd(br, sbh, m, lineNode);
				int end = sbh.getPreLength() + m.end();
				if (!"*+!".contains(sbh.sb.substring(start + 2, start + 3))) {
					// create place holder
					String valueInBack = getValueInBack(sbh.sb.substring(end));
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
		return findCommentEnd(br, sbh, commentPtn.matcher(line), lineNode);
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

			if (indent.length() < curIndent.length()) {
				holder.back();
				if (parentNode == null) {
					// performed only 1st loop time.
					indent = curIndent;
					continue;
				}
				parentNode.childBlocks.addAll(buildNodes(holder, curIndent));
				continue;
			} else if (indent.length() > curIndent.length()
					&& !closePtn.matcher(node.sql).find()) {
				holder.back();
				return list;
			}

			list.add(parentNode = node);
		}
		return list;
	}

}
