package tetz42.clione.util;

import static tetz42.util.Util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import tetz42.util.IOUtil;

public class Config {

	private static volatile Config config;

	public static Config get() {
		Config local = config;
		if (local == null) {
			synchronized (Config.class) {
				if (config == null) {
					config = local = new Config();
				}
			}
		}
		return local;
	}

	public synchronized static void clear() {
		config = null;
	}

	private Properties prop;

	public final String DBMS_PRODUCT_NAME = getStr("DBMS_PRODUCT_NAME");
	public final String SQLFILE_ENCODING = getStr("SQLFILE_ENCODING", "utf-8");
	public final boolean IS_DEVELOPMENT_MODE = getBool("IS_DEVELOPMENT_MODE",
			false);
	public final int SQLFILE_CACHETIME = getNum("SQLFILE_CACHETIME", 0);
	public final int TAB_SIZE = getNum("TAB_SIZE", 4);
	public final int ENTITY_DEPTH_LIMIT = getNum("ENTITY_DEPTH_LIMIT", 8);;
	public final List<String> CONVERTERS = getStrList("CONVERTERS");

	private Config() {
		prop = null;
	}

	private Properties prop() {
		if (prop == null) {
			prop = IOUtil.getProperties("clione.properties");
			prop = prop != null ? prop : new Properties();
		}
		return prop;
	}

	private String getStr(String key) {
		return prop().getProperty(key);
	}

	private String getStr(String key, String defaultValue) {
		return nvl(getStr(key), defaultValue);
	}

	private boolean getBool(String key, boolean defaultValue) {
		String s = prop().getProperty(key);
		return s != null ? "true".equals(s) : defaultValue;
	}

	private int getNum(String key, int defaultValue) {
		String s = prop().getProperty(key);
		return s != null ? Integer.parseInt(s) : defaultValue;
	}

	private List<String> getStrList(String key) {
		ArrayList<String> list = new ArrayList<String>();
		int i = 0;
		while (true) {
			String data = prop().getProperty(key + "." + i++);
			if (data == null)
				break;
			list.add(data);
		}
		return Collections.unmodifiableList(list);
	}
}
