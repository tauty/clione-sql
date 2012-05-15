/*
 * Copyright 2012 tetsuo.ohta[at]gmail.com
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
package tetz42.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import tetz42.clione.exception.ClioneFormatException;

public class HereDoc {

	private static Pattern ptn = Pattern.compile("^<<([^<>]+)>>(\r\n|\r|\n)?",
			Pattern.MULTILINE);

	public static Map<String, String> get(Class<?> clazz) {
		return get(clazz.getResourceAsStream(clazz.getSimpleName() + ".txt"));
	}

	public static Map<String, String> get(Class<?> clazz, String encoding) {
		return get(clazz.getResourceAsStream(clazz.getSimpleName() + ".txt"), encoding);
	}

	public static Map<String, String> get(InputStream in) {
		return parse(IOUtil.toString(in));
	}

	public static Map<String, String> get(InputStream in, String encoding) {
		return parse(IOUtil.toString(in, encoding));
	}

	private static Map<String, String> parse(String s) {
		Map<String, String> map = new HashMap<String, String>();
		RegexpTokenizer mh = new RegexpTokenizer(s, ptn);
		while (mh.find()) {
			String key = mh.matcher().group(1);
			mh.updateTokenPosition();
			Pattern endPtn = Pattern.compile("(\r\n|\r|\n)?<</" + key + ">>",
					Pattern.MULTILINE);
			mh.bind("END", endPtn);
			if (!mh.find("END"))
				throw new ClioneFormatException("The tag, <<" + key
						+ ">>, must be closed by <</" + key + ">>!");
			String val = mh.nextToken();
			map.put(key, val);
		}
		return map;
	}

}
