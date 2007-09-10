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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import java.util.*;
import java.awt.*;


public abstract class ContentLibraryAdapter extends IndexLibraryAdapter implements ContentLibrary
{
  NeuronStyle currentneuron;
  int xpos=5, ypos=5;
  Dimension dim;
  
  public ContentLibraryAdapter(MapController controller,
			       URI libraryneuron, URI librarycontentdescription)
    {
      super(controller, libraryneuron, librarycontentdescription);
      currentneuron=null;
    }
  
  public ContentLibraryAdapter(NeuronStyle ns)
    {
      super(ns);
    }
  
  public boolean addNeuron(NeuronStyle ns)
    {
      try {
	currentneuron=new NeuronStyle(ns);
	currentneuron.connect(conceptmap);
	String title=ns.getTitle();
	if (title==null || title.equals(""))
	  {
	    title = currentneuron.getNeuron().getMetaData().getValue("Title");
	    if(title == null || title.equals(""))
	      title = ns.getURI().toString();
	    currentneuron.setTitle(title);
	  }
	if (ns.getBoundingBox() !=null)
	  {
	    dim=ns.getBoundingBox().getSize();
	    currentneuron.setBoundingBox(new Rectangle(xpos,ypos,dim.width,dim.height));
	  }
	else
	  currentneuron.setBoundingBox(new Rectangle(xpos,ypos,dim.width,dim.height));
      } catch (Exception e) {
	Tracer.debug("Readonly or something in library when trying to copy neuron!" +
		     e.getMessage());
	return false;
      }
      return true;
    }
  public boolean addNeuron(ContentDescription cdesc)
    {
      NeuronStyle ns;
      try{
	ns=conceptmap.addNeuronStyle(cdesc.getURI()); 
	if (ns!=null)
	  {
	    String title=ns.getTitle();
	    if (title==null || title.equals(""))
	      {
		title = ns.getNeuron().getMetaData().getValue("Title");
		if(title == null || title.equals(""))
		  title = ns.getURI().toString();
		ns.setTitle(title);
	      }
	    ns.setBoundingBox(new Rectangle(xpos,ypos,300,70));  //ugly!!! how to know how big text is here?? global design-class?
	    ns.addDataTag("URI");
	    ns.addDataTag("MIMEType");
	    currentneuron=ns;
	  }
      } catch(Exception e)
	{
	  Tracer.trace("Couldn't copy conceptmap's contentdescription to library",Tracer.MINOR_EXT_EVENT);
	  return false;
	}
      return true;
    }
  
  public boolean addNeuron(MapEvent m)
    {
      NeuronStyle ns=null;
      switch (m.hit)
	{
	case MapEvent.HIT_NONE:   // Means that we should make copy of conceptmap,
	                        // i.e it's contentdescription.
	  return addNeuron(m.mapdisplayer.getContentDescription());
	default:
	  if (m.neuronstyle != null  && m.neuronstyle.getNeuron() != null)
	    ns=m.neuronstyle;
	  if (m.rolestyle != null && m.rolestyle.getRoleOwner().getNeuron() != null)
	    ns=m.rolestyle.getRoleOwner();
	  if (ns!=null)
	    return addNeuron(ns);
	}
      return false;
    }
  
  public NeuronStyle pasteNeuron(ConceptMap conceptmap)
    {
      try {
	if (currentneuron!=null)
	  {
	    NeuronStyle ns=new NeuronStyle(currentneuron);
	    if (ns.connect(conceptmap))
	      return ns;
	    else return null;
	  }
      } catch (ReadOnlyException e) {      
      } catch (NeuronStyleException e) {
      } catch (ComponentException e) {
      }
      return null;
    }
  
  public Neuron getCurrentNeuron()
    {
      if (currentneuron!=null)
	return currentneuron.getNeuron();
      return null;
    }
  
  public URI getCurrentNeuronURI()
    {
      if (currentneuron!=null)
	return currentneuron.getURI();
      return null;      
    }
  
  public String getCurrentNeuronTitle()
    {
      if (currentneuron!=null)
	return currentneuron.getTitle();
      return null;      
    }

  public boolean setCurrentNeuron(MapEvent m)
    {
      if (m.hit!=MapEvent.HIT_NONE)
	if (m.getNeuronStyle().getNeuron() !=null)  //Necessary?
	  {
	    currentneuron=m.getNeuronStyle();
	    return true;
	  }
      return false;
    }
  
  public boolean setCurrentNeuron(Neuron ne)
    {
      String uri=ne.getURI();
      java.util.Enumeration en=conceptmap.getNeuronStyles();
      for (;en.hasMoreElements();)
	{
	  NeuronStyle ns=(NeuronStyle) en.nextElement();
	  if (ns.getURI().toString().equals(uri))
	    {
	      currentneuron=ns;
	      return true;
	    }
	}
      return false;
    }
  
  public Enumeration getNeuronStyles()  //Should be overloaded.....
                                       //to sort out sublibrarys and more.
    {
      return conceptmap.getNeuronStyles();
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
	  
	  tool = (Tool) new LibraryChooseTool(manager, this);
	  newBar.addTool(tool);
	  toolSet.addTool(tool);
	  newBar.setDefaultTool(tool);
	  
	  controller.getToolFactory().newToolBar(manager,"LinearHistory", newBar);

	  tool=new LibraryCloseTool(controller);
	  newBar.addTool(tool);
	  toolSet.addTool(tool);
	  
	  return newBar;
	}
      return null;
    }
}
