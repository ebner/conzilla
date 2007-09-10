/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import javax.swing.Box;
import javax.swing.JButton;

import se.kth.cid.component.ContainerManager;

/**
 * @author matthias
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SessionManagerDialog extends SessionChooserDialog {
    
    JButton closeButton;

    public SessionManagerDialog(ContainerManager manager) {
        super(manager);
    }    
    
    /* 
     * @see se.kth.cid.conzilla.session.SessionChooserDialog#initLayout()
     */
    protected void initLayout() {
        Box vertical = Box.createVerticalBox();
        Box buttons = Box.createHorizontalBox();
        buttons.add(Box.createHorizontalGlue());
        buttons.add(editButton);
        buttons.add(newButton);
        buttons.add(removeButton);
        buttons.add(closeButton);


        vertical.add(otherSessionsContainer);
        vertical.add(buttons);

        dialog.getContentPane().add(vertical);
    }
    /*
     * @see se.kth.cid.conzilla.session.SessionChooserDialog#initButtons()
     */
    protected void initButtons() {
        super.initButtons();
        closeButton = new JButton(closeAction);
    }
}
