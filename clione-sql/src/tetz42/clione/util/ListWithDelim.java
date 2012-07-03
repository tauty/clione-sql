package tetz42.clione.util;

import java.util.ArrayList;
import java.util.Collection;

import tetz42.util.Const;

public class ListWithDelim<E> extends ArrayList<E> {

	private static final String OR = "OR";
	private static final String AND = "AND";
	private static final String COMMA = ",";

	/**
	 *
	 */
	private static final long serialVersionUID = -53884053944137307L;

	/**
	 * Generate a list from parameter.
	 *
	 * @param <ELE>
	 *            type parameter of elements
	 * @return the list generated
	 */
	public static <ELE> ListWithDelim<ELE> genList() {
		return new ListWithDelim<ELE>();
	}

	/**
	 * Generate a list from parameter.
	 *
	 * @param <ELE>
	 *            type parameter of elements
	 * @param eles
	 *            elements to be stored
	 * @return the list generated
	 */
	public static <ELE> ListWithDelim<ELE> genList(ELE... eles) {
		ListWithDelim<ELE> list = genList();
		for (ELE ele : eles)
			list.add(ele);
		return list;
	}

	/**
	 * Generate a list from parameter.
	 *
	 * @param <ELE>
	 *            type parameter of elements
	 * @param eles
	 *            elements to be stored
	 * @return the list generated
	 */
	public static <ELE> ListWithDelim<ELE> genList(Iterable<ELE> eles) {
		ListWithDelim<ELE> list = genList();
		for (ELE ele : eles)
			list.add(ele);
		return list;
	}

	private String delim;

	/**
	 * constructor.
	 */
	public ListWithDelim() {
		super();
		or();
	}

	/**
	 * constructor.
	 *
	 * @param c
	 *            the collection to be copied
	 */
	public ListWithDelim(Collection<? extends E> c) {
		super(c);
		if (ListWithDelim.class.isInstance(c)) {
			setDelim(((ListWithDelim<?>) c).getDelim());
		} else {
			or();
		}
	}

	/**
	 * constructor.
	 *
	 * @param initialCapacity
	 */
	public ListWithDelim(int initialCapacity) {
		super(initialCapacity);
		or();
	}

	/**
	 * Set 'OR' as delimiter.
	 *
	 * @return this object
	 */
	public ListWithDelim<E> or() {
		return setDelim(OR);
	}

	/**
	 * Set 'AND' as delimiter.
	 *
	 * @return this object
	 */
	public ListWithDelim<E> and() {
		return setDelim(AND);
	}

	/**
	 * Set ',' as delimiter.
	 *
	 * @return this object
	 */
	public ListWithDelim<E> comma() {
		return setDelim(COMMA);
	}

	/**
	 * Set CRLF as delimiter.
	 *
	 * @return this object
	 */
	public ListWithDelim<E> crlf() {
		return setDelim(Const.CRLF);
	}

	/**
	 * copy delimiter if the parameter is a instance of ListWithDelim
	 *
	 * @param c
	 * @return this object
	 */
	public ListWithDelim<E> copyDelim(Collection<E> c) {
		if (ListWithDelim.class.isInstance(c)) {
			setDelim(((ListWithDelim<?>) c).getDelim());
		}
		return this;
	}

	/**
	 * Getter of delimiter.
	 *
	 * @return the delimiter
	 */
	public String getDelim() {
		return delim;
	}

	/**
	 * Setter of delimiter.
	 *
	 * @param delim
	 *            the delimiter to set
	 * @return this object.
	 */
	public ListWithDelim<E> setDelim(String delim) {
		this.delim = delim;
		return this;
	}

}
