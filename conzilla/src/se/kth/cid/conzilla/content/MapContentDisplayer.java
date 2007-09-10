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
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import javax.swing.*;
import java.net.*;
import java.awt.Container;


public class MapContentDisplayer extends AbstractContentDisplayer
{
  Container  container;
  Object     constraints;
  
  MapScrollPane scrollPane;
  
  public MapContentDisplayer(Container container, Object constraints,
			     ComponentStore store)
    {
      super(store);
      this.container = container;
      this.constraints = constraints;
    }
  
  public void setContent(Component c) throws ContentException
    {
      if(c == null)
	{
	  removeMap();
	  super.setContent(null);
	  return;
	}
      
      if(!(c instanceof ConceptMap))
	{
	  throw new ContentException("Cannot display component "
				     + c.getURI(), c);
	}

      Tracer.debug("MapContent will show " + c.getURI());
      
      try {
	
	MapStoreManager manager = new MapStoreManager(URIClassifier.parseURI(c.getURI()),
						      store);
	
	removeMap();
	
	scrollPane = new MapScrollPane(new MapDisplayer(manager));
	
      } catch (ComponentException e)
	{
	  throw new ContentException("Could not load map " + c.getURI()
				     + ": " + e.getMessage(), c);
	}
      catch (MalformedURIException e)
	{
	  throw new ContentException("Invalid URI " + c.getURI()
				     + ": " + e.getMessage(), c);
	}
      
      container.add(scrollPane, constraints);
      scrollPane.revalidate();
      container.repaint();
      
      super.setContent(c);
    }  
  
  void removeMap()
    {
      if(scrollPane != null)
	{
	  scrollPane.getDisplayer().getStoreManager().detach();
	  scrollPane.getDisplayer().detach();
	  scrollPane.detach();
	  
	  container.remove(scrollPane);
	  scrollPane = null;
	}
    }
}
