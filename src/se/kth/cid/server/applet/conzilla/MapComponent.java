/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import se.kth.cid.graphics.LineDraw;

/**
 * @author matthias
 */
public class MapComponent extends JComponent {

    LinkedList concepts = new LinkedList();
    LinkedList relations = new LinkedList();
    MapApplet ma;

    public MapComponent(MapApplet ma) {
        setVisible(true);
        setLocation(0, 0);
        this.ma = ma;
    }

    public void addRelation(Relation relation) {
        relations.add(new RelationComponent(relation));
    }

    public void addConcept(Concept concept) {
        concepts.add(new ConceptComponent(concept));
    }

    public void removeAll() {
        super.removeAll();
        concepts = new LinkedList();
        relations = new LinkedList();
    }
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
              RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        
        g.setColor(MapApplet.BACKGROUND);
        Dimension d = getSize();
        g.fillRect(0, 0, d.width, d.height);
        for (Iterator coit = concepts.iterator(); coit.hasNext();) {
            ConceptComponent cc = (ConceptComponent) coit.next();
            cc.paintComponent(g);
        }
        for (Iterator relit = relations.iterator(); relit.hasNext();) {
            RelationComponent rc = (RelationComponent) relit.next();
            rc.paintComponent(g);
        }
    }
    public LinkedList getConceptComponents() {
        return concepts;
    }
    public LinkedList getRelationComponents() {
        return relations;
    }
}
