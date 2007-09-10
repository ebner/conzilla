/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.toolbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import se.kth.cid.component.ComponentException;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.layers.CreateLayer;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.edit.menu.TypeSummaryComponent;
import se.kth.cid.conzilla.util.ArrowBorder;
import se.kth.cid.conzilla.util.TreeTagNodeMenuListener;
import se.kth.cid.conzilla.util.TreeTagNodeMenuWrapper;
import se.kth.cid.style.StyleManager;
import se.kth.cid.tree.TreeTagNode;

/**
 * @author matthias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateTools extends JPanel implements TreeTagNodeMenuListener, ActionListener {
    JPanel createTypes;
    TreeTagNodeMenuWrapper typeMenu;
    JLabel selectedMenuLabel;
    TreeTagNode ttn;
    EditMapManager emm;
    CreateLayer createLayer;
    
    public CreateTools(EditMapManager emm, MapController controller, GridModel gridModel) {
        this.emm = emm;
        this.createLayer = new CreateLayer(controller, gridModel);

        TreeTagNode ttn = ConzillaKit.getDefaultKit().getRootLibrary();
        typeMenu = new TreeTagNodeMenuWrapper(ttn, this, true);
        
        createLayer.setCreateOneAtATimeAction(this);
        initLayout();
        initTypeMenuSelection(ttn);
    }
    
    private void initTypeMenuSelection(TreeTagNode ttn) {
        TreeTagNode parent = ttn;
        TreeTagNode child = (TreeTagNode) ttn.getChildren().firstElement();
        while (!child.isLeaf()) {
            parent = child;
            child = (TreeTagNode) parent.getChildren().firstElement();
        }
        selected(parent);
    }

    protected void initLayout() {

        // the Panel.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //The type label
        selectedMenuLabel = new JLabel("Select type set");
        selectedMenuLabel.setBackground(Color.WHITE);
        selectedMenuLabel.setOpaque(true);
        selectedMenuLabel.setAlignmentX(0.5f);
        selectedMenuLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createCompoundBorder(
                        new ArrowBorder(ArrowBorder.SOUTH, ArrowBorder.EAST),
                        BorderFactory.createEmptyBorder(2,2,2,2))));
        selectedMenuLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (typeMenu.isSelected()) {
                    typeMenu.setSelected(false);
                    //typeMenu.getPopupMenu().show(false);
                } else {
                    typeMenu.setSelected(true);
                    typeMenu.getPopupMenu().show(
                            selectedMenuLabel,
                            0,
                            selectedMenuLabel.getBounds().height);
                }
            }
        });
        
        add(selectedMenuLabel);
        add(Box.createVerticalStrut(10));
        
        //The panel of types
        createTypes = new JPanel();
        createTypes.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        createTypes.setLayout(new BoxLayout(createTypes, BoxLayout.Y_AXIS));
        add(createTypes);
    }
    
    public void selected(TreeTagNode treeTagNode) {
        this.ttn = treeTagNode;
        String text = treeTagNode.getURI().substring(
                treeTagNode.getURI().lastIndexOf('/') + 1);
        selectedMenuLabel.setText(text);
        selectedMenuLabel.revalidate();
        selectedMenuLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, selectedMenuLabel.getPreferredSize().height));
        updateCreateTypes();
    }
    
    JToggleButton selectedCreateTypeToggleButton;
    private boolean pushed = false;
    
    public void updateCreateTypes() {
        createTypes.removeAll();
        
  //      ButtonGroup bg = new ButtonGroup();
        
        Iterator it = ttn.getChildren().iterator();
        while (it.hasNext()) {
            final TreeTagNode child = (TreeTagNode) it.next();
            StyleManager styleManager =
                ConzillaKit.getDefaultKit().getStyleManager();
            String typeuri = child.getValue();
            if (typeuri == null) {
                continue;
            }
            URI typeURI;
            try {
                typeURI = new URI(typeuri);
                final Concept typeConcept =
                    ConzillaKit
                        .getDefaultKit()
                        .getResourceStore()
                        .getAndReferenceConcept(
                        typeURI);
                child.setUserObject(typeConcept);
                TypeSummaryComponent tsc =
                    new TypeSummaryComponent(styleManager, typeConcept);
                final JToggleButton toggle = new JToggleButton();
//                bg.add(toggle);
                toggle.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        if (toggle.isSelected()) {
                            if (selectedCreateTypeToggleButton != null
                                    && selectedCreateTypeToggleButton != toggle) {
                                selectedCreateTypeToggleButton.setSelected(false);
                                selectedCreateTypeToggleButton.setBorder(BorderFactory.createRaisedBevelBorder());                                                            
                            }
                            selectedCreateTypeToggleButton = toggle;
                            setTypeConcept(child);
                            toggle.setBorder(BorderFactory.createLoweredBevelBorder());
                        } else {
                            toggle.setBorder(BorderFactory.createRaisedBevelBorder());                            
                            setTypeConcept(null);
                        }
                    }
                });
                toggle.setLayout(new BorderLayout());
                toggle.add(tsc, BorderLayout.CENTER);
                toggle.setBorder(BorderFactory.createRaisedBevelBorder());
                toggle.setAlignmentX(0.5f);
                toggle.setMinimumSize(new Dimension(150,1));
                createTypes.add(toggle);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ComponentException e) {
                e.printStackTrace();
            }            
        }
        createTypes.add(Box.createVerticalGlue());
    }
    
    protected void setTypeConcept(TreeTagNode child) {
        //createLayer.
        if (child != null) {
            if (!pushed) {
                emm.push(createLayer);
                pushed = true;
            }
            createLayer.selected(child);
            emm.getMoveLayer().disableTextMode(null);
        } else {
            if (pushed) {
                emm.pop(createLayer);
                pushed = false;
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        setTypeConcept(null);
        emm.getMoveLayer().editTextOnMapObject(createLayer.getCreatedMapObject());
        selectedCreateTypeToggleButton.setSelected(false);
        selectedCreateTypeToggleButton.setBorder(BorderFactory.createRaisedBevelBorder());
        selectedCreateTypeToggleButton = null;
    }
}
