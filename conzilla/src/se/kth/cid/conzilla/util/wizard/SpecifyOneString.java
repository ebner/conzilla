/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * @author matthias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpecifyOneString extends WizardComponentAdapter {

    String stringKey;
    JTextField textField;
    Timer timer;
    String initialString;

    
    public SpecifyOneString(String message, String helpText, String initialString, String key) {
      super(message, helpText);
      this.initialString = initialString;
      this.stringKey = key;
      setReady(validString(initialString));
    }
    
    protected boolean validString(String string) {
    	return string != null && string.length() > 2;
    }
    
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponentAdapter#constructComponent()
     */
    protected JComponent constructComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        textField = new JTextField();
        textField.setText(initialString);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                textField.getPreferredSize().height));
        panel.add(textField, BorderLayout.NORTH);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));    
        textField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {
                timer.restart();
            }
        });

        timer = new Timer(200, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setReady(validString(textField.getText()));
                timer.stop();
            }
        });

        
        return panel;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#next()
     */
    public void next() {
        passedAlong.put(stringKey, textField.getText());
    }
}
