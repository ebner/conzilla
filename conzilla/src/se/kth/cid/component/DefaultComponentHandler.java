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

package se.kth.cid.component;

import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.util.*;
import java.util.*;


/** The default implementation of the ComponentHandler interface.
 *  This implementation features recursive Path URN resolving and data
 *  format multiplexing.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class DefaultComponentHandler implements ComponentHandler
{
  static final String errorIndent = "    ";
  static final String subErrorIndent = "        ";

  /** The resolver to use.
   */
  PathURNResolver resolver;

  /** Table mapping MIME type -> FormatHandler.
   */
  Hashtable formatHandlers;

  /** This class represents a URI resolving result.
   */
  class TypedURI
  {
    /** The resolved URI.
     */
    URI uri;

    /** The type used by the corresponding path.
     */
    MIMEType type;

    /** The handler used for the above path.
     */
    FormatHandler handler;
    
    TypedURI(URI uri, MIMEType type, FormatHandler handler)
      {
	this.uri = uri;
	this.type = type;
	this.handler = handler;
      }
  }
  
  /** Constructs a DefaultComponentHandler using the given resolver.
   *
   *  @param resolver the resolver to use.
   */
  public DefaultComponentHandler(PathURNResolver resolver)
    {
      this.resolver = resolver;
      formatHandlers = new Hashtable();
    }

    public void setComponentStore(ComponentStore store)
    {
	Iterator it = formatHandlers.values().iterator();
	while (it.hasNext())
	    ((FormatHandler) it.next()).setComponentStore(store); 
    }

  /** Adds a Formathandler to handle the given data format.
   *
   *  @param f the handler for this format.
   */
  public void addFormatHandler(FormatHandler f)
    {
      formatHandlers.put(f.getMIMEType(), f);
    }

  String makeError(String title, StringBuffer errorList)
    {
      return title + "\n Tried the following:\n" + errorList;
    }
  
  String formatError(String error, String subError)
    {
      StringBuffer errorBuf = new StringBuffer("    ");
      errorBuf.append(error);
      if(subError == null)
	return errorBuf.toString();

      errorBuf.append(": \n        ");
      int length = errorBuf.length();
      errorBuf.append(subError);

      int i = 0;
      int index = subError.indexOf('\n');

      while(index != -1)
	{
	  int replInd = length + index + i*8;
	  errorBuf.replace(replInd, replInd + 1, "\n        ");
	  index = subError.indexOf('\n', index + 1);
	  i++;
	}
      
      return errorBuf.toString() + '\n';
    }
    

    public Container loadContainer(URI uri)
    throws ComponentException
    {
	return (Container) loadCoImpl(uri, true);
    }

  public Component loadComponent(URI uri, Container container)
    throws ComponentException
    {
	return loadCoImpl(uri, false);
    }

    protected Component loadCoImpl(URI uri, boolean container)
	throws ComponentException
    {
      StringBuffer errors = new StringBuffer();
      TypedURI[] typeURIs = extractUsableURIs(uri, errors);

      for(int i = 0; i < typeURIs.length; i++)
	{
	  try {
	      if (container)
		  return typeURIs[i].handler.loadContainer(typeURIs[i].uri, uri);
	      else
		  return typeURIs[i].handler.loadComponent(typeURIs[i].uri, uri);
	  } catch (ComponentException e)
	      {
		  errors.append(formatError("Error loading from '" + typeURIs[i].uri + "'",
					    e.getMessage()));
	      }
	}
      throw new ComponentException(makeError("Failed loading of '" + uri + "'.", errors));
    }
  
    //If this uri has a fragment, try load from it's containers formathandler.
    /*	if (container != null)
	{
		try {
		    MIMEType mt = new MIMEType(container.getLoadMIMEType());
		    FormatHandler fh = (FormatHandler) formatHandlers.get(mt);
		    if (fh != null)
			return fh.loadComponent(container, uri);
		    else
			errors.append(formatError("The MIMEType ("+mt.toString()+") of the container, "+container.getURI()+
				   " ,has no formathandler to take care of it!!", ""));
		} catch (MalformedMIMETypeException mmte)
		    {
			errors.append(formatError("The MIMEType of this container is invallid!!!!",mmte.getMessage()));
		    }
		    }*/

  public Object[] checkCreateComponent(URI uri) throws ComponentException
    {
      StringBuffer errors = new StringBuffer();
      TypedURI[] typeURIs = extractUsableURIs(uri, errors);

      for(int i = 0; i < typeURIs.length; i++)
	{
	  try {
	    typeURIs[i].handler.checkCreateComponent(typeURIs[i].uri);
	    return new Object[] {typeURIs[i].uri, typeURIs[i].type};
	    
	  } catch (PathComponentException pce) {   //A path problem can probably be fixed, 
	      throw pce;                           //so the exception is rethrown to higher levels.
	  }catch (ComponentException e)    //A regular ComponentException is unrecoverable. 
	    {                              //Therefore the exception is stacked and the looping continues.
	      errors.append(formatError("Cannot be created at '" + typeURIs[i].uri + "'",
					e.getMessage()));
	    }
	}
      throw new ComponentException(makeError("Cannot create '" + uri + "'", errors));
    }

  Component createComponentImpl(URI uri, URI createURI, MIMEType mimetype, String type, Object extras)  throws ComponentException
    {
      FormatHandler handler = (FormatHandler) formatHandlers.get(mimetype);
      
      if(handler == null)
	Tracer.bug("Cannot create component. Unknown handler for: " + mimetype);
      
      return handler.createComponent(createURI, uri, type, extras);
    }
  
  public Component createComponent(URI compURI, URI createURI, MIMEType mimetype) throws ComponentException
    {
      return createComponentImpl(compURI, createURI, mimetype, FormatHandler.COMPONENT, null);
    }

  public Neuron createNeuron(URI compURI, URI createURI, MIMEType mimetype, URI typeURI) throws ComponentException
    {
      return (Neuron) createComponentImpl(compURI, createURI, mimetype, FormatHandler.NEURON, typeURI);
    }

  public NeuronType createNeuronType(URI compURI, URI createURI, MIMEType mimetype) throws ComponentException
    {
      return (NeuronType) createComponentImpl(compURI, createURI, mimetype, FormatHandler.NEURONTYPE, null);
    }

  public ConceptMap createConceptMap(URI compURI, URI createURI, MIMEType mimetype) throws ComponentException
    {
      return (ConceptMap) createComponentImpl(compURI, createURI, mimetype, FormatHandler.CONCEPTMAP, null);
    }

  public void saveComponent(Component comp)
    throws ComponentException
    {
      URI uri       = URIClassifier.parseValidURI(comp.getLoadURI());
      MIMEType type = null;

      try {
	type = new MIMEType(comp.getLoadMIMEType());
      } catch(MalformedMIMETypeException e)
	{
	  Tracer.bug("Malformed MIME type in component:" + e.getMessage());
	}
      

      FormatHandler handler = (FormatHandler) formatHandlers.get(type);
      
      if(handler == null)
	Tracer.bug("Cannot save component. Unknown handler!");

      
      handler.saveComponent(uri, comp);
      comp.setEdited(false);
    }

  /** Creates a list of possibly usable URIs from an attempt to resolve the URI.
   *
   * @param uri the URI to resolve.
   * @param extras errors an error message where the reason for failure on each found URI,
   *        that did not make it to the list, is listed.
   * @return the usable URIs
   */
  protected TypedURI[] extractUsableURIs(URI uri, StringBuffer errors)
    {
      List resultList = new Vector();

      if(uri instanceof PathURN)
	{
	  ResolveResult[] res;
	  try{
	    res = resolver.resolve((PathURN) uri);
	  } catch (ResolveException e)
	    {
	      errors.append(formatError("Error in resolving '" + uri + "'", e.getMessage()));
	      return new TypedURI[0];
	    }
	  
	  for(int i = 0; i < res.length; i++)
	    addLocation(res[i].uri, res[i].type, resultList, errors);
	}
      else
	  {
	      Iterator it = formatHandlers.values().iterator();
	      while (it.hasNext())
		  {
		      FormatHandler fh = (FormatHandler) it.next();
		      if (fh.canHandleURI(uri))			  
			  resultList.add(new TypedURI(uri, fh.getMIMEType(), fh));
		  }
	  }

      if(resultList.size() == 0)
        errors.append(formatError("Could not resolve '" + uri + "'", null));
      
      return (TypedURI[]) resultList.toArray(new TypedURI[resultList.size()]);
    }

    
    //FIXME: This function is unneccessary, if it works. Remove it.
  /** Determines whether the given resolved URI is usable. Adds it to the list if it is.
   *  Usability depends on whether we have a handler for the the data format.
   *
   *  @param uri the resolved URI.
   *  @param type the resolved type of the path. If null, may be guessed.
   *  @param resultList the list to which results are added.
   *  @param errors the list of errors.
   */
  protected void addLocation(URI uri, MIMEType type, List resultList, StringBuffer errors)
    {
	if(type == null) // Have to guess type
	    if(uri.getScheme().equals("http") || uri instanceof FileURL)
		type = MIMEType.XML;
	    else
		type = MIMEType.XML; // Until better idea
	
      FormatHandler handler = (FormatHandler) formatHandlers.get(type);
	  
      if(handler == null)
	errors.append(formatError("Unsupported component type '" + type + "', used by '" + uri, null));
      else
	resultList.add(new TypedURI(uri, type, handler));
    }
}

