package tetz42.clione.loader;

import static tetz42.clione.util.ClioneUtil.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import tetz42.clione.exception.SQLFileNotFoundException;
import tetz42.clione.io.IOWrapper;
import tetz42.clione.node.SQLNode;
import tetz42.clione.parsar.SQLParser;
import tetz42.clione.setting.Setting;

public class LoaderUtil {
	
	public static String sqlPathPrefix = "SQL file path:";

	private static class NodeHolder {
		private NodeHolder(SQLNode sqlNode, long systime) {
			this.sqlNode = sqlNode;
			this.cachedTime = systime;
		}

		private SQLNode sqlNode;
		private long cachedTime;
	}

	private static final ConcurrentHashMap<String, NodeHolder> cacheByPath = new ConcurrentHashMap<String, NodeHolder>();
	private static final ConcurrentHashMap<String, NodeHolder> cacheBySQL = new ConcurrentHashMap<String, NodeHolder>();

	public static SQLNode getNodeByPath(String sqlPath) {
		if (sqlPath == null)
			throw new NullPointerException("The sql path must not be null.");
		NodeHolder nh = cacheByPath.get(sqlPath);
		if (isCacheInvalid(nh)) {
			nh = createNodeHolder(sqlPath);
		}
		return nh.sqlNode;
	}

	public static SQLNode getNodeByClass(Class<?> clazz, String sqlFileName) {
		if (clazz == null)
			throw new NullPointerException("The class object must not be null.");
		if (sqlFileName == null)
			throw new NullPointerException(
					"The sql file name must not be null.");
		String packageName = clazz.getPackage().getName().toLowerCase()
				.replace(".", "/");
		String className = clazz.getName().substring(packageName.length() + 1);
		return getNodeByPath(packageName + "/sql/" + className + "/"
				+ sqlFileName);
	}

	// TODO implementation as getNodeBySQL(String sql, String resouceInfo)
	public static SQLNode getNodeBySQL(String sql) {
		if (sql == null)
			throw new NullPointerException("The SQL must not be null.");
		NodeHolder nh = cacheBySQL.get(sql);
		if (isCacheInvalid(nh)) {
			InputStream in = new ByteArrayInputStream(sql.getBytes());
			nh = cacheIf(new SQLParser("The SQL passed as parameter.")
					.parse(in), sql, cacheBySQL);
		}
		return nh.sqlNode;
	}

	public static SQLNode getNodeBySQL(String sql, String resouceInfo) {
		if (sql == null)
			throw new NullPointerException("The SQL must not be null.");
		NodeHolder nh = cacheBySQL.get(sql);
		if (isCacheInvalid(nh)) {
			InputStream in = new ByteArrayInputStream(sql.getBytes());
			nh = cacheIf(new SQLParser(resouceInfo)
					.parse(in), sql, cacheBySQL);
		}
		return nh.sqlNode;
	}

	public static SQLNode getNodeByStream(InputStream in) {
		if (in == null)
			throw new NullPointerException(
					"The parameter InputStream must not be null.");
		return new SQLParser("The input stream passed as parameter.").parse(in);
	}

	private static NodeHolder createNodeHolder(final String sqlPath) {
		final String resourceInfo = sqlPathPrefix + sqlPath;
		final InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(sqlPath);
		if (in == null)
			throw new SQLFileNotFoundException("SQL File not found. " + CRLF
					+ resourceInfo);
		return new IOWrapper<NodeHolder>(in) {

			@Override
			protected NodeHolder execute() throws IOException {
				return cacheIf(new SQLParser(resourceInfo).parse(in), sqlPath,
						cacheByPath);
			}
		}.invoke();
	}

	private static boolean isCacheInvalid(NodeHolder nh) {
		if (nh == null)
			return true;
		if (Setting.get().IS_DEVELOPMENT_MODE
				&& nh.cachedTime + Setting.get().SQLFILE_CACHETIME < System
						.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	private static NodeHolder cacheIf(SQLNode sqlNode, String key,
			ConcurrentHashMap<String, NodeHolder> cache) {
		NodeHolder nh = new NodeHolder(sqlNode, System.currentTimeMillis());
		cache.put(key, nh);
		return nh;
	}

}
