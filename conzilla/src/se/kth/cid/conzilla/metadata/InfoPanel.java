/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.util.AttributeEntryUtil;
import se.kth.nada.kmr.shame.applications.util.MetaDataPanel;
import se.kth.nada.kmr.shame.container.EditContainer;
import se.kth.nada.kmr.shame.formlet.CompoundFormletConfiguration;
import se.kth.nada.kmr.shame.formlet.FormletConfiguration;
import se.kth.nada.kmr.shame.formlet.FormletStore;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * TODO: Description
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class InfoPanel extends JPanel {
    
    public static class Conzilla2SHAMEContainerWrapper implements EditContainer {
        se.kth.cid.component.Resource resource;
        RDFModel container;
        
        public Conzilla2SHAMEContainerWrapper(RDFModel container, se.kth.cid.component.Resource editMarkHolder) {
            this.container = container;
            if (editMarkHolder != null) {
                resource = editMarkHolder;
            } else {
                resource = container;
            }
        }
        
        public void setEdited(boolean edited) {
            resource.setEdited(edited);
            container.setEdited(edited);
        }

        public boolean isEdited() {
            return container.isEdited();
        }

        public void save() {
        }

        public Model getModel() {
            return (RDFModel) container;
        }

        public URI getUri() {
            return URI.create(container.getURI());
        }
    }
    
    static {
        FormletStore.requireFormletConfigurations("formlets/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/Simple_Dublin_Core/formlets.rdf");
    }
    static private String dcFormletCId = "http://kmr.nada.kth.se/shame/SDC/formlet#Simple-profile";

    static public void launchInfoPanelInFrame(se.kth.cid.component.Component component) {
        launchInfoPanelInFrame(component, null);
    }

    static public void launchInfoPanelInFrame(se.kth.cid.component.Component component, String formletConfigurationID) {
        final InfoPanel infoPanel = new InfoPanel(component);
        if (formletConfigurationID != null) {
            infoPanel.setFormletConfigurationId(formletConfigurationID);
        }
        JFrame frame = new JFrame();
        frame.setLocation(0, 0);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("Info on resource "+ component.getURI());
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                infoPanel.finishEdit();
            }
        };
        addDoneFunctionality(frame, infoPanel, action);
        frame.setVisible(true);
        frame.pack();
    }
    
    static public void addDoneFunctionality(final JFrame frame, JComponent comp, final AbstractAction action) {
        JPanel vertical = new JPanel();
        vertical.setLayout(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        vertical.add(comp, BorderLayout.CENTER);
        vertical.add(buttons, BorderLayout.SOUTH);
        buttons.add(Box.createHorizontalGlue());
        
        JButton done = new JButton(new AbstractAction("Done") {
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(e);
                frame.setVisible(false);
            } 
        });
        buttons.add(done);
        frame.setContentPane(vertical);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                action.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, null));
            }
        });
    }
    
    MetaDataPanel metadataPanel;
    JComboBox formletsCombo;
    JComboBox containersCombo;
    boolean containerChoosable = true;
    boolean formletConfigurationChoosable = true;
    protected boolean allowEditing = false;
    Component editMarkHolder;
    
    boolean lock = false;
	private Container editContainer;
	private HashMap container2metadata = new HashMap();
    
    public InfoPanel(se.kth.cid.component.Component component) {
        this();
        try {
            String containerString = component.getLoadContainer();
            Container container = ConzillaKit.getDefaultKit().getResourceStore().getAndReferenceContainer(new URI(containerString));
            presentResource(container, component);
        } catch (URISyntaxException urise) {
        	urise.printStackTrace();
        } catch (ComponentException e) {
            e.printStackTrace();
        }
    }
    
    public InfoPanel(String formletConfigurationId) {
        initLayout();
        setFormletConfigurationId(formletConfigurationId);
    }
    
    public InfoPanel() {
        initLayout();
        setFormletConfigurationId(dcFormletCId);
    }

    public void presentResource(Container container, Component resource) {
    	
        launchResource(container, resource, false);
    }

    public void editResource(Container container, Component resource) {
    	editContainer = container;
        launchResource(container, resource, true);
    }
    
    private void launchResource(Conzilla2SHAMEContainerWrapper cw) {
        Resource re = cw.container.createResource(cw.resource.getURI());
        if (cw.container.isEditable() && allowEditing && editContainer == cw.container) {
            metadataPanel.edit(cw, re);
        } else {
            metadataPanel.present(cw, re);
        }
    }
    
    private void launchResource(Container container, se.kth.cid.component.Component component, boolean editable) {
        if (!(container instanceof Model)) {
            return;
        }
        
        if (component != null) {
            editMarkHolder = component;
        }
        initChoosableContainers();
        
        RDFModel model = (RDFModel) container;
        Resource re = model.createResource(editMarkHolder.getURI());
        
        Conzilla2SHAMEContainerWrapper c2scontainer = new Conzilla2SHAMEContainerWrapper(model, editMarkHolder);
        
        if (editable && model.isEditable()) {
            metadataPanel.edit(c2scontainer, re);
        } else {
            metadataPanel.present(c2scontainer, re);
        }
        lock = true;
        containersCombo.setSelectedItem(container);
        lock = false;
    }
    
    public void finishEdit() {
        metadataPanel.finishEdit();
    }
    
    public void setFormletConfigurationId(String fcid) {
        FormletConfiguration fc = FormletStore.getInstance().getFormletConfiguration(fcid);
        if (fc != null) {
            lock = true;
            metadataPanel.setFormletConfigurationId(fc.getId());
            formletsCombo.setSelectedItem(fc);
            lock = false;
        }
    }
    
    public void setContainerChoosable(boolean b) {
        containerChoosable = b;
        if (containersCombo != null) {
            containersCombo.setEnabled(containerChoosable);
        }
    }
    
    public void setFormletConfigurationChoosable(boolean b) {
        formletConfigurationChoosable = b;
        if (formletsCombo != null) {
            formletsCombo.setEnabled(formletConfigurationChoosable);
        }
    }
    
    public void setContainer(Container container) {
        if (container instanceof Model) {
            containersCombo.setSelectedItem(container);
        }
    }
    
    private void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Box controlls = Box.createHorizontalBox();
        
        BasicComboBoxRenderer renderer = new BasicComboBoxRenderer() {
                    public java.awt.Component getListCellRendererComponent(
                        JList list,
                        Object value,
                        int index,
                        boolean isSelected,
                        boolean cellHasFocus) {
                        setBorder(null);
                        if (value instanceof Container) {
                            Container container = (Container) value;
                            String metadata = (String) container2metadata.get(container);
                            String label;
                            if (metadata != null) {
                            	label = AttributeEntryUtil.getTitleAsString(metadata, container.getURI());
                            } else {
                            	label = container.getURI();
                            }
                            return super.getListCellRendererComponent(
                                                    list,
                                                    label,
                                                    index,
                                                    isSelected,
                                                    cellHasFocus);
                        }
                        return super.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    }
                };
        
        
        formletsCombo = new JComboBox(getFormletChoices());
        formletsCombo.setEnabled(formletConfigurationChoosable);
        formletsCombo.addActionListener(new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                if (!lock) {
                    FormletConfiguration fc = (FormletConfiguration) formletsCombo.getSelectedItem();
                    metadataPanel.setFormletConfigurationId(fc.getId());
                }
            }
        });
        
        controlls.add(new JLabel("Form: "));
        controlls.add(formletsCombo);
        controlls.add(Box.createHorizontalStrut(20));
        
        containersCombo = new JComboBox();
        containersCombo.setEnabled(containerChoosable);
        containersCombo.setRenderer(renderer);
        containersCombo.addActionListener(new AbstractAction(){
            public void actionPerformed(ActionEvent arg0) {
                if (!lock) {
                    Object item = containersCombo.getSelectedItem();
                    Conzilla2SHAMEContainerWrapper c2scontainer = new Conzilla2SHAMEContainerWrapper((RDFModel) item, editMarkHolder);
                    launchResource(c2scontainer);
                }
            }
        });
        
        controlls.add(new JLabel("Container: "));
        controlls.add(containersCombo);
        
        controlls.setBorder(BorderFactory.createMatteBorder(0,0,3,0,Color.BLACK));
        add(controlls);

        metadataPanel = new MetaDataPanel(null);
        add(metadataPanel);
    }
    
    protected Vector getFormletChoices() {
        Vector list = new Vector();
        FormletStore store = FormletStore.getInstance();
        for (Iterator fcs = store.getFormletConfigurations().iterator(); fcs.hasNext();) {
            FormletConfiguration fc = (FormletConfiguration) fcs.next();
            if (fc instanceof CompoundFormletConfiguration) {
                list.add(fc);
            }
        }
        return list;
    }
    
    protected void initChoosableContainers() {
    	if (editMarkHolder == null) {
    		ContainerManager cm = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
    		containersCombo.setModel(new DefaultComboBoxModel(new Vector(cm.getContainers(Container.COMMON))));
    	} else {
    		Vector conts = new Vector();
    		ComponentManager cMan = editMarkHolder.getComponentManager(); 
			CollaborillaSupport cs = new CollaborillaSupport(ConfigurationManager
					.getConfiguration());
			CollaborillaReader collaborillaReader = new CollaborillaReader(cs);
    		Iterator it = cMan.getLoadedRelevantContainers().iterator();
    		while (it.hasNext()) {
    			URI uri = (URI) it.next();
    			String metadata = collaborillaReader.getMetaData(uri);
    			Container container = cMan.getContainer(uri); 
    			if (metadata != null) {
    				container2metadata.put(container, metadata);
    			}
    			conts.add(container);
    		}
    		if (editContainer != null && !conts.contains(editContainer)) {
    			conts.add(editContainer);
    		}
    		containersCombo.setModel(new DefaultComboBoxModel(conts));
    	}
    }
}
