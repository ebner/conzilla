/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * Dialog which allows to select sessions and its contained context-maps.
 * 
 * TODO meta-data popup
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class SessionBrowser extends JDialog {
	
	/* Constants for signaling */
    
    public static String BUTTON_OPEN = "button.open";
    
    public static String BUTTON_OPEN_NEW_VIEW = "button.open-new-view";
    
    public static String BUTTON_CANCEL = "button.cancel";
    
	/* Graphical components */
	
    private JButton buttonCancel;

	private JButton buttonOpen;

	private JButton buttonOpenInView;

	private JLabel labelContextMaps;

	private JLabel labelSessions;

	private JList mapList;

	private JScrollPane mapScrollPane;

	private JComboBox sessionComboBox;
    
    /* Internals */
    
    private SessionManager sessionManager;
    
    /**
	 * Wrapper around ContextMap, needed to overwrite toString(). Otherwise just
	 * the map's URIs would be shown.
	 * 
	 * @author Hannes Ebner
	 */
    private class ContextMapWrapper {
    	
		private ContextMap map;

		protected ContextMapWrapper(ContextMap map) {
			this.map = map;
		}

		public String toString() {
			String title = AttributeEntryUtil.getTitleAsString(map);
			if (title == null) {
				title = "(No Title)";
			}
			return title;
		}

		/**
		 * @return Returns the wrapped ContextMap.
		 */
		public ContextMap getContextMap() {
			return map;
		}
		
	}
    
    public SessionBrowser(SessionManager sessionManager) {
    	this.sessionManager = sessionManager;
    	setTitle("Session Browser");
    	setModal(false);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        addWindowStateListener(new WindowStateListener() {
        	public void windowStateChanged(WindowEvent e) {
        		if (e.getNewState() == WindowEvent.WINDOW_CLOSING) {
        			firePropertyChange(BUTTON_CANCEL, null, null);
        		}
        	}
        });
        
        initComponents();
        setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
    }
    
    /* Public methods */
    
    /**
     * Shows the dialog and returns immediately.
     */
    public void showDialog() {
    	new Thread(new Runnable() {
    		public void run() {
    			initSessionList();
    			setVisible(true);
    			Object item = sessionComboBox.getSelectedItem();
    			if (item instanceof Session) {
    				updateMapList((Session) sessionComboBox.getSelectedItem());
    			}
    		}
    	}).start();
    }
    
    /**
	 * @return Returns a Set of ContextMap objects which have been selected
	 *         within the dialog.
	 */
    public Set getSelectedContextMaps() {
    	Set<ContextMap> maps = new HashSet<ContextMap>();
    	Object[] selectedObjects = mapList.getSelectedValues();
    	if (selectedObjects != null) {
    		for (int i = 0; i < selectedObjects.length; i++) {
    			ContextMapWrapper wrappedMap = (ContextMapWrapper) selectedObjects[i];
    			maps.add(wrappedMap.getContextMap());
    		}
    	}
    	return maps;
    }
    
    /* Private methods */
    
    private void initComponents() {
        labelSessions = new JLabel();
        sessionComboBox = new JComboBox();
        labelContextMaps = new JLabel();
        mapScrollPane = new JScrollPane();
        mapList = new JList();
        buttonCancel = new JButton();
        buttonOpenInView = new JButton();
        buttonOpen = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        labelSessions.setText("Choose a Session:");

        labelContextMaps.setText("Choose one or more Context-Maps:");

        mapScrollPane.setViewportView(mapList);

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		firePropertyChange(BUTTON_CANCEL, null, null);
        	}
        });

        buttonOpenInView.setText("Open in new view");
        buttonOpenInView.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openInNewButtonAction();
        	}
        });

        buttonOpen.setText("Open");
        buttonOpen.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openButtonAction();
        	}
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, mapScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, sessionComboBox, 0, 365, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, labelSessions)
                    .add(layout.createSequentialGroup()
                        .add(buttonOpen)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(buttonOpenInView)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(buttonCancel))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, labelContextMaps))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(labelSessions)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sessionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(labelContextMaps)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mapScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(buttonCancel)
                    .add(buttonOpenInView)
                    .add(buttonOpen))
                .addContainerGap())
        );
        
        buttonOpen.setEnabled(false);
        buttonOpenInView.setEnabled(false);
        
        sessionComboBox.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		SwingUtilities.invokeLater(new Runnable() {
        			public void run() {
        				final Session session = (Session) sessionComboBox.getSelectedItem();
        				updateMapList(session);
        			}
        		});
        	}
        });
        
        mapList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mapList.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		if (mapList.getSelectedIndices().length == 1) {
        			buttonOpen.setEnabled(true);
        	        buttonOpenInView.setEnabled(true);
        		} else if (mapList.getSelectedIndices().length > 1) {
        			buttonOpen.setEnabled(false);
        	        buttonOpenInView.setEnabled(true);
        		} else {
        	        buttonOpen.setEnabled(false);
        	        buttonOpenInView.setEnabled(false);
        		}
        	}
        });
        mapList.addMouseListener(new MouseListener() {
        	public void mouseClicked(MouseEvent e) {
        		if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
        			openButtonAction();
        		}
        	}
        	public void mousePressed(MouseEvent e) {}
        	public void mouseEntered(MouseEvent e) {}
        	public void mouseReleased(MouseEvent e) {}
        	public void mouseExited(MouseEvent e) {}
        });
        
        pack();
    }
    
    /**
	 * Initializes the ComboBox for choosing sessions with a model containing a
	 * list of sessions.
	 */
    private void initSessionList() {
    	List<Session> sessions = getSessionList();
    	if (sessions.size() == 0) {
    		sessionComboBox.setEnabled(false);
    	}
    	ComboBoxModel model = new DefaultComboBoxModel(sessions.toArray());
    	sessionComboBox.setModel(model);
    }
    
    /**
	 * @return Returns a sorted list of available sessions.
	 */
	private List<Session> getSessionList() {
		List<Session> sortedSessions = new ArrayList<Session>(sessionManager.getSessions());
		Collections.sort(sortedSessions, new Comparator<Session>() {
			public int compare(Session s1, Session s2) {
				if (s1 == null) {
					return 0;
				}
				return s1.getTitle().compareToIgnoreCase(s2.getTitle());
			}
		});
		return sortedSessions;
    }
    
    /**
	 * Updates the list of context-maps according to the specified session.
	 * 
	 * @param session
	 *            Session to look for context-maps.
	 */
    private void updateMapList(Session session) {
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	if (session == null) {
    		return;
    	}
    	List maps = getMapsOfSession(session);
    	DefaultListModel model = new DefaultListModel();
    	for (Iterator it = maps.iterator(); it.hasNext(); ) {
    		model.addElement(it.next());
    	}
    	mapList.setModel(model);
    	setCursor(Cursor.getDefaultCursor());
    }
    
    /**
	 * @param session
	 *            Session to look for context-maps.
	 * @return Returns a sorted list of context-maps within a session.
	 */
    private List<ContextMapWrapper> getMapsOfSession(Session session) {
    	List<ContextMapWrapper> result = new ArrayList<ContextMapWrapper>();
		String uri = session.getContainerURIForLayouts();
		Container container = null;
		ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
		try {
			container = store.getAndReferenceContainer(URI.create(uri));
		} catch (ComponentException ce) {
			ErrorMessage.showError("Container could not be loaded", "One of this session's containers could not be loaded.", ce, null);
			return result;
		}
		List<String> maps = container.getDefinedContextMaps();
		for (Iterator<String> it = maps.iterator(); it.hasNext(); ) {
			URI mapURI = URI.create((String) it.next());
			ContextMap map = null;
			try {
				map = store.getAndReferenceLocalContextMap(mapURI, session);
			} catch (ComponentException e1) {
				continue;
			}
			result.add(new ContextMapWrapper(map));
		}
		Collections.sort(result, new Comparator<ContextMapWrapper>() {
			public int compare(ContextMapWrapper o1, ContextMapWrapper o2) {
				String map1 = ((ContextMapWrapper) o1).toString();
				String map2 = ((ContextMapWrapper) o2).toString();
				return map1.compareToIgnoreCase(map2);
			}
		});
		return result;
    }
    
    private void openButtonAction() {
    	Set maps = getSelectedContextMaps();
		if (maps.size() > 0) {
			firePropertyChange(BUTTON_OPEN, null, maps);
		}
    }
    
    private void openInNewButtonAction() {
    	Set maps = getSelectedContextMaps();
		if (maps.size() > 0) {
			firePropertyChange(BUTTON_OPEN_NEW_VIEW, null, maps);
		}
    }
    
}