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


package se.kth.cid.conceptmap;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.local.*;
import se.kth.cid.content.*;

import java.awt.Dimension;

import java.util.*;

public class ConceptMap extends LocalComponent
{
  public static final String MIME_TYPE = "application/x-conceptmap";

  public static final int FIRST_CONCEPTMAP_EDIT_CONSTANT
                                              = NeuronType.LAST_NEURONTYPE_EDIT_CONSTANT + 1;
  public static final int MAPSET_EDITED       = FIRST_CONCEPTMAP_EDIT_CONSTANT;
  public static final int BOUNDINGBOX_EDITED  = FIRST_CONCEPTMAP_EDIT_CONSTANT + 1;
  public static final int BACKGROUND_EDITED   = FIRST_CONCEPTMAP_EDIT_CONSTANT + 2;
  public static final int NEURONSTYLE_ADDED   = FIRST_CONCEPTMAP_EDIT_CONSTANT + 3;
  public static final int NEURONSTYLE_REMOVED = FIRST_CONCEPTMAP_EDIT_CONSTANT + 4;
  public static final int ROLESTYLE_REMOVED   = FIRST_CONCEPTMAP_EDIT_CONSTANT + 5;
  public static final int ROLESTYLE_ADDED     = FIRST_CONCEPTMAP_EDIT_CONSTANT + 6;
  public static final int LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT
                                              = FIRST_CONCEPTMAP_EDIT_CONSTANT + 6;
  public static final int LAST_CONCEPTMAP_EDIT_CONSTANT
                                              = RoleStyle.LAST_ROLESTYLE_EDIT_CONSTANT;

  private ComponentLoader loader;
  private Object    appobject;

  private URI mapSetURI;
  ////////////ConceptMap-varibles//////
  //  private URI       aspectset;
  private Dimension boundingbox;
  private int       backgroundcolor;
  // visible neurons??
  ////////////NeuronStyle-varibles/////
  private Hashtable neuronstyles;

  public ConceptMap(ComponentLoader loader)
  {
    this.loader=loader;
    
    appobject=null;
    neuronstyles=new Hashtable();
  }


  /////Overloaded functions from LocalComponent/////
  public void setURI(String uri) throws ReadOnlyException, MalformedURIException
    {
      String suri=getURI();
      super.setURI(uri);
      if (suri!=null)
	{
	URI olduri=new URI(suri);
	URI newuri=new URI(uri);
	loader.renameComponent(olduri,newuri);
	}
    }

  public void disconnectAll() throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    Enumeration en=getNeuronStyles();
    for(;en.hasMoreElements();)
      ((NeuronStyle)en.nextElement()).disconnect();
  }

  public Object  getAppObject()
  {
    return appobject;
  }

  public void    setAppObject(Object ob)
  {
    appobject=ob;
  }
  
  /////////////ConceptMap/////////////
  public Dimension     getBoundingBox()
  {
    return boundingbox;
  }

  public void          setBoundingBox(Dimension bg) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    boundingbox=bg;
    fireEditEvent(new EditEvent(this, BOUNDINGBOX_EDITED, bg));
  }

  public int           getBackgroundColor()
  {
    return backgroundcolor;
  }

  public void          setBackgroundColor(int color) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");      
    backgroundcolor=color;
    fireEditEvent(new EditEvent(this, BACKGROUND_EDITED, new Integer(color)));
  }

  public URI           getMapSet()
  {
    return mapSetURI;
  }
  
  public void          setMapSet(URI mapSetURI) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");      
    this.mapSetURI = mapSetURI;
    fireEditEvent(new EditEvent(this, MAPSET_EDITED, mapSetURI));
  }
  
  /////////////NeuronStyle////////////
  public Enumeration   getNeuronStyles()
  {
    return neuronstyles.elements();
  }

  public NeuronStyle   getNeuronStyle(URI neuronuri)
  {
    return (NeuronStyle) neuronstyles.get(neuronuri);
  }

  public NeuronStyle   addNeuronStyle(URI neuronuri)
    throws ReadOnlyException, NeuronStyleException, ComponentException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    if (neuronstyles.contains(neuronuri))
      throw new NeuronStyleException("Neuron width URI="+neuronuri+" already exists in "+
				     "this map, double occurences is forbidden!");
    NeuronStyle ns=new NeuronStyle(loader, neuronuri, this);
    neuronstyles.put(neuronuri,ns);
    fireEditEvent(new EditEvent(this, NEURONSTYLE_ADDED, ns));
    return ns;
  }

  /** Adds the neuronstyle into this map, shouldn't be called directly
   *  ,use connect in neuronstyle instead.
   *
   *  @see neuronstyle:connect 
   */ 
  public boolean addNeuronStyle(NeuronStyle ns)     
  {
    if (!isEditable() || neuronstyles.contains(ns.getURI()))
      return false;
    Tracer.debug("ConceptMap: addNeuronstyle with uri: "+ns.getURI());
    neuronstyles.put(ns.getURI(),ns);
    fireEditEvent(new EditEvent(this, NEURONSTYLE_ADDED, ns));
    return true;
  }
  /** Update list of neuronstyles when neuronstyle changed uri.
   *  The neuronstyle is itself responsible for calling this function.
   */
  public void   renameNeuronStyle(URI olduri, URI newuri)
    {
      NeuronStyle ns;
      if ( (ns =(NeuronStyle) neuronstyles.remove(olduri)) != null)
	neuronstyles.put(newuri,ns);
    }

  public void          spreadVisibility(int visibility)
  {
  }

  public Enumeration        getVisibleNeuronStyles()
  {
    return null;
  }
  
  /** Not meant to be used directly, call disconnect on the
   *  neuronstyle instead.
   *  @param neuronstyle  a style for a neuron.
   *  @see NeuronStyle
   */
  public void          removeNeuronStyle(URI neuronuri) throws ReadOnlyException
  {
    NeuronStyle ns = (NeuronStyle) neuronstyles.get(neuronuri);
    if(ns != null)
      {
	neuronstyles.remove(neuronuri);
	fireEditEvent(new EditEvent(this, NEURONSTYLE_REMOVED, ns));
      }
  }

  public String toString()
  {
    return "ConceptMap(" + getURI() + ")";
  }

}



