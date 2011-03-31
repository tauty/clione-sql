package tetz42.clione.lang;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.node.LineNode;

public class Instruction {
	public List<Object> params = new ArrayList<Object>();
	public List<LineNode> replacement;
	public boolean isNodeRequired = true;
	public boolean doNothing = false;
	public boolean useValueInBack = false;
	
	public Instruction merge(Instruction another) {
		params.addAll(another.params);
		if(isNodeRequired)
			isNodeRequired = another.isNodeRequired;
		if(replacement == null)
			replacement = another.replacement;
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
