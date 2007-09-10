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


package se.kth.cid.component.local;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import java.util.*;

/** An implementation of Component to be used for components downloaded
 *  over the web.
 *  It is intended to be subclassed by the different component implementations.
 *
 *  @author Mikael Nilsson
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class GenericComponent implements FiringComponent
{
  /** The URI of this component.
   */
  URI      componentURI;

  /** The URI used to load this component.
   */
  protected URI      componentLoadURI;

  /** The MIME type used when loading this component.
   */
  protected MIMEType componentLoadMIMEType;

  /** The editListeners of this component.
   */
  Vector editListeners;

  /** Whether this component is editable.
   */
  boolean isEditable = true;
  
  /** The edited state of this component.
   */
  boolean isEdited = false;

  /** Constructs a GenericComponent
   */
  protected GenericComponent(URI uri, URI loadURI, MIMEType loadType)
    {
      editListeners         = new Vector();
      componentURI          = uri;
      componentLoadURI      = loadURI;
      componentLoadMIMEType = loadType;
    }
  
  public abstract MetaData getMetaData();

  
  public String getURI()
    {
      return componentURI.toString();
    }
  
  public String getLoadURI()
    {
      return componentLoadURI.toString();
    }

  public String getLoadMIMEType()
    {
      return componentLoadMIMEType.toString();
    }

  public boolean isEditable()
  {
    return isEditable;
  }

  /** Sets the editable state of this Component. To be used with extreme care;
   *  this state is not expected to change. This function is intended to be
   *  used exclusively in the construction phase of a LocalComponent.
   *
   * @param editable the new editable state.
   */
  public void setEditable(boolean editable)
    {
      isEditable = editable;
    }
  

  public boolean isEdited()
  {
    return isEdited;
  }

  public void setEdited(boolean b) throws ReadOnlyException
  {
    if(!isEditable())
      throw new ReadOnlyException("This component is read-only");

    isEdited = b;
    if(isEdited)
      fireEditEventNoEdit(new EditEvent(this, this, EDITED, null));
    else
      fireEditEventNoEdit(new EditEvent(this, this, SAVED, null));
  }
  
  public void addEditListener(EditListener l)
  {
    editListeners.addElement(l);
  }

  public void removeEditListener(EditListener l)
  {
    editListeners.removeElement(l);
  }


  /** Fires an EditEvent to all listeners and marks the
   *  component as being edited.
   *
   *  @param e the event to fire.
   */
  public void fireEditEvent(EditEvent e)
  {
    isEdited = true;
    fireEditEventNoEdit(e);
  }

  /** Fires an EditEvent to all listeners without marking the component as being edited.
   *
   *  @param e the event to fire.
   */
  public void fireEditEventNoEdit(EditEvent e)
  {
    //    Tracer.debug("Listeners: " + editListeners.size());
    for(int i = 0; i < editListeners.size(); i++)
      {
	((EditListener) editListeners.elementAt(i)).componentEdited(e);
      }
  }

  /** Tries to parse a URI using this Component's URI as base URI.
   *
   *  @param uri the URI to parse.
   *  @exception InvalidURIException if the URI did not parse.
   */
  public URI tryURI(String uri) throws InvalidURIException
    {
      try {
	return URIClassifier.parseURI(uri, componentURI);
      } catch (MalformedURIException e)
	{
	  throw new InvalidURIException(e.getMessage(), uri);
	}
    }

  /** Constructs a new ID.
   *
   *  The ID is unique with respect to the Strings in the given
   *  Collection (which must not be infinite).
   *
   *  @param uniques the existing ID to not use.
   *  @param uriBase an uri (or any string) to use as base.
   *                 anything after the last '/' will be used.
   *
   *  @return a new unique ID.
   */
  public static String createID(Collection uniques, String uriBase)
    {
      String idBase = "id";
      
      if(uriBase != null)
	{
	  int lastSlash = uriBase.lastIndexOf('/');
	  
	  if(lastSlash + 1 < uriBase.length())
	    idBase = uriBase.substring(lastSlash + 1);
	}
      
      if(!(uniques.contains(idBase)))
	return idBase;
      
      for(int i = 1; true; i++)
	{
	  String s = idBase + i;
	  if(!(uniques.contains(s)))
	    return s;
	}  
    }
}
