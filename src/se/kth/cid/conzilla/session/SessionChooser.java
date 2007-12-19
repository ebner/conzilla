/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import se.kth.cid.component.ContainerManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.edit.wizard.newsession.SessionWizard;

/**
 * @author matthias
 */
public class SessionChooser {

    SessionManager sessionManager;
    protected Vector earlierSessions;
    protected Vector otherSessions;
    protected String uri;
    protected Session session = null;
    protected AbstractAction okAction;
    protected AbstractAction cancelAction;
    protected AbstractAction newAction;
    protected AbstractAction closeAction;
    protected AbstractAction editAction;
    protected AbstractAction removeAction;
    protected AbstractAction manageAction;
    protected AbstractAction unManageAction;
    protected static final String EARLIER = "Earlier Sessions";
    protected static final String OTHER = "Available Sessions";
    protected ContainerManager modelManager;

    public SessionChooser(ContainerManager manager) {
        sessionManager = ConzillaKit.getDefaultKit().getSessionManager();
        modelManager = manager;

        initActions();
    }
    
    protected void initActions() {
        okAction = new AbstractAction("Ok") {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        };
        
        closeAction = new AbstractAction("Close") {
            public void actionPerformed(ActionEvent e) {
                close();
            }            
        };

        manageAction = new AbstractAction("Manage") {
            public void actionPerformed(ActionEvent e) {
                manage();
            }            
        };

        unManageAction = new AbstractAction("Unmanage") {
            public void actionPerformed(ActionEvent e) {
                unManage();
            }            
        };

        cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        };

        newAction = new AbstractAction("New") {
            public void actionPerformed(ActionEvent e) {
                newSession();
            }
        };

        editAction = new AbstractAction("Edit") {
            public void actionPerformed(ActionEvent e) {
                edit();
            }
        };

        removeAction = new AbstractAction("Remove") {
            public void actionPerformed(ActionEvent e) {
                remove();
            }
        };
    }
    
    protected void manage() {
        if (session!=null && !session.isManaged(uri)) {
            session.addManaged(uri);
        }
        updateActions();
    }

    protected void unManage() {
        if (session!=null && session.isManaged(uri)) {
            session.removeManaged(uri);
        }
        updateActions();
    }

    protected void ok() {
        if (session!=null && !session.isManaged(uri)) {
            session.addManaged(uri);
        }
        updateActions();
    }
    
    protected void remove() {
        Collection managed = session.getManaged();
        int nr = managed.size();
        int ans =
            JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to remove the "
                    + session.getTitle()
                    + " project?\n"
                    + "It manages "
                    + nr
                    + " maps!",
                    "Remove",
                    JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION)
            return;
        sessionManager.removeSession(session);
        session = null;
        updateActions();
    }

    protected void edit() {
        SessionEditor.launchProjectEditorDialog(session, sessionManager, modelManager);
    }

    protected void newSession() {
    	 SessionWizard nsw = new SessionWizard(sessionManager);
         nsw.showWizard();
         if (nsw.wizardFinishedSuccessfully()) {
             session = nsw.getNewSession();
             updateActions();
         }
    }

    protected void cancel() {
        session = null;
    }
    
    protected void close() {
    }

    public static Vector getSessionsOf(Collection projects, String uri, boolean managed) {
        Vector al = new Vector();
        for (Iterator ps = projects.iterator(); ps.hasNext();) {
            Session element = (Session) ps.next();
            if (managed) {
                if (element.isManaged(uri)) {
                    al.add(element);
                }
            } else {
                if (uri == null || !element.isManaged(uri)) {
                    al.add(element);
                }
            }
        }
        return al;
    }

    protected void initializeSessionLists() {
        Collection projects = sessionManager.getSessions();

        if (projects.isEmpty()) {
//			session = sessionManager.getSessionFactory().createSession(null);
//			if (SessionEditor.launchProjectEditorDialog(session, sessionManager, modelManager) == JOptionPane.OK_OPTION) {
//				sessionManager.addSession(session);
//			}
            projects = sessionManager.getSessions();
        }

        earlierSessions = getSessionsOf(projects, uri, true);
        otherSessions = getSessionsOf(projects, uri, false);
    }

    public void initializeSessionLists(String uri) {
        this.uri = uri;
        initializeSessionLists();
    }
    
    public void updateActions() {
         if (session == null) {
             manageAction.setEnabled(false);
             removeAction.setEnabled(false);
             editAction.setEnabled(false);
             okAction.setEnabled(false);
             manageAction.setEnabled(false);
             unManageAction.setEnabled(false);
         } else {
             removeAction.setEnabled(true);
             editAction.setEnabled(true);
             okAction.setEnabled(true);
             if (session.isManaged(uri)) {
                 unManageAction.setEnabled(true);
                 manageAction.setEnabled(false);
             } else {
                 unManageAction.setEnabled(false);
                 manageAction.setEnabled(true);
             }
         }
     }
}
