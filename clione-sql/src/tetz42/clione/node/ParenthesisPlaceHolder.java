package tetz42.clione.node;

import tetz42.clione.gen.SQLGenerator;
import tetz42.clione.lang.Instruction;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public class ParenthesisPlaceHolder implements IPlaceHolder {

	private final SQLNode sqlNode;
	private int pos;

	public ParenthesisPlaceHolder(SQLNode sqlNode) {
		this.sqlNode = sqlNode;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		SQLGenerator sqlGenerator = new SQLGenerator();
		String sql = sqlGenerator.genSql(paramMap, sqlNode);
		Instruction inst = new Instruction();
		if (!sqlGenerator.isSqlOutputed) {
			return inst.nodeDispose();
		}
		inst.replacement = "(" + sql + ")";
		inst.params = sqlGenerator.params;
		return inst;
	}

	@Override
	public ClioneFunction getFunction() {
		return null;
	}

	@Override
	public int getPosition() {
		return this.pos;
	}

	@Override
	public void setPosition(int pos) {
		this.pos = pos;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void movePosition(int num) {
		this.pos += num;
	}
}
