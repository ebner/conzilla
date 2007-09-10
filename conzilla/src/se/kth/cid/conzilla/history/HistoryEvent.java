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

package se.kth.cid.conzilla.history;

import se.kth.cid.identity.*;
import se.kth.cid.conzilla.controller.*;


/** This class is used to describe history events.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class HistoryEvent
{
  /** The type fired when a new map is loaded.
   */
  public final static int MAP     = 0;

  /** The type fired when a content selection occurs.
   */
  public final static int CONTENT = 1;

  /** The source of the event.
   */
  MapController source;

  /** The URI of the source map.
   */
  URI sourceMapURI;

  /** The title of the source map.
   */
  String sourceMapTitle;

  
  /** The URI of the source neuron.
   */
  URI sourceNeuronURI;

  /** The title of the source neuron.
   */
  String sourceNeuronTitle;


  /** The URI of the destination component.
   */
  URI destinationURI;

  /** The title of the destination component.
   */
  String destinationTitle;

  /** The type of event.
   */
  int type;

  /** Contructs a HistoryEvent.
   */
  public HistoryEvent(int type, MapController source,
		      URI sourceMapURI, String sourceMapTitle,
		      URI sourceNeuronURI, String sourceNeuronTitle,
		      URI destinationURI, String destinationTitle)
    
  {
    this.type = type;
    this.source = source;

    this.sourceMapURI = sourceMapURI;
    this.sourceMapTitle = sourceMapTitle;
    
    this.sourceNeuronURI = sourceNeuronURI;
    this.sourceNeuronTitle = sourceNeuronTitle;
    
    this.destinationURI = destinationURI;
    this.destinationTitle = destinationTitle;
  }

  /** Returns the type of this HistoryEvent.
   *
   *  @return the type of this HistoryEvent.
   */  
  public int getType()
  {
    return type;
  }

  /** Returns the source of this event.
   *
   *  @return the source of this event.
   */
  public MapController getSource()
  {
    return source;
  }
  
  /** Returns the source map of this event.
   *
   *  @return the source map of this event.
   */
  public URI getSourceMapURI()
  {
    return sourceMapURI;
  }

  /** Returns the title of the source map of this event.
   *
   *  @return the title of the source map of this event.
   */
  public String getSourceMapTitle()
  {
    return sourceMapTitle;
  }

  
  /** Returns the source neuron of this event.
   *
   *  @return the source neuron of this event.
   */
  public URI getSourceNeuronURI()
  {
    return sourceNeuronURI;
  }

  /** Returns the title of the source neuron of this event.
   *
   *  @return the title of the source neuron of this event.
   */
  public String getSourceNeuronTitle()
  {
    return sourceNeuronTitle;
  }

  
  /** Returns the destination of this event.
   *
   *  @return the destination of this event.
   */
  public URI getDestinationURI()
  {
    return destinationURI;
  }
  
  /** Returns the title of the destination of this event.
   *
   *  @return the title of the destination of this event.
   */
  public String getDestinationTitle()
  {
    return destinationTitle;
  }
}
