/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.net.URI;
import java.util.Iterator;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author matthias
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResourceUtil {

    public static Container findContainer(Component comp) {
        try {
            return ConzillaKit
                .getDefaultKit()
                .getResourceStore()
                .getAndReferenceContainer(URI.create(comp.getLoadContainer()));
        } catch (ComponentException e1) {
            e1.printStackTrace();
            Tracer.bug("Couldn't find loadcontainer for contextmap!!!");
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
