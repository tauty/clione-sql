package tetz42.clione.lang.func;

import tetz42.clione.exception.ClioneFormatException;
import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class NumLiteral extends ClioneFunction {

	private String key;

	public NumLiteral(String key) {
		this(key, false);
	}

	public NumLiteral(String key, boolean isNegative) {
		if (isNegative)
			throw new ClioneFormatException("The literal, '!" + key
					+ "', is not supported.");
		this.key = key;
	}

	@Override
	public String getSrc() {
		return key;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		return new Instruction().asNumber().replacement(key);
	}

}
