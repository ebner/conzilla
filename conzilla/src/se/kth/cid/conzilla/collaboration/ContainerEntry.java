/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.collaboration;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * A presentation of an RDFModel as a label + checkbox.
 */
public class ContainerEntry extends JPanel {
	boolean lock = false;

	String containerURI;

	JLabel label;

	JCheckBox cb;

	ContainerManager containerManager;

	Color background = Color.white;

	String metadata;

	boolean highlighted = false;

	MapController controller;
	
	Boolean required;
	
	String dependentContainerURI;

	private boolean refreshNeeded;

	
	public ContainerEntry(MapController controller, ContainerManager cm,
			String containerURI, String fallbackLabel, Boolean required) {
		super();
		this.required = required;
		this.containerManager = cm;
		this.containerURI = containerURI;
		this.controller = controller;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(true);
		
		// Do something nice with titles...
		CollaborillaSupport cs = new CollaborillaSupport(ConfigurationManager
				.getConfiguration());
		CollaborillaReader collaborillaReader = new CollaborillaReader(cs);
		metadata = collaborillaReader.getMetaData(URI.create(containerURI));
		String labelString = null;
		if (metadata != null) {
			labelString = AttributeEntryUtil.getTitleAsString(metadata,
					containerURI);
		} else if (fallbackLabel != null) {
			labelString = fallbackLabel;
		} else {
			labelString = containerURI;
		}
		setBackground(background);
		label = new JLabel(labelString);
		label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		cb = new JCheckBox();
		cb.setBackground(background);
		if (required != null && required.booleanValue()) {
			cb.setEnabled(false);
		}
				
		setSelected(controller.getConceptMap().
				getComponentManager().getContainerVisible(URI.create(containerURI)));
		add(cb);
		add(label);
		add(Box.createHorizontalGlue());
		addListeners();
//		Border border1 = BorderFactory.createLineBorder(Color.black);
		Border border2 = BorderFactory
				.createMatteBorder(1, 1, 1, 1, background);
		setBorder(border2);//BorderFactory.createCompoundBorder(border2, border1));
	}

	public void setDependantContainer(String dependant) {
		dependentContainerURI = dependant;
	}
	
	public void detach() {
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean b) {
		if (highlighted != b) {
			highlighted = b;
			if (highlighted) {
				setBackground(ColorTheme.getBrighterColor(Colors.CONCEPT_FOCUS));
		        Mark overMark= new Mark(Colors.CONCEPT_FOCUS, null, null);

		        Iterator mapObjects = controller.getView().getMapScrollPane().getDisplayer().getMapObjects().iterator();
		        while (mapObjects.hasNext()) {
		            MapObject mo = (MapObject) mapObjects.next();
		            DrawerLayout dl = mo.getDrawerLayout();
		            Concept c = mo.getConcept();
		            if (c.getComponentManager().getLoadedRelevantContainers().contains(URI.create(containerURI)) 
		            		|| dl.getLoadContainer().equals(containerURI)) {
						mo.pushMark(overMark, this);
		            }
		        }
			} else {
				setBackground(background);
		        Iterator mapObjects = controller.getView().getMapScrollPane().getDisplayer().getMapObjects().iterator();
		        while (mapObjects.hasNext()) {
		            MapObject mo = (MapObject) mapObjects.next();
		            mo.popMark(this);
		        }
			}
			repaint();
		}
	}

	public JLabel getLabel() {
		return label;
	}

	void addListeners() {
		ItemListener il;
		il = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				lock = true;
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Container dependentContainer = loadContainerIfNeccessary(dependentContainerURI);
					Container container = loadContainerIfNeccessary(containerURI);
					
					if (refreshNeeded) {
						controller.getConceptMap().refresh();
						try {
							controller.refresh();
						} catch (ControllerException e1) {
						}
						refreshNeeded = false;
					}

					ContainerEntry.this.setVisible(containerURI, true);
					if (dependentContainer != null) {
				        System.out.println("selecting "+dependentContainerURI);
						ContainerEntry.this.setVisible(dependentContainerURI, true);						
					}
					
			        System.out.println("selecting "+container.getURI());
			        setCursor(Cursor.getDefaultCursor());
					// ModelEntry.this.repaint();
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					Container container = loadContainerIfNeccessary(containerURI);
					Container dependentContainer = loadContainerIfNeccessary(dependentContainerURI);
			        System.out.println("deselecting "+container.getURI());
			        setVisible(containerURI, false);
					if (dependentContainer != null) {
				        System.out.println("deselecting "+dependentContainerURI);
				        setVisible(dependentContainerURI, false);
					}
					// .repaint();
				}
				lock = false;
			}
		};
		cb.addItemListener(il);
	}
	
	private void setVisible(String uri, boolean visible) {
		controller.getConceptMap().getComponentManager()
			.setContainerVisible(URI.create(uri), visible);
	}

	private Container loadContainerIfNeccessary(String uri) {
		if (uri == null) {
			return null;
		}
		Container container = containerManager.getContainer(uri);
		if (container == null) {
			try {
				ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
				container = store.getAndReferenceContainer(URI.create(uri));
				refreshNeeded = true;
			} catch (ComponentException e1) {
			}
		}
		return container;
	}

	
	public void setSelected(boolean bo) {
		if (!lock) {
			lock = true;
			cb.setSelected(bo);
			lock = false;
		}
	}

	public String getMetadata() {
		return metadata;
	}
	public String getContainerURI() {
		return containerURI;
	}
}