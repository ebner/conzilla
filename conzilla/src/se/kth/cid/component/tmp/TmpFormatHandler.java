/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.tmp;

import java.net.URI;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.FiringResourceImpl;
import se.kth.cid.component.FormatHandler;
import se.kth.cid.component.Resource;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.generic.MemConcept;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.layout.generic.MemConceptMap;
import se.kth.cid.tree.TreeTagNodeResource;

/** 
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class TmpFormatHandler implements FormatHandler {
    /** The "text/unknown" MIME type.
     */
    public final static MIMEType TMP = new MIMEType("text/unknown", true);

    /** Whether we should bother using the Netscape privilege manager.
     */
    public static boolean usePrivMan = false;

    /** Constructs an TmpFormatHandler.
     */
    public TmpFormatHandler() {}

    public MIMEType getMIMEType() {
        return TMP;
    }

    public void setComponentStore(ResourceStore store) {}

    public TreeTagNodeResource loadTree(URI uri, URI origuri)
        throws ComponentException {
        throw new ComponentException("Loading of temporary components not supported.");
    }

    public Container loadContainer(URI uri, URI origuri)
        throws ComponentException {
        throw new ComponentException("Loading of temporary components not supported.");
    }

    public Resource loadComponent(Container container, URI origuri)
        throws ComponentException {
        throw new ComponentException("Loading of temporary components not supported.");
    }

    public Resource loadComponent(URI uri, URI origuri)
        throws ComponentException {
        throw new ComponentException("Loading of temporary components not supported.");
    }

    public boolean isSavable(URI uri) {
        return false;
    }

    public void checkCreateComponent(URI uri) throws ComponentException {
        //ok!
    }

    public Resource createComponent(
        URI uri,
        URI realURI,
        int type,
        Object extras)
        throws ComponentException {
        //FIXME: MIMEType should be what? unknown?
        switch (type) {
            case CONCEPT :
                return new MemConcept(uri, realURI, TMP, (URI) extras);
            case CONCEPTMAP :
                return new MemConceptMap(uri, realURI, TMP);
            case COMPONENT :
                return new FiringResourceImpl(uri, realURI, TMP);
        }
        throw new ComponentException(
            "Cannot create a component of the type "
                + type
                + ", doesn't know how!");
    }

    public void saveComponent(URI uri, Resource comp)
        throws ComponentException {
        throw new ComponentException("Saving temporary components not implemented!");
    }

    /** Returns whether this formathandler can deal with a specified URI.
     */
    public boolean canHandleURI(URI uri) {
        return uri.getScheme().equals("tmp");
    }

    /**
     * @see se.kth.cid.component.FormatHandler#getContainerManager()
     */
    public ContainerManager getContainerManager() {
        return null;
    }
}
