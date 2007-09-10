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

package se.kth.cid.conzilla.content;
import se.kth.cid.content.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import java.net.*;
import java.awt.Container;


public class MapContentDisplayer extends AbstractContentDisplayer
{
  Container container;
  Object     constraints;

  MapDisplayer displayer;

  ComponentLoader loader;

  public MapContentDisplayer(Container container, Object constraints,
			     ComponentLoader loader)
  {
    this.container = container;
    this.constraints = constraints;
    this.loader = loader;
  }

  public void setContent(ContentDescription cd) throws ContentException
  {
    if(cd == null)
      {
	removeDisplayer();
	super.setContent(null);
	return;
      }
    
    if(!cd.getContentType().toString().equals(ConceptMap.MIME_TYPE))
      {
	throw new ContentException("Cannot display MIMEType "
				   + cd.getContentType(), cd);
      }
    try {
      Tracer.debug("MapContent will show " + cd.getContentURI());
      
      Component comp;
      
      comp = loader.loadComponent(cd.getContentURI(), loader);
      
      if(! (comp instanceof ConceptMap))
	{
	  loader.releaseComponent(comp);
	  throw new ContentException("Content was no conceptmap!", cd);
	}

      
      ConceptMap map = (ConceptMap) comp;

      removeDisplayer();
      
      displayer = new MapDisplayer(map,cd);      
      container.add(displayer, constraints);
      displayer.revalidate();
      container.repaint();

      super.setContent(cd);
    } catch(ComponentException e)
      {
	throw new ContentException("Could not show content:\n "
				   + e.getMessage(), cd);
      }
  }  

  void removeDisplayer()
  {
    if(displayer != null)
      {
	container.remove(displayer);
	loader.releaseComponent(displayer.getMap());
	displayer = null;
      }
  }
}
