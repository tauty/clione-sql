package tetz42.clione.setting;

import java.lang.reflect.Field;
import java.util.Properties;

import tetz42.clione.exception.WrapException;
import tetz42.clione.io.IOUtil;

public class Config {

	private static volatile Config config;

	public static Config get() {
		Config local = config;
		if (local == null) {
			synchronized (Config.class) {
				local = config;
				if (local == null) {
					config = local = new Config();
				}
			}
		}
		return local;
	}

	public synchronized static void clear() {
		config = null;
	}

	public String SQLFILE_ENCODING = null;
	public boolean IS_DEVELOPMENT_MODE = false;
	public int SQLFILE_CACHETIME = 0;
	public int TAB_SIZE = 4;

	private Config() {
		Properties prop = IOUtil.getProperties("clione.properties");
		prop = prop != null ? prop : new Properties();
		for (Field f : getClass().getFields()) {
			if (prop.getProperty(f.getName()) != null) {
				try {
					if (f.getType() == String.class)
						f.set(this, prop.getProperty(f.getName()));
					else if (f.getType() == Integer.class
							|| f.getType() == Integer.TYPE)
						f.set(this, Integer.parseInt(prop.getProperty(f
								.getName())));
					else if (f.getType() == Boolean.class
							|| f.getType() == Boolean.TYPE)
						f.set(this, !prop.getProperty(f.getName()).equals(
								"false"));
				} catch (IllegalArgumentException e) {
					throw new WrapException(e);
				} catch (IllegalAccessException e) {
					throw new WrapException(e);
				}
			}
		}
	}
}
