/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.collaboration;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import se.kth.cid.collaboration.ContextMapPublisher;

/**
 * Provides a modal dialog with a progress bar and a text area with detailed
 * information. Implements a PropertyChangeListener to receive progress updates.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class ProgressInformation implements PropertyChangeListener {

	private JProgressBar progressBar;

	private JToggleButton detailsButton;

	private JButton closeButton;

	private JCheckBox closeOnFinished;

	private JTextArea infoArea;

	private JPanel mainPanel;

	private JScrollPane detailsPane;

	private JDialog dialog;

	private JFrame owner;

	private boolean aborted;

	/**
	 * @param owner Owner of the modal dialog.
	 * @param title Title of the window.
	 */
	public ProgressInformation(JFrame owner, String title) {
		this.dialog = new JDialog(owner, title, true);
		this.owner = owner;
		initComponents();
	}

	/* PRIVATE */

	/**
	 * Initializes all components with start values.
	 */
	private void initComponents() {
		mainPanel = new JPanel(new BorderLayout());

		detailsButton = new JToggleButton("Details", false);
		detailsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				toggleDetails();
			}
		});

		closeButton = new JButton("Close");
		closeButton.setEnabled(false);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dialog.dispose();
				if (!aborted) {
					owner.dispose();
				}
			}
		});

		closeOnFinished = new JCheckBox("Close this window when done", true);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);

		infoArea = new JTextArea(10, 25);
		infoArea.setMargin(new Insets(5, 5, 5, 5));
		infoArea.setEditable(false);

		JPanel topPanel = new JPanel();
		topPanel.add(progressBar, BorderLayout.PAGE_START);
		topPanel.add(detailsButton);
		topPanel.add(closeButton);

		JPanel checkPanel = new JPanel();
		checkPanel.add(closeOnFinished);

		detailsPane = new JScrollPane(infoArea);
		detailsPane.setAutoscrolls(true);

		mainPanel.add(topPanel, BorderLayout.PAGE_START);
		mainPanel.add(checkPanel, BorderLayout.LINE_START);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		dialog.setContentPane(mainPanel);
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// frame.setAlwaysOnTop(true);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
	}

	/**
	 * Listens for progress changes and updates the components holding the information.
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getPropertyName().equals(ContextMapPublisher.PROP_PROGRESS_PERCENTAGE)) {
			final int percentage = ((Integer) pce.getNewValue()).intValue();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setPercentage(percentage);
				}
			});
		} else if (pce.getPropertyName().equals(ContextMapPublisher.PROP_PROGRESS_INFO)) {
			String information = (String) pce.getNewValue();
			printInformation(information);
		} else if (pce.getPropertyName().equals(ContextMapPublisher.PROP_PROGRESS_ERROR)) {
			String error = (String) pce.getNewValue();
			printError(error);
		} else if (pce.getPropertyName().equals(ContextMapPublisher.PROP_PROGRESS_FINISHED)) {
			done();
		} else if (pce.getPropertyName().equals(ContextMapPublisher.PROP_PROGRESS_CANCELLED)) {
			cancel();
		}
	}

	/**
	 * Switches the detailed view on and off.
	 */
	private void toggleDetails() {
		if (detailsButton.isSelected()) {
			mainPanel.add(detailsPane, BorderLayout.PAGE_END);
		} else {
			mainPanel.remove(detailsPane);
		}
		dialog.pack();
	}

	/**
	 * Called when something goes wrong and the activity is cancelled. Shows the
	 * detailed view if it is not shown already.
	 */
	private void cancel() {
		if (!detailsButton.isSelected()) {
			detailsButton.setSelected(true);
			toggleDetails();
		}
		printInformation("\nPublishing cancelled. Please check for errors above.");
		aborted = true;
		closeButton.setEnabled(true);
	}

	/**
	 * Completes the process and closes the dialog depending on the setting.
	 */
	private void done() {
		if (!aborted && closeOnFinished.isSelected()) {
			dialog.dispose();
			owner.dispose();
		} else {
			progressBar.setValue(100);
			closeButton.setEnabled(true);
		}
	}

	/* PUBLIC */

	/**
	 * Sets the percentage of the progress bar.
	 * 
	 * @param percentage Percentage of the progress bar, can be 0-100.
	 */
	public void setPercentage(int percentage) {
		if ((progressBar.isIndeterminate()) && (percentage > 0)) {
			progressBar.setIndeterminate(false);
		}
		progressBar.setValue(percentage);
	}

	/**
	 * @param info Information to be added to the text area.
	 */
	public void printInformation(final String info) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoArea.append(info + "\n");
			}
		});
	}

	/**
	 * Shows an error message and shows the detailed view of the dialog if it is
	 * not shown already.
	 * 
	 * @param info
	 *            Error message to be added to the text area.
	 */
	public void printError(String info) {
		if (!detailsButton.isSelected()) {
			toggleDetails();
		}
		closeOnFinished.setSelected(false);
		printInformation("ERROR: " + info);
	}

	/**
	 * Sets the visibility of the dialog.
	 * 
	 * @param visible True if the dialog is to be shown.
	 */
	public void setVisible(final boolean visible) {
		// this blocks on a JDialog, so we run it in a thread
		new Thread(new Runnable() {
			public void run() {
				dialog.setVisible(visible);
			}
		}).start();
	}

	/**
	 * Disposes the dialog.
	 */
	public void dispose() {
		dialog.dispose();
	}

}