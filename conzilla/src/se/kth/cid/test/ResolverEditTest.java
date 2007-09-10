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
import se.kth.cid.conzilla.identity.*;

import se.kth.cid.identity.*;
import se.kth.cid.identity.pathurn.*;

public class ResolverEditTest
{
  public static void main(String[] argv)
    throws Exception
    {
      if(argv.length < 1)
	{
	  Tracer.trace("Usage: Resolve tableurl [LogLevel]",
		       Tracer.ERROR);
	  System.exit(-1);
	}
      
      if(argv.length > 2)
	Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
      else
	Tracer.setLogLevel(Tracer.NONE);

      URI table = URIClassifier.parseURI(argv[0]);

      ResolverManager manager = new ResolverManager();
      manager.addTable(new ResolverTable(table));

      ResolverEdit edit = new ResolverEdit(manager);
      edit.show();
      
    }
  
}




