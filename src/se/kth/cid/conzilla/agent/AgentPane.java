/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.agent;

import javax.swing.JComponent;

import se.kth.nada.kmr.shame.applications.util.MetaDataPanel;
import se.kth.nada.kmr.shame.formlet.FormletStore;

/**
 * An SHAMEditor for information on the agent.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class AgentPane extends MetaDataPanel{
    //Preload neccessary formlets in the FormletStore.
    static {
        FormletStore.requireFormletConfigurations("formlets/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/foaf/formlets.rdf");
    }
    
    static String agentFormletId =  
        "http://kmr.nada.kth.se/shame/foaf/formlet#all";

    public AgentPane(JComponent parent) {
        super("Conzilla profile editor", agentFormletId, parent.getBackground(), null);
    }
    
}