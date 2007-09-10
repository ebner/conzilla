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
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.edit.layers.*;
import java.awt.*;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */

public class NeuronBoxHandlesStruct
{
    public NeuronStyle neuronStyle;
    
    public BoxHandle ul;
    public BoxHandle ur;
    public BoxHandle lr;
    public BoxHandle ll;
    public BoxTotalHandle tot;
    
    public NeuronBoxHandlesStruct(NeuronStyle ns, GridModel gridModel)
    {
	this.neuronStyle=ns;
	ConceptMap.BoundingBox bb=neuronStyle.getBoundingBox();
	ul = new BoxHandle(bb.pos, gridModel);                                                                //upper left
	ur = new BoxHandle(new ConceptMap.Position(bb.pos.x+bb.dim.width,bb.pos.y), gridModel);               //upper right
	lr = new BoxHandle(new ConceptMap.Position(bb.pos.x+bb.dim.width,bb.pos.y+bb.dim.height), gridModel); //lower right
	ll = new BoxHandle(new ConceptMap.Position(bb.pos.x,bb.pos.y+bb.dim.height), gridModel);              //lower left
	tot = new BoxTotalHandle(ul, ur, lr, ll);
	ul.addNeighbours(ll,ur,lr,tot, true, true);
	ur.addNeighbours(lr,ul,ll,tot, true, false);
	ll.addNeighbours(ul,lr,ur,tot, false, true);
	lr.addNeighbours(ur,ll,ul,tot, false, false);
    }
    
    public void set()
    {
	ConceptMap.Position pos=ul.getPosition();
	ConceptMap.BoundingBox bb=new ConceptMap.BoundingBox(pos.x,pos.y,ur.rectangle.x-ul.rectangle.x,
							     ll.rectangle.y-ul.rectangle.y);
	neuronStyle.setBoundingBox(bb);  
    }    
    public void paint(Graphics2D g)
    {
	if (ul.getParent()!=null)
	    {
		ul.paint(g);
		ur.paint(g);
		lr.paint(g);
		ll.paint(g);
	    }
	if (tot.getParent()!=null)
	    tot.paint(g);
    }
}
