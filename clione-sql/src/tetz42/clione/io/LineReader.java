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
package tetz42.clione.io;

import static tetz42.clione.util.ClioneUtil.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineReader extends BufferedReader {

	private static final Pattern joinPtn = Pattern.compile("--\\s*\\z");
	private static final Pattern blankPtn = Pattern.compile("\\A\\s*\\z");

	int startNo = 0;
	int curNo = 0;

	public LineReader(Reader in) {
		super(in);
	}

	@Override
	public String readLine() throws IOException {
		curNo++;
		startNo = curNo;
		StringBuilder sb = new StringBuilder();
		String line;
		while (null != (line = super.readLine())) {
			if (blankPtn.matcher(line).matches()) {
				curNo++;
				continue;
			}

			sb.append(line);
			Matcher m = joinPtn.matcher(line);
			if (m.find() && m.start() == line.indexOf("--")) {
				curNo++;
				sb.append(CRLF);
			} else {
				break;
			}
		}

		return sb.length() == 0 ? null : sb.toString();
	}

	public int getStartLineNo() {
		return startNo;
	}

	public int getEndLineNo() {
		return startNo == curNo ? 0 : curNo;
	}

	public int getCurLineNo() {
		return curNo;
	}
}
