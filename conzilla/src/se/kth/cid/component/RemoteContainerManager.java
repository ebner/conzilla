/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.util.Collection;
import java.util.Date;

import se.kth.cid.layout.ContextMap;
import se.kth.nada.kmr.collaborilla.client.CollaborillaException;

public interface RemoteContainerManager extends ContainerManager {
 
    /**
     * Publishes everything of a container/session.
     * 
     * We get the necessary infos through these parameters:
     * 
     * contextMap.getURI()
     * contextMap.getRelevantContainers()
     * contextMap.getLoadContainer()
     * ContainerManager containerManager = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager()
     * 
     * @throws CollaborillaException
     */
    void publish(ContextMap contextMap, Collection concepts) throws CollaborillaException;
    
    void unpublish(ContextMap contextMap, Collection concepts) throws CollaborillaException;
    
    int getRevisionCount(String uri) throws CollaborillaException;
    
    Collection getLocations(String uri, int revision) throws CollaborillaException;

    Collection getOriginalContainers(String uri, int revision) throws CollaborillaException;

    Collection getRelevantContainers(String uri, int revision) throws CollaborillaException;
    
    String getContainerInfo(String uri, int revision) throws CollaborillaException; // returns RDF-info
    
    String getContextInfo(String uri, int revision) throws CollaborillaException; // returns RDF-info
    
    Date getDateCreated(String uri, int revision) throws CollaborillaException;
    
    Date getDateModified(String uri, int revision) throws CollaborillaException;

}