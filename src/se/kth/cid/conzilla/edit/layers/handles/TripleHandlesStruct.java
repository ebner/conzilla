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
import se.kth.cid.util.Tracer;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public class TripleHandlesStruct extends LineHandlesStruct
{
    public StatementLayout tripleLayout;

    /** The constructor loads and initializes DefaultHandles from the TripleMapObject, 
     *  no reload is possible, throw away and recreate.
     */
    public TripleHandlesStruct(StatementLayout as)
    {
	this.tripleLayout=as;
	ContextMap.Position points[]=tripleLayout.getLine();
	if (points==null)
	    Tracer.bug("A handledObject for a non visible triple is created,"+
		       "consequently no handles can be displayed!!!!!!!!"+
		       "A HandledObject should ALWAYS be the effect of a selection via a mouseevent.");

	loadHandles(points, as.getPathType());
    }
    
    /** Sets the current positions. 
     *  Before this function is called, the invoker should lock the structure.
     */
    public void set()
    {
	if (handles.isEmpty())
	    return;
	Enumeration en=handles.elements();
	ContextMap.Position points[]=new ContextMap.Position[handles.size()];
	for (int i=0;en.hasMoreElements();i++)
	    {
		points[i]=((DefaultHandle) en.nextElement()).getPosition();
	    }
	tripleLayout.setLine(points);	
    }

    public Collection getDraggers(boolean withEnds)
    {
	return getDraggers(tripleLayout.getPathType(),withEnds);
    }
}
