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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tetz42.util.exception.NoMoreTokenException;

public class RegexpTokenizer {

	private static final int NO_MORE_TOKEN = -1;
	private final Map<String, Matcher> matcherMap = new HashMap<String, Matcher>();
	private final Matcher matcher;
	private final String src;

	private Matcher lastMatcher;
	private int start = 0;
	private int pos = 0;
	private int rememberedStart = 0;
	private int rememberedEnd = 0;

	public RegexpTokenizer(String src, Pattern ptn) {
		this.lastMatcher = this.matcher = ptn.matcher(src);
		this.src = src;
	}

	public RegexpTokenizer bind(String key, Pattern ptn) {
		matcherMap.put(key, ptn.matcher(src));
		return this;
	}

	public String getDelim() {
		if (lastMatcher == null)
			return "";
		return matcher().group();
	}

	public boolean hasNext() {
		return hasNext(matcher);
	}

	public boolean hasNext(String key) {
		return hasNext(getMatcherByKey(key));
	}

	private boolean hasNext(Matcher m) {
		if (rememberedEnd == NO_MORE_TOKEN)
			return false;
		find(m);
		return true;
	}

	public boolean find() {
		return find(matcher);
	}

	public boolean find(String key) {
		return find(getMatcherByKey(key));
	}

	public boolean startsWith() {
		return startsWith(matcher);
	}

	public boolean startsWith(String key) {
		return startsWith(getMatcherByKey(key));
	}

	private Matcher getMatcherByKey(String key) {
		Matcher m = matcherMap.get(key);
		if (m == null)
			throw new NullPointerException("Unknown Parameter '" + key
					+ "' has passed!");
		return m;
	}

	public Matcher matcher() {
		return lastMatcher;
	}

	public RegexpTokenizer forward() {
		return forward(1);
	}

	private RegexpTokenizer forward(int i) {
		pos += i;
		return this;
	}

	public RegexpTokenizer backward() {
		return backward(1);
	}

	public RegexpTokenizer backward(int i) {
		pos -= i;
		return this;
	}

	public RegexpTokenizer updateTokenPosition() {
		if (lastMatcher == null)
			// in case previous find returns false.
			rememberedStart = rememberedEnd = NO_MORE_TOKEN;
		else {
			rememberedStart = start;
			rememberedEnd = pos;
		}
		return this;
	}

	private String rememberAndValue(String value) {
		updateTokenPosition();
		return value;
	}

	public String nextToken() {
		return rememberAndValue(getToken());
	}

	private String getSubstring(int remembered, int tokenEnd) {
		if (remembered == NO_MORE_TOKEN)
			throw new NoMoreTokenException(this.src);
		return src.substring(remembered, tokenEnd);
	}

	public String getToken() {
		return getSubstring(rememberedEnd, start);
	}

	public String nextTokenDelim() {
		return rememberAndValue(getTokenDelim());
	}

	public String getTokenDelim() {
		return getSubstring(rememberedEnd, pos);
	}

	public String nextDelimTokenDelim() {
		return rememberAndValue(getDelimTokenDelim());
	}

	public String getDelimTokenDelim() {
		return getSubstring(rememberedStart, pos);
	}

	public boolean isEnd() {
		return pos >= src.length();
	}

	public char getNextChar() {
		if (isEnd())
			return 0;
		return src.charAt(pos);
	}

	private boolean find(Matcher m) {
		lastMatcher = m;
		if (isEnd() || !m.find(pos)) {
			lastMatcher = null;
			start = pos = src.length();
			return false;
		}
		start = m.start();
		pos = m.end();
		return true;
	}

	private boolean startsWith(Matcher m) {
		lastMatcher = m;
		if (isEnd() || !m.find(pos))
			return false;
		if (pos != m.start())
			return false;
		pos = m.end();
		return true;
	}
}
