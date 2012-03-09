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

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author originally by Deudeu (Denis) at http://forum.java.sun.com/thread.jspa?threadID=296255
 * @author modified by Stefan Rado
 */
public abstract class AbstractTreeTransferHandler implements DragGestureListener, DragSourceListener, DropTargetListener {

	private JTree tree;

	private DefaultMutableTreeNode draggedNode;
	private DefaultMutableTreeNode draggedNodeParent;

	protected AbstractTreeTransferHandler(JTree tree, int action) {
		this.tree = tree;

		new DragSource().createDefaultDragGestureRecognizer(tree, action, this);
		new DropTarget(tree, action, this);
	}

	/* ***** Methods for DragGestureListener ***** */
	public void dragGestureRecognized(DragGestureEvent dge) {
		TreePath path = tree.getSelectionPath();
		if (path != null) {
			draggedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			draggedNodeParent = (DefaultMutableTreeNode) draggedNode.getParent();
			dge.getDragSource().startDrag(dge, DragSource.DefaultMoveNoDrop, null, new Point(0, 0), new NodeTransferable(draggedNode), this);
		}
	}

	/* ***** Methods for DragSourceListener ***** */
	public void dragDropEnd(DragSourceDropEvent dsde) {
		if (dsde.getDropSuccess() && dsde.getDropAction() == DnDConstants.ACTION_MOVE && draggedNodeParent != null) {
			TreeModel sourceTreeModel = tree.getModel();
			if (sourceTreeModel instanceof DefaultTreeModel) {
				((DefaultTreeModel) sourceTreeModel).nodeStructureChanged(draggedNodeParent);
			}
			tree.expandPath(new TreePath(draggedNodeParent.getPath()));
			tree.expandPath(new TreePath(draggedNode.getPath()));
		}
	}

	public void dragEnter(DragSourceDragEvent dsde) {
		updateCursorState(dsde);
	}

	public void dragOver(DragSourceDragEvent dsde) {
		updateCursorState(dsde);
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
		updateCursorState(dsde);
	}

	protected void updateCursorState(DragSourceDragEvent dsde) {
		switch (dsde.getDropAction()) {
		case DnDConstants.ACTION_COPY:
			dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
			break;
		case DnDConstants.ACTION_MOVE:
			dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
			break;
		default:
			dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
		}
	}

	public void dragExit(DragSourceEvent dse) {
		dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
	}

	/* ***** Methods for DropTargetListener ***** */
	public void dragEnter(DropTargetDragEvent dtde) {
		checkAction(dtde);
	}

	public void dragOver(DropTargetDragEvent dtde) {
		checkAction(dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		checkAction(dtde);
	}

	protected void checkAction(DropTargetDragEvent dtde) {
		try {
			DefaultMutableTreeNode draggedNode = (DefaultMutableTreeNode) dtde.getTransferable().getTransferData(NodeTransferable.NODE_FLAVOR);
			int action = dtde.getDropAction();
			if (canPerformAction(tree, draggedNode, action, dtde.getLocation())) {
				dtde.acceptDrag(action);
			} else {
				dtde.rejectDrag();
			}
		} catch (Exception e) {
			dtde.rejectDrag();
		}
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void drop(DropTargetDropEvent dtde) {
		try {
			Transferable transferable = dtde.getTransferable();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) transferable.getTransferData(NodeTransferable.NODE_FLAVOR);
			int action = dtde.getDropAction();
			Point location = dtde.getLocation();
			if (canPerformAction(tree, node, action, location)) {
				DefaultMutableTreeNode newParentNode = (DefaultMutableTreeNode) tree.getPathForLocation(location.x, location.y)
						.getLastPathComponent();
				if (executeDrop(tree, node, newParentNode, action)) {
					dtde.acceptDrop(action);
					dtde.dropComplete(true);
					return;
				}
			}
		} catch (Exception e) {
			// if the transferable doesn't support nodes
		}
		dtde.rejectDrop();
		dtde.dropComplete(false);
	}

	protected abstract boolean canPerformAction(JTree target, DefaultMutableTreeNode draggedNode, int action, Point location);

	protected abstract boolean executeDrop(JTree target, DefaultMutableTreeNode draggedNode, DefaultMutableTreeNode newParentNode, int action);
}
