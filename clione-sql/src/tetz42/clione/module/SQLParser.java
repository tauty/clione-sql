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
package tetz42.clione.module;

import static tetz42.clione.module.GetBackReader.*;
import static tetz42.clione.module.ParamMap.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLParser {

	private static final Pattern ptn = Pattern.compile("\\A(\\s+).*",
			Pattern.DOTALL);
	private static final Pattern commentPtn = Pattern.compile("/\\*|\\*/");

	public List<LineTree> parse(InputStream in) {
		InputStreamReader ir = new InputStreamReader(in);
		GetBackReader br = new GetBackReader(ir);
		List<LineTree> resultList = new ArrayList<LineTree>();
		List<LineTree> list;
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
			throw new WrapException(e.getMessage(), e);
		}
		if (this.commentDepth != 0)
			throw new SQLFormatException("SQL Format Error: too match '/*'");
		return resultList;
	}

	private List<LineTree> buildBlock(GetBackReader br, String indent)
			throws IOException {
		ArrayList<LineTree> list = new ArrayList<LineTree>();

		String line;
		LineTree block = null;
		while (null != (line = br.readLine())) {
			Matcher m = ptn.matcher(line);
			String curIndent = m.matches() ? m.group(1) : "";

			if (indent.length() < curIndent.length()) {
				br.backLine();
				if (block == null) {
					// buildからの呼び出し時のループ初回以外では発生しない。
					// curIndentを切り替えてやり直し。
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

	private LineTree genBlock(String line) {
		int pos = 0;
		LineTree block = new LineTree();
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
					throw new SQLFormatException(key
							+ " must have default element.\n" + line);
				}
				if (block.sql.charAt(end) == '\'') {
					pos = replace(block, begin,
							block.sql.indexOf("'", end + 1) + 1, "?");
				} else if (block.sql.charAt(end) == '(') {
					pos = replace(block, begin,
							block.sql.indexOf(")", end + 1) + 1, "(?)");
				} else if (isWordChar(block.sql.charAt(end))) {
					pos = replace(block, begin, wordEnd(block.sql, end), "?");
				} else {
					throw new SQLFormatException(key
							+ " must have default element.\n" + line);
				}

			} else if (this.commentDepth != 0) {
				this.commentDepth--;
			} else {
				throw new SQLFormatException("SQL Format Error: too much '*/'"
						+ CRLF + line + CRLF + pos);
			}
		}
		return block;
	}

	private int replace(LineTree block, int begin, int end, String question) {
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
