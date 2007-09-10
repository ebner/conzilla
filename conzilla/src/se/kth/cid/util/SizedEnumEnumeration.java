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

/** This class implements a SizedEnumeration on an Enumeration
 *  of known size. Useful for example when using Hashtables.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class SizedEnumEnumeration implements SizedEnumeration
{
  /** The enumeration to use.
   */
  Enumeration enum;

  /** The (magically known) size of the enumeration.
   */
  int size;

  /** The latest object returned.
   */
  Object last;

  /** If we have successfully retrieved any elements.
   */
  boolean started;

  /** Constructs an enumeration.
   *  @param nenum the enumeration to use.
   *  @param nsize the size of this enumeration. Be careful to get this right!
   */
  public SizedEnumEnumeration(Enumeration nenum, int nsize)
    {
      enum = nenum;
      size = nsize;
      started = false;
    }

  public int getSize()
    {
      return size;
    }

  public Object nextElement()
    {
      last = enum.nextElement();

      started = true;
      return last;
    }

  public Object lastElement()
    {
      if(!started)
	throw new NoSuchElementException();
      return last;
    }

  public boolean hasMoreElements()
    {
      return enum.hasMoreElements();
    }

}
