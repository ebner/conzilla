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

/** This class works as a wrapper around Neurons that are content-descriptions,
 *  i.e. of the type "cid:cid.kth.se/neurontype/contentdescription".
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ContentDescription
{
  /** The neuron type for content description neurons.
   */
  public static String contentDescriptionTypeURI
    = "cid:standard/nt/contentdescription";
  public static String contentDescriptionTypeURIProbable
    = "/contentdescription";
  
  
  /** The MIME type of this content.
   */
  MIMEType mimeType;

  /** The URI of this content.
   */
  URI      contentURI;

  /** The URI of this content description.
   */
  URI      descriptionURI;
  
  /** The loader used to retrieve (and later release) the component.
   */
  ComponentLoader loader;

  /** The neuron that is the content description.
   */
  Neuron neuron;

  /** Constructs a ContentDescription from a Neuron.
   *
   *  @param n the neuron that is a content description.
   *  @exception ComponentException if the neuron is no ContentDescription.
   */
  public ContentDescription(Neuron n) throws ComponentException
  {
    setNeuron(n);
  }

  /** Loads a ContentDescription from its URI.
   *
   *  @param uri the uri of the neuron that is a content description.
   *  @param loader the ComponentLoader to use for loading and releasing the Neuron.
   *  @exception ComponentException if the neuron is no ContentDescription.
   */
  public ContentDescription(URI uri, ComponentLoader loader)
    throws ComponentException
  {
    this.loader = loader;
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
   * @param n the Neuron to initialize from.
   *  @exception ComponentException if the neuron is no ContentDescription.
   */
  void setNeuron(Neuron n) throws ComponentException
  {
    neuron = n;

    try {
      descriptionURI = new URI(n.getURI());
    } catch(MalformedURIException e)
      {
	Tracer.trace("Neuron had illegal URI: " + n.getURI() + "!", Tracer.ERROR);
	throw new ComponentException("Neuron had illegal URI: " + n.getURI() + "!");
      }


    if(neuron.getType().indexOf(contentDescriptionTypeURIProbable)==-1)
      {
	if(loader != null)
	  loader.releaseComponent(n);
	throw new ComponentException("Was no content-description: "
				     + descriptionURI +"!");
      }
    String[] content = neuron.getDataValues("URI");
    if(content.length != 1)
      {
	if(loader != null)
	  loader.releaseComponent(n);
	throw new IllegalComponentException("content-description " +
					    descriptionURI + " had "
					    + content.length + " URIs!");
      }
    String[] type    = neuron.getDataValues("MIMEType");
    if(type.length != 1)
      {
	if(loader != null)
	  loader.releaseComponent(n);
	throw new IllegalComponentException("content-description had "
					    + type.length + " MIMETypes!");
      }
    try {
      contentURI = new URI(content[0]);
      mimeType   = new MIMEType(type[0]);
    }
    catch(MalformedURIException e)
      {
	throw new IllegalComponentException("Invalid content URI in ContentDescription: "
					    + e.getURI() + ":\n " + e.getMessage());
      }
    catch(MalformedMIMETypeException e)
      {
	throw new IllegalComponentException("Invalid content MIMEType in ContentDescription: "
					    + e.getType() + ":\n " + e.getMessage());
      }
  }

  /** Returns the MIME type of the content.
   *
   * @return the MIME type of the content.
   */
  public MIMEType getContentType()
  {
    return mimeType;
  }

  /** Returns the URI of the content.
   *
   * @return the URI of the content.
   */
  public URI getContentURI()
  {
    return contentURI;
  }

  /** Returns the URI of this content-description.
   *
   * @return the URI of this content-description.
   */
  public URI getURI()
  {
    return descriptionURI;
  }

  /** Returns the Neuron that contains this content-description.
   *
   * @return the Neuron that contains this content-description.
   */
  public Neuron getNeuron()
  {
    return neuron;
  }

  protected void finalize()
  {
    if(loader != null)
      loader.releaseComponent(neuron);
  }
}
