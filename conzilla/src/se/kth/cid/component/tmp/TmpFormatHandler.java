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


package se.kth.cid.component.tmp;

import se.kth.cid.component.*;
import se.kth.cid.component.local.*;

import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.neuron.local.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conceptmap.local.*;

import java.io.*;
import java.util.*;

/** 
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class TmpFormatHandler implements FormatHandler
{
  /** The "text/unknown" MIME type.
   */
  public final static MIMEType TMP = new MIMEType("text/unknown", true);

  /** Whether we should bother using the Netscape privilege manager.
   */
  public static boolean usePrivMan = false;

  /** Constructs an TmpFormatHandler.
   */
  public TmpFormatHandler()
  {
  }
  
  public MIMEType getMIMEType()
    {
	return TMP;
    }

  public void setComponentStore(ComponentStore store)
    {
    }

  public Container  loadContainer(URI uri, URI origuri)
    throws ComponentException
    {
	throw new ComponentException("Loading of temporary components not supported.");
    }     

  public Component  loadComponent(Container container, URI origuri)
    throws ComponentException
    {
	throw new ComponentException("Loading of temporary components not supported.");
    }	
     
  public Component loadComponent(URI uri, URI origuri)
    throws ComponentException
    {
	throw new ComponentException("Loading of temporary components not supported.");
    }
  
  public boolean isSavable(URI uri)
    {
	return false;
    }
  
  public void checkCreateComponent(URI uri) throws ComponentException
    {
	//ok!
    }
    
  public Component createComponent(URI uri, URI realURI, String type, Object extras)
    throws ComponentException
    {
	//FIXME: MIMEType should be what? unknown?
	if (type.equals(NEURON))
	    return new LocalNeuron(uri, realURI, TMP, (URI) extras);
	    //	    return new ProtegeNeuron(uri, realURI, TMP, (URI) extras);
	else if (type.equals(CONCEPTMAP))
	    return new LocalConceptMap(uri, realURI, TMP);
	else if (type.equals(COMPONENT))
	    return new LocalComponent(uri, realURI, TMP);
	else if (type.equals(NEURONTYPE))
	    return new LocalNeuronType(uri, realURI, TMP);
	throw new ComponentException("Cannot create a component of the type "+
				     type + ", doesn't know how!");
    }

  public void saveComponent(URI uri, Component comp)
    throws ComponentException
    {
	throw new ComponentException("Saving temporary components not implemented!");
    }

  /** Returns whether this formathandler can deal with a specified URI.
   */
  public boolean canHandleURI(URI uri)
    {
	return uri.getScheme().equals("tmp");
    }
	    
}
