/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.PopupMenu;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
/**
 * @author enok
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MapApplet extends JApplet {

    public final static Color BACKGROUND = new Color(255, 255, 153);
    public final static String STARTMAP = null;

    MapModel t;
    PopupMenu pm;
    Map map;
    MapComponent mc;
    MapController mctrl;
    JTextArea ta;
    BrowsePanel bp;

    public MapApplet() throws HeadlessException {
        super();
    }

    public void init() {
        this.setSize(300, 300);
        t = new MapModel();
        map = t.getCurrentMap();

        Vector v = map.getConcepts();
        Vector vv = map.getRelations();

        mc = new MapComponent(this);
        mc.setSize(this.getPreferredSize());
        mc.setBackground(MapApplet.BACKGROUND);

        for (int i = 0; i < v.size(); i++)
            mc.addConcept((Concept) v.get(i));
        for (int i = 0; i < vv.size(); i++)
            mc.addRelation((Relation) vv.get(i));

        bp = new BrowsePanel();
        getContentPane().add(bp, BorderLayout.NORTH);
        getContentPane().add(mc, BorderLayout.CENTER);
        //addTextArea();
        getContentPane().validate();
        mctrl = new MapController(this, t);
        super.init();
    }

    public void stop() {
        super.stop();
    }

    /*	public void addTextArea() {
    		ta = new JTextArea();
    		ta.setPreferredSize(new Dimension(10, 20));
    		ta.setBounds(10, 10, 10, 20);
    		ta.setLocation(0, 0);
    		ta.setSize(10, 20);
    		ta.setText("");
    		ta.setVisible(true);
    		this.getContentPane().add(ta, BorderLayout.SOUTH);
    	}*/

    public void setTextAreaText(String s) {
        if (ta != null)
            ta.setText(s);
    }

    public void removeTextAreaText() {
        if (ta != null) {
            ta.setText("");
        }
    }

    public void updateMap() {
        map = t.getCurrentMap();
        mc.removeAll();

        Vector v = map.getConcepts();
        Vector vv = map.getRelations();
        for (int i = 0; i < v.size(); i++)
            mc.addConcept((Concept) v.get(i));
        for (int i = 0; i < vv.size(); i++)
            mc.addRelation((Relation) vv.get(i));

        this.repaint();
    }

    public MapComponent getMapComponent() {
        return mc;
    }

    public BrowsePanel getBrowsePanel() {
        return bp;
    }
}
