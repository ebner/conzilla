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
import se.kth.cid.component.*;

/** This is the interface representing an Axon in a Neuron.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface Axon
{
  int FIRST_AXON_EDIT_CONSTANT   = Neuron.LAST_NEURON_ONLY_EDIT_CONSTANT + 1;

  int DATAVALUES_EDITED          = FIRST_AXON_EDIT_CONSTANT;

  int LAST_AXON_EDIT_CONSTANT    = DATAVALUES_EDITED;

  /** Returns the ID of this Axon. It is unique within the containing Neuron.
   *
   *  @return the ID of this Axon.
   */
  String getID();

  /** Returns the type of this Axon. This type is one of the AxonTypes given
   *  this Neuron's NeuronType.
   *
   *  @return the type of this Axon.
   */
  String getType();

  /** Returns the URI of the Neuron this Axon points to.
   *  This String may be assumed to be a valid URI that may be relative
   *  to the Neuron.
   *
   *  @return the URI of the pointed-to Neuron.
   */
  String getEndURI();

  /** Returns the Neuron that contains this Axon.
   *
   *  @return the Neuron that contains this Axon.
   */
  Neuron getNeuron();

  /** Returns the data values contained in this Axon.
   *  Never null, but may be empty.
   *
   *  @return the data valuess contained in this Axon.
   */
  Neuron.DataValue[] getDataValues();


  /** Sets the data values of this Axon.
   *
   *  @param values the data values. Should not be empty, but may be null.
   *  @exception ReadOnlyException if the Neuron was not editable.
   */
  void     setDataValues(Neuron.DataValue[] value) throws ReadOnlyException;

}



