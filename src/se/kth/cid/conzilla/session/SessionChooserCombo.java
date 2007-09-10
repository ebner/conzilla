/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.rdf.RDFContainerManager;

/** A session chooser in a dialog.
 * 
 * @author matthias
 */
public class SessionChooserCombo extends JComboBox implements ItemListener {

    SessionManager sessionManager;
    EditMapManager editMapManager;
    RDFContainerManager modelManager;

    public SessionChooserCombo(
        SessionManager sm,
        RDFContainerManager modelManager) {
        this.sessionManager = sm;
        this.modelManager = modelManager;
        
        setEditable(false);

        updateSessions();
    }

    protected void updateSessions() {

        removeItemListener(this);
        removeAllItems();

        //Existing sessions added here.
        for (Iterator sessions = sessionManager.getSessions().iterator();
            sessions.hasNext();
            ) {
            Session session = (Session) sessions.next();
            addItem(session);
        }

        setSelectedIndex(-1);
        addItemListener(this);
    }

    public Session getSession() {
        return (Session) getSelectedItem();
    }

    Object oldSelection;

    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (getSelectedItem() instanceof String) {
                Session session =
                    sessionManager.getSessionFactory().createSession(null);
                if (SessionEditor
                    .launchProjectEditorDialog(
                        session,
                        sessionManager,
                        modelManager)
                    == JOptionPane.OK_OPTION) {
                    sessionManager.addSession(session);
                    updateSessions();
                    setSelectedItem(session);
                } else {
                    if (oldSelection != null) {
                        setSelectedItem(oldSelection);
                    } else {
                        setSelectedIndex(-1);
                    }
                }
            }
        } else {
            oldSelection = e.getItem();
        }
    }
}
