/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import se.kth.cid.conzilla.properties.Images;

/**
 * A simple splash screen showing that something is happening. 
 * 
 * @author Hannes Ebner
 */
public class StartupProgressSplash extends JFrame {
	
    private JPanel imagePanel;
    
    private JPanel mainPanel;
    
    private JProgressBar progressBar;
    
    private JLabel titleLabel;
    
    private JLabel statusLabel;
	
	public StartupProgressSplash() {
		initComponents();
		setUndecorated(true);
		setBackground(Color.WHITE);
		setTitle("Conzilla " + Conzilla.CURRENT_VERSION);
		pack();
	}
	
	public void showSplash() {
		centerScreen();
		requestFocus();
		setVisible(true);
		mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		toFront();
	}
	
	private void centerScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		setLocation(x, y);
	}
	
    private void initComponents() {
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        titleLabel = new JLabel();
        statusLabel = new JLabel();
        mainPanel = new JPanel();
        
        imagePanel = new JPanel() {
        	public void paintComponent(Graphics g) {
        		g.drawImage(Images.getImageIcon(Images.ICON_CONZILLA_64).getImage(), 0, 0, null);
        	}
        };
        
        mainPanel.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		StartupProgressSplash.this.dispose();
        	}
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));

        titleLabel.setText("Conzilla " + Conzilla.CURRENT_VERSION);
        titleLabel.setFont(new Font(null, Font.BOLD, 18));
        statusLabel.setFont(new Font(null, Font.PLAIN, 10));

        imagePanel.setPreferredSize(new java.awt.Dimension(64, 64));
        org.jdesktop.layout.GroupLayout imagePanelLayout = new org.jdesktop.layout.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 64, Short.MAX_VALUE)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 64, Short.MAX_VALUE)
        );

        statusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        statusLabel.setText("statusLabel");

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(imagePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(statusLabel)
                            .add(titleLabel)))
                    .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(imagePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                        .add(titleLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(statusLabel)
                        .add(10, 10, 10)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(24, 24, 24))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
        );
    }
    
    public void setPercentage(final int percent) {
    	if (percent < 0 && percent > 100) {
    		throw new IllegalArgumentException("Value must be between 0 and 100.");
    	}
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		    	progressBar.setIndeterminate(false);
		    	progressBar.setMinimum(0);
		    	progressBar.setMaximum(100);
		    	progressBar.setValue(percent);
			}
    	});
    	toFront();
    }
    
    public void setStatusText(final String text) {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusLabel.setText(text);
			}
    	});
    	toFront();
    }

}