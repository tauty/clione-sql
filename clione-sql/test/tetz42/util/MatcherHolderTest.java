package tetz42.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

import tetz42.util.MatcherHolder;


public class MatcherHolderTest {

	private static Pattern ptn = Pattern.compile("[0-9]");
	
	@Test
	public void getNextChar() throws Exception{
		MatcherHolder mh = new MatcherHolder("0abcdefgh9", ptn);
		assertThat(mh.find(), is(true));
		assertThat(mh.get().group(), is("0"));
		assertThat(mh.getNextChar(), is('a'));
		assertThat(mh.find(), is(true));
		assertThat(mh.get().group(), is("9"));
		assertThat(mh.getNextChar(), is((char)0));
	}
	
}
