package tetz42.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherHolder {

	private final Map<String, Matcher> matcherMap = new HashMap<String, Matcher>();
	private final Matcher matcher;
	private final String src;

	private Matcher lastMatcher;
	private int pos = 0;
	private int start = 0;
	private int end = 0;
	private int prePos = 0;
	private int remembered = 0;
	private boolean isEnd = false;

	public MatcherHolder(String src, Pattern ptn) {
		this.lastMatcher = this.matcher = ptn.matcher(src);
		this.src = src;
	}

	public MatcherHolder bind(String key, Pattern ptn) {
		matcherMap.put(key, ptn.matcher(src));
		return this;
	}

	public String getDelim() {
		if (lastMatcher == null)
			return "";
		return get().group();
	}

	public boolean hasNext() {
		return hasNext(matcher);
	}

	public boolean hasNext(String key) {
		Matcher m = matcherMap.get(key);
		if (m == null)
			throw new NullPointerException("Unknown Parameter '" + key
					+ "' has passed!");
		return hasNext(m);
	}

	// TODO [delim]aaa[delim]bbb[delim] -> '', 'aaa', 'bbb', ''
	private boolean hasNext(Matcher m) {
		boolean result = find(m);
		return result ? result : remembered < src.length();
	}

	public boolean find() {
		return find(matcher);
	}

	public boolean find(String key) {
		Matcher m = matcherMap.get(key);
		if (m == null)
			throw new NullPointerException("Unknown Parameter '" + key
					+ "' has passed!");
		return find(m);
	}

	public boolean startsWith() {
		return startsWith(matcher);
	}

	public boolean startsWith(String key) {
		Matcher m = matcherMap.get(key);
		if (m == null)
			throw new NullPointerException("Unknown Parameter '" + key
					+ "' has passed!");
		return startsWith(m);
	}

	public Matcher get() {
		return lastMatcher;
	}

	public MatcherHolder next() {
		return next(1);
	}

	private MatcherHolder next(int i) {
		pos += i;
		end += i;
		return this;
	}

	public MatcherHolder back() {
		return back(1);
	}

	public MatcherHolder back(int i) {
		pos -= i;
		end -= i;
		isEnd = false;
		return this;
	}

	public MatcherHolder remember() {
		remembered = end;
		return this;
	}

	public MatcherHolder historyBack() {
		pos = prePos;
		end = prePos;
		isEnd = false;
		return this;
	}

	public String nextToken() {
		String result = src.substring(remembered, start);
		remember();
		return result;
	}

	public String getToken() {
		String result = src.substring(remembered, start);
		return result;
	}

	public String nextTokenDelim() {
		String result = src.substring(remembered, end);
		remember();
		return result;
	}

	public String getTokenDelim() {
		String result = src.substring(remembered, end);
		return result;
	}

	public String getRememberedToEnd(int minusFromStart) {
		String result = src.substring(remembered - minusFromStart, end);
		remember();
		return result;
	}

	public int getRememberd() {
		return this.remembered;
	}

	public void setRememberd(int pos) {
		this.pos = this.start = this.end = this.remembered = pos;
	}

	public boolean isEnd() {
		return this.isEnd;
	}

	public String getSrc() {
		return this.src;
	}

	public char getNextChar() {
		if (pos >= src.length()) {
			return 0;
		}
		return src.charAt(pos);
	}

	private boolean find(Matcher m) {
		lastMatcher = m;
		if (isEnd)
			return false;
		prePos = pos;
		if (!m.find(pos)) {
			lastMatcher = null;
			isEnd = true;
			start = end = pos = src.length();
			return false;
		}
		this.start = m.start();
		pos = end = m.end();
		if (prePos == pos)
			pos++;
		if (end >= src.length())
			isEnd = true;
		return true;
	}

	private boolean startsWith(Matcher m) {
		lastMatcher = m;
		if (isEnd)
			return false;
		if (!m.find(pos))
			return false;
		if (this.pos != m.start())
			return false;
		pos = end = m.end();
		if (end >= src.length())
			isEnd = true;
		return true;
	}
}
