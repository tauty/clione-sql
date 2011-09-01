package tetz42.clione.node;

import tetz42.clione.lang.Instruction;
import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public interface IPlaceHolder {
	Instruction perform(ParamMap paramMap);
	ClioneFunction getFunction();
	int getPosition();
	void setPosition(int pos);
	void movePosition(int num);
	int getLength();
}
