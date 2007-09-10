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


public class RootLibrary extends GenericLibrary
{
  TemplateLibrary tempLib;
  BookmarkLibrary bookmarkLib;
  ClipboardLibrary clipboard;

  public RootLibrary(ComponentStore store, URI libNeuronURI)
    throws LibraryException
    {
      super(store, libNeuronURI);

      clipboard=new ClipboardLibrary();
      try {
	for(int i = 0; i < subLibNeurons.size(); i++)
	  {
	    Neuron     n  = (Neuron) subLibNeurons.get(i);
	    NeuronType nt = store.getAndReferenceNeuronType(URIClassifier.parseValidURI(n.getType(), n.getURI()));
	    if (TemplateLibrary.isTemplateLibrary(n,nt))
		tempLib=new TemplateLibrary(store, URIClassifier.parseValidURI(n.getURI()));
	    else if (BookmarkLibrary.isBookmarkLibrary(n,nt))
		bookmarkLib=new BookmarkLibrary(store, URIClassifier.parseValidURI(n.getURI()));
	  }
      } catch(ComponentException e)
	{
	  throw new LibraryException("Could not load neuron type: \n" +
				     e.getMessage());
	}
      if (tempLib== null)
	  throw new LibraryException("Could not load root library, template library is missing: \n");
      if (bookmarkLib == null)
	  throw new LibraryException("Could not load root library, bookmark library is missing: \n");
    }
    
  //FIXME Does this belong here?
  public ClipboardLibrary getClipboardLibrary()
    {
	return clipboard;
    }
  public TemplateLibrary getTemplateLibrary()
    {
	return tempLib;
    }
   
  public BookmarkLibrary getBookmarkLibrary()
    {
	return bookmarkLib;
    }

   
  protected void checkLibrary() throws LibraryException
    {
      if(!isRootLibrary(libNeuron, libNeuronType))
	throw new LibraryException("Neuron was no root library: '" +
				   libNeuron.getURI() + "'.");
    }
  
  public static boolean isRootLibrary(Neuron n, NeuronType type)
    {
      String[] taxon = {"Library", "Generic", "Root"};
      
      return MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
					  "NeuronType", "Conzilla", taxon);
    }  
}

