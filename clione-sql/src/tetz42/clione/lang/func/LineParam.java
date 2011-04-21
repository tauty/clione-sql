package tetz42.clione.lang.func;

public class LineParam extends AbstractParam {
	
	public LineParam(String key, boolean isNegative) {
		super(key, isNegative);
	}
	
	public LineParam(ClioneFunction inside, boolean isNegative) {
		super(inside, isNegative);
	}
	
	@Override
	public String getSrc() {
		return "$" + super.getSrc();
	}
}
