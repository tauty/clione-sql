package tetz42.cellom.parsar.queue;

import static tetz42.util.ReflectionUtil.*;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tetz42.cellom.parsar.queue.annotation.QueueCell;

public class QueueParsar {

	public static <T> T parse(Class<T> clazz, String src) {
		return parse(clazz, new StrToken(src));
	}

	private static <T> T parse(Class<T> clazz, StrToken token) {
		List<Field> fList = avoidDuplication(getAllFields(clazz));
		Collections.sort(fList, new Comparator<Field>() {
			@Override
			public int compare(Field f1, Field f2) {
				QueueCell q1 = getAnnotation(f1, QueueCell.class);
				QueueCell q2 = getAnnotation(f2, QueueCell.class);
				return q1.order() - q2.order();
			}
		});
		T res = newInstance(clazz);
		for (Field f : fList) {
			QueueCell q = getAnnotation(f, QueueCell.class);
			if (isPrimitive(f.getType())) {
				setStringValue(res, f, token.next(q.size()));
			} else {
				setValue(res, f, parse(f.getType(), token));
			}
		}
		return res;
	}

	private static class StrToken {
		private String src;
		private int pos;

		StrToken(String src) {
			this.src = src;
			this.pos = 0;
		}

		String next(int size) {
			String res = this.src.substring(pos, pos + size);
			pos += size;
			return res;
		}
	}

}
