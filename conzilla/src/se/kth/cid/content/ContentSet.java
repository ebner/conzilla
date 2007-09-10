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


package se.kth.cid.content;

import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;

import java.util.*;

/** This class wraps around a Neuron containing a number of "content" roles played
 *  by content-description neurons. It is in essence, a set of content-descriptions.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ContentSet
{
  /** The neuron containing the content set.
   */
  Neuron neuron;

  /** The ContentDescriptions contained in the Neuron.
   */
  Vector contents;

  /** The loader user to load the neurons.
   */
  ComponentLoader loader;

  /** Whether the neuron should be released on finalization.
   */
  boolean shouldRelease = false;

  /** Constructs a ContentSet from a given Neuron.
   *
   * @param n the neuron containing the content set.
   * @param loader the ComponentLoader used to retrieve the content description neurons.
   * @exception ComponentException if something goes wrong.
   */
  public ContentSet(Neuron n, ComponentLoader loader)
    throws ComponentException
  {
    this.loader = loader;
    setNeuron(n);
  }

  /** Constructs a ContentSet from a given Neuron URI.
   *
   * @param uri the URI of the neuron containing the content set.
   * @param loader the ComponentLoader used to retrieve the neurons.
   * @exception ComponentException if something goes wrong.
   */
  public ContentSet(URI uri, ComponentLoader loader)
    throws ComponentException
  {
    this.loader = loader;

    shouldRelease = true;
    Component comp = loader.loadComponent(uri, loader);
    if(! (comp instanceof Neuron))
      {
	loader.releaseComponent(comp);
	throw new ComponentException("Was no neuron: " + uri + "!");
      }
    setNeuron((Neuron) comp);
  }

  /** Initializes the fields in this class from the given Neuron.
   *
   *  @param n the Neuron to initialize from.
   *  @exception ComponentException if something goes wrong.
   */
  void setNeuron(Neuron n) throws ComponentException
  {
    neuron = n;

    contents = new Vector();

    Role[] contentRoles = neuron.getRolesOfType("content");

    try {
      for(int i = 0; i < contentRoles.length; i++)
	{
	  URI contenturi = new URI(contentRoles[i].neuronuri);
	  contents.addElement(new ContentDescription(contenturi, loader));
	}
    } catch (MalformedURIException e)
      {
	Tracer.trace("Role had invalid URI: " + e.getURI() + ": " + e.getMessage(),
		     Tracer.ERROR);
	throw new ComponentException("Role had invalid URI: " + e.getURI() + ":\n " + e.getMessage());
      }
  }

  /** Returns the ContentDescriptions in this ContentSet.
   *
   * @return the ContentDescriptions in this ContentSet.
   */
  public Vector getContents()
  {
    return contents;
  }

  /** Returns the Neuron containing this ContentSet.
   *
   * @return the Neuron containing this ContentSet.
   */
  public Neuron getNeuron()
  {
    return neuron;
  }

  protected void finalize()
  {
    if(shouldRelease)
      loader.releaseComponent(neuron);
  }
}
