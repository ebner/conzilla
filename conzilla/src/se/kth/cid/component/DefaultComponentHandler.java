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


/** ComponentLoader are used to locate and connect with/retrieve
 *  different types of components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class DefaultComponentHandler implements ComponentHandler
{
  PathURNResolver resolver;

  Hashtable formatHandlers;

  class TypedURI
  {
    URI uri;
    MIMEType type;

    TypedURI(URI uri, MIMEType type)
      {
	this.uri = uri;
	this.type = type;
      }
  }
  

  public DefaultComponentHandler(PathURNResolver resolver)
    {
      this.resolver = resolver;
      formatHandlers = new Hashtable();
    }

  public void addFormatHandler(MIMEType type, FormatHandler f)
    {
      formatHandlers.put(type, f);
    }
  
  public Component loadComponent(URI uri)
    throws ComponentException
    {
      boolean isSavable = isSavable(uri);
      
      List errors = new Vector();
      List uriTypeList = new Vector();
      addLocation(uri, null, uriTypeList, errors);

      for(int i = 0; i < uriTypeList.size(); i++)
	{
	  TypedURI tu = (TypedURI) uriTypeList.get(i);
	  
	  FormatHandler handler = (FormatHandler) formatHandlers.get(tu.type);
      
	  if(handler == null)
	    errors.add("Unsupported component type '"
		       + tu.type + "', used by '" + tu.uri +
		       "' when resolving '" + uri + "'");
	  else
	    try {
	      return handler.loadComponent(tu.uri, uri, isSavable);
	    } catch (ComponentException e)
	      {
		errors.add("Error loading component '" + uri +
			   "' from '" + tu.uri + "': \n" + e.getMessage());
	      }
	}
      String error = "Failed loading of '" + uri + "'. Tried: \n";
      for(int i = 0; i < errors.size(); i++)
	error = error + errors.get(i) + "\n";
      throw new ComponentException(error);
    }  

  boolean isSavable(URI uri)
    {
      List errors = new Vector();
      List uriTypeList = new Vector();

      addLocation(uri, null, uriTypeList, errors);

      for(int i = 0; i < uriTypeList.size(); i++)
	{
	  TypedURI tu = (TypedURI) uriTypeList.get(i);
	  
	  FormatHandler handler = (FormatHandler) formatHandlers.get(tu.type);
      
	  if(handler != null && handler.isSavable(tu.uri))
	    return true;
	}
      return false;
    }
  
  
  public boolean canCreateComponent(URI uri) throws ComponentException
    {
      List errors = new Vector();
      List uriTypeList = new Vector();


      addLocation(uri, null, uriTypeList, errors);

      for(int i = 0; i < uriTypeList.size(); i++)
	{
	  TypedURI tu = (TypedURI) uriTypeList.get(i);
	  
	  FormatHandler handler = (FormatHandler) formatHandlers.get(tu.type);
      
	  if(handler == null)
	    errors.add("Unsupported component type '"
		       + tu.type + "', used by '" + tu.uri +
		       "' when resolving '" + uri + "'");
	  else
	    try {
	      return handler.canCreateComponent(tu.uri);
	    } catch (ComponentException e)
	      {
		errors.add("Error testing creatability for '" + uri
			   +  "' to '" + tu.uri + "': \n" + e.getMessage());
	      }
	}
      String error = "Failed testing creatability of '" + uri + "'. Tried: \n";
      for(int i = 0; i < errors.size(); i++)
	error = error + errors.get(i) + "\n";
      throw new ComponentException(error);
    }


  public Component createComponent(URI compURI) throws ComponentException
    {
      List errors = new Vector();
      List uriTypeList = new Vector();

      addLocation(compURI, null, uriTypeList, errors);

      for(int i = 0; i < uriTypeList.size(); i++)
	{
	  TypedURI tu = (TypedURI) uriTypeList.get(i);
	  
	  FormatHandler handler = (FormatHandler) formatHandlers.get(tu.type);
      
	  if(handler == null)
	    errors.add("Unsupported component type '"
		       + tu.type + "', used by '" + tu.uri +
		       "' when resolving '" + compURI + "'");
	  else
	    try {
	      return handler.createComponent(tu.uri, compURI);
	    } catch (ComponentException e)
	      {
		errors.add("Error creating '" + compURI +  "' at '"
			   + tu.uri + "': \n" + e.getMessage());
	      }
	}
      String error = "Failed creating '" + compURI + "'. Tried: \n";
      for(int i = 0; i < errors.size(); i++)
	error = error + errors.get(i) + "\n";
      throw new ComponentException(error);
    }


  public Neuron createNeuron(URI neuronURI, URI typeURI) throws ComponentException
    {
      List errors = new Vector();
      List uriTypeList = new Vector();

      addLocation(neuronURI, null, uriTypeList, errors);

      for(int i = 0; i < uriTypeList.size(); i++)
	{
	  TypedURI tu = (TypedURI) uriTypeList.get(i);
	  
	  FormatHandler handler = (FormatHandler) formatHandlers.get(tu.type);
      
	  if(handler == null)
	    errors.add("Unsupported component type '"
		       + tu.type + "', used by '" + tu.uri +
		       "' when resolving '" + neuronURI + "'");
	  else
	    try {
	      return handler.createNeuron(tu.uri, neuronURI, typeURI);
	    } catch (ComponentException e)
	      {
		errors.add("Error creating '" + neuronURI +  "' at '"
			   + tu.uri + "': \n" + e.getMessage());
	      }
	}
      String error = "Failed creating '" + neuronURI + "'. Tried: \n";
      for(int i = 0; i < errors.size(); i++)
	error = error + errors.get(i) + "\n";
      throw new ComponentException(error);
    }

  public NeuronType createNeuronType(URI neuronTypeURI) throws ComponentException
    {
      List errors = new Vector();
      List uriTypeList = new Vector();

      addLocation(neuronTypeURI, null, uriTypeList, errors);

      for(int i = 0; i < uriTypeList.size(); i++)
	{
	  TypedURI tu = (TypedURI) uriTypeList.get(i);
	  
	  FormatHandler handler = (FormatHandler) formatHandlers.get(tu.type);
      
	  if(handler == null)
	    errors.add("Unsupported component type '"
		       + tu.type + "', used by '" + tu.uri +
		       "' when resolving '" + neuronTypeURI + "'");
	  else
	    try {
	      return handler.createNeuronType(tu.uri, neuronTypeURI);
	    } catch (ComponentException e)
	      {
		errors.add("Error creating '" + neuronTypeURI +  "' at '"
			   + tu.uri + "': \n" + e.getMessage());
	      }
	}
      String error = "Failed creating '" + neuronTypeURI + "'. Tried: \n";
      for(int i = 0; i < errors.size(); i++)
	error = error + errors.get(i) + "\n";
      throw new ComponentException(error);
    }
  
  public ConceptMap createConceptMap(URI mapURI) throws ComponentException
    {
      List errors = new Vector();
      List uriTypeList = new Vector();

      addLocation(mapURI, null, uriTypeList, errors);

      for(int i = 0; i < uriTypeList.size(); i++)
	{
	  TypedURI tu = (TypedURI) uriTypeList.get(i);
	  
	  FormatHandler handler = (FormatHandler) formatHandlers.get(tu.type);
      
	  if(handler == null)
	    errors.add("Unsupported component type '"
		       + tu.type + "', used by '" + tu.uri +
		       "' when resolving '" + mapURI + "'");
	  else
	    try {
	      return handler.createConceptMap(tu.uri, mapURI);
	    } catch (ComponentException e)
	      {
		errors.add("Error creating '" + mapURI +  "' at '"
			   + tu.uri + "': \n" + e.getMessage());
	      }
	}
      String error = "Failed creating '" + mapURI + "'. Tried: \n";
      for(int i = 0; i < errors.size(); i++)
	error = error + errors.get(i) + "\n";
      throw new ComponentException(error);
    }
  
  
  public void saveComponent(Component comp)
    throws ComponentException
    {
      List errors = new Vector();
      List uriTypeList = new Vector();
      URI uri;
      try {
	uri = URIClassifier.parseURI(comp.getURI());
      } catch (MalformedURIException e)
	{
	  throw new ComponentException("Malformed URI '" + e.getURI() +"': \n"
				       + e.getMessage());
	}
      
      addLocation(uri, null, uriTypeList, errors);

      for(int i = 0; i < uriTypeList.size(); i++)
	{
	  TypedURI tu = (TypedURI) uriTypeList.get(i);
	  
	  FormatHandler handler = (FormatHandler) formatHandlers.get(tu.type);
      
	  if(handler == null)
	    errors.add("Unsupported component type '"
		       + tu.type + "', used by '" + tu.uri +
		       "' when resolving '" + uri + "'");
	  else
	    try {
	      handler.saveComponent(tu.uri, comp);
	      return;
	    } catch (ComponentException e)
	      {
		errors.add("Error saving for '" + uri +  "' to '" +
			   tu.uri + "': \n" + e.getMessage());
	      }
	}
      String error = "Failed saving '" + uri + "'. Tried: \n";
      for(int i = 0; i < errors.size(); i++)
	error = error + errors.get(i) + "\n";
      throw new ComponentException(error);
    }

  protected void resolveURN(PathURN urn, List uritypeList, List errors)
    {
      ResolveResult[] res;
      try{
	res = resolver.resolve(urn);
      } catch (ResolveException e)
	{
	  errors.add("Error in resolving '" + urn + "':\n "
		     + e.getMessage());
	  return;
	}
      
      for(int i = 0; i < res.length; i++)
	addLocation(res[i].uri, res[i].type, uritypeList, errors);
    }

  protected void addLocation(URI uri, MIMEType type, List uritypeList, List errors)
    {
      if(uri instanceof PathURN)
	{
	  resolveURN((PathURN) uri, uritypeList, errors);
	  return;
	}
      else
	if(uri instanceof URL && uri.getScheme().equals("http")
	   || uri instanceof FileURL)
	  {
	    if(type == null) // Have to guess type
	      {
		try {
		  type = new MIMEType("text/xml");
		} catch(MalformedMIMETypeException e)
		  {
		    Tracer.trace("'text/xml' was no MIME type!", Tracer.ERROR);
		    errors.add("'text/xml' was no MIME type!");
		  }
	      }
	    uritypeList.add(new TypedURI(uri, type));
	  }
      else
	errors.add("Unsupported URI scheme in '" + uri + "'.");
      
    }

}

