/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.net.URI;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.conzilla.app.ConzillaKit;

import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author matthias
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResourceUtil {
	
	static Log log = LogFactory.getLog(ResourceUtil.class);

    public static Container findContainer(Component comp) {
        try {
            return ConzillaKit
                .getDefaultKit()
                .getResourceStore()
                .getAndReferenceContainer(URI.create(comp.getLoadContainer()));
        } catch (ComponentException e) {
            e.printStackTrace();
            log.error("Couldn't find loadcontainer for contextmap", e);
            return null;
        }
    }

    public static ContainerManager findContainerManager(Component comp) {
        return findContainer(comp).getContainerManager();
    }

    public static boolean isResourceOfClassProperty(Component comp) {
        Iterator types =
            comp.getAttributeEntry(RDF.type.toString()).iterator();
        while (types.hasNext()) {
            AttributeEntry ae = (AttributeEntry) types.next();
            if (ae.getValue().equals(RDF.Property.toString())) {
                return true;
            }
        }
        return false;
    }

}