package tetz42.clione.parsar;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherHolder {

	private final Map<String, Matcher> matcherMap = new HashMap<String, Matcher>();
	private final Matcher matcher;
	private final String src;

	private int pos = 0;
	private int start = 0;
	private int end = 0;
	private int prePos = 0;
	private int remembered = 0;
	private boolean isEnd = false;

	public MatcherHolder(String src, Pattern ptn) {
		this.matcher = ptn.matcher(src);
		this.src = src;
	}

	public MatcherHolder bind(String key, Pattern ptn) {
		matcherMap.put(key, ptn.matcher(src));
		return this;
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

	public Matcher get() {
		return matcher;
	}

	public Matcher get(String key) {
		Matcher m = matcherMap.get(key);
		if (m == null)
			throw new NullPointerException("Unknown Parameter '" + key
					+ "' has passed!");
		return m;
	}

	public MatcherHolder next() {
		return next(1);
	}

	private MatcherHolder next(int i) {
		pos += i;
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

	public String getRememberedToStart() {
		String result = src.substring(remembered, start);
		remember();
		return result;
	}

	public String getRememberedToStartWithoutRemember() {
		String result = src.substring(remembered, start);
		return result;
	}

	public String getRememberedToEnd() {
		String result = src.substring(remembered, end);
		remember();
		return result;
	}

	public String getRememberedToEnd(int minusFromStart) {
		String result = src.substring(remembered - minusFromStart, end);
		remember();
		return result;
	}

	private boolean find(Matcher m) {
		if(isEnd)
			return false;
		prePos = pos;
		boolean result = m.find(pos);
		this.start = m.start();
		pos = end = m.end();
		if (prePos == pos)
			pos++;
		if(end >= src.length())
			isEnd = true;
		return result;
	}

}
