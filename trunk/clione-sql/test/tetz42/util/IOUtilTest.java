package tetz42.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.MissingResourceException;
import java.util.Properties;

import org.junit.Test;

public class IOUtilTest {

	@Test
	public void missing() {
		Properties prop = IOUtil.getPropertiesRB("db.properties");
		try {
			prop.getProperty("unknown");
			fail();
		} catch (MissingResourceException e) {
			assertThat(
					e.getMessage(),
					is("No object for the given key, 'unknown', can be found in 'db.properties'."));
		}
	}

}
