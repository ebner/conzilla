/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.awt.Container;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Resource;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.layout.ContextMap;

public class MapContentDisplayer extends AbstractContentDisplayer {
	
	Log log = LogFactory.getLog(MapContentDisplayer.class);
	
    Container container;

    Object constraints;

    MapScrollPane scrollPane;

    public MapContentDisplayer(Container container, Object constraints) {
        this.container = container;
        this.constraints = constraints;
    }

    //FIXME: Doesn't handle a contentdescription pointing to another uri
    //than itself!!
    public void setContent(Resource c) throws ContentException {
        if (c == null) {
            removeMap();
            super.setContent(null);
            return;
        }

        if (!(c instanceof ContextMap)) {
            throw new ContentException(
                    "Cannot display component " + c.getURI(), c);
        }

        log.debug("MapContent will show " + c.getURI());

        try {
            ConzillaKit kit = ConzillaKit.getDefaultKit();
            MapStoreManager manager = new MapStoreManager(URI.create(c.getURI()), kit.getResourceStore(), kit.getStyleManager(),
                    null);

            removeMap();

            scrollPane = new MapScrollPane(new MapDisplayer(manager));

        } catch (ComponentException e) {
            throw new ContentException("Could not load map " + c.getURI()
                    + ": " + e.getMessage(), c);
        }

        container.add(scrollPane, constraints);
        scrollPane.revalidate();
        container.repaint();

        super.setContent(c);
    }

    void removeMap() {
        if (scrollPane != null) {
            scrollPane.getDisplayer().getStoreManager().detach();
            scrollPane.getDisplayer().detach();
            scrollPane.detach();

            container.remove(scrollPane);
            scrollPane = null;
        }
    }
}