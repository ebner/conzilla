/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.config;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jdesktop.layout.GroupLayout;

import se.kth.cid.collaboration.LocationInformation;

/**
 * Dialog to query location information from the user.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class PublishingLocationDialog extends JDialog {

	private LocationInformation info;

	private Container pane;

	private GroupLayout layout;

	private JPanel generalInfoPanel;

	private JPanel locationsPanel;

	private JLabel titleLabel;

	private JTextField titleField;

	private JTextField descField;

	private JLabel descLabel;

	private JLabel publishingLocationLabel;

	private JTextField publishingLocationField;

	private JLabel publicLocationLabel;

	private JTextField publicLocationField;

	private JButton okButton;

	private JButton cancelButton;

	private boolean closedWithOK;

	/**
	 * @param owner
	 *            The owner/parent window of this dialog. (Important for the
	 *            modal setting of this dialog.
	 * @param locationInfo
	 *            Can be used to set the text-field values. May be null, empty
	 *            fields are presented to the user then.
	 */
	public PublishingLocationDialog(JFrame owner, LocationInformation locationInfo) {
		super(owner);
		if (locationInfo != null) {
			info = locationInfo;
		} else {
			info = new LocationInformation();
		}
		initComponents();
		setModal(true);
		setTitle("Publishing Location");
		setLocationRelativeTo(owner);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		pack();
	}

	/**
	 * Shows the dialog.
	 * 
	 * @return Returns true if the dialog was closed via the OK button,
	 *         otherwise returns false.
	 */
	public boolean showDialog() {
		setVisible(true);
		return closedWithOK;
	}

	/**
	 * Initializes the graphical components.
	 */
	private void initComponents() {
		pane = getContentPane();
		layout = new GroupLayout(pane);
		pane.setLayout(layout);

		locationsPanel = new javax.swing.JPanel();
		locationsPanel.setBorder(BorderFactory.createTitledBorder("Locations"));

		generalInfoPanel = new javax.swing.JPanel();
		generalInfoPanel.setBorder(BorderFactory.createTitledBorder("General Information"));

		titleLabel = new JLabel("Title");
		titleField = new JTextField(info.getTitle());

		descLabel = new javax.swing.JLabel("Decription");
		descField = new javax.swing.JTextField(info.getDescription());

		publishingLocationLabel = new JLabel("Publishing URL");
		publishingLocationField = new JTextField(info.getPublishingLocation());
		publishingLocationField.setToolTipText("<html><body><b>Examples</b><br><br>" +
				"WebDAV<br>dav://server.tld/directory<br><br>" +
				"Secure WebDAV (SSL)<br>davs://server.tld/directory<br><br>" +
				"FTP<br>ftp://server.tld/directory<br><br>" +
				"Authenticated Connections<br>protocol://username:password@server.tld/directory</body></html>");

		publicLocationLabel = new JLabel("Public Access URL");
		publicLocationField = new JTextField(info.getPublicAccessLocation());

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {				
				if ((titleField.getText().trim().length() > 0)
						&& (descField.getText().trim().length() > 0)
						&& (publishingLocationField.getText().trim().length() > 0)
						&& (publicLocationField.getText().trim().length() > 0)) {
					closedWithOK = true;
					setVisible(false);
				} else {
					JOptionPane.showMessageDialog(PublishingLocationDialog.this,
							"The supplied information is incomplete.", "Information incomplete",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});

		org.jdesktop.layout.GroupLayout locationsPanelLayout = new org.jdesktop.layout.GroupLayout(locationsPanel);
		locationsPanel.setLayout(locationsPanelLayout);
		locationsPanelLayout.setHorizontalGroup(locationsPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				locationsPanelLayout.createSequentialGroup().add(
						locationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
								locationsPanelLayout.createSequentialGroup().add(29, 29, 29).add(
										publishingLocationLabel).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(publishingLocationField,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)).add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								locationsPanelLayout.createSequentialGroup().addContainerGap().add(publicLocationLabel)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
												publicLocationField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 281,
												Short.MAX_VALUE))).addContainerGap()));
		locationsPanelLayout.setVerticalGroup(locationsPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				locationsPanelLayout.createSequentialGroup().add(
						locationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
								publishingLocationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(publishingLocationLabel)).add(8, 8,
						8).add(
						locationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
								publicLocationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(publicLocationLabel))
						.addContainerGap(14, Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout generalInfoPanelLayout = new org.jdesktop.layout.GroupLayout(generalInfoPanel);
		generalInfoPanel.setLayout(generalInfoPanelLayout);
		generalInfoPanelLayout.setHorizontalGroup(generalInfoPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				generalInfoPanelLayout.createSequentialGroup().addContainerGap().add(
						generalInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
								titleLabel).add(descLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(
								generalInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
										.add(titleField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 320,
												Short.MAX_VALUE).add(descField,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
						.addContainerGap()));
		generalInfoPanelLayout.setVerticalGroup(generalInfoPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				generalInfoPanelLayout.createSequentialGroup().add(
						generalInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
								titleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(titleLabel)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						generalInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
								descField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(descLabel)).addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(
								layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
										generalInfoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(
										org.jdesktop.layout.GroupLayout.TRAILING, locationsPanel,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(
										org.jdesktop.layout.GroupLayout.TRAILING,
										layout.createSequentialGroup().add(okButton).addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED).add(cancelButton)))
								.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(generalInfoPanel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(locationsPanel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(cancelButton).add(
								okButton)).addContainerGap(14, Short.MAX_VALUE)));
	}

	/**
	 * Used to read information out of the dialog. To be used after a successful
	 * (return value == true) showDialog() command.
	 * 
	 * @return Reads the values of the text-field and returns a
	 *         LocationInformation object.
	 */
	public LocationInformation getLocationInformation() {
		return new LocationInformation(titleField.getText(), descField.getText(), publishingLocationField.getText(),
				publicLocationField.getText());
	}

}