/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package se.kth.cid.conzilla.edit.layers.handles;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.util.*;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public class AxonHandlesStruct extends LineHandlesStruct
{
    public AxonStyle axonStyle;

    /** The constructor loads and initializes DefaultHandles from the AxonMapObject, 
     *  no reload is possible, throw away and recreate.
     */
    public AxonHandlesStruct(AxonStyle as)
    {
	this.axonStyle=as;
	ConceptMap.Position points[]=axonStyle.getLine();
	if (points==null)
	    Tracer.bug("A handledObject for a non visible axon is created,"+
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
	ConceptMap.Position points[]=new ConceptMap.Position[handles.size()];
	for (int i=0;en.hasMoreElements();i++)
	    {
		points[i]=((DefaultHandle) en.nextElement()).getPosition();
	    }
	axonStyle.setLine(points);	
    }

    public Collection getDraggers(boolean withEnds)
    {
	return getDraggers(axonStyle.getPathType(),withEnds);
    }
}
