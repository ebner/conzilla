/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JComponent;

import se.kth.nada.kmr.shame.form.FormContainer;
import se.kth.nada.kmr.shame.form.FormModel;
import se.kth.nada.kmr.shame.workflow.WorkFlowManager;

/**
 * Container for putting SHAME result forms in a Conzilla popup.
 *
 * @author   Henrik Eriksson
 * @version  $Revision$, $Date$
 */
public class ConzillaViewFormContainer implements FormContainer {
    protected JComponent container;
    
    public ConzillaViewFormContainer(JComponent container) {
        this.container = container;
    }
    
    /* 
     * @param form accepts only JComponents.
     * @see se.kth.nada.kmr.shame.form.FormContainer#create(java.awt.Container, se.kth.nada.kmr.shame.form.FormModel)
     */
    public void create(Container form, FormModel model, WorkFlowManager manager) {
        // XXX WorkFlowManager not used.
        this.container.add(form, BorderLayout.CENTER);
    }
}
