package tetz42.clione.lang;

import tetz42.clione.node.LineNode;
import tetz42.clione.util.ParamMap;

public class Literal extends Clione {
	
	private final String literal;
	private final boolean isTerminated;
	
	public Literal(String literal, boolean isTerminated){
		this.literal = literal;
		this.isTerminated = isTerminated;
	}

	@Override
	public Egg perform(ParamMap paramMap) {
		LineNode node = new LineNode();
		// TODO implement literal parsing.
		node.sql.append(this.literal);
		Egg egg = deliver(paramMap);
		egg.replacement.add(node);
		return egg;
	}
	
	@Override
	protected boolean isTerminated() {
		return isTerminated;
	}
}
