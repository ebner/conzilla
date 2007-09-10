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

/** This interface extends java.lang.Enumeration with methods
 *  for querying the enumeration for the number of elements in total,
 *  and for the last element returned.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface SizedEnumeration extends Enumeration
{
  /** Returns the total size of this enumeration.
   *  @return the total size of this enumeration.
   */
  int getSize();

  /** Returns the last element returned by nextElement.
   *  If this method is called before nextElement has been successfully called,
   *  a NoSuchElementException is thrown. After that, it will always return the
   *  latest succesfully retrieved element, even if nextElement() fails (when
   *  the Enumeration has ended).
   *  @return the last successfully retrieved element.
   */
  Object lastElement();
}
