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
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.conzilla.util.*;
import java.applet.*;
import java.net.*;
import java.io.*;

public class ApplicationContentDisplayer extends BrowserContentDisplayer
{
  public ApplicationContentDisplayer(PathURNResolver resolver)
    {
      super(resolver);
    }
  
  protected boolean showDocument(URL url) throws ContentException
    {
      String [] command;
      if (File.separatorChar == '/')
	{
	  command = new String[3];
	  command[0] = "netscape";
	  command[1] = "-remote";
	  command[2] = "openURL(" + url.toString() + ")";
	}
      else
	{
	  command=new String[2];
	  command[0] = "browser.bat";
	  command[1] = url.toString();
	}
      try {
	Runtime.getRuntime().exec(command);
      } catch(IOException e)
	{
	  throw new ContentException("Could not execute browser:\n "
				     + e.getMessage(), null);
	}
      return true;
    }
}