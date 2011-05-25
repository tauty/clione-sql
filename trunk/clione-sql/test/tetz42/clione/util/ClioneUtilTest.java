package tetz42.clione.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static tetz42.clione.util.ClioneUtil.*;

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
}
