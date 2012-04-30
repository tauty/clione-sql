package tetz42.clione.lang;

import static tetz42.util.Util.*;

import java.util.HashSet;
import java.util.LinkedList;

import tetz42.clione.exception.SQLFileNotFoundException;
import tetz42.clione.loader.LoaderUtil;

public class ContextUtil {

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

	private static ThreadLocal<LinkedList<ResInfoHolder>> resourceInfoes = new ThreadLocal<LinkedList<ResInfoHolder>>() {

		@Override
		protected LinkedList<ResInfoHolder> initialValue() {
			return new LinkedList<ResInfoHolder>();
		}
	};

	private static ThreadLocal<HashSet<Object>> negativeValues = new ThreadLocal<HashSet<Object>>() {

		@Override
		protected HashSet<Object> initialValue() {
			return new HashSet<Object>();
		}
	};

	public static void pushResouceInfo(String resourceInfo) {
		resourceInfoes.get().push(new ResInfoHolder(resourceInfo));
	}

	public static void setBeginLineNo(int lineNo) {
		getLatest().beginLineNo = lineNo;
		getLatest().endLineNo = lineNo;
	}

	public static void setEndLineNo(int lineNo) {
		getLatest().endLineNo = lineNo;
	}

	private static ResInfoHolder getLatest() {
		return resourceInfoes.get().getFirst();
	}

	public static String getResourceInfo() {
		return mkStringByCRLF(resourceInfoes.get());
	}

	public static String getResourcePath() {
		String resourceInfo = getLatest().resourceInfo;
		if (resourceInfo.startsWith(LoaderUtil.sqlPathPrefix))
			return resourceInfo.substring(LoaderUtil.sqlPathPrefix.length());
		return null;
	}

	public static String popResourceInfo() {
		return resourceInfoes.get().pop().toString();
	}

	public static boolean isAllPoped() {
		return resourceInfoes.get().isEmpty();
	}

	public static void addNegative(Object... negatives) {
		if (negatives == null)
			return;
		for (Object negative : negatives)
			if (negative != null)
				negativeValues.get().add(negative);
	}

	public static boolean isNegative(Object obj) {
		if (obj == null)
			return true;
		if (Boolean.FALSE.equals(obj))
			return true;
		if (negativeValues.get().contains(obj))
			return true;
		return false;
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
		negativeValues.get().clear();
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
}
