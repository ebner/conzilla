/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.layout.GroupLayout;

import se.kth.cid.collaboration.CollaborillaConfiguration;
import se.kth.cid.collaboration.LocationInformation;
import se.kth.cid.component.ComponentException;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.agent.AgentManager;
import se.kth.cid.conzilla.agent.AgentPane;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFModel;
import se.kth.nada.kmr.shame.applications.util.Container;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Dialog to manipulate the collaboration settings.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class CollaborationSettingsDialog extends JFrame {
	
	Log log = LogFactory.getLog(CollaborationSettingsDialog.class);

	private DefaultListModel locationsModel;
	
	private RDFModel agentContainer;

	/* Visual components */

	private JButton addLocationButton;

	private JPanel authPanel;

	private JButton cancelButton;

	private JTabbedPane collaborationSettingsPane;

	private JButton editLocationButton;

	private JTextField serviceField;

	private JLabel serviceLabel;

	private JList locationsList;

	private JPanel locationsPanel;

	private JScrollPane locationsScrollPane;

	private JButton okButton;

	private JPasswordField passwordField;

	private JLabel passwordLabel;

	private JButton removeLocationButton;

	private JPanel serverPanel;

	private JPanel serverSettingsPanel;

	private JTextField userField;

	private JLabel userLabel;
	
    private JTextField namespaceField;
    
    private JLabel namespaceLabel;
    
    private JPanel namespacePanel;
    
    private JButton createAgentInfoButton;
    
    private JScrollPane infoScrollPane;
    
    private javax.swing.JPanel proxyPanel;

	private javax.swing.JTextField proxyPortField;

	private javax.swing.JLabel proxyPortLabel;

	private javax.swing.JTextField proxyServerField;

	private javax.swing.JLabel proxyServerLabel;

	/**
	 * Creates new form CollaborationSettings.
	 */
	public CollaborationSettingsDialog() {
		initComponents();
		loadSettings();
		updateButtons();
		setTitle("Collaboration settings");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
	}

	/**
	 * Initializes visual components.
	 */
	private void initComponents() {
		collaborationSettingsPane = new JTabbedPane();
		serverSettingsPanel = new JPanel();
		serverPanel = new JPanel();
		serviceLabel = new JLabel();
		serviceField = new JTextField();
		authPanel = new JPanel();
		userLabel = new JLabel();
		passwordLabel = new JLabel();
		userField = new JTextField();
		passwordField = new JPasswordField();
		locationsPanel = new JPanel();
		locationsScrollPane = new JScrollPane();
		addLocationButton = new JButton();
		removeLocationButton = new JButton();
		editLocationButton = new JButton();
		okButton = new JButton();
		cancelButton = new JButton();
        namespacePanel = new JPanel();
        namespaceLabel = new JLabel();
        namespaceField = new JTextField();
        createAgentInfoButton = new JButton();
        infoScrollPane = new JScrollPane();
        proxyPanel = new javax.swing.JPanel();
        proxyServerLabel = new javax.swing.JLabel();
        proxyServerField = new javax.swing.JTextField();
        proxyPortLabel = new javax.swing.JLabel();
        proxyPortField = new javax.swing.JTextField();

		locationsList = new JList() {
			public String getToolTipText(MouseEvent evt) {
				int index = locationToIndex(evt.getPoint());
				LocationInformation info = (LocationInformation) getModel().getElementAt(index);
				return info.getToolTip();
			}
		};

		locationsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					editLocationButtonActionPerformed(new ActionEvent(evt.getSource(), evt.getID(), ""));
				}
			}
		});
		
		locationsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                locationsListValueChanged(evt);
            }
        });

		serverPanel.setBorder(BorderFactory.createTitledBorder("Collaborilla server"));
		serviceLabel.setText("Service");

        org.jdesktop.layout.GroupLayout serverPanelLayout = new org.jdesktop.layout.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(serviceLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serviceField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addContainerGap())
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(serverPanelLayout.createSequentialGroup()
                .add(serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serviceLabel)
                    .add(serviceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

		userLabel.setText("Username");
		userLabel.setEnabled(false);

		passwordLabel.setText("Password");
		passwordLabel.setEnabled(false);

		userField.setEnabled(false);
		passwordField.setEnabled(false);

		TitledBorder authBorder = BorderFactory.createTitledBorder("Authentication");
		authPanel.setBorder(authBorder);

		authPanel.setVisible(true);
		authPanel.setEnabled(false);
        org.jdesktop.layout.GroupLayout authPanelLayout = new org.jdesktop.layout.GroupLayout(authPanel);
        authPanel.setLayout(authPanelLayout);
        authPanelLayout.setHorizontalGroup(
            authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(userLabel)
                    .add(passwordLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(passwordField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                    .add(userField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                .addContainerGap())
        );
        authPanelLayout.setVerticalGroup(
            authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authPanelLayout.createSequentialGroup()
                .add(authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userLabel)
                    .add(userField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(passwordLabel)
                    .add(passwordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        proxyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("HTTP Proxy"));

        proxyServerLabel.setText("Server");

        proxyPortLabel.setText("Port");

        org.jdesktop.layout.GroupLayout proxyPanelLayout = new org.jdesktop.layout.GroupLayout(proxyPanel);
        proxyPanel.setLayout(proxyPanelLayout);
        proxyPanelLayout.setHorizontalGroup(
            proxyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(proxyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(proxyServerLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(proxyServerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(proxyPortLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(proxyPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        proxyPanelLayout.setVerticalGroup(
            proxyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(proxyPanelLayout.createSequentialGroup()
                .add(proxyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(proxyServerLabel)
                    .add(proxyPortLabel)
                    .add(proxyPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(proxyServerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        org.jdesktop.layout.GroupLayout serverSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(serverSettingsPanel);
        serverSettingsPanel.setLayout(serverSettingsPanelLayout);
        serverSettingsPanelLayout.setHorizontalGroup(
            serverSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(serverSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(serverSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(serverPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(authPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, proxyPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        serverSettingsPanelLayout.setVerticalGroup(
            serverSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(serverSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(serverPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(proxyPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

		collaborationSettingsPane.addTab("Server settings", serverSettingsPanel);

		locationsModel = new DefaultListModel();
		locationsList.setModel(locationsModel);
		locationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		locationsScrollPane.setViewportView(locationsList);

		addLocationButton.setText("Add");
		addLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addLocationButtonActionPerformed(evt);
			}
		});

		removeLocationButton.setText("Remove");
		removeLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				removeLocationButtonActionPerformed(evt);
			}
		});

		editLocationButton.setText("Edit");
		editLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				editLocationButtonActionPerformed(evt);
			}
		});

        org.jdesktop.layout.GroupLayout locationsPanelLayout = new org.jdesktop.layout.GroupLayout(locationsPanel);
        locationsPanel.setLayout(locationsPanelLayout);
        locationsPanelLayout.setHorizontalGroup(
            locationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(locationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(locationsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(locationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(addLocationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .add(removeLocationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(editLocationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                .addContainerGap())
        );
        locationsPanelLayout.setVerticalGroup(
            locationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(locationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(locationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(locationsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                    .add(locationsPanelLayout.createSequentialGroup()
                        .add(addLocationButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeLocationButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editLocationButton)))
                .addContainerGap())
        );
		collaborationSettingsPane.addTab("Locations", locationsPanel);
		
		namespaceLabel.setText("Namespace:");

        createAgentInfoButton.setText("Create personal information");
        createAgentInfoButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String namespace = namespaceField.getText().trim();
        		boolean uriValid = isNamespaceValid(namespace);
				if (uriValid) {
	        		if (agentContainer != null) {
	        			int answer = JOptionPane.showConfirmDialog(CollaborationSettingsDialog.this,
	        					"This will override your already existing personal information.\nDo you want to continue?",
	        					"Continue?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	        			if (answer == JOptionPane.NO_OPTION) {
	        				return;
	        			}
	        		}
	        		CollaborillaConfiguration collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
        			createAgentInformation(collabConfig.getAgentURI(namespace));
        		} else {
        			JOptionPane.showMessageDialog(CollaborationSettingsDialog.this,
        					"Please enter a correct namespace.\n\n" +
							"Example:\nhttp://conzilla.org/users/firstname.lastname",
							"Invalid namespace", JOptionPane.ERROR_MESSAGE);
        		}
        	}
        });

        org.jdesktop.layout.GroupLayout namespacePanelLayout = new org.jdesktop.layout.GroupLayout(namespacePanel);
        namespacePanel.setLayout(namespacePanelLayout);
        namespacePanelLayout.setHorizontalGroup(
            namespacePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, namespacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(namespacePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, createAgentInfoButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, namespacePanelLayout.createSequentialGroup()
                        .add(namespaceLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(namespaceField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
                    .add(infoScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE))
                .addContainerGap())
        );
        namespacePanelLayout.setVerticalGroup(
            namespacePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(namespacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(namespacePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(namespaceLabel)
                    .add(namespaceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createAgentInfoButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(infoScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addContainerGap())
        );
        collaborationSettingsPane.addTab("Personal Information", namespacePanel);

		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								layout.createSequentialGroup().add(okButton).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(cancelButton)).add(
								collaborationSettingsPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361,
								Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				layout.createSequentialGroup().addContainerGap().add(collaborationSettingsPane,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(cancelButton).add(
								okButton)).addContainerGap()));
	}
	
	private void locationsListValueChanged(ListSelectionEvent evt) {
		updateButtons();
    }
	
	private void initAgentInformation() {
		CollaborillaConfiguration collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
		ConzillaKit kit = ConzillaKit.getDefaultKit();
		try {
			agentContainer = (RDFModel) kit.getResourceStore().getAndReferenceContainer(CollaborillaConfiguration.AGENT_LOAD_URI);
		} catch (ComponentException e) {
		}
		if ((agentContainer != null) && (collabConfig.getAgentURI(collabConfig.getUserNamespace()) != null)) {
			agentContainer.setPurpose(AgentManager.AGENT);
			AgentPane agentPane = new AgentPane(infoScrollPane);
			agentPane.edit(new Container(agentContainer, null), agentContainer.createResource(collabConfig.getAgentURI(collabConfig.getUserNamespace())));
			infoScrollPane.setViewportView(agentPane);
		} else {
			infoScrollPane.setVisible(false);
		}
	}
	
	private void saveAgentInformation() {
		if (agentContainer != null) {
			agentContainer.setEdited(true);
			try {
				ConzillaKit.getDefaultKit().getResourceStore().getComponentManager().saveResource(agentContainer);
			} catch (ComponentException e) {
				log.error("Agent information could not be saved", e);
			}
		}
	}
	
	private void createAgentInformation(String agentURI) {
		if (agentURI == null) {
			return;
		}
		ConzillaKit kit = ConzillaKit.getDefaultKit();
		URI uri = CollaborillaConfiguration.AGENT_LOAD_URI;
		try {
			Object[] objs = kit.getResourceStore().checkCreateContainer(uri);
			agentContainer = (RDFModel) kit.getResourceStore().createContainer(uri, (URI) objs[0], (MIMEType) objs[1]);
		} catch (ComponentException e) {
			log.error("No existing file with information on agent and cannot create new either", e);
			throw new RuntimeException("No existing file with information on agent and cannot create new either!");
		}
		agentContainer.setPurpose("agent");
		Resource agentResource = agentContainer.createResource(agentURI);
		agentContainer.add(agentContainer.createStatement(agentResource, RDF.type, CV.Agent));
		AgentPane agentPane = new AgentPane(infoScrollPane);
		infoScrollPane.setViewportView(agentPane);
		agentPane.edit(new Container(agentContainer, null), agentContainer.createResource(agentURI));
		infoScrollPane.setVisible(true);
		repaint();
	}
	
	private void updateButtons() {
		boolean enabled = false;
    	if (locationsModel.size() > 0) {
    		enabled = true;
    	}
    	editLocationButton.setEnabled(enabled);
		removeLocationButton.setEnabled(enabled);
	}

	/**
	 * Shows a dialog to edit an already existing location entry.
	 * 
	 * @param evt
	 */
	private void editLocationButtonActionPerformed(ActionEvent evt) {
		int index = locationsList.getSelectedIndex();
		if (index > -1) {
			LocationInformation info = (LocationInformation) locationsList.getSelectedValue();
			PublishingLocationDialog dialog = new PublishingLocationDialog(this, info);
			if (dialog.showDialog()) {
				LocationInformation newInfo = dialog.getLocationInformation();
				locationsModel.setElementAt(newInfo, index);
			}
		}
	}

	/**
	 * Removes the currently selected entry from the list.
	 * 
	 * @param evt
	 */
	private void removeLocationButtonActionPerformed(ActionEvent evt) {
		int index = locationsList.getSelectedIndex();
		if (index > -1) {
			locationsModel.remove(index);
			if (index == locationsModel.getSize()) {
				index--;
			}
			locationsList.setSelectedIndex(index);
			locationsList.ensureIndexIsVisible(index);
		}
	}

	/**
	 * Shows a dialog to edit new location information and adds a new location to the bottom of the list.
	 * 
	 * @param evt
	 */
	private void addLocationButtonActionPerformed(ActionEvent evt) {
		PublishingLocationDialog dialog = new PublishingLocationDialog(this, null);
		if (dialog.showDialog()) {
			LocationInformation info = dialog.getLocationInformation();
			locationsModel.addElement(info);
		}
	}

	/**
	 * Disposes the window without saving the settings.
	 * 
	 * @param evt
	 */
	private void cancelButtonActionPerformed(ActionEvent evt) {
		setVisible(false);
		dispose();
	}

	/**
	 * Stores the settings and disposes the window.
	 * 
	 * @param evt
	 */
	private void okButtonActionPerformed(ActionEvent evt) {
		String message = settingStatus();
		if (message == null) {
			setVisible(false);
			storeSettings();
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, message, "Incomplete configuration", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Loads the current settings from the configuration and sets the fields in the form.
	 */
	private void loadSettings() {
		CollaborillaConfiguration collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
		serviceField.setText(collabConfig.getCollaborillaServiceRoot());
		proxyServerField.setText(collabConfig.getProxyServer());
		proxyPortField.setText(collabConfig.getProxyPort());
		locationsModel = collabConfig.getLocationsListModel();
		locationsList.setModel(locationsModel);
		namespaceField.setText(collabConfig.getUserNamespace());
		initAgentInformation();
	}

	/**
	 * Stores the settings to the configuration.
	 */
	private void storeSettings() {
		CollaborillaConfiguration collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
		collabConfig.setCollaborillaServiceRoot(serviceField.getText());
		collabConfig.setProxyServer(proxyServerField.getText());
		collabConfig.setProxyPort(proxyPortField.getText());
		collabConfig.storeLocations(locationsModel);
		collabConfig.setUserNamespace(namespaceField.getText());
		saveAgentInformation();
	}
	
	private String settingStatus() {
		if (serviceField.getText().trim().length() == 0) {
			return "Please enter the hostname or IP address of the collaboration service.";
		}
		if (!isNamespaceValid(namespaceField.getText().trim())) {
			return "Please enter a correct namespace.\n\n" +
				"Example:\nhttp://conzilla.org/users/firstname.lastname";
		}
		if (locationsModel.getSize() == 0) {
			return "Please configure at least one publication location.";
		}
		if (agentContainer == null) {
			return "Please configure your personal information.";
		}
		
		return null;
	}
	
	private boolean isNamespaceValid(String namespace) {
		if (namespace.length() == 0) {
			return false;
		}
		try {
			URI ns = new URI(namespace);
			if (ns.getScheme() == null) {
				return false;
			}
			if (ns.getHost() == null) {
				return false;
			}
			if (ns.getPath() == null) {
				return false;
			}
		} catch (URISyntaxException urise) {
			return false;
		}
		return true;
	}

}