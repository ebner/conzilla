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


package se.kth.cid.xml;

/** Indicates that the strucure of the xmldocument isn't what was expected.
 *  I.e a missmatch between the actual document with the DTD, schema or 
 *  whatever structure expected. (Could even be an hardcoded structure!)
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class XmlStructureException extends Exception
{
  /** Constructs a XmlStructureException with the specified detail message.
   *
   * @param message the detail message.
   */
  public XmlStructureException(String message)
    {
      super(message);
    }
}




