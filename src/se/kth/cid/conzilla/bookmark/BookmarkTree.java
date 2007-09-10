/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmark;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;

/**
 * BookmarkTree is derived from JTree, and adapts it for managing BookmarkNodes.
 * This includes proper handling of Drag and Drop events for moving around the
 * nodes, tree management with the help of popup menus, etc.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class BookmarkTree extends JTree implements TreeSelectionListener, DragGestureListener, DropTargetListener,
		DragSourceListener {

	private TreePath selectedTreePath;

	private BookmarkNode selectedNode;

	private DragSource dragSource;
	
	private MapController controller;
	
	/**
	 * @param root
	 *            BookmarkNode to be used as root node.
	 * @param controller
	 *            MapController.
	 */
	public BookmarkTree(BookmarkNode root, MapController controller) {
		super(root);
		this.controller = controller;
		initBookmarkTree();
	}
	
	/**
	 * @param model
	 *            TreeModel to be used as the underlying model for the tree.
	 * @param controller
	 *            MapController.
	 */
	public BookmarkTree(TreeModel model, MapController controller) {
		super(model);
		this.controller = controller;
		initBookmarkTree();
	}
	
	/**
	 * The basic setup for the tree. This makes it different from the normal
	 * JTree.
	 */
	private void initBookmarkTree() {
		addTreeSelectionListener(this);
		BookmarkTreeActions popup = new BookmarkTreeActions(this, controller);
		addMouseListener(popup);
		addKeyListener((KeyListener) popup);
		ToolTipManager.sharedInstance().registerComponent(this);
		setAutoscrolls(true);
		
		// Drag and Drop
		dragSource = DragSource.getDefaultDragSource();
		DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
		new DropTarget(this, this);
		
		// This removes right mouse clicks as valid actions (we have a JPopupMenu)
		dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);
		
		setRowHeight(Images.getImageIcon(Images.ICON_BOOKMARK).getIconHeight() + 2);
		setCustomIcons();
	}
	
	/**
	 * Changes the icons of the tree.
	 */
	private void setCustomIcons() {
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    renderer.setOpenIcon(Images.getImageIcon(Images.ICON_BOOKMARK_FOLDER_OPEN));
	    renderer.setClosedIcon(Images.getImageIcon(Images.ICON_BOOKMARK_FOLDER_CLOSED));
	    renderer.setLeafIcon(Images.getImageIcon(Images.ICON_BOOKMARK));
	    setCellRenderer(renderer);
	}
	
	/**
	 * We show our own tooltips.
	 * 
	 * @see javax.swing.JTree#getToolTipText(java.awt.event.MouseEvent)
	 */
	public String getToolTipText(MouseEvent evt) {
		if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
			return null;
		}
		TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
		return ((BookmarkNode) curPath.getLastPathComponent()).getToolTipText();
	}

	/**
	 * @return Returns the currently selected node.
	 */
	public BookmarkNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
	 */
	public void dragGestureRecognized(DragGestureEvent e) {
		BookmarkNode dragNode = getSelectedNode();
		if (dragNode != null) {
			Transferable transferable = (Transferable) dragNode.getUserObject();
			Cursor cursor = DragSource.DefaultMoveDrop;
			dragSource.startDrag(e, cursor, transferable, this);
		}
	}

	/**
	 * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
	 */
	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	/**
	 * @see java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dragEnter(DragSourceDragEvent dsde) {
	}

	/**
	 * @see java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dragOver(DragSourceDragEvent dsde) {
	}

	/**
	 * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.DragSourceDragEvent)
	 */
	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	/**
	 * @see java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
	 */
	public void dragExit(DragSourceEvent dsde) {
	}

	/**
	 * Handles the drop event. Enables the moving around of nodes within the
	 * tree.
	 * 
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent e) {
		Transferable tr = e.getTransferable();

		// We check whether we can do something with the dropped item
		if (!tr.isDataFlavorSupported(BookmarkInformation.INFO_FLAVOR)) {
			e.rejectDrop();
		}

		// We determine the location and node where the drop happened
		Point loc = e.getLocation();
		TreePath destinationPath = getPathForLocation(loc.x, loc.y);

		// We don't continue if the drop target is not appropriate
		if (!isValidDropTarget(destinationPath, selectedTreePath)) {
			e.rejectDrop();
			return;
		}

		// We determine various nodes
		BookmarkNode newParent = (BookmarkNode) destinationPath.getLastPathComponent();
		BookmarkNode parentOfNewParent = (BookmarkNode) newParent.getParent();
		BookmarkNode oldParent = (BookmarkNode) getSelectedNode().getParent();
		BookmarkNode newChild = getSelectedNode();

		// This is now where the moving around of the node happens
		try {
			if (newParent.isLeaf() || (!newParent.isRoot() && (newParent.getAllowsChildren() && newChild.getAllowsChildren()))) {
				int newPosition = parentOfNewParent.getIndex(newParent);
				parentOfNewParent.insert(newChild, newPosition);
			} else {
				oldParent.remove(newChild);
				newParent.add(newChild);
			}
			e.acceptDrop(DnDConstants.ACTION_MOVE);
		} catch (java.lang.IllegalStateException ise) {
			e.rejectDrop();
		}

		// We finish the process and reload the tree to make the changes visible
		e.getDropTargetContext().dropComplete(true);
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		// model.reload(oldParent);
		// model.reload(newParent);
		model.reload();
		
		// We want to expand the affected parts of the tree
		expandPath(new TreePath(oldParent.getPath()));
		expandPath(new TreePath(newParent.getPath()));
		if (parentOfNewParent != null) {
			expandPath(new TreePath(parentOfNewParent.getPath()));
		}
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
	 */
	public void dragEnter(DropTargetDragEvent e) {
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	public void dragExit(DropTargetEvent e) {
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
	 */
	public void dragOver(DropTargetDragEvent e) {
		Point cursorLocationBis = e.getLocation();
		TreePath destinationPath = getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);

		if (isValidDropTarget(destinationPath, selectedTreePath)) {
			BookmarkNode destNode = (BookmarkNode) destinationPath.getLastPathComponent();
			if (destNode.getChildCount() > 0) {
				expandPath(destinationPath);
			}
			e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
		} else {
			e.rejectDrag();
		}
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
	 */
	public void dropActionChanged(DropTargetDragEvent e) {
	}

	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent evt) {
		selectedTreePath = evt.getNewLeadSelectionPath();
		if (selectedTreePath == null) {
			selectedNode = null;
			return;
		}
		selectedNode = (BookmarkNode) selectedTreePath.getLastPathComponent();
	}

	/**
	 * Determines whether the drop target / dropped element combination is
	 * possible.
	 * 
	 * @param destination
	 *            The drop target.
	 * @param dropper
	 *            The dropped element.
	 * @return True if it is possible to drop the target at the dropped
	 *         location.
	 */
	private boolean isValidDropTarget(TreePath destination, TreePath dropper) {
		if ((dropper == null) || (dropper.getParentPath() == null)) {
			return false;
		}
		
		if (dropper.getParentPath().equals(destination)) {
			return false;
		}
		
		if (destination == null) {
			return false;
		}

		if (destination.equals(dropper)) {
			return false;
		}

		if (dropper.isDescendant(destination)) {
			return false;
		}

		return true;
	}

}