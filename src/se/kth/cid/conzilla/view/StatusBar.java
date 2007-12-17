/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.metadata.ContextMapInfoPanel;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.layout.ContextMap;

/**
 * Status bar to show information related to the currently visible context-map.
 * 
 * @author Hannes Ebner
 */
public class StatusBar extends JPanel implements PropertyChangeListener {
	
	private JLabel statusText;
	
	private JPanel content;
	
	private JLabel gap;
	
	private JProgressBar progressBar;
	
	private MouseAdapter mouseAdapter;
	
	private MapController controller;
	
	private boolean mapPublished = false;
	
	private boolean containerPublished = false;
	
	public StatusBar(MapController controller) {
		this.controller = controller;
		
        statusText = new JLabel();
        statusText.setFont(new java.awt.Font("Dialog", 0, 12));
        statusText.setIconTextGap(6);
		
		progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(new java.awt.Font("Dialog", 0, 12));
        content = new JPanel();
        content.add(progressBar);
		gap = new JLabel(" ");
        
        setLayout(new BorderLayout());
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        
        add(gap, BorderLayout.WEST);
        add(statusText, BorderLayout.CENTER);
        add(content, BorderLayout.EAST);
        
        mouseAdapter = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				final JFrame frame = new JFrame("Context-Map Information");
				final ContextMapInfoPanel infoPanel = new ContextMapInfoPanel(StatusBar.this.controller);
				infoPanel.addCloseButtonActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						frame.dispose();
					}
				});
				frame.getContentPane().add(infoPanel);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
				frame.setVisible(true);
				
//				StatusBar.this.controller.getView().addToRight(infoPanel, "Context-Map Information", new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						StatusBar.this.controller.getView().removeFromRight(infoPanel);
//					}
//				});
			}
		};
	}
	
	private void setStatusText(String text) {
		statusText.setText(text);
	}
	
	private void setOnlineIcon(boolean online) {
		if (online) {
			statusText.setIcon(Images.getImageIcon(Images.ICON_MAP_PUBLISHED));
		} else {
			statusText.setIcon(Images.getImageIcon(Images.ICON_MAP_PUBLISHED_GREY));
		}
	}
	
	private void updateInformation() {
		ContextMap map = controller.getConceptMap();
		Config config = ConfigurationManager.getConfiguration();
		CollaborillaSupport collabSupport = new CollaborillaSupport(config);
		CollaborillaReader reader = new CollaborillaReader(collabSupport);
		
		mapPublished = reader.isPublished(URI.create(map.getURI()));
		containerPublished = reader.isPublished(URI.create(map.getLoadContainer()));
		
		if (mapPublished) {
			setStatusText("This context-map is published. Click here for additional information.");
			setOnlineIcon(true);
			statusText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			statusText.removeMouseListener(mouseAdapter);
			statusText.addMouseListener(mouseAdapter);
		} else if (containerPublished) {
			setStatusText("This context-map is not published. Its container is published. Click here for additional information.");
			setOnlineIcon(false);
			statusText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			statusText.removeMouseListener(mouseAdapter);
			statusText.addMouseListener(mouseAdapter);
		} else {
			setStatusText("This context-map is not published.");
			setOnlineIcon(false);
			statusText.setCursor(Cursor.getDefaultCursor());
			statusText.removeMouseListener(mouseAdapter);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof MapController) {
			Object infoObject = evt.getNewValue();
			String propertyName = evt.getPropertyName();
			if (propertyName.equals(MapController.MAP_PROPERTY)) {
				progressBar.setIndeterminate(false);
				progressBar.setString("Done");
				updateInformation();
			} else if (propertyName.equals(MapController.MAP_LOADING)) {
				progressBar.setIndeterminate(true);
				progressBar.setString("Loading");
				setStatusText("Loading context-map...");
			} else if (propertyName.equals(MapController.MAP_LOADING_FAILED)) {
				if (infoObject instanceof Exception) {
					progressBar.setIndeterminate(false);
					setStatusText("Unable to load context-map.");
				}
			}
		}
	}
	
	public boolean isMapPublished() {
		return mapPublished;
	}
	
	public boolean isContainerPublished() {
		return containerPublished;
	}
	
}