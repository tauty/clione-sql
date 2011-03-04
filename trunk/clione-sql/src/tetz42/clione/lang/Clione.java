package tetz42.clione.lang;

import tetz42.clione.util.ParamMap;

public abstract class Clione {

	private Clione child;

	public void setChild(Clione child) {
		this.child = child;
	}

	protected Egg deliver(ParamMap paramMap) {
		return child == null ? new Egg() : child.perform(paramMap);
	}

	public abstract Egg perform(ParamMap paramMap);
}
