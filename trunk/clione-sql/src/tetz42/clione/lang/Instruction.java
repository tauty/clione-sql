package tetz42.clione.lang;

import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.util.Util.*;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.util.ListWithDelim;
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
	public List<Object> params;
	public boolean isNodeDisposed = false;
	public boolean doNothing = false;
	public boolean useValueInBack = false;
	public String replacement;
	public Instruction next = null;
	public ParamMap map = null;
	public boolean status = false;
	public boolean isNumber = false;

	public Instruction() {
		this(new ArrayList<Object>());
	}

	public Instruction(List<Object> params) {
		this.params = params;
	}

	public Instruction number(boolean isNumber) {
		this.isNumber = isNumber;
		return this;
	}

	public Instruction asNumber() {
		return number(true);
	}

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

	public Instruction addReplacement(String str) {
		replacement = replacement + str;
		return this;
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

	public Instruction status(boolean status) {
		this.status = status;
		return this;
	}

	public boolean and() {
		if (status == false)
			return false;
		if (next == null)
			return true;
		return next.and();
	}

	public boolean or() {
		if (status == true)
			return true;
		if (next == null)
			return false;
		return next.or();
	}

	public Instruction merge() {
		if (next != null) {
			this.merge(next.merge());
			next = null;
		}
		return this;
	}

	public Instruction mergeLine(Instruction another) {
		return merge(another, true);
	}

	public Instruction merge(Instruction another) {
		return merge(another, false);
	}

	private Instruction merge(Instruction another, boolean isLine) {

		if (!ListWithDelim.class.isInstance(params)
				&& ListWithDelim.class.isInstance(another.params)) {
			// ListWithDelim win.
			params = new ListWithDelim<Object>(params)
					.copyDelim(another.params);
		}
		params.addAll(another.params);

		if (!isNodeDisposed) // true win
			isNodeDisposed = another.isNodeDisposed;
		if (doNothing) // false win
			doNothing = another.doNothing;
		if (useValueInBack) // false win
			useValueInBack = another.useValueInBack;
		if (isNumber) // false win
			isNumber = another.isNumber;
		if (replacement != null || another.replacement != null) {
			String repOne = this.getReplacement();
			String repAno = another.getReplacement();
			if (isLine) {
				replacement = repOne + CRLF + repAno;
			} else {
				if (repOne.endsWith("?") && repAno.startsWith("?"))
					replacement = repOne + ", " + repAno;
				else
					replacement = repOne + (isEmpty(repOne) ? "" : " ")
							+ repAno;
			}
		}
		if (map == null)
			map = another.map;
		else if (another.map != null)
			map.putAll(another.map);
		this.status = this.status && another.status;
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
