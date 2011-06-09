package tetz42.clione.loader;

import static tetz42.clione.util.ClioneUtil.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;

import tetz42.clione.exception.SQLFileNotFoundException;
import tetz42.clione.node.SQLNode;
import tetz42.clione.parsar.SQLParser;

public class LoaderUtil {

	private static final SQLFileLoader sqlLoader = new SQLFileLoader();

	private static final ConcurrentHashMap<String, SQLNode> cacheByPath = new ConcurrentHashMap<String, SQLNode>();
	private static final ConcurrentHashMap<String, SQLNode> cacheBySQL = new ConcurrentHashMap<String, SQLNode>();

	public static SQLNode getNodeByPath(String sqlPath) {
		if (sqlPath == null)
			throw new NullPointerException("The sql path must not be null.");
		SQLNode sqlNode = cacheByPath.get(sqlPath);
		if (sqlNode == null) {
			final String resourceInfo = "SQL file path:" + sqlPath;
			InputStream in;
			try {
				in = sqlLoader.getResourceAsStream(sqlPath);
				if (in == null)
					throw new SQLFileNotFoundException("SQL File not found. "
							+ CRLF + resourceInfo);
			} catch (MissingResourceException e) {
				throw new SQLFileNotFoundException(
						"SQL File might not be found. " + resourceInfo, e);
			}
			sqlNode = cacheIf(new SQLParser(resourceInfo).parse(in), sqlPath,
					cacheByPath);
		}
		return sqlNode;
	}

	public static SQLNode getNodeByClass(Class<?> clazz, String sqlFileName) {
		if (sqlFileName == null)
			throw new NullPointerException("The sql file name must not be null.");
		String packageName = clazz.getPackage().getName().toLowerCase()
				.replace(".", "/");
		String className = clazz.getName().substring(packageName.length() + 1);
		return getNodeByPath(packageName + "/sql/" + className + "/" + sqlFileName);
	}

	public static SQLNode getNodeBySQL(String sql) {
		if (sql == null)
			throw new NullPointerException("The SQL must not be null.");
		SQLNode sqlNode = cacheBySQL.get(sql);
		if (sqlNode == null) {
			InputStream in = new ByteArrayInputStream(sql.getBytes());
			sqlNode = cacheIf(new SQLParser("The SQL passed as parameter.")
					.parse(in), sql, cacheBySQL);
		}
		return sqlNode;
	}

	public static SQLNode getNodeByStream(InputStream in) {
		if (in == null)
			throw new NullPointerException(
					"The parameter InputStream must not be null.");
		return new SQLParser("The input stream passed as parameter.").parse(in);
	}

	private static SQLNode cacheIf(SQLNode sqlNode, String key,
			ConcurrentHashMap<String, SQLNode> cache) {
		SQLNode cached = cache.putIfAbsent(key, sqlNode);
		if (cached != null)
			return cached;
		return sqlNode;
	}

}
