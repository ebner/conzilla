/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.util.Collection;
import java.util.Date;

public interface RemoteComponentFactory extends ComponentFactory {

    void publish();
    
    void unpublish();

    void setRevision(int revision);

    int getRevisionCount(String uri);

    Collection getOriginalContainers(String uri, int revision); // returns URIs

    Collection getRelevantContainers(String uri, int revision); // returns URIs

    String getContextInfo(String uri, int revision); // returns RDF-info

    Date getDateCreated(String uri, int revision);

    Date getDateModified(String uri, int revision);

}