/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.container;

import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import se.kth.cid.component.Container;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.LayerManager;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * @author matthias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MapInspector extends JPanel{
    ContextMap conceptMap;
    Container container;
    String title;
    int conceptCount;
    int layerCount;
    int reusedConceptCount;
    int linksTo;
    int linksFrom;
    
    public MapInspector(Container container, ContextMap cMap) {
        this.container = container;
        this.conceptMap = cMap;
        init();
        initLayout();
    }
    
    private void init() {
        title = AttributeEntryUtil.getTitleAsString(conceptMap);
        LayerManager lman = conceptMap.getLayerManager();
        Vector layers = lman.getLayers();
        layerCount = layers.size();
        Vector drawerLayouts = lman.getDrawerLayouts(TreeTagNode.IGNORE_VISIBILITY);
        HashSet conceptURIs = new HashSet();
        linksFrom = 0;
//        boolean multiplePresentations = false;
        for (Iterator iter = drawerLayouts.iterator(); iter.hasNext();) {
            DrawerLayout dl = (DrawerLayout) iter.next();
            if (dl.getDetailedMap() != null) {
                linksFrom++;
            }
            if (dl.getLoadContainer().equals(container.getURI())) {
                if (!conceptURIs.add(dl.getConceptURI())) {
//                    multiplePresentations = true;
                }
            }
        }
        conceptCount = conceptURIs.size();
        
        //FIXME reusedConceptCount and linksTo remains to be done.
    }
    
    private void initLayout() {
        removeAll();
        setLayout(new GridLayout(4,2));
        addRow("Title", title);
        addRow("Concept count", conceptCount);
        addRow("Layer count", layerCount);
        addRow("Links from", linksFrom);
    }

    private void addRow(String label, int value) {
        addRow(label, Integer.toString(value));
    }

    private void addRow(String label, String value) {
        add(new JLabel(label+":"));
        JTextField t = new JTextField(value);
        t.setEditable(false);
        add(t);
    }
}
