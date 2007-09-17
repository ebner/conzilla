/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.collaboration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import se.kth.cid.collaboration.CollaborillaConfiguration;
import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.collaboration.ContextMapPublisher;
import se.kth.cid.collaboration.ContributionInformationDiskStore;
import se.kth.cid.collaboration.ContributionInformationStore;
import se.kth.cid.collaboration.LocationInformation;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.agent.AgentManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.rdf.RDFModel;
import se.kth.nada.kmr.shame.applications.util.Container;
import se.kth.nada.kmr.shame.applications.util.MetaDataPanel;
import se.kth.nada.kmr.shame.container.EditContainer;
import se.kth.nada.kmr.shame.util.RDFUtil;
import se.kth.nada.kmr.shame.vocabularies.DC;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Dialog to query the metadata and the destination location for the map and its
 * containers from the user.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class PublishMapDialog extends JFrame {
	
	/* property change constants */
	
	public static String PROP_CLICK_OK = "property.button.ok";
	
	/* private */
    
    private Model mapModel;
    
    private Model contributionModel;
    
    private MetaDataPanel mapMetaPanel;
    
    private MetaDataPanel contributionMetaPanel;
    
    private ContextMap contextMap;
    
    private MapController controller;
    
    private Session session;
    
    private ContainerManager containerManager;
    
    /* Components (autogen) */
    
    private ButtonGroup destRadioGroup;

	private JButton cancelButton;

	private JComboBox destinationComboBox;

	private JPanel contributionPanel;

	private JPanel mapPanel;

	private JRadioButton otherDestRadioButton;

	private JRadioButton previousDestRadioButton;

	private JButton publishButton;

	private JTabbedPane publishingPane;

	private JScrollPane mapScrollPane;

	private JScrollPane contributionScrollPane;

	private ComboBoxModel locationsModel;
	
	private CollaborillaReader collabReader;
	
	private CollaborillaSupport collabSupport;
	
	private CollaborillaConfiguration collabConfig;
	
	private boolean saveMode;
	
	private boolean originalPublication = false;
	
    /**
	 * @param mm
	 *            EditMapManager.
	 * @param cont
	 *            MapController.
	 * @param saveMode
	 *            True if the dialog is used for local saving only. The tab for
	 *            the context-map metadata and the components for selecting a
	 *            destination will be hidden then.
	 */
    public PublishMapDialog(MapController cont, boolean saveMode) {
    	this.controller = cont;
    	this.contextMap = cont.getConceptMap();
    	this.session = contextMap.getComponentManager().getEditingSesssion();
    	
    	if (contextMap.getLoadContainer().equals(session.getContainerURIForLayouts())) {
    		originalPublication = true;
    	}
    	
    	this.saveMode = saveMode;
    	this.containerManager = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
    	this.collabSupport = new CollaborillaSupport(ConfigurationManager.getConfiguration());
    	this.collabReader = new CollaborillaReader(collabSupport);
    	this.collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
    	
    	initContextMapMetaData();
    	initContributionMetaData();
    	
        initComponents();
        loadLocations();
    }
    
    /**
     * Loads available locations from the configuration.
     */
    private void loadLocations() {
    	List<LocationInformation> locList = collabConfig.getLocations();
    	locationsModel = new DefaultComboBoxModel(new Vector<LocationInformation>(locList));
    	destinationComboBox.setModel(locationsModel);
    }
    
    /**
	 * Initializes the contextmap metadata input form with already existing
	 * metadata from the information directory. If there is no information
	 * published yet, it takes the "standard" metadata of the contextmap.
	 */
    private void initContextMapMetaData() {
    	String uri = contextMap.getURI();
    	Resource mapResource;
    	mapMetaPanel = new MetaDataPanel("Editing the context map information", EditPanel.context_form, this.getBackground(), null);
    	mapModel = ModelFactory.createDefaultModel();
    	
//    	String infoFromServer = collabReader.getMetaData(URI.create(uri));
    	
//        if (infoFromServer != null) {
//            StringReader sr = new StringReader(infoFromServer);
//            mapModel.read(sr, uri);
//            mapResource = mapModel.getResource(uri);
//        } else {
    	mapResource = mapModel.createResource(uri);
    	Model mapLoadModel = (Model) containerManager.getContainer(contextMap.getLoadContainer());
    	RDFUtil.getModel(mapLoadModel, mapModel, mapResource, 0);
//        }
        
        EditContainer mapContainer = new Container(mapModel, URI.create(uri));
        mapMetaPanel.edit(mapContainer, mapResource);
    }
    
    /**
	 * Initializes the information container metadata with already existing
	 * metadata about the agent (who is author or contributor) and with already
	 * published information about the container.
	 */
    private void initContributionMetaData() {
    	String agentURI = collabConfig.getAgentURI(collabConfig.getUserNamespace());
    	String contributionInfo = null;
    	String uri = session.getContainerURIForConcepts();
    	Resource informationResource;
        contributionMetaPanel = new MetaDataPanel("Editing the concept container information", EditPanel.container_form,
				this.getBackground(), null);
    	ConzillaKit kit = ConzillaKit.getDefaultKit();
		RDFModel agentContainer;
    	try {
    		agentContainer = (RDFModel) kit.getResourceStore().getAndReferenceContainer(CollaborillaConfiguration.AGENT_LOAD_URI);
			if ((agentContainer != null)) {
				agentContainer.setPurpose(AgentManager.AGENT);
			}
		} catch (ComponentException e) {
			return;
		}

    	Resource agentResource = null;
    	agentResource = agentContainer.createResource(agentURI);
    	
    	contributionModel = ModelFactory.createDefaultModel();
    	
    	ContributionInformationStore infoStore = ContributionInformationDiskStore.getContributionInformationStore();
    	contributionInfo = infoStore.getMetaData(uri);
    	
    	if (contributionInfo == null) { 
    		contributionInfo = collabReader.getMetaData(URI.create(uri));
    	}
    	
    	if (contributionInfo != null) {
            StringReader sr = new StringReader(contributionInfo);
            contributionModel.read(sr, uri);
            informationResource = contributionModel.getResource(uri);
    	} else {
    		informationResource = contributionModel.createResource(uri);
    	}
    	
    	if (agentResource != null) {
    		contributionModel.add(informationResource, DC.creator, agentResource);
    		RDFUtil.getModel(agentContainer, contributionModel, agentResource, 0);
    	}
    	EditContainer infoContainer = new Container(contributionModel, URI.create(uri));
        contributionMetaPanel.edit(infoContainer, informationResource);
    }
    
    /**
     * Initializes the graphical components.
     */
    private void initComponents() {
        destRadioGroup = new ButtonGroup();
        publishingPane = new JTabbedPane();
        mapPanel = new JPanel();
        contributionPanel = new JPanel();
        previousDestRadioButton = new JRadioButton();
        otherDestRadioButton = new JRadioButton();
        destinationComboBox = new JComboBox();
        publishButton = new JButton();
        cancelButton = new JButton();
        mapScrollPane = new JScrollPane();
        contributionScrollPane = new JScrollPane();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Publishing information");
        
        org.jdesktop.layout.GroupLayout mapPanelLayout = new org.jdesktop.layout.GroupLayout(mapPanel);
        mapScrollPane.setViewportView(mapMetaPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
                mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(mapScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
            );
        mapPanelLayout.setVerticalGroup(
                mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(mapScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
            );
        
//        if (!saveMode) {
//        	publishingPane.addTab("Context Map", mapPanel);
//        }

        org.jdesktop.layout.GroupLayout informationPanelLayout = new org.jdesktop.layout.GroupLayout(contributionPanel);
        contributionScrollPane.setViewportView(contributionMetaPanel);
        contributionPanel.setLayout(informationPanelLayout);
        informationPanelLayout.setHorizontalGroup(
                informationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, contributionScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
            );
        informationPanelLayout.setVerticalGroup(
                informationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, contributionScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
            );

        if (originalPublication) {
        	publishingPane.addTab("Session Information", contributionPanel);
        } else {
        	publishingPane.addTab("Contribution Information", contributionPanel);
        }
        
       	String containerTooltip = "<html><b>Information Container:</b> " + session.getContainerURIForConcepts() + "<br>"
       		+ "<b>Presentation Container:</b> " + session.getContainerURIForLayouts() + "</html>";
//        if (saveMode) {
        	publishingPane.setToolTipTextAt(0, containerTooltip);
//        } else {
//        	publishingPane.setToolTipTextAt(0, "<html><b>Context-Map:</b> " + contextMap.getURI() + "</html>");
//            publishingPane.setToolTipTextAt(1, containerTooltip);
//        }

        destRadioGroup.add(previousDestRadioButton);
        LocationInformation prevInfo = collabConfig.getPreviouslyUsedDestination();
        if (prevInfo != null) {
			previousDestRadioButton.setToolTipText(prevInfo.getToolTip());
			previousDestRadioButton.setSelected(true);
        	previousDestRadioButton.setText("Previously used destination: " + prevInfo.getTitle());
        	previousDestRadioButton.setEnabled(true);
        } else {
        	previousDestRadioButton.setEnabled(false);
        	previousDestRadioButton.setSelected(false);
        	previousDestRadioButton.setText("Previously used destination");
        }
        
        previousDestRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        previousDestRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        destRadioGroup.add(otherDestRadioButton);
        if (prevInfo == null) {
        	otherDestRadioButton.setSelected(true);
        }
        otherDestRadioButton.setText("Other destination: ");
        otherDestRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        otherDestRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        if (saveMode) {
        	publishButton.setText("Save");
        	previousDestRadioButton.setVisible(false);
        	otherDestRadioButton.setVisible(false);
        	destinationComboBox.setVisible(false);
        } else {
        	publishButton.setText("Publish");
        }

        cancelButton.setText("Cancel");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, publishingPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(publishButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton))
                    .add(layout.createSequentialGroup()
                        .add(otherDestRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(destinationComboBox, 0, 373, Short.MAX_VALUE))
                    .add(previousDestRadioButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(publishingPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(previousDestRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(otherDestRadioButton)
                    .add(destinationComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(publishButton))
                .addContainerGap())
        );
        
        if (saveMode) {
        	publishButton.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent evt) {
        			setVisible(false);
        			firePropertyChange(PROP_CLICK_OK, null, null);
        		}
        	});
        } else {
        	publishButton.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent evt) {
        			if (otherDestRadioButton.isSelected()) {
        				collabConfig.storePreviouslyUsedDestination((LocationInformation)locationsModel.getSelectedItem());
        			}
        			publish();
        		}
        	});
        }
        
        cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
				dispose();
			}
		});
        
        pack();
        setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
    }
    
    /**
	 * Starts the publishing process. Creates a dialog for showing the progress,
	 * and attaches listeners to the publisher.
	 */
    private void publish() {
    	this.session = contextMap.getComponentManager().getEditingSesssion();
    	final ProgressInformation info = new ProgressInformation(PublishMapDialog.this, "Publishing progress");
    	info.setVisible(true);
    	
		new Thread(new Runnable() {
			public void run() {
				ContextMapPublisher publisher = new ContextMapPublisher(controller, contextMap,
						getLocationInformation(), getMapMetaData(), getContributionMetaData());
				publisher.addPropertyChangeListener(info);
				publisher.publish();
				publisher.removePropertyChangeListener(info);
			}
		}).start();
    }
    
    /**
	 * @return Returns information about the currently selected destination.
	 */
	public LocationInformation getLocationInformation() {
		if (previousDestRadioButton.isSelected()) {
			return collabConfig.getPreviouslyUsedDestination();
		} else {
			return (LocationInformation) locationsModel.getSelectedItem();
		}
	}

	/**
	 * @return Returns the contextmap metadata as String. Null if the model is
	 *         invalid or does not exist.
	 */
	public String getMapMetaData() {
		if ((mapMetaPanel == null) || (mapModel == null)) {
			return null;
			// throw new IllegalStateException("Invalid meta data model.");
		}
		StringWriter sw = new StringWriter();
		mapModel.write(sw, "RDF/XML-ABBREV");
		return sw.toString();
	}

	/**
	 * @return Returns the information container metadata as String. Null if the
	 *         model is invalid or does not exist.
	 */
	public String getContributionMetaData() {
		if ((contributionMetaPanel == null) || (contributionModel == null)) {
			return null;
			// throw new IllegalStateException("Invalid meta data model.");
		}
		StringWriter sw = new StringWriter();
		contributionModel.write(sw, "RDF/XML-ABBREV");
		return sw.toString();
	}
    
}