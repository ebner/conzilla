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


package se.kth.cid.conzilla.edit;
import java.util.*;

/** There can be several different NeuronDialogs,
 *  this class provides methods to access
 *  different NeuronDialogs by a keyword, a (neuron)dialogname.
 *
 *  This class follows the programming pattern of an abstract factory
 *  implemented with prototypes.
 */
class NeuronDialogFactory
{
  /** A Hashtable of NeuronDialogs with dialognames as keys,
   *  typical there should be one called contentdescription.
   *  @see getPossibleDialogs
   */
  Hashtable neuronDialogs;
  
  public NeuronDialogFactory()
    {
      neuronDialogs=new Hashtable();
    }
  
  /** The NeuronDialogs Keys, e.g. (neuron)dialognames.
   *
   *  @return Enumeration contains the keys in the form of String's.
   */
  public Enumeration getPossibleDialogs()
    {
      return neuronDialogs.keys();
    }
  
  /** Returns a new NeuronDialog specified by it's String key dialogname.
   *  The hashtable contains NeuronDialog-prototypes which are copied by
   *  the copy-functionality in NeuronType.
   *
   *  @see NeuronDialog
   *  @return NeuronDialog i.e. an object implementing this interface.
   */
  public NeuronDialog getNeuronDialog(String dialogname)
    {
      return ((NeuronDialog) neuronDialogs.get(dialogname)).copy();
    }

  /** Adds a NeuronDialog.
   * @param nf is the NeuronDialog to add
   * @param dialogname  the key to the dialog.
   */
  public void addNeuronDialog(NeuronDialog nd, String dialogname)
    {
      neuronDialogs.put(dialogname, nd);
    }
  
  /** Removes the NeuronDialog with key dialogname if it exists.
   *
   *  @param dialogname is a String, i.e a key to the dialog to be removed.
   */
  public NeuronDialog removeNeuronDialog(String dialogname)
    {
      return (NeuronDialog) neuronDialogs.remove(dialogname);
    }
}


