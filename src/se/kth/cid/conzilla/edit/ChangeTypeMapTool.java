/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JOptionPane;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.concept.Concept;
import se.kth.cid.concept.Triple;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.menu.TypeMenu;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.util.ResourceUtil;
import se.kth.cid.conzilla.util.TreeTagNodeMenuListener;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.tree.TreeTagNode;

import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Changes the type of Concepts or concept-relations.
 * 
 * @author matthias
 */
public class ChangeTypeMapTool extends Tool implements TreeTagNodeMenuListener {

    public ChangeTypeMapTool(MapController cont) {
        super("CHANGE_TYPE", EditMapManagerFactory.class.getName(), cont);
        TreeTagNode ttn = ConzillaKit.getDefaultKit().getRootLibrary();
        TypeMenu tm = new TypeMenu(ttn, this);
        setJMenuItem(tm);
    }
    
    public boolean updateEnabled() {
        boolean b = false;
        if (mapObject.getConcept() != null) {
        	String containerURI = mapObject.getConcept().getLoadContainer();
        	b = mapObject.getDrawerLayout().getConceptMap().getComponentManager().getEditingSesssion().getContainerURIForConcepts().equals(containerURI);
        }
        return b;
    }
    
    public void selected(TreeTagNode treeTagNode) {
        Concept conceptType = (Concept) treeTagNode.getUserObject();
        boolean isProperty = ResourceUtil.isResourceOfClassProperty(conceptType);
        
        mapObject.getDrawerLayout();
        Concept c = mapObject.getConcept();
        if (!isProperty) {
            if (c.getTriple() != null) {
                JOptionPane.showMessageDialog(null, "Cannot change a concept-relatio-n to a concept.");
                return;
            }
            String attribute = RDF.type.toString();    
            for (Iterator aes = c.getAttributeEntry(attribute).iterator(); aes.hasNext();) {
                AttributeEntry ae = (AttributeEntry) aes.next();
                c.removeAttributeEntry(ae); 
            }
            c.addAttributeEntry(attribute, new ResourceImpl(treeTagNode.getValue()));
        } else {
            if (c.getTriple() == null) {
                if (mapObject.getDrawerLayout() instanceof StatementLayout) {
                    //Indicates that something has gone wrong, the tripple is missing.
                    StatementLayout sl = (StatementLayout) (mapObject.getDrawerLayout());
                    if (! sl.isLiteralStatement()) {
                    	
                    	if (JOptionPane.showConfirmDialog(null, "The layout indicates that the concept should be a concept-relation \n" +
                            "but the actual relation is missing, \n" +
                            "(perhaps a container failed to load, then check that first before you go ahed). \n" +
                            "Go ahed and add the relation information?",
                            "Create concept-relation",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    		c.createTriple(sl.getObjectLayout().getConceptURI(), treeTagNode.getValue(), sl.getObjectLayout().getConceptURI(), false);
                    	} else {
                    		return;
                    	}
                    	
                    } else {
                    	if (JOptionPane.showConfirmDialog(null, "The layout indicates that the concept should be a concept-relation with a literal as object \n" +
                            "but the actual relation is missing, \n" +
                            "(perhaps a container failed to load, then check that first before you go ahed). \n" +
                            "Go ahed and add the relation information, the literal will be set arbitrary and you have to change it later?", 
                            "Create concept-relation with literal as object", 
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    		c.createTriple(sl.getObjectLayout().getConceptURI(), treeTagNode.getValue(), "Change me", true);
                    	} else {
                    		return;
                    	}
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Cannot change a concept to a concept-relation, \n" +
                        "there is to little indormation information, create a new one instead!");
                    return;
                }
            }
            Triple triple = c.getTriple();
            triple.setPredicateURI(treeTagNode.getValue());
        }
        mcontroller.getView().getMapScrollPane().getDisplayer().createMapObjects();
        mcontroller.getView().getMapScrollPane().getDisplayer().repaint();
        mcontroller.getConceptMap().getComponentManager().getUndoManager().makeChange();
    }

	public void actionPerformed(ActionEvent e) {
	}
}
