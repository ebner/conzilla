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

/** This interface represents an external XML entity.
 *  Such an entity has three characteristics:
 *  public ID, system ID and possibly document type (for DTDs).
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface ExternalEntity
{

  /** Returns the public ID of this entity.
   *
   *  @return the public ID of this entity. May be null.
   */
  String getPublicID();

  /** Returns the system ID of this entity.
   *
   *  @return the system ID of this entity. May be null.
   */
  String getSystemID();

  /** Returns the document type of this entity.
   *
   *  This is only reasonable for external DTDs. Other entities will return null here.
   *
   *  @return the document type of this entity. May be null.
   */
  String getDocType();
  

  /** Returns the actual entity.
   *
   *  The object returned must be a Reader or an InputStream.
   *
   *  @return a Reader or an InputStream containing the entity.
   */
  Object getEntity();
}
