/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmark;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.controller.MapController;

/**
 * Provides a KeyListener for keyboard shortcuts, popup menu creation, and
 * several helper methods for loading context-maps in various ways.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class BookmarkTreeActions extends MouseAdapter implements KeyListener {
	
	private BookmarkTree tree;
		
	private MapController controller;
	
	/**
	 * @param tree
	 *            BookmarkTree to operate on.
	 * @param controller
	 *            MapController.
	 */
	public BookmarkTreeActions(BookmarkTree tree, MapController controller) {
		this.tree = tree;
		this.controller = controller;
	}
	
	/**
	 * Deals with the most common key events. 
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		BookmarkNode node = tree.getSelectedNode();
		if (node == null) {
			return;
		}
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			openMap(((BookmarkInformation)node.getUserObject()).getUri(), controller);
			break;
		case KeyEvent.VK_DELETE:
			deleteNode(node);
			break;
		};
	}
	
	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
	}
	
	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
	}
	
	/**
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		processMouseClicks(e);
	}

	/**
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		processMouseClicks(e);
	}
	
	/**
	 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		processMouseClicks(e);
	}
	
	/**
	 * Creates a popup menu which suitable for the specified TreePath.
	 * 
	 * @param path TreePath.
	 */
	private JPopupMenu createMenu(TreePath path) {
		final BookmarkNode node = (BookmarkNode) path.getLastPathComponent();
		final BookmarkInformation nodeInfo = (BookmarkInformation) node.getUserObject();
		
		JMenuItem itemOpen = new JMenuItem("Open");
		itemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openMap(nodeInfo.getUri(), controller);
			}
		});
		if (nodeInfo.getType() != BookmarkInformation.TYPE_CONTEXTMAP) {
			itemOpen.setEnabled(false);
		}
		
		JMenuItem itemOpenTab = new JMenuItem("Open in new view");
		itemOpenTab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openNodeInNewView(node);
			}
		});
		if (!((nodeInfo.getType() == BookmarkInformation.TYPE_CONTEXTMAP) ||
				(nodeInfo.getType() == BookmarkInformation.TYPE_FOLDER))) {
			itemOpenTab.setEnabled(false);
		}
		if (node.getChildCount() == 0) {
			itemOpenTab.setEnabled(false);
		}
		
		JMenuItem itemNewBookmark = new JMenuItem("New Bookmark...");
		itemNewBookmark.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BookmarkEntryDialog dialog = new BookmarkEntryDialog("New Bookmark", BookmarkInformation.TYPE_CONTEXTMAP, controller);
				if (dialog.showDialog()) {
					BookmarkInformation newInfo = dialog.getBookmarkInformation();
					addEntry(node, newInfo);
				}
			}
		});
		
		JMenuItem itemNewFolder = new JMenuItem("New Folder...");
		itemNewFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BookmarkEntryDialog dialog = new BookmarkEntryDialog("New Folder", BookmarkInformation.TYPE_FOLDER, controller);
				if (dialog.showDialog()) {
					BookmarkInformation newInfo = dialog.getBookmarkInformation();
					addEntry(node, newInfo);
				}
			}
		});
		
		JMenuItem itemNewSeparator = new JMenuItem("New Separator");
		itemNewSeparator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BookmarkInformation newInfo = new BookmarkInformation("----------", BookmarkInformation.TYPE_SEPARATOR);
				addEntry(node, newInfo);
			}
		});
		
		JMenuItem itemDelete = new JMenuItem("Delete");
		itemDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteNode(node);
			}
		});
		if (node.isRoot()) {
			itemDelete.setEnabled(false);
		}
		
		JMenuItem itemProperties = new JMenuItem("Properties");
		itemProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BookmarkEntryDialog dialog = new BookmarkEntryDialog("Properties", nodeInfo, false, controller, null, true);
				if (dialog.showDialog()) {
					BookmarkInformation updatedInfo = dialog.getBookmarkInformation();
					node.setUserObject(updatedInfo);
					dialog.dispose();
				}
			}
		});
		if (node.isRoot() || (nodeInfo.getType() == BookmarkInformation.TYPE_SEPARATOR)) {
			itemProperties.setEnabled(false);
		}
		
		// Assemble all menuitems into one menu
		JPopupMenu menu = new JPopupMenu();
		menu.add(itemOpen);
		menu.add(itemOpenTab);
		menu.add(new JSeparator());
		menu.add(itemNewBookmark);
		menu.add(itemNewFolder);
		menu.add(itemNewSeparator);
		menu.add(new JSeparator());
		menu.add(itemDelete);
		menu.add(new JSeparator());
		menu.add(itemProperties);
		
		return menu;
	}
	
	/**
	 * Creates a new node as sibling or child of an already existing node.
	 * 
	 * @param node
	 *            The destination node.
	 * @param newInfo
	 *            BookmarkInformation of the new node.
	 */
	protected void addEntry(BookmarkNode node, BookmarkInformation newInfo) {
		BookmarkNode parentNode = (BookmarkNode) node.getParent();
		BookmarkNode newNode = new BookmarkNode(newInfo);
		if (node.getAllowsChildren()) {
			node.add(newNode);
		} else {
			if (parentNode != null) {
				int pos = parentNode.getIndex(node);
				parentNode.insert(newNode, pos);
			}
		}
		((DefaultTreeModel) tree.getModel()).reload(newNode.getParent());
		tree.expandPath(new TreePath(((BookmarkNode)newNode.getParent()).getPath()));
	}
	
	/**
	 * Shows a popup menu at the current mouse position.
	 * 
	 * @param e
	 *            MouseEvent.
	 */
	private void showMenu(Point pt) {
		TreePath path = tree.getPathForLocation(pt.x, pt.y);
		if (path == null) {
			path = new TreePath(((BookmarkNode)tree.getModel().getRoot()).getPath());
		} else {
			tree.setSelectionPath(path);
		}
		createMenu(path).show(tree, pt.x, pt.y);
	}
	
	/**
	 * Reacts on mouse clicks.
	 * 
	 * Left double click opens a map in the current view. Middle single click
	 * opens a map in a new view.
	 * 
	 * @param e
	 *            MouseEvent.
	 */
	private void processMouseClicks(MouseEvent e) {
		Point pt = e.getPoint();
		
		if (e.isPopupTrigger()) {
			showMenu(pt);
			return;
		}
		
		TreePath path = tree.getPathForLocation(pt.x, pt.y);
		if (path == null) {
			return;
		}
		
		BookmarkNode node = (BookmarkNode) path.getLastPathComponent();
		BookmarkInformation info = node.getBookmarkInformation();
		if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
			if (info.getType() == BookmarkInformation.TYPE_CONTEXTMAP) {
				openMap(info.getUri(), controller);
			}
		} else if (SwingUtilities.isMiddleMouseButton(e) && (e.getClickCount() == 1)) {
			openNodeInNewView(node);
		}
	}
	
	protected static void openMap(final String uriString, final MapController controller) {
		if (uriString != null) {
			boolean threaded = ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_MAPS_THREADED, false);
			if (threaded) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						openMapUnthreaded(uriString, controller);
					}
				});
				thread.start();
			} else {
				openMapUnthreaded(uriString, controller);
			}
		}
    }
    
    protected static void openMapUnthreaded(final String uriString, final MapController controller) {
    	if (uriString != null) {
    		try {
    			URI uri = new URI(uriString);
    			ConzillaKit.getDefaultKit().getConzilla().openMapInOldView(uri, controller.getView());
    		} catch (Exception e) {
    			e.printStackTrace();
    			JOptionPane.showMessageDialog(null, e.getMessage(), "Could not load context-map", JOptionPane.ERROR_MESSAGE);
    		}
    	}
    }
    
	protected static void openMapInNewView(final String uriString, final MapController controller) {
		if (uriString != null) {
			boolean threaded = ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_MAPS_THREADED, false);
			if (threaded) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						openMapInNewViewUnthreaded(uriString, controller);
					}
				});
				thread.start();
			} else {
				openMapInNewViewUnthreaded(uriString, controller);
			}
    	}
	}
	
	protected static void openMapInNewViewUnthreaded(final String uriString, final MapController controller) {
		if (uriString != null) {
			try {
				URI uri = new URI(uriString);
				ConzillaKit.getDefaultKit().getConzilla().openMapInNewView(uri, controller);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Could not load context-map", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void openNodeInNewView(BookmarkNode node) {
		if (node.getBookmarkInformation().getType() == BookmarkInformation.TYPE_CONTEXTMAP) {
			openMapInNewView(node.getBookmarkInformation().getUri(), controller);
		} else if (node.getBookmarkInformation().getType() == BookmarkInformation.TYPE_FOLDER) {
			for (int i = 0; i < node.getChildCount(); i++) {
				BookmarkNode childNode = (BookmarkNode) node.getChildAt(i);
				BookmarkInformation childInfo = childNode.getBookmarkInformation();
				if (childInfo.getType() == BookmarkInformation.TYPE_CONTEXTMAP) {
					openMapInNewView(childNode.getBookmarkInformation().getUri(), controller);
				}
			}
		}
	}
	
	private void deleteNode(BookmarkNode node) {
		if (!node.isRoot()) {
			BookmarkNode parent = (BookmarkNode) node.getParent();
			parent.remove(node);
			((DefaultTreeModel) tree.getModel()).reload(parent);
		}
	}

}