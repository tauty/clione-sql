package tetz42.clione.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.clione.util.ContextUtil.*;
import static tetz42.test.Util.*;

import org.junit.Test;

public class ClioneUtilTest {

	@Test
	public void testEscapeBySharp() {
		assertThat(escapeBySharp("tako"), is("tako"));
		assertThat(escapeBySharp("tako100%"), is("tako100#%"));
		assertThat(escapeBySharp("%_#[％＿"), is("#%#_###[#％#＿"));
		assertThat(escapeBySharp(new StringBuilder().append("%_#[％＿")),
				is("#%#_###[#％#＿"));
	}

	@Test
	public void resourceInfo0() {
		try {
			pushResouceInfo("zero.sql");
			setBeginLineNo(0);
			assertEqualsWithFile(getResourceInfo(), getClass(), "resourceInfo0");
		} finally {
			assertEqualsWithFile(popResourceInfo(), getClass(), "resourceInfo0");
		}
	}

	@Test
	public void resourceInfo1() {
		try {
			pushResouceInfo("tako.sql");
			setBeginLineNo(100);
			assertEqualsWithFile(getResourceInfo(), getClass(), "resourceInfo1");
		} finally {
			assertEqualsWithFile(popResourceInfo(), getClass(), "resourceInfo1");
		}
	}

	@Test
	public void resourceInfo2() {
		try {
			pushResouceInfo("tako.sql");
			setBeginLineNo(888);
			pushResouceInfo("ika.sql");
			setBeginLineNo(1010);
			assertEqualsWithFile(getResourceInfo(), getClass(), "resourceInfo2");
		} finally {
			assertEqualsWithFile(popResourceInfo(), getClass(),
					"resourceInfo2_popped1");
			assertEqualsWithFile(popResourceInfo(), getClass(),
					"resourceInfo2_popped2");
		}
	}
}
