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

/** Implements a SizedEnumeration on a vector.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class SizedVectorEnumeration implements SizedEnumeration
{
  /** The vector to use.
   */
  Vector v;

  /** The current index.
   */
  int index;

  /** The last element retrieved.
   */
  Object lastEl;

  /** Constructs an enumeration from the given vector.
   *
   *  @param nv the Vector to use.
   */
  public SizedVectorEnumeration(Vector nv)
    {
      v = nv;
      index = -1;
    }

  public int getSize()
    {
      return v.size();
    }

  public Object nextElement()
    {
      try {
	lastEl = v.elementAt(index + 1);
      } catch(ArrayIndexOutOfBoundsException e)
	{
	  throw new NoSuchElementException();
	}
      index++;
      return lastEl;
    }

  public Object lastElement()
    {
      if(index == -1)
	throw new NoSuchElementException();
      return lastEl;
    }

  public boolean hasMoreElements()
    {
      return v.size() > 0 && index + 1 < v.size();
    }

}
