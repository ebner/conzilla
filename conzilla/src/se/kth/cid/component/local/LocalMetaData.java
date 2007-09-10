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
import java.util.*;

/** A local and straightforward implementation of the MetaData interface.
 *  This class is used by LocalComponent.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LocalMetaData implements MetaData
{
  /** The tags in this metadata.
   */
  Vector tags;

  /** A cache of the tags in this metadata.
   */
  String[] tagsArray;
  
  /** The metadata table.
   *  Keys are Strings (tag), data are Strings (value).
   */
  Hashtable values;

  /** The component that owns this metadata. Needed to fire EditEvents.
   */
  LocalComponent owner;

  /** Constructs a LocalMetaData with the given component as owner.
   *
   *  @param owner the owner of this meta-data, to be used to fire EditEvents.
   *               May be null.
   */
  public LocalMetaData(LocalComponent owner)
  {
    values = new Hashtable();
    tags = new Vector();
    this.owner = owner;
  }

  public void setValue(String tag, String value) throws ReadOnlyException
    {
      if(owner != null && !owner.isEditable())
	throw new ReadOnlyException("This MetaData is Read-Only!");
      
      if(values.get(tag) != null)
	{
	  if(value != null)
	    {
	      values.put(tag, value);
	      maybeFireEditEvent(new EditEvent(owner,
					       Component.METADATATAG_EDITED,
					       tag));
	    }
	  else
	    {
	      values.remove(tag);
	      tags.removeElement(tag);
	      tagsArray = null;
	      maybeFireEditEvent(new EditEvent(owner,
					       Component.METADATATAG_REMOVED,
					       tag));
	    }
	}
      else if(value != null)
	{
	  values.put(tag, value);
	  tags.addElement(tag);
	  tagsArray = null;
	  maybeFireEditEvent(new EditEvent(owner,
					   Component.METADATATAG_ADDED,
					   tag));
	}    
    }

  /** Used to fire EditEvents if there is an owner.
   *
   *  Fires the specified EditEvent via the owner's fireEditEvent method, if the
   *  owner is non-null.
   *
   * @param e the event to fire.
   */
  void maybeFireEditEvent(EditEvent e)
  {
    if(owner != null)
      owner.fireEditEvent(e);
  }
  
  public String getValue(String tag)
    {
      return (String) values.get(tag);
    }
  
  public String[] getTags()
    {
      if(tagsArray == null)
	{
	  tagsArray = new String[tags.size()];
	  tags.copyInto(tagsArray);
	}
      return tagsArray;
    }
}
