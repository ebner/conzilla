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
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;

/** This interface describes the visual attributes of an Axon contained
 *  in a ConceptMap.
 */
public interface AxonStyle
{
  int FIRST_AXONSTYLE_EDIT_CONSTANT = NeuronStyle.LAST_NEURONSTYLE_EDIT_CONSTANT + 1;
  int LINE_EDITED                   = FIRST_AXONSTYLE_EDIT_CONSTANT;
  int DATATAG_ADDED                 = FIRST_AXONSTYLE_EDIT_CONSTANT + 1;
  int DATATAG_REMOVED    	    = FIRST_AXONSTYLE_EDIT_CONSTANT + 2;
  
  int LAST_AXONSTYLE_EDIT_CONSTANT  = DATATAG_REMOVED;

  /** Removes the AxonStyle from the two connecting NeuronStyles.
   *  This will destroy the AxonStyle.
   */
  void remove()  throws ReadOnlyException;

  /** Returns the line representing this AxonStyle. The last coordinate is
   *  the end connecting to the pointed-to NeuronStyle.
   *
   *  @return the line representing this AxonStyle. Never null,
   *          and contains at least two elements.
   */
  ConceptMap.Position[] getLine();
  
  /** Sets the line representing this AxonStyle.
   *
   * Fires an EditEvent(LINE_EDITED) in the ConceptMap, with the new
   * line as target.
   *
   * @param line the new line. Must not be null, and must contain
   *             at least two elements.
   */
  void setLine(ConceptMap.Position[] line) throws ReadOnlyException;

  /** Returns the ID of this AxonStyle, corresponding to the ID of the Axon
   *  that is presented by this AxonStyle.
   *
   *  @return the ID of this AxonStyle.
   */
  String getAxonID();

  /** Returns the NeuronStyle that is the owner of this AxonStyle.
   *  @return the NeuronStyle that is the owner of this AxonStyle.
   */
  NeuronStyle getOwner();

  /** Returns the NeuronStyle that is the end of this AxonStyle.
   *  @return the NeuronStyle that is the end of this AxonStyle.
   */
  NeuronStyle getEnd();

  /** Returns the data tags that should be displayed.
   *  @return the data tags that should be displayed.
   */
  String[] getDataTags();

  /** Adds a data tag that should be displayed.
   *
   *  Fires an EditEvent(DATATAG_ADDED) in the ConceptMap, with the
   *  new tag name as target.
   *  @param tag the tag name to show.
   */
  void addDataTag(String tag) throws ReadOnlyException;
  
  /** Removes a data tag that should be displayed.
   *
   *  Fires an EditEvent(DATATAG_REMOVED) in the ConceptMap, with the
   *  removed tag name as target.
   *  @param tag the tag name to remove.
   */
  void removeDataTag(String tag) throws ReadOnlyException;

}

