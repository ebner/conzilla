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
import java.util.*;

/** An implementation of Component to be used for components downloaded
 *  over the web.
 *  It is intended to be subclassed by the actual component implementations.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LocalComponent implements Component
{
  /** The URI of this component.
   */
  URI      componentURI;

  /** The editListeners of this component.
   */
  Vector editListeners;

  boolean isEditable = true;
  
  /** The edited state of this component.
   */
  boolean isEdited = false;

  /** The metadata of this component.
   */
  LocalMetaData metaData;
  
  /** Constructs a LocalComponent
   */
  public LocalComponent(URI uri)
  {
    editListeners = new Vector();
    componentURI = uri;
          
    metaData = new LocalMetaData(this);
  }

  public MetaData getMetaData()
  {
    return metaData;
  }  

  
  public String getURI()
  {
    return componentURI.toString();
  }

  public boolean isEditable()
  {
    return isEditable;
  }

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
  protected void fireEditEventNoEdit(EditEvent e)
  {
    //    Tracer.debug("FireEdit: " + e);
    for(int i = 0; i < editListeners.size(); i++)
      {
	((EditListener) editListeners.elementAt(i)).componentEdited(e);
      }
  }

  public URI tryURI(String uri) throws InvalidURIException
    {
      try {
	return URIClassifier.parseURI(uri, componentURI);
      } catch (MalformedURIException e)
	{
	  throw new InvalidURIException(e.getMessage(), uri);
	}
    }
}
