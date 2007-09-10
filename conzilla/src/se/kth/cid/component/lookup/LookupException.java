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


package se.kth.cid.component.lookup;

import se.kth.cid.component.*;
import se.kth.cid.util.*;

/** This exception is thrown during URI lookup if anything goes wrong.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LookupException extends ComponentException
{
  /** The URI that caused problems.
   */
  URI uri;

  /** Constructs a LookupException with the specified detail message and the given URI.
   *
   * @param message the detail message.
   * @param uri the URI which caused problems.
   */
  public LookupException(String message, URI uri)
    {
      super(message);
      this.uri = uri;
    }

  /** Returns the URI that caused problems.
   *
   * @return the URI that caused problems.
   */
  public URI getURI()
  {
    return uri;
  }
  
}

