package tetz42.clione.lang;

import static tetz42.util.Util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tetz42.clione.exception.SQLFileNotFoundException;
import tetz42.clione.lang.dialect.Db2Dialect;
import tetz42.clione.lang.dialect.Dialect;
import tetz42.clione.lang.dialect.MysqlDialect;
import tetz42.clione.lang.dialect.OracleDialect;
import tetz42.clione.lang.dialect.PostgresDialect;
import tetz42.clione.lang.dialect.SqlserverDialect;
import tetz42.clione.loader.LoaderUtil;
import tetz42.clione.util.ParamMap;
import static tetz42.clione.SQLManager.Product.*;

public class ContextUtil {

	private static final Map<String, Dialect> map;
	private static final String STANDARD_RDBMS = "";

	static {
		Map<String, Dialect> m = new HashMap<String, Dialect>();
		m.put(STANDARD_RDBMS, new Dialect());
		m.put(SQLSERVER.name().toLowerCase(), new SqlserverDialect());
		m.put(ORACLE.name().toLowerCase(), new OracleDialect());
		m.put(MYSQL.name().toLowerCase(), new MysqlDialect());
		m.put(DB2.name().toLowerCase(), new Db2Dialect());
		m.put(POSTGRES.name().toLowerCase(), new PostgresDialect());
		map = Collections.unmodifiableMap(m);
	}

	private static class ResInfoHolder {
		String resourceInfo;
		int beginLineNo = 0;
		int endLineNo = 0;

		private ResInfoHolder(String resourceInfo) {
			this.resourceInfo = resourceInfo;
		}

		@Override
		public String toString() {
			if (beginLineNo == 0) {
				return resourceInfo + ", line number: Unknown";
			}
			return resourceInfo + ", line number:" + beginLineNo
					+ (endLineNo == beginLineNo ? "" : "-" + endLineNo);
		}
	}

	public enum IFStatus {
		DO_ELSE, DO_ELSE_NEXT, NOTHING
	}

	private static class Context {
		String productName;
		LinkedList<ResInfoHolder> resourceInfoes = new LinkedList<ContextUtil.ResInfoHolder>();
		HashSet<Object> negativeValues = new HashSet<Object>();
		List<Extention> curExtentions = new ArrayList<Extention>();
		List<ParamMap> curParamMaps = new ArrayList<ParamMap>();
		IFStatus ifStatus = IFStatus.NOTHING;
	}

	private static final ThreadLocal<Context> tcontext = new ThreadLocal<ContextUtil.Context>();

	private static Context getContext() {
		Context context = tcontext.get();
		if (context == null)
			tcontext.set(context = new Context());
		return context;
	}

	public static IFStatus getIFStatus() {
		return getContext().ifStatus;
	}

	public static void setIFStatus(IFStatus ifStatus) {
		getContext().ifStatus = ifStatus;
	}

	public static List<Extention> getCurExtensions() {
		return getContext().curExtentions;
	}

	public static List<ParamMap> getCurParamMaps() {
		return getContext().curParamMaps;
	}

	public static void pushResouceInfo(String resourceInfo) {
		getContext().resourceInfoes.push(new ResInfoHolder(resourceInfo));
	}

	public static void setBeginLineNo(int lineNo) {
		getLatest().beginLineNo = lineNo;
		getLatest().endLineNo = lineNo;
	}

	public static void setEndLineNo(int lineNo) {
		getLatest().endLineNo = lineNo;
	}

	private static ResInfoHolder getLatest() {
		return getContext().resourceInfoes.getFirst();
	}

	public static String getResourceInfo() {
		return mkStringByCRLF(getContext().resourceInfoes);
	}

	public static String getResourcePath() {
		String resourceInfo = getLatest().resourceInfo;
		if (resourceInfo.startsWith(LoaderUtil.sqlPathPrefix))
			return resourceInfo.substring(LoaderUtil.sqlPathPrefix.length());
		return null;
	}

	public static String popResourceInfo() {
		return getContext().resourceInfoes.pop().toString();
	}

	public static boolean isAllPopped() {
		return getContext().resourceInfoes.isEmpty();
	}

	public static void addNegative(Object... negatives) {
		if (negatives == null)
			return;
		for (Object negative : negatives)
			if (negative != null)
				getContext().negativeValues.add(negative);
	}

	public static boolean isNegative(Object obj) {
		if (obj == null || Boolean.FALSE.equals(obj))
			return true;
		return getContext().negativeValues.contains(obj);
	}

	public static boolean isAllNegative(Object... objs) {
		for (Object obj : objs)
			if (!isNegative(obj))
				return false;
		return true;
	}

	public static boolean isAllNegative(Iterable<?> objs) {
		for (Object obj : objs)
			if (!isNegative(obj))
				return false;
		return true;
	}

	public static void clearNegative() {
		getContext().negativeValues.clear();
	}

	public static String fusionPath(String absolutePath, String relativePath) {

		// remove file name from absolute path
		int pos = absolutePath.lastIndexOf('/');
		pos = pos > 0 ? pos : 0;
		absolutePath = absolutePath.substring(0, pos);

		String aPath = absolutePath;
		String rPath = relativePath;

		while (rPath.startsWith(".")) {
			if (rPath.startsWith("./")) {
				rPath = rPath.substring(2);
			} else if (rPath.startsWith("../") && isNotEmpty(aPath)) {
				rPath = rPath.substring(3);
				pos = aPath.lastIndexOf('/');
				pos = pos > 0 ? pos : 0;
				aPath = aPath.substring(0, pos);
			} else {
				throw new SQLFileNotFoundException(mkStringByCRLF(
						"Can not resolve the path below:", absolutePath
								+ relativePath));
			}
		}
		aPath = isEmpty(aPath) ? "" : aPath + "/";

		return aPath + rPath;
	}

	public static String getProductName() {
		return getContext().productName;
	}

	public static void setProductName(String productName) {
		getContext().productName = productName;
	}

	public static Dialect getDialect() {
		Dialect dialect = map.get(getContext().productName);
		return dialect != null ? dialect : map.get(STANDARD_RDBMS);
	}

	public static String escapeBySharp(String src) {
		return src == null ? null : getDialect().needLikeEscape().matcher(src)
				.replaceAll("#$1");
	}

	public static String escapeBySharp(Object obj) {
		return obj == null ? null : escapeBySharp(String.valueOf(obj));
	}

	public static void clear() {
		tcontext.set(null);
	}
}
