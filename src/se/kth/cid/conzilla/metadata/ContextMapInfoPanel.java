/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.awt.event.ActionListener;
import java.io.StringReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.collaboration.MetaDataCache;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.agent.AgentPane;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.rdf.FOAF;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.util.AttributeEntryUtil;
import se.kth.cid.util.Tracer;
import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;
import se.kth.nada.kmr.shame.applications.util.MetaDataPanel;
import se.kth.nada.kmr.shame.container.EditContainer;
import se.kth.nada.kmr.shame.vocabularies.DC;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Panel showing information about a context-map, its creator, session, and
 * contributions.
 * 
 * @author Hannes Ebner
 */
public class ContextMapInfoPanel extends JPanel {
	
    private JScrollPane authorMetadataScrollPane;
    private JPanel authorPanel;
    private JButton closeButton;
    private JPanel contributionsPanel;
    private JTextField mapAuthorField;
    private JLabel mapAuthorLabel;
    private JPanel mapInfoPanel;
    private JScrollPane mapMetadataScrollPane;
    private JTextField mapModifiedField;
    private JLabel mapModifiedLabel;
    private JPanel mapPanel;
    private JTextField mapPublishedField;
    private JLabel mapPublishedLabel;
    private JTextField mapTitleField;
    private JLabel mapTitleLabel;
    private JTextField mapURIField;
    private JLabel mapURILabel;
    private JScrollPane sessionMetadataScrollPane;
    private JPanel sessionPanel;
    private JTabbedPane tabbedPane;
    
    private MapController controller;
    
    public ContextMapInfoPanel(MapController controller) {
    	this.controller = controller;
        initComponents();
        initContextMapInformation();
        initContextMapMetadata();
        initSessionMetadata();
        initAuthorMetadata();
        initContributionTab();
    }
    
    /**
	 * Extracts the name of the author (DC.creator) out of a model. If there are
	 * more than one DC.creator statements, just the first one is taken.
	 * 
	 * @param model
	 *            RDF model.
	 * @return A String with the name of the author, if no DC.creator statements
	 *         exists, null is replied.
	 */
    private static String getAuthorName(Model model) {
    	StmtIterator statements = model.listStatements(null, DC.creator, (String) null);
    	String authorName = null;
    	if (statements.hasNext()) {
    		Statement statement = statements.nextStatement();
    		String creatorURI = statement.getObject().toString();
    		StmtIterator names = model.listStatements(model.createResource(creatorURI), FOAF.name, (String) null);
    		if (names.hasNext()) {
    			authorName = names.nextStatement().getObject().toString();
    		}
    	}
    	return authorName;
    }
    
    private void initContextMapInformation() {
    	ContextMap contextMap = controller.getConceptMap();
    	
    	mapTitleField.setText(AttributeEntryUtil.getTitleAsString(contextMap));
    	mapTitleField.setCaretPosition(0);
    	mapURIField.setText(contextMap.getURI());
    	mapURIField.setCaretPosition(0);
    	
    	Config config = ConfigurationManager.getConfiguration();
    	CollaborillaSupport support = new CollaborillaSupport(config);
    	CollaborillaReader reader = new CollaborillaReader(support);
    	
    	boolean mapIsPublished = reader.isPublished(URI.create(contextMap.getURI()));
    	
    	if (mapIsPublished) {
    		CollaborillaDataSet dataSet = reader.getDataSet(URI.create(contextMap.getURI()), 1);
    		if (dataSet != null) {
    			mapPublishedField.setText(formatDate(dataSet.getTimestampCreated()));
    		} else {
    			mapPublishedField.setText("Unknown");
    		}
    		MetaDataCache cache = ConzillaKit.getDefaultKit().getMetaDataCache();
    		CollaborillaDataSet ds = cache.getDataSet(contextMap.getURI(), null);
    		if (ds != null) {
    			mapModifiedField.setText(formatDate(ds.getTimestampModified()));    		
    		} else {
    			mapModifiedField.setText("Unknown");
    		}
    	} else {
    		mapPublishedField.setText("Unpublished");
    		mapModifiedField.setText("Unpublished");
    	}
    }
    
    private String formatDate(Date date) {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    	String formattedDate = formatter.format(date);
    	return formattedDate;
    }
    
    private void initContextMapMetadata() {
        String containerString = controller.getConceptMap().getLoadContainer();
        URI contextMapURI = URI.create(controller.getConceptMap().getURI());
        Container container = null;
        try {
			container = ConzillaKit.getDefaultKit().getResourceStore().getAndReferenceContainer(URI.create(containerString));
		} catch (ComponentException e) {
			Tracer.debug(e.getMessage());
			return;
		}
		RDFModel model = (RDFModel) container;
		Resource resource = model.createResource(controller.getConceptMap().getURI());
		EditContainer mapContainer = new se.kth.nada.kmr.shame.applications.util.Container(model, contextMapURI);
		MetaDataPanel metaDataPanel = new MetaDataPanel("Context-Map Metadata", EditPanel.context_form, getBackground(), null);
		metaDataPanel.present(mapContainer, resource);
		mapMetadataScrollPane.setViewportView(metaDataPanel);
		resetScrollBarPosition(mapMetadataScrollPane);
    }
    
    private void initAuthorMetadata() {
    	Model sessionModel = getSessionMetaDataModel();
    	if (sessionModel == null) {
    		Tracer.debug("No session metadata found.");
    		tabbedPane.remove(authorPanel);
    		mapAuthorField.setText("Unknown");
    		return;
    	}
    	
    	String authorName = getAuthorName(sessionModel);
    	if (authorName != null) {
    		mapAuthorField.setText(authorName);
    		mapAuthorField.setCaretPosition(0);
    	} else {
    		mapAuthorField.setText("Unknown");
    	}

    	String creatorURI = null;
    	StmtIterator statements = sessionModel.listStatements(null, DC.creator, (String) null);
    	if (statements.hasNext()) {
    		Statement statement = statements.nextStatement();
    		creatorURI = statement.getObject().toString();
    	} else {
    		Tracer.debug("No author metadata found.");
    		tabbedPane.remove(authorPanel);
    		return;
    	}
    	
		AgentPane agentPane = new AgentPane(authorMetadataScrollPane);
		agentPane.present(new se.kth.nada.kmr.shame.applications.util.Container(sessionModel, null), sessionModel.createResource(creatorURI));
		authorMetadataScrollPane.setViewportView(agentPane);
		resetScrollBarPosition(authorMetadataScrollPane);
    }
    
    private Model getSessionMetaDataModel() {
    	String uri = controller.getConceptMap().getLoadContainer();
    	Model model = ModelFactory.createDefaultModel();
    	CollaborillaSupport support = new CollaborillaSupport(ConfigurationManager.getConfiguration());
    	CollaborillaReader collabReader = new CollaborillaReader(support);
    	String contributionInfo = collabReader.getMetaData(URI.create(uri));
    	if (contributionInfo != null) {
            StringReader sr = new StringReader(contributionInfo);
            model.read(sr, uri);
        } else {
        	model = null;
        }
    	return model;
    }
    
    private void initSessionMetadata() {
    	String uri = controller.getConceptMap().getLoadContainer();
    	Model contributionModel = getSessionMetaDataModel();
    	Resource resource;
    	
    	if (contributionModel != null) {
            resource = contributionModel.getResource(uri);
    	} else {
    		Tracer.debug("No session metadata found.");
    		tabbedPane.remove(sessionPanel);
    		return;
    	}  	
    	
    	// We don't want to show the author information here
    	StmtIterator statements = contributionModel.listStatements(null, DC.creator, (String) null);
    	contributionModel.remove(statements);
    	
    	EditContainer infoContainer = new se.kth.nada.kmr.shame.applications.util.Container(contributionModel, URI.create(uri));
        MetaDataPanel metaDataPanel = new MetaDataPanel("Contribution Metadata", EditPanel.container_form, getBackground(), null);
		metaDataPanel.present(infoContainer, resource);
		sessionMetadataScrollPane.setViewportView(metaDataPanel);
		resetScrollBarPosition(sessionMetadataScrollPane);
    }
    
    private void initContributionTab() {
    	// TODO show something here
    	tabbedPane.remove(contributionsPanel);
    }
    
    /**
     * Sets the position of the scrollbar's content to the left upper corner.
     * 
     * @param scrollPane JScrollBar to be resetted.
     */
    private void resetScrollBarPosition(final JScrollPane scrollPane) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
				vScrollBar.setValue(vScrollBar.getMinimum());
				JScrollBar hScrollBar = scrollPane.getHorizontalScrollBar();
				hScrollBar.setValue(hScrollBar.getMinimum());
			}
		});
    }
    
    /**
     * Adds an action listener to the close button.
     * 
     * @param listener Action listener to be executed.
     */
    public void addCloseButtonActionListener(ActionListener listener) {
    	closeButton.addActionListener(listener);
    }
    
    /**
     * Removes an action listener from the close button.
     * 
     * @param listener Action listener to be removed.
     */
    public void removeCloseButtonActionListener(ActionListener listener) {
    	closeButton.removeActionListener(listener);
    }
    
    private void initComponents() {
        tabbedPane = new javax.swing.JTabbedPane();
        mapPanel = new javax.swing.JPanel();
        mapInfoPanel = new javax.swing.JPanel();
        mapTitleLabel = new javax.swing.JLabel();
        mapURILabel = new javax.swing.JLabel();
        mapModifiedLabel = new javax.swing.JLabel();
        mapAuthorLabel = new javax.swing.JLabel();
        mapPublishedLabel = new javax.swing.JLabel();
        mapPublishedField = new javax.swing.JTextField();
        mapTitleField = new javax.swing.JTextField();
        mapURIField = new javax.swing.JTextField();
        mapAuthorField = new javax.swing.JTextField();
        mapModifiedField = new javax.swing.JTextField();
        mapMetadataScrollPane = new javax.swing.JScrollPane();
        sessionPanel = new javax.swing.JPanel();
        sessionMetadataScrollPane = new javax.swing.JScrollPane();
        authorPanel = new javax.swing.JPanel();
        authorMetadataScrollPane = new javax.swing.JScrollPane();
        contributionsPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();

        mapInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General Information"));
        mapTitleLabel.setText("Title");

        mapURILabel.setText("URI");

        mapModifiedLabel.setText("Modified");

        mapAuthorLabel.setText("Author");

        mapPublishedLabel.setText("Published");

        mapPublishedField.setEditable(false);
        mapPublishedField.setBorder(null);

        mapTitleField.setEditable(false);
        mapTitleField.setBorder(null);

        mapURIField.setEditable(false);
        mapURIField.setBorder(null);

        mapAuthorField.setEditable(false);
        mapAuthorField.setBorder(null);

        mapModifiedField.setEditable(false);
        mapModifiedField.setBorder(null);

        org.jdesktop.layout.GroupLayout mapInfoPanelLayout = new org.jdesktop.layout.GroupLayout(mapInfoPanel);
        mapInfoPanel.setLayout(mapInfoPanelLayout);
        mapInfoPanelLayout.setHorizontalGroup(
            mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mapInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mapInfoPanelLayout.createSequentialGroup()
                        .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mapTitleLabel)
                            .add(mapURILabel)
                            .add(mapAuthorLabel))
                        .add(30, 30, 30)
                        .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, mapAuthorField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, mapURIField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                            .add(mapTitleField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)))
                    .add(mapInfoPanelLayout.createSequentialGroup()
                        .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mapPublishedLabel)
                            .add(mapModifiedLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mapPublishedField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                            .add(mapModifiedField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))))
                .addContainerGap())
        );
        mapInfoPanelLayout.setVerticalGroup(
            mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mapInfoPanelLayout.createSequentialGroup()
                .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mapTitleLabel)
                    .add(mapTitleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mapURILabel)
                    .add(mapURIField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mapAuthorLabel)
                    .add(mapAuthorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mapPublishedLabel)
                    .add(mapPublishedField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mapInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mapModifiedLabel)
                    .add(mapModifiedField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        mapMetadataScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Metadata"));

        org.jdesktop.layout.GroupLayout mapPanelLayout = new org.jdesktop.layout.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mapMetadataScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mapInfoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mapInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mapMetadataScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabbedPane.addTab("Context-Map", mapPanel);

        sessionMetadataScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Metadata"));

        org.jdesktop.layout.GroupLayout sessionPanelLayout = new org.jdesktop.layout.GroupLayout(sessionPanel);
        sessionPanel.setLayout(sessionPanelLayout);
        sessionPanelLayout.setHorizontalGroup(
            sessionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sessionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(sessionMetadataScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                .addContainerGap())
        );
        sessionPanelLayout.setVerticalGroup(
            sessionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sessionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(sessionMetadataScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabbedPane.addTab("Session", sessionPanel);

        authorMetadataScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Metadata"));

        org.jdesktop.layout.GroupLayout authorPanelLayout = new org.jdesktop.layout.GroupLayout(authorPanel);
        authorPanel.setLayout(authorPanelLayout);
        authorPanelLayout.setHorizontalGroup(
            authorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authorMetadataScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                .addContainerGap())
        );
        authorPanelLayout.setVerticalGroup(
            authorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authorMetadataScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabbedPane.addTab("Author", authorPanel);

        org.jdesktop.layout.GroupLayout contributionsPanelLayout = new org.jdesktop.layout.GroupLayout(contributionsPanel);
        contributionsPanel.setLayout(contributionsPanelLayout);
        contributionsPanelLayout.setHorizontalGroup(
            contributionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 491, Short.MAX_VALUE)
        );
        contributionsPanelLayout.setVerticalGroup(
            contributionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 473, Short.MAX_VALUE)
        );
        tabbedPane.addTab("Contributions", contributionsPanel);

        closeButton.setText("Close");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .add(closeButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(closeButton)
                .addContainerGap())
        );
    }
    
}