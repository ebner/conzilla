/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.tree.generic;
import java.net.URI;
import java.util.List;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.EditListener;
import se.kth.cid.component.FiringResourceImpl;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.tree.TreeTagNodeResource;
import se.kth.cid.util.TagManager;

/** 
 *
 *  @author  Matthias Palmer
 *  @version $Revision$
 */
public class MemTreeTagNodeComponent
    extends MemTreeTagNode
    implements TreeTagNodeResource {

    FiringResourceImpl comp;

    public MemTreeTagNodeComponent(
        String id,
        Object tag,
        TagManager tagManager) {
        super(id, tag, tagManager);
        URI t = URI.create(id);
        comp = new FiringResourceImpl(t, t, MIMEType.MEM);
    }

    public String getLoadURI() {
        return getURI();
    }
    public boolean isEditable() {
        return true;
    }
    public boolean isEdited() {
        return false;
    }
    public void setEdited(boolean b) throws ReadOnlyException {
    }
    public void addEditListener(EditListener l) {
        comp.addEditListener(l);
    }
    public void removeEditListener(EditListener l) {
        comp.removeEditListener(l);
    }
    public ContainerManager getContainerManager() {
        return null;
    }
    
    //FIXME everything below is automatically generated.... check needed.

    /**
     * @see se.kth.cid.component.Component#getLoadContainer()
     */
    public String getLoadContainer() {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#getType()
     */
    public String getType() {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#getAttributeEntry(String)
     */
    public List getAttributeEntry(String attribute) {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#addAttributeEntry(java.lang.String, java.lang.Object)
     */
    public AttributeEntry addAttributeEntry(String attribute, Object value) {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#getAttributeEntry(java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String)
     */
    public AttributeEntry getAttributeEntry(String attribute, String value, Boolean isValueAURI, String containerURI) {
        return null;
    }
    
    /**
     * @see se.kth.cid.component.Component#removeAttributeEntry(se.kth.cid.component.AttributeEntry)
     */
    public void removeAttributeEntry(AttributeEntry ae) {
    }

    /**
     * @see se.kth.cid.component.Component#remove()
     */
    public void remove() {
    }

    public void removeFromContainer(Container cont) {
    }
    
    /**
     * @see se.kth.cid.component.Component#refreshRelevantContainers()
     */
    public void refreshRelevantContainers() {
    }

	public ComponentManager getComponentManager() {
		return null;
	}
    
}