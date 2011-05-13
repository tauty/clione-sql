package tetz42.clione.setting;

import java.util.Properties;
import java.util.ResourceBundle;

import tetz42.clione.io.IOUtil;

public class Setting {

	private static final ClassLoader loader = new ClioneClassLoader();
	private static Setting setting;

	public static Setting instance() {
		if (setting == null) {
			synchronized (Setting.class) {
				if (setting == null) {
					setting = new Setting();
				}
			}
		}
		if (!setting.isLoaded)
			setting.load();
		return setting;
	}

	private Properties prop;
	private boolean isLoaded;

	private Setting() {
		load();
	}

	public String get(String key) {
		return prop == null ? null : prop.getProperty(key);
	}

	public String get(String key, String defaultValue) {
		String value = get(key);
		return value != null ? value : defaultValue;
	}

	void load() {
		prop = IOUtil.getProperties("clione.properties", loader);
		isLoaded = true;
	}

	// TODO clear method is not thread safe because a thread performing get method and 
	// another thread call clear method, get method might throw NullPointerException. 
	synchronized void clear() {
		prop = null;
		isLoaded = false;
		ResourceBundle.clearCache(loader);
	}
}
