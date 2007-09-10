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

import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.tool.ToolSetBar;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

public abstract class IndexLibraryAdapter extends LibraryAdapter implements IndexLibrary
{
  Hashtable librarys;
  
  public IndexLibraryAdapter(MapController controller,
			 URI libraryneuron, URI librarycontentdescription)
    {
      super(controller, libraryneuron, librarycontentdescription);
      if (!loaded)
	{
	  conceptmap=new ConceptMap(controller.getComponentLoader());
	  conceptmap.setBoundingBox(new Dimension(450,450));
	  conceptmap.setBackgroundColor(Color.white.getRGB());
	}
      librarys=new Hashtable();
      //      categorizeIndexMap();
    }

  public IndexLibraryAdapter(NeuronStyle ns)
    {
      super(ns);
    }
  
  public boolean registerLibrary(IndexLibrary lib, String str, boolean dolayout)
    {
      Tracer.debug("registerLibrary!!!!!!!!!!!! " + str);
      if (lib==null)
	Tracer.debug("lib is null!!!");
      if (librarys.get(str)==null)
	if (lib.placeYourselfInMap(conceptmap)!=null)
	  {
	    librarys.put(str,lib);
	    if (dolayout)
	      fixLayout();
	    return true;
	  }
      return false;
    }
  public NeuronStyle placeYourselfInMap(ConceptMap cm)
    {
      try {
	NeuronStyle ns=new NeuronStyle(controller.getComponentLoader(),
				       libraryneuron, cm);
	ns.connect(cm);
	ns.setDetailedMap(librarycontentdescription);
	try {
	  
	  fixApperance(ns);
	} catch (ReadOnlyException e)
	  {
	    Tracer.trace("The fool hasn't made the containing library for the"
			 +"clipboard editable!!! (probably)", Tracer.MINOR_EXT_EVENT);
	    Tracer.trace(e.getMessage(), Tracer.MINOR_EXT_EVENT);
	  }	
	return ns;
      } catch (Exception e)
	{
	    Tracer.debug("Exception: " + e.getMessage());
	    return null;
	}
    }
  
  protected void fixApperance(NeuronStyle ns) throws ReadOnlyException
    {
      ns.setBoundingBox(new Rectangle(5,5,60,20));
      ns.setTitle("IndexLibrary");
    }
  
  protected void fixLayout()
    {
      try {
      	java.util.Enumeration en=conceptmap.getNeuronStyles();
	int rad=0, pos=0;
	for (;en.hasMoreElements();)
	  {
	    NeuronStyle ns=(NeuronStyle) en.nextElement();
	    pos++;
	    if (pos>=5)
	      {
		rad++;
		pos=0;
		if (rad>=5)
		  rad=0;
	      }
	    Rectangle re=ns.getBoundingBox();
	    if (re!=null)
	      ns.setBoundingBox(new Rectangle(pos*70+10,rad*70+10,re.width, re.height));
	    else
	      ns.setBoundingBox(new Rectangle(pos*70+10,rad*70+10,50, 20));
	  }
      } catch (ReadOnlyException re) 
	{
	  Tracer.trace("Unable to fix Layout for IndexLibrary"+
		       "uneditable?"+ re.getMessage(),Tracer.BUG);	  
	}
    }
  
  public IndexLibrary unregisterLibrary(String str)
    {
      IndexLibrary li=getLibrary(str);
      if (li==null)
	return null;
      try {
	(conceptmap.getNeuronStyle(li.getLibraryNeuron())).disconnect();
      } catch (ReadOnlyException re) 
	{
	  Tracer.trace("Unable to unregister Library from IndexLibrary"+
		       "uneditable?" + re.getMessage(),Tracer.BUG);
	}
      fixLayout();
      return li;
    }

  public void categorizeIndexMap()  // a more general should be placed here,
    // this one should be within resourcelibrary.
    {
      if (!loaded)
	return;
      java.util.Enumeration en=conceptmap.getNeuronStyles();
      for (;en.hasMoreElements();)
	{
	  NeuronStyle ns=(NeuronStyle) en.nextElement();
	  if (ns.getNeuron().getType().endsWith("library"))
	    {
	      String [] strs=ns.getNeuron().getDataValues("TYPE");
	      if (strs.length>=1)
		{
		  if (strs[0].equalsIgnoreCase("Template") &&
		      ns.getDetailedMap()!=null)
		    {
		      TemplateLibrary tl=new TemplateLibrary(controller, ns.getURI(),
							     ns.getDetailedMap()); 
		      librarys.put("templatelibrary",tl);
		    }
		  else if (strs[0].equalsIgnoreCase("History"));
		  else if (strs[0].equalsIgnoreCase("Clipboard"))
		    {
		      ClipboardLibrary cl=new ClipboardLibrary(controller, ns.getURI(),
							       ns.getDetailedMap());
		      librarys.put("clipboardlibrary",cl);
		    }
		  else if (strs[0].equalsIgnoreCase("Catalog"));
		  else ;  //defaultlibrary...
		} 
	    }
	}
    }
  
  public IndexLibrary getLibrary(String str)
    {
      return (IndexLibrary) librarys.get(str);
    }
  public Enumeration getLibraryNames()
    {
      return librarys.keys();
    }  

  public void makeTools(MapManager manager)
  {
    manager.addToolBar(new ToolBarFactory("Library", manager, this));
    manager.addToolBar(new ToolBarFactory("Edit", manager, controller.getToolFactory()));
    manager.setActiveToolBar("Library");
  }
  
  public ToolSetBar newToolBar(MapManager manager, String name, ToolSetBar tsb)
    {
      ToolSet toolSet = manager.getToolSet();
      ToolSetBar newBar;
      if (tsb==null)
	newBar=new ToolSetBar(name);
      else
	newBar=tsb;
      Tool tool;
      
      if(name.equals("Library"))
	{
	  controller.getToolFactory().newToolBar(manager,"BrowseMenu", newBar);
	  controller.getToolFactory().newToolBar(manager,"LinearHistory", newBar);
	  
	  tool=new LibraryCloseTool(controller);
	  newBar.addTool(tool);
	  toolSet.addTool(tool);
	  
	  return newBar;
	}
      return null;
    }
  public void detachToolBar(ToolSetBar tsb)
    {}
}
