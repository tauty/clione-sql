package tetz42.clione.lang;

import java.util.ArrayList;
import java.util.List;

public class Instruction {
	
	/**
	 * In case params.size is one and replacement is null -> '?' and bind params
	 * In case params.size is three and replacement is null -> '?, ?, ?' and bind params
	 * In case params is null and replacement is 'aaa' -> 'aaa'
	 * In case params.size is two and replacement is 'CONCAT(?, ''%'', ?)' 
	 *                                  -> 'CONCAT(?, ''%'', ?)' and bind params
	 */
	public List<Object> params = new ArrayList<Object>();
	public String replacement;
	public boolean isNodeRequired = true;
	public boolean doNothing = false;
	public boolean useValueInBack = false;
	public Instruction next = null;
	
	public Instruction merge(Instruction another) {
		params.addAll(another.params);
		if(isNodeRequired)
			isNodeRequired = another.isNodeRequired;
		if(replacement == null)
			replacement = another.replacement;
		return this;
	}

	// TODO move this function to SQLExecuter
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
