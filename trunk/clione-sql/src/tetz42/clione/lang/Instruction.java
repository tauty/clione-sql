package tetz42.clione.lang;

import java.util.ArrayList;
import java.util.List;

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

	public Instruction nodeDispose() {
		this.isNodeDisposed = true;
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

	public Instruction useValueInBack() {
		this.useValueInBack = true;
		return this;
	}

	public String getReplacement() {
		return replacement != null ? replacement : genQuestions();
	}

	public Instruction clearNext() {
		Instruction next = this.next;
		this.next = null;
		return next;
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
				replacement = repOne + " " + repAno;
		}
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
