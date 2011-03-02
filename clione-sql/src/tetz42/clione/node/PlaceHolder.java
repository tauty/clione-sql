package tetz42.clione.node;

import tetz42.clione.lang.Clione;
import tetz42.clione.lang.Egg;
import tetz42.clione.util.ParamMap;

public class PlaceHolder {
	
	int begin;
	int length;
	
	private final Clione clione;
	
	public PlaceHolder(String src){
		// TODO implementation
		this.clione = null;
	}
	
	public Egg perform(ParamMap paramMap){
		return clione.perform(paramMap);
	}
	
}
