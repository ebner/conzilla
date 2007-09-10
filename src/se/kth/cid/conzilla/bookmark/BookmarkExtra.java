/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmark;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import se.kth.cid.collaboration.CollaborillaConfiguration;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.view.StatusBar;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * Extra for managing bookmarks. Adds a menu to the main window.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class BookmarkExtra implements Extra {

    public static final String BOOKMARKS = "BOOKMARKS";
    
    private BookmarkStore store;
    
    private Map visibleViews = new HashMap();
    
    /**
     * @see se.kth.cid.conzilla.app.Extra#getName()
     */
    public String getName() {
        return "Bookmark Extra";
    }

    /**
     * @see se.kth.cid.conzilla.app.Extra#initExtra(se.kth.cid.conzilla.app.ConzillaKit)
     */
    public boolean initExtra(ConzillaKit kit) {
        if (kit.getMenuFactory() == null) {
            return false;
        }
		kit.getMenuFactory().addExtraMenu(BOOKMARKS, BookmarkExtra.class.getName(), 60);
		store = kit.getBookmarkStore();
		return true;
    }

    /**
     * @see se.kth.cid.conzilla.app.Extra#extendMenu(se.kth.cid.conzilla.tool.ToolsMenu, se.kth.cid.conzilla.controller.MapController)
     */
    public void extendMenu(final ToolsMenu tm, final MapController mc) {
    	if (tm.getName().equals(BOOKMARKS)) {
    		// Tweak: we need the next line (even though it's redundant considering
    		// initMenu()) to be able to do a Ctrl+D before we click on the
    		// Bookmarks menu for the first time (it's not created before we
    		// click on it)
    		tm.addTool(getAddBookmark(tm, mc, new BookmarkTree(store.getTreeModel(), mc)), 0);
    		tm.addMenuListener(new MenuListener() {
    			public void menuSelected(MenuEvent arg0) {
    				initMenu(tm, mc);
    			}
    			public void menuCanceled(MenuEvent arg0) {}
    			public void menuDeselected(MenuEvent arg0) {}
    		});
    	} else if (tm.getName().equals(BrowseMapManagerFactory.BROWSE_MENU)) {
    		// JMenuItem for the map's popup menu goes in here
    	}
    }

    /**
	 * Initializes the bookmarks menu. Adds the whole bookmarks tree at the
	 * bottom.
	 * 
	 * @param toolsMenu
	 *            ToolsMenu.
	 * @param controller
	 *            MapController.
	 */
	private void initMenu(ToolsMenu toolsMenu, MapController controller) {
		toolsMenu.removeAllTools();
        toolsMenu.removeAll();
        
        BookmarkTree tree = new BookmarkTree(store.getTreeModel(), controller);

        toolsMenu.addTool(getAddBookmark(toolsMenu, controller, tree), 100);
        toolsMenu.addTool(getManageBookmarks(toolsMenu, controller, tree), 200);
        
        JMenu bookmarksTreeMenu = (JMenu) createMenuTree((BookmarkNode)store.getTreeModel().getRoot(), controller); 
        if (bookmarksTreeMenu != null) {
        	toolsMenu.addSeparator(300);
        	Component[] components = bookmarksTreeMenu.getMenuComponents();
        	for (int i = 0; i < components.length; i++) {
        		toolsMenu.add(components[i]);
        	}
        }
    }

    /**
	 * Creates and returns the "Manage bookmarks" Tool.
	 * 
	 * @param tm
	 *            ToolsMenu.
	 * @param mc
	 *            MapController.
	 * @param tree
	 *            BookmarkTree.
	 * @return Tool for showing the bookmarks manager.
	 */
	private Tool getManageBookmarks(final ToolsMenu tm, final MapController mc, final BookmarkTree tree) {
		Tool t = new Tool("Manage Bookmarks...", null) {
            public void actionPerformed(ActionEvent ae) {
            	final View activeView = mc.getView();
            	if (visibleViews.containsKey(activeView)) {
            		activeView.removeFromLeft((JScrollPane) visibleViews.get(activeView));
            		visibleViews.remove(activeView);
            	}
            	JScrollPane scrollpane = new JScrollPane(tree);
            	mc.getView().addToLeft(scrollpane, "Bookmarks", new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
            			visibleViews.remove(activeView);
            		}
            	});
            	visibleViews.put(activeView, scrollpane);
            }
        };
        return t;
    }

    /**
	 * Creates and returns the "Add bookmark" Tool.
	 * 
	 * @param tm
	 *            ToolsMenu.
	 * @param mc
	 *            MapController.
	 * @param tree
	 *            BookmarkTree.
	 * @return Tool for adding a bookmark.
	 */
    private Tool getAddBookmark(final ToolsMenu tm, final MapController mc, final BookmarkTree tree) {
        Tool t = new Tool("Bookmark this Context-Map...", null) {
            public void actionPerformed(ActionEvent ae) {
            	CollaborillaConfiguration collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
            	String namespace = collabConfig.getUserNamespace();
            	String contextMapURI = mc.getConceptMap().getURI(); 
            	
            	// We check whether the context-map is local by looking whether the URI starts
            	// with the current users namespace. A cleaner (and much slower) alternative would
            	// be to iterate through all sessions and check whether they contain this map.
            	// Since this would take too much time with lots of session, we don't do this.
            	// If the namespace is empty we ask as well.
            	if ((namespace.length() == 0) || !contextMapURI.startsWith(namespace)) {
                	StatusBar statusBar = mc.getView().getStatusBar();
                	if (!statusBar.isMapPublished() && statusBar.isContainerPublished()) {
                		int answer = JOptionPane.showConfirmDialog(null,
                				"This context-map has not been explicitly published. It is not recommended to bookmark\n" +
                				"an unpublished context-map, as you will not be able to load it again if you have not\n" +
                				"loaded its container before.\n\n" +
                				"Are you sure you want to continue and bookmark this context-map?",
                				"This context-map is not published",
                				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                		if (answer == JOptionPane.NO_OPTION) {
                			return;
                		}
                	}
            	}
            	            	
            	BookmarkInformation preInfo = new BookmarkInformation();
            	preInfo.setType(BookmarkInformation.TYPE_CONTEXTMAP);
            	preInfo.setUri(mc.getConceptMap().getURI());
            	preInfo.setName(AttributeEntryUtil.getTitleAsString(mc.getConceptMap()));
            	
				BookmarkEntryDialog dialog = new BookmarkEntryDialog("Add Bookmark", preInfo, true, mc, null, false);
				if (dialog.showDialog()) {
					BookmarkInformation newInfo = dialog.getBookmarkInformation();
					BookmarkTreeActions actions = new BookmarkTreeActions(tree, mc);
					BookmarkNode selectedNode = dialog.getSelectedNode();
					if (selectedNode != null) {
						actions.addEntry(selectedNode, newInfo);
					} else {
						actions.addEntry((BookmarkNode)tree.getModel().getRoot(), newInfo);
					}
				}
            }
        };
        t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
        return t;
    }
        
	/**
	 * Recursively creates a menu tree consisting of JMenu/JMenuItem/JSeparator,
	 * with a specific node as the starting point.
	 * 
	 * @param node
	 *            BookmarkNode to start at.
	 * @param mc
	 *            MapController.
	 * @return Returns a menu item. Can be JMenu, JMenuItem, or JSeparator.
	 */
	private Object createMenuTree(final BookmarkNode node, final MapController mc) {
		// We don't have a tree if we just have root-node
		if (node.isRoot() && (node.getChildCount() == 0)) {
			return null;
		}
		
		// If we have a leaf-node we return a JMenuItem
		if (node.isLeaf()) {
			return new JMenuItem(node.getBookmarkInformation().toString());
		}
		
		// We need a JMenu because a JMenuItem cannot have children
		final JMenu result = new JMenu(node.getBookmarkInformation().toString());
		configureMenu(result, node);

		// We iterate over all children of a node and call this method with them.
		// Then we add them to the resulting JMenu.
		for (int i = 0; i < node.getChildCount(); i++) {
			BookmarkNode nextNode = (BookmarkNode) node.getChildAt(i);
			if (nextNode.getBookmarkInformation().getType() == BookmarkInformation.TYPE_SEPARATOR) {
				result.add(new JSeparator());
			} else {
				JMenuItem newItem = (JMenuItem) createMenuTree(nextNode, mc);
				configureMenuItem(newItem, nextNode, mc);
				result.add(newItem);
			}
		}
		
		return result;
	}
	
	private void configureMenuItem(JMenuItem item, final BookmarkNode node, final MapController mc)  {
		if (node.getBookmarkInformation().getType() == BookmarkInformation.TYPE_FOLDER) {
			return;
		}
		final BookmarkInformation info = node.getBookmarkInformation();
		item.setIcon(Images.getImageIcon(Images.ICON_BOOKMARK));
		
		// We deactivate the tooltips for now. They are pretty annoying
		// because they hide the title of the bookmarks themselves.
		// item.setToolTipText(node.getToolTipText());
		
		// For some reason JMenuItem does not execute MouseListeners,
		// so we take an ActionListener
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getModifiers() == ActionEvent.ALT_MASK) {
					BookmarkTreeActions.openMapInNewView(info.getUri(), mc);
				} else {
					BookmarkTreeActions.openMap(info.getUri(), mc);
				}
			}
		});
	}
	
	private void configureMenu(final JMenu menu, final BookmarkNode node) {
		if (node.getBookmarkInformation().getType() != BookmarkInformation.TYPE_FOLDER) {
			return;
		}
		menu.setToolTipText(node.getToolTipText());
		menu.setIcon(Images.getImageIcon(Images.ICON_BOOKMARK_FOLDER_CLOSED));
		menu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int state = e.getStateChange();
				if (state == ItemEvent.DESELECTED) {
					menu.setIcon(Images.getImageIcon(Images.ICON_BOOKMARK_FOLDER_CLOSED));
				} else if (state == ItemEvent.SELECTED) {
					menu.setIcon(Images.getImageIcon(Images.ICON_BOOKMARK_FOLDER_OPEN));
				}
			}
		});
		// We need a MouseListener because JMenu does not execute
		// ActionListeners (they are just inherited from JMenuItem)
		menu.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if ((e.getButton() == MouseEvent.BUTTON2) && (e.getClickCount() == 1)) {
					int itemCount = menu.getMenuComponentCount();
					for (int i = 0; i < itemCount; i++) {
						Component comp = menu.getMenuComponent(i);
						if (comp instanceof JMenuItem) {
							JMenuItem item = (JMenuItem) comp;
							ActionListener[] listeners = item.getActionListeners();
							for (int j = 0; j < listeners.length; j++) {
								ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), "middleclick", ActionEvent.ALT_MASK);
								listeners[j].actionPerformed(ae);
							}
						}
					}
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
	}

    /**
     * @see se.kth.cid.conzilla.app.Extra#addExtraFeatures(se.kth.cid.conzilla.controller.MapController, java.lang.Object, java.lang.String, java.lang.String)
     */
    public void addExtraFeatures(MapController c) {
	}

    /**
     * @see se.kth.cid.conzilla.app.Extra#refreshExtra()
     */
    public void refreshExtra() {
    }

    /**
     * @see se.kth.cid.conzilla.app.Extra#saveExtra()
     */
    public boolean saveExtra() {
        return true;
    }

    /**
     * @see se.kth.cid.conzilla.app.Extra#exitExtra()
     */
    public void exitExtra() {
    }

}