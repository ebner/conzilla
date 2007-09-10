/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.wizard.newsession;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.util.wizard.Wizard;

/**
 * @author matthias
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SessionWizard extends Wizard {
    SessionManager sessionManager;

    public SessionWizard(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        ArrayList list = new ArrayList();
        list.add(new SpecifySessionName());
        list.add(new SpecifyNameSpace());
        list.add(new SpecifyContainer.SpecifyInformationContainer());
        list.add(new SpecifyContainer.SpecifyPresentationContainer());
        initWizardComponents(list);
    }

    public Session getNewSession() {
        fixLocalCopy();

        Session session = sessionManager.createAndAddSession(null);
        session.setTitle((String) data.get(SpecifySessionName.SESSION_NAME));
        session.setBaseURIForConcepts((String) data
                .get(SpecifyNameSpace.INFO_NAMESPACE));
        session.setBaseURIForLayouts((String) data
                .get(SpecifyNameSpace.PRES_NAMESPACE));
        session.setContainerURIForConcepts((String) data
                .get(SpecifyContainer.INFO_CONTAINER_URI));
        session.setContainerURIForLayouts((String) data
                .get(SpecifyContainer.PRES_CONTAINER_URI));

        return session;
    }

    /**
     *  Makes sure that the local directory is created.
     *  <ul><li>The directory is created.</li>
     *  <li>If the container exists already, 
     *  it is copied to the directory and its loadContainerURI is changed.</li></ul>
     */
    private void fixLocalCopy() {
    	fixLocalCopy((String) data.get(SpecifyContainer.INFO_CONTAINER_URI));
    	fixLocalCopy((String) data.get(SpecifyContainer.PRES_CONTAINER_URI));
    }
    
    private void fixLocalCopy(String suri) {
    	ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
    	try {
    		URI uri = new URI(suri);
			File file = store.resolveURIToFile(uri);
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdir();
			}
			
			try {
				Container container = store.getAndReferenceContainer(uri);
				container.setLoadURI(file.toURI());
				//Make a copy in the newly created directory, otherwise we will always load the
				//remote copy if we fail to exit gracefully (save the container locally).
				container.setEdited(true);
				store.getComponentManager().saveResource(container);
			} catch (ComponentException e) {
			}
    	} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }    
}