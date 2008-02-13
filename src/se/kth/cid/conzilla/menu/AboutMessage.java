/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.kth.cid.conzilla.app.Conzilla;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.properties.Images;

/**
 * @author matthias
 */
public class AboutMessage extends JDialog {

    private JPanel buttonsPanel;
    private JButton closeButton;
    private JLabel creditsLabel;
    private JLabel aboutLabel;
    private JPanel contentPane;
    
    public AboutMessage() {
        initLayout();
        switchToAbout();
        setSize(new Dimension(350, 570));
        setVisible(true);
        setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
        setTitle("About Conzilla");
        //pack();
    }

    private void initLayout() {
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
        setContentPane(contentPane);
        ImageIcon logo = Images.getImageIcon(Images.IMAGE_LOGO_ABOUT);
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setOpaque(false);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        contentPane.add(logoLabel, BorderLayout.NORTH);
        buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,0,0,0,Color.BLACK),
                BorderFactory.createEmptyBorder(5,10,5,10)));
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        closeButton = new JButton(new AbstractAction("Close") {
            public void actionPerformed(ActionEvent e) {
                AboutMessage.this.dispose();
            }
        });
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);
        
        creditsLabel = new JLabel("<html><body>The following people are acknowledged for their " +
                "contributions to Conzilla:<br>" +
                "<table><tr><td width=\"115\">Ambj&ouml;rn&nbsp;Naeve</td><td>Inventor, Architect, Designer,<br>Tester, Documentation Writer</td></tr>"+
                "<tr><td>Matthias&nbsp;Palm&eacute;r</td><td>Main developer,<br>Technical Coordinator</td></tr>"+
                "<tr><td>Hannes&nbsp;Ebner</td><td>Main Developer</td></tr>"+
                "<tr><td>Mikael&nbsp;Nilsson</td><td>Developer</td></tr>"+
                "<tr><td>Henrik&nbsp;Eriksson</td><td>Developer</td></tr>"+
                "<tr><td>Fredrik&nbsp;Enoksson</td><td>Developer</td></tr>"+
                "<tr><td>Noel&nbsp;Zargarian</td><td>Contributor</td></tr>"+
                "<tr><td>Richard&nbsp;Wessblad</td><td>Contributor</td></tr>"+
                "<tr><td>Ioana&nbsp;Predonescu</td><td>Contributor</td></tr>"+
                "<tr><td>Mark James</td><td>Icon set \"Silk\"</td></tr>"+
                "</table></body></html>");
        creditsLabel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        
        aboutLabel = new JLabel("<html><body><h2>Conzilla " + Conzilla.CURRENT_VERSION + "</h2>" +
                "&copy; 1999-2008 All rights reserved.<br>" +
                "<br>" +
                "Conzilla is licensed under the GNU General Public License (GPL). " +
                "See <a href=\"http://www.conzilla.org\">http://www.conzilla.org</a> for details.<br>" +
                "<br>" +
                "Conzilla is being developed by the Knowledge Management Research Group " + 
                "at KTH (Royal Institute of Technology), Stockholm, Sweden.<br>" + 
                "Website: <a href=\"http://kmr.nada.kth.se\">http://kmr.nada.kth.se</a><br><br>" +
                "The development of Conzilla is to a large extent driven by " +
                "research within Conceptual Modeling and user interface design " +
                "for the Semantic Web.<br><br>" +
                "</body><html>");
        aboutLabel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }
    
    private void switchToAbout() {
        //The message
        contentPane.remove(creditsLabel);
        contentPane.add(aboutLabel, BorderLayout.CENTER);
        contentPane.revalidate();
        
        //The buttons
        JButton creditsButton = new JButton(new AbstractAction("Credits") {
            public void actionPerformed(ActionEvent e) {
                switchToCredits();
            }
        });

        buttonsPanel.removeAll();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(creditsButton);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(closeButton);
        buttonsPanel.revalidate();
        
        contentPane.repaint();
    }
    
    private void switchToCredits() {
        //The message
        contentPane.remove(aboutLabel);         
        contentPane.add(creditsLabel, BorderLayout.CENTER);
        contentPane.revalidate();
        
        //The buttons
        JButton aboutButton = new JButton(new AbstractAction("< About Conzilla") {
            public void actionPerformed(ActionEvent e) {
                switchToAbout();
            }
        });
        
        buttonsPanel.removeAll();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(aboutButton);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(closeButton);
        buttonsPanel.revalidate();
        
        contentPane.repaint();
    }

}