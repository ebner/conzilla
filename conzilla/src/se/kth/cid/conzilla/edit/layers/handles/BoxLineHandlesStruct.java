/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;

import java.util.Collection;
import java.util.Enumeration;

import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.StatementLayout;

/**
 * @author Matthias Palmer
 * @version $version: $
 */
public class BoxLineHandlesStruct extends LineHandlesStruct {
    public StatementLayout statementLayout;

    /**
     * The konstructor loads initializes DefaultHandles from the
     * TripleMapObject, no reload is possible, throw away and recreate.
     */
    public BoxLineHandlesStruct(StatementLayout ns) {
        this.statementLayout = ns;
        ContextMap.Position points[] = statementLayout.getBoxLine();
        /*
         * if (points==null) Tracer.bug("A handledObject for a non visible
         * concepttriple is created,"+ "consequently no handles can be
         * displayed!!!!!!!!"+ "A HandledObject should ALWAYS be the effect of a
         * selection via a mouseevent.");
         */
        if (points != null)
            loadHandles(points, ns.getBoxLinePathType());
    }

    /**
     * Sets the current positions. Before this function is called, the invoker
     * should lock the structure.
     */
    public void set() {
        if (handles.isEmpty())
            return;
        Enumeration en = handles.elements();
        ContextMap.Position points[] = new ContextMap.Position[handles.size()];
        for (int i = 0; en.hasMoreElements(); i++)
            points[i] = ((DefaultHandle) en.nextElement()).getPosition();
        statementLayout.setBoxLine(points);
    }

    public Collection getDraggers(boolean withEnds) {
        if (withEnds) {
            return getDraggers(statementLayout.getBoxLinePathType(), withEnds);   
        } else {
            //Ends are excluded because they are dragged by someone else,
            //But for boxlines, no ones draggs the middle end... so include it anyway.
            Collection col = getDraggers(statementLayout.getBoxLinePathType(), withEnds);
            col.add(getFirstHandle());
            return col;
        }
    }
}

