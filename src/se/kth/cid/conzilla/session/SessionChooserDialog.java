/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.kth.cid.component.ContainerManager;
import se.kth.cid.conzilla.app.ConzillaKit;

/** A session chooser in a dialog.
 * 
 * @author matthias
 */
public class SessionChooserDialog
    extends SessionChooser
    implements ListSelectionListener {

    public class SessionDialog extends JDialog {

        public SessionDialog() {
        	setModal(true);
        }

        protected void fixSessionContainers() {
            earlierSessionList = new JList(earlierSessions);
            otherSessionList = new JList(otherSessions);
            earlierSessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            otherSessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            MouseAdapter ma = new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    if (me.getClickCount() == 2) {
                        okAction.actionPerformed(
                            new ActionEvent(me.getSource(), 0, "ok"));
                    }
                }
            };

            earlierSessionList.addMouseListener(ma);
            otherSessionList.addMouseListener(ma);

            if (session == null) {
                if (earlierSessions.size() != 0) {
                    session = (Session) earlierSessions.elementAt(0);
                    earlierSessionList.setSelectedIndex(0);
                } else if (otherSessions.size() != 0) {
                    session = (Session) otherSessions.elementAt(0);
                    otherSessionList.setSelectedIndex(0);
                }
            } else {
                if (earlierSessions.contains(session)) {
                    earlierSessionList.setSelectedValue(session, true);
                } else if (otherSessions.contains(session)) {
                    otherSessionList.setSelectedValue(session, true);
                }
            }
            
            //Add selection listeners after correct selection is set to avoid unneccesarry events.
            earlierSessionList.addListSelectionListener(SessionChooserDialog.this);
            otherSessionList.addListSelectionListener(SessionChooserDialog.this);

            earlierSessionsContainer.setViewportView(earlierSessionList);
            otherSessionsContainer.setViewportView(otherSessionList);
        }
    }

    JScrollPane earlierSessionsContainer = new JScrollPane();
    JScrollPane otherSessionsContainer = new JScrollPane();
    JList earlierSessionList;
    JList otherSessionList;
    JButton okButton;
    JButton removeButton;
    JButton editButton;
    JButton cancelButton;
    JButton newButton;

    SessionDialog dialog;

    public SessionChooserDialog(ContainerManager manager) {
        super(manager);
        
        initButtons();
        initLayout();   
    }
    
    protected void initButtons() {
        okButton = new JButton(okAction);
        removeButton = new JButton(removeAction);
        editButton = new JButton(editAction);
        cancelButton = new JButton(cancelAction);
        newButton = new JButton(newAction);
        dialog = new SessionDialog();
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        dialog.setContentPane(contentPane);
    }
    
    protected void initLayout() {
        Box vertical = Box.createVerticalBox();
        Box buttons = Box.createHorizontalBox();
        buttons.add(Box.createHorizontalGlue());
        buttons.add(okButton);
        buttons.add(cancelButton);
        buttons.add(newButton);
        buttons.add(removeButton);
        buttons.add(editButton);
        vertical.add(earlierSessionsContainer);
        earlierSessionsContainer.setBorder(BorderFactory.createTitledBorder(EARLIER));
        vertical.add(otherSessionsContainer);
        otherSessionsContainer.setBorder(BorderFactory.createTitledBorder(OTHER));
        vertical.add(buttons);

        dialog.getContentPane().setLayout(new BorderLayout());
        vertical.setBorder(BorderFactory.createTitledBorder("Choose a session for this Context-map"));
        dialog.getContentPane().add(vertical, BorderLayout.CENTER);

        WindowAdapter wa = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	cancel();
            }
        };
        dialog.addWindowListener(wa);
    }

    
    protected void ok() {
        super.ok();
        dialog.setVisible(false);
    }
    
    protected void cancel() {
        super.cancel();
        dialog.setVisible(false);
    }
    
    protected void close() {
        super.close();
        dialog.setVisible(false);
    }

    protected void edit() {
        super.edit();
        earlierSessionList.revalidate();
        otherSessionList.revalidate();
    }

    protected void remove() {
        super.remove();
        initializeSessionLists();
        dialog.fixSessionContainers();
    }
    protected void newSession() {
        super.newSession();
        initializeSessionLists();
        dialog.fixSessionContainers();
    }
    
    protected void manage() {
        super.manage();
        initializeSessionLists();
        dialog.fixSessionContainers();
    }

    protected void unManage() {        
        super.unManage();
        initializeSessionLists();
        dialog.fixSessionContainers();
    }

    public Session findSession(String uri) {
    	Collection projects = sessionManager.getSessions();
    	if (!projects.isEmpty()) {
    		Vector eS = getSessionsOf(projects, uri, true);
    		if (eS.size() == 1) {
    			return (Session) eS.firstElement();
    		}
    	}

    	launchDialog(uri);
    	return session;
    }
    
    public void launchDialog(String uri) {
        initializeSessionLists(uri);
        dialog.fixSessionContainers();
        updateActions();

        dialog.pack();
        dialog.setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
        dialog.setVisible(true);
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();

        if (list.isSelectionEmpty()) {
            return;
        }

        session = null;
        if (list == earlierSessionList) {
            session = (Session) earlierSessionList.getSelectedValue();
            otherSessionList.clearSelection();
        } else {
            session = (Session) otherSessionList.getSelectedValue();
            earlierSessionList.clearSelection();
        }
        updateActions();
    }
}
