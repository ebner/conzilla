/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.menu;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import se.kth.cid.component.ComponentException;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.util.TreeTagNodeMenuListener;
import se.kth.cid.conzilla.util.TreeTagNodeMenuWrapper;
import se.kth.cid.style.StyleManager;
import se.kth.cid.tree.TreeTagNode;


public class TypeMenu extends TreeTagNodeMenuWrapper {

    public TypeMenu(
        TreeTagNode treeTagNode,
        TreeTagNodeMenuListener listener) {
        super(treeTagNode, listener);
    }

    public JMenu constructSubMenu(TreeTagNode node) {
        return new TypeMenu(node, getListener());
    }

    public JMenuItem constructMenuItem(final TreeTagNode child) {
        StyleManager styleManager =
            ConzillaKit.getDefaultKit().getStyleManager();
        String typeuri = child.getValue();
        URI typeURI;
        try {
            typeURI = new URI(typeuri);
            Concept typeConcept =
                ConzillaKit
                    .getDefaultKit()
                    .getResourceStore()
                    .getAndReferenceConcept(
                    typeURI);
            child.setUserObject(typeConcept);
            JMenuItem jmi =
                new TypeSummaryComponent.TypeSummaryMenuItem(styleManager, typeConcept);
            jmi.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent arg0) {
                    getListener().selected(child);
                }
            });
            return jmi;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ComponentException e) {
            e.printStackTrace();
        }
        return null;
    }
}