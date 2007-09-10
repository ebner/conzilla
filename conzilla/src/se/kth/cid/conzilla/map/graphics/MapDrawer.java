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

package se.kth.cid.conzilla.map.graphics;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public abstract class MapDrawer
{
  MapDisplayer displayer;
  
  public MapDrawer(MapDisplayer displayer)
    {
      this.displayer = displayer;
    }

  public Mark getMark(Mark mark)
    {
	if(getErrorState())
	    return new Mark(ColorManager.MAP_NEURON_ERROR, null, null);
	else
	    return mark;
    }    

  public void coloredPaint(Graphics g, NeuronMapObject nmo)
    {
      Mark myMark;
      if (displayer.getGlobalMarkColor()==null)
	  {
	      if (nmo.isDefaultMark())
		  myMark = getMark(nmo.getMark());
	      else
		  myMark=nmo.getMark();
	  }
      else
	  myMark=new Mark(displayer.getGlobalMarkColor(), null, null);

      Color saveColor = g.getColor();

      g.setColor(myMark.foregroundColor);
      doPaint((Graphics2D) g);
      g.setColor(saveColor);
    }

  void doPaint(Graphics2D g)
    {
    }

  public abstract boolean getErrorState();
}
