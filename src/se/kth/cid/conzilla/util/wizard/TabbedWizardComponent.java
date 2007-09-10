/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author matthias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TabbedWizardComponent extends WizardComponentAdapter implements PropertyChangeListener{
    
    JTabbedPane tabbs;
    Vector wizardComponents;
    WizardComponent wizardComponent;
    
    public TabbedWizardComponent(String message, String helpText) {
        super(message, helpText);
        wizardComponents = new Vector();
    }
    
    /**
     * Should be called before {@link #constructComponent()} is called, 
     * hence before this WizardComponent is added to a Wizard.
     * 
     * @param wc the WizardComponent that should be added as a tab, 
     * will be added after any previously added tabs.
     */
    public void addTab(WizardComponent wc) {
        wizardComponents.add(wc);
    }
    
    protected JComponent constructComponent() {
        tabbs = new JTabbedPane();
        tabbs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateTabSelection();
            }
        });
        for (Iterator wcs = wizardComponents.iterator(); wcs.hasNext();) {
            WizardComponent wc = (WizardComponent) wcs.next();
            tabbs.addTab(wc.getText(), wc.getComponent());
        }
        tabbs.setSelectedIndex(0);
        return tabbs;
    }
    
    protected void updateTabSelection() {
        WizardComponent oldWC = wizardComponent;
        Object o = tabbs.getSelectedComponent();
        for (Iterator wcs = wizardComponents.iterator(); wcs.hasNext();) {
            WizardComponent wc = (WizardComponent) wcs.next();
            if (o == wc.getComponent()) {
                wizardComponent = wc;
                break;
            }
        }
        
        if (oldWC != null) {
            oldWC.removePropertyChangeListener(this);
        }
        wizardComponent.addPropertyChangeListener(this);
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#enter()
     */
    public void enter() {
        wizardComponent.enter();
    }
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#finish()
     */
    public void finish() {
        wizardComponent.finish();
    }
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#hasFinish()
     */
    public boolean hasFinish() {
        return wizardComponent.hasFinish();
    }
    
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#init(java.util.Map)
     */
    public void init(Map passedAlong) {
        for (Iterator wcs = wizardComponents.iterator(); wcs.hasNext();) {
            WizardComponent wc = (WizardComponent) wcs.next();
            wc.init(passedAlong);
        }

        super.init(passedAlong);
    }
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#isReady()
     */
    public boolean isReady() {
        return wizardComponent.isReady();
    }
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#next()
     */
    public void next() {
        wizardComponent.next();
    }

    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#previous()
     */
    public void previous() {
        wizardComponent.previous();
    }
    /* (non-Javadoc)
     * @see se.kth.cid.conzilla.util.wizard.WizardComponent#test()
     */
    public boolean test() {
        return wizardComponent.test();
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (wizardComponent != null) {
            setReady(wizardComponent.isReady());
        }
    }
}
