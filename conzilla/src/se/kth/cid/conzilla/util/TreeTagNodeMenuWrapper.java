/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import se.kth.cid.tree.TreeTagNode;

public class TreeTagNodeMenuWrapper extends JMenu implements MenuListener {
    TreeTagNode treeTagNode;
    TreeTagNodeMenuListener listener;
    boolean menumenu;

    
    public class TreeAction extends AbstractAction {
        TreeTagNode ttn;
        public TreeAction(TreeTagNode ttn, String name) {
            super(name);
            this.ttn = ttn;
        }

        public void actionPerformed(ActionEvent ae) {
            listener.selected(ttn);
        }
    }

    public TreeTagNodeMenuWrapper(
            TreeTagNode treeTagNode,
            TreeTagNodeMenuListener listener) {
        this(treeTagNode, listener, false);
    }
    
    public TreeTagNodeMenuWrapper(
        TreeTagNode treeTagNode,
        TreeTagNodeMenuListener listener,
        boolean menumenu) {
        super(
            treeTagNode.getURI().substring(
                treeTagNode.getURI().lastIndexOf('/') + 1));
        this.menumenu = menumenu;
        this.treeTagNode = treeTagNode;
        this.listener = listener;

        addMenuListener(this);
    }

    public TreeTagNodeMenuListener getListener() {
        return listener;
    }
    
    public void menuSelected(MenuEvent menuEvent) {
        removeAll();
        Enumeration e = treeTagNode.children();
        while (e.hasMoreElements()) {
            TreeTagNode childNode = (TreeTagNode) e.nextElement();
            if (menumenu) {
                if (!childNode.isLeaf()) {
                    boolean subChildren = false;
                    Enumeration ee = childNode.children();
                    while (ee.hasMoreElements()) {
                        if (!((TreeTagNode) ee.nextElement()).isLeaf()) {
                            subChildren = true;
                            break;
                        }
                    }
                    if (subChildren) {
                        add(constructSubMenu(childNode));
                    } else {
                        add(constructMenuItem(childNode));
                    }
                }
            } else {
                if (!childNode.isLeaf())
                    add(constructSubMenu(childNode));
                else
                    add(constructMenuItem(childNode));
            }
        }
    }
    
    public JMenu constructSubMenu(TreeTagNode node) {
        return new TreeTagNodeMenuWrapper(node, listener, menumenu);
    }
    
    public JMenuItem constructMenuItem(TreeTagNode child) {
        return new JMenuItem(new TreeAction(child,
                     child.getURI().substring(
                     child.getURI().lastIndexOf('/') + 1)));
    }

    public void menuDeselected(MenuEvent menuEvent) {}
    public void menuCanceled(MenuEvent menuEvent) {}
}
