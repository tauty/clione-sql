/**
 *
 */
package tetz42.util;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Administrator
 *
 */
public class PureList<E> implements Iterable<E>, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6009911785500721458L;

	public final E head;
	public final PureList<E> tail;

	private PureList(E head, PureList<E> tail) {
		this.head = head;
		this.tail = tail;
	}

	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> PureList<T> genList(T... es) {
		return genList(0, es);
	}

	@SuppressWarnings({"unchecked", "varargs"})
	private static <T> PureList<T> genList(int index, T... es) {
		if (es.length >= index)
			return null;
		return cons(es[index], genList(index + 1, es));
	}

	public static <T> PureList<T> cons(T head, PureList<T> tail) {
		return new PureList<T>(head, tail);
	}

	public boolean hasNext() {
		return this.tail != null;
	}

	@Override
	public Iterator<E> iterator() {
		class CurHolder<T> {
			PureList<T> current;
		}
		final CurHolder<E> holder = new CurHolder<E>();
		holder.current = this;
		return new Iterator<E>() {

			@Override
			public boolean hasNext() {
				return holder.current.hasNext();
			}

			@Override
			public E next() {
				E value = holder.current.head;
				holder.current = holder.current.tail;
				return value;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"PureList is immutable.");
			}
		};
	}
}
