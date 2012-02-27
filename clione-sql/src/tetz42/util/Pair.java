/*
 * Copyright 2011 tetsuo.ohta[at]gmail.com
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
package tetz42.util;

public class Pair<T1, T2> {

	public static <T1, T2> Pair<T1, T2> newPair() {
		return new Pair<T1, T2>();
	}

	public static <T1, T2> Pair<T1, T2> pair(T1 first, T2 second) {
		Pair<T1, T2> pair = newPair();
		pair.setFirst(first);
		pair.setSecond(second);
		return pair;
	}

	private T1 first;
	private T2 second;

	public T1 getFirst() {
		return first;
	}

	public void setFirst(T1 first) {
		this.first = first;
	}

	public T2 getSecond() {
		return second;
	}

	public void setSecond(T2 second) {
		this.second = second;
	}
}
