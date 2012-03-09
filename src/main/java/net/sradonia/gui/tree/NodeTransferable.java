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

import java.awt.datatransfer.*;
import javax.swing.tree.*;

/**
 * @author originally by Deudeu (Denis) at http://forum.java.sun.com/thread.jspa?threadID=296255
 * @author modified by Stefan Rado
 */
public class NodeTransferable implements Transferable {
	public final static DataFlavor NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreeNode");
	private final static DataFlavor[] FLAVORS = { NODE_FLAVOR };

	private TreeNode node;

	public NodeTransferable(TreeNode node) {
		this.node = node;
	}

	public TreeNode getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor == NODE_FLAVOR) {
			return node;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor == NODE_FLAVOR);
	}
}
