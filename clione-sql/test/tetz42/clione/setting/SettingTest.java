package tetz42.clione.setting;

import static tetz42.test.Auty.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import tetz42.clione.util.Config;

public class SettingTest {

	@After
	public void after() {
		Config.clear();
	}

	@Test
	public void getUTF8() {
		assertThat(Config.get().SQLFILE_ENCODING, is("utf-8"));
	}

	@Test
	public void config_all() {
		String path1 = "bin/clione.properties";
		String path2 = "bin/clione_all.properties";
		try {
			swapFile(path1, path2);
			assertEqualsWithFile(Config.get(), getClass(), "config_all");
		} finally {
			swapFile(path1, path2);
		}
	}
}
