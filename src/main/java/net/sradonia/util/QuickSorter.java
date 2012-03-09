/*******************************************************************************
 * sradonia tools
 * Copyright (C) 2012 Stefan Rado
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package net.sradonia.util;

import java.util.Comparator;
import java.util.List;

/**
 * Class offering methods for sorting a {@link List} using a quicksort algorithm.
 * 
 * @author Stefan Rado
 */
public class QuickSorter {

	protected QuickSorter() {
		// no-op
	}

	/**
	 * Sorts a {@link List} using a quicksort algorithm.
	 * 
	 * @param list
	 *            the list to sort
	 */
	public static <T> void sort(List<T> list) {
		quicksort(list, null, 0, list.size() - 1);
	}

	/**
	 * Sorts a {@link List} using a quicksort algorithm.
	 * 
	 * @param list
	 *            the list to sort
	 */
	public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
		quicksort(list, comparator, 0, list.size() - 1);
	}

	protected static <T> void quicksort(List<T> list, Comparator<? super T> comparator, int left, int right) {
		// Nothing to sort anymore
		if (left >= right)
			return;

		// Divide and conquer
		int pivot = partition(list, comparator, left, right);
		quicksort(list, comparator, left, pivot - 1);
		quicksort(list, comparator, pivot + 1, right);
	}

	// Split the list from left+1 to right with pivot element list[left]
	protected static <T> int partition(List<T> list, Comparator<? super T> comparator, int left, int right) {
		int i = left + 1;
		int j = right;
		T pivot = list.get(left); // Pivot-Element

		// Pointers move towards center or elements get swapped
		while (i <= j) {
			if (compare(comparator, list.get(i), pivot) <= 0)
				i++;
			else if (compare(comparator, list.get(j), pivot) > 0)
				j--;
			else
				swap(list, i, j);
		}
		// Move pivot element between subsequences
		swap(list, left, j);
		// Return the position of the pivot element
		return j;
	}

	/**
	 * Compares the two objects <code>o1</code> and <code>o2</code> either using the given <code>comparator</code> or (if none given) using their
	 * natural order.
	 * 
	 * @param comparator
	 *            {@link Comparator} to compare the two objects or <code>null</code>
	 * @param o1
	 *            First object
	 * @param o2
	 *            Second object
	 * @return the comparision result
	 */
	@SuppressWarnings("unchecked")
	protected static <T> int compare(Comparator<? super T> comparator, T o1, T o2) {
		if (comparator != null) {
			return comparator.compare(o1, o2);
		} else {
			return ((Comparable<? super T>) o1).compareTo(o2);
		}
	}

	/**
	 * Swaps two elements in a {@link List}.
	 * 
	 * @param list
	 *            the list
	 * @param idx1
	 *            index of the first element
	 * @param idx2
	 *            index of the second element
	 */
	protected static <T> void swap(List<T> list, int idx1, int idx2) {
		T tmp = list.get(idx1);
		list.set(idx1, list.get(idx2));
		list.set(idx2, tmp);
	}

}
