/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.agent;

import java.io.IOException;

import javax.swing.JComponent;

import se.kth.cid.util.Tracer;
import se.kth.nada.kmr.shame.applications.util.FormletStoreSingleton;
import se.kth.nada.kmr.shame.applications.util.MetaDataPanel;

/**
 * An SHAMEditor for information on the agent.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class AgentPane extends MetaDataPanel{
    //Preload neccessary formlets in the FormletStore.
    static {
        try {
			FormletStoreSingleton.requireFormletConfigurations("formlets/formlets.rdf");
			FormletStoreSingleton.requireFormletConfigurations("formlets/foaf/formlets.rdf");
		} catch (IOException e) {
			Tracer.debug(e.getMessage());
		}
    }
    
    static String agentFormletId =  
        "http://kmr.nada.kth.se/shame/foaf/formlet#all";

    public AgentPane(JComponent parent) {
        super("Conzilla profile editor", agentFormletId, parent.getBackground(), null);
    }
    
}