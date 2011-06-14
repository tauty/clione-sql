package tetz42.clione.util;

import static tetz42.clione.util.ClioneUtil.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

	private static ThreadLocal<List<ResInfoHolder>> resourceInfoes = new ThreadLocal<List<ResInfoHolder>>() {

		@Override
		protected List<ResInfoHolder> initialValue() {
			return new ArrayList<ResInfoHolder>();
		}
	};

	private static ThreadLocal<HashSet<Object>> nilValues = new ThreadLocal<HashSet<Object>>() {

		@Override
		protected HashSet<Object> initialValue() {
			return new HashSet<Object>();
		}
	};

	public static void pushResouceInfo(String resourceInfo) {
		resourceInfoes.get().add(new ResInfoHolder(resourceInfo));
	}

	public static void setBeginLineNo(int lineNo) {
		getLatest().beginLineNo = lineNo;
	}

	public static void setEndLineNo(int lineNo) {
		getLatest().endLineNo = lineNo;
	}

	private static ResInfoHolder getLatest() {
		List<ResInfoHolder> list = resourceInfoes.get();
		return list.get(list.size() - 1);
	}

	public static String getResourceInfo() {
		return joinByCrlf(resourceInfoes.get().toArray());
	}

	public static String popResourceInfo() {
		List<ResInfoHolder> list = resourceInfoes.get();
		return list.remove(list.size() - 1).toString();
	}

	public static void addNil(Object... nils) {
		if (nils == null)
			return;
		for (Object nil : nils)
			if (nil != null)
				nilValues.get().add(nil);
	}

	public static boolean isNil(Object obj) {
		if (obj == null)
			return true;
		if (nilValues.get().contains(obj))
			return true;
		if(Boolean.FALSE.equals(obj))
			return true;
		return false;
	}

	public static boolean isAllNil(Object... objs) {
		for (Object obj : objs)
			if (!isNil(obj))
				return false;
		return true;
	}

	public static boolean isAllNil(Iterable<?> objs) {
		for (Object obj : objs)
			if (!isNil(obj))
				return false;
		return true;
	}

	public static void clearNil() {
		nilValues.get().clear();
	}

}
