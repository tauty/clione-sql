package tetz42.clione.util;

import java.io.IOException;

public class SBHolder implements Appendable {

	public final StringBuilder sb = new StringBuilder();
	private int preLength = 0;

	@Override
	public Appendable append(CharSequence cs) {
		preLength = sb.length();
		sb.append(cs);
		return this;
	}

	@Override
	public Appendable append(CharSequence cs, int num1, int num2)
			throws IOException {
		preLength = sb.length();
		sb.append(cs, num1, num2);
		return this;
	}

	@Override
	public Appendable append(char c) {
		preLength = sb.length();
		sb.append(c);
		return this;
	}
	
	public Appendable delete(int begin, int end){
		preLength -= (end - begin);
		sb.delete(begin, end);
		return this;
	}

	public int getPreLength() {
		return preLength;
	}

	public void clear() {
		preLength = 0;
		sb.setLength(0);
	}

}
