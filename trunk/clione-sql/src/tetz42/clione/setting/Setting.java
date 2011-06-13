package tetz42.clione.setting;

import java.lang.reflect.Field;
import java.util.Properties;

import tetz42.clione.exception.WrapException;
import tetz42.clione.io.IOUtil;

public class Setting {

	private static final ClassLoader loader = new ClioneClassLoader();
	private static Setting setting;

	public static Setting get() {
		if (setting == null) {
			synchronized (Setting.class) {
				if (setting == null) {
					setting = new Setting();
					// This method must not return null even if clear method is
					// called special timing.
					return setting;
				}
			}
		}
		return setting;
	}

	public String SQLFILE_ENCODING;
	public int RELOADING_TIME;

	private Setting() {
		Properties prop = IOUtil.getProperties("clione.properties", loader);
		prop = prop != null ? prop : new Properties();
		for (Field f : getClass().getFields()) {
			if (prop.getProperty(f.getName()) != null) {
				try {
					if (f.getType() == String.class)
						f.set(this, prop.getProperty(f.getName()));
					else if (f.getType() == Integer.class)
						f.set(this,
								Integer.parseInt(prop.getProperty(f.getName())));
				} catch (IllegalArgumentException e) {
					throw new WrapException(e);
				} catch (IllegalAccessException e) {
					throw new WrapException(e);
				}
			}
		}
	}

	synchronized static void clear() {
		setting = null;
	}
}
