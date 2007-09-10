/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

/**
 * @author ioana
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
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

public class IdentifierDialog extends JDialog {

    JTextField agentURIField;
    JButton generate;
    JButton next;
    JButton paste;
    JButton cancel;
    Timer timer;
    Box split;
    Box buttons;
    String baseURI;
    JTextArea message;
    JPanel messagePane;

    public IdentifierDialog() {
        setModal(true);
        initLayout();
    }

    private void initLayout() {
        buttons = Box.createHorizontalBox();         
        split = Box.createVerticalBox();
        
        message = new JTextArea();
        message.setEditable(false);
        
        split.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //Workaround to get the same border on JTextArea as on JTextField.
        //Setting the border directly on the JTextArea fails to draw the border correct.
        //Hence I add an intermediate panel where I set the border instead.
        messagePane = new JPanel();
        messagePane.setLayout(new BorderLayout());
        messagePane.add(message, BorderLayout.CENTER);
        messagePane.setBorder(UIManager.getBorder("TextField.border"));

        split.add(messagePane);

        split.add(Box.createVerticalStrut(5));
        agentURIField = new JTextField();
        agentURIField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent ke) {
                delayedCheckURI();
            }
        });
        agentURIField.setColumns(40);
        split.add(agentURIField);
        split.add(Box.createVerticalStrut(5));
        split.add(buttons);
        buttons.add(Box.createHorizontalGlue());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(split, BorderLayout.NORTH);
    }
    

    public void addPasteButton() {
        //The load button
        AbstractAction aal = new AbstractAction("Paste") {
            public void actionPerformed(ActionEvent ae) {
                paste();
            }
        };
        paste = new JButton(aal);
        paste.setToolTipText(
            "Paste identifier from clipboard as content");
        buttons.add(paste);
    }

    public void addLoadButton() {
        //The load button
        AbstractAction aal = new AbstractAction("Load") {
            public void actionPerformed(ActionEvent ae) {
                load();
            }
        };
        paste = new JButton(aal);
        paste.setToolTipText(
            "Load a an existing identity with information from disk");
        buttons.add(paste);
    }

    public void addGenerateURIButton(String baseuri) {
        this.baseURI = baseuri;
        //The generate button
        AbstractAction aag = new AbstractAction("Generate") {
            public void actionPerformed(ActionEvent ae) {
                generateURI();
            }
        };
        generate = new JButton(aag);
        generate.setToolTipText("Generate a globally unique identity (URI).");
        buttons.add(generate);
    }

    public void addNextButton() {
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
    }

    public void addCancelButton() {
        //The cancel button
        AbstractAction aac = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent ae) {
                cancel();
            }
        };
        cancel = new JButton(aac);
        next.setToolTipText("Abort, you will probably be asked again later.");
        buttons.add(cancel);

    }

    public void setTextMessage(String newmessage) {
        message.setText(newmessage);
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

    public void paste() {
        Transferable trans =
            Toolkit
                .getDefaultToolkit()
                .getSystemClipboard()
                .getContents(
                null);
        try {
            String uri =
                (String) trans.getTransferData(
                    DataFlavor.stringFlavor);
            if (uri != null) {
                agentURIField.setText(uri);
                checkURI();                
            }
        } catch (UnsupportedFlavorException ue) {} catch (IOException ie) {}
    }
    
    public void generateURI() {
        String uri =
            URIUtil.createUniqueURIFromBase(baseURI);
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
        String text = agentURIField.getText(); 
        if (text.length() == 0) {
            return null;        
        } else {
            return text;
        }
    }
}
