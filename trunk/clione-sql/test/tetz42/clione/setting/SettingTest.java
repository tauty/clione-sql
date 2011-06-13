package tetz42.clione.setting;

import static tetz42.clione.util.ClioneUtil.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.test.Util.*;

import org.junit.After;
import org.junit.Test;

public class SettingTest {

	@After
	public void after() {
		Setting.clear();
	}

	@Test
	public void getUTF8() {
		assertThat(Setting.get().SQLFILE_ENCODING, is("utf-8"));
	}

	@Test
	public void no_file() {
		hideFile("bin/clione.properties");
		try {
			assertNull(Setting.get().SQLFILE_ENCODING);
		} finally {
			restoreFile("bin/clione.properties");
		}
	}

	@Test
	public void defaultValue() {
		hideFile("bin/clione.properties");
		try {
			assertThat(nvl(Setting.get().SQLFILE_ENCODING, "shift_jis"),
					is("shift_jis"));
		} finally {
			restoreFile("bin/clione.properties");
		}
	}

}
