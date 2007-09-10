/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.wizard.newmap;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import se.kth.cid.conzilla.edit.wizard.newsession.SessionWizard;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.util.wizard.WizardComponentAdapter;

/**
 * @author matthias
 */
public class ChooseSession extends WizardComponentAdapter {
    public static final String CHOOSEN_SESSION = "choosen session";
    private JComboBox sessions = new JComboBox();
    SessionManager sessionManager;
    
    
    public ChooseSession(SessionManager sessionManager) {
        super("Choose the session the map will be edited in:", "Some help text");
        this.sessionManager = sessionManager;
    }
    
    protected JComponent constructComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));

        updateSessions();
        AbstractAction sessionSelect = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                    Session session =
                        (Session) sessions.getSelectedItem();
                    if (session != null) {
                        passedAlong.put(CHOOSEN_SESSION, session);
                        setReady(true);
                    } else {
                        setReady(false);
                    }
                }
            };
        sessions.addActionListener(sessionSelect);
        sessions.setMaximumSize(new Dimension(Integer.MAX_VALUE, sessions.getPreferredSize().height));
        panel.add(sessions);
        panel.add(Box.createHorizontalStrut(10));
        
        AbstractAction createSession = new AbstractAction("Create new session") {
            public void actionPerformed(ActionEvent ae) {
                SessionWizard nsw = new SessionWizard(sessionManager);
                nsw.showWizard();
                if (nsw.wizardFinishedSuccessfully()) {
                    Session session = nsw.getNewSession();
                    updateSessions();
                    sessions.setSelectedItem(session);
                }
            }
        };
        createSession.putValue(
            Action.SHORT_DESCRIPTION,
            "Create new session");
        JButton createSessionButton = new JButton(createSession);
        panel.add(createSessionButton);
        
        return panel;
    }
    
    

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#enter()
     */
    public void enter() {
        updateSessions();
        setReady(sessions.getSelectedItem() != null);
    }
    
    private void updateSessions() {
        Object selectedSession = sessions.getSelectedItem();
        sessions.removeAllItems();
        Collection sessionCollection = sessionManager.getSessions();
        for (Iterator sessionsIt = sessionCollection.iterator();sessionsIt.hasNext();) {
            sessions.addItem(sessionsIt.next());
        }
        if (selectedSession == null || ! sessionCollection.contains(selectedSession)) {
            sessions.setSelectedIndex(-1);
        } else {
            sessions.setSelectedItem(selectedSession);
        }
    }
    
    public boolean hasFinish() {
        return true;
    }
}
