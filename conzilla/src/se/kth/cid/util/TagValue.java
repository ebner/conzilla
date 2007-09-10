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


package se.kth.cid.util;
import java.util.*;

/** Represents a collection of Tag = Value pairs.
 *  Each tag can have several values.
 *
 *  This class is useful in creation time manipulations...
 *
 *  @author  Matthias Palmer
 *  @version $Revision$
 */
public class TagValue 
{
  Hashtable tagValue;

  public TagValue()
    {
      tagValue=new Hashtable();
    }
  
  public Enumeration  getValues(String tag)
    {
      Vector vec=(Vector) tagValue.get(tag);
      if (vec==null)
	return (new Vector()).elements();
      else
	return vec.elements();
    }

  public void setValue(String tag, String value)
    {
      Vector vals=(Vector) tagValue.get(tag);
      if (vals==null)
	{
	  vals=new Vector();
	  vals.addElement(value);
	  tagValue.put(tag,vals);
	}
      else
	vals.addElement(value);
    }
  public boolean removeTag(String tag)
    {
      return tagValue.remove(tag)!=null;
    }
  
  public Enumeration getTags()
    {
      return tagValue.keys();
    }
}
