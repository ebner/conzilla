/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import se.kth.cid.conzilla.properties.Images;

/**
 * @author matthias
 */
public class Wizard extends JDialog implements PropertyChangeListener{
    public static abstract class WrapperAction extends AbstractAction {
        Action action;
        
        public synchronized void addPropertyChangeListener(
                PropertyChangeListener listener) {
            super.addPropertyChangeListener(listener);
            action.addPropertyChangeListener(listener);
        }
        public synchronized void removePropertyChangeListener(
                PropertyChangeListener listener) {
            super.removePropertyChangeListener(listener);
            action.removePropertyChangeListener(listener);
        }
        public WrapperAction(String name, Action action) {
            super(name);
            this.action = action;
        }        
    }
    
    private boolean finishedSuccessfully;
    protected List wizardComponents;
    protected int position;
    protected Map data;
    protected Dimension preferredSizeWCs;
    protected Dimension preferredSizeWCTexts;
    protected JPanel wizardComponentTextPanel;
    protected JPanel wizardComponentPanel;
    AbstractAction next, finish;
    JButton nextButton, finishButton;
    Icon prevIcon, nextIcon;

    protected JPanel buttonBox;
    
    protected Wizard() {
        setModal(true);
    }

    public Wizard(List wizardCs) {
        this();
        initWizardComponents(wizardCs);
    }

    public void initWizardComponents(List wizardCs) {
        this.wizardComponents = wizardCs;
        data = new HashMap();
        initWizardComponents();
        initWizard();
    }
    
    protected void initWizardComponents() {
        int width = 0;
        int height = 0;
        int twidth = 0;
        int theight = 0;
        for (Iterator wcs = wizardComponents.iterator(); wcs.hasNext();) {
            WizardComponent wc = (WizardComponent) wcs.next();
            wc.init(data);
            wc.addPropertyChangeListener(this);
            Dimension di = wc.getComponent().getPreferredSize();
            if (di.width > width) {
                width = di.width;
            }
            if (di.height > height) {
                height = di.height;
            }
            di = new JLabel(wc.getText()).getPreferredSize();
            if (di.width > twidth) {
                twidth = di.width;
            }
            if (di.height > theight) {
                theight = di.height;
            }
        }
        preferredSizeWCs = new Dimension(width+4, height+6);
        preferredSizeWCTexts = new Dimension(twidth+4, theight+6);
    }

    protected void initWizard() {
        prevIcon = Images.getImageIcon(Images.ICON_NAVIGATION_BACK);
        nextIcon = Images.getImageIcon(Images.ICON_NAVIGATION_FORWARD);
        JPanel vertical = new JPanel();
        vertical.setLayout(new BoxLayout(vertical, BoxLayout.Y_AXIS));
        wizardComponentTextPanel = new JPanel();
        wizardComponentTextPanel.setLayout(new BorderLayout());
        wizardComponentPanel = new JPanel();

        wizardComponentPanel.setLayout(new BorderLayout());
        buttonBox = new JPanel();
        buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.X_AXIS));
        buttonBox.setOpaque(true);

        vertical.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        wizardComponentTextPanel.setBorder(BorderFactory.createEtchedBorder());
        wizardComponentPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); //BorderFactory.createEtchedBorder()
        //buttonBox.setBorder(BorderFactory.createEtchedBorder());
       
        vertical.add(wizardComponentTextPanel);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(wizardComponentPanel);
        vertical.add(Box.createVerticalStrut(5));
        vertical.add(buttonBox);
        setContentPane(vertical);
    }
    
    protected void initialize() {
        wizardComponentPanel.setPreferredSize(preferredSizeWCs);
        wizardComponentTextPanel.setPreferredSize(preferredSizeWCTexts);
        Dimension d = wizardComponentTextPanel.getPreferredSize();
        wizardComponentTextPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, d.height));
        changeToWizard(0).enter();
        Dimension prefSize = buttonBox.getPreferredSize();
        buttonBox.setPreferredSize(new Dimension(450, prefSize.height));
        pack();
    }
    
    protected WizardComponent changeToWizard(int position) {
        this.position = position;
        WizardComponent wc = (WizardComponent) wizardComponents.get(position);
        wizardComponentTextPanel.removeAll();
        wizardComponentPanel.removeAll();
        JLabel label = new JLabel(wc.getText());
        wizardComponentTextPanel.add(label, BorderLayout.WEST);
        wizardComponentPanel.add(wc.getComponent(), BorderLayout.CENTER);
        initButtons(position);
        validate();
        getContentPane().repaint();
        return wc;
    }
    
    protected WizardComponent getCurrentWizard() {
        return (WizardComponent) wizardComponents.get(position);
    }

    protected void initButtons(int position) {
        final WizardComponent wc = (WizardComponent) wizardComponents.get(position);
        buttonBox.removeAll();
        
        JLabel nr = new JLabel(""+(position+1)+"/"+wizardComponents.size());
        nr.setBackground(Color.green);
        nr.setOpaque(true);
        nr.setBorder(BorderFactory.createEmptyBorder(2,4,2,4));
        buttonBox.add(nr);
        buttonBox.add(Box.createHorizontalStrut(10));
        
        final String helpText = wc.getHelpText();
        JButton helpButton = new JButton(new AbstractAction("Help") {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(Wizard.this, helpText);
            }
        });
        helpButton.setMnemonic(KeyEvent.VK_H);
        helpButton.setEnabled(helpText != null);
        buttonBox.add(helpButton);

        buttonBox.add(Box.createHorizontalGlue());
        
        JButton cancel = new JButton(new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent ae) {
                cancel();
            }
        });
        cancel.setMnemonic(KeyEvent.VK_C);
        buttonBox.add(cancel);
        buttonBox.add(Box.createHorizontalStrut(10));
        
        JButton previous = new JButton(new AbstractAction("Previous") {
            public void actionPerformed(ActionEvent ae) {
                previous();
            }
        });
        previous.setMnemonic(KeyEvent.VK_P);
        previous.setIcon(prevIcon);
        previous.setEnabled(position != 0);
        buttonBox.add(previous);

        next = new AbstractAction("Next") {
            public void actionPerformed(ActionEvent ae) {
                next();                        
            }
        };
        nextButton = new JButton(next);
        nextButton.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    nextButton.doClick();
                }
            }
        });
        nextButton.setIcon(nextIcon);
        nextButton.setMnemonic(KeyEvent.VK_N);
        next.setEnabled((position < wizardComponents.size()-1) 
                && wc.isReady());
        buttonBox.add(nextButton);

        buttonBox.add(Box.createHorizontalStrut(10));
        finish = new AbstractAction("Finish") {
            public void actionPerformed(ActionEvent ae) {
                finish();
            }
        };
        finishButton = new JButton(finish);
        finishButton.setMnemonic(KeyEvent.VK_F);
        finish.setEnabled((position == wizardComponents.size() -1 
                || wc.hasFinish()) 
                && wc.isReady());
        buttonBox.add(finishButton);
        buttonFocus();
    }
    
    /* (non-Javadoc)
     * @see java.awt.Component#show()
     */
    public void showWizard() {
        finishedSuccessfully = false;
        initialize();
        super.setVisible(true);
        buttonFocus();
    }
    
    public boolean wizardFinishedSuccessfully() {
        return finishedSuccessfully;
    }
    
    protected void buttonFocus() {
        if (position != wizardComponents.size() -1) {
            getRootPane().setDefaultButton(nextButton);
            nextButton.requestFocusInWindow();
        } else {
            getRootPane().setDefaultButton(finishButton);
            finishButton.requestFocusInWindow();
        }
    }

    protected void previous() {
        getCurrentWizard().previous();
        changeToWizard(position-1).enter();
    }

    protected void next() {
        WizardComponent wc = getCurrentWizard();
        if (wc.test()) {
            wc.next();
            changeToWizard(position+1).enter();
        }
    }
    
    protected void cancel() {
        for (int i = wizardComponents.size() -1; i>= 0;i--) {
            WizardComponent wc = (WizardComponent) wizardComponents.get(i);
            wc.cancel();
        }
        setVisible(false);
    }

    protected void finish() {
        WizardComponent wc = (WizardComponent) wizardComponents.get(position);
        
        //If we are not at the end, loop through all
        //of the remaining wizardcomponents via invoking next on them.
        //Stop before the last one.
        int count = position;
        while (count < wizardComponents.size()-1) {
            if (!wc.test()) {
                return;
            }
            wc.next();
            count++;
            wc = (WizardComponent) wizardComponents.get(count);
            wc.enter();            
        }

        //Invoke finish (not next) on the last one.
        if (!wc.test()) {
            return;
        }
        wc.finish();
        
        //So, since all tests where successful we are finished
        //and can close this wizard.
        finishedSuccessfully = true;
        setVisible(false);
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(WizardComponent.IS_READY)) {
            boolean isReady = evt.getNewValue().equals(Boolean.TRUE);
            WizardComponent wc = getCurrentWizard();
            finish.setEnabled((position == wizardComponents.size() -1
                    || wc.hasFinish()) && isReady);                
            next.setEnabled((position != wizardComponents.size() -1) 
                    && isReady);               
        }
    }
}
