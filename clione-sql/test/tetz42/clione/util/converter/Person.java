package tetz42.clione.util.converter;

public class Person {

	public String name;
	public String sex;
	public String address;

	@Override
	public String toString() {
		return name + ":" + sex + ":" + address;
	}

	public Person fromString(String str) {
		String[] ary = str.split(":");
		name = ary[0];
		sex = ary[1];
		address = ary[2];
		return this;
	}
}
