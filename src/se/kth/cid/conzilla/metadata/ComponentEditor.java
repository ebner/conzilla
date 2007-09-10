/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * TODO: Description
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class ComponentEditor extends JFrame{
    JTabbedPane tabs;

    public class ResourceStruct {
        Component component;
        String label;
        
        public ResourceStruct(Component component) {
            this.component = component;
            AttributeEntry ae = AttributeEntryUtil.getTitle(component);
            label = ae != null ? ae.getValue() : null;
        }
        
        public String toString() {
            return label;
        }
    }

    public ComponentEditor() {
        initLayout();
    }
    
    private void initLayout() {
        setLocation(0, 0);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Resource metadata editor");
        tabs = new JTabbedPane(JTabbedPane.LEFT);

        WindowAdapter wa = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
             //finish the editor off
                finishEditingAll();
            //Mark the model as edited, otherwise we can't save it.
                //saveComponent(container);
            }
        };

        addWindowListener(wa);
        //  cframe.getContentPane().setLayout(new BorderLayout());
        setContentPane(tabs);
    }
    
    public void editComponent(Component component) {
//        InfoPanel editor = new InfoPanel();
    }
    
    private void finishEditingAll() {
    }
}
