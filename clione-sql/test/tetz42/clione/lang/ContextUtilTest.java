package tetz42.clione.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.clione.lang.ContextUtil.*;

import org.junit.Test;

import tetz42.clione.exception.SQLFileNotFoundException;

public class ContextUtilTest {

	@Test
	public void testFusionPath() {
		assertThat(fusionPath("jp/test/Tako.sql", "Ika.sql"), is("jp/test/Ika.sql"));
	}

	@Test
	public void testFusionPath_normal() {
		assertThat(fusionPath("jp/test/Tako.sql", "./Ika.sql"), is("jp/test/Ika.sql"));
	}

	@Test
	public void testFusionPath_up() {
		assertThat(fusionPath("jp/test/Tako.sql", "../Ika.sql"), is("jp/Ika.sql"));
	}

	@Test
	public void testFusionPath_2up() {
		assertThat(fusionPath("jp/test/Tako.sql", ".././../Ika.sql"), is("Ika.sql"));
	}

	@Test
	public void testFusionPath_normal_sql() {
		assertThat(fusionPath("jp/test/Tako.sql", "./sql/Ika.sql"), is("jp/test/sql/Ika.sql"));
	}

	@Test
	public void testFusionPath_up_sql() {
		assertThat(fusionPath("jp/test/Tako.sql", "../sql/Ika.sql"), is("jp/sql/Ika.sql"));
	}

	@Test
	public void testFusionPath_2up_sql() {
		assertThat(fusionPath("jp/test/Tako.sql", ".././../sql/Ika.sql"), is("sql/Ika.sql"));
	}

	@Test(expected=SQLFileNotFoundException.class)
	public void testFusionPath_unsupported_dir_name() {
		fusionPath("jp/test/Tako.sql", "../.hanako/sql/Ika.sql");
	}

	@Test(expected=SQLFileNotFoundException.class)
	public void testFusionPath_too_many_up() {
		fusionPath("jp/test/Tako.sql", "../../../Ika.sql");
	}

}
