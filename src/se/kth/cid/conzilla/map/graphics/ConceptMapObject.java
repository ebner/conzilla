/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.MapObjectImpl;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.layout.StatementLayout;

public class ConceptMapObject extends DrawerMapObject {
    Hashtable mapObjects;
    DrawerLayout mapGroupLayout;

    public ConceptMapObject(DrawerLayout drawLayout, MapDisplayer displayer) {
        super(drawLayout, displayer);

        //	if (!(drawLayout instanceof GroupLayout))
        // 	    Tracer.bug("Trying to create a GroupMapObject around something that isn't a GroupLayout");
        mapGroupLayout = drawLayout;
        mapObjects = new Hashtable();

        Enumeration en = mapGroupLayout.getChildren().elements();

        while (en.hasMoreElements()) {
            ResourceLayout os = (ResourceLayout) en.nextElement();
            if (mapGroupLayout.getChildHidden(os.getURI()))
                continue;

            MapObject mo = createMapObject(os);

            if (mo != null)
                mapObjects.put(os.getURI(), mo);
        }
    }

    public MapObject createMapObject(ResourceLayout layout) {
        if (layout instanceof StatementLayout)
            return new TripleMapObject((StatementLayout) layout, displayer);
        else if (layout instanceof ConceptLayout)
            return new ConceptMapObject((ConceptLayout) layout, displayer);
        return null;
    }

    /////////// Update support ///////////  

    void addResourceLayout(String id) {
        ResourceLayout os = (ResourceLayout) mapGroupLayout.getChild(id);
        MapObject mo = createMapObject(os);

        if (mo != null)
            mapObjects.put(id, createMapObject(os));
    }

    void removeResourceLayout(String id) {
        MapObject mo = (MapObject) mapObjects.remove(id);

        if (mo != null)
            mo.detach();
    }

    public void componentEdited(EditEvent e) {
        super.componentEdited(e);

        switch (e.getEditType()) {
            case DrawerLayout.DRAWERLAYOUT_ADDED :
                addResourceLayout((String) e.getTarget());
                break;
            case DrawerLayout.DRAWERLAYOUT_REMOVED :
                removeResourceLayout((String) e.getTarget());
                break;
        }
    }
    /*
        case ConceptLayout.DATATAG_ADDED:
        case ConceptLayout.DATATAG_REMOVED:
        case Concept.ATTRIBUTES_EDITED:
        case ConceptType.DATATAG_ADDED:
        case ConceptType.DATATAG_REMOVED:
        case ConceptType.BOXTYPE_EDITED:
        case ConceptLayout.BOUNDINGBOX_EDITED:
        case ConceptLayout.BODYVISIBLE_EDITED:
        case ConceptLayout.TEXT_ANCHOR_EDITED:
        case se.kth.cid.component.Component.METADATA_EDITED:
        case ConceptLayout.LINE_EDITED:
        case ConceptType.LINETYPE_EDITED:
    */

    /////////// Editing/painting methods //////////////

    public void paint(Graphics g) {
        Iterator childs = mapObjects.values().iterator();
        while (childs.hasNext()) {
            MapObject mo = (MapObject) childs.next();
            if (mo.getVisible())
                mo.paint(g);
        }
        super.paint(g);
        //Right order?? should box paint over boxline and children like now?
    }

    public boolean checkAndFillHit(MapEvent m) {
        if (super.checkAndFillHit(m))
            return true;

        Iterator childs = mapObjects.values().iterator();
        while (childs.hasNext()) {
            MapObject mo = (MapObject) childs.next();
            if (mo.checkAndFillHit(m)) {
                m.parentMapObject = this;
                return true;
            }
        }
        return false;
    }
    protected Collection getBoundingBoxesImpl() {
        Vector boundingboxes = new Vector();

        Iterator childs = mapObjects.values().iterator();
        while (childs.hasNext()) {
            boundingboxes.addAll(
                ((MapObjectImpl) childs.next()).getBoundingboxes());
        }
        return boundingboxes;
    }

    ///////////// Detaching ///////////////

    public void detachImpl() {
        super.detachImpl();
        Iterator triples = mapObjects.values().iterator();

        while (triples.hasNext())
             ((TripleMapObject) triples.next()).detach();
        mapObjects = null;
    }
}
