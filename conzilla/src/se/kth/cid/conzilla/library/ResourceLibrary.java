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


package se.kth.cid.conzilla.library;

import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import java.awt.*;
import javax.swing.*;


public class ResourceLibrary extends IndexLibraryAdapter implements MapEventListener
{
  public static ResourceLibrary getDefault(MapController controller)
    {
      String neuronuri="cid:local/library/ne/resources";
      String cduri="cid:local/library/cd/resources";
      try {
	ResourceLibrary rl=new ResourceLibrary(controller,new URI(neuronuri),
					       new URI(cduri));
      if (rl.loaded)
	{
	  if (rl.conceptmap.isEditingPossible())
	    rl.conceptmap.setEditable(true);	      
	  else
	    {
	      controller.getComponentLoader().releaseComponent(rl.conceptmap);
	      rl.conceptmap=new ConceptMap(controller.getComponentLoader());
	      rl.conceptmap.setBoundingBox(new Dimension(220,220));
	      rl.conceptmap.setBackgroundColor(Color.white.getRGB());
	    }
	}
      rl.loadDefaultLibrarys();
      return rl;
      } catch (Exception e) 
	{
	  Tracer.debug("Couldn't create default ResourceLibrary."+e.getMessage());
	  return null;
	}
    }
  
  public ResourceLibrary(MapController controller,
			 URI libraryneuron, URI librarycontentdescription)
    {
      super(controller, libraryneuron, librarycontentdescription);
      //      presentation=new MapDisplayer(conceptmap,null);
      //      displayer.addMapEventListener(this,MapDisplayer.CLICK);
    }
  
  protected void finalize()
    {
      conceptmap=null;
      //      ((MapDisplayer) presentation).detach();
    }

  public void loadDefaultLibrarys()
    {
      categorizeIndexMap();
      //      if (getLibrary("templatelibrary")==null)
      registerLibrary(TemplateLibrary.getDefault(controller), "templatelibrary", false);
      //      if (getLibrary("clipboardlibrary")==null)
      registerLibrary(ClipboardLibrary.getDefault(controller), "clipboardlibrary", true);
      //      registerLibrary(HistoryLibrary.getDefault(controller), "historylibrary");
      //      registerLibrary(CatalogLibrary.getDefault(controller), "cataloglibrary");
    }
  
  public void eventTriggered(MapEvent m) {}
}


