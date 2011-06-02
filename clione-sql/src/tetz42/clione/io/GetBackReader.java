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
import java.util.regex.Pattern;

public class GetBackReader extends BufferedReader {

	private static final Pattern joinPtn = Pattern.compile("--\\s*\\z");
	private static final Pattern blankPtn = Pattern.compile("\\A\\s*\\z");

	int lineNo = 0;
	String preLine;
	String nextLine;

	public GetBackReader(Reader in) {
		super(in);
	}

	@Override
	public String readLine() throws IOException {
		lineNo++;
		if (nextLine != null) {
			String s = nextLine;
			nextLine = null;
			return s;
		}

		StringBuilder sb = new StringBuilder();
		String line;
		while (null != (line = super.readLine())) {
			if (blankPtn.matcher(line).matches()) {
				lineNo++;
				continue;
			}

			sb.append(line);
			if (joinPtn.matcher(line).find()) {
				lineNo++;
				sb.append(CRLF);
			} else {
				break;
			}
		}

		return preLine = sb.length() == 0 ? null : sb.toString();
	}

	public void backLine() {
		nextLine = preLine;
		lineNo--;
	}
	
	public int getLineNo(){
		return lineNo;
	}
}
