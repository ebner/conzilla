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
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.rdf.RDFContainerManager;

/** A session chooser in a dialog.
 * 
 * @author matthias
 */
public class SessionChooserMenu
    extends ToolsMenu implements MenuListener{

    Vector earlierSessions;
    Vector otherSessions;
    MapController controller;
    RDFContainerManager modelManager;

    public SessionChooserMenu(MapController controller, RDFContainerManager manager) {
        super("Sessions");
        this.modelManager = manager;
        this.controller = controller;
        
        addMenuListener(this);
    }

    protected void fixSessionsMenu() {
        removeAll();
        
        Session current = controller.getConceptMap().getComponentManager().getEditingSesssion();

        add(new JLabel(SessionChooser.EARLIER));
        addSeparator();
        ButtonGroup group = new ButtonGroup();
        for (Iterator earlier = earlierSessions.iterator();
            earlier.hasNext();
            ) {
            final Session earlierSession = (Session) earlier.next();
            JRadioButtonMenuItem rb = new JRadioButtonMenuItem(new AbstractAction(earlierSession.getTitle()) {
                public void actionPerformed(ActionEvent e) {
                    setAsCurrent(earlierSession);
                }
            });
            group.add(rb);
            add(rb);
            if (earlierSession == current) {
                rb.setSelected(true);
            }
        }
        
        add(new JLabel(SessionChooser.OTHER));
        addSeparator();
        for (Iterator other = otherSessions.iterator(); other.hasNext();) {
            final Session otherSession = (Session) other.next();
            add(new AbstractAction(otherSession.getTitle()) {
                public void actionPerformed(ActionEvent e) {
                    setAsCurrent(otherSession);
                }
            });
        }
        addSeparator();
        add(new AbstractAction("Manage sessions") {

            public void actionPerformed(ActionEvent e) {
                SessionChooserDialogManager scdm = new SessionChooserDialogManager(modelManager);
                scdm.launchDialog(controller.getConceptMap().getURI());
            }
            
        });
    }
    protected void setAsCurrent(Session session) {
        String uri = controller.getConceptMap().getURI();
        if (!session.isManaged(uri)) {
            session.addManaged(uri);
        }
        
        controller.changeSessionTo(session);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.MenuListener#menuCanceled(javax.swing.event.MenuEvent)
     */
    public void menuCanceled(MenuEvent e) {
    }

    /* (non-Javadoc)
     * @see javax.swing.event.MenuListener#menuDeselected(javax.swing.event.MenuEvent)
     */
    public void menuDeselected(MenuEvent e) {
    }

    /* (non-Javadoc)
     * @see javax.swing.event.MenuListener#menuSelected(javax.swing.event.MenuEvent)
     */
    public void menuSelected(MenuEvent e) {
        SessionManager sessionManager = ConzillaKit.getDefaultKit().getSessionManager();
    	Collection sessions = sessionManager.getSessions();
        String uri = controller.getConceptMap().getURI();
        earlierSessions = SessionChooser.getSessionsOf(sessions, uri, true);
        otherSessions = SessionChooser.getSessionsOf(sessions, uri, false);
        fixSessionsMenu();
    }
}
