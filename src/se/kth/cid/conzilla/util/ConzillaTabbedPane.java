/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * @author matthias
 */
public class ConzillaTabbedPane extends JPanel {
	
    static class TabStruct {
        public Component tab;
        public String title;
        public ActionListener actionListener;
        public boolean closeButton;
        
        public TabStruct(Component tab, 
                String title, 
                ActionListener actionListener, 
                boolean closeButton) {
            this.tab = tab;
            this.title = title;
            this.actionListener = actionListener;
            this.closeButton = closeButton;
        }
    }
    
    Vector tabStructs = new Vector();
    JTabbedPane tabbedPane;
    
    public ConzillaTabbedPane(int tabPlacement) {
    	tabbedPane = new JTabbedPane(tabPlacement);
    	setLayout(new BorderLayout());
    }
    
    public void addTab(Component comp, String title, ActionListener al, boolean closeButton) {
    	if (hasComponent(comp)) {
    		return;
    	}
    	TabStruct newStruct = new TabStruct(comp, title, al, closeButton);
        tabStructs.add(newStruct);
        rebuild();
    }
    
    public boolean isEmpty() {
        return tabStructs.size() == 0;
    }
    
    public void removeTab(Component comp) {
        boolean removed = false;
        for (Iterator it = tabStructs.iterator(); it.hasNext();) {
            TabStruct ts = (TabStruct) it.next();
            if (ts.tab.equals(comp)) {
                it.remove();

                if (ts.actionListener != null) {
                    ts.actionListener.actionPerformed(new ActionEvent(ts, ActionEvent.ACTION_PERFORMED, null));
                }
                
                removed = true;
                break;
            }
        }
        
        if (removed) {
            rebuild();
        }
    }
    
    public boolean hasComponent(Component comp) {
    	for (Iterator it = tabStructs.iterator(); it.hasNext();) {
            TabStruct ts = (TabStruct) it.next();
            if (ts.tab.equals(comp)) {
            	return true;
            }
    	}
    	return false;
    }
    
    protected void rebuild() {
        removeAll();
        
        if (tabStructs.size() == 1) {
            TabStruct ts = (TabStruct) tabStructs.firstElement();
            add(constructPanelForTab(ts), BorderLayout.CENTER);
        } else if (tabStructs.size() > 1) {
            add(tabbedPane, BorderLayout.CENTER);
            tabbedPane.removeAll();
            for (Iterator it = tabStructs.iterator(); it.hasNext();) {
                TabStruct ts = (TabStruct) it.next();
//                VTextIcon textIcon = new VTextIcon(tabbedPane, ts.title, VTextIcon.ROTATE_LEFT);
                //tabbedPane.addTab(null, textIcon, constructPanelForTab(ts));
                tabbedPane.addTab(ts.title, null, constructPanelForTab(ts));
//                tabbedPane.addTab(null, Images.getImageIcon(Images.ICON_FULLSCREEN), constructPanelForTab(ts));
//                tabbedPane.addTab(ts.title, constructPanelForTab(ts));
            }
            if (tabbedPane.getTabCount() > tabStructs.size()) {
            	tabbedPane.setSelectedIndex(tabStructs.size() - 1);
            } else {
            	tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }
        }
        invalidate();
        repaint();
    }

    private Component constructPanelForTab(final TabStruct ts) {
        if (!ts.closeButton) {
            return ts.tab;
        } else {
            final JPanel vert = new JPanel();
            vert.setLayout(new BorderLayout());
            vert.add(ts.tab, BorderLayout.CENTER);
            vert.add(new JButton(new AbstractAction("Close") {
                public void actionPerformed(ActionEvent e) {
                    removeTab(ts.tab);
                }
            }),BorderLayout.SOUTH);
            return vert;
        }
    }
 
}
