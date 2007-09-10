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
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.component.*;
import java.awt.*;
import java.util.*;

public class ClipboardLibrary extends ContentLibraryAdapter
{
  int column_width=90;
  Vector cbhistory;
  
  public static ClipboardLibrary getDefault(MapController controller)
    {
      String neuronuri="cid:local/library/ne/clipboard";
      String cduri="cid:local/library/cd/clipboard";
      try {
	return new ClipboardLibrary(controller,new URI(neuronuri),
					       new URI(cduri));
      } catch (Exception e) 
	{
	  Tracer.trace("Couldn't create default ClipboardLibrary."+e.getMessage(), Tracer.MAJOR_INT_EVENT);
	  return null;
	}
    }
  public ClipboardLibrary(MapController controller,
			  URI libraryneuron, URI librarycontentdescription)
    {
      super(controller, libraryneuron, librarycontentdescription);
      if (loaded)
	{
	  if (conceptmap.isEditingPossible())
	    conceptmap.setEditable(true);
	  else
	    {
	      controller.getComponentLoader().releaseComponent(conceptmap);
	      conceptmap=new ConceptMap(controller.getComponentLoader());
	      conceptmap.setBoundingBox(new Dimension(220,220));
	      conceptmap.setBackgroundColor(Color.white.getRGB());
	    }
	}
      cbhistory=new Vector();
      //      presentation=new MapDisplayer(conceptmap,null);
    }
  public ClipboardLibrary(NeuronStyle ns)
    {
      super(ns);
    }
  
  public boolean addNeuron(NeuronStyle ns)
    {
      boolean res=false;
      NeuronStyle nstemp;
      if ((nstemp=conceptmap.getNeuronStyle(ns.getURI()))==null)
	{
	  res=super.addNeuron(ns);
	  if (res)
	    cbhistory.addElement(currentneuron);
	}
      else
	{
	  currentneuron=nstemp;
	  cbhistory.removeElement(nstemp);
	  cbhistory.addElement(nstemp);
	  if (nstemp.getBoundingBox() !=null)
	    {
	      dim=nstemp.getBoundingBox().getSize();
	      nstemp.setBoundingBox(new Rectangle(xpos,ypos,dim.width,dim.height));
	    }
	  else
	    nstemp.setBoundingBox(new Rectangle(xpos,ypos,dim.width,dim.height));
	}
      layoutClipboard();
      return res;
    }
  
  public boolean addNeuron(se.kth.cid.content.ContentDescription cdesc)
    {
      boolean res=false;
      NeuronStyle nstemp;
      if ((nstemp=conceptmap.getNeuronStyle(cdesc.getURI()))==null)
	{
	  res=super.addNeuron(cdesc);
	  if (res)
	    cbhistory.addElement(currentneuron);
	}
      else
	{      
	  cbhistory.removeElement(nstemp);
	  cbhistory.addElement(nstemp);	  
	}
      layoutClipboard();
      return res;
    }

  public boolean setCurrentNeuron(MapEvent m)
    {
      if (m.hit!=MapEvent.HIT_NONE)
	if (m.getNeuronStyle().getNeuron() !=null)  //Necessary?
	  {
	    currentneuron=m.getNeuronStyle();
	    cbhistory.removeElement(currentneuron);
	    cbhistory.addElement(currentneuron);
	    if (currentneuron.getBoundingBox() !=null)
	      {
		dim=currentneuron.getBoundingBox().getSize();
		currentneuron.setBoundingBox(new Rectangle(xpos,ypos,dim.width,dim.height));
	      }
	    else
	      currentneuron.setBoundingBox(new Rectangle(xpos,ypos,dim.width,dim.height));
	    layoutClipboard();
	  }
      return false;
    }
      
  protected void fixApperance(NeuronStyle ns)
    {
      ns.setBoundingBox(new Rectangle(5,5,70,20));
      ns.setTitle("Clipboard");	
    }

  protected void layoutClipboard()
    {
      int size=cbhistory.size()-2;
      NeuronStyle ns;
      ypos=80;
      xpos=5;
      while (size>=0 && xpos<=180)
	{
	  ns= (NeuronStyle) cbhistory.elementAt(size);
	  dim=ns.getBoundingBox().getSize();
	  if (dim.width>=column_width)
	    ns.setBoundingBox(new Rectangle(xpos,ypos, column_width,dim.height));
	  else
	    ns.setBoundingBox(new Rectangle(xpos,ypos, dim.width ,dim.height));
	  ypos+=dim.height+5;
	  if (ypos>=180)
	    {
	      ypos=80;
	      xpos+=column_width+5;
	    }
	  size--;
	}
      while (size>=0)
	{
	  ns= (NeuronStyle) cbhistory.elementAt(size);
	  ns.disconnect();
	  cbhistory.removeElementAt(size);
	  size--;
	}
      xpos=5;
      ypos=5;
      controller.getCurrentMapManager().getDisplayer().repaint();
    }
  
  //  NeuronStyle pasteNeuron(ConceptMap conceptmap);
  //  Neuron getCurrentNeuron();
  //  URI getCurrentNeuronURI();
  //  String getCurrentNeuronTitle();

  //  bool setCurrentNeuron(MapEvent m);
  //  bool setCurrentNeuron(Neuron ne);
  //  Enumeration getNeuronStyles();
}
