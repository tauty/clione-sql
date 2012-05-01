package tetz42.clione.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.clione.lang.ContextUtil.*;
import static tetz42.clione.util.ClioneUtil.*;
import static tetz42.test.Auty.*;

import org.junit.Test;

public class ClioneUtilTest {

	@Test
	public void testEscapeBySharp() {
		assertThat(escapeBySharp("tako"), is("tako"));
		assertThat(escapeBySharp("tako100%"), is("tako100#%"));
		// oracle
		setProductName("oracle");
		assertThat(escapeBySharp("%_#[％＿"), is("#%#_##[#％#＿"));
		assertThat(escapeBySharp(new StringBuilder().append("%_#[％＿")),
				is("#%#_##[#％#＿"));
		// db2
		setProductName("db2");
		assertThat(escapeBySharp("%_#[％＿"), is("#%#_##[#％#＿"));
		assertThat(escapeBySharp(new StringBuilder().append("%_#[％＿")),
				is("#%#_##[#％#＿"));
		// SQLServer
		setProductName("sqlserver");
		assertThat(escapeBySharp("%_#[％＿"), is("#%#_###[％＿"));
		assertThat(escapeBySharp(new StringBuilder().append("%_#[％＿")),
				is("#%#_###[％＿"));
		// mysql
		setProductName("mysql");
		assertThat(escapeBySharp("%_#[％＿"), is("#%#_##[％＿"));
		assertThat(escapeBySharp(new StringBuilder().append("%_#[％＿")),
				is("#%#_##[％＿"));
		// sqlite
		setProductName("sqlite");
		assertThat(escapeBySharp("%_#[％＿"), is("#%#_##[％＿"));
		assertThat(escapeBySharp(new StringBuilder().append("%_#[％＿")),
				is("#%#_##[％＿"));
		// unknown
		setProductName(null);
		assertThat(escapeBySharp("%_#[％＿"), is("#%#_##[％＿"));
		assertThat(escapeBySharp(new StringBuilder().append("%_#[％＿")),
				is("#%#_##[％＿"));
	}

	@Test
	public void testNextStr() {
		assertThat(nextChar("1234567890", 1), is("2"));
		assertThat(nextChar("1234567890", 9), is("0"));
		assertNull(nextChar("1234567890", 10));
		assertThat(nextStr("1234567890", 1, 2), is("23"));
		assertThat(nextStr("1234567890", 7, 3), is("890"));
		assertNull(nextStr("1234567890", 7, 4));
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
