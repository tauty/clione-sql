package tetz42.clione.lang;

import static tetz42.clione.util.ClioneUtil.*;

import java.util.HashSet;
import java.util.LinkedList;

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
					+ (endLineNo == 0 ? "" : "-" + endLineNo);
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
	}

	public static void setEndLineNo(int lineNo) {
		getLatest().endLineNo = lineNo;
	}

	private static ResInfoHolder getLatest() {
		return resourceInfoes.get().getFirst();
	}

	public static String getResourceInfo() {
		return joinByCrlf(resourceInfoes.get().toArray());
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

}
