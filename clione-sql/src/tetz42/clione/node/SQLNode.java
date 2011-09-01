package tetz42.clione.node;

import java.util.List;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class SQLNode implements INode{
	
	public List<LineNode> nodes;
	public String resourceInfo;
	
	@Override
	public Instruction perform(ParamMap paramMap) {
		// TODO root should be a field.
		LineNode root = new LineNode(0);
		root.childBlocks = nodes;
		return root.perform(paramMap);
	}
}
