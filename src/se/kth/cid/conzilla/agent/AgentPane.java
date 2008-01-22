/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.agent;

import java.io.IOException;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.nada.kmr.shame.applications.util.FormletStoreSingleton;
import se.kth.nada.kmr.shame.applications.util.MetaDataPanel;

/**
 * An SHAMEditor for information on the agent.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class AgentPane extends MetaDataPanel {
	
	static Log log = LogFactory.getLog(AgentPane.class);
	
    //Preload neccessary formlets in the FormletStore.
    static {
        try {
			FormletStoreSingleton.requireFormletConfigurations("formlets/formlets.rdf");
			FormletStoreSingleton.requireFormletConfigurations("formlets/foaf/formlets.rdf");
		} catch (IOException e) {
			log.error(e);
		}
    }
    
    static String agentFormletId =  
        "http://kmr.nada.kth.se/shame/foaf/formlet#all";

    public AgentPane(JComponent parent) {
        super("Conzilla profile editor", agentFormletId, parent.getBackground(), null);
    }
    
}