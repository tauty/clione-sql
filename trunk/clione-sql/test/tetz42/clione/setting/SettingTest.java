package tetz42.clione.setting;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.test.Util.*;

import org.junit.After;
import org.junit.Test;

public class SettingTest {

	@After
	public void after() {
		Setting.instance().clear();
	}

	@Test
	public void getShift_JIS() {
		assertThat(Setting.instance().get("sqlfile-encoding"), is("utf-8"));
	}

	@Test
	public void no_file() {
		hideFile("bin/clione.properties");
		try {
			assertNull(Setting.instance().get("sqlfile-encoding"));
		} finally {
			restoreFile("bin/clione.properties");
		}
	}

	@Test
	public void defaultValue() {
		hideFile("bin/clione.properties");
		try {
			assertThat(Setting.instance().get("sqlfile-encoding", "shift_jis"),
					is("shift_jis"));
		} finally {
			restoreFile("bin/clione.properties");
		}
	}

}
