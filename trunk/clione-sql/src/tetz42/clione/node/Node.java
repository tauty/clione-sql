package tetz42.clione.node;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class Node implements INode{
	public String sql;
	public List<IPlaceHolder> holders = new ArrayList<IPlaceHolder>();
	
	@Override
	public Instruction perform(ParamMap paramMap) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
