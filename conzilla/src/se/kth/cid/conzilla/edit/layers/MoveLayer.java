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


package se.kth.cid.conzilla.edit.layers;
import se.kth.cid.conzilla.edit.layers.handles.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import java.awt.event.*;


public class MoveLayer extends Layer
{
  LineTool linetool;
  
  public MoveLayer(MapController controller, MapManager manager, GridTool grid, LineTool linetool)
  {
    super(controller, manager, grid);
    this.linetool=linetool;
  }
	       
  protected boolean focus(MapEvent m)
  {
    switch (m.hit)
      {
      case MapEvent.HIT_NONE:
	setHandledObject(null,m);
	return true;
      case MapEvent.HIT_BOX:
      case MapEvent.HIT_TITLE:
      case MapEvent.HIT_DATA:
	if ((mapevent.neuronstyle!=m.neuronstyle || mapevent.hit!=m.hit)
	    && m.neuronstyle.isEditable())
	  {
	    setHandledObject(new HandledBox(m),m);
	    return true;
	  }
	return false;
      case MapEvent.HIT_NEURONLINE:
	if ((mapevent.neuronstyle!=m.neuronstyle || mapevent.hit!=m.hit)
	    && m.neuronstyle.isEditable())
	  {
	    setHandledObject(new HandledNeuronLine(m,linetool),m);
	    return true;
	  }
	return false;
      case MapEvent.HIT_ROLELINE:
	if (mapevent.rolestyle!=m.rolestyle && m.rolestyle.getRoleOwner().isEditable())
	  {
	    setHandledObject(new HandledLine(m,linetool),m);
	    return true;
	  }
	return false;
      }
    return false;
  }  
}
