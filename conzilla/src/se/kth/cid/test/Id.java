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



package se.kth.cid.test;

import se.kth.cid.util.Tracer;
import se.kth.cid.identity.*;

import se.kth.cid.identity.*;

class Id
{
  public static void main(String[] argv)
    throws Exception
    {
      if(argv.length < 2)
	{
	  Tracer.trace("Usage: Id url relurl [LogLevel]",
		       Tracer.ERROR);
	  System.exit(-1);
	}
      
      if(argv.length > 2)
	Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
      else
	Tracer.setLogLevel(Tracer.NONE);

      URI parsedURI = URIClassifier.parseURI(argv[0], null);
      printURI(parsedURI);

      
      printURI(URIClassifier.parseURI(argv[1], parsedURI));
    }

  public static void printURI(URI parsedURI)
    {
      Tracer.debug("Scheme: " + parsedURI.getScheme());
      Tracer.debug("Fragment: " + parsedURI.getFragment());
      if(parsedURI instanceof URL)
	{
	  Tracer.debug("Host: " + ((URL) parsedURI).getHost());
	  Tracer.debug("Port: " + ((URL) parsedURI).getPort());
	  Tracer.debug("Path: " + ((URL) parsedURI).getPath());
	}
      else if(parsedURI instanceof URN)
	{
	  Tracer.debug("URN protocol: " + ((URN) parsedURI).getProtocol());
	  if(parsedURI instanceof PathURN)
	    Tracer.debug("Path: " + ((PathURN) parsedURI).getPath());
	  else
	    Tracer.debug("Protocol specific: "
			 + ((URN) parsedURI).getProtocolSpecific());
	}
      else if(parsedURI instanceof FileURL)
	Tracer.debug("File path: " + ((FileURL) parsedURI).getPath());
      else
	Tracer.debug("URI scheme specific: " + parsedURI.getSchemeSpecific());
    }
}





