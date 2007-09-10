/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.wizard.newsession;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.util.wizard.WizardComponentAdapter;

/**
 * @author matthias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpecifyNameSpace extends WizardComponentAdapter {
    public static final String NAMESPACE="namespace";
    public static final String INFO_NAMESPACE="info namespace";
    public static final String PRES_NAMESPACE="pres namespace";

    JPanel morePanel;
    JPanel morePanelContainer;
    JButton detailsButton;
    JTextField nameSpaceField;
    JTextField infoNSField;
    JTextField presNSField;
    Timer timer;
    boolean detailsIsVisible = false;
	private String namespace;
    
    public SpecifyNameSpace() {
        super("<html><body>Specify the namespace to use in this session, <br>" +
                "e.g. http://www.example.com/people/smith/<br>" +
                "observe that it should preferrably be <br>" +
                "a namespace over which you have control.</body></html>",
                "Here be dragons!");
        setReady(false);
    }
    
    protected JComponent constructComponent() {
        JPanel component = new JPanel();
        component.setLayout(new BoxLayout(component, BoxLayout.Y_AXIS));
        JPanel horiz = new JPanel();
        horiz.setLayout(new BoxLayout(horiz, BoxLayout.X_AXIS));
        component.add(horiz);
        
        nameSpaceField = new JTextField();
        nameSpaceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameSpaceField.getPreferredSize().height));
        nameSpaceField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {
                timer.restart();
            }
        });

        timer = new Timer(200, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                update();
                timer.stop();
            }
        });
        
        horiz.add(nameSpaceField);
        detailsButton = new JButton(new AbstractAction("More Details") {
            public void actionPerformed(ActionEvent e) {
                toggleDetails();
            }
        });
        horiz.add(detailsButton);
        
        //The detailed panel, not visible initially.
        morePanel = new JPanel();

        infoNSField = new JTextField();
        presNSField = new JTextField();
        infoNSField.setMaximumSize(new Dimension(Integer.MAX_VALUE, infoNSField.getPreferredSize().height));
        presNSField.setMaximumSize(new Dimension(Integer.MAX_VALUE, presNSField.getPreferredSize().height));

        
        GridBagLayout gl = new GridBagLayout();
        morePanel.setLayout(gl);
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        
        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = GridBagConstraints.RELATIVE;
        gc.weightx = 0.0;
        morePanel.add(new JLabel("Information Namespace:"), gc);
        
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        morePanel.add(infoNSField, gc);

        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = GridBagConstraints.RELATIVE;
        gc.weightx = 0.0;
        morePanel.add(new JLabel("Presentation Namespace:"), gc);
        
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        morePanel.add(presNSField, gc);

        
        morePanelContainer = new JPanel();
        morePanelContainer.setLayout(new BorderLayout());
        component.add(morePanelContainer); 
        
        return component;
    }
    
    private void toggleDetails() {
        if (detailsIsVisible) {
            detailsIsVisible = false;
            morePanelContainer.removeAll();
            detailsButton.setText("More Details");
            
            getComponent().revalidate();
            getComponent().repaint();
        } else {
            detailsIsVisible = true;
            morePanelContainer.add(morePanel, BorderLayout.CENTER);
            detailsButton.setText("Less Details");
            
            getComponent().revalidate();
            getComponent().repaint();
        }
    }
    
    
    public void enter() {
		super.enter();
		String ns = ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_USER_NAMESPACE);
		if (ns != null) {
			nameSpaceField.setText(ns);
			update();
		}
	}

	private void update() {
        String text = nameSpaceField.getText();
        //String name = (String) passedAlong.get(SpecifySessionName.SESSION_NAME);
        //name = name.replace(' ', '_');
        try {
            new URL(text);
            if (!detailsIsVisible) {
                if (!text.endsWith("/")) {
                    namespace = text+"/";
                }
                infoNSField.setText(namespace+"concept");
                presNSField.setText(namespace+"layout");
            }
            nameSpaceField.setForeground(Color.BLACK);
            setReady(true);
            return;
        } catch (MalformedURLException e) {
            nameSpaceField.setForeground(Color.RED);
            setReady(false);
        }
    }
    
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#hasFinish()
     */
    public boolean hasFinish() {
        return true;
    }
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#next()
     */
    public void next() {
		String ns = ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_USER_NAMESPACE);
		if (ns == null || ns.length() == 0) {
			ConfigurationManager.getConfiguration().setProperty(Settings.CONZILLA_USER_NAMESPACE, nameSpaceField.getText());
		}
        passedAlong.put(NAMESPACE, namespace);
        passedAlong.put(INFO_NAMESPACE, infoNSField.getText());
        passedAlong.put(PRES_NAMESPACE, presNSField.getText());
    }
}
