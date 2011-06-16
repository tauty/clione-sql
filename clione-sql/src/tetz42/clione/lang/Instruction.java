package tetz42.clione.lang;

import static tetz42.clione.util.ClioneUtil.*;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.util.ParamMap;

public class Instruction {

	/**
	 * In case params.size is one and replacement is null -> '?' and bind params<br>
	 * In case params.size is three and replacement is null -> '?, ?, ?' and
	 * bind params<br>
	 * In case params is null and replacement is 'aaa' -> 'aaa' <br>
	 * In case params.size is two and replacement is 'CONCAT(?, ''%'', ?)' ->
	 * 'CONCAT(?, ''%'', ?)' and bind params<br>
	 */
	public List<Object> params = new ArrayList<Object>();
	public boolean isNodeDisposed = false;
	public boolean doNothing = false;
	public boolean useValueInBack = false;
	public String replacement;
	public Instruction next = null;
	public ParamMap map = null;

	public Instruction nodeDispose() {
		return this.nodeDispose(true);
	}

	public Instruction nodeDispose(boolean isNodeDisposed) {
		this.isNodeDisposed = isNodeDisposed;
		return this;
	}

	public Instruction doNothing() {
		this.doNothing = true;
		return this;
	}

	public Instruction next(Instruction next) {
		this.next = next;
		return this;
	}

	public Instruction replacement(String replacement) {
		this.replacement = replacement;
		return this;
	}

	public Instruction useValueInBack() {
		this.useValueInBack = true;
		return this;
	}

	public String getReplacement() {
		return replacement != null ? replacement : genQuestions();
	}

	public Instruction clearParams() {
		this.params.clear();
		return this;
	}

	public Instruction clearNext() {
		Instruction next = this.next;
		this.next = null;
		return next;
	}

	public Instruction $(String key, Object value) {
		if (map == null)
			map = new ParamMap();
		map.put(key, value);
		return this;
	}

	public Instruction merge() {
		if (next != null) {
			this.merge(next.merge());
			next = null;
		}
		return this;
	}

	public Instruction merge(Instruction another) {
		params.addAll(another.params);
		if (!isNodeDisposed) // true win
			isNodeDisposed = another.isNodeDisposed;
		if (doNothing) // false win
			doNothing = another.doNothing;
		if (useValueInBack) // false win
			useValueInBack = another.useValueInBack;
		if (replacement != null || another.replacement != null) {
			String repOne = this.getReplacement();
			String repAno = another.getReplacement();
			if (repOne.endsWith("?") && repAno.startsWith("?"))
				replacement = repOne + ", " + repOne;
			else
				replacement = repOne + (isEmpty(repOne) ? "" : " ") + repAno;
		}
		if (map == null)
			map = another.map;
		else if (another.map != null)
			map.putAll(another.map);
		return this;
	}

	public String genQuestions() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			if (i != 0)
				sb.append(", ");
			sb.append("?");
		}
		return sb.toString();
	}
}