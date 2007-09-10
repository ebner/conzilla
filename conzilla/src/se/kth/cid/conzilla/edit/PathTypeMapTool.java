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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import javax.swing.JOptionPane;
import java.awt.event.*;

/** Tool for switching between straight and curved lines.
 *
 *  @author Matthias Palmer.
 */
public class PathTypeMapTool extends ActionMapMenuTool
{

    static final String CURVE_LINE = "CURVE_LINE";
    static final String STRAIGHTEN_LINE = "STRAIGHTEN_LINE";

    String currentName;

  public PathTypeMapTool(MapController cont)
  {
    super(CURVE_LINE, EditMapManagerFactory.class.getName(), cont);
    currentName = CURVE_LINE;
  }
    
  protected boolean updateEnabled()
    {
      if (mapEvent.hitType!=mapEvent.HIT_AXONLINE ||
	  mapEvent.hitType!=mapEvent.HIT_AXONDATA ||
	  mapEvent.hitType!=mapEvent.HIT_BOXLINE)
	  {
	      int type;
	      if (mapEvent.hitType==mapEvent.HIT_BOXLINE)
		  type = mapObject.getNeuronStyle().getPathType();
	      else
		  type = mapObject.getAxonStyle().getPathType();
	      if (type == AxonStyle.PATH_TYPE_STRAIGHT)
		  currentName = CURVE_LINE;
	      else
		  currentName = STRAIGHTEN_LINE;

	      ConzillaResourceManager.getDefaultManager().customizeButton(getJMenuItem(), EditMapManagerFactory.class.getName(), currentName);

	      return true;
	  }
      return false;
    }

  public void actionPerformed(ActionEvent e)
    {
	if (mapEvent.hitType==mapEvent.HIT_BOXLINE)
	    {
		NeuronStyle ns = mapObject.getNeuronStyle();
		if (ns.getPathType() == AxonStyle.PATH_TYPE_STRAIGHT)
		    {
			ns.setPathType(AxonStyle.PATH_TYPE_CURVE);
			ns.setLine(makeCurveLine(ns.getLine()));
		    }
		else
		    {
			ns.setPathType(AxonStyle.PATH_TYPE_STRAIGHT);
			ns.setLine(makeStraightLine(ns.getLine()));
		    }
	    }
	else
	    {
		AxonStyle as = mapObject.getAxonStyle();
		if (as.getPathType() == AxonStyle.PATH_TYPE_STRAIGHT)
		    {
			as.setPathType(AxonStyle.PATH_TYPE_CURVE);
			as.setLine(makeCurveLine(as.getLine()));
		    }
		else
		    {
			as.setPathType(AxonStyle.PATH_TYPE_STRAIGHT);
			as.setLine(makeStraightLine(as.getLine()));
		    }
	    }
    }

    protected ConceptMap.Position [] makeStraightLine(ConceptMap.Position [] line)
    {
	ConceptMap.Position [] nl = new ConceptMap.Position[((line.length-1)/3) +1];
	for (int i = 0; i<nl.length;i++)
	    nl[i]=line[i*3];
	return nl;
    }

    protected ConceptMap.Position [] makeCurveLine(ConceptMap.Position [] line)
    {
	ConceptMap.Position [] nl = new ConceptMap.Position[((line.length-1)*3)+1];
	nl[0]=line[0];
	for (int i = 1; i<line.length;i++)
	    {
		nl[i*3]=line[i];
		nl[i*3-1]=new ConceptMap.Position((int) (line[i-1].x + ((line[i].x-line[i-1].x)*0.7)),
						  (int) (line[i-1].y + ((line[i].y-line[i-1].y)*0.7)));
		nl[i*3-2]=new ConceptMap.Position((int) (line[i-1].x + ((line[i].x-line[i-1].x)*0.3)),
						  (int) (line[i-1].y + ((line[i].y-line[i-1].y)*0.3)));
	    }
	return nl;
    }
}

