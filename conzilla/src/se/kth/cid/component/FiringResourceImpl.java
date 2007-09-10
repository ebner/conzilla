/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Vector;

import se.kth.cid.identity.MIMEType;

/** An implementation of Resource to be used for components downloaded
 *  over the web.
 *  It is intended to be subclassed by the different component implementations.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class FiringResourceImpl implements FiringResource{
    FormatHandler formatHandler = null;

    /** The URI of this component.
     */
    URI componentURI;

    /** The URI used to load this component.
     */
    protected URI componentLoadURI;

    /** The MIME type used when loading this component.
     */
    protected MIMEType componentLoadMIMEType;

    /** The editListeners of this component.
     */
    protected Vector editListeners;

    /** Whether this component is editable.
     */
    boolean isEditable = true;

    /** The edited state of this component.
     */
    boolean isEdited = false;

    /** Constructs a GenericComponent
     */
    public FiringResourceImpl(URI uri, URI loadURI, MIMEType loadType) {
        editListeners = new Vector();
        componentURI = uri;
        componentLoadURI = loadURI;
        componentLoadMIMEType = loadType;
    }

    public String getURI() {
        return componentURI.toString();
    }

    public String getLoadURI() {
        return componentLoadURI.toString();
    }

    public String getLoadMIMEType() {
        return componentLoadMIMEType.toString();
    }

    /** Sets the editable state of this Resource. To be used with extreme care;
     *  this state is not expected to change. This function is intended to be
     *  used exclusively in the construction phase of a LocalComponent.
     *
     * @param editable the new editable state.
     */
    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean b) throws ReadOnlyException {
        //If edited state isn't changed, do nothing...
        if (isEdited == b)
            return;

        isEdited = b;
        if (isEdited)
            fireEditEventNoEdit(new EditEvent(this, this, EDITED, null));
        else
            fireEditEventNoEdit(new EditEvent(this, this, SAVED, null));
    }

    public void addEditListener(EditListener l) {
        editListeners.addElement(l);
    }

    public void removeEditListener(EditListener l) {
        editListeners.removeElement(l);
    }

    /** Fires an EditEvent to all listeners and marks the
     *  component as being edited.
     *
     *  @param e the event to fire.
     */
    public void fireEditEvent(EditEvent e) {
        isEdited = true;
        fireEditEventNoEdit(e);
    }

    /** Fires an EditEvent to all listeners without marking the component as being edited.
     *
     *  @param e the event to fire.
     */
    public void fireEditEventNoEdit(EditEvent e) {
        for (int i = 0; i < editListeners.size(); i++) {
            ((EditListener) editListeners.elementAt(i)).componentEdited(e);
        }
    }

    /** Tries to parse a URI using this Resource's URI as base URI.
     *
     *  @param uri the URI to parse.
     *  @exception InvalidURIException if the URI did not parse.
     */
    public URI tryURI(String uri) throws InvalidURIException {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new InvalidURIException(e.getMessage(), uri);
        }
    }

    /** Constructs a new ID.
     *
     *  The ID is unique with respect to the Strings in the given
     *  Collection (which must not be infinite).
     *
     *  @param uniques the existing ID to not use.
     *  @param uriBase an uri (or any string) to use as base.
     *                 anything after the last '/' will be used.
     *
     *  @return a new unique ID.
     */
    public static String createID(Collection uniques, String uriBase) {
        String idBase = "id";

        if (uriBase != null) {
            int lastSlash = uriBase.lastIndexOf('/');

            if (lastSlash + 1 < uriBase.length())
                idBase = uriBase.substring(lastSlash + 1);
        }

        if (!(uniques.contains(idBase)))
            return idBase;

        for (int i = 1; true; i++) {
            String s = idBase + i;
            if (!(uniques.contains(s)))
                return s;
        }
    }
}
