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

import se.kth.cid.util.*;
import se.kth.cid.component.*;


/** This exception is thrown by the format savers they do not know how to
 *  write to an URI with the specified scheme.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class UnsupportedSaveProtocolException extends FormatException
{
  /** The protocol (or scheme) that was unknown.
   */
  String protocol;

  /** Constructs an UnsupportedSaveProtocolException for the given protocol
   *  and with the given URI.
   *
   *  @param protocol the protocol that was unknown.
   *  @param uri the URI that specified the unknown protocol.
   */
  public UnsupportedSaveProtocolException(String protocol, URI uri)
  {
    super("Unsupported save protocol: " + protocol, uri);
    this.protocol = protocol;
  }

  /** Returns the unknown protocol.
   *
   *  @return the unknown protocol.
   */
  public String getProtocol()
  {
    return protocol;
  }
}

