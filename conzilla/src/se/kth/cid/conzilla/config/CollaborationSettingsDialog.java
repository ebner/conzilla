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

import org.jdesktop.layout.GroupLayout;

import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.collaboration.LocationInformation;
import se.kth.cid.component.ComponentException;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.agent.AgentManager;
import se.kth.cid.conzilla.agent.AgentPane;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.util.Tracer;
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

	private DefaultListModel locationsModel;
	
	private RDFModel agentContainer;

	/* Visual components */

	private JButton addLocationButton;

	private JPanel authPanel;

	private JButton cancelButton;

	private JTabbedPane collaborationSettingsPane;

	private JButton editLocationButton;

	private JTextField hostField;

	private JLabel hostLabel;

	private JList locationsList;

	private JPanel locationsPanel;

	private JScrollPane locationsScrollPane;

	private JButton okButton;

	private JPasswordField passwordField;

	private JLabel passwordLabel;

	private JTextField portField;

	private JLabel portLabel;

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

	/**
	 * Creates new form CollaborationSettings.
	 */
	public CollaborationSettingsDialog() {
		initComponents();
		loadSettings();
		updateButtons();
		setTitle("Collaboration settings");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
	}

	/**
	 * Initializes visual components.
	 */
	private void initComponents() {
		collaborationSettingsPane = new JTabbedPane();
		serverSettingsPanel = new JPanel();
		serverPanel = new JPanel();
		hostLabel = new JLabel();
		portLabel = new JLabel();
		hostField = new JTextField();
		portField = new JTextField();
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
		hostLabel.setText("Host:");
		portLabel.setText("Port:");

		GroupLayout serverPanelLayout = new GroupLayout(serverPanel);
		serverPanel.setLayout(serverPanelLayout);
		serverPanelLayout.setHorizontalGroup(serverPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				serverPanelLayout.createSequentialGroup().addContainerGap().add(
						serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(hostLabel)
								.add(portLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
						serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(hostField,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE).add(portField,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		serverPanelLayout.setVerticalGroup(serverPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				serverPanelLayout.createSequentialGroup().add(
						serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(hostLabel)
								.add(hostField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						serverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(portLabel)
								.add(portField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		userLabel.setText("Username:");
		userLabel.setEnabled(false);

		passwordLabel.setText("Password:");
		passwordLabel.setEnabled(false);

		userField.setEnabled(false);
		passwordField.setEnabled(false);

		TitledBorder authBorder = BorderFactory.createTitledBorder("Authentication");
		authPanel.setBorder(authBorder);

		GroupLayout authPanelLayout = new GroupLayout(authPanel);
		authPanel.setLayout(authPanelLayout);
		authPanel.setVisible(true);
		authPanel.setEnabled(false);
		authPanel.setLayout(authPanelLayout);
		authPanelLayout.setHorizontalGroup(authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(
						authPanelLayout.createSequentialGroup().addContainerGap().add(
								authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
										userLabel).add(passwordLabel)).addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
										passwordField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224,
										Short.MAX_VALUE).add(userField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										224, Short.MAX_VALUE)).addContainerGap()));
		authPanelLayout.setVerticalGroup(authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(
						authPanelLayout.createSequentialGroup().add(
								authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
										userLabel).add(userField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
										passwordLabel).add(passwordField,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap(
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		GroupLayout serverSettingsPanelLayout = new GroupLayout(serverSettingsPanel);
		serverSettingsPanel.setLayout(serverSettingsPanelLayout);
		serverSettingsPanelLayout.setHorizontalGroup(serverSettingsPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				serverSettingsPanelLayout.createSequentialGroup().addContainerGap().add(
						serverSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
								serverPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(
								org.jdesktop.layout.GroupLayout.TRAILING, authPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
		serverSettingsPanelLayout.setVerticalGroup(serverSettingsPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				serverSettingsPanelLayout.createSequentialGroup().addContainerGap().add(serverPanel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(authPanel,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE).add(21, 21, 21)));

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
        		String uriValid = null;
        		if (namespace.length() == 0) {
        			uriValid = "Please enter a valid namespace first.";
        		}
        		if (agentContainer != null) {
        			int answer = JOptionPane.showConfirmDialog(CollaborationSettingsDialog.this,
        					"This will override your already existing personal information.\nDo you want to continue?",
        					"Continue?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        			if (answer == JOptionPane.NO_OPTION) {
        				return;
        			}
        		}
        		try {
					new URI(namespace);
				} catch (URISyntaxException urise) {
					uriValid = urise.getMessage();
				}
				if (uriValid == null) {
					CollaborillaSupport support = new CollaborillaSupport(ConfigurationManager.getConfiguration());
        			createAgentInformation(support.getAgentURI(namespace));
        		} else {
        			JOptionPane.showMessageDialog(CollaborationSettingsDialog.this, uriValid, "Invalid namespace", JOptionPane.ERROR_MESSAGE);
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
		CollaborillaSupport support = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		ConzillaKit kit = ConzillaKit.getDefaultKit();
		try {
			agentContainer = (RDFModel) kit.getResourceStore().getAndReferenceContainer(CollaborillaSupport.AGENT_LOAD_URI);
		} catch (ComponentException e) {
		}
		if ((agentContainer != null) && (support.getAgentURI(support.getUserNamespace()) != null)) {
			agentContainer.setPurpose(AgentManager.AGENT);
			AgentPane agentPane = new AgentPane(infoScrollPane);
			agentPane.edit(new Container(agentContainer, null), agentContainer.createResource(support.getAgentURI(support.getUserNamespace())));
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
				Tracer.debug("Agent information could not be saved: " + e.getMessage());
			}
		}
	}
	
	private void createAgentInformation(String agentURI) {
		if (agentURI == null) {
			return;
		}
		ConzillaKit kit = ConzillaKit.getDefaultKit();
		URI uri = CollaborillaSupport.AGENT_LOAD_URI;
		try {
			Object[] objs = kit.getResourceStore().checkCreateContainer(uri);
			agentContainer = (RDFModel) kit.getResourceStore().createContainer(uri, (URI) objs[0], (MIMEType) objs[1]);
		} catch (ComponentException e) {
			Tracer.bug(e.getMessage());
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
		CollaborillaSupport support = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		hostField.setText(support.getCollaborillaServer());
		portField.setText(Integer.toString(support.getCollaborillaServerPort()));
		locationsModel = support.getLocationsListModel();
		locationsList.setModel(locationsModel);
		namespaceField.setText(support.getUserNamespace());
		initAgentInformation();
	}

	/**
	 * Stores the settings to the configuration.
	 */
	private void storeSettings() {
		CollaborillaSupport support = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		support.setCollaborillaServer(hostField.getText());
		support.setCollaborillaServerPort(Integer.parseInt(portField.getText()));
		support.storeLocations(locationsModel);
		support.setUserNamespace(namespaceField.getText());
		saveAgentInformation();
	}
	
	private String settingStatus() {
		if (hostField.getText().trim().length() == 0) {
			return "Please enter the hostname or IP address of the collaboration service.";
		}
		if (portField.getText().trim().length() == 0) {
			return "Please enter the port number of the collaboration service.";
		}
		if (namespaceField.getText().trim().length() == 0) {
			return "Please enter your personal namespace.";
		}
		if (locationsModel.getSize() == 0) {
			return "Please configure at least one publication location.";
		}
		if (agentContainer == null) {
			return "Please configure your personal information.";
		}
		
		return null;
	}

}