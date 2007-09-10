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


package se.kth.cid.component;
import se.kth.cid.identity.URI;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.util.*;
import java.util.*;
import java.net.*;
import java.io.*;

/** If a component can't be created due to missing directorys, 
 *  this is the exception created. 
 *  Observe that the path don't have to be the only problem.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class FilePathComponentException extends PathComponentException
{
  File file;
    
  /** 
   *
   * @param message the detail message.
   */
  public FilePathComponentException(String message, File file)
    {
	super(message);
	this.file=file;
    }

  public boolean makePath()
    {
      return (new File(file.getParent())).mkdirs();
    }

  public URI getPath()
    {
      try {
	return URIClassifier.parseValidURI(file.toURL().toString());
      } catch(MalformedURLException e)
	{
	  Tracer.error("Malformed File:\n " + e.getMessage());
	  return null; // never reached
	}
    }
  
  
}
