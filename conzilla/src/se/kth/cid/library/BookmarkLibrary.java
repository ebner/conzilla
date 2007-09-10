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


package se.kth.cid.library;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;

import java.util.*;


public class BookmarkLibrary extends GenericLibrary
{
  
  public BookmarkLibrary(ComponentStore store, URI libNeuronURI)
    throws LibraryException
    {
      super(store, libNeuronURI);
    }

  protected void checkNeuron(Neuron n, NeuronType nt)
    throws LibraryException
    {
      if(!isBookmarkNeuron(n, nt))
	throw new LibraryException("Neuron was no bookmark: '" +
				   libNeuron.getURI() + "'.");
    }

  
  protected void checkLibrary() throws LibraryException
    {
      if(!isBookmarkLibrary(libNeuron, libNeuronType))
	throw new LibraryException("Neuron was no bookmark library: '" +
				   libNeuron.getURI() + "'.");
    }
  
  public static boolean isBookmarkLibrary(Neuron n, NeuronType type)
    {
      String[] taxon = {"Library", "Generic", "Bookmark"};
      
      return MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
					  "NeuronType", "Conzilla", taxon);
    }

  public static boolean isBookmarkNeuron(Neuron n, NeuronType type)
    {
      String[] taxon = {"Reference", "Generic", "Bookmark"};
      
      return MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
					  "NeuronType", "Conzilla", taxon);
    }
}

