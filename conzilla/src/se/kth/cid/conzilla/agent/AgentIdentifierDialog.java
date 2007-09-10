/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.agent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;

import se.kth.cid.identity.URIUtil;

/**
 * A dialog for determining a globally unique identifier for the user of Conzilla.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class AgentIdentifierDialog extends JDialog {
    
    JTextField agentURIField;
    JButton generate;
    JButton next;
    JButton load;
    JButton cancel;
    Timer timer;
    
    public AgentIdentifierDialog() {
        setModal(true);
        initLayout();
    }
    
    private void initLayout() {
        Box buttons = Box.createHorizontalBox();
        JTextArea message = new JTextArea("Before you can create anything in Conzilla you have to have a personal identifier.\n" +
            "It is important that this identifier is globally unique and do not change.\n" +
            "It is not as important exactly how it looks as long as it is constant over time.\n\n" +
            "Give a URI that will be the global identifier for you.");
        message.setEditable(false);
        agentURIField = new JTextField();
        agentURIField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent ke) {
                delayedCheckURI();
            }
        });
        
        agentURIField.setColumns(40);
        Box split = Box.createVerticalBox();
        split.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    //Workaround to get the same border on JTextArea as on JTextField.
    //Setting the border directly on the JTextArea fails to draw the border correct.
    //Hence I add an intermediate panel where I set the border instead.
        JPanel messagePane = new JPanel();
        messagePane.setLayout(new BorderLayout());
        messagePane.add(message, BorderLayout.CENTER);
        messagePane.setBorder(UIManager.getBorder("TextField.border"));

        split.add(messagePane);
        
//        Box URIPane = Box.createHorizontalBox();
        split.add(Box.createVerticalStrut(5));
        split.add(agentURIField);
        split.add(Box.createVerticalStrut(5));
        split.add(buttons);
        
        buttons.add(Box.createHorizontalGlue());

        //The load button
        AbstractAction aal = new AbstractAction("Load") {
            public void actionPerformed(ActionEvent ae) {
                load();
            }
        };
        load = new JButton(aal);
        load.setToolTipText("Load a an existing identity with information from disk");
        buttons.add(load);
        
        //The generate button
        AbstractAction aag = new AbstractAction("Generate") {
            public void actionPerformed(ActionEvent ae) {
                generate();
            }
        };
        generate = new JButton(aag);
        generate.setToolTipText("Generate a globally unique identity (URI).");
        buttons.add(generate);
        
        //The next button
        AbstractAction aao = new AbstractAction("Next") {
            public void actionPerformed(ActionEvent ae) {
                next();
            }
        };
        next = new JButton(aao);
        next.setToolTipText("Proceed to give information about yourself");
        next.setEnabled(false);
        buttons.add(next);

        //The cancel button
        AbstractAction aac = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent ae) {
                cancel();
            }
        };
        cancel = new JButton(aac);
        next.setToolTipText("Abort, you will probably be asked again later.");
        buttons.add(cancel);

        getContentPane().add(split);
        pack();
    }

    private void delayedCheckURI() {
        if (timer == null) {
            timer = new Timer(100, new AbstractAction() {
                public void actionPerformed(ActionEvent ae) {
                    checkURI();
                }
            });
            timer.setRepeats(false);
        }
        
        timer.stop();
        timer.restart();
    }
    
    private boolean checkURI() {
        String uri = agentURIField.getText();
        try {
            URI URI = new URI(uri);
            if (URI.isAbsolute()) {
                next.setEnabled(true);
                agentURIField.setForeground(Color.green);
                return true;
            }
        } catch (URISyntaxException e) {
        }
        next.setEnabled(false);
        agentURIField.setForeground(Color.red);
        return false;
    }
    
    public void load() {
        //TODO
    }
    
    public void generate() {
        String uri = URIUtil.createUniqueURIFromBase("http://www.conzilla.org/people/generated");
        agentURIField.setText(uri);
        checkURI();
    }
    
    public void next() {
        setVisible(false);
    }
    
    public void cancel() {
        agentURIField.setText("");
        setVisible(false);
    }
    
    public String getURI() {
        return agentURIField.getText();
    }
}
