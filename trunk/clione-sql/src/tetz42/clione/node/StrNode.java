package tetz42.clione.node;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class StrNode implements INode {

	private final String sql;

	public StrNode(String sql) {
		this.sql = sql;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		return new Instruction().replacement(sql);
	}

	@Override
	public String toString() {
		return "\"" + sql + "\"";
	}
}
