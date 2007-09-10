/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.collaboration;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.util.ResourceUtil;
import se.kth.cid.util.TagManager;
import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;

public class ContainerEntries extends JPanel implements PropertyChangeListener {
    
	/**
	 * Contains the containers in oposite order compared to the ContainerManager
	 * order. This is due to the graphical presentation.
	 */
	Hashtable containerEntries;
    
    Vector containerEntriesVector;
    MouseInputAdapter mia;
    ContainerEntry choosenContainerEntry;
    MapController controller;
    ContainerManager containerManager;
	PopupContainerInfo popup;

    public ContainerEntries(MapController controller) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.white);
        this.controller = controller;
        popup = new PopupContainerInfo(this);
        
        containerEntries = new Hashtable();
        containerEntriesVector = new Vector();
        mia = new MouseInputAdapter() {
            ContainerEntry firstContainerEntry;

            public void mousePressed(MouseEvent me) {
                if (!(me.getComponent() instanceof ContainerEntry))
                    return;
                firstContainerEntry = (ContainerEntry) me.getComponent();
                if (firstContainerEntry == choosenContainerEntry) {
                	firstContainerEntry.setHighlighted(!firstContainerEntry.isHighlighted());
                } else {
                	firstContainerEntry.setHighlighted(true);
                    if  (choosenContainerEntry != null) {
                    	choosenContainerEntry.setHighlighted(false);
                    }
                    choosenContainerEntry = firstContainerEntry;
                }                
            }
        };
    }
    
    public void activate() {
    	popup.activate();
    }
    
    public void deactivate() {
    	popup.deactivate();
    }

    public ContainerEntry getContainerEntry(String uri, String fallbackLabel, Boolean required) {
        ContainerEntry ce = (ContainerEntry) containerEntries.get(uri);
        if (ce == null) {
            ce = new ContainerEntry(controller, containerManager, uri, fallbackLabel, required);
            containerEntries.put(uri, ce);
        }
        ce.setSelected(controller.getConceptMap().getComponentManager().getContainerVisible(URI.create(uri)));
        return ce;
    }

    
    private void updateClear() {
    	removeAll();
    	if (containerManager != null) {
    		controller.getConceptMap().getComponentManager().getTagManager().removePropertyChangeListener(this);
    	}
    	popup.removeAllPopups();
    	for (Iterator iter = containerEntriesVector.iterator(); iter.hasNext();) {
    		ContainerEntry ce = (ContainerEntry) iter.next();
    		ce.detach();
    		ce.removeMouseListener(mia);
    		ce.removeMouseListener(popup.mouseListener);
    		ce.removeMouseMotionListener(popup.mouseListener);
    	}
    	containerEntriesVector = new Vector();    	
    }
    
    public void clear() {
    	updateClear();
        containerEntries = new Hashtable();
    }

    private void addLabelText(String text) {
		JLabel label = new JLabel(text);
		JPanel labelPanel = new JPanel();
		labelPanel.setOpaque(false);
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(Box.createHorizontalGlue());
		labelPanel.add(label);
		labelPanel.add(Box.createHorizontalGlue());
		labelPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		add(labelPanel);
    }
    
    public void update() {
    	updateClear();
    	
    	containerManager = ResourceUtil.findContainerManager(controller.getConceptMap());
    	ComponentManager compMan = controller.getConceptMap().getComponentManager();
    	compMan.getTagManager().addPropertyChangeListener(this);
    	HashSet included = new HashSet();
    	CollaborillaDataSet cDSet = compMan.getCollaborillaDataSet();
		if (cDSet != null && cDSet.getRequiredContainers() != null) {
			Set required = cDSet.getRequiredContainers();
			add(Box.createVerticalStrut(7));
			addLabelText("Standard perspective");
			HashMap deps = findDependencies(required);
			for (Iterator requiredIt = deps.keySet().iterator(); requiredIt.hasNext();) {
				String requiredContainerURI = (String) requiredIt.next();
				included.add(requiredContainerURI);
				//Container m = containerManager.getContainer(requiredContainerURI);
				ContainerEntry ce = getContainerEntry(requiredContainerURI, null, Boolean.TRUE);
				String depContainerURI = (String) deps.get(requiredContainerURI);
				if ( depContainerURI!= null) {
					ce.setDependantContainer(depContainerURI);
					included.add(depContainerURI);
				}

				if (!containerEntriesVector.contains(ce)) {
					add(ce);
					containerEntriesVector.add(ce);
				}
				ce.addMouseMotionListener(popup.mouseListener);
				ce.addMouseListener(popup.mouseListener);
				ce.addMouseListener(mia);
			}
			Set optional = cDSet.getOptionalContainers();
			add(Box.createVerticalStrut(7));
			addLabelText("Optional contributions");

			if (optional != null) {
				deps = findDependencies(optional);
				for (Iterator optionalIt = deps.keySet().iterator(); optionalIt.hasNext();) {
					String optionalContainerURI = (String) optionalIt.next();
					included.add(optionalContainerURI);
					String depContainerURI = (String) deps.get(optionalContainerURI);
					//Container m = containerManager.getContainer(optionalContainerURI);
					ContainerEntry ce = getContainerEntry(optionalContainerURI, null, Boolean.FALSE);
					if (depContainerURI != null) {
						ce.setDependantContainer((String) deps.get(optionalContainerURI));
						included.add(depContainerURI);
					}
					if (!containerEntriesVector.contains(ce)) {
						add(ce);
						containerEntriesVector.add(ce);
					}
					ce.addMouseMotionListener(popup.mouseListener);
					ce.addMouseListener(popup.mouseListener);
					ce.addMouseListener(mia);
				}
			}
		}
		Set sessions = findLocalContributionsFromSessions(included);
		if (!sessions.isEmpty()) {
			add(Box.createVerticalStrut(7));
			addLabelText("Local Contributions");
//			Iterator it = containerManager.getContainers().iterator();
			Iterator it = sessions.iterator();
			while (it.hasNext()) {
				Session session = (Session) it.next();
				String layoutcontainer = session.getContainerURIForLayouts();
				ContainerEntry ce = getContainerEntry(layoutcontainer, session.getTitle(), null);
				String conceptContainer = session.getContainerURIForConcepts();
				if (!conceptContainer.equals(layoutcontainer) && !included.contains(conceptContainer)) {
					ce.setDependantContainer(conceptContainer);
				}
				add(ce);
				containerEntriesVector.add(ce);
				ce.addMouseMotionListener(popup.mouseListener);
				ce.addMouseListener(popup.mouseListener);
				ce.addMouseListener(mia);
			}
		}
		add(Box.createVerticalGlue());
		JButton refresh = new JButton();
		refresh.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				controller.getConceptMap().refresh();
				try {
					controller.refresh();
				} catch (ControllerException e1) {
				}
				clear();
				update();
			}
		});
		refresh.setIcon(Images.getImageIcon(Images.ICON_REFRESH));
		refresh.setOpaque(false);
		refresh.setBorder(null);
		JPanel labelPanel = new JPanel();
		labelPanel.setOpaque(false);
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(Box.createHorizontalGlue());
		labelPanel.add(refresh);
		add(labelPanel);
		
        revalidate();
        repaint();
    }
    
    private Set findLocalContributionsFromSessions(Set containersToExclude) {
    	HashSet sessions = new HashSet();
    	SessionManager sm = ConzillaKit.getDefaultKit().getSessionManager();
    	String mapURI = controller.getConceptMap().getURI();
    	if (sm != null) {
    		for (Iterator iter = sm.getSessions().iterator(); iter.hasNext();) {
				Session session = (Session) iter.next();
				if (session.isManaged(mapURI)) {
					if (!containersToExclude.contains(session.getContainerURIForLayouts())) {
						sessions.add(session);
					}
				}
			}
    	}
    	return sessions;
    }
    
    private HashMap findDependencies(Set containerURIS) {
    	CollaborillaSupport cs = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		CollaborillaReader collaborillaReader = new CollaborillaReader(cs);

    	HashMap map = new HashMap();
    	for (Iterator curis = containerURIS.iterator(); curis.hasNext();) {
			String curi = (String) curis.next();
			if (map.values().contains(curi)) {
				continue;
			}
			Set containerDeps = collaborillaReader.getRequiredContainers(URI.create(curi));
			if (containerDeps != null && !containerDeps.isEmpty()) {
				String containerDepURI = (String) containerDeps.iterator().next();
				if (containerURIS.contains(containerDepURI)) {
					map.remove(containerDepURI);
					map.put(curi, containerDepURI);
				} else {
					map.put(curi, null);
				}
			} else {
				map.put(curi, null);
			}
		}
    	return map;
    }
    

    public void propertyChange(PropertyChangeEvent e) {
        //	prop.equals(MapController.MAP_PROPERTY) ||
        String prop = e.getPropertyName(); 	
        /*if (prop.equals(ContainerManager.CONTAINER_ADDED)) {
            Tracer.debug("Container is added in ContainerEntries");
            update();
        } else if (prop.equals(ContainerManager.CONTAINER_REMOVED)) {
            Tracer.debug("Container is removed in ContainerEntries");
            containerEntries.remove(((Container) e.getSource()).getURI());
            update();
        } else if (prop.equals(ContainerManager.CONTAINER_ORDER_CHANGED)) {
            Tracer.debug("Order of containers has changed in ContainerEntries");
            Tracer.debug(
                "nr of containers in containerEntries"
                    + containerEntries.size()
                    + " and their names are");
            Iterator it = containerEntries.keySet().iterator();
            while (it.hasNext())
                Tracer.debug("container: " + ((Container) it.next()));
            update();

        } else */
        	if (prop.equals(TagManager.TAG_VISIBILITY_CHANGED)) {
            ContainerEntry entry =
                (ContainerEntry) containerEntries.get(((URI) e.getSource()).toString());
            if (entry != null) {
                entry.setSelected(((Boolean) e.getNewValue()).booleanValue());
            }
            
            if (entry == null || !containerEntriesVector.contains(entry)) {
            	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						update();
					}
            	});
            }
        }
    }
}