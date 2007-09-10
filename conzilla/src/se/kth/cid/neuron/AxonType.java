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


package se.kth.cid.neuron;
import se.kth.cid.util.*;
import se.kth.cid.component.*;

/** This is the interface representing an AxonType in a Neurontype.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface AxonType
{
  int FIRST_AXONTYPE_EDIT_CONSTANT
  = NeuronType.LAST_NEURONTYPE_ONLY_EDIT_CONSTANT + 1;
  
  int MINIMUMMULTIPLICITY_EDITED     = FIRST_AXONTYPE_EDIT_CONSTANT;
  int MAXIMUMMULTIPLICITY_EDITED     = FIRST_AXONTYPE_EDIT_CONSTANT + 1;
  int LINETYPE_EDITED                = FIRST_AXONTYPE_EDIT_CONSTANT + 2;
  int HEADTYPE_EDITED                = FIRST_AXONTYPE_EDIT_CONSTANT + 3;
  int DATATAG_ADDED                  = FIRST_AXONTYPE_EDIT_CONSTANT + 4;
  int DATATAG_REMOVED                = FIRST_AXONTYPE_EDIT_CONSTANT + 5;
  
  int LAST_AXONTYPE_EDIT_CONSTANT    = DATATAG_REMOVED;

  /** Returns the containing NeuronType.
   *
   *  @return the containing NeuronType.
   */
  NeuronType getNeuronType();

  /** Returns the name of the axon type this AxonType represents.
   *
   *  @return the name of the axon type.
   */
  String  getType();


  /** Returns the list of allowed data tags in this AxonType.
   *
   *  @return the list of allowed data tags in this AxonType. Never null,
   *          but may be empty.
   */
  String[] getDataTags();

  /** Adds a data tag to be allowed in this axon type.
   *
   *  @param tag the tag to add.
   *  @exception ReadOnlyException if the containing NeuronType
   *             was not editable.
   */
  void     addDataTag(String tag) throws ReadOnlyException;

  /** Removes a data tag to be allowed in this axon type.
   *
   *  @param tag the tag to remove.
   *  @exception ReadOnlyException if the containing NeuronType
   *             was not editable.
   */
  void     removeDataTag(String tag) throws ReadOnlyException;

  /** Returns the minimum multiplicity of this axon type.
   *  This is the recommended minimum number of axons that should
   *  be of this type in any given Neuron having the containing NeuronType as
   *  type. It is not normally enforced, but should be by editing tools.
   *
   *  @return the minimum multiplicity.
   */
  int     getMinimumMultiplicity();

  /** Sets the minimum multiplicity of this axon type.
   *
   *  @param mult the minimum multiplicity. Must be non-negative.
   *  @exception ReadOnlyException if the containing NeuronType
   *             was not editable.
   */
  void    setMinimumMultiplicity(int mult) throws ReadOnlyException;    

  /** Returns the maximum multiplicity of this axon type.
   *  This is the recommended maximum number of axons that should
   *  be of this type in any given Neuron having the containing NeuronType as
   *  type. It is not normally enforced, but should be by editing tools.
   *  It is always greater or equal to the minimum multiplicity.
   *
   *  @return the maximum multiplicity.
   */
  int     getMaximumMultiplicity();

  /** Sets the maximum multiplicity of this axon type.
   *
   *  @param mult the maximum multiplicity. Must be greater or
   *         equal to the minimum multiplicity.
   *  @exception ReadOnlyException if the containing NeuronType
   *             was not editable.
   */
  void    setMaximumMultiplicity(int mult) throws ReadOnlyException;    

  /** Returns the line type of this axon type.
   *
   *  @return the line type of this axon type. Never null.
   */
  NeuronType.LineType  getLineType();

  /** Sets the line type of this axon type.
   *
   *  @param type the line type of this axon type. Never null.
   *  @exception ReadOnlyException if the containing NeuronType
   *             was not editable.
   *  @exception NeuronException if the type was not valid.
   */
  void    setLineType(NeuronType.LineType type) throws ReadOnlyException, NeuronException;

  /** Returns the head type of this axon type.
   *
   *  @return the head type of this axon type. Never null.
   */
  NeuronType.HeadType getHeadType();

  /** Sets the head type of this axon type.
   *
   *  @param type the head type of this axon type. Never null.
   *  @exception ReadOnlyException if the containing NeuronType
   *             was not editable.
   *  @exception NeuronException if the type was not valid.
   */
  void    setHeadType(NeuronType.HeadType type) throws ReadOnlyException, NeuronException;
  
}
