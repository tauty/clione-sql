package tetz42.util;

import java.util.ArrayList;
import java.util.HashMap;

public class Util {

	public static final <K, V> HashMap<K, V> newHashMap(){
		return new HashMap<K, V>();
	}

	public static final <E> ArrayList<E> newArrayList(){
		return new ArrayList<E>();
	}

}
