/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionImpl;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.Tracer;

/**
 * A tree to display sessions and its maps. Provides various listeners for
 * proper handling of mouse, key and other events.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class SessionTree extends JTree implements TreeSelectionListener, TreeWillExpandListener, KeyListener, MouseListener {

	private TreePath selectedTreePath;

	private SessionNode selectedNode;

	private MapController controller;
	
	private SessionManager sessionManager;
	
	private SessionPopupInfo popup;
	
	private class SessionTreeCellRenderer extends DefaultTreeCellRenderer {
		
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			SessionNode node = (SessionNode) value;

			switch (node.getType()) {
			case SessionNode.TYPE_CONTEXTMAP:
				setLeafIcon(Images.getImageIcon(Images.ICON_CONTEXT_MAP));
				break;
			case SessionNode.TYPE_CONTRIBUTION:
				setLeafIcon(Images.getImageIcon(Images.ICON_CONTRIBUTION));
				break;
			case SessionNode.TYPE_ROOT:
		    	setOpenIcon(Images.getImageIcon(Images.ICON_SESSIONS_BROWSE));
			    setClosedIcon(Images.getImageIcon(Images.ICON_SESSIONS_BROWSE));
			    setLeafIcon(Images.getImageIcon(Images.ICON_SESSIONS_BROWSE));
			    break;
			case SessionNode.TYPE_SESSION:
				setOpenIcon(Images.getImageIcon(Images.ICON_SESSION_OPEN));
			    setClosedIcon(Images.getImageIcon(Images.ICON_SESSION_CLOSED));
			    break;
			}
		    
		    return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		}

	}
		
	/**
	 * @param root
	 *            BookmarkNode to be used as root node.
	 * @param controller
	 *            MapController.
	 */
	public SessionTree(MapController controller, SessionManager sessionManager) {
		super(new SessionNode("Sessions", SessionNode.TYPE_ROOT));
		this.controller = controller;
		this.sessionManager = sessionManager;
		
		popup = new SessionPopupInfo(this);
		
		initTree();
		refresh();
	}
	
	/**
	 * Initializes tree settings and adds listeners.
	 */
	private void initTree() {
		addTreeSelectionListener(this);
		addTreeWillExpandListener(this);
		addMouseListener(this);
		addKeyListener(this);
		ToolTipManager.sharedInstance().registerComponent(this);
		setAutoscrolls(true);
		setRowHeight(Images.getImageIcon(Images.ICON_SESSION_OPEN).getIconHeight() + 2);
		setCustomIcons();
	}
	
	/**
	 * Removes all sessions from the root-node and adds them from new.
	 */
	public void refresh() {
		((SessionNode) getModel().getRoot()).removeAllChildren();
		addSessionsToRoot();
	}
	
	public void activateMetaDataInformation() {
		addMouseMotionListener(popup.mouseListener);
		addMouseListener(popup.mouseListener);
		popup.activate();
	}
	
	public void deactivateMetaDataInformation() {
		removeMouseMotionListener(popup.mouseListener);
		removeMouseListener(popup.mouseListener);
		popup.deactivate();
	}
	
	/**
	 * Adds sessions as nodes with the root-node as parent.
	 */
	private void addSessionsToRoot() {
		SessionNode root = (SessionNode) getModel().getRoot();
		List sessions = getSessionList(true);
		for (Iterator it = sessions.iterator(); it.hasNext(); ) {
			SessionNode node = new SessionNode(it.next(), SessionNode.TYPE_SESSION);
			root.add(node);
		}
		((DefaultTreeModel) getModel()).reload();
	}
	
	/**
	 * @param node
	 *            Node to be used as parent.
	 * @return True if the maps of a session could be determined. False if there
	 *         went something wrong, e.g. the containers of a session could not
	 *         be loaded.
	 */
	private boolean setChildrenOfSessionNode(SessionNode node) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Session session = (Session) node.getUserObject();
		List maps = getMaps(session, false);
//		List contributions = getContributions(session, false);
//		if (maps == null && contributions == null) {
		if (maps == null) {
			setCursor(Cursor.getDefaultCursor());
			return false;
		}
		List<SessionNode> children = new ArrayList<SessionNode>();
		if (maps != null) {
			for (Iterator it = maps.iterator(); it.hasNext(); ) {
				SessionNode newNode = new SessionNode(it.next(), SessionNode.TYPE_CONTEXTMAP);
				children.add(newNode);
			}
		}
//		if (contributions != null) {
//			for (Iterator it = contributions.iterator(); it.hasNext(); ) {
//				SessionNode newNode = new SessionNode(it.next(), SessionNode.TYPE_CONTRIBUTION);
//				children.add(newNode);
//			}
//		}
		
		Collections.sort(children, new Comparator<SessionNode>() {
			public int compare(SessionNode o1, SessionNode o2) {
				return o1.toString().compareToIgnoreCase(o2.toString());
			}
		});
		node.removeAllChildren();
		for (Iterator it = children.iterator(); it.hasNext(); ) {
			node.add((SessionNode) it.next());
		}
		
		((DefaultTreeModel) getModel()).reload(node);
		setCursor(Cursor.getDefaultCursor());
		return true;
	}
	
	/**
	 * Changes the icons of the tree.
	 */
	private void setCustomIcons() {
		DefaultTreeCellRenderer renderer = new SessionTreeCellRenderer();
	    setCellRenderer(renderer);
	}
	
//	/**
//	 * We show our own tooltips.
//	 * 
//	 * @see javax.swing.JTree#getToolTipText(java.awt.event.MouseEvent)
//	 */
//	public String getToolTipText(MouseEvent evt) {
//		if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
//			return null;
//		}
//		TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
//		return ((SessionNode) curPath.getLastPathComponent()).getToolTipText();
//	}

	/**
	 * @return Returns the currently selected node.
	 */
	public SessionNode getSelectedNode() {
		return selectedNode;
	}
	
	/**
	 * Creates a popup menu which suitable for the specified TreePath.
	 * 
	 * @param path TreePath.
	 */
	private JPopupMenu createMenu(TreePath path) {
		final SessionNode node = (SessionNode) path.getLastPathComponent();
		String uri = null;
		if (node.isLoadable()) {
			uri = node.getURI();
		}
				
		JPopupMenu menu = new JPopupMenu();
		
		if (uri != null) {
			final String uriFinal = uri;
			
			JMenuItem itemOpen = new JMenuItem("Open");
			itemOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openMap(uriFinal, false);
				}
			});
			menu.add(itemOpen);
			
			JMenuItem itemOpenTab = new JMenuItem("Open in new view");
			itemOpenTab.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openMap(uriFinal, true);
				}
			});
			menu.add(itemOpenTab);
		}
		
		if (node.getType() == SessionNode.TYPE_SESSION) {
			JMenuItem itemRename = new JMenuItem("Rename");
			itemRename.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					renameSession(node);
				}
			});
			menu.add(itemRename);
		}
		
		return menu;
	}

    /**
	 * @return Returns a sorted list of available sessions.
	 */
	private List getSessionList(boolean sort) {
		List<Session> sessions = new ArrayList<Session>(sessionManager.getSessions());
		if (sort) {
			Collections.sort(sessions, new Comparator<Session>() {
				public int compare(Session s1, Session s2) {
					if (s1.getTitle() != null) {
						return s1.getTitle().compareToIgnoreCase(s2.getTitle());
					} else {
						return 0;
					}
				}
			});
		}
		return sessions;
    }
	
    /**
	 * @param session
	 *            Session to look in for context-maps.
	 * @return Returns a sorted list of context-maps within a session.
	 */
    private List getMaps(Session session, boolean sort) {
    	List<ContextMap> result = new ArrayList<ContextMap>();
		String uri = session.getContainerURIForLayouts();
		Container container = null;
		ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
		try {
			container = store.getAndReferenceContainer(URI.create(uri));
		} catch (ComponentException ce) {
			Tracer.debug("Layout container of session could not be loaded. URI: " + uri);
			return null;
		}
		List maps = container.getDefinedContextMaps();
		for (Iterator it = maps.iterator(); it.hasNext(); ) {
			URI mapURI = URI.create((String) it.next());
			ContextMap map = null;
			try {
				map = store.getAndReferenceLocalContextMap(mapURI, session);
			} catch (ComponentException e1) {
				continue;
			}
			result.add(map);
		}
		if (sort) {
			Collections.sort(result, new Comparator<ContextMap>() {
				public int compare(ContextMap o1, ContextMap o2) {
					String map1 = new SessionNode(o1, SessionNode.TYPE_CONTEXTMAP).toString();
					String map2 = new SessionNode(o2, SessionNode.TYPE_CONTEXTMAP).toString();
					return map1.compareToIgnoreCase(map2);
				}
			});
		}
		return result;
    }
    
//    private List getContributions(Session session, boolean sort) {
//    	List maps = getMaps(session, false);
//    	List managed = new ArrayList(session.getManaged());
//    	if (maps == null) {
//    		return managed;
//    	}
//    	List mapURIs = new ArrayList();
//    	for (Iterator it = maps.iterator(); it.hasNext(); ) {
//    		ContextMap map = (ContextMap) it.next();
//    		mapURIs.add(map.getURI());
//    	}
//    	managed.removeAll(mapURIs);
//		if (sort) {
//			Collections.sort(managed, new Comparator() {
//				public int compare(Object o1, Object o2) {
//					String map1 = new SessionNode(o1, SessionNode.TYPE_CONTRIBUTION).toString();
//					String map2 = new SessionNode(o2, SessionNode.TYPE_CONTRIBUTION).toString();
//					return map1.compareToIgnoreCase(map2);
//				}
//			});
//		}
//    	return managed;
//    }
    
    /**
	 * Opens a context-map.
	 * 
	 * @param uriString
	 *            URI of the context-map.
	 * @param newView
	 *            Specifies whether the map is supposed to be opened in a new
	 *            view or in the currently displayed one.
	 */
    private void openMap(final String uriString, final boolean newView) {
    	if (uriString != null) {
    		final URI uri = URI.create(uriString);
    		Thread thread = new Thread(new Runnable() {
				public void run() {
		    		try {
		    			if (newView) {
		    				ConzillaKit.getDefaultKit().getConzilla().openMapInNewView(uri, controller);
		    			} else {
		    				ConzillaKit.getDefaultKit().getConzilla().openMapInOldView(uri, controller.getView());
		    			}
		    		} catch (ControllerException e) {
		    			ErrorMessage.showError("Unable to open map", "Conzilla was not able to open the context-map.", e, null);
		    		}
				}
    		});
    		thread.start();
    	}
    }
    
    private void renameSession(final SessionNode node) {  
    	SessionImpl session = (SessionImpl) node.getUserObject();
    	String result = JOptionPane.showInputDialog(SessionTree.this, "Please enter a name for this session", session.getTitle());
    	if (result != null) {
    		session.setTitle(result);
    	}
    }
    
    /* Listeners */
    
	/**
	 * Adds maps to a session-node before it collapses.
	 * 
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
		SessionNode node = (SessionNode) e.getPath().getLastPathComponent();
		if (node.getUserObject() instanceof Session) {
			if (!setChildrenOfSessionNode(node)) {
				throw new ExpandVetoException(e, "Unable to expand the node. Containers or maps could not be loaded.");
			}
		}
	}
	
	public void treeWillCollapse(TreeExpansionEvent e) {
	}

	/**
	 * Sets the selectedNode value if the the selected value in the tree
	 * changes.
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent evt) {
		selectedTreePath = evt.getNewLeadSelectionPath();
		if (selectedTreePath == null) {
			selectedNode = null;
			return;
		}
		selectedNode = (SessionNode) selectedTreePath.getLastPathComponent();
	}

	/**
	 * Opens a map on "Enter".
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		SessionNode node = getSelectedNode();
		if (node == null) {
			return;
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (node.isLoadable()) {
				openMap(node.getURI(), false);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_F2) {
			if (node.getType() == SessionNode.TYPE_SESSION) {
				renameSession(node);
			}
		}
	}
	
	private void handleMouseEvent(MouseEvent e) {
		Point pt = e.getPoint();
		TreePath path = getPathForLocation(pt.x, pt.y);

		if (e.isPopupTrigger()) {
			if (path == null) {
				path = new TreePath(((SessionNode) getModel().getRoot()).getPath());
			} else {
				setSelectionPath(path);
			}
			createMenu(path).show(this, pt.x, pt.y);
		} else {
			if (path == null) {
				return;
			}
			SessionNode node = (SessionNode) path.getLastPathComponent();
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
				if (node.isLoadable()) {
					openMap(node.getURI(), false);
				}
			} else if (SwingUtilities.isMiddleMouseButton(e) && (e.getClickCount() == 1)) {
				if (node.isLoadable()) {
					openMap(node.getURI(), true);
				}
			}
		}
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		handleMouseEvent(e);
	}
	
	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		handleMouseEvent(e);
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		handleMouseEvent(e);
	}

	public void keyTyped(KeyEvent e) {
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

}