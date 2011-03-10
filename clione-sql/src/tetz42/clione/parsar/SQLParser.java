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

import static tetz42.clione.util.ParamMap.*;
import static tetz42.clione.util.ClioneUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.exception.WrapException;
import tetz42.clione.io.GetBackReader;
import tetz42.clione.node.LineNode;
import tetz42.clione.setting.Setting;

public class SQLParser {

	private static final Pattern ptn = Pattern.compile("\\A(\\s+).*",
			Pattern.DOTALL);
	private static final Pattern commentPtn = Pattern.compile("/\\*|\\*/");

	private String resourceInfo = null;

	public SQLParser(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public List<LineNode> parse(InputStream in) {
		InputStreamReader ir;
		try {
			ir = new InputStreamReader(in, Setting.instance().get(
					"sqlfile-encoding", "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new WrapException(e.getMessage() + CRLF
					+ "The setting of 'clione.properties' might be wrong. "
					+ "The key name = 'sqlfile-encoding'", e);
		}
		GetBackReader br = new GetBackReader(ir);
		List<LineNode> resultList = new ArrayList<LineNode>();
		List<LineNode> list;
		try {
			try {
				do {
					resultList.addAll(list = buildBlock(br, ""));
				} while (list.size() != 0);
			} finally {
				br.close();
				ir.close();
				in.close();
			}
		} catch (IOException e) {
			throw new WrapException(e.getMessage() + CRLF + resourceInfo, e);
		}
		if (this.commentDepth != 0)
			throw new ClioneFormatException("SQL Format Error: too match '/*'"
					+ CRLF + resourceInfo);
		return resultList;
	}

	private List<LineNode> buildBlock(GetBackReader br, String indent)
			throws IOException {
		ArrayList<LineNode> list = new ArrayList<LineNode>();

		String line;
		LineNode block = null;
		while (null != (line = br.readLine())) {
			Matcher m = ptn.matcher(line);
			String curIndent = m.matches() ? m.group(1) : "";

			if (indent.length() < curIndent.length()) {
				br.backLine();
				if (block == null) {
					// performed only 1st loop time.
					indent = curIndent;
					continue;
				}
				block.childBlocks.addAll(buildBlock(br, curIndent));
				continue;
			} else if (indent.length() > curIndent.length()
					&& !line.matches("\\s*\\).*")) {
				br.backLine();
				return list;
			}

			list.add(block = genBlock(line));
		}
		return list;
	}

	private int commentDepth = 0;

	private LineNode genBlock(String line) {
		int pos = 0;
		LineNode block = new LineNode();
		block.sql.append(line);
		Matcher m = commentPtn.matcher(block.sql);
		while (m.find(pos)) {
			pos = m.end();
			if (m.group().equals("/*")) {
				int begin = m.start();
				if (this.commentDepth != 0) {
					// - /* - \n
					// /* - \n
					this.commentDepth++;
					continue;
				} else if (!m.find(pos)) {
					// /* - \n
					this.commentDepth++;
					break;
				} else if (m.group().equals("/*")) {
					// /* - /*
					this.commentDepth += 2;
					pos = m.end();
					continue;
				}

				// /* - */
				int end = pos = m.end();
				String comment = block.sql.substring(begin, end);
				String key = comment.substring(2, comment.length() - 2);
				Matcher keyM = KEY_PTN.matcher(key);
				if (!keyM.matches())
					continue;
				key = key.trim();
				block.keys.add(key);

				if (key.startsWith("&"))
					continue; // '&' means parameter is not replaced.

				if (end >= block.sql.length()) {
					if (!key.startsWith("?")) {
						// do not have default value
						pos = replace(block, begin, end, "?");
					} else {
						throw new ClioneFormatException(joinByCrlf("[" + key
								+ "] must have default value as follows:",
								"\t... /* " + key + " */'DEFAULT_VALUE'",
								"wrong part:", line,
								resourceInfo != null ? resourceInfo : ""));
					}
				} else if (block.sql.charAt(end) == '\'') {
					pos = replace(block, begin,
							block.sql.indexOf("'", end + 1) + 1, "?");
				} else if (block.sql.charAt(end) == '(') {
					pos = replace(block, begin,
							block.sql.indexOf(")", end + 1) + 1, "(?)");
				} else if (isWordChar(block.sql.charAt(end))) {
					pos = replace(block, begin, wordEnd(block.sql, end), "?");
				} else if (!key.startsWith("?")) {
					// do not have default value
					pos = replace(block, begin, end, "?");
				} else {
					throw new ClioneFormatException(joinByCrlf("[" + key
							+ "] must have default value as follows:",
							"\t... /* " + key + " */'DEFAULT_VALUE'",
							"wrong part:", line,
							resourceInfo != null ? resourceInfo : ""));
				}

			} else if (this.commentDepth != 0) {
				this.commentDepth--;
			} else {
				throw new ClioneFormatException(
						"SQL Format Error: too much '*/'" + CRLF + line + CRLF
								+ "position:" + pos + CRLF + resourceInfo);
			}
		}
		return block;
	}

	private int replace(LineNode block, int begin, int end, String question) {
		block.vals.add(block.sql.substring(begin, end));
		block.sql.replace(begin, end, question);
		return begin + question.length();
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
}
