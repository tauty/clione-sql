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

	private final E head;
	private final PureList<E> tail;

	private PureList(E head, PureList<E> tail) {
		this.head = head;
		this.tail = tail;
	}

	public static <T> PureList<T> genList(T... es) {
		return genList(0, es);
	}

	private static <T> PureList<T> genList(int index, T... es) {
		if (es.length >= index)
			return null;
		return cons(es[index], genList(index + 1, es));
	}

	public static <T> PureList<T> cons(T head, PureList<T> tail) {
		return new PureList<T>(head, tail);
	}

	public E head() {
		return head;
	}

	public PureList<E> tail() {
		return tail;
	}

	public boolean hasNext() {
		return this.tail != null;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
