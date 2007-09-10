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

package se.kth.cid.conzilla.content;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.URI;
import se.kth.cid.identity.PathURN;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.conzilla.util.*;
import java.applet.*;
import java.net.*;
import java.util.*;

public abstract class BrowserContentDisplayer extends AbstractContentDisplayer
{
  protected PathURNResolver resolver;
  
  public BrowserContentDisplayer(PathURNResolver res)
    {
      this.resolver = res;
    }
  
  public void setContent(Component c) throws ContentException
    {
      if(c == null)
	{
	  super.setContent(null);
	  return;
	}

      Tracer.debug("Browser will show " + c.getURI());
      
      URI baseuri=URIClassifier.parseValidURI(c.getURI());
      URL[] urls = extractUsableURLs(MetaDataUtils.getLocations(c.getMetaData().get_technical_location(), baseuri));

      boolean fail = true;
      for(int i = 0; i < urls.length && fail; i++)
	{
	  try {
	    fail = ! showDocument(urls[i]);
	  }
	  catch(ContentException e)
	    {
	      throw new ContentException(e.getMessage(), c);
	    }
	}
      if(fail)
	throw new ContentException("No valid location for content found!", c);
      else
	super.setContent(c);
    }

  protected abstract boolean showDocument(URL url) throws ContentException;

  
  protected URL[] extractUsableURLs(URI[] uri)
    {
      List resultList = new Vector();

      for(int i = 0; i < uri.length; i++)
	{
	  if(uri[i] instanceof PathURN)
	    {
	      ResolveResult[] res;
	      try {
		res = resolver.resolve((PathURN) uri[i]);
		for(int j = 0; j < res.length; j++)
		  try {
		      resultList.add(new URL(res[j].uri.toString()));
		  } catch(MalformedURLException e)
		    {
		      Tracer.trace("Malformed URL '" + res[j].uri + "':\n " +
				   e.getMessage(), Tracer.WARNING);
		    }
	      } catch (ResolveException e)
		{
		  Tracer.trace("Error in resolving '" + uri[i] + "':\n " +
			       e.getMessage(), Tracer.WARNING);
		}
	    }
	  else
	    try {
	      resultList.add(new URL(uri[i].toString()));
	    } catch(MalformedURLException e)
	      {
		Tracer.trace("Malformed URL '" + uri[i] + "':\n " +
			     e.getMessage(), Tracer.WARNING);
	      }
	}
      return (URL[]) resultList.toArray(new URL[resultList.size()]);
    }

}
