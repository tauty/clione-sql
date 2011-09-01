package tetz42.clione.node;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public interface INode {
	Instruction perform(ParamMap paramMap);
}
