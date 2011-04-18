package tetz42.clione.lang.func;

import tetz42.clione.lang.Instruction;
import tetz42.clione.util.ParamMap;

public class Extention extends ClioneFunction {

	protected final String func;
	protected final boolean isNegative;

	public Extention(String key, boolean isNegative, String literal) {
		this.isNegative = isNegative;
		this.func = key;
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	LIKE /* %CNV_L '%' @PARAM_1 ' ' $PARAM_2 '%' *\/'%TAKO IKA%'
	LIKE CONCAT('%', ?, ' ', ?, '%') -- PARAM PARAM_1, PARAM_2
	LIKE '%' || ? || ' ' || ? || '%' -- PARAM PARAM_1, PARAM_2
	*/

}
