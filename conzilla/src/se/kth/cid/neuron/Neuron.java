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

/** This is the interface representing Neurons.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface Neuron extends Component
{
  int FIRST_NEURON_EDIT_CONSTANT = LAST_COMPONENT_EDIT_CONSTANT + 1;

  int DATAVALUES_EDITED          = FIRST_NEURON_EDIT_CONSTANT;
  int AXON_ADDED                 = FIRST_NEURON_EDIT_CONSTANT + 1;
  int AXON_REMOVED               = FIRST_NEURON_EDIT_CONSTANT + 2;

  int LAST_NEURON_ONLY_EDIT_CONSTANT  = AXON_REMOVED;
  int LAST_NEURON_EDIT_CONSTANT  = Axon.LAST_AXON_EDIT_CONSTANT;


  /** Returns the URI of the NeuronType of this Neuron.
   *  This String may be assumed to be a valid URI that may be relative
   *  to this Neuron.
   *
   *  @return the URI of the NeuronType of this Neuron.
   */
  String       getType();

  /** Returns the data values contained in this Neuron.
   *  Never null, but may be empty.
   *
   *  @return the data valuess contained in this Neuron.
   */
  DataValue[] getDataValues();


  /** Sets the data values of this Neuron.
   *
   *  @param values the data values. Should not be empty, but may be null.
   *  @exception ReadOnlyException if the Neuron was not editable.
   */
  void     setDataValues(DataValue[] value) throws ReadOnlyException;


  /** Returns the Axons of this Neuron.
   *
   *  @return the Axons of this Neuron. Never null.
   */
  Axon[]    getAxons();

  /** Returns the Axon with the given ID, or null if no such Axon is found.
   *
   *  @param id the id of the Axon.
   *  @return the Axon with the given ID.
   */
  Axon      getAxon(String id); 

  /** Removes the given Axon.
   *
   *  @param id the Axon to remove.
   *  @exception ReadOnlyException if the Neuron was not editable.
   */
  void      removeAxon(String id) throws ReadOnlyException;

  /** Adds an axon to this Neuron. The Axon ID will be generated.
   *
   *  @param type the type of the Axon.
   *  @param neuronURI the URI of the pointed-to Neuron.
   *
   *  @return the created Axon.
   *  @exception ReadOnlyException if the Neuron was not editable.
   *  @exception NeuronException if the Axon could not be created.
   *  @exception InvalidURIException if the Neuron URI was not valid.
   */
  Axon      addAxon(String type, String neuronURI)
    throws ReadOnlyException, NeuronException, InvalidURIException;

}



