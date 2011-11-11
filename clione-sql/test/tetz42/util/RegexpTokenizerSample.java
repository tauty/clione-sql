package tetz42.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpTokenizerSample {

	private final Map<String, Matcher> matcherMap = new HashMap<String, Matcher>();
	private final List<Status> statusList = new ArrayList<Status>();
	private final Matcher baseMatcher;
	private final String src;

	private Matcher matcher;
	private int statusPos;
	private int rememberedStart = 0;
	private int rememberedEnd = 0;

	private class Status {
		private int pos = 0;
		private int start = 0;
		private int end = 0;
	}

	public RegexpTokenizerSample(String src, Pattern ptn) {
		this.matcher = this.baseMatcher = ptn.matcher(src);
		this.src = src;
		this.statusList.add(new Status());
		this.statusPos = 0;
	}

	private Status status() {
		return status(statusPos);
	}

	private Status status(int statusPos) {
		if (statusList.size() <= statusPos)
			return null;
		return statusList.get(statusPos);
	}

	public RegexpTokenizerSample bind(String key, Pattern ptn) {
		matcherMap.put(key, ptn.matcher(src));
		return this;
	}

	public boolean hasNext() {
		return hasNext(baseMatcher);
	}

	public boolean hasNext(String key) {
		Matcher m = matcherMap.get(key);
		if (m == null)
			throw new NullPointerException("Unknown Parameter '" + key
					+ "' has passed!");
		return hasNext(m);
	}

	public boolean startsWith() {
		return startsWith(baseMatcher);
	}

	public boolean startsWith(String key) {
		Matcher m = matcherMap.get(key);
		if (m == null)
			throw new NullPointerException("Unknown Parameter '" + key
					+ "' has passed!");
		return startsWith(m);
	}

	public Matcher get() {
		return matcher;
	}

	public RegexpTokenizerSample forward() {
		return forward(1);
	}

	private RegexpTokenizerSample forward(int i) {
		Status status = status();
		status.pos += i;
		return this;
	}

	public RegexpTokenizerSample back() {
		return back(1);
	}

	public RegexpTokenizerSample back(int i) {
		Status status = status();
		status.pos -= i;
		return this;
	}

	public RegexpTokenizerSample setNextStatus() {
		Status status = status();
		rememberedStart = status.start;
		rememberedEnd = status.end;
		return this;
	}

	public boolean historyBack() {
		if (statusPos < 1)
			return false;
		statusPos--;
		return true;
	}

	public boolean historyForward() {
		if (statusPos >= statusList.size() - 1)
			return false;
		statusPos++;
		return true;
	}

	public String nextToken() {
		String result = getToken();
		setNextStatus();
		return result;
	}

	public String getToken() {
		return src.substring(rememberedEnd, status().start);
	}

	public String nextTokenDelim() {
		String result = getTokenDelim();
		setNextStatus();
		return result;
	}

	public String getTokenDelim() {
		return src.substring(rememberedEnd, status().end);
	}

	public String nextDelimToken() {
		String result = getDelimToken();
		setNextStatus();
		return result;
	}

	public String getDelimToken() {
		return src.substring(rememberedStart, status().start);
	}

	public String nextDelimTokenDelim() {
		String result = getDelimTokenDelim();
		setNextStatus();
		return result;
	}

	public String getDelimTokenDelim() {
		return src.substring(rememberedStart, status().end);
	}

	public boolean isEnd() {
		return rememberedEnd >= src.length();
	}

	public String getSrc() {
		return this.src;
	}

	public char getNextChar() {
		if (status().pos >= src.length()) {
			return 0;
		}
		return src.charAt(status().pos);
	}

	private boolean hasNext(Matcher m) {
		matcher = m;
		if (status().pos >= src.length())
			return false;
		Status status = new Status();
		if (m.find(status().pos)) {
			status.start = m.start();
			status.pos = status.end = m.end();
			if (status().pos == status.pos)
				status.pos++;
		} else {
			status.start = src.length();
			status.pos = status.end = src.length();
		}
		statusPos++;
		if (statusPos == statusList.size())
			statusList.add(status);
		else {
			statusList.set(statusPos, status);
		}
		return true;
	}

	private boolean startsWith(Matcher m) {
		matcher = m;
		Status status = status();
		if (status.pos >= src.length())
			return false;
		if (!m.find(status.pos))
			return false;
		if (status.pos != m.start())
			return false;
		status.pos = status.end = m.end();
		return true;
	}
}
