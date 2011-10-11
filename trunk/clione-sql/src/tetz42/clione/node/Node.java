package tetz42.clione.node;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class Node implements INode {
	public String sql;
	public List<IPlaceHolder> holders = new ArrayList<IPlaceHolder>();

	@Override
	public Instruction perform(ParamMap paramMap) {
		Instruction myInst = new Instruction();
		StringBuilder sql = new StringBuilder(this.sql);
		int remainder = 0;
		for (IPlaceHolder holder : this.holders) {
			Instruction inst = holder.perform(paramMap);
			if (inst.isNodeDisposed)
				return myInst.nodeDispose();
			if (inst.doNothing)
				continue;
			String query = inst.getReplacement();
			sql.replace(holder.getPosition() + remainder,
					holder.getPosition() + holder.getLength() + remainder,
					query);
			myInst.params.addAll(inst.params);
			remainder += query.length();
		}
		return myInst.replacement(sql.toString());
	}

	@Override
	public boolean isMultiLine() {
		return false;
	}

}
