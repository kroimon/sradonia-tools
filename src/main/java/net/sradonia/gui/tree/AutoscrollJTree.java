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

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * A {@link JTree} extension that implements the {@link Autoscroll} interface.
 * 
 * @see Autoscroll
 * 
 * @author Stefan Rado
 */
public class AutoscrollJTree extends JTree implements Autoscroll {
	private static final long serialVersionUID = 7107199411876307491L;

	/**
	 * The default autoscroll margin
	 */
	public static final int DEFAULT_AUTOSCROLL_MARGIN = 20;

	/**
	 * The current autoscroll margin
	 */
	protected int autoScrollMargin = DEFAULT_AUTOSCROLL_MARGIN;

	public AutoscrollJTree() {
		super();
	}

	public AutoscrollJTree(Hashtable<?, ?> value) {
		super(value);
	}

	public AutoscrollJTree(Object[] value) {
		super(value);
	}

	public AutoscrollJTree(TreeModel newModel) {
		super(newModel);
	}

	public AutoscrollJTree(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}

	public AutoscrollJTree(TreeNode root) {
		super(root);
	}

	public AutoscrollJTree(Vector<?> value) {
		super(value);
	}

	/**
	 * @return the margin of the so called autoscroll region
	 */
	public int getAutoScrollMargin() {
		return autoScrollMargin;
	}

	/**
	 * @param autoScrollMargin
	 *            the new margin for the autoscroll region
	 */
	public void setAutoScrollMargin(int autoScrollMargin) {
		this.autoScrollMargin = autoScrollMargin;
	}

	public void autoscroll(Point cursorLocation) {
		int realrow = getClosestRowForLocation(cursorLocation.x, cursorLocation.y);
		Rectangle outer = getBounds();
		if (cursorLocation.y + outer.y <= autoScrollMargin) {
			if (realrow < 1)
				realrow = 0;
			else
				realrow -= 1;
		} else {
			if (realrow < getRowCount() - 1)
				realrow += 1;
		}
		scrollRowToVisible(realrow);
	}

	public Insets getAutoscrollInsets() {
		Rectangle outer = getBounds();
		Rectangle inner = getParent().getBounds();
		return new Insets(inner.y - outer.y + autoScrollMargin, inner.x - outer.x + autoScrollMargin, outer.height - inner.height - inner.y + outer.y
				+ autoScrollMargin, outer.width - inner.width - inner.x + outer.x + autoScrollMargin);
	}

}
