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
import se.kth.cid.util.*;
import java.util.*;

/** An implementation of Component to be used for components downloaded over the web.
 *  It is intended to be subclasses by the actual component implementations.
 *  It always has isEditingPossible == true, but note that this does not mean it
 *  is savable.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LocalComponent implements Component
{
  /** The URI of this component.
   */
  URI      componentURI;

  /** The metadata of this component.
   */
  LocalMetaData metaData;

  /** The editability state of this component.
   */
  boolean  editable = true;

  /** The editListeners of this component.
   */
  Vector editListeners;

  /** The edited state of this component.
   */
  boolean isEdited = false;

  /** Constructs a LocalComponent
   */
  public LocalComponent()
  {
    metaData = new LocalMetaData(this);
    editListeners = new Vector();
  }


  public String  getURI()
  {
    if(componentURI != null)
      return componentURI.toString();
    else
      return null;
  }

  public void setURI(String uri) throws ReadOnlyException, MalformedURIException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    componentURI = new URI(uri);
    fireEditEvent(new EditEvent(this, URI_EDITED, uri));
  }  

  public MetaData getMetaData()
  {
    return metaData;
  }  

  /** Always returns true.
   *
   *  As LocalComponents always may be edited, this always returns true.
   */
  public boolean isEditingPossible()
  {
    return true;
  }
  
  public void setEditable(boolean edit)
  {
    if(edit == editable)
      return;
    editable = edit;

    fireEditEventNoEdit(new EditEvent(this, EDITABLE_CHANGED, null));
  }
  
  public boolean isEditable()
  {
    return editable;
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


  /** Fires an EditEvent to all listeners and marks the component as being edited.
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
}
