package tetz42.clione.lang;

import java.util.ArrayList;
import java.util.List;

import tetz42.clione.lang.func.ClioneFunction;
import tetz42.clione.util.ParamMap;

public class Unparsed extends ClioneFunction {

	static ThreadLocal<List<Unparsed>> unparsedList = new ThreadLocal<List<Unparsed>>() {
		@Override
		protected List<Unparsed> initialValue() {
			return new ArrayList<Unparsed>();
		};
	};

	private String unparsedStr;

	public Unparsed(String unparsedStr) {
		this.unparsedStr = unparsedStr;
		unparsedList.get().add(this);
	}

	@Override
	public Instruction perform(ParamMap paramMap) {
		return null;
	}
	
	@Override
	public String getString() {
		return unparsedStr;
	}

	@Override
	public String toString() {
		return unparsedStr;
	}
}
