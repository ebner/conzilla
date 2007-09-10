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


package se.kth.cid.conceptmap.local;
import se.kth.cid.identity.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.local.*;

import java.util.*;

/** A straightforward implementation of the ConeptMap interface.
 *
 *  @author Mikael Nilsson
 *  @author Matthias Palmer
 *  @version $Revision$
 */

public abstract class GenericConceptMap extends GenericComponent implements ConceptMap, LayerListener 
{
  
  protected ConceptMap.Dimension dimension;

    //  protected Hashtable neuronStyles = null;
  protected LocalLayerManager layerManager;
  protected Vector orderedNeuronStyles = null;
  Vector axonsWithObjectEndUnmatched;
  Vector axonsWithSubjectEndUnmatched;


  protected GenericConceptMap(URI mapURI, URI loadURI, MIMEType loadType)
    {
      super(mapURI, loadURI, loadType);
      layerManager = new LocalLayerManager();
      layerManager.addLayerListener(this);
      dimension = new ConceptMap.Dimension(400, 400);
      axonsWithObjectEndUnmatched = new Vector();
      axonsWithSubjectEndUnmatched = new Vector();
    }

    ///////////LayerListener////////////////
  public void layerChange(LayerEvent event)
    {
	clearNeuronStyleCache();
    }

  /////////////ConceptMap/////////////
  public ConceptMap.Dimension   getDimension()
  {
    return dimension;
  }

  public void setDimension(ConceptMap.Dimension dim) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      if(dim == null)
	throw new  IllegalArgumentException("Null dimension.");

      dimension = dim;
      fireEditEvent(new EditEvent(this, this, DIMENSION_EDITED, dim));
    }
  
  
  /////////////NeuronStyle////////////
  
  public LayerManager getLayerManager()
    {
	return layerManager;
    }


  public NeuronStyle[]   getNeuronStyles()
    {
	if (orderedNeuronStyles == null)
	    orderedNeuronStyles = layerManager.getNeuronStyles(MapGroupStyle.IGNORE_VISIBILITY);
	return (NeuronStyle[]) orderedNeuronStyles.toArray(new NeuronStyle[orderedNeuronStyles.size()]);
    }

  public NeuronStyle   getNeuronStyle(String mapID)
    {
	return layerManager.getNeuronStyle(mapID);
    }

  public void clearNeuronStyleCache()
    {
	orderedNeuronStyles = null;
    }

  protected void cacheNeuronStyles()
    {
	if (orderedNeuronStyles == null)
	    orderedNeuronStyles = layerManager.getNeuronStyles(MapGroupStyle.IGNORE_VISIBILITY);
    }

  /** Works as a factory for neuronstyles.
   */
  protected abstract NeuronStyle addNeuronStyleImpl(String mapID, String neuronuri, String parentMapID) throws InvalidURIException;
	

  /** Used to add a NeuronStyle with a known ID. Typically used only
   *  when loading a saved ConceptMap.
   *  May fail if the Neuron does not exist, but not necessarily.
   *
   *  @param mapID the ID of the NeuronStyle.
   *  @param neuronURI the URI of the Neuron to be represented.
   *
   *  @return the new NeuronStyle.
   *  @exception ReadOnlyException if this ConceptMap was not editable.
   *  @exception ConceptMapException if the ID was aleady in use.
   *  @exception InvalidURIException if the URI was not valid.
   */
  public NeuronStyle   addNeuronStyle(String mapID, String neuronuri)
    throws ReadOnlyException, InvalidURIException, ConceptMapException
    {
	return addNeuronStyle(mapID, neuronuri, layerManager.getEditMapGroupStyle().getURI());
    }


  public NeuronStyle   addNeuronStyle(String mapID, String neuronuri, String parentMapID)
    throws ReadOnlyException, InvalidURIException, ConceptMapException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
            
      //FIXME:  should all ObjectStyles IDs be taken into account??
      if(layerManager.IDSet().contains(mapID))
	throw new ConceptMapException("The NeuronStyle with Id '" + mapID
				      + "' was already in this map.");
      
      clearNeuronStyleCache();
      NeuronStyle ns = addNeuronStyleImpl(mapID, neuronuri, parentMapID);

      connectNeuronStyle((ExtendedNeuronStyle) ns);
      fireEditEvent(new EditEvent(this, this, NEURONSTYLE_ADDED, mapID));
      return ns;
    }

  
  
  public NeuronStyle   addNeuronStyle(String neuronuri)
    throws ReadOnlyException, InvalidURIException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      String mapID = createID(layerManager.IDSet(), neuronuri);


      clearNeuronStyleCache();      
      NeuronStyle ns = addNeuronStyleImpl(mapID, neuronuri, null);

      connectNeuronStyle((ExtendedNeuronStyle) ns);
      fireEditEvent(new EditEvent(this, this, NEURONSTYLE_ADDED, mapID));
      return ns;
    }
  

  /** Removes the given NeuronStyle.
   *  Do not use directly. Call NeuronStyle.remove() instead.
   *
   *  @param neuronstyle  a style for a neuron.
   *  @see NeuronStyle
   */
  protected void removeNeuronStyle(NeuronStyle neuronstyle)
    {
	if(layerManager.removeNeuronStyle(neuronstyle))
	    clearNeuronStyleCache();
	else
	    Tracer.bug("No such NeuronStyle when removing: '" + neuronstyle.getURI() + "'!");
	dissconnectNeuronStyle((ExtendedNeuronStyle) neuronstyle);
    }

  protected void dissconnectNeuronStyle(ExtendedNeuronStyle ns)
    {
	AxonStyle [] ass = ns.getObjectOfAxonStyles();
	for (int i=0;i<ass.length;i++)
	    {
		((ExtendedAxonStyle) ass[i]).setObject(null);
		axonsWithObjectEndUnmatched.add(ass[i]);
	    }
	ass = ns.getSubjectOfAxonStyles();
	for (int i=0;i<ass.length;i++)
	    {
		((ExtendedAxonStyle) ass[i]).setSubject(null);
		axonsWithSubjectEndUnmatched.add(ass[i]);
	    }
    }	

  protected void connectNeuronStyle(ExtendedNeuronStyle ns)
    {
	Iterator it = axonsWithObjectEndUnmatched.iterator();
	while (it.hasNext())
	    {
		ExtendedAxonStyle as = (ExtendedAxonStyle) it.next();
		if (as.getObjectURI().equals(ns.getURI()))
		    {
			ns.addObjectOfAxonStyle(as);
			it.remove();
		    }
	    }
	it = axonsWithSubjectEndUnmatched.iterator();
	while (it.hasNext())
	    {
		ExtendedAxonStyle as = (ExtendedAxonStyle) it.next();
		if (as.getSubjectURI().equals(ns.getURI()))
		    {
			ns.addSubjectOfAxonStyle(as);
			it.remove();
		    }
	    }
    }

  public void addAxonStyleEnds(ExtendedAxonStyle as)
    {
	ExtendedNeuronStyle lnso = (ExtendedNeuronStyle) getNeuronStyle(as.getObjectURI());
	if (lnso != null)
	    lnso.addObjectOfAxonStyle(as);
	else
	    axonsWithObjectEndUnmatched.add(as);

	ExtendedNeuronStyle lnss = (ExtendedNeuronStyle) getNeuronStyle(as.getSubjectURI());
	if (lnss != null)
	    lnss.addSubjectOfAxonStyle(as);
	else
	    axonsWithSubjectEndUnmatched.add(as);
    }

  public void removeAxonStyleEnds(ExtendedAxonStyle as)
    {
	ExtendedNeuronStyle lnso = (ExtendedNeuronStyle) getNeuronStyle(as.getObjectURI());
	if (lnso != null)
	    lnso.removeObjectOfAxonStyle(as);
	else
	    axonsWithObjectEndUnmatched.remove(as);

	ExtendedNeuronStyle lnss = (ExtendedNeuronStyle) getNeuronStyle(as.getSubjectURI());
	if (lnss != null)
	    lnss.removeSubjectOfAxonStyle(as);
	else
	    axonsWithSubjectEndUnmatched.remove(as);
    }
}



