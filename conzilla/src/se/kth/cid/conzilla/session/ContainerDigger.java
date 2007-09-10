/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.kth.cid.component.Container;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.util.ErrorMessage;

/**
 * @author matthias
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ContainerDigger extends JDialog implements ListSelectionListener{
    
    Vector maps;
    JList list;
    JButton open;
    JButton done;
    
    public ContainerDigger(Container container) {
        maps = new Vector();
        
        maps.addAll(container.getDefinedContextMaps());
        
        fixLayout();
        setVisible(true);
    }
    
    void fixLayout() {
        list = new JList(maps);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(list, BorderLayout.NORTH);
        Box buttons = Box.createHorizontalBox();
        getContentPane().add(buttons, BorderLayout.SOUTH);
        
        open = new JButton(new AbstractAction("Open") {
            public void actionPerformed(ActionEvent e) {
                open();
            }});
        open.setEnabled(false);
        done = new JButton(new AbstractAction("Done") {
            public void actionPerformed(ActionEvent e) {
            	setVisible(false);
            }
        });
        buttons.add(Box.createHorizontalGlue());
        buttons.add(open);
        buttons.add(done);
		setTitle("Load contaioned maps");
		pack();
    }
    
    void updateButtons() {
        String map = (String) list.getSelectedValue();
        open.setEnabled(map != null);
    }
    
    void open() {
        String map = (String) list.getSelectedValue();
        try {
            ConzillaKit.getDefaultKit().getConzilla().openMapInNewView(new URI(map), null);
        } catch (ControllerException e) {
            ErrorMessage.showError("Open Map", "Failed open map" + map, e, this);
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TOxDO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        updateButtons();
    }
}
