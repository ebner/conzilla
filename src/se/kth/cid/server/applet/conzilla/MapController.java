/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.event.MouseInputListener;

import se.kth.cid.graphics.LineDraw;
/**
 * @author enok
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MapController {

    MapApplet mapApplet;
    MapModel mapModel;
    LinkedList concepts;
    LinkedList relations;

    JButton back;
    JButton forward;
    /**
     * 
     */
    public MapController(MapApplet ma, MapModel mm) {
        mapApplet = ma;
        mapModel = mm;
        concepts = ma.getMapComponent().getConceptComponents();
        relations = ma.getMapComponent().getRelationComponents();
        MouseInputListener mouseListener = new MapMouseListener();
        ma.getMapComponent().addMouseListener(mouseListener);
        ma.getMapComponent().addMouseMotionListener(mouseListener);

        BrowsePanel bp = mapApplet.getBrowsePanel();
        if (bp != null) {
            JButton[] jbs = bp.getButtons();
            if (jbs != null && jbs.length >= 3) {
                ActionListener al = new BrowseActionListener();
                back = jbs[0];
                forward = jbs[1];
                back.addActionListener(al);
                forward.addActionListener(al);
                jbs[2].addActionListener(al);
            }
        }
    }

    private void update() {
        mapApplet.updateMap();
        concepts = mapApplet.getMapComponent().getConceptComponents();
        relations = mapApplet.getMapComponent().getRelationComponents();
    }

    public class MapMouseListener implements MouseInputListener {

        ConceptComponent currentConceptComponent;
        RelationComponent currentRelationComponent;

        public void mouseClicked(MouseEvent me) {
            for (Iterator iter = concepts.iterator(); iter.hasNext();) {
                ConceptComponent c = (ConceptComponent) iter.next();
                if (c.box.contains(me.getPoint())) {
                    String surfMap = c.getConcept().getSurfMap();
                    if (surfMap != null) {
                        if (!mapModel.isCurrentLast())
                            mapModel.removeForward();
                        mapModel.loadMap(surfMap);
                        forward.setEnabled(false);
                        back.setEnabled(true);
                        update();
                    }
                }
            }
        }
        public void mousePressed(MouseEvent me) {

        }

        public void mouseReleased(MouseEvent me) {
        }

        public void mouseExited(MouseEvent me) {
        }

        public void mouseEntered(MouseEvent me) {
        }

        public void mouseDragged(MouseEvent me) {
        }

        public void mouseMoved(MouseEvent me) {
            boolean handcursor = false;
            if (currentConceptComponent != null) {
                currentConceptComponent.highlight(false);
                currentConceptComponent = null;
            }
            for (Iterator iter = concepts.iterator(); iter.hasNext();) {
                ConceptComponent c = (ConceptComponent) iter.next();

                if (c.hitBox.contains(me.getX(), me.getY())) {           
                    if (c.box.contains(me.getX(), me.getY())
                            || c.box.intersects(new Rectangle(me.getX()-4, me.getY()-4, 8,8))) {
                        currentConceptComponent = c;
                        currentConceptComponent.highlight(true);
                        if (c.getConcept().getSurfMap() != null) {
                            handcursor = true;
                        }
                        break;
                    }
                }                
            }

            if (currentRelationComponent != null) {
                currentRelationComponent.highlight(false);
                currentRelationComponent = null;
            }
            for (Iterator iter = relations.iterator(); iter.hasNext();) {
                RelationComponent r = (RelationComponent) iter.next();
                if (LineDraw
                    .findIntersectionOnPath(me.getX(), me.getY(), r.path)
                    > -1) {
                    currentRelationComponent = r;
                    currentRelationComponent.highlight(true);
                    if (currentRelationComponent.getRelation().getSurfMap()
                        != null) {
                        handcursor = true;
                    }
                }
            }
            mapApplet.getMapComponent().repaint();
            if (handcursor)
                mapApplet.getMapComponent().setCursor(
                    new Cursor(Cursor.HAND_CURSOR));
            else
                mapApplet.getMapComponent().setCursor(
                    new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private class BrowseActionListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            if ("home".equals(ae.getActionCommand())) {
                mapModel.loadMap(MapApplet.STARTMAP);
                if (!mapModel.isCurrentLast())
                    mapModel.removeForward();
                forward.setEnabled(false);
                if (mapModel.isCurrentFirst()) {
                    back.setEnabled(false);
                }
                update();
            } else if ("back".equals(ae.getActionCommand())) {
                Map m = mapModel.getPrevious();
                update();
                forward.setEnabled(true);
                if (mapModel.isCurrentFirst()) {
                    back.setEnabled(false);
                }
            } else if ("forward".equals(ae.getActionCommand())) {
                mapModel.getNext();
                update();
                back.setEnabled(true);
                if (mapModel.isCurrentLast()) {
                    forward.setEnabled(false);
                }
            }
        }
    }
    /* public void mousePressed(MouseEvent me) {
    		if (me.isPopupTrigger()) {
    			System.out.println("Tjoho!");
    			//((ConceptComponent) me.getComponent()).getPopupMenu().show(
    			//me.getComponent(), me.getX(),me.getY());			   
    		} else {
    			mapModel.loadMap(extractConceptComponent(me)
    					.getConcept()
    					.getSurfMap());
    			mapApplet.updateMap();
    		}
    	}*/

    /*	class ConceptMenuActionListener implements ActionListener {
    		private ConceptComponent cc;
    		public ConceptMenuActionListener(ConceptComponent cc) {
    			this.cc = cc;
    			System.out.println("Addar actionlistener");
    		}
    		public void actionPerformed(ActionEvent ae) {
    			System.out.println("Action performed...");
    			if ("surf".equalsIgnoreCase(ae.getActionCommand())) {
    				mapModel.loadMap(cc.getConcept().getSurfMap());
    				System.out.println("Fixar ny...");
    				mapApplet.repaint();
    			}
    		}
            }*/

}
