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
package net.sradonia.gui.tree;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import net.sradonia.util.QuickSorter;

/**
 * @author Stefan Rado
 */
public class SortedMutableTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 3530047806410368015L;

	protected Comparator<? super MutableTreeNode> comparator;

	public SortedMutableTreeNode() {
		super();
	}

	public SortedMutableTreeNode(Object userObject) {
		super(userObject);
	}

	public SortedMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	public SortedMutableTreeNode(Object userObject, boolean allowsChildren, Comparator<? super MutableTreeNode> comparator) {
		super(userObject, allowsChildren);
		setComparator(comparator);
	}

	@SuppressWarnings("unchecked")
	public void setComparator(Comparator<? super MutableTreeNode> comparator) {
		this.comparator = comparator;
		if (children != null && children.size() > 0)
			QuickSorter.sort(children, comparator);
	}

	public Comparator<? super MutableTreeNode> getComparator() {
		return comparator;
	}

	@Override
	public void add(MutableTreeNode newChild) {
		insert(newChild, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insert(MutableTreeNode newChild, int childIndex) {
		if (newChild != null && newChild.getParent() == this) {
			remove(newChild);
		}

		if (children == null) {
			childIndex = 0;
		} else {
			childIndex = Collections.binarySearch(children, newChild, comparator);
			if (childIndex < 0) {
				childIndex = -childIndex - 1;
			}
		}
		super.insert(newChild, childIndex);
	}
}
