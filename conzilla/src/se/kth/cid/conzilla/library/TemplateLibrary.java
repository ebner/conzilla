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
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import java.awt.*;


public class TemplateLibrary extends ContentLibraryAdapter
{

  public static TemplateLibrary getDefault(MapController controller)
    {
      String neuronuri="cid:local/template/ne/template";
      String cduri="cid:local/template/cd/templatemap";
      try {
	return new TemplateLibrary(controller,new URI(neuronuri),
					       new URI(cduri));
      } catch (Exception e) 
	{
	  Tracer.trace("Couldn't create default TemplateLibrary."+e.getMessage(),
		       Tracer.MAJOR_INT_EVENT);
	  return null;
	}
    }
  public TemplateLibrary(MapController controller,
			  URI libraryneuron, URI librarycontentdescription)
    {
      super(controller, libraryneuron, librarycontentdescription);
      //      presentation=new MapDisplayer(conceptmap,null);
    }
  public TemplateLibrary(NeuronStyle ns)
    {
      super(ns);
    }

  protected void fixApperance(NeuronStyle ns) throws ReadOnlyException
    {
      ns.setBoundingBox(new Rectangle(5,5,70,20));
      ns.setTitle("Templates");
    }
  
  public java.util.Enumeration getTemplates()
    {
      return getNeuronStyles();
    }
}
