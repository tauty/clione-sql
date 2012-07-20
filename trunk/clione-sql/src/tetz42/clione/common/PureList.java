/*
 * Copyright 2012 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tetz42.clione.common;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Administrator
 * 
 */
public class PureList<E> implements Iterable<E>, Serializable {

	/***/
	private static final long serialVersionUID = -6009911785500721458L;

	@SuppressWarnings({ "unchecked", "varargs" })
	public static <T> PureList<T> genList() {
		return new PureList<T>(null, null) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEmpty() {
				return true;
			}
		};
	}

	public static <T> PureList<T> cons(T head, PureList<T> tail) {
		return new PureList<T>(head, tail);
	}

	// @SuppressWarnings({"unchecked", "varargs"})
	// public static <T> PureList<T> genList(T... es) {
	// return genList(0, es);
	// }
	//
	// @SuppressWarnings({"unchecked", "varargs"})
	// private static <T> PureList<T> genList(int index, T... es) {
	// if (es.length >= index)
	// return null;
	// return cons(es[index], genList(index + 1, es));
	// }

	public final E head;
	public final PureList<E> tail;

	private PureList(E head, PureList<E> tail) {
		this.head = head;
		this.tail = tail;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean hasNext() {
		return tail != null && !tail.isEmpty();
	}

	public PureList<E> reverse() {
		PureList<E> pl = genList();
		for (E value : this) {
			pl = cons(value, pl);
		}
		return pl;
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
