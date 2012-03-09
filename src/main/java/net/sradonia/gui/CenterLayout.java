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
package net.sradonia.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Layout manager that centers the first visible child component.
 * 
 * @author Stefan Rado
 */
public class CenterLayout implements LayoutManager {

	private boolean fillWidth;
	private boolean fillHeight;

	public CenterLayout() {
		this(false, false);
	}

	public CenterLayout(boolean fillWidth, boolean fillHeight) {
		this.fillWidth = fillWidth;
		this.fillHeight = fillHeight;
	}

	private Component getComponent(Container parent) {
		Component[] c = parent.getComponents();
		for (int i = 0; i < c.length; i++) {
			if (c[i].isVisible()) {
				return c[i];
			}
		}
		return null;
	}

	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Component c = getComponent(parent);
			Insets insets = parent.getInsets();
			if (c != null) {
				Dimension d = c.getMinimumSize();
				return new Dimension((int) d.getWidth() + insets.left + insets.right, (int) d.getHeight() + insets.top + insets.bottom);
			} else {
				return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
			}
		}
	}

	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Component c = getComponent(parent);
			Insets insets = parent.getInsets();
			if (c != null) {
				Dimension d = c.getPreferredSize();
				return new Dimension((int) d.getWidth() + insets.left + insets.right, (int) d.getHeight() + insets.top + insets.bottom);
			} else {
				return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
			}
		}
	}

	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Component c = getComponent(parent);
			if (c != null) {
				Insets parentInsets = parent.getInsets();
				Dimension parentSize = parent.getSize();
				Dimension componentSize = c.getPreferredSize();

				int x, width;
				if (!fillWidth) {
					x = parentInsets.left + Math.max((parentSize.width - parentInsets.left - parentInsets.right - componentSize.width) / 2, 0);
					width = componentSize.width;
				} else {
					x = parentInsets.left;
					width = Math.max(parentSize.width - parentInsets.left - parentInsets.right, 0);
				}

				int y, height;
				if (!fillHeight) {
					y = parentInsets.top + (Math.max((parentSize.height - parentInsets.top - parentInsets.bottom - componentSize.height) / 2, 0));
					height = componentSize.height;
				} else {
					y = parentInsets.top;
					height = Math.max(parentSize.height - parentInsets.top - parentInsets.bottom, 0);
				}

				c.setBounds(x, y, width, height);
			}
		}
	}

	public void addLayoutComponent(String name, Component comp) {
		// no-op
	}

	public void removeLayoutComponent(Component comp) {
		// no-op
	}

}
