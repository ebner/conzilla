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


/** A local conceptmap with local metadata.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */

public class LocalConceptMap extends GenericConceptMap
{
  /** The metadata of this component.
   */
  protected GenericMetaData metaData;

  /** Creates an empty ConceptMap with the given URI.
   *
   *  @param mapURI the URI of this map.
   *  @param loadURI the URI used to load this map.
   *  @param loadType the MIME type used to load this map.
   */
  public LocalConceptMap(URI mapURI, URI loadURI, MIMEType loadType)
    {
      super(mapURI, loadURI, loadType);
      metaData = new LocalMetaData(this);
    }

    public MetaData getMetaData()
    {
	return metaData;
    }

  /////////////ConceptMap/////////////
  
  
  /////////////NeuronStyle////////////
  protected NeuronStyle addNeuronStyleImpl(String mapID, String neuronURI, String parentMapID)
      throws InvalidURIException
    {
	NeuronStyle ns = new LocalNeuronStyle(mapID, neuronURI, this);
	layerManager.addNeuronStyle(ns, this, parentMapID);

	return ns;
    }

}



