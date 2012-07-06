package tetz42.clione.util.converter;

public final class Employee extends Person {

	public String title;

	@Override
	public String toString() {
		return super.toString() + ":" + title;
	}

	@Override
	public Person fromString(String str) {
		String[] ary = str.split(":");
		name = ary[0];
		sex = ary[1];
		address = ary[2];
		title = ary[3];
		return this;
	}
}
