/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 * @author matthias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WizardComponentAdapter implements WizardComponent {

    private String text;
    private String helpText;
    protected Map passedAlong;
    private JComponent component;
    protected PropertyChangeSupport pcs;
    boolean isReady = true;
    
    public WizardComponentAdapter(String text, String helpText) {
        this.text = text;
        this.helpText = helpText;
        this.pcs = new PropertyChangeSupport(this);
    }
    
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#hasFinish()
     */
    public boolean hasFinish() {
        return false;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#init(java.util.Map)
     */
    public void init(Map passedAlong) {
        this.passedAlong = passedAlong;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#getText()
     */
    public String getText() {
        return text;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#getHelpText()
     */
    public String getHelpText() {
        return helpText;
    }

    protected void setComponent(JComponent component) {
        this.component = component;
    }
    
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#getComponent()
     */
    public JComponent getComponent() {
        if (component == null) {
            this.component = constructComponent();
        }
        return component;
    }
    
    protected JComponent constructComponent() {
        return new JTextArea("Error, You have not specified a component for this" +
                "WizardComponent!!!!");
    }
    
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#enter()
     */
    public void enter() {
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#previous()
     */
    public void previous() {
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#cancel()
     */
    public void cancel() {
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#test()
     */
    public boolean test() {
        return true;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#next()
     */
    public void next() {
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#finish()
     */
    public void finish() {
        next();
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    protected void setReady(boolean ready) {
        if (isReady != ready) {
            isReady = ready;
            if (ready) {
                pcs.firePropertyChange(WizardComponent.IS_READY, Boolean.FALSE, Boolean.TRUE);
            } else {
                pcs.firePropertyChange(WizardComponent.IS_READY, Boolean.TRUE, Boolean.FALSE);
            }
        }
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#isReady()
     */
    public boolean isReady() {
        // TODO Auto-generated method stub
        return isReady;
    }
}
