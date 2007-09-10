/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.wizard.newmap;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import se.kth.cid.component.ContainerManager;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.util.wizard.WizardComponentAdapter;

/**
 * @author matthias
 *
 */
public class SpecifyMapID extends WizardComponentAdapter {
    public static final String MAP_ID = "map id";
    
    JTextField mapId;
    JTextField mapURI;
    String namespace;
    Timer timer;
    ContainerManager containerManager;
    
    public SpecifyMapID(ContainerManager containerManager) {
        super("<html>Give the map a globally unique identifier.<br>" +
                "(Choose a localname for yourself or<br>" +
                "accept the uniquely generated.)</html>", "Some help");
        this.containerManager = containerManager;
    }
    
    protected JComponent constructComponent() {
        JPanel panel = new JPanel();
        mapId = new JTextField();
        mapURI = new JTextField();
        mapURI.setEnabled(false);

        GridBagLayout gl = new GridBagLayout();
        panel.setLayout(gl);
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        
        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = GridBagConstraints.RELATIVE;
        panel.add(new JLabel("Map id:"), gc);
        
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        panel.add(mapId, gc);
        
        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = GridBagConstraints.RELATIVE;
        gc.weightx = 0.0;
        panel.add(new JLabel("Map URI:"), gc);
                
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        panel.add(mapURI, gc);
        
        timer = new Timer(200, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        mapId.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {
                timer.restart();
            }
        });
        
        
        return panel;
    }
    
    protected void update() {
        mapURI.setText(namespace+mapId.getText());
        try {
            new URI(mapURI.getText());
            setReady(mapId.getText().length() != 0);
            mapId.setBackground(Color.WHITE);
        } catch (URISyntaxException e) {
            mapId.setBackground(Color.RED);
            setReady(false);
        }
    }
    
    protected void generate() {
        Session session = (Session) passedAlong.get(ChooseSession.CHOOSEN_SESSION);
        namespace = session.getBaseURIForLayouts();
        String base =  namespace + "CM";
        String uri = containerManager.createUniqueURI(base);
        mapURI.setText(uri);
        mapId.setText("CM"+uri.substring(base.length()));
        setReady(true);
    }
    
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#enter()
     */
    public void enter() {
        generate();
    }
    
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#next()
     */
    public void next() {
        update();
        passedAlong.put(MAP_ID, URI.create(mapURI.getText()));
    }
}
