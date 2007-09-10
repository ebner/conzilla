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


package se.kth.cid.conzilla.map;

import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.center.*;
import java.awt.event.*;

public class MapEvent 
{
  public final static int    HIT_NONE       = 0;
  public final static int    HIT_BOX        = 1;
  public final static int    HIT_TITLE      = 2;
  public final static int    HIT_DATA       = 3;
  public final static int    HIT_NEURONLINE = 4;
  public final static int    HIT_ROLELINE   = 5;

  public MouseEvent mouseevent;
  public int hit;
  public MapDisplayer mapdisplayer;
  public NeuronStyle neuronstyle;
  public RoleStyle rolestyle;
  public AppObject paintobject;
  public int linesegmenthit;

  public boolean editabilityChecked=false;
  public boolean neuronEditable=false;
  public boolean mapEditable=false;
  
  public MapEvent(MouseEvent mouseevent, MapDisplayer mapdisplayer)
    {
      this(mouseevent,0,null,null);
      this.mapdisplayer=mapdisplayer;
    }
  public MapEvent(MouseEvent mouseevent,int hit,NeuronStyle neuronstyle,
		  RoleStyle rolestyle)
  {
    this.mouseevent=mouseevent;
    this.hit=hit;
    this.neuronstyle=neuronstyle;
    this.rolestyle=rolestyle;
    this.mapdisplayer=null;
    linesegmenthit=-1;
  }
  public void consume()
  {
    mouseevent.consume();
  }
  public boolean isConsumed()
  {
    return mouseevent.isConsumed();
  }
  public Object getHitObject()
  {
    if (neuronstyle!=null)
      return neuronstyle;
    if (rolestyle!=null)
      return rolestyle;
    return null;
  }
  public NeuronStyle getNeuronStyle()
  {
    if (neuronstyle!=null)
      return neuronstyle;
    if (rolestyle!=null)
	return rolestyle.getRoleOwner();
    return null;
  }

  public void checkEditability(ConzKit kit)
    {
      mapEditable=mapdisplayer.getMap().isEditingPossible() && kit.saver!=null
	&& kit.saver.isComponentSavable(mapdisplayer.getMap());
      NeuronStyle ns=getNeuronStyle();
      if (ns!=null)
	{
	  Neuron ne=ns.getNeuron();
	  if (ne!=null)
	    neuronEditable=ne.isEditingPossible() && kit.saver!=null
	      && kit.saver.isComponentSavable(ne);
	}
      editabilityChecked=true;
    }
}
