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
import se.kth.cid.util.*;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.identity.URI;
import java.util.*;
import java.net.*;

/** If a component can't be created due to missing directorys, 
 *  this is the exception created. 
 *  Observe that the path don't have to be the only problem.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class FtpPathComponentException extends PathComponentException
{
  public FtpHandler handler;
  public URL url;
  public List [] list;
    
  /** 
   *
   * @param message the detail message.
   */
  public FtpPathComponentException(String message, FtpHandler handler, List[] list, URL url)
    {
	super(message);
	this.handler=handler;
	this.list=list;
	this.url=url;
    }
  public boolean makePath()
    {
	List li=handler.makePathValid(url, list[0], list[1]);
	return li.size()==list[1].size();	    
    }

  public URI getPath()
    {
      return URIClassifier.parseValidURI(url.toString());
    }
  
}

