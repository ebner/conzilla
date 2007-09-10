/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util.wizard;

import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JComponent;

/**
 * @author matthias
 */
public interface WizardComponent {
    String IS_READY = "is ready";
    
    boolean hasFinish();
    boolean isReady();
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
    void init(Map passedAlong);
    String getText();
    String getHelpText();
    JComponent getComponent();
    
    void enter();
    void previous();
    void cancel();
    boolean test();
    void next();
    void finish();
}
