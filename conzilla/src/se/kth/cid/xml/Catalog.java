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

import java.util.*;
import java.io.*;
import java.net.*;

/** This class is a catalog of external entities, including DTDs.
 *  It is used to find specific, known, external entities
 *  when they are encountered in an XML document.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 * 
 */
public class Catalog
{
  /** Maps pubID(String) --> ExternalEntity
   */
  Hashtable pubIDTable;

  /** Maps sysID(String) --> ExternalEntity
   */
  Hashtable sysIDTable;

  /** Maps docType(String) --> ExternalEntity
   */
  Hashtable docTypeTable;

  /** Constructs an empty catalog
   */
  public Catalog()
    {
      pubIDTable = new Hashtable();
      sysIDTable = new Hashtable();
      docTypeTable = new Hashtable();
    }

  /** Adds an antity to the catalog.
   *
   *  @param ent the entity to add.
   */
  public void addEntity(ExternalEntity ent)
    {
      if(ent.getPublicID() != null)
	pubIDTable.put(ent.getPublicID(), ent);
      if(ent.getSystemID() != null)
	sysIDTable.put(ent.getSystemID(), ent);
      if(ent.getDocType() != null)
	docTypeTable.put(ent.getDocType(), ent);
    }
  
  /** Returns the entity matching the given ID.
   *
   *  The entity is first looked for using the public ID, then
   *  the system ID and last, the docType. No check is done to ensure
   *  all fields match.
   * 
   *  @param pubID the public ID to look for. May be null.
   *  @param sysID the system ID to look for. May be null.
   *  @param docType the docType to look for. May be null.
   *  @return the external entity or null if none matched.
   */
  public ExternalEntity resolveEntity(String pubID, String sysID, String docType)
    {
      if(pubID != null)
	{
	  ExternalEntity ent = (ExternalEntity) pubIDTable.get(pubID);
	  if(ent != null)
	    return ent;
	}
      if(sysID != null)
	{
	  ExternalEntity ent = (ExternalEntity) sysIDTable.get(sysID);
	  if(ent != null)
	    return ent;
	}
      if(docType != null)
	{
	  ExternalEntity ent = (ExternalEntity) docTypeTable.get(docType);
	  if(ent != null)
	    return ent;
	}
      return null;
    }
}
