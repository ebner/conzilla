/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.bookmarkrdf;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JTree;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.properties.Images;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @version  $Revision$, $Date$
 * @author ioana
 */

public class BookmarksTree extends JTree implements TreeSelectionListener {
    Model model;
    Resource root;
    FormItemPane bookmarksitem;
    JMenu menu = new JMenu();
    ConzillaKit kit;
  
    public AbstractAction newBMfolder;
    public AbstractAction removeCmBm;
    public AbstractAction removebmfolder;

    public class ResourceCellRenderer extends DefaultTreeCellRenderer {

        Icon root, roots;
        Icon cmbm, cmbms;
        Icon bme, bmes;

        public ResourceCellRenderer() {            
            bme= Images.getImageIcon(Images.ICON_BOOKMARK_ENVIRONMENT);
            bmes= Images.getImageIcon(Images.ICON_BOOKMARK_ENVIRONMENT_SELECTED);
            cmbm= Images.getImageIcon(Images.ICON_BOOKMARK_CONTEXTMAP);
            cmbms= Images.getImageIcon(Images.ICON_BOOKMARK_CONTEXTMAP_SELECTED);            
//            root = IconUtil.getFormItem("Root");
//            roots = IconUtil.getFormItem("Root-Selected");
        }
        
   /**
         * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
         */
        public Component getTreeCellRendererComponent(
            JTree tree,
            Object node,
            boolean selected,
            boolean arg3,
            boolean arg4,
            int arg5,
            boolean arg6) {
            super.getTreeCellRendererComponent(
                tree,
                node,
                selected,
                arg3,
                arg4,
                arg5,
                arg6);
            TreeNode resourceNode = (TreeNode) node;
            switch (resourceNode.getType()) {
                case TreeNode.ROOT :
                  setIcon(selected ? roots : root);
                break;
                case TreeNode.BOOKMARKFOLDER :
                    setIcon(selected ? bmes : bme);
                    break;
                case TreeNode.CMBOOKMARK :
                    setIcon(selected ? cmbms : cmbm);
                    break;
                    //TO DO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                case TreeNode.CONCEPTBOOKMARK :
                                  setIcon(selected ? roots : root);
                                break;
                case TreeNode.CONCEPTINCONTEXTBOOKMARK :
                                                  setIcon(selected ? roots : root);
                                                break;
            }
            return this;
        }
    }

    public BookmarksTree(Model model, Resource root, FormItemPane shame) {
        this.model = model;
        this.root = root;
        this.bookmarksitem = shame;
        this.kit = ConzillaKit.getDefaultKit();

        initActions();
        menu.add(newBMfolder);
        menu.add(removebmfolder);
        menu.add(removeCmBm);
        
        getSelectionModel().addTreeSelectionListener(this);
        initTreeModel();
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);

        MouseInputAdapter dragListener = new MouseInputAdapter() {
            public void mouseDragged(MouseEvent e) {
                drag(e);
            }
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    TreePath selectionPath = getSelectionPath();
                    if (selectionPath != null) {
                        TreeNode selectedNode = (TreeNode) selectionPath.getLastPathComponent();
                        TreePath overPath = getClosestPathForLocation(e.getX(), e.getY());
                        if (overPath != null && overPath.getLastPathComponent() == selectedNode) {
                            menu.getPopupMenu().show(BookmarksTree.this, e.getX(), e.getY());
                        }
                    }
                }
                initDrag(e);
            }
            public void mouseReleased(MouseEvent e) {
                stopDrag(e);
            }
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    launchEditor();
                }
            }
        };

        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
        setCellRenderer(new ResourceCellRenderer());
    }

    private void initActions() {
        newBMfolder = new AbstractAction("Create a new Bookmarks Folder here.") {
                    public void actionPerformed(ActionEvent arg0) {
                            ((TreeNode) BookmarksTree
                                .this
                                .getSelectionPath()
                                .getLastPathComponent())
                                .createAndAddChild(TreeNode.BOOKMARKFOLDER, bookmarksitem);
                                //should also activate the shame editor
                    }
                };
                
    removebmfolder = new AbstractAction("Remove this Bookmarks Folder.") {
             public void actionPerformed(ActionEvent arg0) {
             //    shame.editFormItem(null);
                 ((TreeNode) BookmarksTree
                         .this
                         .getSelectionPath()
                         .getLastPathComponent()).deleteMyselfAndChildrenAndParentConnection(bookmarksitem);
             }
         };
                 
        removeCmBm = new AbstractAction("Remove this Bookmark.") {
             public void actionPerformed(ActionEvent arg0) {
             //    shame.editFormItem(null);
                 ((TreeNode) BookmarksTree
                         .this
                         .getSelectionPath()
                         .getLastPathComponent()).deleteBookmarkLeaf(bookmarksitem);                         
             }
         };        
    }

    public void initTreeModel() {
        DefaultMutableTreeNode rootNode = new TreeNode(this, root);
        setModel(new DefaultTreeModel(rootNode));
    }

    // DRAGGING
    private TreePath dragStartPath = null;

    private void initDrag(MouseEvent e) {
        dragStartPath = getPathForLocation(e.getX(), e.getY());
        if (dragStartPath == null)
            return;

        if (dragStartPath.getPathCount() == 1) {
            dragStartPath = null;
        }

    }

    private void drag(MouseEvent e) {

        TreePath dragStopPath = getPathForLocation(e.getX(), e.getY());

        if (dragStopPath != null
            && dragStartPath != null
            && !dragStartPath.equals(dragStopPath)
            && dragStartPath.getParentPath().equals(
                dragStopPath.getParentPath())) {
            TreeNode startNode =
                (TreeNode) dragStartPath.getLastPathComponent();
            TreeNode stopNode =
                (TreeNode) dragStopPath.getLastPathComponent();

            TreeNode parent = (TreeNode) startNode.getParent();
    
            parent.switchOrder(startNode,stopNode);
 
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            treeModel.nodeStructureChanged(parent);
            setSelectionPath(dragStartPath);
        }
    }

    private void stopDrag(MouseEvent e) {
        dragStartPath = null;
    }

    TreeNode resourceNodeEdited = null;

    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void launchEditor() {
        DefaultTreeModel treeModel = (DefaultTreeModel) getModel();

        if (resourceNodeEdited != null) {
            treeModel.nodeChanged(resourceNodeEdited);
        }

        TreePath path = getSelectionPath();
        if (path == null) {
            resourceNodeEdited = null;
            return;
        }

        resourceNodeEdited = (TreeNode) path.getLastPathComponent();
        treeModel.nodeChanged(resourceNodeEdited);
        bookmarksitem.editFormItem(resourceNodeEdited);
        bookmarksitem.revalidate();
        bookmarksitem.repaint();
    }
    
    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
  
    public void valueChanged(TreeSelectionEvent arg0) {
        TreePath path = arg0.getNewLeadSelectionPath();
        if (path != null) {
            TreeNode rn = (TreeNode) path.getLastPathComponent();
//            boolean parentResourceNode = rn.getParent() == null;
            newBMfolder.setEnabled(
                rn.isBookmarkEnvironmentItem()
                    || (rn.getParent() == null && rn.getChildCount() == 0));

            removebmfolder.setEnabled(rn.getParent() != null && rn.isBookmarkEnvironmentItem());
            removeCmBm.setEnabled(rn.isContextMapBookmark() || rn.isConceptAloneBookmark() || rn.isConceptInContextBookmark());
        } else {
            newBMfolder.setEnabled(false);
            removebmfolder.setEnabled(false);
            removeCmBm.setEnabled(false);
        }
    }

}