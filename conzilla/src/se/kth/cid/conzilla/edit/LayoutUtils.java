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


package se.kth.cid.conzilla.edit;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.util.*;

/** 
 *
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class LayoutUtils 
{
    
    /** Calculates the best initial axonline between the neuronStyle containing the 
     *  axon and the NeuronStyle to attach to.
     *
     *  @param axonOwner is the NeuronStyle were the axon belongs.
     *  @param otherEnd is the NeuronStyle in the end of the axon.
     */
  public static ConceptMap.Position[] axonLine(NeuronStyle axonOwner, NeuronStyle otherEnd, ConceptMap.Position press, GridModel gm) 
    {
	ConceptMap.Position [] pos=new ConceptMap.Position[2];
	pos[0] = findPosition_FirstFromAxons(axonOwner, press, gm);
	if (pos[0] == null)
	    Tracer.bug("Trying to add axon to non visible NeuronStyle, despite the fact it is choosen by a mapEvent!!!!! inconsistant!!!!");
	pos[1] = findPosition_FirstFromBody(otherEnd, pos[0], gm);
	
	return pos;
    }

    /** Calculates the best initial boxline for a NeuronStyle.
     *  The NeuronStyle has to be visible somehow.
     *
     *  @param owner is the NeuronStyle were the axon belongs.
     *  @param mapEvent is the event that sets the boxline visibel (has to be over the Owner.
     */
  public static ConceptMap.Position[] boxLine(NeuronStyle owner, MapEvent mapEvent, GridModel gm) 
    {
	if (owner.getPathType()==AxonStyle.PATH_TYPE_STRAIGHT)
	    {
		ConceptMap.Position [] pos=new ConceptMap.Position[2];
		ConceptMap.Position press = getPosition(mapEvent);
		pos[0] = findPosition_FromAxons(owner);
		if (pos[0] == null)
		    pos[0] = getPosition(mapEvent);
		pos[1] = findPosition_FirstFromBody(owner, pos[0], gm);
		if (pos[0] == null)
		    pos[0] = getPosition(mapEvent);
		return pos;
	    }
	else
	    {
		ConceptMap.Position [] pos=new ConceptMap.Position[4];
		ConceptMap.Position press = getPosition(mapEvent);
		pos[0] = findPosition_FromAxons(owner);
		if (pos[0] == null)
		    pos[0] = getPosition(mapEvent);
		pos[3] = findPosition_FirstFromBody(owner, pos[0], gm);
		if (pos[0] == null)
		    pos[0] = getPosition(mapEvent);

		pos[1] = new ConceptMap.Position((int) (pos[0].x +((pos[3].x-pos[0].x)*0.3)), 
						 (int) (pos[0].y +((pos[3].y-pos[0].y)*0.3)));
		pos[2] = new ConceptMap.Position((int) (pos[0].x +((pos[3].x-pos[0].x)*0.7)), 
						 (int) (pos[0].y +((pos[3].y-pos[0].y)*0.7)));
		return pos;
		
	    }
    }

    /** Use this function when you want a box on the grid.
     *
     *  @param gridModel  the current gridmodel.
     *  @param prefX  the preferred x-position for the upper left corner of the box.
     *  @param prefY  the preferred y-position for the upper left corner of the box.
     *  @param prefDim the preferred dimension, the box will have at least this size.
     */
  public static ConceptMap.BoundingBox preferredBoxOnGrid(GridModel gridModel, int prefX, int prefY, Dimension prefDim)
    {
	ConceptMap.BoundingBox box = new ConceptMap.BoundingBox(prefX,
								prefY,
								prefDim.width+5,
								prefDim.height+5);
	onGridMinimumSize(box, gridModel);
	return box;
    }
	
  public static ConceptMap.Position onGrid(ConceptMap.Position pos, GridModel gridmodel)
    {
	
	pos.x+=(int) gridmodel.getGranularity()/2.0;
	pos.y+=(int) gridmodel.getGranularity()/2.0;
	pos.x-=pos.x%gridmodel.getGranularity();
	pos.y-=pos.y%gridmodel.getGranularity();
	return pos;
    }
  public static ConceptMap.Dimension onGrid(ConceptMap.Dimension dim, GridModel gridmodel)
    {
	dim.width+=(int) gridmodel.getGranularity()/2.0;
	dim.height+=(int) gridmodel.getGranularity()/2.0;
	dim.width-=dim.width%gridmodel.getGranularity();
	dim.height-=dim.height%gridmodel.getGranularity();
	return dim;
    }
  public static ConceptMap.BoundingBox onGrid(ConceptMap.BoundingBox bb, GridModel gridmodel)
    {
	onGrid(bb.pos, gridmodel);
	onGrid(bb.dim, gridmodel);
	return bb;
    }
  public static ConceptMap.BoundingBox onGridMinimumSize(ConceptMap.BoundingBox bb, GridModel gridmodel)
    {
	onGrid(bb.pos, gridmodel);
	bb.dim.width+=(int) gridmodel.getGranularity()/2.0;
	bb.dim.height+=(int) gridmodel.getGranularity()/2.0;
	onGrid(bb.dim, gridmodel);
	return bb;
    }
  protected static ConceptMap.Position getPosition(MapEvent mapEvent)
    {
	return new ConceptMap.Position(mapEvent.mapX, mapEvent.mapY);
    }

    /** Calculates the middle of the axonsStyles inner end.
     *
     *  @returns a position, may be null if the NeuronStyle doesn't have any AxonStyles. 
     */
  public static ConceptMap.Position findPosition_FromAxons(NeuronStyle axonOwner) 
    {
	ConceptMap.Position pos=new ConceptMap.Position(0,0);
	AxonStyle [] as=axonOwner.getAxonStyles();
	if (as.length != 0)
	    {
		for (int i=0;i<as.length;i++)
		    {
			pos.x+=as[i].getLine()[0].x;
			pos.y+=as[i].getLine()[0].y;
		    }
		pos.x=(int) ((double) pos.x)/as.length;
		pos.y=(int) ((double) pos.y)/as.length;
	    }
	else 
	    return null;
	return pos;
    }
    
    /** Calcualtes the best line-end-point on the NeuronStyle, 
     *  i.e. chooses first from axon-middle then box and last it's boxline. 
     *
     * @see findPosition_FromBody
     * @see findPosition_FromAxons
     */
  public static ConceptMap.Position findPosition_FirstFromAxons(NeuronStyle axonOwner, ConceptMap.Position press, GridModel gm) 
    {
	ConceptMap.Position pos;
	pos = findPosition_FromAxons(axonOwner);
	if (pos!=null && gm!=null && gm.isGridOn())
	    {
		int gran=gm.getGranularity();
		pos.x=(pos.x+gran/2)/gran*gran;
		pos.y=(pos.y+gran/2)/gran*gran;
	    }
		
	if (pos == null && axonOwner.getBodyVisible())
	    {
		pos=new ConceptMap.Position(0,0);
		if (axonOwner.getLine()!=null)
		    {
			pos.x=axonOwner.getLine()[0].x;
			pos.y=axonOwner.getLine()[0].y;
		    }
		else
		    pos=findPosition_FromBody(axonOwner, press, gm);
	    }
	return pos;
    }
	    
    /** Calculates the best line-end-point on the NeuronStyle, 
     *  i.e. chooses first from box, then boxline and last axons-middle.
     *
     * @see findPosition_FromBody
     * @see findPosition_FromAxons
     */
  public static ConceptMap.Position findPosition_FirstFromBody(NeuronStyle bodyOwner, ConceptMap.Position press, GridModel gm)
    {
	ConceptMap.Position pos=findPosition_FromBody(bodyOwner,press, gm);
	if (pos == null)
	    pos = findPosition_FromAxons(bodyOwner);
	return pos;
    }

    /** Calculates the best point on the body given the other end of the line, 
     *  i.e first box and then boxline.
     * 
     *  @returns a position, null if the box isn't visible.
     */
  protected static ConceptMap.Position findPosition_FromBody(NeuronStyle boxOwner, ConceptMap.Position press, GridModel gm)
    {
	int gran=1;
	int halfgran=0;
	if (gm!=null && gm.isGridOn())
	    {
		gran=gm.getGranularity();
		halfgran=gran/2;
	    }

	ConceptMap.Position pos=new ConceptMap.Position(0,0);
	ConceptMap.BoundingBox bb=boxOwner.getBoundingBox();
	if (boxOwner.getBodyVisible())
	    {
		if (press.x < bb.pos.x || 
		    press.x > bb.pos.x + bb.dim.width)
		    {
			pos.x=bb.pos.x + (press.x < bb.pos.x ? 0 : bb.dim.width); 
			
			if (press.y < bb.pos.y)
			    pos.y=bb.pos.y;
			else if (press.y > bb.pos.y + bb.dim.height)
			    pos.y=bb.pos.y+bb.dim.height;
			else 
			    pos.y=(press.y+halfgran)/gran*gran;
		    }
		else 
		    {
			if (press.y < bb.pos.y)
			    {
				pos.x=(press.x+halfgran)/gran*gran;
				pos.y=bb.pos.y;
			    }
			else if (press.y > bb.pos.y + bb.dim.height)
			    {
				pos.x=(press.x+halfgran)/gran*gran;
				pos.y=bb.pos.y+bb.dim.height;
			    }
			else 
			    {
				pos.x=bb.pos.x;
				pos.y=bb.pos.y;
			    }	    
		    }
	    }
	else
	    pos = null;
	return pos;
    }

    public static ConceptMap.Position findMiddleOfNeuronStyles_RegardingBody(NeuronStyle ns, 
					 Collection finished_locations, GridModel gm)
    {
	int nr = 0;
	AxonStyle [] ass = ns.getAxonStyles();
	int x=0, y=0;
	for (int i=0; i<ass.length;i++)
	    {
		if (!finished_locations.contains(ass[i].getObject()))
		    continue;
		ConceptMap.BoundingBox bb = ass[i].getObject().getBoundingBox();
		x+=bb.pos.x+bb.dim.width/2;
		y+=bb.pos.y+bb.dim.height/2;
		nr++;
	    }
	ConceptMap.Position point1 = new ConceptMap.Position((int) (x/nr),(int) (y/nr));
	ConceptMap.Position point2 = findPosition_FromBody(ns, point1, gm);
	return point2==null ? point1 : point2;
    }
}

